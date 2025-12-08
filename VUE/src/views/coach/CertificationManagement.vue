<template>
  <Layout>
    <div class="cert-container">
      <div class="page-header">
        <h2>专业资格认证</h2>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon> 上传新证书
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated count="3" />
      </div>

      <!-- 证书列表 (Grid Layout) -->
      <div v-else-if="certList.length > 0" class="cert-grid">
        <el-card v-for="cert in certList" :key="cert.id" class="cert-card" shadow="hover">
          <div class="cert-image-wrapper">
            <!-- 修复了这里的属性格式 -->
            <el-image 
              :src="cert.imageUrl" 
              :preview-src-list="[cert.imageUrl]"
              fit="cover" 
              class="cert-image"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="status-badge" v-if="false">
              <el-tag :type="getStatusType(cert.status)" effect="dark">
                {{ getStatusText(cert.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="cert-info">
            <h3 class="cert-name">{{ cert.name }}</h3>
            <p class="cert-meta">
              <el-icon><OfficeBuilding /></el-icon> {{ cert.issuingAuthority || '未知机构' }}
            </p>
            <p class="cert-meta">
              <el-icon><Calendar /></el-icon> 有效期: {{ cert.expiryDate || '永久有效' }}
            </p>
          </div>

          <div class="cert-actions">
            <el-button type="primary" link @click="openEditDialog(cert)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(cert)">删除</el-button>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无认证证书，快去上传提升专业度吧！">
          <el-button type="primary" @click="openAddDialog">立即上传</el-button>
        </el-empty>
      </div>

      <!-- 添加/编辑 弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑证书' : '上传新证书'"
        width="550px"
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          
          <el-form-item label="证书名称" prop="name">
            <el-input v-model="form.name" placeholder="例如：ACE-CPT 私人教练认证" />
          </el-form-item>

          <el-form-item label="颁发机构" prop="issuingAuthority">
            <el-input v-model="form.issuingAuthority" placeholder="例如：ACE, NSCA" />
          </el-form-item>

          <el-form-item label="证书图片" prop="imageUrl">
            <el-upload
              class="cert-uploader"
              action="#"
              :http-request="handleUpload"
              :show-file-list="false"
              :before-upload="beforeUpload"
            >
              <img v-if="form.imageUrl" :src="form.imageUrl" class="uploaded-img" />
              <el-icon v-else class="uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">支持 JPG/PNG 格式，大小不超过 5MB</div>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="颁发日期" prop="issueDate">
                <el-date-picker 
                  v-model="form.issueDate" 
                  type="date" 
                  placeholder="选择日期" 
                  value-format="YYYY-MM-DD"
                  style="width: 100%" 
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="有效期至" prop="expiryDate">
                <el-date-picker 
                  v-model="form.expiryDate" 
                  type="date" 
                  placeholder="选择日期 (可选)" 
                  value-format="YYYY-MM-DD"
                  style="width: 100%" 
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm" :loading="submitting">
              {{ isEdit ? '保存修改' : '立即上传' }}
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Picture, OfficeBuilding, Calendar } from '@element-plus/icons-vue'
import Layout from '@/components/common/Layout.vue'
import { getMyCertifications, addCertification, updateCertification, deleteCertification, uploadCertImage } from '@/api/coach.js'
import { showSuccess, showError, confirmDelete } from '@/utils/feedback.js'

const loading = ref(false)
const certList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  issuingAuthority: '',
  imageUrl: '',
  issueDate: '',
  expiryDate: ''
})

const rules = {
  name: [{ required: true, message: '请输入证书名称', trigger: 'blur' }],
  issuingAuthority: [{ required: true, message: '请输入颁发机构', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传证书图片', trigger: 'change' }],
  issueDate: [{ required: true, message: '请选择颁发日期', trigger: 'change' }]
}

// 获取列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await getMyCertifications()
    certList.value = res || []
  } catch (error) {
    console.error('Fetch error:', error)
  } finally {
    loading.value = false
  }
}

// 上传相关逻辑
const beforeUpload = (file) => {
  const isImg = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImg) showError('只能上传 JPG/PNG 文件!')
  if (!isLt5M) showError('图片大小不能超过 5MB!')
  return isImg && isLt5M
}

const handleUpload = async (options) => {
  try {
    const res = await uploadCertImage(options.file)
    form.imageUrl = res // 假设后端直接返回URL字符串，如果是对象请调整为 res.url
    showSuccess('图片上传成功')
  } catch (error) {
    showError('图片上传失败')
  }
}

// 表单操作
const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', issuingAuthority: '', imageUrl: '', issueDate: '', expiryDate: '' })
  dialogVisible.value = true
}

const openEditDialog = (item) => {
  isEdit.value = true
  Object.assign(form, item)
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateCertification(form)
          showSuccess('修改成功')
        } else {
          await addCertification(form)
          showSuccess('上传成功')
        }
        dialogVisible.value = false
        fetchList()
      } catch (error) {
        showError('操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleDelete = async (item) => {
  try {
    await confirmDelete(`证书 "${item.name}"`)
    await deleteCertification(item.id)
    showSuccess('删除成功')
    fetchList()
  } catch (err) {
    // cancelled
  }
}

// 辅助函数
const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '已认证', 2: '审核未通过' }
  return map[status] || '未知'
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.cert-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.cert-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.cert-card {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s;
}

.cert-card:hover {
  transform: translateY(-5px);
}

.cert-image-wrapper {
  position: relative;
  height: 200px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.cert-image {
  width: 100%;
  height: 100%;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 30px;
}

.cert-info {
  padding: 15px 0;
}

.cert-name {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.cert-meta {
  margin: 5px 0;
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.cert-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
}

/* 上传组件样式 */
.cert-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: border-color 0.3s;
}

.cert-uploader:hover {
  border-color: #409EFF;
}

.uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.uploaded-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.loading-container {
  padding: 40px;
}

.empty-state {
  padding: 60px 0;
}

@media (max-width: 768px) {
  .cert-grid {
    grid-template-columns: 1fr;
  }
}
</style>