# 数字法治底座前端

基于 Vue 3、TypeScript、Vite、Element Plus、Vue Router、Pinia、Axios、MockJS、vite-plugin-mock、ECharts 与 SCSS 的管理端前端工程。

## 运行

```bash
npm install
npm run dev
```

访问 `http://localhost:5173`，演示账号：

```text
账号：admin
密码：123456
```

构建与校验：

```bash
npm run type-check
npm run build
npm run preview
```

## 已实现模块

- Mock 登录、退出、Token 持久化、用户信息与按钮权限
- 工作台统计卡、事件趋势图、最新事件与我的待办
- 系统管理：用户、角色、菜单、部门、权限、数据权限
- 接入管理：三方接入方、应用、API 授权、IP 白名单、免认证接口
- 事件中心：事件列表、新建事件、详情、我的待办、流程记录
- 消息中心：消息、通知模板、站内消息、发送记录
- 定时任务：任务管理、执行记录
- 日志审计：登录、操作、异常日志
- 个人中心与前端架构说明

管理列表支持 Mock 分页、条件查询、新增、编辑、删除、批量选择、状态切换和 CSV 导出。只读审计类页面保留查询与查看能力。

## 调用链与后端切换

```text
Views / Components
       ↓
Pinia / API Service
       ↓
Axios Request
       ↓
/api/web/v1/**
       ↓
当前：vite-plugin-mock
后期：Spring Cloud Gateway
```

页面内没有直接引用 Mock 数组。正式联调时关闭 `vite.config.ts` 中的 Mock 插件，并通过 `.env.production` 配置 `VITE_API_BASE_URL` 即可；页面层与 API Service 的调用方式无需改变。

## 目录说明

```text
mock/                 Mock 接口与模拟数据
src/api/              API Service
src/assets/styles/    全局主题与样式
src/components/       公共组件
src/config/           资源页面字段与业务配置
src/directives/       权限指令
src/layout/           主框架、侧栏、顶部、标签栏
src/router/           静态路由与认证守卫
src/stores/           Pinia 状态
src/types/            TypeScript 数据契约
src/utils/            请求、Token、存储工具
src/views/            业务页面
```

## Mock 契约

统一响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页响应的 `data` 包含 `records`、`total`、`pageNum`、`pageSize`。Mock 新增、编辑、删除和状态切换仅保存在当前 Vite 开发服务内存中，重启服务后恢复初始数据。
