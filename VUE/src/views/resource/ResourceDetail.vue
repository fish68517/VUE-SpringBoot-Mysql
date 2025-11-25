没问题！我已经将 “资源详情” (Resource Detail) 页面全部汉化。

主要修改包括：

界面文本：按钮、标题、提示语全部改为中文。

日期格式：调整为符合中国习惯的 年-月-日 格式。

类型显示：增加了 formatContentType 函数，将后台的 video/article 转换为 视频/文章 显示。

交互提示：收藏成功/失败的弹窗提示也改为了中文。

请复制以下代码覆盖你原来的文件：

code
Html
play_circle
download
content_copy
expand_less
<template>
  <div class="resource-detail-container">
    <!-- 加载状态 (Loading State) -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40">
        <Loading />
      </el-icon>
      <p>正在加载资源...</p>
    </div>

    <!-- 资源内容 (Resource Content) -->
    <div v-else-if="resource" class="resource-content">
      <!-- 头部与返回按钮 (Header with Back Button) -->
      <div class="resource-header">
        <el-button @click="goBack" :icon="ArrowLeft">返回资源列表</el-button>
        <el-button
          :type="isCollected ? 'warning' : 'primary'"
          :icon="isCollected ? StarFilled : Star"
          @click="toggleCollection"
          :loading="collectionLoading"
        >
          {{ isCollected ? '取消收藏' : '加入收藏' }}
        </el-button>
      </div>

      <!-- 资源标题和元数据 (Resource Title and Meta) -->
      <div class="resource-title-section">
        <h1>{{ resource.title }}</h1>
        <div class="resource-meta">
          <!-- 这里使用了 formatContentType 将英文类型转中文 -->
          <el-tag :type="getTagType()" size="large">{{ formatContentType(resource.contentType) }}</el-tag>
          <div class="meta-item">
            <el-icon><View /></el-icon>
            <span>{{ resource.viewCount || 0 }} 次观看</span>
          </div>
          <div v-if="resource.creatorName" class="meta-item">
            <el-icon><User /></el-icon>
            <span>{{ resource.creatorName }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>{{ formatDate(resource.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 资源简介 (Resource Description) -->
      <div v-if="resource.description" class="resource-description">
        <h3>资源简介</h3>
        <p>{{ resource.description }}</p>
      </div>

      <!-- 根据类型显示主要内容 (Resource Content Based on Type) -->
      <div class="resource-main-content">
        <!-- 视频内容 (Video Content) -->
        <div v-if="resource.contentType?.toLowerCase() === 'video'" class="video-container">
          <video
            v-if="resource.fileUrl"
            :src="getFileUrl(resource.fileUrl)"
            controls
            class="video-player"
          >
            您的浏览器不支持视频播放。
          </video>
          <el-empty v-else description="视频文件不可用" />
        </div>

        <!-- 文档内容 (Document Content) -->
        <div v-else-if="resource.contentType?.toLowerCase() === 'document'" class="document-container">
          <el-card>
            <div class="document-info">
              <el-icon :size="64"><Document /></el-icon>
              <h3>文档附件</h3>
              <p v-if="resource.fileUrl">点击下方按钮下载文档</p>
              <el-button
                v-if="resource.fileUrl"
                type="primary"
                :icon="Download"
                @click="downloadFile"
              >
                下载文档
              </el-button>
              <el-empty v-else description="文档文件不可用" />
            </div>
          </el-card>
        </div>

        <!-- 文章内容 (Article Content) -->
        <div v-else-if="resource.contentType?.toLowerCase() === 'article'" class="article-container">
          <el-card>
            <div class="article-content" v-html="sanitizedContent"></div>
            <el-empty v-if="!resource.content" description="暂无文章内容" />
          </el-card>
        </div>

        <!-- 未知类型 (Unknown Type) -->
        <div v-else class="unknown-type">
          <el-empty description="不支持的内容类型" />
        </div>
      </div>

      <!-- 创作者信息 (Creator Information) -->
      <div v-if="resource.creatorName && resource.creatorRole === 'coach'" class="creator-section">
        <el-card>
          <h3>关于创作者</h3>
          <div class="creator-info">
            <el-icon :size="48"><UserFilled /></el-icon>
            <div>
              <h4>{{ resource.creatorName }}</h4>
              <p>专业教练</p>
              <el-button
                type="primary"
                size="small"
                @click="viewCoachProfile"
              >
                查看教练主页
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 错误状态 (Error State) -->
    <el-empty
      v-else
      description="未找到该资源"
      :image-size="200"
    >
      <el-button type="primary" @click="goBack">返回资源列表</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  ArrowLeft,
  Star,
  StarFilled,
  View,
  User,
  Clock,
  Document,
  Download,
  UserFilled,
  Loading
} from '@element-plus/icons-vue';
import { getResourceById } from '@/api/resource';
import { getCollections, addCollection, removeCollection } from '@/api/collection';
import { showSuccess, showError } from '@/utils/feedback';

const route = useRoute();
const router = useRouter();

const resource = ref(null);
const loading = ref(false);
const isCollected = ref(false);
const collectionLoading = ref(false);

const sanitizedContent = computed(() => {
  if (!resource.value?.content) return '';
  // Basic sanitization - in production, use a library like DOMPurify
  return resource.value.content
    .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
    .replace(/on\w+="[^"]*"/g, '');
});

const getTagType = () => {
  const type = resource.value?.contentType?.toLowerCase();
  switch (type) {
    case 'video':
      return 'primary';
    case 'document':
      return 'success';
    case 'article':
      return 'warning';
    default:
      return 'info';
  }
};

// 新增：转换内容类型为中文显示
const formatContentType = (type) => {
  if (!type) return '';
  const map = {
    video: '视频',
    article: '文章',
    document: '文档'
  };
  return map[type.toLowerCase()] || '其他';
};

const getFileUrl = (url) => {
  if (!url) return '';
  // If URL is relative, prepend the API base URL
  if (url.startsWith('/')) {
    return import.meta.env.VITE_API_BASE_URL.replace('/api', '') + url;
  }
  return url;
};

// 修改：使用中文日期格式
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

const fetchResource = async () => {
  loading.value = true;
  try {
    const id = route.params.id;
    resource.value = await getResourceById(id);
    await checkIfCollected();
  } catch (error) {
    console.error('Failed to fetch resource:', error);
    showError('加载资源详情失败');
  } finally {
    loading.value = false;
  }
};

const checkIfCollected = async () => {
  try {
    const collections = await getCollections();
    isCollected.value = collections.some(
      (collection) => collection.resourceId === resource.value.id
    );
  } catch (error) {
    console.error('Failed to check collection status:', error);
    isCollected.value = false;
  }
};

const toggleCollection = async () => {
  collectionLoading.value = true;
  try {
    if (isCollected.value) {
      await removeCollection(resource.value.id);
      isCollected.value = false;
      showSuccess('已取消收藏');
    } else {
      await addCollection(resource.value.id);
      isCollected.value = true;
      showSuccess('已加入收藏');
    }
  } catch (error) {
    console.error('Failed to toggle collection:', error);
    showError('更新收藏状态失败');
  } finally {
    collectionLoading.value = false;
  }
};

const downloadFile = () => {
  if (!resource.value?.fileUrl) return;
  const url = getFileUrl(resource.value.fileUrl);
  window.open(url, '_blank');
};

const viewCoachProfile = () => {
  if (resource.value?.creatorId) {
    router.push(`/coaches/${resource.value.creatorId}`);
  }
};

const goBack = () => {
  router.push('/resources');
};

onMounted(() => {
  fetchResource();
});
</script>

<style scoped>
/* 样式保持不变 */
.resource-detail-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #909399;
}

.loading-container p {
  margin-top: 16px;
  font-size: 16px;
}

.resource-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.resource-title-section h1 {
  margin: 0 0 16px 0;
  font-size: 32px;
  font-weight: 600;
  color: #303133;
}

.resource-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.resource-description {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.resource-description h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #303133;
}

.resource-description p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.resource-main-content {
  margin: 24px 0;
}

.video-container {
  width: 100%;
}

.video-player {
  width: 100%;
  max-height: 600px;
  border-radius: 8px;
  background-color: #000;
}

.document-container,
.article-container {
  margin: 24px 0;
}

.document-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 40px 20px;
  text-align: center;
}

.document-info h3 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.document-info p {
  margin: 0;
  color: #606266;
}

.article-content {
  line-height: 1.8;
  font-size: 16px;
  color: #303133;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin-top: 24px;
  margin-bottom: 12px;
  color: #303133;
}

.article-content :deep(p) {
  margin-bottom: 16px;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.creator-section {
  margin-top: 32px;
}

.creator-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #303133;
}

.creator-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.creator-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #303133;
}

.creator-info p {
  margin: 0 0 12px 0;
  color: #909399;
  font-size: 14px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .resource-detail-container {
    padding: 16px;
  }

  .resource-title-section h1 {
    font-size: 24px;
  }

  .resource-header {
    flex-direction: column;
    align-items: stretch;
  }

  .resource-header .el-button {
    width: 100%;
  }

  .video-player {
    max-height: 300px;
  }

  .creator-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>