<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的任务列表</span>
          <el-button type="primary" @click="openTaskDialog(null)">
            <el-icon><Plus /></el-icon> 新增任务
          </el-button>
        </div>
      </template>

      <el-table :data="userTasks" style="width: 100%" v-loading="loading">
        <el-table-column prop="taskTitleText" label="任务标题" width="200" />
        <el-table-column prop="taskDescriptionText" label="任务描述" show-overflow-tooltip />
        <el-table-column prop="taskDeadlineTimestamp" label="截止日期" width="180" />
        <el-table-column prop="taskStatusEnum" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.taskStatusEnum)">{{ scope.row.taskStatusEnum }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="openTaskDialog(scope.row)">编辑</el-button>
            <el-popconfirm title="确定要删除这个任务吗？" @confirm="handleDelete(scope.row.taskFocusId)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新增任务'" width="500px">
      <el-form ref="formRef" :model="taskForm" label-width="80px">
        <el-form-item label="任务标题">
          <el-input v-model="taskForm.taskTitleText"></el-input>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.taskDescriptionText" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="截止日期">
           <el-date-picker v-model="taskForm.taskDeadlineTimestamp" type="datetime" placeholder="选择日期时间" style="width: 100%"></el-date-picker>
        </el-form-item>
         <el-form-item label="任务状态">
          <el-select v-model="taskForm.taskStatusEnum" placeholder="请选择状态">
            <el-option label="pending" value="pending"></el-option>
            <el-option label="in_progress" value="in_progress"></el-option>
            <el-option label="completed" value="completed"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../api/NetWorkApi.js';
import { Plus } from '@element-plus/icons-vue';

const currentUserId = 1;
const userTasks = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const isEdit = ref(false);

const taskForm = reactive({
  taskFocusId: null,
  campusUserId: currentUserId,
  taskTitleText: '',
  taskDescriptionText: '',
  taskDeadlineTimestamp: '',
  taskStatusEnum: 'pending',
});

const fetchTasks = async () => {
  loading.value = true;
  try {
    const response = await api.taskFocusApi.list();
    const allTasks = response.data.data || [];
    userTasks.value = allTasks.filter(task => task.campusUserId === currentUserId);
  } catch (error) {
    ElMessage.error('任务列表加载失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchTasks);

const openTaskDialog = (task) => {
  if (task) {
    // 编辑模式
    isEdit.value = true;
    Object.assign(taskForm, task);
  } else {
    // 新增模式
    isEdit.value = false;
    Object.assign(taskForm, {
      taskFocusId: null,
      campusUserId: currentUserId,
      taskTitleText: '',
      taskDescriptionText: '',
      taskDeadlineTimestamp: '',
      taskStatusEnum: 'pending',
    });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.taskFocusApi.update(taskForm);
      ElMessage.success('更新成功');
    } else {
      await api.taskFocusApi.create(taskForm);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchTasks(); // 重新加载列表
  } catch (error) {
    ElMessage.error('操作失败');
    console.error(error);
  }
};

const handleDelete = async (id) => {
  try {
    await api.taskFocusApi.delete(id);
    ElMessage.success('删除成功');
    fetchTasks(); // 重新加载列表
  } catch (error) {
    ElMessage.error('删除失败');
    console.error(error);
  }
};

const getStatusTagType = (status) => {
  switch (status) {
    case 'completed': return 'success';
    case 'in_progress': return 'primary';
    case 'pending': return 'warning';
    default: return 'info';
  }
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>