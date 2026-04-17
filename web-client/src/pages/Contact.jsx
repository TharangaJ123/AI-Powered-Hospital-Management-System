import { motion } from 'framer-motion'
import { 
  Phone, 
  Mail, 
  MapPin, 
  Clock, 
  Send, 
  MessageSquare, 
  HelpCircle,
  PhoneCall
} from 'lucide-react'
import { useState, useEffect } from 'react'
import { submitContactForm } from '../services/contact'

const Contact = ({ user, patientProfile }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: '',
    message: ''
  })

  // Pre-fill form if user is logged in
  useEffect(() => {
    if (user) {
      setFormData(prev => ({
        ...prev,
        name: user.firstName ? `${user.firstName} ${user.lastName || ''}`.trim() : prev.name,
        email: user.email || prev.email
      }))
    }
  }, [user])

  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsSubmitting(true)
    
    try {
      const payload = {
        ...formData,
        patientId: user?.role === 'PATIENT' ? patientProfile?.id : null
      }
      
      await submitContactForm(payload)
      setIsSubmitted(true)
      setFormData({ name: '', email: '', subject: '', message: '' })
    } catch (error) {
      console.error('Error submitting form:', error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const contactMethods = [
    { 
      icon: Phone, 
      title: "Call Us", 
      details: "+94 11 234 5678", 
      desc: "Mon-Sat from 8am to 8pm",
      color: "bg-blue-50 text-blue-600"
    },
    { 
      icon: Mail, 
      title: "Email Us", 
      details: "care@omnihealth.com", 
      desc: "Online support 24/7",
      color: "bg-teal-50 text-teal-600"
    },
    { 
      icon: MapPin, 
      title: "Visit Us", 
      details: "No 123, Health Avenue, Colombo 07", 
      desc: "Our main hospital campus",
      color: "bg-purple-50 text-purple-600"
    }
  ]

  return (
    <div className="pt-20">
      {/* Header Section */}
      <section className="py-24 bg-slate-50 border-b border-slate-100 overflow-hidden relative">
        <div className="absolute top-0 left-0 w-96 h-96 bg-[#0066cc]/5 rounded-full blur-[100px] -ml-48 -mt-48"></div>
        <div className="max-w-7xl mx-auto px-4 relative z-10 text-center space-y-4">
          <span className="text-[#00a69c] font-black uppercase tracking-widest text-xs">Reach Out to Us</span>
          <h1 className="text-4xl md:text-6xl font-black text-[#002d5a]">We're Always Here <br />to Support You.</h1>
          <p className="text-slate-500 max-w-xl mx-auto text-lg pt-4 leading-relaxed">
            Whether you have a medical inquiry, need assistance with your appointments, or want to provide feedback, our team is ready to help.
          </p>
        </div>
      </section>

      <section className="py-24 max-w-7xl mx-auto px-4">
        <div className="grid lg:grid-cols-2 gap-16 items-start">
          {/* Left Column: Form */}
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="bg-white rounded-3xl border border-slate-100 shadow-xl p-8 md:p-12 space-y-8"
          >
            <div className="space-y-2">
              <h2 className="text-3xl font-extrabold text-[#002d5a]">Send a Message</h2>
              <p className="text-slate-500">Fill out the form below and we'll get back to you within 24 hours.</p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700">Full Name</label>
                  <input 
                    type="text" 
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                    placeholder="John Doe"
                    className="w-full px-4 py-3.5 rounded-xl border border-slate-200 outline-none focus:border-[#0066cc] transition-colors"
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-bold text-slate-700">Email Address</label>
                  <input 
                    type="email" 
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    placeholder="john@example.com"
                    className="w-full px-4 py-3.5 rounded-xl border border-slate-200 outline-none focus:border-[#0066cc] transition-colors"
                  />
                </div>
              </div>
              <div className="space-y-2">
                <label className="text-sm font-bold text-slate-700">Subject</label>
                <input 
                  type="text" 
                  required
                  value={formData.subject}
                  onChange={(e) => setFormData({...formData, subject: e.target.value})}
                  placeholder="Inquiry about services"
                  className="w-full px-4 py-3.5 rounded-xl border border-slate-200 outline-none focus:border-[#0066cc] transition-colors"
                />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-bold text-slate-700">Your Message</label>
                <textarea 
                  required
                  rows="5"
                  value={formData.message}
                  onChange={(e) => setFormData({...formData, message: e.target.value})}
                  placeholder="How can we help you?"
                  className="w-full px-4 py-3.5 rounded-xl border border-slate-200 outline-none focus:border-[#0066cc] resize-none transition-colors"
                ></textarea>
              </div>

              <button 
                type="submit" 
                disabled={isSubmitting}
                className="btn-primary w-full py-4 flex items-center justify-center gap-3 disabled:opacity-70 group"
              >
                {isSubmitting ? 'Sending Message...' : (
                  <>
                    <span>Send Message</span>
                    <Send className="w-5 h-5 group-hover:translate-x-2' group-hover:-translate-y-1 transition-transform" />
                  </>
                )}
              </button>

              {isSubmitted && (
                <motion.div 
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="p-4 rounded-xl bg-green-50 border border-green-200 text-green-700 flex items-center gap-3 text-sm font-bold"
                >
                  <MessageSquare className="w-5 h-5" />
                  Message sent successfully! Our team will contact you soon.
                </motion.div>
              )}
            </form>
          </motion.div>

          {/* Right Column: Details */}
          <div className="space-y-8">
            {/* Quick Contact Cards */}
            <div className="grid gap-6">
              {contactMethods.map((method, idx) => (
                <motion.div 
                  key={idx}
                  whileHover={{ x: 10 }}
                  className="p-6 rounded-3xl border border-slate-100 bg-white shadow-sm flex items-start gap-6 group hover:shadow-xl hover:border-[#0066cc]/20 transition-all duration-300"
                >
                  <div className={`p-4 rounded-2xl ${method.color} transition-colors group-hover:scale-110`}>
                    <method.icon className="w-6 h-6" />
                  </div>
                  <div className="space-y-1">
                    <h3 className="font-extrabold text-lg text-[#002d5a]">{method.title}</h3>
                    <p className="font-bold text-[#0066cc]">{method.details}</p>
                    <p className="text-sm text-slate-500">{method.desc}</p>
                  </div>
                </motion.div>
              ))}
            </div>

            {/* Support Shortcuts */}
            <div className="bg-[#002d5a] p-8 rounded-[2.5rem] text-white space-y-6 relative overflow-hidden group">
              <div className="absolute bottom-0 right-0 w-64 h-64 bg-white/5 rounded-full blur-[60px] -mr-32 -mb-32 group-hover:bg-white/10 transition-all"></div>
              <h3 className="text-xl font-extrabold flex items-center gap-2">
                <HelpCircle className="w-6 h-6 text-[#00a69c]" />
                Help Center
              </h3>
              <div className="space-y-4 text-slate-300 text-sm">
                <p className="leading-relaxed">Access our comprehensive FAQ to find quick answers about payments, reports, and procedures.</p>
                <div className="pt-4 border-t border-white/10 flex flex-col gap-4">
                  <div className="flex items-center justify-between group cursor-pointer hover:text-white transition-colors">
                    <span className="font-bold">24/7 Emergency Support</span>
                    <PhoneCall className="w-6 h-6 text-red-500 bg-white p-1 rounded-full animate-pulse" />
                  </div>
                  <button className="btn-secondary w-full border-white/20 hover:bg-white/10">Browse Knowledge Base</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Map Placeholder */}
      <section className="pb-24 max-w-7xl mx-auto px-4">
        <div className="h-96 w-full rounded-[3rem] bg-slate-100 relative overflow-hidden border border-slate-200 group">
          <img 
            src="https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?q=80&w=2033&auto=format&fit=crop" 
            className="w-full h-full object-cover grayscale opacity-20 group-hover:scale-110 transition-transform duration-[2000ms]" 
            alt="Map View"
          />
          <div className="absolute inset-0 flex flex-col items-center justify-center text-center p-8">
            <div className="w-16 h-16 bg-[#0066cc] rounded-2xl flex items-center justify-center text-white mb-6 shadow-2xl shadow-[#0066cc]/40 animate-bounce">
              <MapPin className="w-8 h-8" />
            </div>
            <h3 className="text-2xl font-black text-[#002d5a]">Located in the Heart <br />of Colombo.</h3>
            <p className="text-slate-500 mt-2 font-bold uppercase tracking-widest text-xs">OmniHealth Main Hospital Campus</p>
            <button className="mt-8 px-8 py-3 bg-white text-[#0066cc] font-black rounded-full shadow-lg hover:shadow-2xl hover:-translate-y-1 transition-all">Get Driving Directions</button>
          </div>
        </div>
      </section>
    </div>
  )
}

export default Contact
