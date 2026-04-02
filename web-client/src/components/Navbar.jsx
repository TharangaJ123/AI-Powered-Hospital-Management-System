import { Phone, Search, Menu, User, MapPin, Clock, BrainCircuit, LogOut } from 'lucide-react'
import { useState } from 'react'
import { Link } from 'react-router-dom'

const Navbar = ({ user, onLoginClick, onSignupClick, onLogout, onProfileClick, onBookAppointmentClick }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const displayName = user?.name || user?.fullName || user?.username || user?.email || 'User'

  return (
    <header className="fixed top-0 w-full z-50">
      {/* Top Utility Bar */}
      <div className="bg-[#00a69c] text-white py-2 px-4 hidden md:block">
        <div className="max-w-7xl mx-auto flex justify-between items-center text-xs font-medium">
          <div className="flex items-center space-x-6">
            <div className="flex items-center space-x-2">
              <Phone className="w-3.5 h-3.5" />
              <span>Emergency: 1344 (24/7)</span>
            </div>
            <div className="flex items-center space-x-2 border-l border-white/30 pl-6">
              <MapPin className="w-3.5 h-3.5" />
              <span>Find Us</span>
            </div>
          </div>
          <div className="flex items-center space-x-6">
            <div className="flex items-center space-x-2">
              <Clock className="w-3.5 h-3.5" />
              <span>OPD: 8:00 AM - 8:00 PM</span>
            </div>
            <div className="flex items-center space-x-4">
              <a href="#" className="hover:opacity-80 transition-opacity underline decoration-white/30 underline-offset-4">Pay Online</a>
              <a href="#" className="hover:opacity-80 transition-opacity underline decoration-white/30 underline-offset-4">Lab Reports</a>
            </div>
          </div>
        </div>
      </div>

      {/* Main Navigation */}
      <nav className="bg-white/90 backdrop-blur-xl border-b border-slate-200/60 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 h-20 flex items-center justify-between">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-3 group cursor-pointer">
            <div className="bg-[#0066cc] p-2 rounded-xl shadow-[#0066cc]/20 shadow-lg transform group-hover:rotate-6 transition-transform">
              <BrainCircuit className="text-white w-7 h-7" />
            </div>
            <div className="flex flex-col">
              <span className="text-2xl font-black tracking-tighter text-slate-800 leading-none">
                OMNI<span className="text-[#0066cc]">HEALTH</span>
              </span>
              <span className="text-[10px] font-bold text-[#00a69c] tracking-[0.2em] uppercase mt-0.5">
                Excellence in Care
              </span>
            </div>
          </Link>

          {/* Desktop Menu */}
          <div className="hidden lg:flex items-center space-x-8 text-[14px] font-semibold text-slate-700">
            <Link to="/about" className="hover:text-[#0066cc] transition-colors relative group">
              About Us
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </Link>
            <a href="#" className="hover:text-[#0066cc] transition-colors relative group">
              Specialties
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </a>
            <a href="#" className="hover:text-[#0066cc] transition-colors relative group">
              Doctors
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </a>
            <a href="#" className="hover:text-[#0066cc] transition-colors relative group">
              Services
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </a>
            <Link to="/contact" className="hover:text-[#0066cc] transition-colors relative group">
              Contact
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </Link>
            {user?.role === 'ADMIN' && (
              <Link to="/admin" className="text-[#0066cc] font-bold relative group">
                Dashboard
                <span className="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#0066cc]"></span>
              </Link>
            )}
          </div>

          {/* Action Buttons */}
          <div className="flex items-center space-x-4">
            <button className="p-2 hover:bg-slate-100 rounded-full transition-colors hidden sm:block">
              <Search className="w-5 h-5 text-slate-600" />
            </button>
            <button 
              onClick={onBookAppointmentClick}
              className="btn-primary flex items-center space-x-2 text-sm px-6 py-2.5 hover:opacity-90 transition-opacity"
              title={user ? "Book an appointment" : "Login to book an appointment"}
            >
              <span>Book Appointment</span>
            </button>
            {user ? (
              <div className="hidden sm:flex items-center gap-2">
                <button
                  type="button"
                  className="w-10 h-10 rounded-full bg-[#0066cc]/10 border border-[#0066cc]/20 flex items-center justify-center text-[#0066cc] hover:bg-[#0066cc] hover:text-white transition-all shadow-inner"
                  onClick={onProfileClick}
                  aria-label="Go to profile"
                  title={`${displayName} (${(user?.role || 'PATIENT').toUpperCase()})`}
                >
                  <User className="w-5 h-5" />
                </button>
                <button
                  type="button"
                  onClick={onLogout}
                  className="inline-flex items-center justify-center gap-2 px-4 py-2 rounded-full border border-slate-200 text-sm font-semibold text-slate-700 hover:text-red-600 hover:border-red-200 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  Logout
                </button>
              </div>
            ) : (
              <div className="hidden sm:flex items-center gap-2">
                <button
                  type="button"
                  onClick={onLoginClick}
                  className="px-4 py-2 text-sm font-semibold rounded-full border border-slate-200 text-slate-700 hover:text-[#0066cc] hover:border-[#0066cc]/40 transition-colors"
                >
                  Login
                </button>
                <button
                  type="button"
                  onClick={onSignupClick}
                  className="btn-secondary text-sm px-4 py-2"
                >
                  Sign Up
                </button>
              </div>
            )}
            <button 
              className="lg:hidden p-2 text-slate-600"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              <Menu className="w-6 h-6" />
            </button>
          </div>
        </div>
      </nav>

      {/* Mobile Menu (Optional expansion) */}
      {isMenuOpen && (
        <div className="lg:hidden bg-white border-b border-slate-200 px-4 py-6 space-y-4 animate-in slide-in-from-top duration-300">
          <Link to="/about" className="block font-bold text-slate-800" onClick={() => setIsMenuOpen(false)}>About Us</Link>
          <a href="#" className="block font-bold text-slate-800">Specialties</a>
          <a href="#" className="block font-bold text-slate-800">Doctors</a>
          <a href="#" className="block font-bold text-slate-800">Services</a>
          <Link to="/contact" className="block font-bold text-slate-800" onClick={() => setIsMenuOpen(false)}>Contact</Link>
          {user?.role === 'ADMIN' && (
            <Link to="/admin" className="block font-bold text-[#0066cc]" onClick={() => setIsMenuOpen(false)}>Admin Dashboard</Link>
          )}
          {user ? (
            <div className="space-y-3">
              <button
                type="button"
                onClick={() => {
                  setIsMenuOpen(false)
                  onProfileClick()
                }}
                className="w-full text-left font-bold text-[#0066cc]"
              >
                My Profile
              </button>
              <button
                type="button"
                onClick={() => {
                  setIsMenuOpen(false)
                  onLogout()
                }}
                className="w-full text-left font-bold text-red-600"
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-3 pt-2">
              <button
                type="button"
                onClick={() => {
                  setIsMenuOpen(false)
                  onLoginClick()
                }}
                className="px-4 py-2 rounded-full border border-slate-300 text-sm font-semibold"
              >
                Login
              </button>
              <button
                type="button"
                onClick={() => {
                  setIsMenuOpen(false)
                  onSignupClick()
                }}
                className="btn-secondary text-sm px-4 py-2"
              >
                Sign Up
              </button>
            </div>
          )}
        </div>
      )}
    </header>
  )
}

export default Navbar

