import type { ReactNode } from "react";

// Calming, centered onboarding shell — the web analogue of the ConstraintLayout
// screens (dark-teal background, centered content, generous vertical rhythm).
export function Screen({ children }: { children: ReactNode }) {
  return (
    <main className="mx-auto flex min-h-screen w-full max-w-md flex-col items-center px-8 pb-8 pt-16 text-center">
      {children}
    </main>
  );
}
