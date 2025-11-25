好的，这是汉化后的 “社区动态列表” (Community Feed) 页面代码。

我已经将所有界面文本、按钮、提示语以及 JavaScript 中的反馈消息都翻译成了中文。

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="community-feed">
    <!-- 顶部标题栏 -->
    <div class="feed-header">
      <h2>社区动态</h2>
      <el-button type="primary" @click="handleCreatePost">
        <el-icon><Edit /></el-icon>
        发布动态
      </el-button>
    </div>

    <!-- 动态列表区域 -->
    <div v-loading="loading" class="feed-content">
      <template v-if="!loading && posts.length > 0">
        <post-card
          v-for="post in posts"
          :key="post.id"
          :post="post"
          @refresh="loadPosts"
          @edit="handleEditPost"
        />
      </template>

      <!-- 空状态 -->
      <el-empty
        v-else-if="!loading && posts.length === 0"
        description="暂无动态，快来发布第一条吧！"
      >
        <el-button type="primary" @click="handleCreatePost">发布动态</el-button>
      </el-empty>
    </div>

    <!-- 分页器 -->
    <div v-if="total > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- 编辑动态弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑动态"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="editForm" label-position="top">
        <el-form-item label="内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享您的健身心得..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleUpdatePost">
          更新
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Edit } from '@element-plus/icons-vue'
import { getDynamics, updateDynamic } from '@/api/community'
import PostCard from '@/components/community/PostCard.vue'
import { showSuccess, showError, showWarning } from '@/utils/feedback'

const router = useRouter()

const loading = ref(false)
const posts = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const editDialogVisible = ref(false)
const submitting = ref(false)
const editForm = ref({
  id: null,
  content: '',
  imageUrls: ''
})

onMounted(() => {
  console.log('CommunityFeed mounted')
  loadPosts()
})

const loadPosts = async () => {
  loading.value = true
  try {
    const response = await getDynamics({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    posts.value = response.content || []
    total.value = response.totalElements || 0
  } catch (error) {
    showError('加载动态失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadPosts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadPosts()
}

const handleCreatePost = () => {
  router.push('/community/create')
}

const handleEditPost = (post) => {
  editForm.value = {
    id: post.id,
    content: post.content,
    imageUrls: post.imageUrls || ''
  }
  editDialogVisible.value = true
}

const handleUpdatePost = async () => {
  if (!editForm.value.content.trim()) {
    showWarning('请输入动态内容')
    return
  }

  submitting.value = true
  try {
    await updateDynamic(editForm.value.id, {
      content: editForm.value.content,
      imageUrls: editForm.value.imageUrls
    })
    
    showSuccess('动态更新成功')
    editDialogVisible.value = false
    loadPosts()
  } catch (error) {
    showError('更新动态失败')
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  editForm.value = {
    id: null,
    content: '',
    imageUrls: ''
  }
}
</script>

<style scoped>
.community-feed {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.feed-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.feed-content {
  min-height: 400px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .community-feed {
    padding: 12px;
  }

  .feed-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .feed-header h2 {
    font-size: 20px;
  }

  .pagination {
    overflow-x: auto;
  }

  :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
