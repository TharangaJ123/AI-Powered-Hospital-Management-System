const API_BASE = import.meta.env.VITE_AUTH_API_URL || '/api'

const SESSION_STORAGE_KEY = 'omnihealth_auth'

const normalizeRole = (role) => {
  if (!role || typeof role !== 'string') {
    return 'PATIENT'
  }

  return role.replace('ROLE_', '').toUpperCase()
}

const normalizeAuthResponse = async (response) => {
  let payload = null

  try {
    payload = await response.json()
  } catch {
    payload = null
  }

  if (!response.ok) {
    const message = payload?.message || payload?.error || 'Authentication failed. Please try again.'
    throw new Error(message)
  }

  const token = payload?.token || payload?.accessToken || payload?.jwt || null
  const sourceUser = payload?.user || payload?.data || payload
  const user = {
    id: sourceUser?.id,
    name: sourceUser?.name,
    email: sourceUser?.email,
    role: normalizeRole(sourceUser?.role),
  }

  return {
    token,
    user,
    raw: payload,
  }
}

const postAuth = async (path, body) => {
  try {
    const response = await fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    })

    return normalizeAuthResponse(response)
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error('Unable to reach the server. Please ensure backend services are running and try again.')
    }

    throw error
  }
}

export const loginUser = async ({ email, password }) => {
  return postAuth('/auth/login', { email, password })
}

export const registerUser = async ({ role, name, email, password, phoneNumber, address, dateOfBirth, specialization, doctorRegistrationNumber }) => {
  const [firstName, ...rest] = name.trim().split(/\s+/)
  const lastName = rest.join(' ')

  await postAuth('/auth/register', {
    email,
    password,
    role,
    firstName,
    lastName,
    phoneNumber,
    address,
    dateOfBirth,
    specialization,
    doctorRegistrationNumber,
  })

  return loginUser({ email, password })
}

export const getPatientProfile = async ({ userId, token }) => {
  const response = await fetch(`${API_BASE}/patients/${userId}/profile`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    const payload = await response.json().catch(() => null)
    const message = payload?.message || 'Unable to load patient profile.'
    throw new Error(message)
  }

  return response.json()
}

export const updatePatientProfile = async ({ userId, token, profile }) => {
  const response = await fetch(`${API_BASE}/patients/${userId}/profile`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(profile),
  })

  if (!response.ok) {
    const payload = await response.json().catch(() => null)
    const message = payload?.message || 'Unable to update patient profile.'
    throw new Error(message)
  }

  return response.json()
}

export const persistSession = (session) => {
  localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session))
}

export const readSession = () => {
  try {
    const serialized = localStorage.getItem(SESSION_STORAGE_KEY)

    if (!serialized) {
      return null
    }

    return JSON.parse(serialized)
  } catch {
    return null
  }
}

export const clearSession = () => {
  localStorage.removeItem(SESSION_STORAGE_KEY)
}

export const getAllUsers = async (token) => {
  try {
    const response = await fetch(`${API_BASE}/admin/users`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'Failed to fetch users')
    }

    return await response.json()
  } catch (error) {
    throw error
  }
}

export const verifyDoctor = async (id, token) => {
  const response = await fetch(`${API_BASE}/admin/doctors/${id}/verify`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to verify doctor')
  return response.json()
}

export const deleteUser = async (id, token) => {
  const response = await fetch(`${API_BASE}/admin/users/${id}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to delete user')
}

export const getPlatformOperations = async (token) => {
  const response = await fetch(`${API_BASE}/admin/operations`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) throw new Error('Failed to fetch operations')
  return response.json()
}
