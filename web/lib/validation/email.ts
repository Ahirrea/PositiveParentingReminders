// Ported from AccountCreationActivity.kt (validateEmail, lines 60-76).
// Three-state model matching the Android behaviour exactly:
//   - empty   -> no error shown, submit disabled
//   - valid   -> no error shown, submit enabled
//   - invalid -> error "Invalid email format", submit disabled
export type EmailState = "empty" | "valid" | "invalid";

// Android used android.util.Patterns.EMAIL_ADDRESS. This is the documented
// single-line equivalent of that pattern's practical behaviour.
const EMAIL_PATTERN =
  /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/;

export function isValidEmail(email: string): boolean {
  return EMAIL_PATTERN.test(email);
}

export function emailState(email: string): EmailState {
  if (email.length === 0) return "empty";
  return isValidEmail(email) ? "valid" : "invalid";
}

// Convenience for the form: submit is only enabled when the address is valid.
export function canSubmitEmail(email: string): boolean {
  return emailState(email) === "valid";
}
