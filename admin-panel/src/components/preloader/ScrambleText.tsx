"use client";

import React, { useEffect, useState, useRef } from "react";

interface ScrambleTextProps {
  text: string;
  scrambleSpeed?: number;
  delay?: number;
  className?: string;
  continuous?: boolean;
}

const GLYPHS = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

export const ScrambleText: React.FC<ScrambleTextProps> = ({
  text,
  scrambleSpeed = 30,
  delay = 0,
  className = "",
  continuous = false,
}) => {
  const [displayText, setDisplayText] = useState(text);
  const intervalRef = useRef<number | null>(null);
  const timeoutRef = useRef<number | null>(null);

  useEffect(() => {
    let iteration = 0;
    const maxIterations = text.length + 3;

    timeoutRef.current = window.setTimeout(() => {
      intervalRef.current = window.setInterval(() => {
        iteration++;
        
        const randomized = text
          .split("")
          .map((char, index) => {
            if (char === " " || char === "\n" || char === "/" || char === ":" || char === "-") return char;
            if (index < iteration - 2) return text[index];
            return GLYPHS[Math.floor(Math.random() * GLYPHS.length)];
          })
          .join("");

        setDisplayText(randomized);

        if (iteration >= maxIterations) {
          if (intervalRef.current) clearInterval(intervalRef.current);
          setDisplayText(text);

          if (continuous) {
            const nextGlitchDelay = 2000 + Math.random() * 2500;
            timeoutRef.current = window.setTimeout(() => {
              let glitchStep = 0;
              const glitchInterval = window.setInterval(() => {
                glitchStep++;
                const glitched = text
                  .split("")
                  .map((c) => {
                    if (Math.random() < 0.2 && c !== " " && c !== "/") return GLYPHS[Math.floor(Math.random() * GLYPHS.length)];
                    return c;
                  })
                  .join("");
                setDisplayText(glitched);
                if (glitchStep > 3) {
                  clearInterval(glitchInterval);
                  setDisplayText(text);
                }
              }, 70);
            }, nextGlitchDelay);
          }
        }
      }, scrambleSpeed);
    }, delay);

    return () => {
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [text, scrambleSpeed, delay, continuous]);

  return <span className={className}>{displayText}</span>;
};
