import React, { useState } from 'react';
import { Star, X, CheckCircle, AlertCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { submitReview } from '../services/reviews';

const ReviewFormModal = ({ isOpen, onClose, doctor, patient, token, onReviewSubmitted }) => {
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);
  const [comment, setComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (rating === 0) {
      setError('Please select a star rating.');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    const reviewData = {
      doctorId: doctor.id,
      patientId: patient.id,
      patientName: patient.fullName || 'Verified Patient',
      rating,
      comment
    };

    try {
      await submitReview(reviewData, token);
      setSuccess(true);
      setTimeout(() => {
        onClose();
        if (onReviewSubmitted) onReviewSubmitted();
      }, 2000);
    } catch (err) {
      setError(err.message || 'Failed to submit review. You must have a COMPLETED appointment with this doctor.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
        <motion.div 
          initial={{ opacity: 0, scale: 0.9, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.9, y: 20 }}
          className="bg-white rounded-[2.5rem] shadow-2xl w-full max-w-lg overflow-hidden"
        >
          <div className="p-8 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
            <div>
              <h2 className="text-2xl font-black text-[#002d5a]">Rate Your Experience</h2>
              <p className="text-sm text-slate-500 font-medium">Dr. {doctor.firstName} {doctor.lastName}</p>
            </div>
            <button 
              onClick={onClose}
              className="p-2 hover:bg-white rounded-full transition-colors text-slate-400 hover:text-slate-600 shadow-sm border border-slate-100"
            >
              <X className="w-6 h-6" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="p-8 space-y-8">
            {success ? (
              <div className="py-10 text-center space-y-4">
                <div className="w-20 h-20 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto shadow-inner">
                  <CheckCircle className="w-10 h-10" />
                </div>
                <h3 className="text-xl font-bold text-slate-800 tracking-tight">Review Submitted!</h3>
                <p className="text-slate-500 text-sm">Thank you for your valuable feedback.</p>
              </div>
            ) : (
              <>
                {error && (
                  <div className="p-4 bg-red-50 border border-red-100 rounded-2xl flex items-center gap-3 text-red-600 animate-pulse">
                    <AlertCircle className="w-5 h-5 flex-shrink-0" />
                    <p className="text-xs font-bold uppercase tracking-wide">{error}</p>
                  </div>
                )}

                <div className="space-y-4">
                  <label className="text-xs font-black text-slate-400 uppercase tracking-widest text-center block">Star Rating</label>
                  <div className="flex justify-center gap-4">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        type="button"
                        className="transition-transform active:scale-125 focus:outline-none"
                        onClick={() => setRating(star)}
                        onMouseEnter={() => setHover(star)}
                        onMouseLeave={() => setHover(0)}
                      >
                        <Star
                          className={`w-10 h-10 transition-colors ${
                            star <= (hover || rating) 
                            ? 'fill-amber-400 text-amber-400 drop-shadow-[0_0_8px_rgba(251,191,36,0.5)]' 
                            : 'text-slate-200 hover:text-slate-300'
                          }`}
                        />
                      </button>
                    ))}
                  </div>
                  <p className="text-center text-xs font-bold text-[#0066cc] uppercase tracking-tighter h-4">
                    {rating === 1 ? 'Poor' : rating === 2 ? 'Fair' : rating === 3 ? 'Good' : rating === 4 ? 'Very Good' : rating === 5 ? 'Exceptional' : ''}
                  </p>
                </div>

                <div className="space-y-3">
                  <label className="text-xs font-black text-slate-400 uppercase tracking-widest block">Detailed Feedback</label>
                  <textarea
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    required
                    placeholder="Describe your session, the specialist's expertise, and the overall experience..."
                    rows={4}
                    className="w-full p-6 bg-slate-50 border border-slate-100 rounded-2xl outline-none focus:ring-2 focus:ring-[#0066cc]/10 focus:border-[#0066cc] transition-all font-medium text-slate-600 resize-none leading-relaxed shadow-inner"
                  />
                </div>

                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full py-5 bg-[#002d5a] text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-[#003d7a] transition-all shadow-xl shadow-blue-900/20 active:scale-95 disabled:opacity-50"
                >
                  {isSubmitting ? 'Verifying Session & Syncing...' : 'Publish Official Review'}
                </button>
              </>
            )}
          </form>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};

export default ReviewFormModal;
