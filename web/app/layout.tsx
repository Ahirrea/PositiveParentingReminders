import type { Metadata, Viewport } from "next";
import "./globals.css";
import { strings } from "@/lib/strings";

export const metadata: Metadata = {
  title: strings.appName,
  description: strings.onboarding.subtitle,
};

export const viewport: Viewport = {
  themeColor: "#133542",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-canvas text-white">{children}</body>
    </html>
  );
}
