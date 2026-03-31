import { Phone, Search, Menu, User, MapPin, Clock, BrainCircuit } from 'lucide-react'
import { useState } from 'react'

const Navbar = ({ onBookAppointment }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false)

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
          <div className="flex items-center space-x-3 group cursor-pointer">
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
          </div>

          {/* Desktop Menu */}
          <div className="hidden lg:flex items-center space-x-8 text-[14px] font-semibold text-slate-700">
            <a href="#" className="hover:text-[#0066cc] transition-colors relative group">
              About Us
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </a>
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
            <a href="#" className="hover:text-[#0066cc] transition-colors relative group">
              Contact
              <span className="absolute bottom-[-4px] left-0 w-0 h-0.5 bg-[#0066cc] transition-all group-hover:w-full"></span>
            </a>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center space-x-4">
            <button className="p-2 hover:bg-slate-100 rounded-full transition-colors hidden sm:block">
              <Search className="w-5 h-5 text-slate-600" />
            </button>
            <button onClick={onBookAppointment} className="btn-primary flex items-center space-x-2 text-sm px-6 py-2.5">
              <span>Book Appointment</span>
            </button>
            <div className="w-10 h-10 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-500 hover:bg-[#0066cc] hover:text-white transition-all cursor-pointer shadow-inner">
              <User className="w-5 h-5" />
            </div>
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
          <a href="#" className="block font-bold text-slate-800">About Us</a>
          <a href="#" className="block font-bold text-slate-800">Specialties</a>
          <a href="#" className="block font-bold text-slate-800">Doctors</a>
          <a href="#" className="block font-bold text-slate-800">Services</a>
          <a href="#" className="block font-bold text-slate-800">Contact</a>
        </div>
      )}
    </header>
  )
}

export default Navbar

