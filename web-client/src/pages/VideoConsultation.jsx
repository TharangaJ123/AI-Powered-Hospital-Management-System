import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getTelemedicineSession, completeTelemedicineSession } from '../services/telemedicine';
import { completeAppointment } from '../services/appointments';
import { Loader2, PhoneOff, Video, MessageSquare, Monitor, XCircle, ShieldCheck, User } from 'lucide-react';
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
      const loadJitsiScript = () => {
        return new Promise((resolve) => {
          if (window.JitsiMeetExternalAPI) {
            resolve();
            return;
          }
          const script = document.createElement('script');
          script.src = 'https://meet.jit.si/external_api.js';
          script.async = true;
          script.onload = () => resolve();
          document.body.appendChild(script);
        });
      };

      loadJitsiScript().then(() => initJitsi());

      return () => {
        if (api) {
          api.dispose();
        }
      };
    }
  }, [session, api]);

  const initJitsi = () => {
    if (!window.JitsiMeetExternalAPI) return;

    const domain = 'meet.jit.si';
    const options = {
      roomName: session.roomName,
      width: '100%',
      height: '100%',
      parentNode: jitsiContainerRef.current,
      configOverwrite: {
        prejoinPageEnabled: false,
        disableDeepLinking: true,
        startWithAudioMuted: false,
        startWithVideoMuted: false,
      },
      interfaceConfigOverwrite: {
        TOOLBAR_BUTTONS: [
          'microphone', 'camera', 'chat', 'tileview', 'fullscreen',
          'hangup', 'videoquality', 'filmstrip', 'shortcuts',
          'videobackgroundblur', 'help', 'mute-everyone',
        ],
        SETTINGS_SECTIONS: ['devices', 'language', 'moderator', 'profile'],
      },
      userInfo: {
        displayName: `${user.role === 'DOCTOR' ? 'Dr.' : ''} ${user.email.split('@')[0]}`,
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
    if (api) {
      api.dispose();
    }
    if (user.role === 'DOCTOR') {
      try {
        await completeTelemedicineSession(appointmentId, token);
        await completeAppointment(appointmentId, token);
      } catch (err) {
        console.error('Failed to complete session on server:', err);
      }
    }
    navigate('/profile');
  };

  if (loading) {
    return (
      <div className="flex-grow flex flex-col items-center justify-center bg-[#000b1a] text-white">
        <motion.div
           initial={{ opacity: 0, scale: 0.8 }}
           animate={{ opacity: 1, scale: 1 }}
           className="relative"
        >
          <div className="absolute inset-0 bg-blue-500/20 blur-3xl rounded-full" />
          <Loader2 className="w-20 h-20 animate-spin text-blue-400 relative z-10" />
        </motion.div>
        <h2 className="text-2xl font-black mt-8 tracking-widest uppercase bg-gradient-to-r from-blue-400 to-teal-400 bg-clip-text text-transparent">Initializing Secure Channel</h2>
        <p className="mt-4 text-blue-200/40 font-bold uppercase text-[10px] tracking-[0.3em]">End-to-End Encrypted Tunnel Active</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-grow flex flex-col items-center justify-center bg-[#000b1a] p-6 relative overflow-hidden">
        {/* Animated Background Orbs */}
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-600/10 blur-[120px] rounded-full animate-pulse" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-red-600/5 blur-[120px] rounded-full animate-pulse" style={{ animationDelay: '1s' }} />

        <motion.div 
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="max-w-md w-full bg-white/[0.03] backdrop-blur-3xl rounded-[3.5rem] p-12 lg:p-16 border border-white/10 text-center relative z-10 shadow-2xl"
        >
          <div className="w-24 h-24 bg-gradient-to-br from-red-500/20 to-red-600/5 rounded-[2rem] flex items-center justify-center mx-auto mb-10 text-red-400 border border-red-500/20 shadow-lg shadow-red-500/10">
            <XCircle className="w-12 h-12" />
          </div>
          
          <h2 className="text-3xl lg:text-4xl font-black text-white mb-6 tracking-tight">Access Restricted</h2>
          
          <div className="bg-white/5 rounded-2xl p-6 mb-10 border border-white/5">
            <p className="text-blue-100/60 font-medium leading-relaxed italic text-sm">
              "{error}"
            </p>
          </div>

          <div className="space-y-4">
             <button 
              onClick={() => window.location.reload()}
              className="w-full py-5 bg-white text-[#000b1a] rounded-2xl font-black text-xs uppercase tracking-widest hover:bg-blue-50 transition-all shadow-xl active:scale-95"
            >
              Retry Connection
            </button>
            <button 
              onClick={() => navigate('/profile')}
              className="w-full py-5 bg-white/5 text-white/60 hover:text-white rounded-2xl font-black text-xs uppercase tracking-widest border border-white/10 hover:bg-white/10 transition-all active:scale-95"
            >
              Return to Dashboard
            </button>
          </div>

          <div className="mt-12 flex items-center justify-center gap-2">
            <ShieldCheck className="w-4 h-4 text-blue-500/40" />
            <span className="text-[10px] font-black text-blue-500/40 uppercase tracking-[0.2em]">Secure Gateway Protocol</span>
          </div>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-[9999] flex flex-col bg-[#000b1a] overflow-hidden">
      {/* Dynamic Background Elements */}
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-600/10 blur-[120px] rounded-full animate-pulse" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-teal-600/10 blur-[120px] rounded-full animate-pulse" style={{ animationDelay: '2s' }} />
      </div>

      {/* Premium Header */}
      <div className="absolute top-0 left-0 right-0 z-[100] p-6 lg:p-8 pointer-events-none">
        <div className="max-w-full mx-auto flex items-center justify-between">
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="flex items-center gap-6 pointer-events-auto bg-[#001d3d]/40 backdrop-blur-2xl px-6 py-4 rounded-[2rem] border border-white/10"
          >
             <div className="w-14 h-14 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center text-white shadow-lg shadow-blue-500/30">
                <Video className="w-7 h-7" />
             </div>
             <div>
                <div className="flex items-center gap-3">
                  <h3 className="text-white font-black text-sm uppercase tracking-widest">Medical Consultation</h3>
                  <div className="flex items-center gap-1.5 px-2 py-0.5 bg-blue-500/20 border border-blue-500/30 rounded-full">
                    <ShieldCheck className="w-3 h-3 text-blue-400" />
                    <span className="text-[9px] font-black text-blue-400 uppercase">Secure</span>
                  </div>
                </div>
                <div className="flex items-center gap-2 mt-1">
                   <div className="w-2 h-2 rounded-full bg-red-500 animate-pulse" />
                   <p className="text-white/40 text-[10px] font-bold uppercase tracking-widest">Live Stream • {user.role}</p>
                </div>
             </div>
          </motion.div>
          
          <motion.button 
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            onClick={handleHangup}
            className="pointer-events-auto px-8 py-5 bg-red-500/10 hover:bg-red-500 backdrop-blur-3xl border border-red-500/30 text-red-500 hover:text-white rounded-[1.8rem] flex items-center gap-3 transition-all duration-500 group shadow-2xl shadow-red-500/20 active:scale-95"
          >
            <PhoneOff className="w-5 h-5 group-hover:rotate-[135deg] transition-transform duration-500" />
            <span className="text-[10px] font-black uppercase tracking-[0.2em]">Terminate Session</span>
          </motion.button>
        </div>
      </div>

      {/* Jitsi Iframe Container */}
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.5 }}
        className="flex-grow w-full h-full relative z-10" 
        ref={jitsiContainerRef} 
      />

      {/* Floating User Info Overlay (Bottom Left) */}
      <div className="absolute bottom-10 left-10 z-[100] pointer-events-none hidden lg:block">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1 }}
          className="flex items-center gap-4 bg-white/5 backdrop-blur-xl border border-white/10 px-6 py-4 rounded-[2rem]"
        >
          <div className="w-10 h-10 rounded-full bg-white/10 flex items-center justify-center">
            <User className="w-5 h-5 text-white/60" />
          </div>
          <div>
            <p className="text-white/40 text-[9px] font-black uppercase tracking-widest">Connected as</p>
            <p className="text-white text-xs font-black">{user.email}</p>
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default VideoConsultation;

