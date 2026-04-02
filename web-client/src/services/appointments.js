const API_BASE = import.meta.env.VITE_API_URL || '/api'

export const createAppointment = async (appointmentData) => {
  try {
    const response = await fetch(`${API_BASE}/appointments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(appointmentData),
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to book appointment')
    }

    return await response.json()
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error('Unable to reach the server. Please ensure backend services are running.')
    }
    throw error
  }
}

export const getAppointmentsByPatient = async (patientId, token) => {
  try {
    const response = await fetch(`${API_BASE}/appointments/patient/${patientId}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to fetch appointments')
    }

    return await response.json()
  } catch (error) {
    throw error
  }
}

export const getAllAppointments = async (token) => {
  try {
    const response = await fetch(`${API_BASE}/appointments`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to fetch all appointments')
    }

    return await response.json()
  } catch (error) {
    throw error
  }
}

export const cancelAppointment = async (id, token) => {
  const response = await fetch(`${API_BASE}/appointments/${id}/cancel`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to cancel appointment')
  return response.json()
}

export const completeAppointment = async (id, token) => {
  const response = await fetch(`${API_BASE}/appointments/${id}/complete`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to complete appointment')
  return response.json()
}

export const deleteAppointment = async (id, token) => {
  const response = await fetch(`${API_BASE}/appointments/${id}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to delete appointment')
}
export const updateAppointment = async (id, appointmentData, token) => {
  const response = await fetch(`${API_BASE}/appointments/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(appointmentData)
  })
  if (!response.ok) throw new Error('Failed to update appointment')
  return response.json()
}
