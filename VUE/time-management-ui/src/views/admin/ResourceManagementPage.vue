<template>
  <el-card>
    <template #header>
        <div class="card-header">
            <span>学习资源维护</span>
            <el-button type="primary" @click="openDialog(null)">新增资源</el-button>
        </div>
    </template>
    <el-table :data="resources" v-loading="loading">
      <el-table-column prop="resourceNameText" label="资源名称" />
      <el-table-column prop="resourceUrlText" label="URL" show-overflow-tooltip />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.learnResourceId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑资源' : '新增资源'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="资源名称">
          <el-input v-model="form.resourceNameText" />
        </el-form-item>
        <el-form-item label="资源URL">
          <el-input v-model="form.resourceUrlText" />
        </el-form-item>
        <el-form-item label="资源描述">
          <el-input v-model="form.resourceDescriptionText" type="textarea" />
        </el-form-item>
         <el-form-item label="分类">
          <el-select v-model="form.resourceCategoryId" placeholder="请选择分类">
            <el-option 
              v-for="cat in categories" 
              :key="cat.resourceCategoryId" 
              :label="cat.categoryNameText" 
              :value="cat.resourceCategoryId" />
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
import { ref, onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';

const resources = ref([]);
const categories = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = reactive({
  learnResourceId: null,
  resourceNameText: '',
  resourceUrlText: '',
  resourceDescriptionText: '',
  resourceCategoryId: null,
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [resRes, catRes] = await Promise.all([
      api.learnResourceApi.list(),
      api.resourceCategoryApi.list(),
    ]);
    resources.value = resRes.data || [];
    categories.value = catRes.data || [];
  } catch (error) { ElMessage.error("加载失败"); } finally { loading.value = false; }
};

onMounted(fetchData);

const openDialog = (res) => {
  if (res) {
    isEdit.value = true;
    Object.assign(form, res);
  } else {
    isEdit.value = false;
    Object.assign(form, { learnResourceId: null, resourceNameText: '', resourceUrlText: '', resourceDescriptionText: '', resourceCategoryId: null });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.learnResourceApi.update(form);
      ElMessage.success("更新成功");
    } else {
      await api.learnResourceApi.create(form);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchData();
  } catch (error) { ElMessage.error("操作失败"); }
};

const handleDelete = async (id) => {
  try {
    await api.learnResourceApi.delete(id);
    ElMessage.success("删除成功");
    fetchData();
  } catch (error) { ElMessage.error("删除失败"); }
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>