import { Calendar, Clock, User, MapPin, ArrowLeft, Phone, CheckCircle, BadgeCheck, Stethoscope } from 'lucide-react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { createAppointment, checkDoctorAvailabilityByDate } from '../services/appointments'
import { getAllDoctors, getDoctorLeaves } from '../services/doctors'

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
  const [availabilityError, setAvailabilityError] = useState('')
  const [isCheckingAvailability, setIsCheckingAvailability] = useState(false)

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

  const isDateWithinLeave = (date, leave) => {
    if (!date || !leave?.startDate || !leave?.endDate) {
      return false
    }

    return date >= leave.startDate && date <= leave.endDate
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }))

    if (name === 'date' || name === 'doctorId' || name === 'speciality') {
      setAvailabilityError('')
    }
  }

  useEffect(() => {
    const checkAvailability = async () => {
      const hasDoctor = Boolean(formData.doctorId)
      const hasDate = Boolean(formData.date)

      if (!hasDoctor || !hasDate) {
        setAvailabilityError('')
        return
      }

      const numericDoctorId = parseInt(formData.doctorId.replace('dr_', ''), 10)
      if (Number.isNaN(numericDoctorId)) {
        setAvailabilityError('Please select a valid doctor.')
        return
      }

      setIsCheckingAvailability(true)

      try {
        const leaves = await getDoctorLeaves(numericDoctorId)
        const leaveConflict = (leaves || []).find((leave) => {
          const status = String(leave?.status || '').toUpperCase()
          const isApproved = status === 'APPROVED' || status === 'ACCEPTED'
          return isApproved && isDateWithinLeave(formData.date, leave)
        })

        if (leaveConflict) {
          setAvailabilityError('Doctor is on approved leave on the selected day.')
          return
        }

        const result = await checkDoctorAvailabilityByDate(numericDoctorId, formData.date)
        setAvailabilityError(result?.available ? '' : (result?.message || 'Doctor is not available on this day.'))
      } catch (error) {
        setAvailabilityError(error.message || 'Unable to validate doctor availability right now.')
      } finally {
        setIsCheckingAvailability(false)
      }
    }

    checkAvailability()
  }, [formData.doctorId, formData.date])

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!user) {
      alert('Please log in as a patient to book an appointment.')
      onLoginClick?.()
      return
    }

    const resolvedPatientId = patientProfile?.id ?? user?.id
    if (!resolvedPatientId) {
      alert('Unable to determine patient profile. Please complete your profile and try again.')
      return
    }

    if (availabilityError) {
      alert(availabilityError)
      return
    }

    const numericDoctorId = parseInt(formData.doctorId.replace('dr_', ''), 10)
    if (!Number.isNaN(numericDoctorId) && formData.date) {
      try {
        const leaves = await getDoctorLeaves(numericDoctorId)
        const hasApprovedLeave = (leaves || []).some((leave) => {
          const status = String(leave?.status || '').toUpperCase()
          const isApproved = status === 'APPROVED' || status === 'ACCEPTED'
          return isApproved && isDateWithinLeave(formData.date, leave)
        })

        if (hasApprovedLeave) {
          const message = 'Doctor is on approved leave on the selected day.'
          setAvailabilityError(message)
          alert(message)
          return
        }
      } catch (error) {
        const message = error.message || 'Unable to validate doctor leave dates.'
        setAvailabilityError(message)
        alert(message)
        return
      }
    }

    setIsSubmitting(true)

    try {
      // Combine date and time
      const appointmentDate = new Date(`${formData.date}T${formData.timeSlot}:00`).toISOString()
      
      const payload = {
        patientId: resolvedPatientId,
        doctorId: parseInt(formData.doctorId.replace('dr_', '')),
        appointmentDate: appointmentDate,
        fullName: null,
        phoneNumber: null,
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
    <main className="pt-28 pb-16 px-4 min-h-screen bg-[radial-gradient(circle_at_0%_0%,#e0f2fe_0%,#f8fafc_45%,#eef2ff_100%)] relative overflow-hidden">
      <div className="absolute -top-32 -left-20 w-72 h-72 bg-cyan-200/30 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-52 -right-20 w-72 h-72 bg-blue-200/30 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-4xl mx-auto relative z-10">
        {/* Header */}
        <div className="mb-10">
          <button
            onClick={() => navigate('/')}
            className="group inline-flex items-center gap-2 text-[#0066cc] hover:text-[#0052a3] font-semibold mb-5 transition-colors"
          >
            <ArrowLeft className="w-5 h-5 transition-transform group-hover:-translate-x-1" />
            Back to Home
          </button>
          <div className="rounded-3xl border border-white/70 bg-white/70 backdrop-blur-md shadow-xl p-8 md:p-10 flex flex-col md:flex-row md:items-end justify-between gap-6">
            <div>
              <p className="text-xs font-black uppercase tracking-[0.22em] text-[#0066cc] mb-3">Omnihealth Booking Desk</p>
              <h1 className="text-4xl md:text-5xl font-black text-slate-800 leading-tight mb-3">Book an Appointment</h1>
              <p className="text-slate-600 max-w-xl">Choose your specialist and secure your consultation in minutes with live availability checks.</p>

              <div className="mt-5 flex flex-wrap gap-2">
                <span className="px-3 py-1.5 rounded-full bg-blue-50 text-blue-700 text-[11px] font-bold uppercase tracking-wider border border-blue-100">Instant Confirmation</span>
                <span className="px-3 py-1.5 rounded-full bg-cyan-50 text-cyan-700 text-[11px] font-bold uppercase tracking-wider border border-cyan-100">Leave-Aware Scheduling</span>
                <span className="px-3 py-1.5 rounded-full bg-indigo-50 text-indigo-700 text-[11px] font-bold uppercase tracking-wider border border-indigo-100">Conflict-Protected</span>
              </div>
            </div>
            {!user && (
              <div className="bg-amber-50 border border-amber-200 px-4 py-3 rounded-xl text-sm text-amber-800 flex items-center gap-2 shadow-sm">
                <span className="w-2 h-2 bg-amber-500 rounded-full animate-pulse"></span>
                Booking as Guest. <button onClick={onLoginClick} className="font-bold underline">Login</button> instead?
              </div>
            )}
          </div>
        </div>

        {/* Appointment Form */}
        <div className="bg-white rounded-3xl shadow-2xl border border-white p-8 md:p-10">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="pb-2 border-b border-slate-100">
              <h2 className="text-xl font-extrabold text-slate-800">Patient Details</h2>
              <p className="text-sm text-slate-500 mt-1">We use these details for confirmation and clinic coordination.</p>
            </div>
            
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

            <div className="pt-2 pb-2 border-b border-slate-100">
              <h2 className="text-xl font-extrabold text-slate-800">Consultation Preferences</h2>
              <p className="text-sm text-slate-500 mt-1">Pick a specialty, doctor, and preferred date and time.</p>
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
                {isCheckingAvailability && (
                  <p className="text-xs text-slate-500 mt-2">Checking doctor availability...</p>
                )}
                {!!availabilityError && (
                  <p className="text-xs text-red-600 mt-2 font-semibold">{availabilityError}</p>
                )}
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
            <div className="pt-2">
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
              disabled={isSubmitting || !!availabilityError || isCheckingAvailability}
              className="w-full btn-primary py-4 font-black rounded-xl hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed shadow-xl shadow-blue-300/50"
            >
              {isSubmitting ? 'Processing...' : 'Confirm Appointment'}
            </button>
          </form>

          {/* Info Box */}
          <div className="mt-8 p-6 bg-gradient-to-r from-[#f8fafc] via-white to-[#f0f9ff] border border-slate-200 rounded-2xl flex gap-4">
            <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center shrink-0 text-blue-600 shadow-sm">
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
