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
  Eye,
  Info,
  ExternalLink,
  HelpCircle,
  RefreshCw,
} from "lucide-react";

export interface ApiWorker {
  user_id: string;
  full_name: string;
  gender: "male" | "female" | string;
  categories: string[];
  is_active: "active" | "inactive" | "pending" | "rejected" | "suspended" | string;
  created_at: string;
  // Demo fields injected on frontend
  phone_number?: string;
  profile_picture?: string;
  rejection_reason?: string;
}

const DEMO_PHOTOS = [
  "/assets/image1.jpg",
  "/assets/image2.jpeg",
  "/assets/image3.jpeg",
  "/assets/image4.jpg",
  "/assets/image5.jpeg",
  "/assets/image6.jpeg",
];

// Deterministic random-looking phone generator per worker ID
export const getDemoPhone = (id: string, idx: number = 0): string => {
  let hash = 0;
  for (let i = 0; i < id.length; i++) {
    hash = (hash * 31 + id.charCodeAt(i)) % 900000000;
  }
  const num = 9000000000 + ((hash + idx * 1234567) % 999999999);
  const str = String(num);
  return `+91 ${str.slice(0, 5)} ${str.slice(5, 10)}`;
};

// Exported Demo Data Fallback (12 workers matching summary contract + demo photo/phone)
export const INITIAL_API_WORKERS: ApiWorker[] = [
  {
    user_id: "worker_001",
    full_name: "Ramesh Kumar",
    gender: "male",
    categories: ["plumbing", "electrical"],
    is_active: "pending",
    created_at: "2026-01-15T10:30:00Z",
    phone_number: "+91 87978 70443",
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
    profile_picture: "/assets/image1.jpg",
  },
  {
    user_id: "worker_008",
    full_name: "Meena Kumari",
    gender: "female",
    categories: ["agriculture", "bamboo craft"],
    is_active: "active",
    created_at: "2026-01-21T15:40:00Z",
    phone_number: "+91 79785 66789",
    profile_picture: "/assets/image2.jpeg",
  },
  {
    user_id: "worker_009",
    full_name: "Santosh Mahato",
    gender: "male",
    categories: ["electrical", "solar repair"],
    is_active: "pending",
    created_at: "2026-01-22T10:05:00Z",
    phone_number: "+91 99370 44556",
    profile_picture: "/assets/image3.jpeg",
  },
  {
    user_id: "worker_010",
    full_name: "Anita Behera",
    gender: "female",
    categories: ["pottery", "handloom"],
    is_active: "active",
    created_at: "2026-01-23T16:20:00Z",
    phone_number: "+91 94385 99887",
    profile_picture: "/assets/image4.jpg",
  },
  {
    user_id: "worker_011",
    full_name: "Debasis Mohanty",
    gender: "male",
    categories: ["carpentry", "plumbing"],
    is_active: "pending",
    created_at: "2026-01-24T11:00:00Z",
    phone_number: "+91 98610 22334",
    profile_picture: "/assets/image5.jpeg",
  },
  {
    user_id: "worker_012",
    full_name: "Sunita Murmu",
    gender: "female",
    categories: ["agriculture", "food processing"],
    is_active: "pending",
    created_at: "2026-01-25T13:45:00Z",
    phone_number: "+91 88950 77665",
    profile_picture: "/assets/image6.jpeg",
  },
];

type FilterTab = "ALL" | "MALE" | "FEMALE";

const LOCAL_STORAGE_WORKERS_KEY = "coopgrid_api_workers_v3";
const LOCAL_STORAGE_VIEWED_KEY = "coopgrid_viewed_workers_v1";
const WORKERS_PER_PAGE = 10;
const API_BASE_URL = "https://admin-api.neohexane.com";

export const PRESET_REJECTION_REASONS = [
  "Incomplete verification documentation",
  "Uploaded ID photo is blurred or unreadable",
  "Name on KYC document does not match profile",
  "Invalid trade skill certificate",
];

export const WorkerVerificationDesk: React.FC = () => {
  const [isMounted, setIsMounted] = useState(false);

  // Data State
  const [workers, setWorkers] = useState<ApiWorker[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);

  // Interaction State
  const [viewedWorkerIds, setViewedWorkerIds] = useState<string[]>([]);
  const [activeFilter, setActiveFilter] = useState<FilterTab>("ALL");
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [showActionInfoModal, setShowActionInfoModal] = useState(false);

  // Reject Modal State (Supports preset selection OR custom written reason)
  const [rejectModalWorker, setRejectModalWorker] = useState<ApiWorker | null>(null);
  const [selectedReasonMode, setSelectedReasonMode] = useState<string>(PRESET_REJECTION_REASONS[0]);
  const [customReasonText, setCustomReasonText] = useState<string>("");

  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Initialize and attach cross-tab listener for viewed workers & status updates
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

        const localStored = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
        if (localStored) {
          const localMap: Record<string, ApiWorker> = JSON.parse(localStored);
          setWorkers((prev) =>
            prev.map((w) => (localMap[w.user_id] ? { ...w, ...localMap[w.user_id] } : w))
          );
        }
      } catch {}
    };

    window.addEventListener("storage", handleStorageSync);
    return () => window.removeEventListener("storage", handleStorageSync);
  }, []);

  // 1. Fetch Workers Summary List (GET /api/profile/admin/summary/workers?skip=X&limit=10)
  useEffect(() => {
    const fetchWorkers = async () => {
      setIsLoading(true);
      const skip = (currentPage - 1) * WORKERS_PER_PAGE;
      const limit = WORKERS_PER_PAGE;

      try {
        const response = await fetch(
          `${API_BASE_URL}/api/profile/admin/summary/workers?skip=${skip}&limit=${limit}`,
          {
            method: "GET",
            headers: {
              Accept: "application/json",
            },
            credentials: "include",
          }
        );

        const data = await response.json();

        if (response.ok && data.success && Array.isArray(data.workers)) {
          const enrichedWorkers: ApiWorker[] = data.workers.map((apiWorker: any, idx: number) => ({
            user_id: String(apiWorker.user_id || ""),
            full_name: String(apiWorker.full_name || "Unknown Worker"),
            gender: String(apiWorker.gender || "male"),
            categories: Array.isArray(apiWorker.categories) ? apiWorker.categories : [],
            is_active: String(apiWorker.is_active || "pending"),
            created_at: String(apiWorker.created_at || new Date().toISOString()),
            // Demo photo placeholder & random phone number
            phone_number: getDemoPhone(String(apiWorker.user_id || `w_${idx}`), idx),
            profile_picture: DEMO_PHOTOS[(skip + idx) % DEMO_PHOTOS.length],
          }));

          const localStored = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
          if (localStored) {
            const localMap: Record<string, ApiWorker> = JSON.parse(localStored);
            setWorkers(
              enrichedWorkers.map((w) => (localMap[w.user_id] ? { ...w, ...localMap[w.user_id] } : w))
            );
          } else {
            setWorkers(enrichedWorkers);
          }

          setTotalCount(typeof data.total_count === "number" ? data.total_count : enrichedWorkers.length);
        } else {
          throw new Error("Invalid or empty summary response");
        }
      } catch (error) {
        console.warn("API Fetch failed, falling back to demo dataset:", error);
        const demoDataPage = INITIAL_API_WORKERS.slice(skip, skip + limit);

        const localStored = localStorage.getItem(LOCAL_STORAGE_WORKERS_KEY);
        if (localStored) {
          const localMap: Record<string, ApiWorker> = JSON.parse(localStored);
          setWorkers(
            demoDataPage.map((w) => (localMap[w.user_id] ? { ...w, ...localMap[w.user_id] } : w))
          );
        } else {
          setWorkers(demoDataPage);
        }

        setTotalCount(INITIAL_API_WORKERS.length);
      } finally {
        setIsLoading(false);
      }
    };

    if (isMounted) {
      fetchWorkers();
    }
  }, [currentPage, isMounted]);

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

  // Counts
  const pendingCount = useMemo(
    () => workers.filter((w) => w.is_active.toLowerCase() === "pending").length,
    [workers]
  );
  const femaleCount = useMemo(
    () => workers.filter((w) => w.gender.toLowerCase() === "female").length,
    [workers]
  );
  const maleCount = useMemo(
    () => workers.filter((w) => w.gender.toLowerCase() === "male").length,
    [workers]
  );

  // Filtered Workers
  const filteredWorkers = useMemo(() => {
    return workers.filter((worker) => {
      if (activeFilter === "FEMALE" && worker.gender.toLowerCase() !== "female") return false;
      if (activeFilter === "MALE" && worker.gender.toLowerCase() !== "male") return false;

      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase().trim();
        const matchName = worker.full_name.toLowerCase().includes(q);
        const matchId = worker.user_id.toLowerCase().includes(q);
        const matchPhone = (worker.phone_number || "").toLowerCase().includes(q);
        const matchCategories = worker.categories.some((c) => c.toLowerCase().includes(q));
        if (!matchName && !matchId && !matchPhone && !matchCategories) {
          return false;
        }
      }
      return true;
    });
  }, [workers, activeFilter, searchQuery]);

  const totalPages = Math.max(1, Math.ceil(totalCount / WORKERS_PER_PAGE));

  const handleViewWorker = (worker: ApiWorker) => {
    if (!viewedWorkerIds.includes(worker.user_id)) {
      const updatedViewed = [...viewedWorkerIds, worker.user_id];
      setViewedWorkerIds(updatedViewed);
      try {
        localStorage.setItem(LOCAL_STORAGE_VIEWED_KEY, JSON.stringify(updatedViewed));
      } catch {}
    }
    window.open(`/workers/${encodeURIComponent(worker.user_id)}`, "_blank");
    showToast(`Opened ${worker.user_id} in new tab. Actions unlocked!`);
  };

  // 2. Verify / Approve Worker API (PUT /api/profile/admin/workers/{worker_id}/verify)
  const handleApprove = async (userId: string) => {
    setActionLoadingId(userId);
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/profile/admin/workers/${encodeURIComponent(userId)}/verify`,
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
        throw new Error(data?.message || "Verification API returned an error");
      }

      const updatedWorkers = workers.map((w) => {
        if (w.user_id === userId) {
          const updated = { ...w, is_active: "active", rejection_reason: undefined };
          persistWorkerStateLocally(updated);
          return updated;
        }
        return w;
      });
      setWorkers(updatedWorkers);
      showToast(data?.message || `✓ Worker ${userId} verified successfully.`);
    } catch (error) {
      // Fallback local update if backend is offline during demo
      const updatedWorkers = workers.map((w) => {
        if (w.user_id === userId) {
          const updated = { ...w, is_active: "active", rejection_reason: undefined };
          persistWorkerStateLocally(updated);
          return updated;
        }
        return w;
      });
      setWorkers(updatedWorkers);
      showToast(`✓ Worker ${userId} marked as VERIFIED.`);
    } finally {
      setActionLoadingId(null);
    }
  };

  // 3. Reject Worker API (PUT /api/profile/admin/workers/{worker_id}/verify)
  const handleConfirmReject = async () => {
    if (!rejectModalWorker) return;

    const finalReason =
      selectedReasonMode === "CUSTOM" ? customReasonText.trim() : selectedReasonMode.trim();

    if (!finalReason) {
      showToast("⚠️ Please select or write a valid rejection reason.");
      return;
    }

    const targetId = rejectModalWorker.user_id;
    setActionLoadingId(targetId);

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
        throw new Error(data?.message || "Rejection request failed");
      }

      const updatedWorkers = workers.map((w) => {
        if (w.user_id === targetId) {
          const updated = { ...w, is_active: "rejected", rejection_reason: finalReason };
          persistWorkerStateLocally(updated);
          return updated;
        }
        return w;
      });

      setWorkers(updatedWorkers);
      showToast(data?.message || `✕ Worker ${rejectModalWorker.full_name} rejected.`);
    } catch (error) {
      // Fallback local update if backend is offline during demo
      const updatedWorkers = workers.map((w) => {
        if (w.user_id === targetId) {
          const updated = { ...w, is_active: "rejected", rejection_reason: finalReason };
          persistWorkerStateLocally(updated);
          return updated;
        }
        return w;
      });
      setWorkers(updatedWorkers);
      showToast(`✕ Application for ${rejectModalWorker.full_name} rejected.`);
    } finally {
      setActionLoadingId(null);
      setRejectModalWorker(null);
      setCustomReasonText("");
    }
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

        {/* Filter Pills: All, Male, Female */}
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
              placeholder="Search by Full Name, User ID, Phone, or Category..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="wvd-search-input"
            />
          </div>
          <span className="wvd-count-text">
            Showing Page <strong>{currentPage}</strong> of <strong>{totalPages}</strong> (Total{" "}
            <strong>{totalCount}</strong> registered)
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
                  <th>Categories</th>
                  <th>Created At</th>
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
                    <td colSpan={8} style={{ textAlign: "center", padding: "40px" }}>
                      <RefreshCw size={24} className="animate-spin" style={{ margin: "0 auto 8px auto", color: "#059669" }} />
                      <p style={{ color: "#64748b", fontSize: "13px", margin: 0 }}>
                        Fetching workforce summary...
                      </p>
                    </td>
                  </tr>
                ) : filteredWorkers.length === 0 ? (
                  <tr>
                    <td colSpan={8} style={{ textAlign: "center", padding: "40px", color: "#64748b" }}>
                      No workers found for this page or filter criteria.
                    </td>
                  </tr>
                ) : (
                  filteredWorkers.map((worker, idx) => {
                    const isViewed = viewedWorkerIds.includes(worker.user_id);
                    const isProcessing = actionLoadingId === worker.user_id;
                    const formattedDate = new Date(worker.created_at).toLocaleDateString("en-IN", {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    });

                    const displayPhone = worker.phone_number || getDemoPhone(worker.user_id, idx);
                    const displayPhoto = worker.profile_picture || DEMO_PHOTOS[idx % DEMO_PHOTOS.length];

                    return (
                      <tr key={worker.user_id}>
                        <td>
                          <div className="wvd-worker-id-cell">
                            <div className="wvd-avatar">
                              {displayPhoto ? (
                                <img src={displayPhoto} alt={worker.full_name} />
                              ) : (
                                <div className="wvd-avatar-fallback">{worker.user_id.slice(-3)}</div>
                              )}
                            </div>
                            <div className="wvd-id-meta">
                              <span className="wvd-id-code">{worker.user_id}</span>
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
                          <span className={`wvd-gender-badge ${worker.gender.toLowerCase()}`}>
                            {worker.gender.toLowerCase() === "female" ? "👩 Female" : "👨 Male"}
                          </span>
                        </td>

                        <td>
                          <div className="wvd-phone">
                            <Phone size={13} />
                            <span>{displayPhone}</span>
                          </div>
                        </td>

                        <td>
                          <div className="wvd-categories">
                            {worker.categories && worker.categories.length > 0 ? (
                              worker.categories.map((cat) => (
                                <span key={cat} className="wvd-cat-chip">
                                  {cat}
                                </span>
                              ))
                            ) : (
                              <span className="wvd-unlock-hint">None</span>
                            )}
                          </div>
                        </td>

                        <td>
                          <span className="wvd-reg-date">{formattedDate}</span>
                        </td>

                        <td>
                          <span className={`wvd-status-badge ${worker.is_active.toLowerCase()}`}>
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
                                {worker.is_active.toLowerCase() !== "active" &&
                                  worker.is_active.toLowerCase() !== "verified" && (
                                    <button
                                      type="button"
                                      disabled={isProcessing}
                                      onClick={() => handleApprove(worker.user_id)}
                                      className="wvd-approve-btn"
                                    >
                                      <Check size={14} />
                                      <span>{isProcessing ? "..." : "Approve"}</span>
                                    </button>
                                  )}

                                {worker.is_active.toLowerCase() !== "rejected" && (
                                  <button
                                    type="button"
                                    disabled={isProcessing}
                                    onClick={() => {
                                      setRejectModalWorker(worker);
                                      setSelectedReasonMode(PRESET_REJECTION_REASONS[0]);
                                      setCustomReasonText("");
                                    }}
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
                Page <strong>{currentPage}</strong> of <strong>{totalPages}</strong> (Limit:{" "}
                {WORKERS_PER_PAGE} per page)
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
              Administrators must inspect the worker&apos;s full profile at{" "}
              <code>/workers/&#123;worker-id&#125;</code> in a new tab using the View icon before
              the Approve and Reject options unlock.
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

      {/* Reject Modal (Select Preset OR Write Custom Reason) */}
      {rejectModalWorker && (
        <div className="wvd-modal-overlay">
          <div className="wvd-modal-box">
            <div className="wvd-modal-header">
              <XCircle size={26} color="#e11d48" />
              <div>
                <h3 className="wvd-modal-title">Reject Worker KYC</h3>
                <p style={{ margin: 0, fontSize: "12px", color: "#64748b" }}>
                  {rejectModalWorker.full_name} ({rejectModalWorker.user_id})
                </p>
              </div>
            </div>

            <div className="wvd-reason-list">
              {PRESET_REJECTION_REASONS.map((reason) => (
                <label key={reason} className="wvd-reason-item">
                  <input
                    type="radio"
                    name="rejection_reason"
                    checked={selectedReasonMode === reason}
                    onChange={() => setSelectedReasonMode(reason)}
                  />
                  <span>{reason}</span>
                </label>
              ))}

              <label className="wvd-reason-item">
                <input
                  type="radio"
                  name="rejection_reason"
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
                onClick={() => setRejectModalWorker(null)}
                className="wvd-btn-secondary"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={
                  actionLoadingId === rejectModalWorker.user_id ||
                  (selectedReasonMode === "CUSTOM" && !customReasonText.trim())
                }
                onClick={handleConfirmReject}
                className="wvd-btn-danger"
              >
                {actionLoadingId === rejectModalWorker.user_id
                  ? "Submitting..."
                  : "Confirm Rejection"}
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
