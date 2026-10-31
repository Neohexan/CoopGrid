"use client";

import React, { useState, useEffect } from "react";
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
} from "lucide-react";
import {
  INITIAL_API_WORKERS,
  ApiWorker as BasicApiWorker,
  PRESET_REJECTION_REASONS,
  getDemoPhone,
} from "../WorkerVerificationDesk/WorkerVerificationDesk";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v3";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";
const API_BASE_URL = "https://app-api.neohexane.com";

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

// Helper: checks if a value is present (not null, undefined, empty string, or empty array)
const hasValue = (val: unknown): boolean => {
  if (val === null || val === undefined || val === "") return false;
  if (Array.isArray(val) && val.length === 0) return false;
  return true;
};

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

  // Reject Modal State (Select preset or write custom reason)
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [selectedReasonMode, setSelectedReasonMode] = useState<string>(PRESET_REJECTION_REASONS[0]);
  const [customReasonText, setCustomReasonText] = useState<string>("");

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
          let finalWorker: DetailedApiWorker = {
            ...data,
            worker_id: resolvedId,
            user_id: resolvedId,
            phone_number: data.phone_number || getDemoPhone(resolvedId),
            profile_picture: data.profile_picture || "/assets/image1.jpg",
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
          throw new Error("Worker profile not found on API");
        }
      } catch (error) {
        console.warn("API detail fetch failed, building fallback detail profile:", error);

        const baseMatch =
          INITIAL_API_WORKERS.find((w) => w.user_id === workerId) || INITIAL_API_WORKERS[0];

        const fallbackDetail: DetailedApiWorker = {
          worker_id: workerId,
          user_id: workerId,
          full_name: baseMatch ? baseMatch.full_name : "Ramesh Kumar",
          gender: baseMatch ? baseMatch.gender : "male",
          dob_millis: 631152000000,
          alt_phone_number: "9876543210",
          email: "ramesh@example.com",
          categories: baseMatch ? baseMatch.categories : ["plumbing", "electrical"],
          is_active: baseMatch ? baseMatch.is_active : "pending",
          created_at: baseMatch ? baseMatch.created_at : "2026-01-15T10:30:00Z",
          phone_number: baseMatch?.phone_number || getDemoPhone(workerId),
          profile_picture: baseMatch?.profile_picture || "/assets/image1.jpg",
          address: {
            address_type: "home",
            house_or_building_no: "12A",
            street_locality: "MG Road",
            pincode: "560001",
            state_code: "KA",
            state_name: "Karnataka",
            district_code: "BLR",
            district_name: "Bengaluru Urban",
            block_code: "BLR_N",
            block_name: "North Bengaluru",
            latitude: 12.9716,
            longitude: 77.5946,
          },
          skills: [
            {
              category_code: "PLUMBING",
              trade_code: "PIPE_FITTING",
              sub_skill: "Water pipe fitting",
              primary_category: "Plumbing",
              specific_skill: "Pipe installation",
              sub_specific_skill: "PVC pipe installation",
              experience_years: "5",
              expected_wage: "800",
              wage_type: "daily",
              work_radius_km: 25.0,
              availability_type: "full_time",
            },
          ],
          machinery_items: [
            {
              category_id: "machinery",
              sub_category_id: "tractor",
              custom_machine_name: "Mahindra Tractor",
              rate: "1500",
              rate_unit: "per_day",
              service_radius_km: 30.0,
              is_driver_and_fuel_included: true,
            },
          ],
          agri_supply_profile: {
            business_name: "Ramesh Agro Store",
            category_ids: ["seeds"],
            sub_category_ids: ["vegetable_seeds"],
            offers_delivery: true,
            offers_store_pickup: true,
            service_radius_km: 10.0,
            offers_wholesale: false,
          },
          kyc: {
            doc_type: "voter_id",
            doc_number: "[Aadhaar Redacted]",
            front_image_id: "img_front_001",
            back_image_id: null,
            verification_status: "verified",
            skill_cert_name_or_number: "CERT-123",
            skill_cert_image_id: "img_cert_001",
            machinery_rc_number: null,
            machinery_fitness_cert_number: null,
            machinery_license_photo_id: null,
            machinery_photo_id: null,
            agri_business_license_number: null,
            agri_business_name: null,
            agri_license_doc_id: null,
            agri_shop_photo_id: null,
          },
        };

        try {
          const rawWorkers = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
          if (rawWorkers) {
            const localMap: Record<string, BasicApiWorker> = JSON.parse(rawWorkers);
            if (localMap[workerId]) {
              fallbackDetail.is_active = localMap[workerId].is_active;
              fallbackDetail.rejection_reason = localMap[workerId].rejection_reason;
            }
          }
        } catch {}

        setWorker(fallbackDetail);
      } finally {
        setIsLoading(false);
      }
    };

    fetchWorkerDetails();
  }, [workerId]);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

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
    } catch {
      syncLocalWorkerStatus({
        is_active: "active",
        rejection_reason: null,
        kyc: worker.kyc ? { ...worker.kyc, verification_status: "VERIFIED" } : worker.kyc,
      });
      showToast(`✓ Worker ${worker.full_name} marked as VERIFIED!`);
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
    } catch {
      syncLocalWorkerStatus({
        is_active: "rejected",
        rejection_reason: finalReason,
        kyc: worker.kyc ? { ...worker.kyc, verification_status: "REJECTED" } : worker.kyc,
      });
      setShowRejectModal(false);
      setCustomReasonText("");
      showToast(`✕ Application for ${worker.full_name} rejected.`);
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
    { label: "Primary Phone (Demo)", value: worker.phone_number },
    { label: "Alt Phone Number", value: worker.alt_phone_number },
    { label: "Email Address", value: worker.email },
    { label: "Account Status", value: worker.is_active },
    { label: "Registered On", value: formattedCreatedDate },
  ].filter((item) => hasValue(item.value));

  // 2. Address Dynamic Columns (Only if worker.address is not null)
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

  // 3. Skills Dynamic Columns (Only columns that have at least one non-null value across skills[])
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

  // 6. KYC Details Dynamic Columns (Strictly filters out all null KYC fields)
  const kycEntries = worker.kyc
    ? [
        { label: "Document Type", value: worker.kyc.doc_type },
        { label: "Document Number", value: worker.kyc.doc_number },
        { label: "Verification Status", value: worker.kyc.verification_status },
        { label: "Front Image ID", value: worker.kyc.front_image_id },
        { label: "Back Image ID", value: worker.kyc.back_image_id },
        { label: "Skill Cert Name / No", value: worker.kyc.skill_cert_name_or_number },
        { label: "Skill Cert Image ID", value: worker.kyc.skill_cert_image_id },
        { label: "Machinery RC Number", value: worker.kyc.machinery_rc_number },
        { label: "Machinery Fitness Cert No", value: worker.kyc.machinery_fitness_cert_number },
        { label: "Machinery License Photo ID", value: worker.kyc.machinery_license_photo_id },
        { label: "Machinery Photo ID", value: worker.kyc.machinery_photo_id },
        { label: "Agri Business Name", value: worker.kyc.agri_business_name },
        { label: "Agri Business License No", value: worker.kyc.agri_business_license_number },
        { label: "Agri License Doc ID", value: worker.kyc.agri_license_doc_id },
        { label: "Agri Shop Photo ID", value: worker.kyc.agri_shop_photo_id },
      ].filter((item) => hasValue(item.value))
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
            <strong>{worker.full_name}</strong> ({displayId}). Only sections and table columns
            with available non-null data are displayed below.
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
            DYNAMIC SECTION 5: KYC DETAILS TABLE (Hidden if kyc is null)
           ==================================================================== */}
        {kycEntries.length > 0 && (
          <section className="wdp-card">
            <h2 className="wdp-section-heading">
              <FileCheck size={18} color="#059669" />
              <span>KYC & Compliance Documentation</span>
            </h2>
            <div className="wdp-dyn-table-wrap">
              <table className="wdp-dyn-table">
                <thead>
                  <tr>
                    {kycEntries.map((item) => (
                      <th key={item.label}>{item.label}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    {kycEntries.map((item) => (
                      <td key={item.label}>{String(item.value)}</td>
                    ))}
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        )}
      </main>

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
