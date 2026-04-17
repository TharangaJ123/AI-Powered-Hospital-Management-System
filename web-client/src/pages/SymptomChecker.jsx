import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { 
  Brain, 
  Send, 
  Stethoscope, 
  AlertTriangle, 
  CheckCircle2, 
  ArrowRight, 
  Activity, 
  ShieldCheck,
  RefreshCcw,
  Sparkles
} from 'lucide-react'
import { Link } from 'react-router-dom'
import { checkSymptoms } from '../services/aiService'

const SymptomChecker = () => {
  const [symptoms, setSymptoms] = useState('')
  const [age, setAge] = useState('')
  const [gender, setGender] = useState('Male')
  const [medicalHistory, setMedicalHistory] = useState('')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!symptoms.trim() || !age) return

    setLoading(true)
    setError(null)
    setResult(null)

    try {
      const data = await checkSymptoms({ symptoms, age: parseInt(age), gender, medicalHistory })
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const getUrgencyColor = (level) => {
    switch (level?.toUpperCase()) {
      case 'EMERGENCY': return 'bg-red-500 text-white shadow-red-500/20'
      case 'HIGH': return 'bg-orange-500 text-white shadow-orange-500/20'
      case 'MEDIUM': return 'bg-amber-500 text-white shadow-amber-500/20'
      default: return 'bg-blue-500 text-white shadow-blue-500/20'
    }
  }

  const getUrgencyIcon = (level) => {
    switch (level?.toUpperCase()) {
      case 'EMERGENCY': return <AlertTriangle className="w-6 h-6 animate-pulse" />
      case 'HIGH': return <AlertTriangle className="w-6 h-6" />
      default: return <Activity className="w-6 h-6" />
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 pt-28 pb-20">
      <div className="max-w-7xl mx-auto px-4">
        <div className="grid lg:grid-cols-2 gap-16 items-start">
          
          {/* Left Column: Input Form */}
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="space-y-8"
          >
            <div className="space-y-4">
              <span className="inline-flex items-center space-x-2 px-3 py-1 bg-indigo-50 border border-indigo-100 rounded-full text-indigo-500 text-[10px] font-black uppercase tracking-widest">
                <Sparkles className="w-3 h-3" />
                <span>Powered by Gemini 1.5 Flash</span>
              </span>
              <h1 className="text-5xl font-black text-[#002d5a] tracking-tight leading-tight">
                AI Symptom <br /> Analysis Engine.
              </h1>
              <p className="text-slate-500 text-lg font-medium leading-relaxed max-w-lg">
                Describe your symptoms in natural language. Our AI will analyze them and suggest potential clinical paths.
              </p>
            </div>

            <form onSubmit={handleSubmit} className="bg-white rounded-[2.5rem] p-8 shadow-xl border border-slate-100 space-y-6">
              
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label className="text-xs font-black text-slate-400 uppercase tracking-widest flex items-center gap-2">
                    <Activity className="w-4 h-4 text-[#0066cc]" />
                    Age
                  </label>
                  <input 
                    type="number"
                    value={age}
                    onChange={(e) => setAge(e.target.value)}
                    className="w-full bg-slate-50 rounded-xl px-4 py-3 border border-slate-100 focus:border-[#0066cc] focus:ring-4 focus:ring-[#0066cc]/10 outline-none transition-all font-bold text-slate-700"
                    placeholder="25"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-xs font-black text-slate-400 uppercase tracking-widest flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 text-[#0066cc]" />
                    Gender
                  </label>
                  <select 
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                    className="w-full bg-slate-50 rounded-xl px-4 py-3 border border-slate-100 focus:border-[#0066cc] focus:ring-4 focus:ring-[#0066cc]/10 outline-none transition-all font-bold text-slate-700"
                  >
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
              </div>

              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest flex items-center gap-2">
                  <Stethoscope className="w-4 h-4 text-[#0066cc]" />
                  Medical History
                </label>
                <textarea 
                  value={medicalHistory}
                  onChange={(e) => setMedicalHistory(e.target.value)}
                  className="w-full h-24 bg-slate-50 rounded-2xl p-4 border border-slate-100 focus:border-[#0066cc] focus:ring-4 focus:ring-[#0066cc]/10 outline-none transition-all font-medium leading-relaxed text-slate-700"
                  placeholder="Mention current diseases (e.g. Hypertension, Diabetes) or hidden conditions..."
                />
              </div>

              <div className="space-y-2">
                <label className="text-xs font-black text-slate-400 uppercase tracking-widest flex items-center gap-2">
                  <Brain className="w-4 h-4 text-[#0066cc]" />
                  Describe your symptoms
                </label>
                <textarea 
                  value={symptoms}
                  onChange={(e) => setSymptoms(e.target.value)}
                  className="w-full h-32 bg-slate-50 rounded-2xl p-6 border border-slate-100 focus:border-[#0066cc] focus:ring-4 focus:ring-[#0066cc]/10 outline-none transition-all font-medium leading-relaxed text-slate-700"
                  placeholder="e.g. I have a persistent headache for 3 days and some dizziness..."
                  required
                />
              </div>

              <button 
                type="submit"
                disabled={loading || !symptoms.trim() || !age}
                className="w-full flex items-center justify-center space-x-3 bg-[#002d5a] text-white py-5 rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl shadow-blue-900/20 active:scale-95 transition-all disabled:opacity-50"
              >
                {loading ? (
                  <>
                    <span>Analyzing with Intelligence...</span>
                    <RefreshCcw className="w-4 h-4 animate-spin" />
                  </>
                ) : (
                  <>
                    <span>Generate AI Insights</span>
                    <Send className="w-4 h-4" />
                  </>
                )}
              </button>

              <p className="text-[10px] text-center text-slate-400 font-bold uppercase tracking-tighter leading-relaxed">
                By using this AI service, you acknowledge that these are preliminary 
                suggestions and NOT a professional medical diagnosis. 
                Always consult a qualified doctor.
              </p>
            </form>

            {/* Quick Tips */}
            <div className="grid grid-cols-2 gap-4">
               <div className="p-6 bg-blue-50 border border-blue-100 rounded-3xl space-y-2">
                  <ShieldCheck className="w-6 h-6 text-[#0066cc]" />
                  <h4 className="text-sm font-black text-[#002d5a]">Private & Secure</h4>
                  <p className="text-[10px] text-slate-500 font-bold uppercase">Data encrypted end-to-end</p>
               </div>
               <div className="p-6 bg-green-50 border border-green-100 rounded-3xl space-y-2">
                  <Activity className="w-6 h-6 text-[#00a69c]" />
                  <h4 className="text-sm font-black text-[#002d5a]">Smart Specialty</h4>
                  <p className="text-[10px] text-slate-500 font-bold uppercase">Matches with vetted experts</p>
               </div>
            </div>
          </motion.div>

          {/* Right Column: AI Results */}
          <div className="relative min-h-[600px]">
            <AnimatePresence mode="wait">
              {!result && !loading && !error && (
                <motion.div 
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  className="absolute inset-0 flex flex-col items-center justify-center text-center space-y-6"
                >
                  <div className="w-24 h-24 bg-slate-200 rounded-full flex items-center justify-center animate-pulse">
                    <Brain className="w-10 h-10 text-slate-400" />
                  </div>
                  <div className="space-y-2">
                    <h3 className="text-xl font-extrabold text-slate-400">Waiting for Data...</h3>
                    <p className="text-sm text-slate-400 max-w-xs mx-auto">Input your symptoms to trigger the AI diagnostic analysis protocol.</p>
                  </div>
                </motion.div>
              )}

              {loading && (
                <motion.div 
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-white rounded-[3rem] p-12 shadow-2xl border border-slate-100 space-y-10"
                >
                  <div className="space-y-6">
                    <div className="h-10 bg-slate-100 rounded-full w-32 animate-pulse"></div>
                    <div className="space-y-3">
                      <div className="h-6 bg-slate-50 rounded-full w-full animate-pulse"></div>
                      <div className="h-6 bg-slate-50 rounded-full w-3/4 animate-pulse"></div>
                    </div>
                  </div>
                  <div className="pt-8 grid grid-cols-3 gap-4">
                     <div className="h-24 bg-slate-50 rounded-3xl animate-pulse"></div>
                     <div className="h-24 bg-slate-50 rounded-3xl animate-pulse"></div>
                     <div className="h-24 bg-slate-50 rounded-3xl animate-pulse"></div>
                  </div>
                  <div className="pt-8 h-20 bg-slate-900 rounded-2xl animate-pulse"></div>
                </motion.div>
              )}

              {error && (
                <motion.div 
                   initial={{ opacity: 0, scale: 0.95 }}
                   animate={{ opacity: 1, scale: 1 }}
                   className="bg-red-50 rounded-[3rem] p-12 border border-red-100 text-center space-y-6"
                >
                   <div className="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mx-auto">
                      <AlertTriangle className="w-10 h-10 text-red-500" />
                   </div>
                   <div className="space-y-2">
                      <h3 className="text-2xl font-black text-red-600">Connectivity Protocol Failure</h3>
                      <p className="text-red-500 font-medium px-8">{error}</p>
                   </div>
                   <button 
                    onClick={handleSubmit}
                    className="px-8 py-3 bg-red-600 text-white rounded-xl font-black text-xs uppercase tracking-widest"
                   >
                     Retry Connection
                   </button>
                </motion.div>
              )}

              {result && (
                <motion.div 
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-white rounded-[3rem] p-10 shadow-2xl border border-slate-100 space-y-8"
                >
                  {/* Urgency Badge */}
                  <div className={`p-6 rounded-[2rem] shadow-xl flex items-center justify-between ${getUrgencyColor(result.urgencyLevel)}`}>
                     <div className="flex items-center gap-4">
                        {getUrgencyIcon(result.urgencyLevel)}
                        <div>
                          <p className="text-[10px] font-black uppercase tracking-widest opacity-80">Urgency Level Assessment</p>
                          <h4 className="text-xl font-black">{result.urgencyLevel}</h4>
                        </div>
                     </div>
                     <CheckCircle2 className="w-8 h-8 opacity-20" />
                  </div>

                  {/* Diagnosis */}
                  <div className="space-y-3">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                      <h3 className="text-lg font-black text-[#002d5a] uppercase tracking-widest flex items-center gap-2">
                        <Brain className="w-4 h-4 text-[#0066cc]" />
                        Preliminary Assessment
                      </h3>
                      <span className="inline-block px-3 py-1 bg-slate-100 border border-slate-200 rounded-full text-[9px] font-black text-slate-500 uppercase tracking-tighter">
                        Clinical: {result.clinicalCondition}
                      </span>
                    </div>
                    <p className="text-slate-600 text-lg leading-relaxed font-bold italic">
                      "{result.diagnosis}"
                    </p>
                  </div>

                  {/* Recommendations */}
                  <div className="space-y-4">
                     <h3 className="text-sm font-black text-slate-400 uppercase tracking-widest">Recommended Actions</h3>
                     <div className="bg-slate-50 rounded-3xl p-6 border border-slate-100 space-y-3">
                        {result.recommendations.map((rec, i) => (
                          <div key={i} className="flex gap-3">
                            <div className="w-1.5 h-1.5 rounded-full bg-[#0066cc] mt-1.5 flex-shrink-0" />
                            <p className="text-slate-600 text-sm font-medium leading-relaxed">
                              {rec}
                            </p>
                          </div>
                        ))}
                     </div>
                  </div>

                  {/* Specialists */}
                  <div className="space-y-4">
                    <h3 className="text-sm font-black text-slate-400 uppercase tracking-widest">Recommended Specialists</h3>
                    <div className="flex flex-wrap gap-3">
                      {result.recommendedSpecialties.map((spec, i) => (
                        <div key={i} className="flex items-center gap-2 px-4 py-2 bg-[#002d5a]/5 border border-[#002d5a]/10 rounded-full text-[#002d5a] font-bold text-xs">
                          <Stethoscope className="w-3 h-3 text-[#00a69c]" />
                          {spec}
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* CTA */}
                  <div className="pt-6">
                    <Link 
                      to={`/doctors?specialty=${result.recommendedSpecialties[0]}`}
                      className="w-full flex items-center justify-center space-x-3 bg-gradient-to-r from-[#002d5a] to-[#0066cc] text-white py-5 rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl shadow-blue-900/20 active:scale-95 transition-all"
                    >
                      <span>Locate Recommended Specialist</span>
                      <ArrowRight className="w-4 h-4" />
                    </Link>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SymptomChecker
