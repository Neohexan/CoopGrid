import React from "react";

export default function Page() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[70vh] px-4">
      <div className="bg-white p-8 rounded-2xl shadow-sm border border-slate-200 max-w-md w-full text-center">
        <h1 className="text-2xl font-bold text-slate-800 mb-2">Coming Soon</h1>
        <div className="flex justify-center mb-4">
          <span className="w-12 h-1 rounded-full overflow-hidden flex">
            <span className="w-1/3 h-full bg-[#0284c7]" />
            <span className="w-1/3 h-full bg-[#10b981]" />
            <span className="w-1/3 h-full bg-[#84cc16]" />
          </span>
        </div>
        <p className="text-sm text-slate-500">
          This module is currently under development. Please check back later.
        </p>
      </div>
    </div>
  );
}
