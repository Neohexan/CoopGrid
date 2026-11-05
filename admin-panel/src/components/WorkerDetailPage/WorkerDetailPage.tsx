"use client";

import React, { useState, useEffect, useCallback } from "react";
import "./WorkerDetailPage.css";
import "../WorkerVerificationDesk/WorkerVerificationDesk.css";
import {
  Check,
  X,
  ArrowLeft,
  ShieldCheck,
  AlertCircle,
  CheckCircle2,
  XCircle,
  User,
  Tag,
  RefreshCw,
  MapPin,
  Wrench,
  Tractor,
  Truck,
  FileCheck,
  Download,
  Eye,
  Image as ImageIcon,
} from "lucide-react";
import {
  ApiWorker as BasicApiWorker,
  PRESET_REJECTION_REASONS,
  DEMO_PHOTOS,
  getDemoPhone,
} from "../WorkerVerificationDesk/WorkerVerificationDesk";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v3";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";
const API_BASE_URL = "https://admin-api.neohexane.com";

export interface DetailedApiWorker {
  worker_id: string;
  user_id?: string;
  full_name: string;
  gender: string;
  dob_millis?: number | null;
  alt_phone_number?: string | null;
  email?: string | null;
  categories: string[];
  is_active: string;
  created_at: string;
  phone_number?: string;
  profile_picture?: string;
  rejection_reason?: string | null;
  address?: {
    address_type?: string | null;
    house_or_building_no?: string | null;
    street_locality?: string | null;
    pincode?: string | null;
    state_code?: string | null;
    state_name?: string | null;
    district_code?: string | null;
    district_name?: string | null;
    block_code?: string | null;
    block_name?: string | null;
    latitude?: number | null;
    longitude?: number | null;
  } | null;
  skills?: Array<{
    category_code?: string | null;
    trade_code?: string | null;
    sub_skill?: string | null;
    primary_category?: string | null;
    specific_skill?: string | null;
    sub_specific_skill?: string | null;
    experience_years?: string | null;
    expected_wage?: string | null;
    wage_type?: string | null;
    work_radius_km?: number | null;
    availability_type?: string | null;
  }>;
  machinery_items?: Array<{
    category_id?: string | null;
    sub_category_id?: string | null;
    custom_machine_name?: string | null;
    rate?: string | null;
    rate_unit?: string | null;
    service_radius_km?: number | null;
    is_driver_and_fuel_included?: boolean | null;
  }>;
  agri_supply_profile?: {
    business_name?: string | null;
    category_ids?: string[] | null;
    sub_category_ids?: string[] | null;
    offers_delivery?: boolean | null;
    offers_store_pickup?: boolean | null;
    service_radius_km?: number | null;
    offers_wholesale?: boolean | null;
  } | null;
  kyc?: {
    doc_type?: string | null;
    doc_number?: string | null;
    front_image_id?: string | null;
    back_image_id?: string | null;
    verification_status?: string | null;
    skill_cert_name_or_number?: string | null;
    skill_cert_image_id?: string | null;
    machinery_rc_number?: string | null;
    machinery_fitness_cert_number?: string | null;
    machinery_license_photo_id?: string | null;
    machinery_photo_id?: string | null;
    agri_business_license_number?: string | null;
    agri_business_name?: string | null;
    agri_license_doc_id?: string | null;
    agri_shop_photo_id?: string | null;
  } | null;
}

const hasValue = (val: unknown): boolean => {
  if (val === null || val === undefined || val === "") return false;
  if (Array.isArray(val) && val.length === 0) return false;
  return true;
};

const getDemoPhotoById = (id: string): string => {
  let hash = 0;
  for (let i = 0; i < id.length; i++) {
    hash = (hash + id.charCodeAt(i)) % DEMO_PHOTOS.length;
  }
  return DEMO_PHOTOS[hash];
};

// ============================================================================
// MEDIA STREAM & DOWNLOAD HELPERS (/api/media/stream/{mediaId})
// ============================================================================

export function getMediaUrl(mediaId: string): string {
  return `${API_BASE_URL}/api/media/stream/${encodeURIComponent(mediaId)}`;
}

const getExtensionFromMime = (mimeType: string | null): string => {
  if (!mimeType) return "";
  const lower = mimeType.toLowerCase();
  if (lower.includes("jpeg") || lower.includes("jpg")) return ".jpg";
  if (lower.includes("png")) return ".png";
  if (lower.includes("webp")) return ".webp";
  if (lower.includes("pdf")) return ".pdf";
  return "";
};

const getMediaErrorMessage = (status: number): string => {
  if (status === 400) return "400 Bad Request: Invalid mediaId.";
  if (status === 401) return "401 Unauthorized: Session expired. Please log in again.";
  if (status === 404) return "404 Not Found: Media file is unavailable on server.";
  if (status >= 500) return "500 Server Error: Storage or database error.";
  return `Media fetch failed (HTTP ${status})`;
};

export async function loadMedia(mediaId: string): Promise<{ objectUrl: string; mimeType: string }> {
  const response = await fetch(getMediaUrl(mediaId), {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(getMediaErrorMessage(response.status));
  }

  const blob = await response.blob();
  const mimeType = response.headers.get("Content-Type") || blob.type || "image/jpeg";
  const objectUrl = URL.createObjectURL(blob);
  return { objectUrl, mimeType };
}

export async function downloadMedia(mediaId: string, fileName = "download"): Promise<void> {
  const response = await fetch(getMediaUrl(mediaId), {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(getMediaErrorMessage(response.status));
  }

  const blob = await response.blob();
  const ext = getExtensionFromMime(response.headers.get("Content-Type") || blob.type);
  const finalFileName = ext && !fileName.toLowerCase().endsWith(ext) ? `${fileName}${ext}` : fileName;

  const downloadUrl = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = downloadUrl;
  link.download = finalFileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(downloadUrl);
}

// ============================================================================
// KYC MEDIA STREAM CARD COMPONENT
// ============================================================================

interface KycMediaCardProps {
  label: string;
  mediaId: string;
  workerId: string;
  onPreview: (url: string, label: string, mediaId: string) => void;
  onToast: (msg: string) => void;
}

const KycMediaCard: React.FC<KycMediaCardProps> = ({
  label,
  mediaId,
  workerId,
  onPreview,
  onToast,
}) => {
  const [objectUrl, setObjectUrl] = useState<string | null>(null);
  const [mimeType, setMimeType] = useState<string>("image/jpeg");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isDownloading, setIsDownloading] = useState<boolean>(false);

  const fetchStream = useCallback(async () => {
    setIsLoading(true);
    setErrorMsg(null);

    try {
      const result = await loadMedia(mediaId);
      setObjectUrl((prev) => {
        if (prev) URL.revokeObjectURL(prev);
        return result.objectUrl;
      });
      setMimeType(result.mimeType);
    } catch (err: any) {
      console.error(`Error streaming media (${mediaId}):`, err);
      setErrorMsg(err?.message || "Media fetch failed");
    } finally {
      setIsLoading(false);
    }
  }, [mediaId]);

  useEffect(() => {
    let activeUrl: string | null = null;
    let isCancelled = false;

    const run = async () => {
      setIsLoading(true);
      setErrorMsg(null);
      try {
        const result = await loadMedia(mediaId);
        if (isCancelled) {
          URL.revokeObjectURL(result.objectUrl);
          return;
        }
        activeUrl = result.objectUrl;
        setObjectUrl(result.objectUrl);
        setMimeType(result.mimeType);
      } catch (err: any) {
        if (!isCancelled) {
          setErrorMsg(err?.message || "Media fetch failed");
        }
      } finally {
        if (!isCancelled) {
          setIsLoading(false);
        }
      }
    };

    run();

    return () => {
      isCancelled = true;
      if (activeUrl) {
        URL.revokeObjectURL(activeUrl);
      }
    };
  }, [mediaId]);

  const handleDownloadClick = async () => {
    setIsDownloading(true);
    try {
      const safeLabel = label.toLowerCase().replace(/[^a-z0-9]+/g, "_");
      await downloadMedia(mediaId, `${workerId}_${safeLabel}_${mediaId}`);
      onToast(`✓ Downloaded ${label}`);
    } catch (err: any) {
      onToast(`✕ ${err?.message || "Media download failed"}`);
    } finally {
      setIsDownloading(false);
    }
  };

  const isPdf = mimeType.toLowerCase().includes("pdf");

  return (
    <div className="wdp-media-card">
      <div className="wdp-media-card-header">
        <div>
          <span className="wdp-media-label">{label}</span>
          <span className="wdp-media-id" title={mediaId}>
            ID: {mediaId}
          </span>
        </div>
        <button
          type="button"
          onClick={handleDownloadClick}
          disabled={isDownloading}
          className="wdp-media-dl-btn"
          title="Download original media file"
        >
          <Download size={14} />
          <span>{isDownloading ? "..." : "Download"}</span>
        </button>
      </div>

      <div className="wdp-media-preview-box">
        {isLoading ? (
          <div className="wdp-media-state">
            <RefreshCw size={22} className="animate-spin" style={{ color: "#059669" }} />
            <span>Streaming media...</span>
          </div>
        ) : errorMsg ? (
          <div className="wdp-media-state wdp-media-error">
            <AlertCircle size={22} color="#e11d48" />
            <span>{errorMsg}</span>
            <button type="button" onClick={fetchStream} className="wdp-media-retry-btn">
              Retry Stream
            </button>
          </div>
        ) : objectUrl ? (
          isPdf ? (
            <iframe src={objectUrl} title={label} className="wdp-media-iframe" />
          ) : (
            <div
              className="wdp-media-img-wrap"
              onClick={() => onPreview(objectUrl, label, mediaId)}
            >
              <img src={objectUrl} alt={label} className="wdp-media-img" />
              <div className="wdp-media-zoom-overlay">
                <Eye size={16} />
                <span>Click to Enlarge</span>
              </div>
            </div>
          )
        ) : null}
      </div>
    </div>
  );
};

// ============================================================================
// MAIN WORKER DETAIL PAGE COMPONENT
// ============================================================================

interface ColumnDef<T> {
  key: keyof T;
  label: string;
  render?: (val: any, row: T) => React.ReactNode;
}

interface WorkerDetailPageProps {
  workerId: string;
  onBack?: () => void;
}

export const WorkerDetailPage: React.FC<WorkerDetailPageProps> = ({ workerId, onBack }) => {
  const [isMounted, setIsMounted] = useState(false);
  const [worker, setWorker] = useState<DetailedApiWorker | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Reject Modal State
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [selectedReasonMode, setSelectedReasonMode] = useState<string>(PRESET_REJECTION_REASONS[0]);
  const [customReasonText, setCustomReasonText] = useState<string>("");

  // Lightbox Modal State for streamed KYC media
  const [lightboxMedia, setLightboxMedia] = useState<{
    url: string;
    label: string;
    mediaId: string;
  } | null>(null);

  useEffect(() => {
    setIsMounted(true);

    // 1. Mark worker as viewed in localStorage so desk row unlocks
    try {
      const rawViewed = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
      const viewedIds: string[] = rawViewed ? JSON.parse(rawViewed) : [];
      if (!viewedIds.includes(workerId)) {
        localStorage.setItem(LOCAL_STORAGE_VIEWED_KEY, JSON.stringify([...viewedIds, workerId]));
      }
    } catch {}

    // 2. Fetch in-depth worker profile from GET /api/profile/admin/workers/{worker_id}
    const fetchWorkerDetails = async () => {
      setIsLoading(true);
      try {
        const response = await fetch(
          `${API_BASE_URL}/api/profile/admin/workers/${encodeURIComponent(workerId)}`,
          {
            method: "GET",
            headers: {
              Accept: "application/json",
            },
            credentials: "include",
          }
        );

        const data = await response.json();

        if (response.ok && data.success && (data.worker_id || data.user_id)) {
          const resolvedId = String(data.worker_id || data.user_id || workerId);
          const finalWorker: DetailedApiWorker = {
            ...data,
            worker_id: resolvedId,
            user_id: resolvedId,
            phone_number: data.phone_number || getDemoPhone(resolvedId),
            profile_picture: data.profile_picture || getDemoPhotoById(resolvedId),
          };

          try {
            const rawWorkers = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
            if (rawWorkers) {
              const localMap: Record<string, BasicApiWorker> = JSON.parse(rawWorkers);
              if (localMap[resolvedId]) {
                finalWorker.is_active = localMap[resolvedId].is_active;
                finalWorker.rejection_reason = localMap[resolvedId].rejection_reason;
              }
            }
          } catch {}

          setWorker(finalWorker);
        } else {
          setWorker(null);
        }
      } catch (error) {
        console.error("Failed to fetch worker profile from API:", error);
        setWorker(null);
      } finally {
        setIsLoading(false);
      }
    };

    fetchWorkerDetails();
  }, [workerId]);

  const showToast = useCallback((msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  }, []);

  const syncLocalWorkerStatus = (updates: Partial<DetailedApiWorker>) => {
    if (!worker) return;
    const targetId = worker.worker_id || worker.user_id || workerId;
    const updated = { ...worker, ...updates };
    setWorker(updated);

    try {
      const raw = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
      const storageMap: Record<string, BasicApiWorker> = raw ? JSON.parse(raw) : {};
      storageMap[targetId] = {
        user_id: targetId,
        full_name: updated.full_name,
        gender: updated.gender,
        categories: updated.categories,
        is_active: String(updates.is_active || updated.is_active),
        created_at: updated.created_at,
        phone_number: updated.phone_number,
        profile_picture: updated.profile_picture,
        rejection_reason: updates.rejection_reason ?? updated.rejection_reason ?? undefined,
      };
      localStorage.setItem(LOCAL_STORAGE_WORKERS_KEY, JSON.stringify(storageMap));
    } catch {}
  };

  // PUT /api/profile/admin/workers/{worker_id}/verify (Approve)
  const handleApprove = async () => {
    if (!worker) return;
    const targetId = worker.worker_id || worker.user_id || workerId;
    setIsSubmitting(true);

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/profile/admin/workers/${encodeURIComponent(targetId)}/verify`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            verification_status: "VERIFIED",
            rejection_reason: null,
          }),
        }
      );

      const data = await response.json().catch(() => null);
      if (!response.ok || (data && data.success === false)) {
        throw new Error(data?.message || "Verification failed");
      }

      syncLocalWorkerStatus({
        is_active: "active",
        rejection_reason: null,
        kyc: worker.kyc ? { ...worker.kyc, verification_status: "VERIFIED" } : worker.kyc,
      });
      showToast(data?.message || `✓ Worker ${worker.full_name} verified!`);
    } catch (error: any) {
      showToast(error?.message || "Failed to verify worker.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // PUT /api/profile/admin/workers/{worker_id}/verify (Reject)
  const handleReject = async () => {
    if (!worker) return;
    const finalReason =
      selectedReasonMode === "CUSTOM" ? customReasonText.trim() : selectedReasonMode.trim();

    if (!finalReason) {
      showToast("⚠️ Please select or write a rejection reason.");
      return;
    }

    const targetId = worker.worker_id || worker.user_id || workerId;
    setIsSubmitting(true);

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/profile/admin/workers/${encodeURIComponent(targetId)}/verify`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            verification_status: "REJECTED",
            rejection_reason: finalReason,
          }),
        }
      );

      const data = await response.json().catch(() => null);
      if (!response.ok || (data && data.success === false)) {
        throw new Error(data?.message || "Rejection failed");
      }

      syncLocalWorkerStatus({
        is_active: "rejected",
        rejection_reason: finalReason,
        kyc: worker.kyc ? { ...worker.kyc, verification_status: "REJECTED" } : worker.kyc,
      });
      setShowRejectModal(false);
      setCustomReasonText("");
      showToast(data?.message || `✕ Worker ${worker.full_name} rejected.`);
    } catch (error: any) {
      showToast(error?.message || "Failed to reject worker.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isMounted) return null;

  if (isLoading) {
    return (
      <div className="wdp-wrapper" style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
        <div style={{ textAlign: "center" }}>
          <RefreshCw size={32} className="animate-spin" style={{ margin: "0 auto 12px auto", color: "#059669" }} />
          <p style={{ color: "#64748b", fontSize: "14px", margin: 0 }}>Fetching full worker profile...</p>
        </div>
      </div>
    );
  }

  if (!worker) {
    return (
      <div className="wdp-wrapper" style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
        <div className="wdp-card" style={{ maxWidth: "420px", textAlign: "center" }}>
          <AlertCircle size={44} color="#f59e0b" style={{ margin: "0 auto 12px auto" }} />
          <h2 style={{ margin: "0 0 8px 0" }}>Worker Not Found</h2>
          <p style={{ margin: "0", fontSize: "13px", color: "#64748b" }}>
            No registration records found for ID: <strong>{workerId}</strong>
          </p>
        </div>
      </div>
    );
  }

  const displayId = worker.worker_id || worker.user_id || workerId;
  const formattedCreatedDate = new Date(worker.created_at).toLocaleDateString("en-IN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
  const formattedDob = hasValue(worker.dob_millis)
    ? new Date(Number(worker.dob_millis)).toLocaleDateString("en-IN", {
        year: "numeric",
        month: "long",
        day: "numeric",
      })
    : null;

  // ============================================================================
  // DYNAMIC TABLE BUILDERS (Only non-null fields get table headers & cells)
  // ============================================================================

  // 1. Personal & Contact Overview Dynamic Columns
  const personalOverviewEntries = [
    { label: "Worker ID", value: displayId },
    { label: "Full Name", value: worker.full_name },
    { label: "Gender", value: worker.gender },
    { label: "Date of Birth", value: formattedDob },
    { label: "Mobile Number", value: worker.phone_number },
    { label: "Alt Phone Number", value: worker.alt_phone_number },
    { label: "Email Address", value: worker.email },
    { label: "Account Status", value: worker.is_active },
    { label: "Registered On", value: formattedCreatedDate },
  ].filter((item) => hasValue(item.value));

  // 2. Address Dynamic Columns
  const addressEntries = worker.address
    ? [
        { label: "Address Type", value: worker.address.address_type },
        { label: "House / Building No", value: worker.address.house_or_building_no },
        { label: "Street / Locality", value: worker.address.street_locality },
        { label: "Block Name", value: worker.address.block_name },
        { label: "Block Code", value: worker.address.block_code },
        { label: "District Name", value: worker.address.district_name },
        { label: "District Code", value: worker.address.district_code },
        { label: "State Name", value: worker.address.state_name },
        { label: "State Code", value: worker.address.state_code },
        { label: "Pincode", value: worker.address.pincode },
        { label: "Latitude", value: worker.address.latitude },
        { label: "Longitude", value: worker.address.longitude },
      ].filter((item) => hasValue(item.value))
    : [];

  // 3. Skills Dynamic Columns
  const skillsList = worker.skills ?? [];
  const allSkillColumns: ColumnDef<(typeof skillsList)[number]>[] = [
    { key: "primary_category", label: "Primary Category" },
    { key: "category_code", label: "Category Code" },
    { key: "trade_code", label: "Trade Code" },
    { key: "sub_skill", label: "Sub Skill" },
    { key: "specific_skill", label: "Specific Skill" },
    { key: "sub_specific_skill", label: "Sub-Specific Skill" },
    { key: "experience_years", label: "Experience (Yrs)" },
    { key: "expected_wage", label: "Expected Wage", render: (val) => `₹${val}` },
    { key: "wage_type", label: "Wage Type" },
    { key: "work_radius_km", label: "Work Radius (km)", render: (val) => `${val} km` },
    { key: "availability_type", label: "Availability" },
  ];
  const activeSkillColumns = allSkillColumns.filter((col) =>
    skillsList.some((row) => hasValue(row[col.key]))
  );

  // 4. Machinery Items Dynamic Columns
  const machineryList = worker.machinery_items ?? [];
  const allMachineryColumns: ColumnDef<(typeof machineryList)[number]>[] = [
    { key: "custom_machine_name", label: "Machine Name" },
    { key: "category_id", label: "Category ID" },
    { key: "sub_category_id", label: "Sub-Category ID" },
    { key: "rate", label: "Rate", render: (val) => `₹${val}` },
    { key: "rate_unit", label: "Rate Unit" },
    { key: "service_radius_km", label: "Service Radius (km)", render: (val) => `${val} km` },
    {
      key: "is_driver_and_fuel_included",
      label: "Driver & Fuel Included",
      render: (val) => (val === true ? "Yes" : val === false ? "No" : ""),
    },
  ];
  const activeMachineryColumns = allMachineryColumns.filter((col) =>
    machineryList.some((row) => hasValue(row[col.key]))
  );

  // 5. Agriculture Supply Profile Dynamic Columns
  const agriEntries = worker.agri_supply_profile
    ? [
        { label: "Business Name", value: worker.agri_supply_profile.business_name },
        {
          label: "Category IDs",
          value: worker.agri_supply_profile.category_ids?.join(", "),
        },
        {
          label: "Sub-Category IDs",
          value: worker.agri_supply_profile.sub_category_ids?.join(", "),
        },
        {
          label: "Offers Delivery",
          value:
            typeof worker.agri_supply_profile.offers_delivery === "boolean"
              ? worker.agri_supply_profile.offers_delivery
                ? "Yes"
                : "No"
              : null,
        },
        {
          label: "Offers Store Pickup",
          value:
            typeof worker.agri_supply_profile.offers_store_pickup === "boolean"
              ? worker.agri_supply_profile.offers_store_pickup
                ? "Yes"
                : "No"
              : null,
        },
        {
          label: "Service Radius (km)",
          value: hasValue(worker.agri_supply_profile.service_radius_km)
            ? `${worker.agri_supply_profile.service_radius_km} km`
            : null,
        },
        {
          label: "Offers Wholesale",
          value:
            typeof worker.agri_supply_profile.offers_wholesale === "boolean"
              ? worker.agri_supply_profile.offers_wholesale
                ? "Yes"
                : "No"
              : null,
        },
      ].filter((item) => hasValue(item.value))
    : [];

  // 6. KYC Metadata Dynamic Columns (Text/Number fields)
  const kycMetadataEntries = worker.kyc
    ? [
        { label: "Document Type", value: worker.kyc.doc_type },
        { label: "Document Number", value: worker.kyc.doc_number },
        { label: "Verification Status", value: worker.kyc.verification_status },
        { label: "Skill Cert Name / No", value: worker.kyc.skill_cert_name_or_number },
        { label: "Machinery RC Number", value: worker.kyc.machinery_rc_number },
        { label: "Machinery Fitness Cert No", value: worker.kyc.machinery_fitness_cert_number },
        { label: "Agri Business Name", value: worker.kyc.agri_business_name },
        { label: "Agri Business License No", value: worker.kyc.agri_business_license_number },
      ].filter((item) => hasValue(item.value))
    : [];

  // 7. KYC Streamable Media Items (All 7 image/doc ID fields highlighted from kyc)
  const kycMediaItems = worker.kyc
    ? [
        { label: "ID Front Image", mediaId: worker.kyc.front_image_id },
        { label: "ID Back Image", mediaId: worker.kyc.back_image_id },
        { label: "Skill Certificate Image", mediaId: worker.kyc.skill_cert_image_id },
        { label: "Machinery License Photo", mediaId: worker.kyc.machinery_license_photo_id },
        { label: "Machinery Photo", mediaId: worker.kyc.machinery_photo_id },
        { label: "Agri License Document", mediaId: worker.kyc.agri_license_doc_id },
        { label: "Agri Shop Photo", mediaId: worker.kyc.agri_shop_photo_id },
      ].filter((item): item is { label: string; mediaId: string } => hasValue(item.mediaId))
    : [];

  return (
    <div className="wdp-wrapper">
      {/* Sticky Header */}
      <header className="wdp-header">
        <div className="wdp-header-inner">
          <div className="wdp-header-left">
            <button
              type="button"
              onClick={() => {
                if (onBack) {
                  onBack();
                } else if (window.opener) {
                  window.close();
                } else {
                  window.location.href = "/applications";
                }
              }}
              className="wdp-back-btn"
            >
              <ArrowLeft size={16} />
              <span>Back to Desk</span>
            </button>
            <span className="wdp-divider" />
            <span className="wdp-id-pill">{displayId}</span>
          </div>

          <span className={`wdp-status-pill ${worker.is_active.toLowerCase()}`}>
            {worker.is_active}
          </span>
        </div>
      </header>

      {/* Main Content */}
      <main className="wdp-main">
        {/* Notice Banner */}
        <div className="wdp-notice">
          <ShieldCheck size={20} style={{ flexShrink: 0, marginTop: "2px" }} />
          <div>
            <strong>Official Verification File:</strong> Inspecting in-depth profile for{" "}
            <strong>{worker.full_name}</strong> ({displayId}). Only sections, table columns, and
            uploaded media streams with available non-null data are displayed below.
          </div>
        </div>

        {/* Worker Top Card */}
        <div className="wdp-card">
          <div className="wdp-card-layout">
            {/* Square Profile Photo */}
            <div className="wdp-photo-col">
              <div className="wdp-photo-box">
                {worker.profile_picture ? (
                  <img src={worker.profile_picture} alt={worker.full_name} />
                ) : (
                  <div className="wdp-photo-fallback">
                    <User size={56} />
                    <span style={{ fontSize: "12px", fontWeight: 700, marginTop: "6px" }}>
                      {displayId}
                    </span>
                  </div>
                )}
                <span className="wdp-square-badge">SQUARE ID</span>
              </div>
              <span className="wdp-photo-id">ID: {displayId}</span>
            </div>

            {/* Primary Info & Dynamic Personal Table */}
            <div className="wdp-info-col">
              <div>
                <div className="wdp-name-row">
                  <h1 className="wdp-name">{worker.full_name}</h1>
                  <span className={`wvd-gender-badge ${worker.gender.toLowerCase()}`}>
                    {worker.gender.toLowerCase() === "female" ? "👩 Female" : "👨 Male"}
                  </span>
                </div>
              </div>

              {/* Dynamic Personal Overview Table */}
              {personalOverviewEntries.length > 0 && (
                <div className="wdp-dyn-section">
                  <h3 className="wdp-dyn-title">Personal & Contact Details</h3>
                  <div className="wdp-dyn-table-wrap">
                    <table className="wdp-dyn-table">
                      <thead>
                        <tr>
                          {personalOverviewEntries.map((col) => (
                            <th key={col.label}>{col.label}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          {personalOverviewEntries.map((col) => (
                            <td key={col.label}>{String(col.value)}</td>
                          ))}
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {/* Registered Categories (Hidden if empty) */}
              {worker.categories && worker.categories.length > 0 && (
                <div>
                  <span className="wdp-cat-section-title">Registered Trade Categories</span>
                  <div className="wdp-cat-list">
                    {worker.categories.map((cat) => (
                      <span key={cat} className="wdp-cat-badge">
                        <Tag size={13} />
                        <span>{cat}</span>
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {/* Previous Rejection Reason */}
              {hasValue(worker.rejection_reason) && (
                <div className="wdp-rejection-box">
                  <strong>Rejection Reason: </strong>
                  <span>{worker.rejection_reason}</span>
                </div>
              )}

              {/* Action Buttons Bar */}
              <div className="wdp-action-bar">
                <span style={{ fontSize: "13px", color: "#64748b" }}>
                  Current Status:{" "}
                  <strong style={{ color: "#0f172a", textTransform: "uppercase" }}>
                    {worker.is_active}
                  </strong>
                </span>
                <div className="wdp-action-buttons">
                  {worker.is_active.toLowerCase() !== "active" &&
                    worker.is_active.toLowerCase() !== "verified" && (
                      <button
                        type="button"
                        disabled={isSubmitting}
                        onClick={handleApprove}
                        className="wdp-approve-btn"
                      >
                        <Check size={16} />
                        <span>{isSubmitting ? "Updating..." : "Approve (Verify KYC)"}</span>
                      </button>
                    )}

                  {worker.is_active.toLowerCase() !== "rejected" && (
                    <button
                      type="button"
                      disabled={isSubmitting}
                      onClick={() => {
                        setSelectedReasonMode(PRESET_REJECTION_REASONS[0]);
                        setCustomReasonText("");
                        setShowRejectModal(true);
                      }}
                      className="wdp-reject-btn"
                    >
                      <X size={16} />
                      <span>Reject Application</span>
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* ====================================================================
            DYNAMIC SECTION 1: ADDRESS TABLE (Hidden if address is null)
           ==================================================================== */}
        {addressEntries.length > 0 && (
          <section className="wdp-card">
            <h2 className="wdp-section-heading">
              <MapPin size={18} color="#059669" />
              <span>Registered Address Details</span>
            </h2>
            <div className="wdp-dyn-table-wrap">
              <table className="wdp-dyn-table">
                <thead>
                  <tr>
                    {addressEntries.map((item) => (
                      <th key={item.label}>{item.label}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    {addressEntries.map((item) => (
                      <td key={item.label}>{String(item.value)}</td>
                    ))}
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        )}

        {/* ====================================================================
            DYNAMIC SECTION 2: SKILLS TABLE (Hidden if skills[] is empty)
           ==================================================================== */}
        {skillsList.length > 0 && activeSkillColumns.length > 0 && (
          <section className="wdp-card">
            <h2 className="wdp-section-heading">
              <Wrench size={18} color="#059669" />
              <span>Verified Trade Skills ({skillsList.length})</span>
            </h2>
            <div className="wdp-dyn-table-wrap">
              <table className="wdp-dyn-table">
                <thead>
                  <tr>
                    {activeSkillColumns.map((col) => (
                      <th key={String(col.key)}>{col.label}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {skillsList.map((skill, rowIdx) => (
                    <tr key={rowIdx}>
                      {activeSkillColumns.map((col) => {
                        const rawVal = skill[col.key];
                        return (
                          <td key={String(col.key)}>
                            {hasValue(rawVal)
                              ? col.render
                                ? col.render(rawVal, skill)
                                : String(rawVal)
                              : "—"}
                          </td>
                        );
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {/* ====================================================================
            DYNAMIC SECTION 3: MACHINERY TABLE (Hidden if machinery_items[] is empty)
           ==================================================================== */}
        {machineryList.length > 0 && activeMachineryColumns.length > 0 && (
          <section className="wdp-card">
            <h2 className="wdp-section-heading">
              <Tractor size={18} color="#059669" />
              <span>Registered Machinery Items ({machineryList.length})</span>
            </h2>
            <div className="wdp-dyn-table-wrap">
              <table className="wdp-dyn-table">
                <thead>
                  <tr>
                    {activeMachineryColumns.map((col) => (
                      <th key={String(col.key)}>{col.label}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {machineryList.map((machine, rowIdx) => (
                    <tr key={rowIdx}>
                      {activeMachineryColumns.map((col) => {
                        const rawVal = machine[col.key];
                        return (
                          <td key={String(col.key)}>
                            {hasValue(rawVal)
                              ? col.render
                                ? col.render(rawVal, machine)
                                : String(rawVal)
                              : "—"}
                          </td>
                        );
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {/* ====================================================================
            DYNAMIC SECTION 4: AGRI SUPPLY PROFILE TABLE (Hidden if null)
           ==================================================================== */}
        {agriEntries.length > 0 && (
          <section className="wdp-card">
            <h2 className="wdp-section-heading">
              <Truck size={18} color="#059669" />
              <span>Agriculture Supply Profile</span>
            </h2>
            <div className="wdp-dyn-table-wrap">
              <table className="wdp-dyn-table">
                <thead>
                  <tr>
                    {agriEntries.map((item) => (
                      <th key={item.label}>{item.label}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    {agriEntries.map((item) => (
                      <td key={item.label}>{String(item.value)}</td>
                    ))}
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        )}

        {/* ====================================================================
            DYNAMIC SECTION 5: KYC DETAILS & STREAMED MEDIA GALLERY
           ==================================================================== */}
        {(kycMetadataEntries.length > 0 || kycMediaItems.length > 0) && (
          <section className="wdp-card">
            <h2 className="wdp-section-heading">
              <FileCheck size={18} color="#059669" />
              <span>KYC & Compliance Documentation</span>
            </h2>

            {/* KYC Metadata Table */}
            {kycMetadataEntries.length > 0 && (
              <div className="wdp-dyn-table-wrap" style={{ marginBottom: kycMediaItems.length > 0 ? "24px" : "0" }}>
                <table className="wdp-dyn-table">
                  <thead>
                    <tr>
                      {kycMetadataEntries.map((item) => (
                        <th key={item.label}>{item.label}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      {kycMetadataEntries.map((item) => (
                        <td key={item.label}>{String(item.value)}</td>
                      ))}
                    </tr>
                  </tbody>
                </table>
              </div>
            )}

            {/* Streamed Media Gallery (/api/media/stream/{mediaId}) */}
            {kycMediaItems.length > 0 && (
              <div className="wdp-media-section">
                <h3 className="wdp-dyn-title" style={{ display: "flex", alignItems: "center", gap: "6px", marginBottom: "12px" }}>
                  <ImageIcon size={15} color="#059669" />
                  <span>Uploaded Verification Media ({kycMediaItems.length})</span>
                </h3>
                <div className="wdp-media-grid">
                  {kycMediaItems.map((item) => (
                    <KycMediaCard
                      key={`${item.label}-${item.mediaId}`}
                      label={item.label}
                      mediaId={item.mediaId}
                      workerId={displayId}
                      onPreview={(url, label, mediaId) =>
                        setLightboxMedia({ url, label, mediaId })
                      }
                      onToast={showToast}
                    />
                  ))}
                </div>
              </div>
            )}
          </section>
        )}
      </main>

      {/* Lightbox Modal for Fullscreen Media Inspection */}
      {lightboxMedia && (
        <div
          className="wvd-modal-overlay"
          onClick={() => setLightboxMedia(null)}
        >
          <div
            className="wdp-lightbox-box"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="wdp-lightbox-header">
              <div>
                <h3 className="wvd-modal-title">{lightboxMedia.label}</h3>
                <span className="wdp-media-id">Media ID: {lightboxMedia.mediaId}</span>
              </div>
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <button
                  type="button"
                  onClick={async () => {
                    try {
                      const safeLabel = lightboxMedia.label.toLowerCase().replace(/[^a-z0-9]+/g, "_");
                      await downloadMedia(
                        lightboxMedia.mediaId,
                        `${displayId}_${safeLabel}_${lightboxMedia.mediaId}`
                      );
                      showToast(`✓ Downloaded ${lightboxMedia.label}`);
                    } catch (err: any) {
                      showToast(`✕ ${err?.message || "Media download failed"}`);
                    }
                  }}
                  className="wdp-media-dl-btn"
                >
                  <Download size={14} />
                  <span>Download</span>
                </button>
                <button
                  type="button"
                  onClick={() => setLightboxMedia(null)}
                  className="wvd-btn-secondary"
                >
                  Close
                </button>
              </div>
            </div>
            <div className="wdp-lightbox-body">
              <img
                src={lightboxMedia.url}
                alt={lightboxMedia.label}
                className="wdp-lightbox-img"
              />
            </div>
          </div>
        </div>
      )}

      {/* Reject Modal (Select Preset OR Write Custom Reason) */}
      {showRejectModal && (
        <div className="wvd-modal-overlay">
          <div className="wvd-modal-box">
            <div className="wvd-modal-header">
              <XCircle size={26} color="#e11d48" />
              <div>
                <h3 className="wvd-modal-title">Reject Worker KYC</h3>
                <p style={{ margin: "0", fontSize: "12px", color: "#64748b" }}>
                  {worker.full_name} ({displayId})
                </p>
              </div>
            </div>

            <div className="wvd-reason-list">
              {PRESET_REJECTION_REASONS.map((reason) => (
                <label key={reason} className="wvd-reason-item">
                  <input
                    type="radio"
                    name="detail_rejection_reason"
                    checked={selectedReasonMode === reason}
                    onChange={() => setSelectedReasonMode(reason)}
                  />
                  <span>{reason}</span>
                </label>
              ))}

              <label className="wvd-reason-item">
                <input
                  type="radio"
                  name="detail_rejection_reason"
                  checked={selectedReasonMode === "CUSTOM"}
                  onChange={() => setSelectedReasonMode("CUSTOM")}
                />
                <span>Write a custom reason...</span>
              </label>

              {selectedReasonMode === "CUSTOM" && (
                <textarea
                  rows={3}
                  placeholder="Enter specific reason for rejecting this worker's KYC..."
                  value={customReasonText}
                  onChange={(e) => setCustomReasonText(e.target.value)}
                  className="wvd-custom-reason-input"
                  autoFocus
                />
              )}
            </div>

            <div className="wvd-modal-actions">
              <button
                type="button"
                onClick={() => setShowRejectModal(false)}
                className="wvd-btn-secondary"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={
                  isSubmitting ||
                  (selectedReasonMode === "CUSTOM" && !customReasonText.trim())
                }
                onClick={handleReject}
                className="wvd-btn-danger"
              >
                {isSubmitting ? "Submitting..." : "Confirm Rejection"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toast */}
      {toastMessage && (
        <div className="wvd-toast">
          <CheckCircle2 size={18} color="#34d399" />
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
};

export default WorkerDetailPage;
