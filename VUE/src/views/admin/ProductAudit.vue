<template>
  <div class="product-audit">
    <h1>商品审核</h1>

    <div class="filter-section">
      <el-button type="primary" @click="fetchProductList">刷新</el-button>
    </div>

    <el-table :data="productList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <img v-if="row.image" :src="getImageUrl(row.image)" class="product-img" alt="商品图" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="shopName" label="店铺" width="120" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="90" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="success" size="small" @click="handleApproveProduct(row)">通过</el-button>
          <el-button type="danger" size="small" @click="handleRejectDialog(row)">拒绝</el-button>
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
        @change="fetchProductList"
      />
    </div>

    <el-dialog v-model="rejectDialogVisible" title="拒绝商品" width="500px">
      <el-form>
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectReason" type="textarea" rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmRejectProduct">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import * as adminApi from "@/api/admin";

const productList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const rejectDialogVisible = ref(false);
const rejectReason = ref("");
const rejectingProductId = ref(null);

const getImageUrl = (src) => {
  if (!src) return "";
  if (src.startsWith("http")) return src;
  return `http://localhost:8080/uploads/${src}`;
};

const fetchProductList = async () => {
  loading.value = true;
  try {
    const response = await adminApi.getProductAuditList({
      page: currentPage.value - 1,
      size: pageSize.value
    });
    productList.value = response.content || [];
    total.value = response.totalElements || 0;
  } catch (error) {
    console.error(error);
    ElMessage.error("获取待审核商品失败");
  } finally {
    loading.value = false;
  }
};

const handleApproveProduct = (row) => {
  ElMessageBox.confirm(
    `确认通过商品「${row.name}」吗？`,
    "提示",
    {
      confirmButtonText: "确认",
      cancelButtonText: "取消",
      type: "success"
    }
  )
    .then(async () => {
      await adminApi.auditProduct(row.id, { approved: true, reason: "" });
      ElMessage.success("商品已通过审核");
      fetchProductList();
    })
    .catch(() => {});
};

const handleRejectDialog = (row) => {
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
    console.error(error);
    ElMessage.error("拒绝失败");
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  return new Date(dateString).toLocaleString("zh-CN");
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
  margin: 0 0 20px;
  font-size: 24px;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
}

.product-img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
