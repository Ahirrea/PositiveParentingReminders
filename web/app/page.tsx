import { redirect } from "next/navigation";

// Root entry. Once auth lands (Phase 2) this decides between the onboarding
// flow and the authenticated app; for now it always starts onboarding.
export default function Home() {
  redirect("/onboarding");
}
