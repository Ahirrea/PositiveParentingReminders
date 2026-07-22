import type { InputHTMLAttributes } from "react";

type TextFieldProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: string | null;
};

// Outlined text field echoing Material's OutlinedBox TextInputLayout: a floating
// label, white stroke, and an error line beneath (shown only when `error` is set).
export function TextField({
  label,
  error,
  id,
  className = "",
  ...props
}: TextFieldProps) {
  const fieldId = id ?? props.name ?? "textfield";
  return (
    <div className="w-full text-left">
      <label htmlFor={fieldId} className="mb-1.5 block text-sm text-white/80">
        {label}
      </label>
      <input
        id={fieldId}
        className={`w-full rounded-lg border bg-transparent px-4 py-3 text-white placeholder-white/40 outline-none focus:ring-2 ${
          error
            ? "border-red-300 focus:ring-red-300/40"
            : "border-white/60 focus:ring-white/40"
        } ${className}`}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${fieldId}-error` : undefined}
        {...props}
      />
      {error ? (
        <p id={`${fieldId}-error`} className="mt-1.5 text-sm text-red-300">
          {error}
        </p>
      ) : null}
    </div>
  );
}
