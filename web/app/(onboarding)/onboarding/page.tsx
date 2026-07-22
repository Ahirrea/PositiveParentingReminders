import Link from "next/link";
import { Screen } from "@/components/ui/Screen";
import { Button } from "@/components/ui/Button";
import { LottieStage } from "@/components/onboarding/LottieStage";
import { strings } from "@/lib/strings";

// Step 1 — Welcome. Mandala animation plays once (no loop), mirroring
// activity_onboarding.xml (app:lottie_loop="false").
export default function WelcomePage() {
  return (
    <Screen>
      <LottieStage src="/animations/mandala.lottie" loop={false} label="Welcome animation" />
      <h1 className="mt-16 text-headline">{strings.onboarding.welcomeTitle}</h1>
      <p className="mt-3 text-subhead text-white/85">{strings.onboarding.subtitle}</p>
      <div className="mt-auto w-full pt-10">
        <Link href="/onboarding/value">
          <Button>{strings.onboarding.buttonText}</Button>
        </Link>
      </div>
    </Screen>
  );
}
