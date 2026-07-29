import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject, signal } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faSpinner } from '@fortawesome/free-solid-svg-icons';
import {
  ArcElement,
  BarController,
  BarElement,
  CategoryScale,
  Chart,
  ChartConfiguration,
  DoughnutController,
  Legend,
  LinearScale,
  LogarithmicScale,
  Tooltip,
} from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';

import { Fy2026Service } from './fy2026.service';
import { Fy2026ChartLevel, IFy2026AgencySpend, IFy2026ContractSpend, IFy2026PoSpend } from './fy2026.model';

Chart.register(
  ArcElement,
  BarController,
  BarElement,
  CategoryScale,
  DoughnutController,
  Legend,
  LinearScale,
  LogarithmicScale,
  Tooltip,
  ChartDataLabels,
);

const DOUGHNUT_PALETTE = [
  '#0d6e6e',
  '#1a8a8a',
  '#2a9d8f',
  '#3d5a80',
  '#ee9b00',
  '#ca6702',
  '#bb3e03',
  '#ae2012',
  '#9b2226',
  '#005f73',
  '#0a9396',
  '#94d2bd',
  '#e9d8a6',
  '#6d597a',
  '#b56576',
];

const TOP_AGENCY_COUNT = 20;
const OTHERS_LABEL = 'Others';
const OTHERS_COLOR = '#8a9199';

type Fy2026ChartCanvas = HTMLCanvasElement & { __fy2026Chart?: Chart };

type TopAgencySlice = { kind: 'agency'; row: IFy2026AgencySpend } | { kind: 'others'; spend: number };

function bindChartToCanvas(canvas: HTMLCanvasElement, chart: Chart): void {
  (canvas as Fy2026ChartCanvas).__fy2026Chart = chart;
}

function unbindChartFromCanvas(canvas: HTMLCanvasElement | undefined | null): void {
  if (canvas) {
    delete (canvas as Fy2026ChartCanvas).__fy2026Chart;
  }
}

@Component({
  selector: 'jhi-fy2026',
  templateUrl: './fy2026.component.html',
  styleUrl: './fy2026.component.scss',
  imports: [FontAwesomeModule],
})
export default class Fy2026Component implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('topAgencyCanvas') topAgencyCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('othersAgencyCanvas') othersAgencyCanvas?: ElementRef<HTMLCanvasElement>;

  readonly faArrowLeft = faArrowLeft;
  readonly faSpinner = faSpinner;

  level = signal<Fy2026ChartLevel>('agency');
  loading = signal(true);
  error = signal<string | null>(null);
  hasOthersAgencies = signal(false);

  selectedAgencyAcronym = signal<string | null>(null);
  selectedAgencyLabel = signal<string | null>(null);
  selectedContractNumber = signal<string | null>(null);
  selectedContractLabel = signal<string | null>(null);

  private readonly fy2026Service = inject(Fy2026Service);

  private drillChart: Chart | null = null;
  private topAgencyChart: Chart | null = null;
  private othersAgencyChart: Chart | null = null;
  private agencyRows: IFy2026AgencySpend[] = [];
  private topSlices: TopAgencySlice[] = [];
  private othersAgencyRows: IFy2026AgencySpend[] = [];
  private contractRows: IFy2026ContractSpend[] = [];
  private poRows: IFy2026PoSpend[] = [];
  private viewReady = false;
  private pendingRender: (() => void) | null = null;

  ngOnInit(): void {
    this.loadAgencies();
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.pendingRender) {
      const render = this.pendingRender;
      this.pendingRender = null;
      render();
    }
  }

  ngOnDestroy(): void {
    this.destroyAllCharts();
  }

  goToAgencies(): void {
    this.selectedAgencyAcronym.set(null);
    this.selectedAgencyLabel.set(null);
    this.selectedContractNumber.set(null);
    this.selectedContractLabel.set(null);
    this.level.set('agency');
    this.scheduleRender(() => this.renderAgencyCharts());
  }

  goToContracts(): void {
    const acronym = this.selectedAgencyAcronym();
    if (!acronym) {
      this.goToAgencies();
      return;
    }
    this.selectedContractNumber.set(null);
    this.selectedContractLabel.set(null);
    this.level.set('contract');
    this.loadContracts(acronym);
  }

  chartHeight(): string {
    const level = this.level();
    if (level === 'agency') {
      return '420px';
    }
    if (level === 'po') {
      if (this.poRows.length > 10) {
        return `${Math.max(320, this.poRows.length * 28 + 80)}px`;
      }
      return '420px';
    }
    const count = this.contractRows.length;
    return `${Math.max(320, count * 28 + 80)}px`;
  }

  heading(): string {
    switch (this.level()) {
      case 'contract':
        return `IT spend by contract — ${this.selectedAgencyLabel() ?? ''}`;
      case 'po':
        return `IT spend by purchase order — ${this.selectedContractLabel() ?? ''}`;
      default:
        return 'FY2026 IT spend by agency';
    }
  }

  private loadAgencies(): void {
    this.loading.set(true);
    this.error.set(null);
    this.fy2026Service.getSpendByAgency().subscribe({
      next: rows => {
        this.agencyRows = rows;
        this.splitAgencyRows();
        this.loading.set(false);
        this.level.set('agency');
        this.scheduleRender(() => this.renderAgencyCharts());
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load agency spend data.');
      },
    });
  }

  private splitAgencyRows(): void {
    const sorted = [...this.agencyRows].sort((a, b) => this.spendValue(b.spend) - this.spendValue(a.spend));
    const top = sorted.slice(0, TOP_AGENCY_COUNT);
    this.othersAgencyRows = sorted.slice(TOP_AGENCY_COUNT);
    this.hasOthersAgencies.set(this.othersAgencyRows.length > 0);

    this.topSlices = top.map(row => ({ kind: 'agency' as const, row }));
    if (this.othersAgencyRows.length > 0) {
      const othersSpend = this.othersAgencyRows.reduce((sum, row) => sum + this.spendValue(row.spend), 0);
      this.topSlices.push({ kind: 'others', spend: othersSpend });
    }
  }

  private loadContracts(agencyAcronym: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.destroyAgencyCharts();
    this.fy2026Service.getSpendByContract(agencyAcronym).subscribe({
      next: rows => {
        this.contractRows = this.buildContractRowsWithOthers(rows);
        this.loading.set(false);
        this.level.set('contract');
        this.scheduleRender(() => this.renderContractChart());
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load contract spend data.');
      },
    });
  }

  private loadPurchaseOrders(agencyAcronym: string, contractNumber: string | null): void {
    this.loading.set(true);
    this.error.set(null);
    this.fy2026Service.getSpendByPo(agencyAcronym, contractNumber).subscribe({
      next: rows => {
        this.loading.set(false);
        if (rows.length === 1) {
          const id = rows[0].purchaseOrderId;
          if (id != null) {
            window.open(`/purchase-order/${id}/view`, '_blank', 'noopener,noreferrer');
          }
          // Stay on the contract chart — no third chart for a single PO.
          this.selectedContractNumber.set(null);
          this.selectedContractLabel.set(null);
          return;
        }
        if (rows.length < 2) {
          this.error.set('No purchase orders found for this contract.');
          return;
        }
        this.poRows = this.buildPoRowsWithOthers(rows);
        this.level.set('po');
        this.scheduleRender(() => this.renderPoChart());
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load purchase order spend data.');
      },
    });
  }

  private scheduleRender(render: () => void): void {
    if (this.viewReady) {
      // Double rAF so Angular can create canvases after @if level switches.
      requestAnimationFrame(() => {
        requestAnimationFrame(() => render());
      });
    } else {
      this.pendingRender = render;
    }
  }

  private destroyAllCharts(): void {
    this.destroyAgencyCharts();
    this.destroyDrillChart();
  }

  private destroyAgencyCharts(): void {
    unbindChartFromCanvas(this.topAgencyCanvas?.nativeElement);
    unbindChartFromCanvas(this.othersAgencyCanvas?.nativeElement);
    if (this.topAgencyChart) {
      this.topAgencyChart.destroy();
      this.topAgencyChart = null;
    }
    if (this.othersAgencyChart) {
      this.othersAgencyChart.destroy();
      this.othersAgencyChart = null;
    }
  }

  private destroyDrillChart(): void {
    unbindChartFromCanvas(this.chartCanvas?.nativeElement);
    if (this.drillChart) {
      this.drillChart.destroy();
      this.drillChart = null;
    }
  }

  private buildContractRowsWithOthers(rows: IFy2026ContractSpend[]): IFy2026ContractSpend[] {
    const major: IFy2026ContractSpend[] = [];
    let othersSpend = 0;
    let othersCount = 0;
    for (const row of rows) {
      if (this.spendValue(row.spend) >= 50_000) {
        major.push(row);
      } else {
        othersSpend += this.spendValue(row.spend);
        othersCount += 1;
      }
    }
    if (othersCount > 0) {
      major.push({
        contractTitle: OTHERS_LABEL,
        contractNumber: null,
        spend: othersSpend,
        isOthers: true,
      });
    }
    return major;
  }

  private buildPoRowsWithOthers(rows: IFy2026PoSpend[]): IFy2026PoSpend[] {
    const major: IFy2026PoSpend[] = [];
    let othersSpend = 0;
    let othersCount = 0;
    for (const row of rows) {
      if (this.spendValue(row.spend) >= 50_000) {
        major.push(row);
      } else {
        othersSpend += this.spendValue(row.spend);
        othersCount += 1;
      }
    }
    if (othersCount > 0) {
      major.push({
        purchaseOrderId: null,
        poNumber: OTHERS_LABEL,
        poTitle: OTHERS_LABEL,
        spend: othersSpend,
        isOthers: true,
      });
    }
    return major;
  }

  private poBarLabel(row: IFy2026PoSpend): string {
    if (row.isOthers) {
      return OTHERS_LABEL;
    }
    const label = row.poNumber ?? row.poTitle ?? 'Unknown PO';
    return label.length > 40 ? `${label.slice(0, 37)}…` : label;
  }

  private isNoContractTitle(title: string | null | undefined): boolean {
    const trimmed = title?.trim();
    return trimmed == null || trimmed === '' || trimmed.toUpperCase() === 'NO CONTRACT';
  }

  /** Prefer real title; never show the literal "NO CONTRACT" — use contract number instead. */
  private contractDisplayName(row: IFy2026ContractSpend): string {
    if (row.isOthers) {
      return OTHERS_LABEL;
    }
    if (!this.isNoContractTitle(row.contractTitle)) {
      return row.contractTitle!.trim();
    }
    const number = row.contractNumber?.trim();
    return number && number !== '' ? number : 'Untitled contract';
  }

  private contractBarLabel(row: IFy2026ContractSpend): string {
    const label = this.contractDisplayName(row);
    return label.length > 48 ? `${label.slice(0, 45)}…` : label;
  }

  /** Log scales cannot include 0; keep a tiny floor for plotting only. */
  private spendValue(spend: number | null | undefined): number {
    return spend ?? 0;
  }

  private logSpendValue(spend: number | null | undefined): number {
    return Math.max(this.spendValue(spend), 1);
  }

  /** Use log when max/min spans more than one order of magnitude (ratio > 10). */
  private needsLogScale(values: number[]): boolean {
    const positive = values.filter(v => v > 0);
    if (positive.length < 2) {
      return false;
    }
    const max = Math.max(...positive);
    const min = Math.min(...positive);
    return max / min > 10;
  }

  private valueAxisScale(
    useLog: boolean,
    showTicks: boolean,
  ): {
    type?: 'logarithmic';
    min?: number;
    grid: { display: boolean };
    ticks: { display: boolean; callback?: (value: string | number) => string };
  } {
    if (useLog) {
      return {
        type: 'logarithmic',
        min: 1,
        grid: { display: false },
        ticks: showTicks
          ? {
              display: true,
              callback: value => this.formatCurrencyCompact(+value),
            }
          : { display: false },
      };
    }
    return {
      grid: { display: false },
      ticks: showTicks
        ? {
            display: true,
            callback: value => this.formatCurrencyCompact(+value),
          }
        : { display: false },
    };
  }

  private selectAgency(row: IFy2026AgencySpend): void {
    if (row.agencyAcronym == null || row.agencyAcronym === '') {
      return;
    }
    this.selectedAgencyAcronym.set(row.agencyAcronym);
    this.selectedAgencyLabel.set(row.agency ?? row.agencyAcronym);
    this.loadContracts(row.agencyAcronym);
  }

  private scrollToOthersChart(): void {
    this.othersAgencyCanvas?.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  private renderAgencyCharts(): void {
    this.destroyDrillChart();
    this.destroyAgencyCharts();

    const topCanvas = this.topAgencyCanvas?.nativeElement;
    if (!topCanvas) {
      this.pendingRender = () => this.renderAgencyCharts();
      return;
    }

    const topLabels = this.topSlices.map(slice =>
      slice.kind === 'others' ? OTHERS_LABEL : (slice.row.agency ?? slice.row.agencyAcronym ?? 'Unknown'),
    );
    const topData = this.topSlices.map(slice => (slice.kind === 'others' ? slice.spend : this.spendValue(slice.row.spend)));
    const topColors = this.topSlices.map((slice, i) =>
      slice.kind === 'others' ? OTHERS_COLOR : DOUGHNUT_PALETTE[i % DOUGHNUT_PALETTE.length],
    );

    this.topAgencyChart = new Chart(
      topCanvas,
      this.buildDoughnutConfig(topLabels, topData, topColors, index => {
        const slice = this.topSlices[index];
        if (slice.kind === 'others') {
          this.scrollToOthersChart();
          return;
        }
        this.selectAgency(slice.row);
      }),
    );
    bindChartToCanvas(topCanvas, this.topAgencyChart);

    if (!this.hasOthersAgencies()) {
      return;
    }

    const othersCanvas = this.othersAgencyCanvas?.nativeElement;
    if (!othersCanvas) {
      this.pendingRender = () => this.renderAgencyCharts();
      return;
    }

    const othersLabels = this.othersAgencyRows.map(r => r.agency ?? r.agencyAcronym ?? 'Unknown');
    const othersData = this.othersAgencyRows.map(r => this.spendValue(r.spend));
    const othersColors = othersLabels.map((_, i) => DOUGHNUT_PALETTE[(i + 3) % DOUGHNUT_PALETTE.length]);

    this.othersAgencyChart = new Chart(
      othersCanvas,
      this.buildDoughnutConfig(othersLabels, othersData, othersColors, index => {
        this.selectAgency(this.othersAgencyRows[index]);
      }),
    );
    bindChartToCanvas(othersCanvas, this.othersAgencyChart);
  }

  private buildDoughnutConfig(
    labels: string[],
    data: number[],
    colors: string[],
    onSliceClick: (index: number) => void,
  ): ChartConfiguration<'doughnut'> {
    return {
      type: 'doughnut',
      data: {
        labels,
        datasets: [
          {
            data,
            backgroundColor: colors,
            borderColor: '#fff',
            borderWidth: 2,
            hoverOffset: 8,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: { boxWidth: 14, font: { size: 11 } },
          },
          datalabels: {
            display: false,
          },
          tooltip: {
            callbacks: {
              label: ctx => {
                const value = typeof ctx.parsed === 'number' ? ctx.parsed : 0;
                return ` ${this.formatCurrency(value)}`;
              },
            },
          },
        },
        onClick(_event, elements) {
          if (!elements.length) {
            return;
          }
          onSliceClick(elements[0].index);
        },
      },
    };
  }

  private renderContractChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) {
      this.pendingRender = () => this.renderContractChart();
      return;
    }
    this.destroyAllCharts();

    const labels = this.contractRows.map(r => this.contractBarLabel(r));
    const rawValues = this.contractRows.map(r => this.spendValue(r.spend));
    const useLog = this.needsLogScale(rawValues);
    const data = useLog ? this.contractRows.map(r => this.logSpendValue(r.spend)) : rawValues;
    const backgroundColor = this.contractRows.map(r => (r.isOthers ? OTHERS_COLOR : '#0d6e6e'));
    const hoverBackgroundColor = this.contractRows.map(r => (r.isOthers ? OTHERS_COLOR : '#0a5555'));

    const onContractBarClick = (index: number): void => {
      const row = this.contractRows[index];
      if (row.isOthers) {
        return;
      }
      const agency = this.selectedAgencyAcronym();
      if (agency == null || agency === '') {
        return;
      }
      const contractNumber = row.contractNumber ?? null;
      this.selectedContractNumber.set(contractNumber);
      this.selectedContractLabel.set(this.contractDisplayName(row));
      this.loadPurchaseOrders(agency, contractNumber);
    };

    const onContractBarHover = (event: { native?: Event | null }, elements: { index: number }[]): void => {
      const target = event.native?.target;
      if (!(target instanceof HTMLElement)) {
        return;
      }
      if (!elements.length) {
        target.style.cursor = 'default';
        return;
      }
      const row = this.contractRows[elements[0].index];
      target.style.cursor = row.isOthers ? 'default' : 'pointer';
    };

    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Spend',
            data,
            backgroundColor,
            hoverBackgroundColor,
            borderRadius: 4,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        layout: {
          padding: { right: 80 },
        },
        plugins: {
          legend: { display: false },
          datalabels: {
            anchor: 'end',
            align: 'right',
            offset: 6,
            clamp: true,
            clip: false,
            color: '#1a2b2b',
            font: { size: 11, weight: 600 },
            formatter: (_value, context) => this.formatCurrency(this.spendValue(this.contractRows[context.dataIndex].spend)),
          },
          tooltip: {
            callbacks: {
              title: items => {
                const index = items[0]?.dataIndex ?? 0;
                const row = this.contractRows[index];
                return this.contractDisplayName(row);
              },
              label: items => {
                const index = items.dataIndex;
                return ` ${this.formatCurrency(this.spendValue(this.contractRows[index].spend))}`;
              },
            },
          },
        },
        scales: {
          x: this.valueAxisScale(useLog, false),
          y: {
            grid: { display: false },
            ticks: { font: { size: 11 } },
          },
        },
        onClick(_event, elements) {
          if (!elements.length) {
            return;
          }
          onContractBarClick(elements[0].index);
        },
        onHover(event, elements) {
          onContractBarHover(event, elements);
        },
      },
    };

    this.drillChart = new Chart(canvas, config);
    bindChartToCanvas(canvas, this.drillChart);
  }

  private renderPoChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) {
      this.pendingRender = () => this.renderPoChart();
      return;
    }
    this.destroyAllCharts();

    const horizontal = this.poRows.length > 10;
    const labels = this.poRows.map(r => this.poBarLabel(r));
    const rawValues = this.poRows.map(r => this.spendValue(r.spend));
    const useLog = this.needsLogScale(rawValues);
    const data = useLog ? this.poRows.map(r => this.logSpendValue(r.spend)) : rawValues;
    const backgroundColor = this.poRows.map(r => (r.isOthers ? OTHERS_COLOR : '#ca6702'));
    const hoverBackgroundColor = this.poRows.map(r => (r.isOthers ? OTHERS_COLOR : '#9b4e02'));

    const openPurchaseOrder = (index: number): void => {
      const row = this.poRows[index];
      if (row.isOthers || row.purchaseOrderId == null) {
        return;
      }
      window.open(`/purchase-order/${row.purchaseOrderId}/view`, '_blank', 'noopener,noreferrer');
    };

    const onPoBarHover = (event: { native?: Event | null }, elements: { index: number }[]): void => {
      const target = event.native?.target;
      if (!(target instanceof HTMLElement)) {
        return;
      }
      if (!elements.length) {
        target.style.cursor = 'default';
        return;
      }
      const row = this.poRows[elements[0].index];
      target.style.cursor = row.isOthers ? 'default' : 'pointer';
    };

    const categoryScale = {
      grid: { display: false },
      ticks: {
        font: { size: 10 },
        ...(horizontal
          ? {}
          : {
              maxRotation: 45,
              minRotation: 45,
            }),
      },
    };
    const valueScale = this.valueAxisScale(useLog, false);

    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Spend',
            data,
            backgroundColor,
            hoverBackgroundColor,
            borderRadius: 2,
            borderSkipped: false,
          },
        ],
      },
      options: {
        indexAxis: horizontal ? 'y' : 'x',
        responsive: true,
        maintainAspectRatio: false,
        layout: {
          padding: horizontal ? { right: 80 } : { top: 28 },
        },
        plugins: {
          legend: { display: false },
          datalabels: {
            anchor: 'end',
            align: horizontal ? 'right' : 'top',
            offset: horizontal ? 6 : 2,
            clamp: true,
            clip: false,
            color: '#1a2b2b',
            font: { size: 10, weight: 600 },
            formatter: (_value, context) => this.formatCurrency(this.spendValue(this.poRows[context.dataIndex].spend)),
          },
          tooltip: {
            callbacks: {
              title: items => {
                const index = items[0]?.dataIndex ?? 0;
                const row = this.poRows[index];
                if (row.isOthers) {
                  return OTHERS_LABEL;
                }
                const num = row.poNumber ?? '';
                const title = row.poTitle ?? '';
                return title ? `${num} — ${title}` : num || 'Purchase order';
              },
              label: items => {
                const index = items.dataIndex;
                return ` ${this.formatCurrency(this.spendValue(this.poRows[index].spend))}`;
              },
            },
          },
        },
        scales: horizontal
          ? {
              x: valueScale,
              y: categoryScale,
            }
          : {
              x: categoryScale,
              y: valueScale,
            },
        onClick(_event, elements) {
          if (!elements.length) {
            return;
          }
          openPurchaseOrder(elements[0].index);
        },
        onHover(event, elements) {
          onPoBarHover(event, elements);
        },
      },
    };

    this.drillChart = new Chart(canvas, config);
    bindChartToCanvas(canvas, this.drillChart);
  }

  private formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(value);
  }

  private formatCurrencyCompact(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      notation: 'compact',
      maximumFractionDigits: 1,
    }).format(value);
  }
}
