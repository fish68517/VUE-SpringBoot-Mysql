# 购票流程页面实现文档

## 概述

本文档描述了沈阳音乐节管理系统的前端购票流程页面实现。购票流程分为4个步骤，用户通过逐步填写信息完成购票。

## 文件结构

```
VUE/src/
├── pages/
│   └── TicketPurchase.vue          # 购票流程主页面
├── components/
│   └── ticket/
│       ├── SessionSelection.vue    # 场次选择组件
│       ├── ZoneSelection.vue       # 分区选择组件
│       ├── BuyerInformation.vue    # 购票人信息填写组件
│       └── PaymentSimulation.vue   # 支付模拟组件
├── api/
│   └── ticket.ts                   # 购票相关API接口
└── router/
    └── index.ts                    # 路由配置（已更新）
```

## 功能说明

### 1. 场次选择 (SessionSelection.vue)

**功能：**
- 显示所有可用的演出场次
- 支持按状态筛选（可购票、已售罄、已结束）
- 显示场次的开始和结束时间
- 点击选择按钮进入下一步

**关键特性：**
- 响应式网格布局
- 状态标签显示
- 禁用已售罄或已结束的场次
- 加载状态骨架屏

### 2. 分区选择 (ZoneSelection.vue)

**功能：**
- 显示选定场次的所有分区
- 实时显示每个分区的剩余票量
- 显示分区价格
- 显示库存进度条
- 支持返回上一步

**关键特性：**
- 库存进度条可视化
- 剩余票量低于10张时高亮显示
- 已售罄分区禁用选择
- 响应式网格布局

### 3. 购票人信息填写 (BuyerInformation.vue)

**功能：**
- 支持一人代购最多4人
- 每个购票人需填写身份证号和姓名
- 实时验证身份证号格式（18位数字）
- 实时验证姓名长度（2-50字符）
- 显示订单总金额
- 支持添加/删除购票人

**关键特性：**
- 表单验证
- 错误提示
- 动态添加/删除购票人
- 订单摘要显示
- 总金额实时计算

**验证规则：**
- 身份证号：必须为18位数字
- 姓名：长度2-50个字符，不能为空

### 4. 支付模拟 (PaymentSimulation.vue)

**功能：**
- 显示订单信息摘要
- 支持选择支付方式（微信、支付宝、银行卡）
- 需要同意购票协议和隐私政策
- 模拟支付流程
- 支付成功后显示成功提示

**关键特性：**
- 支付方式选择
- 协议同意复选框
- 支付成功对话框
- 订单信息确认

## API 接口

### ticket.ts

```typescript
// 获取所有场次
getSessions(): Promise<TicketSession[]>

// 获取所有分区
getZones(): Promise<TicketZone[]>

// 获取分区详情
getZoneById(id: number): Promise<TicketZone>

// 创建购票订单
createOrder(data: TicketOrderRequest): Promise<TicketOrderResponse>
```

### 数据模型

**TicketSession**
```typescript
{
  id: number
  festivalId: number
  name: string
  startTime: string
  endTime: string
  status: string  // 'available', 'sold_out', 'ended'
}
```

**TicketZone**
```typescript
{
  id: number
  sessionId: number
  name: string
  totalCapacity: number
  soldCount: number
  remainingCapacity: number
  price: number
}
```

**TicketOrderRequest**
```typescript
{
  sessionId: number
  zoneId: number
  buyers: Array<{
    idNumber: string
    name: string
  }>
}
```

**TicketOrderResponse**
```typescript
{
  orderId: number
  ticketIds: number[]
  totalAmount: number
  ticketCount: number
  status: string
  message: string
}
```

## 路由配置

在 `VUE/src/router/index.ts` 中添加了新路由：

```typescript
{
  path: '/ticket-purchase',
  name: 'TicketPurchase',
  component: () => import('../pages/TicketPurchase.vue'),
  meta: { requiresAuth: true },
}
```

## 使用方式

### 从首页进入购票流程

在 `Home.vue` 中添加了"购票"按钮，点击后跳转到购票页面：

```typescript
const handleGoToTicket = () => {
  router.push('/ticket-purchase')
}
```

### 完整的购票流程

1. 用户点击首页的"购票"按钮
2. 进入场次选择页面，选择要购票的演出场次
3. 进入分区选择页面，选择要购票的分区
4. 进入购票人信息填写页面，填写购票人信息（支持1-4人）
5. 进入支付页面，选择支付方式并确认支付
6. 支付成功后，系统生成电子票并跳转到"我的电子票"页面

## 样式设计

### 进度指示器

- 显示当前步骤和已完成步骤
- 使用颜色区分不同状态（未开始、进行中、已完成）
- 响应式设计，在移动设备上自适应

### 卡片设计

- 使用白色卡片背景
- 边框和阴影提供视觉层次
- 悬停效果提升交互体验
- 禁用状态降低透明度

### 响应式布局

- 桌面端：多列网格布局
- 平板端：2列布局
- 移动端：单列布局

## 错误处理

### 常见错误

1. **加载失败**
   - 显示错误提示信息
   - 提供重试选项

2. **验证失败**
   - 显示具体的验证错误信息
   - 高亮显示有问题的字段

3. **创建订单失败**
   - 显示后端返回的错误信息
   - 允许用户返回重新填写

## 性能优化

1. **代码分割**
   - 使用动态导入加载购票页面
   - 减少初始加载时间

2. **图片优化**
   - 使用占位符图片
   - 支持懒加载

3. **状态管理**
   - 使用 ref 管理本地状态
   - 避免不必要的重新渲染

## 测试建议

### 单元测试

- 测试表单验证逻辑
- 测试金额计算
- 测试状态转换

### 集成测试

- 测试完整的购票流程
- 测试错误处理
- 测试返回上一步功能

### 端到端测试

- 测试从首页到支付成功的完整流程
- 测试各种边界情况

## 已知限制

1. 支付是模拟的，不涉及真实支付处理
2. 电子票生成在后端处理，前端只显示成功提示
3. 防黄牛检查由后端在创建订单时进行

## 未来改进

1. 添加优惠券/折扣码支持
2. 支持多语言
3. 添加订单预览功能
4. 支持保存草稿
5. 添加支付失败重试机制
