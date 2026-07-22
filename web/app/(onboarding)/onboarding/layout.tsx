import type { ReactNode } from "react";

// Grouped route segment for the onboarding flow. The dark-teal canvas comes
// from the root body; this just scopes the flow.
export default function OnboardingLayout({
  children,
}: {
  children: ReactNode;
}) {
  return <>{children}</>;
}
