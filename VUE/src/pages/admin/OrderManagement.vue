<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品订单管理</h2>
      <el-button type="success">导出订单Excel</el-button>
    </div>

    <el-tabs model-value="paid">
      <el-tab-pane label="已支付" name="paid" />
      <el-tab-pane label="已发货" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
    </el-tabs>

    <div class="content-area">
      <el-alert title="功能说明" type="info" :closable="false" class="mb-20">
        在此处修改物流单号、模拟发货操作。本系统不支持退款功能。
      </el-alert>

      <el-table :data="[]" border>
        <el-table-column label="订单号" />
        <el-table-column label="收货人信息" />
        <el-table-column label="商品明细" />
        <el-table-column label="总金额" />
        <el-table-column label="物流单号" />
        <el-table-column label="操作">
          <template #default>
            <el-button size="small" type="primary">发货/填单号</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.page-container { background: white; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.mb-20 { margin-bottom: 20px; }
</style>