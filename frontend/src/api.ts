export class ApiError extends Error { constructor(message: string, public status: number) { super(message); } }
export async function api<T>(path: string, method = 'GET', body?: unknown): Promise<T> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (method !== 'GET') {
    const response = await fetch('/api/auth/csrf', { credentials: 'same-origin' });
    if (!response.ok) throw new ApiError('Não foi possível validar a sessão. Atualize a página.', response.status);
    const csrf = await response.json(); headers[csrf.headerName] = csrf.token;
  }
  const response = await fetch(`/api${path}`, { method, credentials: 'same-origin', headers, body: body === undefined ? undefined : JSON.stringify(body) });
  if (response.status === 204) return undefined as T;
  const data = await response.json().catch(() => ({ error: 'O servidor não respondeu como esperado.' }));
  if (!response.ok) {
    if (response.status === 401 && !path.startsWith('/auth/')) window.dispatchEvent(new Event('session-expired'));
    throw new ApiError(data.error || 'Não foi possível concluir. Tente novamente.', response.status);
  }
  return data as T;
}
