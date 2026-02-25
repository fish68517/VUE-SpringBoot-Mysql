<template>
  <div class="detail-container">
    <el-card>
      <el-row :gutter="20">
        <el-col :xs="24" :md="12">
          <el-image 
            src="https://via.placeholder.com/400x300" 
            fit="cover"
            style="width: 100%; height: 300px"
          />
        </el-col>
        <el-col :xs="24" :md="12">
          <h2>景点名称</h2>
          <div class="info-item">
            <span class="label">位置:</span>
            <span>广州市</span>
          </div>
          <div class="info-item">
            <span class="label">门票价格:</span>
            <span class="price">¥50</span>
          </div>
          <div class="info-item">
            <span class="label">营业时间:</span>
            <span>09:00 - 18:00</span>
          </div>
          <div class="info-item">
            <span class="label">描述:</span>
            <p>景点详细描述信息</p>
          </div>
          <el-button type="primary" size="large" style="width: 100%; margin-top: 20px">
            预订门票
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>用户评价</span>
        </div>
      </template>
      <el-empty description="暂无评价" />
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.detail-container {
  padding: 20px;
}

.info-item {
  margin: 15px 0;
  display: flex;
  align-items: flex-start;
}

.label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.price {
  color: #ff6b6b;
  font-size: 20px;
  font-weight: bold;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
