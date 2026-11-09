"use client";

import React, { useState, useEffect } from "react";
import "./AdminLogin.css";
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
  User,
  Phone,
  Mail,
  MapPin,
  Building,
  Home,
  Upload,
  Shield,
} from "lucide-react";

const API_BASE_URL = "https://admin-api.neohexane.com";

export interface AdminSessionData {
  adminId: string;
  uuid?: string;
  loginTime: string;
  fullName?: string;
  phoneNumber?: string;
  email?: string;
  profileImageMediaId?: string;
  role?: string;
  stateName?: string;
  districtName?: string;
  blockName?: string;
  pincode?: string;
}

interface AdminLoginProps {
  onLoginSuccess: (session: AdminSessionData) => void;
}

// State Name to Standard Code Lookup
const STATE_CODE_MAP: Record<string, string> = {
  "andhra pradesh": "AP",
  "arunachal pradesh": "AR",
  assam: "AS",
  bihar: "BR",
  chhattisgarh: "CG",
  goa: "GA",
  gujarat: "GJ",
  haryana: "HR",
  "himachal pradesh": "HP",
  jharkhand: "JH",
  karnataka: "KA",
  kerala: "KL",
  "madhya pradesh": "MP",
  maharashtra: "MH",
  manipur: "MN",
  meghalaya: "ML",
  mizoram: "MZ",
  nagaland: "NL",
  odisha: "OD",
  punjab: "PB",
  rajasthan: "RJ",
  sikkim: "SK",
  "tamil nadu": "TN",
  telangana: "TG",
  tripura: "TR",
  "uttar pradesh": "UP",
  uttarakhand: "UK",
  "west bengal": "WB",
  delhi: "DL",
  "new delhi": "DL",
  "jammu and kashmir": "JK",
  ladakh: "LA",
  puducherry: "PY",
  chandigarh: "CH",
};

const generateStateCode = (stateName: string): string => {
  if (!stateName) return "";
  const normalized = stateName.trim().toLowerCase();
  if (STATE_CODE_MAP[normalized]) return STATE_CODE_MAP[normalized];
  return stateName.replace(/[^a-zA-Z]/g, "").slice(0, 2).toUpperCase();
};

const generateDistrictCode = (stateCode: string, districtName: string): string => {
  if (!districtName) return "";
  const cleanDist = districtName.replace(/[^a-zA-Z0-9]/g, "").slice(0, 3).toUpperCase();
  return stateCode ? `${stateCode}-${cleanDist}` : cleanDist;
};

const generateBlockCode = (blockName: string): string => {
  if (!blockName) return "";
  const cleanBlock = blockName.replace(/[^a-zA-Z0-9]/g, "").slice(0, 4).toUpperCase();
  return `BLK-${cleanBlock || "01"}`;
};

const createUniqueMediaId = (): string => {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return crypto.randomUUID();
  }
  return `media-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
};

// Upload Media Helper matching media.txt specification
async function uploadMedia(file: File, mediaId: string): Promise<{ media_id: string }> {
  const formData = new FormData();
  formData.append("mediaId", mediaId);
  formData.append("fileName", file.name);
  formData.append("contentType", file.type || "application/octet-stream");
  formData.append("file", file);

  const response = await fetch(`${API_BASE_URL}/api/media/upload`, {
    method: "POST",
    credentials: "include",
    body: formData,
  });

  const data = await response.json();

  if (!response.ok || data.success !== true) {
    throw new Error(data.message || "Media upload failed");
  }

  return data;
}

// Synthesized Web Audio chimes
const playAudioFeedback = (type: "click" | "success") => {
  if (typeof window === "undefined") return;
  try {
    const AudioContextClass =
      window.AudioContext ||
      (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
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

  // First-Time Password Change State
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [changePasswordSuccess, setChangePasswordSuccess] = useState(false);

  // First-Time Admin Profile State (from admin_profile.txt)
  const [role] = useState("ADMIN"); // Kept as-is per specification
  const [fullName, setFullName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [email, setEmail] = useState("");

  // Profile Image Upload State (from media.txt)
  const [profileImageFile, setProfileImageFile] = useState<File | null>(null);
  const [profilePreviewUrl, setProfilePreviewUrl] = useState<string | null>(null);
  const [profileImageMediaId, setProfileImageMediaId] = useState<string>("");
  const [isUploadingMedia, setIsUploadingMedia] = useState(false);
  const [mediaUploadStatus, setMediaUploadStatus] = useState<string>("No file chosen");

  // Dynamic Pincode & Address State
  const [pincode, setPincode] = useState("");
  const [stateName, setStateName] = useState("");
  const [stateCode, setStateCode] = useState("");
  const [districtName, setDistrictName] = useState("");
  const [districtCode, setDistrictCode] = useState("");
  const [blockOptions, setBlockOptions] = useState<string[]>([]);
  const [blockPlaceholder, setBlockPlaceholder] = useState("Waiting for Pincode...");
  const [isBlockDisabled, setIsBlockDisabled] = useState(true);
  const [blockName, setBlockName] = useState("");
  const [blockCode, setBlockCode] = useState("");
  const [streetLocality, setStreetLocality] = useState("");
  const [houseOrBuildingNo, setHouseOrBuildingNo] = useState("");

  // Automatic Carousel State
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % CAROUSEL_IMAGES.length);
    }, 4500);
    return () => clearInterval(timer);
  }, []);

  // Cleanup preview object URL
  useEffect(() => {
    return () => {
      if (profilePreviewUrl) {
        URL.revokeObjectURL(profilePreviewUrl);
      }
    };
  }, [profilePreviewUrl]);

  const handlePrev = () => {
    playAudioFeedback("click");
    setCurrentIndex((prev) => (prev - 1 + CAROUSEL_IMAGES.length) % CAROUSEL_IMAGES.length);
  };

  const handleNext = () => {
    playAudioFeedback("click");
    setCurrentIndex((prev) => (prev + 1) % CAROUSEL_IMAGES.length);
  };

  // Profile Picture Selection & Upload Handler
  const handleProfilePictureChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setProfileImageFile(file);
    setProfileImageMediaId("");
    setErrorMessage(null);

    if (profilePreviewUrl) {
      URL.revokeObjectURL(profilePreviewUrl);
    }
    setProfilePreviewUrl(URL.createObjectURL(file));

    // Attempt immediate upload via /api/media/upload
    setIsUploadingMedia(true);
    setMediaUploadStatus("Uploading photo...");
    try {
      const generatedId = createUniqueMediaId();
      const uploadRes = await uploadMedia(file, generatedId);
      setProfileImageMediaId(uploadRes.media_id);
      setMediaUploadStatus("Photo uploaded ✓");
    } catch {
      // If session requires password change before media upload, defer upload to submit handler
      setMediaUploadStatus(`Selected: ${file.name} (will upload on submit)`);
    } finally {
      setIsUploadingMedia(false);
    }
  };

  // Dynamic Pincode Lookup Handler (using https://api.postalpincode.in/pincode/{pincode})
  const handlePincodeChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const rawValue = e.target.value.replace(/\D/g, "").slice(0, 6);
    setPincode(rawValue);

    // Reset fields if pincode is altered or < 6 digits
    if (rawValue.length < 6) {
      setStateName("");
      setStateCode("");
      setDistrictName("");
      setDistrictCode("");
      setBlockName("");
      setBlockCode("");
      setBlockOptions([]);
      setBlockPlaceholder("Waiting for Pincode...");
      setIsBlockDisabled(true);
      return;
    }

    if (rawValue.length === 6) {
      setBlockPlaceholder("Fetching blocks...");
      setIsBlockDisabled(true);

      try {
        const response = await fetch(`https://api.postalpincode.in/pincode/${rawValue}`);
        const data = await response.json();

        if (Array.isArray(data) && data[0]?.Status === "Success" && Array.isArray(data[0]?.PostOffice)) {
          const postOffices = data[0].PostOffice;
          const fetchedDistrict = String(postOffices[0]?.District || "");
          const fetchedState = String(postOffices[0]?.State || "");

          const autoStateCode = generateStateCode(fetchedState);
          const autoDistrictCode = generateDistrictCode(autoStateCode, fetchedDistrict);

          setDistrictName(fetchedDistrict);
          setStateName(fetchedState);
          setStateCode(autoStateCode);
          setDistrictCode(autoDistrictCode);

          // Extract all 'Block' names and remove duplicates using Set
          let uniqueBlocks: string[] = [
            ...new Set<string>(
              postOffices
                .map((po: { Block?: string }) => String(po.Block || "").trim())
                .filter(Boolean)
            ),
          ];

          // Fallback: If the API returns "NA" for the Block, use the Post Office 'Name' (Locality)
          if (uniqueBlocks.length === 0 || uniqueBlocks[0] === "NA" || uniqueBlocks[0] === "") {
            uniqueBlocks = [
              ...new Set<string>(
                postOffices
                  .map((po: { Name?: string }) => String(po.Name || "").trim())
                  .filter(Boolean)
              ),
            ];
          }

          setBlockOptions(uniqueBlocks);
          setBlockPlaceholder("Select Block");
          setIsBlockDisabled(false);
          setBlockName("");
          setBlockCode("");
        } else {
          alert("Invalid Pincode. Please check again.");
          setStateName("");
          setStateCode("");
          setDistrictName("");
          setDistrictCode("");
          setBlockOptions([]);
          setBlockName("");
          setBlockCode("");
          setBlockPlaceholder("Invalid Pincode");
          setIsBlockDisabled(true);
        }
      } catch (error) {
        console.error("Error fetching pincode data:", error);
        setBlockPlaceholder("Error loading blocks");
        setIsBlockDisabled(true);
      }
    }
  };

  // Block Dropdown Selection Handler (auto-adds block_code)
  const handleBlockChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selected = e.target.value;
    setBlockName(selected);
    setBlockCode(selected ? generateBlockCode(selected) : "");
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
    // HARDCODED TEST BYPASS
    // ==========================================
    if (trimmedAdminId === "Coop" && password === "admin") {
      setTimeout(() => {
        setIsLoading(false);
        playAudioFeedback("success");
        onLoginSuccess({
          adminId: "Coop",
          role: "ADMIN",
          loginTime: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
        });
      }, 600);
      return;
    }
    // ==========================================

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/admin/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ admin_id: trimmedAdminId, password }),
      });

      const data = await response.json();

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
      const msg =
        err instanceof Error ? err.message : "Unable to connect to authentication server.";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  // 2. First-Time Password Change + Media Upload + Admin Profile Creation Handler
  const handlePasswordAndProfileSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    const trimmedAdminId = adminId.trim();
    const trimmedFullName = fullName.trim();
    const trimmedPhone = phoneNumber.trim();
    const trimmedEmail = email.trim();

    // Password Validations
    if (!currentPassword || !newPassword) {
      setErrorMessage("Please fill out your current and new password.");
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

    // Admin Profile Validations (per admin_profile.txt)
    if (trimmedFullName.length < 2 || trimmedFullName.length > 128) {
      setErrorMessage("Full Name is required (between 2 and 128 characters).");
      return;
    }
    if (trimmedPhone.length < 10 || trimmedPhone.length > 20) {
      setErrorMessage("Phone Number is required (between 10 and 20 characters).");
      return;
    }
    if (trimmedEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)) {
      setErrorMessage("Please enter a valid email address.");
      return;
    }

    playAudioFeedback("click");
    setIsLoading(true);

    try {
      // STEP 1: Change Admin Password
      const passwordEndpoint = `${API_BASE_URL}/api/auth/private/admin/change-password${
        adminUuid ? `?uuid=${encodeURIComponent(adminUuid)}` : ""
      }`;

      const pwResponse = await fetch(passwordEndpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          old_password: currentPassword,
          new_password: newPassword,
        }),
      });

      const pwData = await pwResponse.json();
      if (!pwResponse.ok || pwData.success !== true) {
        throw new Error(pwData.message || "Password change failed");
      }

      // STEP 2: Upload Profile Picture if selected and not yet uploaded
      let finalMediaId = profileImageMediaId;
      if (profileImageFile && !finalMediaId) {
        const generatedId = createUniqueMediaId();
        const uploadRes = await uploadMedia(profileImageFile, generatedId);
        finalMediaId = uploadRes.media_id;
        setProfileImageMediaId(finalMediaId);
      }

      // STEP 3: Create Admin Profile (POST /api/profile/admin/profile)
      const profilePayload = {
        user_id: trimmedAdminId,
        full_name: trimmedFullName,
        phone_number: trimmedPhone,
        email: trimmedEmail || null,
        profile_image_media_id: finalMediaId || null,
        role: role,
        state_code: stateCode || null,
        state_name: stateName || null,
        district_code: districtCode || null,
        district_name: districtName || null,
        block_code: blockCode || null,
        block_name: blockName || null,
        pincode: pincode || null,
        street_locality: streetLocality.trim() || null,
        house_or_building_no: houseOrBuildingNo.trim() || null,
      };

      const profileResponse = await fetch(`${API_BASE_URL}/api/profile/admin/profile`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        credentials: "include",
        body: JSON.stringify(profilePayload),
      });

      const profileData = await profileResponse.json();
      if (!profileResponse.ok || profileData.success !== true) {
        throw new Error(profileData.message || "Failed to create admin profile");
      }

      setChangePasswordSuccess(true);
      playAudioFeedback("success");

      setTimeout(() => {
        onLoginSuccess({
          adminId: trimmedAdminId,
          uuid: adminUuid,
          loginTime: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
          fullName: trimmedFullName,
          phoneNumber: trimmedPhone,
          email: trimmedEmail || undefined,
          profileImageMediaId: finalMediaId || undefined,
          role: role,
          stateName: stateName || undefined,
          districtName: districtName || undefined,
          blockName: blockName || undefined,
          pincode: pincode || undefined,
        });
      }, 900);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Onboarding setup failed";
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

          <button type="button" onClick={handlePrev} className="al-nav-btn al-nav-prev">
            <ChevronLeft size={20} />
          </button>
          <button type="button" onClick={handleNext} className="al-nav-btn al-nav-next">
            <ChevronRight size={20} />
          </button>

          <div className="al-dots">
            {CAROUSEL_IMAGES.map((_, idx) => (
              <button
                key={idx}
                type="button"
                onClick={() => {
                  playAudioFeedback("click");
                  setCurrentIndex(idx);
                }}
                className={`al-dot ${idx === currentIndex ? "active" : "inactive"}`}
                title={`Slide ${idx + 1}`}
              />
            ))}
          </div>
        </div>

        {/* Right Column: Form */}
        <div
          className={`al-form-section ${currentView === "login" ? "al-center-mode" : ""}`}
        >
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
                <AlertCircle size={16} style={{ flexShrink: 0 }} />
                <span>{errorMessage}</span>
              </div>
            )}

            {changePasswordSuccess && (
              <div className="al-alert al-alert-success">
                <CheckCircle2 size={16} style={{ flexShrink: 0 }} />
                <span>Password & Admin Profile saved! Redirecting...</span>
              </div>
            )}

            {/* View 1: Standard Login */}
            {currentView === "login" && (
              <form onSubmit={handleLogin} className="al-form">
                <div className="al-field">
                  <label htmlFor="adminId" className="al-label">
                    Admin ID
                  </label>
                  <div className="al-input-group">
                    <span className="al-input-icon-left">
                      <KeyRound size={16} />
                    </span>
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
                  <label htmlFor="password" className="al-label">
                    Password
                  </label>
                  <div className="al-input-group">
                    <span className="al-input-icon-left">
                      <Lock size={16} />
                    </span>
                    <input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      disabled={isLoading}
                      placeholder="Enter Password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="al-input"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="al-input-icon-right"
                    >
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                </div>

                <button type="submit" disabled={isLoading} className="al-btn al-btn-blue">
                  {isLoading ? (
                    <>
                      <RefreshCw size={16} className="animate-spin" /> Verifying...
                    </>
                  ) : (
                    <>
                      Log In <ArrowRight size={16} />
                    </>
                  )}
                </button>
              </form>
            )}

            {/* View 2: First-Time Password Change & Profile Setup */}
            {currentView === "first_time_password" && (
              <div className="al-form">
                <div className="al-ft-header">
                  <span className="al-ft-badge">First-Time Onboarding</span>
                  <h2 className="al-ft-title">Update Password & Complete Profile</h2>
                </div>

                <form onSubmit={handlePasswordAndProfileSubmit} className="al-form">
                  {/* Fixed User ID & Role (Kept as-is) */}
                  <div className="al-grid-2">
                    <div className="al-field">
                      <label className="al-label">User ID</label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <KeyRound size={15} />
                        </span>
                        <input
                          type="text"
                          value={adminId.trim()}
                          readOnly
                          disabled
                          className="al-input"
                        />
                      </div>
                    </div>

                    <div className="al-field">
                      <label className="al-label">Role</label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <Shield size={15} />
                        </span>
                        <input
                          type="text"
                          value={role}
                          readOnly
                          disabled
                          className="al-input"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Profile Picture Upload */}
                  <div className="al-field">
                    <label className="al-label">Profile Picture</label>
                    <div className="al-avatar-upload">
                      <div className="al-avatar-preview">
                        {profilePreviewUrl ? (
                          <img src={profilePreviewUrl} alt="Profile preview" />
                        ) : (
                          <User size={26} />
                        )}
                      </div>
                      <div className="al-avatar-meta">
                        <label htmlFor="profilePicInput" className="al-file-btn">
                          <Upload size={14} />
                          <span>
                            {isUploadingMedia ? "Uploading..." : "Upload Profile Photo"}
                          </span>
                        </label>
                        <input
                          id="profilePicInput"
                          type="file"
                          accept="image/*"
                          disabled={isLoading || changePasswordSuccess || isUploadingMedia}
                          onChange={handleProfilePictureChange}
                          style={{ display: "none" }}
                        />
                        <span
                          className={`al-upload-status ${
                            profileImageMediaId ? "uploaded" : ""
                          }`}
                        >
                          {mediaUploadStatus}
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Personal Details */}
                  <div className="al-section-divider">Personal Details</div>

                  <div className="al-field">
                    <label htmlFor="fullName" className="al-label">
                      <span>
                        Full Name <span className="al-label-req">*</span>
                      </span>
                    </label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left">
                        <User size={15} />
                      </span>
                      <input
                        id="fullName"
                        type="text"
                        required
                        minLength={2}
                        maxLength={128}
                        placeholder="Enter Full Name"
                        disabled={isLoading || changePasswordSuccess}
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        className="al-input"
                      />
                    </div>
                  </div>

                  <div className="al-grid-2">
                    <div className="al-field">
                      <label htmlFor="phoneNumber" className="al-label">
                        <span>
                          Phone Number <span className="al-label-req">*</span>
                        </span>
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <Phone size={15} />
                        </span>
                        <input
                          id="phoneNumber"
                          type="tel"
                          required
                          minLength={10}
                          maxLength={20}
                          placeholder="9876543210"
                          disabled={isLoading || changePasswordSuccess}
                          value={phoneNumber}
                          onChange={(e) => setPhoneNumber(e.target.value)}
                          className="al-input"
                        />
                      </div>
                    </div>

                    <div className="al-field">
                      <label htmlFor="email" className="al-label">
                        Email Address
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <Mail size={15} />
                        </span>
                        <input
                          id="email"
                          type="email"
                          placeholder="admin@example.com"
                          disabled={isLoading || changePasswordSuccess}
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          className="al-input"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Address Details (Pincode First) */}
                  <div className="al-section-divider">Address Details</div>

                  <div className="al-grid-2">
                    <div className="al-field">
                      <label htmlFor="pincode" className="al-label">
                        Pincode
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <MapPin size={15} />
                        </span>
                        <input
                          id="pincode"
                          type="text"
                          inputMode="numeric"
                          maxLength={6}
                          placeholder="6-digit Pincode"
                          disabled={isLoading || changePasswordSuccess}
                          value={pincode}
                          onChange={handlePincodeChange}
                          className="al-input"
                        />
                      </div>
                    </div>

                    <div className="al-field">
                      <label htmlFor="blockDropdown" className="al-label">
                        Block
                      </label>
                      <div className="al-input-group">
                        <select
                          id="blockDropdown"
                          disabled={isBlockDisabled || isLoading || changePasswordSuccess}
                          value={blockName}
                          onChange={handleBlockChange}
                          className="al-select no-left-icon"
                        >
                          <option value="">{blockPlaceholder}</option>
                          {blockOptions.map((blk) => (
                            <option key={blk} value={blk}>
                              {blk}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>
                  </div>

                  <div className="al-grid-2">
                    <div className="al-field">
                      <label htmlFor="state" className="al-label">
                        State
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <Building size={15} />
                        </span>
                        <input
                          id="state"
                          type="text"
                          readOnly
                          placeholder="Auto-filled from PIN"
                          value={stateName}
                          className="al-input"
                        />
                      </div>
                    </div>

                    <div className="al-field">
                      <label htmlFor="district" className="al-label">
                        District
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <MapPin size={15} />
                        </span>
                        <input
                          id="district"
                          type="text"
                          readOnly
                          placeholder="Auto-filled from PIN"
                          value={districtName}
                          className="al-input"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="al-grid-2">
                    <div className="al-field">
                      <label htmlFor="houseOrBuildingNo" className="al-label">
                        House / Building No
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <Home size={15} />
                        </span>
                        <input
                          id="houseOrBuildingNo"
                          type="text"
                          placeholder="e.g. 12A"
                          disabled={isLoading || changePasswordSuccess}
                          value={houseOrBuildingNo}
                          onChange={(e) => setHouseOrBuildingNo(e.target.value)}
                          className="al-input"
                        />
                      </div>
                    </div>

                    <div className="al-field">
                      <label htmlFor="streetLocality" className="al-label">
                        Street / Locality
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <MapPin size={15} />
                        </span>
                        <input
                          id="streetLocality"
                          type="text"
                          placeholder="e.g. MG Road"
                          disabled={isLoading || changePasswordSuccess}
                          value={streetLocality}
                          onChange={(e) => setStreetLocality(e.target.value)}
                          className="al-input"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Security / Password Update */}
                  <div className="al-section-divider">Security Credentials</div>

                  <div className="al-field">
                    <label htmlFor="currentPassword" className="al-label">
                      <span>
                        Current Password <span className="al-label-req">*</span>
                      </span>
                    </label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left">
                        <Lock size={15} />
                      </span>
                      <input
                        id="currentPassword"
                        type={showCurrentPassword ? "text" : "password"}
                        disabled={isLoading || changePasswordSuccess}
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        className="al-input"
                      />
                      <button
                        type="button"
                        onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                        className="al-input-icon-right"
                      >
                        {showCurrentPassword ? <EyeOff size={15} /> : <Eye size={15} />}
                      </button>
                    </div>
                  </div>

                  <div className="al-grid-2">
                    <div className="al-field">
                      <label htmlFor="newPassword" className="al-label">
                        <span>
                          New Password <span className="al-label-req">*</span>
                        </span>
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <KeyRound size={15} />
                        </span>
                        <input
                          id="newPassword"
                          type={showNewPassword ? "text" : "password"}
                          placeholder="Min 8 chars"
                          disabled={isLoading || changePasswordSuccess}
                          value={newPassword}
                          onChange={(e) => setNewPassword(e.target.value)}
                          className="al-input"
                        />
                        <button
                          type="button"
                          onClick={() => setShowNewPassword(!showNewPassword)}
                          className="al-input-icon-right"
                        >
                          {showNewPassword ? <EyeOff size={15} /> : <Eye size={15} />}
                        </button>
                      </div>
                    </div>

                    <div className="al-field">
                      <label htmlFor="confirmNewPassword" className="al-label">
                        <span>
                          Confirm Password <span className="al-label-req">*</span>
                        </span>
                      </label>
                      <div className="al-input-group">
                        <span className="al-input-icon-left">
                          <KeyRound size={15} />
                        </span>
                        <input
                          id="confirmNewPassword"
                          type={showConfirmPassword ? "text" : "password"}
                          placeholder="Re-enter password"
                          disabled={isLoading || changePasswordSuccess}
                          value={confirmNewPassword}
                          onChange={(e) => setConfirmNewPassword(e.target.value)}
                          className="al-input"
                        />
                        <button
                          type="button"
                          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                          className="al-input-icon-right"
                        >
                          {showConfirmPassword ? <EyeOff size={15} /> : <Eye size={15} />}
                        </button>
                      </div>
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={isLoading || changePasswordSuccess || isUploadingMedia}
                    className="al-btn al-btn-green"
                  >
                    {isLoading ? (
                      <>
                        <RefreshCw size={16} className="animate-spin" /> Saving Profile & Password...
                      </>
                    ) : (
                      <>
                        Save Profile & Enter <ArrowRight size={16} />
                      </>
                    )}
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
