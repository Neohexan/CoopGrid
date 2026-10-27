"use client";

import React, { useEffect, useRef } from "react";
import gsap from "gsap";

interface OrbitalArcsProps {
  mirrored?: boolean;
}

export const OrbitalArcs: React.FC<OrbitalArcsProps> = ({ mirrored = false }) => {
  const side = mirrored ? "right" : "left";
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ delay: 0.5 });
      tl.to(`.loader-draw-circle-cw-${side}`, { strokeDashoffset: 1229.5, duration: 1.5, ease: "power1.inOut" }, 0);
      tl.to(`.loader-draw-circle-ccw-${side}`, { strokeDashoffset: 1229.5, duration: 1.5, ease: "power1.inOut" }, 0);
      tl.to(`.loader-draw-line-${side}`, { strokeDashoffset: 0, duration: 1, ease: "power1.inOut" }, 0.3);
    }, containerRef);

    return () => ctx.revert();
  }, [side]);

  return (
    <div
      ref={containerRef}
      className={`absolute h-full top-1/2 -translate-y-1/2 hidden md:flex items-center pointer-events-none z-15 loader-circle-svg ${mirrored ? "scale-x-[-1]" : ""}`}
      style={mirrored ? { left: "calc(50% + 46vh)" } : { right: "calc(50% + 46vh)" }}
    >
      <svg style={{ height: "92vh", width: "auto" }} viewBox="0 0 492 784" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="100" cy="391.624" r="391.312" stroke="#282828" strokeWidth="0.623598" className={`loader-draw-circle-cw-${side}`} style={{ strokeDasharray: 2459, strokeDashoffset: 2459 }} />
        <circle cx="100" cy="391.624" r="391.312" stroke="#282828" strokeWidth="0.623598" className={`loader-draw-circle-ccw-${side}`} style={{ strokeDasharray: 2459, strokeDashoffset: 2459, transformOrigin: "100px 391.624px", transform: "scaleY(-1)" }} />
        <line x1="491" y1="378.124" x2="0" y2="378.124" stroke="#282828" className={`loader-draw-line-${side}`} style={{ strokeDasharray: 491, strokeDashoffset: 491 }} />
        <line x1="491.13" y1="378.141" x2="-1.86977" y2="245.141" stroke="#282828" className={`loader-draw-line-${side}`} style={{ strokeDasharray: 520, strokeDashoffset: 520 }} />
        <line x1="490.871" y1="378.141" x2="-1.12865" y2="509.141" stroke="#282828" className={`loader-draw-line-${side}`} style={{ strokeDasharray: 520, strokeDashoffset: 520 }} />
      </svg>
    </div>
  );
};
