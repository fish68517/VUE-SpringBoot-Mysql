<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="actions">
        <el-input placeholder="搜索用户(手机号/姓名)" style="width: 200px; margin-right: 10px;" />
        <el-button type="success">导出Excel</el-button>
      </div>
    </div>

    <el-alert title="功能说明" type="success" :closable="false" class="mb-20">
      用户实名信息查看（脱敏）、封禁/解封用户、积分手动调整/冻结、登录日志查看。
    </el-alert>

    <el-table :data="[]" border style="width: 100%">
      <el-table-column label="ID" width="80" />
      <el-table-column label="手机号" />
      <el-table-column label="昵称" />
      <el-table-column label="实名姓名" />
      <el-table-column label="身份证号 (后四位明文)" />
      <el-table-column label="当前积分" />
      <el-table-column label="状态 (正常/封禁)" />
      <el-table-column label="操作" width="250">
        <template #default>
          <el-button size="small">查看详情</el-button>
          <el-button size="small" type="warning">调整积分</el-button>
          <el-button size="small" type="danger">封禁</el-button>
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