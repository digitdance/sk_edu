const API_BASE = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
const ACCESS_TOKEN_KEY = 'teacher-access-token'

export class ApiError extends Error {
  constructor(message: string, readonly status: number, readonly code?: number) {
    super(message)
    this.name = 'ApiError'
  }
}

interface ApiEnvelope<T> {
  code: number
  data: T
  message?: string
}

interface ApiRequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown
}

export function setAccessToken(token: string | null) {
  if (token) localStorage.setItem(ACCESS_TOKEN_KEY, token)
  else localStorage.removeItem(ACCESS_TOKEN_KEY)
}

export async function apiRequest<T>(
  path: string,
  options: ApiRequestOptions = {},
  anonymous = false,
): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')

  let body: BodyInit | undefined
  if (options.body !== undefined) {
    if (options.body instanceof FormData || typeof options.body === 'string') {
      body = options.body
    } else {
      headers.set('Content-Type', 'application/json')
      body = JSON.stringify(options.body)
    }
  }

  if (!anonymous) {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY)
    if (token) headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
    body,
  })
  let envelope: ApiEnvelope<T> | undefined
  try {
    envelope = await response.json() as ApiEnvelope<T>
  } catch {
    // Preserve a useful HTTP error when the server returns a non-JSON response.
  }

  if (response.status === 401) {
    setAccessToken(null)
    window.dispatchEvent(new Event('teacher-session-expired'))
  }
  if (!response.ok || !envelope || envelope.code !== 0) {
    throw new ApiError(envelope?.message || `请求失败（${response.status}）`, response.status, envelope?.code)
  }
  return envelope.data
}
