import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

const baseUrl = __ENV.API_BASE_URL || 'http://localhost:8080';

export default function () {
  const workouts = http.get(`${baseUrl}/v1/workouts`);
  check(workouts, { 'workouts returns 200': (response) => response.status === 200 });

  const analytics = http.get(`${baseUrl}/v1/analytics/progress/workouts-breakdown`);
  check(analytics, { 'analytics returns 200': (response) => response.status === 200 });
  sleep(1);
}
