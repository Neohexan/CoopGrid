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

// Upload Media Helper (POST /api/media/upload)
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

type OnboardingStep = "login" | "password_change" | "profile_completion";

export const AdminLogin: React.FC<AdminLoginProps> = ({ onLoginSuccess }) => {
  const [currentStep, setCurrentStep] = useState<OnboardingStep>("login");
  const [mustChangePassword, setMustChangePassword] = useState<boolean>(true);

  // Page 1: Login Form State
  const [adminId, setAdminId] = useState("");
  const [password, setPassword] = useState("");
  const [adminUuid, setAdminUuid] = useState<string | undefined>(undefined);
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  // Page 2: Dedicated Password Change Page State
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [changePasswordSuccess, setChangePasswordSuccess] = useState(false);

  // Page 3: Dedicated Profile Completion Page State (Accessible ONLY when mustChangePassword === false)
  const [role] = useState("ADMIN"); // Kept as-is per specification
  const [fullName, setFullName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [email, setEmail] = useState("");
  const [profileSaveSuccess, setProfileSaveSuccess] = useState(false);

  // Profile Image Upload State
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

  // ==========================================================================
  // STEP 1: ADMIN LOGIN HANDLER (POST /api/auth/admin/login)
  // ==========================================================================
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

      // If must_change_password is true, lock everything else and send to Password Change Page
      if (data.must_change_password === true) {
        setMustChangePassword(true);
        setAdminUuid(data.uuid);
        setCurrentPassword(password);
        setPassword("");
        setErrorMessage(null);
        setCurrentStep("password_change");
        return;
      }

      // must_change_password is false
      setMustChangePassword(false);
      setAdminUuid(data.uuid);

      // Check if Admin Profile already exists on server
      try {
        const profileCheck = await fetch(`${API_BASE_URL}/api/profile/admin/profile`, {
          method: "GET",
          headers: { Accept: "application/json" },
          credentials: "include",
        });

        if (profileCheck.status === 404) {
          // Password is changed, but profile not created yet -> send to Profile Completion Page
          setCurrentStep("profile_completion");
          return;
        }

        const profileData = await profileCheck.json().catch(() => null);
        if (profileCheck.ok && profileData && profileData.success === true) {
          playAudioFeedback("success");
          onLoginSuccess({
            adminId: profileData.user_id || trimmedAdminId,
            uuid: data.uuid,
            loginTime: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
            fullName: profileData.full_name,
            phoneNumber: profileData.phone_number,
            email: profileData.email || undefined,
            profileImageMediaId: profileData.profile_image_media_id || undefined,
            role: profileData.role || "ADMIN",
            stateName: profileData.state_name || undefined,
            districtName: profileData.district_name || undefined,
            blockName: profileData.block_name || undefined,
            pincode: profileData.pincode || undefined,
          });
          return;
        }
      } catch {
        // Proceed to dashboard if profile check is unreachable
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

  // ==========================================================================
  // STEP 2: DEDICATED PASSWORD CHANGE PAGE HANDLER
  // ==========================================================================
  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!currentPassword || !newPassword) {
      setErrorMessage("Please fill out all password fields.");
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
      const endpoint = `${API_BASE_URL}/api/auth/private/admin/change-password${
        adminUuid ? `?uuid=${encodeURIComponent(adminUuid)}` : ""
      }`;

      const response = await fetch(endpoint, {
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

      const data = await response.json();

      if (!response.ok || data.success !== true) {
        throw new Error(data.message || "Password change failed");
      }

      // Refresh auth session cookie with the new password so must_change_password === false on the backend cookie
      const trimmedAdminId = adminId.trim();
      const verifyLoginRes = await fetch(`${API_BASE_URL}/api/auth/admin/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          admin_id: trimmedAdminId,
          password: newPassword,
        }),
      });

      const verifyLoginData = await verifyLoginRes.json().catch(() => null);

      if (verifyLoginData && verifyLoginData.must_change_password === true) {
        throw new Error(
          "Password change did not clear must_change_password flag. Please try again."
        );
      }

      // Confirmed must_change_password === false!
      setMustChangePassword(false);
      setChangePasswordSuccess(true);
      playAudioFeedback("success");

      // Redirect to the separate Profile Completion Page
      setTimeout(() => {
        setChangePasswordSuccess(false);
        setErrorMessage(null);
        setCurrentStep("profile_completion");
      }, 800);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Password change failed";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  // ==========================================================================
  // STEP 3: DEDICATED PROFILE COMPLETION PAGE HANDLERS
  // (Only accessible after mustChangePassword === false)
  // ==========================================================================
  const handleProfilePictureChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (mustChangePassword) {
      setErrorMessage("Please change your password first before uploading media.");
      return;
    }

    const file = e.target.files?.[0];
    if (!file) return;

    setProfileImageFile(file);
    setProfileImageMediaId("");
    setErrorMessage(null);

    if (profilePreviewUrl) {
      URL.revokeObjectURL(profilePreviewUrl);
    }
    setProfilePreviewUrl(URL.createObjectURL(file));

    setIsUploadingMedia(true);
    setMediaUploadStatus("Uploading photo to media server...");
    try {
      const generatedId = createUniqueMediaId();
      const uploadRes = await uploadMedia(file, generatedId);
      setProfileImageMediaId(uploadRes.media_id);
      setMediaUploadStatus(`Uploaded ✓ (ID: ${uploadRes.media_id.slice(0, 8)}...)`);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Upload failed, will retry on submit";
      setMediaUploadStatus(msg);
    } finally {
      setIsUploadingMedia(false);
    }
  };

  // Dynamic Pincode Lookup (https://api.postalpincode.in/pincode/{pincode})
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

        if (
          Array.isArray(data) &&
          data[0]?.Status === "Success" &&
          Array.isArray(data[0]?.PostOffice)
        ) {
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

  const handleBlockChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selected = e.target.value;
    setBlockName(selected);
    setBlockCode(selected ? generateBlockCode(selected) : "");
  };

  const handleProfileSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (mustChangePassword) {
      setErrorMessage("Password must be updated before completing your profile.");
      setCurrentStep("password_change");
      return;
    }

    const trimmedAdminId = adminId.trim();
    const trimmedFullName = fullName.trim();
    const trimmedPhone = phoneNumber.trim();
    const trimmedEmail = email.trim();

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
      let finalMediaId = profileImageMediaId;
      if (profileImageFile && !finalMediaId) {
        const generatedId = createUniqueMediaId();
        const uploadRes = await uploadMedia(profileImageFile, generatedId);
        finalMediaId = uploadRes.media_id;
        setProfileImageMediaId(finalMediaId);
      }

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

      setProfileSaveSuccess(true);
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
      const msg = err instanceof Error ? err.message : "Failed to save admin profile";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  // ==========================================================================
  // SEPARATE FULL-PAGE RENDER: PROFILE COMPLETION PAGE
  // (Rendered ONLY when currentStep === "profile_completion" && !mustChangePassword)
  // ==========================================================================
  if (currentStep === "profile_completion" && !mustChangePassword) {
    return (
      <div className="al-profile-page">
        <div className="al-profile-card">
          {/* Top Brand & Stepper Header */}
          <div className="al-profile-topbar">
            <div className="al-brand">
              <div className="al-logo-box al-logo-sm">
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

            <div className="al-stepper">
              <span className="al-step-pill done">
                <CheckCircle2 size={14} /> 1. Password Updated
              </span>
              <span className="al-step-arrow">→</span>
              <span className="al-step-pill current">2. Complete Admin Profile</span>
            </div>
          </div>

          <div className="al-profile-header-block">
            <h2 className="al-profile-main-title">Complete Your Administrator Profile</h2>
            <p className="al-profile-subtitle">
              Your password has been verified (<code>must_change_password: false</code>). Please
              complete your profile and jurisdiction details below to enter the dashboard.
            </p>
          </div>

          {errorMessage && (
            <div className="al-alert al-alert-error">
              <AlertCircle size={16} style={{ flexShrink: 0 }} />
              <span>{errorMessage}</span>
            </div>
          )}

          {profileSaveSuccess && (
            <div className="al-alert al-alert-success">
              <CheckCircle2 size={16} style={{ flexShrink: 0 }} />
              <span>Admin profile created successfully! Entering dashboard...</span>
            </div>
          )}

          <form onSubmit={handleProfileSubmit} className="al-profile-form">
            {/* Fixed User ID & Role (Kept as-is) */}
            <div className="al-grid-2">
              <div className="al-field">
                <label className="al-label">User ID (Locked)</label>
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
                <label className="al-label">Role (Locked)</label>
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
                    <User size={28} />
                  )}
                </div>
                <div className="al-avatar-meta">
                  <label htmlFor="profilePicInput" className="al-file-btn">
                    <Upload size={14} />
                    <span>{isUploadingMedia ? "Uploading..." : "Choose Profile Picture"}</span>
                  </label>
                  <input
                    id="profilePicInput"
                    type="file"
                    accept="image/*"
                    disabled={isLoading || profileSaveSuccess || isUploadingMedia}
                    onChange={handleProfilePictureChange}
                    style={{ display: "none" }}
                  />
                  <span
                    className={`al-upload-status ${profileImageMediaId ? "uploaded" : ""}`}
                  >
                    {mediaUploadStatus}
                  </span>
                </div>
              </div>
            </div>

            {/* Personal Details */}
            <div className="al-section-divider">Personal & Contact Information</div>

            <div className="al-grid-3">
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
                    disabled={isLoading || profileSaveSuccess}
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="al-input"
                  />
                </div>
              </div>

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
                    disabled={isLoading || profileSaveSuccess}
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
                    disabled={isLoading || profileSaveSuccess}
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="al-input"
                  />
                </div>
              </div>
            </div>

            {/* Address Details (Pincode First) */}
            <div className="al-section-divider">Jurisdiction & Address Details</div>

            <div className="al-grid-3">
              <div className="al-field">
                <label htmlFor="pincode" className="al-label">
                  1. Pincode
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
                    placeholder="Enter 6-digit Pincode"
                    disabled={isLoading || profileSaveSuccess}
                    value={pincode}
                    onChange={handlePincodeChange}
                    className="al-input"
                  />
                </div>
              </div>

              <div className="al-field">
                <label htmlFor="state" className="al-label">
                  2. State (Auto-filled)
                </label>
                <div className="al-input-group">
                  <span className="al-input-icon-left">
                    <Building size={15} />
                  </span>
                  <input
                    id="state"
                    type="text"
                    readOnly
                    placeholder="Waiting for Pincode..."
                    value={stateName}
                    className="al-input"
                  />
                </div>
              </div>

              <div className="al-field">
                <label htmlFor="district" className="al-label">
                  3. District (Auto-filled)
                </label>
                <div className="al-input-group">
                  <span className="al-input-icon-left">
                    <MapPin size={15} />
                  </span>
                  <input
                    id="district"
                    type="text"
                    readOnly
                    placeholder="Waiting for Pincode..."
                    value={districtName}
                    className="al-input"
                  />
                </div>
              </div>
            </div>

            <div className="al-grid-3">
              <div className="al-field">
                <label htmlFor="blockDropdown" className="al-label">
                  4. Block
                </label>
                <div className="al-input-group">
                  <select
                    id="blockDropdown"
                    disabled={isBlockDisabled || isLoading || profileSaveSuccess}
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

              <div className="al-field">
                <label htmlFor="houseOrBuildingNo" className="al-label">
                  5. House / Building No
                </label>
                <div className="al-input-group">
                  <span className="al-input-icon-left">
                    <Home size={15} />
                  </span>
                  <input
                    id="houseOrBuildingNo"
                    type="text"
                    placeholder="e.g. 12A"
                    disabled={isLoading || profileSaveSuccess}
                    value={houseOrBuildingNo}
                    onChange={(e) => setHouseOrBuildingNo(e.target.value)}
                    className="al-input"
                  />
                </div>
              </div>

              <div className="al-field">
                <label htmlFor="streetLocality" className="al-label">
                  6. Street / Locality
                </label>
                <div className="al-input-group">
                  <span className="al-input-icon-left">
                    <MapPin size={15} />
                  </span>
                  <input
                    id="streetLocality"
                    type="text"
                    placeholder="e.g. Connaught Place"
                    disabled={isLoading || profileSaveSuccess}
                    value={streetLocality}
                    onChange={(e) => setStreetLocality(e.target.value)}
                    className="al-input"
                  />
                </div>
              </div>
            </div>

            <div className="al-profile-submit-row">
              <button
                type="submit"
                disabled={isLoading || profileSaveSuccess || isUploadingMedia}
                className="al-btn al-btn-green"
              >
                {isLoading ? (
                  <>
                    <RefreshCw size={16} className="animate-spin" /> Saving Admin Profile...
                  </>
                ) : (
                  <>
                    Complete Profile & Enter Dashboard <ArrowRight size={16} />
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    );
  }

  // ==========================================================================
  // PAGE 1 (LOGIN) & PAGE 2 (PASSWORD CHANGE) RENDER
  // ==========================================================================
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

        {/* Right Column: Login OR Password Change */}
        <div className="al-form-section al-center-mode">
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
                <span>
                  Password updated (must_change_password = false)! Redirecting to Profile
                  Completion...
                </span>
              </div>
            )}

            {/* PAGE 1: Standard Login */}
            {currentStep === "login" && (
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

            {/* PAGE 2: Dedicated Password Change Page (Active while mustChangePassword === true) */}
            {currentStep === "password_change" && (
              <div className="al-form">
                <div className="al-ft-header">
                  <span className="al-ft-badge">Mandatory Security Step</span>
                  <h2 className="al-ft-title">Change Temporary Password</h2>
                  <p className="al-ft-desc">
                    You must update your temporary password before you can access profile setup.
                  </p>
                </div>

                <form onSubmit={handlePasswordChange} className="al-form">
                  <div className="al-field">
                    <label htmlFor="currentPassword" className="al-label">
                      Current Password
                    </label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left">
                        <Lock size={16} />
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
                        {showCurrentPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <div className="al-field">
                    <label htmlFor="newPassword" className="al-label">
                      New Password
                    </label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left">
                        <KeyRound size={16} />
                      </span>
                      <input
                        id="newPassword"
                        type={showNewPassword ? "text" : "password"}
                        placeholder="Minimum 8 characters"
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
                        {showNewPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <div className="al-field">
                    <label htmlFor="confirmNewPassword" className="al-label">
                      Confirm New Password
                    </label>
                    <div className="al-input-group">
                      <span className="al-input-icon-left">
                        <KeyRound size={16} />
                      </span>
                      <input
                        id="confirmNewPassword"
                        type={showConfirmPassword ? "text" : "password"}
                        placeholder="Re-enter new password"
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
                        {showConfirmPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={isLoading || changePasswordSuccess}
                    className="al-btn al-btn-green"
                  >
                    {isLoading ? (
                      <>
                        <RefreshCw size={16} className="animate-spin" /> Updating Password...
                      </>
                    ) : (
                      <>
                        Change Password & Proceed to Profile <ArrowRight size={16} />
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
