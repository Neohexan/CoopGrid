"use client";

import React, { useState, useEffect } from "react";
import { usePathname } from "next/navigation";
import { AdminLogin, AdminSessionData } from "./AdminLogin/AdminLogin";
import { Header } from "./Header/Header";
import { PensatoriPreloader } from "./preloader/PensatoriPreloader"; // Adjust path if needed

const SESSION_STORAGE_KEY = "coopgrid_admin_session";
const PRELOADER_SEEN_KEY = "coopgrid_preloader_seen";

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [adminSession, setAdminSession] = useState<AdminSessionData | null>(null);
  const [isHydrated, setIsHydrated] = useState(false);
  
  // Global Preloader State
  const [showPreloader, setShowPreloader] = useState(false);
  const [replayKey, setReplayKey] = useState(0);

  const pathname = usePathname();

  useEffect(() => {
    try {
      const saved = sessionStorage.getItem(SESSION_STORAGE_KEY);
      if (saved) {
        setAdminSession(JSON.parse(saved));
        
        // If they are logged in but haven't seen the preloader this session (e.g. fresh tab)
        const hasSeenPreloader = sessionStorage.getItem(PRELOADER_SEEN_KEY);
        if (!hasSeenPreloader) {
          setShowPreloader(true);
          sessionStorage.setItem(PRELOADER_SEEN_KEY, "true");
        }
      }
    } catch {}
    setIsHydrated(true);
  }, []);

  const handleLoginSuccess = (sessionData: AdminSessionData) => {
    setAdminSession(sessionData);
    try {
      sessionStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(sessionData));
      
      // Trigger preloader immediately on successful login
      setShowPreloader(true);
      sessionStorage.setItem(PRELOADER_SEEN_KEY, "true");
    } catch {}
  };

  const handleLogout = () => {
    setAdminSession(null);
    try {
      sessionStorage.removeItem(SESSION_STORAGE_KEY);
      sessionStorage.removeItem(PRELOADER_SEEN_KEY); // Reset preloader lock on logout
    } catch {}
  };

  const handleReplayPreloader = () => {
    setShowPreloader(true);
    setReplayKey((prev) => prev + 1);
  };

  if (!isHydrated) return null;

  if (!adminSession) {
    return <AdminLogin onLoginSuccess={handleLoginSuccess} />;
  }

  const isSingleWorkerPage = pathname.startsWith("/workers/") && pathname.split("/").length > 2;

  return (
    <>
      {/* Global Preloader Overlay */}
      {showPreloader && (
        <div className="fixed inset-0 z-[9999] bg-[#f5f5f5] flex items-center justify-center">
          <PensatoriPreloader 
            replayTrigger={replayKey} 
            speedMultiplier={1} 
            onComplete={() => setShowPreloader(false)} 
          />
        </div>
      )}

      {/* Global Header */}
      {!isSingleWorkerPage && (
        <Header 
          adminSession={adminSession} 
          onLogout={handleLogout} 
          onReplayPreloader={handleReplayPreloader} 
        />
      )}
      
      {/* Page Content */}
      {children}
    </>
  );
}
