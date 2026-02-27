<template>
  <div class="admin-page-container">
    <el-card shadow="never" class="box-card">
      <template #header>
        <div class="card-header">
          <span>农资产品管理</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">添加新农资</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="filters" class="search-form">
        <el-form-item label="产品名称">
          <el-input v-model="filters.productName" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.category" placeholder="选择分类" clearable style="width: 150px">
            <el-option label="肥料" value="肥料" />
            <el-option label="农药" value="农药" />
            <el-option label="种子" value="种子" />
            <el-option label="支架材料" value="支架材料" />
            <el-option label="灌溉设备" value="灌溉设备" />
            <el-option label="防护用品" value="防护用品" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="Search">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="products" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="productName" label="产品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元)" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存数量" width="100" align="center" />
        <el-table-column prop="applicableWeather" label="适用天气" width="120" />
        <el-table-column prop="createdAt" label="上架时间" width="180" />
        
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑农资产品' : '添加新农资产品'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="产品名称" required>
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="产品类别" required v-if="!isEdit">
          <el-select v-model="form.category" placeholder="选择类别" style="width: 100%;">
            <el-option label="肥料" value="肥料" />
            <el-option label="农药" value="农药" />
            <el-option label="种子" value="种子" />
            <el-option label="防护用品" value="防护用品" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :precision="2" :step="1" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="库存" required v-if="!isEdit">
          <el-input-number v-model="form.stock" :min="0" :step="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="适用天气">
          <el-input v-model="form.applicableWeather" placeholder="如：晴天、阴天" />
        </el-form-item>
        <el-form-item label="产品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入产品详细说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productAPI } from '@/api/product'

const loading = ref(false)
const submitLoading = ref(false)
const products = ref([])
const filters = ref({ productName: '', category: '' })
const pagination = ref({ current: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = ref({
  productName: '', category: '', price: 0, stock: 0, applicableWeather: '', description: ''
})

// 查询与分页
const handleSearch = async () => {
  loading.value = true
  try {
    const res = await productAPI.getProducts({
      productName: filters.value.productName,
      category: filters.value.category
    })
    let allData = res.data || res || []
    pagination.value.total = allData.length
    
    const startIdx = (pagination.value.current - 1) * pagination.value.size
    const endIdx = startIdx + pagination.value.size
    products.value = allData.slice(startIdx, endIdx)
  } catch (error) {
    ElMessage.error('获取产品列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => { pagination.value.current = page; handleSearch() }
const handlePageSizeChange = (size) => { pagination.value.size = size; pagination.value.current = 1; handleSearch() }

// 新增
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.value = { productName: '', category: '', price: 0, stock: 0, applicableWeather: '', description: '' }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  form.value = {
    productName: row.productName,
    category: row.category,
    price: row.price,
    stock: row.stock,
    applicableWeather: row.applicableWeather,
    description: row.description
  }
  dialogVisible.value = true
}

// 提交
const submitForm = async () => {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await productAPI.updateProduct(currentId.value, {
        productName: form.value.productName,
        description: form.value.description,
        price: form.value.price,
        applicableWeather: form.value.applicableWeather
      })
      ElMessage.success('产品更新成功')
    } else {
      const userStr = localStorage.getItem('user')
      const merchantId = userStr ? JSON.parse(userStr).userId : 1
      await productAPI.createProduct({ ...form.value, merchantId })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该农资产品吗?', '警告', { type: 'warning' })
    .then(async () => {
      try {
        await productAPI.deleteProduct(row.id)
        ElMessage.success('删除成功')
        handleSearch()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    }).catch(() => {})
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.admin-page-container { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-size: 16px; font-weight: bold; }
.search-form { margin-bottom: 20px; }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>