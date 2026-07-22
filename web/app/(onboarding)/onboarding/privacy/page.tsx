import Link from "next/link";
import { Screen } from "@/components/ui/Screen";
import { Button } from "@/components/ui/Button";
import { LottieStage } from "@/components/onboarding/LottieStage";
import { strings } from "@/lib/strings";

// Step 3 — Privacy (activity_onboarding_step3.xml): lock animation loops
// (app:lottie_loop="true").
export default function PrivacyPage() {
  return (
    <Screen>
      <LottieStage src="/animations/lock.json" loop label="Privacy lock animation" />
      <h1 className="mt-14 text-headline">{strings.onboarding.step3Title}</h1>
      <p className="mt-4 text-subhead text-white/85">{strings.onboarding.step3Message}</p>
      <div className="mt-auto w-full pt-10">
        <Link href="/onboarding/account">
          <Button>{strings.onboarding.step3Button}</Button>
        </Link>
      </div>
    </Screen>
  );
}
