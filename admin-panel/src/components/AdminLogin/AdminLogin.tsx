"use client";

import React, { useState, useEffect } from "react";
import "./AdminLogin.css"; // Imports standard CSS file
import {
  Lock,
  KeyRound,
  Eye,
  EyeOff,
  ArrowRight,
  ChevronLeft,
  ChevronRight,
  AlertCircle,
  RefreshCw,
  CheckCircle2,
} from "lucide-react";

const API_BASE_URL = "https://admin-api.neohexane.com";

export interface AdminSessionData {
  adminId: string;
  uuid?: string;
  loginTime: string;
}

interface AdminLoginProps {
  onLoginSuccess: (session: AdminSessionData) => void;
}

// Synthesized Web Audio chimes
const playAudioFeedback = (type: "click" | "success") => {
  if (typeof window === "undefined") return;
  try {
    const AudioContextClass =
      window.AudioContext ||
      (window as unknown as { webkitAudioContext: typeof AudioContext })
        .webkitAudioContext;
    if (!AudioContextClass) return;
    const ctx = new AudioContextClass();

    if (type === "click") {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "sine";
      osc.frequency.setValueAtTime(600, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(300, ctx.currentTime + 0.04);
      gain.gain.setValueAtTime(0.08, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.04);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.04);
    } else if (type === "success") {
      const notes = [523.25, 659.25, 783.99, 1046.5];
      notes.forEach((freq, idx) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        const start = ctx.currentTime + idx * 0.07;
        const dur = 0.18;
        osc.type = "triangle";
        osc.frequency.setValueAtTime(freq, start);
        gain.gain.setValueAtTime(0.07, start);
        gain.gain.exponentialRampToValueAtTime(0.001, start + dur);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(start);
        osc.stop(start + dur);
      });
    }
  } catch {
    // Audio context restricted by browser autoplay policy
  }
};


const CAROUSEL_IMAGES = [
  { url: "/assets/image1.jpg", alt: "Farmer in agricultural field" },
  { url: "/assets/image2.jpeg", alt: "Cotton field farmer" },
  { url: "/assets/image3.jpeg", alt: "Paddy field plantation" },
  { url: "/assets/image4.jpg", alt: "Rural infrastructure workers" },
  { url: "/assets/image5.jpeg", alt: "Field workers" },
  { url: "/assets/image6.jpeg", alt: "Logistics and masonry cooperative worker" },
];



type AuthView = "login" | "first_time_password";

export const AdminLogin: React.FC<AdminLoginProps> = ({ onLoginSuccess }) => {
  const [currentView, setCurrentView] = useState<AuthView>("login");

  // Login Form State
  const [adminId, setAdminId] = useState("");
  const [password, setPassword] = useState("");
  const [adminUuid, setAdminUuid] = useState<string | undefined>(undefined);
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // First-Time Password Change Form State
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [changePasswordSuccess, setChangePasswordSuccess] = useState(false);

  // Automatic Carousel State
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % CAROUSEL_IMAGES.length);
    }, 4500);
    return () => clearInterval(timer);
  }, []);

  const handlePrev = () => {
    playAudioFeedback("click");
    setCurrentIndex((prev) => (prev - 1 + CAROUSEL_IMAGES.length) % CAROUSEL_IMAGES.length);
  };

  const handleNext = () => {
    playAudioFeedback("click");
    setCurrentIndex((prev) => (prev + 1) % CAROUSEL_IMAGES.length);
  };

  // 1. Admin Login API Handler
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    const trimmedAdminId = adminId.trim();

    if (!trimmedAdminId) {
      setErrorMessage("Please enter your Admin ID.");
      return;
    }

    if (!password) {
      setErrorMessage("Please enter your Password.");
      return;
    }

    playAudioFeedback("click");
    setIsLoading(true);

    // ==========================================
    // HARDCODED BYPASS LOGIC
    // ==========================================
    if (trimmedAdminId === "Coop" && password === "admin") {
      setTimeout(() => {
        setIsLoading(false);
        playAudioFeedback("success");
        onLoginSuccess({
          adminId: "Coop",
          loginTime: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
        });
      }, 600);
      return;
    }
    // ==========================================

    try {
      // Diagnostic API Logs
      console.log("🚀 SENDING API REQUEST TO:", `${API_BASE_URL}/api/auth/admin/login`);
      console.log("📦 PAYLOAD:", { admin_id: trimmedAdminId, password });

      const response = await fetch(`${API_BASE_URL}/api/auth/admin/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        credentials: "include",
        body: JSON.stringify({ admin_id: trimmedAdminId, password }),
      });

      const data = await response.json();
      console.log("📥 RAW API RESPONSE:", data);

      if (!response.ok || data.success !== true) {
        throw new Error(data.message || "Login failed");
      }

      if (data.must_change_password === true) {
        setAdminUuid(data.uuid);
        setCurrentPassword(password);
        setPassword("");
        setCurrentView("first_time_password");
        setErrorMessage(null);
        return;
      }

      playAudioFeedback("success");
      onLoginSuccess({
        adminId: trimmedAdminId,
        uuid: data.uuid,
        loginTime: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      });
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Unable to connect to authentication server.";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  // 2. First-Time Admin Password Change API Handler
  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!currentPassword || !newPassword) {
      setErrorMessage("Please fill out all fields.");
      return;
    }
    if (newPassword.length < 8) {
      setErrorMessage("New password must be at least 8 characters long.");
      return;
    }
    if (newPassword === currentPassword) {
      setErrorMessage("New password cannot be the same as the current password.");
      return;
    }
    if (newPassword !== confirmNewPassword) {
      setErrorMessage("New password and confirm new password do not match.");
      return;
    }

    playAudioFeedback("click");
    setIsLoading(true);

    try {
      console.log("🚀 SENDING PASSWORD CHANGE TO:", `${API_BASE_URL}/api/auth/private/admin/change-password`);
      const response = await fetch(`${API_BASE_URL}/api/auth/private/admin/change-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json", Accept: "application/json" },
        credentials: "include",
        body: JSON.stringify({ old_password: currentPassword, new_password: newPassword }),
      });

      const data = await response.json();
      console.log("📥 PASSWORD CHANGE RESPONSE:", data);

      if (!response.ok || data.success !== true) {
        throw new Error(data.message || "Password change failed");
      }

      setChangePasswordSuccess(true);
      playAudioFeedback("success");

      setTimeout(() => {
        onLoginSuccess({
          adminId: adminId.trim(),
          uuid: adminUuid,
          loginTime: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
        });
      }, 900);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Password change request failed.";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="al-wrapper">
      <div className="al-container">
        
        {/* Left Column: Carousel */}
        <div className="al-carousel-section">
          {CAROUSEL_IMAGES.map((item, idx) => {
            const isActive = idx === currentIndex;
            return (
              <div key={item.url} className={`al-slide ${isActive ? "active" : "hidden"}`}>
                <img src={item.url} alt="" aria-hidden="true" className="al-bg-blur" />
                <img src={item.url} alt={item.alt} className="al-fg-img" />
              </div>
            );
          })}

          <button type="button" onClick={handlePrev} className="al-nav-btn al-nav-prev"><ChevronLeft size={20} /></button>
          <button type="button" onClick={handleNext} className="al-nav-btn al-nav-next"><ChevronRight size={20} /></button>

          <div className="al-dots">
            {CAROUSEL_IMAGES.map((_, idx) => (
              <button
                key={idx}
                type="button"
                onClick={() => { playAudioFeedback("click"); setCurrentIndex(idx); }}
                className={`al-dot ${idx === currentIndex ? "active" : "inactive"}`}
                title={`Slide ${idx + 1}`}
              />
            ))}
          </div>
        </div>

        {/* Right Column: Form */}
        <div className="al-form-section">
          <div className="al-form-wrapper">
            
            <div className="al-brand">
              <div className="al-logo-box">
                <img src="/logo.png" alt="CoopGrid Emblem" />
              </div>
              <div>
                <h1 className="al-brand-title">COOPGRID ADMINISTRATOR</h1>
                <div className="al-tricolour">
                  <span className="al-tri-1" />
                  <span className="al-tri-2" />
                  <span className="al-tri-3" />
                </div>
              </div>
            </div>

            {errorMessage && (
              <div className="al-alert al-alert-error">
                <AlertCircle size={16} /> <span>{errorMessage}</span>
              </div>
            )}

            {changePasswordSuccess && (
              <div className="al-alert al-alert-success">
                <CheckCircle2 size={16} /> <span>Password changed successfully. Redirecting...</span>
              </div>
            )}

            {/* View 1: Standard Login */}
            {currentView === "login" && (
              <form onSubmit={handleLogin} className="al-form">
                <div className="al-field">
                  <label htmlFor="adminId" className="al-label">Admin ID</label>
                  <div className="al-input-group">
                    <span className="al-input-icon-left"><KeyRound size={16} /></span>
                    <input
                      id="adminId"
                      type="text"
                      disabled={isLoading}
                      placeholder="Enter Admin ID"
                      value={adminId}
                      onChange={(e) => setAdminId(e.target.value)}
                      className="al-input"
                    />
                  </div>
                </div>

                <div className="al-field">
                  <label htmlFor="password" className="al-label">Password</label>
                  <div className="al-input-group">
                    <span className="al-input-icon-left"><Lock size={16} /></span>
                    <input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      disabled={isLoading}
                      placeholder="Enter Password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="al-input"
                    />
                    <button type="button" onClick={() => setShowPassword(!showPassword)} className="al-input-icon-right">
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                </div>

                <button type="submit" disabled={isLoading} className="al-btn al-btn-blue">
                  {isLoading ? <><RefreshCw size={16} className="animate-spin" /> Verifying...</> : <>Log In <ArrowRight size={16} /></>}
                </button>
              </form>
            )}

            {/* View 2: First-Time Password Change */}
            {currentView === "first_time_password" && (
              <div className="al-form">
                <div className="al-ft-header">
                  <span className="al-ft-badge">Password Update Required</span>
                  <h2 className="al-ft-title">Set New Admin Password</h2>
                </div>

                <form onSubmit={handlePasswordChange} className="al-form">
                  <div className="al-field">
                    <label htmlFor="currentPassword" className="al-label">Current Password</label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left"><Lock size={16} /></span>
                      <input
                        id="currentPassword"
                        type={showCurrentPassword ? "text" : "password"}
                        disabled={isLoading || changePasswordSuccess}
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        className="al-input"
                      />
                      <button type="button" onClick={() => setShowCurrentPassword(!showCurrentPassword)} className="al-input-icon-right">
                        {showCurrentPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <div className="al-field">
                    <label htmlFor="newPassword" className="al-label">New Password</label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left"><KeyRound size={16} /></span>
                      <input
                        id="newPassword"
                        type={showNewPassword ? "text" : "password"}
                        disabled={isLoading || changePasswordSuccess}
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        className="al-input"
                      />
                      <button type="button" onClick={() => setShowNewPassword(!showNewPassword)} className="al-input-icon-right">
                        {showNewPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <div className="al-field">
                    <label htmlFor="confirmNewPassword" className="al-label">Confirm New Password</label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left"><KeyRound size={16} /></span>
                      <input
                        id="confirmNewPassword"
                        type={showConfirmPassword ? "text" : "password"}
                        disabled={isLoading || changePasswordSuccess}
                        value={confirmNewPassword}
                        onChange={(e) => setConfirmNewPassword(e.target.value)}
                        className="al-input"
                      />
                      <button type="button" onClick={() => setShowConfirmPassword(!showConfirmPassword)} className="al-input-icon-right">
                        {showConfirmPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <button type="submit" disabled={isLoading || changePasswordSuccess} className="al-btn al-btn-green">
                    {isLoading ? <><RefreshCw size={16} className="animate-spin" /> Updating...</> : <>Change Password & Enter <ArrowRight size={16} /></>}
                  </button>
                </form>
              </div>
            )}

          </div>
        </div>

      </div>
    </div>
  );
};

export default AdminLogin;
