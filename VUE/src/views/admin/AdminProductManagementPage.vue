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

      <!-- 产品列表表格 -->
      <el-table :data="products" style="width: 100%" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="sku" label="产品SKU" width="150" />
        <el-table-column prop="name" label="产品名称" width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="产品描述" show-overflow-tooltip />
        
        <!-- 新增：创建人列 -->
        <el-table-column label="创建人" width="120">
          <template #default="scope">
            <el-tag size="small" type="info" effect="plain">
              {{ getUserName(scope.row.createdBy) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 新增：创建时间列 -->
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openDialog(scope.row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-popconfirm title="确定要删除这个产品吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" link>
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑产品' : '新增产品'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入产品名称"></el-input>
        </el-form-item>
        <el-form-item label="产品SKU" prop="sku">
          <el-input v-model="form.sku" placeholder="请输入唯一SKU编码"></el-input>
        </el-form-item>
        <el-form-item label="产品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3"></el-input>
        </el-form-item>

        <!-- 新增：创建人选择 -->
        <el-form-item label="创建人" prop="createdBy">
          <el-select v-model="form.createdBy" placeholder="请选择创建人" style="width: 100%" filterable>
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.fullName || user.username"
              :value="user.id"
            >
              <span style="float: left">{{ user.fullName || user.username }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ user.username }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 新增：创建时间选择 -->
        <el-form-item label="创建时间" prop="createdAt">
          <el-date-picker
            v-model="form.createdAt"
            type="datetime"
            placeholder="选择创建时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';

const products = ref([]);
const userList = ref([]); // 存储用户列表
const loading = ref(true);
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);

const form = reactive({
  id: null,
  sku: '',
  name: '',
  description: '',
  createdBy: null, // 新增
  createdAt: '',   // 新增
});

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  sku: [{ required: true, message: '请输入产品SKU', trigger: 'blur' }],
  createdBy: [{ required: true, message: '请选择创建人', trigger: 'change' }],
  createdAt: [{ required: true, message: '请选择创建时间', trigger: 'change' }],
};

// 初始化加载所有数据
const fetchData = async () => {
  loading.value = true;
  try {
    // 并行请求产品列表和用户列表
    const [productsRes, usersRes] = await Promise.all([
      api.productsApi.list(),
      api.usersApi.list()
    ]);
    
    products.value = productsRes.data || [];
    userList.value = usersRes.data || [];
  } catch (error) {
    ElMessage.error('数据加载失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchData);

// 辅助函数：根据ID获取用户名称
const getUserName = (userId) => {
  if (!userId) return '未知';
  const user = userList.value.find(u => u.id === userId);
  return user ? (user.fullName || user.username) : '未知用户';
};

// 辅助函数：格式化时间
const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return dateStr.replace('T', ' ').substring(0, 19);
};

const openDialog = (product) => {
  if (product) {
    isEdit.value = true;
    // 回填数据
    Object.assign(form, product);
  } else {
    isEdit.value = false;
    // 重置表单，默认选中当前时间
    Object.assign(form, { 
      id: null, 
      sku: '', 
      name: '', 
      description: '',
      createdBy: null, // 这里可以尝试自动填入当前登录用户的ID，如果状态管理里有的话
      createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19) // 默认当前时间
    });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEdit.value) {
          await api.productsApi.update(form);
          ElMessage.success('更新成功');
        } else {
          await api.productsApi.create(form);
          ElMessage.success('新增成功');
        }
        dialogVisible.value = false;
        fetchData(); // 刷新列表
      } catch (error) {
        ElMessage.error('操作失败');
        console.error(error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const handleDelete = async (id) => {
  try {
    await api.productsApi.delete(id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error) {
    ElMessage.error('删除失败，可能存在关联数据');
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