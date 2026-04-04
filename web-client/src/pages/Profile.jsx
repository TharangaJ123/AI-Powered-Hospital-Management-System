import { useState, useEffect } from 'react'
import { Calendar, Clock, User as UserIcon, BadgeCheck, Loader2, ChevronLeft, ChevronRight, CheckCircle, AlertCircle } from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'
import PatientProfileCard from '../components/PatientProfileCard'
import DoctorProfileCard from '../components/DoctorProfileCard'
import { getAllDoctors, requestDoctorLeave, getDoctorLeaves } from '../services/doctors'
import { getAppointmentsByPatient, getAppointmentsByDoctor, updateAppointment, cancelAppointment, completeAppointment } from '../services/appointments'
import ReviewFormModal from '../components/ReviewFormModal'

const Profile = ({
  user,
  token,
  patientProfile,
  profileError,
  isSavingProfile,
  onSavePatientProfile,
  onLoginClick,
  onSignupClick,
  doctorProfile,
  onSaveDoctorProfile,
}) => {
  const [appointments, setAppointments] = useState([])
  const [doctors, setDoctors] = useState([])
  const [isLoadingAppointments, setIsLoadingAppointments] = useState(false)
  const [appointmentError, setAppointmentError] = useState(null)
  const [rescheduleData, setRescheduleData] = useState({ id: null, date: '' })
  const [isUpdating, setIsUpdating] = useState(false)
  const [isReviewModalOpen, setIsReviewModalOpen] = useState(false)
  const [selectedDoctorForReview, setSelectedDoctorForReview] = useState(null)
  const [activeTab, setActiveTab] = useState('profile')
  const [leaves, setLeaves] = useState([])
  const [isRequestingLeave, setIsRequestingLeave] = useState(false)
  const [leaveForm, setLeaveForm] = useState({
    startDate: '',
    endDate: '',
    reason: ''
  })

  // Calendar State
  const [viewDate, setViewDate] = useState(new Date())
  const [selectedCalendarDay, setSelectedCalendarDay] = useState(null)
  useEffect(() => {
    fetchData()
  }, [user, patientProfile?.id, token])

  const fetchData = async () => {
    if (!user || !token) return
    if (user.role === 'PATIENT' && !patientProfile?.id) return
    if (user.role === 'DOCTOR' && !doctorProfile?.id) return

    setIsLoadingAppointments(true)
    try {
      if (user.role === 'PATIENT' && patientProfile?.id) {
        const [appsData, docsData] = await Promise.all([
          getAppointmentsByPatient(patientProfile.id, token),
          getAllDoctors()
        ])
        setAppointments(appsData)
        setDoctors(docsData)
      } else if (user.role === 'DOCTOR' && doctorProfile?.id) {
        const [appsData, leavesData] = await Promise.all([
          getAppointmentsByDoctor(doctorProfile.id, token),
          getDoctorLeaves(doctorProfile.id, token)
        ])
        setAppointments(appsData)
        setLeaves(leavesData)
      }
    } catch (err) {
      setAppointmentError(err.message || 'Failed to load dashboard data')
    } finally {
      setIsLoadingAppointments(false)
    }
  }
  const handleAction = async (actionFn, appointmentId) => {
    setIsUpdating(true)
    try {
      await actionFn(appointmentId, token)
      await fetchData()
    } catch (err) {
      setAppointmentError(err.message)
    } finally {
      setIsUpdating(false)
    }
  }

  const handleReschedule = async (appointmentId) => {
    if (!rescheduleData.date || !token) return
    setIsUpdating(true)
    try {
      const appToUpdate = appointments.find(a => a.id === appointmentId)
      await updateAppointment(appointmentId, { ...appToUpdate, appointmentDate: rescheduleData.date }, token)
      setRescheduleData({ id: null, date: '' })
      await fetchData() // Refresh
    } catch (err) {
      setAppointmentError(err.message)
    } finally {
      setIsUpdating(false)
    }
  }

  const handleRequestLeave = async (e) => {
    e.preventDefault()
    if (!leaveForm.startDate || !leaveForm.endDate || !token) return
    setIsRequestingLeave(true)
    try {
      await requestDoctorLeave({
        doctorId: doctorProfile.id,
        startDate: leaveForm.startDate,
        endDate: leaveForm.endDate,
        reason: leaveForm.reason
      }, token)
      setLeaveForm({ startDate: '', endDate: '', reason: '' })
      await fetchData() // Refresh leaves list
    } catch (err) {
      setAppointmentError(err.message)
    } finally {
      setIsRequestingLeave(false)
    }
  }

  const getDoctorName = (doctorId) => {
    const cleanId = String(doctorId).replace('dr_', '')
    const doc = doctors.find(d => String(d.id) === cleanId)
    return doc ? `Dr. ${doc.firstName} ${doc.lastName}` : `Doctor ID: ${cleanId}`
  }

  const openReviewModal = (doctorId) => {
    const cleanId = String(doctorId).replace('dr_', '')
    const doc = doctors.find(d => String(d.id) === cleanId)
    if (doc) {
      setSelectedDoctorForReview(doc)
      setIsReviewModalOpen(true)
    }
  }

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'PENDING': return 'bg-blue-100 text-blue-700 border-blue-200'
      case 'BOOKED': return 'bg-indigo-100 text-indigo-700 border-indigo-200'
      case 'SCHEDULED': return 'bg-sky-100 text-sky-700 border-sky-200'
      case 'COMPLETED': return 'bg-green-100 text-green-700 border-green-200'
      case 'CANCELLED': return 'bg-red-100 text-red-700 border-red-200'
      default: return 'bg-slate-100 text-slate-700 border-slate-200'
    }
  }

  // Calendar Helpers
  const daysInMonth = (year, month) => new Date(year, month + 1, 0).getDate()
  const firstDayOfMonth = (year, month) => new Date(year, month, 1).getDay()

  const generateCalendarDays = () => {
    const year = viewDate.getFullYear()
    const month = viewDate.getMonth()
    const totalDays = daysInMonth(year, month)
    const firstDay = firstDayOfMonth(year, month)

    const days = []
    // Padding for previous month
    for (let i = 0; i < firstDay; i++) {
      days.push({ day: null, currentMonth: false })
    }
    // Days of current month
    for (let i = 1; i <= totalDays; i++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`
      const dayAppointments = (appointments || []).filter(app => app.appointmentDate && app.appointmentDate.startsWith(dateStr))
      days.push({
        day: i,
        currentMonth: true,
        dateStr,
        hasAppointments: dayAppointments.length > 0,
        appointments: dayAppointments
      })
    }
    return days
  }

  const changeMonth = (offset) => {
    const newDate = new Date(viewDate)
    newDate.setMonth(newDate.getMonth() + offset)
    setViewDate(newDate)
    setSelectedCalendarDay(null)
  }

  if (!user) {
    return (
      <main className="w-full pt-32 pb-20 px-4">
        <section className="max-w-3xl mx-auto rounded-2xl border border-slate-200 bg-white shadow-sm p-8 text-center">
          <h1 className="text-2xl md:text-3xl font-extrabold text-slate-900">My Profile</h1>
          <p className="mt-3 text-slate-600">Please sign in to view and manage your profile.</p>
          <div className="mt-6 flex items-center justify-center gap-3">
            <button
              type="button"
              onClick={onLoginClick}
              className="px-5 py-2.5 text-sm font-semibold rounded-full border border-slate-200 text-slate-700 hover:text-[#0066cc] hover:border-[#0066cc]/40 transition-colors"
            >
              Login
            </button>
            <button
              type="button"
              onClick={onSignupClick}
              className="btn-secondary text-sm px-5 py-2.5"
            >
              Sign Up
            </button>
          </div>
        </section>
      </main>
    )
  }

  return (
    <main className="w-full pt-32 pb-12">

      {user?.role === 'PATIENT' && (
        <>
          <PatientProfileCard
            profile={patientProfile}
            onSave={onSavePatientProfile}
            isSaving={isSavingProfile}
            error={profileError}
            sectionId="patient-profile-form"
          />

          <section className="max-w-7xl mx-auto px-4 mt-8 pb-20">
            <div className="rounded-2xl border border-slate-200 bg-white shadow-sm p-6 overflow-hidden">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h2 className="text-xl font-extrabold text-slate-900 flex items-center gap-2">
                    <Calendar className="w-6 h-6 text-[#0066cc]" />
                    My Appointments
                  </h2>
                  <p className="text-sm text-slate-500 mt-1">Track and manage your scheduled hospital visits.</p>
                </div>
              </div>

              {isLoadingAppointments ? (
                <div className="flex flex-col items-center justify-center py-12 text-slate-400">
                  <Loader2 className="w-10 h-10 animate-spin mb-4 text-[#0066cc]" />
                  <p className="text-sm">Fetching your history...</p>
                </div>
              ) : appointmentError ? (
                <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm flex items-center gap-2">
                  <p>{appointmentError}</p>
                </div>
              ) : (appointments || []).length === 0 ? (
                <div className="text-center py-12 rounded-xl border border-dashed border-slate-200">
                  <Calendar className="w-12 h-12 text-slate-200 mx-auto mb-3" />
                  <h3 className="text-slate-900 font-semibold italic">No appointments found</h3>
                  <p className="text-slate-500 text-sm mt-1">When you book a consultation, it will appear here.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {(appointments || []).map((app) => (
                    <div key={app?.id} className="relative group rounded-2xl border border-slate-200 p-5 hover:border-[#0066cc]/40 hover:shadow-md transition-all">
                      <div className="flex items-start justify-between mb-4">
                        <div className="p-2 rounded-xl bg-slate-50 text-slate-400">
                          <Calendar className="w-5 h-5" />
                        </div>
                        <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider border ${getStatusColor(app.status)}`}>
                          {app.status}
                        </span>
                      </div>

                      <h4 className="font-bold text-slate-900 flex items-center gap-2">
                        <UserIcon className="w-4 h-4 text-[#0066cc]" />
                        {getDoctorName(app.doctorId)}
                      </h4>

                      <div className="mt-4 space-y-2 text-sm text-slate-600">
                        <div className="flex items-center gap-2">
                          <Calendar className="w-4 h-4 text-slate-400" />
                          <span>{new Date(app.appointmentDate).toLocaleDateString(undefined, {
                            weekday: 'long',
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric'
                          })}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Clock className="w-4 h-4 text-slate-400" />
                          <span>{new Date(app.appointmentDate).toLocaleTimeString(undefined, {
                            hour: '2-digit',
                            minute: '2-digit'
                          })}</span>
                        </div>
                      </div>

                      <div className="mt-5 pt-4 border-t border-slate-100">
                        {rescheduleData.id === app.id ? (
                          <div className="flex flex-col gap-3">
                            <input
                              type="datetime-local"
                              className="w-full text-xs p-2 rounded-lg border border-slate-200 outline-none focus:ring-1 focus:ring-[#0066cc]"
                              value={rescheduleData.date}
                              onChange={(e) => setRescheduleData({ ...rescheduleData, date: e.target.value })}
                            />
                            <div className="flex items-center gap-2">
                              <button
                                onClick={() => handleReschedule(app.id)}
                                disabled={isUpdating}
                                className="flex-1 py-1.5 text-xs font-bold rounded-lg bg-[#0066cc] text-white hover:bg-[#004d99] disabled:opacity-50"
                              >
                                {isUpdating ? 'Saving...' : 'Confirm'}
                              </button>
                              <button
                                onClick={() => setRescheduleData({ id: null, date: '' })}
                                className="flex-1 py-1.5 text-xs font-bold rounded-lg bg-slate-100 text-slate-400"
                              >
                                Cancel
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-1.5 text-[#0066cc] text-xs font-semibold">
                              <BadgeCheck className="w-4 h-4" />
                              Verified
                            </div>
                            {['PENDING', 'SCHEDULED', 'BOOKED'].includes(app?.status?.toUpperCase()) && canReschedule(app?.appointmentDate) && (
                              <button
                                onClick={() => setRescheduleData({ id: app.id, date: app.appointmentDate })}
                                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-[#0066cc]/10 text-[#0066cc] text-xs font-bold hover:bg-[#0066cc] hover:text-white transition-all"
                              >
                                <Calendar className="w-3.5 h-3.5" />
                                Reschedule
                              </button>
                            )}
                            {app?.status?.toUpperCase() === 'COMPLETED' && (
                              <button
                                onClick={() => openReviewModal(app.doctorId)}
                                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-amber-500/10 text-amber-600 text-xs font-bold hover:bg-amber-500 hover:text-white transition-all shadow-sm"
                              >
                                <Star className="w-3.5 h-3.5 fill-amber-500 border-none" />
                                Review
                              </button>
                            )}
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Premium Calendar View */}
            <div className="mt-8 grid lg:grid-cols-3 gap-8">
              <div className="lg:col-span-2 rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
                <div className="p-6 bg-slate-50/50 border-b border-slate-200 flex items-center justify-between">
                  <h3 className="font-extrabold text-slate-900 flex items-center gap-2">
                    <Calendar className="w-5 h-5 text-[#0066cc]" />
                    Interactive Schedule
                  </h3>
                  <div className="flex items-center gap-4">
                    <span className="text-sm font-bold text-slate-600">
                      {viewDate.toLocaleDateString(undefined, { month: 'long', year: 'numeric' })}
                    </span>
                    <div className="flex items-center gap-1">
                      <button onClick={() => changeMonth(-1)} className="p-1.5 rounded-lg hover:bg-slate-200 transition-colors">
                        <ChevronLeft className="w-5 h-5" />
                      </button>
                      <button onClick={() => changeMonth(1)} className="p-1.5 rounded-lg hover:bg-slate-200 transition-colors">
                        <ChevronRight className="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                </div>

                <div className="p-6">
                  <div className="grid grid-cols-7 gap-2 mb-4">
                    {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(d => (
                      <div key={d} className="text-center text-[11px] font-bold uppercase tracking-wider text-slate-400">{d}</div>
                    ))}
                  </div>
                  <div className="grid grid-cols-7 gap-2">
                    {generateCalendarDays().map((item, idx) => (
                      <button
                        key={idx}
                        disabled={!item.day}
                        onClick={() => setSelectedCalendarDay(item)}
                        className={`
                          relative h-14 md:h-20 rounded-xl transition-all flex flex-col items-center justify-center gap-1
                          ${!item.day ? 'bg-transparent' : 'bg-white hover:bg-[#0066cc]/5 border border-slate-100 hover:border-[#0066cc]/20'}
                          ${selectedCalendarDay?.dateStr === item.dateStr ? 'border-[#0066cc] bg-[#0066cc]/5 ring-2 ring-[#0066cc]/10' : ''}
                          ${item.day && new Date().toDateString() === new Date(item.dateStr).toDateString() ? 'bg-blue-50 text-[#0066cc]' : ''}
                        `}
                      >
                        {item.day && (
                          <>
                            <span className={`text-sm font-bold ${item.day && new Date().toDateString() === new Date(item.dateStr).toDateString() ? 'text-[#0066cc]' : 'text-slate-700'}`}>
                              {item.day}
                            </span>
                            {item.hasAppointments && (
                              <div className="flex gap-0.5">
                                {item.appointments.slice(0, 3).map((_, i) => (
                                  <div key={i} className="w-1.5 h-1.5 rounded-full bg-[#0066cc]" />
                                ))}
                              </div>
                            )}
                          </>
                        )}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              {/* Day Details Side Panel */}
              <div className="rounded-2xl border border-slate-200 bg-white shadow-sm p-6">
                <h3 className="font-extrabold text-slate-900 border-b border-slate-100 pb-4 mb-4">
                  Day Details
                </h3>
                {!selectedCalendarDay ? (
                  <div className="text-center py-12 text-slate-400">
                    <Calendar className="w-10 h-10 mx-auto mb-3 opacity-20" />
                    <p className="text-sm italic">Select a date from the calendar to view its schedule.</p>
                  </div>
                ) : selectedCalendarDay.appointments.length === 0 ? (
                  <div className="text-center py-12 text-slate-400 animate-in fade-in slide-in-from-bottom-2">
                    <CheckCircle className="w-10 h-10 mx-auto mb-3 text-green-200" />
                    <p className="text-sm font-semibold text-slate-600">Free Day!</p>
                    <p className="text-xs mt-1">No appointments scheduled for this date.</p>
                  </div>
                ) : (
                  <div className="space-y-4 animate-in fade-in slide-in-from-right-4">
                    <div className="flex items-center gap-2 text-[#0066cc] font-bold text-sm mb-6">
                      <Calendar className="w-4 h-4" />
                      {new Date(selectedCalendarDay.dateStr).toLocaleDateString(undefined, {
                        month: 'short', day: 'numeric', year: 'numeric'
                      })}
                    </div>
                    {selectedCalendarDay?.appointments?.map(app => (
                      <div key={app?.id} className="p-4 rounded-xl border border-slate-200 bg-slate-50 hover:border-[#0066cc]/40 transition-all">
                        <p className="text-xs font-bold text-[#0066cc] uppercase tracking-wider mb-2">{app?.status}</p>
                        <p className="text-sm font-extrabold text-slate-900 mb-2">{getDoctorName(app?.doctorId)}</p>
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-3 text-xs text-slate-500 font-medium">
                            <div className="flex items-center gap-1">
                              <Clock className="w-3.5 h-3.5" />
                              {app?.appointmentDate && new Date(app.appointmentDate).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })}
                            </div>
                          </div>
                          {['PENDING', 'SCHEDULED', 'BOOKED'].includes(app?.status?.toUpperCase()) && canReschedule(app?.appointmentDate) && (
                            <button
                              onClick={() => setRescheduleData({ id: app.id, date: app.appointmentDate })}
                              className="flex items-center gap-1 px-2 py-1 rounded-md bg-[#0066cc]/5 text-[#0066cc] text-[10px] font-extrabold hover:bg-[#0066cc] hover:text-white transition-all uppercase"
                            >
                              <Calendar className="w-3 h-3" />
                              Edit
                            </button>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Review Modal */}
            {selectedDoctorForReview && (
              <ReviewFormModal
                isOpen={isReviewModalOpen}
                onClose={() => setIsReviewModalOpen(false)}
                doctor={selectedDoctorForReview}
                patient={patientProfile}
                token={token}
                onReviewSubmitted={fetchData}
              />
            )}
          </section>
        </>
      )}
      {user?.role === 'DOCTOR' && (
        <div className="max-w-7xl mx-auto px-4 mt-8">
          {/* Dashboard Tabs */}
          <div className="flex items-center bg-slate-100 p-2 rounded-[2rem] w-fit mb-12 border border-slate-200 shadow-inner">
            <button
              onClick={() => setActiveTab('profile')}
              className={`flex items-center space-x-3 px-10 py-4 rounded-[1.5rem] text-xs font-black uppercase tracking-widest transition-all duration-300 ${
                activeTab === 'profile' 
                ? 'bg-[#002d5a] text-white shadow-xl scale-105' 
                : 'text-slate-500 hover:text-slate-800 hover:bg-slate-200/50'
              }`}
            >
              <UserIcon className={`w-4 h-4 ${activeTab === 'profile' ? 'text-blue-300' : 'text-slate-400'}`} />
              <span>Clinical Profile</span>
            </button>
            <button
              onClick={() => setActiveTab('appointments')}
              className={`flex items-center space-x-3 px-10 py-4 rounded-[1.5rem] text-xs font-black uppercase tracking-widest transition-all duration-300 ${
                activeTab === 'appointments' 
                ? 'bg-[#002d5a] text-white shadow-xl scale-105' 
                : 'text-slate-500 hover:text-slate-800 hover:bg-slate-200/50'
              }`}
            >
              <Clock className={`w-4 h-4 ${activeTab === 'appointments' ? 'text-blue-300' : 'text-slate-400'}`} />
              <span>Patient Consultations</span>
            </button>
            <button
              onClick={() => setActiveTab('leaves')}
              className={`flex items-center space-x-3 px-10 py-4 rounded-[1.5rem] text-xs font-black uppercase tracking-widest transition-all duration-300 ${
                activeTab === 'leaves' 
                ? 'bg-[#002d5a] text-white shadow-xl scale-105' 
                : 'text-slate-500 hover:text-slate-800 hover:bg-slate-200/50'
              }`}
            >
              <Calendar className={`w-4 h-4 ${activeTab === 'leaves' ? 'text-blue-300' : 'text-slate-400'}`} />
              <span>Manage Leaves</span>
            </button>
          </div>

          <AnimatePresence mode="wait">
            {activeTab === 'profile' ? (
              <motion.div
                key="profile"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ duration: 0.4 }}
              >
                <DoctorProfileCard
                  profile={doctorProfile}
                  onSave={onSaveDoctorProfile}
                  isSaving={isSavingProfile}
                  error={profileError}
                />
              </motion.div>
            ) : activeTab === 'leaves' ? (
              <motion.div
                key="leaves"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ duration: 0.4 }}
                className="space-y-12 pb-20"
              >
                {/* Leave Request Form */}
                <div className="bg-white rounded-[3rem] p-10 md:p-16 border border-slate-100 shadow-xl space-y-10">
                  <div className="space-y-2">
                    <h2 className="text-3xl font-black text-[#002d5a]">Request Time-Off</h2>
                    <p className="text-slate-500 font-medium">Submit your leave request for administrative approval.</p>
                  </div>

                  <form onSubmit={handleRequestLeave} className="grid md:grid-cols-2 gap-8 items-end">
                    <div className="space-y-4">
                      <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">Start Date</label>
                      <input 
                        type="date" 
                        required
                        value={leaveForm.startDate}
                        onChange={(e) => setLeaveForm({...leaveForm, startDate: e.target.value})}
                        className="w-full px-6 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-blue-100 outline-none transition-all font-bold text-[#002d5a]"
                      />
                    </div>
                    <div className="space-y-4">
                      <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">End Date</label>
                      <input 
                        type="date" 
                        required
                        value={leaveForm.endDate}
                        onChange={(e) => setLeaveForm({...leaveForm, endDate: e.target.value})}
                        className="w-full px-6 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-blue-100 outline-none transition-all font-bold text-[#002d5a]"
                      />
                    </div>
                    <div className="md:col-span-2 space-y-4">
                      <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">Reason (Optional)</label>
                      <textarea 
                        value={leaveForm.reason}
                        onChange={(e) => setLeaveForm({...leaveForm, reason: e.target.value})}
                        placeholder="Clinical conferences, personal reasons, etc."
                        rows="3"
                        className="w-full px-6 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-blue-100 outline-none transition-all font-medium text-slate-600 resize-none"
                      />
                    </div>
                    <div className="md:col-span-2">
                      <button 
                        type="submit"
                        disabled={isRequestingLeave}
                        className="w-full md:w-fit px-12 py-5 bg-[#002d5a] text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-[#003d7a] transition-all shadow-xl shadow-blue-900/10 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        {isRequestingLeave ? 'Submitting Request...' : 'Submit Leave Request'}
                      </button>
                    </div>
                  </form>
                </div>

                {/* Leaves History */}
                <div className="space-y-6">
                  <h3 className="text-xl font-black text-[#002d5a] px-2">Leave History & Status</h3>
                  {leaves.length > 0 ? (
                    <div className="grid gap-4">
                      {leaves.map((leave, i) => (
                        <div key={leave.id || i} className="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-6 hover:shadow-md transition-shadow">
                          <div className="flex items-center space-x-6">
                             <div className={`p-4 rounded-2xl ${
                               leave.status === 'APPROVED' ? 'bg-green-50 text-green-600' : 
                               leave.status === 'REJECTED' ? 'bg-red-50 text-red-600' : 
                               'bg-amber-50 text-amber-600'
                             }`}>
                               <Calendar className="w-6 h-6" />
                             </div>
                             <div>
                                <p className="text-lg font-extrabold text-[#002d5a]">
                                  {new Date(leave.startDate).toLocaleDateString()} - {new Date(leave.endDate).toLocaleDateString()}
                                </p>
                                <p className="text-sm text-slate-500 font-medium">{leave.reason || 'No reason provided'}</p>
                             </div>
                          </div>
                          <div className="flex items-center space-x-4">
                             <span className={`px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-widest ${
                               leave.status === 'APPROVED' ? 'bg-green-100 text-green-700' : 
                               leave.status === 'REJECTED' ? 'bg-red-100 text-red-700' : 
                               'bg-amber-100 text-amber-700'
                             }`}>
                               {leave.status}
                             </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-20 bg-slate-50 border-2 border-dashed border-slate-200 rounded-[3rem]">
                       <Calendar className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                       <p className="text-slate-500 font-bold uppercase tracking-widest text-xs">No leave requests found</p>
                    </div>
                  )}
                </div>
              </motion.div>
            ) : (
              <motion.div
                key="appointments"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ duration: 0.4 }}
                className="pb-20"
              >
                <div className="rounded-[2.5rem] border border-slate-200 bg-white shadow-sm p-8 md:p-12 overflow-hidden">
                  <div className="flex items-center justify-between mb-12">
                    <div>
                      <h2 className="text-3xl font-black text-[#002d5a] flex items-center gap-4">
                        <Clock className="w-10 h-10 text-[#0066cc]" />
                        Consultation Schedule
                      </h2>
                      <p className="text-slate-500 mt-2 font-medium text-lg">Manage your patient stream and visit status in real-time.</p>
                    </div>
                  </div>

                  {isLoadingAppointments ? (
                    <div className="flex flex-col items-center justify-center py-32 text-slate-400">
                      <Loader2 className="w-16 h-16 animate-spin mb-6 text-[#0066cc]" />
                      <p className="text-sm font-black uppercase tracking-widest">Hydrating Schedule...</p>
                    </div>
                  ) : appointmentError ? (
                    <div className="p-8 rounded-3xl bg-red-50 border border-red-100 text-red-600 font-bold flex items-center gap-4 shadow-sm">
                      <AlertCircle className="w-8 h-8" />
                      <p className="text-lg">{appointmentError}</p>
                    </div>
                  ) : (appointments || []).length === 0 ? (
                    <div className="text-center py-32 rounded-[3.5rem] border-2 border-dashed border-slate-100 bg-slate-50/50">
                      <div className="w-24 h-24 bg-white rounded-full flex items-center justify-center mx-auto mb-8 shadow-sm">
                        <Calendar className="w-10 h-10 text-slate-200" />
                      </div>
                      <h3 className="text-slate-900 text-2xl font-black italic">No Patient Visits Logged</h3>
                      <p className="text-slate-500 mt-3 max-w-sm mx-auto font-medium text-lg">Your queue is currently empty. New patient bookings will appear here instantly.</p>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-10">
                      {(appointments || []).map((app) => (
                        <div key={app?.id} className="relative group rounded-[2.5rem] border border-slate-100 p-8 bg-white hover:border-[#0066cc]/20 hover:shadow-2xl hover:shadow-blue-500/5 transition-all duration-700 flex flex-col">
                          <div className="flex items-start justify-between mb-8">
                            <div className="w-16 h-16 rounded-3xl bg-slate-50 flex items-center justify-center text-[#0066cc] group-hover:bg-[#0066cc] group-hover:text-white transition-all duration-700 shadow-sm">
                              <UserIcon className="w-8 h-8" />
                            </div>
                            <span className={`px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-widest border shadow-sm ${getStatusColor(app.status)}`}>
                              {app.status}
                            </span>
                          </div>

                          <div className="space-y-6 flex-grow">
                            <div>
                              <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1.5">Scheduled Patient</p>
                              <h4 className="text-2xl font-black text-[#002d5a] tracking-tight">{app.patientName}</h4>
                            </div>

                            <div className="grid grid-cols-2 gap-6 pt-6 border-t border-slate-50">
                              <div>
                                <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2">Visit Date</p>
                                <div className="flex items-center gap-2.5 text-sm font-bold text-slate-700">
                                   <Calendar className="w-4 h-4 text-slate-400" />
                                   {new Date(app.appointmentDate).toLocaleDateString()}
                                </div>
                              </div>
                              <div>
                                <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2">Arrival Time</p>
                                <div className="flex items-center gap-2.5 text-sm font-bold text-slate-700">
                                   <Clock className="w-4 h-4 text-slate-400" />
                                   {new Date(app.appointmentDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </div>
                              </div>
                            </div>
                          </div>

                          <div className="mt-10 pt-8 border-t border-slate-50 flex gap-4">
                             {app.status === 'PENDING' && (
                               <button 
                                 onClick={() => handleAction(completeAppointment, app.id)}
                                 className="flex-1 py-4 bg-[#00a69c] text-white text-[11px] font-black uppercase tracking-widest rounded-2xl hover:bg-[#008d84] transition-all shadow-xl shadow-cyan-500/20 active:scale-95"
                               >
                                 Complete Visit
                               </button>
                             )}
                             {['PENDING', 'SCHEDULED'].includes(app.status?.toUpperCase()) && (
                               <button 
                                 onClick={() => handleAction(cancelAppointment, app.id)}
                                 className="flex-1 py-4 bg-red-50 text-red-600 text-[11px] font-black uppercase tracking-widest rounded-2xl hover:bg-red-100 transition-all font-bold active:scale-95"
                               >
                                 Deny Visit
                               </button>
                             )}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      )}
    </main>
  )
}

export default Profile
