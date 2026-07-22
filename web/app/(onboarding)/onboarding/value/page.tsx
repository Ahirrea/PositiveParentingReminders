import Link from "next/link";
import { Screen } from "@/components/ui/Screen";
import { Button } from "@/components/ui/Button";
import { HeartIcon } from "@/components/icons/HeartIcon";
import { strings } from "@/lib/strings";

// Step 2 — Value proposition (activity_onboarding_step2.xml): white heart icon.
export default function ValuePage() {
  return (
    <Screen>
      <HeartIcon className="mt-8 h-28 w-28 text-white" />
      <h1 className="mt-14 text-headline">{strings.onboarding.step2Title}</h1>
      <p className="mt-4 text-subhead text-white/85">{strings.onboarding.step2Message}</p>
      <div className="mt-auto w-full pt-10">
        <Link href="/onboarding/privacy">
          <Button>{strings.onboarding.step2Button}</Button>
        </Link>
      </div>
    </Screen>
  );
}
