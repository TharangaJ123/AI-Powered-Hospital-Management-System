import { 
  Calendar, 
  MapPin, 
  Search, 
  ArrowRight,
  Stethoscope,
  Activity,
  Heart,
  Baby,
  Brain,
  ShieldCheck,
  TrendingUp,
  Microscope,
  PhoneCall,
  ChevronRight,
  FlaskConical,
  CreditCard,
  History,
  Truck,
  Clock
} from 'lucide-react'
import { motion } from 'framer-motion'
import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'

const specialties = [
  { name: 'Cardiology', icon: Heart, desc: 'Advanced heart care services and specialized treatments.' },
  { name: 'Neurology', icon: Brain, desc: 'Expert care for complex brain and nervous system disorders.' },
  { name: 'Paediatrics', icon: Baby, desc: 'Comprehensive healthcare for infants, children, and teenagers.' },
  { name: 'Laboratory', icon: FlaskConical, desc: 'Precise and timely diagnostic testing and analysis.' },
  { name: 'Radiology', icon: Microscope, desc: 'High-tech imaging for accurate internal diagnostics.' },
  { name: 'Orthopaedics', icon: Activity, desc: 'Leading-edge bone, joint, and muscle care.' },
  { name: 'Ambulance', icon: Truck, desc: '24/7 high-speed life support emergency response fleet.' }
]

const specialtyToDoctorId = {
  Cardiology: 1,
  Neurology: 2,
  Paediatrics: 3,
  Laboratory: 4,
  Radiology: 5,
  Orthopaedics: 6,
  Ambulance: 7
}

const HeroSlide = ({ image, title, subtitle, ctaText, onPrimaryClick }) => (
  <div className="relative h-[85vh] w-full flex items-center overflow-hidden">
    <div className="absolute inset-0 z-0">
      <img src={image} className="w-full h-full object-cover brightness-50" alt="Hospital" />
      <div className="absolute inset-0 bg-gradient-to-r from-[#002d5a]/60 to-transparent"></div>
    </div>
    <div className="max-w-7xl mx-auto px-4 w-full relative z-10">
      <motion.div 
        initial={{ opacity: 0, x: -50 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.8 }}
        className="max-w-xl text-white space-y-6"
      >
        <span className="inline-block px-4 py-1.5 bg-[#00a69c]/80 backdrop-blur-md rounded-full text-xs font-bold uppercase tracking-widest">
          The Pinnacle of Medical Excellence
        </span>
        <h1 className="text-5xl md:text-7xl font-extrabold leading-tight tracking-tight">
          {title}
        </h1>
        <p className="text-lg text-slate-200">
          {subtitle}
        </p>
        <div className="flex flex-col sm:flex-row gap-8 pt-8">
          <button onClick={onPrimaryClick} className="btn-primary flex items-center justify-center space-x-2 px-8">
            <span>{ctaText}</span>
            <ChevronRight className="w-4 h-4" />
          </button>
          <button className="px-8 py-3 border-2 border-white text-white rounded-full font-semibold hover:bg-white/10 transition-all flex items-center justify-center">
            View Documentation
          </button>
        </div>
      </motion.div>
    </div>
  </div>
)

const Home = ({ onBookAppointment, user }) => {
  const navigate = useNavigate()
  const [activeSlide, setActiveSlide] = useState(0)
  const [bookingForm, setBookingForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    specialty: '',
    date: '',
    time: ''
  })
  const [isSubmitted, setIsSubmitted] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState('')
  const [bookingReference, setBookingReference] = useState(null)
  const isSubmittingRef = useRef(false)

  const slides = [
    {
      image: "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?q=80&w=2053&auto=format&fit=crop",
      title: "World-Class Healthcare, Reimagined.",
      subtitle: "Experience the next generation of AI-powered healthcare in a patient-centered environment.",
      ctaText: "Book an Appointment"
    },
    {
      image: "https://images.unsplash.com/photo-1516549655169-df83a0774514?q=80&w=2070&auto=format&fit=crop",
      title: "Leading Edge Medical Research.",
      subtitle: "State-of-the-art laboratory facilities and clinical research centers for precise diagnostics.",
      ctaText: "Find a Doctor"
    }
  ]

  useEffect(() => {
    const timer = setInterval(() => {
      setActiveSlide((prev) => (prev + 1) % slides.length)
    }, 8000)
    return () => clearInterval(timer)
  }, [])

  const handleQuickAccessClick = (label) => {
    if (label === 'Book Appointment' && onBookAppointment) {
      onBookAppointment()
    } else if (label === 'Find a Doctor') {
      navigate('/doctors')
    }
  }

  useEffect(() => {
    if (user) {
      setBookingForm(prev => ({
        ...prev,
        fullName: user.name || '',
        email: user.email || '',
      }))
    }
  }, [user])

  const handleBookingInputChange = (event) => {
    const { name, value } = event.target
    setBookingForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleBookingSubmit = async (event) => {
    event.preventDefault()
    if (isSubmittingRef.current) {
      return
    }

    isSubmittingRef.current = true
    setIsSubmitted(false)
    setSubmitError('')
    setBookingReference(null)
    setIsSubmitting(true)

    try {
      const patientId = Number(localStorage.getItem('patientId') || 1)
      const doctorId = specialtyToDoctorId[bookingForm.specialty] || 1
      const response = await fetch('/api/appointments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          patientId,
          doctorId,
          fullName: bookingForm.fullName,
          email: bookingForm.email,
          phoneNumber: bookingForm.phone,
          appointmentDate: `${bookingForm.date}T${bookingForm.time}:00`,
          reason: "Quick Booking via Home Page",
          consultationType: (bookingForm.specialty || "General") + " Consultation"
        })
      })

      if (!response.ok) {
        if (response.status === 409) {
          throw new Error('DUPLICATE_APPOINTMENT')
        }
        throw new Error(`Booking failed with status ${response.status}`)
      }

      const savedAppointment = await response.json()
      setBookingReference(savedAppointment?.id ?? null)
      setIsSubmitted(true)
      setBookingForm({
        fullName: '',
        phone: '',
        specialty: '',
        date: '',
        time: ''
      })
    } catch (error) {
      if (error.message === 'DUPLICATE_APPOINTMENT') {
        setSubmitError('This appointment already exists for the selected date and time. Please choose a different slot.')
      } else {
        setSubmitError('Could not save appointment to server. Please check backend services and try again.')
      }
      setIsSubmitted(false)
    } finally {
      isSubmittingRef.current = false
      setIsSubmitting(false)
    }
  }

  return (
    <div className="relative">
      {/* Hero Section */}
      <section className="pt-28 md:pt-32">
        <HeroSlide
          {...slides[activeSlide]}
          onPrimaryClick={
            slides[activeSlide].ctaText === 'Book an Appointment' 
              ? onBookAppointment 
              : () => navigate('/doctors')
          }
        />
      </section>

      {/* Quick Access Tiles */}
      <section className="max-w-7xl mx-auto px-4 -mt-12 relative z-20">
        <div className="grid grid-cols-2 md:grid-cols-5 gap-0 shadow-2xl rounded-3xl overflow-hidden glass-card">
          {[
            { icon: Calendar, label: 'Book Appointment', bg: 'bg-white hover:bg-slate-50' },
            { icon: Search, label: 'Find a Doctor', bg: 'bg-white hover:bg-slate-50' },
            { icon: Truck, label: 'Ambulance Support', bg: 'bg-white hover:bg-slate-50' },
            { icon: History, label: 'Medical History', bg: 'bg-white hover:bg-slate-50' },
            { icon: PhoneCall, label: 'Emergency Help', bg: 'bg-[#e53e3e] text-white hover:bg-red-700' }
          ].map((item, idx) => (
            <button
              key={idx}
              onClick={() => handleQuickAccessClick(item.label)}
              className={`${item.bg} flex flex-col items-center justify-center py-10 px-4 border-r border-slate-100 last:border-0 transition-all group`}
            >
              <div className={`p-4 rounded-full mb-3 group-hover:scale-110 transition-transform ${item.label === 'Emergency Help' ? 'bg-white/20' : 'bg-[#0066cc]/10'}`}>
                <item.icon className={`w-8 h-8 ${item.label === 'Emergency Help' ? 'text-white' : 'text-[#0066cc]'}`} />
              </div>
              <span className="text-sm font-bold text-center leading-tight">{item.label}</span>
            </button>
          ))}
        </div>
      </section>

      {/* Why Choose Us / Statistics */}
      <section className="section-padding max-w-7xl mx-auto grid lg:grid-cols-2 gap-16 items-center">
        <div className="space-y-8">
          <div>
            <span className="text-[#00a69c] font-bold tracking-widest text-sm uppercase mb-2 block">Our Commitment</span>
            <h2 className="text-4xl font-extrabold text-[#002d5a]">Redefining the Future of Hospital Care</h2>
          </div>
          <p className="text-slate-600 leading-relaxed text-lg">
            Our AI-powered ecosystem integrates every aspect of hospital management, from intelligent scheduling to real-time clinical diagnostics, ensuring a seamless experience for both medical staff and patients.
          </p>
          <div className="grid grid-cols-2 gap-8 pt-4">
            <div className="border-l-4 border-[#0066cc] pl-6 py-2">
              <h4 className="text-4xl font-black text-[#0066cc]">500+</h4>
              <p className="text-sm font-bold text-slate-500 uppercase tracking-tighter">Specialist Doctors</p>
            </div>
            <div className="border-l-4 border-[#00a69c] pl-6 py-2">
              <h4 className="text-4xl font-black text-[#00a69c]">25k+</h4>
              <p className="text-sm font-bold text-slate-500 uppercase tracking-tighter">Successful Surgeries</p>
            </div>
          </div>
          <button className="btn-secondary">Explore Our Innovations</button>
        </div>
        <div className="relative group">
          <div className="absolute -inset-4 bg-[#0066cc]/10 rounded-[2rem] transform group-hover:rotate-3 transition-all"></div>
          <img 
            src="/ai-care.png" 
            className="relative rounded-[2rem] shadow-2xl h-[500px] w-full object-cover border-8 border-white" 
            alt="AI-Powered Hospital Excellence" 
          />
        </div>
      </section>

      {/* Specialties Section */}
      <section className="bg-slate-50 section-padding">
        <div className="max-w-7xl mx-auto space-y-12">
          <div className="text-center space-y-4">
            <h2 className="text-4xl font-extrabold text-[#002d5a]">Centres of Excellence</h2>
            <p className="text-slate-500 max-w-2xl mx-auto text-lg leading-relaxed">
              We specialize in providing world-class medical services across several domains, utilizing the latest innovations in clinical science.
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {specialties.map((specialty, idx) => (
              <motion.div 
                key={idx}
                whileHover={{ y: -10 }}
                className="bg-white p-8 rounded-3xl border border-slate-100 shadow-sm hover:shadow-xl transition-all cursor-pointer group"
              >
                <div className="w-16 h-16 bg-[#0066cc]/5 rounded-2xl flex items-center justify-center mb-8 group-hover:bg-[#0066cc] transition-colors">
                  <specialty.icon className="w-8 h-8 text-[#0066cc] group-hover:text-white transition-colors" />
                </div>
                <h3 className="text-xl font-bold text-[#002d5a] mb-4">{specialty.name}</h3>
                <p className="text-slate-500 text-sm leading-relaxed mb-6">{specialty.desc}</p>
                <div className="flex items-center text-[#00a69c] font-bold text-sm tracking-tight">
                  Learn More <ArrowRight className="ml-2 w-4 h-4 group-hover:translate-x-2 transition-transform" />
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* AI Intelligence Monitoring Section (Keeping the core theme of the user request) */}
      <section className="section-padding bg-white">
        <div className="max-w-7xl mx-auto bg-slate-900 rounded-[3rem] p-12 text-white relative overflow-hidden group">
          <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-[#0066cc]/20 rounded-full blur-[100px] -mr-64 -mt-64 group-hover:bg-[#0066cc]/30 transition-all duration-700"></div>
          <div className="grid lg:grid-cols-2 gap-16 items-center relative z-10">
            <div className="space-y-8">
              <div className="inline-flex items-center space-x-2 px-4 py-2 bg-blue-500/20 border border-blue-500/30 rounded-full text-blue-400 text-xs font-black uppercase tracking-widest">
                <Activity className="w-4 h-4 animate-pulse" />
                <span>Live Intelligence Feed</span>
              </div>
              <h2 className="text-4xl font-extrabold leading-tight">Secure Microservices Architecture</h2>
              <p className="text-slate-400 text-lg">
                The OMNIHEALTH ecosystem is built on a distributed microservices architecture, ensuring high availability, peak performance, and enterprise-grade security for your medical data.
              </p>
              <div className="flex space-x-8">
                <div className="flex items-center space-x-2">
                  <ShieldCheck className="text-green-400 w-6 h-6" />
                  <span className="text-sm font-bold uppercase tracking-widest text-slate-300">Data Encrypted</span>
                </div>
                <div className="flex items-center space-x-2">
                  <TrendingUp className="text-[#00a69c] w-6 h-6" />
                  <span className="text-sm font-bold uppercase tracking-widest text-slate-300">Auto-Scaling</span>
                </div>
              </div>
            </div>
            <div className="bg-white/5 border border-white/10 p-8 rounded-3xl backdrop-blur-sm shadow-2xl">
              <div className="flex justify-between items-center mb-6 border-b border-white/10 pb-4">
                <span className="text-xs font-black tracking-widest text-blue-400 uppercase">System Heartbeat</span>
                <div className="flex items-center space-x-2">
                   <div className="w-2 h-2 bg-green-500 rounded-full animate-ping"></div>
                   <span className="text-[10px] text-slate-400 font-bold uppercase">Optimal</span>
                </div>
              </div>
              <div className="space-y-3 font-mono text-[11px] text-slate-400">
                <p className="flex justify-between"><span className="text-blue-200">{"[AUTH_SRV]"}</span> <span>Status: Healthy</span></p>
                <p className="flex justify-between"><span className="text-[#00a69c]">{"[DOCTOR_API]"}</span> <span>Latency: 24ms</span></p>
                <p className="flex justify-between"><span className="text-amber-200">{"[AI_DIAGNOSTICS]"}</span> <span>Processing...</span></p>
                <div className="h-2 w-full bg-slate-800 rounded-full mt-4 overflow-hidden">
                  <motion.div 
                    initial={{ width: 0 }}
                    animate={{ width: "85%" }}
                    transition={{ duration: 1.5, repeat: Infinity, repeatType: "reverse" }}
                    className="h-full bg-gradient-to-r from-blue-600 to-[#00a69c]"
                  ></motion.div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Ambulance Section */}
      <section className="section-padding bg-white overflow-hidden">
        <div className="max-w-7xl mx-auto">
          <div className="bg-[#e53e3e]/[0.03] border border-red-100 rounded-[4rem] p-12 md:p-20 relative overflow-hidden group">
            <div className="absolute top-0 right-0 w-[800px] h-[800px] bg-red-500/5 rounded-full blur-[120px] -mr-96 -mt-96 group-hover:bg-red-500/10 transition-all duration-1000"></div>
            
            <div className="grid lg:grid-cols-2 gap-20 items-center relative z-10">
              <div className="order-2 lg:order-1 relative">
                <div className="absolute -inset-6 bg-red-500/10 rounded-[3rem] blur-2xl animate-pulse"></div>
                <img 
                  src="/amb.png" 
                  className="relative w-full h-auto object-contain transform group-hover:scale-105 transition-all duration-700" 
                  alt="High-Speed Ambulance Service" 
                />
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[120%] h-[120%] border-2 border-red-500/5 rounded-full animate-spin-slow"></div>
              </div>

              <div className="order-1 lg:order-2 space-y-8">
                <div className="inline-flex items-center space-x-3 px-5 py-2 bg-red-500/10 border border-red-500/20 rounded-full text-[#e53e3e] text-xs font-black uppercase tracking-widest">
                  <span className="w-2 h-2 bg-[#e53e3e] rounded-full animate-ping"></span>
                  <span>Emergency Response Unit (ERU)</span>
                </div>
                
                <h2 className="text-5xl md:text-6xl font-black text-[#002d5a] leading-[1.1]">
                  Life-Link <br/>
                  <span className="text-[#e53e3e]">Ambulance</span> Fleet
                </h2>
                
                <p className="text-slate-600 text-lg leading-relaxed font-medium">
                  Our state-of-the-art ambulance fleet acts as a mobile ICU, equipped with high-performance ventilators, defibrillators, and advanced life-support monitoring systems. 
                  <span className="block mt-4 text-[#002d5a] font-bold">Guaranteed arrival within 10-15 minutes across any city location.</span>
                </p>

                <div className="grid sm:grid-cols-2 gap-8 py-6">
                  <div className="flex items-start space-x-4">
                    <div className="w-10 h-10 bg-white rounded-xl shadow-lg flex items-center justify-center text-[#e53e3e] flex-shrink-0">
                      <Clock className="w-5 h-5" />
                    </div>
                    <div>
                      <h4 className="font-black text-[#002d5a] text-sm uppercase tracking-tight">24/7 Rapid Reach</h4>
                      <p className="text-slate-500 text-xs mt-1 font-medium">Strategic dispatch units across the city.</p>
                    </div>
                  </div>
                  <div className="flex items-start space-x-4">
                    <div className="w-10 h-10 bg-white rounded-xl shadow-lg flex items-center justify-center text-[#e53e3e] flex-shrink-0">
                      <ShieldCheck className="w-5 h-5" />
                    </div>
                    <div>
                      <h4 className="font-black text-[#002d5a] text-sm uppercase tracking-tight">ICU Standard</h4>
                      <p className="text-slate-500 text-xs mt-1 font-medium">Certified paramedics on every dispatch.</p>
                    </div>
                  </div>
                </div>

                <div className="pt-6">
                  <a 
                    href="tel:1344"
                    className="inline-flex items-center space-x-4 bg-[#e53e3e] text-white px-10 py-5 rounded-[2rem] font-black text-sm uppercase tracking-widest shadow-2xl shadow-red-500/40 hover:bg-[#c53030] hover:scale-105 active:scale-95 transition-all group"
                  >
                    <PhoneCall className="w-5 h-5 group-hover:rotate-12 transition-transform" />
                    <span>Emergency Hotline: 1344</span>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Appointment Booking Section */}
      <section id="appointment-section" className="section-padding bg-slate-50 scroll-mt-32">
        <div className="max-w-7xl mx-auto grid lg:grid-cols-2 gap-10 items-start">
          <div className="space-y-5">
            <span className="text-[#00a69c] font-bold tracking-widest text-sm uppercase block">Appointment Desk</span>
            <h2 className="text-4xl font-extrabold text-[#002d5a] leading-tight">Book an Appointment in Seconds</h2>
            <p className="text-slate-600 text-lg leading-relaxed">
              Reserve your visit with your preferred specialty. A member of our team will confirm your appointment shortly.
            </p>
            <div className="bg-white border border-slate-100 rounded-2xl p-5 shadow-sm">
              <p className="text-sm text-slate-500 font-semibold">Need urgent help?</p>
              <p className="text-2xl text-[#e53e3e] font-extrabold tracking-tight mt-1">Call 1344 now</p>
            </div>
          </div>

          <form onSubmit={handleBookingSubmit} className="bg-white rounded-3xl border border-slate-100 shadow-lg p-8 space-y-5">
            <div className="grid sm:grid-cols-2 gap-4">
              <div className="sm:col-span-2">
                <label htmlFor="fullName" className="text-sm font-bold text-slate-700">Full Name</label>
                <input
                  id="fullName"
                  name="fullName"
                  value={bookingForm.fullName}
                  onChange={handleBookingInputChange}
                  required
                  className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-[#0066cc]"
                  placeholder="Enter your full name"
                />
              </div>

              <div className="sm:col-span-2">
                <label htmlFor="email" className="text-sm font-bold text-slate-700">Email Address</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={bookingForm.email}
                  onChange={handleBookingInputChange}
                  required
                  className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-[#0066cc]"
                  placeholder="Enter your email"
                />
              </div>

              <div>
                <label htmlFor="phone" className="text-sm font-bold text-slate-700">Phone Number</label>
                <input
                  id="phone"
                  name="phone"
                  type="tel"
                  value={bookingForm.phone}
                  onChange={handleBookingInputChange}
                  required
                  className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-[#0066cc]"
                  placeholder="07x xxx xxxx"
                />
              </div>

              <div>
                <label htmlFor="specialty" className="text-sm font-bold text-slate-700">Specialty</label>
                <select
                  id="specialty"
                  name="specialty"
                  value={bookingForm.specialty}
                  onChange={handleBookingInputChange}
                  required
                  className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-[#0066cc]"
                >
                  <option value="">Select specialty</option>
                  {specialties.map((specialty) => (
                    <option key={specialty.name} value={specialty.name}>{specialty.name}</option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="date" className="text-sm font-bold text-slate-700">Date</label>
                <input
                  id="date"
                  name="date"
                  type="date"
                  value={bookingForm.date}
                  onChange={handleBookingInputChange}
                  required
                  min={new Date().toISOString().split('T')[0]}
                  className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-[#0066cc]"
                />
              </div>

              <div>
                <label htmlFor="time" className="text-sm font-bold text-slate-700">Preferred Time</label>
                <input
                  id="time"
                  name="time"
                  type="time"
                  value={bookingForm.time}
                  onChange={handleBookingInputChange}
                  required
                  className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-3 outline-none focus:border-[#0066cc]"
                />
              </div>
            </div>

            <button type="submit" disabled={isSubmitting} className="btn-primary w-full py-3.5 text-center disabled:opacity-70 disabled:cursor-not-allowed">
              {isSubmitting ? 'Saving Appointment...' : 'Confirm Appointment'}
            </button>

            {isSubmitted && (
              <p className="text-sm font-semibold text-green-700 bg-green-50 border border-green-200 rounded-xl px-4 py-3">
                Appointment request saved successfully{bookingReference ? ` (Ref #${bookingReference})` : ''}. Our team will contact you soon.
              </p>
            )}

            {submitError && (
              <p className="text-sm font-semibold text-red-700 bg-red-50 border border-red-200 rounded-xl px-4 py-3">
                {submitError}
              </p>
            )}
          </form>
        </div>
      </section>

      {/* Floating Action Button */}
      <div className="fixed bottom-8 right-8 z-50">
        <button className="bg-green-500 text-white w-16 h-16 rounded-full flex items-center justify-center shadow-2xl hover:scale-110 transition-transform active:scale-95 group relative">
          <PhoneCall className="w-7 h-7" />
          <span className="absolute -top-12 right-0 bg-white text-slate-900 text-xs px-3 py-1.5 rounded-lg shadow-lg opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap font-bold">
            Emergency Help!
          </span>
        </button>
      </div>
    </div>
  )
}

export default Home

