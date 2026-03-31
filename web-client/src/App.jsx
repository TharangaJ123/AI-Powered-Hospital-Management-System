import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'

function App() {
  return (
    <div className="min-h-screen bg-[#fcfdfe] text-slate-900 flex flex-col">
      <Navbar />
      <Home />
      <Footer />
    </div>
  )
}

export default App
