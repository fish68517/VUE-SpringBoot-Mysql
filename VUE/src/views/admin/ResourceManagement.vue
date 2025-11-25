
<template>
  <div class="resource-management">
    <h1>资源管理</h1>

    <!-- 操作栏 (Action Bar) -->
    <el-card class="action-card">
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        创建资源
      </el-button>
    </el-card>

    <!-- 资源列表表格 (Resources Table) -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="resources"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="title" label="标题" min-width="200" />

        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.contentType)">
              {{ formatContentType(row.contentType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建者" width="150">
          <template #default="{ row }">
            <span v-if="row.creatorName">{{ row.creatorName }}</span>
            <span v-else class="text-muted">系统</span>
          </template>
        </el-table-column>

        <el-table-column label="浏览量" width="120" align="center">
          <template #default="{ row }">
            <el-icon><View /></el-icon>
            {{ row.viewCount || 0 }}
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              link
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              link
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 (Empty State) -->
      <div v-if="!loading && resources.length === 0" class="empty-state">
        <el-empty description="暂无资源" />
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

    <!-- 创建/编辑资源弹窗 (Create/Edit Resource Dialog) -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑资源' : '创建资源'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入资源标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容类型" prop="contentType">
          <el-select
            v-model="form.contentType"
            placeholder="请选择内容类型"
            style="width: 100%"
            @change="handleContentTypeChange"
          >
            <el-option label="视频" value="video" />
            <el-option label="文章" value="article" />
            <el-option label="文档" value="document" />
          </el-select>
        </el-form-item>

        <!-- 视频/文档文件上传 (File Upload for Video/Document) -->
        <el-form-item
          v-if="form.contentType === 'video' || form.contentType === 'document'"
          label="文件"
          prop="fileUrl"
        >
          <el-upload
            class="upload-component"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :limit="1"
            :file-list="fileList"
            :on-remove="handleFileRemove"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              上传 {{ formatContentType(form.contentType) }}
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                <span v-if="form.contentType === 'video'">
                  MP4, AVI 格式，最大 100MB
                </span>
                <span v-else>
                  PDF, DOC, DOCX 格式，最大 10MB
                </span>
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <!-- 文章内容编辑器 (Text Editor for Article) -->
        <el-form-item
          v-if="form.contentType === 'article'"
          label="内容"
          prop="content"
        >
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入文章内容"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Plus, Edit, Delete, View, Upload } from '@element-plus/icons-vue';
import { getResources, createResource, updateResource, deleteResource } from '@/api/resource';
import { getToken } from '@/utils/auth';
import { showSuccess, showError, confirmDelete } from '@/utils/feedback';

const loading = ref(false);
const saving = ref(false);
const resources = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);

const dialogVisible = ref(false);
const isEditing = ref(false);
const formRef = ref(null);
const fileList = ref([]);

const form = ref({
  id: null,
  title: '',
  description: '',
  contentType: '',
  fileUrl: '',
  content: ''
});

const formRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过 200 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: '描述不能超过 1000 个字符', trigger: 'blur' }
  ],
  contentType: [
    { required: true, message: '请选择内容类型', trigger: 'change' }
  ],
  fileUrl: [
    {
      validator: (rule, value, callback) => {
        if ((form.value.contentType === 'video' || form.value.contentType === 'document') && !value) {
          callback(new Error('请上传文件'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
  content: [
    {
      validator: (rule, value, callback) => {
        if (form.value.contentType === 'article' && !value) {
          callback(new Error('请输入文章内容'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const uploadUrl = computed(() => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  return form.value.contentType === 'video'
    ? `${baseUrl}/api/upload/video`
    : `${baseUrl}/api/upload/image`;
});

const uploadHeaders = computed(() => {
  return {
    Authorization: `Bearer ${getToken()}`
  };
});

const fetchResources = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };

    const data = await getResources(params);
    
    if (data.content) {
      resources.value = data.content;
      total.value = data.totalElements || data.content.length;
    } else if (Array.isArray(data)) {
      resources.value = data;
      total.value = data.length;
    } else {
      resources.value = [];
      total.value = 0;
    }
  } catch (error) {
    showError('加载资源失败');
    resources.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchResources();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchResources();
};

const getTypeTagType = (type) => {
  const types = {
    video: 'success',
    article: 'primary',
    document: 'warning'
  };
  return types[type] || 'info';
};

const formatContentType = (type) => {
  const map = {
    video: '视频',
    article: '文章',
    document: '文档'
  };
  return map[type] || type;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

const handleCreate = () => {
  isEditing.value = false;
  dialogVisible.value = true;
};

const handleEdit = (resource) => {
  isEditing.value = true;
  form.value = {
    id: resource.id,
    title: resource.title,
    description: resource.description || '',
    contentType: resource.contentType,
    fileUrl: resource.fileUrl || '',
    content: resource.content || ''
  };

  if (resource.fileUrl) {
    fileList.value = [{
      name: resource.title,
      url: resource.fileUrl
    }];
  }

  dialogVisible.value = true;
};

const handleContentTypeChange = () => {
  form.value.fileUrl = '';
  form.value.content = '';
  fileList.value = [];
};

const beforeUpload = (file) => {
  const isVideo = form.value.contentType === 'video';
  const maxSize = isVideo ? 100 * 1024 * 1024 : 10 * 1024 * 1024;

  if (file.size > maxSize) {
    showError(`文件大小不能超过 ${isVideo ? '100MB' : '10MB'}`);
    return false;
  }

  return true;
};

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.fileUrl = response.data;
    showSuccess('文件上传成功');
  } else {
    showError(response.msg || '上传失败');
  }
};

const handleUploadError = () => {
  showError('文件上传失败');
};

const handleFileRemove = () => {
  form.value.fileUrl = '';
};

const handleSave = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    saving.value = true;
    try {
      const data = {
        title: form.value.title,
        description: form.value.description,
        contentType: form.value.contentType,
        fileUrl: form.value.fileUrl,
        content: form.value.content
      };

      if (isEditing.value) {
        await updateResource(form.value.id, data);
        showSuccess('资源更新成功');
      } else {
        await createResource(data);
        showSuccess('资源创建成功');
      }

      dialogVisible.value = false;
      fetchResources();
    } catch (error) {
      showError(`${isEditing.value ? '更新' : '创建'}资源失败`);
    } finally {
      saving.value = false;
    }
  });
};

const resetForm = () => {
  form.value = {
    id: null,
    title: '',
    description: '',
    contentType: '',
    fileUrl: '',
    content: ''
  };
  fileList.value = [];
  if (formRef.value) {
    formRef.value.clearValidate();
  }
};

const handleDelete = async (resource) => {
  try {
    // 这里传入中文提示，utils/feedback 内部会显示
    await confirmDelete(`资源 "${resource.title}"`);

    await deleteResource(resource.id);
    showSuccess('资源删除成功');
    
    if (resources.value.length === 1 && currentPage.value > 1) {
      currentPage.value--;
    }
    fetchResources();
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      showError('删除资源失败');
    }
  }
};

onMounted(() => {
  fetchResources();
});
</script>

<style scoped>
/* 样式保持不变 */
.resource-management {
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
  color: #303133;
}

.action-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.text-muted {
  color: #909399;
}

.empty-state {
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.upload-component {
  width: 100%;
}

@media (max-width: 768px) {
  .resource-management {
    padding: 10px;
  }
}
</style>