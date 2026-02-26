<template>
  <div class="admin-routes-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>路线管理</span>
          <el-button type="primary" size="small" @click="openCreateDialog">
            新增路线
          </el-button>
        </div>
      </template>
      
      <el-table :data="routes" stripe>
        <el-table-column prop="id" label="路线ID" width="80" />
        <el-table-column prop="name" label="路线名称" />
        <el-table-column prop="durationDays" label="天数" width="80" />
        <el-table-column prop="totalPrice" label="总价格" width="100" />
        <el-table-column label="项目数" width="80">
          <template #default="{ row }">
            {{ row.items ? row.items.length : 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editRoute(row)">编辑</el-button>
            <el-button type="info" size="small" @click="manageItems(row)">管理项目</el-button>
            <el-button type="danger" size="small" @click="deleteRoute(row.id)">删除</el-button>
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
        @current-change="loadRoutes"
        @size-change="loadRoutes"
      />
    </el-card>

    <!-- 创建/编辑路线对话框 -->
    <el-dialog 
      v-model="showCreateDialog" 
      :title="editingRoute ? '编辑路线' : '新增路线'" 
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="路线名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入路线名称" />
        </el-form-item>
        <el-form-item label="天数" prop="durationDays">
          <el-input-number v-model="formData.durationDays" :min="1" />
        </el-form-item>
        <el-form-item label="总价格" prop="totalPrice">
          <el-input-number v-model="formData.totalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="路线描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            rows="4"
            placeholder="请输入路线描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 管理路线项目对话框 -->
    <el-dialog 
      v-model="showItemsDialog" 
      title="管理路线项目" 
      width="700px"
    >
      <div v-if="selectedRoute">
        <p style="margin-bottom: 15px"><strong>路线: {{ selectedRoute.name }}</strong></p>
        
        <!-- 路线项目列表 -->
        <el-table :data="routeItems" stripe style="margin-bottom: 20px">
          <el-table-column prop="dayNumber" label="第几天" width="80" />
          <el-table-column prop="itemType" label="项目类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.itemType === 'attraction' ? 'success' : 'info'">
                {{ row.itemType === 'attraction' ? '景点' : '酒店' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="itemId" label="项目ID" width="80" />
          <el-table-column prop="sequence" label="顺序" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="removeItem(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加项目表单 -->
        <el-divider />
        <p style="margin-bottom: 15px"><strong>添加项目</strong></p>
        <el-form :model="newItemData" label-width="100px">
          <el-form-item label="第几天">
            <el-input-number v-model="newItemData.dayNumber" :min="1" />
          </el-form-item>
          <el-form-item label="项目类型">
            <el-select v-model="newItemData.itemType" placeholder="选择项目类型">
              <el-option label="景点" value="attraction" />
              <el-option label="酒店" value="hotel" />
            </el-select>
          </el-form-item>
          <el-form-item label="项目ID">
            <el-input-number v-model="newItemData.itemId" :min="1" />
          </el-form-item>
          <el-form-item label="顺序">
            <el-input-number v-model="newItemData.sequence" :min="0" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addItem">添加项目</el-button>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showItemsDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const routes = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const showCreateDialog = ref(false)
const showItemsDialog = ref(false)
const selectedRoute = ref(null)
const routeItems = ref([])
const loading = ref(false)
const formRef = ref(null)
const editingRoute = ref(null)

const formData = ref({
  name: '',
  description: '',
  durationDays: 1,
  totalPrice: 0
})

const newItemData = ref({
  dayNumber: 1,
  itemType: 'attraction',
  itemId: null,
  sequence: 0
})

const formRules = {
  name: [
    { required: true, message: '路线名称不能为空', trigger: 'blur' }
  ],
  durationDays: [
    { required: true, message: '天数不能为空', trigger: 'blur' }
  ],
  totalPrice: [
    { required: true, message: '总价格不能为空', trigger: 'blur' }
  ]
}

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 打开创建对话框
 */
const openCreateDialog = () => {
  editingRoute.value = null
  resetForm()
  showCreateDialog.value = true
}

/**
 * 加载路线列表
 */
const loadRoutes = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/routes/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    )
    const data = await response.json()

    if (data.code === '0') {
      routes.value = data.data.routes
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载路线列表失败')
    }
  } catch (error) {
    ElMessage.error('加载路线列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 编辑路线
 */
const editRoute = (route) => {
  editingRoute.value = route
  formData.value = {
    name: route.name,
    description: route.description,
    durationDays: route.durationDays,
    totalPrice: route.totalPrice
  }
  showCreateDialog.value = true
}

/**
 * 提交表单（创建或编辑路线）
 */
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const url = editingRoute.value
        ? `${API_BASE_URL}/routes/admin/${editingRoute.value.id}`
        : `${API_BASE_URL}/routes/admin/create`
      
      const method = editingRoute.value ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData.value)
      })
      const data = await response.json()

      if (data.code === '0') {
        ElMessage.success(editingRoute.value ? '路线编辑成功' : '路线创建成功')
        showCreateDialog.value = false
        editingRoute.value = null
        resetForm()
        loadRoutes()
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
    description: '',
    durationDays: 1,
    totalPrice: 0
  }
}

/**
 * 删除路线
 */
const deleteRoute = (routeId) => {
  ElMessageBox.confirm('确定要删除该路线吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/routes/admin/${routeId}`,
          {
            method: 'DELETE'
          }
        )
        const data = await response.json()

        if (data.code === '0') {
          ElMessage.success('路线删除成功')
          loadRoutes()
        } else {
          ElMessage.error(data.message || '删除路线失败')
        }
      } catch (error) {
        ElMessage.error('删除路线失败: ' + error.message)
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

/**
 * 管理路线项目
 */
const manageItems = async (route) => {
  selectedRoute.value = route
  await loadRouteItems(route.id)
  showItemsDialog.value = true
}

/**
 * 加载路线项目
 */
const loadRouteItems = async (routeId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/routes/${routeId}/items`)
    const data = await response.json()

    if (data.code === '0') {
      routeItems.value = data.data.items
    } else {
      ElMessage.error(data.message || '加载路线项目失败')
    }
  } catch (error) {
    ElMessage.error('加载路线项目失败: ' + error.message)
  }
}

/**
 * 添加路线项目
 */
const addItem = async () => {
  if (!newItemData.value.itemId) {
    ElMessage.warning('请输入项目ID')
    return
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/routes/admin/${selectedRoute.value.id}/items`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newItemData.value)
      }
    )
    const data = await response.json()

    if (data.code === '0') {
      ElMessage.success('项目添加成功')
      resetNewItemForm()
      await loadRouteItems(selectedRoute.value.id)
    } else {
      ElMessage.error(data.message || '添加项目失败')
    }
  } catch (error) {
    ElMessage.error('添加项目失败: ' + error.message)
  }
}

/**
 * 删除路线项目
 */
const removeItem = async (itemId) => {
  ElMessageBox.confirm('确定要删除该项目吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/routes/admin/items/${itemId}`,
          {
            method: 'DELETE'
          }
        )
        const data = await response.json()

        if (data.code === '0') {
          ElMessage.success('项目删除成功')
          await loadRouteItems(selectedRoute.value.id)
        } else {
          ElMessage.error(data.message || '删除项目失败')
        }
      } catch (error) {
        ElMessage.error('删除项目失败: ' + error.message)
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

/**
 * 重置新项目表单
 */
const resetNewItemForm = () => {
  newItemData.value = {
    dayNumber: 1,
    itemType: 'attraction',
    itemId: null,
    sequence: 0
  }
}

// 页面加载时获取路线列表
onMounted(() => {
  loadRoutes()
})
</script>

<style scoped>
.admin-routes-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
