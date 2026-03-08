export type User = {
  id: number
  username: string
  name: string
  email: string
  mobile: string
  role: string
}

export type Flight = {
  id: number
  flightNumber: string
  source: string
  destination: string
  departureTime: string
  arrivalTime: string
  availableSeats: number
  price: number
  airline: string
}

export type Booking = {
  id: number
  bookingCode: string
  flightId: number
  flightNumber: string
  source: string
  destination: string
  departureTime: string
  price: number
  status: string
  passengerName: string
  passengerEmail: string
  passengerMobile: string
}

const API_URL = (import.meta as any).env?.VITE_API_URL || 'http://localhost:8080'

async function request<T>(
  path: string,
  opts: RequestInit & { token?: string } = {},
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(opts.headers as any),
  }
  if (opts.token) headers.Authorization = `Bearer ${opts.token}`

  const res = await fetch(`${API_URL}${path}`, {
    ...opts,
    headers,
  })

  const text = await res.text()
  const data = text ? JSON.parse(text) : {}
  if (!res.ok) {
    const msg = data?.error || `Request failed (${res.status})`
    throw new Error(msg)
  }
  return data as T
}

export async function register(payload: {
  name: string
  email: string
  mobile: string
  username: string
  password: string
}): Promise<User> {
  return request<User>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function login(payload: {
  username: string
  password: string
}): Promise<{ token: string; user: User }> {
  return request<{ token: string; user: User }>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function getFlights(params: {
  source?: string
  destination?: string
}): Promise<{ items: Flight[] }> {
  const q = new URLSearchParams()
  if (params.source) q.set('source', params.source)
  if (params.destination) q.set('destination', params.destination)
  const qs = q.toString()
  return request<{ items: Flight[] }>(`/api/flights${qs ? `?${qs}` : ''}`, {
    method: 'GET',
  })
}

export async function createBooking(
  token: string,
  payload: {
    flightId: number
    passengerName: string
    passengerEmail: string
    passengerMobile: string
  },
): Promise<Booking> {
  return request<Booking>('/api/bookings', {
    method: 'POST',
    token,
    body: JSON.stringify(payload),
  })
}

export async function getBookings(
  token: string,
): Promise<{ items: Booking[] }> {
  return request<{ items: Booking[] }>('/api/bookings', {
    method: 'GET',
    token,
  })
}

