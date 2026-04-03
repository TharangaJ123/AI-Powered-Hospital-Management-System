import { useEffect, useState } from 'react'
import { CircleAlert, LoaderCircle, Save, UserRound } from 'lucide-react'

const EMPTY_PROFILE = {
  firstName: '',
  lastName: '',
  phoneNumber: '',
  address: '',
  dateOfBirth: '',
}

const PatientProfileCard = ({ profile, onSave, isSaving, error, sectionId }) => {
  const [formData, setFormData] = useState(EMPTY_PROFILE)

  useEffect(() => {
    setFormData({
      firstName: profile?.firstName || '',
      lastName: profile?.lastName || '',
      phoneNumber: profile?.phoneNumber || '',
      address: profile?.address || '',
      dateOfBirth: profile?.dateOfBirth || '',
    })
  }, [profile])

  const handleSubmit = async (event) => {
    event.preventDefault()
    await onSave({
      firstName: formData.firstName.trim(),
      lastName: formData.lastName.trim(),
      phoneNumber: formData.phoneNumber.trim(),
      address: formData.address.trim(),
      dateOfBirth: formData.dateOfBirth,
    })
  }

  return (
    <section id={sectionId} className="max-w-7xl mx-auto px-4 mt-8 mb-8 w-full">
      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm p-6">
        <div className="flex items-center gap-3 mb-5">
          <div className="w-10 h-10 rounded-xl bg-[#0066cc]/10 text-[#0066cc] flex items-center justify-center">
            <UserRound className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-extrabold text-slate-900">Patient Profile</h2>
            <p className="text-sm text-slate-500">Keep your profile details updated for better care coordination.</p>
          </div>
        </div>

        {error && (
          <div className="mb-4 flex items-start gap-2 rounded-xl border border-red-100 bg-red-50 px-3 py-2.5 text-sm text-red-700">
            <CircleAlert className="w-4 h-4 mt-0.5 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="grid md:grid-cols-2 gap-4">
          <div>
            <label className="auth-label" htmlFor="profile-first-name">First Name</label>
            <input
              id="profile-first-name"
              className="auth-input"
              value={formData.firstName}
              onChange={(event) => setFormData((prev) => ({ ...prev, firstName: event.target.value }))}
              required
            />
          </div>
          <div>
            <label className="auth-label" htmlFor="profile-last-name">Last Name</label>
            <input
              id="profile-last-name"
              className="auth-input"
              value={formData.lastName}
              onChange={(event) => setFormData((prev) => ({ ...prev, lastName: event.target.value }))}
            />
          </div>
          <div>
            <label className="auth-label" htmlFor="profile-phone">Phone Number</label>
            <input
              id="profile-phone"
              className="auth-input"
              value={formData.phoneNumber}
              onChange={(event) => setFormData((prev) => ({ ...prev, phoneNumber: event.target.value }))}
              placeholder="07xxxxxxxx"
            />
          </div>
          <div>
            <label className="auth-label" htmlFor="profile-dob">Date of Birth</label>
            <input
              id="profile-dob"
              type="date"
              className="auth-input"
              value={formData.dateOfBirth}
              onChange={(event) => setFormData((prev) => ({ ...prev, dateOfBirth: event.target.value }))}
            />
          </div>
          <div className="md:col-span-2">
            <label className="auth-label" htmlFor="profile-address">Address</label>
            <input
              id="profile-address"
              className="auth-input"
              value={formData.address}
              onChange={(event) => setFormData((prev) => ({ ...prev, address: event.target.value }))}
              placeholder="Street, City"
            />
          </div>
          <div className="md:col-span-2 pt-1">
            <button type="submit" className="btn-primary inline-flex items-center gap-2" disabled={isSaving}>
              {isSaving ? (
                <>
                  <LoaderCircle className="w-4 h-4 animate-spin" />
                  Saving...
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  Save Profile
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </section>
  )
}

export default PatientProfileCard
