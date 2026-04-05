import { useEffect, useState } from 'react'
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import AuthModal from './components/AuthModal'
import Home from './pages/Home'
import Profile from './pages/Profile'
import Appointments from './pages/Appointments'
import AboutUs from './pages/AboutUs'
import Contact from './pages/Contact'
import AdminDashboard from './pages/AdminDashboard'
import {
  clearSession,
  getPatientProfile,
  loginUser,
  persistSession,
  readSession,
  registerUser,
  updatePatientProfile,
} from './services/auth'
import {
  getDoctorByUserId,
  createDoctorProfile,
  updateDoctorProfile
} from './services/doctors'
import Doctors from './pages/Doctors'
import Services from './pages/Services'
import DoctorDetail from './pages/DoctorDetail'
import SymptomChecker from './pages/SymptomChecker'

function App() {
  const navigate = useNavigate()
  const [authMode, setAuthMode] = useState(null)
  const [session, setSession] = useState(null)
  const [patientProfile, setPatientProfile] = useState(null)
  const [doctorProfile, setDoctorProfile] = useState(null)
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

  useEffect(() => {
    const loadDoctor = async () => {
      if (!session?.token || session?.user?.role !== 'DOCTOR' || !session?.user?.id) {
        setDoctorProfile(null)
        return
      }

      try {
        const profile = await getDoctorByUserId(session.user.id)
        setDoctorProfile(profile)
      } catch (error) {
        console.error('Failed to load doctor profile:', error)
      }
    }

    loadDoctor()
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

    if (nextSession.user.role === 'ADMIN') {
      navigate('/admin')
    } else {
      navigate('/profile')
    }
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

    if (nextSession.user.role === 'ADMIN') {
      navigate('/admin')
    } else {
      navigate('/')
    }
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

  const handleBookAppointmentClick = () => {
    // Navigate to appointments page (allow guest access)
    navigate('/appointments')
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

  const handleDoctorProfileSave = async (profileData) => {
    if (!session?.token || !session?.user?.id) {
      return
    }

    setIsSavingProfile(true)
    setProfileError('')

    try {
      let updated
      if (doctorProfile?.id) {
        updated = await updateDoctorProfile(doctorProfile.id, profileData, session.token)
      } else {
        updated = await createDoctorProfile({ ...profileData, userId: session.user.id }, session.token)
      }
      setDoctorProfile(updated)
    } catch (error) {
      setProfileError(error.message || 'Unable to update doctor profile.')
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
        onBookAppointmentClick={handleBookAppointmentClick}
      />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<AboutUs />} />
        <Route 
          path="/contact" 
          element={
            <Contact 
              user={session?.user} 
              patientProfile={patientProfile} 
            />
          } 
        />
        <Route path="/services" element={<Services />} />
        <Route path="/admin" element={<AdminDashboard token={session?.token} />} />
        <Route
          path="/profile"
          element={(
            <Profile
              user={session?.user || null}
              token={session?.token}
              patientProfile={patientProfile}
              profileError={profileError}
              isSavingProfile={isSavingProfile}
              onSavePatientProfile={handlePatientProfileSave}
              doctorProfile={doctorProfile}
              onSaveDoctorProfile={handleDoctorProfileSave}
              onLoginClick={() => handleOpenAuth('login')}
              onSignupClick={() => handleOpenAuth('signup')}
            />
          )}
        />
        <Route path="/doctors" element={<Doctors />} />
        <Route path="/doctors/:id" element={<DoctorDetail />} />
        <Route path="/ai-symptom-checker" element={<SymptomChecker />} />
        <Route
          path="/appointments"
          element={(
            <Appointments
              user={session?.user || null}
              patientProfile={patientProfile}
              onLoginClick={() => handleOpenAuth('login')}
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
