import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '/api';
const API_GATEWAY_URL = `${API_BASE}/telemedicine/sessions`;

const getAuthHeader = (token) => ({
  headers: {
    Authorization: `Bearer ${token}`,
  },
});

export const createTelemedicineSession = async (appointmentId, token) => {
  try {
    const response = await axios.post(`${API_GATEWAY_URL}/${appointmentId}`, {}, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to create video session');
  }
};

export const getTelemedicineSession = async (appointmentId, token) => {
  try {
    const response = await axios.get(`${API_GATEWAY_URL}/${appointmentId}/join`, getAuthHeader(token));
    return response.data;
  } catch (error) {
    if (error.response?.status === 404) {
      return null;
    }
    throw new Error(error.response?.data?.message || 'Failed to fetch video session');
  }
};

export const completeTelemedicineSession = async (appointmentId, token) => {
  try {
    const response = await axios.put(`${API_GATEWAY_URL}/${appointmentId}/complete`, {}, getAuthHeader(token));
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Failed to complete video session');
  }
};
