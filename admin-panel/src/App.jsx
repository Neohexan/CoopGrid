import { useState, useEffect } from 'react';

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [apiError, setApiError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  
  const [workers, setWorkers] = useState([]);
  const [activeDoc, setActiveDoc] = useState(null);

  const fetchWorkers = async () => {
    try {
      setApiError(null); 
      console.log("Fetching worker data...");
      
      const response = await fetch('https://admin-api.neohexane.com/admin/pending-workers', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP Error! Status: ${response.status} ${response.statusText}`);
      }

      const rawData = await response.json();
      console.log("Raw Backend Data:", rawData);
      
      // Auto-detect the array no matter how the backend wrapped it
      let workersArray = null;
      if (Array.isArray(rawData)) {
        workersArray = rawData; // It's a pure array
      } else if (rawData && Array.isArray(rawData.data)) {
        workersArray = rawData.data; // Wrapped in { data: [...] }
      } else if (rawData && Array.isArray(rawData.workers)) {
        workersArray = rawData.workers; // Wrapped in { workers: [...] }
      } else if (rawData && typeof rawData === 'object') {
        // Find ANY array inside the object
        const foundArray = Object.values(rawData).find(val => Array.isArray(val));
        if (foundArray) workersArray = foundArray;
      }

      if (workersArray) {
        setWorkers(workersArray);
      } else {
        throw new Error("Connected successfully, but no Array was found in the response. The backend sent this exact data: \n\n" + JSON.stringify(rawData, null, 2));
      }
    } catch (err) {
      console.error("Fetch failed:", err);
      setApiError(err.toString());
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchWorkers();
    }
  }, [isLoggedIn]);

  const unapprovedWorkers = workers.filter(w => w.verification_status === 'Unapproved');
  const verifiedWorkers = workers.filter(w => w.verification_status === 'Approved');
  const blacklistedWorkers = workers.filter(w => w.verification_status === 'Rejected');

  const handleLogin = (e) => {
    e.preventDefault();
    if (credentials.username === 'SIH_Admin' && credentials.password === 'BCAKCC') {
      setIsLoggedIn(true);
      setError('');
    } else {
      setError('Unauthorized Access: Invalid credentials.');
    }
  };

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleRefresh = () => {
    fetchWorkers();
  };

  const handleApprove = async (worker_id) => {
    try {
      await fetch('https://admin-api.neohexane.com/admin/update-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ worker_id, verification_status: 'Approved' })
      });
      fetchWorkers();
    } catch (err) {
      console.error("Approval request failed:", err);
    }
  };

  const handleReject = async (worker_id) => {
    try {
      await fetch('https://admin-api.neohexane.com/admin/update-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ worker_id, verification_status: 'Rejected' })
      });
      fetchWorkers();
    } catch (err) {
      console.error("Rejection request failed:", err);
    }
  };

  if (!isLoggedIn) {
    return (
      <div style={styles.container}>
        <div style={styles.loginBox}>
          <h2 style={styles.darkText}>CoopGrid Admin</h2>
          <form onSubmit={handleLogin} style={styles.form}>
            <input type="text" name="username" placeholder="Username" value={credentials.username} onChange={handleChange} style={styles.input} required />
            <div style={styles.passwordWrapper}>
              <input type={showPassword ? "text" : "password"} name="password" placeholder="Password" value={credentials.password} onChange={handleChange} style={styles.passwordInput} required />
              <button type="button" onClick={() => setShowPassword(!showPassword)} style={styles.eyeButton}>
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
            {error && <p style={styles.error}>{error}</p>}
            <button type="submit" style={styles.button}>Secure Login</button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.dashboard}>
      <header style={styles.header}>
        <h2 style={styles.headerTitle}>CoopGrid Federation</h2>
        <div style={styles.headerButtons}>
          <button onClick={handleRefresh} style={styles.refreshButton}>↻ Refresh</button>
          <button onClick={() => setIsLoggedIn(false)} style={styles.logoutButton}>Sign Out</button>
        </div>
      </header>
      
      <main style={styles.mainContent}>
        
        {apiError && (
          <div style={{ backgroundColor: '#fef2f2', border: '2px solid #ef4444', padding: '15px', color: '#991b1b', fontWeight: 'bold', whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
            🚨 DATA FORMAT ERROR: <br/><br/> {apiError}
          </div>
        )}

        <div style={styles.section}>
          <h3 style={styles.darkText}>Worker Verification Registry (Pending)</h3>
          {unapprovedWorkers.length === 0 ? <p style={styles.emptyText}>no data available</p> : (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr style={styles.tableHeader}>
                    <th style={styles.th}>ID</th><th style={styles.th}>Name</th><th style={styles.th}>Phone</th><th style={styles.th}>Status</th><th style={styles.th}>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {unapprovedWorkers.map((worker) => (
                    <tr key={worker.worker_id} style={styles.tableRow}>
                      <td style={styles.td}><strong>{worker.worker_id}</strong></td>
                      <td style={styles.td}>{worker.name}</td>
                      <td style={styles.td}>{worker.phone_number}</td>
                      <td style={styles.td}>Pending</td>
                      <td style={styles.td}>
                        <div style={styles.actionButtons}>
                          <button onClick={() => handleApprove(worker.worker_id)} style={styles.approveBtn}>Approve</button>
                          <button onClick={() => handleReject(worker.worker_id)} style={styles.rejectBtn}>Reject</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
        
        {/* Verified Workers */}
        <div style={styles.section}>
          <h3 style={styles.darkText}>Verified Workers</h3>
          {verifiedWorkers.length === 0 ? <p style={styles.emptyText}>no data available</p> : (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr style={styles.tableHeader}>
                    <th style={styles.th}>ID</th><th style={styles.th}>Name</th><th style={styles.th}>Phone</th><th style={styles.th}>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {verifiedWorkers.map((worker) => (
                    <tr key={worker.worker_id} style={styles.tableRow}>
                      <td style={styles.td}><strong>{worker.worker_id}</strong></td>
                      <td style={styles.td}>{worker.name}</td>
                      <td style={styles.td}>{worker.phone_number}</td>
                      <td style={styles.td}><span style={styles.statusVerified}>Approved</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Blacklisted Workers */}
        <div style={styles.section}>
          <h3 style={styles.darkText}>Blacklisted Workers</h3>
          {blacklistedWorkers.length === 0 ? <p style={styles.emptyText}>no data available</p> : (
            <div style={styles.tableWrapper}>
              <table style={styles.table}>
                <thead>
                  <tr style={styles.tableHeader}>
                    <th style={styles.th}>ID</th><th style={styles.th}>Name</th><th style={styles.th}>Phone</th><th style={styles.th}>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {blacklistedWorkers.map((worker) => (
                    <tr key={worker.worker_id} style={styles.tableRow}>
                      <td style={styles.td}><strong>{worker.worker_id}</strong></td>
                      <td style={styles.td}>{worker.name}</td>
                      <td style={styles.td}>{worker.phone_number}</td>
                      <td style={styles.td}><span style={styles.statusRejected}>Rejected</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

const styles = {
  container: { display: 'flex', height: '100vh', alignItems: 'center', justifyContent: 'center', backgroundColor: '#f4f4f5', padding: '20px' },
  loginBox: { padding: '40px', backgroundColor: '#ffffff', border: '2px solid #000', width: '100%', maxWidth: '360px', textAlign: 'center', boxShadow: '4px 4px 0px #000' },
  form: { display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '20px' },
  input: { padding: '12px', border: '2px solid #000', fontFamily: 'monospace', color: '#000', backgroundColor: '#fff', fontSize: '14px', width: '100%', boxSizing: 'border-box' },
  passwordWrapper: { display: 'flex', border: '2px solid #000', backgroundColor: '#fff', width: '100%', boxSizing: 'border-box' },
  passwordInput: { flex: '1', padding: '12px', border: 'none', outline: 'none', fontFamily: 'monospace', color: '#000', fontSize: '14px', minWidth: '0' },
  eyeButton: { padding: '0 12px', backgroundColor: '#f4f4f5', border: 'none', borderLeft: '2px solid #000', cursor: 'pointer', fontSize: '16px', display: 'flex', alignItems: 'center', justifyContent: 'center' },
  button: { padding: '12px', backgroundColor: '#000', color: '#fff', border: 'none', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px', textTransform: 'uppercase' },
  error: { color: '#ef4444', fontSize: '14px', margin: '0', fontWeight: 'bold' },
  dashboard: { fontFamily: 'sans-serif', backgroundColor: '#f4f4f5', minHeight: '100vh' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 20px', backgroundColor: '#000', color: 'white', flexWrap: 'wrap', gap: '15px' },
  headerTitle: { margin: 0, fontSize: '1.2rem', whiteSpace: 'nowrap' },
  headerButtons: { display: 'flex', gap: '10px' },
  refreshButton: { padding: '8px 12px', backgroundColor: '#fff', color: '#000', border: '2px solid #000', cursor: 'pointer', fontWeight: 'bold', fontSize: '12px' },
  logoutButton: { padding: '8px 12px', backgroundColor: '#ef4444', color: 'white', border: '2px solid #ef4444', cursor: 'pointer', fontWeight: 'bold', fontSize: '12px' },
  mainContent: { padding: '20px', display: 'flex', flexDirection: 'column', gap: '20px' },
  section: { display: 'flex', flexDirection: 'column', gap: '15px' },
  darkText: { color: '#000', margin: '0', fontSize: '1.3rem' },
  emptyText: { color: '#a1a1aa', fontStyle: 'italic', textTransform: 'uppercase', letterSpacing: '1px', fontSize: '14px' },
  tableWrapper: { width: '100%', overflowX: 'auto', backgroundColor: '#fff', border: '2px solid #000', boxShadow: '4px 4px 0px #000' },
  table: { width: '100%', borderCollapse: 'collapse', minWidth: '500px', color: '#000' }, 
  tableHeader: { backgroundColor: '#e4e4e7', textAlign: 'left', borderBottom: '2px solid #000' },
  th: { padding: '12px 15px', color: '#000', fontWeight: 'bold', fontSize: '13px' },
  td: { padding: '12px 15px', borderBottom: '1px solid #d4d4d8', color: '#000', fontSize: '14px' },
  tableRow: { transition: 'background-color 0.2s' },
  statusVerified: { backgroundColor: '#000', color: '#fff', padding: '4px 8px', fontSize: '12px', fontWeight: 'bold', border: '1px solid #000' },
  statusRejected: { backgroundColor: '#ef4444', color: '#fff', padding: '4px 8px', fontSize: '12px', fontWeight: 'bold', border: '1px solid #991b1b' },
  actionButtons: { display: 'flex', gap: '8px' },
  approveBtn: { padding: '6px 12px', backgroundColor: '#22c55e', color: '#fff', border: '2px solid #166534', cursor: 'pointer', fontWeight: 'bold', fontSize: '12px' },
  rejectBtn: { padding: '6px 12px', backgroundColor: '#ef4444', color: '#fff', border: '2px solid #991b1b', cursor: 'pointer', fontWeight: 'bold', fontSize: '12px' }
};
