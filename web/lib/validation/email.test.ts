import { describe, expect, it } from "vitest";
import { canSubmitEmail, emailState, isValidEmail } from "./email";

describe("email validation (ported from AccountCreationActivity.kt)", () => {
  it("treats an empty field as 'empty' (no error, submit disabled)", () => {
    expect(emailState("")).toBe("empty");
    expect(canSubmitEmail("")).toBe(false);
  });

  it("flags a malformed address as 'invalid' (error, submit disabled)", () => {
    for (const bad of ["a@b", "no-at-sign", "foo@bar", "@example.com", "a@b."]) {
      expect(emailState(bad)).toBe("invalid");
      expect(canSubmitEmail(bad)).toBe(false);
    }
  });

  it("accepts a well-formed address as 'valid' (no error, submit enabled)", () => {
    for (const ok of ["a@b.com", "katharina.froehling@generic.de", "x+y@sub.example.co"]) {
      expect(emailState(ok)).toBe("valid");
      expect(isValidEmail(ok)).toBe(true);
      expect(canSubmitEmail(ok)).toBe(true);
    }
  });
});
