<template>
  <div class="page-container">
    <div class="page-header">
      <h2>数据仪表盘 (Dashboard)</h2>
      <el-button type="primary">导出报表图片</el-button>
    </div>

    <div class="placeholder-content">
      <el-alert title="功能说明" type="info" :closable="false" show-icon>
        此处将集成 ECharts 展示以下数据：
        <ul>
          <li>今日/近7日/近30日订单量趋势（票务+商品）</li>
          <li>用户注册增长曲线</li>
          <li>分区售票实时热力图（剩余票量/售罄速度）</li>
          <li>商品销量TOP10</li>
          <li>打卡任务完成统计（通过率分析）</li>
          <li>实时天气与音乐节倒计时</li>
        </ul>
      </el-alert>
      
      <div class="chart-grid">
        <div class="chart-placeholder">订单趋势图表占位</div>
        <div class="chart-placeholder">售票热力图占位</div>
        <div class="chart-placeholder">商品排行占位</div>
        <div class="chart-placeholder">打卡统计占位</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}
.chart-placeholder {
  height: 300px;
  background: #f5f7fa;
  border: 2px dashed #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  border-radius: 4px;
}
</style>