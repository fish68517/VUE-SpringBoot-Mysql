<template>
  <div class="notice-detail-page">
    <div class="detail-breadcrumb"><router-link to="/notices">公告列表</router-link><span>/</span><span>公告详情</span></div>
    <article v-loading="loading" class="notice-article">
      <template v-if="notice">
        <span :class="`notice-kind kind-${notice.noticeType}`">{{ noticeTypeText(notice.noticeType) }}</span>
        <h1>{{ notice.title }}</h1>
        <div class="notice-meta">发布时间：{{ notice.publishTime }}</div>
        <div class="notice-body">{{ notice.content }}</div>
        <router-link to="/notices" class="section-link">← 返回公告列表</router-link>
      </template>
    </article>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getNoticeDetail } from '../../api/home'

const props = defineProps({ id: { type: String, required: true } })
const notice = ref(null)
const loading = ref(false)

const loadNotice = async () => {
  loading.value = true
  try {
    notice.value = await getNoticeDetail(props.id)
  } catch (error) {
    ElMessage.error(error.message || '公告详情加载失败')
  } finally {
    loading.value = false
  }
}

const noticeTypeText = value => ({ 0: '平台通知', 1: '维护通知', 2: '使用提示' }[value] || '公告')

onMounted(loadNotice)
</script>
