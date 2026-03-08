import { useEffect, useMemo, useState } from 'react'
import {
  createBooking,
  getBookings,
  getFlights,
  login,
  register,
} from './api'
import type { Booking, Flight, User } from './api'

type AuthState =
  | { status: 'logged_out' }
  | { status: 'logged_in'; token: string; user: User }

const LS_TOKEN = 'airline_token'
const LS_USER = 'airline_user'

export default function App() {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [auth, setAuth] = useState<AuthState>({ status: 'logged_out' })
  const [msg, setMsg] = useState<string>('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem(LS_TOKEN)
    const userRaw = localStorage.getItem(LS_USER)
    if (token && userRaw) {
      try {
        const user = JSON.parse(userRaw) as User
        setAuth({ status: 'logged_in', token, user })
      } catch {
        localStorage.removeItem(LS_TOKEN)
        localStorage.removeItem(LS_USER)
      }
    }
  }, [])

  const header = useMemo(() => {
    if (auth.status === 'logged_in') {
      return `Welcome, ${auth.user.name}`
    }
    return 'Airline Reservation'
  }, [auth])

  return (
    <div className="page">
      <div className="topbar">
        <div className="brand">{header}</div>
        {auth.status === 'logged_in' ? (
          <button
            className="btn btn-secondary"
            onClick={() => {
              localStorage.removeItem(LS_TOKEN)
              localStorage.removeItem(LS_USER)
              setAuth({ status: 'logged_out' })
              setMode('login')
              setMsg('')
            }}
          >
            Logout
          </button>
        ) : null}
      </div>

      <div className="container">
        {msg ? <div className="alert">{msg}</div> : null}

        {auth.status === 'logged_in' ? (
          <Dashboard auth={auth} setMsg={setMsg} />
        ) : mode === 'login' ? (
          <LoginCard
            busy={busy}
            onSwitch={() => {
              setMode('register')
              setMsg('')
            }}
            onSubmit={async (username, password) => {
              setBusy(true)
              setMsg('')
              try {
                const res = await login({ username, password })
                localStorage.setItem(LS_TOKEN, res.token)
                localStorage.setItem(LS_USER, JSON.stringify(res.user))
                setAuth({ status: 'logged_in', token: res.token, user: res.user })
              } catch (e: any) {
                setMsg(e?.message || 'Login failed')
              } finally {
                setBusy(false)
              }
            }}
          />
        ) : (
          <RegisterCard
            busy={busy}
            onSwitch={() => {
              setMode('login')
              setMsg('')
            }}
            onSubmit={async (payload) => {
              setBusy(true)
              setMsg('')
              try {
                await register(payload)
                setMode('login')
                setMsg('Registration successful. Please login.')
              } catch (e: any) {
                setMsg(e?.message || 'Registration failed')
              } finally {
                setBusy(false)
              }
            }}
          />
        )}
      </div>
    </div>
  )
}

function LoginCard({
  onSubmit,
  onSwitch,
  busy,
}: {
  onSubmit: (username: string, password: string) => Promise<void>
  onSwitch: () => void
  busy: boolean
}) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  return (
    <div className="card">
      <h2 className="card-title">Login</h2>
      <div className="grid">
        <label className="field">
          <div className="label">Username</div>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
          />
        </label>
        <label className="field">
          <div className="label">Password</div>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </label>
      </div>
      <div className="row">
        <button
          className="btn"
          disabled={busy}
          onClick={() => onSubmit(username.trim(), password)}
        >
          {busy ? 'Signing in...' : 'Login'}
        </button>
        <button className="btn btn-secondary" disabled={busy} onClick={onSwitch}>
          Create account
        </button>
      </div>
    </div>
  )
}

function RegisterCard({
  onSubmit,
  onSwitch,
  busy,
}: {
  onSubmit: (payload: {
    name: string
    email: string
    mobile: string
    username: string
    password: string
  }) => Promise<void>
  onSwitch: () => void
  busy: boolean
}) {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [mobile, setMobile] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')

  return (
    <div className="card">
      <h2 className="card-title">Register</h2>
      <div className="grid">
        <label className="field">
          <div className="label">Full name</div>
          <input value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <label className="field">
          <div className="label">Email</div>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
          />
        </label>
        <label className="field">
          <div className="label">Mobile</div>
          <input
            value={mobile}
            onChange={(e) => setMobile(e.target.value)}
            inputMode="numeric"
            placeholder="9876543210"
          />
        </label>
        <label className="field">
          <div className="label">Username</div>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
          />
        </label>
        <label className="field">
          <div className="label">Password</div>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="new-password"
          />
        </label>
        <label className="field">
          <div className="label">Confirm password</div>
          <input
            type="password"
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            autoComplete="new-password"
          />
        </label>
      </div>
      <div className="row">
        <button
          className="btn"
          disabled={busy}
          onClick={() => {
            if (password !== confirm) return
            return onSubmit({
              name: name.trim(),
              email: email.trim(),
              mobile: mobile.trim(),
              username: username.trim(),
              password,
            })
          }}
        >
          {busy ? 'Creating...' : 'Register'}
        </button>
        <button className="btn btn-secondary" disabled={busy} onClick={onSwitch}>
          Back to login
        </button>
      </div>
      {password !== confirm && confirm ? (
        <div className="hint">Passwords do not match</div>
      ) : null}
    </div>
  )
}

function Dashboard({
  auth,
  setMsg,
}: {
  auth: Extract<AuthState, { status: 'logged_in' }>
  setMsg: (s: string) => void
}) {
  const [tab, setTab] = useState<'flights' | 'bookings'>('flights')
  const [source, setSource] = useState('')
  const [destination, setDestination] = useState('')
  const [flights, setFlights] = useState<Flight[]>([])
  const [bookings, setBookings] = useState<Booking[]>([])
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    ;(async () => {
      setBusy(true)
      try {
        const res = await getFlights({})
        setFlights(res.items || [])
      } catch (e: any) {
        setMsg(e?.message || 'Failed to load flights')
      } finally {
        setBusy(false)
      }
    })()
  }, [setMsg])

  useEffect(() => {
    if (tab !== 'bookings') return
    ;(async () => {
      setBusy(true)
      try {
        const res = await getBookings(auth.token)
        setBookings(res.items || [])
      } catch (e: any) {
        setMsg(e?.message || 'Failed to load bookings')
      } finally {
        setBusy(false)
      }
    })()
  }, [tab, auth.token, setMsg])

  async function doSearch() {
    setBusy(true)
    setMsg('')
    try {
      const res = await getFlights({
        source: source.trim(),
        destination: destination.trim(),
      })
      setFlights(res.items || [])
    } catch (e: any) {
      setMsg(e?.message || 'Search failed')
    } finally {
      setBusy(false)
    }
  }

  async function doBook(f: Flight) {
    const passengerName = prompt('Passenger name', auth.user.name) || ''
    if (!passengerName.trim()) return
    const passengerEmail = prompt('Passenger email', auth.user.email) || ''
    if (!passengerEmail.trim()) return
    const passengerMobile = prompt('Passenger mobile', auth.user.mobile) || ''
    if (!passengerMobile.trim()) return

    setBusy(true)
    setMsg('')
    try {
      const b = await createBooking(auth.token, {
        flightId: f.id,
        passengerName: passengerName.trim(),
        passengerEmail: passengerEmail.trim(),
        passengerMobile: passengerMobile.trim(),
      })
      setMsg(`Booking confirmed. PNR: ${b.bookingCode}`)
      const res = await getFlights({
        source: source.trim(),
        destination: destination.trim(),
      })
      setFlights(res.items || [])
    } catch (e: any) {
      setMsg(e?.message || 'Booking failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card">
      <div className="tabs">
        <button
          className={tab === 'flights' ? 'tab tab-active' : 'tab'}
          onClick={() => setTab('flights')}
        >
          Flights
        </button>
        <button
          className={tab === 'bookings' ? 'tab tab-active' : 'tab'}
          onClick={() => setTab('bookings')}
        >
          My bookings
        </button>
      </div>

      {tab === 'flights' ? (
        <>
          <div className="row">
            <input
              className="input"
              placeholder="From"
              value={source}
              onChange={(e) => setSource(e.target.value)}
            />
            <input
              className="input"
              placeholder="To"
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
            />
            <button className="btn" disabled={busy} onClick={doSearch}>
              Search
            </button>
          </div>

          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Flight</th>
                  <th>Route</th>
                  <th>Departure</th>
                  <th>Seats</th>
                  <th>Price</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {flights.map((f) => (
                  <tr key={f.id}>
                    <td>{f.flightNumber}</td>
                    <td>
                      {f.source} → {f.destination}
                    </td>
                    <td>{f.departureTime}</td>
                    <td>{f.availableSeats}</td>
                    <td>₹{Math.round(f.price)}</td>
                    <td>
                      <button
                        className="btn btn-secondary"
                        disabled={busy || f.availableSeats <= 0}
                        onClick={() => doBook(f)}
                      >
                        Book
                      </button>
                    </td>
                  </tr>
                ))}
                {!flights.length ? (
                  <tr>
                    <td colSpan={6} className="muted">
                      No flights found
                    </td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </div>
        </>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>PNR</th>
                <th>Flight</th>
                <th>Route</th>
                <th>Passenger</th>
                <th>Departure</th>
                <th>Price</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {bookings.map((b) => (
                <tr key={b.id}>
                  <td>{b.bookingCode}</td>
                  <td>{b.flightNumber}</td>
                  <td>
                    {b.source} → {b.destination}
                  </td>
                  <td>{b.passengerName}</td>
                  <td>{b.departureTime}</td>
                  <td>₹{Math.round(b.price)}</td>
                  <td>{b.status}</td>
                </tr>
              ))}
              {!bookings.length ? (
                <tr>
                  <td colSpan={7} className="muted">
                    No bookings yet
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

