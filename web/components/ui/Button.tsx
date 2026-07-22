import type { ButtonHTMLAttributes } from "react";

type Variant = "filled" | "outlined";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: Variant;
};

// Mirrors the Material buttons used in onboarding: a filled primary action and
// an outlined secondary (used for "Sign in with Google").
export function Button({
  variant = "filled",
  className = "",
  ...props
}: ButtonProps) {
  const base =
    "inline-flex w-full items-center justify-center gap-3 rounded-full px-6 py-3.5 text-base font-medium transition disabled:cursor-not-allowed disabled:opacity-40";
  const variants: Record<Variant, string> = {
    filled: "bg-white text-canvas hover:bg-white/90",
    outlined: "border border-white/80 text-white hover:bg-white/10",
  };
  return (
    <button className={`${base} ${variants[variant]} ${className}`} {...props} />
  );
}
