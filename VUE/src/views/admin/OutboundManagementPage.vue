<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>出库记录流水</span>
          <el-button type="warning" @click="openOutboundDialog">
            <el-icon><EditPen /></el-icon> 手动登记出库
          </el-button>
        </div>
      </template>

      <!-- 出库记录列表 -->
      <el-table :data="outboundLogs" v-loading="loading" stripe>
        <el-table-column prop="id" label="日志ID" width="80" />
        <el-table-column prop="product.sku" label="产品SKU" width="180" />
        <el-table-column prop="product.name" label="产品名称" />
        <el-table-column prop="inventory.batchCode" label="批次号" width="200" />
        <el-table-column prop="quantityChange" label="出库数量" width="100">
             <template #default="scope">
                <el-tag type="danger">{{ scope.row.quantityChange }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="user.fullName" label="操作员" width="120" />
        <el-table-column prop="notes" label="备注" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="操作时间" width="180" />
      </el-table>
    </el-card>

    <!-- 手动登记出库 Dialog -->
    <el-dialog v-model="dialogVisible" title="手动登记出库" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择批次" prop="inventoryId">
          <el-select 
            v-model="form.inventoryId" 
            placeholder="请搜索并选择库存批次" 
            filterable 
            remote
            :remote-method="searchInventory"
            :loading="searchLoading"
            style="width: 100%;"
            @change="handleBatchSelect"
          >
            <el-option
              v-for="item in inventoryOptions"
              :key="item.id"
              :label="`${item.product.name} (批次: ${item.batchCode}, 当前库存: ${item.quantity})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity">
          <!-- 
            - 初始状态: max 是 Infinity，组件正常渲染
            - 选择后: max 是具体的库存数量，组件更新
          -->
          <el-input-number v-model="form.quantity" :min="1" :max="selectedBatchMaxQuantity" controls-position="right" />
              <!-- 
            这里的 v-if 逻辑需要微调，因为 Infinity 也是一个真值。
            我们应该只在选中了批次时才显示提示。
          -->
           <span v-if="form.inventoryId && selectedBatchMaxQuantity !== Infinity" style="margin-left: 10px; color: #999;">
              当前批次最大可出库: {{ selectedBatchMaxQuantity }}
          </span>
        </el-form-item>
        <el-form-item label="出库事由" prop="notes">
          <el-input v-model="form.notes" type="textarea" placeholder="例如：样品领用、盘亏调整等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleManualOutbound">确认出库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';
import { EditPen } from '@element-plus/icons-vue';

const loading = ref(true);
const outboundLogs = ref([]);
const dialogVisible = ref(false);
const formRef = ref(null);

// 用于手动出库的库存选项
const allInventory = ref([]);
const inventoryOptions = ref([]);
const searchLoading = ref(false);

// 假设当前登录管理员用户ID为 1
const currentAdminUserId = 1;

// 手动出库表单
const form = reactive({
  inventoryId: null,
  quantity: 1,
  notes: '',
});

const rules = reactive({
    inventoryId: [{ required: true, message: '必须选择一个库存批次', trigger: 'change' }],
    quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }],
    notes: [{ required: true, message: '请输入出库事由', trigger: 'blur' }],
});

const selectedBatchMaxQuantity = computed(() => {
    // 1. 如果没有选择任何库存 (form.inventoryId 是 falsy 值)
    if (!form.inventoryId) {
        // 返回 Infinity，这样 max 属性就变成了一个非常大的数，
        // min(1) > max(Infinity) 的条件永远不会成立，从而避免了报错。
        return Infinity; 
    }
    
    // 2. 根据 form.inventoryId 查找选中的库存项
    const selected = allInventory.value.find(item => item.id === form.inventoryId);
    
    // 3. 如果找到了，返回其实际库存数量；如果由于某种原因没找到，
    //    同样返回 Infinity 作为安全备用值。
    return selected ? selected.quantity : Infinity;
});

// 获取所有需要的数据
const fetchData = async () => {
  loading.value = true;
  try {
    const [logsRes, inventoryRes, productsRes, usersRes] = await Promise.all([
      api.transactionLogsApi.list(),
      api.inventoryApi.list(),
      api.productsApi.list(),
      api.usersApi.list()
    ]);

    const productsMap = new Map((productsRes.data || []).map(p => [p.id, p]));
    const usersMap = new Map((usersRes.data || []).map(u => [u.id, u]));
    
    // 缓存所有库存信息，并附加产品信息
    allInventory.value = (inventoryRes.data || []).map(inv => ({
        ...inv,
        product: productsMap.get(inv.productId) || { name: '未知产品' }
    }));
    const inventoryMap = new Map(allInventory.value.map(i => [i.id, i]));
    
    // 过滤并组合出库日志数据
  outboundLogs.value = (logsRes.data || [])
    // 1. 先对原始数据进行排序，这样最准确
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .filter(log => log.type === '出库')
    .map(log => {
      const inventoryItem = inventoryMap.get(log.inventoryId);

      // 2. 在 map 中只做数据组合和格式化
      return {
        ...log,
        // 将原始的 'T' 替换为空格
        createdAt: log.createdAt ? log.createdAt.replace('T', ' ') : 'N/A',
        inventory: inventoryItem,
        product: inventoryItem ? inventoryItem.product : { name: '未知产品', sku: 'N/A' },
        user: usersMap.get(log.userId) || { fullName: '未知用户' }
      };
    });

  } catch (error) {
    console.error("加载出库记录失败:", error);
    ElMessage.error("数据加载失败");
  } finally {
    loading.value = false;
  }
};

onMounted(fetchData);

// --- 手动出库相关 ---
const openOutboundDialog = () => {

    console.log("Opening outbound dialog");
    formRef.value?.resetFields();
    form.quantity = 1;
    inventoryOptions.value = []; // 清空之前的搜索结果
    dialogVisible.value = true;
};

// 远程搜索库存批次
const searchInventory = (query) => {
    if (query) {
        searchLoading.value = true;
        setTimeout(() => { // 模拟网络延迟
            inventoryOptions.value = allInventory.value.filter(item => {
                const queryLower = query.toLowerCase();
                return item.quantity > 0 && (
                    item.batchCode.toLowerCase().includes(queryLower) ||
                    item.product.name.toLowerCase().includes(queryLower)
                );
            });
            searchLoading.value = false;
        }, 200);
    } else {
        inventoryOptions.value = [];
    }
};

const handleBatchSelect = () => {
    form.quantity = 1; // 选中后重置数量为1
};

// 提交手动出库
const handleManualOutbound = () => {

  console.log("Submitting manual outbound:", form);
  formRef.value.validate(async (valid) => {
    if (valid) {
      const selectedInventory = allInventory.value.find(item => item.id === form.inventoryId);
      if (!selectedInventory) {
          ElMessage.error("无效的库存批次");
          return;
      }

      if (form.quantity > selectedInventory.quantity) {
          ElMessage.error("出库数量不能大于当前批次库存");
          return;
      }
      
      try {
        loading.value = true;
        
        // 1. 创建出库流水日志
        const logData = {
            inventoryId: form.inventoryId,
            userId: currentAdminUserId,
            type: '出库',
            quantityChange: -form.quantity, // 出库数量为负数
            quantityAfterTransaction: selectedInventory.quantity - form.quantity,
            notes: form.notes,
        };
        await api.transactionLogsApi.create(logData);

        // 2. 更新库存表中的数量
        const updatedInventory = {
            ...selectedInventory,
            quantity: selectedInventory.quantity - form.quantity,
        };
        await api.inventoryApi.update(updatedInventory);

        ElMessage.success("手动出库登记成功！");
        dialogVisible.value = false;
        await fetchData(); // 重新加载所有数据

      } catch (error) {
        console.error("手动出库失败:", error);
        ElMessage.error("操作失败: " + (error.response?.data?.message || error.message));
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>