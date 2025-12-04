<template>
  <div class="order-management-container">
    <div class="page-header">
      <h2>商品订单管理</h2>
      <div class="header-actions">
        <el-input 
          v-model="searchKeyword" 
          placeholder="请输入订单号" 
          style="width: 240px; margin-right: 12px;" 
          clearable 
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button type="success" @click="handleExport">
          <el-icon><Download /></el-icon> 导出订单Excel
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" type="card" class="order-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="已支付 (待发货)" name="paid" />
      <el-tab-pane label="已发货 (运输中)" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
    </el-tabs>

    <div class="content-area" v-loading="loading">
      <el-alert 
        v-if="activeTab === 'paid'"
        title="操作提示" 
        type="warning" 
        description="请及时为已支付的订单填写物流单号并发货。" 
        show-icon 
        :closable="false" 
        class="mb-20"
      />

      <el-table :data="orders" border style="width: 100%" row-key="id">
        <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
        
        <el-table-column label="收货人信息" width="220">
          <template #default="{ row }">
            <div><el-icon><User /></el-icon> {{ row.userPhone }}</div>
            <div class="address-text"><el-icon><Location /></el-icon> {{ row.address }}</div>
          </template>
        </el-table-column>

        <el-table-column label="商品明细">
          <template #default="{ row }">
            <div v-for="(item, index) in row.items" :key="index" class="order-item-row">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-qty">x{{ item.quantity }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="下单时间" width="170" />

        <el-table-column label="物流单号" width="160">
          <template #default="{ row }">
            <span v-if="row.trackingNumber">{{ row.trackingNumber }}</span>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'paid'" 
              type="primary" 
              size="small" 
              @click="openShipDialog(row)"
            >
              发货/填单号
            </el-button>

            <template v-else-if="row.status === 'shipped'">
              <el-button size="small" @click="openShipDialog(row)">修改单号</el-button>
              <el-popconfirm title="确定模拟订单已送达完成吗？" @confirm="handleComplete(row)">
                <template #reference>
                  <el-button size="small" type="success" plain>完成订单</el-button>
                </template>
              </el-popconfirm>
            </template>

            <el-tag v-else type="success">已归档</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>
    </div>

    <el-dialog v-model="shipDialog.visible" title="订单发货" width="400px">
      <el-form :model="shipDialog.form" label-position="top">
        <el-form-item label="订单号">
          <el-input v-model="shipDialog.currentRow.orderNo" disabled />
        </el-form-item>
        <el-form-item label="物流单号" required>
          <el-input v-model="shipDialog.form.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="shipDialog.loading" @click="confirmShip">
          确认发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Download, User, Location } from '@element-plus/icons-vue'
import { adminOrderApi, type AdminProductOrder } from '@/api/adminOrder' // 引用之前定义的接口
import { formatDate } from '@/utils/date' // 假设有这个工具函数，如果没有可以直接用 new Date().toLocaleString()

// --- 状态定义 ---
const loading = ref(false)
const activeTab = ref('paid') // 默认显示待发货
const searchKeyword = ref('')
const orders = ref<AdminProductOrder[]>([])

// 分页状态
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 发货弹窗状态
const shipDialog = reactive({
  visible: false,
  loading: false,
  currentRow: {} as any, // 暂存当前操作行
  form: {
    trackingNumber: ''
  }
})

// --- 方法 ---

// 1. 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1, // 后端通常是 0-based，根据实际情况调整
      size: pagination.size,
      status: activeTab.value,
      orderNo: searchKeyword.value || undefined
    }
    
    const res = await adminOrderApi.getOrders(params)
    // 兼容处理：根据实际后端返回结构调整，假设是 res.data 或 res.data.content
    const data = (res as any).data || res
    
    // 如果是 PageResponse 结构
    if (data.content) {
      orders.value = data.content
      pagination.total = data.totalElements
    } else {
      // 如果直接是数组（用于测试）
      orders.value = Array.isArray(data) ? data : []
      pagination.total = orders.value.length
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 2. Tab 切换
const handleTabChange = () => {
  pagination.page = 1 // 重置页码
  loadOrders()
}

// 3. 搜索
const handleSearch = () => {
  pagination.page = 1
  loadOrders()
}

// 4. 打开搜索弹窗
const openShipDialog = (row: AdminProductOrder) => {
  shipDialog.currentRow = row
  // 如果已有单号（修改场景），回填
  shipDialog.form.trackingNumber = row.trackingNumber || ''
  shipDialog.visible = true
}

// 5. 确认发货
const confirmShip = async () => {
  if (!shipDialog.form.trackingNumber) {
    ElMessage.warning('请输入物流单号')
    return
  }

  shipDialog.loading = true
  try {
    await adminOrderApi.shipOrder(shipDialog.currentRow.id, shipDialog.form.trackingNumber)
    ElMessage.success('发货成功')
    shipDialog.visible = false
    loadOrders() // 刷新列表
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    shipDialog.loading = false
  }
}

// 6. 完成订单
const handleComplete = async (row: AdminProductOrder) => {
  try {
    await adminOrderApi.completeOrder(row.id)
    ElMessage.success('订单已标记为完成')
    loadOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 7. 导出 Excel
const handleExport = async () => {
  try {
    const params = {
      status: activeTab.value,
      orderNo: searchKeyword.value || undefined
    }
    const blob = await adminOrderApi.exportOrders(params)
    const url = window.URL.createObjectURL(new Blob([blob as any]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `orders_${activeTab.value}_${Date.now()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('导出开始')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 初始化
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-management-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.order-tabs {
  margin-bottom: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.order-item-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 4px;
  border-bottom: 1px dashed #eee;
  padding-bottom: 2px;
}

.order-item-row:last-child {
  border-bottom: none;
}

.item-name {
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 180px;
}

.item-qty {
  color: #999;
}

.address-text {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-text {
  color: #f56c6c;
  font-weight: bold;
}

.text-gray {
  color: #ccc;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>