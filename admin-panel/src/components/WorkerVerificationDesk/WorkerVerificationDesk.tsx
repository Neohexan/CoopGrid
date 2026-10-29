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
} from "lucide-react";

export interface ApiWorker {
  user_id: string;
  full_name: string;
  gender: "male" | "female";
  categories: string[];
  is_active: "active" | "inactive" | "pending" | "rejected" | "suspended";
  created_at: string;
  phone_number: string;
  state: string;
  pincode: string;
  district: string;
  profile_picture?: string;
  rejection_reason?: string;
}

export const INITIAL_API_WORKERS: ApiWorker[] = [
  {
    user_id: "worker_001",
    full_name: "Ramesh Kumar",
    gender: "male",
    categories: ["plumbing", "electrical"],
    is_active: "pending",
    created_at: "2026-01-15T10:30:00Z",
    phone_number: "+91 87978 70443",
    state: "Odisha",
    district: "Rayagada",
    pincode: "765022",
    profile_picture: "/assets/image1.jpg",
  },
  {
    user_id: "worker_002",
    full_name: "Sita Devi",
    gender: "female",
    categories: ["agriculture", "handloom"],
    is_active: "pending",
    created_at: "2026-01-16T08:15:00Z",
    phone_number: "+91 94381 02345",
    state: "Odisha",
    district: "Rayagada",
    pincode: "765017",
    profile_picture: "/assets/image2.jpeg",
  },
  {
    user_id: "worker_003",
    full_name: "Abhimanyu Kumar",
    gender: "male",
    categories: ["plumbing", "masonry"],
    is_active: "active",
    created_at: "2026-01-10T14:20:00Z",
    phone_number: "+91 98765 43211",
    state: "Odisha",
    district: "Rayagada",
    pincode: "765022",
    profile_picture: "/assets/image3.jpeg",
  },
  {
    user_id: "worker_004",
    full_name: "Kavita Sahu",
    gender: "female",
    categories: ["pottery", "agriculture"],
    is_active: "pending",
    created_at: "2026-01-18T11:45:00Z",
    phone_number: "+91 94378 90123",
    state: "Odisha",
    district: "Koraput",
    pincode: "764020",
    profile_picture: "/assets/image4.jpg",
  },
  {
    user_id: "worker_005",
    full_name: "Prakash Nayak",
    gender: "male",
    categories: ["carpentry", "electrical"],
    is_active: "inactive",
    created_at: "2026-01-08T09:00:00Z",
    phone_number: "+91 98612 34567",
    state: "Odisha",
    district: "Ganjam",
    pincode: "760001",
    profile_picture: "/assets/image5.jpeg",
  },
  {
    user_id: "worker_006",
    full_name: "Lakshmi Majhi",
    gender: "female",
    categories: ["handloom", "tailoring"],
    is_active: "active",
    created_at: "2026-01-19T09:30:00Z",
    phone_number: "+91 94371 11223",
    state: "Odisha",
    district: "Kalahandi",
    pincode: "766001",
    profile_picture: "/assets/image6.jpeg",
  },
  {
    user_id: "worker_007",
    full_name: "Bikramunda Sardar",
    gender: "male",
    categories: ["masonry", "agriculture"],
    is_active: "pending",
    created_at: "2026-01-20T12:10:00Z",
    phone_number: "+91 82490 55432",
    state: "Jharkhand",
    district: "Seraikela",
    pincode: "832108",
  },
  {
    user_id: "worker_008",
    full_name: "Meena Kumari",
    gender: "female",
    categories: ["agriculture", "bamboo craft"],
    is_active: "active",
    created_at: "2026-01-21T15:40:00Z",
    phone_number: "+91 79785 66789",
    state: "Jharkhand",
    district: "East Singhbhum",
    pincode: "831001",
  },
  {
    user_id: "worker_009",
    full_name: "Santosh Mahato",
    gender: "male",
    categories: ["electrical", "solar repair"],
    is_active: "pending",
    created_at: "2026-01-22T10:05:00Z",
    phone_number: "+91 99370 44556",
    state: "Jharkhand",
    district: "Ranchi",
    pincode: "834001",
  },
  {
    user_id: "worker_010",
    full_name: "Anita Behera",
    gender: "female",
    categories: ["pottery", "handloom"],
    is_active: "active",
    created_at: "2026-01-23T16:20:00Z",
    phone_number: "+91 94385 99887",
    state: "Odisha",
    district: "Mayurbhanj",
    pincode: "757001",
  },
  {
    user_id: "worker_011",
    full_name: "Debasis Mohanty",
    gender: "male",
    categories: ["carpentry", "plumbing"],
    is_active: "pending",
    created_at: "2026-01-24T11:00:00Z",
    phone_number: "+91 98610 22334",
    state: "Odisha",
    district: "Cuttack",
    pincode: "753001",
  },
  {
    user_id: "worker_012",
    full_name: "Sunita Murmu",
    gender: "female",
    categories: ["agriculture", "food processing"],
    is_active: "pending",
    created_at: "2026-01-25T13:45:00Z",
    phone_number: "+91 88950 77665",
    state: "Jharkhand",
    district: "West Singhbhum",
    pincode: "833201",
  },
];

type FilterTab = "ALL" | "MALE" | "FEMALE";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v2";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";
const WORKERS_PER_PAGE = 10;

export const WorkerVerificationDesk: React.FC = () => {
  // Hydration Fix: Start with default state, sync after mount
  const [isMounted, setIsMounted] = useState(false);
  const [workers, setWorkers] = useState<ApiWorker[]>(INITIAL_API_WORKERS);
  const [viewedWorkerIds, setViewedWorkerIds] = useState<string[]>([]);

  const [activeFilter, setActiveFilter] = useState<FilterTab>("ALL");
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [showActionInfoModal, setShowActionInfoModal] = useState(false);
  const [rejectModalWorker, setRejectModalWorker] = useState<ApiWorker | null>(null);
  const [rejectionReasonText, setRejectionReasonText] = useState("Incomplete verification documentation");
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Safely sync with localStorage only on the client
  useEffect(() => {
    setIsMounted(true);

    try {
      const storedWorkers = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
      if (storedWorkers) {
        setWorkers(JSON.parse(storedWorkers));
      } else {
        localStorage.setItem(LOCAL_STORAGE_WORKERS_KEY, JSON.stringify(INITIAL_API_WORKERS));
      }

      const storedViewed = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
      if (storedViewed) {
        setViewedWorkerIds(JSON.parse(storedViewed));
      }
    } catch {}

    const handleStorageSync = () => {
      try {
        const sw = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
        if (sw) setWorkers(JSON.parse(sw));
        const sv = localStorage.getItem(LOCAL_STORAGE_VIEWED_KEY);
        if (sv) setViewedWorkerIds(JSON.parse(sv));
      } catch {}
    };

    window.addEventListener("storage", handleStorageSync);
    return () => window.removeEventListener("storage", handleStorageSync);
  }, []);

  const updateWorkers = (newWorkers: ApiWorker[]) => {
    setWorkers(newWorkers);
    try {
      localStorage.setItem(LOCAL_STORAGE_WORKERS_KEY, JSON.stringify(newWorkers));
    } catch {}
  };

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  // Counts
  const pendingCount = useMemo(() => workers.filter((w) => w.is_active === "pending").length, [workers]);
  const femaleCount = useMemo(() => workers.filter((w) => w.gender === "female").length, [workers]);
  const maleCount = useMemo(() => workers.filter((w) => w.gender === "male").length, [workers]);

  // Filtered Workers
  const filteredWorkers = useMemo(() => {
    return workers.filter((worker) => {
      if (activeFilter === "FEMALE" && worker.gender !== "female") return false;
      if (activeFilter === "MALE" && worker.gender !== "male") return false;

      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase().trim();
        const matchName = worker.full_name.toLowerCase().includes(q);
        const matchId = worker.user_id.toLowerCase().includes(q);
        const matchPhone = worker.phone_number.includes(q);
        const matchDistrict = worker.district.toLowerCase().includes(q);
        const matchState = worker.state.toLowerCase().includes(q);
        const matchPincode = worker.pincode.includes(q);
        const matchCategories = worker.categories.some((c) => c.toLowerCase().includes(q));
        if (!matchName && !matchId && !matchPhone && !matchDistrict && !matchState && !matchPincode && !matchCategories) {
          return false;
        }
      }
      return true;
    });
  }, [workers, activeFilter, searchQuery]);

  // Reset to page 1 when filter or search changes
  useEffect(() => {
    setCurrentPage(1);
  }, [activeFilter, searchQuery]);

  // Pagination Logic
  const totalPages = Math.max(1, Math.ceil(filteredWorkers.length / WORKERS_PER_PAGE));
  const paginatedWorkers = useMemo(() => {
    const startIndex = (currentPage - 1) * WORKERS_PER_PAGE;
    return filteredWorkers.slice(startIndex, startIndex + WORKERS_PER_PAGE);
  }, [filteredWorkers, currentPage]);

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
    const updated = workers.map((w) => (w.user_id === userId ? { ...w, is_active: "active" as const } : w));
    updateWorkers(updated);
    showToast(`✓ Worker ${userId} approved and activated.`);
  };

  const handleConfirmReject = () => {
    if (!rejectModalWorker) return;
    const targetId = rejectModalWorker.user_id;
    const updated = workers.map((w) =>
      w.user_id === targetId ? { ...w, is_active: "rejected" as const, rejection_reason: rejectionReasonText } : w
    );
    updateWorkers(updated);
    showToast(`✕ Application for ${rejectModalWorker.full_name} rejected.`);
    setRejectModalWorker(null);
  };

  // Prevents the hydration mismatch error entirely
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
            <span>All</span>
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
                {pendingCount} Worker Registration(s) Awaiting Federation Approval
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
              placeholder="Search by Full Name, User ID, Category, Phone, or District..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="wvd-search-input"
            />
          </div>
          <span className="wvd-count-text">
            Showing{" "}
            <strong>
              {filteredWorkers.length === 0
                ? 0
                : (currentPage - 1) * WORKERS_PER_PAGE + 1}
              –
              {Math.min(currentPage * WORKERS_PER_PAGE, filteredWorkers.length)}
            </strong>{" "}
            of <strong>{filteredWorkers.length}</strong> workers
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
                {paginatedWorkers.map((worker) => {
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
                })}
              </tbody>
            </table>
          </div>

          {/* Pagination Footer (10 per page) */}
          {totalPages > 1 && (
            <div className="wvd-pagination">
              <span className="wvd-page-info">
                Page <strong>{currentPage}</strong> of <strong>{totalPages}</strong> (Max {WORKERS_PER_PAGE} per page)
              </span>
              <div className="wvd-page-controls">
                <button
                  type="button"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  className="wvd-page-btn"
                >
                  Previous
                </button>
                {Array.from({ length: totalPages }, (_, idx) => idx + 1).map((pageNum) => (
                  <button
                    key={pageNum}
                    type="button"
                    onClick={() => setCurrentPage(pageNum)}
                    className={`wvd-page-btn ${currentPage === pageNum ? "active" : ""}`}
                  >
                    {pageNum}
                  </button>
                ))}
                <button
                  type="button"
                  disabled={currentPage === totalPages}
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
