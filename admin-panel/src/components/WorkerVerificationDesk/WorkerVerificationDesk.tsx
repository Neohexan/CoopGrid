"use client";

import React, { useState, useMemo, useEffect } from "react";
import "./WorkerVerificationDesk.css";
import {
  Check,
  X,
  AlertTriangle,
  Search,
  CheckCircle2,
  XCircle,
  Phone,
  MapPin,
  Eye,
  Info,
  ExternalLink,
  HelpCircle,
  RefreshCw,
} from "lucide-react";

export interface ApiWorker {
  user_id: string;
  full_name: string;
  gender: "male" | "female";
  categories: string[];
  is_active: "active" | "inactive" | "pending" | "rejected" | "suspended";
  created_at: string;
  // Filled locally with demo data since backend doesn't provide them yet
  phone_number?: string;
  state?: string;
  pincode?: string;
  district?: string;
  profile_picture?: string;
  rejection_reason?: string;
}

type FilterTab = "ALL" | "MALE" | "FEMALE";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v3";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";
const WORKERS_PER_PAGE = 10;
const API_BASE_URL = "https://app-api.neohexane.com";

export const WorkerVerificationDesk: React.FC = () => {
  const [isMounted, setIsMounted] = useState(false);
  
  // Data State
  const [workers, setWorkers] = useState<ApiWorker[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  
  // Interaction State
  const [viewedWorkerIds, setViewedWorkerIds] = useState<string[]>([]);
  const [activeFilter, setActiveFilter] = useState<FilterTab>("ALL");
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [showActionInfoModal, setShowActionInfoModal] = useState(false);
  const [rejectModalWorker, setRejectModalWorker] = useState<ApiWorker | null>(null);
  const [rejectionReasonText, setRejectionReasonText] = useState("Incomplete verification documentation");
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Initialize and attach cross-tab listener for viewed workers
  useEffect(() => {
    setIsMounted(true);
    try {
      const storedViewed = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
      if (storedViewed) setViewedWorkerIds(JSON.parse(storedViewed));
    } catch {}

    const handleStorageSync = () => {
      try {
        const sv = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
        if (sv) setViewedWorkerIds(JSON.parse(sv));
      } catch {}
    };

    window.addEventListener("storage", handleStorageSync);
    return () => window.removeEventListener("storage", handleStorageSync);
  }, []);

  // Fetch Workers from API when pagination changes
  useEffect(() => {
    const fetchWorkers = async () => {
      setIsLoading(true);
      const skip = (currentPage - 1) * WORKERS_PER_PAGE;
      const limit = WORKERS_PER_PAGE;

      try {
        const response = await fetch(`${API_BASE_URL}/api/profile/admin/summary/workers?skip=${skip}&limit=${limit}`, {
          method: "GET",
          headers: {
            "Accept": "application/json",
          },
          credentials: "include", // Required if your backend uses cookie auth
        });

        const data = await response.json();

        if (data.success && data.workers) {
          // Merge API data with Demo Data for missing fields
          const enrichedWorkers: ApiWorker[] = data.workers.map((apiWorker: any, idx: number) => {
            return {
              ...apiWorker,
              // Injecting Demo Data
              phone_number: `+91 9${Math.floor(Math.random() * 900000000) + 100000000}`,
              district: ["Rayagada", "Ranchi", "Seraikela", "Cuttack"][idx % 4],
              state: ["Odisha", "Jharkhand"][idx % 2],
              pincode: `76${Math.floor(Math.random() * 9000) + 1000}`,
              profile_picture: idx % 2 === 0 ? "/assets/image1.jpg" : undefined,
            };
          });

          // Check if we have local overrides (approvals/rejections) stored and apply them
          const localStored = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
          if (localStored) {
            const localMap: Record<string, ApiWorker> = JSON.parse(localStored);
            const finalWorkers = enrichedWorkers.map(w => localMap[w.user_id] ? { ...w, ...localMap[w.user_id] } : w);
            setWorkers(finalWorkers);
          } else {
            setWorkers(enrichedWorkers);
          }
          
          setTotalCount(data.total_count || 0);
        }
      } catch (error) {
        console.error("Failed to fetch workers:", error);
        showToast("Error connecting to server. Displaying empty state.");
      } finally {
        setIsLoading(false);
      }
    };

    if (isMounted) {
      fetchWorkers();
    }
  }, [currentPage, isMounted]);

  // Persist Action State (Approve/Reject) locally so it survives pagination/refreshes
  const persistWorkerStateLocally = (updatedWorker: ApiWorker) => {
    try {
      const currentStored = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
      const storageMap: Record<string, ApiWorker> = currentStored ? JSON.parse(currentStored) : {};
      storageMap[updatedWorker.user_id] = updatedWorker;
      localStorage.setItem(LOCAL_STORAGE_WORKERS_KEY, JSON.stringify(storageMap));
    } catch {}
  };

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  // Filter computations (applied on the currently fetched page data)
  const pendingCount = useMemo(() => workers.filter((w) => w.is_active === "pending").length, [workers]);
  const femaleCount = useMemo(() => workers.filter((w) => w.gender === "female").length, [workers]);
  const maleCount = useMemo(() => workers.filter((w) => w.gender === "male").length, [workers]);

  const filteredWorkers = useMemo(() => {
    return workers.filter((worker) => {
      if (activeFilter === "FEMALE" && worker.gender !== "female") return false;
      if (activeFilter === "MALE" && worker.gender !== "male") return false;

      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase().trim();
        const matchName = worker.full_name.toLowerCase().includes(q);
        const matchId = worker.user_id.toLowerCase().includes(q);
        const matchCategories = worker.categories.some((c) => c.toLowerCase().includes(q));
        if (!matchName && !matchId && !matchCategories) {
          return false;
        }
      }
      return true;
    });
  }, [workers, activeFilter, searchQuery]);


  const totalPages = Math.max(1, Math.ceil(totalCount / WORKERS_PER_PAGE));

  // Action Handlers
  const handleViewWorker = (worker: ApiWorker) => {
    if (!viewedWorkerIds.includes(worker.user_id)) {
      const updatedViewed = [...viewedWorkerIds, worker.user_id];
      setViewedWorkerIds(updatedViewed);
      try {
        localStorage.setItem(LOCAL_STORAGE_VIEWED_KEY, JSON.stringify(updatedViewed));
      } catch {}
    }
    window.open(`/workers/${worker.user_id}`, "_blank");
    showToast(`Opened ${worker.user_id} in new tab. Actions unlocked!`);
  };

  const handleApprove = (userId: string) => {
    const updatedWorkers = workers.map((w) => {
      if (w.user_id === userId) {
        const updated = { ...w, is_active: "active" as const };
        persistWorkerStateLocally(updated);
        return updated;
      }
      return w;
    });
    setWorkers(updatedWorkers);
    showToast(`✓ Worker ${userId} approved and activated.`);
  };

  const handleConfirmReject = () => {
    if (!rejectModalWorker) return;
    const targetId = rejectModalWorker.user_id;
    
    const updatedWorkers = workers.map((w) => {
      if (w.user_id === targetId) {
        const updated = { ...w, is_active: "rejected" as const, rejection_reason: rejectionReasonText };
        persistWorkerStateLocally(updated);
        return updated;
      }
      return w;
    });
    
    setWorkers(updatedWorkers);
    showToast(`✕ Application for ${rejectModalWorker.full_name} rejected.`);
    setRejectModalWorker(null);
  };

  if (!isMounted) return null;

  return (
    <div className="wvd-wrapper">
      <div className="wvd-container">
        
        {/* Header */}
        <div className="wvd-header">
          <h1 className="wvd-title">Worker Verification & Safety Desk</h1>
          <p className="wvd-subtitle">
            Verify credentials, inspect profile details, and approve cooperative membership.
          </p>
        </div>

        {/* Filter Pills */}
        <div className="wvd-filters">
          <button
            type="button"
            onClick={() => setActiveFilter("ALL")}
            className={`wvd-filter-btn ${activeFilter === "ALL" ? "active" : ""}`}
          >
            <span>All Loaded</span>
            <span className="wvd-filter-badge">{workers.length}</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveFilter("MALE")}
            className={`wvd-filter-btn ${activeFilter === "MALE" ? "active" : ""}`}
          >
            <span>👨‍🌾 Male</span>
            <span className="wvd-filter-badge">{maleCount}</span>
          </button>

          <button
            type="button"
            onClick={() => setActiveFilter("FEMALE")}
            className={`wvd-filter-btn ${activeFilter === "FEMALE" ? "active" : ""}`}
          >
            <span>👩‍🌾 Female</span>
            <span className="wvd-filter-badge">{femaleCount}</span>
          </button>
        </div>

        {/* Amber Alert Banner */}
        {pendingCount > 0 && (
          <div className="wvd-alert-banner">
            <AlertTriangle size={20} className="wvd-alert-icon" />
            <div>
              <h3 className="wvd-alert-title">
                {pendingCount} Worker Registration(s) on this page Awaiting Approval
              </h3>
              <p className="wvd-alert-desc">
                Inspect their profile details via the eye icon to review and approve.
              </p>
            </div>
          </div>
        )}

        {/* Search & Showing Count */}
        <div className="wvd-toolbar">
          <div className="wvd-search-wrap">
            <Search size={16} className="wvd-search-icon" />
            <input
              type="text"
              placeholder="Search by Full Name, User ID, or Category..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="wvd-search-input"
            />
          </div>
          <span className="wvd-count-text">
            Showing Page <strong>{currentPage}</strong> of <strong>{totalPages}</strong> (Total <strong>{totalCount}</strong> registered)
          </span>
        </div>

        {/* Table Card */}
        <div className="wvd-table-card">
          <div className="wvd-table-scroll">
            <table className="wvd-table">
              <thead>
                <tr>
                  <th>Worker ID & Photo</th>
                  <th>Full Name</th>
                  <th>Gender</th>
                  <th>Phone Number</th>
                  <th>District, State & PIN</th>
                  <th>Categories</th>
                  <th>Status</th>
                  <th className="wvd-th-center">
                    <div className="wvd-th-actions">
                      <span>Actions</span>
                      <button
                        type="button"
                        onClick={() => setShowActionInfoModal((prev) => !prev)}
                        className="wvd-info-btn"
                        title="Click to view Action verification policy"
                      >
                        <Info size={15} />
                      </button>
                    </div>
                  </th>
                </tr>
              </thead>

              <tbody>
                {isLoading ? (
                  <tr>
                    <td colSpan={8} style={{ textAlign: 'center', padding: '40px' }}>
                       <RefreshCw size={24} className="animate-spin text-emerald-600 mx-auto mb-2" />
                       <p className="text-slate-500 font-medium text-sm">Fetching workforce data...</p>
                    </td>
                  </tr>
                ) : filteredWorkers.length === 0 ? (
                  <tr>
                    <td colSpan={8} style={{ textAlign: 'center', padding: '40px', color: '#64748b' }}>
                       No workers found for this page or filter criteria.
                    </td>
                  </tr>
                ) : (
                  filteredWorkers.map((worker) => {
                    const isViewed = viewedWorkerIds.includes(worker.user_id);
                    const formattedDate = new Date(worker.created_at).toLocaleDateString("en-IN", {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    });

                    return (
                      <tr key={worker.user_id}>
                        <td>
                          <div className="wvd-worker-id-cell">
                            <div className="wvd-avatar">
                              {worker.profile_picture ? (
                                <img src={worker.profile_picture} alt={worker.full_name} />
                              ) : (
                                <div className="wvd-avatar-fallback">
                                  {worker.user_id.slice(-3)}
                                </div>
                              )}
                            </div>
                            <div className="wvd-id-meta">
                              <span className="wvd-id-code">{worker.user_id}</span>
                              <span className="wvd-reg-date">Reg: {formattedDate}</span>
                            </div>
                          </div>
                        </td>

                        <td>
                          <button
                            type="button"
                            onClick={() => handleViewWorker(worker)}
                            className="wvd-name-btn"
                          >
                            <span>{worker.full_name}</span>
                            <ExternalLink size={13} />
                          </button>
                        </td>

                        <td>
                          <span className={`wvd-gender-badge ${worker.gender}`}>
                            {worker.gender === "female" ? "👩 Female" : "👨 Male"}
                          </span>
                        </td>

                        <td>
                          <div className="wvd-phone">
                            <Phone size={13} />
                            <span>{worker.phone_number}</span>
                          </div>
                        </td>

                        <td>
                          <div className="wvd-location">
                            <span className="wvd-loc-main">
                              <MapPin size={13} />
                              <span>
                                {worker.district}, {worker.state}
                              </span>
                            </span>
                            <span className="wvd-loc-pin">PIN: {worker.pincode}</span>
                          </div>
                        </td>

                        <td>
                          <div className="wvd-categories">
                            {worker.categories.map((cat) => (
                              <span key={cat} className="wvd-cat-chip">
                                {cat}
                              </span>
                            ))}
                          </div>
                        </td>

                        <td>
                          <span className={`wvd-status-badge ${worker.is_active}`}>
                            {worker.is_active}
                          </span>
                        </td>

                        <td>
                          <div className="wvd-actions-cell">
                            <button
                              type="button"
                              onClick={() => handleViewWorker(worker)}
                              className={`wvd-view-btn ${isViewed ? "viewed" : "unviewed"}`}
                            >
                              <Eye size={14} />
                              <span>{isViewed ? "Re-inspect" : "View"}</span>
                            </button>

                            {isViewed ? (
                              <>
                                {worker.is_active !== "active" && (
                                  <button
                                    type="button"
                                    onClick={() => handleApprove(worker.user_id)}
                                    className="wvd-approve-btn"
                                  >
                                    <Check size={14} />
                                    <span>Approve</span>
                                  </button>
                                )}

                                {worker.is_active !== "rejected" && (
                                  <button
                                    type="button"
                                    onClick={() => setRejectModalWorker(worker)}
                                    className="wvd-reject-btn"
                                  >
                                    <X size={14} />
                                    <span>Reject</span>
                                  </button>
                                )}
                              </>
                            ) : (
                              <span className="wvd-unlock-hint">(View to unlock)</span>
                            )}
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination Footer */}
          {totalPages > 1 && (
            <div className="wvd-pagination">
              <span className="wvd-page-info">
                Page <strong>{currentPage}</strong> of <strong>{totalPages}</strong>
              </span>
              <div className="wvd-page-controls">
                <button
                  type="button"
                  disabled={currentPage === 1 || isLoading}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  className="wvd-page-btn"
                >
                  Previous
                </button>
                {Array.from({ length: totalPages }, (_, idx) => idx + 1).map((pageNum) => (
                  <button
                    key={pageNum}
                    type="button"
                    disabled={isLoading}
                    onClick={() => setCurrentPage(pageNum)}
                    className={`wvd-page-btn ${currentPage === pageNum ? "active" : ""}`}
                  >
                    {pageNum}
                  </button>
                ))}
                <button
                  type="button"
                  disabled={currentPage === totalPages || isLoading}
                  onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                  className="wvd-page-btn"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </div>

      </div>

      {/* Info Modal */}
      {showActionInfoModal && (
        <div className="wvd-modal-overlay">
          <div className="wvd-modal-box">
            <div className="wvd-modal-header">
              <HelpCircle size={24} color="#d97706" />
              <h3 className="wvd-modal-title">Action Verification Protocol</h3>
            </div>
            <div className="wvd-modal-quote">
              &quot;Verify the details before to either reject or approve&quot;
            </div>
            <p className="wvd-modal-text">
              Administrators must inspect the worker&apos;s full profile at <code>/workers/&#123;worker-id&#125;</code> in a new tab using the View icon before the Approve and Reject options unlock.
            </p>
            <div className="wvd-modal-actions">
              <button
                type="button"
                onClick={() => setShowActionInfoModal(false)}
                className="wvd-btn-primary"
              >
                Understood
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reject Modal */}
      {rejectModalWorker && (
        <div className="wvd-modal-overlay">
          <div className="wvd-modal-box">
            <div className="wvd-modal-header">
              <XCircle size={26} color="#e11d48" />
              <div>
                <h3 className="wvd-modal-title">Reject Registration</h3>
                <p style={{ margin: 0, fontSize: "12px", color: "#64748b" }}>
                  {rejectModalWorker.full_name} ({rejectModalWorker.user_id})
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
                    checked={rejectionReasonText === reason}
                    onChange={() => setRejectionReasonText(reason)}
                  />
                  <span>{reason}</span>
                </label>
              ))}
            </div>
            <div className="wvd-modal-actions">
              <button
                type="button"
                onClick={() => setRejectModalWorker(null)}
                className="wvd-btn-secondary"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleConfirmReject}
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

export default WorkerVerificationDesk;
