<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>入库单管理</span>
          <div>
            <el-button type="primary" @click="openCreateDialog">
               <el-icon><Plus /></el-icon> 手动创建入库单
            </el-button>
            <el-upload
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              style="display: inline-block; margin-left: 10px;"
            >
              <el-button type="success">
                <el-icon><Upload /></el-icon> 从Excel导入
              </el-button>
            </el-upload>
            <el-button @click="downloadTemplate" style="margin-left: 10px;">
                <el-icon><Download /></el-icon> 下载导入模板
            </el-button>
          </div>
        </div>
      </template>

      <!-- 入库单列表 -->
      <el-table :data="orders" v-loading="loading">
        <el-table-column type="expand">
          <template #default="props">
            <div style="padding: 10px 20px;">
              <h4>入库批次详情 (入库单ID: {{ props.row.id }})</h4>
              <el-table :data="props.row.inventoryItems" size="small" border>
                 <el-table-column prop="batchCode" label="批次号 (二维码内容)" />
                 <el-table-column label="产品SKU">
                    <template #default="scope">{{ getProductName(scope.row.productId, 'sku') }}</template>
                 </el-table-column>
                 <el-table-column label="产品名称">
                    <template #default="scope">{{ getProductName(scope.row.productId, 'name') }}</template>
                 </el-table-column>
                 <el-table-column prop="quantity" label="计划数量" />
              </el-table>
              <el-button 
                type="primary" 
                plain 
                size="small" 
                @click="openQrCodeDialog(props.row.inventoryItems)" 
                style="margin-top: 10px;"
                :disabled="!props.row.inventoryItems || props.row.inventoryItems.length === 0"
               >
                 <el-icon><Grid /></el-icon> 生成/打印二维码
              </el-button>
              <el-button
                  type="info"
                  plain
                  size="small"
                  @click="downloadOrderDetails(props.row)"
                  style="margin-top: 10px; margin-left: 10px;"
                  :disabled="!props.row.inventoryItems || props.row.inventoryItems.length === 0"
              >
                  <el-icon><Document /></el-icon> 下载批次详情
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNumber" label="入库单号" />
        <el-table-column prop="creator.fullName" label="创建人" />
        <el-table-column prop="status" label="状态">
            <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="220">
            <template #default="scope">
                 <el-button
                    size="small"
                    type="primary"
                    link
                    @click="downloadOrderDetails(scope.row)"
                    :disabled="!scope.row.inventoryItems || scope.row.inventoryItems.length === 0"
                 >
                    <el-icon><Download /></el-icon> 下载详情
                 </el-button>
                 <el-button size="small" type="danger" @click="handleDeleteOrder(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-card>

     <!-- 手动创建入库单 Dialog -->
     <el-dialog v-model="createDialogVisible" title="手动创建入库单">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="入库单号">
          <el-input v-model="createForm.orderNumber" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.notes" type="textarea" />
        </el-form-item>
        <!-- 入库商品明细 -->
         <el-form-item label="入库明细">
            <div v-for="(item, index) in createForm.items" :key="index" class="item-row">
                 <el-select v-model="item.productId" placeholder="选择产品" filterable style="width: 200px;">
                    <el-option
                        v-for="product in products"
                        :key="product.id"
                        :label="`${product.name} (${product.sku})`"
                        :value="product.id"
                    />
                 </el-select>
                 <el-input-number v-model="item.quantity" :min="1" placeholder="数量" style="width: 120px; margin: 0 10px;" />
                 <el-button type="danger" :icon="Delete" circle @click="removeItem(index)" v-if="createForm.items.length > 1"></el-button>
            </div>
            <el-button @click="addItem" style="margin-top: 5px;"><el-icon><Plus /></el-icon> 添加商品</el-button>
         </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleManualCreate">确定创建</el-button>
      </template>
    </el-dialog>

    <!-- 二维码生成与打印 Dialog -->
    <el-dialog v-model="qrCodeDialogVisible" title="批次二维码" width="80%" top="5vh">
       <div id="qrCodePrintArea" class="qr-code-grid">
           <div v-for="item in itemsToQrCode" :key="item.batchCode" class="qr-code-item">
               <!-- 【关键修改】render-as="svg"： -->
               <!-- 使用 SVG 模式渲染，这样 innerHTML 就能获取到二维码的 DOM 结构，从而可以被打印出来 -->
               <qrcode-vue :value="item.batchCode" :size="qrCodeSize" level="H" render-as="svg" />
               
               <div class="qr-code-label">
                   <p>产品: {{ getProductName(item.productId, 'name') }}</p>
                   <p>SKU: {{ getProductName(item.productId, 'sku') }}</p>
                   <p>批次: {{ item.batchCode }}</p>
               </div>
           </div>
       </div>
       <template #footer>
           <el-button @click="qrCodeDialogVisible = false">关闭</el-button>
           <el-button type="primary" @click="printQrCodes">
               <el-icon><Printer /></el-icon> 打印
            </el-button>
       </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../../api/NetWorkApi.js';
import { Plus, Upload, Download, Delete, Printer, Grid, Document } from '@element-plus/icons-vue';
import * as XLSX from 'xlsx';
import QrcodeVue from 'qrcode.vue';
import apiClientInstance from '../../api/NetWorkApi.js';

const orders = ref([]);
const products = ref([]); 
const loading = ref(true);
const createDialogVisible = ref(false);
const qrCodeDialogVisible = ref(false);
const itemsToQrCode = ref([]); 
const qrCodeSize = ref(100); 

const currentAdminUserId = 1;

const createForm = reactive({
  orderNumber: `IN-${Date.now()}`,
  createdByUserId: currentAdminUserId,
  status: '待处理',
  notes: '',
  items: [{ productId: null, quantity: 1 }]
});

const uploadUrl = computed(() => `${apiClientInstance.defaults.baseURL}/inbound-orders/import`);

const fetchProducts = async () => {
    try {
        const res = await api.productsApi.list();
        products.value = res.data || [];
    } catch (error) {
        ElMessage.error("加载产品列表失败");
    }
};

const fetchData = async () => {
    loading.value = true;
    try {
        const [ordersRes, inventoryRes, usersRes] = await Promise.all([
            api.inboundOrdersApi.list(),
            api.inventoryApi.list(),
            api.usersApi.list()
        ]);
        
        const allOrders = ordersRes.data || [];
        const allInventory = inventoryRes.data || [];
        const usersMap = new Map((usersRes.data || []).map(u => [u.id, u]));

        const inventoryByOrderId = allInventory.reduce((acc, item) => {
            if (item.inboundOrderId) {
                if (!acc[item.inboundOrderId]) {
                    acc[item.inboundOrderId] = [];
                }
                acc[item.inboundOrderId].push({
                    id: item.id,
                    batchCode: item.batchCode,
                    productId: item.productId, 
                    quantity: item.quantity,
                    receivedAt: item.receivedAt,
                    createdAt: item.createdAt,
                    updatedAt: item.updatedAt
                });
            }
            return acc;
        }, {});

        orders.value = allOrders.map(order => ({
            ...order,
            creator: usersMap.get(order.createdByUserId) || { fullName: '未知' },
            inventoryItems: inventoryByOrderId[order.id] || []
        }));

    } catch (error) {
        console.error("加载入库单数据失败:", error);
        ElMessage.error("加载入库单数据失败");
    } finally {
        loading.value = false;
    }
};

const getProductName = (productId, field = 'name') => {
    const product = products.value.find(p => p.id === productId);
    return product ? product[field] : '未知产品';
};

onMounted(() => {
    fetchProducts();
    fetchData();
});

const openCreateDialog = () => {
  Object.assign(createForm, {
      orderNumber: `IN-${Date.now()}`,
      createdByUserId: currentAdminUserId,
      status: '待处理',
      notes: '',
      items: [{ productId: null, quantity: 1 }]
  });
  createDialogVisible.value = true;
};

const addItem = () => {
    createForm.items.push({ productId: null, quantity: 1 });
};

const removeItem = (index) => {
    createForm.items.splice(index, 1);
};

const handleManualCreate = async () => {
    if (!createForm.items.every(item => item.productId && item.quantity > 0)) {
        ElMessage.warning('请确保所有入库明细都选择了产品且数量大于0');
        return;
    }

    try {
        loading.value = true;
        const orderData = {
            orderNumber: createForm.orderNumber,
            createdByUserId: createForm.createdByUserId,
            status: createForm.status,
            notes: createForm.notes,
        };
        const orderRes = await api.inboundOrdersApi.create(orderData);
        
        const createdOrderId = orderRes.data?.id; 
        if (!createdOrderId) {
             throw new Error("创建入库单失败，未返回ID");
        }

        const inventoryPromises = createForm.items.map(item => {
             const timestampPart = Date.now().toString().slice(-6); 
             const randomPart = Math.random().toString(36).substring(2, 6); 
             const batchCode = `BAT-${createdOrderId}-${item.productId}-${timestampPart}-${randomPart}`;
             const inventoryData = {
                 productId: item.productId,
                 batchCode: batchCode,
                 inboundOrderId: createdOrderId,
                 quantity: item.quantity,
             };
             return api.inventoryApi.create(inventoryData);
        });

        await Promise.all(inventoryPromises);

        ElMessage.success("入库单及批次创建成功");
        createDialogVisible.value = false;
        fetchData(); 
    } catch (error) {
        console.error("手动创建入库单失败:", error);
        ElMessage.error("操作失败: " + (error.response?.data?.message || error.message));
    } finally {
        loading.value = false;
    }
};

const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || file.type === 'application/vnd.ms-excel' || file.name.endsWith('.csv');
  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件 (xls, xlsx, csv)!');
  }
  return isExcel;
};

const handleUploadSuccess = (response, file, fileList) => {
  ElMessage.success('Excel 文件上传成功，后端正在处理...');
  setTimeout(() => {
    fetchData();
  }, 3000);
};

const handleUploadError = (error, file, fileList) => {
  console.error("上传失败:", error);
  try {
      const errorMsg = JSON.parse(error.message || '{}').message || '上传失败，请检查文件格式或联系管理员';
      ElMessage.error(errorMsg);
  } catch(e) {
      ElMessage.error('上传失败，请检查文件格式或联系管理员');
  }
};

const downloadTemplate = () => {
    const templateData = [
        { SKU: '产品SKU (必填)', Quantity: '数量 (必填, 正整数)' },
        { SKU: 'SKU-A001', Quantity: 100 },
        { SKU: 'SKU-B002', Quantity: 50 },
    ];
    const ws = XLSX.utils.json_to_sheet(templateData, { skipHeader: true });
    ws['!cols'] = [ { wch: 20 }, { wch: 15 } ]; 
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, '入库模板');
    XLSX.writeFile(wb, 'inbound_template.xlsx');
};

const downloadOrderDetails = (order) => {
    if (!order.inventoryItems || order.inventoryItems.length === 0) {
        ElMessage.warning('该入库单没有关联的批次信息可供下载');
        return;
    }

    const exportData = order.inventoryItems.map(item => ({
        '批次号 (二维码内容)': item.batchCode,
        '产品SKU': getProductName(item.productId, 'sku'),
        '产品名称': getProductName(item.productId, 'name'),
        '计划数量': item.quantity,
    }));

    const ws = XLSX.utils.json_to_sheet(exportData);
    ws['!cols'] = [
        { wch: 30 }, 
        { wch: 20 }, 
        { wch: 30 }, 
        { wch: 10 } 
    ];

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, `入库单-${order.orderNumber}`);
    XLSX.writeFile(wb, `入库单详情_${order.orderNumber}.xlsx`);
    ElMessage.success(`入库单 ${order.orderNumber} 的批次详情已开始下载`);
};

const openQrCodeDialog = (items) => {
    if (!items || items.length === 0) {
        ElMessage.warning('该入库单没有关联的库存批次信息');
        return;
    }
    itemsToQrCode.value = items;
    qrCodeDialogVisible.value = true;
};

const printQrCodes = () => {
    const printContent = document.getElementById('qrCodePrintArea');
    if (printContent) {
        const newWindow = window.open('', '_blank');
        newWindow.document.write('<html><head><title>打印二维码</title>');
        newWindow.document.write(`
            <style>
                body { margin: 20px; font-family: sans-serif; }
                .qr-code-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; }
                .qr-code-item { border: 1px solid #ccc; padding: 10px; text-align: center; page-break-inside: avoid; }
                .qr-code-label { margin-top: 5px; font-size: 10px; line-height: 1.2; }
                .qr-code-label p { margin: 2px 0; }
                @media print {
                    body { margin: 0; }
                    .qr-code-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; }
                    .qr-code-item { border: none; padding: 5px; }
                     .qr-code-label { font-size: 8px; }
                }
            </style>
        `);
        newWindow.document.write('</head><body>');
        newWindow.document.write(printContent.innerHTML);
        newWindow.document.write('</body></html>');
        newWindow.document.close();
        newWindow.focus();
        setTimeout(() => { newWindow.print(); newWindow.close(); }, 500);
    }
};

const handleDeleteOrder = async (id) => {
    ElMessageBox.confirm(
        '确定要删除此入库单吗？关联的库存批次信息可能也会受影响。',
        '警告',
        {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await api.inboundOrdersApi.delete(id);
            ElMessage.success("删除成功");
            fetchData();
        } catch (error) {
            ElMessage.error("删除失败: " + (error.response?.data?.message || error.message));
        }
    }).catch(() => {
    });
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
.item-row {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
}
.qr-code-grid {
    display: grid;
    /* 自动填充，最小宽度200px，最大1fr，会自动换行 */
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); 
    gap: 20px; /* 二维码之间的间距 */
    max-height: 70vh; /* 限制最大高度，出现滚动条 */
    overflow-y: auto; /* 垂直滚动 */
}
.qr-code-item {
    border: 1px solid #eee;
    padding: 15px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}
.qr-code-label {
    margin-top: 10px;
    font-size: 12px;
    text-align: center;
    line-height: 1.4;
}
.qr-code-label p {
    margin: 3px 0;
}

@media print {
  body * {
    visibility: hidden;
  }
  #qrCodePrintArea, #qrCodePrintArea * {
    visibility: visible;
  }
  #qrCodePrintArea {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
   .qr-code-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px; 
    max-height: none; 
    overflow-y: visible; 
  }
  .qr-code-item {
     border: none;
     padding: 5px;
     page-break-inside: avoid; 
  }
  .qr-code-label { font-size: 8px; }
}
</style>