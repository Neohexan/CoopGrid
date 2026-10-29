"use client";

import React, { useState, useRef, useEffect } from "react";
import { usePathname } from "next/navigation";
import Link from "next/link"; // 👈 IMPORT NEXT.JS LINK
import { AdminSessionData } from "../AdminLogin/AdminLogin";

interface HeaderProps {
  adminSession: AdminSessionData;
  onLogout: () => void;
  onReplayPreloader?: () => void;
  isAudioMuted?: boolean;
  onToggleAudio?: () => void;
  speedMultiplier?: number;
  onSpeedChange?: (speed: number) => void;
}

// Inline SVG Icons
const UserIcon = ({ size = 24, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle>
  </svg>
);
const Volume2Icon = ({ size = 24, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path><path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
  </svg>
);
const VolumeXIcon = ({ size = 24, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><line x1="23" y1="9" x2="17" y2="15"></line><line x1="17" y1="9" x2="23" y2="15"></line>
  </svg>
);
const RotateCcwIcon = ({ size = 24, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="1 4 1 10 7 10"></polyline><path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"></path>
  </svg>
);
const ShieldCheckIcon = ({ size = 24, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path><path d="m9 12 2 2 4-4"></path>
  </svg>
);
const KeyIcon = ({ size = 24, color = "currentColor", className = "" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <path d="m15.5 7.5 2.3 2.3a1 1 0 0 0 1.4 0l2.1-2.1a1 1 0 0 0 0-1.4L19 4"></path><path d="m21 2-9.6 9.6"></path><circle cx="7.5" cy="15.5" r="5.5"></circle>
  </svg>
);
const ActivityIcon = ({ size = 24, color = "currentColor", className = "" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
  </svg>
);
const DatabaseIcon = ({ size = 24, color = "currentColor", className = "" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path>
  </svg>
);
const SlidersIcon = ({ size = 24, color = "currentColor", className = "" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <line x1="4" y1="21" x2="4" y2="14"></line><line x1="4" y1="10" x2="4" y2="3"></line><line x1="12" y1="21" x2="12" y2="12"></line><line x1="12" y1="8" x2="12" y2="3"></line><line x1="20" y1="21" x2="20" y2="16"></line><line x1="20" y1="12" x2="20" y2="3"></line><line x1="1" y1="14" x2="7" y2="14"></line><line x1="9" y1="8" x2="15" y2="8"></line><line x1="17" y1="16" x2="23" y2="16"></line>
  </svg>
);
const LogOutIcon = ({ size = 24, color = "currentColor", className = "" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line>
  </svg>
);
const CheckCircle2Icon = ({ size = 24, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="10"></circle><path d="m9 12 2 2 4-4"></path>
  </svg>
);

export const Header: React.FC<HeaderProps> = ({
  adminSession,
  onLogout,
  onReplayPreloader,
  isAudioMuted = false,
  onToggleAudio,
}) => {
  const pathname = usePathname();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const [sessionToast, setSessionToast] = useState<string | null>(null);

  const userDropdownRef = useRef<HTMLDivElement>(null);
  const menuDropdownRef = useRef<HTMLDivElement>(null);
  const userButtonRef = useRef<HTMLButtonElement>(null);
  const menuButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;
      if (
        isUserMenuOpen &&
        userDropdownRef.current &&
        !userDropdownRef.current.contains(target) &&
        userButtonRef.current &&
        !userButtonRef.current.contains(target)
      ) {
        setIsUserMenuOpen(false);
      }

      if (
        isMenuOpen &&
        menuDropdownRef.current &&
        !menuDropdownRef.current.contains(target) &&
        menuButtonRef.current &&
        !menuButtonRef.current.contains(target)
      ) {
        setIsMenuOpen(false);
      }
    };

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        setIsMenuOpen(false);
        setIsUserMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [isUserMenuOpen, isMenuOpen]);

  const triggerToast = (msg: string) => {
    setSessionToast(msg);
    setTimeout(() => setSessionToast(null), 3000);
  };

  const navItems = [
    { name: "Dashboard", href: "/" },
    { name: "Applications", href: "/applications" },
    { name: "Approved", href: "/approved" },
    { name: "Blacklisted", href: "/blacklist" },
    { name: "Broadcast", href: "/broadcast" },
    { name: "Complains", href: "/complains" },
  ];

  const poweredByLinks = [
    { label: "Ministry of Cooperation", href: "https://cooperation.gov.in/" },
    { label: "NITI Aayog", href: "https://niti.gov.in/" },
    { label: "Department of Agriculture", href: "https://agrimachinery.nic.in/" },
  ];

  return (
    <>
      <style>{`
        .cg-header {
          position: sticky; top: 0; z-index: 40; width: 100%; max-width: 100vw; box-sizing: border-box;
          display: flex; align-items: center; justify-content: space-between;
          padding: 10px 12px; background-color: rgba(245, 245, 245, 0.95); backdrop-filter: blur(12px); 
          border-bottom: 1px solid rgba(0,0,0,0.08); user-select: none;
        }
        @media (min-width: 640px) { .cg-header { padding: 16px 24px; } }

        .cg-brand-link { display: flex; align-items: center; gap: 10px; text-decoration: none; cursor: pointer; min-width: 0; }
        .cg-brand-logo { width: 36px; height: 36px; object-fit: contain; flex-shrink: 0; }
        @media (min-width: 640px) { .cg-brand-logo { width: 46px; height: 46px; } }
        
        .cg-brand-title { 
          font-weight: bold; font-size: 11px; letter-spacing: 0.03em; text-transform: uppercase; 
          color: #171717; margin: 0; line-height: 1.15; font-family: sans-serif; white-space: nowrap; 
          overflow: hidden; text-overflow: ellipsis; max-width: 160px;
        }
        @media (min-width: 380px) { .cg-brand-title { font-size: 12.5px; max-width: 200px; } }
        @media (min-width: 640px) { .cg-brand-title { font-size: 16px; max-width: none; } }
        
        .cg-accent-strip { display: flex; align-items: center; margin-top: 4px; }
        .cg-accent-bar { width: 38px; height: 3.5px; border-radius: 9999px; overflow: hidden; display: flex; }
        @media (min-width: 640px) { .cg-accent-bar { width: 52px; height: 4.5px; } }
        .cg-accent-blue { width: 33.33%; height: 100%; background-color: #0284c7; }
        .cg-accent-green { width: 33.33%; height: 100%; background-color: #10b981; }
        .cg-accent-lime { width: 33.33%; height: 100%; background-color: #84cc16; }

        .cg-controls { display: flex; align-items: center; gap: 6px; flex-shrink: 0; }
        @media (min-width: 640px) { .cg-controls { gap: 10px; } }

        .cg-btn-util {
          display: flex; align-items: center; gap: 4px; padding: 5px 8px; border-radius: 9999px; font-family: monospace; font-size: 10px;
          background-color: #ffffff; border: 1px solid #d4d4d8; color: #3f3f46; cursor: pointer; transition: all 0.2s ease;
        }
        @media (min-width: 640px) { .cg-btn-util { padding: 6px 12px; font-size: 11px; gap: 6px; } }
        .cg-btn-util:hover { border-color: #171717; color: #171717; }
        .cg-btn-util-hide-mobile { display: none; }
        @media (min-width: 768px) { .cg-btn-util-hide-mobile { display: flex; } }

        .cg-user-btn {
          position: relative; padding: 6px; border-radius: 8px; border: 1px solid #d4d4d8; display: flex; align-items: center; justify-content: center;
          background-color: #ffffff; color: #27272a; cursor: pointer; transition: all 0.2s ease;
        }
        @media (min-width: 640px) { .cg-user-btn { padding: 8px; } }
        .cg-user-btn:hover { background-color: #fafafa; border-color: #27272a; }
        .cg-user-btn.active { background-color: #171717; color: #ffffff; border-color: #171717; }
        
        .cg-online-dot-wrap { position: absolute; top: -2px; right: -2px; display: flex; width: 8px; height: 8px; }
        .cg-online-ping { position: absolute; display: inline-flex; width: 100%; height: 100%; border-radius: 50%; background-color: #34d399; opacity: 0.75; animation: cg-ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite; }
        .cg-online-dot { position: relative; display: inline-flex; border-radius: 50%; width: 8px; height: 8px; background-color: #10b981; border: 1px solid #ffffff; }
        @keyframes cg-ping { 75%, 100% { transform: scale(2); opacity: 0; } }

        .cg-modal {
          position: absolute; right: 0; margin-top: 10px; border-radius: 16px; border: 1px solid rgba(255,255,255,0.1); 
          box-shadow: 0 25px 50px -12px rgba(0,0,0,0.3); color: #ffffff; z-index: 50; max-width: calc(100vw - 20px);
        }
        
        .cg-user-modal { width: 270px; padding: 14px; background-color: rgba(24,24,27,0.98); backdrop-filter: blur(16px); }
        @media (min-width: 640px) { .cg-user-modal { width: 330px; padding: 20px; } }
        
        .cg-um-header { display: flex; align-items: flex-start; gap: 10px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .cg-um-avatar { position: relative; width: 38px; height: 38px; border-radius: 10px; background: #27272a; border: 1px solid rgba(255,255,255,0.1); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
        .cg-um-avatar-dot { position: absolute; bottom: -2px; right: -2px; width: 10px; height: 10px; background-color: #10b981; border-radius: 50%; border: 2px solid #18181b; }
        .cg-um-info { min-width: 0; flex: 1; }
        .cg-um-name-row { display: flex; align-items: center; justify-content: space-between; gap: 6px; }
        .cg-um-name { font-size: 13px; font-family: sans-serif; font-weight: 600; color: #ffffff; margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .cg-um-badge { font-size: 9.5px; font-family: monospace; padding: 1px 5px; background-color: rgba(16,185,129,0.2); color: #34d399; border-radius: 9999px; flex-shrink: 0; }
        .cg-um-email { font-size: 10px; font-family: monospace; color: #a1a1aa; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin: 2px 0 0 0; }
        .cg-um-clearance { font-size: 9.5px; font-family: monospace; color: #84cc16; margin: 3px 0 0 0; display: flex; align-items: center; gap: 4px; }
        
        .cg-um-telemetry { margin: 10px 0; padding: 10px; border-radius: 10px; background-color: rgba(255,255,255,0.04); font-family: monospace; font-size: 10px; }
        .cg-um-tel-row { display: flex; justify-content: space-between; color: #a1a1aa; margin-bottom: 4px; gap: 8px; }
        .cg-um-tel-row:last-child { margin-bottom: 0; }
        .cg-um-tel-val { color: #e4e4e7; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .cg-um-tel-val-ok { color: #34d399; font-weight: 600; }

        .cg-um-actions { display: flex; flex-direction: column; gap: 2px; font-size: 11.5px; font-family: sans-serif; }
        .cg-um-btn { width: 100%; display: flex; align-items: center; gap: 8px; padding: 7px 10px; border-radius: 6px; color: #d4d4d8; background: transparent; border: none; cursor: pointer; text-align: left; }
        .cg-um-btn:hover { color: #ffffff; background-color: rgba(255,255,255,0.08); }
        .cg-um-icon { color: #a1a1aa; }
        
        .cg-um-toast { margin-top: 8px; padding: 6px 8px; background-color: rgba(16,185,129,0.1); border: 1px solid rgba(16,185,129,0.3); border-radius: 6px; font-size: 10.5px; color: #34d399; display: flex; align-items: center; gap: 6px; font-family: monospace; }
        .cg-um-footer { margin-top: 10px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.1); display: flex; align-items: center; justify-content: space-between; }
        .cg-um-logout { display: flex; align-items: center; gap: 4px; font-size: 11px; font-family: sans-serif; color: #f87171; background: transparent; border: none; cursor: pointer; padding: 0; }
        .cg-um-logout:hover { color: #fca5a5; }
        .cg-um-version { font-size: 9px; font-family: monospace; color: #71717a; }

        .cg-burger-btn {
          padding: 8px 10px; background-color: #18181b; color: #ffffff; border-radius: 8px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: background 0.2s ease;
        }
        @media (min-width: 640px) { .cg-burger-btn { padding: 9px 12px; } }
        .cg-burger-btn:hover { background-color: #000000; }
        
        .cg-burger-icon { width: 16px; height: 10px; display: flex; flex-direction: column; justify-content: space-between; align-items: flex-end; }
        @media (min-width: 640px) { .cg-burger-icon { width: 18px; height: 11px; } }
        .cg-bar { height: 2px; background-color: #ffffff; border-radius: 9999px; transition: all 0.3s ease; }
        
        .cg-bar-top { width: 100%; }
        .cg-bar-bot { width: 60%; }
        .cg-burger-btn:hover .cg-bar-bot { width: 100%; }
        
        .cg-burger-icon.open .cg-bar-top { width: 100%; transform: translateY(4px) rotate(45deg); }
        .cg-burger-icon.open .cg-bar-bot { width: 100%; transform: translateY(-4px) rotate(-45deg); }

        .cg-nav-modal { width: 270px; padding: 18px; background-color: rgba(28,28,31,0.98); backdrop-filter: blur(24px); }
        @media (min-width: 640px) { .cg-nav-modal { width: 310px; padding: 24px; } }
        
        .cg-nav-header { padding-bottom: 8px; border-bottom: 1px solid rgba(255,255,255,0.1); margin-bottom: 4px; }
        .cg-nav-title { font-size: 11px; font-family: monospace; text-transform: uppercase; letter-spacing: 0.1em; color: #a1a1aa; margin: 0; }
        
        .cg-nav-links { display: flex; flex-direction: column; }
        .cg-nav-item {
          display: flex; align-items: center; justify-content: space-between; padding: 9px 0; border-bottom: 1px solid rgba(255,255,255,0.05); text-decoration: none; cursor: pointer;
        }
        .cg-nav-item-text { font-size: 18px; font-weight: 300; font-family: sans-serif; letter-spacing: -0.025em; margin: 0; transition: transform 0.2s; }
        @media (min-width: 640px) { .cg-nav-item-text { font-size: 22px; } }
        .cg-nav-item-arrow { font-size: 16px; }
        @media (min-width: 640px) { .cg-nav-item-arrow { font-size: 20px; } }
        
        .cg-nav-item.normal { color: #a1a1aa; }
        .cg-nav-item.normal:hover { color: #ffffff; padding-left: 4px; }
        .cg-nav-item.normal .cg-nav-item-arrow { color: #71717a; }
        .cg-nav-item.active { color: #ffffff; font-weight: 500; padding-left: 4px; }

        .cg-nav-media { margin-top: 10px; padding-top: 10px; border-top: 1px solid rgba(255,255,255,0.05); }
        .cg-nav-media-title { font-size: 10.5px; font-family: monospace; color: #a1a1aa; margin: 0 0 6px 0; }
        .cg-nav-media-list { display: flex; flex-direction: column; gap: 5px; }
        .cg-nav-media-link { display: flex; align-items: center; gap: 6px; font-size: 11px; font-family: monospace; color: #d4d4d8; text-decoration: none; }
        .cg-nav-media-link:hover { color: #ffffff; }
      `}</style>
      
      <header className="cg-header">
        {/* 1. LEFT: Branding & Logo */}
        <div style={{ display: "flex", alignItems: "center", minWidth: 0, flex: 1, marginRight: "8px" }}>
          {/* Changed to Next.js Link */}
          <Link href="/" className="cg-brand-link">
            <div style={{ position: "relative", flexShrink: 0 }}>
              <img
                src="/logo.png"
                alt="CoopGrid Emblem"
                className="cg-brand-logo"
              />
            </div>

            <div style={{ display: "flex", flexDirection: "column", minWidth: 0 }}>
              <h1 className="cg-brand-title">COOPGRID ADMINISTRATOR</h1>
              <div className="cg-accent-strip">
                <span className="cg-accent-bar">
                  <span className="cg-accent-blue" />
                  <span className="cg-accent-green" />
                  <span className="cg-accent-lime" />
                </span>
              </div>
            </div>
          </Link>
        </div>

        {/* 2. RIGHT: Controls Group */}
        <div className="cg-controls">
          {onToggleAudio && (
            <button
              onClick={onToggleAudio}
              className="cg-btn-util cg-btn-util-hide-mobile"
              title={isAudioMuted ? "Unmute sounds" : "Mute sounds"}
            >
              {isAudioMuted ? (
                <VolumeXIcon size={13} color="#a1a1aa" />
              ) : (
                <Volume2Icon size={13} color="#059669" />
              )}
              <span>{isAudioMuted ? "Muted" : "Audio On"}</span>
            </button>
          )}

          {onReplayPreloader && (
            <button
              onClick={onReplayPreloader}
              className="cg-btn-util"
              title="Replay Kinetic Preloader"
            >
              <RotateCcwIcon size={13} color="#3f3f46" />
              <span className="hidden sm:inline">Replay Loader</span>
            </button>
          )}

          {/* User Profile Button & Floating Admin Info Modal */}
          <div style={{ position: "relative" }}>
            <button
              ref={userButtonRef}
              onClick={() => {
                setIsUserMenuOpen((prev) => !prev);
                if (isMenuOpen) setIsMenuOpen(false);
              }}
              aria-expanded={isUserMenuOpen}
              className={`cg-user-btn ${isUserMenuOpen ? "active" : ""}`}
              title={`Logged in as ${adminSession.adminId}`}
            >
              <UserIcon size={16} />
              <span className="cg-online-dot-wrap">
                <span className="cg-online-ping" />
                <span className="cg-online-dot" />
              </span>
            </button>

            {isUserMenuOpen && (
              <div ref={userDropdownRef} className="cg-modal cg-user-modal">
                <div className="cg-um-header">
                  <div className="cg-um-avatar">
                    <UserIcon size={18} color="#34d399" />
                    <span className="cg-um-avatar-dot" />
                  </div>
                  <div className="cg-um-info">
                    <div className="cg-um-name-row">
                      <h3 className="cg-um-name">{adminSession.adminId}</h3>
                      <span className="cg-um-badge">VERIFIED</span>
                    </div>
                    <p className="cg-um-email">
                      {adminSession.uuid
                        ? `UUID: ${adminSession.uuid.slice(0, 18)}...`
                        : `${adminSession.adminId.toLowerCase()}@neohexane.com`}
                    </p>
                    <p className="cg-um-clearance">
                      <ShieldCheckIcon size={11} /> Authenticated Admin
                    </p>
                  </div>
                </div>

                <div className="cg-um-telemetry">
                  <div className="cg-um-tel-row">
                    <span>Admin ID:</span>
                    <span className="cg-um-tel-val">{adminSession.adminId}</span>
                  </div>
                  <div className="cg-um-tel-row">
                    <span>API Gateway:</span>
                    <span className="cg-um-tel-val-ok">
                      admin-api.neohexane.com
                    </span>
                  </div>
                  <div className="cg-um-tel-row">
                    <span>Login Time:</span>
                    <span className="cg-um-tel-val">
                      {adminSession.loginTime} • Active
                    </span>
                  </div>
                </div>

                <div className="cg-um-actions">
                  <button
                    onClick={() =>
                      triggerToast(`Active ID: ${adminSession.adminId}`)
                    }
                    className="cg-um-btn"
                  >
                    <KeyIcon size={13} className="cg-um-icon" />{" "}
                    <span>API Credentials</span>
                  </button>
                  <button
                    onClick={() => triggerToast("Telemetry Matrix refreshed")}
                    className="cg-um-btn"
                  >
                    <ActivityIcon size={13} className="cg-um-icon" />{" "}
                    <span>Telemetry Stream</span>
                  </button>
                  <button
                    onClick={() =>
                      triggerToast("Connected: admin-api.neohexane.com")
                    }
                    className="cg-um-btn"
                  >
                    <DatabaseIcon size={13} className="cg-um-icon" />{" "}
                    <span>Workspace: Prod</span>
                  </button>
                  <button
                    onClick={() => triggerToast("Preferences saved")}
                    className="cg-um-btn"
                  >
                    <SlidersIcon size={13} className="cg-um-icon" />{" "}
                    <span>Preferences</span>
                  </button>
                </div>

                {sessionToast && (
                  <div className="cg-um-toast">
                    <CheckCircle2Icon size={13} /> <span>{sessionToast}</span>
                  </div>
                )}

                <div className="cg-um-footer">
                  <button
                    onClick={() => {
                      setIsUserMenuOpen(false);
                      onLogout();
                    }}
                    className="cg-um-logout"
                  >
                    <LogOutIcon size={13} /> <span>Terminate Session</span>
                  </button>
                  <span className="cg-um-version">v2.4.0-CORE</span>
                </div>
              </div>
            )}
          </div>

          {/* Hamburger Button & Drawer */}
          <div style={{ position: "relative" }}>
            <button
              ref={menuButtonRef}
              onClick={() => {
                setIsMenuOpen((prev) => !prev);
                if (isUserMenuOpen) setIsUserMenuOpen(false);
              }}
              aria-expanded={isMenuOpen}
              className="cg-burger-btn"
            >
              <div className={`cg-burger-icon ${isMenuOpen ? "open" : ""}`}>
                <span className="cg-bar cg-bar-top" />
                <span className="cg-bar cg-bar-bot" />
              </div>
            </button>

            {isMenuOpen && (
              <div ref={menuDropdownRef} className="cg-modal cg-nav-modal">
                <div className="cg-nav-header">
                  <h4 className="cg-nav-title">Navigation</h4>
                </div>

                <nav className="cg-nav-links">
                  {navItems.map((item) => {
                    const isCurrent = pathname === item.href;
                    
                    return (
                      // 👈 CHANGED <a> TO <Link> HERE
                      <Link
                        key={item.name}
                        href={item.href}
                        onClick={() => setIsMenuOpen(false)}
                        className={`cg-nav-item ${
                          isCurrent ? "active" : "normal"
                        }`}
                      >
                        <p className="cg-nav-item-text">{item.name}</p>
                        <span className="cg-nav-item-arrow">
                          {isCurrent ? "↗" : "→"}
                        </span>
                      </Link>
                    );
                  })}
                </nav>

                <div className="cg-nav-media">
                  <h4 className="cg-nav-media-title">Powered By:</h4>
                  <div className="cg-nav-media-list">
                    {/* External links stay as standard <a> tags */}
                    {poweredByLinks.map((link) => (
                      <a
                        key={link.label}
                        href={link.href}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="cg-nav-media-link"
                      >
                        <span style={{ color: "#34d399" }}>↗</span>{" "}
                        <span>{link.label}</span>
                      </a>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </header>
    </>
  );
};
