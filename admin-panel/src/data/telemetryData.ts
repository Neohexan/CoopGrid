export interface TelemetryModule {
  left: [string, string][];
  rightText: string[];
  rightBox: [string[], string[]];
}

// Replaced generic skills with CoopGrid's target grassroots workforce & assets
export const LEFT_SKILLS: string[] = [
  "Electricians",
  "Plumbers",
  "Caregivers",
  "Carpenters",
  "Gardeners",
  "Technicians",
  "Tractors",
  "Harvesters",
];

// Replaced generic years with System Status / Telemetry flags
export const RIGHT_YEARS: string[] = [
  "Auth: SECURE",
  "Role: ADMIN",
  "Sync: ACTIVE",
  "Proxy: TUNNEL",
  "Edge: RUST",
  "Node: ONLINE",
  "DB: ISOLATED",
  "OPEX: ZERO",
];

export const TELEMETRY_MODULES: TelemetryModule[] = [
  {
    left: [
      ["MODULE 01", "COOPGRID ADMIN CORE"],
      ["GOVERNANCE / SECURITY", "FEDERATION WORKSPACE"],
    ],
    rightText: [
      "Centralized Admin Dashboard for Cooperative Governance,",
      "(left to right) overseeing worker verification,",
      "resource leasing, and community logistics",
      "as a self-sustained ecosystem.",
    ],
    rightBox: [
      ["SYSTEM / CORE 01", "ID: COOP-ADMIN-A01"],
      [
        "Strictly utilitarian dashboard.",
        "Zero visual clutter.",
        "Role-Based Access Control (RBAC) active.",
      ],
    ],
  },
  {
    left: [
      ["MODULE 02", "VERIFICATION PIPELINE"],
      ["WORKFORCE STATUS: PENDING", "MODE: PROFILING"],
    ],
    rightText: [
      "Grassroots workers securely onboarded.",
      "Profiles routed to Pending Registry.",
      "Identity and skill sets cross-referenced.",
      "Awaiting single-click federation approval.",
    ],
    rightBox: [
      ["WORKFORCE / MODULE 02", "ID: VERIFY-Q-B07"],
      [
        "Frictionless administration.",
        "No onboarding bottlenecks.",
        "Zero-commission empowerment.",
      ],
    ],
  },
  {
    left: [
      ["MODULE 03", "AGRI-RESOURCE HUB"],
      ["P2P LEASING / ACTIVE", "HYPERLOCAL MARKETPLACE"],
    ],
    rightText: [
      "Peer-to-peer machinery leasing synchronized.",
      "Farmers monetizing idle assets (tractors, harvesters).",
      "Surplus materials circulating locally.",
      "Farming micro-economy stabilized.",
    ],
    rightBox: [
      ["RESOURCE / MODULE 03", "ID: AGRI-HUB-C12"],
      [
        "Custom hourly rates (₹/hr) logged.",
        "Direct local sales secured.",
        "Community capital retained.",
      ],
    ],
  },
  {
    left: [
      ["MODULE 04", "PROXIMITY MATCHING"],
      ["GEO-SPATIAL ROUTING", "EMERGENCY PROTOCOL STANDBY"],
    ],
    rightText: [
      "Hyper-fast, proximity-based matching algorithms.",
      "Aligning household gig requests with",
      "the absolute closest available workers",
      "to minimize response times.",
    ],
    rightBox: [
      ["MATCHING / MODULE 04", "ID: GEO-ROUTE-D09"],
      [
        "Worker autonomy fully retained.",
        "Self-determined service rates.",
        "Location-based pinging active.",
      ],
    ],
  },
  {
    left: [
      ["MODULE 05", "CLOUD INFRASTRUCTURE"],
      ["RUST API GATEWAY", "SELF-HOSTED VPS / NEAR-ZERO OPEX"],
    ],
    rightText: [
      "Isolated Python FastAPI microservices.",
      "Tokio-async Rust edge gateway filtering DDoS.",
      "Traffic routed securely via Cloudflare Tunnels.",
      "100% data sovereignty guaranteed.",
    ],
    rightBox: [
      ["INFRA / MODULE 05", "ID: SERVER-NODE-E03"],
      [
        "Nanosecond token validation.",
        "No third-party cloud database.",
        "Offline-first Sync: ACTIVE.",
      ],
    ],
  },
];
