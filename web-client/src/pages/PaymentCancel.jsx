import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { AlertCircle, ArrowLeft } from 'lucide-react'

const PaymentCancel = () => {
  const navigate = useNavigate()

  useEffect(() => {
    // Clear pending appointment
    localStorage.removeItem('pendingAppointment')
  }, [navigate])

  return (
    <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
      <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center">
        <div className="w-20 h-20 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-6 text-amber-600">
          <AlertCircle className="w-12 h-12" />
        </div>
        <h2 className="text-3xl font-bold text-slate-800 mb-4">Payment Cancelled</h2>
        <p className="text-slate-600 mb-8">
          Your payment has been cancelled. No charges were made to your account.
        </p>
        <div className="space-y-3">
          <button
            onClick={() => navigate('/payment')}
            className="w-full px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors flex items-center justify-center gap-2"
          >
            Try Payment Again
          </button>
          <button
            onClick={() => navigate('/appointments')}
            className="w-full px-6 py-3 bg-slate-200 text-slate-700 rounded-xl hover:bg-slate-300 transition-colors flex items-center justify-center gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Appointments
          </button>
        </div>
      </div>
    </main>
  )
}

export default PaymentCancel
