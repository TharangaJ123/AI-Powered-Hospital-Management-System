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
  ShieldAlert
} from 'lucide-react'
import { useState, useEffect } from 'react'
import { 
  getAllAppointments, 
  cancelAppointment, 
  completeAppointment, 
  deleteAppointment,
  updateAppointment 
} from '../services/appointments'
import { getAllDoctors } from '../services/doctors'
import { 
  getAllUsers, 
  verifyDoctor, 
  deleteUser, 
  getPlatformOperations 
} from '../services/auth'

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
    operations: null
  })
  const [rescheduleData, setRescheduleData] = useState({ id: null, date: '' })

  const fetchDashboardData = async () => {
    if (!token) return
    
    setLoading(true)
    setError(null)
    try {
      const [allApps, allDocs, allUsers, ops] = await Promise.all([
        getAllAppointments(token),
        getAllDoctors(),
        getAllUsers(token),
        getPlatformOperations(token).catch(() => null)
      ])

      const patientUsers = allUsers.filter(u => u.role === 'PATIENT')
      
      setDashboardStats({
        appointments: allApps,
        doctors: allDocs,
        patients: patientUsers,
        allUsers: allUsers,
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
            onClick={() => setActiveView(action.id)}
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

  const renderDoctors = () => (
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
            placeholder="Search doctors or specialty..."
            className="pl-11 pr-6 py-2.5 rounded-full border border-slate-100 bg-white text-sm font-medium focus:ring-2 focus:ring-[#0066cc]/20 outline-none w-full md:w-80"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredDoctors.map((doc) => (
          <div key={doc.id} className="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-sm">
            <div className="flex items-center gap-4 mb-6">
              <div className="w-14 h-14 rounded-2xl bg-[#00a69c]/10 flex items-center justify-center text-[#00a69c]">
                <Stethoscope className="w-7 h-7" />
              </div>
              <div>
                <h4 className="text-lg font-black text-slate-900">{doc.fullName}</h4>
                <p className="text-xs font-bold text-[#00a69c] uppercase tracking-wider">{doc.specialization}</p>
              </div>
            </div>
            <div className="space-y-3 mb-6">
              <div className="flex items-center justify-between text-xs">
                <span className="text-slate-400 font-bold uppercase tracking-widest text-[9px]">Status</span>
                <span className={`px-2 py-0.5 rounded-full font-black tracking-wider uppercase ${
                  doc.verified ? 'bg-green-50 text-green-600' : 'bg-orange-50 text-orange-600'
                }`}>
                  {doc.verified ? 'Verified' : 'Pending Verification'}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="text-slate-400 font-bold uppercase tracking-widest text-[9px]">Experience</span>
                <span className="text-slate-600 font-black">8 Years</span>
              </div>
            </div>
            <div className="flex gap-2">
              {!doc.verified && (
                <button 
                  onClick={() => handleAction(verifyDoctor, doc.id)}
                  className="flex-1 btn-primary text-xs py-2.5"
                >
                  Verify Doctor
                </button>
              )}
              <button 
                onClick={() => handleAction(deleteUser, doc.id)}
                className="p-2.5 rounded-xl border border-slate-100 text-slate-300 hover:bg-red-50 hover:text-red-600 transition-all"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )

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
      {activeView === 'LOGS' && renderLogs()}

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
