<template>
  <div class="page-container">
    <div class="page-header">
      <h2>打卡任务管理 (盛京文化)</h2>
      <el-button type="primary">新增打卡任务</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card header="任务配置">
          <el-alert title="技术亮点" type="success" :closable="false" class="mb-20">
            支持 LBS 地理围栏设置 (经纬度 + 半径范围)。
          </el-alert>
          <div class="placeholder-list">
            <p>任务1：故宫打卡 (积分: 50)</p>
            <p>任务2：中街美食 (积分: 30)</p>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card header="打卡审核列表">
           <div class="actions mb-20">
             <el-button type="success" size="small">批量通过</el-button>
             <el-button type="danger" size="small">批量拒绝</el-button>
           </div>
           <el-table :data="[]" border>
             <el-table-column label="用户" />
             <el-table-column label="上传图片" />
             <el-table-column label="地理位置验证" />
             <el-table-column label="操作" />
           </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.mb-20 { margin-bottom: 20px; }
.placeholder-list p { padding: 10px; border-bottom: 1px solid #eee; }
</style>