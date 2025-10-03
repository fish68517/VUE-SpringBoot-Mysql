import axios from 'axios';

// 从您的 SpringBoot BaseController 继承的类的 @RequestMapping 推断出基础 URL
// 例如 CampusUserController 的 @RequestMapping 是 "/campusUser"
// 您需要根据后端实际情况调整这里的 baseURL
const apiClient = axios.create({
    baseURL: 'http://localhost:8081', // SpringBoot 后端地址 (端口号以 application.properties 为准)
    headers: {
        'Content-Type': 'application/json'
    }
});

/**
 * 封装用户相关的 API 请求
 */
export default {
    /**
     * 用户登录
     * @param {object} userData - 包含 email 和 password 的对象
     */
    login(userData) {
        // 假设登录接口在后端的 /login 路径下
        return apiClient.post('/login', userData);
    },

    /**
     * 用户注册
     * @param {object} userData - 包含 campusEmailAddr, password, campusUserType 等的用户信息对象
     */
    register(userData) {
        // 根据您的 BaseController，保存操作通常是直接 POST 到根路径
        return apiClient.post('/', userData);
    },

    /**
     * 根据 ID 获取用户信息
     * @param {number} id - 用户ID
     */
    getUserById(id) {
        return apiClient.get(`/${id}`);
    },

    /**
     * 获取所有用户列表
     */
    getAllUsers() {
        return apiClient.get('/list');
    }
};
