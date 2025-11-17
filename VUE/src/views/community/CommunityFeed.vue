<template>
  <div class="community-feed">
    <div class="feed-header">
      <h2>Community</h2>
      <el-button type="primary" @click="handleCreatePost">
        <el-icon><edit /></el-icon>
        Create Post
      </el-button>
    </div>

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

      <el-empty
        v-else-if="!loading && posts.length === 0"
        description="No posts yet. Be the first to share!"
      >
        <el-button type="primary" @click="handleCreatePost">Create Post</el-button>
      </el-empty>
    </div>

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

    <!-- Edit Post Dialog -->
    <el-dialog
      v-model="editDialogVisible"
      title="Edit Post"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="editForm" label-position="top">
        <el-form-item label="Content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="6"
            placeholder="Share your fitness journey..."
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitting" @click="handleUpdatePost">
          Update
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
    showError('Failed to load posts')
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
    showWarning('Please enter post content')
    return
  }

  submitting.value = true
  try {
    await updateDynamic(editForm.value.id, {
      content: editForm.value.content,
      imageUrls: editForm.value.imageUrls
    })
    
    showSuccess('Post updated successfully')
    editDialogVisible.value = false
    loadPosts()
  } catch (error) {
    showError('Failed to update post')
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
