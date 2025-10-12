<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>入库单管理</span>
          <el-button type="primary" @click="openDialog(null)">
             <el-icon><Plus /></el-icon> 新增入库单
          </el-button>
        </div>
      </template>
      <el-table :data="orders" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="order_number" label="入库单号" />
        <el-table-column prop="creator.full_name" label="创建人" />
        <el-table-column prop="status" label="状态">
            <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" show-overflow-tooltip />
        <el-table-column prop="created_at" label="创建时间" />
        <el-table-column label="操作" width="150">
            <template #default="scope">
                <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-card>

     <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑入库单' : '新增入库单'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="入库单号">
          <el-input v-model="form.order_number" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../api/NetWorkApi.js';
import { Plus } from '@element-plus/icons-vue';

const orders = ref([]);
const loading = ref(true);
const dialogVisible = ref(false);
const isEdit = ref(false);
// 假设当前登录用户ID为2 (李经理)
const currentUserId = 2;

const form = reactive({
  id: null,
  order_number: '',
  created_by_user_id: currentUserId,
  status: '待处理',
  notes: ''
});

const fetchData = async () => {
    loading.value = true;
    try {
        const [ordersRes, usersRes] = await Promise.all([
            api.inboundOrdersApi.list(),
            api.usersApi.list()
        ]);
        const allOrders = ordersRes.data || [];
        const allUsers = usersRes.data || [];
        const usersMap = new Map(allUsers.map(u => [u.id, u]));

        orders.value = allOrders.map(order => ({
            ...order,
            creator: usersMap.get(order.created_by_user_id) || { full_name: '未知' }
        }));
    } catch (error) {
        ElMessage.error("加载失败");
    } finally {
        loading.value = false;
    }
};

onMounted(fetchData);

const openDialog = (order) => {
  if (order) {
    isEdit.value = true;
    Object.assign(form, order);
  } else {
    isEdit.value = false;
    Object.assign(form, { id: null, order_number: `IN-${Date.now()}`, created_by_user_id: currentUserId, status: '待处理', notes: '' });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.inboundOrdersApi.update(form);
      ElMessage.success("更新成功");
    } else {
      await api.inboundOrdersApi.create(form);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchData();
  } catch (error) { ElMessage.error("操作失败"); }
};

const handleDelete = async (id) => {
    try {
        await api.inboundOrdersApi.delete(id);
        ElMessage.success("删除成功");
        fetchData();
    } catch (error) { ElMessage.error("删除失败"); }
};

const getStatusTagType = (status) => {
    if (status === '已完成') return 'success';
    if (status === '处理中') return 'primary';
    if (status === '待处理') return 'warning';
    return 'info';
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>