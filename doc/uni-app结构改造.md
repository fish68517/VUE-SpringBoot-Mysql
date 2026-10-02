# uni-app 结构改造

日期：2026-10-02。本次仅修改本地工程并验证编译，没有部署或改动云空间配置、数据。

## 改造结果

以用户指定的 `E:/bishe27/服务平台小程序/公考小程序/code/apps/client` 为只读参考，采用相同的 Vue 3 + Vite + `src` 结构及同版 DCloud 构建依赖。当前根目录即 uni-app CLI 工程，可直接导入 HBuilderX，原真实 appid `__UNI__1F79125` 写入 `src/manifest.json`。

官方说明 CLI 工程的 manifest 位于 src，已有 CLI 工程可以导入 HBuilderX，支持网站发行：[manifest 配置](https://uniapp.dcloud.net.cn/collocation/manifest)、[uni-app 快速上手](https://uniapp.dcloud.net.cn/quickstart)。实际操作以当前 HBuilderX 的菜单与输出提示为准。

## 保留和调整

- 原登录、三个账号权限、台账、收款分配、设备详情编辑、导入导出和 PDF 预览业务保留；原云函数主源码和 HTTP 接口保持不变。
- 原 dist 源码拆分进入 `src/ledger/`。`pages/index/index.vue` 通过 `v-html` 挂载仓库中的固定 HTML 模板，再在 mounted 中启动独立的业务运行实例。该模板是本地源码，不是用户输入或远端返回的 HTML。
- 没有 iframe、web-view 或旧网站跳转；Vue/uni-app 负责应用入口、路由和构建，原 DOM 实现负责台账界面。没有把所有表单重写为 Vue 数据绑定，此举限制改造范围并保持业务行为。
- 原生表单样式以 `styles.css?raw` 加载，挂载时插入 style，卸载时移除，避免 uni-app 编译器将 input/label 等选择器改写为 uni 组件名。
- 原模块缓存查询参数移除，由 Vite 生成带哈希的资源名；PDF Worker 改为 `?url` 导入，跟随构建产物定位。JSZip 改为固定版本 npm 依赖。
- 样例移动到 `src/static/sample/`；旧业务数据中的 `sample/...` 附件路径在读取时映射到新位置。云存储完整 URL 不变。
- 页面卸载移除全局认证事件、注销 WebMCP 工具、清除保存定时器及释放 PDF 对象 URL；页面内委托事件绑定到页面根元素，避免跨页面残留。
- 禁用 uni 统计；未加入新的第三方统计配置。当前为浏览器端应用，使用 hash 路由，不需要服务器 history 回退规则，不启用 SSR。
- `.gitignore` 现在排除整个生成目录 dist；源码、依赖锁文件、云函数仍需提交。版本控制中旧 dist 文件的删除与新 src 文件的新增一起提交，表示源码迁移，并非源码丢失。

## 旧版保留位置

原网页源码备份：`backups/before-uni-app-migration/dist/`。迁移时旧工作目录也移至 `backups/before-uni-app-migration/original-dist/`，旧标准部署工程移至 `backups/before-uni-app-migration/deployment-project/`。备份不提交 Git。

原线上部署记录及上传前备份仍保留；它们描述的是此次结构迁移前的发布。以后不再维护 `tools/zsh-ledger-deploy` 副本，也不再把 dist 根目录当主源码。

## 验证

1. `npm run build:h5` 本地生产构建成功，输出 `dist/build/h5/`。
2. HBuilderX CLI 以项目根路径执行 `publish web --webHosting false`，识别真实 appid、Vue 3，并返回“编译成功”“导出 Web 成功”，输出 `dist/build/web/`。此命令明确关闭托管上传。
3. `npm test`：22 项通过，包括多项目分配、删除回退、日期、云函数权限与省市字段保护。
4. 针对 HBuilderX 最终 `dist/build/web/` 产物的 17 项浏览器 Mock 检查全部通过，覆盖管理员、两个业务员、手机宽度、到账分配/删除、维保筛选、设备日期与详情编辑、批量删除，并增加旧样例 PDF 地址和打包后的 Worker 实际渲染检查。结果见 `artifacts/local-tests/browser-report.json`。

验证仅针对本地构建与 Mock 数据，不表示完成生产登录、云存储读写或本次云端发布。完整发行步骤见根 [README](../README.md)。
