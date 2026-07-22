"use client";

import { DotLottieReact } from "@lottiefiles/dotlottie-react";

type LottieStageProps = {
  // Path under /public (both .lottie and .json are supported by the player).
  src: string;
  // Mirrors Android's app:lottie_loop flag per screen.
  loop: boolean;
  label: string;
};

// Web analogue of Android's LottieAnimationView (200dp square, autoplay).
// Welcome uses the mandala (no loop); the privacy screen uses the lock (looping).
export function LottieStage({ src, loop, label }: LottieStageProps) {
  return (
    <div className="h-48 w-48" role="img" aria-label={label}>
      <DotLottieReact src={src} autoplay loop={loop} />
    </div>
  );
}
