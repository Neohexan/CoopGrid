"use client";

import React, { useEffect, useState, useRef } from "react";
import gsap from "gsap";
import { GlobeSvg } from "./GlobeSvg";
import { OrbitalArcs } from "./OrbitalArcs";
import { ScrambleText } from "./ScrambleText";
import { LEFT_SKILLS, RIGHT_YEARS, TELEMETRY_MODULES } from "@/data/telemetryData";

interface PensatoriPreloaderProps {
  onComplete?: () => void;
  replayTrigger?: number;
  speedMultiplier?: number;
}

export const PensatoriPreloader: React.FC<PensatoriPreloaderProps> = ({
  onComplete,
  replayTrigger = 0,
  speedMultiplier = 1,
}) => {
  const [progress, setProgress] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setProgress(0);

    const ctx = gsap.context(() => {
      const element = containerRef.current;
      if (!element) return;

      // Safely query DOM elements
      const circles = element.querySelectorAll(".loader-circle");
      const boxes = element.querySelectorAll(".loader-box");
      const secondaries = element.querySelectorAll(".loader-circle-secondary");
      const leftItems = element.querySelectorAll(".loader-list-item-left");
      const rightItems = element.querySelectorAll(".loader-list-item-right");
      const rightDots = element.querySelectorAll(".loader-right-dot");
      const blinds = element.querySelectorAll(".loader-blind");
      const expandBlinds = element.querySelectorAll(".blind-expand");
      const logo = element.querySelector(".loader-close-logo");
      const flagParts = element.querySelectorAll(".loader-close-logo-flag-part");
      const wrapper = element.querySelector(".loader-wrapper");
      const rotateEl = element.querySelector(".loader-rotate");
      const svgEls = element.querySelectorAll(".loader-circle-svg");

      // Initial GSAP States
      if (wrapper) gsap.set(wrapper, { opacity: 1, pointerEvents: "auto", display: "flex" });
      if (circles.length) gsap.set(circles, { scale: 0 });
      if (boxes.length) gsap.set(boxes, { opacity: 0 });
      if (secondaries.length) gsap.set(secondaries, { opacity: 0 });
      if (leftItems.length) gsap.set(leftItems, { opacity: 0 });
      if (rightItems.length) gsap.set(rightItems, { opacity: 0 });
      if (rightDots.length) gsap.set(rightDots, { backgroundColor: "#282828" });
      if (blinds.length) gsap.set(blinds, { width: "0%" });
      if (expandBlinds.length) gsap.set(expandBlinds, { scaleY: 1 });
      if (logo) gsap.set(logo, { opacity: 0 });
      if (flagParts.length) gsap.set(flagParts, { scaleY: 0 });

      if (rotateEl) {
        gsap.to(rotateEl, {
          rotate: -360,
          duration: 35 / speedMultiplier,
          repeat: -1,
          ease: "linear",
        });
      }

      const tl = gsap.timeline({
        onUpdate: () => {
          const currentProgress = Math.round(100 * tl.progress());
          setProgress(currentProgress);
        },
        onComplete: () => {
          const outroTl = gsap.timeline({
            onComplete: () => {
              if (wrapper) {
                gsap.to(wrapper, {
                  delay: 0.6 / speedMultiplier,
                  opacity: 0,
                  duration: 0.8 / speedMultiplier,
                  ease: "power2.inOut",
                  onComplete: () => {
                    gsap.set(wrapper, { pointerEvents: "none" });
                    onComplete?.();
                  },
                });
              } else {
                onComplete?.();
              }
            },
          });

          const isMobile = window.matchMedia("(max-width: 767px)").matches;

          if (circles.length) {
            outroTl.to(
              circles,
              { scale: 0, duration: 1.2 / speedMultiplier, stagger: { each: 0.03 / speedMultiplier, from: "end" }, ease: "power2.in" },
              0.15
            );
          }

          if (svgEls.length) outroTl.to(svgEls, { opacity: 0, duration: 1.0 / speedMultiplier }, 0.05);
          if (secondaries.length) outroTl.to(secondaries, { opacity: 0, duration: 0.5 / speedMultiplier }, 0);

          if (blinds.length) {
            outroTl.to(
              blinds,
              { width: "100%", duration: 1.1 / speedMultiplier, stagger: 0.02 / speedMultiplier, ease: "power3.inOut" },
              0.35
            );
          }

          if (expandBlinds.length) {
            outroTl.to(
              expandBlinds,
              { scaleY: 1.35, duration: (isMobile ? 0.6 : 0.9) / speedMultiplier, ease: "power2.inOut" },
              isMobile ? "<+=0.1" : "<+=0.2"
            );
          }

          if (logo) {
            outroTl.addLabel("logo", "<+=0.25");
            outroTl.to(logo, { opacity: 1, duration: 0.9 / speedMultiplier, ease: "power2.out" }, "logo");
          }

          if (flagParts.length) {
            outroTl.fromTo(
              flagParts,
              { scaleY: 0 },
              { scaleY: 1, duration: 0.4 / speedMultiplier, ease: "power2.inOut", stagger: 0.25 / speedMultiplier },
              "logo"
            );
          }
        },
      });

      // Intro Animations
      if (circles.length) tl.to(circles, { scale: 1, duration: 0.9 / speedMultiplier, stagger: 0.05 / speedMultiplier, ease: "back.out(1.1)" });
      if (boxes.length) tl.to(boxes, { opacity: 1, duration: 0.8 / speedMultiplier, stagger: 0.05 / speedMultiplier }, 0);
      if (secondaries.length) tl.to(secondaries, { opacity: 1, duration: 0.5 / speedMultiplier, stagger: 0.015 / speedMultiplier }, 0.3);
      if (leftItems.length) tl.to(leftItems, { opacity: 1, duration: 0.8 / speedMultiplier, stagger: 0.08 / speedMultiplier }, 0.35);
      if (rightItems.length) tl.to(rightItems, { opacity: 1, duration: 0.8 / speedMultiplier, stagger: 0.08 / speedMultiplier }, 0.35);
      
      if (rightDots.length) {
        tl.to(
          rightDots,
          { backgroundColor: "#51FD01", boxShadow: "0 0 12px rgba(81, 253, 1, 0.9)", duration: 1.2 / speedMultiplier, stagger: 0.06 / speedMultiplier, ease: "power1.inOut" },
          0.2
        );
      }

      tl.to({}, { duration: 2.4 / speedMultiplier });
    }, containerRef);

    return () => ctx.revert();
  }, [onComplete, replayTrigger, speedMultiplier]);

  return (
    <>
      <style>{`
        /* Core Layout Constraints */
        .pl-wrapper {
          position: fixed; top: 0; left: 0; right: 0; bottom: 0; width: 100vw; height: 100vh;
          background-color: #111111; z-index: 99999; display: flex; align-items: center; justify-content: center;
          overflow: hidden; user-select: none;
        }

        /* The Responsive Telemetry Grid */
        .pl-grid {
          position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: grid; 
          grid-template-columns: 1fr; 
          row-gap: 20px; /* Explicit vertical margin between blocks */
          padding: 16px 4px; /* Reduced left and right margins for mobile */
          z-index: 20; 
          pointer-events: none; opacity: 0.95; box-sizing: border-box; 
          align-content: space-between;
        }
        @media (min-width: 1280px) { 
          .pl-grid { 
            grid-template-columns: repeat(3, 1fr); 
            padding: 24px 16px; 
            column-gap: 64px; 
            row-gap: 16px; 
          } 
        }

        /* Module Part A (Left side - Studio Specs) */
        .pl-mod-a {
          display: none; border: 1px solid rgba(40,40,40, 0.5); background-color: rgba(17, 17, 17, 0.70); 
          backdrop-filter: blur(4px); padding: 16px; flex-direction: row; gap: 24px; justify-content: space-between;
          height: 100%; overflow: hidden;
        }
        @media (min-width: 1280px) { 
          .pl-mod-a { display: flex; grid-column: span 2 / span 2; } 
        }

        /* Module Part B (Right side - Status LEDs) */
        .pl-mod-b {
          border: 1px solid rgba(40,40,40, 0.5); background-color: rgba(17, 17, 17, 0.80); backdrop-filter: blur(4px); 
          padding: 12px 8px; /* Slightly reduced padding to help prevent overflow on mobile */
          display: flex; flex-direction: row; justify-content: space-between; gap: 12px;
          height: 100%; overflow: hidden; /* Prevents text bleeding out of the container */
        }
        @media (min-width: 1280px) { 
          .pl-mod-b { grid-column: span 1 / span 1; padding: 16px; gap: 16px; } 
        }

        /* Inner Text Typography (Expanded Font Sizes) */
        .pl-col-text { display: flex; flex-direction: column; justify-content: space-between; color: rgba(255,255,255,0.95); font-size: 13px; font-family: var(--font-tech-mono), monospace; width: 45%; height: 100%; }
        .pl-col-sub { display: flex; flex-direction: column; justify-content: space-between; width: 55%; font-size: 11px; font-family: var(--font-tech-mono), monospace; color: #737373; line-height: 1.4; height: 100%; }
        
        .pl-spec-col { display: flex; flex-direction: column; justify-content: space-between; color: #ffffff; font-size: 11px; font-family: var(--font-tech-mono), monospace; width: 60%; height: 100%; }
        @media (min-width: 1280px) { .pl-spec-col { font-size: 13px; } }
        
        .pl-status-col { display: flex; flex-direction: column; justify-content: space-between; font-size: 9.5px; font-family: var(--font-tech-mono), monospace; color: rgba(255,255,255,0.85); flex-shrink: 0; height: 100%; }
        @media (min-width: 1280px) { .pl-status-col { font-size: 11px; } }

        /* Outro Elements */
        .pl-blinds-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 30; display: flex; flex-direction: column; pointer-events: none; }
        .pl-blind { width: 0%; flex-basis: 20%; background-color: #f5f5f5; transform-origin: center; }
        .pl-logo-wrap { position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); z-index: 35; display: flex; flex-direction: column; align-items: center; justify-content: center; pointer-events: none; }
        .pl-logo-box { padding: 24px; background-color: rgba(255,255,255,0.4); backdrop-filter: blur(12px); border-radius: 16px; border: 1px solid rgba(0,0,0,0.05); box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25); display: flex; flex-direction: column; align-items: center; }

        /* Central Globe Environment */
        .pl-globe-area { position: absolute; inset: 0; z-index: 21; display: flex; align-items: center; justify-content: center; pointer-events: none; }
        .pl-globe-cont { position: relative; width: 85vw; max-width: 460px; aspect-ratio: 1/1; display: flex; align-items: center; justify-content: center; }
        @media (min-width: 768px) { .pl-globe-cont { max-width: 620px; } }
        @media (min-width: 1024px) { .pl-globe-cont { max-width: 720px; } }
        @media (min-width: 1280px) { .pl-globe-cont { max-width: 840px; } }
        
        .pl-circle-sec { position: absolute; aspect-ratio: 1/1; border-radius: 9999px; border: 1px solid rgba(255,255,255,0.1); pointer-events: none; z-index: 5; opacity: 0; }
        .pl-c-1 { height: 92%; position: absolute; aspect-ratio: 1/1; border-radius: 9999px; border: 1px solid rgba(255,255,255,0.4); z-index: 6; }
        .pl-c-2 { height: 78%; position: absolute; aspect-ratio: 1/1; border-radius: 9999px; border: 1px solid rgba(255,255,255,0.5); overflow: hidden; z-index: 7; }
        .pl-c-3 { height: 64%; position: absolute; aspect-ratio: 1/1; border-radius: 9999px; border: 1px solid rgba(255,255,255,0.5); z-index: 8; }
        .pl-c-4 { height: 50%; position: absolute; aspect-ratio: 1/1; border-radius: 9999px; background-color: #f5f5f5; z-index: 10; display: flex; align-items: center; justify-content: center; overflow: hidden; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25); }
        
        .pl-type-loading { font-family: var(--font-ballet), cursive, serif; font-size: 11vw; line-height: 1.2; }
        @media (min-width: 640px) { .pl-type-loading { font-size: 8vw; } }
        @media (min-width: 768px) { .pl-type-loading { font-size: 3.2vw; } }
        @media (min-width: 1024px) { .pl-type-loading { font-size: 2.6vw; } }
        
        .pl-type-pct { font-family: var(--font-tech-mono), monospace; font-size: 2.8vw; position: absolute; top: 0; left: 84%; letter-spacing: -0.05em; font-weight: 500; color: #222222; }
        @media (min-width: 640px) { .pl-type-pct { font-size: 1.8vw; } }
        @media (min-width: 768px) { .pl-type-pct { font-size: 0.7vw; } }
        
        .pl-skills { position: absolute; display: none; flex-direction: column; gap: 4px; font-size: 10px; color: #434343; font-family: var(--font-tech-mono), monospace; }
        @media (min-width: 768px) { .pl-skills { display: flex; font-size: 12px; } }
        .pl-skills-left { left: 12px; text-align: left; }
        .pl-skills-right { right: 12px; text-align: right; }
        @media (min-width: 640px) { .pl-skills-left { left: 24px; } .pl-skills-right { right: 24px; } }
        @media (min-width: 768px) { .pl-skills-left { left: 32px; } .pl-skills-right { right: 32px; } }
      `}</style>
      
      <div ref={containerRef} aria-hidden="true" className="pl-wrapper loader-wrapper">
        
        {/* 1. OUTRO BLINDS & LOGO REVEAL */}
        <div className="pl-blinds-overlay">
          <div className="pl-logo-wrap loader-close-logo">
            <div className="pl-logo-box">
              <img src="/logo.png" alt="Custom Brand Logo" style={{ width: "140px", height: "auto", objectFit: "contain", filter: "drop-shadow(0 4px 6px rgba(0,0,0,0.1))" }} />
              <div style={{ textAlign: "center", marginTop: "12px" }}>
                <h2 style={{ color: "#1a1a1a", fontWeight: "bold", fontSize: "16px", letterSpacing: "0.1em", textTransform: "uppercase", margin: 0 }}>COOPGRID ADMIN</h2>
                <p style={{ color: "#666666", fontSize: "10px", fontFamily: "var(--font-tech-mono), monospace", letterSpacing: "0.05em", margin: "4px 0 0 0" }}>FEDERATION • VERIFICATION • SECURITY</p>
              </div>
              <span style={{ width: "100%", marginTop: "12px", overflow: "hidden", borderRadius: "9999px", display: "flex" }}>
                <svg width="100%" height="8" viewBox="0 0 292 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path className="loader-close-logo-flag-part" style={{ transformOrigin: "bottom" }} d="M97.2033 0H0V19.7034H97.2033V0Z" fill="#0284c7" />
                  <path className="loader-close-logo-flag-part" style={{ transformOrigin: "bottom" }} d="M194.399 0H97.1953V19.7034H194.399V0Z" fill="#10b981" />
                  <path className="loader-close-logo-flag-part" style={{ transformOrigin: "bottom" }} d="M291.617 0H194.414V19.7034H291.617V0Z" fill="#84cc16" />
                </svg>
              </span>
            </div>
          </div>
          {Array.from({ length: 5 }).map((_, t) => (
            <div key={t} className={`pl-blind loader-blind ${t !== 0 && t !== 4 ? "blind-expand" : ""}`} />
          ))}
        </div>

        {/* 2. BACKGROUND TELEMETRY GRID MATRIX */}
        <div 
          className="pl-grid" 
          // Uses minmax(min-content, 1fr) so boxes never compress smaller than their text on tiny screens
          style={{ gridTemplateRows: `repeat(${TELEMETRY_MODULES.length}, minmax(min-content, 1fr))` }}
        >
          {TELEMETRY_MODULES.map((module, idx) => (
            <React.Fragment key={idx}>
              
              {/* PART A: Studio Spec (Desktop Only, takes up 2 columns) */}
              <div className="pl-mod-a loader-box">
                <div className="pl-col-text">
                  {module.left.map((pair, pIdx) => (
                    <div key={pIdx} style={{ lineHeight: "1.4" }}>
                      <p style={{ color: "rgba(255,255,255,0.85)", fontWeight: 600, margin: 0, paddingBottom: "2px" }}>
                        <ScrambleText text={pair[0]} scrambleSpeed={25} delay={150 * pIdx} />
                      </p>
                      <p style={{ color: "#8a8a8a", margin: 0 }}>
                        <ScrambleText text={pair[1]} scrambleSpeed={30} delay={200 * pIdx} />
                      </p>
                    </div>
                  ))}
                </div>
                <div className="pl-col-sub">
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", color: "#666666", borderBottom: "1px solid #333333", paddingBottom: "6px" }}>
                    <span>T1 2 3 4 5 6 7</span>
                    <span style={{ color: "rgba(81, 253, 1, 0.7)", fontWeight: 600 }}>SYNCHRONIZED</span>
                  </div>
                  <div style={{ padding: "6px 0", flexGrow: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
                    {module.rightText.map((line, lIdx) => (
                      <span key={lIdx} style={{ display: "block", color: "#8a8a8a" }}>
                        <ScrambleText text={line} scrambleSpeed={28} delay={100 * lIdx} />
                      </span>
                    ))}
                  </div>
                  <div style={{ fontSize: "10px", color: "#555555", borderTop: "1px solid #333333", paddingTop: "6px" }}>
                    COOPGRID ADMIN CORE • Architecture Model
                  </div>
                </div>
              </div>

              {/* PART B: Status Box (Takes up 1 column on desktop, 100% on mobile) */}
              <div className="pl-mod-b loader-box">
                <div className="pl-spec-col">
                  <div>
                    <p style={{ fontWeight: 600, color: "#ffffff", margin: 0, paddingBottom: "4px" }}>
                      <ScrambleText text={module.rightBox[0][0]} scrambleSpeed={25} />
                    </p>
                    <p style={{ color: "rgba(81, 253, 1, 0.9)", margin: 0, fontWeight: 600 }}>
                      <ScrambleText text={module.rightBox[0][1]} scrambleSpeed={30} delay={100} />
                    </p>
                  </div>
                  <div style={{ color: "#8a8a8a", lineHeight: "1.4" }}>
                    {module.rightBox[1].map((spec, sIdx) => (
                      <p key={sIdx} style={{ margin: 0 }}>
                        <ScrambleText text={spec} scrambleSpeed={30} delay={150 * sIdx} />
                      </p>
                    ))}
                  </div>
                </div>

                <div className="pl-status-col">
                  {Array.from({ length: 7 }).map((_, dotIdx) => (
                    <div key={dotIdx} style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "10px" }}>
                      <ScrambleText
                        text={`${(dotIdx + 1).toString().padStart(2, "0")} STATUS / ACTIVE`}
                        scrambleSpeed={30}
                        delay={80 * dotIdx}
                        continuous={true}
                        style={{ color: "#a3a3a3" }}
                      />
                      <div style={{ width: "9px", height: "9px", borderRadius: "50%", backgroundColor: "#282828", flexShrink: 0 }} className="loader-right-dot" />
                    </div>
                  ))}
                </div>
              </div>
              
            </React.Fragment>
          ))}
        </div>

        {/* 3. ARCS */}
        <OrbitalArcs mirrored={false} />
        <OrbitalArcs mirrored={true} />

        {/* 4. GLOBE */}
        <div className="pl-globe-area">
          <div className="pl-globe-cont">
            {Array.from({ length: 34 }).map((_, t) => (
              <div key={t} style={{ height: `${59 + t * 1.1}%` }} className="pl-circle-sec loader-circle-secondary" />
            ))}

            <div className="pl-c-1 loader-circle" />
            <div className="pl-c-2 loader-circle">
              <div style={{ position: "relative", width: "100%", height: "100%", transformOrigin: "center", color: "#e6e6e6" }} className="loader-rotate">
                <GlobeSvg style={{ width: "100%", height: "100%", objectFit: "contain", filter: "drop-shadow(0 0 12px rgba(255,255,255,0.15))" }} />
              </div>
            </div>
            <div className="pl-c-3 loader-circle" />
            
            <div className="pl-c-4 loader-circle">
              <div style={{ position: "relative", width: "100%", height: "100%", display: "flex", alignItems: "center", justifyContent: "center" }}>
                <div style={{ display: "flex", flexDirection: "column", alignItems: "center", color: "#383838", position: "relative", zIndex: 10 }}>
                  <span className="pl-type-loading">Loading</span>
                  <span className="pl-type-pct">({progress}%)</span>
                </div>

                <div className="pl-skills pl-skills-left loader-list-item-left">
                  {LEFT_SKILLS.map((skill, n) => {
                    const norm = LEFT_SKILLS.length > 1 ? (2 * n) / (LEFT_SKILLS.length - 1) - 1 : 0;
                    return <div key={n} style={{ transform: `translateX(${-(30 * (1 - norm * norm))}%)` }}>{skill}</div>;
                  })}
                </div>

                <div className="pl-skills pl-skills-right loader-list-item-right">
                  {RIGHT_YEARS.map((year, n) => {
                    const norm = RIGHT_YEARS.length > 1 ? (2 * n) / (RIGHT_YEARS.length - 1) - 1 : 0;
                    return <div key={n} style={{ transform: `translateX(${30 * (1 - norm * norm)}%)` }}>{year}</div>;
                  })}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
