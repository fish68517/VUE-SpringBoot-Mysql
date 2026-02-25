<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card class="welcome-card">
          <h2>欢迎来到广州旅游管理系统</h2>
          <p>发现广州的美景、美食和文化特色</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card">
          <div class="feature-icon">🏛️</div>
          <h3>景点门票</h3>
          <p>预订广州特色景点门票</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card">
          <div class="feature-icon">🏨</div>
          <h3>酒店预订</h3>
          <p>预订舒适的酒店住宿</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card">
          <div class="feature-icon">🛍️</div>
          <h3>旅游商品</h3>
          <p>购买广州特色旅游商品</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card">
          <div class="feature-icon">🗺️</div>
          <h3>推荐路线</h3>
          <p>获取个性化旅游路线推荐</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
            </div>
          </template>
          <el-empty description="暂无公告" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
}

.welcome-card p {
  margin: 0;
  font-size: 16px;
}

.feature-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.feature-card h3 {
  margin: 10px 0;
  color: #333;
}

.feature-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
