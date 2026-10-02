# 中晟辉业务台账 · uni-app H5

当前项目为 **uni-app CLI / Vue 3 / Vite** 结构，与参考工程 `公考小程序/code/apps/client` 一样使用 `src/manifest.json`、`src/pages.json` 和 Vue 页面入口。HBuilderX 直接打开本目录即可运行、发行网站。真实 AppID 已沿用 `__UNI__1F79125`。

## 在 HBuilderX 中发行

1. 在 HBuilderX 中导入**本项目根目录（包含 package.json 的目录）**，不要只打开 `src`、`dist` 或旧的部署副本。
2. 本机依赖已安装。换电脑先在根目录执行 `npm ci`；需要本机 Node.js/npm。
3. 打开 `src/manifest.json` 检查应用名称和 H5 配置。当前为根路径 `/`、hash 路由、非 SSR。
4. 选中项目，选择 **发行 → 网站-PC Web 或手机 H5**。本机 HBuilderX 5.24 发行产物是 **`dist/build/web/`**，以发行控制台实际提示的输出目录为准。
5. 需要发布时，由你在发行界面选择前端网页托管与原阿里云空间 `zsh-ledger-prod`。本次仅完成结构改造及本地编译，未上传网页或云函数。

普通网站发行只负责前端；如果以后修改云函数，仍需在根 `uniCloud-aliyun/cloudfunctions/zsh-ledger-api` 上上传该函数。首次在本工程中使用云功能时，关联已有空间 `mp-f4c892a2-7540-459d-9a6f-ccae78f87037`，不要新建空空间或初始化生产数据。

## 目录与修改位置

```text
项目根目录/
├─ package.json / package-lock.json    固定构建依赖与命令
├─ vite.config.js                     uni-app Vite 插件
├─ index.html                         Vite H5 入口
├─ src/
│  ├─ App.vue / main.js               Vue 3 应用入口
│  ├─ manifest.json / pages.json      AppID、H5 与页面配置
│  ├─ pages/index/index.vue           台账页面及挂载/卸载生命周期
│  ├─ ledger/
│  │  ├─ layout.html                  台账页面 HTML 片段
│  │  ├─ app.js                       现有台账业务与 DOM 交互
│  │  ├─ styles.css                   页面样式
│  │  ├─ cloud-config.js              原 HTTP 云函数地址
│  │  ├─ cloud-api.js                 登录、台账、附件 API
│  │  ├─ ledger-data.js               到款分配等纯逻辑
│  │  └─ vendor/                     PDF.js 与 Worker 原依赖
│  └─ static/sample/                 原 Excel / PDF 样例
├─ uniCloud-aliyun/cloudfunctions/    原云函数主源码
├─ tests/                            逻辑与构建产物 Mock 回归测试
├─ dist/build/web/                   HBuilderX 5.24 Web 发行输出，不提交
├─ dist/build/h5/                    npm build:h5 输出，不提交
└─ doc/                             项目文档
```

页面通过 uni-app 页面生命周期挂载原生 HTML 表单和表格，保留原 DOM 业务代码，没有使用 iframe 加载旧网站。此次目标为 PC/H5 工程化，未将业务重写为全响应式 Vue 组件，也未适配微信小程序或原生 App；请使用网站发行，不启用 SSR。

## 命令行

```powershell
npm ci
npm run dev:h5        # 本地开发，实际请求仍指向原云空间
npm run build:h5      # 本地生产构建，输出 dist/build/h5，不上传
npm test             # 22 项纯逻辑/云函数 Mock 测试
npm run test:browser  # 检查 dist/build/h5，需要本机 Microsoft Edge
```

测试会拦截业务请求到本地 Mock，阻止其他外部请求，不修改生产数据。检查 HBuilderX 的发行产物可以在 PowerShell 设置 `$env:TEST_WEB_ROOT='dist/build/web'` 后运行 `npm run test:browser`。本地静态预览脚本 `server.ps1` 默认打开命令行构建后的 `dist/build/h5`。

源码应修改 `src/`，不要再编辑生成的 `dist/`。云端地址保存在 `src/ledger/cloud-config.js` 并随前端打包；修改配置后需要重新发行。云函数仍为 CommonJS，根 package.json 没有设置 `type: module`，避免改变云函数 Node 模块解释方式。

迁移详情、验证边界与旧版备份见 [uni-app 结构改造](doc/uni-app结构改造.md)。旧部署记录保留作历史参考，旧部署工程已移至本地备份。
