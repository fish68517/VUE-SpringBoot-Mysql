# 实施计划

- [ ] 1. 搭建后端基础架构
  - 配置 Spring Boot 项目基础结构
  - 创建统一响应封装类 Result 和 ResultCode
  - 实现全局异常处理器 GlobalExceptionHandler
  - 配置 CORS 跨域支持
  - 配置 application.yml 数据库连接
  - _需求: 所有需求的基础_

- [x] 2. 创建数据库和实体类





  - 编写数据库初始化 SQL 脚本，创建所有表结构
  - 创建 User 实体类，包含 JPA 注解
  - 创建 Pet 实体类，配置与 User 的多对一关系
  - 创建 Shop 实体类，配置与 User 的多对一关系
  - 创建 Category 实体类
  - 创建 Product 实体类，配置与 Shop 和 Category 的关系
  - 创建 Cart 实体类，配置与 User 和 Product 的关系
  - 创建 Order 和 OrderItem 实体类，配置订单关系
  - 创建 Review 实体类，配置评价关系
  - 创建 CommunityPost 和 CommunityReply 实体类
  - 创建 PostLike 实体类
  - 创建 Coupon 和 UserCoupon 实体类
  - 创建 BrowseHistory 实体类
  - _需求: 1.1, 2.1, 3.1, 4.1, 6.1, 7.1, 8.1, 9.1, 11.1, 12.1, 15.1_

- [x] 3. 实现用户模块




- [x] 3.1 创建用户 Repository 和 Service


  - 创建 UserRepository 接口，继承 JpaRepository
  - 创建 UserService 接口和 UserServiceImpl 实现类
  - 实现用户注册方法，包含用户名重复检查
  - 实现用户登录方法，明文密码验证
  - 实现获取用户信息方法
  - 实现更新用户信息方法
  - _需求: 1.1, 1.2, 1.3, 2.1, 2.2_

- [x] 3.2 创建用户 Controller 和 DTO


  - 创建 UserController，定义 RESTful API
  - 创建 RegisterDTO 和 LoginDTO
  - 实现 POST /api/user/register 注册接口
  - 实现 POST /api/user/login 登录接口
  - 实现 GET /api/user/profile 获取用户信息接口
  - 实现 PUT /api/user/profile 更新用户信息接口
  - _需求: 1.1, 1.2, 1.4, 2.1, 2.2_

- [x] 4. 实现宠物档案模块




- [x] 4.1 创建宠物档案 Repository 和 Service


  - 创建 PetRepository 接口
  - 创建 PetService 接口和 PetServiceImpl 实现类
  - 实现创建宠物档案方法
  - 实现查询用户宠物列表方法
  - 实现更新宠物信息方法
  - 实现删除宠物档案方法
  - _需求: 3.1, 3.2, 3.3, 3.4_

- [x] 4.2 创建宠物档案 Controller


  - 创建 PetController
  - 实现 POST /api/pet 创建宠物档案接口
  - 实现 GET /api/pet/list 获取宠物列表接口
  - 实现 PUT /api/pet/{id} 更新宠物信息接口
  - 实现 DELETE /api/pet/{id} 删除宠物档案接口
  - _需求: 3.1, 3.2, 3.3, 3.4_

- [x] 5. 实现商品分类和商品模块




- [x] 5.1 创建商品分类功能


  - 创建 CategoryRepository 接口
  - 创建 CategoryService 接口和实现类
  - 实现获取所有分类方法
  - 创建 CategoryController
  - 实现 GET /api/category/list 获取分类列表接口
  - _需求: 4.3_

- [x] 5.2 创建商品基础功能


  - 创建 ProductRepository 接口，定义自定义查询方法
  - 创建 ProductService 接口和 ProductServiceImpl 实现类
  - 实现商品分页查询方法
  - 实现商品详情查询方法
  - 实现按分类查询商品方法
  - 实现商品搜索方法（按名称或描述）
  - _需求: 4.1, 4.2, 4.3, 4.4_

- [x] 5.3 创建商品 Controller（用户端）


  - 创建 ProductController
  - 实现 GET /api/product/list 商品列表接口（分页）
  - 实现 GET /api/product/{id} 商品详情接口
  - 实现 GET /api/product/search 商品搜索接口
  - 实现 GET /api/product/category/{id} 按分类查询接口
  - _需求: 4.1, 4.2, 4.3, 4.4_

- [x] 6. 实现个性化推荐功能




- [x] 6.1 创建浏览历史功能


  - 创建 BrowseHistoryRepository 接口
  - 创建 BrowseHistoryService 接口和实现类
  - 实现记录浏览历史方法
  - 实现获取用户浏览历史方法
  - _需求: 5.2_

- [x] 6.2 实现推荐算法


  - 在 ProductService 中实现基于宠物种类的推荐方法
  - 实现基于浏览历史的推荐方法
  - 创建 RecommendController
  - 实现 GET /api/product/recommend 推荐商品接口
  - _需求: 5.1, 5.2, 5.3_

- [x] 7. 实现购物车模块




- [x] 7.1 创建购物车 Repository 和 Service


  - 创建 CartRepository 接口
  - 创建 CartService 接口和 CartServiceImpl 实现类
  - 实现添加商品到购物车方法，包含库存验证
  - 实现查询用户购物车方法
  - 实现更新购物车商品数量方法
  - 实现删除购物车商品方法
  - 实现清空购物车方法
  - _需求: 6.1, 6.2, 6.3, 6.4, 6.5_

- [x] 7.2 创建购物车 Controller


  - 创建 CartController
  - 实现 POST /api/cart 添加到购物车接口
  - 实现 GET /api/cart/list 获取购物车列表接口
  - 实现 PUT /api/cart/{id} 更新数量接口
  - 实现 DELETE /api/cart/{id} 删除商品接口
  - 实现 DELETE /api/cart/clear 清空购物车接口
  - _需求: 6.1, 6.2, 6.3, 6.4_

- [x] 8. 实现订单模块




- [x] 8.1 创建订单 Repository 和 Service


  - 创建 OrderRepository 和 OrderItemRepository 接口
  - 创建 OrderService 接口和 OrderServiceImpl 实现类
  - 实现创建订单方法，包含库存扣减和购物车清空
  - 实现生成订单号方法
  - 实现查询用户订单列表方法
  - 实现查询订单详情方法
  - 实现支付订单方法（更新状态）
  - 实现取消订单方法（恢复库存）
  - 实现确认收货方法
  - _需求: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 8.2 创建订单 Controller


  - 创建 OrderController
  - 创建 CreateOrderDTO
  - 实现 POST /api/order 创建订单接口
  - 实现 GET /api/order/list 获取订单列表接口
  - 实现 GET /api/order/{id} 获取订单详情接口
  - 实现 PUT /api/order/{id}/pay 支付订单接口
  - 实现 PUT /api/order/{id}/cancel 取消订单接口
  - 实现 PUT /api/order/{id}/complete 确认收货接口
  - _需求: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 9. 实现商品评价模块




- [x] 9.1 创建评价 Repository 和 Service


  - 创建 ReviewRepository 接口
  - 创建 ReviewService 接口和 ReviewServiceImpl 实现类
  - 实现创建评价方法，验证订单状态
  - 实现查询商品评价列表方法
  - 实现查询用户评价列表方法
  - _需求: 8.1, 8.2, 8.3_

- [x] 9.2 创建评价 Controller


  - 创建 ReviewController
  - 创建 CreateReviewDTO
  - 实现 POST /api/review 创建评价接口
  - 实现 GET /api/review/product/{id} 获取商品评价接口
  - 实现 GET /api/review/user 获取用户评价接口
  - _需求: 8.1, 8.2_

- [x] 10. 实现社区模块





- [x] 10.1 创建社区帖子功能


  - 创建 CommunityPostRepository 接口
  - 创建 CommunityPostService 接口和实现类
  - 实现发布帖子方法
  - 实现查询帖子列表方法（分页，按时间倒序）
  - 实现查询帖子详情方法
  - 实现删除帖子方法
  - 实现增加浏览数方法
  - _需求: 9.1, 9.2, 9.3, 9.4_

- [x] 10.2 创建社区评论和点赞功能


  - 创建 CommunityReplyRepository 和 PostLikeRepository 接口
  - 创建 CommunityReplyService 和 PostLikeService 接口及实现类
  - 实现发布评论方法
  - 实现查询帖子评论方法
  - 实现点赞/取消点赞方法
  - 实现查询用户是否点赞方法
  - _需求: 10.1, 10.2, 10.3, 10.4_

- [x] 10.3 创建社区 Controller


  - 创建 CommunityController
  - 实现 POST /api/community/post 发布帖子接口
  - 实现 GET /api/community/post/list 获取帖子列表接口
  - 实现 GET /api/community/post/{id} 获取帖子详情接口
  - 实现 DELETE /api/community/post/{id} 删除帖子接口
  - 实现 POST /api/community/reply 发布评论接口
  - 实现 GET /api/community/reply/{postId} 获取评论接口
  - 实现 POST /api/community/like/{postId} 点赞/取消点赞接口
  - _需求: 9.1, 9.2, 9.3, 9.4, 10.1, 10.2, 10.3, 10.4_

- [x] 11. 实现店铺管理模块（店家端）




- [x] 11.1 创建店铺 Repository 和 Service


  - 创建 ShopRepository 接口
  - 创建 ShopService 接口和 ShopServiceImpl 实现类
  - 实现创建店铺方法
  - 实现查询店铺信息方法
  - 实现更新店铺信息方法
  - 实现查询店铺商品列表方法
  - _需求: 11.1, 11.2, 11.3, 11.4_

- [x] 11.2 创建店铺 Controller


  - 创建 ShopController
  - 实现 POST /api/shop 创建店铺接口
  - 实现 GET /api/shop/{id} 获取店铺信息接口
  - 实现 PUT /api/shop/{id} 更新店铺信息接口
  - 实现 GET /api/shop/{id}/products 获取店铺商品接口
  - _需求: 11.1, 11.2, 11.3, 11.4_

- [x] 12. 实现商品管理功能（店家端）




- [x] 12.1 扩展商品 Service 添加店家功能


  - 在 ProductService 中添加创建商品方法
  - 添加更新商品方法
  - 添加删除商品方法
  - 添加上下架商品方法
  - 添加查询店铺商品列表方法
  - _需求: 12.1, 12.2, 12.3, 12.4, 12.5_

- [x] 12.2 创建店家商品管理 Controller


  - 创建 ShopProductController
  - 实现 POST /api/shop/product 创建商品接口
  - 实现 GET /api/shop/product/list 获取店铺商品列表接口
  - 实现 PUT /api/shop/product/{id} 更新商品接口
  - 实现 DELETE /api/shop/product/{id} 删除商品接口
  - 实现 PUT /api/shop/product/{id}/status 上下架商品接口
  - _需求: 12.1, 12.2, 12.3, 12.4, 12.5_

- [x] 13. 实现库存管理功能（店家端）





- [x] 13.1 实现库存管理逻辑


  - 在 ProductService 中添加更新库存方法
  - 实现库存扣减方法（订单创建时调用）
  - 实现库存恢复方法（订单取消时调用）
  - 实现库存为零时自动标记缺货
  - _需求: 13.1, 13.2, 13.3_

- [x] 13.2 创建库存管理接口


  - 在 ShopProductController 中添加库存管理接口
  - 实现 PUT /api/shop/product/{id}/stock 更新库存接口
  - _需求: 13.1_

- [x] 14. 实现订单处理功能（店家端）




- [x] 14.1 扩展订单 Service 添加店家功能


  - 在 OrderService 中添加查询店铺订单列表方法
  - 添加发货方法（更新订单状态和发货时间）
  - _需求: 14.1, 14.2, 14.3_

- [x] 14.2 创建店家订单管理 Controller


  - 创建 ShopOrderController
  - 实现 GET /api/shop/order/list 获取店铺订单列表接口
  - 实现 GET /api/shop/order/{id} 获取订单详情接口
  - 实现 PUT /api/shop/order/{id}/ship 发货接口
  - _需求: 14.1, 14.2, 14.3_

- [x] 15. 实现优惠券管理功能（店家端）




- [x] 15.1 创建优惠券 Repository 和 Service


  - 创建 CouponRepository 和 UserCouponRepository 接口
  - 创建 CouponService 接口和 CouponServiceImpl 实现类
  - 实现创建优惠券方法
  - 实现查询店铺优惠券列表方法
  - 实现查询店铺可用优惠券方法
  - 实现用户领取优惠券方法
  - 实现查询用户优惠券方法
  - 实现使用优惠券方法（订单创建时调用）
  - _需求: 15.1, 15.2, 15.3, 15.4_

- [x] 15.2 创建优惠券 Controller


  - 创建 CouponController
  - 实现 POST /api/coupon 创建优惠券接口（店家）
  - 实现 GET /api/coupon/list 获取店铺优惠券列表接口（店家）
  - 实现 GET /api/coupon/shop/{shopId} 获取店铺可用优惠券接口（用户）
  - 实现 POST /api/coupon/{id}/receive 领取优惠券接口（用户）
  - 实现 GET /api/coupon/user 获取用户优惠券接口（用户）
  - _需求: 15.1, 15.2, 15.3, 15.4_

- [x] 16. 实现管理员用户管理功能




- [x] 16.1 扩展用户 Service 添加管理员功能


  - 在 UserService 中添加查询所有用户列表方法（分页）
  - 添加搜索用户方法
  - 添加启用/禁用用户方法
  - _需求: 16.1, 16.2, 16.3, 16.4_

- [x] 16.2 创建管理员用户管理 Controller


  - 创建 AdminUserController
  - 实现 GET /api/admin/user/list 获取用户列表接口
  - 实现 GET /api/admin/user/search 搜索用户接口
  - 实现 PUT /api/admin/user/{id}/status 启用/禁用用户接口
  - _需求: 16.1, 16.2, 16.3, 16.4_

- [x] 17. 实现管理员店铺管理功能




- [x] 17.1 扩展店铺 Service 添加管理员功能


  - 在 ShopService 中添加查询所有店铺列表方法（分页）
  - 添加审核店铺方法
  - 添加启用/禁用店铺方法
  - _需求: 17.1, 17.2, 17.3, 17.4_

- [x] 17.2 创建管理员店铺管理 Controller


  - 创建 AdminShopController
  - 实现 GET /api/admin/shop/list 获取店铺列表接口
  - 实现 PUT /api/admin/shop/{id}/audit 审核店铺接口
  - 实现 PUT /api/admin/shop/{id}/status 启用/禁用店铺接口
  - _需求: 17.1, 17.2, 17.3, 17.4_

- [x] 18. 实现管理员商品审核功能





- [x] 18.1 扩展商品 Service 添加审核功能


  - 在 ProductService 中添加查询待审核商品列表方法
  - 添加审核商品方法（通过/拒绝）
  - _需求: 18.1, 18.2, 18.3, 18.4_

- [x] 18.2 创建管理员商品审核 Controller


  - 创建 AdminProductController
  - 实现 GET /api/admin/product/list 获取待审核商品列表接口
  - 实现 PUT /api/admin/product/{id}/audit 审核商品接口
  - _需求: 18.2, 18.3, 18.4_

- [x] 19. 实现管理员社区内容审核功能





- [x] 19.1 扩展社区 Service 添加管理员功能


  - 在 CommunityPostService 中添加查询所有帖子列表方法（分页）
  - 在 CommunityReplyService 中添加删除评论方法
  - _需求: 19.1, 19.2, 19.3_

- [x] 19.2 创建管理员社区管理 Controller


  - 创建 AdminCommunityController
  - 实现 GET /api/admin/post/list 获取帖子列表接口
  - 实现 DELETE /api/admin/post/{id} 删除帖子接口
  - 实现 DELETE /api/admin/reply/{id} 删除评论接口
  - _需求: 19.1, 19.2, 19.3_

- [ ] 20. 实现管理员数据统计看板功能























- [x] 20.1 创建统计 Service


  - 创建 StatisticsService 接口和实现类
  - 实现统计用户总数方法
  - 实现统计店铺总数方法
  - 实现统计商品总数方法
  - 实现统计订单总数方法
  - 实现按时间范围统计新增用户数方法
  - 实现按时间范围统计新增订单数和交易金额方法
  - 实现统计各分类商品数量和销售额方法
  - _需求: 20.1, 20.2, 20.3_

- [ ] 20.2 创建管理员数据看板 Controller
  - 创建 AdminDashboardController
  - 实现 GET /api/admin/dashboard 获取数据看板接口
  - 实现 GET /api/admin/dashboard/statistics 获取时间范围统计接口
  - 实现 GET /api/admin/dashboard/category 获取分类统计接口
  - _需求: 20.1, 20.2, 20.3_

- [ ] 21. 搭建前端基础架构
  - 配置 Vite 项目和 Element Plus
  - 封装 Axios 请求工具（request.js），包含请求/响应拦截器
  - 配置 Vue Router，创建路由模块文件
  - 配置 Pinia，创建 user store 和 cart store
  - 创建公共布局组件 Layout.vue
  - 创建导航栏组件 NavBar.vue
  - 创建页脚组件 Footer.vue
  - _需求: 所有需求的基础_

- [ ] 22. 实现前端用户模块
- [ ] 22.1 创建用户 API 和页面
  - 在 api/user.js 中封装用户相关 API（注册、登录、获取信息、更新信息）
  - 创建 views/user/Register.vue 注册页面
  - 创建 views/user/Login.vue 登录页面
  - 创建 views/user/Profile.vue 个人中心页面
  - 实现用户注册表单和验证
  - 实现用户登录表单和验证
  - 实现个人信息展示和编辑
  - _需求: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3_

- [ ] 22.2 实现用户状态管理
  - 完善 store/user.js，实现登录、登出、获取用户信息
  - 实现路由守卫，未登录用户重定向到登录页
  - 实现 Token 自动携带和过期处理
  - _需求: 1.4_

- [ ] 23. 实现前端宠物档案模块
- [ ] 23.1 创建宠物档案 API 和页面
  - 在 api/pet.js 中封装宠物档案相关 API
  - 创建 views/user/PetList.vue 宠物列表页面
  - 创建 views/user/PetForm.vue 宠物表单组件
  - 实现宠物列表展示
  - 实现添加宠物档案表单
  - 实现编辑宠物档案功能
  - 实现删除宠物档案功能
  - _需求: 3.1, 3.2, 3.3, 3.4_

- [ ] 24. 实现前端商品浏览模块
- [ ] 24.1 创建商品 API 和公共组件
  - 在 api/product.js 中封装商品相关 API
  - 创建 components/ProductCard.vue 商品卡片组件
  - 创建 components/Pagination.vue 分页组件
  - _需求: 4.1, 4.4_

- [ ] 24.2 创建商品页面
  - 创建 views/user/Home.vue 首页，展示推荐商品和热门商品
  - 创建 views/user/ProductList.vue 商品列表页面
  - 实现商品分类筛选功能
  - 实现商品搜索功能
  - 实现商品价格排序功能
  - 创建 views/user/ProductDetail.vue 商品详情页面
  - 实现商品详情展示、图片轮播、评价列表
  - 实现加入购物车按钮
  - _需求: 4.1, 4.2, 4.3, 4.4, 5.3_

- [ ] 25. 实现前端购物车模块
- [ ] 25.1 创建购物车 API 和状态管理
  - 在 api/cart.js 中封装购物车相关 API
  - 完善 store/cart.js，实现购物车状态管理
  - _需求: 6.1, 6.2, 6.3, 6.4_

- [ ] 25.2 创建购物车页面
  - 创建 views/user/Cart.vue 购物车页面
  - 实现购物车商品列表展示
  - 实现商品数量调整功能
  - 实现删除商品功能
  - 实现商品选择和全选功能
  - 实现总价计算
  - 实现结算按钮，跳转到订单确认页
  - _需求: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 26. 实现前端订单模块
- [ ] 26.1 创建订单 API 和页面
  - 在 api/order.js 中封装订单相关 API
  - 创建 views/user/OrderConfirm.vue 订单确认页面
  - 实现订单商品展示、收货地址选择、优惠券选择
  - 实现订单提交功能
  - 创建 views/user/OrderList.vue 订单列表页面
  - 实现订单状态筛选
  - 实现订单列表展示
  - 创建 views/user/OrderDetail.vue 订单详情页面
  - 实现订单详情展示
  - 实现支付、取消、确认收货按钮
  - _需求: 7.1, 7.2, 7.3, 7.4, 7.5_

- [ ] 27. 实现前端评价模块
- [ ] 27.1 创建评价 API 和组件
  - 在 api/review.js 中封装评价相关 API
  - 创建 components/ReviewList.vue 评价列表组件
  - 创建 views/user/ReviewForm.vue 评价表单页面
  - 实现评价表单（评分、内容、图片上传）
  - 实现评价提交功能
  - 在商品详情页集成评价列表组件
  - _需求: 8.1, 8.2, 8.3_

- [ ] 28. 实现前端社区模块
- [ ] 28.1 创建社区 API 和页面
  - 在 api/community.js 中封装社区相关 API
  - 创建 views/community/PostList.vue 社区帖子列表页面
  - 实现帖子列表展示（分页）
  - 创建 views/community/PostDetail.vue 帖子详情页面
  - 实现帖子详情展示
  - 实现评论列表展示
  - 实现发布评论功能
  - 实现点赞/取消点赞功能
  - 创建 views/community/PostForm.vue 发布帖子页面
  - 实现发布帖子表单（标题、内容、图片上传）
  - _需求: 9.1, 9.2, 9.3, 9.4, 10.1, 10.2, 10.3, 10.4_

- [ ] 29. 实现前端店家端模块
- [ ] 29.1 创建店铺管理页面
  - 在 api/shop.js 中封装店铺相关 API
  - 创建 views/shop/ShopLayout.vue 店家端布局组件
  - 创建 views/shop/ShopDashboard.vue 店铺首页
  - 创建 views/shop/ShopInfo.vue 店铺信息管理页面
  - 实现店铺信息展示和编辑
  - _需求: 11.1, 11.2, 11.3_

- [ ] 29.2 创建商品管理页面
  - 创建 views/shop/ProductList.vue 商品管理页面
  - 实现商品列表展示（表格形式）
  - 创建 views/shop/ProductForm.vue 商品表单页面
  - 实现添加商品表单（名称、价格、库存、分类、描述、图片）
  - 实现编辑商品功能
  - 实现上下架商品功能
  - 实现删除商品功能
  - 实现库存调整功能
  - _需求: 12.1, 12.2, 12.3, 12.4, 12.5, 13.1_

- [ ] 29.3 创建订单管理页面
  - 创建 views/shop/OrderList.vue 店铺订单列表页面
  - 实现订单列表展示（表格形式）
  - 实现订单状态筛选
  - 创建 views/shop/OrderDetail.vue 店铺订单详情页面
  - 实现订单详情展示
  - 实现发货功能
  - _需求: 14.1, 14.2, 14.3_

- [ ] 29.4 创建优惠券管理页面
  - 在 api/coupon.js 中封装优惠券相关 API
  - 创建 views/shop/CouponList.vue 优惠券管理页面
  - 实现优惠券列表展示
  - 创建 views/shop/CouponForm.vue 优惠券表单页面
  - 实现创建优惠券表单（名称、折扣金额、使用条件、有效期）
  - _需求: 15.1, 15.2_

- [ ] 30. 实现前端管理员端模块
- [ ] 30.1 创建管理员布局和数据看板
  - 在 api/admin.js 中封装管理员相关 API
  - 创建 views/admin/AdminLayout.vue 管理员端布局组件
  - 创建 views/admin/Dashboard.vue 数据看板页面
  - 实现数据统计展示（用户数、店铺数、商品数、订单数）
  - 实现图表展示（使用 ECharts）
  - _需求: 20.1, 20.2, 20.3_

- [ ] 30.2 创建用户管理页面
  - 创建 views/admin/UserManage.vue 用户管理页面
  - 实现用户列表展示（表格形式）
  - 实现用户搜索功能
  - 实现启用/禁用用户功能
  - _需求: 16.1, 16.2, 16.3, 16.4_

- [ ] 30.3 创建店铺管理页面
  - 创建 views/admin/ShopManage.vue 店铺管理页面
  - 实现店铺列表展示（表格形式）
  - 实现店铺审核功能
  - 实现启用/禁用店铺功能
  - _需求: 17.1, 17.2, 17.3, 17.4_

- [ ] 30.4 创建商品审核页面
  - 创建 views/admin/ProductAudit.vue 商品审核页面
  - 实现待审核商品列表展示
  - 实现商品审核功能（通过/拒绝）
  - _需求: 18.1, 18.2, 18.3, 18.4_

- [ ] 30.5 创建社区管理页面
  - 创建 views/admin/PostManage.vue 社区管理页面
  - 实现帖子列表展示
  - 实现删除帖子功能
  - 实现删除评论功能
  - _需求: 19.1, 19.2, 19.3_

- [ ]* 31. 编写后端单元测试
  - 为 UserService 编写单元测试
  - 为 ProductService 编写单元测试
  - 为 OrderService 编写单元测试
  - 为 CommunityPostService 编写单元测试
  - _需求: 所有需求_

- [ ]* 32. 编写前端组件测试
  - 为 ProductCard 组件编写测试
  - 为 NavBar 组件编写测试
  - 为 Cart 页面编写测试
  - _需求: 所有需求_

- [ ] 33. 系统集成和优化
  - 前后端联调测试所有功能
  - 优化数据库查询性能，添加必要索引
  - 优化前端加载性能，实现图片懒加载
  - 实现前端路由懒加载
  - 添加加载状态和错误提示
  - 优化移动端响应式布局
  - _需求: 所有需求_

- [ ] 34. 文档和部署准备
  - 编写 README.md 项目说明文档
  - 编写数据库初始化脚本和说明
  - 编写部署文档
  - 准备演示数据
  - 项目打包和部署测试
  - _需求: 所有需求_
