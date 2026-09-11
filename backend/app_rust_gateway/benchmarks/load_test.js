import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 500 },   // 30 sec me 500 Virtual Users tak ramp-up
    { duration: '1m', target: 2000 },  // 1 min tak 2,000 concurrent users sustained load
    { duration: '30s', target: 10000 }, // 30 sec me 10,000 peak users SPIKE test
    { duration: '30s', target: 0 },     // Ramp-down to 0
  ],
  thresholds: {
    http_req_duration: ['p(95)<100'], // 95% requests 100ms se kam hone chahiye
    http_req_failed: ['rate<0.01'],    // 1% se kam errors allowed
  },
};

const BASE_URL = 'http://127.0.0.1:8001';

// Dummy Valid JWT Token for load test
const VALID_JWT = 'YOUR_GENERATED_BENCHMARK_JWT_TOKEN_HERE';

export default function () {
  // Scenario 1: Health check endpoint
  let res1 = http.get(`${BASE_URL}/health`);
  check(res1, { 'status is 200': (r) => r.status === 200 });

  // Scenario 2: Protected API Endpoint (JWT Verification Under Load)
  let params = {
    headers: {
      'Authorization': `Bearer ${VALID_JWT}`,
      'Content-Type': 'application/json',
    },
  };
  let res2 = http.get(`${BASE_URL}/auth/protected-demo`, params);
  check(res2, { 'protected status is 200': (r) => r.status === 200 });

  sleep(0.1); // 100ms delay between user requests
}