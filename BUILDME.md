<!--
 * @Author: kaix
 * @Date: 2023-03-12 18:20:24
 * @LastEditTime: 2023-03-13 15:17:47
 * @LastEditors: kaix
 * @Description: 
-->
# CoolV-comps 可视化低代码插拔式vue3组件库

### 通过UnoCSS为组件库添加样式系统，可以通过属性定制组件样式。
 1. 引入 UnoCSS 样式；
 2. 实现组件属性定制按钮样式；
 3. 实现【Icon图标按钮】。


### Vitepress搭建文档

1. Vitepress 作为静态文档生成器，提供将 markdown 生成静态网站的能力；
2. 通过配置主题获取 vue 实例，加载组件库，对组件库运行 Demo 进行展示；
3. 通过引用 DemoBlock Markdown 插槽，可以达到同时展示 Demo 和代码块的酷炫效果。


### 编码与项目结构规范
- 引入项目规范，配置自动化检查工具，避免代码架构退化

1. Eslint 代码检查工具；
2. Prettier 代码格式化工具；
3. Git commit 提交检查脚本；
4. Husky + git hook 提交前校验。

### 组件库打包格式
- 组件库能够兼容多种组件库打包格式，并可以输出压缩版本
  1. 配置Vite 输出多种格式模块；
  2. 配置SourceMap映射；
  3. 测试打包结果。

### 组件库的按需引入
- 经典方法：组件单独分包 + 按需导入 + babel-plugin-component ( 自动化按需引入)；
- 次时代方法：ESModule + Treeshaking + 自动按需 import（unplugin-vue-components 自动化配置）。

### peerDependencies
- 如果安装此组件库最好也要安装这里面的npm包

## 代码提交

- feat: 新功能
- fix: 修复 Bug
- docs: 文档修改
- perf: 性能优化
- revert: 版本回退
- ci: CICD 集成相关
- test: 添加测试代码
- refactor: 代码重构
- build: 影响项目构建或依赖修改
- style: 不影响程序逻辑的代码修改
- chore: 不属于以上类型的其他类型(日常事务)