import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'

function App() {
  const handleBookAppointment = () => {
    const appointmentSection = document.getElementById('appointment-section')
    if (appointmentSection) {
      appointmentSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }

  return (
    <div className="min-h-screen bg-[#fcfdfe] text-slate-900 flex flex-col">
      <Navbar onBookAppointment={handleBookAppointment} />
      <Home onBookAppointment={handleBookAppointment} />
      <Footer onBookAppointment={handleBookAppointment} />
    </div>
  )
}

export default App
