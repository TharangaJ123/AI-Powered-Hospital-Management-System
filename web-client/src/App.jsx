import { useEffect, useState } from 'react'
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import AuthModal from './components/AuthModal'
import Home from './pages/Home'
import Profile from './pages/Profile'
import {
  clearSession,
  getPatientProfile,
  loginUser,
  persistSession,
  readSession,
  registerUser,
  updatePatientProfile,
} from './services/auth'

function App() {
  const navigate = useNavigate()
  const [authMode, setAuthMode] = useState(null)
  const [session, setSession] = useState(null)
  const [patientProfile, setPatientProfile] = useState(null)
  const [profileError, setProfileError] = useState('')
  const [isSavingProfile, setIsSavingProfile] = useState(false)

  useEffect(() => {
    const existing = readSession()
    if (existing) {
      setSession(existing)
    }
  }, [])

  useEffect(() => {
    const loadProfile = async () => {
      if (!session?.token || session?.user?.role !== 'PATIENT' || !session?.user?.id) {
        setPatientProfile(null)
        setProfileError('')
        return
      }

      try {
        const profile = await getPatientProfile({
          userId: session.user.id,
          token: session.token,
        })
        setPatientProfile(profile)
        setProfileError('')
      } catch (error) {
        setProfileError(error.message || 'Unable to load patient profile.')
      }
    }

    loadProfile()
  }, [session])

  const handleOpenAuth = (mode) => {
    setAuthMode(mode)
  }

  const handleCloseAuth = () => {
    setAuthMode(null)
  }

  const handleLogin = async (credentials) => {
    const authResult = await loginUser({
      email: credentials.email,
      password: credentials.password,
    })

    if (credentials.selectedRole && authResult?.user?.role !== credentials.selectedRole) {
      throw new Error(`This account is registered as ${authResult?.user?.role || 'UNKNOWN'}, not ${credentials.selectedRole}.`)
    }

    const nextSession = {
      token: authResult.token,
      user: authResult.user,
    }

    persistSession(nextSession)
    setSession(nextSession)
    handleCloseAuth()
    navigate('/profile')
  }

  const handleSignup = async (data) => {
    const authResult = await registerUser(data)
    const nextSession = {
      token: authResult.token,
      user: authResult.user,
    }

    persistSession(nextSession)
    setSession(nextSession)
    handleCloseAuth()
    navigate('/profile')
  }

  const handleLogout = () => {
    clearSession()
    setSession(null)
    setPatientProfile(null)
    setProfileError('')
    navigate('/')
  }

  const handleProfileClick = () => {
    if (!session?.user) {
      handleOpenAuth('login')
      return
    }

    navigate('/profile')
  }

  const handlePatientProfileSave = async (profileData) => {
    if (!session?.token || !session?.user?.id) {
      return
    }

    setIsSavingProfile(true)
    setProfileError('')

    try {
      const updated = await updatePatientProfile({
        userId: session.user.id,
        token: session.token,
        profile: profileData,
      })
      setPatientProfile(updated)
    } catch (error) {
      setProfileError(error.message || 'Unable to update patient profile.')
    } finally {
      setIsSavingProfile(false)
    }
  }

  return (
    <div className="min-h-screen bg-[#fcfdfe] text-slate-900 flex flex-col">
      <Navbar
        user={session?.user || null}
        onLoginClick={() => handleOpenAuth('login')}
        onSignupClick={() => handleOpenAuth('signup')}
        onLogout={handleLogout}
        onProfileClick={handleProfileClick}
      />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route
          path="/profile"
          element={(
            <Profile
              user={session?.user || null}
              patientProfile={patientProfile}
              profileError={profileError}
              isSavingProfile={isSavingProfile}
              onSavePatientProfile={handlePatientProfileSave}
              onLoginClick={() => handleOpenAuth('login')}
              onSignupClick={() => handleOpenAuth('signup')}
            />
          )}
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <Footer />
      {authMode && (
        <AuthModal
          mode={authMode}
          onClose={handleCloseAuth}
          onLogin={handleLogin}
          onSignup={handleSignup}
        />
      )}
    </div>
  )
}

export default App
