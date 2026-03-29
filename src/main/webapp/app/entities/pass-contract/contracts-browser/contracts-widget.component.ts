import { CurrencyPipe } from '@angular/common';
import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import FormatDatePipe from 'app/shared/pipes/format-date.pipe';
import TruncatePipe from 'app/shared/pipes/truncate.pipe';
import { PassContractService } from 'app/entities/pass-contract/service/pass-contract.service';

/** Contract data from MCP tool result (JSON). Dates are strings. */
export interface ContractData {
  id: number;
  title?: string | null;
  description?: string | null;
  contractNumber?: string | null;
  agencyAcronym?: string | null;
  agencyName?: string | null;
  contractAmount?: number | null;
  awardDate?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  supplier?: string | null;
  contractStatus?: string | null;
  contractingOfficer?: string | null;
  contractDetailsLink?: string | null;
  [key: string]: unknown;
}

/** Pagination params for API calls (page, size, sort, q, awardDate.*, etc.) */
export interface ApiParams {
  [key: string]: string | number;
}

@Component({
  selector: 'jhi-contracts-widget',
  templateUrl: './contracts-widget.component.html',
  imports: [FontAwesomeModule, NgbModule, TruncatePipe, FormatDatePipe, CurrencyPipe],
})
export default class ContractsWidgetComponent implements OnInit, OnDestroy {
  contracts = signal<ContractData[]>([]);
  expandedId = signal<number | null>(null);
  error = signal<string | null>(null);
  waiting = signal(true);

  /** Pagination state; null when no pagination (legacy array format). */
  page = signal<number>(0);
  totalItems = signal<number>(0);
  size = signal<number>(20);
  apiParams = signal<ApiParams | null>(null);
  loadingPage = signal(false);

  totalPages = computed(() => {
    const total = this.totalItems();
    const sz = this.size();
    return sz > 0 ? Math.ceil(total / sz) : 0;
  });

  hasPagination = computed(() => this.apiParams() !== null && this.totalPages() > 1);

  private readonly passContractService = inject(PassContractService);
  private messageHandler = (event: MessageEvent) => this.handleMessage(event);

  ngOnInit(): void {
    window.addEventListener('message', this.messageHandler, { passive: true });
    window.addEventListener('openai:set_globals', this.openAiSetGlobalsHandler, { passive: true });
    this.applyHostToolOutput((window as unknown as { openai?: { toolOutput?: unknown } }).openai?.toolOutput);
  }

  ngOnDestroy(): void {
    window.removeEventListener('message', this.messageHandler);
    window.removeEventListener('openai:set_globals', this.openAiSetGlobalsHandler);
  }

  private readonly openAiSetGlobalsHandler = (event: Event): void => {
    const detail = (event as CustomEvent<{ globals?: { toolOutput?: unknown } }>).detail;
    const out = detail?.globals?.toolOutput ?? (window as unknown as { openai?: { toolOutput?: unknown } }).openai?.toolOutput;
    this.applyHostToolOutput(out);
  };

  private handleMessage(event: MessageEvent): void {
    if (event.source !== window.parent) return;
    const msg = event.data;
    if (!msg || msg.jsonrpc !== '2.0') return;
    if (msg.method !== 'ui/notifications/tool-result') return;

    const params = msg.params;
    if (!params) return;

    this.logHostToolOutput('postMessage (ui/notifications/tool-result)', params);
    this.applyToolResultPayload(params);
  }

  /** ChatGPT / MCP Apps: tool result as notification params or as window.openai.toolOutput. */
  private applyToolResultPayload(params: { structuredContent?: unknown; content?: { type?: string; text?: string }[] }): void {
    let applied = this.tryApplyStructuredContent(params.structuredContent);
    if (!applied && this.isContractsPageToolPayload(params)) {
      applied = this.tryApplyStructuredContent(params);
    }
    if (applied) {
      this.error.set(null);
      this.waiting.set(false);
      return;
    }

    const text = params.content?.[0]?.text;
    if (text) {
      try {
        this.applyParsedResult(JSON.parse(text));
        this.error.set(null);
      } catch {
        this.error.set('Failed to parse contract data.');
      }
      this.waiting.set(false);
      return;
    }

    this.waiting.set(false);
  }

  private applyHostToolOutput(toolOutput: unknown): void {
    if (toolOutput == null) return;
    this.logHostToolOutput('window.openai.toolOutput / openai:set_globals', toolOutput);
    const out = toolOutput as { structuredContent?: unknown; content?: { text?: string }[] };
    this.applyToolResultPayload(out);
  }

  /** Logs whenever the host delivers MCP / ChatGPT tool result data to the widget. */
  private logHostToolOutput(source: string, payload: unknown): void {
    // eslint-disable-next-line no-console -- intentional widget debugging
    console.log(`[jhi-contracts-widget] host tool output (${source}):`, payload);
  }

  /** Host may send {@code structuredContent}, or the same object at the top level (no wrapper). */
  private isContractsPageToolPayload(v: unknown): boolean {
    return typeof v === 'object' && v !== null && Array.isArray((v as { contracts?: unknown }).contracts);
  }

  /** @returns true if structuredContent was recognized and applied */
  private tryApplyStructuredContent(structured: unknown): boolean {
    if (structured == null) return false;

    if (Array.isArray(structured)) {
      this.contracts.set(structured as ContractData[]);
      this.apiParams.set(null);
      return true;
    }

    if (typeof structured === 'object' && Array.isArray((structured as { contracts?: unknown }).contracts)) {
      const parsed = structured as {
        contracts: ContractData[];
        page?: number;
        totalItems?: number;
        size?: number;
        apiParams?: ApiParams;
      };
      this.contracts.set(parsed.contracts);
      this.page.set(Number(parsed.page ?? 0));
      this.totalItems.set(Number(parsed.totalItems ?? 0));
      this.size.set(Number(parsed.size ?? 20));
      this.apiParams.set(parsed.apiParams ?? {});
      return true;
    }

    return false;
  }

  private applyParsedResult(parsed: unknown): void {
    if (this.tryApplyStructuredContent(parsed)) {
      return;
    }
    if (parsed && typeof parsed === 'object') {
      this.contracts.set([parsed as ContractData]);
      this.apiParams.set(null);
    } else if (parsed !== null && parsed !== undefined) {
      this.contracts.set([parsed as ContractData]);
      this.apiParams.set(null);
    }
  }

  loadPage(pageIndex: number): void {
    const params = this.apiParams();
    if (!params) return;
    this.loadingPage.set(true);
    this.error.set(null);
    const req = { ...params, page: pageIndex };
    this.passContractService.query(req).subscribe({
      next: res => {
        this.contracts.set((res.body ?? []) as ContractData[]);
        this.page.set(pageIndex);
        const totalHeader = res.headers.get(TOTAL_COUNT_RESPONSE_HEADER);
        if (totalHeader) this.totalItems.set(Number(totalHeader));
        this.loadingPage.set(false);
      },
      error: () => {
        this.error.set('Failed to load page.');
        this.loadingPage.set(false);
      },
    });
  }

  toggleRow(id: number): void {
    this.expandedId.set(this.expandedId() === id ? null : id);
  }

  isExpanded(id: number): boolean {
    return this.expandedId() === id;
  }

  trackId = (c: ContractData): number => c.id;
}
