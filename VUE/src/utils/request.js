import axios from 'axios';
import { ElMessage } from 'element-plus';
import { getToken } from './auth';
import router from '../router';

// Create axios instance with base configuration
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor - attach authorization token
request.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor - handle responses and errors
request.interceptors.response.use(
  (response) => {
    const { code, msg, data } = response.data;
    
    // Check if response follows ApiResponse format
    if (code !== undefined) {
      if (code === 200) {
        // Success - return data directly
        return data;
      } else {
        // Business error - provide user-friendly message
        const errorMsg = msg || 'Request failed';
        ElMessage.error({
          message: errorMsg,
          duration: 3000,
          showClose: true
        });
        return Promise.reject(new Error(errorMsg));
      }
    }
    
    // If not ApiResponse format, return raw data
    return response.data;
  },
  (error) => {
    // Handle HTTP errors with user-friendly messages
    let errorMessage = 'An error occurred';
    
    if (error.response) {
      const status = error.response.status;
      const serverMessage = error.response.data?.msg || error.response.data?.message;
      
      switch (status) {
        case 400:
          errorMessage = serverMessage || 'Invalid request. Please check your input.';
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
          break;
        case 401:
          errorMessage = serverMessage || 'Your session has expired. Please login again.';
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
          // Clear auth data and redirect to login
          localStorage.removeItem('auth_token');
          localStorage.removeItem('user_info');
          router.push('/login');
          break;
        case 403:
          errorMessage = serverMessage || 'You do not have permission to access this resource.';
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
          // Optionally redirect to unauthorized page
          router.push('/unauthorized');
          break;
        case 404:
          errorMessage = serverMessage || 'The requested resource was not found.';
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
          break;
        case 409:
          errorMessage = serverMessage || 'A conflict occurred. The resource may already exist.';
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
          break;
        case 422:
          errorMessage = serverMessage || 'Validation failed. Please check your input.';
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
          break;
        case 500:
          errorMessage = serverMessage || 'Server error. Please try again later.';
          ElMessage.error({
            message: errorMessage,
            duration: 4000,
            showClose: true
          });
          break;
        case 502:
        case 503:
        case 504:
          errorMessage = 'Service temporarily unavailable. Please try again later.';
          ElMessage.error({
            message: errorMessage,
            duration: 4000,
            showClose: true
          });
          break;
        default:
          errorMessage = serverMessage || `Request failed with status ${status}`;
          ElMessage.error({
            message: errorMessage,
            duration: 3000,
            showClose: true
          });
      }
    } else if (error.request) {
      // Request was made but no response received
      errorMessage = 'Network error. Please check your internet connection and try again.';
      ElMessage.error({
        message: errorMessage,
        duration: 4000,
        showClose: true
      });
    } else {
      // Something else happened
      errorMessage = error.message || 'An unexpected error occurred';
      ElMessage.error({
        message: errorMessage,
        duration: 3000,
        showClose: true
      });
    }
    
    // Log error in development mode
    if (import.meta.env.DEV) {
      console.error('API Error:', error);
    }
    
    return Promise.reject(error);
  }
);

export default request;
