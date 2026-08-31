<script setup lang="ts">
import { Connection, DataBoard, Lock, Monitor, SetUp } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader/index.vue'

const modules = [
  { name: '页面层 Views', desc: '业务页面与路由入口', icon: Monitor },
  { name: '组件层 Components', desc: '可复用交互与展示组件', icon: DataBoard },
  { name: '状态层 Pinia', desc: '认证、权限与界面状态', icon: SetUp },
  { name: 'Service / API', desc: '统一业务服务与数据契约', icon: Connection },
  { name: 'Axios Request', desc: 'Token、异常和响应拦截', icon: Lock },
]
</script>

<template>
  <section class="architecture-page">
    <PageHeader title="前端架构说明" description="当前使用 Mock 开发，页面与服务层无需感知后端切换。" />

    <div class="architecture-hero page-card">
      <span class="eyebrow">VUE 3 · TYPESCRIPT · MOCK FIRST</span>
      <h2>一套面向真实后端契约设计的前端架构</h2>
      <p>所有页面严格通过 API Service 与 Axios Request 访问 <code>/api/web/v1/**</code>，当前由 Mock Server 响应；联调时仅需调整环境配置。</p>
      <div class="tech-list"><el-tag v-for="tech in ['Vue 3', 'TypeScript', 'Vite', 'Element Plus', 'Pinia', 'Axios', 'MockJS', 'ECharts', 'SCSS']" :key="tech" effect="plain">{{ tech }}</el-tag></div>
    </div>

    <div class="flow-panel page-card">
      <div class="flow-title"><h3>核心调用链</h3><span>页面层不直接依赖 Mock 数据</span></div>
      <div class="flow-row">
        <template v-for="(item, index) in modules" :key="item.name">
          <article class="flow-node"><div><el-icon><component :is="item.icon" /></el-icon></div><strong>{{ item.name }}</strong><span>{{ item.desc }}</span></article>
          <div v-if="index < modules.length - 1" class="flow-arrow">→</div>
        </template>
      </div>
      <div class="switch-row"><span>当前开发阶段</span><b>Mock API / 本地 TS 数据</b><i>无感切换</i><span>后期联调阶段</span><b>Spring Cloud Gateway</b></div>
    </div>

    <div class="principle-grid">
      <article class="page-card"><span>01</span><h3>统一契约</h3><p>Mock 返回统一的 ResponseResult 与 PageResult 结构，TypeScript Interface 与未来后端 DTO 字段保持一致。</p></article>
      <article class="page-card"><span>02</span><h3>模块边界</h3><p>认证、系统、接入、事件、消息、任务、日志模块分别组织路由、页面、API 与模拟数据。</p></article>
      <article class="page-card"><span>03</span><h3>权限先行</h3><p>使用 Mock Token、Pinia 用户状态与 Permission Code 完成菜单和按钮级权限演示。</p></article>
      <article class="page-card"><span>04</span><h3>可替换数据源</h3><p>业务页只调用 Service。接入 Gateway 时无需修改页面逻辑，只需关闭 Mock 并配置 API Base URL。</p></article>
    </div>
  </section>
</template>

<style scoped lang="scss">
.architecture-hero { position: relative; overflow: hidden; padding: 32px 35px; margin-bottom: 14px; background: linear-gradient(125deg, #303641, #242832 72%, #3b2b27); color: #fff; }
.architecture-hero::after { content: ''; position: absolute; right: -90px; top: -150px; width: 400px; height: 400px; border: 1px solid rgba(231,119,72,.18); border-radius: 50%; }
.eyebrow { color: #e5855d; font-size: 9px; letter-spacing: 2px; }.architecture-hero h2 { margin: 12px 0 10px; font-size: 24px; }.architecture-hero p { max-width: 760px; margin: 0; color: #aeb5c0; font-size: 13px; line-height: 1.8; }.architecture-hero code { color: #ef936b; }
.tech-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 23px; }.tech-list .el-tag { --el-tag-text-color: #eeb195; --el-tag-border-color: rgba(236,140,96,.35); --el-tag-bg-color: rgba(216,95,43,.08); }
.flow-panel { padding: 23px 28px; margin-bottom: 14px; }
.flow-title { display: flex; align-items: baseline; gap: 10px; margin-bottom: 25px; }.flow-title h3 { margin: 0; font-size: 16px; }.flow-title span { color: #9ba2ad; font-size: 11px; }
.flow-row { display: flex; align-items: stretch; justify-content: space-between; }
.flow-node { min-width: 145px; flex: 1; display: flex; flex-direction: column; align-items: center; padding: 18px 10px; border: 1px solid #e8ebef; border-radius: 7px; background: #fafbfc; text-align: center; }
.flow-node div { width: 39px; height: 39px; display: grid; place-items: center; margin-bottom: 10px; border-radius: 8px; background: #fff0e9; color: var(--brand); font-size: 18px; }.flow-node strong { font-size: 12px; }.flow-node span { margin-top: 5px; color: #9ca3ae; font-size: 9px; }
.flow-arrow { display: grid; place-items: center; padding: 0 9px; color: #d78a68; font-size: 20px; }
.switch-row { display: flex; align-items: center; justify-content: center; gap: 12px; margin-top: 20px; padding: 13px; border-radius: 6px; background: #f7f8fa; color: #8c94a0; font-size: 11px; }.switch-row b { color: #3e4552; }.switch-row i { padding: 3px 9px; border-radius: 12px; background: #fff0e9; color: var(--brand); font-style: normal; }
.principle-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; }.principle-grid article { padding: 22px; }.principle-grid article > span { color: #e28a65; font-family: Georgia, serif; font-size: 24px; }.principle-grid h3 { margin: 10px 0 8px; font-size: 14px; }.principle-grid p { margin: 0; color: #7f8794; font-size: 11px; line-height: 1.75; }
@media (max-width: 1250px) { .flow-node { min-width: 120px; }.flow-arrow { padding-inline: 5px; }.principle-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
