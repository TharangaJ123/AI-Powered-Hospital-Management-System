const API_BASE = import.meta.env.VITE_API_URL || '/api'

export const createTelemedicineSession = async (appointmentId, token) => {
  const response = await fetch(`${API_BASE}/telemedicine/sessions/${appointmentId}`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to create telemedicine session')
  return response.json()
}

export const joinTelemedicineSession = async (appointmentId, token) => {
  const response = await fetch(`${API_BASE}/telemedicine/sessions/${appointmentId}/join`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to join telemedicine session. The doctor might not have started it yet.')
  return response.json()
}

export const completeTelemedicineSession = async (appointmentId, token) => {
  const response = await fetch(`${API_BASE}/telemedicine/sessions/${appointmentId}/complete`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to complete telemedicine session')
  return response.json()
}
