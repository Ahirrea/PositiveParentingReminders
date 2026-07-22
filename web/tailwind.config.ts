import type { Config } from "tailwindcss";

// Design tokens ported from the Android app (res/values/colors.xml).
const config: Config = {
  content: [
    "./app/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
    "./lib/**/*.{ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // colors.xml -> animation_background (#133542), the calming dark-teal backdrop.
        canvas: "#133542",
      },
      fontSize: {
        // Rough parity with the Android AppCompat type scale used on onboarding.
        headline: ["1.5rem", { lineHeight: "2rem", fontWeight: "600" }],
        subhead: ["1.0625rem", { lineHeight: "1.6rem" }],
        caption: ["0.8125rem", { lineHeight: "1.2rem" }],
      },
    },
  },
  plugins: [],
};

export default config;
