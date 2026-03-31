const API_BASE = import.meta.env.VITE_AUTH_API_URL || '/api'

const SESSION_STORAGE_KEY = 'omnihealth_auth'

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
  const user = payload?.user || payload?.data || {
    name: payload?.name,
    email: payload?.email,
    role: payload?.role,
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

export const registerUser = async ({ name, email, password }) => {
  const [firstName, ...rest] = name.trim().split(/\s+/)
  const lastName = rest.join(' ')

  await postAuth('/patients/register', {
    email,
    password,
    firstName,
    lastName,
  })

  return loginUser({ email, password })
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
