import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import {
  Stethoscope,
  MapPin,
  Star,
  Clock,
  Calendar,
  Award,
  ShieldCheck,
  MessageCircle,
  Video,
  ChevronLeft,
  Search,
  CheckCircle2,
  TrendingUp,
  Activity,
  Heart,
  PhoneCall,
  ArrowRight
} from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'
import { getDoctorById } from '../services/doctors'
import { getAppointmentsByDoctor } from '../services/appointments'
import { getDoctorStats, getDoctorReviews } from '../services/reviews'

const DoctorDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [doctor, setDoctor] = useState(null)
  const [appointments, setAppointments] = useState([])
  const [stats, setStats] = useState({ averageRating: 0.0, totalReviews: 0 })
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    window.scrollTo(0, 0)
    fetchDoctor()
  }, [id])

  const fetchDoctor = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await getDoctorById(id)
      setDoctor(data)

      // Fetch appointments in a separate try-catch to avoid hiding the whole profile on failure
      try {
        if (token) {
          const appts = await getAppointmentsByDoctor(id, token)
          setAppointments(appts.filter(a => ['APPROVED', 'SCHEDULED', 'COMPLETED'].includes(a.status)))
        }

        try {
          const fetchedStats = await getDoctorStats(id)
          const fetchedReviews = await getDoctorReviews(id)
          setStats(fetchedStats)
          setReviews(fetchedReviews)
        } catch (reviewErr) {
          console.warn('Could not fetch doctor reviews:', reviewErr)
        }
      } catch (apptErr) {
        console.warn('Could not fetch doctor schedule:', apptErr)
      }

      // Fetch reviews
      try {
        const reviewsData = await getDoctorReviews(id)
        setReviews(reviewsData)
      } catch (reviewErr) {
        console.warn('Could not fetch doctor reviews:', reviewErr)
      }
    } catch (err) {
      setError('Unable to load doctor profile. Please verify that the specialist directory service is online.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 pt-20">
        <div className="flex flex-col items-center space-y-4">
          <div className="w-16 h-16 border-4 border-[#0066cc] border-t-transparent rounded-full animate-spin"></div>
          <p className="text-slate-500 font-bold uppercase tracking-widest text-xs">Loading Specialist Profile...</p>
        </div>
      </div>
    )
  }

  if (error || !doctor) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 pt-20 px-4 text-center">
        <div className="w-24 h-24 bg-red-100 rounded-full flex items-center justify-center mb-8">
          <Search className="w-10 h-10 text-red-500" />
        </div>
        <h2 className="text-3xl font-extrabold text-[#002d5a] mb-4">Specialist Profile Not Found</h2>
        <p className="text-slate-500 max-w-md mx-auto mb-8 font-medium">The requested doctor profile might have been archived or moved. Please search the directory again.</p>
        <Link
          to="/doctors"
          className="px-10 py-4 bg-[#002d5a] text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-[#003d7a] transition-all"
        >
          Return to Directory
        </Link>
      </div>
    )
  }

  const fullName = `Dr. ${doctor.firstName} ${doctor.lastName}`
  const displayImage = doctor.profilePhotoUrl || `https://ui-avatars.com/api/?name=${doctor.firstName}+${doctor.lastName}&background=0D8ABC&color=fff&size=512`

  return (
    <div className="min-h-screen bg-slate-50 pt-24 md:pt-32 pb-20">
      <div className="max-w-7xl mx-auto px-4">
        {/* Breadcrumb */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center space-x-2 text-slate-500 hover:text-[#0066cc] font-bold text-xs uppercase tracking-widest mb-10 transition-colors group"
        >
          <ChevronLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
          <span>Back to Results</span>
        </button>

        <div className="grid lg:grid-cols-12 gap-12 items-start">
          {/* Left Column: Profile Card */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="lg:col-span-4 space-y-8"
          >
            <div className="bg-white rounded-[3rem] border border-slate-100 shadow-xl overflow-hidden">
              <div className="relative h-80 overflow-hidden">
                <img src={displayImage} className="w-full h-full object-cover" alt={fullName} />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent"></div>
                <div className="absolute bottom-6 left-8 right-8 text-white">
                  <span className="px-3 py-1 bg-[#00a69c] rounded-full text-[10px] font-black uppercase tracking-widest mb-2 inline-block">
                    {doctor.status}
                  </span>
                  <h1 className="text-3xl font-extrabold">{fullName}</h1>
                  <p className="text-slate-200 font-medium text-sm flex items-center mt-1">
                    <Stethoscope className="w-4 h-4 mr-2" /> {doctor.specialization}
                  </p>
                </div>
              </div>

              <div className="p-8 space-y-6">
                <div className="grid grid-cols-2 gap-6 pb-6 border-b border-slate-100">
                  <div className="space-y-1">
                    <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">Experience</p>
                    <p className="text-lg font-extrabold text-[#002d5a]">{doctor.experienceYears}+ Years</p>
                  </div>
                  <div className="space-y-1 text-right">
                    <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">Global Rank</p>
                    <p className="text-lg font-extrabold text-[#00a69c]">Top 1%</p>
                  </div>
                </div>

                <div className="space-y-4">
                  <div className="flex items-center justify-between group cursor-help">
                    <div className="flex items-center space-x-3">
                      <div className="p-2 bg-blue-50 rounded-xl group-hover:bg-[#0066cc] transition-colors">
                        <Clock className="w-4 h-4 text-[#0066cc] group-hover:text-white" />
                      </div>
                      <span className="text-sm font-bold text-slate-700">Waiting Time</span>
                    </div>
                    <span className="text-sm font-black text-[#002d5a] tracking-tight">~ 15 Mins</span>
                  </div>
                  <div className="flex items-center justify-between group cursor-help">
                    <div className="flex items-center space-x-3">
                      <div className="p-2 bg-green-50 rounded-xl group-hover:bg-green-600 transition-colors">
                        <CheckCircle2 className="w-4 h-4 text-green-600 group-hover:text-white" />
                      </div>
                      <span className="text-sm font-bold text-slate-700">Verified Expert</span>
                    </div>
                    <span className="text-sm font-black text-green-600 tracking-tight">YES</span>
                  </div>
                </div>

                <Link
                  to={`/appointments?doctorId=${id}&specialty=${doctor.specialization}`}
                  className="w-full flex items-center justify-center space-x-3 bg-gradient-to-r from-[#002d5a] to-[#0066cc] text-white py-5 rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl shadow-blue-900/20 active:scale-95 transition-all"
                >
                  <span>Request Appointment</span>
                  <Calendar className="w-4 h-4" />
                </Link>

                <div className="pt-2 text-center">
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-tighter">
                    Securing clinical data via enterprise blockchain
                  </p>
                </div>
              </div>
            </div>

            {/* Quick Stats Grid */}
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-lg text-center space-y-2 group hover:border-[#00a69c] transition-all">
                <Heart className="w-8 h-8 text-[#e53e3e] mx-auto group-hover:scale-110 transition-transform" />
                <h4 className="text-2xl font-black text-[#002d5a]">1.2k</h4>
                <p className="text-[10px] font-bold text-slate-400 uppercase">Patients</p>
              </div>
              <div className="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-lg text-center space-y-2 group hover:border-[#0066cc] transition-all">
                <Star className="w-8 h-8 text-amber-400 mx-auto group-hover:rotate-12 transition-transform" />
                <h4 className="text-2xl font-black text-[#002d5a]">
                  {stats.averageRating > 0 ? stats.averageRating.toFixed(1) : 'New'}
                </h4>
                <p className="text-[10px] font-bold text-slate-400 uppercase">Avg Rating</p>
              </div>
            </div>
          </motion.div>

          {/* Right Column: Detailed Info */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.1 }}
            className="lg:col-span-8 space-y-10"
          >
            {/* Biography & Vision */}
            <div className="bg-white rounded-[3rem] p-10 border border-slate-100 shadow-xl space-y-8">
              <div className="space-y-4">
                <div className="inline-flex items-center space-x-2 px-3 py-1 bg-indigo-50 border border-indigo-100 rounded-full text-indigo-500 text-[10px] font-black uppercase tracking-widest">
                  <Award className="w-3 h-3" />
                  <span>Specialist Vision</span>
                </div>
                <h2 className="text-3xl font-extrabold text-[#002d5a]">Professional Biography</h2>
                <p className="text-slate-600 leading-relaxed text-lg italic">
                  "{doctor.bio || `Dr. ${doctor.lastName} is a highly accomplished specialist dedicated to advancing the boundary of clinical excellence through compassionate care and technological innovation.`}"
                </p>
              </div>

              <div className="grid md:grid-cols-2 gap-10">
                <div className="space-y-4">
                  <h3 className="text-lg font-extrabold text-[#002d5a] flex items-center">
                    <Award className="w-5 h-5 mr-3 text-[#0066cc]" /> Academic Foundations
                  </h3>
                  <div className="bg-slate-50 rounded-2xl p-6 border border-slate-100">
                    <p className="font-bold text-[#002d5a] text-lg">{doctor.qualification || 'M.B.B.S., M.D.'}</p>
                    <p className="text-slate-500 text-sm mt-1">Board Certified Specialist</p>
                  </div>
                </div>
                <div className="space-y-4">
                  <h3 className="text-lg font-extrabold text-[#002d5a] flex items-center">
                    <ShieldCheck className="w-5 h-5 mr-3 text-[#00a69c]" /> Verified Credentials
                  </h3>
                  <div className="bg-slate-50 rounded-2xl p-6 border border-slate-100">
                    <p className="font-bold text-[#002d5a] text-lg">License No: {doctor.licenseNumber || 'SLMC-9244X'}</p>
                    <p className="text-slate-500 text-sm mt-1 text-green-600 flex items-center">
                      <CheckCircle2 className="w-3.5 h-3.5 mr-1" /> Active License (Verified)
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Specialties & Services */}
            <div className="grid md:grid-cols-2 gap-8">
              <div className="bg-white rounded-[3rem] p-10 border border-slate-100 shadow-xl space-y-6">
                <h3 className="text-xl font-extrabold text-[#002d5a]">Digital Health Integration</h3>
                <div className="space-y-4">
                  <div className={`p-6 rounded-2xl border-2 transition-all flex items-center justify-between ${doctor.isAvailableForTelemedicine ? 'border-green-100 bg-green-50' : 'border-slate-100 bg-slate-50 opacity-60'}`}>
                    <div className="flex items-center space-x-4">
                      <div className={`p-3 rounded-xl ${doctor.isAvailableForTelemedicine ? 'bg-green-600 text-white' : 'bg-slate-300 text-white'}`}>
                        <Video className="w-6 h-6" />
                      </div>
                      <div>
                        <p className="font-bold text-[#002d5a]">Consultation via Video</p>
                        <p className="text-xs text-slate-500">Secure telehealth session</p>
                      </div>
                    </div>
                    {doctor.isAvailableForTelemedicine && <CheckCircle2 className="text-green-600 w-5 h-5" />}
                  </div>

                  <div className="p-6 rounded-2xl border-2 border-blue-100 bg-blue-50 flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                      <div className="p-3 bg-[#0066cc] text-white rounded-xl">
                        <MessageCircle className="w-6 h-6" />
                      </div>
                      <div>
                        <p className="font-bold text-[#002d5a]">Smart Chat Support</p>
                        <p className="text-xs text-slate-500">24/7 AI-monitored portal</p>
                      </div>
                    </div>
                    <CheckCircle2 className="text-blue-600 w-5 h-5" />
                  </div>
                </div>
              </div>

              <div className="bg-[#002d5a] rounded-[3rem] p-10 text-white space-y-6 relative overflow-hidden group">
                <div className="absolute top-0 right-0 w-[200px] h-[200px] bg-blue-400/10 rounded-full blur-[40px] -mr-16 -mt-16 group-hover:scale-125 transition-transform duration-700"></div>
                <h3 className="text-xl font-extrabold relative z-10">Consultation Pricing</h3>
                <div className="space-y-4 relative z-10">
                  <div className="bg-white/5 border border-white/10 rounded-2xl p-6 backdrop-blur-md">
                    <div className="flex justify-between items-center mb-2">
                      <span className="text-sm font-bold text-slate-300 uppercase tracking-widest">Base Session Fee</span>
                      <span className="text-2xl font-black text-[#00a69c]">Rs. {doctor.consultationFee?.toLocaleString()}</span>
                    </div>
                    <p className="text-[10px] text-slate-400 leading-relaxed uppercase">
                      Excludes hospital facility charges and medicine costs.
                    </p>
                  </div>
                  <div className="flex items-center space-x-3 text-xs font-bold text-slate-300">
                    <ShieldCheck className="w-4 h-4 text-[#00a69c]" />
                    <span>Insurance Integrated Payment Support</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Specialist Schedule */}
            <div className="bg-white rounded-[3rem] p-10 border border-slate-100 shadow-xl space-y-8">
              <div className="flex justify-between items-center">
                <div className="space-y-1">
                  <h3 className="text-2xl font-black text-[#002d5a]">Clinical Schedule</h3>
                  <p className="text-slate-500 text-sm font-medium">Currently confirmed patient consultations</p>
                </div>
                <div className="bg-blue-50 px-4 py-2 rounded-xl border border-blue-100">
                  <span className="text-[#0066cc] font-black text-xs uppercase tracking-widest">{appointments.length} Slots Occupied</span>
                </div>
              </div>

              {appointments.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {appointments.map((appt, index) => (
                    <div key={index} className="flex items-center justify-between p-5 bg-slate-50 rounded-2xl border border-slate-100 hover:border-[#0066cc]/30 transition-all group">
                      <div className="flex items-center space-x-4">
                        <div className="w-12 h-12 bg-white rounded-xl shadow-sm flex items-center justify-center text-[#0066cc] group-hover:bg-[#002d5a] group-hover:text-white transition-all">
                          <Clock className="w-6 h-6" />
                        </div>
                        <div>
                          <p className="font-bold text-[#002d5a]">{new Date(appt.appointmentDate).toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric' })}</p>
                          <p className="text-xs font-bold text-slate-400 uppercase tracking-tighter">{new Date(appt.appointmentDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</p>
                        </div>
                      </div>
                      <div className="text-right">
                        <span className={`px-3 py-1 rounded-full text-[9px] font-black uppercase tracking-widest ${appt.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'
                          }`}>
                          {appt.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12 bg-slate-50 rounded-[2rem] border border-dashed border-slate-200">
                  <Calendar className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                  <p className="text-slate-500 font-bold">No active appointments scheduled.</p>
                  <p className="text-xs text-slate-400 mt-1 uppercase tracking-widest font-black">Ready for new bookings</p>
                </div>
              )}
            </div>

            {/* Patient Reviews Section */}
            <div className="bg-white rounded-[3rem] p-10 border border-slate-100 shadow-xl space-y-8 mt-10">
              <div className="flex justify-between items-center">
                <div className="space-y-1">
                  <h3 className="text-xl font-black text-[#002d5a]">Patient Reviews</h3>
                  <p className="text-slate-500 text-sm font-medium">Feedback from verified patients</p>
                </div>
                <div className="bg-amber-50 px-4 py-2 rounded-xl border border-amber-100">
                  <span className="text-amber-600 font-black text-xs uppercase tracking-widest">{stats.totalReviews} Reviews</span>
                </div>
              </div>

              {reviews.length > 0 ? (
                <div className="space-y-6">
                  {reviews.map((review, idx) => (
                    <div key={idx} className="p-6 bg-slate-50 rounded-2xl border border-slate-100 relative shadow-sm">
                      <div className="flex items-center gap-1 mb-3">
                        {[...Array(5)].map((_, i) => (
                          <Star key={i} className={`w-4 h-4 ${i < review.rating ? 'text-amber-400 fill-amber-400' : 'text-slate-300'}`} />
                        ))}
                      </div>
                      <p className="font-medium text-slate-700 italic">"{review.comment}"</p>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12 bg-slate-50 rounded-[2rem] border border-dashed border-slate-200">
                  <MessageCircle className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                  <p className="text-slate-500 font-bold">No reviews yet.</p>
                </div>
              )}
            </div>
          </motion.div>
        </div>
      </div>

      {/* Floating Action for Mobile */}
      <div className="lg:hidden fixed bottom-6 left-6 right-6 z-50">
        <Link
          to={`/appointments?doctorId=${id}&specialty=${doctor.specialization}`}
          className="w-full flex items-center justify-center space-x-3 bg-[#00a69c] text-white py-5 rounded-2xl font-black text-xs uppercase tracking-widest shadow-2xl active:scale-95 transition-all"
        >
          <span>Book Visit</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>

      {/* Trust Quote Section */}
      <section className="section-padding bg-slate-50 mt-20">
        <div className="max-w-7xl mx-auto flex flex-col items-center text-center space-y-10">
          <div className="inline-flex items-center space-x-2 px-4 py-2 bg-white rounded-full shadow-lg text-[#002d5a] text-xs font-black uppercase tracking-widest border border-slate-100">
            <PhoneCall className="w-4 h-4" />
            <span>Dedicated Care Hotline: 1344</span>
          </div>
          <h2 className="text-4xl font-extrabold text-[#002d5a] leading-tight max-w-2xl">
            We ensure every patient receives world-class clinical attention with the utmost security.
          </h2>
          <p className="text-slate-500 max-w-xl text-lg font-medium">
            Transparency, trust, and intelligence are at the heart of the OMNIHEALTH specialist ecosystem.
          </p>
        </div>
      </section>
    </div>
  )
}

export default DoctorDetail
