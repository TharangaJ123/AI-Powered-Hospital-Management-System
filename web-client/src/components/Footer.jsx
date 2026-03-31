import { Facebook, Twitter, Instagram, Linkedin, Mail, Phone, MapPin, BrainCircuit } from 'lucide-react'

const Footer = () => {
    return (
      <footer className="bg-slate-900 text-slate-300 pt-20 pb-10 px-4 mt-auto">
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mb-16">
          {/* Brand Column */}
          <div className="space-y-6">
            <div className="flex items-center space-x-3">
              <div className="bg-[#0066cc] p-2 rounded-xl">
                <BrainCircuit className="text-white w-6 h-6" />
              </div>
              <span className="text-xl font-black tracking-tighter text-white uppercase">
                OMNI<span className="text-[#0066cc]">HEALTH</span>
              </span>
            </div>
            <p className="text-sm leading-relaxed text-slate-400">
              Leading the way in medical excellence with AI-powered diagnostics and compassionate patient care. We are committed to providing the highest standards of healthcare to our community.
            </p>
            <div className="flex space-x-4">
              <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-[#0066cc] hover:text-white transition-all transform hover:-translate-y-1">
                <Facebook className="w-5 h-5" />
              </a>
              <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-[#0066cc] hover:text-white transition-all transform hover:-translate-y-1">
                <Twitter className="w-5 h-5" />
              </a>
              <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-[#0066cc] hover:text-white transition-all transform hover:-translate-y-1">
                <Instagram className="w-5 h-5" />
              </a>
              <a href="#" className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center hover:bg-[#0066cc] hover:text-white transition-all transform hover:-translate-y-1">
                <Linkedin className="w-5 h-5" />
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-white font-bold mb-6 border-l-4 border-[#00a69c] pl-4">Quick Links</h4>
            <ul className="space-y-4 text-sm">
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Find a Doctor</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Book an Appointment</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Health Check Packages</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Laboratory Services</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Telemedicine Call</a></li>
            </ul>
          </div>

          {/* Specialties */}
          <div>
            <h4 className="text-white font-bold mb-6 border-l-4 border-[#00a69c] pl-4">Centres of Excellence</h4>
            <ul className="space-y-4 text-sm">
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Cardiology</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Neurology</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Orthopaedics</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Oncology</a></li>
              <li><a href="#" className="hover:text-[#00a69c] transition-colors">Paediatrics</a></li>
            </ul>
          </div>

          {/* Contact Info */}
          <div>
            <h4 className="text-white font-bold mb-6 border-l-4 border-[#00a69c] pl-4">Contact Us</h4>
            <div className="space-y-4 text-sm">
              <div className="flex items-start space-x-3">
                <MapPin className="w-5 h-5 text-[#0066cc] shrink-0" />
                <p>123 Medical Avenue, Healthcare City, Colombo 03, Sri Lanka</p>
              </div>
              <div className="flex items-center space-x-3">
                <Phone className="w-5 h-5 text-[#0066cc] shrink-0" />
                <p>+94 11 234 5678</p>
              </div>
              <div className="flex items-center space-x-3">
                <Mail className="w-5 h-5 text-[#0066cc] shrink-0" />
                <p>info@omnihealth.com</p>
              </div>
              <div className="mt-6 pt-6 border-t border-slate-800">
                <p className="text-[#e53e3e] font-bold flex items-center space-x-2">
                  <span className="w-2 h-2 bg-[#e53e3e] rounded-full animate-ping"></span>
                  <span>Emergency Hotline: 1344</span>
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="max-w-7xl mx-auto pt-8 border-t border-slate-800 flex flex-col md:flex-row justify-between items-center text-xs text-slate-500">
          <p>© 2026 OMNIHEALTH Hospital Management System. All Rights Reserved.</p>
          <div className="flex space-x-6 mt-4 md:mt-0">
            <a href="#" className="hover:text-white transition-colors">Privacy Policy</a>
            <a href="#" className="hover:text-white transition-colors">Terms of Service</a>
            <a href="#" className="hover:text-white transition-colors">Cookies Settings</a>
          </div>
        </div>
      </footer>
    )
  }
  
  export default Footer

  
