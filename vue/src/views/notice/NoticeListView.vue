<template>
  <div class="notice-page">
    <section class="page-heading-panel compact-heading">
      <div>
        <p class="eyebrow">PLATFORM NOTICES</p>
        <h1>公告与使用提示</h1>
        <p>查看平台通知、站点维护安排和新能源汽车充电使用提示。</p>
      </div>
      <div class="heading-stat"><strong>{{ total }}</strong><span>条已发布公告</span></div>
    </section>

    <section v-loading="loading" class="notice-page-list">
      <el-empty v-if="!loading && notices.length === 0" description="暂无已发布公告" />
      <router-link v-for="notice in notices" :key="notice.id" :to="`/notices/${notice.id}`" class="notice-page-card">
        <span :class="`notice-kind kind-${notice.noticeType}`">{{ noticeTypeText(notice.noticeType) }}</span>
        <div>
          <h2>{{ notice.title }}</h2>
          <p>{{ notice.content }}</p>
          <small>{{ notice.publishTime }}</small>
        </div>
        <b>→</b>
      </router-link>
    </section>

    <div v-if="total > pageSize" class="pagination-row">
      <el-pagination v-model:current-page="pageNum" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadNotices" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPublishedNotices } from '../../api/home'

const notices = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)

const loadNotices = async () => {
  loading.value = true
  try {
    const result = await getPublishedNotices({ pageNum: pageNum.value, pageSize })
    notices.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    ElMessage.error(error.message || '公告加载失败')
  } finally {
    loading.value = false
  }
}

const noticeTypeText = value => ({ 0: '平台通知', 1: '维护通知', 2: '使用提示' }[value] || '公告')

onMounted(loadNotices)
</script>
