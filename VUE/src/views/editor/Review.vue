<template>
  <div class="review">
    <h1>稿件管理</h1>
    <el-tabs>
      <el-tab-pane label="待初审">
        <el-table :data="pendingReview" stripe>
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="author" label="作者" width="120" />
           <el-table-column label="投稿日期" width="150">
          <template #default="{ row }">
            {{ formatDate(row.submissionDate) }}
          </template>
        </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default>
              <el-button link type="primary" size="small">审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="审稿中">
        <el-table :data="underReview" stripe>
          <el-table-column prop="id" label="稿件ID" width="100" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="reviewer" label="审稿人" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column label="操作" width="150">
            <template #default>
              <el-button link type="primary" size="small">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const pendingReview = ref([])
const underReview = ref([])
</script>

<style scoped>
.review {
  padding: 20px;
}

.review h1 {
  margin-bottom: 20px;
  color: #333;
}
</style>
