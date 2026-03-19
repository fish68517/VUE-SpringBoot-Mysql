<template>
  <div class="dashboard">
    <h1>数据看板</h1>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-label">用户总数</div>
        <div class="stat-value">{{ dashboardData.totalUsers }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">店铺总数</div>
        <div class="stat-value">{{ dashboardData.totalShops }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">商品总数</div>
        <div class="stat-value">{{ dashboardData.totalProducts }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">订单总数</div>
        <div class="stat-value">{{ dashboardData.totalOrders }}</div>
      </div>
    </div>

    <div class="statistics-section">
      
      <div class="filter-group" v-if="false">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
        <el-button type="primary" @click="fetchStatistics">查询</el-button>
      </div>

      <div class="stats-grid small" v-if="false">
        <div class="stat-card small">
          <div class="stat-label">新增用户</div>
          <div class="stat-value">{{ statisticsData.newUsers }}</div>
        </div>
        <div class="stat-card small">
          <div class="stat-label">新增订单</div>
          <div class="stat-value">{{ statisticsData.newOrders }}</div>
        </div>
        <div class="stat-card small">
          <div class="stat-label">交易金额</div>
          <div class="stat-value">¥{{ statisticsData.totalAmount }}</div>
        </div>
      </div>
    </div>

    <div class="charts-section">
      <h2>分类商品统计（库存/销量）</h2>
      <div id="categoryChart" style="width: 100%; height: 420px"></div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from "vue";
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

const formatDateTime = (d, endOfDay = false) => {
  const date = new Date(d);
  if (endOfDay) {
    date.setHours(23, 59, 59, 0);
  } else {
    date.setHours(0, 0, 0, 0);
  }
  const pad = (n) => String(n).padStart(2, "0");
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const fetchDashboard = async () => {
  try {
    dashboardData.value = await adminApi.getDashboard();
  } catch (error) {
    console.error(error);
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
      startTime: formatDateTime(dateRange.value[0], false),
      endTime: formatDateTime(dateRange.value[1], true)
    };

    statisticsData.value = await adminApi.getDashboardStatistics(params);
  } catch (error) {
    console.error(error);
    ElMessage.error("获取统计数据失败");
  }
};

const fetchCategoryProductStatistics = async () => {
  try {
    const [categoryData, productData] = await Promise.all([
      adminApi.getDashboardCategory(),
      adminApi.getDashboardProduct()
    ]);

    const categoryNames = Array.from(
      new Set([
        ...Object.keys(categoryData || {}),
        ...Object.keys(productData || {})
      ])
    );

    const chartData = categoryNames.map((name) => ({
      categoryName: name,
      stock: Number(productData?.[name]?.stock ?? 0),
      sales: Number(productData?.[name]?.sales ?? 0)
    }));

    renderCategoryChart(chartData);
  } catch (error) {
    console.error(error);
    ElMessage.error("获取分类商品统计失败");
  }
};

const renderCategoryChart = (data) => {
  if (!categoryChart) {
    const chartDom = document.getElementById("categoryChart");
    if (!chartDom) return;
    categoryChart = echarts.init(chartDom);
  }

  categoryChart.setOption({
    tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
    legend: { data: ["库存", "销量"] },
    grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
    xAxis: {
      type: "category",
      data: data.map((item) => item.categoryName)
    },
    yAxis: [
      { type: "value", name: "库存" },
      { type: "value", name: "销量" }
    ],
    series: [
      {
        name: "库存",
        type: "bar",
        yAxisIndex: 0,
        data: data.map((item) => item.stock)
      },
      {
        name: "销量",
        type: "line",
        smooth: true,
        yAxisIndex: 1,
        data: data.map((item) => item.sales)
      }
    ]
  });
};

onMounted(async () => {
  await fetchDashboard();
  await fetchCategoryProductStatistics();
});

onBeforeUnmount(() => {
  if (categoryChart) {
    categoryChart.dispose();
    categoryChart = null;
  }
});
</script>

<style scoped>
.dashboard {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

h1 {
  margin: 0 0 20px;
  font-size: 24px;
  color: #333;
}

h2 {
  margin: 20px 0 15px;
  font-size: 18px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 14px;
  background: #fafafa;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.filter-group {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 12px;
}

.charts-section {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .filter-group {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
