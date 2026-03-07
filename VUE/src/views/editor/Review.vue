<template>
  <div class="review">
    <h1>稿件管理</h1>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待初审" name="submitted">
        <el-table :data="pendingReview" stripe v-loading="loading">
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="投稿日期" width="180">
            <template #default="{ row }">
              {{ formatDate(row.submissionDate) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleAction(row, '初审')">初审</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="审稿中" name="underReview">
        <el-table :data="underReview" stripe v-loading="loading">
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="状态" width="120">
            <template #default>
              <el-tag type="primary">审稿中</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleAction(row, '查看进度')">查看进度</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="需修改" name="revisionRequired">
        <el-table :data="revisionRequiredList" stripe v-loading="loading">
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="状态" width="120">
            <template #default>
              <el-tag type="warning">等待作者修改</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleAction(row, '查看')">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已录用" name="accepted">
        <el-table :data="acceptedList" stripe v-loading="loading">
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="状态" width="120">
            <template #default>
              <el-tag type="success">已录用</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleAction(row, '查看')">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已拒稿" name="rejected">
        <el-table :data="rejectedList" stripe v-loading="loading">
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="状态" width="120">
            <template #default>
              <el-tag type="danger">已拒稿</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleAction(row, '查看')">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/services/api' // 依然强烈建议使用带 Token 的 API 实例

const router = useRouter()
const activeTab = ref('submitted')
const loading = ref(false)

// 各状态的数据列表
const pendingReview = ref([])
const underReview = ref([])
const revisionRequiredList = ref([])
const acceptedList = ref([])
const rejectedList = ref([])

onMounted(() => {
  loadAllManuscripts()
})

const loadAllManuscripts = async () => {
  loading.value = true
  try {
    // 调用获取所有稿件的接口，实际接口地址请根据后端调整
    const response = await api.get('/api/manuscripts') 
    
    if (response.data && response.code === 200) {
      const allManuscripts = response.data || []
      
      // 核心修改：严格按照 Manuscript.java 的状态枚举进行分类过滤
      pendingReview.value = allManuscripts.filter(m => m.status === 'SUBMITTED')
      underReview.value = allManuscripts.filter(m => m.status === 'UNDER_REVIEW')
      revisionRequiredList.value = allManuscripts.filter(m => m.status === 'REVISION_REQUIRED')
      acceptedList.value = allManuscripts.filter(m => m.status === 'ACCEPTED')
      rejectedList.value = allManuscripts.filter(m => m.status === 'REJECTED')
    }
  } catch (error) {
    ElMessage.error('加载稿件列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

// 通用的操作跳转处理（可根据你的路由配置具体细化）
const handleAction = (row, actionType) => {
  if (actionType === '初审') {
    router.push('/editor/initial-review') // 跳转到初审页面
  } else if (actionType === '查看进度') {
    router.push('/editor/review-progress') // 跳转到审稿进度页面
  } else {
    // 这里可以预留一个统一的稿件详情弹窗或页面跳转
    ElMessage.info(`查看稿件 ${row.title} 的详情`)
  }
}
</script>

<style scoped>
.review {
  padding: 20px;
}

.review h1 {
  margin-bottom: 20px;
  color: #333;
}

:deep(.el-table) {
  margin-top: 10px;
}
</style>