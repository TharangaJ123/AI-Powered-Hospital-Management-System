const API_GW_URL = (import.meta.env.VITE_API_URL || '/api') + '/reviews';

export const createReview = async (reviewData, token) => {
    const res = await fetch(`${API_GW_URL}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(reviewData)
    });
    if (!res.ok) throw new Error('Failed to submit review');
    return res.json();
};

export const getDoctorStats = async (doctorId) => {
    const res = await fetch(`${API_GW_URL}/doctor/${doctorId}/stats`);
    if (!res.ok) return { averageRating: 0.0, totalReviews: 0 };
    return res.json();
};

export const getDoctorReviews = async (doctorId) => {
    const res = await fetch(`${API_GW_URL}/doctor/${doctorId}`);
    if (!res.ok) return [];
    return res.json();
};

export const getPatientReviews = async (patientId, token) => {
    const res = await fetch(`${API_GW_URL}/patient/${patientId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    if (!res.ok) return [];
    return res.json();
};
