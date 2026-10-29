"use client";

import React, { useState, useEffect } from "react";
import "./WorkerDetailPage.css";
import "../WorkerVerificationDesk/WorkerVerificationDesk.css"; 
import {
  Check,
  X,
  ArrowLeft,
  Phone,
  MapPin,
  Calendar,
  ShieldCheck,
  AlertCircle,
  CheckCircle2,
  XCircle,
  User,
  Tag,
  Building,
  RefreshCw,
  Mail,
  Truck,
  Wrench,
  Tractor
} from "lucide-react";
// Removed INITIAL_API_WORKERS import
import { ApiWorker as BasicApiWorker } from "../WorkerVerificationDesk/WorkerVerificationDesk";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v3";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";
const API_BASE_URL = "https://app-api.neohexane.com";

// Expanded Interface matching the detailed API response
export interface DetailedApiWorker extends BasicApiWorker {
  dob_millis?: number | null;
  alt_phone_number?: string | null;
  email?: string | null;
  address?: {
    address_type: string;
    house_or_building_no?: string | null;
    street_locality: string;
    pincode: string;
    state_code: string;
    state_name: string;
    district_code: string;
    district_name: string;
    block_code: string;
    block_name: string;
    latitude?: number | null;
    longitude?: number | null;
  } | null;
  skills?: Array<{
    category_code?: string | null;
    trade_code?: string | null;
    sub_skill: string;
    primary_category: string;
    specific_skill: string;
    sub_specific_skill: string;
    experience_years: string;
    expected_wage: string;
    wage_type: string;
    work_radius_km: number;
    availability_type: string;
  }>;
  machinery_items?: Array<{
    category_id: string;
    sub_category_id: string;
    custom_machine_name: string;
    rate: string;
    rate_unit: string;
    service_radius_km: number;
    is_driver_and_fuel_included: boolean;
  }>;
  agri_supply_profile?: {
    business_name: string;
    category_ids: string[];
    sub_category_ids: string[];
    offers_delivery: boolean;
    offers_store_pickup: boolean;
    service_radius_km: number;
    offers_wholesale: boolean;
  } | null;
  kyc?: {
    doc_type: string;
    doc_number: string;
    verification_status: string;
    [key: string]: any;
  } | null;
}

interface WorkerDetailPageProps {
  workerId: string;
  onBack?: () => void;
}

export const WorkerDetailPage: React.FC<WorkerDetailPageProps> = ({ workerId, onBack }) => {
  const [isMounted, setIsMounted] = useState(false);
  const [worker, setWorker] = useState<DetailedApiWorker | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [rejectReason, setRejectReason] = useState("Incomplete verification documentation");
  const [showRejectModal, setShowRejectModal] = useState(false);

  useEffect(() => {
    setIsMounted(true);

    // 1. Mark worker as viewed in localStorage
    try {
      const rawViewed = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
      const viewedIds: string[] = rawViewed ? JSON.parse(rawViewed) : [];
      if (!viewedIds.includes(workerId)) {
        localStorage.setItem(LOCAL_STORAGE_VIEWED_KEY, JSON.stringify([...viewedIds, workerId]));
      }
    } catch {}

    // 2. Fetch detailed profile from API
    const fetchWorkerDetails = async () => {
      setIsLoading(true);
      try {
        const response = await fetch(`${API_BASE_URL}/api/profile/admin/workers/${workerId}`, {
          method: "GET",
          headers: {
            "Accept": "application/json"
          },
          credentials: "include"
        });

        const data = await response.json();

        if (data.success && data.worker_id) {
          // Check local storage to see if we've already locally approved/rejected them
          let finalWorker = data;
          try {
            const rawWorkers = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
            if (rawWorkers) {
              const localMap: Record<string, BasicApiWorker> = JSON.parse(rawWorkers);
              if (localMap[workerId]) {
                finalWorker = { ...data, is_active: localMap[workerId].is_active, rejection_reason: localMap[workerId].rejection_reason };
              }
            }
          } catch {}

          // Inject demo phone if API returned null for primary 
          if (!finalWorker.phone_number) {
            finalWorker.phone_number = `+91 9${Math.floor(Math.random() * 900000000) + 100000000}`;
          }

          setWorker(finalWorker as DetailedApiWorker);
        } else {
          setWorker(null); // Not found
        }
      } catch (error) {
        console.error("Failed to fetch worker details:", error);
        
        // Fallback to local storage summary if API completely fails (Fallback is now an empty array)
        try {
          const rawWorkers = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
          const list: BasicApiWorker[] = rawWorkers ? Object.values(JSON.parse(rawWorkers)) : [];
          const match = list.find((w) => w.user_id === workerId);
          if (match) {
            setWorker(match as DetailedApiWorker);
          } else {
            setWorker(null);
          }
        } catch {
          setWorker(null);
        }
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

  const updateWorkerRecord = (updates: Partial<DetailedApiWorker>) => {
    if (!worker) return;
    const updated = { ...worker, ...updates };
    setWorker(updated as DetailedApiWorker);
    
    // Sync the status change back to the main verification desk's localStorage
    try {
      const raw = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
      const storageMap: Record<string, BasicApiWorker> = raw ? JSON.parse(raw) : {};
      
      // If it exists in map, update it. If not, create a basic summary entry.
      if (storageMap[worker.user_id]) {
        storageMap[worker.user_id] = { ...storageMap[worker.user_id], ...updates };
      } else {
        storageMap[worker.user_id] = {
          user_id: worker.user_id,
          full_name: worker.full_name,
          gender: worker.gender,
          categories: worker.categories,
          is_active: updates.is_active || worker.is_active,
          created_at: worker.created_at,
          phone_number: worker.phone_number || "",
          state: worker.address?.state_name || "",
          district: worker.address?.district_name || "",
          pincode: worker.address?.pincode || "",
        };
      }
      localStorage.setItem(LOCAL_STORAGE_WORKERS_KEY, JSON.stringify(storageMap));
    } catch {}
  };

  const handleApprove = () => {
    updateWorkerRecord({ is_active: "active" });
    showToast(`✓ Worker ${worker?.full_name} (${worker?.user_id}) approved!`);
  };

  const handleReject = () => {
    updateWorkerRecord({ is_active: "rejected", rejection_reason: rejectReason });
    setShowRejectModal(false);
    showToast(`✕ Application for ${worker?.full_name} rejected.`);
  };

  const handleSuspend = () => {
    updateWorkerRecord({ is_active: "suspended" });
    showToast(`⊘ Worker ${worker?.full_name} suspended.`);
  };

  if (!isMounted) return null;

  if (isLoading) {
    return (
      <div className="wdp-wrapper" style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
        <div style={{ textAlign: "center" }}>
          <RefreshCw size={32} className="animate-spin text-emerald-600 mx-auto mb-4" />
          <p className="text-slate-500 font-medium text-sm">Fetching secure profile...</p>
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

  const formattedDate = new Date(worker.created_at).toLocaleDateString("en-IN", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

  const addressText = worker.address 
    ? `${worker.address.house_or_building_no ? worker.address.house_or_building_no + ", " : ""}${worker.address.street_locality}`
    : "Address unavailable";

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
            <span className="wdp-id-pill">{worker.user_id}</span>
          </div>

          <span className={`wdp-status-pill ${worker.is_active}`}>
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
            <strong>Official Verification File:</strong> Inspecting details for{" "}
            <strong>{worker.full_name}</strong>. Review all trade categories, geographic
            coverage, and contact details below before issuing an approval or rejection clearance.
          </div>
        </div>

        {/* Worker Profile Card */}
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
                      {worker.user_id}
                    </span>
                  </div>
                )}
                <span className="wdp-square-badge">SQUARE ID</span>
              </div>
              <span className="wdp-photo-id">ID: {worker.user_id}</span>
            </div>

            {/* Information Details */}
            <div className="wdp-info-col">
              <div>
                <div className="wdp-name-row">
                  <h1 className="wdp-name">{worker.full_name}</h1>
                  <span className={`wvd-gender-badge ${worker.gender}`}>
                    {worker.gender === "female" ? "👩 Female" : "👨 Male"}
                  </span>
                </div>
                <p className="wdp-reg-time">
                  <Calendar size={14} />
                  <span>Registered on {formattedDate}</span>
                </p>
                {worker.email && (
                  <p className="wdp-reg-time mt-1">
                    <Mail size={14} />
                    <span>{worker.email}</span>
                  </p>
                )}
              </div>

              {/* 4-Column Spec Grid */}
              <div className="wdp-spec-grid">
                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">Phone Number</span>
                  <div className="wdp-spec-val">
                    <Phone size={14} color="#64748b" />
                    <span>{worker.phone_number}</span>
                  </div>
                  {worker.alt_phone_number && (
                    <div style={{ fontSize: '11px', color: '#64748b', marginTop: '4px' }}>
                      Alt: {worker.alt_phone_number}
                    </div>
                  )}
                </div>

                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">District</span>
                  <div className="wdp-spec-val">
                    <MapPin size={14} color="#64748b" />
                    <span style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {worker.address?.district_name || worker.district || "N/A"}
                    </span>
                  </div>
                  <div style={{ fontSize: '11px', color: '#64748b', marginTop: '4px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {addressText}
                  </div>
                </div>

                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">State</span>
                  <div className="wdp-spec-val">
                    <Building size={14} color="#64748b" />
                    <span>{worker.address?.state_name || worker.state || "N/A"}</span>
                  </div>
                </div>

                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">Pincode</span>
                  <div className="wdp-spec-val">
                    <span>{worker.address?.pincode || worker.pincode || "N/A"}</span>
                  </div>
                </div>
              </div>

              {/* Trade Categories Summary */}
              <div>
                <span className="wdp-cat-section-title">Registered Trade Categories</span>
                <div className="wdp-cat-list">
                  {worker.categories.map((cat) => (
                    <span key={cat} className="wdp-cat-badge">
                      <Tag size={13} />
                      <span>{cat}</span>
                    </span>
                  ))}
                  {worker.categories.length === 0 && <span className="text-xs text-slate-500">No categories listed</span>}
                </div>
              </div>

              {/* Advanced Sections: Skills, Machinery, Agri */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t border-slate-100">
                
                {/* Specific Skills */}
                {worker.skills && worker.skills.length > 0 && (
                  <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
                     <span className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5 mb-3">
                        <Wrench size={14} /> Trade Skills Details
                     </span>
                     <div className="space-y-3">
                       {worker.skills.map((skill, idx) => (
                         <div key={idx} className="text-sm bg-white p-2.5 rounded-lg border border-slate-100">
                            <div className="font-semibold text-slate-800">{skill.sub_skill}</div>
                            <div className="text-xs text-slate-500 mt-1">Exp: {skill.experience_years} yrs • Radius: {skill.work_radius_km}km</div>
                            <div className="text-xs font-mono text-emerald-700 font-semibold mt-1">₹{skill.expected_wage} / {skill.wage_type}</div>
                         </div>
                       ))}
                     </div>
                  </div>
                )}

                {/* Machinery */}
                {worker.machinery_items && worker.machinery_items.length > 0 && (
                  <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
                     <span className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5 mb-3">
                        <Tractor size={14} /> Registered Machinery
                     </span>
                     <div className="space-y-3">
                       {worker.machinery_items.map((machine, idx) => (
                         <div key={idx} className="text-sm bg-white p-2.5 rounded-lg border border-slate-100">
                            <div className="font-semibold text-slate-800">{machine.custom_machine_name}</div>
                            <div className="text-xs text-slate-500 mt-1">Service Radius: {machine.service_radius_km}km</div>
                            <div className="text-xs font-mono text-emerald-700 font-semibold mt-1">
                              ₹{machine.rate} / {machine.rate_unit} {machine.is_driver_and_fuel_included && '(Incl. fuel)'}
                            </div>
                         </div>
                       ))}
                     </div>
                  </div>
                )}

                {/* Agri Profile */}
                {worker.agri_supply_profile && (
                  <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 md:col-span-2">
                     <span className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center gap-1.5 mb-3">
                        <Truck size={14} /> Agri-Supply Profile
                     </span>
                     <div className="text-sm bg-white p-3 rounded-lg border border-slate-100 flex flex-wrap gap-x-8 gap-y-2">
                        <div>
                          <span className="text-xs text-slate-500 block">Business Name</span>
                          <span className="font-semibold text-slate-800">{worker.agri_supply_profile.business_name}</span>
                        </div>
                        <div>
                          <span className="text-xs text-slate-500 block">Delivery Available</span>
                          <span className="font-semibold text-slate-800">{worker.agri_supply_profile.offers_delivery ? 'Yes' : 'No'} ({worker.agri_supply_profile.service_radius_km}km)</span>
                        </div>
                        <div>
                          <span className="text-xs text-slate-500 block">Wholesale</span>
                          <span className="font-semibold text-slate-800">{worker.agri_supply_profile.offers_wholesale ? 'Yes' : 'No'}</span>
                        </div>
                     </div>
                  </div>
                )}
              </div>

              {/* Previous Rejection Note */}
              {worker.rejection_reason && worker.is_active === 'rejected' && (
                <div className="wdp-rejection-box">
                  <strong>Previous Rejection Reason: </strong>
                  <span>{worker.rejection_reason}</span>
                </div>
              )}

              {/* Action Buttons */}
              <div className="wdp-action-bar">
                <span style={{ fontSize: "13px", color: "#64748b" }}>
                  Status: <strong style={{ color: "#0f172a", textTransform: "uppercase" }}>{worker.is_active}</strong>
                </span>
                <div className="wdp-action-buttons">
                  {worker.is_active !== "active" && (
                    <button type="button" onClick={handleApprove} className="wdp-approve-btn">
                      <Check size={16} />
                      <span>Approve Worker</span>
                    </button>
                  )}
                  {worker.is_active !== "rejected" && (
                    <button
                      type="button"
                      onClick={() => setShowRejectModal(true)}
                      className="wdp-reject-btn"
                    >
                      <X size={16} />
                      <span>Reject Application</span>
                    </button>
                  )}
                  {worker.is_active === "active" && (
                    <button type="button" onClick={handleSuspend} className="wdp-suspend-btn">
                      Suspend
                    </button>
                  )}
                </div>
              </div>
            </div>

          </div>
        </div>
      </main>

      {/* Reject Modal */}
      {showRejectModal && (
        <div className="wvd-modal-overlay">
          <div className="wvd-modal-box">
            <div className="wvd-modal-header">
              <XCircle size={26} color="#e11d48" />
              <div>
                <h3 className="wvd-modal-title">Reject Registration</h3>
                <p style={{ margin: "0", fontSize: "12px", color: "#64748b" }}>
                  {worker.full_name} ({worker.user_id})
                </p>
              </div>
            </div>
            <div className="wvd-reason-list">
              {[
                "Incomplete verification documentation",
                "Invalid trade skill certificate",
                "Contact unverified",
                "Address mismatch",
              ].map((reason) => (
                <label key={reason} className="wvd-reason-item">
                  <input
                    type="radio"
                    name="reason"
                    checked={rejectReason === reason}
                    onChange={() => setRejectReason(reason)}
                  />
                  <span>{reason}</span>
                </label>
              ))}
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
                onClick={handleReject}
                className="wvd-btn-danger"
              >
                Confirm Rejection
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
