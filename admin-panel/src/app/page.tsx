"use client";

import React from "react";

export default function Home() {
  return (
    <main className="font-sans selection:bg-black selection:text-white flex-1 flex flex-col">
      <div className="max-w-7xl w-full mx-auto px-6 py-10 flex-1 flex flex-col justify-between">
        <div>
          {/* Welcome Sub-banner */}
          <div className="mb-8">
            <h2 className="text-2xl font-bold tracking-tight">
              CoopGrid Admin
            </h2>
            <p className="text-xs font-mono text-neutral-500 uppercase tracking-widest mt-0.5">
              Decentralized Gig & Community Platform Core
            </p>
          </div>

          {/* Dashboard Core Content Grid */}
          <section className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
            {/* Metric Card 1 */}
            <div className="bg-white p-6 rounded-2xl border border-neutral-200 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-[10px] font-mono tracking-wider text-neutral-400 uppercase">
                  System Status
                </span>
                <h3 className="text-xl font-semibold mt-1 text-emerald-600 flex items-center gap-2">
                  <span className="size-2 rounded-full bg-emerald-500 animate-pulse" />
                  Synchronized Active
                </h3>
              </div>
              <p className="text-xs text-neutral-500 mt-4 font-mono">
                Worker verification queues and lease pipelines are live.
              </p>
            </div>

            {/* Metric Card 2 */}
            <div className="bg-white p-6 rounded-2xl border border-neutral-200 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-[10px] font-mono tracking-wider text-neutral-400 uppercase">
                  Verification Pipeline
                </span>
                <h3 className="text-xl font-semibold mt-1">Pending Review</h3>
              </div>
              <p className="text-xs text-neutral-500 mt-4 font-mono">
                New worker profiles awaiting proximity mapping.
              </p>
            </div>

            {/* Metric Card 3 */}
            <div className="bg-white p-6 rounded-2xl border border-neutral-200 shadow-sm flex flex-col justify-between">
              <div>
                <span className="text-[10px] font-mono tracking-wider text-neutral-400 uppercase">
                  Agri-Resource Hub
                </span>
                <h3 className="text-xl font-semibold mt-1">Active Leases</h3>
              </div>
              <p className="text-xs text-neutral-500 mt-4 font-mono">
                Peer-to-peer machinery and fertilizer exchange feeds active.
              </p>
            </div>
          </section>

          {/* Platform Mission Statement Block */}
          <section className="bg-white border border-neutral-200 p-8 rounded-2xl shadow-sm">
            <h2 className="text-lg font-medium mb-2">Platform Overview</h2>
            <p className="text-neutral-600 text-sm leading-relaxed max-w-3xl">
              CoopGrid is a digital ecosystem designed to break the monopoly of
              private service platforms by directly connecting skilled
              cooperative federation workers with local households. Powered by
              an offline-first mobile app alongside this utilitarian React admin
              dashboard, the system eliminates restrictive verification
              bottlenecks and middleman exploitation.
            </p>
          </section>
        </div>

        {/* Footer */}
        <footer className="text-center text-xs text-neutral-400 font-mono py-6 border-t border-neutral-200 mt-12">
          CoopGrid Admin Panel • Secured via ED25519 SSH Cryptographic Signing
        </footer>
      </div>
    </main>
  );
}
