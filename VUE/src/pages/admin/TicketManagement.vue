<template>
  <div class="page-container">
    <el-tabs type="border-card">
      <el-tab-pane label="场次与分区管理">
        <div class="tab-header">
          <el-button type="primary">新增演出场次</el-button>
          <el-button type="success">手动补票/减票</el-button>
        </div>
        <el-alert title="功能说明" type="warning" :closable="false" class="mb-20">
          设置分区库存（限流）、价格策略。
        </el-alert>
        <div class="placeholder-box">场次列表 / 分区库存管理表格区域</div>
      </el-tab-pane>
      
      <el-tab-pane label="票务订单列表">
        <div class="search-bar mb-20">
          <el-select placeholder="选择场次" style="width: 150px; margin-right: 10px;"></el-select>
          <el-input placeholder="身份证号/订单号" style="width: 200px; margin-right: 10px;" />
          <el-button type="primary">查询</el-button>
          <el-button>导出Excel</el-button>
        </div>
        <el-alert title="核心风控" type="error" :closable="false" class="mb-20">
          防黄牛检查：同一身份证号在同一场次只能购买一张。
        </el-alert>
        <el-table :data="[]" border>
          <el-table-column label="订单号" />
          <el-table-column label="购票人信息" />
          <el-table-column label="场次/分区" />
          <el-table-column label="状态" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.page-container { padding: 20px; }
.tab-header, .search-bar { display: flex; margin-bottom: 20px; }
.mb-20 { margin-bottom: 20px; }
.placeholder-box { height: 200px; border: 1px dashed #ccc; display: flex; align-items: center; justify-content: center; color: #999;}
</style>