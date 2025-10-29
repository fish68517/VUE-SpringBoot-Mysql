### 1. Node创建VUE项目
# 使用 Vite 创建一个新的 Vue 项目
npm create vite@latest

# 根据提示输入项目名称，例如：time-management-ui
# 选择框架：Vue
# 选择变体：JavaScript 或 TypeScript

# 安装依赖
cd time-management-ui
npm install
npm install axios element-plus

# 配置 Element Plus
# 在 src/main.js 中引入并配置 Element Plus：
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')

# 4：创建 API 服务
在 src 目录下创建一个 api 文件夹，并新建 user.js 文件，用于封装用户相关的 API 请求。
// src/api/user.js
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

# 5：创建 Vue 组件
在 src/components 目录下创建你的组件。例如，创建一个 UserList.vue 来显示用户列表。

# 6：在主页面中使用组件
修改 App.vue 来使用你新创建的组件。

# 7：运行与调试
启动后端：在 IntelliJ IDEA 中运行 TimeManagementSystemApplication 的 main 方法。

启动前端：在 time-management-ui 项目的命令行中运行 npm run dev。

在浏览器中打开 Vite 提供的地址（通常是 http://localhost:5173），你就可以看到前端页面，并且它会从后端获取数据。