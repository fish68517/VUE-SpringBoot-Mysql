<template>
  <section class="page-card" style="padding: 24px;">
    <div class="toolbar">
      <div class="toolbar-title">公告管理</div>
      <div class="toolbar-actions">
        <el-button v-if="isAdmin" type="primary" @click="openCreate">发布公告</el-button>
        <el-button @click="loadNotices">刷新</el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col v-for="item in notices" :key="item.id" :span="12" style="margin-bottom: 16px;">
        <div class="notice-card">
          <div class="notice-head">
            <div class="notice-title">
              {{ item.title }}
              <el-tag v-if="item.pinned" size="small" type="danger" effect="dark">置顶</el-tag>
            </div>
            <div class="notice-meta">{{ item.publisherName }} | {{ item.publishTime?.replace('T', ' ') }}</div>
          </div>
          <div class="notice-content">{{ item.content }}</div>
          <div class="notice-footer">
            <el-tag :type="item.status === 'ACTIVE' ? 'success' : 'info'">{{ item.status }}</el-tag>
            <div v-if="isAdmin">
              <el-button link type="primary" @click="openEdit(item)">编辑</el-button>
              <el-popconfirm title="确认删除该公告？" @confirm="removeNotice(item.id)">
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-empty v-if="!notices.length" description="暂无公告" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '发布公告'" width="620px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="公告标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="公告内容"><el-input v-model="form.content" type="textarea" rows="6" /></el-form-item>
        <el-form-item label="公告状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="form.pinned" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotice">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
const notices = ref([])
const dialogVisible = ref(false)
const form = reactive(defaultForm())

function defaultForm() {
  return {
    id: null,
    title: '',
    content: '',
    status: 'ACTIVE',
    pinned: false
  }
}

function resetForm() {
  Object.assign(form, defaultForm())
}

async function loadNotices() {
  notices.value = await http.get('/api/notices', {
    params: { onlyActive: isAdmin.value ? false : true }
  })
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitNotice() {
  const payload = {
    title: form.title,
    content: form.content,
    status: form.status,
    pinned: form.pinned
  }
  if (form.id) {
    await http.put(`/api/notices/${form.id}`, payload)
  } else {
    await http.post('/api/notices', payload)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadNotices()
}

async function removeNotice(id) {
  await http.delete(`/api/notices/${id}`)
  ElMessage.success('删除成功')
  loadNotices()
}

onMounted(loadNotices)
</script>

<style scoped>
.notice-card {
  height: 100%;
  padding: 18px;
  border-radius: 18px;
  border: 1px solid rgba(197, 211, 228, 0.9);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(245, 250, 255, 0.96));
}

.notice-head {
  margin-bottom: 12px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: #1c3651;
}

.notice-meta {
  margin-top: 8px;
  color: #7087a2;
  font-size: 13px;
}

.notice-content {
  min-height: 88px;
  line-height: 1.8;
  color: #30485f;
  white-space: pre-wrap;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
}
</style>
