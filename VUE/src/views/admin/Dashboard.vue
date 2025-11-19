<template>
  <div class="dashboard">
    <h1>数据看板</h1>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon users">👥</div>
        <div class="stat-content">
          <div class="stat-label">用户总数</div>
          <div class="stat-value">{{ dashboardData.totalUsers }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon shops">🏪</div>
        <div class="stat-content">
          <div class="stat-label">店铺总数</div>
          <div class="stat-value">{{ dashboardData.totalShops }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon products">📦</div>
        <div class="stat-content">
          <div class="stat-label">商品总数</div>
          <div class="stat-value">{{ dashboardData.totalProducts }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orders">📋</div>
        <div class="stat-content">
          <div class="stat-label">订单总数</div>
          <div class="stat-value">{{ dashboardData.totalOrders }}</div>
        </div>
      </div>
    </div>

    <!-- 时间范围统计 -->
    <div class="statistics-section">
      <h2>时间范围统计</h2>
      <div class="filter-group">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
        <el-button type="primary" @click="fetchStatistics">查询</el-button>
      </div>
      
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <div class="stat-label">新增用户</div>
            <div class="stat-value">{{ statisticsData.newUsers }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <div class="stat-label">新增订单</div>
            <div class="stat-value">{{ statisticsData.newOrders }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <div class="stat-label">交易金额</div>
            <div class="stat-value">¥{{ statisticsData.totalAmount }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="charts-section">
      <div class="chart-container">
        <h2>分类商品统计</h2>
        <div id="categoryChart" style="width: 100%; height: 400px;"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import * as echarts from "echarts";
import * as adminApi from "@/api/admin";

const dashboardData = ref({
  totalUsers: 0,
  totalShops: 0,
  totalProducts: 0,
  totalOrders: 0
});

const statisticsData = ref({
  newUsers: 0,
  newOrders: 0,
  totalAmount: 0
});

const dateRange = ref([]);
let categoryChart = null;

const fetchDashboard = async () => {
  try {
    const data = await adminApi.getDashboard();
    dashboardData.value = data;
  } catch (error) {
    ElMessage.error("获取数据看板失败");
  }
};

const fetchStatistics = async () => {
  try {
    if (!dateRange.value || dateRange.value.length !== 2) {
      ElMessage.warning("请选择时间范围");
      return;
    }
    
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    };
    
    const data = await adminApi.getDashboardStatistics(params);
    statisticsData.value = data;
  } catch (error) {
    ElMessage.error("获取统计数据失败");
  }
};

const fetchCategoryStatistics = async () => {
  try {
    const data = await adminApi.getDashboardCategory();
    renderCategoryChart(data);
  } catch (error) {
    ElMessage.error("获取分类统计失败");
  }
};

const renderCategoryChart = (data) => {
  if (!categoryChart) {
    const chartDom = document.getElementById("categoryChart");
    categoryChart = echarts.init(chartDom);
  }

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow"
      }
    },
    legend: {
      data: ["商品数量", "销售额"]
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true
    },
    xAxis: {
      type: "category",
      data: data.map(item => item.categoryName)
    },
    yAxis: [
      {
        type: "value",
        name: "商品数量"
      },
      {
        type: "value",
        name: "销售额"
      }
    ],
    series: [
      {
        name: "商品数量",
        type: "bar",
        data: data.map(item => item.productCount),
        yAxisIndex: 0
      },
      {
        name: "销售额",
        type: "line",
        data: data.map(item => item.salesAmount),
        yAxisIndex: 1
      }
    ]
  };

  categoryChart.setOption(option);
};

const handleDateChange = () => {
  // 日期变化时的处理
};

onMounted(() => {
  fetchDashboard();
  fetchCategoryStatistics();
});
</script>

<style scoped>
.dashboard {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

h1 {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #333;
}

h2 {
  margin: 20px 0 15px 0;
  font-size: 18px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.shops {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.products {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.orders {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon {
  font-size: 32px;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.statistics-section {
  margin-bottom: 30px;
}

.filter-group {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.charts-section {
  margin-top: 30px;
}

.chart-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-card {
    padding: 15px;
  }

  .stat-icon {
    font-size: 24px;
    margin-right: 10px;
  }

  .stat-value {
    font-size: 20px;
  }

  .filter-group {
    flex-direction: column;
  }
}
</style>
