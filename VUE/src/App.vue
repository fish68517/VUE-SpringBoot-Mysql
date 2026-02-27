<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background: linear-gradient(180deg, #e0f2fe 0%, #fef3c7 100%);
  color: #111827;
}

#app {
  min-height: 100vh;
}

a {
  color: #2563eb;
  text-decoration: none;
}

.el-card {
  border-radius: 16px;
  border: 1px solid #c7d2fe;
  box-shadow: 0 14px 36px rgba(59, 130, 246, 0.18);
}

.el-card__header {
  text-align: center;
  font-weight: 600;
  color: #1d4ed8;
}

.el-card__body {
  text-align: center;
}

.el-table th.el-table__cell,
.el-table td.el-table__cell,
.el-table .cell {
  text-align: center;
}

.el-table th.el-table__cell {
  background-color: #e0f2fe;
}

.el-table__header .cell,
.el-table__body .cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.el-table__body .cell {
  gap: 8px;
  flex-wrap: wrap;
}

.el-form {
  margin: 0 auto;
}

.el-form--inline {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.el-form-item__label {
  text-align: center;
  justify-content: center;
  color: #1f2937;
}

.el-form-item__content {
  justify-content: center;
}

.el-input__wrapper {
  border-radius: 10px;
  background-color: #ffffff;
}

.el-button {
  border-radius: 10px;
}

.el-button--primary {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

.el-button--primary:hover,
.el-button--primary:focus {
  background-color: #2563eb;
  border-color: #2563eb;
}

.el-button--success {
  background-color: #22c55e;
  border-color: #22c55e;
}

.el-button--warning {
  background-color: #f59e0b;
  border-color: #f59e0b;
}

.el-button--danger {
  background-color: #ef4444;
  border-color: #ef4444;
}

.el-tag--success {
  background-color: #dcfce7;
  border-color: #22c55e;
  color: #15803d;
}

.el-tag--warning {
  background-color: #fef3c7;
  border-color: #f59e0b;
  color: #b45309;
}

.el-tag--danger {
  background-color: #fee2e2;
  border-color: #ef4444;
  color: #b91c1c;
}

.el-tag--info,
.el-tag--primary {
  background-color: #e0f2fe;
  border-color: #3b82f6;
  color: #1d4ed8;
}

.el-tabs__header {
  justify-content: center;
}

.el-dialog__header {
  text-align: center;
}

.el-dialog__body {
  text-align: center;
}

.el-descriptions__label,
.el-descriptions__content {
  text-align: center;
}

.layout-container .main {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.layout-container .main > * {
  width: 100%;
}
</style>
