import { motion } from 'framer-motion'
import { Stethoscope, Star, MapPin, Clock, MessageCircle, Video } from 'lucide-react'
import { Link } from 'react-router-dom'

const DoctorCard = ({ doctor }) => {
  const {
    id,
    firstName,
    lastName,
    specialization,
    qualification,
    experienceYears,
    consultationFee,
    isAvailableForTelemedicine,
    profilePhotoUrl,
    status
  } = doctor

  const fullName = `Dr. ${firstName} ${lastName}`
  const displayImage = profilePhotoUrl || `https://ui-avatars.com/api/?name=${firstName}+${lastName}&background=0D8ABC&color=fff&size=128`

  return (
    <motion.div
      whileHover={{ y: -5 }}
      className="bg-white rounded-3xl border border-slate-100 shadow-sm hover:shadow-xl transition-all overflow-hidden group flex flex-col h-full"
    >
      {/* Header Container */}
      <div className="relative h-48 overflow-hidden">
        <img
          src={displayImage}
          alt={fullName}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
        
        {/* Status Badge */}
        <div className="absolute top-4 left-4">
          <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest backdrop-blur-md ${
            status === 'APPROVED' ? 'bg-green-500/80 text-white' : 'bg-amber-500/80 text-white'
          }`}>
            {status}
          </span>
        </div>

        {/* Telemedicine Badge */}
        {isAvailableForTelemedicine && (
          <div className="absolute top-4 right-4">
            <div className="bg-[#00a69c]/90 backdrop-blur-md p-2 rounded-xl text-white" title="Telemedicine Available">
              <Video className="w-4 h-4" />
            </div>
          </div>
        )}

        <div className="absolute bottom-4 left-4 right-4 text-white">
          <h3 className="text-xl font-bold leading-tight">{fullName}</h3>
          <p className="text-xs font-medium text-slate-200 flex items-center mt-1">
            <Stethoscope className="w-3 h-3 mr-1" /> {specialization}
          </p>
        </div>
      </div>

      {/* Body */}
      <div className="p-6 space-y-4 flex-grow">
        <div className="flex justify-between items-start">
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Qualification</p>
            <p className="text-sm font-semibold text-[#002d5a] line-clamp-1">{qualification}</p>
          </div>
          <div className="text-right">
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Fee</p>
            <p className="text-sm font-bold text-[#0066cc]">Rs. {consultationFee?.toLocaleString()}</p>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 py-2 border-y border-slate-50">
          <div className="flex items-center space-x-2">
            <div className="p-1.5 bg-blue-50 rounded-lg">
              <Clock className="w-3.5 h-3.5 text-[#0066cc]" />
            </div>
            <div>
              <p className="text-[10px] font-bold text-slate-400 uppercase">Experience</p>
              <p className="text-xs font-bold text-[#002d5a]">{experienceYears}+ Yrs</p>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <div className="p-1.5 bg-amber-50 rounded-lg">
              <Star className="w-3.5 h-3.5 text-amber-500" />
            </div>
            <div>
              <p className="text-[10px] font-bold text-slate-400 uppercase">Rating</p>
              <p className="text-xs font-bold text-[#002d5a]">4.9 (120+)</p>
            </div>
          </div>
        </div>

        <p className="text-slate-500 text-xs leading-relaxed line-clamp-2">
          Expert {specialization} with extensive experience in clinical diagnostics and patient-centered care.
        </p>
      </div>

      {/* Actions */}
      <div className="p-6 pt-0 mt-auto">
        <Link 
          to={`/doctors/${id}`}
          className="flex items-center justify-center space-x-2 w-full py-4 bg-[#002d5a] text-white rounded-2xl text-xs font-black uppercase tracking-widest hover:bg-[#003d7a] transition-all shadow-xl shadow-blue-900/10 active:scale-95"
        >
          <span>Book Now</span>
        </Link>
      </div>
    </motion.div>
  )
}

export default DoctorCard
