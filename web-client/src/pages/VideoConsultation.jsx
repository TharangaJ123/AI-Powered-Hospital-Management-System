import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getTelemedicineSession, completeTelemedicineSession } from '../services/telemedicine';
import { Loader2, PhoneOff, Maximize, Mic, Video, MessageSquare, Monitor, XCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const VideoConsultation = ({ user, token }) => {
  const { appointmentId } = useParams();
  const navigate = useNavigate();
  const [session, setSession] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const jitsiContainerRef = useRef(null);
  const [api, setApi] = useState(null);

  useEffect(() => {
    if (!token || !user) {
      navigate('/');
      return;
    }

    const loadSession = async () => {
      try {
        const data = await getTelemedicineSession(appointmentId, token);
        if (!data) {
          setError('Video session not found or not yet started by the doctor.');
        } else {
          setSession(data);
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadSession();
  }, [appointmentId, token, user, navigate]);

  useEffect(() => {
    if (session && !api && jitsiContainerRef.current) {
      // Load Jitsi script if not already present
      const script = document.createElement('script');
      script.src = 'https://meet.jit.si/external_api.js';
      script.async = true;
      script.onload = () => initJitsi();
      document.body.appendChild(script);

      return () => {
        if (api) {
          api.dispose();
        }
      };
    }
  }, [session, api]);

  const initJitsi = () => {
    const domain = 'meet.jit.si';
    const options = {
      roomName: session.roomName,
      width: '100%',
      height: '100%',
      parentNode: jitsiContainerRef.current,
      configOverwrite: {
        prejoinPageEnabled: false,
        disableDeepLinking: true,
      },
      interfaceConfigOverwrite: {
        TOOLBAR_BUTTONS: [
          'microphone', 'camera', 'chat', 'tileview', 'fullscreen',
          'hangup', 'videoquality', 'filmstrip', 'shortcuts',
          'tileview', 'videobackgroundblur', 'help', 'mute-everyone',
        ],
      },
      userInfo: {
        displayName: `${user.role}: ${user.email.split('@')[0]}`,
        email: user.email,
      },
    };

    const jitsiApi = new window.JitsiMeetExternalAPI(domain, options);
    
    jitsiApi.addEventListener('videoConferenceLeft', () => {
      handleHangup();
    });

    setApi(jitsiApi);
  };

  const handleHangup = async () => {
    if (user.role === 'DOCTOR') {
      try {
        await completeTelemedicineSession(appointmentId, token);
      } catch (err) {
        console.error('Failed to complete session on server:', err);
      }
    }
    navigate('/profile');
  };

  if (loading) {
    return (
      <div className="flex-grow flex flex-col items-center justify-center bg-[#001d3d] text-white">
        <Loader2 className="w-16 h-16 animate-spin text-blue-400 mb-6" />
        <h2 className="text-2xl font-black tracking-widest uppercase">Connecting to Secure Session</h2>
        <p className="mt-4 text-blue-200/60 font-medium">Encrypting tunnel with OmniHealth Hospital...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-grow flex flex-col items-center justify-center bg-slate-50 p-6">
        <motion.div 
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="max-w-md w-full bg-white rounded-[3rem] p-12 shadow-2xl shadow-red-500/10 border border-red-50 border-t-4 border-t-red-500 text-center"
        >
          <div className="w-20 h-20 bg-red-50 rounded-full flex items-center justify-center mx-auto mb-8 text-red-500">
            <XCircle className="w-10 h-10" />
          </div>
          <h2 className="text-3xl font-black text-[#002d5a] mb-4">Consultation Error</h2>
          <p className="text-slate-500 font-medium mb-8 leading-relaxed">{error}</p>
          <button 
            onClick={() => navigate('/profile')}
            className="w-full py-4 bg-[#002d5a] text-white rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-[#003d7a] transition-all"
          >
            Return to Dashboard
          </button>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="flex-grow flex flex-col bg-[#000b1a] overflow-hidden relative">
      {/* Premium Header */}
      <div className="absolute top-0 left-0 right-0 z-50 p-6 pointer-events-none">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-4 pointer-events-auto">
             <div className="w-12 h-12 bg-white/10 backdrop-blur-xl rounded-2xl border border-white/20 flex items-center justify-center text-blue-400">
                <Video className="w-6 h-6" />
             </div>
             <div>
                <h3 className="text-white font-black text-sm uppercase tracking-widest">In-Progress Consultation</h3>
                <div className="flex items-center gap-2">
                   <div className="w-2 h-2 rounded-full bg-red-500 animate-pulse" />
                   <p className="text-white/40 text-[10px] font-black uppercase tracking-tighter">Secure High-Resolution Stream</p>
                </div>
             </div>
          </div>
          
          <button 
            onClick={handleHangup}
            className="pointer-events-auto px-6 py-3 bg-red-500/10 hover:bg-red-500 backdrop-blur-xl border border-red-500/20 text-red-500 hover:text-white rounded-2xl flex items-center gap-2 transition-all duration-500 group shadow-lg shadow-red-500/20"
          >
            <PhoneOff className="w-4 h-4 group-hover:scale-125 transition-transform" />
            <span className="text-[10px] font-black uppercase tracking-widest">End Session</span>
          </button>
        </div>
      </div>

      {/* Jitsi Iframe Container */}
      <div className="flex-grow w-full h-full" ref={jitsiContainerRef} />

      {/* Footer Info Overlay */}
      <div className="absolute bottom-6 left-1/2 -translate-x-1/2 z-50 pointer-events-none opacity-0 hover:opacity-100 transition-opacity">
         <div className="px-6 py-3 bg-white/5 backdrop-blur-xl border border-white/10 rounded-full flex items-center gap-6">
            {[Mic, Video, Monitor, MessageSquare, Maximize].map((Icon, i) => (
              <div key={i} className="text-white/40 hover:text-white transition-colors">
                 <Icon className="w-4 h-4" />
              </div>
            ))}
         </div>
      </div>
    </div>
  );
};

export default VideoConsultation;
