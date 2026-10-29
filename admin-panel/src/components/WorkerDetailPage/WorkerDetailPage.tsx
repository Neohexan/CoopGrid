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
} from "lucide-react";
import { INITIAL_API_WORKERS, ApiWorker } from "../WorkerVerificationDesk/WorkerVerificationDesk";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v2";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";

interface WorkerDetailPageProps {
  workerId: string;
  onBack?: () => void;
}

export const WorkerDetailPage: React.FC<WorkerDetailPageProps> = ({ workerId, onBack }) => {
  const [isMounted, setIsMounted] = useState(false);
  const [worker, setWorker] = useState<ApiWorker | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [rejectReason, setRejectReason] = useState("Incomplete verification documentation");
  const [showRejectModal, setShowRejectModal] = useState(false);

  useEffect(() => {
    setIsMounted(true);

    try {
      const rawViewed = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
      const viewedIds: string[] = rawViewed ? JSON.parse(rawViewed) : [];
      if (!viewedIds.includes(workerId)) {
        localStorage.setItem(LOCAL_STORAGE_VIEWED_KEY, JSON.stringify([...viewedIds, workerId]));
      }
    } catch {}

    try {
      const rawWorkers = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
      const list: ApiWorker[] = rawWorkers ? JSON.parse(rawWorkers) : INITIAL_API_WORKERS;
      const match = list.find((w) => w.user_id === workerId);
      if (match) {
        setWorker(match);
        return;
      }
    } catch {}

    setWorker({
      user_id: workerId,
      full_name: workerId === "worker_002" ? "Sita Devi" : "Ramesh Kumar",
      gender: workerId === "worker_002" ? "female" : "male",
      categories: workerId === "worker_002" ? ["agriculture", "handloom"] : ["plumbing", "electrical"],
      is_active: "pending",
      created_at: "2026-01-15T10:30:00Z",
      phone_number: "+91 87978 70443",
      state: "Odisha",
      district: "Rayagada",
      pincode: "765022",
      profile_picture: workerId === "worker_002" ? "/assets/image2.jpeg" : "/assets/image1.jpg",
    });
  }, [workerId]);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  const updateWorkerRecord = (updates: Partial<ApiWorker>) => {
    if (!worker) return;
    const updated = { ...worker, ...updates };
    setWorker(updated);
    try {
      const raw = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
      const list: ApiWorker[] = raw ? JSON.parse(raw) : INITIAL_API_WORKERS;
      const nextList = list.map((w) => (w.user_id === worker.user_id ? { ...w, ...updates } : w));
      localStorage.setItem(LOCAL_STORAGE_WORKERS_KEY, JSON.stringify(nextList));
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
              </div>

              {/* 4-Column Spec Grid */}
              <div className="wdp-spec-grid">
                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">Phone Number</span>
                  <div className="wdp-spec-val">
                    <Phone size={14} color="#64748b" />
                    <span>{worker.phone_number}</span>
                  </div>
                </div>

                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">District</span>
                  <div className="wdp-spec-val">
                    <MapPin size={14} color="#64748b" />
                    <span>{worker.district}</span>
                  </div>
                </div>

                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">State</span>
                  <div className="wdp-spec-val">
                    <Building size={14} color="#64748b" />
                    <span>{worker.state}</span>
                  </div>
                </div>

                <div className="wdp-spec-box">
                  <span className="wdp-spec-label">Pincode</span>
                  <div className="wdp-spec-val">
                    <span>{worker.pincode}</span>
                  </div>
                </div>
              </div>

              {/* Trade Categories */}
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

              {/* Previous Rejection Note */}
              {worker.rejection_reason && (
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
