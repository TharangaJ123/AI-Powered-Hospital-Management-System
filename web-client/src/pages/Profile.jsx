import PatientProfileCard from '../components/PatientProfileCard'
import DoctorProfileCard from '../components/DoctorProfileCard'

const Profile = ({
  user,
  patientProfile,
  profileError,
  isSavingProfile,
  onSavePatientProfile,
  onLoginClick,
  onSignupClick,
  doctorProfile,
  onSaveDoctorProfile,
}) => {
  if (!user) {
    return (
      <main className="w-full pt-32 pb-20 px-4">
        <section className="max-w-3xl mx-auto rounded-2xl border border-slate-200 bg-white shadow-sm p-8 text-center">
          <h1 className="text-2xl md:text-3xl font-extrabold text-slate-900">My Profile</h1>
          <p className="mt-3 text-slate-600">Please sign in to view and manage your profile.</p>
          <div className="mt-6 flex items-center justify-center gap-3">
            <button
              type="button"
              onClick={onLoginClick}
              className="px-5 py-2.5 text-sm font-semibold rounded-full border border-slate-200 text-slate-700 hover:text-[#0066cc] hover:border-[#0066cc]/40 transition-colors"
            >
              Login
            </button>
            <button
              type="button"
              onClick={onSignupClick}
              className="btn-secondary text-sm px-5 py-2.5"
            >
              Sign Up
            </button>
          </div>
        </section>
      </main>
    )
  }

  return (
    <main className="w-full pt-32 pb-12">
      <section className="max-w-7xl mx-auto px-4 mb-8 w-full">
        <div className="rounded-2xl border border-slate-200 bg-white shadow-sm p-6">
          <h1 className="text-lg font-extrabold text-slate-900">My Profile</h1>
          <p className="text-sm text-slate-500 mt-1">View your account details and manage your profile information.</p>
          <div className="mt-4 grid md:grid-cols-3 gap-4 text-sm">
            <div className="rounded-xl bg-slate-50 border border-slate-200 px-4 py-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Name</p>
              <p className="mt-1 font-semibold text-slate-900">
                {user?.name || user?.fullName || 'Not provided'}
              </p>
            </div>
            <div className="rounded-xl bg-slate-50 border border-slate-200 px-4 py-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Email</p>
              <p className="mt-1 font-semibold text-slate-900 break-all">{user?.email || 'Not provided'}</p>
            </div>
            <div className="rounded-xl bg-slate-50 border border-slate-200 px-4 py-3">
              <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Role</p>
              <p className="mt-1 font-semibold text-slate-900">{user?.role || 'PATIENT'}</p>
            </div>
          </div>
        </div>
      </section>

      {user?.role === 'PATIENT' && (
        <PatientProfileCard
          profile={patientProfile}
          onSave={onSavePatientProfile}
          isSaving={isSavingProfile}
          error={profileError}
          sectionId="patient-profile-form"
        />
      )}
      {user?.role === 'DOCTOR' && (
        <DoctorProfileCard
          profile={doctorProfile}
          onSave={onSaveDoctorProfile}
          isSaving={isSavingProfile}
          error={profileError}
        />
      )}
    </main>
  )
}

export default Profile
