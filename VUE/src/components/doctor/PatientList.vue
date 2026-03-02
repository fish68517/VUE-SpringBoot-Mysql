<template>
  <div class="patient-list-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ $t('menu.patientList') || '患者列表' }}</h2>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-filter-area">
      <el-input
        v-model="searchQuery"
        :placeholder="$t('common.search') || '搜索患者名称或用户名'"
        clearable
        class="search-input"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>

      <!-- 筛选选项 -->
      <div class="filter-options">
        <el-select
          v-model="filterGender"
          :placeholder="$t('user.gender') || '性别'"
          clearable
          class="filter-select"
          @change="handleFilter"
        >
          <el-option label="男" value="MALE" />
          <el-option label="女" value="FEMALE" />
          <el-option label="其他" value="OTHER" />
        </el-select>

        <el-button type="primary" @click="resetFilters" class="reset-btn">
          {{ $t('common.reset') || '重置' }}
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-skeleton v-if="isLoading" :rows="5" animated />

    <!-- 患者列表 -->
    <div v-else class="patient-list">
      <!-- 空状态 -->
      <el-empty
        v-if="filteredPatients.length === 0"
        :description="$t('common.noData') || '暂无患者数据'"
        class="empty-state"
      />

      <!-- 患者卡片列表 -->
      <div v-else class="patient-cards">
        <div
          v-for="patient in filteredPatients"
          :key="patient.id"
          class="patient-card"
          @click="goToPatientRecord(patient.id)"
        >
          <!-- 患者头部信息 -->
          <div class="patient-header">
            <div class="patient-name">
              <span class="name">{{ patient.name || patient.username }}</span>
              <span class="gender" :class="'gender-' + (patient.gender || 'unknown')">
                {{ formatGender(patient.gender) }}
              </span>
            </div>
            <div class="patient-age">
              {{ patient.age ? patient.age + '岁' : '年龄未知' }}
            </div>
          </div>

          <!-- 患者详细信息 -->
          <div class="patient-details">
            <div class="detail-item">
              <span class="label">{{ $t('auth.username') || '用户名' }}:</span>
              <span class="value">{{ patient.username }}</span>
            </div>
            <div class="detail-item">
              <span class="label">{{ $t('user.phone') || '电话' }}:</span>
              <span class="value">{{ patient.phone || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">{{ $t('auth.email') || '邮箱' }}:</span>
              <span class="value">{{ patient.email || '-' }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="patient-actions">
            <el-button
              type="primary"
              size="small"
              @click.stop="goToPatientRecord(patient.id)"
            >
              {{ $t('common.view') || '查看档案' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      @close="errorMessage = ''"
      class="error-alert"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { doctorAPI } from '../../services/api'
import { authService } from '../../services/auth'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'PatientList',
  components: {
    Search
  },
  setup() {
    const router = useRouter()
    const isLoading = ref(true)
    const errorMessage = ref('')
    const searchQuery = ref('')
    const filterGender = ref('')

    const patientList = reactive({
      data: []
    })

    // 计算过滤后的患者列表
    const filteredPatients = computed(() => {
      return patientList.data.filter(patient => {
        // 搜索过滤
        const searchLower = searchQuery.value.toLowerCase()
        const matchesSearch = !searchQuery.value || 
          (patient.name && patient.name.toLowerCase().includes(searchLower)) ||
          (patient.username && patient.username.toLowerCase().includes(searchLower)) ||
          (patient.phone && patient.phone.includes(searchQuery.value)) ||
          (patient.email && patient.email.toLowerCase().includes(searchLower))

        // 性别过滤
        const matchesGender = !filterGender.value || patient.gender === filterGender.value

        return matchesSearch && matchesGender
      })
    })

    // 获取患者列表
    const fetchPatients = async () => {
      try {
        isLoading.value = true
        errorMessage.value = ''

        // 获取当前医师信息
        const currentUser = authService.getUser()
        if (!currentUser || !currentUser.id) {
          throw new Error('无法获取医师信息')
        }

        // 调用API获取患者列表，传递医师ID
        const response = await doctorAPI.getPatients(currentUser.id)

        if (response && response.data) {
          patientList.data = response.data
        } else {
          patientList.data = []
        }
      } catch (error) {
        console.error('获取患者列表失败:', error)
        errorMessage.value = error.response?.data?.message || '获取患者列表失败'
        patientList.data = []
      } finally {
        isLoading.value = false
      }
    }

    // 搜索处理
    const handleSearch = () => {
      // 搜索是实时的，通过计算属性自动过滤
    }

    // 筛选处理
    const handleFilter = () => {
      // 筛选是实时的，通过计算属性自动过滤
    }

    // 重置筛选
    const resetFilters = () => {
      searchQuery.value = ''
      filterGender.value = ''
    }

    // 格式化性别显示
    const formatGender = (gender) => {
      const genderMap = {
        'MALE': '男',
        'FEMALE': '女',
        'OTHER': '其他'
      }
      return genderMap[gender] || '未知'
    }

    // 跳转到患者档案
    const goToPatientRecord = (patientId) => {
      router.push(`/doctor/patient/${patientId}`)
    }

    // 页面加载时获取患者列表
    onMounted(() => {
      fetchPatients()
    })

    return {
      isLoading,
      errorMessage,
      searchQuery,
      filterGender,
      filteredPatients,
      handleSearch,
      handleFilter,
      resetFilters,
      formatGender,
      goToPatientRecord
    }
  }
}
</script>

<style scoped>
.patient-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.search-filter-area {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  flex: 1;
  min-width: 250px;
}

.filter-options {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  width: 150px;
}

.reset-btn {
  white-space: nowrap;
}

.patient-list {
  display: flex;
  flex-direction: column;
}

.empty-state {
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.patient-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.patient-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.patient-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
  transform: translateY(-2px);
}

.patient-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7eb;
}

.patient-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.gender {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.gender-MALE {
  background-color: #409eff;
}

.gender-FEMALE {
  background-color: #f56c6c;
}

.gender-OTHER {
  background-color: #e6a23c;
}

.gender-unknown {
  background-color: #909399;
}

.patient-age {
  font-size: 14px;
  color: #606266;
  background-color: #f5f7fa;
  padding: 6px 12px;
  border-radius: 4px;
}

.patient-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.detail-item .label {
  color: #606266;
  font-weight: 500;
  min-width: 80px;
}

.detail-item .value {
  color: #333;
  word-break: break-all;
  text-align: right;
  flex: 1;
  margin-left: 10px;
}

.patient-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.error-alert {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .patient-cards {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .patient-list-container {
    padding: 15px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .search-filter-area {
    flex-direction: column;
    gap: 10px;
  }

  .search-input {
    width: 100%;
    min-width: unset;
  }

  .filter-options {
    width: 100%;
    justify-content: space-between;
  }

  .filter-select {
    flex: 1;
    min-width: 120px;
  }

  .patient-cards {
    grid-template-columns: 1fr;
  }

  .patient-card {
    padding: 15px;
  }

  .patient-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .patient-age {
    align-self: flex-start;
  }

  .detail-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .detail-item .value {
    text-align: left;
    margin-left: 0;
    margin-top: 5px;
  }
}
</style>
