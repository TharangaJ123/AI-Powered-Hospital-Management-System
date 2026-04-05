import { useEffect, useMemo, useState } from 'react'
import { CircleAlert, LoaderCircle, ShieldCheck, X } from 'lucide-react'

const EMPTY_LOGIN = {
  email: '',
  password: '',
  role: 'PATIENT',
}

const EMPTY_SIGNUP = {
  role: 'PATIENT',
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  phoneNumber: '',
  address: '',
  dateOfBirth: '',
  specialization: '',
  doctorRegistrationNumber: '',
}

const ROLE_OPTIONS = ['PATIENT', 'DOCTOR', 'ADMIN']

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const AuthModal = ({ mode, onClose, onLogin, onSignup }) => {
  const [activeMode, setActiveMode] = useState(mode)
  const [loginData, setLoginData] = useState(EMPTY_LOGIN)
  const [signupData, setSignupData] = useState(EMPTY_SIGNUP)
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    setActiveMode(mode)
    setError('')
  }, [mode])

  const modalTitle = useMemo(() => {
    return activeMode === 'signup' ? 'Create Your Account' : 'Welcome Back'
  }, [activeMode])

  const validateLogin = () => {
    if (!ROLE_OPTIONS.includes(loginData.role)) {
      return 'Please select a valid role.'
    }

    if (!emailRegex.test(loginData.email.trim())) {
      return 'Please enter a valid email address.'
    }

    if (loginData.password.length < 6) {
      return 'Password must be at least 6 characters.'
    }

    return ''
  }

  const validateSignup = () => {
    if (!ROLE_OPTIONS.includes(signupData.role)) {
      return 'Please select a valid role.'
    }

    if (signupData.name.trim().length < 2) {
      return 'Please enter your full name.'
    }

    if (!emailRegex.test(signupData.email.trim())) {
      return 'Please enter a valid email address.'
    }

    if (signupData.password.length < 8) {
      return 'Password must be at least 8 characters.'
    }

    if (signupData.password !== signupData.confirmPassword) {
      return 'Passwords do not match.'
    }

    if (signupData.role === 'PATIENT') {
      if (!signupData.dateOfBirth) return 'Please enter your date of birth.'
      const dob = new Date(signupData.dateOfBirth)
      const today = new Date()
      if (dob > today) return 'Date of Birth cannot be in the future.'
    }

    return ''
  }

  const handleLoginSubmit = async (event) => {
    event.preventDefault()
    const validationError = validateLogin()

    if (validationError) {
      setError(validationError)
      return
    }

    setError('')
    setIsLoading(true)

    try {
      await onLogin({
        email: loginData.email.trim(),
        password: loginData.password,
        selectedRole: loginData.role,
      })
      setLoginData(EMPTY_LOGIN)
    } catch (submitError) {
      setError(submitError.message || 'Unable to sign in at the moment.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSignupSubmit = async (event) => {
    event.preventDefault()
    const validationError = validateSignup()

    if (validationError) {
      setError(validationError)
      return
    }

    setError('')
    setIsLoading(true)

    try {
      await onSignup({
        role: signupData.role,
        name: signupData.name.trim(),
        email: signupData.email.trim(),
        password: signupData.password,
        phoneNumber: signupData.phoneNumber,
        address: signupData.address.trim(),
        dateOfBirth: signupData.dateOfBirth,
        specialization: signupData.role === 'DOCTOR' ? signupData.specialization.trim() : null,
        doctorRegistrationNumber: signupData.role === 'DOCTOR' ? signupData.doctorRegistrationNumber.trim() : null,
      })
      setSignupData(EMPTY_SIGNUP)
    } catch (submitError) {
      setError(submitError.message || 'Unable to create account at the moment.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-[100] bg-slate-950/55 backdrop-blur-sm px-4 py-8 flex items-center justify-center">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl overflow-y-auto max-h-full border border-slate-100">
        <div className="relative px-6 py-6 bg-gradient-to-br from-[#002d5a] to-[#0066cc] text-white">
          <button
            type="button"
            onClick={onClose}
            className="absolute top-4 right-4 text-white/80 hover:text-white transition-colors"
            aria-label="Close authentication dialog"
          >
            <X className="w-5 h-5" />
          </button>
          <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-[11px] uppercase tracking-wider bg-white/10 border border-white/20 font-bold">
            <ShieldCheck className="w-3.5 h-3.5" />
            Secure Access
          </span>
          <h2 className="text-2xl font-extrabold mt-4">{modalTitle}</h2>
          <p className="text-sm text-blue-100 mt-1">Use your credentials to access OMNIHEALTH services.</p>
        </div>

        <div className="p-6">
          <div className="bg-slate-100 p-1 rounded-xl grid grid-cols-2 gap-1 mb-6">
            <button
              type="button"
              onClick={() => {
                setActiveMode('login')
                setError('')
              }}
              className={`py-2.5 text-sm font-bold rounded-lg transition-all ${
                activeMode === 'login' ? 'bg-white text-[#0066cc] shadow-sm' : 'text-slate-500'
              }`}
            >
              Login
            </button>
            <button
              type="button"
              onClick={() => {
                setActiveMode('signup')
                setError('')
              }}
              className={`py-2.5 text-sm font-bold rounded-lg transition-all ${
                activeMode === 'signup' ? 'bg-white text-[#0066cc] shadow-sm' : 'text-slate-500'
              }`}
            >
              Sign Up
            </button>
          </div>

          {error && (
            <div className="mb-4 flex items-start gap-2 rounded-xl border border-red-100 bg-red-50 px-3 py-2.5 text-sm text-red-700">
              <CircleAlert className="w-4 h-4 mt-0.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {activeMode === 'login' ? (
            <form onSubmit={handleLoginSubmit} className="space-y-4">
              <div>
                <label htmlFor="login-role" className="auth-label">Role</label>
                <select
                  id="login-role"
                  value={loginData.role}
                  onChange={(event) => setLoginData((prev) => ({ ...prev, role: event.target.value }))}
                  className="auth-input"
                  required
                >
                  {ROLE_OPTIONS.map((role) => (
                    <option key={role} value={role}>{role}</option>
                  ))}
                </select>
              </div>
              <div>
                <label htmlFor="login-email" className="auth-label">Email</label>
                <input
                  id="login-email"
                  type="email"
                  value={loginData.email}
                  onChange={(event) => setLoginData((prev) => ({ ...prev, email: event.target.value }))}
                  className="auth-input"
                  placeholder="you@example.com"
                  autoComplete="email"
                  required
                />
              </div>
              <div>
                <label htmlFor="login-password" className="auth-label">Password</label>
                <input
                  id="login-password"
                  type="password"
                  value={loginData.password}
                  onChange={(event) => setLoginData((prev) => ({ ...prev, password: event.target.value }))}
                  className="auth-input"
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                />
              </div>
              <button type="submit" className="btn-primary w-full justify-center" disabled={isLoading}>
                {isLoading ? (
                  <span className="inline-flex items-center gap-2">
                    <LoaderCircle className="w-4 h-4 animate-spin" />
                    Signing In...
                  </span>
                ) : (
                  'Login'
                )}
              </button>
            </form>
          ) : (
            <form onSubmit={handleSignupSubmit} className="space-y-4">
              <div>
                <label htmlFor="signup-role" className="auth-label">Role</label>
                <select
                  id="signup-role"
                  value={signupData.role}
                  onChange={(event) => setSignupData((prev) => ({ ...prev, role: event.target.value }))}
                  className="auth-input"
                  required
                >
                  {ROLE_OPTIONS.map((role) => (
                    <option key={role} value={role}>{role}</option>
                  ))}
                </select>
              </div>
              <div>
                <label htmlFor="signup-name" className="auth-label">Full Name</label>
                <input
                  id="signup-name"
                  type="text"
                  value={signupData.name}
                  onChange={(event) => setSignupData((prev) => ({ ...prev, name: event.target.value }))}
                  className="auth-input"
                  placeholder="Jane Doe"
                  autoComplete="name"
                  required
                />
              </div>
              <div>
                <label htmlFor="signup-email" className="auth-label">Email</label>
                <input
                  id="signup-email"
                  type="email"
                  value={signupData.email}
                  onChange={(event) => setSignupData((prev) => ({ ...prev, email: event.target.value }))}
                  className="auth-input"
                  placeholder="you@example.com"
                  autoComplete="email"
                  required
                />
              </div>
              <div>
                <label htmlFor="signup-password" className="auth-label">Password</label>
                <input
                  id="signup-password"
                  type="password"
                  value={signupData.password}
                  onChange={(event) => setSignupData((prev) => ({ ...prev, password: event.target.value }))}
                  className="auth-input"
                  placeholder="Create a strong password"
                  autoComplete="new-password"
                  required
                />
              </div>
              <div>
                <label htmlFor="signup-confirm-password" className="auth-label">Confirm Password</label>
                <input
                  id="signup-confirm-password"
                  type="password"
                  value={signupData.confirmPassword}
                  onChange={(event) => setSignupData((prev) => ({ ...prev, confirmPassword: event.target.value }))}
                  className="auth-input"
                  placeholder="Confirm password"
                  autoComplete="new-password"
                  required
                />
              </div>
              {signupData.role === 'PATIENT' && (
                <>
                  <div>
                    <label htmlFor="signup-phone" className="auth-label">Phone Number</label>
                    <input
                      id="signup-phone"
                      type="text"
                      value={signupData.phoneNumber}
                      onChange={(event) => setSignupData((prev) => ({ ...prev, phoneNumber: event.target.value }))}
                      className="auth-input"
                      placeholder="07xxxxxxxx"
                      autoComplete="tel"
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="signup-dob" className="auth-label">Date of Birth</label>
                    <input
                      id="signup-dob"
                      type="date"
                      value={signupData.dateOfBirth}
                      onChange={(event) => setSignupData((prev) => ({ ...prev, dateOfBirth: event.target.value }))}
                      className="auth-input"
                      max={new Date().toISOString().split('T')[0]}
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="signup-address" className="auth-label">Address</label>
                    <input
                      id="signup-address"
                      type="text"
                      value={signupData.address}
                      onChange={(event) => setSignupData((prev) => ({ ...prev, address: event.target.value }))}
                      className="auth-input"
                      placeholder="Street, City"
                      autoComplete="street-address"
                      required
                    />
                  </div>
                </>
              )}
              {signupData.role === 'DOCTOR' && (
                <>
                  <div>
                    <label htmlFor="signup-specialization" className="auth-label">Specialization</label>
                    <input
                      id="signup-specialization"
                      type="text"
                      value={signupData.specialization}
                      onChange={(event) => setSignupData((prev) => ({ ...prev, specialization: event.target.value }))}
                      className="auth-input"
                      placeholder="e.g. Cardiology, Pediatrics"
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="signup-reg-no" className="auth-label">Medical Registration No.</label>
                    <input
                      id="signup-reg-no"
                      type="text"
                      value={signupData.doctorRegistrationNumber}
                      onChange={(event) => setSignupData((prev) => ({ ...prev, doctorRegistrationNumber: event.target.value }))}
                      className="auth-input"
                      placeholder="Reg-XXXXX"
                      required
                    />
                  </div>
                </>
              )}
              <button type="submit" className="btn-secondary w-full justify-center" disabled={isLoading}>
                {isLoading ? (
                  <span className="inline-flex items-center gap-2">
                    <LoaderCircle className="w-4 h-4 animate-spin" />
                    Creating Account...
                  </span>
                ) : (
                  'Sign Up'
                )}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}

export default AuthModal
