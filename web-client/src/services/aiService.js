const API_BASE = import.meta.env.VITE_API_URL || '/api'

export const checkSymptoms = async (symptoms, additionalInfo = '') => {
  try {
    const response = await fetch(`${API_BASE}/ai/check-symptoms`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ symptoms, additionalInfo }),
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || 'AI service is currently unavailable.')
    }

    return await response.json()
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error('Unable to reach the AI service. Please ensure the backend is running.')
    }
    throw error
  }
}
