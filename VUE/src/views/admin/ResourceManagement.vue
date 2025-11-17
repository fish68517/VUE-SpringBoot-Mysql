<template>
  <div class="resource-management">
    <h1>Resource Management</h1>

    <!-- Action Bar -->
    <el-card class="action-card">
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        Create Resource
      </el-button>
    </el-card>

    <!-- Resources Table -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="resources"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="title" label="Title" min-width="200" />

        <el-table-column label="Type" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.contentType)">
              {{ row.contentType }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Creator" width="150">
          <template #default="{ row }">
            <span v-if="row.creatorName">{{ row.creatorName }}</span>
            <span v-else class="text-muted">System</span>
          </template>
        </el-table-column>

        <el-table-column label="View Count" width="120" align="center">
          <template #default="{ row }">
            <el-icon><View /></el-icon>
            {{ row.viewCount || 0 }}
          </template>
        </el-table-column>

        <el-table-column label="Created Date" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              link
            >
              <el-icon><Edit /></el-icon>
              Edit
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              link
            >
              <el-icon><Delete /></el-icon>
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Empty State -->
      <div v-if="!loading && resources.length === 0" class="empty-state">
        <el-empty description="No resources found" />
      </div>

      <!-- Pagination -->
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

    <!-- Create/Edit Resource Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? 'Edit Resource' : 'Create Resource'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="Title" prop="title">
          <el-input
            v-model="form.title"
            placeholder="Enter resource title"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="Description" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="Enter resource description"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="Content Type" prop="contentType">
          <el-select
            v-model="form.contentType"
            placeholder="Select content type"
            style="width: 100%"
            @change="handleContentTypeChange"
          >
            <el-option label="Video" value="video" />
            <el-option label="Article" value="article" />
            <el-option label="Document" value="document" />
          </el-select>
        </el-form-item>

        <!-- File Upload for Video/Document -->
        <el-form-item
          v-if="form.contentType === 'video' || form.contentType === 'document'"
          label="File"
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
              Upload {{ form.contentType }}
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                <span v-if="form.contentType === 'video'">
                  MP4, AVI files, max 100MB
                </span>
                <span v-else>
                  PDF, DOC, DOCX files, max 10MB
                </span>
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <!-- Text Editor for Article -->
        <el-form-item
          v-if="form.contentType === 'article'"
          label="Content"
          prop="content"
        >
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="Enter article content"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          Save
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
    { required: true, message: 'Please enter title', trigger: 'blur' },
    { max: 200, message: 'Title cannot exceed 200 characters', trigger: 'blur' }
  ],
  description: [
    { max: 1000, message: 'Description cannot exceed 1000 characters', trigger: 'blur' }
  ],
  contentType: [
    { required: true, message: 'Please select content type', trigger: 'change' }
  ],
  fileUrl: [
    {
      validator: (rule, value, callback) => {
        if ((form.value.contentType === 'video' || form.value.contentType === 'document') && !value) {
          callback(new Error('Please upload a file'));
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
          callback(new Error('Please enter article content'));
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
    showError('Failed to load resources');
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

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
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
    showError(`File size cannot exceed ${isVideo ? '100MB' : '10MB'}`);
    return false;
  }

  return true;
};

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.fileUrl = response.data;
    showSuccess('File uploaded successfully');
  } else {
    showError(response.msg || 'Upload failed');
  }
};

const handleUploadError = () => {
  showError('File upload failed');
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
        showSuccess('Resource updated successfully');
      } else {
        await createResource(data);
        showSuccess('Resource created successfully');
      }

      dialogVisible.value = false;
      fetchResources();
    } catch (error) {
      showError(`Failed to ${isEditing.value ? 'update' : 'create'} resource`);
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
    await confirmDelete(`resource "${resource.title}"`);

    await deleteResource(resource.id);
    showSuccess('Resource deleted successfully');
    
    if (resources.value.length === 1 && currentPage.value > 1) {
      currentPage.value--;
    }
    fetchResources();
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      showError('Failed to delete resource');
    }
  }
};

onMounted(() => {
  fetchResources();
});
</script>

<style scoped>
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
