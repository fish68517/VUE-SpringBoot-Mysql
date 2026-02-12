<template>
  <div class="posts-container">
    <h2>岗位浏览</h2>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索岗位名称或科室"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 岗位列表 -->
    <el-card class="posts-list-card">
      <el-empty v-if="posts.length === 0" description="暂无岗位" />
      
      <el-row :gutter="20" v-else>
        <el-col v-for="post in posts" :key="post.id" :xs="24" :sm="12" :md="8">
          <div class="post-card">
            <div class="post-header">
              <h3>{{ post.title }}</h3>
              <el-tag v-if="post.status === 'PUBLISHED'" type="success">招聘中</el-tag>
              <el-tag v-else type="info">已下架</el-tag>
            </div>
            <div class="post-body">
              <p><strong>科室：</strong>{{ post.department }}</p>
              <p><strong>名额：</strong>{{ post.quota }}</p>
              <p><strong>周期：</strong>{{ post.duration }}周</p>
              <p class="description">{{ post.description }}</p>
            </div>
            <div class="post-footer">
              <el-button type="primary" size="small" @click="showPostDetail(post)">
                查看详情
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 岗位详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="岗位详情" width="600px">
      <div v-if="selectedPost" class="post-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="岗位名称">{{ selectedPost.title }}</el-descriptions-item>
          <el-descriptions-item label="科室">{{ selectedPost.department }}</el-descriptions-item>
          <el-descriptions-item label="岗位描述">{{ selectedPost.description }}</el-descriptions-item>
          <el-descriptions-item label="招聘名额">{{ selectedPost.quota }}</el-descriptions-item>
          <el-descriptions-item label="实习周期">{{ selectedPost.duration }}周</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="selectedPost.status === 'PUBLISHED'" type="success">招聘中</el-tag>
            <el-tag v-else type="info">已下架</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          type="primary"
          @click="goToApply"
          :disabled="!selectedPost || selectedPost.status !== 'PUBLISHED'"
        >
          申请岗位
        </el-button>
      </template>
    </el-dialog>

    <!-- 申请表单对话框 -->
    <el-dialog v-model="applyDialogVisible" title="申请岗位" width="600px" @close="resetApplyForm">
      <el-form v-if="selectedPost" :model="applyForm" label-width="100px">
        <el-form-item label="岗位名称">
          <span>{{ selectedPost.title }}</span>
        </el-form-item>
        <el-form-item label="申请理由" required>
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            rows="4"
            placeholder="请填写申请理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApplication" :loading="applyLoading">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPosts, getPostDetail } from '@/api/post'
import { createApplication } from '@/api/application'
import { useRouter } from 'vue-router'

const router = useRouter()

// 数据
const posts = ref([])
const selectedPost = ref(null)
const detailDialogVisible = ref(false)
const applyDialogVisible = ref(false)
const applyLoading = ref(false)

const searchForm = ref({
  keyword: '',
})

const applyForm = ref({
  reason: '',
})

// 获取岗位列表
const fetchPosts = async () => {
  try {
    const response = await getPosts({
      keyword: searchForm.value.keyword,
    })
    posts.value = response.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取岗位列表失败')
  }
}

// 搜索
const handleSearch = () => {
  fetchPosts()
}

// 重置搜索
const handleReset = () => {
  searchForm.value.keyword = ''
  fetchPosts()
}

// 显示岗位详情
const showPostDetail = async (post) => {
  try {
    const response = await getPostDetail(post.id)
    selectedPost.value = response.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '获取岗位详情失败')
  }
}

// 跳转到申请表单
const goToApply = () => {
  detailDialogVisible.value = false
  applyDialogVisible.value = true
}

// 重置申请表单
const resetApplyForm = () => {
  applyForm.value.reason = ''
}

// 提交申请
const submitApplication = async () => {
  if (!applyForm.value.reason.trim()) {
    ElMessage.warning('请填写申请理由')
    return
  }

  applyLoading.value = true
  try {
    await createApplication({
      postId: selectedPost.value.id,
      reason: applyForm.value.reason,
    })
    ElMessage.success('申请提交成功')
    applyDialogVisible.value = false
    resetApplyForm()
    // 跳转到我的申请页面
    router.push('/student/applications')
  } catch (error) {
    ElMessage.error(error.message || '申请提交失败')
  } finally {
    applyLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.posts-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.posts-list-card {
  margin-bottom: 20px;
}

.post-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.3s;
}

.post-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.post-body {
  flex: 1;
  margin-bottom: 10px;
}

.post-body p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.description {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #909399;
}

.post-footer {
  text-align: right;
}

.post-detail {
  padding: 20px 0;
}
</style>
