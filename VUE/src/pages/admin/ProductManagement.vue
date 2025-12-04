<template>
  <div class="page-container">
    <div class="page-header">
      <h2>周边商品管理</h2>
      <div>
        <el-button>分类管理</el-button>
        <el-button type="primary">发布新商品</el-button>
      </div>
    </div>

    <el-alert title="库存预警" type="warning" show-icon class="mb-20">
      当前有 3 个商品库存低于 10 件，请及时补货。
    </el-alert>

    <el-table :data="[]" border style="width: 100%">
      <el-table-column label="商品图片" width="100" />
      <el-table-column label="商品名称" />
      <el-table-column label="规格" />
      <el-table-column label="原价/现价" />
      <el-table-column label="库存">
         <template #default>
           <span>100</span>
         </template>
      </el-table-column>
      <el-table-column label="状态 (上架/下架)" />
      <el-table-column label="操作">
        <template #default>
          <el-button size="small">编辑</el-button>
          <el-button size="small" type="danger">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page-container { background: white; padding: 20px; border-radius: 8px; }
.page-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.mb-20 { margin-bottom: 20px; }
</style>