<template>
  <div class="shop-manage">
    <h1>店铺管理</h1>
    
    <!-- 筛选 -->
    <div class="filter-section">
      <div class="filter-group">
        <el-select
          v-model="statusFilter"
          placeholder="按状态筛选"
          clearable
          @change="handleFilterChange"
        >
          <el-option label="待审核" :value="0" />
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
        <el-button type="primary" @click="fetchShopList">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>

    <!-- 店铺列表表格 -->
    <div class="table-section">
      <el-table
        :data="shopList"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="店铺名称" width="150" />
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip />
        <el-table-column label="店主" width="120">
          <template #default="{ row }">
            {{ row.user?.username || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="handleAuditShop(row, true)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="handleAuditShop(row, false)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              size="small"
              @click="handleDisableShop(row)"
            >
              禁用
            </el-button>
            <el-button
              v-if="row.status === 2"
              type="success"
              size="small"
              @click="handleEnableShop(row)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
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
      page: currentPage.value,
      pageSize: pageSize.value
    };
    
    if (statusFilter.value !== null && statusFilter.value !== undefined) {
      params.status = statusFilter.value;
    }
    
    const response = await adminApi.getShopList(params);
    shopList.value = response.data || [];
    total.value = response.total || 0;
  } catch (error) {
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

const getStatusLabel = (status) => {
  const labels = {
    0: "待审核",
    1: "正常",
    2: "禁用"
  };
  return labels[status] || "-";
};

const getStatusType = (status) => {
  const types = {
    0: "info",
    1: "success",
    2: "danger"
  };
  return types[status] || "info";
};

const handleAuditShop = (row, approved) => {
  const message = approved ? "确定要通过该店铺吗？" : "确定要拒绝该店铺吗？";
  const title = approved ? "通过审核" : "拒绝审核";
  
  ElMessageBox.confirm(message, title, {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: approved ? "success" : "warning"
  })
    .then(async () => {
      try {
        await adminApi.auditShop(row.id, { approved });
        ElMessage.success(approved ? "店铺已通过审核" : "店铺已拒绝");
        fetchShopList();
      } catch (error) {
        ElMessage.error("审核失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleDisableShop = (row) => {
  ElMessageBox.confirm(
    `确定要禁用店铺 "${row.name}" 吗？`,
    "警告",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }
  )
    .then(async () => {
      try {
        await adminApi.updateShopStatus(row.id, { status: 2 });
        ElMessage.success("店铺已禁用");
        fetchShopList();
      } catch (error) {
        ElMessage.error("禁用店铺失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleEnableShop = (row) => {
  ElMessageBox.confirm(
    `确定要启用店铺 "${row.name}" 吗？`,
    "确认",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "info"
    }
  )
    .then(async () => {
      try {
        await adminApi.updateShopStatus(row.id, { status: 1 });
        ElMessage.success("店铺已启用");
        fetchShopList();
      } catch (error) {
        ElMessage.error("启用店铺失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
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
  margin: 0 0 20px 0;
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
  width: 150px;
}

.table-section {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .filter-group {
    flex-direction: column;
  }

  .filter-group :deep(.el-select) {
    width: 100%;
  }
}
</style>
