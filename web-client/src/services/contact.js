const API_BASE = '/api/contact'

export const submitContactForm = async (contactData) => {
  const response = await fetch(`${API_BASE}/submit`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(contactData)
  })
  if (!response.ok) throw new Error('Failed to submit contact message')
  return response.json()
}

export const getAllContactForms = async (token) => {
  const response = await fetch(`${API_BASE}/all`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  if (!response.ok) throw new Error('Failed to fetch contact forms')
  return response.json()
}

export const getContactFormsByUser = async (patientId, token) => {
  const response = await fetch(`${API_BASE}/user/${patientId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  if (!response.ok) throw new Error('Failed to fetch user contact forms')
  return response.json()
}

export const replyToContactForm = async (id, adminReply, token) => {
  const response = await fetch(`${API_BASE}/${id}/reply`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ adminReply })
  })
  if (!response.ok) throw new Error('Failed to reply to contact form')
  return response.json()
}
