import { Calendar, Clock, User, MapPin, ArrowLeft, Phone, CheckCircle, BadgeCheck, Stethoscope } from 'lucide-react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { createAppointment } from '../services/appointments'
import { getAllDoctors } from '../services/doctors'

const Appointments = ({ user, patientProfile, onLoginClick }) => {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    speciality: searchParams.get('specialty') || '',
    doctorId: searchParams.get('doctorId') ? `dr_${searchParams.get('doctorId')}` : '',
    date: '',
    timeSlot: '',
    reason: '',
  })
  const [doctorsList, setDoctorsList] = useState([])
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isSuccess, setIsSuccess] = useState(false)

  useEffect(() => {
    if (user) {
      setFormData(prev => ({
        ...prev,
        fullName: user.name || '',
        phoneNumber: patientProfile?.phoneNumber || '',
      }))
    }
  }, [user, patientProfile])

  useEffect(() => {
    const fetchDoctors = async () => {
      try {
        const data = await getAllDoctors()
        setDoctorsList(data)
        
        // If doctorId was provided in URL, make sure speciality is also set if missing
        const urlDoctorId = searchParams.get('doctorId')
        if (urlDoctorId && !formData.speciality) {
          const doctor = data.find(d => d.id.toString() === urlDoctorId)
          if (doctor) {
            setFormData(prev => ({ ...prev, speciality: doctor.specialization }))
          }
        }
      } catch (err) {
        console.error('Failed to load doctors:', err)
      }
    }

    fetchDoctors()
  }, [searchParams])

  const specialities = [...new Set(doctorsList.map(d => d.specialization))].filter(Boolean).sort()
  const filteredDoctors = doctorsList.filter(d => 
    !formData.speciality || d.specialization === formData.speciality
  )

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsSubmitting(true)

    try {
      // Combine date and time
      const appointmentDate = new Date(`${formData.date}T${formData.timeSlot}:00`).toISOString()
      
      const payload = {
        patientId: (user && patientProfile) ? patientProfile.id : (user ? user.id : null),
        doctorId: parseInt(formData.doctorId.replace('dr_', '')),
        appointmentDate: appointmentDate,
        fullName: formData.fullName,
        phoneNumber: formData.phoneNumber,
        reason: formData.reason,
        consultationType: formData.speciality + " Consultation"
      }

      await createAppointment(payload)
      setIsSuccess(true)
      
      // Reset form after 3 seconds or on navigation
      setTimeout(() => {
        setIsSuccess(false)
        setFormData({
          fullName: user?.name || '',
          phoneNumber: patientProfile?.phoneNumber || '',
          speciality: '',
          doctorId: '',
          date: '',
          timeSlot: '',
          reason: '',
        })
      }, 5000)
    } catch (error) {
      alert('Error booking appointment: ' + error.message)
    } finally {
      setIsSubmitting(false)
    }
  }

  if (isSuccess) {
    return (
      <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center animate-in fade-in zoom-in duration-500">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6 text-green-600">
            <CheckCircle className="w-12 h-12" />
          </div>
          <h2 className="text-3xl font-bold text-slate-800 mb-4">Booking Confirmed!</h2>
          <p className="text-slate-600 mb-8">
            Your appointment has been successfully scheduled. You will receive a confirmation message shortly.
          </p>
          <button
            onClick={() => navigate('/')}
            className="w-full btn-primary py-3 font-semibold rounded-xl"
          >
            Go to Dashboard
          </button>
        </div>
      </main>
    )
  }

  return (
    <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50">
      <div className="max-w-3xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => navigate('/')}
            className="flex items-center gap-2 text-[#0066cc] hover:text-[#0052a3] font-semibold mb-4 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
            Back to Home
          </button>
          <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
            <div>
              <h1 className="text-4xl font-bold text-slate-800 mb-2">Book an Appointment</h1>
              <p className="text-slate-600">Schedule a consultation with our healthcare professionals</p>
            </div>
            {!user && (
              <div className="bg-amber-50 border border-amber-200 px-4 py-2 rounded-lg text-sm text-amber-800 flex items-center gap-2">
                <span className="w-2 h-2 bg-amber-500 rounded-full animate-pulse"></span>
                Booking as Guest. <button onClick={onLoginClick} className="font-bold underline">Login</button> instead?
              </div>
            )}
          </div>
        </div>

        {/* Appointment Form */}
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Full Name */}
              <div>
                <label className="auth-label">
                  <User className="w-5 h-5" />
                  Full Name
                </label>
                <input
                  type="text"
                  name="fullName"
                  value={formData.fullName}
                  onChange={handleInputChange}
                  required
                  placeholder="Enter your full name"
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all"
                />
              </div>

              {/* Phone Number */}
              <div>
                <label className="auth-label">
                  <Phone className="w-5 h-5" />
                  Phone Number
                </label>
                <input
                  type="tel"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleInputChange}
                  required
                  placeholder="e.g. +1 234 567 890"
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Specialization Selection */}
              <div>
                <label className="auth-label">
                  <Stethoscope className="w-5 h-5" />
                  Select Speciality
                </label>
                <select
                  name="speciality"
                  value={formData.speciality}
                  onChange={(e) => {
                    handleInputChange(e)
                    setFormData(prev => ({ ...prev, doctorId: '' }))
                  }}
                  required
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all"
                >
                  <option value="">Choose a speciality...</option>
                  {specialities.map(spec => (
                    <option key={spec} value={spec}>{spec}</option>
                  ))}
                </select>
              </div>

              {/* Doctor Selection */}
              <div>
                <label className="auth-label">
                  <BadgeCheck className="w-5 h-5" />
                  Select Doctor
                </label>
                <select
                  name="doctorId"
                  value={formData.doctorId}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all"
                >
                  <option value="">{formData.speciality ? `Choose a ${formData.speciality} doctor...` : 'First select a speciality...'}</option>
                  {filteredDoctors.map(doctor => (
                    <option key={doctor.id} value={`dr_${doctor.id}`}>
                      {doctor.firstName} {doctor.lastName}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Date Selection */}
              <div>
                <label className="auth-label">
                  <Calendar className="w-5 h-5" />
                  Preferred Date
                </label>
                <input
                  type="date"
                  name="date"
                  value={formData.date}
                  onChange={handleInputChange}
                  required
                  min={new Date().toISOString().split('T')[0]}
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all"
                />
              </div>

              {/* Time Slot Selection */}
              <div>
                <label className="auth-label">
                  <Clock className="w-5 h-5" />
                  Preferred Time Slot
                </label>
                <select
                  name="timeSlot"
                  value={formData.timeSlot}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all"
                >
                  <option value="">Select a time...</option>
                  <option value="08:00">8:00 AM</option>
                  <option value="09:00">9:00 AM</option>
                  <option value="10:00">10:00 AM</option>
                  <option value="11:00">11:00 AM</option>
                  <option value="14:00">2:00 PM</option>
                  <option value="15:00">3:00 PM</option>
                  <option value="16:00">4:00 PM</option>
                  <option value="17:00">5:00 PM</option>
                </select>
              </div>
            </div>

            {/* Reason for Appointment */}
            <div>
              <label className="auth-label">
                <MapPin className="w-5 h-5" />
                Reason for Appointment
              </label>
              <textarea
                name="reason"
                value={formData.reason}
                onChange={handleInputChange}
                placeholder="Please describe your symptoms or reason for the appointment..."
                rows="4"
                className="w-full px-4 py-3 rounded-xl border border-slate-300 focus:border-[#0066cc] focus:ring-2 focus:ring-[#0066cc]/10 outline-none transition-all resize-none"
              />
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full btn-primary py-4 font-bold rounded-xl hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-blue-200"
            >
              {isSubmitting ? 'Processing...' : 'Confirm Appointment'}
            </button>
          </form>

          {/* Info Box */}
          <div className="mt-8 p-6 bg-[#f8fafc] border border-slate-200 rounded-2xl flex gap-4">
            <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center shrink-0 text-blue-600">
              <Clock className="w-5 h-5" />
            </div>
            <p className="text-sm text-slate-600 leading-relaxed">
              <strong>Pre-Appointment Info:</strong> Please arrive 15 minutes before your scheduled time. 
              Bring any relevant medical history or current prescriptions with you. 
              For cancellations, please notify us at least 24 hours in advance.
            </p>
          </div>
        </div>
      </div>
    </main>
  )
}

export default Appointments
