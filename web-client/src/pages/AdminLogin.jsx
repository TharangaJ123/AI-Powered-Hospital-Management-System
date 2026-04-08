import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ShieldCheck, LoaderCircle, CircleAlert, BrainCircuit } from 'lucide-react'
import { loginAdmin, persistSession } from '../services/auth'

const AdminLogin = ({ setSession }) => {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setIsLoading(true)

    try {
      const authResult = await loginAdmin({ email, password })
      
      const session = {
        token: authResult.token,
        user: authResult.user,
      }

      persistSession(session)
      setSession(session)
      navigate('/admin')
    } catch (err) {
      setError(err.message || 'Invalid admin credentials.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="inline-flex bg-[#0066cc] p-3 rounded-2xl shadow-lg shadow-[#0066cc]/20 mb-4">
            <BrainCircuit className="text-white w-8 h-8" />
          </div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tighter">
            ADMIN<span className="text-[#0066cc]">PORTAL</span>
          </h1>
          <p className="text-slate-500 font-medium mt-2">Authorized Personnel Only</p>
        </div>

        <div className="bg-white rounded-3xl shadow-xl border border-slate-200 overflow-hidden">
          <div className="bg-gradient-to-br from-[#002d5a] to-[#0066cc] p-6 text-white">
            <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-[10px] uppercase tracking-wider bg-white/10 border border-white/20 font-bold">
              <ShieldCheck className="w-3.5 h-3.5" />
              Secure Login
            </span>
            <h2 className="text-xl font-bold mt-3">Staff Authentication</h2>
          </div>

          <form onSubmit={handleSubmit} className="p-8 space-y-6">
            {error && (
              <div className="flex items-start gap-3 p-4 rounded-2xl bg-red-50 border border-red-100 text-sm text-red-700">
                <CircleAlert className="w-5 h-5 shrink-0 mt-0.5" />
                <p className="font-medium">{error}</p>
              </div>
            )}

            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-2 ml-1" htmlFor="email">
                  Admin Email
                </label>
                <input
                  id="email"
                  type="email"
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-[#0066cc] focus:border-transparent outline-none transition-all font-medium"
                  placeholder="admin@omnihealth.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-2 ml-1" htmlFor="password">
                  Password
                </label>
                <input
                  id="password"
                  type="password"
                  className="w-full px-5 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-2 focus:ring-[#0066cc] focus:border-transparent outline-none transition-all font-medium"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-[#0066cc] hover:bg-[#0052a3] text-white font-bold py-4 rounded-2xl shadow-lg shadow-[#0066cc]/25 transition-all flex items-center justify-center gap-3 disabled:opacity-70"
            >
              {isLoading ? (
                <>
                  <LoaderCircle className="w-5 h-5 animate-spin" />
                  Authenticating...
                </>
              ) : (
                'Access Dashboard'
              )}
            </button>
          </form>
        </div>

        <div className="mt-8 text-center">
          <button 
            onClick={() => navigate('/')}
            className="text-slate-400 hover:text-[#0066cc] text-sm font-bold transition-colors"
          >
            ← Back to Main Site
          </button>
        </div>
      </div>
    </div>
  )
}

export default AdminLogin
