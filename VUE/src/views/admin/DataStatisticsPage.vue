<template>
  <div>
    <el-card>
       <template #header>数据统计分析</template>

        <el-form :inline="true" :model="filterOptions">
            <el-form-item label="时间范围">
                <el-date-picker
                    v-model="filterOptions.dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="YYYY-MM-DD"
                    @change="handleDateChange"
                />
            </el-form-item>
             <el-form-item>
                <el-button type="primary" @click="fetchStatisticsData">查询</el-button>
            </el-form-item>
        </el-form>

        <el-divider />

        <el-row :gutter="20">
            <el-col :span="12">
                <el-card shadow="never">
                  <template #header>出入库流水趋势 (按天)</template>
                  <v-chart class="chart" :option="flowTrendOption" autoresize />
                </el-card>
            </el-col>
            <el-col :span="12">
                 <el-card shadow="never">
                  <template #header>产品出库量Top 10 (按数量)</template>
                  <v-chart class="chart" :option="topProductsOption" autoresize />
                </el-card>
            </el-col>
        </el-row>

        <el-divider />

        <el-row style="margin-top: 20px;">
            <el-col :span="24">
                <el-card shadow="never">
                    <template #header>
                        <div class="card-header">
                            <span>产品出入库明细统计</span>
                            <el-input
                                v-model="searchText"
                                placeholder="搜索产品名称或SKU"
                                clearable
                                style="width: 200px;"
                                @input="filterTableData"
                            />
                        </div>
                    </template>
                    <el-table :data="filteredTableData" v-loading="loading" stripe height="400px">
                        <el-table-column prop="productName" label="产品名称" sortable />
                        <el-table-column prop="productSku" label="SKU" sortable />
                        <el-table-column prop="inboundCount" label="入库次数" sortable />
                        <el-table-column prop="inboundQuantity" label="入库总量" sortable />
                        <el-table-column prop="outboundCount" label="出库次数" sortable />
                        <el-table-column prop="outboundQuantity" label="出库总量" sortable />
                        <el-table-column prop="currentStock" label="当前库存" sortable />
                    </el-table>
                </el-card>
            </el-col>
        </el-row>

    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, provide } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api/NetWorkApi.js';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, BarChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent, // 导入数据缩放组件
  ToolboxComponent // 导入工具箱组件
} from 'echarts/components';
import VChart, { THEME_KEY } from 'vue-echarts';

// ECharts 注册
use([
    CanvasRenderer, LineChart, BarChart, TitleComponent, TooltipComponent,
    LegendComponent, GridComponent, DataZoomComponent, ToolboxComponent
]);
provide(THEME_KEY, 'light'); // 或者 'dark'

const loading = ref(true);
const allLogs = ref([]); // 存储原始日志数据
const allProducts = ref([]);
const allInventory = ref([]);

// 筛选条件
const filterOptions = reactive({
  dateRange: [], // [startDate, endDate]
});

// 图表配置
const flowTrendOption = ref({});
const topProductsOption = ref({});

// 表格数据
const tableData = ref([]);
const searchText = ref('');
const filteredTableData = computed(() => {
    if (!searchText.value) {
        return tableData.value;
    }
    const searchLower = searchText.value.toLowerCase();
    return tableData.value.filter(item =>
        item.productName.toLowerCase().includes(searchLower) ||
        item.productSku.toLowerCase().includes(searchLower)
    );
});

// 获取所有基础数据
const fetchBaseData = async () => {
    loading.value = true;
    try {
        const [logsRes, productsRes, inventoryRes] = await Promise.all([
            api.transactionLogsApi.list(),
            api.productsApi.list(),
            api.inventoryApi.list()
        ]);
        allLogs.value = logsRes.data || [];
        allProducts.value = productsRes.data || [];
        allInventory.value = inventoryRes.data || [];

        // 设置默认时间范围为最近7天
        setDefaultDateRange(7);
        // 获取并处理统计数据
        processStatisticsData();

    } catch (error) {
        console.error("加载基础数据失败:", error);
        ElMessage.error("数据加载失败");
    } finally {
        loading.value = false;
    }
};

// 设置默认日期范围
const setDefaultDateRange = (days) => {
    const end = new Date();
    const start = new Date();
    start.setTime(start.getTime() - 3600 * 1000 * 24 * days);
    filterOptions.dateRange = [formatDate(start), formatDate(end)];
};

// 格式化日期为 YYYY-MM-DD
const formatDate = (date) => {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
};

// 处理日期范围变化
const handleDateChange = () => {
    // 日期变化时可以自动查询，或者保留查询按钮手动触发
    // fetchStatisticsData();
};

// 获取并处理统计数据
const fetchStatisticsData = () => {
    loading.value = true;
    // 模拟异步，实际项目中如果数据量大，这些统计应由后端完成
    setTimeout(() => {
        processStatisticsData();
        loading.value = false;
    }, 300); // 延迟模拟处理
};

// 核心数据处理逻辑
const processStatisticsData = () => {
    const startDate = filterOptions.dateRange ? filterOptions.dateRange[0] : null;
    const endDate = filterOptions.dateRange ? filterOptions.dateRange[1] : null;

    // 1. 筛选时间范围内的日志
    const filteredLogs = allLogs.value.filter(log => {
        if (!startDate || !endDate) return true; // 没有日期范围则包含所有
        const logDate = log.createdAt.substring(0, 10);
        return logDate >= startDate && logDate <= endDate;
    });

    // 2. 准备出入库流水趋势数据
    const flowTrend = {};
    if (startDate && endDate) {
        let currentDate = new Date(startDate);
        const lastDate = new Date(endDate);
        while (currentDate <= lastDate) {
            const dateStr = formatDate(currentDate);
            flowTrend[dateStr] = { date: dateStr, inbound: 0, outbound: 0 };
            currentDate.setDate(currentDate.getDate() + 1);
        }
    }

    filteredLogs.forEach(log => {
        const dateStr = log.createdAt.substring(0, 10);
        if (flowTrend[dateStr]) {
            if (log.type === '入库') {
                flowTrend[dateStr].inbound += Math.abs(log.quantityChange);
            } else if (log.type === '出库') {
                flowTrend[dateStr].outbound += Math.abs(log.quantityChange);
            }
        }
    });
    const trendData = Object.values(flowTrend).sort((a,b) => a.date.localeCompare(b.date));


    // 3. 准备产品出库量Top 10数据
    const inventoryMap = new Map(allInventory.value.map(i => [i.id, i.productId]));
    const productOutbound = {};
    filteredLogs.forEach(log => {
        if (log.type === '出库') {
            const productId = inventoryMap.get(log.inventoryId);
            if (productId) {
                productOutbound[productId] = (productOutbound[productId] || 0) + Math.abs(log.quantityChange);
            }
        }
    });

    const productsMap = new Map(allProducts.value.map(p => [p.id, p]));
    const topProductsData = Object.entries(productOutbound)
        .map(([productId, quantity]) => ({
            productId: parseInt(productId),
            productName: productsMap.get(parseInt(productId))?.name || '未知产品',
            quantity: quantity
        }))
        .sort((a, b) => b.quantity - a.quantity)
        .slice(0, 10); // 取前10

    // 4. 准备表格数据
    const productStats = {};
    allProducts.value.forEach(p => {
        productStats[p.id] = {
            productId: p.id,
            productName: p.name,
            productSku: p.sku,
            inboundCount: 0,
            inboundQuantity: 0,
            outboundCount: 0,
            outboundQuantity: 0,
            currentStock: 0
        };
    });

    filteredLogs.forEach(log => {
        const productId = inventoryMap.get(log.inventoryId);
        if (productId && productStats[productId]) {
            if (log.type === '入库') {
                productStats[productId].inboundCount++;
                productStats[productId].inboundQuantity += Math.abs(log.quantityChange);
            } else if (log.type === '出库') {
                productStats[productId].outboundCount++;
                productStats[productId].outboundQuantity += Math.abs(log.quantityChange);
            }
        }
    });
    
    // 计算当前库存
    allInventory.value.forEach(item => {
        if (productStats[item.productId]) {
            productStats[item.productId].currentStock += item.quantity;
        }
    });

    tableData.value = Object.values(productStats);

    // 更新图表
    updateCharts(trendData, topProductsData);
};


// 更新 ECharts 图表
const updateCharts = (trendData, topProductsData) => {
    // 出入库流水趋势图
    flowTrendOption.value = {
        tooltip: { trigger: 'axis' },
        legend: { data: ['入库总量', '出库总量'] },
        grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
        toolbox: { feature: { saveAsImage: {} } },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: trendData.map(d => d.date)
        },
        yAxis: { type: 'value', name: '数量' },
        dataZoom: [{ type: 'inside' }, { type: 'slider' }], // 添加缩放
        series: [
            {
                name: '入库总量',
                type: 'line',
                smooth: true,
                data: trendData.map(d => d.inbound),
                itemStyle: { color: '#67C23A' }
            },
            {
                name: '出库总量',
                type: 'line',
                smooth: true,
                data: trendData.map(d => d.outbound),
                itemStyle: { color: '#F56C6C' }
            }
        ]
    };

    // 产品出库量Top 10图
    topProductsOption.value = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value', boundaryGap: [0, 0.01] },
        yAxis: {
            type: 'category',
            data: topProductsData.map(p => p.productName).reverse(), // 反转使最大值在顶部
            axisLabel: {
                 interval: 0, // 强制显示所有标签
                 formatter: function (value) { // 标签过长时截断
                     return value.length > 8 ? value.substring(0, 8) + '...' : value;
                 }
            }
        },
        series: [
            {
                name: '出库数量',
                type: 'bar',
                data: topProductsData.map(p => p.quantity).reverse(), // 反转数据
                itemStyle: { color: '#E6A23C' }
            }
        ]
    };
};


onMounted(fetchBaseData);

</script>

<style scoped>
.chart {
  height: 400px; /* 调整图表高度 */
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>