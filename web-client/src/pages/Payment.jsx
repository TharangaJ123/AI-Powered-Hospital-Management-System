import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { 
  CreditCard, 
  ArrowLeft, 
  Shield, 
  Lock, 
  CheckCircle, 
  AlertCircle,
  Calendar,
  Clock,
  User,
  Stethoscope
} from 'lucide-react'

const Payment = ({ session }) => {
  const navigate = useNavigate()
  const location = useLocation()
  const [isProcessing, setIsProcessing] = useState(false)
  const [isSuccess, setIsSuccess] = useState(false)
  const [errors, setErrors] = useState({})
  const [selectedMethod, setSelectedMethod] = useState('card')
  
  // Get user and token from session
  const user = session?.user
  const token = session?.token
  
  // Get appointment details from location state
  const appointmentDetails = location.state?.appointmentDetails || {}
  
  const [formData, setFormData] = useState({
    cardNumber: '',
    cardName: '',
    expiryDate: '',
    cvv: '',
    // UPI details
    upiId: '',
    // Net banking
    bankName: '',
    accountNumber: '',
    ifscCode: ''
  })

  const consultationFee = 2500 // Fixed consultation fee
  const platformFee = 50
  const totalAmount = consultationFee + platformFee

  useEffect(() => {
    // If no appointment details, redirect back
    if (!appointmentDetails.doctorName) {
      navigate('/appointments')
    }
  }, [appointmentDetails, navigate])

  // Backend API integration - no PayHere script needed
  const [payhereReady, setPayhereReady] = useState(true)

  // Backend handles PayHere callbacks - no frontend handlers needed

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
    
    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }))
    }
  }

  const validateForm = () => {
    const newErrors = {}
    
    if (selectedMethod === 'card') {
      if (!formData.cardNumber.trim()) {
        newErrors.cardNumber = 'Card number is required'
      } else if (!/^\d{16}$/.test(formData.cardNumber.replace(/\s/g, ''))) {
        newErrors.cardNumber = 'Invalid card number'
      }
      
      if (!formData.cardName.trim()) {
        newErrors.cardName = 'Cardholder name is required'
      }
      
      if (!formData.expiryDate.trim()) {
        newErrors.expiryDate = 'Expiry date is required'
      } else if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(formData.expiryDate)) {
        newErrors.expiryDate = 'Invalid format (MM/YY)'
      }
      
      if (!formData.cvv.trim()) {
        newErrors.cvv = 'CVV is required'
      } else if (!/^\d{3,4}$/.test(formData.cvv)) {
        newErrors.cvv = 'Invalid CVV'
      }
    } else if (selectedMethod === 'upi') {
      if (!formData.upiId.trim()) {
        newErrors.upiId = 'UPI ID is required'
      } else if (!/^[a-zA-Z0-9._-]+@[a-zA-Z]{2,}$/.test(formData.upiId)) {
        newErrors.upiId = 'Invalid UPI ID format'
      }
    } else if (selectedMethod === 'netbanking') {
      if (!formData.bankName.trim()) {
        newErrors.bankName = 'Bank name is required'
      }
      if (!formData.accountNumber.trim()) {
        newErrors.accountNumber = 'Account number is required'
      }
      if (!formData.ifscCode.trim()) {
        newErrors.ifscCode = 'IFSC code is required'
      } else if (!/^[A-Z]{4}0[A-Z0-9]{6}$/.test(formData.ifscCode.toUpperCase())) {
        newErrors.ifscCode = 'Invalid IFSC code'
      }
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (selectedMethod === 'card') {
      // For card payment, use PayHere
      setIsProcessing(true)
      await initiatePayHerePayment()
      return
    }
    
    if (!validateForm()) {
      return
    }
    
    setIsProcessing(true)
    
    try {
      // Simulate payment processing
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      // Store appointment details for PaymentSuccess page to create appointment
      localStorage.setItem('pendingAppointment', JSON.stringify(appointmentDetails))
      
      setIsSuccess(true)
      
      // After success, redirect to PaymentSuccess page after 3 seconds
      setTimeout(() => {
        navigate('/payment-success', { 
          state: { 
            paymentSuccess: true,
            appointmentDetails: appointmentDetails 
          } 
        })
      }, 3000)
    } catch (error) {
      setIsProcessing(false)
      alert('Payment processing failed: ' + error.message)
    }
  }

  const formatCardNumber = (value) => {
    const v = value.replace(/\s/g, '')
    const matches = v.match(/\d{4,16}/g)
    const match = matches && matches[0] || ''
    const parts = []
    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4))
    }
    if (parts.length) {
      return parts.join(' ')
    } else {
      return v
    }
  }

  const handleCardNumberChange = (e) => {
    const formatted = formatCardNumber(e.target.value)
    setFormData(prev => ({ ...prev, cardNumber: formatted }))
  }


  const initiatePayHerePayment = async () => {
    // Check if backend is ready
    if (!payhereReady) {
      console.error('PayHere backend payment system not ready...')
      alert('Payment system is initializing. Please wait a moment and try again.')
      setIsProcessing(false)
      return
    }

    // Check rate limiting - prevent too rapid requests
    const lastPaymentAttempt = localStorage.getItem('lastPaymentAttempt')
    const now = Date.now()
    if (lastPaymentAttempt && (now - parseInt(lastPaymentAttempt)) < 5000) {
      alert('Please wait a few seconds before trying again.')
      setIsProcessing(false)
      return
    }

    // Generate unique order ID
    const orderId = 'ORD_' + Date.now()
    
    // Check if running in development
    if (window.location.hostname === 'localhost') {
      console.log('PayHere backend API mode: sandbox')
      console.log('Backend URL: http://localhost:8084')
      console.log('Redirect URLs:', window.location.origin)
    }
    
    // Save appointment details for success page
    localStorage.setItem('pendingAppointment', JSON.stringify(appointmentDetails))
    localStorage.setItem('lastPaymentAttempt', now.toString())
    
    // Start PayHere payment via backend API
    try {
      console.log('Starting PayHere payment via backend API...')
      
      // Call backend to get PayHere checkout URL
      const response = await fetch('http://localhost:8084/api/payments/payhere/initiate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          orderId: orderId,
          amount: totalAmount,
          items: `Appointment with ${appointmentDetails.doctorName}`,
          firstName: appointmentDetails.fullName.split(' ')[0] || 'Patient',
          lastName: appointmentDetails.fullName.split(' ').slice(1).join(' ') || 'Name',
          email: appointmentDetails.email || '',
          phone: appointmentDetails.phoneNumber || '0000000000',
          address: "Colombo",
          city: "Colombo",
          country: "Sri Lanka",
          // Appointment details for automatic booking after payment
          patientId: appointmentDetails.patientId,
          doctorId: appointmentDetails.doctorId,
          appointmentDate: new Date(`${appointmentDetails.date}T${appointmentDetails.timeSlot}:00`).toISOString(),
          consultationType: appointmentDetails.consultationType,
          reason: appointmentDetails.reason,
          specialty: appointmentDetails.specialty
        })
      })

      if (!response.ok) {
        if (response.status === 429) {
          throw new Error('PayHere rate limit exceeded. Please wait a few minutes before trying again.')
        }
        throw new Error('Failed to initiate payment with backend')
      }

      const data = await response.json()
      console.log('Backend response:', data)
      console.log('PayHere checkout URL:', data.checkoutUrl)

      // Create form and submit to PayHere
      const form = document.createElement('form')
      form.method = 'POST'
      form.action = data.checkoutUrl
      form.style.display = 'none'

      // Add all form fields from backend
      Object.keys(data.paymentFormFields).forEach(key => {
        const input = document.createElement('input')
        input.type = 'hidden'
        input.name = key
        input.value = data.paymentFormFields[key]
        form.appendChild(input)
      })

      document.body.appendChild(form)
      form.submit()

    } catch (error) {
      console.error('PayHere backend API error:', error)
      
      if (error.message.includes('rate limit')) {
        alert('PayHere is experiencing high traffic. Please wait 2-3 minutes before trying again.')
      } else {
        alert('Payment failed to start. Please try again.')
      }
      
      setIsProcessing(false)
      // Clear rate limiting on error to allow immediate retry for non-rate-limit errors
      if (!error.message.includes('rate limit')) {
        localStorage.removeItem('lastPaymentAttempt')
      }
    }
  }

  const handlePaymentCompletion = async () => {
    // Appointment is now automatically created by the payment service
    // No manual booking needed here
    setIsSuccess(true)
    
    // After success, redirect to appointments page after 3 seconds
    setTimeout(() => {
      navigate('/appointments', { 
        state: { 
          paymentSuccess: true,
          appointmentDetails: appointmentDetails,
          autoBooked: true // Flag to indicate automatic booking
        } 
      })
    }, 3000)
  }

  if (isSuccess) {
    return (
      <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center animate-in fade-in zoom-in duration-500">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6 text-green-600">
            <CheckCircle className="w-12 h-12" />
          </div>
          <h2 className="text-3xl font-bold text-slate-800 mb-4">
            Payment Successful!
          </h2>
          <p className="text-slate-600 mb-8">
            Your payment of Rs. {totalAmount.toFixed(2)} has been processed successfully. Appointment booking confirmed!
          </p>
          <div className="text-sm text-slate-500 mb-6">
            Redirecting to your appointments...
          </div>
        </div>
      </main>
    )
  }

  return (
    <main className="pt-28 pb-16 px-4 min-h-screen bg-[radial-gradient(circle_at_0%_0%,#e0f2fe_0%,#f8fafc_45%,#eef2ff_100%)] relative overflow-hidden">
      <div className="absolute -top-32 -left-20 w-72 h-72 bg-cyan-200/30 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-52 -right-20 w-72 h-72 bg-blue-200/30 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-6xl mx-auto relative z-10">
        {/* Header */}
        <div className="mb-6 sm:mb-8">
          <button
            onClick={() => navigate('/appointments')}
            className="group inline-flex items-center gap-2 text-[#0066cc] hover:text-[#0052a3] font-semibold mb-4 sm:mb-5 transition-colors text-sm sm:text-base"
          >
            <ArrowLeft className="w-4 h-4 sm:w-5 sm:h-5 transition-transform group-hover:-translate-x-1" />
            Back to Booking
          </button>
          <div className="rounded-3xl border border-white/70 bg-white/70 backdrop-blur-md shadow-xl p-4 sm:p-6 lg:p-8">
            <div className="flex flex-col sm:flex-row items-start gap-3 sm:gap-4 mb-4">
              <div className="w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-full flex items-center justify-center text-white flex-shrink-0">
                <Shield className="w-4 h-4 sm:w-6 sm:h-6" />
              </div>
              <div className="flex-1 min-w-0">
                <h1 className="text-2xl sm:text-3xl font-black text-slate-800">Secure Payment</h1>
                <p className="text-sm sm:text-base text-slate-600">Complete your appointment booking with confidence</p>
              </div>
            </div>
            
            {/* Security Badge */}
            <div className="flex items-center gap-2 text-xs sm:text-sm text-green-600 bg-green-50 px-3 sm:px-4 py-2 rounded-xl inline-flex">
              <Lock className="w-3 h-3 sm:w-4 sm:h-4" />
              <span className="hidden sm:inline">256-bit SSL Encrypted Payment</span>
              <span className="sm:hidden">SSL Encrypted</span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 lg:gap-8">
          {/* Payment Form */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-3xl shadow-2xl border border-white p-4 sm:p-6 lg:p-8 h-full">
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Payment Method Selection */}
                <div>
                  <div className="flex items-center gap-2 mb-4">
                    <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-lg flex items-center justify-center text-white flex-shrink-0 shadow-lg">
                      <CreditCard className="w-5 h-5" />
                    </div>
                    <h3 className="text-lg sm:text-xl font-bold text-slate-800">Select Payment Method</h3>
                  </div>
                  <div className="grid grid-cols-1 gap-3 sm:gap-2">
                    <button
                      type="button"
                      onClick={() => setSelectedMethod('card')}
                      className={`p-4 sm:p-5 rounded-xl border-2 transition-all hover:scale-105 hover:shadow-lg ${
                        selectedMethod === 'card'
                          ? 'border-blue-500 bg-blue-50 text-blue-700 shadow-md'
                          : 'border-slate-200 hover:border-slate-300 bg-white'
                      }`}
                    >
                      <div className="flex items-center justify-center gap-2 sm:gap-3 mb-3 flex-wrap">
                        <CreditCard className="w-5 h-5 sm:w-6 sm:h-6" />
                        <span className="text-xs sm:text-sm font-bold bg-gradient-to-r from-orange-500 to-red-500 text-white px-2 sm:px-3 py-1 rounded-full">PayHere</span>
                      </div>
                      <span className="text-sm sm:text-base font-medium">Card with PayHere</span>
                      <p className="text-xs sm:text-sm text-slate-500 mt-1">Secure payment via PayHere</p>
                    </button>
                  </div>
                </div>

                {/* Card Payment Form */}
                {selectedMethod === 'card' && (
                  <div className="bg-gradient-to-r from-orange-50 to-red-50 border border-orange-200 rounded-xl p-4 sm:p-6">
                    <div className="flex flex-col sm:flex-row items-start gap-3 sm:gap-4 mb-4">
                      <div className="w-12 h-12 sm:w-14 sm:h-14 lg:w-12 lg:h-12 bg-gradient-to-br from-orange-500 to-red-500 rounded-full flex items-center justify-center text-white flex-shrink-0 shadow-lg">
                        <CreditCard className="w-5 h-5 sm:w-6 sm:h-6" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className="font-bold text-slate-800 text-base sm:text-lg">PayHere Payment</h4>
                        <p className="text-sm sm:text-base text-slate-600">Secure payment via PayHere gateway</p>
                        {!payhereReady && (
                          <p className="text-xs text-amber-600 mt-1">
                            ⏳ Backend payment system initializing...
                          </p>
                        )}
                        {payhereReady && (
                          <p className="text-xs text-green-600 mt-1">
                            ✅ Backend payment system ready
                          </p>
                        )}
                        {!payhereReady && (
                          <button 
                            type="button"
                            onClick={() => window.location.reload()}
                            className="text-xs text-blue-600 mt-1 underline"
                          >
                            🔄 Refresh page
                          </button>
                        )}
                      </div>
                    </div>
                    
                    <div className="space-y-1 sm:space-y-2 text-sm sm:text-base">
                      <div className="flex items-center gap-1 text-green-600 flex-wrap p-2 sm:p-3 bg-green-50 rounded-lg">
                        <CheckCircle className="w-4 h-4 sm:w-5 sm:h-5 flex-shrink-0" />
                        <span className="font-medium">256-bit SSL encryption</span>
                      </div>
                      <div className="flex items-center gap-1 text-green-600 flex-wrap p-2 sm:p-3 bg-green-50 rounded-lg">
                        <CheckCircle className="w-4 h-4 sm:w-5 sm:h-5 flex-shrink-0" />
                        <span className="font-medium">Supports Visa, Mastercard, AMEX</span>
                      </div>
                      <div className="flex items-center gap-1 text-green-600 flex-wrap p-2 sm:p-3 bg-green-50 rounded-lg">
                        <CheckCircle className="w-4 h-4 sm:w-5 sm:h-5 flex-shrink-0" />
                        <span className="font-medium">Instant payment confirmation</span>
                      </div>
                    </div>

                    <div className="mt-2 p-2 sm:p-3 bg-white/70 rounded-lg">
                      <p className="text-xs text-slate-600 leading-relaxed">
                        <strong>Note:</strong> You will be redirected to PayHere's secure payment page to complete the transaction.
                      </p>
                    </div>
                  </div>
                )}


                {/* Submit Button */}
                <button
                  type="submit"
                  disabled={isProcessing}
                  className="w-full bg-gradient-to-r from-blue-600 to-cyan-600 text-white py-3 sm:py-4 font-bold rounded-xl hover:opacity-90 transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-xl shadow-blue-300/50 text-sm sm:text-base"
                >
                  {isProcessing ? (
                    <span className="flex items-center justify-center gap-2">
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      {selectedMethod === 'card' ? 'Redirecting to PayHere...' : 'Processing...'}
                    </span>
                  ) : (
                    `Pay Rs. ${totalAmount.toFixed(2)} `
                  )}
                </button>

              </form>
            </div>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-3xl shadow-2xl border border-white p-4 sm:p-5 lg:p-6 sticky top-4 sm:top-6 lg:top-32 h-full">
              <div className="flex items-center gap-3 mb-6">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-lg flex items-center justify-center text-white flex-shrink-0 shadow-lg">
                  <Calendar className="w-5 h-5" />
                </div>
                <h3 className="text-lg sm:text-xl font-bold text-slate-800">Order Summary</h3>
              </div>
              
              {/* Appointment Details */}
              <div className="bg-slate-50 rounded-xl p-3 sm:p-4 mb-4">
                <h4 className="text-base font-semibold text-slate-700 mb-3 flex items-center gap-2">
                  <User className="w-5 h-5" />
                  Appointment Details
                </h4>
                <div className="space-y-2 sm:space-y-3">
                  <div className="flex items-start gap-48">
                    <span className="text-sm text-slate-600 w-16 flex-shrink-0">Patient</span>
                    <span className="text-sm font-medium text-slate-800 flex-1">{appointmentDetails.fullName || user?.name || 'Guest User'}</span>
                  </div>
                  <div className="flex items-start gap-48">
                    <span className="text-sm text-slate-600 w-16 flex-shrink-0">Doctor</span>
                    <span className="text-sm font-medium text-slate-800 flex-1">{appointmentDetails.doctorName || 'Selected Doctor'}</span>
                  </div>
                  <div className="flex items-start gap-48">
                    <span className="text-sm text-slate-600 w-16 flex-shrink-0">Date</span>
                    <span className="text-sm font-medium text-slate-800 flex-1">{appointmentDetails.date || 'Selected Date'}</span>
                  </div>
                  <div className="flex items-start gap-48">
                    <span className="text-sm text-slate-600 w-16 flex-shrink-0">Time</span>
                    <span className="text-sm font-medium text-slate-800 flex-1">{appointmentDetails.timeSlot || 'Selected Time'}</span>
                  </div>
                </div>
              </div>

              {/* Cost Breakdown */}
              <div className="bg-gradient-to-br from-blue-50 to-cyan-50 rounded-xl p-3 sm:p-4 mb-4">
                <h4 className="text-base font-semibold text-slate-700 mb-3 flex items-center gap-2">
                  <CreditCard className="w-5 h-5" />
                  Cost Breakdown
                </h4>
                <div className="space-y-2 sm:space-y-3">
                  <div className="flex justify-between items-center py-3 border-b border-blue-100">
                    <span className="text-sm text-slate-600">Consultation Fee</span>
                    <span className="text-sm font-medium text-slate-800">Rs. {consultationFee.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between items-center py-3 border-b border-blue-100">
                    <span className="text-sm text-slate-600">Platform Fee</span>
                    <span className="text-sm font-medium text-slate-800">Rs. {platformFee.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between items-center py-4 bg-gradient-to-r from-blue-500 to-cyan-500 text-white rounded-lg px-4">
                    <span className="text-base sm:text-lg font-bold">Total Amount</span>
                    <span className="text-base sm:text-lg font-bold">Rs. {totalAmount.toFixed(2)}</span>
                  </div>
                </div>
              </div>

              {/* Security Info */}
              <div className="bg-green-50 border border-green-200 rounded-xl p-3 sm:p-4">
                <div className="flex items-start gap-2 sm:gap-3">
                  <Shield className="w-4 h-4 sm:w-5 sm:h-5 text-green-600 mt-0.5" />
                  <div className="flex-1 min-w-0">
                    <h4 className="font-medium text-green-800 text-sm sm:text-base mb-1">Secure Payment</h4>
                    <p className="text-xs sm:text-sm text-green-700 leading-relaxed">
                      Your payment information is encrypted and secure. We never store your card details.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  )
}

export default Payment
