import { defineConfig } from '@playwright/test';
export default defineConfig({
  testDir: './tests', workers: 1, timeout: 60000,
  use: {
    screenshot: 'only-on-failure',
    baseURL: process.env.E2E_URL || 'http://127.0.0.1:5173',
    viewport: { width: 1440, height: 1040 },
    launchOptions: process.env.CHROMIUM_PATH ? { executablePath: process.env.CHROMIUM_PATH, args: ['--no-sandbox', '--no-zygote', '--single-process'] } : {},
  },
  reporter: 'list',
});
