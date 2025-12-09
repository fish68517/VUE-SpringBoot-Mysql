<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户管理</span>
        <el-button type="primary" @click="openDialog(null)">
          <el-icon><Plus /></el-icon> 新增用户
        </el-button>
      </div>
    </template>

    <el-table :data="users" v-loading="loading" style="width: 100%">
      <el-table-column prop="campusUserId" label="ID" width="80" />
      <el-table-column prop="campusNickname" label="昵称" />
      <el-table-column prop="campusEmailAddr" label="邮箱" />
      <el-table-column prop="campusSchoolId" label="学号" />
      <el-table-column prop="campusUserType" label="类型" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.campusUserType === 'admin' ? 'danger' : 'info'">
            {{ scope.row.campusUserType === 'admin' ? '管理员' : '学生' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="campusStatusFlag" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.campusStatusFlag)">
            {{ formatStatus(scope.row.campusStatusFlag) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button 
            v-if="scope.row.campusStatusFlag !== 2"
            size="small" type="danger" 
            @click="toggleStatus(scope.row, 2)">禁用</el-button>
          <el-button 
            v-else
            size="small" type="success" 
            @click="toggleStatus(scope.row, 1)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="form.campusNickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.campusEmailAddr" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password placeholder="默认密码" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="form.campusSchoolId" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="form.campusUserType">
            <el-option label="学生" value="student" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js'; // 注意路径

const users = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);

const form = reactive({
  campusUserId: null,
  campusNickname: '',
  campusEmailAddr: '',
  password: '',
  campusSchoolId: '',
  campusUserType: 'student',
  campusStatusFlag: 1
});

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await api.campusUserApi.list();
    users.value = res.data.data || res.data || [];
  } catch (e) {
    ElMessage.error('加载失败');
  } finally {
    loading.value = false;
  }
};

const openDialog = (row) => {
  if (row) {
    isEdit.value = true;
    Object.assign(form, row);
    form.password = ''; // 编辑时不显示密码
  } else {
    isEdit.value = false;
    Object.assign(form, {
      campusUserId: null, campusNickname: '', campusEmailAddr: '', 
      password: '', campusSchoolId: '', campusUserType: 'student', campusStatusFlag: 1
    });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.campusUserApi.update(form);
      ElMessage.success('更新成功');
    } else {
      await api.campusUserApi.create(form);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchData();
  } catch (e) {
    ElMessage.error('操作失败');
  }
};

const toggleStatus = async (row, status) => {
  try {
    row.campusStatusFlag = status;
    await api.campusUserApi.update(row);
    ElMessage.success('状态更新成功');
  } catch (e) {
    ElMessage.error('操作失败');
    fetchData(); // 恢复状态
  }
};

const formatStatus = (s) => s === 0 ? '待审核' : (s === 1 ? '正常' : '禁用');
const getStatusType = (s) => s === 0 ? 'warning' : (s === 1 ? 'success' : 'danger');

onMounted(fetchData);
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>