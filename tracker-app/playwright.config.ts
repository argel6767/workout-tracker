import { defineConfig, devices } from '@playwright/test';

const javaHome = process.env.JAVA_HOME?.replace(/^"(.*)"$/, '$1');

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  timeout: 60_000,
  retries: process.env.CI ? 2 : 0,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  webServer: [
    {
      command: 'mvn -f ../workout-tracker/pom.xml spring-boot:test-run -Dspring-boot.run.profiles=test',
      url: 'http://localhost:8080/v1/muscles',
      reuseExistingServer: false,
      timeout: 120_000,
      env: javaHome ? { JAVA_HOME: javaHome } : undefined,
    },
    {
      command: 'pnpm dev --host localhost',
      url: 'http://localhost:5173',
      reuseExistingServer: false,
      timeout: 120_000,
      env: { VITE_API_BASE_URL: 'http://localhost:8080' },
    },
  ],
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
