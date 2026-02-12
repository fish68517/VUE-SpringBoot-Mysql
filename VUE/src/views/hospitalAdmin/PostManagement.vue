<template>
  <div class="post-management-container">
    <h2>岗位管理</h2>
    
    <!-- 创建岗位按钮 -->
    <el-card class="box-card" style="margin-bottom: 20px;">
      <el-button type="primary" @click="handleCreatePost">+ 创建岗位</el-button>
    </el-card>
    
    <!-- 岗位列表 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>岗位列表</span>
          <el-button type="primary" @click="loadPosts">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="posts" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="岗位名称" width="150" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="quota" label="名额" width="80" />
        <el-table-column prop="duration" label="实习周期" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEditPost(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.status === 'PUBLISHED' ? 'warning' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)">
              {{ row.status === 'PUBLISHED' ? '下架' : '上架' }}
            </el-button>
            <el-button type="info" size="small" @click="handleViewDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 创建/编辑岗位对话框 -->
    <el-dialog 
      v-model="postDialogVisible" 
      :title="isEditingPost ? '编辑岗位' : '创建岗位'" 
      width="600px">
      <div v-if="postForm" class="post-form">
        <div class="form-group">
          <label>岗位名称：</label>
          <el-input v-model="postForm.title" placeholder="请输入岗位名称" />
        </div>
        <div class="form-group">
          <label>科室：</label>
          <el-input v-model="postForm.department" placeholder="请输入科室名称" />
        </div>
        <div class="form-group">
          <label>岗位描述：</label>
          <el-input 
            v-model="postForm.description" 
            type="textarea" 
            rows="4"
            placeholder="请输入岗位描述" />
        </div>
        <div class="form-group">
          <label>名额：</label>
          <el-input-number v-model="postForm.quota" :min="1" />
        </div>
        <div class="form-group">
          <label>实习周期（周）：</label>
          <el-input-number v-model="postForm.duration" :min="1" />
        </div>
        <div class="form-group">
          <label>面向学校：</label>
          <el-select 
            v-model="postForm.visibleSchools" 
            multiple 
            placeholder="选择面向的学校">
            <el-option 
              v-for="school in availableSchools" 
              :key="school.id" 
              :label="school.name" 
              :value="school.id" />
          </el-select>
        </div>
      </div>
      <template #footer>
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPostForm">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 岗位详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="岗位详情" width="600px">
      <div v-if="selectedPost" class="detail-view">
        <div class="detail-group">
          <label>岗位名称：</label>
          <span>{{ selectedPost.title }}</span>
        </div>
        <div class="detail-group">
          <label>科室：</label>
          <span>{{ selectedPost.department }}</span>
        </div>
        <div class="detail-group">
          <label>岗位描述：</label>
          <p class="description-text">{{ selectedPost.description }}</p>
        </div>
        <div class="detail-group">
          <label>名额：</label>
          <span>{{ selectedPost.quota }}</span>
        </div>
        <div class="detail-group">
          <label>实习周期：</label>
          <span>{{ selectedPost.duration }} 周</span>
        </div>
        <div class="detail-group">
          <label>状态：</label>
          <el-tag :type="getStatusType(selectedPost.status)">{{ selectedPost.status }}</el-tag>
        </div>
        <div class="detail-group">
          <label>创建时间：</label>
          <span>{{ selectedPost.createdAt }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getHospitalPosts, 
  createPost, 
  updatePost, 
  updatePostStatus 
} from '@/api/hospitalAdmin'

const posts = ref([])
const loading = ref(false)
const postDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedPost = ref(null)
const isEditingPost = ref(false)
const availableSchools = ref([])

const postForm = ref({
  title: '',
  department: '',
  description: '',
  quota: 1,
  duration: 8,
  visibleSchools: []
})

const loadPosts = async () => {
  loading.value = true
  try {
    const response = await getHospitalPosts()
    posts.value = response.data.data || []
  } catch (error) {
    ElMessage.error('加载岗位列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'ARCHIVED': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleCreatePost = () => {
  isEditingPost.value = false
  postForm.value = {
    title: '',
    department: '',
    description: '',
    quota: 1,
    duration: 8,
    visibleSchools: []
  }
  postDialogVisible.value = true
}

const handleEditPost = (row) => {
  isEditingPost.value = true
  postForm.value = {
    id: row.id,
    title: row.title,
    department: row.department,
    description: row.description,
    quota: row.quota,
    duration: row.duration,
    visibleSchools: row.visibleSchools || []
  }
  postDialogVisible.value = true
}

const handleViewDetail = (row) => {
  selectedPost.value = row
  detailDialogVisible.value = true
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 'PUBLISHED' ? 'ARCHIVED' : 'PUBLISHED'
    await updatePostStatus(row.id, { status: newStatus })
    ElMessage.success('岗位状态已更新')
    loadPosts()
  } catch (error) {
    ElMessage.error('更新岗位状态失败: ' + (error.response?.data?.message || error.message))
  }
}

const submitPostForm = async () => {
  try {
    if (isEditingPost.value) {
      await updatePost(postForm.value.id, postForm.value)
      ElMessage.success('岗位已更新')
    } else {
      await createPost(postForm.value)
      ElMessage.success('岗位已创建')
    }
    postDialogVisible.value = false
    loadPosts()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.post-management-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}

.post-form,
.detail-view {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group,
.detail-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label,
.detail-group label {
  font-weight: bold;
  color: #333;
}

.description-text {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}
</style>
