
<template>
  <div class="content-moderation">
    <h1>内容审核</h1>

    <!-- 筛选栏 (Filter Bar) -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 300px"
          @change="handleFilterChange"
        />
        <el-button type="primary" @click="handleFilterChange">
          <el-icon><Search /></el-icon>
          筛选
        </el-button>
      </div>
    </el-card>

    <!-- 状态标签页 (Status Tabs) -->
    <el-card class="tabs-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待审核" name="pending">
          <el-badge :value="pendingCount" :max="99" class="tab-badge" />
        </el-tab-pane>
        <el-tab-pane label="已通过" name="approved" />
        <el-tab-pane label="已拒绝" name="rejected" />
      </el-tabs>

      <!-- 动态列表表格 (Posts Table) -->
      <el-table
        v-loading="loading"
        :data="posts"
        style="width: 100%"
        stripe
      >
        <el-table-column label="作者" width="150">
          <template #default="{ row }">
            <div class="author-cell">
              <el-avatar :src="row.userAvatar" :size="32">
                {{ row.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="author-name">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="内容" min-width="300">
          <template #default="{ row }">
            <div class="content-preview">
              {{ truncateContent(row.content) }}
            </div>
            <div v-if="row.imageUrls" class="image-count">
              <el-icon><Picture /></el-icon>
              {{ getImageCount(row.imageUrls) }} 张图片
            </div>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="info"
              size="small"
              @click="handleViewPost(row)"
              link
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              @click="handleApprove(row)"
              link
            >
              <el-icon><Check /></el-icon>
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              @click="handleReject(row)"
              link
            >
              <el-icon><Close /></el-icon>
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 (Empty State) -->
      <div v-if="!loading && posts.length === 0" class="empty-state">
        <el-empty :description="`暂无${formatStatus(activeTab)}内容`" />
      </div>

      <!-- 分页 (Pagination) -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 审核历史 (Moderation History) -->
    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span>最近审核记录</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="action in moderationHistory"
          :key="action.id"
          :timestamp="formatDate(action.timestamp)"
          placement="top"
        >
          <el-tag :type="action.action === 'approved' ? 'success' : 'danger'" size="small">
            {{ action.action === 'approved' ? '已通过' : '已拒绝' }}
          </el-tag>
          <span class="history-content">
            用户 <strong>{{ action.username }}</strong> 发布的内容
            <span v-if="action.reason"> - 原因: {{ action.reason }}</span>
          </span>
        </el-timeline-item>
      </el-timeline>
      <div v-if="moderationHistory.length === 0" class="empty-state">
        <el-empty description="暂无审核历史" :image-size="80" />
      </div>
    </el-card>

    <!-- 查看动态详情弹窗 (View Post Dialog) -->
    <el-dialog
      v-model="viewDialogVisible"
      title="动态详情"
      width="600px"
    >
      <div v-if="selectedPost" class="post-detail">
        <div class="post-author">
          <el-avatar :src="selectedPost.userAvatar" :size="50">
            {{ selectedPost.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <div class="author-info">
            <div class="author-name">{{ selectedPost.username }}</div>
            <div class="post-date">{{ formatDate(selectedPost.createdAt) }}</div>
          </div>
        </div>

        <div class="post-content">
          {{ selectedPost.content }}
        </div>

        <div v-if="selectedPost.imageUrls" class="post-images">
          <el-image
            v-for="(url, index) in getImageUrls(selectedPost.imageUrls)"
            :key="index"
            :src="url"
            :preview-src-list="getImageUrls(selectedPost.imageUrls)"
            fit="cover"
            class="post-image"
          />
        </div>

        <div class="post-stats">
          <span>
            <el-icon><Like /></el-icon>
             {{ selectedPost.likeCount || 0 }} 点赞
          </span>
          <span>
            <el-icon><ChatDotRound /></el-icon>
            {{ selectedPost.commentCount || 0 }} 评论
          </span>
        </div>
      </div>

      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button
          v-if="selectedPost?.status === 'pending'"
          type="success"
          @click="handleApprove(selectedPost)"
        >
          通过
        </el-button>
        <el-button
          v-if="selectedPost?.status === 'pending'"
          type="danger"
          @click="handleReject(selectedPost)"
        >
          拒绝
        </el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因弹窗 (Reject Reason Dialog) -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝内容"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejecting">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { showSuccess, showError } from '@/utils/feedback';
import {
  Search,
  View,
  Check,
  Close,
  Picture,
  Star,
  ChatDotRound
} from '@element-plus/icons-vue';
import { getModerationQueue, approveContent, rejectContent } from '@/api/admin';

const loading = ref(false);
const rejecting = ref(false);
const posts = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);
const activeTab = ref('pending');
const dateRange = ref(null);
const pendingCount = ref(0);

const viewDialogVisible = ref(false);
const rejectDialogVisible = ref(false);
const selectedPost = ref(null);
const rejectForm = ref({
  reason: ''
});

const moderationHistory = ref([]);

const fetchPosts = async () => {
  loading.value = true;
  try {
    const params = {
      status: activeTab.value,
      page: currentPage.value - 1,
      size: pageSize.value
    };

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().split('T')[0];
      params.endDate = dateRange.value[1].toISOString().split('T')[0];
    }

    const data = await getModerationQueue(params);
    
    if (data.content) {
      posts.value = data.content;
      total.value = data.totalElements || data.content.length;
    } else if (Array.isArray(data)) {
      posts.value = data;
      total.value = data.length;
    } else {
      posts.value = [];
      total.value = 0;
    }

    // Update pending count
    if (activeTab.value === 'pending') {
      pendingCount.value = total.value;
    }
  } catch (error) {
    showError('加载内容失败');
    posts.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const fetchModerationHistory = async () => {
  try {
    // This would be a separate API call in a real implementation
    // For now, we'll use a placeholder
    moderationHistory.value = [];
  } catch (error) {
    console.error('加载审核历史失败');
  }
};

const handleTabChange = () => {
  currentPage.value = 1;
  fetchPosts();
};

const handleFilterChange = () => {
  currentPage.value = 1;
  fetchPosts();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchPosts();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchPosts();
};

const truncateContent = (content) => {
  if (!content) return '';
  return content.length > 100 ? content.substring(0, 100) + '...' : content;
};

const getImageCount = (imageUrls) => {
  if (!imageUrls) return 0;
  return imageUrls.split(',').filter(url => url.trim()).length;
};

const getImageUrls = (imageUrls) => {
  if (!imageUrls) return [];
  return imageUrls.split(',').filter(url => url.trim());
};

const getStatusTagType = (status) => {
  const types = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  };
  return types[status] || 'info';
};

// 汉化状态文本
const formatStatus = (status) => {
  const map = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  };
  return map[status] || status;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const handleViewPost = (post) => {
  selectedPost.value = post;
  viewDialogVisible.value = true;
};

const handleApprove = async (post) => {
  try {
    await approveContent(post.id);
    showSuccess('内容已审核通过');
    
    // Add to moderation history
    moderationHistory.value.unshift({
      id: Date.now(),
      action: 'approved',
      username: post.username,
      timestamp: new Date().toISOString()
    });

    viewDialogVisible.value = false;
    fetchPosts();
  } catch (error) {
    showError('审核操作失败');
  }
};

const handleReject = (post) => {
  selectedPost.value = post;
  rejectForm.value.reason = '';
  rejectDialogVisible.value = true;
  viewDialogVisible.value = false;
};

const confirmReject = async () => {
  if (!selectedPost.value) return;

  rejecting.value = true;
  try {
    await rejectContent(selectedPost.value.id, rejectForm.value.reason);
    showSuccess('内容已拒绝');
    
    // Add to moderation history
    moderationHistory.value.unshift({
      id: Date.now(),
      action: 'rejected',
      username: selectedPost.value.username,
      reason: rejectForm.value.reason,
      timestamp: new Date().toISOString()
    });

    rejectDialogVisible.value = false;
    fetchPosts();
  } catch (error) {
    showError('操作失败');
  } finally {
    rejecting.value = false;
  }
};

onMounted(() => {
  fetchPosts();
  fetchModerationHistory();
});
</script>

<style scoped>
/* 样式保持不变 */
.content-moderation {
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.tabs-card {
  margin-bottom: 20px;
}

.tab-badge {
  margin-left: 8px;
}

.author-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 14px;
  color: #303133;
}

.content-preview {
  color: #606266;
  line-height: 1.5;
  margin-bottom: 4px;
}

.image-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}

.empty-state {
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.history-card {
  margin-top: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.history-content {
  margin-left: 8px;
  color: #606266;
}

.post-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-info {
  flex: 1;
}

.author-info .author-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.post-date {
  font-size: 12px;
  color: #909399;
}

.post-content {
  color: #303133;
  line-height: 1.6;
  white-space: pre-wrap;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
}

.post-image {
  width: 100%;
  height: 150px;
  border-radius: 8px;
  cursor: pointer;
}

.post-stats {
  display: flex;
  gap: 24px;
  color: #909399;
  font-size: 14px;
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-bar .el-date-picker,
  .filter-bar .el-button {
    width: 100% !important;
  }

  .post-images {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>