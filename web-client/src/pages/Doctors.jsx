import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { 
  Search, 
  Filter, 
  MapPin, 
  Stethoscope, 
  Video, 
  ChevronDown, 
  ArrowRight,
  TrendingUp,
  Activity,
  Heart,
  Baby,
  Brain,
  FlaskConical,
  Microscope
} from 'lucide-react'
import { getAllDoctors, getDoctorsBySpecialization } from '../services/doctors'
import DoctorCard from '../components/DoctorCard'

const specialties = [
  { id: 'all', name: 'All Specialties', icon: Activity },
  { id: 'Cardiology', name: 'Cardiology', icon: Heart },
  { id: 'Neurology', name: 'Neurology', icon: Brain },
  { id: 'Paediatrics', name: 'Paediatrics', icon: Baby },
  { id: 'Laboratory', name: 'Laboratory', icon: FlaskConical },
  { id: 'Radiology', name: 'Radiology', icon: Microscope },
  { id: 'Orthopaedics', name: 'Orthopaedics', icon: Stethoscope }
]

const Doctors = () => {
  const [doctors, setDoctors] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedSpecialty, setSelectedSpecialty] = useState('all')

  useEffect(() => {
    fetchDoctors()
  }, [])

  const fetchDoctors = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await getAllDoctors()
      setDoctors(data)
    } catch (err) {
      setError('Unable to load doctor profiles. Please check if the doctor-management service is running.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSpecialtyChange = async (specialtyId) => {
    setSelectedSpecialty(specialtyId)
    setLoading(true)
    try {
      if (specialtyId === 'all') {
        const data = await getAllDoctors()
        setDoctors(data)
      } else {
        const data = await getDoctorsBySpecialization(specialtyId)
        setDoctors(data)
      }
    } catch (err) {
      setError('Failed to filter doctors.')
    } finally {
      setLoading(false)
    }
  }

  const filteredDoctors = doctors.filter(doctor => {
    const fullName = `${doctor.firstName} ${doctor.lastName}`.toLowerCase()
    const matchesSearch = fullName.includes(searchTerm.toLowerCase()) || 
                          doctor.specialization.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesSearch
  })

  return (
    <div className="min-h-screen bg-white">
      {/* Search Header */}
      <section className="pt-32 pb-20 relative overflow-hidden bg-slate-900">
        <div className="absolute inset-0 z-0">
          <img 
            src="https://images.unsplash.com/photo-1579684385127-1ef15d508118?q=80&w=2680&auto=format&fit=crop" 
            className="w-full h-full object-cover opacity-20 filter brightness-75 scale-110" 
            alt="Search for Medical Experts" 
          />
          <div className="absolute inset-0 bg-gradient-to-b from-slate-900/80 via-slate-900/40 to-[#002d5a]"></div>
        </div>

        <div className="max-w-7xl mx-auto px-4 relative z-10 text-center space-y-10">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="space-y-4"
          >
            <span className="inline-flex items-center space-x-2 px-4 py-2 bg-blue-500/20 border border-blue-500/30 rounded-full text-blue-400 text-xs font-black uppercase tracking-widest mb-4">
              <Stethoscope className="w-4 h-4" />
              <span>Medical Expert Directory</span>
            </span>
            <h1 className="text-5xl md:text-6xl font-extrabold text-white leading-tight">
              Connect with World-Class Healthcare <br className="hidden md:block" /> Specialists Worldwide.
            </h1>
            <p className="text-slate-300 text-xl max-w-2xl mx-auto font-medium">
              Filter by expertise, view clinical profiles, and book your next consultation in minutes with OMNIHEALTH Intelligence.
            </p>
          </motion.div>

          <motion.div 
            initial={{ opacity: 0, transform: 'scale(0.95)' }}
            animate={{ opacity: 1, transform: 'scale(1)' }}
            transition={{ delay: 0.3, duration: 0.8 }}
            className="max-w-3xl mx-auto"
          >
            <div className="bg-white/10 backdrop-blur-3xl p-3 md:p-4 rounded-[2.5rem] border border-white/20 shadow-2xl flex flex-col md:flex-row gap-2">
              <div className="flex-grow relative group">
                <Search className="absolute left-6 top-1/2 -translate-y-1/2 w-6 h-6 text-white/50 group-hover:text-white transition-colors" />
                <input 
                  type="text" 
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  placeholder="Search by name, specialization, or keyword..."
                  className="w-full h-16 bg-white/5 border border-white/10 rounded-[1.8rem] pl-16 pr-6 text-white text-lg font-medium placeholder:text-white/30 outline-none focus:bg-white/10 transition-all"
                />
              </div>
              <button className="bg-[#00a69c] hover:bg-[#008d84] text-white px-10 h-16 rounded-[1.8rem] font-black text-sm uppercase tracking-widest transition-all shadow-xl hover:shadow-cyan-500/20 active:scale-95">
                Search Database
              </button>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Specialty Filter */}
      <section className="-mt-12 relative z-20 max-w-7xl mx-auto px-4">
        <div className="flex flex-nowrap md:flex-wrap items-center gap-4 overflow-x-auto pb-4 md:pb-0 scrollbar-hide">
          {specialties.map((specialty) => (
            <button
              key={specialty.id}
              onClick={() => handleSpecialtyChange(specialty.id)}
              className={`flex items-center space-x-3 px-8 py-5 rounded-[2rem] whitespace-nowrap transition-all border shadow-lg ${
                selectedSpecialty === specialty.id 
                ? 'bg-white border-[#0066cc] text-[#0066cc] scale-105 active:scale-95' 
                : 'bg-white border-slate-100 text-slate-500 hover:border-slate-300 hover:bg-slate-50'
              }`}
            >
              <specialty.icon className={`w-6 h-6 ${selectedSpecialty === specialty.id ? 'text-[#0066cc]' : 'text-slate-400'}`} />
              <span className="font-bold text-sm tracking-tight">{specialty.name}</span>
            </button>
          ))}
        </div>
      </section>

      {/* Main Content */}
      <section className="section-padding max-w-7xl mx-auto px-4">
        <div className="flex justify-between items-center mb-16 border-b border-slate-100 pb-8 mt-12">
          <div>
            <h2 className="text-3xl font-extrabold text-[#002d5a]">
              Available Specialists <span className="text-slate-400 font-medium ml-4 text-xl">({filteredDoctors.length})</span>
            </h2>
            <p className="text-slate-500 mt-2 font-medium">Browse verified medical experts based on your criteria.</p>
          </div>
          <div className="flex items-center space-x-2 text-sm font-bold text-slate-400 uppercase tracking-widest cursor-pointer hover:text-[#0066cc] transition-colors">
            <span>Sort by Rank</span>
            <ChevronDown className="w-5 h-5" />
          </div>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {[1, 2, 3, 4, 5, 6, 7, 8].map((i) => (
              <div key={i} className="bg-slate-50 rounded-3xl h-[450px] animate-pulse"></div>
            ))}
          </div>
        ) : error ? (
          <div className="text-center py-32 bg-red-50 rounded-[3rem] border border-red-100 border-dashed">
            <h3 className="text-2xl font-bold text-red-700 mb-4">{error}</h3>
            <p className="text-red-500 max-w-md mx-auto mb-8 font-medium">Please verify that all microservices (API Gateway, Discovery Service, and Doctor Service) are correctly running.</p>
            <button 
              onClick={fetchDoctors}
              className="px-10 py-4 bg-red-600 text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-red-700 transition-all font-sans"
            >
              Retry Connection
            </button>
          </div>
        ) : filteredDoctors.length === 0 ? (
          <div className="text-center py-32 bg-slate-50 rounded-[3rem] border border-slate-100 border-dashed">
             <div className="w-24 h-24 bg-slate-200 rounded-full flex items-center justify-center mx-auto mb-8">
                <Search className="w-10 h-10 text-slate-400" />
             </div>
             <h3 className="text-2xl font-bold text-slate-700">No Specialists Found</h3>
             <p className="text-slate-500 mt-2 max-w-sm mx-auto font-medium">We couldn't find any doctors matching your search or filter requirements. Try relaxing your filters.</p>
             <button 
                onClick={() => {setSearchTerm(''); setSelectedSpecialty('all'); fetchDoctors();}}
                className="mt-8 px-10 py-4 border-2 border-[#002d5a] text-[#002d5a] rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-[#002d5a] hover:text-white transition-all font-sans"
             >
               Clear All Filters
             </button>
          </div>
        ) : (
          <motion.div 
            layout
            className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8"
          >
            <AnimatePresence>
              {filteredDoctors.map((doctor) => (
                <div key={doctor.id}>
                  <DoctorCard doctor={doctor} />
                </div>
              ))}
            </AnimatePresence>
          </motion.div>
        )}
      </section>

      {/* Trust Quote / CTA */}
      <section className="section-padding bg-slate-50 mt-20">
        <div className="max-w-7xl mx-auto bg-gradient-to-r from-[#002d5a] to-[#0066cc] rounded-[3.5rem] p-16 text-white flex flex-col md:flex-row items-center justify-between shadow-2xl relative overflow-hidden group">
          <div className="absolute top-0 right-0 w-[400px] h-[400px] bg-white/10 rounded-full blur-[80px] -mr-32 -mt-32"></div>
          <div className="max-w-xl relative z-10 space-y-6">
            <h2 className="text-4xl font-extrabold leading-tight">Can't find what you're <br /> looking for?</h2>
            <p className="text-blue-100 text-lg font-medium leading-relaxed">
              Our 24-hour medical concierge is here to help you find the right specialist and schedule urgent care if needed.
            </p>
            <div className="flex flex-wrap gap-4 pt-4">
              <button className="px-8 py-4 bg-[#00a69c] text-white rounded-2xl font-black text-xs uppercase tracking-widest active:scale-95 transition-all shadow-xl shadow-cyan-900/20">
                Speak with Concierge
              </button>
              <button className="px-8 py-4 border border-white/30 text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-white/10 active:scale-95 transition-all">
                Find Nearest Hospital
              </button>
            </div>
          </div>
          <div className="hidden lg:block relative z-10">
            <img 
              src="/doc.png" 
              className="w-[450px] -mb-20 transform group-hover:scale-105 transition-transform duration-700 drop-shadow-2xl" 
              alt="Medical Specialist" 
            />
          </div>
        </div>
      </section>
    </div>
  )
}

export default Doctors
