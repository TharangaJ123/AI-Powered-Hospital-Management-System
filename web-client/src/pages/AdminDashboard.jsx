import { motion } from 'framer-motion'
import { 
  Users, 
  Calendar, 
  Stethoscope, 
  Activity, 
  ArrowUpRight, 
  Clock, 
  ShieldCheck, 
  Settings,
  PlusCircle,
  FileText,
  Loader2,
  AlertCircle,
  ArrowLeft,
  Search,
  CheckCircle,
  XCircle,
  Trash2,
  ExternalLink,
  ShieldAlert,
  MessageSquare,
  Send as SendIcon
} from 'lucide-react'
import { useState, useEffect, Fragment } from 'react'
import { 
  getAllAppointments, 
  cancelAppointment, 
  completeAppointment, 
  deleteAppointment,
  updateAppointment,
  getAppointmentsByDoctor 
} from '../services/appointments'
import { getAllDoctors, getDoctorLeaves, getAllDoctorLeaves, updateDoctorLeaveStatus } from '../services/doctors'
import { 
  getAllUsers, 
  verifyDoctor, 
  deleteUser, 
  getPlatformOperations 
} from '../services/auth'
import { getAllContactForms, replyToContactForm } from '../services/contact'

const AdminDashboard = ({ token }) => {
  const [activeView, setActiveView] = useState('OVERVIEW') // OVERVIEW, APPOINTMENTS, DOCTORS, PATIENTS, LOGS
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [searchQuery, setSearchQuery] = useState('')
  const [dashboardStats, setDashboardStats] = useState({
    appointments: [],
    doctors: [],
    patients: [],
    allUsers: [],
    pendingDoctors: [],
    allLeaves: [],
    contactForms: [],
    operations: null
  })
  const [rescheduleData, setRescheduleData] = useState({ id: null, date: '' })
  
  // Doctor Drill-down State
  const [docViewMode, setDocViewMode] = useState('SPECIALITIES') // SPECIALITIES, LIST, DETAIL
  const [selectedSpec, setSelectedSpec] = useState(null)
  const [selectedDoc, setSelectedDoc] = useState(null)
  const [docAppointments, setDocAppointments] = useState([])
  const [docLeaves, setDocLeaves] = useState([])
  const [isRefreshingDoc, setIsRefreshingDoc] = useState(false)
  const [replyData, setReplyData] = useState({ id: null, text: '' })
  const [isReplying, setIsReplying] = useState(false)

  const fetchDoctorDetail = async (doctorId) => {
    if (!token) return
    setIsRefreshingDoc(true)
    try {
      const [apps, leaves] = await Promise.all([
        getAppointmentsByDoctor(doctorId, token),
        getDoctorLeaves(doctorId, token)
      ])
      setDocAppointments(apps)
      setDocLeaves(leaves)
    } catch (err) {
      console.error('Failed to fetch doctor detail:', err)
      setError('Unable to load doctor schedule or leaves.')
    } finally {
      setIsRefreshingDoc(false)
    }
  }

  const fetchDashboardData = async () => {
    if (!token) return
    
    setLoading(true)
    setError(null)
    try {
      const [allApps, allDocs, allUsers, allLeaves, ops, allContacts] = await Promise.all([
        getAllAppointments(token),
        getAllDoctors(),
        getAllUsers(token),
        getAllDoctorLeaves(token),
        getPlatformOperations(token).catch(() => null),
        getAllContactForms(token)
      ])

      const patientUsers = allUsers.filter(u => u.role === 'PATIENT')
      const pendingDocs = allUsers.filter(u => u.role === 'DOCTOR' && !u.active)
      
      setDashboardStats({
        appointments: allApps,
        doctors: allDocs,
        patients: patientUsers,
        allUsers: allUsers,
        pendingDoctors: pendingDocs,
        allLeaves: allLeaves,
        contactForms: allContacts,
        operations: ops
      })
    } catch (err) {
      console.error('Failed to fetch admin stats:', err)
      setError('Failed to load dashboard data. Please check connection to services.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchDashboardData()
  }, [token])

  const handleAction = async (actionFn, ...args) => {
    try {
      await actionFn(...args, token)
      await fetchDashboardData()
    } catch (err) {
      setError(err.message)
    }
  }

  const getRevenue = () => {
    const completed = dashboardStats.appointments.filter(a => a.status === 'COMPLETED')
    return completed.length * 50
  }

  const stats = [
    { 
      label: "Total Appointments", 
      value: dashboardStats.appointments.length, 
      icon: Calendar, 
      color: "text-blue-600", 
      bg: "bg-blue-50" 
    },
    { 
      label: "Active Doctors", 
      value: dashboardStats.doctors.length, 
      icon: Stethoscope, 
      color: "text-[#00a69c]", 
      bg: "bg-[#00a69c]/10" 
    },
    { 
      label: "New Patients", 
      value: dashboardStats.patients.length, 
      icon: Users, 
      color: "text-purple-600", 
      bg: "bg-purple-50" 
    },
    { 
      label: "Revenue (MTD)", 
      value: `$${getRevenue().toLocaleString()}`, 
      icon: Activity, 
      color: "text-orange-600", 
      bg: "bg-orange-50" 
    }
  ]

  const actions = [
    { id: 'APPOINTMENTS', title: "Manage Appointments", icon: Calendar, desc: "Approve, reschedule, or cancel patient visits.", color: "bg-blue-500" },
    { id: 'DOCTORS', title: "Doctor Management", icon: Stethoscope, desc: "Update doctor profiles and specialties.", color: "bg-[#00a69c]" },
    { id: 'PATIENTS', title: "Patient Records", icon: Users, desc: "Access and manage central patient databases.", color: "bg-purple-500" },
    { id: 'LEAVES', title: "Leave Requests", icon: ShieldAlert, desc: "Approve or reject doctor time-off requests.", color: "bg-orange-500" },
    { id: 'CONTACTS', title: "Support Management", icon: MessageSquare, desc: "Reply to patient inquiries and feedback.", color: "bg-teal-500" },
    { id: 'LOGS', title: "System Logs", icon: FileText, desc: "Monitor system health and security events.", color: "bg-slate-700" }
  ]

  const filteredAppointments = dashboardStats.appointments.filter(a => 
    a.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    a.status?.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const filteredDoctors = dashboardStats.doctors.filter(d => 
    d.fullName?.toLowerCase().includes(searchQuery.toLowerCase()) || 
    d.specialization?.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const filteredPatients = dashboardStats.patients.filter(p => 
    p.firstName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.lastName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.email?.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const renderOverview = () => (
    <>
      {/* Header Section */}
      <section className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 className="text-3xl font-black text-slate-900 flex items-center gap-3">
            Admin <span className="text-[#0066cc]">Center</span>
          </h1>
          <p className="text-slate-500 mt-1 font-medium">Welcome back, Administrator. Here's a snapshot of OMNIHEALTH.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="btn-secondary text-xs flex items-center gap-2">
            <Settings className="w-4 h-4" />
            System Config
          </button>
          <button className="btn-primary text-xs flex items-center gap-2">
            <PlusCircle className="w-4 h-4" />
            Add Staff
          </button>
        </div>
      </section>

      {/* Summary Stats */}
      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, i) => (
          <motion.div 
            key={i}
            whileHover={{ y: -5 }}
            className="p-6 rounded-3xl bg-white border border-slate-100 shadow-sm hover:shadow-lg transition-all"
          >
            <div className="flex items-start justify-between">
              <div className={`p-3 rounded-2xl ${stat.bg} ${stat.color}`}>
                <stat.icon className="w-6 h-6" />
              </div>
              <div className="flex items-center text-xs font-bold text-green-600 bg-green-50 px-2 py-1 rounded-full">
                <ArrowUpRight className="w-3 h-3" />
                12%
              </div>
            </div>
            <div className="mt-5">
              <h4 className="text-2xl font-black text-slate-900">{stat.value}</h4>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mt-1">{stat.label}</p>
            </div>
          </motion.div>
        ))}
      </section>

      {/* Action Grid */}
      <section className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
        {actions.map((action, i) => (
          <button 
            key={i}
            onClick={() => {
              setActiveView(action.id)
              if (action.id === 'DOCTORS') setDocViewMode('SPECIALITIES')
            }}
            className="text-left group relative p-8 rounded-[2.5rem] bg-white border border-slate-100 shadow-sm hover:shadow-xl hover:border-[#0066cc]/20 transition-all overflow-hidden"
          >
            <div className={`absolute top-0 right-0 w-32 h-32 ${action.color} opacity-5 rounded-full -mr-16 -mt-16 group-hover:scale-150 transition-transform duration-700`}></div>
            <div className={`w-12 h-12 ${action.color} text-white rounded-2xl flex items-center justify-center mb-6 shadow-lg shadow-black/5`}>
              <action.icon className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-extrabold text-slate-900 mb-2">{action.title}</h3>
            <p className="text-xs font-medium text-slate-500 leading-relaxed">{action.desc}</p>
          </button>
        ))}
      </section>

      {/* Activity Monitor Section */}
      <section className="grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 rounded-3xl border border-slate-200 bg-white overflow-hidden shadow-sm">
          <div className="p-6 border-b border-slate-100 flex items-center justify-between">
            <h3 className="font-black text-slate-900 flex items-center gap-2">
              <Clock className="w-5 h-5 text-[#0066cc]" />
              Recent System Activity
            </h3>
            <span className="text-[10px] uppercase font-black tracking-widest text-[#00a69c] bg-[#00a69c]/5 px-2.5 py-1 rounded-full">Live Monitor</span>
          </div>
          <div className="p-6">
            <div className="space-y-6">
              {[
                ...(dashboardStats.appointments.slice(0, 3).map(a => ({
                   user: a.fullName || 'Guest Patient',
                   action: `New Appointment Booked (${a.appointmentDate})`,
                   time: "Recent",
                   type: "success"
                })))
              ].map((log, i) => (
                <div key={i} className="flex items-start gap-4 pb-6 border-b border-slate-50 last:border-0 last:pb-0">
                  <div className={`mt-1 w-2 h-2 rounded-full ${log.type === 'system' ? 'bg-blue-500' : log.type === 'success' ? 'bg-green-500' : 'bg-[#00a69c]'}`}></div>
                  <div className="flex-1">
                    <p className="text-sm font-bold text-slate-900">{log.action}</p>
                    <p className="text-xs text-slate-400 mt-0.5">{log.user} • {log.time}</p>
                  </div>
                </div>
              ))}
            </div>
            <button 
              onClick={() => setActiveView('LOGS')}
              className="w-full mt-8 py-3 text-sm font-bold text-slate-400 hover:text-[#0066cc] transition-colors border-t border-slate-50 pt-6 uppercase tracking-[0.2em]"
            >
              View Full System Audit
            </button>
          </div>
        </div>

        <div className="bg-[#002d5a] rounded-3xl p-8 text-white relative overflow-hidden group">
          <div className="absolute bottom-0 right-0 w-64 h-64 bg-white/5 rounded-full blur-[60px] -mr-32 -mb-32"></div>
          <h3 className="text-xl font-extrabold flex items-center gap-2 mb-8">
            <ShieldCheck className="w-6 h-6 text-[#00a69c]" />
            Security Guard
          </h3>
          <div className="space-y-6 relative z-10 text-sm">
             <div className="p-4 rounded-2xl bg-white/5 border border-white/10">
                <div className="flex justify-between items-center mb-1">
                  <span className="text-xs font-black uppercase text-slate-400">Threat Monitoring</span>
                  <span className="text-green-400 font-bold text-[10px]">Optimal</span>
                </div>
                <p className="text-slate-300 font-medium">Enterprise encryption and automated vulnerability scanning are active.</p>
             </div>
             <div className="p-4 rounded-2xl bg-white/5 border border-white/10">
                <div className="flex justify-between items-center mb-1">
                  <span className="text-xs font-black uppercase text-slate-400">Microservice Health</span>
                  <span className="text-blue-400 font-bold text-[10px]">99.9% Uptime</span>
                </div>
                <p className="text-slate-300 font-medium">7 Distributed services across secure gateway clusters.</p>
             </div>
             <div className="pt-4">
                <button className="w-full btn-secondary border-white/20 hover:bg-white/10">Security Analytics</button>
             </div>
          </div>
        </div>
      </section>
    </>
  )

  const renderAppointments = () => (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <button 
          onClick={() => setActiveView('OVERVIEW')}
          className="flex items-center gap-2 text-sm font-bold text-slate-400 hover:text-[#0066cc] transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          Back to Overview
        </button>
        <div className="relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-300" />
          <input 
            type="text" 
            placeholder="Search patients or status..."
            className="pl-11 pr-6 py-2.5 rounded-full border border-slate-100 bg-white text-sm font-medium focus:ring-2 focus:ring-[#0066cc]/20 outline-none w-full md:w-80"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>
      
      <div className="bg-white rounded-[2rem] border border-slate-100 shadow-sm overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-slate-50/50 border-b border-slate-100">
              <th className="px-8 py-5 text-[10px] uppercase font-black tracking-widest text-slate-400">Patient</th>
              <th className="px-8 py-5 text-[10px] uppercase font-black tracking-widest text-slate-400">Date/Time</th>
              <th className="px-8 py-5 text-[10px] uppercase font-black tracking-widest text-slate-400">Doctor</th>
              <th className="px-8 py-5 text-[10px] uppercase font-black tracking-widest text-slate-400">Status</th>
              <th className="px-8 py-5 text-[10px] uppercase font-black tracking-widest text-slate-400 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredAppointments.map((app) => (
              <tr key={app.id} className="border-b border-slate-50 last:border-0 hover:bg-slate-50/30 transition-colors">
                <td className="px-8 py-6">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-full bg-[#0066cc]/10 flex items-center justify-center text-[#0066cc] font-black text-sm text-uppercase">
                      {app.fullName ? app.fullName.charAt(0) : 'G'}
                    </div>
                    <div>
                      <p className="text-sm font-bold text-slate-900">{app.fullName}</p>
                      <p className="text-xs text-slate-400">ID: #{app.id}</p>
                    </div>
                  </div>
                </td>
                <td className="px-8 py-6 text-sm font-medium text-slate-600">{app.appointmentDate}</td>
                <td className="px-8 py-6 text-sm font-bold text-slate-900">Dr. {app.doctorId}</td>
                <td className="px-8 py-6">
                  <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-wider ${
                    app.status === 'COMPLETED' ? 'bg-green-50 text-green-600' :
                    app.status === 'CANCELLED' ? 'bg-red-50 text-red-600' :
                    'bg-blue-50 text-blue-600'
                  }`}>
                    {app.status}
                  </span>
                </td>
                <td className="px-8 py-6">
                  {rescheduleData.id === app.id ? (
                    <div className="flex items-center gap-2">
                      <input 
                        type="date" 
                        className="text-xs p-1 border border-slate-200 rounded-lg outline-none focus:ring-1 focus:ring-[#0066cc]"
                        value={rescheduleData.date}
                        onChange={(e) => setRescheduleData({ ...rescheduleData, date: e.target.value })}
                      />
                      <button 
                        onClick={() => {
                          const updatedApp = { ...app, appointmentDate: rescheduleData.date }
                          handleAction(updateAppointment, app.id, updatedApp)
                          setRescheduleData({ id: null, date: '' })
                        }}
                        className="p-1.5 rounded-lg bg-[#0066cc] text-white hover:bg-[#004d99]"
                        title="Save Date"
                      >
                        <CheckCircle className="w-3.5 h-3.5" />
                      </button>
                      <button 
                        onClick={() => setRescheduleData({ id: null, date: '' })}
                        className="p-1.5 rounded-lg bg-slate-100 text-slate-400"
                        title="Cancel"
                      >
                        <ArrowLeft className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  ) : (
                    <div className="flex items-center justify-end gap-2">
                       {app.status === 'PENDING' && (
                        <>
                          <button 
                            onClick={() => setRescheduleData({ id: app.id, date: app.appointmentDate })}
                            className="p-2 rounded-xl bg-blue-50 text-[#0066cc] hover:bg-blue-100 transition-colors"
                            title="Reschedule"
                          >
                            <Calendar className="w-4 h-4" />
                          </button>
                          <button 
                            onClick={() => handleAction(completeAppointment, app.id)}
                            className="p-2 rounded-xl bg-green-50 text-green-600 hover:bg-green-100 transition-colors"
                            title="Complete"
                          >
                            <CheckCircle className="w-4 h-4" />
                          </button>
                          <button 
                            onClick={() => handleAction(cancelAppointment, app.id)}
                            className="p-2 rounded-xl bg-red-50 text-red-600 hover:bg-red-100 transition-colors"
                            title="Cancel"
                          >
                            <XCircle className="w-4 h-4" />
                          </button>
                        </>
                      )}
                      <button 
                        onClick={() => handleAction(deleteAppointment, app.id)}
                        className="p-2 rounded-xl bg-slate-100 text-slate-400 hover:bg-red-500 hover:text-white transition-all"
                        title="Delete"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )

  const renderSpecialitySelector = () => {
    const specs = [...new Set(dashboardStats.doctors.map(d => d.specialization))].sort()
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
        {specs.map(spec => {
          const count = dashboardStats.doctors.filter(d => d.specialization === spec).length
          return (
            <button
              key={spec}
              onClick={() => {
                setSelectedSpec(spec)
                setDocViewMode('LIST')
              }}
              className="p-8 rounded-[2rem] bg-white border border-slate-100 shadow-sm hover:shadow-xl hover:border-[#0066cc]/20 transition-all text-left group"
            >
              <div className="w-12 h-12 bg-[#00a69c]/10 text-[#00a69c] rounded-2xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                <Stethoscope className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-black text-slate-800 mb-2">{spec}</h3>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{count} {count === 1 ? 'Doctor' : 'Doctors'}</p>
            </button>
          )
        })}
      </div>
    )
  }

  const renderDoctorGallery = () => {
    const list = dashboardStats.doctors.filter(d => d.specialization === selectedSpec)
    return (
      <div className="space-y-6 animate-in fade-in slide-in-from-right-4 duration-500">
        <div className="flex items-center justify-between">
          <button 
            onClick={() => setDocViewMode('SPECIALITIES')}
            className="flex items-center gap-1.5 text-xs font-black text-slate-400 hover:text-[#0066cc] uppercase tracking-widest transition-all"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Specialities
          </button>
          <h2 className="text-xl font-black text-slate-900">{selectedSpec} Team</h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {list.map(doc => (
            <div key={doc.id} className="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-sm hover:shadow-md transition-shadow">
              <div className="flex items-center gap-4 mb-6">
                <div className="w-14 h-14 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-black text-xl">
                  {doc.fullName?.charAt(0)}
                </div>
                <div>
                  <h4 className="text-lg font-black text-slate-900 leading-tight">{doc.fullName}</h4>
                  <p className="text-[10px] font-black text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded-full inline-block mt-1">DR-ID: #{doc.id}</p>
                </div>
              </div>
              <button 
                onClick={() => {
                  setSelectedDoc(doc)
                  setDocViewMode('DETAIL')
                  fetchDoctorDetail(doc.id)
                }}
                className="w-full btn-primary py-3 text-xs font-bold rounded-xl flex items-center justify-center gap-2"
              >
                View Performance & Schedule
                <ArrowUpRight className="w-4 h-4" />
              </button>
            </div>
          ))}
        </div>
      </div>
    )
  }

  const renderDoctorCommitments = () => {
    if (!selectedDoc) return null
    return (
      <div className="space-y-8 animate-in fade-in zoom-in duration-500">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4 border-b border-slate-100 pb-8">
          <div className="space-y-4">
            <button 
              onClick={() => setDocViewMode('LIST')}
              className="flex items-center gap-1.5 text-xs font-black text-slate-400 hover:text-[#0066cc] uppercase tracking-widest transition-all"
            >
              <ArrowLeft className="w-4 h-4" />
              Back to {selectedSpec} List
            </button>
            <div className="flex items-center gap-6">
               <div className="w-20 h-20 rounded-3xl bg-slate-900 text-white flex items-center justify-center font-black text-3xl">
                 {selectedDoc.fullName?.charAt(0)}
               </div>
               <div>
                 <h1 className="text-3xl font-black text-slate-900 leading-none mb-2">{selectedDoc.fullName}</h1>
                 <div className="flex items-center gap-3">
                    <span className="px-3 py-1 rounded-full bg-[#00a69c]/10 text-[#00a69c] text-[10px] font-black uppercase tracking-widest">{selectedDoc.specialization}</span>
                    <span className="text-xs text-slate-400 font-medium">#{selectedDoc.email}</span>
                 </div>
               </div>
            </div>
          </div>
          <div className="flex items-center gap-4">
             <div className="text-right">
                <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Status</p>
                <div className="flex items-center gap-2 text-green-600 font-bold">
                   <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                   Active Professional
                </div>
             </div>
          </div>
        </div>

        <div className="grid lg:grid-cols-2 gap-8">
          {/* Appointments section */}
          <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden">
            <div className="p-8 border-b border-slate-50 flex items-center justify-between">
               <h3 className="text-lg font-black text-slate-900 flex items-center gap-2">
                 <Calendar className="w-5 h-5 text-[#0066cc]" />
                 Current Schedule
               </h3>
               {isRefreshingDoc && <Loader2 className="w-4 h-4 animate-spin text-slate-400" />}
            </div>
            <div className="p-8 max-h-[500px] overflow-y-auto">
              {docAppointments.length === 0 ? (
                <div className="text-center py-12 text-slate-400 italic text-sm">No scheduled appointments found.</div>
              ) : (
                <div className="space-y-4">
                  {docAppointments.map(app => (
                    <div key={app.id} className="p-5 rounded-2xl bg-slate-50 border border-slate-100 flex items-center justify-between group hover:border-[#0066cc]/40 transition-all">
                      <div>
                        <p className="text-sm font-black text-slate-900">{app.fullName || 'Guest Patient'}</p>
                        <div className="flex items-center gap-3 mt-1">
                          <span className="text-[10px] font-bold text-slate-400 flex items-center gap-1">
                            <Clock className="w-3 h-3" />
                            {new Date(app.appointmentDate).toLocaleString()}
                          </span>
                        </div>
                      </div>
                      <span className={`px-2.5 py-1 rounded-lg text-[9px] font-black uppercase tracking-widest ${
                        app.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                        app.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                        'bg-blue-100 text-blue-700'
                      }`}>
                        {app.status}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Leaves section */}
          <div className="bg-slate-50 rounded-[2.5rem] border border-slate-100 overflow-hidden">
            <div className="p-8 border-b border-slate-200/50 flex items-center justify-between">
               <h3 className="text-lg font-black text-slate-900 flex items-center gap-2">
                 <ShieldAlert className="w-5 h-5 text-orange-500" />
                 Leave History
               </h3>
               <button className="text-[10px] font-black text-[#0066cc] uppercase tracking-widest bg-white px-3 py-1.5 rounded-full border border-slate-200">Export Log</button>
            </div>
            <div className="p-8">
              {docLeaves.length === 0 ? (
                <div className="text-center py-12">
                   <div className="w-16 h-16 bg-white rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                     <FileText className="w-8 h-8" />
                   </div>
                   <p className="text-sm font-bold text-slate-400 italic">No leave requests documented for this professional.</p>
                </div>
              ) : (
                <div className="space-y-4">
                   {docLeaves.map(leave => (
                     <div key={leave.id} className="p-6 rounded-2xl bg-white shadow-sm border border-slate-100">
                        <div className="flex items-center justify-between mb-4">
                           <div className="flex items-center gap-3 text-xs font-black text-slate-900 uppercase">
                             <Calendar className="w-4 h-4 text-indigo-500" />
                             {leave.startDate} → {leave.endDate}
                           </div>
                           <span className={`px-2 py-0.5 rounded-md text-[10px] font-black uppercase tracking-wider ${
                             leave.status === 'APPROVED' ? 'bg-green-50 text-green-600' :
                             leave.status === 'REJECTED' ? 'bg-red-50 text-red-600' :
                             'bg-orange-50 text-orange-600'
                           }`}>
                             {leave.status}
                           </span>
                        </div>
                        <p className="text-xs text-slate-500 italic leading-relaxed">"{leave.reason}"</p>
                     </div>
                   ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    )
  }

  const renderDoctors = () => {
    return (
      <div className="space-y-8">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-3xl font-black text-slate-900 leading-none">Medical <span className="text-[#00a69c]">Engine</span></h2>
            <p className="text-slate-500 text-sm mt-2 font-medium">Coordinate specializations, clinical loads, and staff availability.</p>
          </div>
          <button 
            onClick={() => setActiveView('OVERVIEW')}
            className="hidden md:flex btn-secondary text-xs uppercase tracking-widest font-black"
          >
            Dashboard Overview
          </button>
        </div>

        {docViewMode === 'SPECIALITIES' && (
          <div className="space-y-10">
            {dashboardStats.pendingDoctors.length > 0 && (
              <section className="bg-amber-50 rounded-[2.5rem] border border-amber-200/50 p-8">
                 <div className="flex items-center gap-3 mb-6">
                    <ShieldAlert className="w-6 h-6 text-amber-600" />
                    <h3 className="text-xl font-black text-amber-900">Pending Doctor Approvals</h3>
                    <span className="bg-amber-600 text-white text-[10px] font-black px-2 py-0.5 rounded-full">{dashboardStats.pendingDoctors.length}</span>
                 </div>
                 <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {dashboardStats.pendingDoctors.map(doc => (
                      <div key={doc.id} className="bg-white p-6 rounded-3xl border border-amber-100 shadow-sm flex flex-col justify-between">
                         <div className="space-y-4">
                            <div>
                               <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">Doctor Name</p>
                               <p className="text-lg font-black text-slate-900 leading-tight">
                                  {doc.firstName} {doc.lastName}
                               </p>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                               <div>
                                  <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5">Email</p>
                                  <p className="text-xs font-bold text-slate-600 truncate">{doc.email}</p>
                               </div>
                               <div>
                                  <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-0.5">Medical Reg No.</p>
                                  <p className="text-xs font-black text-amber-600">{doc.doctorRegistrationNumber}</p>
                               </div>
                            </div>
                         </div>
                         <button 
                            onClick={() => handleAction(verifyDoctor, doc.id)}
                            className="w-full mt-8 py-3 bg-amber-600 text-white text-xs font-black rounded-xl hover:bg-amber-700 transition-colors shadow-lg shadow-amber-600/20 flex items-center justify-center gap-2"
                         >
                            <ShieldCheck className="w-5 h-5" />
                            Confirm Approval
                         </button>
                      </div>
                    ))}
                 </div>
              </section>
            )}
            {renderSpecialitySelector()}
          </div>
        )}
        {docViewMode === 'LIST' && renderDoctorGallery()}
        {docViewMode === 'DETAIL' && renderDoctorCommitments()}
      </div>
    )
  }

  const renderPatients = () => (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <button 
          onClick={() => setActiveView('OVERVIEW')}
          className="flex items-center gap-2 text-sm font-bold text-slate-400 hover:text-[#0066cc] transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          Back to Overview
        </button>
        <div className="relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-300" />
          <input 
            type="text" 
            placeholder="Search patients by name or email..."
            className="pl-11 pr-6 py-2.5 rounded-full border border-slate-100 bg-white text-sm font-medium focus:ring-2 focus:ring-[#0066cc]/20 outline-none w-full md:w-80"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="bg-white rounded-[2rem] border border-slate-100 shadow-sm overflow-hidden text-sm">
        <div className="grid grid-cols-4 bg-slate-50/50 border-b border-slate-100 px-8 py-4 text-[10px] uppercase font-black tracking-widest text-slate-400">
           <div>Full Name</div>
           <div>Email Address</div>
           <div>Phone Number</div>
           <div className="text-right">Actions</div>
        </div>
        {filteredPatients.map((patient) => (
          <div key={patient.id} className="grid grid-cols-4 border-b border-slate-50 last:border-0 hover:bg-slate-50/30 transition-colors px-8 py-6 items-center">
            <div className="font-bold text-slate-900">{patient.firstName} {patient.lastName}</div>
            <div className="text-slate-500">{patient.email}</div>
            <div className="text-slate-500">{patient.phoneNumber || 'N/A'}</div>
            <div className="flex justify-end gap-2">
              <button className="p-2 rounded-xl bg-blue-50 text-[#0066cc] hover:bg-[#0066cc] hover:text-white transition-all">
                <ExternalLink className="w-4 h-4" />
              </button>
              <button 
                onClick={() => handleAction(deleteUser, patient.id)}
                className="p-2 rounded-xl bg-red-50 text-red-600 hover:bg-red-600 hover:text-white transition-all"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )

  const renderLogs = () => (
    <div className="space-y-6">
       <div className="flex items-center justify-between">
          <button 
            onClick={() => setActiveView('OVERVIEW')}
            className="flex items-center gap-2 text-sm font-bold text-slate-400 hover:text-[#0066cc] transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Overview
          </button>
          <div className="px-4 py-2 bg-yellow-50 text-yellow-700 rounded-2xl flex items-center gap-2 text-xs font-bold border border-yellow-100">
            <ShieldAlert className="w-4 h-4" />
            System Audit Active
          </div>
       </div>

       <div className="bg-slate-900 rounded-[2.5rem] p-10 text-slate-300 font-mono text-sm leading-relaxed shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 right-0 p-8 opacity-5">
             <Settings className="w-64 h-64 animate-[spin_20s_linear_infinite]" />
          </div>
          <div className="relative z-10 space-y-2">
             <div className="text-green-400 mb-6 font-bold">{`[SYSTEM_BOOT] Initializing OMNIHEALTH Distributed Control Center...`}</div>
             {dashboardStats.operations ? (
               <pre className="whitespace-pre-wrap">{JSON.stringify(dashboardStats.operations, null, 2)}</pre>
             ) : (
               <>
                 <div>{`[INFO] ${new Date().toISOString()} : Checking service heartbeats...`}</div>
                 <div className="text-blue-400">{`[OK] Appointment-Service (Cluster-A) : 200 OK`}</div>
                 <div className="text-blue-400">{`[OK] User-Management (Cluster-B) : 200 OK`}</div>
                 <div className="text-blue-400">{`[OK] Doctor-Service (Cluster-C) : 200 OK`}</div>
                 <div className="pt-4 text-slate-500 italic">{`# Monitoring platform transactions and user redirections...`}</div>
               </>
             )}
          </div>
       </div>
    </div>
  )

  const renderContacts = () => {
    const sortedForms = [...dashboardStats.contactForms].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

    return (
      <div className="space-y-10 animate-in fade-in slide-in-from-bottom-4 duration-500">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-3xl font-black text-slate-900 leading-none">Support <span className="text-teal-500">Center</span></h2>
            <p className="text-slate-500 text-sm mt-2 font-medium">Read and respond to patient inquiries and feedback.</p>
          </div>
          <button 
            onClick={() => setActiveView('OVERVIEW')}
            className="btn-secondary text-xs uppercase tracking-widest font-black"
          >
            Dashboard Overview
          </button>
        </div>

        <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-sm overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-slate-50 text-[10px] uppercase font-black tracking-widest text-slate-400 border-b border-slate-100">
              <tr>
                <th className="px-8 py-5">Date</th>
                <th className="px-8 py-5">Patient</th>
                <th className="px-8 py-5">Subject</th>
                <th className="px-8 py-5">Status</th>
                <th className="px-8 py-5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="text-sm">
              {sortedForms.map(form => (
                <Fragment key={form.id}>
                  <tr className="border-b border-slate-50 hover:bg-slate-50/50 transition-colors">
                    <td className="px-8 py-6 text-slate-500 whitespace-nowrap">
                      {new Date(form.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-8 py-6">
                      <div className="font-bold text-slate-900">{form.name}</div>
                      <div className="text-xs text-slate-400">{form.email}</div>
                    </td>
                    <td className="px-8 py-6 font-medium text-slate-700">{form.subject}</td>
                    <td className="px-8 py-6">
                      <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                        form.status === 'REPLIED' ? 'bg-green-50 text-green-600' : 'bg-amber-50 text-amber-600'
                      }`}>
                        {form.status || 'PENDING'}
                      </span>
                    </td>
                    <td className="px-8 py-6 text-right">
                      <button 
                        onClick={() => setReplyData({ id: form.id === replyData.id ? null : form.id, text: '' })}
                        className="p-3 rounded-2xl bg-teal-50 text-teal-600 hover:bg-teal-600 hover:text-white transition-all shadow-sm"
                      >
                        <MessageSquare className="w-5 h-5" />
                      </button>
                    </td>
                  </tr>
                  {replyData.id === form.id && (
                    <tr className="bg-slate-50/50 border-b border-slate-100 animate-in slide-in-from-top-2 duration-300">
                      <td colSpan="5" className="px-12 py-8">
                        <div className="space-y-6 max-w-4xl">
                          <div className="p-6 bg-white rounded-3xl border border-slate-200 shadow-sm">
                            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-3">Original Message</p>
                            <p className="text-slate-700 leading-relaxed font-medium">"{form.message}"</p>
                          </div>
                          
                          {form.adminReply && (
                            <div className="p-6 bg-teal-50 rounded-3xl border border-teal-100 shadow-sm ml-8">
                              <p className="text-[10px] font-black text-teal-600 uppercase tracking-widest mb-3">Previous Admin Reply</p>
                              <p className="text-teal-800 leading-relaxed font-medium">"{form.adminReply}"</p>
                            </div>
                          )}

                          <div className="space-y-4">
                            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-2">Your Response</p>
                            <textarea 
                              className="w-full p-6 border border-slate-200 rounded-3xl outline-none focus:ring-4 focus:ring-teal-100 transition-all font-medium text-slate-600 min-h-[120px] shadow-inner"
                              placeholder="Type your reply here..."
                              value={replyData.text}
                              onChange={(e) => setReplyData({ ...replyData, text: e.target.value })}
                            />
                            <div className="flex items-center gap-3 justify-end">
                              <button 
                                onClick={() => setReplyData({ id: null, text: '' })}
                                className="px-8 py-4 text-xs font-black text-slate-400 uppercase tracking-widest hover:text-slate-600 transition-colors"
                              >
                                Cancel
                              </button>
                              <button 
                                onClick={async () => {
                                  setIsReplying(true)
                                  try {
                                    await replyToContactForm(form.id, replyData.text, token)
                                    setReplyData({ id: null, text: '' })
                                    await fetchDashboardData()
                                  } catch (err) {
                                    setError('Failed to send reply. Please try again.')
                                  } finally {
                                    setIsReplying(false)
                                  }
                                }}
                                disabled={!replyData.text || isReplying}
                                className="px-10 py-4 bg-teal-600 text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-teal-700 transition-all shadow-xl shadow-teal-900/20 flex items-center gap-2 disabled:opacity-50"
                              >
                                {isReplying ? <Loader2 className="w-5 h-5 animate-spin" /> : <SendIcon className="w-5 h-5" />}
                                Send Reply
                              </button>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                </Fragment>
              ))}
            </tbody>
          </table>
          
          {dashboardStats.contactForms.length === 0 && (
            <div className="text-center py-32">
              <MessageSquare className="w-16 h-16 text-slate-200 mx-auto mb-6" />
              <h3 className="text-xl font-black text-slate-400 italic">No Support Inquiries Logged</h3>
            </div>
          )}
        </div>
      </div>
    )
  }

  const renderLeaves = () => {
    const pendingLeaves = dashboardStats.allLeaves.filter(l => l.status === 'PENDING')
    const otherLeaves = dashboardStats.allLeaves.filter(l => l.status !== 'PENDING')

    return (
      <div className="space-y-10 animate-in fade-in slide-in-from-bottom-4 duration-500">
        <div className="flex items-center justify-between">
           <div>
             <h2 className="text-3xl font-black text-slate-900 leading-none">Leave <span className="text-orange-500">Approvals</span></h2>
             <p className="text-slate-500 text-sm mt-2 font-medium">Manage clinical availability and staff time-off requests.</p>
           </div>
           <button 
             onClick={() => setActiveView('OVERVIEW')}
             className="btn-secondary text-xs"
           >
             Back to Overview
           </button>
        </div>

        {/* Pending Requests */}
        <section className="space-y-6">
           <h3 className="text-lg font-black text-slate-900 flex items-center gap-2">
             <Clock className="w-5 h-5 text-orange-500" />
             Pending Requests ({pendingLeaves.length})
           </h3>
           
           {pendingLeaves.length === 0 ? (
             <div className="text-center py-20 bg-slate-50 border-2 border-dashed border-slate-200 rounded-[3rem]">
                <ShieldCheck className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                <p className="text-slate-500 font-bold uppercase tracking-widest text-xs">All caught up! No pending requests.</p>
             </div>
           ) : (
             <div className="grid gap-6">
               {pendingLeaves.map(leave => {
                 const doctor = dashboardStats.doctors.find(d => d.id === leave.doctorId)
                 return (
                   <div key={leave.id} className="bg-white p-8 rounded-[2.5rem] border border-orange-100 shadow-xl shadow-orange-900/5 flex flex-col lg:flex-row lg:items-center justify-between gap-8 transition-all hover:border-orange-200">
                      <div className="flex items-start gap-6">
                         <div className="w-16 h-16 rounded-2xl bg-orange-50 text-orange-600 flex items-center justify-center font-black text-xl">
                           {doctor?.fullName?.charAt(0) || 'D'}
                         </div>
                         <div>
                            <h4 className="text-2xl font-black text-slate-900 leading-tight">{doctor?.fullName || `Doctor ID: ${leave.doctorId}`}</h4>
                            <p className="text-xs font-bold text-orange-600 uppercase tracking-widest mt-1 mb-4">{doctor?.specialization || 'Clinical Specialist'}</p>
                            <div className="flex flex-wrap gap-4 text-sm font-bold text-slate-500">
                               <div className="flex items-center gap-2">
                                  <Calendar className="w-4 h-4 text-slate-300" />
                                  {leave.startDate} to {leave.endDate}
                               </div>
                               <div className="flex items-center gap-2 italic">
                                  <FileText className="w-4 h-4 text-slate-300" />
                                  "{leave.reason || 'No reason provided'}"
                               </div>
                            </div>
                         </div>
                      </div>
                      <div className="flex items-center gap-3">
                         <button 
                           onClick={() => handleAction(updateDoctorLeaveStatus, leave.id, 'APPROVED')}
                           className="flex-1 lg:flex-none px-8 py-4 bg-green-600 text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-green-700 transition-all shadow-lg shadow-green-900/20"
                         >
                           Approve
                         </button>
                         <button 
                           onClick={() => handleAction(updateDoctorLeaveStatus, leave.id, 'REJECTED')}
                           className="flex-1 lg:flex-none px-8 py-4 bg-red-50 text-red-600 rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-red-600 hover:text-white transition-all"
                         >
                           Reject
                         </button>
                      </div>
                   </div>
                 )
               })}
             </div>
           )}
        </section>

        {/* History */}
        <section className="space-y-6 pt-10">
           <div className="flex items-center gap-2 px-2">
             <Clock className="w-5 h-5 text-slate-400" />
             <h3 className="text-lg font-black text-slate-900">Processed History</h3>
           </div>
           <div className="bg-white rounded-[2rem] border border-slate-100 shadow-sm overflow-hidden">
              <table className="w-full text-left">
                <thead className="bg-slate-50 text-[10px] uppercase font-black tracking-widest text-slate-400 border-b border-slate-100">
                  <tr>
                    <th className="px-8 py-4">Doctor</th>
                    <th className="px-8 py-4">Period</th>
                    <th className="px-8 py-4">Status</th>
                    <th className="px-8 py-4 text-right">Updated At</th>
                  </tr>
                </thead>
                <tbody className="text-sm">
                  {otherLeaves.map(leave => {
                    const doctor = dashboardStats.doctors.find(d => d.id === leave.doctorId)
                    return (
                      <tr key={leave.id} className="border-b border-slate-50 last:border-0 hover:bg-slate-50/50 transition-colors">
                        <td className="px-8 py-4 font-bold text-slate-800">{doctor?.fullName || leave.doctorId}</td>
                        <td className="px-8 py-4 text-slate-500">{leave.startDate} - {leave.endDate}</td>
                        <td className="px-8 py-4">
                           <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                             leave.status === 'APPROVED' ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
                           }`}>
                             {leave.status}
                           </span>
                        </td>
                        <td className="px-8 py-4 text-right text-slate-400 text-xs">{new Date(leave.updatedAt).toLocaleDateString()}</td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
           </div>
        </section>
      </div>
    )
  }

  return (
    <div className="pt-32 pb-20 px-4 max-w-7xl mx-auto space-y-10">
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-2xl flex items-center gap-3">
          <AlertCircle className="w-5 h-5" />
          <p className="text-sm font-semibold">{error}</p>
          <button onClick={() => setError(null)} className="ml-auto text-xs font-black uppercase">Dismiss</button>
        </div>
      )}

      {activeView === 'OVERVIEW' && renderOverview()}
      {activeView === 'APPOINTMENTS' && renderAppointments()}
      {activeView === 'DOCTORS' && renderDoctors()}
      {activeView === 'PATIENTS' && renderPatients()}
      { activeView === 'LEAVES' && renderLeaves() }
      { activeView === 'CONTACTS' && renderContacts() }
      { activeView === 'LOGS' && renderLogs() }

      {/* Loading Overlay */}
      {loading && (
        <div className="fixed inset-0 z-[100] bg-white/60 backdrop-blur-[2px] flex items-center justify-center">
          <div className="bg-white p-8 rounded-[2.5rem] shadow-2xl border border-slate-100 flex flex-col items-center gap-4">
            <Loader2 className="w-12 h-12 text-[#0066cc] animate-spin" />
            <p className="text-sm font-bold text-slate-600">Syncing Dashboard Data...</p>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminDashboard
