<template>
  <div class="products-container">
    <Card title="农资产品">
      
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="4">
          <el-input
            v-model="filters.productName"
            placeholder="请输入产品名称"
            @input="handleSearch"
            clearable
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
            v-model="filters.category"
            placeholder="选择类别"
            @change="handleSearch"
            clearable
          >
            <el-option label="全部" value="" />
            <el-option label="肥料" value="肥料" />
            <el-option label="农药" value="农药" />
            <el-option label="种子" value="种子" />
            <el-option label="支架材料" value="支架材料" />
            <el-option label="灌溉设备" value="灌溉设备" />
            <el-option label="防护用品" value="防护用品" />
            <el-option label="排水设备" value="排水设备" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-input
            v-model.number="filters.minPrice"
            type="number"
            placeholder="最低价格"
            @input="handleSearch"
            clearable
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-input
            v-model.number="filters.maxPrice"
            type="number"
            placeholder="最高价格"
            @input="handleSearch"
            clearable
          />
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="8" class="action-btn-col">
          <el-button type="primary" @click="handleAdd" icon="Plus">添加农资产品</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col
          v-for="product in paginatedProducts"
          :key="product.id"
          :xs="24"
          :sm="12"
          :md="8"
          class="product-col"
        >
          <div class="product-card">
            <h3>{{ product.productName }}</h3>
            <p class="category">{{ product.category }}</p>
            <p class="description">{{ product.description }}</p>
            <div class="product-info">
              <span class="price">¥{{ product.price }}</span>
              <span class="stock">库存: {{ product.stock }}</span>
            </div>
            
            <div class="card-actions">
              <el-button
                type="primary"
                size="small"
                @click="handleAddToCart(product)"
                :disabled="product.stock === 0"
              >
                {{ product.stock > 0 ? '加入购物车' : '缺货' }}
              </el-button>
              
              <el-button
                type="warning"
                size="small"
                @click="handleEdit(product)"
              >
                编辑
              </el-button>
            </div>
            
          </div>
        </el-col>
      </el-row>

      <div class="pagination-wrapper">
        <el-pagination
          v-if="pagination.total > 0"
          :current-page="pagination.current"
          :page-size="pagination.size"
          :page-sizes="[12, 24, 36]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-page-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </Card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑农资产品' : '添加农资产品'" width="500px">
      <el-form :model="productForm" label-width="90px">
        <el-form-item label="产品名称" required>
          <el-input v-model="productForm.productName" placeholder="请输入产品名称" />
        </el-form-item>
        
        <el-form-item label="产品类别" required v-if="!isEdit">
          <el-select v-model="productForm.category" placeholder="选择类别" style="width: 100%;">
            <el-option label="肥料" value="肥料" />
            <el-option label="农药" value="农药" />
            <el-option label="种子" value="种子" />
            <el-option label="支架材料" value="支架材料" />
            <el-option label="灌溉设备" value="灌溉设备" />
            <el-option label="防护用品" value="防护用品" />
            <el-option label="排水设备" value="排水设备" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="产品价格" required>
          <el-input-number v-model="productForm.price" :precision="2" :step="1" :min="0" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="库存数量" required v-if="!isEdit">
          <el-input-number v-model="productForm.stock" :min="0" :step="1" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="适用天气">
          <el-input v-model="productForm.applicableWeather" placeholder="如：晴天、阴天" />
        </el-form-item>
        
        <el-form-item label="产品描述">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入描述信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定保存</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { productAPI } from '@/api/product'
import Card from '@/components/common/Card.vue'

import { useCartStore } from '@/stores/cart.js'

// ---------- 弹窗与表单状态 ----------
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentProductId = ref(null)

// 在其他声明变量的地方初始化它
const cartStore = useCartStore()

const productForm = ref({
  productName: '',
  category: '',
  price: 0,
  stock: 0,
  applicableWeather: '',
  description: ''
})

// 点击添加按钮
const handleAdd = () => {
  isEdit.value = false
  currentProductId.value = null
  productForm.value = {
    productName: '',
    category: '',
    price: 0,
    stock: 0,
    applicableWeather: '',
    description: ''
  }
  dialogVisible.value = true
}

// 点击编辑按钮
const handleEdit = (product) => {
  isEdit.value = true
  currentProductId.value = product.id
  productForm.value = {
    productName: product.productName,
    category: product.category,
    price: product.price,
    stock: product.stock,
    applicableWeather: product.applicableWeather,
    description: product.description
  }
  dialogVisible.value = true
}

// 提交表单 (新增 / 更新)
const submitForm = async () => {
  if (!productForm.value.productName || productForm.value.price === null || productForm.value.price === undefined) {
    ElMessage.warning('产品名称和价格不能为空')
    return
  }
  if (!isEdit.value && (!productForm.value.category || productForm.value.stock === null)) {
    ElMessage.warning('产品类别和库存不能为空')
    return
  }

  try {
    if (isEdit.value) {
      // 走更新接口
      await productAPI.updateProduct(currentProductId.value, {
        productName: productForm.value.productName,
        description: productForm.value.description,
        price: productForm.value.price,
        applicableWeather: productForm.value.applicableWeather
      })
      ElMessage.success('更新成功')
    } else {
      // 走添加接口，从 localStorage 获取当前商家的 ID (假设存在 user.userId 中)
      const userStr = localStorage.getItem('user')
      const merchantId = userStr ? JSON.parse(userStr).userId : null
      
      if (!merchantId) {
        ElMessage.error('无法获取当前商家信息，请重新登录')
        return
      }

      await productAPI.createProduct({
        ...productForm.value,
        merchantId: merchantId
      })
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    handleSearch() // 刷新列表
  } catch (error) {
    console.error(error)
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}


// ---------- 列表、搜索与分页 ----------
const filters = ref({
  productName: '',
  category: '',
  minPrice: null,
  maxPrice: null,
})

const products = ref([])
const pagination = ref({
  current: 1,
  size: 12,
  total: 0,
})

// 前端计算分页数据 (因为后端 query 接口返回的是所有匹配结果的 List)
const paginatedProducts = computed(() => {
  const start = (pagination.value.current - 1) * pagination.value.size
  const end = start + pagination.value.size
  return products.value.slice(start, end)
})

const handleSearch = async () => {
  try {
    // 根据你的 axios 拦截器，这里 response 可能直接是数据，也可能是包含 data 的对象
    const res = await productAPI.getProducts({
      productName: filters.value.productName,
      category: filters.value.category,
      minPrice: filters.value.minPrice,
      maxPrice: filters.value.maxPrice
    })
    
    // 兼容取值逻辑
    const dataList = res.data || res || []
    products.value = dataList
    
    // 设置总条数并回到第一页
    pagination.value.total = dataList.length
    // 如果是触发搜索，通常需要将页码重置为 1
    // pagination.value.current = 1 
  } catch (error) {
    console.error('获取产品列表失败', error)
    ElMessage.error('获取产品列表失败')
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
}

const handlePageSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
}

const handleAddToCart = (product) => {
  // 1. 调用 Pinia Store 的方法，将产品加入全局状态
  cartStore.addToCart(product)
  // 2. 给出成功提示
  ElMessage.success(`已添加 ${product.productName} 到购物车`)
}
onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.products-container {
  padding: 20px;
}

.filter-row {
  margin-bottom: 20px;
}

.action-btn-col {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.product-col {
  margin-bottom: 20px;
}

.product-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background-color: #ffffff; /* 调整为白色更清晰 */
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.product-card h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.product-card .category {
  margin: 5px 0;
  color: #fff;
  background-color: #909399;
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.product-card .description {
  margin: 10px 0;
  color: #606266;
  font-size: 13px;
  flex-grow: 1; /* 让描述部分占据剩余空间，把按钮推到最下面 */
  line-height: 1.5;
}

.product-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0 10px 0;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.stock {
  font-size: 13px;
  color: #909399;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto; /* 保证按钮始终在卡片最底部 */
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>