<template>
  <div class="my-training-plans">
    <div class="page-header">
      <h1>My Training Plans</h1>
      <p class="subtitle">View and track your personalized training plans</p>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <el-radio-group v-model="statusFilter" @change="handleFilterChange">
        <el-radio-button label="all">All</el-radio-button>
        <el-radio-button label="active">Active</el-radio-button>
        <el-radio-button label="completed">Completed</el-radio-button>
      </el-radio-group>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- Plans List -->
    <div v-else-if="filteredPlans.length > 0" class="plans-list">
      <PlanCard 
        v-for="plan in filteredPlans" 
        :key="plan.id" 
        :plan="plan"
        @click="handlePlanClick"
      />
    </div>

    <!-- Empty State -->
    <div v-else class="empty-state">
      <el-empty :description="emptyStateMessage">
        <template #image>
          <el-icon :size="100" color="#909399">
            <Document />
          </el-icon>
        </template>
        <el-button type="primary" @click="handleContactCoach">
          Find a Coach
        </el-button>
      </el-empty>
    </div>

    <!-- Plan Detail Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedPlan?.name"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedPlan" class="plan-detail">
        <div class="detail-section">
          <h3>Plan Information</h3>
          <div class="info-grid">
            <div class="info-row">
              <span class="label">Coach:</span>
              <span class="value">{{ selectedPlan.coachName }}</span>
            </div>
            <div class="info-row">
              <span class="label">Status:</span>
              <el-tag :type="getStatusType(selectedPlan.status)" size="small">
                {{ getStatusText(selectedPlan.status) }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">Start Date:</span>
              <span class="value">{{ selectedPlan.startDate }}</span>
            </div>
            <div class="info-row">
              <span class="label">End Date:</span>
              <span class="value">{{ selectedPlan.endDate }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3>Description</h3>
          <p class="description">{{ selectedPlan.description }}</p>
        </div>

        <div class="detail-section">
          <h3>Exercises</h3>
          <ExerciseList :exercises-data="selectedPlan.exercises" />
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">Close</el-button>
      </template>
    </el-dialog>
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

const router = useRouter();

const loading = ref(false);
const plans = ref([]);
const statusFilter = ref('all');
const dialogVisible = ref(false);
const selectedPlan = ref(null);

// Computed properties
const filteredPlans = computed(() => {
  if (statusFilter.value === 'all') {
    return plans.value;
  }
  return plans.value.filter(plan => plan.status === statusFilter.value);
});

const emptyStateMessage = computed(() => {
  if (statusFilter.value === 'all') {
    return 'No training plans assigned yet. Contact a coach to get started!';
  }
  return `No ${statusFilter.value} training plans found.`;
});

// Methods
const fetchPlans = async () => {
  loading.value = true;
  try {
    const response = await getTrainingPlans();
    plans.value = response.content || response || [];
  } catch (error) {
    console.error('Failed to fetch training plans:', error);
    showError('Failed to load training plans');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  // Filter is handled by computed property
};

const handlePlanClick = (plan) => {
  selectedPlan.value = plan;
  dialogVisible.value = true;
};

const handleContactCoach = () => {
  // Navigate to home page (coaches list not yet implemented)
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

const getStatusText = (status) => {
  const textMap = {
    active: 'Active',
    completed: 'Completed',
    cancelled: 'Cancelled'
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
