import axios from 'axios';

const API_URL = 'http://localhost:8082/api/reviews'; // doctor-management service

export const submitReview = async (reviewData, token) => {
    try {
        const response = await axios.post(API_URL, reviewData, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const getDoctorReviews = async (doctorId) => {
    try {
        const response = await axios.get(`${API_URL}/doctor/${doctorId}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const getDoctorAverageRating = async (doctorId) => {
    try {
        const response = await axios.get(`${API_URL}/doctor/${doctorId}/average`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const deleteReview = async (reviewId, token) => {
    try {
        const response = await axios.delete(`${API_URL}/${reviewId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};
