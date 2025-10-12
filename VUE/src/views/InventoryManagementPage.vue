<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存批次查询</span>
        </div>
      </template>
      <el-table :data="inventory" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="库存ID" width="80" />
        <el-table-column prop="product.sku" label="产品SKU" width="180" />
        <el-table-column prop="product.name" label="产品名称" />
        <el-table-column prop="batch_code" label="批次号" width="200" />
        <el-table-column prop="quantity" label="当前库存量" width="120" />
        <el-table-column prop="inbound_order_id" label="关联入库单ID" width="140" />
        <el-table-column prop="received_at" label="入库时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../api/NetWorkApi.js';

const inventory = ref([]);
const loading = ref(true);

const fetchInventory = async () => {
  loading.value = true;
  try {
    // 为了显示产品信息，理想情况下后端接口应返回关联的产品数据
    // 此处我们假设后端 /inventory/list 返回的数据结构中包含了 product 对象
    // 如果没有，您可能需要再次请求产品列表并进行匹配
    const [invRes, prodRes] = await Promise.all([
        api.inventoryApi.list(),
        api.productsApi.list()
    ]);
    
    const allInventory = invRes.data || [];
    const allProducts = prodRes.data || [];

    const productsMap = new Map(allProducts.map(p => [p.id, p]));

    inventory.value = allInventory.map(item => ({
        ...item,
        product: productsMap.get(item.productId) || { sku: 'N/A', name: '未知产品' }
    }));

  } catch (error) {
    ElMessage.error('库存列表加载失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchInventory);
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>