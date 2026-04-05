import { motion } from 'framer-motion'
import { 
  Truck, 
  Clock, 
  ShieldCheck, 
  PhoneCall, 
  FlaskConical, 
  Microscope, 
  Activity, 
  Stethoscope,
  Heart,
  Brain,
  Video,
  Hospital
} from 'lucide-react'

const serviceList = [
  {
    icon: Truck,
    title: "Emergency Ambulance Service",
    desc: "24/7 high-speed life support ambulance service with ICU facilities and expert paramedics on board.",
    color: "bg-red-500",
    linkText: "Request Emergency Now"
  },
  {
    icon: FlaskConical,
    title: "Diagnostic Laboratory",
    desc: "Advanced automated clinical laboratory providing highly accurate results for thousands of tests.",
    color: "bg-blue-600",
    linkText: "View Packages"
  },
  {
    icon: Microscope,
    title: "Imaging & Radiology",
    desc: "Equipped with state-of-the-art MRI, CT Scan, and 4D Ultrasound for precise internal health monitoring.",
    color: "bg-[#00a69c]",
    linkText: "Learn More"
  },
  {
    icon: Hospital,
    title: "Pharmacy Services",
    desc: "In-house 24/7 pharmacy ensuring availability of all critical medications and surgical supplies.",
    color: "bg-[#002d5a]",
    linkText: "Order Online"
  },
  {
    icon: Video,
    title: "Telemedicine",
    desc: "Consult with our world-class specialists from the comfort of your home via high-definition video calls.",
    color: "bg-[#0066cc]",
    linkText: "Book Session"
  },
  {
    icon: Activity,
    title: "Physiotherapy & Rehab",
    desc: "Specialized rehabilitation programs designed to help patients regain mobility and physical strength.",
    color: "bg-orange-500",
    linkText: "Details"
  }
]

const Services = () => {
  return (
    <div className="pt-20 pb-20">
      {/* Header */}
      <section className="relative py-24 bg-[#002d5a] text-center overflow-hidden">
        <div className="absolute inset-0 z-0 opacity-10">
           <img 
             src="https://images.unsplash.com/photo-1586773860418-d3b9d97355e1?q=80&w=2066&auto=format&fit=crop" 
             className="w-full h-full object-cover"
             alt="Hospital bg"
           />
        </div>
        <div className="max-w-7xl mx-auto px-4 relative z-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="space-y-6"
          >
            <span className="inline-block px-4 py-1.5 bg-[#00a69c]/20 border border-[#00a69c]/30 rounded-full text-[#00a69c] text-xs font-bold uppercase tracking-widest">
              Our Professional Care
            </span>
            <h1 className="text-4xl md:text-6xl font-black text-white">Hospital Services</h1>
            <p className="text-slate-400 max-w-2xl mx-auto text-lg leading-relaxed">
              We provide a comprehensive range of medical services, supported by industry-leading professionals and cutting-edge technology.
            </p>
          </motion.div>
        </div>
      </section>

      {/* Services Grid */}
      <section className="max-w-7xl mx-auto px-4 mt-20">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
          {serviceList.map((service, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              whileHover={{ y: -10 }}
              transition={{ duration: 0.5, delay: index * 0.1 }}
              className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/50 p-10 group overflow-hidden relative"
            >
              <div className="absolute top-0 right-0 w-32 h-32 bg-slate-50 -mr-16 -mt-16 rounded-full group-hover:scale-150 transition-transform duration-700"></div>
              
              <div className={`w-16 h-16 ${service.color} rounded-2xl flex items-center justify-center text-white mb-8 shadow-lg group-hover:scale-110 transition-transform relative z-10`}>
                <service.icon className="w-8 h-8" />
              </div>

              <div className="relative z-10 space-y-4">
                <h3 className="text-2xl font-black text-[#002d5a]">{service.title}</h3>
                <p className="text-slate-500 leading-relaxed font-medium">
                  {service.desc}
                </p>
                <div className="pt-4">
                   <button className="flex items-center space-x-2 text-[#0066cc] font-black text-xs uppercase tracking-widest group-hover:translate-x-2 transition-transform">
                     <span>{service.linkText}</span>
                     <Clock className="w-4 h-4" />
                   </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Emergency Section */}
      <section className="max-w-7xl mx-auto px-4 mt-24">
        <div className="bg-[#e53e3e] rounded-[3rem] p-12 text-white relative overflow-hidden group shadow-2xl shadow-red-500/20">
           <div className="absolute top-0 right-0 w-[600px] h-[600px] bg-white/5 rounded-full blur-[100px] -mr-64 -mt-64 group-hover:bg-white/10 transition-all duration-700"></div>
           <div className="grid lg:grid-cols-2 gap-16 items-center relative z-10">
             <div className="space-y-8">
               <div className="flex items-center space-x-3">
                  <div className="p-3 bg-white/20 rounded-2xl">
                    <Truck className="w-8 h-8 animate-pulse" />
                  </div>
                  <h2 className="text-4xl font-extrabold leading-tight">Emergency Ambulance <br/>Service (1344)</h2>
               </div>
               <p className="text-white/80 text-lg leading-relaxed">
                 Our state-of-the-art ambulance fleet is distributed strategically across the city to ensure Arrival within 10-15 minutes. Each vehicle acts as a mobile ICU.
               </p>
               <div className="flex gap-4">
                  <button className="bg-white text-red-600 px-8 py-4 rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl ring-4 ring-white/20 hover:scale-105 transition-all">
                    Call Amblance Dispatch
                  </button>
               </div>
             </div>
             <div className="hidden lg:block relative">
                <img 
                  src="/amb.png" 
                  className="rounded-3xl border-4 border-white/20 shadow-2xl skew-x-1 hover:skew-x-0 transition-transform duration-700" 
                  alt="Ambulance"
                />
             </div>
           </div>
        </div>
      </section>
    </div>
  )
}

export default Services
