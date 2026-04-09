import { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { CheckCircle, ArrowLeft } from 'lucide-react'
import { createAppointment } from '../services/appointments'

const PaymentSuccess = ({ session }) => {
  const navigate = useNavigate()
  const location = useLocation()
  const [isProcessing, setIsProcessing] = useState(true)
  const [isSuccess, setIsSuccess] = useState(false)
  const [error, setError] = useState(null)

  const user = session?.user
  const token = session?.token

  useEffect(() => {
    const completePayment = async () => {
      try {
        // Get order ID from URL params or query string
        const urlParams = new URLSearchParams(location.search)
        const orderId = urlParams.get('order_id')
        
        // Get appointment details from localStorage or session
        const appointmentDetails = JSON.parse(localStorage.getItem('pendingAppointment') || '{}')
        
        if (!appointmentDetails.doctorName) {
          setError('No appointment details found')
          setIsProcessing(false)
          return
        }

        // Create appointment after successful payment
        const appointmentDate = new Date(`${appointmentDetails.date}T${appointmentDetails.timeSlot}:00`).toISOString()
        
        const payload = {
          patientId: appointmentDetails.patientId,
          doctorId: appointmentDetails.doctorId,
          appointmentDate: appointmentDate,
          fullName: appointmentDetails.fullName,
          email: appointmentDetails.email,
          phoneNumber: appointmentDetails.phoneNumber,
          reason: appointmentDetails.reason,
          consultationType: appointmentDetails.consultationType
        }
        
        await createAppointment(payload, token)
        
        // Clear pending appointment
        localStorage.removeItem('pendingAppointment')
        
        setIsSuccess(true)
        setIsProcessing(false)
        
        // Redirect to appointments after 3 seconds
        setTimeout(() => {
          navigate('/appointments', { 
            state: { 
              paymentSuccess: true,
              appointmentDetails: appointmentDetails 
            } 
          })
        }, 3000)
        
      } catch (err) {
        setError('Payment successful but appointment booking failed: ' + err.message)
        setIsProcessing(false)
      }
    }

    completePayment()
  }, [location, navigate, token])

  if (isProcessing) {
    return (
      <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center">
          <div className="w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto mb-6"></div>
          <h2 className="text-2xl font-bold text-slate-800 mb-4">Processing Payment...</h2>
          <p className="text-slate-600">Please wait while we confirm your payment and book your appointment.</p>
        </div>
      </main>
    )
  }

  if (error) {
    return (
      <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6 text-red-600">
            <CheckCircle className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-slate-800 mb-4">Payment Error</h2>
          <p className="text-slate-600 mb-6">{error}</p>
          <button
            onClick={() => navigate('/appointments')}
            className="px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors"
          >
            Back to Appointments
          </button>
        </div>
      </main>
    )
  }

  return (
    <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
      <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center animate-in fade-in zoom-in duration-500">
        <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6 text-green-600">
          <CheckCircle className="w-12 h-12" />
        </div>
        <h2 className="text-3xl font-bold text-slate-800 mb-4">Payment Successful!</h2>
        <p className="text-slate-600 mb-8">
          Your payment has been processed successfully and appointment has been booked!
        </p>
        <div className="text-sm text-slate-500 mb-6">
          Redirecting to your appointments...
        </div>
      </div>
    </main>
  )
}

export default PaymentSuccess
