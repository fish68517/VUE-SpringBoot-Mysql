<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CircleCheck, Clock, Document, EditPen, User } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader/index.vue'
import StatusTag from '@/components/StatusTag/index.vue'
import { getResourcePage } from '@/api/resource'
import type { ResourceRecord } from '@/types/system'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const event = ref<ResourceRecord>({ id: Number(route.params.id) })

onMounted(async () => {
  const result = await getResourcePage('event', 'event', { pageNum: 1, pageSize: 100 })
  event.value = result.data.records.find((item) => item.id === Number(route.params.id)) || result.data.records[0] || event.value
  loading.value = false
})
</script>

<template>
  <section v-loading="loading" class="event-detail">
    <PageHeader title="事件详情" description="查看事件基本信息、办理进度和流程记录。">
      <el-button :icon="ArrowLeft" @click="router.back()">返回</el-button>
      <el-button type="primary" :icon="EditPen">处理事件</el-button>
    </PageHeader>

    <div class="detail-hero page-card">
      <div class="hero-icon"><el-icon><Document /></el-icon></div>
      <div class="hero-main"><span>{{ event.eventNo }}</span><h2>{{ event.title }}</h2><div class="hero-meta"><StatusTag :status="event.priority || '普通'" /><StatusTag :status="event.processStatus || '待受理'" /><span>事件类型：{{ event.category || '行政执法监督' }}</span></div></div>
      <div class="hero-owner"><span>当前经办人</span><strong>{{ event.assignee || '刘明宇' }}</strong><small>{{ event.deptName || '执法监督处' }}</small></div>
    </div>

    <div class="detail-grid">
      <article class="info-card page-card">
        <div class="card-title"><h3>事件信息</h3><span>EVENT INFORMATION</span></div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="事件编号">{{ event.eventNo }}</el-descriptions-item>
          <el-descriptions-item label="事件来源">{{ event.source || '平台录入' }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ event.initiator || '系统管理员' }}</el-descriptions-item>
          <el-descriptions-item label="发起时间">{{ event.createTime }}</el-descriptions-item>
          <el-descriptions-item label="承办部门">{{ event.deptName || '执法监督处' }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ event.deadline || '2026-09-02 18:00:00' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ event.contactName || '张老师' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ event.contactPhone || '138****5612' }}</el-descriptions-item>
          <el-descriptions-item label="事件描述" :span="2"><div class="description">{{ event.description || '收到行政执法监督线索，请承办部门核查相关执法材料与办理程序，并在规定时限内反馈核查结果。' }}</div></el-descriptions-item>
        </el-descriptions>
        <div class="attachment"><el-icon><Document /></el-icon><div><strong>执法监督线索材料.pdf</strong><span>2.4 MB · 发起人上传</span></div><el-button link type="primary">下载</el-button></div>
      </article>

      <article class="process-card page-card">
        <div class="card-title"><h3>办理进度</h3><span>PROCESS TIMELINE</span></div>
        <el-timeline>
          <el-timeline-item type="success" :icon="CircleCheck" size="large" timestamp="2026-08-26 09:20">
            <div class="timeline-content"><strong>事件发起</strong><p>系统管理员提交事件，自动生成事件编号</p><span><el-icon><User /></el-icon> 系统管理员</span></div>
          </el-timeline-item>
          <el-timeline-item type="success" :icon="CircleCheck" size="large" timestamp="2026-08-26 09:35">
            <div class="timeline-content"><strong>部门受理</strong><p>执法监督处确认受理并分派经办人</p><span><el-icon><User /></el-icon> 王海峰</span></div>
          </el-timeline-item>
          <el-timeline-item type="primary" :icon="Clock" size="large" timestamp="当前节点">
            <div class="timeline-content active"><strong>材料核查</strong><p>正在核查执法程序与相关材料</p><span><el-icon><User /></el-icon> 刘明宇</span></div>
          </el-timeline-item>
          <el-timeline-item hollow timestamp="待流转"><div class="timeline-content muted"><strong>结果复核</strong><p>等待前序节点完成</p></div></el-timeline-item>
          <el-timeline-item hollow timestamp="待流转"><div class="timeline-content muted"><strong>事件办结</strong><p>等待审核通过后归档</p></div></el-timeline-item>
        </el-timeline>
      </article>
    </div>
  </section>
</template>

<style scoped lang="scss">
.event-detail { min-height: 100%; }
.detail-hero { display: flex; align-items: center; gap: 17px; padding: 23px 25px; margin-bottom: 14px; }
.hero-icon { width: 54px; height: 54px; display: grid; place-items: center; border-radius: 11px; background: #fff0e9; color: var(--brand); font-size: 25px; }
.hero-main { min-width: 0; flex: 1; }
.hero-main > span { color: #949ca8; font-size: 11px; }
.hero-main h2 { margin: 5px 0 10px; font-size: 19px; }
.hero-meta { display: flex; align-items: center; gap: 8px; color: #8a92a0; font-size: 11px; }
.hero-owner { min-width: 150px; display: flex; flex-direction: column; padding-left: 24px; border-left: 1px solid #edf0f3; }
.hero-owner span { color: #999faa; font-size: 10px; }
.hero-owner strong { margin-top: 4px; font-size: 14px; }
.hero-owner small { margin-top: 3px; color: #858d9a; }
.detail-grid { display: grid; grid-template-columns: minmax(0, 1.55fr) minmax(340px, .75fr); gap: 14px; }
.info-card, .process-card { padding: 21px 23px; }
.card-title { display: flex; align-items: baseline; gap: 9px; margin-bottom: 18px; }
.card-title h3 { margin: 0; font-size: 15px; }
.card-title span { color: #b3b8c1; font-size: 8px; letter-spacing: 1.2px; }
.description { min-height: 72px; line-height: 1.8; }
.attachment { display: flex; align-items: center; gap: 12px; margin-top: 16px; padding: 12px 14px; border: 1px solid #e9ecf0; border-radius: 6px; background: #fafbfc; }
.attachment > .el-icon { color: var(--brand); font-size: 24px; }
.attachment div { flex: 1; display: flex; flex-direction: column; gap: 3px; }
.attachment strong { font-size: 12px; }.attachment span { color: #9aa1ad; font-size: 10px; }
.timeline-content { padding-bottom: 8px; }
.timeline-content strong { color: #3a404c; font-size: 13px; }
.timeline-content p { margin: 6px 0; color: #7e8794; font-size: 11px; line-height: 1.6; }
.timeline-content span { display: flex; align-items: center; gap: 3px; color: #a0a6b0; font-size: 10px; }
.timeline-content.active { padding: 10px 12px; border: 1px solid #f2c6b1; border-radius: 6px; background: #fff8f4; }
.timeline-content.muted { opacity: .55; }
</style>
