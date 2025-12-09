<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>商品库存操作日志</span>
        <el-button @click="fetchData" :icon="Refresh" circle></el-button>
      </div>
    </template>

    <!-- 筛选区域 -->
    <div class="filter-container">
        <el-input 
          v-model="searchText" 
          placeholder="搜索产品名称、SKU或批次号..." 
          style="width: 300px; margin-right: 15px;" 
          clearable 
          prefix-icon="Search" 
        />
        
        <el-select v-model="filterType" placeholder="操作类型" clearable style="width: 150px; margin-right: 15px;">
            <el-option label="入库" value="入库" />
            <el-option label="出库" value="出库" />
            <el-option label="盘点" value="盘点" />
        </el-select>

        <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 300px;"
        />
    </div>

    <!-- 日志列表表格 -->
    <el-table :data="filteredLogs" v-loading="loading" stripe style="width: 100%" height="600">
      <el-table-column prop="id" label="ID" width="80" sortable />
      
      <el-table-column label="产品信息" min-width="200">
          <template #default="scope">
              <div class="product-info">
                  <span class="product-name">{{ scope.row.productName }}</span>
                  <span class="product-sku">SKU: {{ scope.row.productSku }}</span>
              </div>
          </template>
      </el-table-column>

      <el-table-column prop="batchCode" label="批次号" width="180" show-overflow-tooltip />

      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.type)" effect="dark">{{ scope.row.type }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="quantityChange" label="变动数量" width="120">
          <template #default="scope">
              <span :class="scope.row.quantityChange > 0 ? 'text-success' : 'text-danger'">
                  {{ scope.row.quantityChange > 0 ? '+' : '' }}{{ scope.row.quantityChange }}
              </span>
          </template>
      </el-table-column>

      <el-table-column prop="quantityAfterTransaction" label="结存" width="100" />

      <el-table-column prop="operatorName" label="操作员" width="120" />

      <el-table-column prop="createdAt" label="操作时间" width="180" sortable>
          <template #default="scope">
              {{ formatTime(scope.row.createdAt) }}
          </template>
      </el-table-column>

      <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh, Search } from '@element-plus/icons-vue';
import api from '../../api/NetWorkApi.js';

const logs = ref([]);
const loading = ref(true);
const searchText = ref('');
const filterType = ref('');
const dateRange = ref(null);

// 计算属性：前端过滤
const filteredLogs = computed(() => {
    return logs.value.filter(log => {
        // 1. 文本搜索
        const searchLower = searchText.value.toLowerCase();
        const matchesSearch = !searchText.value || 
            (log.productName && log.productName.toLowerCase().includes(searchLower)) ||
            (log.productSku && log.productSku.toLowerCase().includes(searchLower)) ||
            (log.batchCode && log.batchCode.toLowerCase().includes(searchLower));
        
        // 2. 类型过滤
        const matchesType = !filterType.value || log.type === filterType.value;

        // 3. 日期过滤
        let matchesDate = true;
        if (dateRange.value && dateRange.value.length === 2 && log.createdAt) {
            const logDate = log.createdAt.substring(0, 10);
            matchesDate = logDate >= dateRange.value[0] && logDate <= dateRange.value[1];
        }

        return matchesSearch && matchesType && matchesDate;
    });
});

const fetchData = async () => {
  loading.value = true;
  try {
    // 并行获取 日志、库存(为了拿批次号和productId)、产品(为了拿名称)、用户(为了拿操作员名)
    const [logsRes, inventoryRes, productsRes, usersRes] = await Promise.all([
        api.transactionLogsApi.list(),
        api.inventoryApi.list(),
        api.productsApi.list(),
        api.usersApi.list()
    ]);

    const rawLogs = logsRes.data || [];
    const inventoryList = inventoryRes.data || [];
    const productsList = productsRes.data || [];
    const usersList = usersRes.data || [];

    // 构建 Map 加速查找
    const productMap = new Map(productsList.map(p => [p.id, p]));
    const userMap = new Map(usersList.map(u => [u.id, u]));
    
    // 构建 Inventory Map: ID -> { batchCode, productId }
    const inventoryMap = new Map(inventoryList.map(inv => [inv.id, inv]));

    // 组装数据
    logs.value = rawLogs.map(log => {
        const inventory = inventoryMap.get(log.inventoryId);
        const product = inventory ? productMap.get(inventory.productId) : null;
        const user = userMap.get(log.userId);

        return {
            ...log,
            batchCode: inventory ? inventory.batchCode : '未知批次',
            productName: product ? product.name : '未知产品',
            productSku: product ? product.sku : '-',
            operatorName: user ? (user.fullName || user.username) : '未知用户'
        };
    }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // 按时间倒序

  } catch (error) {
    console.error(error);
    ElMessage.error("日志数据加载失败");
  } finally {
    loading.value = false;
  }
};

const getTypeTag = (type) => {
    if (type === '入库') return 'success';
    if (type === '出库') return 'danger';
    if (type === '盘点') return 'warning';
    return 'info';
};

const formatTime = (timeStr) => {
    if (!timeStr) return '-';
    return timeStr.replace('T', ' ').substring(0, 19);
};

onMounted(fetchData);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-container {
    margin-bottom: 20px;
    display: flex;
    flex-wrap: wrap;
}
.product-info {
    display: flex;
    flex-direction: column;
}
.product-name {
    font-weight: bold;
}
.product-sku {
    font-size: 12px;
    color: #909399;
}
.text-success {
    color: #67c23a;
    font-weight: bold;
}
.text-danger {
    color: #f56c6c;
    font-weight: bold;
}
</style>