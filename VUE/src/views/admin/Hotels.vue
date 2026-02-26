<template>
  <div class="admin-hotels-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>酒店管理</span>
          <el-button type="primary" size="small" @click="openCreateDialog">
            新增酒店
          </el-button>
        </div>
      </template>
      
      <el-table :data="hotels" stripe>
        <el-table-column prop="id" label="酒店ID" width="80" />
        <el-table-column prop="name" label="酒店名称" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="rating" label="评分" width="80" />
        <el-table-column label="房间类型" width="200">
          <template #default="{ row }">
            <div class="rooms-cell">
              <el-tag
                v-for="room in row.rooms"
                :key="room.id"
                closable
                @close="deleteRoom(room.id)"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ room.roomType }} (¥{{ room.pricePerNight }}/晚)
              </el-tag>
              <el-button
                type="primary"
                size="small"
                text
                @click="showRoomDialog(row)"
              >
                + 添加房间
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editHotel(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteHotel(row.id)">删除</el-button>
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
        @current-change="loadHotels"
        @size-change="loadHotels"
      />
    </el-card>

    <!-- 添加房间对话框 -->
    <el-dialog v-model="roomDialogVisible" title="添加房间" width="400px">
      <div v-if="selectedHotel">
        <p style="margin-bottom: 15px">酒店: {{ selectedHotel.name }}</p>
        <el-form :model="roomFormData" label-width="100px">
          <el-form-item label="房间类型">
            <el-input v-model="roomFormData.roomType" placeholder="例如: 单人间、双人间" />
          </el-form-item>
          <el-form-item label="每晚价格">
            <el-input-number v-model="roomFormData.pricePerNight" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="房间数量">
            <el-input-number v-model="roomFormData.quantity" :min="1" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addRoom">确定</el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑酒店对话框 -->
    <el-dialog 
      v-model="showCreateDialog" 
      :title="editingHotel ? '编辑酒店' : '新增酒店'" 
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="酒店名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入酒店位置" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input-number v-model="formData.rating" :min="0" :max="5" :precision="1" />
        </el-form-item>
        <el-form-item label="酒店描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            rows="4"
            placeholder="请输入酒店描述"
          />
        </el-form-item>
        <el-form-item label="酒店图片" prop="imageUrl">
          <el-input v-model="formData.imageUrl" placeholder="请输入酒店图片URL" />
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

const hotels = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const roomDialogVisible = ref(false)
const showCreateDialog = ref(false)
const selectedHotel = ref(null)
const loading = ref(false)
const formRef = ref(null)
const editingHotel = ref(null)

const formData = ref({
  name: '',
  location: '',
  rating: 0,
  description: '',
  imageUrl: ''
})

const roomFormData = ref({
  roomType: '',
  pricePerNight: 0,
  quantity: 1
})

const formRules = {
  name: [
    { required: true, message: '酒店名称不能为空', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '位置不能为空', trigger: 'blur' }
  ],
  rating: [
    { required: true, message: '评分不能为空', trigger: 'blur' }
  ]
}

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 打开创建对话框
 */
const openCreateDialog = () => {
  editingHotel.value = null
  resetForm()
  showCreateDialog.value = true
}

/**
 * 加载酒店列表
 */
const loadHotels = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `${API_BASE_URL}/hotels/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    )
    const data = await response.json()

    if (data.code === '0') {
      hotels.value = data.data.hotels
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '加载酒店列表失败')
    }
  } catch (error) {
    ElMessage.error('加载酒店列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 显示房间添加对话框
 */
const showRoomDialog = (hotel) => {
  selectedHotel.value = hotel
  roomFormData.value = {
    roomType: '',
    pricePerNight: 0,
    quantity: 1
  }
  roomDialogVisible.value = true
}

/**
 * 添加房间
 */
const addRoom = async () => {
  if (!roomFormData.value.roomType) {
    ElMessage.warning('请输入房间类型')
    return
  }
  if (roomFormData.value.pricePerNight <= 0) {
    ElMessage.warning('每晚价格必须大于0')
    return
  }
  if (roomFormData.value.quantity <= 0) {
    ElMessage.warning('房间数量必须大于0')
    return
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/hotels/admin/${selectedHotel.value.id}/rooms`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(roomFormData.value)
      }
    )
    const data = await response.json()

    if (data.code === '0') {
      ElMessage.success('房间添加成功')
      roomDialogVisible.value = false
      loadHotels()
    } else {
      ElMessage.error(data.message || '添加房间失败')
    }
  } catch (error) {
    ElMessage.error('添加房间失败: ' + error.message)
  }
}

/**
 * 删除房间
 */
const deleteRoom = async (roomId) => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/hotels/admin/rooms/${roomId}`,
      {
        method: 'DELETE'
      }
    )
    const data = await response.json()

    if (data.code === '0') {
      ElMessage.success('房间删除成功')
      loadHotels()
    } else {
      ElMessage.error(data.message || '删除房间失败')
    }
  } catch (error) {
    ElMessage.error('删除房间失败: ' + error.message)
  }
}

/**
 * 编辑酒店
 */
const editHotel = (hotel) => {
  editingHotel.value = hotel
  formData.value = {
    name: hotel.name,
    location: hotel.location,
    rating: hotel.rating,
    description: hotel.description,
    imageUrl: hotel.imageUrl
  }
  showCreateDialog.value = true
}

/**
 * 提交表单（创建或编辑酒店）
 */
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const url = editingHotel.value
        ? `${API_BASE_URL}/hotels/admin/${editingHotel.value.id}`
        : `${API_BASE_URL}/hotels/admin/create`
      
      const method = editingHotel.value ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData.value)
      })
      const data = await response.json()

      if (data.code === '0') {
        ElMessage.success(editingHotel.value ? '酒店编辑成功' : '酒店创建成功')
        showCreateDialog.value = false
        editingHotel.value = null
        resetForm()
        loadHotels()
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
    rating: 0,
    description: '',
    imageUrl: ''
  }
}

/**
 * 删除酒店
 */
const deleteHotel = (hotelId) => {
  ElMessageBox.confirm('确定要删除该酒店吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/hotels/admin/${hotelId}`,
          {
            method: 'DELETE'
          }
        )
        const data = await response.json()

        if (data.code === '0') {
          ElMessage.success('酒店删除成功')
          loadHotels()
        } else {
          ElMessage.error(data.message || '删除酒店失败')
        }
      } catch (error) {
        ElMessage.error('删除酒店失败: ' + error.message)
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 页面加载时获取酒店列表
onMounted(() => {
  loadHotels()
})
</script>

<style scoped>
.admin-hotels-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rooms-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}
</style>
