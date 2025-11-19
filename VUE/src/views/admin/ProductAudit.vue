<template>
  <div class="product-audit">
    <h1>商品审核</h1>
    
    <!-- 筛选 -->
    <div class="filter-section">
      <div class="filter-group">
        <el-button type="primary" @click="fetchProductList">刷新</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>

    <!-- 商品列表表格 -->
    <div class="table-section">
      <el-table
        :data="productList"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <img
              v-if="row.image"
              :src="row.image"
              alt="商品图片"
              style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" width="150" show-overflow-tooltip />
        <el-table-column label="店铺" width="120">
          <template #default="{ row }">
            {{ row.shop?.name || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="分类" width="100">
          <template #default="{ row }">
            {{ row.category?.name || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              @click="handleApproveProduct(row)"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleRejectProduct(row)"
            >
              拒绝
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleViewProduct(row)"
            >
              查看
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
          @change="fetchProductList"
        />
      </div>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="商品详情" width="600px">
      <div v-if="selectedProduct" class="product-detail">
        <div class="detail-item">
          <span class="label">商品名称：</span>
          <span>{{ selectedProduct.name }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商品描述：</span>
          <span>{{ selectedProduct.description }}</span>
        </div>
        <div class="detail-item">
          <span class="label">价格：</span>
          <span>¥{{ selectedProduct.price }}</span>
        </div>
        <div class="detail-item">
          <span class="label">库存：</span>
          <span>{{ selectedProduct.stock }}</span>
        </div>
        <div class="detail-item">
          <span class="label">分类：</span>
          <span>{{ selectedProduct.category?.name }}</span>
        </div>
        <div class="detail-item">
          <span class="label">店铺：</span>
          <span>{{ selectedProduct.shop?.name }}</span>
        </div>
        <div class="detail-item">
          <span class="label">商品图片：</span>
          <img
            v-if="selectedProduct.image"
            :src="selectedProduct.image"
            alt="商品图片"
            style="width: 200px; height: 200px; object-fit: cover; border-radius: 4px;"
          />
        </div>
      </div>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝商品" width="500px">
      <div class="reject-form">
        <el-form>
          <el-form-item label="拒绝原因">
            <el-input
              v-model="rejectReason"
              type="textarea"
              rows="4"
              placeholder="请输入拒绝原因"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmRejectProduct">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as adminApi from "@/api/admin";

const productList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const detailDialogVisible = ref(false);
const rejectDialogVisible = ref(false);
const selectedProduct = ref(null);
const rejectReason = ref("");
const rejectingProductId = ref(null);

const fetchProductList = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    
    const response = await adminApi.getProductAuditList(params);
    productList.value = response.data || [];
    total.value = response.total || 0;
  } catch (error) {
    ElMessage.error("获取待审核商品列表失败");
  } finally {
    loading.value = false;
  }
};

const resetFilter = () => {
  currentPage.value = 1;
  fetchProductList();
};

const handleApproveProduct = (row) => {
  ElMessageBox.confirm(
    `确定要通过商品 "${row.name}" 的审核吗？`,
    "确认",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "success"
    }
  )
    .then(async () => {
      try {
        await adminApi.auditProduct(row.id, { approved: true });
        ElMessage.success("商品已通过审核");
        fetchProductList();
      } catch (error) {
        ElMessage.error("审核失败");
      }
    })
    .catch(() => {
      // 取消操作
    });
};

const handleRejectProduct = (row) => {
  selectedProduct.value = row;
  rejectingProductId.value = row.id;
  rejectReason.value = "";
  rejectDialogVisible.value = true;
};

const confirmRejectProduct = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning("请输入拒绝原因");
    return;
  }

  try {
    await adminApi.auditProduct(rejectingProductId.value, {
      approved: false,
      reason: rejectReason.value
    });
    ElMessage.success("商品已拒绝");
    rejectDialogVisible.value = false;
    fetchProductList();
  } catch (error) {
    ElMessage.error("拒绝失败");
  }
};

const handleViewProduct = (row) => {
  selectedProduct.value = row;
  detailDialogVisible.value = true;
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
};

onMounted(() => {
  fetchProductList();
});
</script>

<style scoped>
.product-audit {
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

.table-section {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.product-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.detail-item .label {
  width: 100px;
  font-weight: bold;
  color: #333;
}

.detail-item span {
  flex: 1;
  color: #666;
  word-break: break-all;
}

.reject-form {
  padding: 20px 0;
}

@media (max-width: 768px) {
  .filter-group {
    flex-direction: column;
  }
}
</style>
