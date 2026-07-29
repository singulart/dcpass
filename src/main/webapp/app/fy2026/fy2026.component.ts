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

import { Fy2026Service } from './fy2026.service';
import { Fy2026ChartLevel, IFy2026AgencySpend, IFy2026ContractSpend, IFy2026PoSpend } from './fy2026.model';

Chart.register(ArcElement, BarController, BarElement, CategoryScale, DoughnutController, Legend, LinearScale, LogarithmicScale, Tooltip);

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

@Component({
  selector: 'jhi-fy2026',
  templateUrl: './fy2026.component.html',
  styleUrl: './fy2026.component.scss',
  imports: [FontAwesomeModule],
})
export default class Fy2026Component implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('chartCanvas') chartCanvas?: ElementRef<HTMLCanvasElement>;

  readonly faArrowLeft = faArrowLeft;
  readonly faSpinner = faSpinner;

  level = signal<Fy2026ChartLevel>('agency');
  loading = signal(true);
  error = signal<string | null>(null);

  selectedAgencyAcronym = signal<string | null>(null);
  selectedAgencyLabel = signal<string | null>(null);
  selectedContractNumber = signal<string | null>(null);
  selectedContractLabel = signal<string | null>(null);

  private readonly fy2026Service = inject(Fy2026Service);

  private chart: Chart | null = null;
  private agencyRows: IFy2026AgencySpend[] = [];
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
    this.destroyChart();
  }

  goToAgencies(): void {
    this.selectedAgencyAcronym.set(null);
    this.selectedAgencyLabel.set(null);
    this.selectedContractNumber.set(null);
    this.selectedContractLabel.set(null);
    this.level.set('agency');
    this.renderAgencyChart();
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
      return '480px';
    }
    if (level === 'po') {
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
        this.loading.set(false);
        this.level.set('agency');
        this.scheduleRender(() => this.renderAgencyChart());
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load agency spend data.');
      },
    });
  }

  private loadContracts(agencyAcronym: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.fy2026Service.getSpendByContract(agencyAcronym).subscribe({
      next: rows => {
        this.contractRows = rows.filter(r => this.spendValue(r.spend) >= 50_000);
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
        this.poRows = rows;
        this.loading.set(false);
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
    if (this.viewReady && this.chartCanvas) {
      requestAnimationFrame(() => render());
    } else {
      this.pendingRender = render;
    }
  }

  private destroyChart(): void {
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }
  }

  private spendValue(spend: number | null | undefined): number {
    return spend ?? 0;
  }

  /** Log scales cannot include 0; keep a tiny floor for plotting only. */
  private logSpendValue(spend: number | null | undefined): number {
    return Math.max(this.spendValue(spend), 1);
  }

  private renderAgencyChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) {
      this.pendingRender = () => this.renderAgencyChart();
      return;
    }
    this.destroyChart();

    const labels = this.agencyRows.map(r => r.agency ?? r.agencyAcronym ?? 'Unknown');
    const data = this.agencyRows.map(r => this.spendValue(r.spend));
    const colors = labels.map((_, i) => DOUGHNUT_PALETTE[i % DOUGHNUT_PALETTE.length]);

    const config: ChartConfiguration<'doughnut'> = {
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
          tooltip: {
            callbacks: {
              label: ctx => {
                const value = typeof ctx.parsed === 'number' ? ctx.parsed : 0;
                return ` ${this.formatCurrency(value)}`;
              },
            },
          },
        },
        onClick: (_event, elements) => {
          if (!elements.length) {
            return;
          }
          const index = elements[0].index;
          const row = this.agencyRows[index];
          if (row.agencyAcronym == null || row.agencyAcronym === '') {
            return;
          }
          this.selectedAgencyAcronym.set(row.agencyAcronym);
          this.selectedAgencyLabel.set(row.agency ?? row.agencyAcronym);
          this.loadContracts(row.agencyAcronym);
        },
      },
    };

    this.chart = new Chart(canvas, config);
  }

  private renderContractChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) {
      this.pendingRender = () => this.renderContractChart();
      return;
    }
    this.destroyChart();

    const labels = this.contractRows.map(r => {
      const title = r.contractTitle ?? 'NO CONTRACT';
      const label = title === 'NO CONTRACT' ? (r.contractNumber ?? 'NO CONTRACT') : title;
      return label.length > 48 ? `${label.slice(0, 45)}…` : label;
    });
    const data = this.contractRows.map(r => this.logSpendValue(r.spend));

    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Spend',
            data,
            backgroundColor: '#0d6e6e',
            hoverBackgroundColor: '#0a5555',
            borderRadius: 4,
          },
        ],
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              title: items => {
                const index = items[0]?.dataIndex ?? 0;
                const row = this.contractRows[index];
                const title = row.contractTitle ?? 'NO CONTRACT';
                const num = row.contractNumber ?? 'NO CONTRACT';
                return `${title} (${num})`;
              },
              label: items => {
                const index = items.dataIndex;
                return ` ${this.formatCurrency(this.spendValue(this.contractRows[index].spend))}`;
              },
            },
          },
        },
        scales: {
          x: {
            type: 'logarithmic',
            min: 1,
            ticks: {
              callback: value => this.formatCurrencyCompact(+value),
            },
          },
          y: {
            ticks: { font: { size: 11 } },
          },
        },
        onClick: (_event, elements) => {
          if (!elements.length) {
            return;
          }
          const index = elements[0].index;
          const row = this.contractRows[index];
          const agency = this.selectedAgencyAcronym();
          if (agency == null || agency === '') {
            return;
          }
          const contractNumber = row.contractNumber ?? null;
          this.selectedContractNumber.set(contractNumber);
          this.selectedContractLabel.set(
            row.contractTitle && row.contractTitle !== 'NO CONTRACT' ? row.contractTitle : (row.contractNumber ?? 'NO CONTRACT'),
          );
          this.loadPurchaseOrders(agency, contractNumber);
        },
      },
    };

    this.chart = new Chart(canvas, config);
  }

  private renderPoChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) {
      this.pendingRender = () => this.renderPoChart();
      return;
    }
    this.destroyChart();

    const labels = this.poRows.map(r => {
      const label = r.poNumber ?? r.poTitle ?? 'Unknown PO';
      return label.length > 40 ? `${label.slice(0, 37)}…` : label;
    });
    const data = this.poRows.map(r => this.spendValue(r.spend));

    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Spend',
            data,
            backgroundColor: '#ca6702',
            hoverBackgroundColor: '#9b4e02',
            borderRadius: 2,
            borderSkipped: false,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              title: items => {
                const index = items[0]?.dataIndex ?? 0;
                const row = this.poRows[index];
                const num = row.poNumber ?? '';
                const title = row.poTitle ?? '';
                return title ? `${num} — ${title}` : num || 'Purchase order';
              },
              label: ctx => ` ${this.formatCurrency(ctx.parsed.y)}`,
            },
          },
        },
        scales: {
          x: {
            ticks: {
              font: { size: 10 },
              maxRotation: 45,
              minRotation: 45,
            },
            grid: { display: false },
          },
          y: {
            ticks: {
              callback: value => this.formatCurrencyCompact(+value),
            },
            grid: { color: 'rgba(202, 103, 2, 0.12)' },
          },
        },
      },
    };

    this.chart = new Chart(canvas, config);
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
