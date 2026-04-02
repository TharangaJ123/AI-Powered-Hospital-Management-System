import { motion } from 'framer-motion'
import { 
  Target, 
  Eye, 
  Award, 
  Users, 
  ShieldCheck, 
  Activity,
  Heart,
  BrainCircuit,
  Globe
} from 'lucide-react'

const AboutUs = () => {
  const values = [
    { icon: Heart, title: "Patient First", desc: "Our patients are at the heart of everything we do, ensuring compassionate care for every individual." },
    { icon: Activity, title: "Innovation", desc: "Leveraging AI and modern medical technology to provide cutting-edge diagnostics and treatments." },
    { icon: ShieldCheck, title: "Integrity", desc: "Maintaining the highest ethical standards and transparency in all our medical practices." },
    { icon: Award, title: "Excellence", desc: "Striving for clinical excellence through continuous learning and state-of-the-art facilities." }
  ]

  const stats = [
    { label: "Founded", value: "1995" },
    { label: "Bed Capacity", value: "1,200+" },
    { label: "Staff Members", value: "4,500+" },
    { label: "Annual Patients", value: "250k+" }
  ]

  return (
    <div className="pt-20">
      {/* Hero Section */}
      <section className="relative py-24 bg-slate-900 overflow-hidden">
        <div className="absolute inset-0 z-0">
          <img 
            src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?q=80&w=2053&auto=format&fit=crop" 
            className="w-full h-full object-cover opacity-30 grayscale" 
            alt="Hospital Hallway" 
          />
          <div className="absolute inset-0 bg-gradient-to-b from-[#002d5a]/80 to-[#002d5a]"></div>
        </div>
        
        <div className="max-w-7xl mx-auto px-4 relative z-10 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="space-y-6"
          >
            <span className="inline-block px-4 py-1.5 bg-[#00a69c]/20 border border-[#00a69c]/30 rounded-full text-[#00a69c] text-xs font-bold uppercase tracking-widest">
              Our Journey of Excellence
            </span>
            <h1 className="text-4xl md:text-6xl font-black text-white leading-tight">
              A Legacy of Care,<br />A Future of AI-Driven Health.
            </h1>
            <p className="text-slate-400 max-w-2xl mx-auto text-lg leading-relaxed">
              OMNIHEALTH is a premier multi-specialty hospital dedicated to providing world-class healthcare through innovation, compassion, and clinical excellence.
            </p>
          </motion.div>
        </div>
      </section>

      {/* Mission & Vision */}
      <section className="py-24 max-w-7xl mx-auto px-4 grid md:grid-cols-2 gap-12">
        <motion.div 
          whileHover={{ y: -5 }}
          className="p-10 rounded-[2.5rem] bg-[#0066cc]/5 border border-[#0066cc]/10 space-y-6"
        >
          <div className="w-14 h-14 bg-[#0066cc] rounded-2xl flex items-center justify-center text-white">
            <Target className="w-8 h-8" />
          </div>
          <h2 className="text-3xl font-extrabold text-[#002d5a]">Our Mission</h2>
          <p className="text-slate-600 leading-relaxed">
            To provide accessible, high-quality, and cost-effective healthcare services to all sections of society, integrating the latest medical advancements with humane care.
          </p>
        </motion.div>

        <motion.div 
          whileHover={{ y: -5 }}
          className="p-10 rounded-[2.5rem] bg-[#00a69c]/5 border border-[#00a69c]/10 space-y-6"
        >
          <div className="w-14 h-14 bg-[#00a69c] rounded-2xl flex items-center justify-center text-white">
            <Eye className="w-8 h-8" />
          </div>
          <h2 className="text-3xl font-extrabold text-[#002d5a]">Our Vision</h2>
          <p className="text-slate-600 leading-relaxed">
            To be a global leader in healthcare innovation, recognized for our clinical outcomes, patient safety, and pioneering AI-driven medical diagnostic systems.
          </p>
        </motion.div>
      </section>

      {/* Core Values */}
      <section className="bg-slate-50 py-24">
        <div className="max-w-7xl mx-auto px-4">
          <div className="text-center mb-16 space-y-4">
            <h2 className="text-4xl font-extrabold text-[#002d5a]">Our Shared Values</h2>
            <p className="text-slate-500 max-w-xl mx-auto">The principles that guide our interactions with patients, staff, and the community.</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((v, i) => (
              <div key={i} className="bg-white p-8 rounded-3xl border border-slate-100 shadow-sm hover:shadow-xl transition-all group">
                <v.icon className="w-10 h-10 text-[#0066cc] mb-6 group-hover:scale-110 transition-transform" />
                <h3 className="text-xl font-bold text-[#002d5a] mb-3">{v.title}</h3>
                <p className="text-sm text-slate-500 leading-relaxed">{v.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Statistics */}
      <section className="py-24 max-w-7xl mx-auto px-4">
        <div className="bg-[#002d5a] rounded-[3rem] p-12 text-white overflow-hidden relative group">
          <div className="absolute top-0 right-0 w-96 h-96 bg-[#00a69c]/20 rounded-full blur-[100px] -mr-48 -mt-48 group-hover:bg-[#00a69c]/30 transition-all duration-700"></div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-12 relative z-10 text-center">
            {stats.map((s, i) => (
              <div key={i} className="space-y-2">
                <h4 className="text-4xl md:text-5xl font-black text-[#00a69c]">{s.value}</h4>
                <p className="text-xs uppercase font-bold tracking-[0.2em] text-slate-400">{s.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Corporate Leadership Placeholder */}
      <section className="py-24 max-w-7xl mx-auto px-4 text-center space-y-12">
        <div className="space-y-4">
          <h2 className="text-4xl font-extrabold text-[#002d5a]">Leading with Purpose</h2>
          <p className="text-slate-500 max-w-xl mx-auto">Our leadership team brings together decades of medical and technological expertise.</p>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-12">
          {[
            { role: "Chief Medical Officer", name: "Dr. Elena Vance" },
            { role: "Hospital Director", name: "Robert J. Sterling" },
            { role: "Head of AI Research", name: "Dr. Alan Turing Jr." }
          ].map((leader, i) => (
            <div key={i} className="space-y-4 group">
              <div className="aspect-square bg-slate-100 rounded-3xl overflow-hidden relative">
                <div className="absolute inset-0 bg-gradient-to-t from-[#002d5a]/40 to-transparent"></div>
                <div className="w-full h-full flex items-center justify-center text-slate-300">
                  <Users className="w-16 h-16 opacity-20 group-hover:scale-110 transition-transform" />
                </div>
              </div>
              <div>
                <h4 className="font-bold text-xl text-[#002d5a]">{leader.name}</h4>
                <p className="text-sm font-semibold text-[#00a69c] uppercase tracking-wider">{leader.role}</p>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}

export default AboutUs
