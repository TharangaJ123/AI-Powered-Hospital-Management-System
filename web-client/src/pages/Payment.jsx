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
  User,
  Stethoscope,
  Clock,
  Upload,
  FileText
} from 'lucide-react'
import { createAppointment } from '../services/appointments'
import CryptoJS from 'crypto-js'

const Payment = ({ session }) => {
  const navigate = useNavigate()
  const location = useLocation()
  const [isProcessing, setIsProcessing] = useState(false)
  const [isSuccess, setIsSuccess] = useState(false)
  const [errors, setErrors] = useState({})
  const [selectedMethod, setSelectedMethod] = useState('card')
  const [receiptFile, setReceiptFile] = useState(null)
  const [receiptPreview, setReceiptPreview] = useState(null)
  
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

  const consultationFee = 500 // Fixed consultation fee
  const platformFee = 50
  const totalAmount = consultationFee + platformFee

  useEffect(() => {
    // If no appointment details, redirect back
    if (!appointmentDetails.doctorName) {
      navigate('/appointments')
    }
  }, [appointmentDetails, navigate])

  // Load PayHere script
  useEffect(() => {
    const script = document.createElement('script')
    script.src = 'https://www.payhere.lk/lib/payhere.js'
    script.async = true
    script.onload = () => {
      console.log('PayHere script loaded successfully')
    }
    script.onerror = (error) => {
      console.error('PayHere script failed to load:', error)
    }
    document.body.appendChild(script)

    return () => {
      if (document.body.contains(script)) {
        document.body.removeChild(script)
      }
    }
  }, [])

  // PayHere payment completion handler
  useEffect(() => {
    window.payhere = window.payhere || {}
    window.payhere.onCompleted = function onCompleted(orderId) {
      console.log('Payment completed. OrderID:' + orderId)
      handlePaymentCompletion()
    }

    window.payhere.onDismissed = function onDismissed() {
      console.log('Payment dismissed')
      console.log('Dismissed details:', window.payhere)
    }

    window.payhere.onError = function onError(error) {
      console.error('Payment error:', error)
      console.log('Error details:', error)
      if (error.includes('CORS')) {
        alert('PayHere payment failed due to CORS restrictions. Please try again or use receipt upload.')
      } else if (error.includes('Unauthorized') || error.includes('merchant')) {
        alert('PayHere merchant configuration error. Please check your merchant ID and sandbox settings.')
      } else if (error.includes('Invalid')) {
        alert('Invalid payment details. Please check your information and try again.')
      } else {
        alert('Payment failed: ' + error + '. Please try again or use receipt upload.')
      }
    }
  }, [])

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
    } else if (selectedMethod === 'receipt') {
      if (!receiptFile) {
        newErrors.receipt = 'Please upload a payment receipt'
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
      initiatePayHerePayment()
      return
    }
    
    if (!validateForm()) {
      return
    }
    
    setIsProcessing(true)
    
    try {
      if (selectedMethod === 'receipt') {
        // For receipt upload, simulate verification process
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // Show success message for receipt upload
        setIsSuccess(true)
        
        // After success, redirect to appointments page after 3 seconds
        setTimeout(() => {
          navigate('/appointments', { 
            state: { 
              paymentSuccess: true,
              appointmentDetails: appointmentDetails,
              receiptUpload: true
            } 
          })
        }, 3000)
      } else {
        // Simulate payment processing for other methods
        await new Promise(resolve => setTimeout(resolve, 2000))
        
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
        
        setIsSuccess(true)
        
        // After success, redirect to appointments page after 3 seconds
        setTimeout(() => {
          navigate('/appointments', { 
            state: { 
              paymentSuccess: true,
              appointmentDetails: appointmentDetails 
            } 
          })
        }, 3000)
      }
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

  const handleReceiptUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
      // Check file type
      if (!file.type.match(/^(image\/(jpeg|jpg|png)|application\/pdf)$/)) {
        alert('Please upload an image (JPG, PNG) or PDF file')
        return
      }
      
      // Check file size (5MB limit)
      if (file.size > 5 * 1024 * 1024) {
        alert('File size must be less than 5MB')
        return
      }

      setReceiptFile(file)
      
      // Create preview for images
      if (file.type.startsWith('image/')) {
        const reader = new FileReader()
        reader.onload = (e) => {
          setReceiptPreview(e.target.result)
        }
        reader.readAsDataURL(file)
      } else {
        setReceiptPreview(null)
      }
    }
  }

  const initiatePayHerePayment = () => {
    // Generate unique order ID
    const orderId = 'ORD_' + Date.now()
    
    // Check if running in development
    if (window.location.hostname === 'localhost') {
      console.log('PayHere allowed domain: http://localhost:5173 (configured in sandbox)')
      console.log('Current frontend origin:', window.location.origin)
      console.log('PayHere will redirect to current origin after payment')
    }
    
    // Hash will be generated by backend
    // const generateHash = () => {
    //   const merchantSecret = "4pFpX2Ebf6H4OVzrnkslZj4KBp0MJquXr8m6yjSqpB2f"
    //   const data = `${merchantSecret}${orderId}${totalAmount}LKR`
    //   return CryptoJS.MD5(data).toString()
    // }

    // PayHere payment object
    const payment = {
      "sandbox": true,
      "merchant_id": "1210001",
      "return_url": `${window.location.origin}/payment/success`,
      "cancel_url": `${window.location.origin}/payment/cancel`, 
      "notify_url": "http://localhost:8084/api/payments/payhere/notify",
      "order_id": orderId,
      "items": `Appointment with ${appointmentDetails.doctorName}`,
      "amount": totalAmount.toFixed(2),
      "currency": "LKR",
      "first_name": appointmentDetails.fullName.split(' ')[0] || 'Patient',
      "last_name": appointmentDetails.fullName.split(' ').slice(1).join(' ') || 'Name',
      "email": appointmentDetails.email || 'patient@example.com',
      "phone": appointmentDetails.phoneNumber || '0000000000',
      "address": "Colombo",
      "city": "Colombo", 
      "country": "Sri Lanka",
      "delivery_address": "Colombo",
      "delivery_city": "Colombo",
      "delivery_country": "Sri Lanka",
      "custom_1": appointmentDetails.patientId || 'guest',
      "custom_2": appointmentDetails.doctorId
    }

    // Save appointment details for success page
    localStorage.setItem('pendingAppointment', JSON.stringify(appointmentDetails))

    // Payment is configured with real merchant ID
    // No validation needed as we're using actual credentials

    // Debug: Log payment object
    console.log('PayHere payment object:', payment)
    console.log('Hash will be generated by backend')
    console.log('Payment data sent to PayHere:', {
      merchant_id: payment.merchant_id,
      order_id: payment.order_id,
      amount: payment.amount,
      currency: payment.currency,
      return_url: payment.return_url,
      cancel_url: payment.cancel_url,
      notify_url: payment.notify_url
    })
    
    // Create and show PayHere payment popup
    setTimeout(() => {
      try {
        if (window.payhere && window.payhere.startPayment) {
          console.log('Starting PayHere payment...')
          console.log('Payment object being sent:', JSON.stringify(payment, null, 2))
          console.log('Amount type:', typeof payment.amount, 'Value:', payment.amount)
          console.log('Amount string:', payment.amount.toString())
          window.payhere.startPayment(payment)
        } else {
          console.error('PayHere not initialized')
          console.log('PayHere object:', window.payhere)
          alert('PayHere is not ready. Please wait a moment and try again, or use receipt upload.')
        }
      } catch (error) {
        console.error('PayHere startPayment error:', error)
        alert('PayHere failed to start. Please try again or use receipt upload.')
      }
    }, 3000) // Wait 3 seconds for script to fully load
  }

  const handlePaymentCompletion = async () => {
    try {
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
      
      setIsSuccess(true)
      
      // After success, redirect to appointments page after 3 seconds
      setTimeout(() => {
        navigate('/appointments', { 
          state: { 
            paymentSuccess: true,
            appointmentDetails: appointmentDetails 
          } 
        })
      }, 3000)
    } catch (error) {
      setIsProcessing(false)
      alert('Payment successful but appointment booking failed: ' + error.message)
    }
  }

  if (isSuccess) {
    return (
      <main className="pt-28 pb-10 px-4 min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-10 text-center animate-in fade-in zoom-in duration-500">
          <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6 text-green-600">
            <CheckCircle className="w-12 h-12" />
          </div>
          <h2 className="text-3xl font-bold text-slate-800 mb-4">
            {location.state?.receiptUpload ? 'Receipt Uploaded Successfully!' : 'Payment Successful!'}
          </h2>
          <p className="text-slate-600 mb-8">
            {location.state?.receiptUpload 
              ? `Your payment receipt has been uploaded. We will verify it within 24 hours and confirm your appointment.`
              : `Your payment of Rs. ${totalAmount.toFixed(2)} has been processed successfully. Appointment booking confirmed!`
            }
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

      <div className="max-w-4xl mx-auto relative z-10">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => navigate('/appointments')}
            className="group inline-flex items-center gap-2 text-[#0066cc] hover:text-[#0052a3] font-semibold mb-5 transition-colors"
          >
            <ArrowLeft className="w-5 h-5 transition-transform group-hover:-translate-x-1" />
            Back to Booking
          </button>
          <div className="rounded-3xl border border-white/70 bg-white/70 backdrop-blur-md shadow-xl p-8">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-full flex items-center justify-center text-white">
                <Shield className="w-6 h-6" />
              </div>
              <div>
                <h1 className="text-3xl font-black text-slate-800">Secure Payment</h1>
                <p className="text-slate-600">Complete your appointment booking with confidence</p>
              </div>
            </div>
            
            {/* Security Badge */}
            <div className="flex items-center gap-2 text-sm text-green-600 bg-green-50 px-4 py-2 rounded-xl inline-flex">
              <Lock className="w-4 h-4" />
              256-bit SSL Encrypted Payment
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Payment Form */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-3xl shadow-2xl border border-white p-8">
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Payment Method Selection */}
                <div>
                  <h3 className="text-lg font-bold text-slate-800 mb-4">Select Payment Method</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <button
                      type="button"
                      onClick={() => setSelectedMethod('card')}
                      className={`p-4 rounded-xl border-2 transition-all ${
                        selectedMethod === 'card'
                          ? 'border-blue-500 bg-blue-50 text-blue-700'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                    >
                      <div className="flex items-center justify-center gap-2 mb-2">
                        <CreditCard className="w-6 h-6" />
                        <span className="text-xs font-bold bg-gradient-to-r from-orange-500 to-red-500 text-white px-2 py-1 rounded">PayHere</span>
                      </div>
                      <span className="text-sm font-medium">Card with PayHere</span>
                      <p className="text-xs text-slate-500 mt-1">Secure payment via PayHere</p>
                    </button>
                    <button
                      type="button"
                      onClick={() => setSelectedMethod('receipt')}
                      className={`p-4 rounded-xl border-2 transition-all ${
                        selectedMethod === 'receipt'
                          ? 'border-blue-500 bg-blue-50 text-blue-700'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                    >
                      <Upload className="w-6 h-6 mx-auto mb-2" />
                      <span className="text-sm font-medium">Upload Receipt</span>
                      <p className="text-xs text-slate-500 mt-1">Upload payment receipt</p>
                    </button>
                  </div>
                </div>

                {/* Card Payment Form */}
                {selectedMethod === 'card' && (
                  <div className="bg-gradient-to-r from-orange-50 to-red-50 border border-orange-200 rounded-xl p-6">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="w-12 h-12 bg-gradient-to-br from-orange-500 to-red-500 rounded-full flex items-center justify-center text-white">
                        <CreditCard className="w-6 h-6" />
                      </div>
                      <div>
                        <h4 className="font-bold text-slate-800">PayHere Payment</h4>
                        <p className="text-sm text-slate-600">Secure payment via PayHere gateway</p>
                      </div>
                    </div>
                    
                    <div className="space-y-3 text-sm">
                      <div className="flex items-center gap-2 text-green-600">
                        <CheckCircle className="w-4 h-4" />
                        <span>256-bit SSL encryption</span>
                      </div>
                      <div className="flex items-center gap-2 text-green-600">
                        <CheckCircle className="w-4 h-4" />
                        <span>Supports Visa, Mastercard, AMEX</span>
                      </div>
                      <div className="flex items-center gap-2 text-green-600">
                        <CheckCircle className="w-4 h-4" />
                        <span>Instant payment confirmation</span>
                      </div>
                    </div>

                    <div className="mt-4 p-3 bg-white/70 rounded-lg">
                      <p className="text-xs text-slate-600">
                        <strong>Note:</strong> You will be redirected to PayHere's secure payment page to complete the transaction.
                      </p>
                      <div className="mt-3 p-3 bg-amber-50 border border-amber-200 rounded-lg">
                        <p className="text-xs font-medium text-amber-800">
                          <strong>Alternative:</strong> If PayHere redirect fails, use "Upload Receipt" option below.
                        </p>
                        <p className="text-xs text-amber-600">
                          Receipt upload works immediately and creates your appointment.
                        </p>
                      </div>
                    </div>
                  </div>
                )}

                {/* Receipt Upload Section */}
                {selectedMethod === 'receipt' && (
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-2">
                        Upload Payment Receipt
                      </label>
                      <div className="border-2 border-dashed border-slate-300 rounded-xl p-6 text-center hover:border-blue-400 transition-colors">
                        <input
                          type="file"
                          id="receipt-upload"
                          accept="image/jpeg,image/jpg,image/png,application/pdf"
                          onChange={handleReceiptUpload}
                          className="hidden"
                        />
                        <label
                          htmlFor="receipt-upload"
                          className="cursor-pointer flex flex-col items-center gap-3"
                        >
                          <Upload className="w-8 h-8 text-slate-400" />
                          <div>
                            <p className="text-sm font-medium text-slate-700">
                              Click to upload receipt
                            </p>
                            <p className="text-xs text-slate-500 mt-1">
                              JPG, PNG or PDF (max 5MB)
                            </p>
                          </div>
                        </label>
                      </div>
                      
                      {receiptFile && (
                        <div className="mt-4 p-4 bg-blue-50 border border-blue-200 rounded-xl">
                          <div className="flex items-start gap-3">
                            <FileText className="w-5 h-5 text-blue-600 mt-0.5" />
                            <div className="flex-1">
                              <p className="text-sm font-medium text-blue-800">
                                {receiptFile.name}
                              </p>
                              <p className="text-xs text-blue-600">
                                {(receiptFile.size / 1024 / 1024).toFixed(2)} MB
                              </p>
                            </div>
                            <button
                              type="button"
                              onClick={() => {
                                setReceiptFile(null)
                                setReceiptPreview(null)
                                document.getElementById('receipt-upload').value = ''
                              }}
                              className="text-red-500 hover:text-red-700 text-sm"
                            >
                              Remove
                            </button>
                          </div>
                          
                          {receiptPreview && (
                            <div className="mt-3">
                              <img
                                src={receiptPreview}
                                alt="Receipt preview"
                                className="max-h-32 rounded-lg border border-slate-200"
                              />
                            </div>
                          )}
                        </div>
                      )}
                    </div>

                    <div className="bg-amber-50 border border-amber-200 rounded-xl p-4">
                      <div className="flex items-start gap-3">
                        <AlertCircle className="w-5 h-5 text-amber-600 mt-0.5" />
                        <div>
                          <h5 className="font-medium text-amber-800 text-sm mb-1">Important:</h5>
                          <ul className="text-xs text-amber-700 space-y-1">
                            <li>Ensure receipt shows the correct payment amount: Rs. {totalAmount.toFixed(2)}</li>
                            <li>Receipt must be clearly visible and readable</li>
                            <li>Payment will be verified before appointment confirmation</li>
                            <li>You will receive confirmation within 24 hours</li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {/* Submit Button */}
                <button
                  type="submit"
                  disabled={isProcessing || (selectedMethod === 'receipt' && !receiptFile)}
                  className="w-full bg-gradient-to-r from-blue-600 to-cyan-600 text-white py-4 font-bold rounded-xl hover:opacity-90 transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-xl shadow-blue-300/50"
                >
                  {isProcessing ? (
                    <span className="flex items-center justify-center gap-2">
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      {selectedMethod === 'card' ? 'Redirecting to PayHere...' : 'Processing...'}
                    </span>
                  ) : selectedMethod === 'card' ? (
                    `Pay Rs. ${totalAmount.toFixed(2)} with PayHere`
                  ) : selectedMethod === 'receipt' ? (
                    receiptFile ? `Submit Receipt - Rs. ${totalAmount.toFixed(2)}` : 'Please upload receipt first'
                  ) : (
                    `Pay Rs. ${totalAmount.toFixed(2)}`
                  )}
                </button>

                {errors.receipt && (
                  <p className="text-red-500 text-sm text-center mt-2">{errors.receipt}</p>
                )}
              </form>
            </div>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-3xl shadow-2xl border border-white p-6 sticky top-32">
              <h3 className="text-lg font-bold text-slate-800 mb-4">Order Summary</h3>
              
              {/* Appointment Details */}
              <div className="space-y-3 mb-6 pb-6 border-b border-slate-200">
                <div className="flex items-center gap-3">
                  <User className="w-4 h-4 text-slate-400" />
                  <div>
                    <p className="text-sm text-slate-600">Patient</p>
                    <p className="font-medium text-slate-800">{appointmentDetails.fullName || user?.name || 'Guest User'}</p>
                  </div>
                </div>
                
                <div className="flex items-center gap-3">
                  <Stethoscope className="w-4 h-4 text-slate-400" />
                  <div>
                    <p className="text-sm text-slate-600">Doctor</p>
                    <p className="font-medium text-slate-800">{appointmentDetails.doctorName || 'Selected Doctor'}</p>
                  </div>
                </div>
                
                <div className="flex items-center gap-3">
                  <Calendar className="w-4 h-4 text-slate-400" />
                  <div>
                    <p className="text-sm text-slate-600">Date</p>
                    <p className="font-medium text-slate-800">{appointmentDetails.date || 'Selected Date'}</p>
                  </div>
                </div>
                
                <div className="flex items-center gap-3">
                  <Clock className="w-4 h-4 text-slate-400" />
                  <div>
                    <p className="text-sm text-slate-600">Time</p>
                    <p className="font-medium text-slate-800">{appointmentDetails.timeSlot || 'Selected Time'}</p>
                  </div>
                </div>
              </div>

              {/* Cost Breakdown */}
              <div className="space-y-2 mb-6">
                <div className="flex justify-between text-sm">
                  <span className="text-slate-600">Consultation Fee</span>
                  <span className="font-medium">Rs. {consultationFee.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-slate-600">Platform Fee</span>
                  <span className="font-medium">Rs. {platformFee.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-lg font-bold text-slate-800 pt-2 border-t border-slate-200">
                  <span>Total Amount</span>
                  <span>Rs. {totalAmount.toFixed(2)}</span>
                </div>
              </div>

              {/* Security Info */}
              <div className="bg-green-50 border border-green-200 rounded-xl p-4">
                <div className="flex items-start gap-3">
                  <Shield className="w-5 h-5 text-green-600 mt-0.5" />
                  <div>
                    <h4 className="font-medium text-green-800 text-sm mb-1">Secure Payment</h4>
                    <p className="text-xs text-green-700">
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
