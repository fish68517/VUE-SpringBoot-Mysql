# 前端快速参考指南

## API集成

### 使用API模块

```javascript
import { userAPI } from '@/api/user'
import { productAPI } from '@/api/product'
import { orderAPI } from '@/api/order'

// 调用API
const user = await userAPI.login({ username, password })
const products = await productAPI.getProducts({ category: 'fertilizer' })
const order = await orderAPI.createOrder(orderData)
```

### 错误处理

```javascript
try {
  const data = await userAPI.login(form)
  // data已经是解析后的数据
  authStore.setAuth(data.token, data)
} catch (error) {
  // error.message包含后端返回的错误信息
  ElMessage.error(error.message)
}
```

## Store使用

### 获取数据

```javascript
import { useProductsStore } from '@/stores/products'

const productStore = useProductsStore()

// 获取产品列表
await productStore.fetchProducts({ category: 'fertilizer' })
console.log(productStore.products) // 产品列表

// 获取单个产品
await productStore.fetchProductById(1)
console.log(productStore.currentProduct) // 当前产品
```

### 创建/更新数据

```javascript
// 创建产品
const newProduct = await productStore.createProduct({
  productName: '新产品',
  category: '肥料',
  price: 99.99,
  stock: 100
})

// 更新产品
const updated = await productStore.updateProduct(1, {
  price: 89.99,
  stock: 150
})

// 删除产品
await productStore.deleteProduct(1)
```

## 数据验证

### 验证API响应

```javascript
import { validateAPIResponse } from '@/utils/apiValidator'

const response = await userAPI.login(form)
const result = validateAPIResponse(response, 'login')

if (!result.valid) {
  console.error('响应验证失败:', result.errors)
}
```

### 验证特定字段

```javascript
import { 
  validateEmail, 
  validatePhone, 
  validateStringLength 
} from '@/utils/apiValidator'

// 验证邮箱
if (!validateEmail('test@example.com')) {
  console.error('邮箱格式不正确')
}

// 验证手机号
if (!validatePhone('13800138000')) {
  console.error('手机号格式不正确')
}

// 验证字符串长度
if (!validateStringLength('password', 6, 20)) {
  console.error('密码长度不符合要求')
}
```

## 性能监控

### 查看性能统计

```javascript
import { performanceMonitor } from '@/utils/performanceMonitor'

// 打印性能报告
performanceMonitor.printReport()

// 获取特定API的统计
const stats = performanceMonitor.getStats()
console.log(stats.login) // { count: 1, average: 250, max: 250, min: 250 }

// 获取平均响应时间
const avgTime = performanceMonitor.getAverageTime('login')
console.log(`登录平均响应时间: ${avgTime}ms`)
```

### 获取页面性能指标

```javascript
import { getPagePerformanceMetrics, getMemoryInfo } from '@/utils/performanceMonitor'

// 获取页面加载性能
const metrics = getPagePerformanceMetrics()
console.log('页面加载时间:', metrics.pageLoadTime)
console.log('DOM解析时间:', metrics.domParseTime)

// 获取内存使用情况
const memory = getMemoryInfo()
console.log('已使用内存:', memory.usedJSHeapSize, 'MB')
console.log('总堆大小:', memory.totalJSHeapSize, 'MB')
```

## 常见任务

### 用户登录

```javascript
// Login.vue
const handleLogin = async () => {
  try {
    await formRef.value.validate()
    const response = await userAPI.login(form.value)
    authStore.setAuth(response.token, response)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message)
  }
}
```

### 获取产品列表

```javascript
// Products.vue
const productStore = useProductsStore()

onMounted(async () => {
  try {
    await productStore.fetchProducts({ category: selectedCategory.value })
  } catch (error) {
    ElMessage.error('获取产品列表失败')
  }
})
```

### 创建订单

```javascript
// Orders.vue
const handleCreateOrder = async () => {
  try {
    const order = await orderStore.createOrder({
      productId: selectedProduct.value.id,
      quantity: quantity.value,
      deliveryAddress: address.value
    })
    ElMessage.success('订单创建成功')
    router.push(`/orders/${order.id}`)
  } catch (error) {
    ElMessage.error(error.message)
  }
}
```

## 调试技巧

### 查看存储的数据

```javascript
// 在浏览器控制台执行
// 查看token
localStorage.getItem('token')

// 查看用户信息
JSON.parse(localStorage.getItem('user'))

// 查看Store状态
// 使用Vue DevTools查看Pinia store状态
```

### 查看API请求

```javascript
// 打开浏览器开发者工具 -> Network标签
// 查看所有API请求和响应
// 检查请求头中的Authorization
// 检查响应格式是否正确
```

### 性能分析

```javascript
// 在浏览器控制台执行
import { performanceMonitor } from '@/utils/performanceMonitor'

// 查看所有API的性能统计
performanceMonitor.printReport()

// 查看页面加载性能
const metrics = getPagePerformanceMetrics()
console.table(metrics)
```

## 常见问题

### Q: 登录后仍然无法访问受保护的资源

A: 检查以下几点：
1. Token是否正确存储在localStorage
2. Authorization header是否正确设置（Bearer token）
3. 后端是否正确验证JWT token

### Q: API响应格式错误

A: 检查以下几点：
1. 后端是否返回Result包装格式
2. 前端API客户端是否正确解析
3. 使用apiValidator验证响应格式

### Q: 性能指标显示响应时间过长

A: 检查以下几点：
1. 网络连接是否正常
2. 后端服务器是否正常运行
3. 数据库查询是否优化
4. 是否需要启用缓存

## 最佳实践

1. **始终使用API模块**：不要直接调用apiClient
2. **使用Store管理状态**：不要在组件中直接管理数据
3. **验证API响应**：使用apiValidator验证响应格式
4. **监控性能指标**：定期查看性能报告
5. **统一错误处理**：使用try-catch捕获错误
6. **使用TypeScript**：提高代码质量和可维护性
7. **编写单元测试**：确保代码质量

## 相关文档

- [前后端集成指南](../SpringBoot/FRONTEND_BACKEND_INTEGRATION_GUIDE.md)
- [前后端测试指南](../FRONTEND_BACKEND_TESTING.md)
- [集成优化总结](../INTEGRATION_OPTIMIZATION_SUMMARY.md)
- [任务完成报告](../TASK_33_COMPLETION_REPORT.md)

## 联系方式

如有问题，请参考相关文档或联系开发团队。
