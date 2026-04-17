import React from 'react';
import { Star, MessageCircle, Calendar, User, UserCheck } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const DoctorReviewsList = ({ reviews, averageRating, reviewCount }) => {
  return (
    <div className="bg-white rounded-[3rem] p-10 md:p-16 border border-slate-100 shadow-xl overflow-hidden space-y-12">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-8 pb-10 border-b border-slate-50">
        <div className="space-y-4">
          <div className="inline-flex items-center space-x-2 px-3 py-1 bg-amber-50 border border-amber-100 rounded-full text-amber-600 text-[10px] font-black uppercase tracking-widest animate-pulse">
            <Star className="w-3 h-3 fill-amber-400 border-none" />
            <span>Patient Feedback Verified</span>
          </div>
          <h2 className="text-4xl font-extrabold text-[#002d5a] tracking-tight">Patient Reviews</h2>
          <p className="text-slate-500 font-medium text-lg leading-relaxed">
            Real patient experiences gathered from our clinical network across the globe.
          </p>
        </div>
        
        <div className="flex gap-8 items-center bg-slate-50 p-8 rounded-[2rem] border border-white shadow-inner">
          <div className="text-center group">
            <p className="text-5xl font-black text-[#002d5a] group-hover:scale-110 transition-transform">
                {averageRating?.toFixed(1) || '0.0'}
            </p>
            <div className="flex justify-center mt-2 group-hover:rotate-3 transition-transform">
              {[1, 2, 3, 4, 5].map((s) => (
                <Star 
                  key={s} 
                  className={`w-4 h-4 ${s <= Math.round(averageRating || 0) ? 'fill-amber-400 text-amber-400 drop-shadow-[0_0_4px_rgba(251,191,36,0.5)]' : 'text-slate-200'}`} 
                />
              ))}
            </div>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-3">Average Clinical Score</p>
          </div>
          <div className="w-px h-16 bg-slate-200"></div>
          <div className="text-center group">
            <p className="text-5xl font-black text-[#00a69c] group-hover:scale-110 transition-transform">
                {reviewCount || 0}
            </p>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-5 mt-auto">Verified Evaluations Received</p>
          </div>
        </div>
      </div>

      <div className="grid gap-10">
        {(!reviews || reviews.length === 0) ? (
          <div className="text-center py-24 bg-slate-50 rounded-[3rem] border border-dashed border-slate-200">
            <div className="w-20 h-20 bg-white rounded-full flex items-center justify-center mx-auto mb-6 shadow-sm border border-slate-100">
                <MessageCircle className="w-8 h-8 text-slate-200" />
            </div>
            <h3 className="text-2xl font-black text-slate-400 italic">No reviews yet</h3>
            <p className="text-xs font-bold text-slate-300 uppercase tracking-widest mt-3">Be the first verified patient to share your experience</p>
          </div>
        ) : (
          <div className="space-y-12">
            {reviews.map((review, index) => (
              <motion.div 
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: index * 0.1 }}
                key={review.id} 
                className="group flex flex-col md:flex-row gap-10"
              >
                <div className="flex-shrink-0">
                  <div className="w-20 h-20 bg-white rounded-3xl border border-slate-100 shadow-xl flex items-center justify-center group-hover:bg-[#002d5a] group-hover:scale-110 transition-all duration-500 overflow-hidden relative">
                    <User className="w-10 h-10 text-slate-100 absolute -bottom-2 -right-2 opacity-50" />
                    <UserCheck className="w-8 h-8 text-[#0066cc] group-hover:text-white transition-colors" />
                  </div>
                </div>
                
                <div className="flex-grow space-y-10">
                  <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div className="space-y-1">
                      <h4 className="text-xl font-black text-[#002d5a] tracking-tight">{review.patientName}</h4>
                      <div className="flex items-center gap-4">
                        <div className="flex">
                           {[1, 2, 3, 4, 5].map((s) => (
                             <Star 
                               key={s} 
                               className={`w-3.5 h-3.5 ${s <= review.rating ? 'fill-amber-400 text-amber-400' : 'text-slate-100'}`} 
                             />
                           ))}
                        </div>
                        <div className="flex items-center gap-1.5 text-slate-400 text-[10px] font-bold uppercase">
                          <Calendar className="w-3.5 h-3.5" />
                          {new Date(review.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })}
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div className="relative">
                    <div className="absolute -left-6 top-0 w-1 h-full bg-blue-50 rounded-full group-hover:bg-[#0066cc] transition-all"></div>
                    <p className="text-slate-600 italic text-lg leading-relaxed font-medium">
                        "{review.comment}"
                    </p>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
      
      <div className="pt-10 border-t border-slate-50 flex flex-col md:flex-row md:items-center gap-4 text-slate-400">
        <div className="flex items-center gap-2">
            <UserCheck className="w-4 h-4 text-green-500" />
            <span className="text-[10px] font-black uppercase tracking-tighter">Enterprise Session Verification Active</span>
        </div>
        <div className="hidden md:block w-px h-3 bg-slate-200"></div>
        <span className="text-[10px] font-black uppercase tracking-tighter">Feedback cycle monitored by clinical administration</span>
      </div>
    </div>
  );
};

export default DoctorReviewsList;
