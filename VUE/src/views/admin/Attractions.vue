<template>
  <div class="admin-attractions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>景点管理</span>
          <el-button type="primary" size="small" @click="openCreateDialog">
            新增景点
          </el-button>
        </div>
      </template>
      
      <el-table :data="attractions" stripe>
        <el-table-column prop="id" label="景点ID" width="80" />
        <el-table-column prop="name" label="景点名称" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="ticketPrice" label="门票价格" />
        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <div class="tags-cell">
              <el-tag
                v-for="tag in row.tags"
                :key="tag"
                closable
                @close="removeTag(row.id, tag)"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ tag }}
              </el-tag>
              <el-button
                type="primary"
                size="small"
                text
                @click="showTagDialog(row)"
              >
                + 添加标签
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="isGuangzhouSpecial" label="广州特色">
          <template #default="{ row }">
            <el-tag :type="row.isGuangzhouSpecial ? 'success' : 'info'">
              {{ row.isGuangzhouSpecial ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editAttraction(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteAttraction(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
        @current-change="loadAttractions"
        @size-change="loadAttractions"
      />
    </el-card>

    <!-- 添加标签对话框 -->
    <el-dialog v-model="tagDialogVisible" title="添加标签" width="400px">
      <div v-if="selectedAttraction">
        <p style="margin-bottom: 15px">景点: {{ selectedAttraction.name }}</p>
        <el-select
          v-model="newTag"
          placeholder="选择或输入标签"
          filterable
          allow-create
          default-first-option
          style="width: 100%; margin-bottom: 15px"
        >
          <el-option label="美食" value="美食" />
          <el-option label="文化" value="文化" />
          <el-option label="历史" value="历史" />
          <el-option label="现代" value="现代" />
          <el-option label="建筑" value="建筑" />
          <el-option label="公园" value="公园" />
          <el-option label="休闲" value="休闲" />
          <el-option label="自然" value="自然" />
          <el-option label="生态" value="生态" />
          <el-option label="动物" value="动物" />
          <el-option label="家庭" value="家庭" />
          <el-option label="夜景" value="夜景" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addTag">确定</el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑景点对话框 -->
    <el-dialog 
      v-model="showCreateDialog" 
      :title="editingAttraction ? '编辑景点' : '新增景点'" 
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入景点位置" />
        </el-form-item>
        <el-form-item label="门票价格" prop="ticketPrice">
          <el-input-number v-model="formData.ticketPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="营业时间" prop="openingHours">
          <el-input v-model="formData.openingHours" placeholder="例如: 09:00-17:00" />
        </el-form-item>
        <el-form-item label="景点描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            rows="4"
            placeholder="请输入景点描述"
          />
        </el-form-item>
        <el-form-item label="景点图片" prop="imageUrl">
          <el-input v-model="formData.imageUrl" placeholder="请输入景点图片URL" />
        </el-form-item>
        <el-form-item label="广州特色" prop="isGuangzhouSpecial">
          <el-switch v-model="formData.isGuangzhouSpecial" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const attractions = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const tagDialogVisible = ref(false)
const showCreateDialog = ref(false)
const selectedAttraction = ref(null)
const newTag = ref('')
const loading = ref(false)
const formRef = ref(null)
const editingAttraction = ref(null)

const formData = ref({
  name: '',
  location: '',
  ticketPrice: 0,
  openingHours: '',
  description: '',
  imageUrl: '',
  isGuangzhouSpecial: false
})

const formRules = {
  name: [
    { required: true, message: '景点名称不能为空', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '位置不能为空', trigger: 'blur' }
  ],
  ticketPrice: [
    { required: true, message: '门票价格不能为空', trigger: 'blur' }
  ],
  openingHours: [
    { required: true, message: '营业时间不能为空', trigger: 'blur' }
  ]
}

const API_BASE_URL = 'http://localhost:8080'

/**
 * 打开创建对话框
 */
const openCreateDialog = () => {
  editingAttraction.value = null
  resetForm()
  showCreateDialog.value = true
}

/**
 * 加载景点列表
 */
const loadAttractions = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/attractions/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    )
    const data = await response.json()

    if (data.code === 0) {
      attractions.value = data.data.attractions
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载景点列表失败')
    }
  } catch (error) {
    ElMessage.error('加载景点列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 显示标签添加对话框
 */
const showTagDialog = (attraction) => {
  selectedAttraction.value = attraction
  newTag.value = ''
  tagDialogVisible.value = true
}

/**
 * 添加标签
 */
const addTag = async () => {
  if (!newTag.value) {
    ElMessage.warning('请选择或输入标签')
    return
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/attractions/admin/${selectedAttraction.value.id}/tags`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ tagName: newTag.value })
      }
    )
    const data = await response.json()

    if (data.code === 0) {
      ElMessage.success('标签添加成功')
      tagDialogVisible.value = false
      loadAttractions()
    } else {
      ElMessage.error(data.message || '添加标签失败')
    }
  } catch (error) {
    ElMessage.error('添加标签失败: ' + error.message)
  }
}

/**
 * 删除标签
 */
const removeTag = async (attractionId, tagName) => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/attractions/admin/${attractionId}/tags/${tagName}`,
      {
        method: 'DELETE'
      }
    )
    const data = await response.json()

    if (data.code === 0) {
      ElMessage.success('标签删除成功')
      loadAttractions()
    } else {
      ElMessage.error(data.message || '删除标签失败')
    }
  } catch (error) {
    ElMessage.error('删除标签失败: ' + error.message)
  }
}

/**
 * 编辑景点
 */
const editAttraction = (attraction) => {
  editingAttraction.value = attraction
  formData.value = {
    name: attraction.name,
    location: attraction.location,
    ticketPrice: attraction.ticketPrice,
    openingHours: attraction.openingHours,
    description: attraction.description,
    imageUrl: attraction.imageUrl,
    isGuangzhouSpecial: attraction.isGuangzhouSpecial
  }
  showCreateDialog.value = true
}

/**
 * 提交表单（创建或编辑景点）
 */
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const url = editingAttraction.value
        ? `${API_BASE_URL}/attractions/admin/${editingAttraction.value.id}`
        : `${API_BASE_URL}/attractions/admin/create`
      
      const method = editingAttraction.value ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData.value)
      })
      const data = await response.json()

      if (data.code === 0) {
        ElMessage.success(editingAttraction.value ? '景点编辑成功' : '景点创建成功')
        showCreateDialog.value = false
        editingAttraction.value = null
        resetForm()
        loadAttractions()
      } else {
        ElMessage.error(data.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败: ' + error.message)
    }
  })
}

/**
 * 重置表单
 */
const resetForm = () => {
  formData.value = {
    name: '',
    location: '',
    ticketPrice: 0,
    openingHours: '',
    description: '',
    imageUrl: '',
    isGuangzhouSpecial: false
  }
}

/**
 * 删除景点
 */
const deleteAttraction = (attractionId) => {
  ElMessageBox.confirm('确定要删除该景点吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/attractions/admin/${attractionId}`,
          {
            method: 'DELETE'
          }
        )
        const data = await response.json()

        if (data.code === 0) {
          ElMessage.success('景点删除成功')
          loadAttractions()
        } else {
          ElMessage.error(data.message || '删除景点失败')
        }
      } catch (error) {
        ElMessage.error('删除景点失败: ' + error.message)
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 页面加载时获取景点列表
onMounted(() => {
  loadAttractions()
})
</script>

<style scoped>
.admin-attractions-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}
</style>
