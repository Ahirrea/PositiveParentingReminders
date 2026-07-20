#!/bin/bash
# SessionStart hook for Claude Code on the web.
#
# The remote container is a fresh clone with a JDK but NO Android SDK, so
# `./gradlew assembleDebug|lint|test` fail out of the box. This installs the
# Android command-line tools + the SDK packages this project needs, persists
# ANDROID_HOME for the session, and warms the Gradle distribution/dependency
# caches so the first build is fast.
#
# NETWORK REQUIREMENT: needs egress to services.gradle.org (+ its github.com
# redirect target) and dl.google.com. If your environment's network policy
# blocks these, the toolchain cannot be fetched — pick a policy that allows
# them. See https://code.claude.com/docs/en/claude-code-on-the-web
#
# Idempotent and non-interactive. Runs only in the remote environment.
#
# Runs in ASYNC mode: the JSON directive below tells Claude Code to start the
# session immediately while this installs in the background. Trade-off: an early
# build/lint/test may race the install if it fires before this finishes.
set -euo pipefail

# Local (non-web) sessions use the developer's own Android Studio SDK.
if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

# Detach from the session startup; keep running in the background (10 min cap).
# This MUST be the first thing written to stdout — all progress logs go to stderr.
echo '{"async": true, "asyncTimeout": 600000}'

PROJECT_DIR="${CLAUDE_PROJECT_DIR:-$(pwd)}"
ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
CMDLINE_TOOLS_VERSION="11076708"  # commandlinetools revision; pins the download
PLATFORM="platforms;android-36"
BUILD_TOOLS="build-tools;36.0.0"

echo "[session-start] preparing Android toolchain in $ANDROID_HOME" >&2

# gradlew loses its +x bit on some checkouts.
chmod +x "$PROJECT_DIR/gradlew" 2>/dev/null || true

install_cmdline_tools() {
  local dest="$ANDROID_HOME/cmdline-tools/latest"
  if [ -x "$dest/bin/sdkmanager" ]; then
    echo "[session-start] cmdline-tools already present" >&2
    return 0
  fi
  echo "[session-start] downloading Android command-line tools" >&2
  mkdir -p "$ANDROID_HOME/cmdline-tools"
  local zip="/tmp/android-cmdline-tools.zip"
  curl -fSL --retry 3 -o "$zip" \
    "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
  rm -rf "$ANDROID_HOME/cmdline-tools/tmp"
  unzip -q -o "$zip" -d "$ANDROID_HOME/cmdline-tools/tmp"
  # The zip extracts to a top-level "cmdline-tools/" dir; sdkmanager expects it
  # nested under "latest/".
  rm -rf "$dest"
  mv "$ANDROID_HOME/cmdline-tools/tmp/cmdline-tools" "$dest"
  rm -rf "$ANDROID_HOME/cmdline-tools/tmp" "$zip"
}

install_cmdline_tools

export ANDROID_HOME
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

echo "[session-start] accepting licenses & installing SDK packages" >&2
yes | sdkmanager --licenses >/dev/null 2>&1 || true
sdkmanager --install "platform-tools" "$PLATFORM" "$BUILD_TOOLS"

# Point the Gradle build at the SDK (local.properties is git-ignored).
if ! grep -q "^sdk.dir=" "$PROJECT_DIR/local.properties" 2>/dev/null; then
  echo "sdk.dir=$ANDROID_HOME" >> "$PROJECT_DIR/local.properties"
fi

# Persist for the rest of the session so later Bash commands see the SDK.
if [ -n "${CLAUDE_ENV_FILE:-}" ]; then
  {
    echo "export ANDROID_HOME=\"$ANDROID_HOME\""
    echo "export ANDROID_SDK_ROOT=\"$ANDROID_HOME\""
    echo "export PATH=\"$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:\$PATH\""
  } >> "$CLAUDE_ENV_FILE"
fi

# Warm the Gradle distribution + dependency caches (cached in the container image).
echo "[session-start] warming Gradle caches" >&2
( cd "$PROJECT_DIR" && ./gradlew --no-daemon help >/dev/null 2>&1 ) || \
  echo "[session-start] warning: gradle warm-up failed (deps will download on first build)" >&2

echo "[session-start] done" >&2
