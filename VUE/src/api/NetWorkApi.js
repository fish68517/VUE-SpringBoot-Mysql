import axios from 'axios';

// apiClient 实例负责与后端的所有HTTP通信
const BASE_URL = 'http://localhost:8080';

const apiClient = axios.create({
    baseURL: BASE_URL,
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
         // --- 新增：注册接口 ---
        register(data) {
            // 假设后端注册接口为 POST /users/register 或者直接复用 POST /users (创建用户)
            // 根据常规做法，如果有专门的注册逻辑（比如不需要鉴权），通常是 /users/register
            // 如果复用创建接口，则是 apiClient.post('/users', data);
            // 这里我们假设有一个专门的注册入口或者开放的创建入口
            return apiClient.post('/users/register', data); 
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
         createOrder(data) {
            return apiClient.post('/inbound-orders/create/order', data);
        },
        
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
        },

        /**
         * 更新入库单信息 (部分更新)
         * @param {number | string} id - 要更新的入库单ID
         * @param {object} data - 包含要更新字段的对象, 例如 { status: '已完成' }
         * @returns {Promise} - Axios Promise 对象
         */
        updateById(id, data) {
            return apiClient.patch(`/inbound-orders/${id}`, data);
        },
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
    },

    // ===== 新增：系统日志接口 =====
    systemLogsApi: {
        list() {
            return apiClient.get('/system-logs/list');
        }
    }
};

export default api;
export { BASE_URL };