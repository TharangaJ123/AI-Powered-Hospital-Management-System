const API_BASE = import.meta.env.VITE_API_URL || '/api'

/**
 * Get all doctor profiles
 */
export const getAllDoctors = async () => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles`)
    if (!response.ok) {
      throw new Error('Failed to fetch doctors')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in getAllDoctors:', error)
    throw error
  }
}

/**
 * Get doctor profile by ID
 */
export const getDoctorById = async (id) => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles/${id}`)
    if (!response.ok) {
      throw new Error('Doctor profile not found')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in getDoctorById:', error)
    throw error
  }
}

/**
 * Get doctor profile by User ID
 */
export const getDoctorByUserId = async (userId) => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles/user/${userId}`)
    if (!response.ok) {
      // It's possible the doctor record doesn't exist yet
      if (response.status === 404) return null
      throw new Error('Error fetching doctor profile')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in getDoctorByUserId:', error)
    throw error
  }
}

/**
 * Create a new doctor profile
 */
export const createDoctorProfile = async (doctorData, token) => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(doctorData),
    })
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to create doctor profile')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in createDoctorProfile:', error)
    throw error
  }
}

/**
 * Update an existing doctor profile
 */
export const updateDoctorProfile = async (id, doctorData, token) => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(doctorData),
    })
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to update doctor profile')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in updateDoctorProfile:', error)
    throw error
  }
}

/**
 * Get doctors by specialization
 */
export const getDoctorsBySpecialization = async (specialization) => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles/specialization/${specialization}`)
    if (!response.ok) {
      throw new Error('Failed to fetch doctors by specialization')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in getDoctorsBySpecialization:', error)
    throw error
  }
}

/**
 * Get telemedicine-enabled doctors
 */
export const getTelemedicineDoctors = async () => {
  try {
    const response = await fetch(`${API_BASE}/doctors/profiles/telemedicine`)
    if (!response.ok) {
      throw new Error('Failed to fetch telemedicine doctors')
    }
    return await response.json()
  } catch (error) {
    console.error('Error in getTelemedicineDoctors:', error)
    throw error
  }
}
