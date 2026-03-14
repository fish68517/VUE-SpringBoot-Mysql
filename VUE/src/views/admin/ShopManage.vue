<template>
  <div class="shop-manage">
    <h1>店铺管理</h1>

    <div class="filter-section">
      <div class="filter-group">
        <el-select v-model="statusFilter" placeholder="按状态筛选" clearable @change="handleFilterChange">
          <el-option label="待审核" :value="0" />
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
        <el-button type="primary" @click="fetchShopList">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>

    <el-table :data="shopList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="店铺名称" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="success" size="small" @click="handleAuditShop(row, 1)">通过</el-button>
          <el-button v-if="row.status === 0" type="danger" size="small" @click="handleAuditShop(row, 2)">拒绝</el-button>
          <el-button v-if="row.status === 1" type="warning" size="small" @click="handleUpdateShopStatus(row, 2)">禁用</el-button>
          <el-button v-if="row.status === 2" type="success" size="small" @click="handleUpdateShopStatus(row, 1)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @change="fetchShopList"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as adminApi from "@/api/admin";

const shopList = ref([]);
const loading = ref(false);
const statusFilter = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const fetchShopList = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };
    if (statusFilter.value !== null && statusFilter.value !== undefined) {
      params.status = statusFilter.value;
    }
    const response = await adminApi.getShopList(params);
    shopList.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    console.error(error);
    ElMessage.error("获取店铺列表失败");
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  currentPage.value = 1;
  fetchShopList();
};

const resetFilter = () => {
  statusFilter.value = null;
  currentPage.value = 1;
  fetchShopList();
};

const getStatusLabel = (status) => ({ 0: "待审核", 1: "正常", 2: "禁用" }[status] || "-");
const getStatusType = (status) => ({ 0: "info", 1: "success", 2: "danger" }[status] || "info");

const handleAuditShop = (row, status) => {
  const action = status === 1 ? "通过" : "拒绝";
  ElMessageBox.confirm(`确认${action}店铺「${row.name}」吗？`, "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: status === 1 ? "success" : "warning"
  })
    .then(async () => {
      await adminApi.auditShop(row.id, status);
      ElMessage.success(`店铺已${action}`);
      fetchShopList();
    })
    .catch(() => {});
};

const handleUpdateShopStatus = (row, status) => {
  const action = status === 1 ? "启用" : "禁用";
  ElMessageBox.confirm(`确认${action}店铺「${row.name}」吗？`, "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning"
  })
    .then(async () => {
      await adminApi.updateShopStatus(row.id, status);
      ElMessage.success(`店铺已${action}`);
      fetchShopList();
    })
    .catch(() => {});
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  return new Date(dateString).toLocaleString("zh-CN");
};

onMounted(() => {
  fetchShopList();
});
</script>

<style scoped>
.shop-manage {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

h1 {
  margin: 0 0 20px;
  font-size: 24px;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.filter-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-group :deep(.el-select) {
  width: 160px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
