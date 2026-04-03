import { useState, useEffect } from 'react'
import { 
  Stethoscope, 
  Award, 
  FileText, 
  Phone, 
  Briefcase, 
  DollarSign, 
  Video, 
  CheckCircle2, 
  AlertCircle,
  Camera,
  Save,
  Globe,
  ShieldCheck,
  Loader2
} from 'lucide-react'
import { motion } from 'framer-motion'
import { uploadImageToCloudinary } from '../services/cloudinary'
import { useRef } from 'react'

const DoctorProfileCard = ({ profile, onSave, isSaving, error }) => {
  const fileInputRef = useRef(null)
  const [isUploading, setIsUploading] = useState(false)
  const [uploadError, setUploadError] = useState(null)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    specialization: '',
    qualification: '',
    experienceYears: 0,
    licenseNumber: '',
    bio: '',
    consultationFee: 0,
    isAvailableForTelemedicine: false,
    phone: '',
    email: '',
    profilePhotoUrl: ''
  })

  useEffect(() => {
    if (profile) {
      setFormData({
        firstName: profile.firstName || '',
        lastName: profile.lastName || '',
        specialization: profile.specialization || '',
        qualification: profile.qualification || '',
        experienceYears: profile.experienceYears || 0,
        licenseNumber: profile.licenseNumber || '',
        bio: profile.bio || '',
        consultationFee: profile.consultationFee || 0,
        isAvailableForTelemedicine: profile.isAvailableForTelemedicine || false,
        phone: profile.phone || '',
        email: profile.email || '',
        profilePhotoUrl: profile.profilePhotoUrl || ''
      })
    }
  }, [profile])

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (type === 'number' ? Number(value) : value)
    }))
  }

  const handleCameraClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    setIsUploading(true)
    setUploadError(null)

    try {
      const url = await uploadImageToCloudinary(file)
      setFormData(prev => ({
        ...prev,
        profilePhotoUrl: url
      }))
    } catch (err) {
      setUploadError('Failed to upload image. Please check your Cloudinary configuration.')
      console.error(err)
    } finally {
      setIsUploading(false)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    onSave(formData)
  }

  return (
    <section className="max-w-7xl mx-auto px-4 mb-16">
      <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl overflow-hidden">
        <div className="bg-[#002d5a] p-8 md:p-12 text-white relative overflow-hidden">
           <div className="absolute top-0 right-0 w-[300px] h-[300px] bg-blue-500/10 rounded-full blur-[60px] -mr-16 -mt-16"></div>
           <div className="relative z-10 flex flex-col md:flex-row items-center gap-8">
              <div className="relative group">
                <div className="w-32 h-32 rounded-3xl overflow-hidden border-4 border-white/20 shadow-2xl">
                   <img 
                      src={formData.profilePhotoUrl || `https://ui-avatars.com/api/?name=${formData.firstName}+${formData.lastName}&background=0D8ABC&color=fff&size=256`} 
                      className={`w-full h-full object-cover ${isUploading ? 'opacity-40 animate-pulse' : ''}`} 
                      alt="Profile" 
                   />
                </div>
                <input 
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileChange}
                  className="hidden"
                  accept="image/*"
                />
                <button 
                  type="button" 
                  onClick={handleCameraClick}
                  disabled={isUploading}
                  className="absolute -bottom-2 -right-2 bg-white text-[#002d5a] p-2 rounded-xl shadow-lg hover:bg-slate-100 transition-all disabled:opacity-50"
                >
                  {isUploading ? <Loader2 className="w-5 h-5 animate-spin" /> : <Camera className="w-5 h-5" />}
                </button>
              </div>
              <div className="text-center md:text-left space-y-2">
                 <h2 className="text-3xl font-extrabold">Professional Specialist Profile</h2>
                 <p className="text-blue-200 text-lg">Manage your clinical identity and consultation settings.</p>
                 <div className="flex flex-wrap justify-center md:justify-start gap-4 pt-2">
                   <span className="px-3 py-1 bg-white/10 rounded-full text-[10px] font-black uppercase tracking-widest border border-white/20">
                     Verification: {profile?.status || 'PENDING'}
                   </span>
                   <span className="px-3 py-1 bg-white/10 rounded-full text-[10px] font-black uppercase tracking-widest border border-white/20 flex items-center">
                     <ShieldCheck className="w-3 h-3 mr-2 text-[#00a69c]" /> Trusted Specialist
                   </span>
                 </div>
              </div>
           </div>
        </div>

        <form onSubmit={handleSubmit} className="p-8 md:p-12 space-y-12">
          {error && (
            <div className="flex items-center space-x-3 bg-red-50 p-4 rounded-2xl border border-red-100 text-red-600">
              <AlertCircle className="w-5 h-5 flex-shrink-0" />
              <p className="text-sm font-bold">{error}</p>
            </div>
          )}

          {uploadError && (
            <div className="flex items-center space-x-3 bg-red-50 p-4 rounded-2xl border border-red-100 text-red-600">
              <AlertCircle className="w-5 h-5 flex-shrink-0" />
              <p className="text-sm font-bold">{uploadError}</p>
            </div>
          )}

          {/* Core Identity */}
          <div className="space-y-6">
            <h3 className="text-xl font-extrabold text-[#002d5a] flex items-center">
              <Stethoscope className="w-6 h-6 mr-3 text-[#0066cc]" /> Basic Professional Info
            </h3>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">First Name</label>
                <input 
                  type="text" 
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  className="w-full bg-slate-50 border border-slate-100 rounded-xl px-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                  placeholder="e.g. John"
                />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Last Name</label>
                <input 
                  type="text" 
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  className="w-full bg-slate-50 border border-slate-100 rounded-xl px-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                  placeholder="e.g. Doe"
                />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Mobile Number</label>
                <div className="relative">
                  <Phone className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-300" />
                  <input 
                    type="tel" 
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    className="w-full bg-slate-50 border border-slate-100 rounded-xl pl-12 pr-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                    placeholder="07x xxxxxxx"
                  />
                </div>
              </div>
            </div>
          </div>

          <hr className="border-slate-50" />

          {/* Expertise & Licenses */}
          <div className="space-y-6">
            <h3 className="text-xl font-extrabold text-[#002d5a] flex items-center">
              <Award className="w-6 h-6 mr-3 text-[#00a69c]" /> Clinical Expertise
            </h3>
            <div className="grid md:grid-cols-2 gap-8">
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Main Specialization</label>
                <select 
                  name="specialization"
                  value={formData.specialization}
                  onChange={handleChange}
                  className="w-full bg-slate-50 border border-slate-100 rounded-xl px-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                >
                  <option value="">Select Specialization</option>
                  <option value="Cardiology">Cardiology</option>
                  <option value="Neurology">Neurology</option>
                  <option value="Paediatrics">Paediatrics</option>
                  <option value="Orthopaedics">Orthopaedics</option>
                  <option value="Radiology">Radiology</option>
                  <option value="General Practice">General Practice</option>
                </select>
              </div>
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Primary Qualification</label>
                <div className="relative">
                  <Award className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-300" />
                  <input 
                    type="text" 
                    name="qualification"
                    value={formData.qualification}
                    onChange={handleChange}
                    className="w-full bg-slate-50 border border-slate-100 rounded-xl pl-12 pr-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                    placeholder="e.g. MBBS, MD (Cardiology)"
                  />
                </div>
              </div>
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Experience (Years)</label>
                <div className="relative">
                  <Briefcase className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-300" />
                  <input 
                    type="number" 
                    name="experienceYears"
                    value={formData.experienceYears}
                    onChange={handleChange}
                    className="w-full bg-slate-50 border border-slate-100 rounded-xl pl-12 pr-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                  />
                </div>
              </div>
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">SLMC License Number</label>
                <div className="relative">
                  <FileText className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-300" />
                  <input 
                    type="text" 
                    name="licenseNumber"
                    value={formData.licenseNumber}
                    onChange={handleChange}
                    className="w-full bg-slate-50 border border-slate-100 rounded-xl pl-12 pr-4 py-3 outline-none focus:border-[#0066cc] font-medium transition-all"
                    placeholder="e.g. SLMC-12345"
                  />
                </div>
              </div>
            </div>
          </div>

          <hr className="border-slate-50" />

          {/* Consultation Settings */}
          <div className="space-y-6">
            <h3 className="text-xl font-extrabold text-[#002d5a] flex items-center">
              <DollarSign className="w-6 h-6 mr-3 text-[#e53e3e]" /> Consultation Settings
            </h3>
            <div className="grid md:grid-cols-2 gap-8">
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Base Consultation Fee (Rs.)</label>
                <div className="relative">
                  <span className="absolute left-4 top-1/2 -translate-y-1/2 font-bold text-slate-400 text-sm">Rs.</span>
                  <input 
                    type="number" 
                    name="consultationFee"
                    value={formData.consultationFee}
                    onChange={handleChange}
                    className="w-full bg-slate-50 border border-slate-100 rounded-xl pl-12 pr-4 py-3 outline-none focus:border-[#e53e3e]/30 font-bold text-[#002d5a] transition-all"
                  />
                </div>
              </div>
              <div className="flex flex-col justify-end">
                <label className="relative inline-flex items-center cursor-pointer group p-3 bg-slate-50 rounded-2xl border border-slate-100 select-none">
                  <input 
                    type="checkbox" 
                    name="isAvailableForTelemedicine"
                    checked={formData.isAvailableForTelemedicine}
                    onChange={handleChange}
                    className="sr-only peer" 
                  />
                  <div className="relative w-11 h-6 bg-slate-200 rounded-full peer peer-focus:ring-4 peer-focus:ring-[#00a69c]/20 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#00a69c]"></div>
                  <span className="ms-3 text-sm font-bold text-[#002d5a] flex items-center">
                    <Video className={`w-4 h-4 mr-2 ${formData.isAvailableForTelemedicine ? 'text-[#00a69c]' : 'text-slate-400'}`} />
                    Opt-in for Telemedicine Consultations
                  </span>
                </label>
              </div>
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-xs font-black text-slate-400 uppercase tracking-widest">Professional Bio / Description</label>
            <textarea 
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={4}
              className="w-full bg-slate-50 border border-slate-100 rounded-2xl px-6 py-4 outline-none focus:border-[#0066cc] font-medium leading-relaxed transition-all"
              placeholder="Tell your patients about your clinical approach, research interests, and vision..."
            ></textarea>
          </div>

          <div className="pt-8 flex flex-col md:flex-row gap-4 justify-between items-center bg-slate-50 -mx-8 md:-mx-12 -mb-8 md:-mb-12 p-8 md:p-12 border-t border-slate-100">
             <div className="flex items-center space-x-2 text-slate-400">
                <AlertCircle className="w-4 h-4" />
                <span className="text-[10px] font-bold uppercase tracking-widest">Some fields require admin verification after change</span>
             </div>
             <button 
                type="submit"
                disabled={isSaving}
                className="w-full md:w-auto min-w-[240px] flex items-center justify-center space-x-3 bg-[#0066cc] text-white py-4 px-8 rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-[#0052a3] shadow-xl shadow-blue-500/10 active:scale-95 transition-all disabled:opacity-50"
             >
               {isSaving ? 'Synchronizing Data...' : 'Save Clinical Profile'}
               <Save className="w-4 h-4" />
             </button>
          </div>
        </form>
      </div>
    </section>
  )
}

export default DoctorProfileCard
