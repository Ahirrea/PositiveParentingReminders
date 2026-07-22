// Centralized UI copy — mirrors the Android app's string resources so we keep
// the repo's "no hardcoded UI strings" convention on the web too.
// Ported verbatim from app/src/main/res/values/strings.xml. The Step 3 copy was
// hardcoded in activity_onboarding_step3.xml (not in strings.xml) and is ported here.
export const strings = {
  appName: "PositiveParentingApp",

  onboarding: {
    // Step 1 — Welcome
    welcomeTitle: "Welcome to Positive Parenting Reminders.",
    subtitle:
      "A small private space just for you to take a breather and reflect on everyday parenting.",
    buttonText: "Let's go",

    // Step 2 — Value proposition
    step2Title: "Find a moment for yourself every day.",
    step2Message:
      "Our app helps you move from reacting to conscious, mindful action in just 2-3 minutes a day.",
    step2Button: "Next",

    // Step 3 — Privacy (was hardcoded in the Android layout)
    step3Title: "Your thoughts are secure and private.",
    step3Message:
      "Your entries are encrypted throughout and completely confidential. Honest reflection needs a protected space.",
    step3Button: "Understood",
  },

  account: {
    title: "Secure your personal journal.",
    subtitle:
      "Create an account so that your entries are stored securely and are only accessible to you.",
    emailHint: "Email address",
    buttonText: "Create account",
    googleButtonText: "Sign in with Google",
    privacyCaption:
      "We respect your privacy. Your data is encrypted and never shared.",
    successMessage: "Thanks! Full account creation is coming soon.",
    googleComingSoon: "Google sign-in is coming soon.",
    invalidEmailMessage: "Invalid email format",
  },
} as const;
