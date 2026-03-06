<template>
  <div class="shop-coupons">
    <div class="page-header">
      <h1>优惠券管理</h1>
      <router-link to="/shop/coupons/create" class="btn-add">➕ 创建优惠券</router-link>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="coupons.length > 0" class="coupons-grid">
      <div v-for="coupon in coupons" :key="coupon.id" class="coupon-card">
        <div class="coupon-header">
          <div class="coupon-name">{{ coupon.name }}</div>
          <span class="status-badge" :class="getStatusClass(coupon.status)">
            {{ getStatusText(coupon.status) }}
          </span>
        </div>

        <div class="coupon-content">
          <div class="discount-info">
            <div class="discount-amount">¥{{ coupon.discountAmount }}</div>
            <div class="discount-label">优惠金额</div>
          </div>

          <div class="coupon-details">
            <div class="detail-item">
              <span class="label">最低消费：</span>
              <span class="value">¥{{ coupon.minAmount || 0 }}</span>
            </div>
            <div class="detail-item">
              <span class="label">发放总数：</span>
              <span class="value">{{ coupon.totalCount || "无限" }}</span>
            </div>
            <div class="detail-item">
              <span class="label">已使用：</span>
              <span class="value">{{ coupon.usedCount || 0 }}</span>
            </div>
            <div class="detail-item">
              <span class="label">有效期：</span>
              <span class="value">{{ formatDate(coupon.startTime) }} ~ {{ formatDate(coupon.endTime) }}</span>
            </div>
          </div>
        </div>

        <div class="coupon-actions">
          <router-link :to="`/shop/coupons/edit/${coupon.id}`" class="btn-edit">编辑</router-link>
          <button @click="handleDelete(coupon.id)" class="btn-delete">删除</button>
        </div>
      </div>
    </div>
    <div v-else class="empty">暂无优惠券</div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <button
        @click="currentPage--"
        :disabled="currentPage === 1"
        class="page-btn"
      >
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
      <button
        @click="currentPage++"
        :disabled="currentPage === totalPages"
        class="page-btn"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getShopCouponList } from "@/api/coupon";
import { deleteCoupon } from "@/api/coupon";

const loading = ref(false);
const coupons = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
import { useUserStore } from "@/store/userStore";
const userStore = useUserStore();

const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

const getStatusText = (status) => {
  const statusMap = {
    0: "禁用",
    1: "启用"
  };
  return statusMap[status] || "未知";
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-disabled",
    1: "status-active"
  };
  return classMap[status] || "";
};

const formatDate = (date) => {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("zh-CN");
};

const loadCoupons = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value -1,
      pageSize: pageSize.value
    };

    // 增加shopId
    // 添加shopId
    params.shopId = userStore.userInfo?.shopId;
    const data = await getShopCouponList(params);
    coupons.value = data || [];
    total.value = data?.total || 0;
  } catch (error) {
    ElMessage.error("加载优惠券列表失败");
  } finally {
    loading.value = false;
  }
};

const handleDelete = async (couponId) => {
  try {
    await ElMessageBox.confirm("确定要删除该优惠券吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    });
    // TODO: 调用删除API
    await deleteCoupon(couponId);

    ElMessage.success("优惠券已删除");
    loadCoupons();
  } catch (error) {
    if (error !== "cancel") {
      // log
      console.log("删除失败  ",error);
      ElMessage.error("删除失败");
    }
  }
};

watch(currentPage, () => {
  loadCoupons();
});

onMounted(() => {
  loadCoupons();
});
</script>

<style scoped>
.shop-coupons {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.btn-add {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-add:hover {
  background: #5568d3;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coupons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.coupon-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.coupon-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.coupon-name {
  font-size: 16px;
  font-weight: 600;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.3);
  color: white;
}

.status-active {
  background: rgba(39, 174, 96, 0.8);
}

.status-disabled {
  background: rgba(149, 165, 166, 0.8);
}

.coupon-content {
  padding: 20px;
  display: flex;
  gap: 20px;
}

.discount-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 80px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.discount-amount {
  font-size: 24px;
  font-weight: 600;
  color: #e74c3c;
}

.discount-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.coupon-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.detail-item .label {
  color: #999;
}

.detail-item .value {
  color: #333;
  font-weight: 500;
}

.coupon-actions {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
}

.btn-edit,
.btn-delete {
  flex: 1;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit {
  background: #3498db;
  color: white;
  text-decoration: none;
  display: block;
  text-align: center;
}

.btn-edit:hover {
  background: #2980b9;
}

.btn-delete {
  background: #e74c3c;
  color: white;
}

.btn-delete:hover {
  background: #c0392b;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  padding: 20px;
}

.page-btn {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #5568d3;
}

.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .coupons-grid {
    grid-template-columns: 1fr;
  }

  .coupon-content {
    flex-direction: column;
  }
}
</style>
