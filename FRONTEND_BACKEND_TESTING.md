# 前后端集成测试指南

## 概述

本文档提供了前后端集成测试的完整指南，包括测试用例、测试方法和验证步骤。

## 一、测试环境准备

### 1.1 后端环境

```bash
# 启动MySQL数据库
# 确保数据库已创建并初始化

# 启动SpringBoot应用
cd SpringBoot
mvn spring-boot:run
# 应用将在 http://localhost:8080 启动
```

### 1.2 前端环境

```bash
# 安装依赖
cd VUE
npm install

# 启动开发服务器
npm run dev
# 应用将在 http://localhost:5173 启动
```

## 二、集成测试用例

### 2.1 用户认证流程

#### 测试用例：用户注册
```
步骤：
1. 打开注册页面 http://localhost:5173/register
2. 填写表单：
   - 用户名: test_user_001
   - 邮箱: test001@example.com
   - 密码: password123
   - 确认密码: password123
   - 用户类型: 农户
   - 地区: 河南省郑州市
3. 点击注册按钮

预期结果：
- 显示"注册成功，请登录"提示
- 自动跳转到登录页面
- 后端日志显示用户创建成功
```

#### 测试用例：用户登录
```
步骤：
1. 打开登录页面 http://localhost:5173/login
2. 填写表单：
   - 用户名: test_user_001
   - 密码: password123
3. 点击登录按钮

预期结果：
- 显示"登录成功"提示
- 自动跳转到首页
- localStorage中存储token和用户信息
- 后续请求的Authorization header包含token
```

#### 测试用例：登录失败
```
步骤：
1. 打开登录页面
2. 填写表单：
   - 用户名: test_user_001
   - 密码: wrongpassword
3. 点击登录按钮

预期结果：
- 显示"登录失败"或"用户名或密码错误"提示
- 不跳转页面
- localStorage中没有token
```

### 2.2 气象数据流程

#### 测试用例：获取当前气象数据
```
步骤：
1. 登录系统
2. 打开天气页面 http://localhost:5173/weather
3. 选择地区：河南省郑州市
4. 点击查询按钮

预期结果：
- 显示该地区的当前气象数据
- 包含温度、湿度、降水、风速等信息
- 数据来自后端API
- 响应时间 < 200ms
```

#### 测试用例：获取气象预报
```
步骤：
1. 在天气页面选择"预报"标签
2. 选择地区和时间范围
3. 点击查询按钮

预期结果：
- 显示该地区的气象预报数据
- 包含多天的预报信息
- 数据按时间排序
```

### 2.3 预警管理流程

#### 测试用例：查看预警列表
```
步骤：
1. 登录系统
2. 打开预警页面 http://localhost:5173/warnings
3. 页面自动加载预警列表

预期结果：
- 显示所有活跃预警
- 包含预警类型、地区、严重程度、描述
- 可以按地区或类型筛选
- 响应时间 < 200ms
```

#### 测试用例：查看预警详情
```
步骤：
1. 在预警列表中点击某个预警
2. 打开预警详情页面

预期结果：
- 显示预警的完整信息
- 包含预警等级、影响范围、预期时间、防控建议
- 显示相关的推荐农资
```

### 2.4 农资产品流程

#### 测试用例：浏览产品列表
```
步骤：
1. 登录系统
2. 打开产品页面 http://localhost:5173/products
3. 页面自动加载产品列表

预期结果：
- 显示所有可用的农资产品
- 包含产品名称、类别、价格、库存
- 可以按类别筛选
- 响应时间 < 300ms
```

#### 测试用例：搜索产品
```
步骤：
1. 在产品页面的搜索框输入关键词
2. 按Enter或点击搜索按钮

预期结果：
- 显示匹配的产品列表
- 搜索结果实时更新
- 响应时间 < 300ms
```

### 2.5 订单管理流程

#### 测试用例：创建订单
```
步骤：
1. 登录系统
2. 打开产品页面
3. 选择一个产品
4. 输入购买数量
5. 点击"立即购买"按钮

预期结果：
- 显示订单确认页面
- 显示订单总价
- 订单状态为"待支付"
- 后端库存已扣减
```

#### 测试用例：支付订单
```
步骤：
1. 在订单确认页面点击"支付"按钮
2. 选择支付方式
3. 完成支付

预期结果：
- 订单状态变为"已支付"
- 显示"支付成功"提示
- 订单可以在订单列表中查看
```

#### 测试用例：查看订单列表
```
步骤：
1. 登录系统
2. 打开订单页面 http://localhost:5173/orders
3. 页面自动加载订单列表

预期结果：
- 显示用户的所有订单
- 包含订单号、产品、数量、总价、状态
- 可以按状态筛选
- 可以查看订单详情
```

### 2.6 推荐系统流程

#### 测试用例：获取推荐
```
步骤：
1. 登录系统
2. 打开推荐页面 http://localhost:5173/recommendations
3. 选择一个预警
4. 点击"获取推荐"按钮

预期结果：
- 显示基于该预警的推荐农资列表
- 推荐按优先级排序
- 显示推荐原因
- 显示产品库存状态
```

#### 测试用例：接受推荐
```
步骤：
1. 在推荐列表中点击某个推荐的"接受"按钮
2. 系统记录用户接受了该推荐

预期结果：
- 推荐状态变为"已接受"
- 可以直接购买该产品
- 推荐记录保存到数据库
```

### 2.7 数据分析流程

#### 测试用例：查看统计数据
```
步骤：
1. 登录系统（需要管理员权限）
2. 打开分析页面 http://localhost:5173/analytics
3. 页面自动加载统计数据

预期结果：
- 显示订单统计、预警统计、用户统计
- 显示热销产品排行
- 可以按时间范围筛选
- 响应时间 < 500ms
```

#### 测试用例：导出数据
```
步骤：
1. 在分析页面点击"导出"按钮
2. 选择导出格式（CSV或Excel）
3. 点击"确认导出"

预期结果：
- 浏览器下载数据文件
- 文件包含完整的统计数据
- 文件格式正确
```

## 三、数据一致性验证

### 3.1 验证登录响应

```javascript
// 在浏览器控制台执行
const user = JSON.parse(localStorage.getItem('user'))
console.log('用户信息:', user)
// 验证包含以下字段：
// - token
// - userId
// - username
// - userType
// - email
// - phone
// - region
```

### 3.2 验证API响应格式

```javascript
// 打开浏览器开发者工具 -> Network标签
// 查看任意API请求的响应
// 验证响应格式：
{
  "code": 200,
  "message": "操作成功",
  "data": { /* 实际数据 */ },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 3.3 验证错误处理

```javascript
// 测试错误响应
// 1. 发送无效请求
// 2. 验证错误响应格式
{
  "code": 400,
  "message": "错误信息",
  "timestamp": "2024-01-01T12:00:00"
}
```

## 四、性能测试

### 4.1 响应时间测试

```javascript
// 在浏览器控制台执行
import { performanceMonitor } from '@/utils/performanceMonitor'

// 查看性能统计
performanceMonitor.printReport()

// 输出示例：
// === API 性能报告 ===
// login:
//   调用次数: 1
//   平均时间: 250ms
//   最大时间: 250ms
//   最小时间: 250ms
//   阈值: 500ms
```

### 4.2 页面加载性能

```javascript
// 在浏览器控制台执行
import { getPagePerformanceMetrics } from '@/utils/performanceMonitor'

const metrics = getPagePerformanceMetrics()
console.log('页面加载性能:', metrics)

// 输出示例：
// {
//   pageLoadTime: 2500,
//   domParseTime: 1200,
//   resourceLoadTime: 1300,
//   dnsTime: 50,
//   tcpTime: 100,
//   serverTime: 200,
//   renderTime: 800
// }
```

### 4.3 内存使用情况

```javascript
// 在浏览器控制台执行
import { getMemoryInfo } from '@/utils/performanceMonitor'

const memory = getMemoryInfo()
console.log('内存使用:', memory)

// 输出示例：
// {
//   usedJSHeapSize: 45,
//   totalJSHeapSize: 60,
//   jsHeapSizeLimit: 2048
// }
```

## 五、错误场景测试

### 5.1 网络错误

```
步骤：
1. 打开浏览器开发者工具
2. 在Network标签中选择"Offline"
3. 尝试执行任何API操作

预期结果：
- 显示网络错误提示
- 不会导致应用崩溃
- 用户可以重试操作
```

### 5.2 超时错误

```
步骤：
1. 在浏览器开发者工具中限制网络速度（Slow 3G）
2. 执行API操作
3. 等待超过10秒

预期结果：
- 显示"请求超时"提示
- 不会导致应用崩溃
- 用户可以重试操作
```

### 5.3 授权错误

```
步骤：
1. 登录系统
2. 清除localStorage中的token
3. 尝试访问受保护的页面

预期结果：
- 自动跳转到登录页面
- 显示"请重新登录"提示
- 不会显示错误的数据
```

## 六、跨浏览器测试

### 6.1 测试浏览器

- Chrome (最新版本)
- Firefox (最新版本)
- Safari (最新版本)
- Edge (最新版本)

### 6.2 测试项目

- 页面加载
- 表单提交
- API请求
- 数据显示
- 错误处理

## 七、自动化测试

### 7.1 后端集成测试

```bash
# 运行所有集成测试
mvn test -f SpringBoot/pom.xml

# 运行特定测试类
mvn test -Dtest=FrontendBackendIntegrationTest -f SpringBoot/pom.xml

# 运行特定测试方法
mvn test -Dtest=FrontendBackendIntegrationTest#testLoginResponseFormat -f SpringBoot/pom.xml
```

### 7.2 前端单元测试

```bash
# 运行所有单元测试
npm run test

# 运行特定测试文件
npm run test -- src/utils/apiValidator.test.js

# 生成覆盖率报告
npm run test:coverage
```

## 八、测试检查清单

- [ ] 用户注册功能正常
- [ ] 用户登录功能正常
- [ ] 用户登出功能正常
- [ ] 气象数据显示正确
- [ ] 预警信息显示正确
- [ ] 产品列表显示正确
- [ ] 订单创建成功
- [ ] 订单支付成功
- [ ] 推荐系统工作正常
- [ ] 数据分析显示正确
- [ ] 错误处理正确
- [ ] 性能指标达到要求
- [ ] 跨浏览器兼容性良好
- [ ] 响应时间在阈值内
- [ ] 没有控制台错误

## 九、问题报告模板

```
标题: [功能] 问题描述

环境:
- 浏览器: Chrome 120
- 操作系统: Windows 10
- 前端版本: 1.0.0
- 后端版本: 1.0.0

步骤:
1. ...
2. ...
3. ...

预期结果:
...

实际结果:
...

错误信息:
...

截图:
[附加截图]
```

## 十、性能优化建议

1. **启用缓存**：对频繁访问的数据启用缓存
2. **分页加载**：对大列表使用分页或虚拟滚动
3. **请求去重**：使用防抖处理频繁请求
4. **代码分割**：使用动态导入分割代码
5. **图片优化**：压缩和延迟加载图片
6. **CDN加速**：使用CDN加速静态资源
7. **数据库优化**：添加索引，优化查询
8. **API优化**：支持字段选择，减少返回数据量

## 十一、参考资源

- [Spring Boot测试文档](https://spring.io/guides/gs/testing-web/)
- [Vue 3测试指南](https://vuejs.org/guide/scaling-up/testing.html)
- [Axios文档](https://axios-http.com/)
- [Web性能API](https://developer.mozilla.org/en-US/docs/Web/API/Performance)
