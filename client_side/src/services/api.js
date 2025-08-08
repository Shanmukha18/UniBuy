import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken: refreshToken
        });
        
        const { token } = response.data;
        localStorage.setItem('accessToken', token);
        
        originalRequest.headers.Authorization = `Bearer ${token}`;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
};

// Products API
export const productsAPI = {
  getAll: () => api.get('/products'),
  getById: (id) => api.get(`/products/${id}`),
  create: (product) => api.post('/products', product),
  update: (id, product) => api.put(`/products/${id}`, product),
  delete: (id) => api.delete(`/products/${id}`),
};

// Cart API
export const cartAPI = {
  getCart: (userId) => api.get(`/cart/${userId}`),
  debugCart: (userId) => api.get(`/cart/debug/${userId}`),
  addToCart: (userId, productId, quantity) => 
    api.post(`/cart/${userId}/add/${productId}?quantity=${quantity}`),
  updateItem: (userId, productId, quantity) => 
    api.put(`/cart/${userId}/update/${productId}?quantity=${quantity}`),
  removeItem: (userId, productId) => 
    api.delete(`/cart/${userId}/remove/${productId}`),
  clearCart: (userId) => api.delete(`/cart/${userId}/clear`),
};

// Orders API
export const ordersAPI = {
  getAll: () => api.get('/orders'),
  getById: (id) => api.get(`/orders/${id}`),
  getByUser: (userId) => api.get(`/orders/user/${userId}`),
  debugOrder: (userId) => api.get(`/orders/debug/${userId}`),
  placeOrder: (orderData) => api.post('/orders', orderData),
  checkout: (userId) => api.post(`/orders/checkout/${userId}`),
  updateStatus: (id, status) => api.put(`/orders/${id}/status`, { status }),
  updatePaymentStatus: (id, paymentId, paymentStatus) => 
    api.put(`/orders/${id}/payment?paymentId=${paymentId}&paymentStatus=${paymentStatus}`),
};

// Payment API
export const paymentAPI = {
  createOrder: (paymentRequest) => api.post('/payments/create-order', paymentRequest),
  verifyPayment: (verificationRequest) => api.post('/payments/verify', verificationRequest),
};

// Users API
export const usersAPI = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  update: (id, userData) => api.put(`/users/${id}`, userData),
};

export default api;
