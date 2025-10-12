<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>部门管理</span>
        <el-button type="primary" @click="openDialog(null)">新增部门</el-button>
      </div>
    </template>
    <el-table :data="departments" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="部门名称" />
      <el-table-column prop="created_at" label="创建时间" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="部门名称">
          <el-input v-model="form.name" />
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
import { ref, onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';

const departments = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = reactive({
  id: null,
  name: '',
});

const fetchDepartments = async () => {
  loading.value = true;
  try {
    const res = await api.departmentsApi.list();
    departments.value = res.data || [];
  } catch (error) { ElMessage.error("加载失败"); } finally { loading.value = false; }
};

onMounted(fetchDepartments);

const openDialog = (dep) => {
  if (dep) {
    isEdit.value = true;
    Object.assign(form, dep);
  } else {
    isEdit.value = false;
    Object.assign(form, { id: null, name: '' });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.departmentsApi.update(form);
      ElMessage.success("更新成功");
    } else {
      await api.departmentsApi.create(form);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchDepartments();
  } catch (error) { ElMessage.error("操作失败"); }
};

const handleDelete = async (id) => {
  try {
    // 注意：如果部门下有用户，直接删除可能会因外键约束失败
    await api.departmentsApi.delete(id);
    ElMessage.success("删除成功");
    fetchDepartments();
  } catch (error) { ElMessage.error("删除失败，请先确认该部门下没有用户"); }
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>