<template>
  <div class="exercise-list">
    <div v-if="exercises.length === 0" class="empty-state">
      <el-empty description="No exercises in this plan" />
    </div>
    
    <div v-else>
      <div 
        v-for="(exercise, index) in exercises" 
        :key="index" 
        class="exercise-item"
      >
        <div class="exercise-header">
          <span class="exercise-number">{{ index + 1 }}</span>
          <h4 class="exercise-name">{{ exercise.name }}</h4>
          <el-checkbox 
            v-if="showCompletion"
            v-model="exercise.completed"
            @change="handleCompletionChange(index, exercise.completed)"
          >
            Completed
          </el-checkbox>
        </div>
        
        <div class="exercise-details">
          <div v-if="exercise.sets" class="detail-item">
            <el-icon><Document /></el-icon>
            <span>Sets: {{ exercise.sets }}</span>
          </div>
          
          <div v-if="exercise.reps" class="detail-item">
            <el-icon><Refresh /></el-icon>
            <span>Reps: {{ exercise.reps }}</span>
          </div>
          
          <div v-if="exercise.duration" class="detail-item">
            <el-icon><Timer /></el-icon>
            <span>Duration: {{ exercise.duration }}</span>
          </div>
        </div>
        
        <p v-if="exercise.notes" class="exercise-notes">
          <strong>Notes:</strong> {{ exercise.notes }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Document, Refresh, Timer } from '@element-plus/icons-vue';

const props = defineProps({
  exercisesData: {
    type: [String, Array],
    required: true
  },
  showCompletion: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['completion-change']);

const exercises = ref([]);

// Parse exercises data
const parseExercises = (data) => {
  if (Array.isArray(data)) {
    return data;
  }
  
  if (typeof data === 'string') {
    try {
      return JSON.parse(data);
    } catch (e) {
      console.error('Failed to parse exercises:', e);
      return [];
    }
  }
  
  return [];
};

// Initialize exercises
exercises.value = parseExercises(props.exercisesData);

// Watch for changes in exercisesData
watch(() => props.exercisesData, (newData) => {
  exercises.value = parseExercises(newData);
}, { deep: true });

const handleCompletionChange = (index, completed) => {
  emit('completion-change', { index, completed, exercise: exercises.value[index] });
};
</script>

<style scoped>
.exercise-list {
  padding: 8px 0;
}

.empty-state {
  padding: 20px;
  text-align: center;
}

.exercise-item {
  padding: 16px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  margin-bottom: 12px;
  background-color: #FAFAFA;
}

.exercise-item:last-child {
  margin-bottom: 0;
}

.exercise-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.exercise-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background-color: #409EFF;
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.exercise-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.exercise-details {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.detail-item .el-icon {
  font-size: 16px;
  color: #909399;
}

.exercise-notes {
  margin: 8px 0 0 0;
  padding: 8px;
  background-color: #F5F7FA;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.exercise-notes strong {
  color: #303133;
}
</style>
