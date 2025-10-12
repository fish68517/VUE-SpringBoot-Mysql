<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总用户数" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="产品种类数 (SKU)" :value="stats.totalProducts" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总库存量 (件)" :value="stats.totalStockQuantity" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="待处理入库单" :value="stats.pendingInboundOrders" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div>库存产品分布</div>
          </template>
          <v-chart class="chart" :option="stockPieOption" autoresize />
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <div>近7日出入库动态</div>
          </template>
          <v-chart class="chart" :option="transactionBarOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

     <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
            <el-card shadow="never">
                <template #header>
                    <div>入库单状态分布</div>
                </template>
                <v-chart class="chart" :option="inboundOrderStatusPieOption" autoresize />
            </el-card>
        </el-col>
     </el-row>

  </div>
</template>

<script setup>
import { reactive, onMounted, provide, ref } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { PieChart, BarChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components';
import VChart, { THEME_KEY } from 'vue-echarts';
import api from '../../api/NetWorkApi.js';
import { ElMessage } from 'element-plus';

// ECharts 注册
use([CanvasRenderer, PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);
provide(THEME_KEY, 'light');

// 顶部统计数据
const stats = reactive({
  totalUsers: 0,
  totalProducts: 0,
  totalStockQuantity: 0,
  pendingInboundOrders: 0,
});

// ECharts 图表配置
const stockPieOption = ref({});
const transactionBarOption = ref({});
const inboundOrderStatusPieOption = ref({});

onMounted(async () => {
  try {
    // 并发请求所有需要的数据
    const [usersRes, productsRes, inventoryRes, inboundOrdersRes, transactionLogsRes] = await Promise.all([
      api.usersApi.list(),
      api.productsApi.list(),
      api.inventoryApi.list(),
      api.inboundOrdersApi.list(),
      api.transactionLogsApi.list(),
    ]);

    const allUsers = usersRes.data || [];
    const allProducts = productsRes.data || [];
    const allInventory = inventoryRes.data || [];
    const allInboundOrders = inboundOrdersRes.data || [];
    const allTransactionLogs = transactionLogsRes.data || [];

    // 1. 计算顶部统计卡片数据
    stats.totalUsers = allUsers.length;
    stats.totalProducts = allProducts.length;
    stats.totalStockQuantity = allInventory.reduce((sum, item) => sum + item.quantity, 0);
    stats.pendingInboundOrders = allInboundOrders.filter(order => order.status === '待处理').length;

    // 2. 准备库存产品分布饼图数据
    const productsMap = new Map(allProducts.map(p => [p.id, p.name]));
    const stockDistribution = allInventory.reduce((acc, item) => {
      const productName = productsMap.get(item.productId) || '未知产品';
      acc[productName] = (acc[productName] || 0) + item.quantity;
      return acc;
    }, {});
    const pieData = Object.keys(stockDistribution).map(key => ({
      name: key,
      value: stockDistribution[key]
    }));

    // 3. 准备近7日出入库动态柱状图数据
    const last7Days = Array.from({ length: 7 }, (_, i) => {
      const d = new Date();
      d.setDate(d.getDate() - i);
      return d.toISOString().split('T')[0];
    }).reverse();

    const dailyTransactions = last7Days.map(day => {
        const dayLogs = allTransactionLogs.filter(log => log.createdAt.startsWith(day));
        return {
            inbound: dayLogs.filter(log => log.type === '入库').length,
            outbound: dayLogs.filter(log => log.type === '出库').length,
        };
    });

    // 4. 准备入库单状态分布数据
    const orderStatusCounts = allInboundOrders.reduce((acc, order) => {
        const status = order.status || '未知状态';
        acc[status] = (acc[status] || 0) + 1;
        return acc;
    }, {});
    const orderStatusPieData = Object.keys(orderStatusCounts).map(key => ({
        name: key,
        value: orderStatusCounts[key]
    }));


    // 更新 ECharts 配置
    updateCharts(pieData, last7Days.map(d => d.slice(5)), dailyTransactions, orderStatusPieData);

  } catch (error) {
    console.error("加载管理后台统计数据失败:", error);
    ElMessage.error("加载仪表盘数据失败，请检查网络连接或联系管理员");
  }
});

// 更新所有图表的配置
const updateCharts = (pieData, barXAxis, barSeries, orderStatusPieData) => {
  // 库存产品分布图
  stockPieOption.value = {
    tooltip: { trigger: 'item', formatter: '{b} : {c}件 ({d}%)' },
    legend: {
        orient: 'vertical',
        left: 'left',
        top: 'center'
    },
    series: [{
      name: '库存分布',
      type: 'pie',
      radius: '80%',
      center: ['65%', '50%'],
      data: pieData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  };

  // 近7日出入库动态图
  transactionBarOption.value = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['入库次数', '出库次数'] },
    xAxis: { type: 'category', data: barXAxis },
    yAxis: { type: 'value' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [
      {
        name: '入库次数',
        type: 'bar',
        stack: 'total',
        data: barSeries.map(d => d.inbound),
        color: '#67C23A'
      },
      {
        name: '出库次数',
        type: 'bar',
        stack: 'total',
        data: barSeries.map(d => d.outbound),
        color: '#F56C6C'
      }
    ]
  };

  // 入库单状态分布图
  inboundOrderStatusPieOption.value = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { top: 'bottom' },
    series: [
        {
            name: '入库单状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: {
                show: false,
                position: 'center'
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 20,
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
            data: orderStatusPieData
        }
    ]
  };
};

</script>

<style scoped>
.stat-card {
  text-align: center;
}
.chart {
  height: 350px;
}
</style>