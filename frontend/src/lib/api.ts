import type { ApiResponse } from '@/types';

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

// ─── Token management ─────────────────────────────────────────────────────────
export function getToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('sf_token');
}

export function setToken(token: string): void {
  localStorage.setItem('sf_token', token);
}

export function removeToken(): void {
  localStorage.removeItem('sf_token');
}

// ─── API Error ────────────────────────────────────────────────────────────────
export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
    public readonly errors?: Record<string, string[]>
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

// ─── Core fetch wrapper ───────────────────────────────────────────────────────
async function request<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    removeToken();
    if (typeof window !== 'undefined') {
      window.location.href = '/login';
    }
    throw new ApiError(401, 'Unauthorized — please log in again.');
  }

  if (!res.ok) {
    let message = `Request failed with status ${res.status}`;
    let errors: Record<string, string[]> | undefined;
    try {
      const body = await res.json();
      message = body.message ?? message;
      errors = body.errors;
    } catch {
      // ignore parse errors
    }
    throw new ApiError(res.status, message, errors);
  }

  // 204 No Content
  if (res.status === 204) return undefined as unknown as T;

  const wrapper = (await res.json()) as ApiResponse<T>;
  return wrapper.data;
}

// ─── HTTP helpers ─────────────────────────────────────────────────────────────
export const api = {
  get: <T>(path: string) => request<T>(path, { method: 'GET' }),

  post: <T>(path: string, body?: unknown) =>
    request<T>(path, {
      method: 'POST',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    }),

  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, {
      method: 'PATCH',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    }),

  delete: <T>(path: string) => request<T>(path, { method: 'DELETE' }),

  // Raw text download (for SQL exports)
  download: async (path: string): Promise<string> => {
    const token = getToken();
    const headers: Record<string, string> = {};
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const res = await fetch(`${API_BASE}${path}`, { headers });
    if (!res.ok) throw new ApiError(res.status, `Download failed (${res.status})`);
    return res.text();
  },
};
