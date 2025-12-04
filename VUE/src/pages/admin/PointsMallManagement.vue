<template>
  <div class="page-container">
    <el-tabs type="border-card">
      <el-tab-pane label="积分商品管理">
        <div class="header mb-20">
          <el-button type="primary">新增积分商品</el-button>
        </div>
        <el-alert title="特色功能" type="info" :closable="false" class="mb-20">
          支持实体商品（需发货）和虚拟权益（如升级券二维码）配置。
          库存需开启实时倒计时显示。
        </el-alert>
        <el-table :data="[]" border>
          <el-table-column label="商品名称" />
          <el-table-column label="类型 (实体/虚拟)" />
          <el-table-column label="所需积分" />
          <el-table-column label="剩余库存" />
          <el-table-column label="状态" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="兑换订单审核">
        <div class="header mb-20">
          <el-button>导出Excel</el-button>
        </div>
        <el-table :data="[]" border>
          <el-table-column label="兑换单号" />
          <el-table-column label="用户" />
          <el-table-column label="兑换商品" />
          <el-table-column label="消耗积分" />
          <el-table-column label="操作">
            <template #default>
              <el-button size="small" type="primary">通过并扣分</el-button>
              <el-button size="small" type="danger">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.page-container { padding: 20px; }
.mb-20 { margin-bottom: 20px; }
</style>