"use client";

import React, { useState, useRef, useEffect } from "react";
import { usePathname } from "next/navigation";
import Link from "next/link";
import { AdminSessionData } from "../AdminLogin/AdminLogin";

const API_BASE_URL = "https://admin-api.neohexane.com";

interface HeaderProps {
  adminSession: AdminSessionData;
  onLogout: () => void;
  onReplayPreloader?: () => void;
  isAudioMuted?: boolean;
  onToggleAudio?: () => void;
  speedMultiplier?: number;
  onSpeedChange?: (speed: number) => void;
}

// Server response shape for GET /api/profile/admin/profile (excluding _code fields in UI)
interface AdminProfileResponse {
  success: boolean;
  user_id: string;
  full_name: string;
  phone_number: string;
  email?: string | null;
  profile_image_media_id?: string | null;
  role?: string | null;
  is_active?: boolean;
  state_name?: string | null;
  district_name?: string | null;
  block_name?: string | null;
  pincode?: string | null;
  street_locality?: string | null;
  house_or_building_no?: string | null;
  created_at?: string | null;
  updated_at?: string | null;
}

const hasValue = (val: unknown): boolean =>
  val !== null && val !== undefined && String(val).trim() !== "";

// Inline SVG Icons (Scaled up defaults)
const UserIcon = ({ size = 26, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
    <circle cx="12" cy="7" r="4"></circle>
  </svg>
);

const Volume2Icon = ({ size = 18, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
    <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
    <path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
  </svg>
);

const VolumeXIcon = ({ size = 18, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
    <line x1="23" y1="9" x2="17" y2="15"></line>
    <line x1="17" y1="9" x2="23" y2="15"></line>
  </svg>
);

const RotateCcwIcon = ({ size = 18, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="1 4 1 10 7 10"></polyline>
    <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"></path>
  </svg>
);

const ShieldCheckIcon = ({ size = 14, color = "currentColor" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
    <path d="m9 12 2 2 4-4"></path>
  </svg>
);

const LogOutIcon = ({ size = 16, color = "currentColor", className = "" }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
    <polyline points="16 17 21 12 16 7"></polyline>
    <line x1="21" y1="12" x2="9" y2="12"></line>
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

  // Server-fetched Admin Profile & Streamed Avatar State
  const [adminProfile, setAdminProfile] = useState<AdminProfileResponse | null>(null);
  const [avatarObjectUrl, setAvatarObjectUrl] = useState<string | null>(null);

  const userDropdownRef = useRef<HTMLDivElement>(null);
  const menuDropdownRef = useRef<HTMLDivElement>(null);
  const userButtonRef = useRef<HTMLButtonElement>(null);
  const menuButtonRef = useRef<HTMLButtonElement>(null);

  // 1. Fetch Admin Profile from GET /api/profile/admin/profile
  useEffect(() => {
    let isCancelled = false;

    const fetchAdminProfile = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/api/profile/admin/profile`, {
          method: "GET",
          headers: {
            Accept: "application/json",
          },
          credentials: "include",
        });

        if (!response.ok) return;
        const data: AdminProfileResponse = await response.json();

        if (!isCancelled && data && data.success) {
          setAdminProfile(data);
        }
      } catch (err) {
        console.error("Failed to fetch admin profile in header:", err);
      }
    };

    fetchAdminProfile();

    return () => {
      isCancelled = true;
    };
  }, [adminSession.adminId]);

  // 2. Stream Profile Picture from GET /api/media/stream/{mediaId}
  const activeMediaId =
    adminProfile?.profile_image_media_id || adminSession.profileImageMediaId || null;

  useEffect(() => {
    if (!activeMediaId) {
      setAvatarObjectUrl(null);
      return;
    }

    let isCancelled = false;
    let createdUrl: string | null = null;

    const loadProfileImage = async () => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/api/media/stream/${encodeURIComponent(activeMediaId)}`,
          {
            method: "GET",
            credentials: "include",
          }
        );

        if (!response.ok) {
          throw new Error(`Media stream failed with status ${response.status}`);
        }

        const blob = await response.blob();
        if (isCancelled) return;

        createdUrl = URL.createObjectURL(blob);
        setAvatarObjectUrl(createdUrl);
      } catch (err) {
        console.error("Failed to stream admin profile image:", err);
        if (!isCancelled) {
          setAvatarObjectUrl(null);
        }
      }
    };

    loadProfileImage();

    return () => {
      isCancelled = true;
      if (createdUrl) {
        URL.revokeObjectURL(createdUrl);
      }
    };
  }, [activeMediaId]);

  // Close menus on outside click or Escape key
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

  // Resolved Admin Display Values (No _code fields included)
  const displayUserId = adminProfile?.user_id || adminSession.adminId;
  const displayFullName = adminProfile?.full_name || adminSession.fullName || displayUserId;
  const displayRole = adminProfile?.role || adminSession.role || null;
  const displayEmail = adminProfile?.email || adminSession.email || null;
  const displayPhone = adminProfile?.phone_number || adminSession.phoneNumber || null;
  const displayHouse = adminProfile?.house_or_building_no || null;
  const displayStreet = adminProfile?.street_locality || null;
  const displayBlock = adminProfile?.block_name || adminSession.blockName || null;
  const displayDistrict = adminProfile?.district_name || adminSession.districtName || null;
  const displayState = adminProfile?.state_name || adminSession.stateName || null;
  const displayPincode = adminProfile?.pincode || adminSession.pincode || null;
  const isActiveStatus = adminProfile?.is_active ?? true;

  // Build dynamic info blocks (automatically hides any missing field)
  const personalInfoRows = [
    { label: "User ID", value: displayUserId },
    { label: "Phone Number", value: displayPhone },
    { label: "Login Time", value: adminSession.loginTime },
  ].filter((row) => hasValue(row.value));

  const addressInfoRows = [
    { label: "House / Bldg", value: displayHouse },
    { label: "Street / Locality", value: displayStreet },
    { label: "Block", value: displayBlock },
    { label: "District", value: displayDistrict },
    { label: "State", value: displayState },
    { label: "Pincode", value: displayPincode },
  ].filter((row) => hasValue(row.value));

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
          position: sticky;
          top: 0;
          z-index: 40;
          width: 100%;
          max-width: 100vw;
          box-sizing: border-box;
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 14px 18px;
          background-color: rgba(245, 245, 245, 0.96);
          backdrop-filter: blur(14px);
          border-bottom: 1px solid rgba(0, 0, 0, 0.09);
          user-select: none;
        }
        @media (min-width: 640px) {
          .cg-header {
            padding: 20px 36px;
          }
        }

        /* Enlarged Branding & Logo */
        .cg-brand-link {
          display: flex;
          align-items: center;
          gap: 14px;
          text-decoration: none;
          cursor: pointer;
          min-width: 0;
        }
        @media (min-width: 640px) {
          .cg-brand-link {
            gap: 18px;
          }
        }

        .cg-brand-logo {
          width: 52px;
          height: 52px;
          object-fit: contain;
          flex-shrink: 0;
        }
        @media (min-width: 640px) {
          .cg-brand-logo {
            width: 68px;
            height: 68px;
          }
        }

        .cg-brand-title {
          font-weight: 800;
          font-size: 14.5px;
          letter-spacing: 0.04em;
          text-transform: uppercase;
          color: #111827;
          margin: 0;
          line-height: 1.15;
          font-family: sans-serif;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          max-width: 210px;
        }
        @media (min-width: 400px) {
          .cg-brand-title {
            font-size: 16.5px;
            max-width: 260px;
          }
        }
        @media (min-width: 640px) {
          .cg-brand-title {
            font-size: 22px;
            max-width: none;
          }
        }

        .cg-accent-strip {
          display: flex;
          align-items: center;
          margin-top: 6px;
        }
        .cg-accent-bar {
          width: 56px;
          height: 5px;
          border-radius: 9999px;
          overflow: hidden;
          display: flex;
        }
        @media (min-width: 640px) {
          .cg-accent-bar {
            width: 82px;
            height: 6px;
          }
        }
        .cg-accent-blue {
          width: 33.33%;
          height: 100%;
          background-color: #0284c7;
        }
        .cg-accent-green {
          width: 33.33%;
          height: 100%;
          background-color: #10b981;
        }
        .cg-accent-lime {
          width: 33.33%;
          height: 100%;
          background-color: #84cc16;
        }

        /* Enlarged Controls & Buttons */
        .cg-controls {
          display: flex;
          align-items: center;
          gap: 10px;
          flex-shrink: 0;
        }
        @media (min-width: 640px) {
          .cg-controls {
            gap: 14px;
          }
        }

        .cg-btn-util {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 14px;
          border-radius: 9999px;
          font-family: monospace;
          font-size: 12px;
          font-weight: 600;
          background-color: #ffffff;
          border: 1px solid #d4d4d8;
          color: #27272a;
          cursor: pointer;
          transition: all 0.2s ease;
        }
        @media (min-width: 640px) {
          .cg-btn-util {
            padding: 10px 18px;
            font-size: 13.5px;
            gap: 8px;
          }
        }
        .cg-btn-util:hover {
          border-color: #171717;
          color: #171717;
          background-color: #fafafa;
        }
        .cg-btn-util-hide-mobile {
          display: none;
        }
        @media (min-width: 768px) {
          .cg-btn-util-hide-mobile {
            display: flex;
          }
        }

        /* Enlarged User Profile Button */
        .cg-user-btn {
          position: relative;
          width: 44px;
          height: 44px;
          padding: 0;
          border-radius: 12px;
          border: 1.5px solid #d4d4d8;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #ffffff;
          color: #27272a;
          cursor: pointer;
          transition: all 0.2s ease;
          overflow: visible;
        }
        @media (min-width: 640px) {
          .cg-user-btn {
            width: 52px;
            height: 52px;
            border-radius: 14px;
          }
        }
        .cg-user-btn:hover {
          background-color: #fafafa;
          border-color: #18181b;
        }
        .cg-user-btn.active {
          background-color: #18181b;
          color: #ffffff;
          border-color: #18181b;
        }

        .cg-user-btn-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 10.5px;
        }
        @media (min-width: 640px) {
          .cg-user-btn-img {
            border-radius: 12.5px;
          }
        }

        .cg-online-dot-wrap {
          position: absolute;
          top: -3px;
          right: -3px;
          display: flex;
          width: 11px;
          height: 11px;
          z-index: 2;
        }
        .cg-online-ping {
          position: absolute;
          display: inline-flex;
          width: 100%;
          height: 100%;
          border-radius: 50%;
          background-color: #34d399;
          opacity: 0.75;
          animation: cg-ping 1.5s cubic-bezier(0, 0, 0.2, 1) infinite;
        }
        .cg-online-dot {
          position: relative;
          display: inline-flex;
          border-radius: 50%;
          width: 11px;
          height: 11px;
          background-color: #10b981;
          border: 2px solid #ffffff;
        }
        @keyframes cg-ping {
          75%, 100% {
            transform: scale(2);
            opacity: 0;
          }
        }

        /* Floating Modals */
        .cg-modal {
          position: absolute;
          right: 0;
          margin-top: 12px;
          border-radius: 20px;
          border: 1px solid rgba(255, 255, 255, 0.12);
          box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.35);
          color: #ffffff;
          z-index: 50;
          max-width: calc(100vw - 24px);
        }

        .cg-user-modal {
          width: 320px;
          padding: 18px;
          background-color: rgba(24, 24, 27, 0.98);
          backdrop-filter: blur(18px);
        }
        @media (min-width: 640px) {
          .cg-user-modal {
            width: 380px;
            padding: 24px;
          }
        }

        .cg-um-header {
          display: flex;
          align-items: center;
          gap: 14px;
          padding-bottom: 16px;
          border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .cg-um-avatar {
          position: relative;
          width: 58px;
          height: 58px;
          border-radius: 14px;
          background: #27272a;
          border: 1.5px solid rgba(255, 255, 255, 0.15);
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          overflow: visible;
        }
        .cg-um-avatar-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 12.5px;
        }
        .cg-um-avatar-dot {
          position: absolute;
          bottom: -3px;
          right: -3px;
          width: 13px;
          height: 13px;
          background-color: #10b981;
          border-radius: 50%;
          border: 2.5px solid #18181b;
        }
        .cg-um-info {
          min-width: 0;
          flex: 1;
        }
        .cg-um-name-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 8px;
        }
        .cg-um-name {
          font-size: 16px;
          font-family: sans-serif;
          font-weight: 700;
          color: #ffffff;
          margin: 0;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .cg-um-badge {
          font-size: 10.5px;
          font-family: monospace;
          font-weight: 700;
          padding: 2px 8px;
          background-color: rgba(16, 185, 129, 0.2);
          color: #34d399;
          border-radius: 9999px;
          flex-shrink: 0;
        }
        .cg-um-email {
          font-size: 12px;
          font-family: monospace;
          color: #a1a1aa;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          margin: 4px 0 0 0;
        }
        .cg-um-clearance {
          font-size: 11.5px;
          font-family: monospace;
          color: #84cc16;
          margin: 5px 0 0 0;
          display: flex;
          align-items: center;
          gap: 5px;
        }

        /* Info Blocks inside Modal */
        .cg-um-block-label {
          font-size: 10.5px;
          font-family: monospace;
          text-transform: uppercase;
          letter-spacing: 0.08em;
          color: #71717a;
          margin: 14px 0 6px 0;
        }
        .cg-um-telemetry {
          padding: 12px 14px;
          border-radius: 12px;
          background-color: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.05);
          font-family: monospace;
          font-size: 12px;
        }
        .cg-um-tel-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          color: #a1a1aa;
          margin-bottom: 7px;
          gap: 12px;
        }
        .cg-um-tel-row:last-child {
          margin-bottom: 0;
        }
        .cg-um-tel-val {
          color: #f4f4f5;
          font-weight: 600;
          text-align: right;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .cg-um-footer {
          margin-top: 16px;
          padding-top: 14px;
          border-top: 1px solid rgba(255, 255, 255, 0.1);
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
        .cg-um-logout {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          font-weight: 600;
          font-family: sans-serif;
          color: #f87171;
          background: transparent;
          border: none;
          cursor: pointer;
          padding: 0;
        }
        .cg-um-logout:hover {
          color: #fca5a5;
        }
        .cg-um-version {
          font-size: 10.5px;
          font-family: monospace;
          color: #71717a;
        }

        /* Enlarged Hamburger Button */
        .cg-burger-btn {
          width: 44px;
          height: 44px;
          padding: 0;
          background-color: #18181b;
          color: #ffffff;
          border-radius: 12px;
          border: none;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: background 0.2s ease;
        }
        @media (min-width: 640px) {
          .cg-burger-btn {
            width: 52px;
            height: 52px;
            border-radius: 14px;
          }
        }
        .cg-burger-btn:hover {
          background-color: #000000;
        }

        .cg-burger-icon {
          width: 22px;
          height: 13px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          align-items: flex-end;
        }
        @media (min-width: 640px) {
          .cg-burger-icon {
            width: 25px;
            height: 15px;
          }
        }
        .cg-bar {
          height: 2.5px;
          background-color: #ffffff;
          border-radius: 9999px;
          transition: all 0.3s ease;
        }

        .cg-bar-top {
          width: 100%;
        }
        .cg-bar-bot {
          width: 62%;
        }
        .cg-burger-btn:hover .cg-bar-bot {
          width: 100%;
        }

        .cg-burger-icon.open .cg-bar-top {
          width: 100%;
          transform: translateY(5.5px) rotate(45deg);
        }
        .cg-burger-icon.open .cg-bar-bot {
          width: 100%;
          transform: translateY(-5.5px) rotate(-45deg);
        }

        /* Enlarged Navigation Drawer */
        .cg-nav-modal {
          width: 300px;
          padding: 20px;
          background-color: rgba(28, 28, 31, 0.98);
          backdrop-filter: blur(24px);
        }
        @media (min-width: 640px) {
          .cg-nav-modal {
            width: 350px;
            padding: 26px;
          }
        }

        .cg-nav-header {
          padding-bottom: 10px;
          border-bottom: 1px solid rgba(255, 255, 255, 0.1);
          margin-bottom: 6px;
        }
        .cg-nav-title {
          font-size: 12px;
          font-family: monospace;
          text-transform: uppercase;
          letter-spacing: 0.1em;
          color: #a1a1aa;
          margin: 0;
        }

        .cg-nav-links {
          display: flex;
          flex-direction: column;
        }
        .cg-nav-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 11px 0;
          border-bottom: 1px solid rgba(255, 255, 255, 0.05);
          text-decoration: none;
          cursor: pointer;
        }
        .cg-nav-item-text {
          font-size: 20px;
          font-weight: 300;
          font-family: sans-serif;
          letter-spacing: -0.025em;
          margin: 0;
          transition: transform 0.2s;
        }
        @media (min-width: 640px) {
          .cg-nav-item-text {
            font-size: 24px;
          }
        }
        .cg-nav-item-arrow {
          font-size: 18px;
        }
        @media (min-width: 640px) {
          .cg-nav-item-arrow {
            font-size: 22px;
          }
        }

        .cg-nav-item.normal {
          color: #a1a1aa;
        }
        .cg-nav-item.normal:hover {
          color: #ffffff;
          padding-left: 4px;
        }
        .cg-nav-item.normal .cg-nav-item-arrow {
          color: #71717a;
        }
        .cg-nav-item.active {
          color: #ffffff;
          font-weight: 600;
          padding-left: 4px;
        }

        .cg-nav-media {
          margin-top: 12px;
          padding-top: 12px;
          border-top: 1px solid rgba(255, 255, 255, 0.05);
        }
        .cg-nav-media-title {
          font-size: 11.5px;
          font-family: monospace;
          color: #a1a1aa;
          margin: 0 0 8px 0;
        }
        .cg-nav-media-list {
          display: flex;
          flex-direction: column;
          gap: 6px;
        }
        .cg-nav-media-link {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 12.5px;
          font-family: monospace;
          color: #d4d4d8;
          text-decoration: none;
        }
        .cg-nav-media-link:hover {
          color: #ffffff;
        }
      `}</style>

      <header className="cg-header">
        {/* 1. LEFT: Enlarged Branding & Logo */}
        <div
          style={{
            display: "flex",
            alignItems: "center",
            minWidth: 0,
            flex: 1,
            marginRight: "12px",
          }}
        >
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

        {/* 2. RIGHT: Enlarged Controls Group */}
        <div className="cg-controls">
          {onToggleAudio && (
            <button
              onClick={onToggleAudio}
              className="cg-btn-util cg-btn-util-hide-mobile"
              title={isAudioMuted ? "Unmute sounds" : "Mute sounds"}
            >
              {isAudioMuted ? (
                <VolumeXIcon size={18} color="#a1a1aa" />
              ) : (
                <Volume2Icon size={18} color="#059669" />
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
              <RotateCcwIcon size={18} color="#3f3f46" />
              <span className="hidden sm:inline">Replay Loader</span>
            </button>
          )}

          {/* User Profile Button & Floating Admin Profile Modal */}
          <div style={{ position: "relative" }}>
            <button
              ref={userButtonRef}
              onClick={() => {
                setIsUserMenuOpen((prev) => !prev);
                if (isMenuOpen) setIsMenuOpen(false);
              }}
              aria-expanded={isUserMenuOpen}
              className={`cg-user-btn ${isUserMenuOpen ? "active" : ""}`}
              title={`Logged in as ${displayFullName}`}
            >
              {avatarObjectUrl ? (
                <img
                  src={avatarObjectUrl}
                  alt={displayFullName}
                  className="cg-user-btn-img"
                />
              ) : (
                <UserIcon size={24} />
              )}
              <span className="cg-online-dot-wrap">
                <span className="cg-online-ping" />
                <span className="cg-online-dot" />
              </span>
            </button>

            {isUserMenuOpen && (
              <div ref={userDropdownRef} className="cg-modal cg-user-modal">
                {/* Modal Header: Streamed Avatar + Name + Conditional Email & Role */}
                <div className="cg-um-header">
                  <div className="cg-um-avatar">
                    {avatarObjectUrl ? (
                      <img
                        src={avatarObjectUrl}
                        alt={displayFullName}
                        className="cg-um-avatar-img"
                      />
                    ) : (
                      <UserIcon size={28} color="#34d399" />
                    )}
                    <span className="cg-um-avatar-dot" />
                  </div>

                  <div className="cg-um-info">
                    <div className="cg-um-name-row">
                      <h3 className="cg-um-name">{displayFullName}</h3>
                      <span className="cg-um-badge">
                        {isActiveStatus ? "ACTIVE" : "INACTIVE"}
                      </span>
                    </div>

                    {hasValue(displayEmail) && (
                      <p className="cg-um-email">{displayEmail}</p>
                    )}

                    {hasValue(displayRole) && (
                      <p className="cg-um-clearance">
                        <ShieldCheckIcon size={14} /> {displayRole}
                      </p>
                    )}
                  </div>
                </div>

                {/* Block 1: Contact & Session Info (Hidden if empty) */}
                {personalInfoRows.length > 0 && (
                  <>
                    <div className="cg-um-block-label">Admin Details</div>
                    <div className="cg-um-telemetry">
                      {personalInfoRows.map((row) => (
                        <div key={row.label} className="cg-um-tel-row">
                          <span>{row.label}:</span>
                          <span className="cg-um-tel-val">{row.value}</span>
                        </div>
                      ))}
                    </div>
                  </>
                )}

                {/* Block 2: Jurisdiction & Address Info (Hidden if empty, no _code fields) */}
                {addressInfoRows.length > 0 && (
                  <>
                    <div className="cg-um-block-label">Jurisdiction & Address</div>
                    <div className="cg-um-telemetry">
                      {addressInfoRows.map((row) => (
                        <div key={row.label} className="cg-um-tel-row">
                          <span>{row.label}:</span>
                          <span className="cg-um-tel-val">{row.value}</span>
                        </div>
                      ))}
                    </div>
                  </>
                )}

                <div className="cg-um-footer">
                  <button
                    onClick={() => {
                      setIsUserMenuOpen(false);
                      onLogout();
                    }}
                    className="cg-um-logout"
                  >
                    <LogOutIcon size={16} /> <span>Terminate Session</span>
                  </button>
                  <span className="cg-um-version">v2.4.0-CORE</span>
                </div>
              </div>
            )}
          </div>

          {/* Enlarged Hamburger Button & Drawer */}
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
