import { IFy2026AgencySpend } from './fy2026.model';

export const FY2026_OTHERS_LABEL = 'Others';
export const FY2026_OTHERS_COLOR = '#8a9199';
export const FY2026_TOP_AGENCY_COUNT = 20;

export interface Fy2026LegendItem {
  label: string;
  color: string;
  value: number;
  valueFormatted: string;
  kind: 'agency' | 'others';
  row?: IFy2026AgencySpend;
}

export interface Fy2026BarItem {
  label: string;
  valueFormatted: string;
  pct: number;
  color: string;
  clickable: boolean;
}

export function fy2026Amount(value: number | null | undefined): number {
  return value ?? 0;
}

export function fy2026FormatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(value);
}

export function fy2026NeedsLogScale(values: number[]): boolean {
  const positive = values.filter(v => v > 0);
  if (positive.length < 2) {
    return false;
  }
  const max = Math.max(...positive);
  const min = Math.min(...positive);
  return max / min > 10;
}

export function fy2026BarWidthPct(value: number, max: number, useLog: boolean): number {
  if (max <= 0 || value <= 0) {
    return 0;
  }
  if (!useLog) {
    return Math.max(2, (value / max) * 100);
  }
  const logMax = Math.log10(Math.max(max, 1));
  const logVal = Math.log10(Math.max(value, 1));
  if (logMax <= 0) {
    return 100;
  }
  return Math.max(2, (logVal / logMax) * 100);
}

/** True when the gesture looks like a click (not a text-selection drag). */
export function fy2026IsPlainClick(down: { x: number; y: number } | null, event: MouseEvent, movementThresholdPx = 5): boolean {
  if (down == null) {
    return true;
  }
  const moved = Math.hypot(event.clientX - down.x, event.clientY - down.y);
  if (moved > movementThresholdPx) {
    return false;
  }
  const selection = window.getSelection()?.toString() ?? '';
  return selection.length === 0;
}

export function fy2026BuildBarItems(rows: Array<{ label: string; value: number; color: string; clickable: boolean }>): Fy2026BarItem[] {
  const values = rows.map(r => r.value);
  const max = Math.max(0, ...values);
  const useLog = fy2026NeedsLogScale(values);
  return rows.map(row => ({
    label: row.label,
    valueFormatted: fy2026FormatCurrency(row.value),
    pct: fy2026BarWidthPct(row.value, max, useLog),
    color: row.color,
    clickable: row.clickable,
  }));
}
