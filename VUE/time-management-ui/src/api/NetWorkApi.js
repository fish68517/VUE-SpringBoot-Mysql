import axios from 'axios';

// apiClient 实例负责与后端的所有HTTP通信
const apiClient = axios.create({
    // 后端服务的基础URL，根据 application.properties 确定
    baseURL: 'http://localhost:8081', // SpringBoot 后端地址
    headers: {
        'Content-Type': 'application/json'
    }
});

/**
 * 封装所有后端 API 请求
 * 每个对象（如 campusUserApi）对应后端一个 Controller
 */
const api = {

    //=========================== 1. 校园用户 (CampusUser) ===========================
    campusUserApi: {
        login(credentials) {
            // 【注意】/login 是一个标准的RESTful登录路径。
            // 您需要在后端的 CampusUserController.java 中添加一个 @PostMapping("/login") 方法来处理此请求。
            return apiClient.post('/campusUser/login', credentials);
        },
        register(data) { // 注册
            return apiClient.post('/campusUser/register', data);
        },
        getById(id) {
            return apiClient.get(`/campusUser/${id}`);
        },
        list() {
            return apiClient.get('/campusUser/list');
        },
        update(data) {
            return apiClient.put('/campusUser', data);
        },
        delete(id) {
            return apiClient.delete(`/campusUser/${id}`);
        }
    },

    //=========================== 2. 任务专注 (TaskFocus) ===========================
    taskFocusApi: {
        create(data) {
            return apiClient.post('/taskFocus', data);
        },
        getById(id) {
            return apiClient.get(`/taskFocus/${id}`);
        },
        list() {
            return apiClient.get('/taskFocus/list');
        },
        update(data) {
            return apiClient.put('/taskFocus', data);
        },
        delete(id) {
            return apiClient.delete(`/taskFocus/${id}`);
        }
    },

    //=========================== 3. 专注记录 (FocusRecord) ===========================
    focusRecordApi: {
        create(data) {
            return apiClient.post('/focusRecord', data);
        },
        getById(id) {
            return apiClient.get(`/focusRecord/${id}`);
        },
        list() {
            return apiClient.get('/focusRecord/list');
        },
        update(data) {
            return apiClient.put('/focusRecord', data);
        },
        delete(id) {
            return apiClient.delete(`/focusRecord/${id}`);
        }
    },

    //=========================== 4. 习惯追踪 (HabitTrack) ===========================
    habitTrackApi: {
        create(data) {
            return apiClient.post('/habitTrack', data);
        },
        getById(id) {
            return apiClient.get(`/habitTrack/${id}`);
        },
        list() {
            return apiClient.get('/habitTrack/list');
        },
        update(data) {
            return apiClient.put('/habitTrack', data);
        },
        delete(id) {
            return apiClient.delete(`/habitTrack/${id}`);
        }
    },

    //=========================== 5. 习惯打卡 (HabitCheckin) ===========================
    habitCheckinApi: {
        create(data) {
            return apiClient.post('/habitCheckin', data);
        },
        getById(id) {
            return apiClient.get(`/habitCheckin/${id}`);
        },
        list() {
            return apiClient.get('/habitCheckin/list');
        },
        update(data) {
            return apiClient.put('/habitCheckin', data);
        },
        delete(id) {
            return apiClient.delete(`/habitCheckin/${id}`);
        }
    },

    //=========================== 6. 成就徽章 (Achievement) ===========================
    achievementApi: {
        create(data) {
            return apiClient.post('/achievement', data);
        },
        getById(id) {
            return apiClient.get(`/achievement/${id}`);
        },
        list() {
            return apiClient.get('/achievement/list');
        },
        update(data) {
            return apiClient.put('/achievement', data);
        },
        delete(id) {
            return apiClient.delete(`/achievement/${id}`);
        }
    },

    //=========================== 7. 用户成就关联 (UserAchieveRel) ===========================
    userAchieveRelApi: {
        create(data) {
            return apiClient.post('/userAchieveRel', data);
        },
        getById(id) {
            return apiClient.get(`/userAchieveRel/${id}`);
        },
        list() {
            return apiClient.get('/userAchieveRel/list');
        },
        update(data) {
            return apiClient.put('/userAchieveRel', data);
        },
        delete(id) {
            return apiClient.delete(`/userAchieveRel/${id}`);
        }
    },

    //=========================== 8. 学习资源 (LearnResource) ===========================
    learnResourceApi: {
        create(data) {
            return apiClient.post('/learnResource', data);
        },
        getById(id) {
            return apiClient.get(`/learnResource/${id}`);
        },
        list() {
            return apiClient.get('/learnResource/list');
        },
        update(data) {
            return apiClient.put('/learnResource', data);
        },
        delete(id) {
            return apiClient.delete(`/learnResource/${id}`);
        }
    },

    //=========================== 9. 资源分类 (ResourceCategory) ===========================
    resourceCategoryApi: {
        create(data) {
            return apiClient.post('/resourceCategory', data);
        },
        getById(id) {
            return apiClient.get(`/resourceCategory/${id}`);
        },
        list() {
            return apiClient.get('/resourceCategory/list');
        },
        update(data) {
            return apiClient.put('/resourceCategory', data);
        },
        delete(id) {
            return apiClient.delete(`/resourceCategory/${id}`);
        }
    },

    //=========================== 10. 校园好友 (CampusFriend) ===========================
    campusFriendApi: {
        create(data) {
            return apiClient.post('/campusFriend', data);
        },
        getById(id) {
            return apiClient.get(`/campusFriend/${id}`);
        },
        list() {
            return apiClient.get('/campusFriend/list');
        },
        update(data) {
            return apiClient.put('/campusFriend', data);
        },
        delete(id) {
            return apiClient.delete(`/campusFriend/${id}`);
        }
    },

    //=========================== 11. 用户设置 (UserSetting) ===========================
    userSettingApi: {
        create(data) {
            return apiClient.post('/userSetting', data);
        },
        getById(id) {
            return apiClient.get(`/userSetting/${id}`);
        },
        list() {
            return apiClient.get('/userSetting/list');
        },
        update(data) {
            return apiClient.put('/userSetting', data);
        },
        delete(id) {
            return apiClient.delete(`/userSetting/${id}`);
        }
    },

    //=========================== 12. 通知中心 (NotificationCenter) ===========================
    notificationCenterApi: {
        create(data) {
            return apiClient.post('/notificationCenter', data);
        },
        getById(id) {
            return apiClient.get(`/notificationCenter/${id}`);
        },
        list() {
            return apiClient.get('/notificationCenter/list');
        },
        update(data) {
            return apiClient.put('/notificationCenter', data);
        },
        delete(id) {
            return apiClient.delete(`/notificationCenter/${id}`);
        }
    },

    // (以下两个是您项目中可能用于测试的 Controller，也一并提供)
    //=========================== Schedules ===========================
    schedulesApi: {
        create(data) {
            return apiClient.post('/schedules', data);
        },
        getById(id) {
            return apiClient.get(`/schedules/${id}`);
        },
        list() {
            return apiClient.get('/schedules/list');
        },
        update(data) {
            return apiClient.put('/schedules', data);
        },
        delete(id) {
            return apiClient.delete(`/schedules/${id}`);
        }
    },
    
    //=========================== User (Test) ===========================
    userApi: {
        create(data) {
            return apiClient.post('/user', data);
        },
        getById(id) {
            return apiClient.get(`/user/${id}`);
        },
        list() {
            return apiClient.get('/user/list');
        },
        update(data) {
            return apiClient.put('/user', data);
        },
        delete(id) {
            return apiClient.delete(`/user/${id}`);
        }
    },

    // ... 之前的代码

    // =========================== 4. 资源详情与上传 ===========================
    resourceDetailApi: {
        // 根据基础资源ID获取详情
        getByResourceId(id) {
            return apiClient.get(`/resourceDetail/getByResourceId/${id}`);
        },
        // 保存或更新详情
        saveOrUpdate(data) {
            if (data.detailId) {
                return apiClient.put('/resourceDetail', data);
            } else {
                return apiClient.post('/resourceDetail', data);
            }
        },
        // 文件上传 (返回文件名)
        uploadFile(file, type) {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('type', type); // 'image' or 'video'
            return apiClient.post('/resourceDetail/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
        }
    },
    
    
    // ... 之前的代码

    // =========================== 5. 用户资源交互 (点赞/收藏) ===========================
    userResourceActionApi: {
        // 获取列表 (用于管理员监控)
        list() {
            return apiClient.get('/userResourceAction/list');
        },
        // 删除记录
        delete(id) {
            return apiClient.delete(`/userResourceAction/${id}`);
        },
        // 执行操作 (可选，管理员也能帮用户点赞?)
        toggle(userId, resourceId, type) {
            return apiClient.post(`/userResourceAction/toggle/${userId}/${resourceId}/${type}`);
        }
    },
    
    // ...
};

export default api;