const API_BASE = (import.meta.env.VITE_AUTH_API_URL || '/api').replace('/auth', '') + '/doctors'

export const getAllDoctors = async () => {
  const response = await fetch(`${API_BASE}/profiles`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to fetch doctor profiles.')
  }

  return response.json()
}

export const getDoctorsBySpecialization = async (specialization) => {
  const response = await fetch(`${API_BASE}/profiles/specialization/${specialization}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to fetch doctors for specialization.')
  }

  return response.json()
}

export const getDoctorById = async (id) => {
  const response = await fetch(`${API_BASE}/profiles/${id}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to fetch doctor profile.')
  }

  return response.json()
}

export const getTelemedicineDoctors = async () => {
  const response = await fetch(`${API_BASE}/profiles/telemedicine`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to fetch telemedicine doctors.')
  }

  return response.json()
}

export const getDoctorByUserId = async (userId) => {
  const response = await fetch(`${API_BASE}/profiles/user/${userId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to fetch doctor profile by user ID.')
  }

  return response.json()
}

export const createDoctorProfile = async (profileData, token) => {
  const response = await fetch(`${API_BASE}/profiles`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(profileData),
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to create doctor profile.')
  }

  return response.json()
}

export const updateDoctorProfile = async (id, profileData, token) => {
  const response = await fetch(`${API_BASE}/profiles/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(profileData),
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to update doctor profile.')
  }

  return response.json()
}

export const getDoctorLeaves = async (doctorId, token) => {
  const LEAVE_API = (import.meta.env.VITE_AUTH_API_URL || '/api').replace('/auth', '') + '/doctor-leaves'
  const response = await fetch(`${LEAVE_API}/doctor/${doctorId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to fetch doctor leaves.')
  }

  return response.json()
}
