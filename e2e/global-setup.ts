import net from 'node:net';
import type { FullConfig } from '@playwright/test';

const frontendURL = process.env.E2E_BASE_URL ?? 'http://localhost:4200';
const backendURL = process.env.E2E_BACKEND_URL ?? 'http://localhost:8080';
const coverageEnabled = process.env.E2E_COVERAGE === '1' || process.env.E2E_COVERAGE === 'true';
const jacocoPort = Number(process.env.JACOCO_PORT ?? 6300);

async function waitFor(url: string, label: string, attempts = 60): Promise<void> {
  let lastError: unknown;
  for (let i = 0; i < attempts; i++) {
    try {
      const response = await fetch(url, { signal: AbortSignal.timeout(3000) });
      if (response.ok || response.status < 500) {
        return;
      }
      lastError = new Error(`${label} returned HTTP ${response.status}`);
    } catch (error) {
      lastError = error;
    }
    await new Promise(resolve => setTimeout(resolve, 1000));
  }
  throw new Error(
    `${label} is not reachable at ${url}. Start the live app first:\n` +
      `  ./npmw run backend:start:coverage   # (or backend:start without Java coverage)\n` +
      `  ./npmw run start\n` +
      `Last error: ${String(lastError)}`,
  );
}

function isPortOpen(port: number, host = '127.0.0.1'): Promise<boolean> {
  return new Promise(resolve => {
    const socket = net.connect({ host, port }, () => {
      socket.end();
      resolve(true);
    });
    socket.on('error', () => resolve(false));
  });
}

export default async function globalSetup(_config: FullConfig): Promise<void> {
  await waitFor(`${backendURL}/management/health`, 'Backend');
  await waitFor(frontendURL, 'Frontend');

  if (coverageEnabled) {
    const jacocoUp = await isPortOpen(jacocoPort);
    if (!jacocoUp) {
      throw new Error(
        `E2E coverage requires the backend JaCoCo agent on port ${jacocoPort}.\n` +
          `Stop the plain backend and start:\n` +
          `  ./npmw run backend:start:coverage\n` +
          `Then re-run: ./npmw run e2e:coverage`,
      );
    }
  }
}
