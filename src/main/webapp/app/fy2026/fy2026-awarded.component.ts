import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject, signal } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { ArcElement, Chart, ChartConfiguration, DoughnutController, Tooltip } from 'chart.js';

import { PassContractService } from 'app/entities/pass-contract/service/pass-contract.service';

import { Fy2026Service } from './fy2026.service';
import { Fy2026AwardedChartLevel, IFy2026AgencySpend, IFy2026ContractSpend } from './fy2026.model';
import {
  FY2026_OTHERS_COLOR,
  FY2026_OTHERS_LABEL,
  FY2026_TOP_AGENCY_COUNT,
  Fy2026BarItem,
  Fy2026LegendItem,
  fy2026Amount,
  fy2026BuildBarItems,
  fy2026FormatCurrency,
  fy2026IsPlainClick,
} from './fy2026-chart.util';

Chart.register(ArcElement, DoughnutController, Tooltip);

const DOUGHNUT_PALETTE = [
  '#3d5a80',
  '#4a6fa5',
  '#ee9b00',
  '#ca6702',
  '#bb3e03',
  '#6d597a',
  '#b56576',
  '#005f73',
  '#0a9396',
  '#94d2bd',
  '#e9d8a6',
  '#9b2226',
  '#ae2012',
  '#1a8a8a',
  '#2a9d8f',
];

type Fy2026AwardedChartCanvas = HTMLCanvasElement & { __fy2026Chart?: Chart };

type TopAgencySlice = { kind: 'agency'; row: IFy2026AgencySpend } | { kind: 'others'; spend: number };

function bindChartToCanvas(canvas: HTMLCanvasElement, chart: Chart): void {
  (canvas as Fy2026AwardedChartCanvas).__fy2026Chart = chart;
}

function unbindChartFromCanvas(canvas: HTMLCanvasElement | undefined | null): void {
  if (canvas) {
    delete (canvas as Fy2026AwardedChartCanvas).__fy2026Chart;
  }
}

@Component({
  selector: 'jhi-fy2026-awarded',
  templateUrl: './fy2026-awarded.component.html',
  styleUrl: './fy2026.component.scss',
  imports: [FontAwesomeModule],
})
export class Fy2026AwardedComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('topAgencyCanvas') topAgencyCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('othersAgencyCanvas') othersAgencyCanvas?: ElementRef<HTMLCanvasElement>;

  readonly faArrowLeft = faArrowLeft;
  readonly faSpinner = faSpinner;

  level = signal<Fy2026AwardedChartLevel>('agency');
  loading = signal(true);
  error = signal<string | null>(null);
  hasOthersAgencies = signal(false);

  selectedAgencyAcronym = signal<string | null>(null);
  selectedAgencyLabel = signal<string | null>(null);

  topLegendItems = signal<Fy2026LegendItem[]>([]);
  othersLegendItems = signal<Fy2026LegendItem[]>([]);
  contractBarItems = signal<Fy2026BarItem[]>([]);

  private readonly fy2026Service = inject(Fy2026Service);
  private readonly passContractService = inject(PassContractService);

  private topAgencyChart: Chart | null = null;
  private othersAgencyChart: Chart | null = null;
  private agencyRows: IFy2026AgencySpend[] = [];
  private topSlices: TopAgencySlice[] = [];
  private othersAgencyRows: IFy2026AgencySpend[] = [];
  private contractRows: IFy2026ContractSpend[] = [];
  private viewReady = false;
  private pendingRender: (() => void) | null = null;
  private pointerDown: { x: number; y: number } | null = null;

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
    this.destroyAgencyCharts();
  }

  goToAgencies(): void {
    this.selectedAgencyAcronym.set(null);
    this.selectedAgencyLabel.set(null);
    this.level.set('agency');
    this.scheduleRender(() => this.renderAgencyCharts());
  }

  heading(): string {
    if (this.level() === 'contract') {
      return `Contracts with most awarded IT task orders - ${this.selectedAgencyLabel() ?? ''}`;
    }
    return 'FY2026 awarded IT task orders by agency';
  }

  onPointerDown(event: MouseEvent): void {
    this.pointerDown = { x: event.clientX, y: event.clientY };
  }

  onTopLegendClick(event: MouseEvent, item: Fy2026LegendItem): void {
    if (!fy2026IsPlainClick(this.pointerDown, event)) {
      return;
    }
    if (item.kind === 'others') {
      this.scrollToOthersChart();
      return;
    }
    if (item.row) {
      this.selectAgency(item.row);
    }
  }

  onOthersLegendClick(event: MouseEvent, item: Fy2026LegendItem): void {
    if (!fy2026IsPlainClick(this.pointerDown, event) || !item.row) {
      return;
    }
    this.selectAgency(item.row);
  }

  onContractRowClick(event: MouseEvent, index: number): void {
    if (!fy2026IsPlainClick(this.pointerDown, event)) {
      return;
    }
    const row = this.contractRows[index];
    if (!row || row.isOthers) {
      return;
    }
    const contractNumber = row.contractNumber?.trim();
    if (!contractNumber) {
      return;
    }
    this.passContractService.query({ 'contractNumber.equals': contractNumber, size: 1 }).subscribe({
      next: response => {
        const id = response.body?.[0]?.id;
        if (id != null) {
          window.open(`/pass-contract/${id}/view`, '_blank', 'noopener,noreferrer');
        }
      },
    });
  }

  private loadAgencies(): void {
    this.loading.set(true);
    this.error.set(null);
    this.fy2026Service.getAwardedByAgency().subscribe({
      next: rows => {
        this.agencyRows = rows;
        this.splitAgencyRows();
        this.loading.set(false);
        this.level.set('agency');
        this.scheduleRender(() => this.renderAgencyCharts());
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load awarded agency data.');
      },
    });
  }

  private splitAgencyRows(): void {
    const sorted = [...this.agencyRows].sort((a, b) => fy2026Amount(b.spend) - fy2026Amount(a.spend));
    const top = sorted.slice(0, FY2026_TOP_AGENCY_COUNT);
    this.othersAgencyRows = sorted.slice(FY2026_TOP_AGENCY_COUNT);
    this.hasOthersAgencies.set(this.othersAgencyRows.length > 0);

    this.topSlices = top.map(row => ({ kind: 'agency' as const, row }));
    if (this.othersAgencyRows.length > 0) {
      const othersSpend = this.othersAgencyRows.reduce((sum, row) => sum + fy2026Amount(row.spend), 0);
      this.topSlices.push({ kind: 'others', spend: othersSpend });
    }
  }

  private loadContracts(agencyAcronym: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.destroyAgencyCharts();
    this.fy2026Service.getAwardedByContract(agencyAcronym).subscribe({
      next: rows => {
        this.contractRows = this.buildContractRowsWithOthers(rows);
        this.contractBarItems.set(
          fy2026BuildBarItems(
            this.contractRows.map(row => ({
              label: this.contractDisplayName(row),
              value: fy2026Amount(row.spend),
              color: row.isOthers ? FY2026_OTHERS_COLOR : '#ca6702',
              clickable: this.isContractClickable(row),
            })),
          ),
        );
        this.loading.set(false);
        this.level.set('contract');
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Failed to load awarded contract data.');
      },
    });
  }

  private scheduleRender(render: () => void): void {
    if (this.viewReady) {
      requestAnimationFrame(() => {
        requestAnimationFrame(() => render());
      });
    } else {
      this.pendingRender = render;
    }
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

  private buildContractRowsWithOthers(rows: IFy2026ContractSpend[]): IFy2026ContractSpend[] {
    const major: IFy2026ContractSpend[] = [];
    let othersSpend = 0;
    let othersCount = 0;
    for (const row of rows) {
      if (fy2026Amount(row.spend) >= 50_000) {
        major.push(row);
      } else {
        othersSpend += fy2026Amount(row.spend);
        othersCount += 1;
      }
    }
    if (othersCount > 0) {
      major.push({
        contractTitle: FY2026_OTHERS_LABEL,
        contractNumber: null,
        spend: othersSpend,
        isOthers: true,
      });
    }
    return major;
  }

  private isNoContractTitle(title: string | null | undefined): boolean {
    const trimmed = title?.trim();
    return trimmed == null || trimmed === '' || trimmed.toUpperCase() === 'NO CONTRACT';
  }

  private contractDisplayName(row: IFy2026ContractSpend): string {
    if (row.isOthers) {
      return FY2026_OTHERS_LABEL;
    }
    if (!this.isNoContractTitle(row.contractTitle)) {
      return row.contractTitle!.trim();
    }
    const number = row.contractNumber?.trim();
    return number && number !== '' ? number : 'Untitled contract';
  }

  private isContractClickable(row: IFy2026ContractSpend): boolean {
    if (row.isOthers) {
      return false;
    }
    const number = row.contractNumber?.trim();
    return number != null && number !== '';
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
    this.destroyAgencyCharts();

    const topCanvas = this.topAgencyCanvas?.nativeElement;
    if (!topCanvas) {
      this.pendingRender = () => this.renderAgencyCharts();
      return;
    }

    const topLabels = this.topSlices.map(slice =>
      slice.kind === 'others' ? FY2026_OTHERS_LABEL : (slice.row.agency ?? slice.row.agencyAcronym ?? 'Unknown'),
    );
    const topData = this.topSlices.map(slice => (slice.kind === 'others' ? slice.spend : fy2026Amount(slice.row.spend)));
    const topColors = this.topSlices.map((slice, i) =>
      slice.kind === 'others' ? FY2026_OTHERS_COLOR : DOUGHNUT_PALETTE[i % DOUGHNUT_PALETTE.length],
    );

    this.topLegendItems.set(
      this.topSlices.map((slice, i) => ({
        label: topLabels[i],
        color: topColors[i],
        value: topData[i],
        valueFormatted: fy2026FormatCurrency(topData[i]),
        kind: slice.kind,
        row: slice.kind === 'agency' ? slice.row : undefined,
      })),
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
      this.othersLegendItems.set([]);
      return;
    }

    const othersCanvas = this.othersAgencyCanvas?.nativeElement;
    if (!othersCanvas) {
      this.pendingRender = () => this.renderAgencyCharts();
      return;
    }

    const othersLabels = this.othersAgencyRows.map(r => r.agency ?? r.agencyAcronym ?? 'Unknown');
    const othersData = this.othersAgencyRows.map(r => fy2026Amount(r.spend));
    const othersColors = othersLabels.map((_, i) => DOUGHNUT_PALETTE[(i + 3) % DOUGHNUT_PALETTE.length]);

    this.othersLegendItems.set(
      this.othersAgencyRows.map((row, i) => ({
        label: othersLabels[i],
        color: othersColors[i],
        value: othersData[i],
        valueFormatted: fy2026FormatCurrency(othersData[i]),
        kind: 'agency' as const,
        row,
      })),
    );

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
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: ctx => {
                const value = typeof ctx.parsed === 'number' ? ctx.parsed : 0;
                return ` ${fy2026FormatCurrency(value)}`;
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
}
