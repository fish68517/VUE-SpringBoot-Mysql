import axios from 'axios';

// apiClient 实例负责与后端的所有HTTP通信
const apiClient = axios.create({
    // 后端服务的基础URL，请根据您的Spring Boot项目配置进行修改
    baseURL: 'http://localhost:8080', // 默认的Spring Boot端口
    headers: {
        'Content-Type': 'application/json'
    }
});

/**
 * 封装所有后端 API 请求
 * 每个对象（如 usersApi）对应后端一个 Controller
 */
const api = {

    //=========================== 1. 部门 (Departments) ===========================
    departmentsApi: {
        create(data) {
            return apiClient.post('/departments', data);
        },
        getById(id) {
            return apiClient.get(`/departments/${id}`);
        },
        list() {
            return apiClient.get('/departments/list');
        },
        update(data) {
            return apiClient.put('/departments', data);
        },
        delete(id) {
            return apiClient.delete(`/departments/${id}`);
        }
    },

    //=========================== 2. 角色 (Roles) ===========================
    rolesApi: {
        list() {
            return apiClient.get('/roles/list');
        }
        // 通常角色是预设的，较少提供创建、更新、删除接口
    },

    //=========================== 3. 用户 (Users) ===========================
    usersApi: {
        login(credentials) {
            // 后端需要提供一个 /users/login 的接口
            return apiClient.post('/users/login', credentials);
        },
        create(data) {
            return apiClient.post('/users', data);
        },
        getById(id) {
            return apiClient.get(`/users/${id}`);
        },
        list() {
            return apiClient.get('/users/list');
        },
        update(data) {
            return apiClient.put('/users', data);
        },
        delete(id) {
            return apiClient.delete(`/users/${id}`);
        }
    },

    //=========================== 4. 产品 (Products) ===========================
    productsApi: {
        create(data) {
            return apiClient.post('/products', data);
        },
        getById(id) {
            return apiClient.get(`/products/${id}`);
        },
        list() {
            return apiClient.get('/products/list');
        },
        update(data) {
            return apiClient.put('/products', data);
        },
        delete(id) {
            return apiClient.delete(`/products/${id}`);
        }
    },

    //=========================== 5. 入库单 (Inbound Orders) ===========================
    inboundOrdersApi: {
        create(data) {
            return apiClient.post('/inbound-orders', data);
        },
        getById(id) {
            return apiClient.get(`/inbound-orders/${id}`);
        },
        list() {
            return apiClient.get('/inbound-orders/list');
        },
        update(data) {
            return apiClient.put('/inbound-orders', data);
        },
        delete(id) {
            return apiClient.delete(`/inbound-orders/${id}`);
        }
    },

    //=========================== 6. 库存 (Inventory) ===========================
    inventoryApi: {
        create(data) {
            return apiClient.post('/inventory', data);
        },
        getById(id) {
            return apiClient.get(`/inventory/${id}`);
        },
        // 根据批次号查询，用于扫码
        getByBatchCode(batchCode) {
            return apiClient.get(`/inventory/batch/${batchCode}`);
        },
        list() {
            return apiClient.get('/inventory/list');
        },
        update(data) {
            return apiClient.put('/inventory', data);
        },
        delete(id) {
            return apiClient.delete(`/inventory/${id}`);
        }
    },

    //=========================== 7. 异动日志 (Transaction Logs) ===========================
    transactionLogsApi: {
        create(data) {
            return apiClient.post('/transaction-logs', data);
        },
        listByInventoryId(inventoryId) {
            return apiClient.get(`/transaction-logs/inventory/${inventoryId}`);
        },
        list() {
            return apiClient.get('/transaction-logs/list');
        }
    }
};

export default api;