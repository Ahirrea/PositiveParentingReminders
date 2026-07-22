"use client";

import { useState } from "react";
import { Screen } from "@/components/ui/Screen";
import { Button } from "@/components/ui/Button";
import { TextField } from "@/components/ui/TextField";
import { GoogleLogo } from "@/components/icons/GoogleLogo";
import { strings } from "@/lib/strings";
import { canSubmitEmail, emailState } from "@/lib/validation/email";

// Account creation (activity_account_creation.xml + AccountCreationActivity.kt).
// Auth is wired in Phase 2; here the buttons are inert placeholders, exactly as
// the Android screen is today (it only showed a "coming soon" Toast).
export default function AccountPage() {
  const [email, setEmail] = useState("");
  const [toast, setToast] = useState<string | null>(null);

  const state = emailState(email);
  // Match Android: error text only when a non-empty value fails validation.
  const error = state === "invalid" ? strings.account.invalidEmailMessage : null;
  const canSubmit = canSubmitEmail(email);

  return (
    <Screen>
      <h1 className="mt-4 text-headline">{strings.account.title}</h1>
      <p className="mt-4 text-subhead text-white/85">{strings.account.subtitle}</p>

      <div className="mt-8 w-full">
        <TextField
          name="email"
          type="email"
          autoComplete="email"
          label={strings.account.emailHint}
          value={email}
          error={error}
          onChange={(e) => {
            setEmail(e.target.value);
            setToast(null);
          }}
        />
      </div>

      <div className="mt-6 w-full space-y-4">
        <Button
          disabled={!canSubmit}
          onClick={() => setToast(strings.account.successMessage)}
        >
          {strings.account.buttonText}
        </Button>
        <Button
          variant="outlined"
          onClick={() => setToast(strings.account.googleComingSoon)}
        >
          <GoogleLogo className="h-5 w-5" />
          {strings.account.googleButtonText}
        </Button>
      </div>

      {toast ? (
        <p role="status" className="mt-4 text-subhead text-white/90">
          {toast}
        </p>
      ) : null}

      <p className="mt-auto pt-8 text-caption text-white/70">
        {strings.account.privacyCaption}
      </p>
    </Screen>
  );
}
