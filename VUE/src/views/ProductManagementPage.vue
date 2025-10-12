<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>产品主数据管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon> 新增产品
          </el-button>
        </div>
      </template>

      <el-table :data="products" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="sku" label="产品SKU" width="180" />
        <el-table-column prop="name" label="产品名称" />
        <el-table-column prop="description" label="产品描述" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
            <el-popconfirm title="确定要删除这个产品吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑产品' : '新增产品'" width="600px">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="产品名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="产品SKU">
          <el-input v-model="form.sku"></el-input>
        </el-form-item>
        <el-form-item label="产品描述">
          <el-input v-model="form.description" type="textarea"></el-input>
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

const products = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const isEdit = ref(false);

const form = reactive({
  id: null,
  sku: '',
  name: '',
  description: '',
});

const fetchProducts = async () => {
  loading.value = true;
  try {
    const response = await api.productsApi.list();
    products.value = response.data || [];
  } catch (error) {
    ElMessage.error('产品列表加载失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchProducts);

const openDialog = (product) => {
  if (product) {
    isEdit.value = true;
    Object.assign(form, product);
  } else {
    isEdit.value = false;
    Object.assign(form, { id: null, sku: '', name: '', description: '' });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.productsApi.update(form);
      ElMessage.success('更新成功');
    } else {
      await api.productsApi.create(form);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    fetchProducts();
  } catch (error) {
    ElMessage.error('操作失败');
    console.error(error);
  }
};

const handleDelete = async (id) => {
  try {
    await api.productsApi.delete(id);
    ElMessage.success('删除成功');
    fetchProducts();
  } catch (error) {
    ElMessage.error('删除失败');
    console.error(error);
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