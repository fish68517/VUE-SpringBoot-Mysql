<template>
  <div class="points-mall-container">
    <div class="page-header">
      <h2>积分商城管理</h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      
      <el-tab-pane label="积分商品管理" name="goods">
        <div class="tab-action-bar">
          <el-button type="primary" @click="openGoodDialog()">
            <el-icon><Plus /></el-icon> 新增积分商品
          </el-button>
        </div>

        <el-alert 
          title="管理提示" 
          type="info" 
          description="虚拟商品（如电子券）无需发货；实体商品在用户兑换后需要填写物流单号。" 
          show-icon 
          :closable="false" 
          class="mb-20"
        />

        <el-table :data="goodsList" border v-loading="loading.goods">
          <el-table-column label="商品图片" width="100">
            <template #default="{ row }">
              <el-image 
                :src="row.image" 
                :preview-src-list="[row.image]" 
                fit="cover" 
                class="table-image"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" min-width="150" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 'virtual' ? 'success' : 'primary'">
                {{ row.type === 'virtual' ? '虚拟权益' : '实体商品' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="pointsPrice" label="所需积分" width="120">
            <template #default="{ row }">
              <span class="points-text">{{ row.pointsPrice }} 积分</span>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="120">
             <template #default="{ row }">
               <span :class="{ 'low-stock': row.stock < 10 }">{{ row.stock }}</span>
             </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isActive ? 'success' : 'info'">
                {{ row.isActive ? '上架中' : '已下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="openGoodDialog(row)">编辑</el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click="handleDeleteGood(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.goods.page"
            v-model:page-size="pagination.goods.size"
            layout="total, prev, pager, next"
            :total="pagination.goods.total"
            @current-change="loadGoods"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="兑换订单审核" name="orders">
        <div class="tab-action-bar">
          <div class="filters">
            <el-select v-model="orderFilterStatus" placeholder="状态筛选" style="width: 150px;" @change="loadOrders">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="pending" />
              <el-option label="已发货/已完成" value="completed" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </div>
          <el-button type="success" @click="handleExportOrders">
            <el-icon><Download /></el-icon> 导出订单
          </el-button>
        </div>

        <el-table :data="ordersList" border v-loading="loading.orders">
          <el-table-column prop="id" label="兑换单号" width="100" />
          <el-table-column prop="userName" label="用户名/手机号" width="150" />
          <el-table-column prop="goodName" label="兑换商品" min-width="150" />
          <el-table-column label="消耗积分" width="120">
            <template #default="{ row }">
              <span class="points-text">-{{ row.pointsCost }}</span>
            </template>
          </el-table-column>
          <el-table-column label="收货信息" min-width="200" show-overflow-tooltip>
             <template #default="{ row }">
               {{ row.shippingInfo || '无 (虚拟商品)' }}
             </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getOrderStatusType(row.status)">
                {{ getOrderStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div v-if="row.status === 'pending'">
                <el-button size="small" type="primary" @click="openAuditDialog(row)">通过</el-button>
                <el-button size="small" type="danger" @click="handleRejectOrder(row)">拒绝</el-button>
              </div>
              <span v-else class="text-gray">无需操作</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.orders.page"
            v-model:page-size="pagination.orders.size"
            layout="total, prev, pager, next"
            :total="pagination.orders.total"
            @current-change="loadOrders"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog 
      v-model="dialog.good" 
      :title="isEdit ? '编辑积分商品' : '新增积分商品'" 
      width="500px"
    >
      <el-form :model="goodForm" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="goodForm.name" />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-input v-model="goodForm.image" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="goodForm.type">
            <el-radio label="physical">实体商品</el-radio>
            <el-radio label="virtual">虚拟权益</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所需积分">
          <el-input-number v-model="goodForm.pointsPrice" :min="1" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="goodForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="goodForm.isActive" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.good = false">取消</el-button>
        <el-button type="primary" @click="submitGood" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog 
      v-model="dialog.audit" 
      title="兑换审核 - 发货" 
      width="400px"
    >
      <div v-if="currentAuditOrder?.type === 'physical' || !isVirtualOrder(currentAuditOrder)" class="mb-20">
        <p><strong>商品：</strong> {{ currentAuditOrder?.goodName }}</p>
        <p><strong>收货信息：</strong> {{ currentAuditOrder?.shippingInfo }}</p>
        <el-divider />
        <el-form label-position="top">
          <el-form-item label="物流单号" required>
             <el-input v-model="auditForm.shippingNo" placeholder="请输入快递单号" />
          </el-form-item>
        </el-form>
      </div>
      <div v-else>
        <el-alert title="虚拟商品自动发货" type="success" :closable="false">
          该商品为虚拟权益，审核通过后系统将自动发放权益（如生成核销码）。
        </el-alert>
      </div>
      <template #footer>
        <el-button @click="dialog.audit = false">取消</el-button>
        <el-button type="primary" @click="confirmAuditPass" :loading="submitting">
          确认通过
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'
import { pointsApi, type PointsGood, type ExchangeOrder } from '@/api/points'

// --- 状态管理 ---
const activeTab = ref('goods')
const submitting = ref(false)
const orderFilterStatus = ref('')

const loading = reactive({
  goods: false,
  orders: false
})

const pagination = reactive({
  goods: { page: 1, size: 10, total: 0 },
  orders: { page: 1, size: 10, total: 0 }
})

const dialog = reactive({
  good: false,
  audit: false
})

const isEdit = ref(false)
const goodsList = ref<PointsGood[]>([])
const ordersList = ref<ExchangeOrder[]>([])

// 表单数据
const goodForm = reactive<PointsGood>({
  name: '',
  image: '',
  type: 'physical',
  pointsPrice: 0,
  stock: 0,
  isActive: true
})

// 审核相关
const currentAuditOrder = ref<any>(null) // 临时存放当前审核的订单
const auditForm = reactive({
  shippingNo: ''
})

// --- 初始化 ---
onMounted(() => {
  loadGoods()
})

const handleTabChange = (name: string) => {
  if (name === 'goods') loadGoods()
  if (name === 'orders') loadOrders()
}

// ---------------- 1. 商品管理逻辑 ----------------

const loadGoods = async () => {
  loading.goods = true
  try {
    const params = {
      page: pagination.goods.page - 1,
      size: pagination.goods.size
    }
    const res: any = await pointsApi.getGoods(params)
    const data = res.data || res
    
    // 适配后端分页返回结构
    if (data.content) {
      goodsList.value = data.content
      pagination.goods.total = data.totalElements
    } else {
      goodsList.value = Array.isArray(data) ? data : []
    }
  } catch (err) {
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.goods = false
  }
}

const openGoodDialog = (row?: PointsGood) => {
  if (row) {
    isEdit.value = true
    Object.assign(goodForm, row)
  } else {
    isEdit.value = false
    goodForm.id = undefined
    goodForm.name = ''
    goodForm.image = ''
    goodForm.type = 'physical'
    goodForm.pointsPrice = 100
    goodForm.stock = 99
    goodForm.isActive = true
  }
  dialog.good = true
}

const submitGood = async () => {
  if (!goodForm.name || !goodForm.pointsPrice) {
    ElMessage.warning('请完善商品信息')
    return
  }
  
  submitting.value = true
  try {
    await pointsApi.saveGood(goodForm)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialog.good = false
    loadGoods()
  } catch (err) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

const handleDeleteGood = (row: PointsGood) => {
  ElMessageBox.confirm(`确定删除商品 "${row.name}" 吗?`, '警告', { type: 'warning' })
    .then(async () => {
      if (row.id) {
        await pointsApi.deleteGood(row.id)
        ElMessage.success('已删除')
        loadGoods()
      }
    })
    .catch(() => {})
}

// ---------------- 2. 订单审核逻辑 ----------------

const loadOrders = async () => {
  loading.orders = true
  try {
    const params = {
      page: pagination.orders.page - 1,
      size: pagination.orders.size,
      status: orderFilterStatus.value || undefined
    }
    const res: any = await pointsApi.getExchangeOrders(params)
    const data = res.data || res

    if (data.content) {
      ordersList.value = data.content
      pagination.orders.total = data.totalElements
    } else {
      ordersList.value = Array.isArray(data) ? data : []
    }
  } catch (err) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.orders = false
  }
}

// 判断是否虚拟商品订单（这里简单通过 shippingInfo 是否为空 或 关联商品类型判断，假设 row 里没带 type，需后端支持或根据 shippingInfo 判断）
const isVirtualOrder = (row: any) => {
  // 如果后端返回了 type 最好，否则根据是否有收货地址判断
  // 这里假设如果有 shippingInfo 则是实体
  return !row.shippingInfo
}

const openAuditDialog = (row: ExchangeOrder) => {
  currentAuditOrder.value = row
  auditForm.shippingNo = ''
  dialog.audit = true
}

const confirmAuditPass = async () => {
  if (!currentAuditOrder.value) return
  
  const isVirtual = isVirtualOrder(currentAuditOrder.value)
  if (!isVirtual && !auditForm.shippingNo) {
    ElMessage.warning('实体商品请填写物流单号')
    return
  }

  submitting.value = true
  try {
    // 状态传 'shipped' (已发货) 或 'completed' (虚拟商品直接完成)
    const status = isVirtual ? 'completed' : 'shipped'
    
    if (currentAuditOrder.value.id) {
        await pointsApi.auditExchange(currentAuditOrder.value.id, status, auditForm.shippingNo)
        ElMessage.success('审核通过')
        dialog.audit = false
        loadOrders()
    }
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleRejectOrder = (row: ExchangeOrder) => {
  ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
    .then(async ({ value }) => {
      if(row.id) {
          // 这里复用 audit 接口，status='rejected'，shippingNo 传拒绝原因或空
          await pointsApi.auditExchange(row.id, 'rejected', value) // 这里的 value 视后端实现，可能需要单独字段
          ElMessage.success('已拒绝该申请')
          loadOrders()
      }
    })
    .catch(() => {})
}

const handleExportOrders = async () => {
  try {
    const blob = await pointsApi.exportExchangeOrders({ status: orderFilterStatus.value })
    const url = window.URL.createObjectURL(new Blob([blob as any]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `exchange_orders_${Date.now()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('导出成功')
  } catch (err) {
    ElMessage.error('导出失败')
  }
}

// --- 辅助函数 ---
const getOrderStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    shipped: 'primary',
    completed: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getOrderStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待审核',
    shipped: '已发货',
    completed: '已完成',
    rejected: '已拒绝'
  }
  return map[status] || status
}
</script>

<style scoped>
.points-mall-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  min-height: 600px;
}

.page-header {
  margin-bottom: 20px;
}

.tab-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.table-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.points-text {
  color: #e6a23c;
  font-weight: bold;
}

.low-stock {
  color: #f56c6c;
  font-weight: bold;
}

.text-gray {
  color: #999;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>