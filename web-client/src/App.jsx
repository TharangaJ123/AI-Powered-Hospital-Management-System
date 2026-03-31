import { useEffect, useState } from 'react'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import AuthModal from './components/AuthModal'
import Home from './pages/Home'
import {
  clearSession,
  loginUser,
  persistSession,
  readSession,
  registerUser,
} from './services/auth'

function App() {
  const [authMode, setAuthMode] = useState(null)
  const [session, setSession] = useState(null)

  useEffect(() => {
    const existing = readSession()
    if (existing) {
      setSession(existing)
    }
  }, [])

  const handleOpenAuth = (mode) => {
    setAuthMode(mode)
  }

  const handleCloseAuth = () => {
    setAuthMode(null)
  }

  const handleLogin = async (credentials) => {
    const authResult = await loginUser(credentials)
    const nextSession = {
      token: authResult.token,
      user: authResult.user,
    }

    persistSession(nextSession)
    setSession(nextSession)
    handleCloseAuth()
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
  }

  const handleLogout = () => {
    clearSession()
    setSession(null)
  }

  return (
    <div className="min-h-screen bg-[#fcfdfe] text-slate-900 flex flex-col">
      <Navbar
        user={session?.user || null}
        onLoginClick={() => handleOpenAuth('login')}
        onSignupClick={() => handleOpenAuth('signup')}
        onLogout={handleLogout}
      />
      <Home />
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
