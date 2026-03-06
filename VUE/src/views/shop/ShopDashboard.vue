<template>
  <div class="shop-dashboard">
    <div class="dashboard-header">
      <h1>店铺首页</h1>
      <p>欢迎回到您的店铺</p>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📦</div>
          <div class="stat-content">
            <div class="stat-label">商品总数</div>
            <div class="stat-value">{{ shopStats.productCount || 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📋</div>
          <div class="stat-content">
            <div class="stat-label">订单总数</div>
            <div class="stat-value">{{ shopStats.orderCount || 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <div class="stat-label">总销售额</div>
            <div class="stat-value">¥{{ shopStats.totalSales || 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-content">
            <div class="stat-label">店铺评分</div>
            <div class="stat-value">{{ shopStats.rating || 0 }}</div>
          </div>
        </div>
      </div>

      <!-- 快速操作 -->
      <div class="quick-actions">
        <h2>快速操作</h2>
        <div class="action-buttons">
          <router-link to="/shop/products" class="action-btn">
            <span class="icon">➕</span>
            <span>添加商品</span>
          </router-link>
          <router-link to="/shop/orders" class="action-btn">
            <span class="icon">📦</span>
            <span>查看订单</span>
          </router-link>
          <router-link to="/shop/info" class="action-btn">
            <span class="icon">⚙️</span>
            <span>店铺设置</span>
          </router-link>
          <router-link to="/shop/coupons" class="action-btn">
            <span class="icon">🎟️</span>
            <span>优惠券</span>
          </router-link>
        </div>
      </div>

      <!-- 店铺信息 -->
      <div class="shop-info-section">
        <h2>店铺信息</h2>
        <div class="info-card">
          <div class="info-item">
            <span class="label">店铺名称：</span>
            <span class="value">{{ shopInfo.name || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">店铺状态：</span>
            <span class="value" :class="getStatusClass(shopInfo.status)">
              {{ getStatusText(shopInfo.status) }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">创建时间：</span>
            <span class="value">{{ formatDate(shopInfo.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getShopInfo, getShopProductList, getShopOrderList } from "@/api/shop";
import { useUserStore } from "@/store/userStore";

const userStore = useUserStore();
const loading = ref(false);
const shopInfo = ref({});
const shopStats = ref({
  productCount: 0,
  orderCount: 0,
  totalSales: 0,
  rating: 0
});

const getStatusText = (status) => {
  const statusMap = {
    0: "待审核",
    1: "正常",
    2: "禁用"
  };
  return statusMap[status] || "未知";
};

const getStatusClass = (status) => {
  const classMap = {
    0: "status-pending",
    1: "status-active",
    2: "status-disabled"
  };
  return classMap[status] || "";
};

const formatDate = (date) => {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("zh-CN");
};

const loadShopData = async () => {
  loading.value = true;
  try {
    // 获取店铺信息
    const shopId = userStore.userInfo?.shopId;
    console.log("获取店铺信息成功:", shopId);
    const shopData = await getShopInfo(shopId);
    console.log("获取店铺信息成功:", shopData);
    shopInfo.value = shopData || {};

    // 获取商品列表统计
    const productsData = await getShopProductList({ shopId:shopId, page: 1, size: 1 });
    shopStats.value.productCount = productsData?.total || 0;

    // 获取订单列表统计
    const ordersData = await getShopOrderList({ shopId:shopId,page: 1, size: 1 });
    shopStats.value.orderCount = ordersData?.total || 0;

    // 计算总销售额（简化版，实际应从后端获取）
    shopStats.value.totalSales = 0;
    shopStats.value.rating = 4.5;
  } catch (error) {
    console.error("加载店铺数据失败:", error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadShopData();
});
</script>

<style scoped>
.shop-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 30px;
}

.dashboard-header h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #333;
}

.dashboard-header p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  font-size: 32px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #999;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.quick-actions {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.quick-actions h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.3s ease;
  gap: 10px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.action-btn .icon {
  font-size: 24px;
}

.action-btn span:last-child {
  font-size: 14px;
}

.shop-info-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.shop-info-section h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
}

.info-card {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  color: #999;
  font-size: 14px;
}

.info-item .value {
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.status-pending {
  color: #f39c12;
}

.status-active {
  color: #27ae60;
}

.status-disabled {
  color: #e74c3c;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
