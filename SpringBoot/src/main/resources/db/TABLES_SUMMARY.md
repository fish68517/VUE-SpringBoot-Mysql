# 数据库表结构总结

## 表清单

### 用户相关表

#### users（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 用户ID（主键） |
| phone | VARCHAR(20) | 手机号（唯一） |
| password | VARCHAR(255) | 密码（加密存储） |
| nickname | VARCHAR(100) | 昵称 |
| avatar | VARCHAR(500) | 头像URL |
| email | VARCHAR(100) | 邮箱 |
| real_name | VARCHAR(100) | 真实姓名 |
| id_number | VARCHAR(18) | 身份证号（唯一，防黄牛） |
| is_real_name_verified | BOOLEAN | 是否完成实名认证 |
| points | BIGINT | 积分余额 |
| is_blocked | BOOLEAN | 是否被封禁 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：phone, id_number, created_at

---

### 音乐节信息表

#### festivals（音乐节表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 音乐节ID（主键） |
| name | VARCHAR(200) | 音乐节名称 |
| description | TEXT | 音乐节描述 |
| start_date | DATETIME | 开始时间 |
| end_date | DATETIME | 结束时间 |
| location | VARCHAR(500) | 举办地点 |
| poster_url | VARCHAR(500) | 海报URL |
| status | VARCHAR(50) | 状态：ongoing/ended |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：status, start_date

---

#### artists（艺人表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 艺人ID（主键） |
| name | VARCHAR(200) | 艺人名称 |
| description | TEXT | 艺人描述 |
| image_url | VARCHAR(500) | 艺人图片URL |
| is_local_band | BOOLEAN | 是否为沈阳本土乐队 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：is_local_band

---

#### schedules（演出日程表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 日程ID（主键） |
| festival_id | BIGINT | 音乐节ID（外键） |
| artist_id | BIGINT | 艺人ID（外键） |
| stage_name | VARCHAR(100) | 舞台名称 |
| start_time | DATETIME | 演出开始时间 |
| end_time | DATETIME | 演出结束时间 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：festival_id, artist_id, start_time

---

### 购票相关表

#### ticket_sessions（票务场次表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 场次ID（主键） |
| festival_id | BIGINT | 音乐节ID（外键） |
| name | VARCHAR(200) | 场次名称 |
| start_time | DATETIME | 场次开始时间 |
| end_time | DATETIME | 场次结束时间 |
| status | VARCHAR(50) | 状态：available/sold_out/ended |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：festival_id, status, start_time

---

#### ticket_zones（分区表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 分区ID（主键） |
| session_id | BIGINT | 场次ID（外键） |
| name | VARCHAR(100) | 分区名称（A区/B区/站区/VIP区） |
| total_capacity | INT | 总容纳人数 |
| sold_count | INT | 已售数量 |
| price | DECIMAL(10, 2) | 票价 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：session_id, name

---

#### tickets（电子票表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 电子票ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| session_id | BIGINT | 场次ID（外键） |
| zone_id | BIGINT | 分区ID（外键） |
| buyer_id_number | VARCHAR(18) | 购票人身份证号 |
| buyer_name | VARCHAR(100) | 购票人姓名 |
| qr_code | VARCHAR(500) | 二维码URL |
| status | VARCHAR(50) | 状态：valid/used/expired |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**唯一约束**：session_id + buyer_id_number（防黄牛）

**索引**：user_id, session_id, zone_id, status, created_at

---

### 商品相关表

#### product_categories（商品分类表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 分类ID（主键） |
| name | VARCHAR(100) | 分类名称 |
| description | TEXT | 分类描述 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：name

---

#### products（商品表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 商品ID（主键） |
| category_id | BIGINT | 分类ID（外键） |
| name | VARCHAR(200) | 商品名称 |
| description | TEXT | 商品描述 |
| images | JSON | 商品图片URL列表（JSON数组） |
| original_price | DECIMAL(10, 2) | 原价 |
| current_price | DECIMAL(10, 2) | 现价 |
| stock | INT | 库存 |
| specs | JSON | 规格信息（JSON对象） |
| is_active | BOOLEAN | 是否上架 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：category_id, is_active, created_at

---

### 订单相关表

#### orders（订单表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 订单ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| order_type | VARCHAR(50) | 订单类型：ticket/product |
| total_amount | DECIMAL(10, 2) | 订单总金额 |
| status | VARCHAR(50) | 订单状态：paid/shipped/completed/cancelled |
| shipping_address | VARCHAR(500) | 收货地址 |
| tracking_number | VARCHAR(100) | 物流单号 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：user_id, order_type, status, created_at

---

#### order_items（订单项表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 订单项ID（主键） |
| order_id | BIGINT | 订单ID（外键） |
| product_id | BIGINT | 商品ID（外键） |
| quantity | INT | 数量 |
| unit_price | DECIMAL(10, 2) | 单价 |
| created_at | TIMESTAMP | 创建时间 |

**索引**：order_id, product_id

---

### 打卡任务相关表

#### checkin_tasks（打卡任务表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 任务ID（主键） |
| name | VARCHAR(200) | 任务名称 |
| description | TEXT | 任务描述 |
| cover_image | VARCHAR(500) | 任务封面图URL |
| points | INT | 奖励积分 |
| latitude | DECIMAL(10, 8) | 地理位置纬度 |
| longitude | DECIMAL(11, 8) | 地理位置经度 |
| radius | INT | 地理围栏半径（米） |
| start_time | DATETIME | 任务开始时间 |
| end_time | DATETIME | 任务结束时间 |
| status | VARCHAR(50) | 状态：ongoing/ended |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：status, start_time, end_time

---

#### checkin_records（打卡记录表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 打卡记录ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| task_id | BIGINT | 任务ID（外键） |
| photo | VARCHAR(500) | 打卡照片URL |
| latitude | DECIMAL(10, 8) | 打卡位置纬度 |
| longitude | DECIMAL(11, 8) | 打卡位置经度 |
| status | VARCHAR(50) | 状态：pending/approved/rejected |
| reject_reason | VARCHAR(500) | 拒绝原因 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**唯一约束**：user_id + task_id（一人一任务一次）

**索引**：user_id, task_id, status, created_at

---

### 积分相关表

#### points_mall（积分商城商品表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 商品ID（主键） |
| name | VARCHAR(200) | 商品名称 |
| description | TEXT | 商品描述 |
| images | JSON | 商品图片URL列表（JSON数组） |
| points_required | INT | 所需积分 |
| type | VARCHAR(50) | 商品类型：entity/virtual |
| stock | INT | 库存 |
| countdown_end_time | DATETIME | 倒计时结束时间 |
| is_active | BOOLEAN | 是否上架 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：type, is_active, created_at

---

#### points_exchanges（积分兑换订单表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 兑换订单ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| mall_item_id | BIGINT | 商城商品ID（外键） |
| points_used | INT | 使用积分 |
| status | VARCHAR(50) | 状态：pending/approved/shipped/completed/rejected |
| shipping_address | VARCHAR(500) | 收货地址 |
| tracking_number | VARCHAR(100) | 物流单号 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：user_id, mall_item_id, status, created_at

---

#### points_history（积分流水表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 流水ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| change_amount | INT | 变动积分 |
| change_type | VARCHAR(50) | 变动类型：checkin/purchase/exchange/manual_adjust |
| related_id | BIGINT | 关联的任务/订单ID |
| description | VARCHAR(500) | 描述 |
| created_at | TIMESTAMP | 创建时间 |

**索引**：user_id, change_type, created_at

---

### 内容相关表

#### articles（文章表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 文章ID（主键） |
| title | VARCHAR(300) | 文章标题 |
| content | LONGTEXT | 文章内容（富文本） |
| images | JSON | 文章图片URL列表（JSON数组） |
| author | VARCHAR(100) | 作者 |
| type | VARCHAR(50) | 文章类型：news/guide/food_recommendation |
| likes | INT | 点赞数 |
| views | INT | 浏览数 |
| is_published | BOOLEAN | 是否发布 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：type, is_published, created_at

---

#### article_comments（文章评论表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 评论ID（主键） |
| article_id | BIGINT | 文章ID（外键） |
| user_id | BIGINT | 用户ID（外键） |
| content | VARCHAR(1000) | 评论内容 |
| likes | INT | 点赞数 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：article_id, user_id, created_at

---

#### user_collections（用户收藏表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 收藏ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| article_id | BIGINT | 文章ID（外键） |
| created_at | TIMESTAMP | 创建时间 |

**唯一约束**：user_id + article_id

**索引**：user_id, article_id

---

#### festival_follows（音乐节关注表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 关注ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| festival_id | BIGINT | 音乐节ID（外键） |
| followed_at | TIMESTAMP | 关注时间 |
| updated_at | TIMESTAMP | 更新时间 |

**唯一约束**：user_id + festival_id

**索引**：user_id, festival_id

---

### 其他表

#### weather_info（天气信息表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 天气ID（主键） |
| festival_id | BIGINT | 音乐节ID（外键） |
| date | DATE | 日期 |
| temperature | INT | 温度（摄氏度） |
| humidity | INT | 湿度（百分比） |
| wind_speed | INT | 风速（km/h） |
| weather_description | VARCHAR(100) | 天气描述 |
| clothing_suggestion | VARCHAR(500) | 穿衣建议 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**唯一约束**：festival_id + date

**索引**：festival_id, date

---

#### transport_info（交通信息表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 交通信息ID（主键） |
| festival_id | BIGINT | 音乐节ID（外键） |
| type | VARCHAR(50) | 交通类型：subway/bus/parking/guide |
| title | VARCHAR(200) | 标题 |
| description | TEXT | 描述 |
| details | JSON | 详细信息（JSON对象） |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：festival_id, type

---

## 统计信息

- **总表数**：22 个
- **用户相关表**：1 个
- **音乐节信息表**：3 个
- **购票相关表**：3 个
- **商品相关表**：2 个
- **订单相关表**：2 个
- **打卡任务相关表**：2 个
- **积分相关表**：3 个
- **内容相关表**：4 个
- **其他表**：2 个

## 关键约束

### 唯一约束（防止重复）
1. `users.phone` - 手机号唯一
2. `users.id_number` - 身份证号唯一（防黄牛）
3. `tickets.session_id + buyer_id_number` - 同一场次同一身份证号只能购一张票
4. `checkin_records.user_id + task_id` - 同一用户同一任务只能打卡一次
5. `user_collections.user_id + article_id` - 同一用户同一文章只能收藏一次
6. `festival_follows.user_id + festival_id` - 同一用户同一音乐节只能关注一次
7. `weather_info.festival_id + date` - 同一音乐节同一日期只有一条天气记录

### 外键约束
所有外键都设置了 `ON DELETE CASCADE`，确保删除父记录时自动删除相关子记录。

## 字符集和排序规则

所有表都使用：
- **字符集**：`utf8mb4`（支持完整 Unicode，包括表情符号）
- **排序规则**：`utf8mb4_unicode_ci`（不区分大小写）

## 存储引擎

所有表都使用 `InnoDB` 存储引擎，支持事务和外键约束。
