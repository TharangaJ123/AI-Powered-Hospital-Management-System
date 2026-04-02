import { useState, useEffect } from 'react'
import { Calendar, Clock, User as UserIcon, BadgeCheck, Loader2, ChevronLeft, ChevronRight } from 'lucide-react'
import PatientProfileCard from '../components/PatientProfileCard'
import { getAppointmentsByPatient } from '../services/appointments'
import { getAllDoctors } from '../services/doctors'

const Profile = ({
  user,
  token,
  patientProfile,
  profileError,
  isSavingProfile,
  onSavePatientProfile,
  onLoginClick,
  onSignupClick,
}) => {
  const [appointments, setAppointments] = useState([])
  const [doctors, setDoctors] = useState([])
  const [isLoadingAppointments, setIsLoadingAppointments] = useState(false)
  const [appointmentError, setAppointmentError] = useState(null)
  
  // Calendar State
  const [viewDate, setViewDate] = useState(new Date())
  const [selectedCalendarDay, setSelectedCalendarDay] = useState(null)
  useEffect(() => {
    const fetchData = async () => {
      if (!user || user.role !== 'PATIENT' || !patientProfile?.id || !token) return

      setIsLoadingAppointments(true)
      try {
        const [appsData, docsData] = await Promise.all([
          getAppointmentsByPatient(patientProfile.id, token),
          getAllDoctors()
        ])
        setAppointments(appsData)
        setDoctors(docsData)
      } catch (err) {
        setAppointmentError(err.message || 'Failed to load appointments')
      } finally {
        setIsLoadingAppointments(false)
      }
    }

    fetchData()
  }, [user, patientProfile?.id, token])

  const getDoctorName = (doctorId) => {
    // doctorId comes as 'dr_123' in the form but backend might return it differently or just the numeric ID.
    // Let's normalize it.
    const cleanId = String(doctorId).replace('dr_', '')
    const doc = doctors.find(d => String(d.id) === cleanId)
    return doc ? `Dr. ${doc.firstName} ${doc.lastName}` : `Doctor ID: ${cleanId}`
  }

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'SCHEDULED': return 'bg-blue-100 text-blue-700 border-blue-200'
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
      const dayAppointments = appointments.filter(app => app.appointmentDate.startsWith(dateStr))
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
      <section className="max-w-7xl mx-auto px-4 mb-8 w-full">
        <div className="rounded-2xl border border-slate-200 bg-white shadow-sm p-6">
          <h1 className="text-lg font-extrabold text-slate-900">My Profile</h1>
          <p className="text-sm text-slate-500 mt-1">View your account details and manage your profile information.</p>
          <div className="mt-4 grid md:grid-cols-3 gap-4 text-sm">
            <div className="rounded-xl bg-slate-50 border border-slate-200 px-4 py-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Name</p>
              <p className="mt-1 font-semibold text-slate-900">
                {user?.name || user?.fullName || 'Not provided'}
              </p>
            </div>
            <div className="rounded-xl bg-slate-50 border border-slate-200 px-4 py-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Email</p>
              <p className="mt-1 font-semibold text-slate-900 break-all">{user?.email || 'Not provided'}</p>
            </div>
            <div className="rounded-xl bg-slate-50 border border-slate-200 px-4 py-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Role</p>
              <p className="mt-1 font-semibold text-slate-900">{user?.role || 'PATIENT'}</p>
            </div>
          </div>
        </div>
      </section>

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
              ) : appointments.length === 0 ? (
                <div className="text-center py-12 rounded-xl border border-dashed border-slate-200">
                  <Calendar className="w-12 h-12 text-slate-200 mx-auto mb-3" />
                  <h3 className="text-slate-900 font-semibold italic">No appointments found</h3>
                  <p className="text-slate-500 text-sm mt-1">When you book a consultation, it will appear here.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {appointments.map((app) => (
                    <div key={app.id} className="relative group rounded-2xl border border-slate-200 p-5 hover:border-[#0066cc]/40 hover:shadow-md transition-all">
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

                      <div className="mt-5 pt-4 border-t border-slate-100 flex items-center justify-between">
                        <span className="text-[10px] text-slate-400 font-medium font-mono uppercase">Ref: #{app.id}</span>
                        <div className="flex items-center gap-1.5 text-[#0066cc] text-xs font-semibold">
                          <BadgeCheck className="w-4 h-4" />
                          Verified
                        </div>
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
                    {selectedCalendarDay.appointments.map(app => (
                      <div key={app.id} className="p-4 rounded-xl border border-slate-200 bg-slate-50 hover:border-[#0066cc]/40 transition-all">
                        <p className="text-xs font-bold text-[#0066cc] uppercase tracking-wider mb-2">{app.status}</p>
                        <p className="text-sm font-extrabold text-slate-900 mb-2">{getDoctorName(app.doctorId)}</p>
                        <div className="flex items-center gap-3 text-xs text-slate-500 font-medium">
                          <div className="flex items-center gap-1">
                            <Clock className="w-3.5 h-3.5" />
                            {new Date(app.appointmentDate).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </section>
        </>
      )}
    </main>
  )
}

export default Profile
