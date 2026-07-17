/**
 * Minimal MCP SSE client for Spring AI MCP servers (protocol: SSE).
 * Speaks JSON-RPC over GET /sse + POST message endpoint.
 */
export type JsonRpcResponse = {
  jsonrpc: '2.0';
  id?: number | string;
  result?: unknown;
  error?: { code: number; message: string; data?: unknown };
};

type Pending = {
  resolve: (value: JsonRpcResponse) => void;
  reject: (reason?: unknown) => void;
};

export class McpSseClient {
  private messageUrl: string | null = null;
  private nextId = 1;
  private readonly pending = new Map<number | string, Pending>();
  private abortController: AbortController | null = null;
  private readerTask: Promise<void> | null = null;

  constructor(private readonly baseUrl: string) {}

  async connect(timeoutMs = 15_000): Promise<void> {
    this.abortController = new AbortController();
    const sseUrl = `${this.baseUrl.replace(/\/+$/, '')}/sse`;

    const response = await fetch(sseUrl, {
      headers: { Accept: 'text/event-stream' },
      signal: this.abortController.signal,
    });
    if (!response.ok || !response.body) {
      throw new Error(`Failed to open SSE at ${sseUrl}: HTTP ${response.status}`);
    }

    const endpointPromise = new Promise<string>((resolve, reject) => {
      const timer = setTimeout(() => reject(new Error('Timed out waiting for MCP endpoint event')), timeoutMs);
      this.readerTask = this.readSse(response.body!, (event, data) => {
        if (event === 'endpoint') {
          clearTimeout(timer);
          const path = data.trim();
          resolve(path.startsWith('http') ? path : `${this.baseUrl.replace(/\/+$/, '')}${path.startsWith('/') ? '' : '/'}${path}`);
        } else if (event === 'message') {
          this.handleMessage(data);
        }
      }).catch(reject);
    });

    this.messageUrl = await endpointPromise;
    await this.initialize();
  }

  async close(): Promise<void> {
    this.abortController?.abort();
    this.abortController = null;
    try {
      await this.readerTask;
    } catch {
      // aborted
    }
    for (const [, pending] of this.pending) {
      pending.reject(new Error('MCP client closed'));
    }
    this.pending.clear();
  }

  async listTools(): Promise<Array<{ name: string; description?: string }>> {
    const response = await this.request('tools/list', {});
    const result = response.result as { tools: Array<{ name: string; description?: string }> };
    return result.tools ?? [];
  }

  async callTool(name: string, args: Record<string, unknown> = {}): Promise<JsonRpcResponse> {
    return this.request('tools/call', { name, arguments: args });
  }

  async listResources(): Promise<Array<{ uri: string; name?: string; description?: string; mimeType?: string }>> {
    const response = await this.request('resources/list', {});
    const result = response.result as {
      resources: Array<{ uri: string; name?: string; description?: string; mimeType?: string }>;
    };
    return result.resources ?? [];
  }

  async readResource(uri: string): Promise<JsonRpcResponse> {
    return this.request('resources/read', { uri });
  }

  private async initialize(): Promise<void> {
    const init = await this.request('initialize', {
      protocolVersion: '2024-11-05',
      capabilities: {},
      clientInfo: { name: 'dcpass-e2e', version: '1.0.0' },
    });
    if (init.error) {
      throw new Error(`MCP initialize failed: ${init.error.message}`);
    }
    await this.notify('notifications/initialized', {});
  }

  private async request(method: string, params: Record<string, unknown>): Promise<JsonRpcResponse> {
    if (!this.messageUrl) {
      throw new Error('MCP client is not connected');
    }
    const id = this.nextId++;
    const payload = { jsonrpc: '2.0', id, method, params };

    const responsePromise = new Promise<JsonRpcResponse>((resolve, reject) => {
      this.pending.set(id, { resolve, reject });
      setTimeout(() => {
        if (this.pending.has(id)) {
          this.pending.delete(id);
          reject(new Error(`Timed out waiting for MCP response to ${method}`));
        }
      }, 30_000);
    });

    const httpResponse = await fetch(this.messageUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Accept: 'application/json, text/event-stream' },
      body: JSON.stringify(payload),
    });

    // Some servers reply inline on the POST; others only via SSE.
    if (httpResponse.ok) {
      const contentType = httpResponse.headers.get('content-type') ?? '';
      if (contentType.includes('application/json')) {
        const body = (await httpResponse.json()) as JsonRpcResponse;
        if (body.id !== undefined) {
          this.pending.delete(id);
          return body;
        }
      } else if (contentType.includes('text/event-stream') && httpResponse.body) {
        // drain is handled by shared SSE; fall through to wait
      } else {
        const text = await httpResponse.text();
        if (text.trim().startsWith('{')) {
          const body = JSON.parse(text) as JsonRpcResponse;
          if (body.id !== undefined) {
            this.pending.delete(id);
            return body;
          }
        }
      }
    } else if (httpResponse.status !== 202 && httpResponse.status !== 200) {
      this.pending.delete(id);
      throw new Error(`MCP POST ${method} failed: HTTP ${httpResponse.status} ${await httpResponse.text()}`);
    }

    return responsePromise;
  }

  private async notify(method: string, params: Record<string, unknown>): Promise<void> {
    if (!this.messageUrl) {
      throw new Error('MCP client is not connected');
    }
    const httpResponse = await fetch(this.messageUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ jsonrpc: '2.0', method, params }),
    });
    if (!httpResponse.ok && httpResponse.status !== 202) {
      throw new Error(`MCP notify ${method} failed: HTTP ${httpResponse.status}`);
    }
  }

  private handleMessage(data: string): void {
    let message: JsonRpcResponse;
    try {
      message = JSON.parse(data) as JsonRpcResponse;
    } catch {
      return;
    }
    if (message.id === undefined) {
      return;
    }
    const pending = this.pending.get(message.id);
    if (pending) {
      this.pending.delete(message.id);
      pending.resolve(message);
    }
  }

  private async readSse(
    body: ReadableStream<Uint8Array>,
    onEvent: (event: string, data: string) => void,
  ): Promise<void> {
    const reader = body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';
    let eventName = 'message';
    let dataLines: string[] = [];

    const flush = () => {
      if (dataLines.length === 0) {
        eventName = 'message';
        return;
      }
      onEvent(eventName, dataLines.join('\n'));
      eventName = 'message';
      dataLines = [];
    };

    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        flush();
        break;
      }
      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split(/\r?\n/);
      buffer = lines.pop() ?? '';
      for (const line of lines) {
        if (line === '') {
          flush();
        } else if (line.startsWith('event:')) {
          eventName = line.slice(6).trim();
        } else if (line.startsWith('data:')) {
          dataLines.push(line.slice(5).trimStart());
        }
      }
    }
  }
}
