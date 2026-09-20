# 郑州城市智慧供水演示系统 · 第二阶段

普通 uni-app Vue 3 工程，业务数据由本地 JSON 提供。支持 PC/H5 和 App-vue 资源编译，不包含业务后端、数据库、云函数或真实设备接口。

## 本地启动

在本目录执行：

```powershell
npm ci
npm run prepare:demo
npm run dev:h5
```

开发地址：`http://127.0.0.1:5173`。也可双击 `启动H5开发.cmd`。

本地发行预览：

```powershell
npm run build:h5
npm run preview
```

发行预览地址：`http://127.0.0.1:4173`。不要直接双击 `index.html`。

## 演示账号

登录页点击管理员、操作员或访客可自动填入演示账号。完整账号与密码集中维护在 `account/users.json`，说明见 `account/README.md`。均为虚构凭据，前端没有生产安全隔离。

## 常用命令

```powershell
npm run check
npm test
npm run build:h5
npm run release:check
npm run build:app
```

`build:app` 生成 App-vue 资源，**不是 APK**。不调用 Android 云打包，也不上传 uniCloud。实际 APK 需在 HBuilderX 中配置自己的 AppID、包名和签名后打包。

浏览器回归（需本机安装 Chrome，先启动 `npm run preview`）：

```powershell
npm run test:browser
```

## HBuilderX

打开本目录 `code`。源码入口 `App.vue`、`main.ts`、`pages.json`、`manifest.json` 均在工程根目录。工程也提供 CLI 构建脚本，显式指定根目录为 `UNI_INPUT_DIR`。不要只导入 `pages` 或整个父级文档目录。

已按 5.24 对应的 uni-app 编译器锁定依赖；不要只单独升级 Vue 或某个 `@dcloudio/*` 包。具体本机运行记录见 `../doc/第二阶段验收记录.md`。

## 代码与数据

- `data/`：设施、管线、告警、工单、巡检、DMA、时序、公告及示例附件，业务源数据唯一维护位置。
- `account/`：演示账号、角色及权限。
- `domain/`：类型、计量公式、校验规则。
- `services/mock/engine.ts`：事务式本地保存、权限与业务状态迁移。
- `repositories/seed.ts`：读取基础 JSON；`stores/demo.ts`：界面可见范围和状态。
- `navigation/`：路由及登录拦截、H5详情参数同步、返回位置恢复。
- `components/`：领域页面组件；`pages/`：实际 uni-app 页面注册入口。
- `static/vendor/`：构建准备脚本复制的 ECharts，含许可证。
- `unpackage/dist/build/h5/`：H5 发布目录，用户自行上传目录内容。
- `unpackage/dist/build/app/`：App-vue 编译资源。
- `unpackage/evidence/`：本地验收记录、截图、CSV样例。

修改 JSON 后运行校验和构建；网页表单只保存本机状态，不改写源 JSON。原始需求文档中的地图和云参数没有嵌入客户端。

## 第二阶段

已补齐点线编辑和六级审核、框选面选、爆管影响、图层字段、模板和区域路线、改派、轨迹回放、设备模拟采样与启停、能耗方案、DMA 阈值与历史、六类日报月报、本地视频、门户定制、通知配置和快照迁移。移动端使用同一业务层。

新增测试命令：`npm run test:phase2`，30 分钟 H5 稳定性检查：`npm run test:stability`。需先启动本地发行预览。测试材料在 `unpackage/evidence/phase2`。

使用和数据维护见 [第二阶段使用与数据维护说明](../doc/第二阶段使用与数据维护说明.md)，打包与用户上传步骤见 [第二阶段本地构建与交接说明](../doc/第二阶段本地构建与交接说明.md)。未执行 uniCloud 上传；Android 签名 APK 和真机验收须另行完成，App-vue 资源编译通过不等同于真机通过。
