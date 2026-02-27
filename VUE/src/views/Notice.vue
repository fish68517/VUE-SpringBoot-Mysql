<template>
  <div class="notice-container">
    <el-card>
      <template #header>
        <span>系统公告</span>
      </template>

      <div v-if="notices.length === 0" class="empty-tip">
        暂无公告
      </div>

      <div v-else class="notice-list">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notice-item"
          @click="showNotice(notice)"
        >
          <div class="notice-header">
            <el-tag :type="typeTag(notice.type)" size="small">{{ typeName(notice.type) }}</el-tag>
            <span class="notice-title">{{ notice.title }}</span>
          </div>
          <div class="notice-time">{{ formatTime(notice.publishTime) }}</div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="currentNotice?.title" width="600px">
      <div class="notice-detail">
        <div class="notice-meta">
          <el-tag :type="typeTag(currentNotice?.type)" size="small">
            {{ typeName(currentNotice?.type) }}
          </el-tag>
          <span class="time">{{ formatTime(currentNotice?.publishTime) }}</span>
        </div>
        <div class="notice-content">{{ currentNotice?.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPublishedNotices } from '../api/notice'

const notices = ref([])
const dialogVisible = ref(false)
const currentNotice = ref(null)

const typeName = (type) => {
  const names = { 0: '普通', 1: '重要', 2: '紧急' }
  return names[type] || '普通'
}

const typeTag = (type) => {
  const tags = { 0: 'info', 1: 'warning', 2: 'danger' }
  return tags[type] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const showNotice = (notice) => {
  currentNotice.value = notice
  dialogVisible.value = true
}

const loadNotices = async () => {
  const res = await getPublishedNotices()
  notices.value = res.data
}

onMounted(() => {
  loadNotices()
})
</script>

<style scoped>
.notice-container {
  max-width: 800px;
  margin: 0 auto;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px;
}

.notice-list {
  text-align: left;
}

.notice-item {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.3s;
}

.notice-item:hover {
  background: #f5f7fa;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notice-title {
  font-size: 16px;
  font-weight: 500;
}

.notice-time {
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}

.notice-detail {
  text-align: left;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.notice-meta .time {
  color: #909399;
  font-size: 14px;
}

.notice-content {
  line-height: 1.8;
  white-space: pre-wrap;
  color: #606266;
}
</style>
