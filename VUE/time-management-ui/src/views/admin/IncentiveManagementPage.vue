<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>激励徽章配置</span>
        <el-button type="primary" @click="openDialog(null)">新增徽章</el-button>
      </div>
    </template>
    <el-table :data="achievements" v-loading="loading">
      <el-table-column prop="achieveNameText" label="徽章名称" />
      <el-table-column prop="achieveDescriptionText" label="描述" />
      <el-table-column prop="achieveRuleText" label="获取规则" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.achievementId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑徽章' : '新增徽章'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="徽章名称">
          <el-input v-model="form.achieveNameText" />
        </el-form-item>
        <el-form-item label="徽章描述">
          <el-input v-model="form.achieveDescriptionText" type="textarea" />
        </el-form-item>
        <el-form-item label="获取规则">
          <el-input v-model="form.achieveRuleText" type="textarea" />
        </el-form-item>
        <el-form-item label="图标URL">
          <el-input v-model="form.achieveIconUrl" />
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

const achievements = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = reactive({
  achievementId: null,
  achieveNameText: '',
  achieveDescriptionText: '',
  achieveRuleText: '',
  achieveIconUrl: 'https://example.com/badges/default.png', // 默认图标
});

const fetchAchievements = async () => {
  loading.value = true;
  try {
    const res = await api.achievementApi.list();
    achievements.value = res.data.data || [];
  } catch (error) { ElMessage.error("加载失败"); } finally { loading.value = false; }
};

onMounted(fetchAchievements);

const openDialog = (ach) => {
  if (ach) {
    isEdit.value = true;
    Object.assign(form, ach);
  } else {
    isEdit.value = false;
    Object.assign(form, { achievementId: null, achieveNameText: '', achieveDescriptionText: '', achieveRuleText: '', achieveIconUrl: 'https://example.com/badges/default.png' });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.achievementApi.update(form);
      ElMessage.success("更新成功");
    } else {
      await api.achievementApi.create(form);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchAchievements();
  } catch (error) { ElMessage.error("操作失败"); }
};

const handleDelete = async (id) => {
  try {
    await api.achievementApi.delete(id);
    ElMessage.success("删除成功");
    fetchAchievements();
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