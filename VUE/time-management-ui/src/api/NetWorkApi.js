import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // 后端地址
    headers: {
        'Content-Type': 'application/json'
    }
});

export default {
    getUsers() {
        return apiClient.get('/users');
    },
    getUser(id) {
        return apiClient.get(`/users/${id}`);
    },
    registerUser(user) {
        return apiClient.post('/users/register', user);
    }
};
