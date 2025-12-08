
<template>
  <div class="my-training-plans">
   
   
     <div class="my-training-plans">
    <div class="page-header">
      <h1>我的训练计划</h1>
      <p class="subtitle">查看、追踪并反馈您的专属训练计划</p>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs">
      <!-- Tab 1: 我的训练计划 -->
      <el-tab-pane label="训练计划" name="plans">
        <!-- 筛选区域 -->
        <div class="filter-section">
          <el-radio-group v-model="statusFilter" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="active">进行中</el-radio-button>
            <el-radio-button label="completed">已完成</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 列表、加载、空状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="filteredPlans.length > 0" class="plans-list">
          <PlanCard
            v-for="plan in filteredPlans"
            :key="plan.id"
            :plan="plan"
            @click="handlePlanClick(plan)"
          />
        </div>
        <div v-else class="empty-state">
           <el-empty :description="emptyStateMessage">
             <template #image>
               <el-icon :size="100" color="#909399"><Document /></el-icon>
             </template>
             <el-button type="primary" @click="handleContactCoach">寻找教练</el-button>
           </el-empty>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 训练反馈 -->
      <el-tab-pane label="训练反馈" name="feedback">
        <div v-if="loading" class="loading-container">
           <el-skeleton :rows="3" animated />
        </div>
        <!-- 直接使用所有计划列表，不进行状态过滤 -->
        <div v-else-if="plans.length > 0" class="plans-list">
           <p class="feedback-prompt">请选择一个训练计划以查看或提交反馈。</p>
           <PlanCard
             v-for="plan in plans"
             :key="plan.id"
             :plan="plan"
             @click="handleFeedbackClick(plan)"
             clickable-style="feedback"
           />
        </div>
        <div v-else class="empty-state">
           <el-empty description="暂无任何训练计划，无法提交反馈。">
             <template #image>
               <el-icon :size="100" color="#909399"><Document /></el-icon>
             </template>
           </el-empty>
        </div>
      </el-tab-pane>
    </el-tabs>
    </div>
    
    
     <!-- 计划详情弹窗 (Plan Detail Dialog) -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedPlan?.name"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedPlan" class="plan-detail">
        <div class="detail-section">
          <h3>计划信息</h3>
          <div class="info-grid">
            <div class="info-row">
              <span class="label">教练：</span>
              <span class="value">{{ selectedPlan.coachName }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态：</span>
              <el-tag :type="getStatusType(selectedPlan.status)" size="small">
                {{ getStatusText(selectedPlan.status) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">开始日期：</span>
              <span class="value">{{ selectedPlan.startDate }}</span>
            </div>
            <div class="info-row">
              <span class="label">结束日期：</span>
              <span class="value">{{ selectedPlan.endDate }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3>计划详情</h3>
          <p class="description">{{ selectedPlan.description }}</p>
        </div>

        <div class="detail-section">
          <h3>训练动作</h3>
          <!-- 假设 ExerciseList 组件内部处理了显示，如果内部也有英文需要单独修改那个文件 -->
          <ExerciseList :exercises-data="selectedPlan.exercises" />
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

       <!-- 新增: 训练反馈弹窗 -->
    <TrainingFeedbackDialog
      v-if="selectedPlanForFeedback"
      v-model:visible="feedbackDialogVisible"
      :plan="selectedPlanForFeedback"
    />

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Document } from '@element-plus/icons-vue';
import { getTrainingPlans } from '../../api/training';
import PlanCard from '../../components/training/PlanCard.vue';
import ExerciseList from '../../components/training/ExerciseList.vue';
import { showError } from '@/utils/feedback';
import TrainingFeedbackDialog from '../../components/training/TrainingFeedbackDialog.vue';



const router = useRouter();

const loading = ref(false);
const plans = ref([]);
const statusFilter = ref('all');
const dialogVisible = ref(false);
const selectedPlan = ref(null);

// 新增：训练反馈弹窗 state
const feedbackDialogVisible = ref(false);
const selectedPlanForFeedback = ref(null);

// Computed properties
const filteredPlans = computed(() => {
  if (statusFilter.value === 'all') {
    return plans.value;
  }
  return plans.value.filter(plan => plan.status === statusFilter.value);
});

// 新增：点击计划卡片 (在"训练反馈"Tab)
const handleFeedbackClick = (plan) => {
  // 打印计划信息
  console.log('点击计划卡片:', plan);
  selectedPlanForFeedback.value = plan;
  feedbackDialogVisible.value = true;
};

// 动态生成中文的空状态提示
const emptyStateMessage = computed(() => {
  if (statusFilter.value === 'all') {
    return '暂无训练计划，联系教练为您制定专属计划吧！';
  }
  const statusMap = {
    active: '进行中',
    completed: '已完成'
  };
  const statusText = statusMap[statusFilter.value] || statusFilter.value;
  return `暂无${statusText}的训练计划。`;
});

// Methods
const fetchPlans = async () => {
  loading.value = true;
  try {
    const response = await getTrainingPlans();
    plans.value = response.content || response || [];
  } catch (error) {
    console.error('获取训练计划失败:', error);
    showError('获取训练计划失败');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  // 过滤逻辑由 computed 属性处理
};

const handlePlanClick = (plan) => {
  selectedPlan.value = plan;
  dialogVisible.value = true;
};

const handleContactCoach = () => {
  // 跳转到首页（因为教练列表功能尚未实现）
  router.push('/home');
};

const getStatusType = (status) => {
  const statusMap = {
    active: 'success',
    completed: 'info',
    cancelled: 'danger'
  };
  return statusMap[status] || 'info';
};

// 将状态码转换为中文显示
const getStatusText = (status) => {
  const textMap = {
    active: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  };
  return textMap[status] || status;
};

// Lifecycle
onMounted(() => {
  fetchPlans();
});
</script>

<style scoped>
.my-training-plans {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.filter-section {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #F5F7FA;
  border-radius: 8px;
}

.loading-container {
  padding: 24px;
}

.plans-list {
  display: grid;
  gap: 16px;
}

.empty-state {
  padding: 60px 24px;
  text-align: center;
}

.empty-state .el-button {
  margin-top: 16px;
}

/* Dialog Styles */
.plan-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-row .label {
  font-weight: 600;
  color: #606266;
  min-width: 100px;
}

.info-row .value {
  color: #303133;
}

.description {
  margin: 0;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* Responsive Design */
@media (max-width: 768px) {
  .my-training-plans {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .filter-section {
    padding: 12px;
  }

  .el-dialog {
    width: 95% !important;
  }
}
</style>
