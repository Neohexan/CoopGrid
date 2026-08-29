import { useState } from 'react';

export default function App() {
  const [activeTab, setActiveTab] = useState('queue');
  const [selectedDoc, setSelectedDoc] = useState(null);

  const [workers, setWorkers] = useState([
    { id: "W_101", name: "Arvind", trade: "Android Dev", coop: "Jamshedpur Labour Coop", phone: "+91 98765 43210", doc: "ID-9921-AADHAAR" },
    { id: "W_102", name: "Ritin Kumar", trade: "web dev", coop: "Eastern Welfare", phone: "+91 91234 56789", doc: "CERT-CAREGIVER-L2" },
    { id: "W_103", name: "Piyush", trade: "Speaker", coop: "Ranchi Tech Coop", phone: "+91 99887 76655", doc: "ID-4432-AADHAAR" }
  ]);

  const [approvedWorkers, setApprovedWorkers] = useState([]);

  const handleApprove = (id) => {
    const approvedWorker = workers.find((w) => w.id === id);
    if (approvedWorker) {
      setApprovedWorkers([...approvedWorkers, approvedWorker]);
      setWorkers(workers.filter((w) => w.id !== id));
    }
  };

  const handleReject = (id) => {
    setWorkers(workers.filter((w) => w.id !== id));
  };

  return (
    <div className="flex flex-col md:flex-row h-screen bg-black text-gray-100 font-mono selection:bg-gray-500 overflow-hidden">
      
      {/* NAVIGATION (Top Bar on Mobile, Sidebar on Desktop) */}
      <div className="w-full md:w-64 border-b-2 md:border-b-0 md:border-r-2 border-gray-800 bg-gray-950 flex flex-col shrink-0 z-10">
        <div className="p-4 md:p-6 border-b-2 border-gray-800 flex justify-between items-center md:block">
          <h1 className="text-xl font-bold text-white tracking-widest uppercase">
            CoopGrid Admin
          </h1>
        </div>
        <nav className="flex flex-row md:flex-col overflow-x-auto">
          <button 
            onClick={() => setActiveTab('queue')}
            className={`flex-1 md:w-full whitespace-nowrap text-center md:text-left px-4 md:px-6 py-3 font-bold uppercase text-xs md:text-sm tracking-wider transition-colors border-b-4 md:border-b-0 md:border-l-4 ${activeTab === 'queue' ? 'bg-gray-800 border-white text-white' : 'border-transparent text-gray-500 hover:bg-gray-900 hover:text-gray-300'}`}
          >
            Verification Queue
          </button>
          <button 
            onClick={() => setActiveTab('forecast')}
            className={`flex-1 md:w-full whitespace-nowrap text-center md:text-left px-4 md:px-6 py-3 font-bold uppercase text-xs md:text-sm tracking-wider transition-colors border-b-4 md:border-b-0 md:border-l-4 ${activeTab === 'forecast' ? 'bg-gray-800 border-yellow-400 text-yellow-400' : 'border-transparent text-gray-500 hover:bg-gray-900 hover:text-gray-300'}`}
          >
            AI Forecasting
          </button>
        </nav>
      </div>

      {/* MAIN CONTENT AREA */}
      <div className="flex-1 flex flex-col bg-black overflow-hidden relative">
        
        {/* HEADER */}
        <header className="px-4 md:px-6 py-4 border-b-2 border-gray-800 flex justify-between items-center bg-gray-950 shrink-0">
          <div>
            <h2 className="text-lg md:text-2xl font-bold uppercase tracking-tight text-white">
              {activeTab === 'queue' ? 'Pending Verifications' : 'Demand Forecasting'}
            </h2>
            <p className="text-gray-500 text-xs mt-1 uppercase tracking-widest hidden md:block">
              {activeTab === 'queue' ? 'Review and approve cooperative workers' : 'Predictive models for service allocation'}
            </p>
          </div>
          {activeTab === 'queue' && (
            <div className="bg-black px-3 py-1.5 md:px-4 md:py-2 border border-gray-700 font-bold text-xs tracking-widest uppercase text-yellow-400 whitespace-nowrap">
              Queue: {workers.length}
            </div>
          )}
        </header>

        {/* TAB CONTENT */}
        <div className="flex-1 overflow-y-auto p-3 md:p-8">
          
          {/* QUEUE TAB */}
          {activeTab === 'queue' && (
            <div className="space-y-6 md:space-y-8">
              
              {/* PENDING TABLE */}
              <div className="border border-gray-800 bg-gray-950 shadow-[4px_4px_0_0_rgba(31,41,55,1)] flex flex-col max-w-full">
                <div className="p-3 md:p-4 border-b border-gray-800 bg-gray-900">
                  <h3 className="text-white font-bold uppercase tracking-widest text-xs md:text-sm">Action Required</h3>
                </div>
                <div className="overflow-x-auto w-full">
                  <table className="w-full min-w-[750px] text-left border-collapse text-sm">
                    <thead>
                      <tr className="bg-gray-900 border-b border-gray-800 text-xs uppercase tracking-widest text-gray-400 whitespace-nowrap">
                        <th className="p-3 md:p-4 border-r border-gray-800">Worker Profile</th>
                        <th className="p-3 md:p-4 border-r border-gray-800">Trade & Coop</th>
                        <th className="p-3 md:p-4 border-r border-gray-800 text-center">Docs</th>
                        <th className="p-3 md:p-4 text-center">Authorization</th>
                      </tr>
                    </thead>
                    <tbody>
                      {workers.length === 0 ? (
                        <tr><td colSpan="4" className="p-6 md:p-8 text-center text-gray-500 uppercase font-bold text-xs md:text-sm">Queue Empty</td></tr>
                      ) : (
                        workers.map((worker) => (
                          <tr key={worker.id} className="border-b border-gray-800 hover:bg-gray-900/50 transition-colors whitespace-nowrap">
                            <td className="p-3 md:p-4 border-r border-gray-800">
                              <div className="font-bold text-white">{worker.name}</div>
                              <div className="text-xs text-gray-500 mt-1">{worker.phone}</div>
                              <div className="text-xs text-gray-600 mt-1">{worker.id}</div>
                            </td>
                            <td className="p-3 md:p-4 border-r border-gray-800">
                              <div className="text-yellow-500 font-bold uppercase text-xs tracking-wider">{worker.trade}</div>
                              <div className="text-gray-400 text-xs mt-1">{worker.coop}</div>
                            </td>
                            <td className="p-3 md:p-4 border-r border-gray-800 text-center">
                              <button 
                                onClick={() => setSelectedDoc(worker.doc)}
                                className="px-3 py-1.5 border border-gray-600 text-gray-300 text-xs uppercase font-bold hover:bg-white hover:text-black transition-colors"
                              >
                                View
                              </button>
                            </td>
                            <td className="p-3 md:p-4 flex gap-2 justify-center items-center">
                              <button onClick={() => handleApprove(worker.id)} className="px-3 py-1.5 md:px-4 md:py-2 bg-white text-black font-bold uppercase text-[10px] md:text-xs tracking-wider hover:bg-gray-300 transition-colors">
                                Approve
                              </button>
                              <button onClick={() => handleReject(worker.id)} className="px-3 py-1.5 md:px-4 md:py-2 border border-gray-700 text-gray-400 font-bold uppercase text-[10px] md:text-xs tracking-wider hover:text-white hover:bg-gray-800 transition-colors">
                                Reject
                              </button>
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* APPROVED WORKERS SECTION */}
              <div className="border border-gray-800 bg-gray-950 shadow-[4px_4px_0_0_rgba(31,41,55,1)] flex flex-col max-w-full">
                <div className="p-3 md:p-4 border-b border-gray-800 bg-gray-900 flex justify-between items-center">
                  <h3 className="text-gray-400 font-bold uppercase tracking-widest text-xs md:text-sm">Recently Approved</h3>
                  <div className="text-[10px] md:text-xs font-bold text-emerald-400 uppercase tracking-widest">Total: {approvedWorkers.length}</div>
                </div>
                <div className="overflow-x-auto w-full">
                  <table className="w-full min-w-[600px] text-left border-collapse text-sm">
                    <thead>
                      <tr className="bg-gray-900 border-b border-gray-800 text-xs uppercase tracking-widest text-gray-500 whitespace-nowrap">
                        <th className="p-3 md:p-4 border-r border-gray-800 w-1/2">Worker Profile</th>
                        <th className="p-3 md:p-4 border-r border-gray-800">Trade & Coop</th>
                        <th className="p-3 md:p-4 text-center">Status</th>
                      </tr>
                    </thead>
                    <tbody className="opacity-80">
                      {approvedWorkers.length === 0 ? (
                        <tr><td colSpan="3" className="p-6 md:p-8 text-center text-gray-600 uppercase font-bold text-[10px] md:text-xs">No workers approved yet</td></tr>
                      ) : (
                        approvedWorkers.map((worker) => (
                          <tr key={`apprv-${worker.id}`} className="border-b border-gray-800 whitespace-nowrap">
                            <td className="p-3 md:p-4 border-r border-gray-800">
                              <div className="font-bold text-gray-300">{worker.name}</div>
                              <div className="text-xs text-gray-600 mt-1">{worker.id}</div>
                            </td>
                            <td className="p-3 md:p-4 border-r border-gray-800">
                              <div className="text-gray-400 font-bold uppercase text-xs tracking-wider">{worker.trade}</div>
                            </td>
                            <td className="p-3 md:p-4 text-center">
                              <span className="px-2 py-1 md:px-3 md:py-1 bg-emerald-900/30 text-emerald-500 border border-emerald-900/50 uppercase text-[10px] md:text-xs font-bold tracking-widest">
                                Active
                              </span>
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

            </div>
          )}

          {/* FORECAST TAB */}
          {activeTab === 'forecast' && (
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 md:gap-6">
              <div className="border border-gray-800 bg-gray-950 p-4 md:p-6 shadow-[4px_4px_0_0_rgba(31,41,55,1)]">
                <h3 className="text-yellow-400 font-bold uppercase tracking-widest text-xs md:text-sm mb-4 md:mb-6 border-b border-gray-800 pb-2">Demand Spike Alert</h3>
                <div className="text-3xl md:text-4xl font-bold text-white mb-2">+42%</div>
                <div className="text-gray-400 text-xs md:text-sm uppercase mb-4">Electricians Needed in Zone B (Next 7 Days)</div>
                <p className="text-[10px] md:text-xs text-gray-500 leading-relaxed">
                  AI model indicates severe weather patterns will trigger electrical maintenance requests. Recommend allocating 15 additional verified electricians to the Jamshedpur sector immediately.
                </p>
                <button className="mt-4 md:mt-6 w-full py-2.5 md:py-3 border border-yellow-400 text-yellow-400 hover:bg-yellow-400 hover:text-black font-bold uppercase text-[10px] md:text-xs tracking-widest transition-colors">
                  Trigger Cooperative Alert
                </button>
              </div>
              
              <div className="border border-gray-800 bg-gray-950 p-4 md:p-6 shadow-[4px_4px_0_0_rgba(31,41,55,1)]">
                <h3 className="text-white font-bold uppercase tracking-widest text-xs md:text-sm mb-4 md:mb-6 border-b border-gray-800 pb-2">Service Trend Forecast</h3>
                <div className="space-y-4">
                  <div>
                    <div className="flex justify-between text-[10px] md:text-xs uppercase mb-1">
                      <span className="text-gray-400">Plumbing</span>
                      <span className="text-white">High</span>
                    </div>
                    <div className="h-1.5 md:h-2 w-full bg-gray-900"><div className="h-full bg-white w-[80%]"></div></div>
                  </div>
                  <div>
                    <div className="flex justify-between text-[10px] md:text-xs uppercase mb-1">
                      <span className="text-gray-400">Carpentry</span>
                      <span className="text-white">Normal</span>
                    </div>
                    <div className="h-1.5 md:h-2 w-full bg-gray-900"><div className="h-full bg-gray-500 w-[45%]"></div></div>
                  </div>
                  <div>
                    <div className="flex justify-between text-[10px] md:text-xs uppercase mb-1">
                      <span className="text-gray-400">Caregiving</span>
                      <span className="text-white">Low</span>
                    </div>
                    <div className="h-1.5 md:h-2 w-full bg-gray-900"><div className="h-full bg-gray-700 w-[20%]"></div></div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* DOCUMENT MODAL */}
        {selectedDoc && (
          <div className="absolute inset-0 bg-black/90 flex items-center justify-center p-4 z-50">
            <div className="border border-gray-700 bg-gray-950 w-full max-w-md p-4 md:p-6 shadow-[8px_8px_0_0_rgba(255,255,255,0.1)]">
              <div className="flex justify-between items-center mb-4 md:mb-6 border-b border-gray-800 pb-2 md:pb-4">
                <h3 className="text-white font-bold uppercase tracking-widest text-xs md:text-sm">Document Viewer</h3>
                <button onClick={() => setSelectedDoc(null)} className="text-gray-500 hover:text-white font-bold text-lg md:text-base">✕</button>
              </div>
              <div className="aspect-video bg-gray-900 border border-gray-800 flex items-center justify-center mb-4 md:mb-6">
                <span className="text-gray-600 font-mono text-[10px] md:text-xs uppercase text-center px-4">
                  [ Encrypted Image Data ]<br/><br/>{selectedDoc}
                </span>
              </div>
              <button onClick={() => setSelectedDoc(null)} className="w-full py-2.5 md:py-3 bg-white text-black font-bold uppercase text-[10px] md:text-xs tracking-widest hover:bg-gray-300 transition-colors">
                Close Viewer
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
