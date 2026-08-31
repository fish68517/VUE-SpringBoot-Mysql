# 数字法治底座总体设计

## 1. 设计目标

数字法治底座采用微服务 + 基础能力模块化架构，为事件流转、消息通知、定时任务、系统管理等业务能力提供统一底座。

总体设计目标：

- **高内聚**：缓存、日志、安全、数据访问、接口契约等基础能力各归其位，在对应模块内封装完整能力。
- **低耦合**：业务模块只依赖稳定契约和基础设施模块，不直接依赖其他业务模块实现。
- **可复用**：跨模块能力沉淀到 core/cache/data/security/log/api 等基础模块，通过自动装配、注解、SPI、RPC 契约复用。
- **可选部署**：业务服务可按能力独立部署；基础 jar 能力按需引入，未引入时不影响其他服务启动。
- **API 高度统一**：所有业务 HTTP API 使用统一路径、请求 DTO、响应包装、分页、异常、权限和日志规范。

## 2. 技术架构

| 分类 | 技术选型 |
|---|---|
| 构建工具 | Maven 3.9.X |
| Java | 17 |
| Web 框架 | Spring Boot 3.5.16 |
| 微服务 | Spring Cloud 2025.0.3 |
| 服务注册配置 | Nacos 3.1.1 |
| RPC | Dubbo 3.3.6 |
| 数据库 | MySQL 8.0 |
| ORM | MyBatis-Plus |
| 缓存 | Redis 7.2.15 |
| 工作流 | Flowable 7.2.0 |
| 定时任务 | XXL-JOB 3.3.2 |
| 链路追踪 | SkyWalking OAP 10.4.0 + Java Agent 9.7.0 |
| OSS | 默认 MinIO |

## 3. 总体架构

```text
                        ┌────────────────────┐
                        │      前端应用       │
                        └─────────┬──────────┘
                                  │ HTTP
                                  ▼
                        ┌────────────────────┐
                        │ gateway API 网关    │
                        │ JWT签名校验/转发    │
                        └─────────┬──────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        ▼                         ▼                         ▼
┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│ auth         │          │ system       │          │ event        │
│ 统一认证中心   │          │ 系统管理       │         │ 事件中心      │
└──────┬───────┘          └──────┬───────┘          └──────┬───────┘
       │                         │                         │
       │ Dubbo RPC               │ Dubbo Provider           │
       ▼                         ▼                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ api 接口契约层：RPC接口、跨模块DTO、稳定契约，不包含业务实现       │
└─────────────────────────────────────────────────────────────────┘
       ▲                         ▲                         ▲
       │                         │                         │
┌──────┴─────────────────────────┴─────────────────────────┴──────┐
│ core 基础能力层                                                   │
│ core-common / cache / crypto / data / log / security / file     │
└─────────────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────────────┐
│ MySQL / Redis / Nacos / MinIO / SkyWalking / XXL-JOB / MQ等基础设施 │
└─────────────────────────────────────────────────────────────────┘
```

## 4. 模块职责

### 4.1 业务服务模块

| 模块 | 定位 | 主要职责 | 可选部署 |
|---|---|---|---|
| `ctis-digital-law-gateway` | API 网关 | JWT 签名校验、请求路由、Token 透传、统一入口治理 | 必选 |
| `ctis-digital-law-auth` | 统一认证中心 | 基于 Spring Authorization Server 的 OAuth2/OIDC 认证、JWT 签发、Token 生命周期、登录锁定流程编排 | 必选 |
| `ctis-digital-law-system` | 系统管理与接入管理 | 用户、角色、菜单、部门、权限源数据、数据权限源数据、三方接入方、渠道、应用密钥元数据、接口授权、IP 白名单、免认证接口配置等接入元数据 | 必选 |
| `ctis-digital-law-event` | 事件中心 | 流程引擎、自定义表单、业务事件流转 | 可选 |
| `ctis-digital-law-message` | 消息通知 | MQ、短信、站内信、通知模板 | 可选 |
| `ctis-digital-law-job` | 定时任务 | XXL-JOB 执行器、任务调度执行 | 可选 |
| `ctis-digital-law-integration` | 三方接入适配 | 渝快政、渝快办等外部系统协议适配、回调处理、接口编排 | 可选 |

### 4.2 基础能力模块

| 模块 | 定位 | 主要职责 | 使用方式 |
|---|---|---|---|
| `ctis-digital-law-api` | 接口契约 | Dubbo RPC 接口、跨模块 RPC DTO | 业务服务依赖 |
| `ctis-digital-law-core-common` | 框架核心公共 | `ResponseResult`、异常模型、分页请求、上下文模型、API前缀常量、敏感字段注解/枚举、框架常量等平台级轻量契约 | 所有模块可依赖 |
| `ctis-digital-law-cache` | 缓存基础 | Redis 适配、公共/跨模块缓存 Key 常量、Key 生成器、版本号失效、批量/精准失效工具 | 按需依赖 |
| `ctis-digital-law-crypto` | 加解密基础 | Encryptor SPI、算法适配、KeyProvider、密钥版本、轮换策略 | security/data/log 按需依赖 |
| `ctis-digital-law-data` | 数据访问基础 | MyBatis-Plus 配置、分页、`BaseEntity` 实体基类、审计字段填充、数据权限拦截器、敏感字段加解密 TypeHandler/拦截器 | 访问数据库的服务依赖 |
| `ctis-digital-law-file` | 文件基础 | 文件上传、文件下载、对象存储适配、文件访问地址生成、文件元数据管理、存储桶与对象 Key 规则封装，默认对接 MinIO | 需要文件能力的服务按需依赖 |
| `ctis-digital-law-log` | 操作日志 | `@OperationLog`、日志采集、脱敏、异步处理、LogHandler SPI | 按需依赖 |
| `ctis-digital-law-security` | 安全鉴权 | JWT 解析、SecurityContext、权限校验、当前用户上下文、权限加载、HTTP 传输解密/响应加密 | 资源服务依赖 |
| `ctis-digital-law-common` | 二开业务公共 | 二次开发业务可复用的枚举、DTO、常量、工具和扩展公共逻辑 | 二开业务模块按需依赖 |

## 5. 依赖原则

### 5.1 基本规则

1. `api` 只放契约，不放实现。
2. `core-common` 只能放平台框架级、基础模块共享、无二开业务语义的公共模型、注解、枚举和常量，不放具体业务规则、二开业务枚举、业务工具类、加解密算法、Filter、TypeHandler 等实现。
3. `cache/crypto/data/log/security/file` 必须封装本领域完整基础能力，业务模块不得重复实现。
4. `common` 只放二次开发业务可复用的枚举、DTO、常量、工具和扩展公共逻辑，不承载框架基础能力实现。
5. 业务服务之间不得直接依赖实现模块，跨服务调用只能通过 `api` 契约 + Dubbo RPC。
6. 数据 Owner 模块负责维护自己的数据和 RPC 实现，其他模块不得直连其数据库表。
7. 新增跨模块能力前必须说明职责归属、调用方向、是否会引入循环依赖。

### 5.2 推荐依赖方向

```text
业务服务(system/event/message/job/auth)
  ├── api
  ├── core-common
  ├── common     (二开业务公共能力按需)
  ├── cache      (按需)
  ├── crypto     (涉及加解密时)
  ├── data       (访问数据库时)
  ├── file       (上传、下载或访问对象存储时)
  ├── log        (需要操作日志时)
  └── security   (资源服务需要鉴权时)

integration → api / core-common / common / cache / crypto / log

integration 如需暴露受保护 HTTP 接口，可按资源服务规则引入 security；仅作为渠道协议适配和 RPC Provider 时不依赖 security。

security → api / cache / crypto / core-common
data     → api / security / crypto / core-common  (数据权限需要当前用户和RPC契约时)
file     → core-common / data / security / crypto  (文件元数据、鉴权上下文或存储凭证加密按需引入)
log      → crypto / core-common
cache    → core-common
crypto   → core-common
api      → core-common
common   → core-common
```

禁止依赖方向：

```text
security → system
event/message/job → system 实现模块
integration → system 实现模块
api → 任意业务实现模块
core-common → cache/data/log/security/file/业务模块
file → 业务实现模块
crypto → security/data/log/业务模块
common → cache/data/log/security/crypto/file/业务实现模块
```

## 6. 核心链路设计

### 6.1 登录与请求鉴权链路

```text
1. 前端提交登录请求到 auth
2. auth 通过 SysUserRpc 调 system 查询用户和状态
3. auth 校验 BCrypt 密码
4. auth 记录登录成功/失败，失败计数由 system 通过 Redis 维护
5. auth 签发瘦身 JWT，只携带 userId、username，不携带权限集合
6. 请求进入 gateway，gateway 校验 JWT 签名并透传 Token
7. 下游资源服务通过 security 解析 JWT
8. security 使用 cache 模块生成权限缓存 Key，优先查 Redis
9. 权限缓存未命中时，security 默认通过 SysPermissionRpc 调 system 查询权限；启用本地 DB 冗余模式时，由 security 的 LocalDbPermissionLoader 直连权限库查询
10. security 填充 SecurityContext，Controller 使用 @PreAuthorize 校验权限
```

权限加载模式：

| 模式 | 配置值 | 说明 | 适用场景 |
|---|---|---|---|
| RPC 模式 | `rpc` | 默认模式，通过 `SysPermissionRpc` 调 system 查询权限 | 微服务标准部署 |
| 本地 DB 冗余模式 | `local-db` | security 直接查询权限表，减少对 system RPC 的运行时依赖 | 单体化部署、同库部署、容灾冗余 |
| 空权限模式 | `none` | 返回空权限集合，失败默认拒绝 | 测试、无权限源兜底 |

> `local-db` 是可选冗余实现，不改变 `PermissionLoader` SPI。启用后 security 会依赖数据访问能力和 system 权限表结构，必须在部署说明中明确其耦合成本。

### 6.2 数据权限链路

```text
1. 业务 Mapper 或 Service 方法标注 @DataScope
2. data 模块 MyBatis 拦截器识别注解
3. 拦截器通过 security 获取当前用户
4. 拦截器通过 api 契约调用 system 获取角色数据范围
5. system 返回部门范围、本人范围等权限源数据
6. data 模块拼接安全 SQL 条件
7. Mapper 执行业务查询
```

设计边界：

- `@DataScope` 注解归 `core-common` 或 data 统一暴露，不归单个业务模块。
- SQL 拼接能力归 `data`。
- 用户角色、部门、数据范围源数据归 `system`。
- 当前用户上下文归 `security`。

### 6.3 操作日志链路

```text
1. Controller 方法标注 @OperationLog
2. log 模块 AOP 自动采集请求、响应、异常、耗时、操作人
3. log 模块统一脱敏 password/token/secret/credential/pwd 等敏感字段
4. 默认 AsyncLogHandler 异步批量写入
5. 业务模块可通过自定义 LogHandler 替换为 Kafka、ES 或其他存储
```

设计边界：

- 业务模块只使用注解或 LogHandler 接口。
- 日志表结构、脱敏规则、异步队列、降级策略归 `log` 模块。
- 业务模块不得直接拼装操作日志表 SQL。

### 6.4 缓存链路

```text
业务模块/cache使用方
  │
  ▼
cache 模块 Key 生成器 / Redis 工具 / 版本号工具
  │
  ├── 公共权限版本：perm:version
  ├── 公共权限缓存：user:perms:{version}:{userId}
  └── 通用失效工具：精准删除 / 版本号递增 / 批量删除
```

设计边界：

- 公共/跨模块缓存 Key 常量、Key 格式、默认 Key 生成、版本号规则必须封装在 `cache` 模块。
- 模块私有缓存 Key 可放在模块内部，但必须通过模块内常量或 KeyProvider 统一生成，禁止散落字符串拼接。例如 `system` 独有的 `user:info:{userId}`、`dept:tree`、`role:menus:{roleId}` 可由 system 内部封装。
- system 负责权限数据变化后的缓存失效动作。
- security 负责权限缓存读写。
- 使用方不得自行硬编码跨模块共享 Key 字符串。

#### 6.4.1 跨模块缓存 Key 规范

公共/跨模块缓存 Key 的常量、格式和默认生成规则统一归 `ctis-digital-law-cache`，模块私有 Key 留在模块内部封装。

| Key | 说明 | 写入方 | 失效方 |
|---|---|---|---|
| `perm:version` | 权限缓存版本号 | system | system |
| `user:perms:{version}:{userId}` | 用户权限标识集合 | security | system |
| `auth:token:version:{userId}` | 用户 Token 版本号 | auth/system | auth/system |
| `auth:jti:blacklist:{jti}` | access token 登出或撤销黑名单 | auth | TTL 自动失效 |
| `auth:refresh:{tokenId}` | refresh token 状态或轮换记录 | auth | auth |
| `auth:client:{clientId}` | OAuth2 客户端配置缓存 | auth | auth/system |
| `open:client:{appId}` | 三方接入方基础配置和密钥元数据 | security/gateway | system |
| `open:grant:{appId}:{apiCode}` | 接入方接口授权和 scope | security/gateway | system |
| `open:ip:{appId}` | 接入方 IP 白名单 | gateway/security | system |
| `open:nonce:{appId}:{nonce}` | Open API 防重放 nonce | security/gateway | TTL 自动失效 |
| `auth:exempt:{method}:{pathHash}` | 免认证接口认证模式 | security/gateway | system |
| `external:login:state:{provider}:{state}` | 外部身份登录 state 防重放 | auth | TTL 自动失效 |
| `external:login:code:{provider}:{codeHash}` | 外部身份 authCode/ticket 防重放 | auth/integration | TTL 自动失效 |

约束：

- 使用方不得在业务代码中散落拼接上述 Key。
- TTL 和版本号策略由 cache 模块提供默认值，业务模块只能通过配置调整时长，不直接调整 Key 格式。
- system 作为接入元数据 Owner，负责接入方、授权、IP 白名单、免认证配置变更后的缓存失效。
- nonce、state、authCode/ticket 属于短 TTL 一次性 Key，命中重复必须拒绝请求。

### 6.5 文件上传下载链路

文件上传、下载和对象存储访问统一归 `ctis-digital-law-file`，默认适配 MinIO，后续可通过存储适配接口扩展其他 OSS。

```text
业务模块/前端
  │
  ▼
file 模块上传/下载接口或文件服务
  │
  ├── security 获取当前用户和权限上下文
  ├── data 记录文件元数据（按是否需要持久化引入）
  ├── crypto 保护存储凭证或敏感文件属性（按需）
  └── MinIO/OSS 执行对象读写
```

设计边界：

- 业务模块不得直接散落 MinIO 客户端调用、Bucket 名称、对象 Key 拼接和临时访问地址生成规则。
- file 模块负责统一校验文件大小、类型、对象 Key 规则、下载授权和访问地址有效期。
- 文件数据 Owner 默认为 file 模块；业务模块只保存业务与文件标识的关联关系。
- 暂不单独编写 file 模块详细设计，后续需要扩展断点续传、秒传、预览、病毒扫描等能力时再补充。

### 6.6 敏感数据加密链路

数据库敏感字段加密和 HTTP 传输解密按能力分层，避免 `core-common` 承担实现职责。

```text
core-common
  ├── @SensitiveEncrypt
  ├── SensitiveTypeEnum
  └── CryptoSceneEnum

crypto
  ├── Encryptor SPI
  ├── AES/SM4/RSA/SM2 算法适配
  ├── KeyProvider
  ├── 密钥版本
  └── 密钥轮换策略

security
  ├── HTTP 请求解密 Filter
  ├── 响应加密 Filter
  ├── 请求签名校验
  └── timestamp + nonce 防重放

data
  ├── MyBatis TypeHandler
  ├── MyBatis 拦截器
  └── 字段写入加密、读取解密
```

职责边界：

- `core-common` 只放注解、枚举、常量和轻量配置模型，不放具体加解密算法。
- `crypto` 只提供通用加解密能力，不感知 HTTP、数据库表、业务字段。
- `security` 负责 HTTP 传输层解密、响应加密、签名和防重放，依赖 `crypto` 但不向 `data` 暴露实现。
- `data` 负责数据库字段加解密集成，依赖 `crypto`，不依赖 `security` 的 HTTP 能力。
- 业务模块只通过注解、配置和自动装配使用能力，不直接调用底层算法拼装密文。

## 7. 统一 API 规范

### 7.1 HTTP API 规范

| 项 | 规范 |
|---|---|
| 请求方法 | 只允许 `GET`、`POST` |
| 入参 | 优先使用 `Req` DTO，禁止直接绑定数据库实体 |
| 出参 | 统一返回 `ResponseResult<T>` |
| 分页 | 查询 DTO 继承 `BasePageReq`，返回框架统一分页结构 |
| 校验 | 使用 Jakarta Validation |
| 权限 | 使用 Spring Security，Controller 方法标注 `@PreAuthorize` |
| 日志 | 写操作、敏感操作标注 `@OperationLog` |
| 命名 | `模块:业务:操作` 权限标识，如 `system:user:list` |

### 7.2 推荐路径规范

平台 HTTP API 统一采用端类型 + 版本号前缀：

| 访问端 | API 前缀 | 说明 |
|---|---|---|
| Web 管理端 | `/api/web/v1` | 管理后台、PC Web 控制台 |
| H5/移动端 | `/api/h5/v1` | 移动端、H5、小程序 WebView |
| Open API | `/api/open/v1` | 对外开放接口，需单独签名、限流和审计 |
| 内部管理/运维 | `/api/admin/v1` | 运维管理接口，默认不对公网暴露 |

示例：

```text
GET  /api/web/v1/system/user/page
GET  /api/h5/v1/event/task/page
POST /api/open/v1/message/notify/send
```

`/api/{client}/v{version}` 属于跨模块、无业务归属的通用常量，应放入 `ctis-digital-law-core-common`。

建议定义：

```java
package cn.chinatelecom.cq.ctis.digital.law.core.common.constant;

/**
 * API 路径前缀常量
 */
public final class ApiPathConstants {

    public static final String API_ROOT = "/api";

    public static final String WEB_V1 = API_ROOT + "/web/v1";

    public static final String H5_V1 = API_ROOT + "/h5/v1";

    public static final String OPEN_V1 = API_ROOT + "/open/v1";

    public static final String ADMIN_V1 = API_ROOT + "/admin/v1";

    private ApiPathConstants() {
    }
}
```

Controller 路径推荐使用常量组合，避免硬编码前缀：

```java
@RestController
@RequestMapping(ApiPathConstants.WEB_V1 + "/system/user")
public class SysUserController {
}
```

具体资源路径规范：

```text
GET  /api/{client}/v{version}/{module}/{resource}/page         分页查询
GET  /api/{client}/v{version}/{module}/{resource}/{id}         详情查询
GET  /api/{client}/v{version}/{module}/{resource}/list         下拉或轻量列表
POST /api/{client}/v{version}/{module}/{resource}              新增
POST /api/{client}/v{version}/{module}/{resource}/update       修改
POST /api/{client}/v{version}/{module}/{resource}/delete       删除
POST /api/{client}/v{version}/{module}/{resource}/changeStatus 状态变更
```

示例：

```text
GET  /api/web/v1/system/user/page
GET  /api/web/v1/system/user/{id}
POST /api/web/v1/system/user
POST /api/web/v1/system/user/update
POST /api/web/v1/system/user/delete
POST /api/web/v1/system/user/changeStatus
```

约束：

- API 前缀常量放 `core-common`，不放具体业务模块。
- 业务模块不得自行定义 `/api/web/v1`、`/api/h5/v1` 等重复常量。
- API 版本升级优先新增 `WEB_V2`、`H5_V2`，不得直接破坏已有 v1 契约。
- gateway 路由、Knife4j/OpenAPI 分组、Controller `@RequestMapping` 应使用同一套前缀约定。

### 7.3 三方系统接入规范

三方系统接入统一走 Open API 前缀，按渠道和资源组织路径：

```text
/api/open/v1/{channel}/{module}/{resource}/{action}
```

示例：

```text
POST /api/open/v1/ykz/system/user/sync
POST /api/open/v1/ykb/event/task/push
POST /api/open/v1/common/message/notify/callback
```

渠道编码：

| 渠道 | 编码 | 说明 |
|---|---|---|
| 渝快政 | `ykz` | 面向政务工作端、组织用户、统一待办等场景 |
| 渝快办 | `ykb` | 面向群众端、移动办事、事项查询等场景 |
| 通用开放接口 | `common` | 非特定渠道的标准三方接口 |

接入职责边界：

| 能力 | 归属模块 | 说明 |
|---|---|---|
| 接入方管理 | `system` | 维护 appId、appName、渠道、状态、联系人、有效期 |
| 密钥与证书元数据 | `system` | 维护 keyVersion、公钥、证书指纹、密钥有效期；密钥明文不得落库 |
| 接口授权 | `system` | 维护接入方可访问的接口、scope、数据范围和回调地址白名单 |
| IP 白名单 | `system` | 维护接入方来源 IP/CIDR，供 gateway/security 校验 |
| 接入配置缓存 | `cache` | app 配置、接口授权、nonce 防重放缓存 Key 和默认生成规则 |
| 签名与加解密 | `security` + `crypto` | security 编排 HTTP 校验流程，crypto 提供算法和 KeyProvider |
| 限流与路由 | `gateway` | 按 appId、channel、path 做限流、路由和入口审计 |
| 渠道协议适配 | `integration` | 适配渝快政、渝快办差异协议，不承载业务数据 Owner 职责 |
| 业务处理 | 业务 Owner 模块 | 例如用户归 system，流程归 event，消息归 message |

三方调用链路：

```text
三方系统
  ↓
gateway 校验路径、IP 白名单、限流
  ↓
security 读取 appId、timestamp、nonce、signature
  ↓
security 通过 system RPC 或 cache 获取接入方配置和接口授权
  ↓
security 校验签名、timestamp、nonce、防重放、接口 scope
  ↓
必要时调用 crypto 解密请求体
  ↓
integration 做渠道协议转换或直接路由到业务 Owner 模块
  ↓
业务 Owner 模块处理并记录操作日志
```

接入方管理建议数据模型：

| 表 | 说明 | 关键字段 |
|---|---|---|
| `sys_open_client` | 三方接入方 | app_id、app_name、channel、status、expire_time、contact |
| `sys_open_client_key` | 接入方密钥/证书版本 | app_id、key_version、public_key、secret_cipher、algorithm、status、expire_time |
| `sys_open_api` | 开放接口目录 | api_code、path、method、auth_mode、scope、status |
| `sys_open_client_api` | 接入方接口授权 | app_id、api_code、scope、rate_limit、data_scope |
| `sys_open_ip_whitelist` | 来源 IP 白名单 | app_id、ip_cidr、status |
| `sys_open_callback` | 回调地址白名单 | app_id、callback_url、status |

约束：

- 三方接口默认不使用用户登录 JWT，不等于无保护；必须使用 appId + 签名 + timestamp + nonce + scope 授权。
- appSecret、私钥、对称密钥不得明文存储；如需落库只能存密文或 KMS 引用。
- 渝快政、渝快办只作为渠道适配，不允许业务模块直接散落渠道协议解析逻辑。
- Open API 的新增、下线、授权变更必须记录操作日志。

### 7.4 免认证接口规范

免认证只表示跳过登录态 JWT 鉴权，不表示跳过全部安全控制。平台将接口认证模式分为：

| 认证模式 | 配置值 | 说明 | 示例 |
|---|---|---|---|
| 登录 JWT | `jwt` | Web/H5 登录用户访问，走 Spring Security 权限 | `/api/web/v1/system/user/page` |
| 开放签名 | `open-sign` | 三方系统访问，跳过用户 JWT，但必须校验 app 签名、timestamp、nonce、scope | `/api/open/v1/ykz/...` |
| 外部身份登录 | `external-login` | 渠道内用户登录入口，跳过用户 JWT，但必须完成渠道身份认证、state 校验和自动开户/绑定 | `/api/h5/v1/auth/ykb/login` |
| 内部信任 | `internal` | 内网服务间回调，必须限制网段、服务名、Header 或 mTLS | `/api/admin/v1/job/callback` |
| 完全匿名 | `anonymous` | 仅允许健康检查、验证码图片、公开字典等低风险接口 | `/api/open/v1/common/health` |

免认证接口管理规则：

- 免认证配置 Owner 为 `system`，由系统管理维护接口路径、方法、认证模式、风险等级、启停状态、有效期。
- `gateway` 只做入口白名单和路由放行，最终安全语义由 `security` 根据认证模式执行。
- `anonymous` 必须最小化，禁止用于写操作、敏感数据查询、个人信息查询、业务审批、文件下载等接口。
- `open-sign` 不纳入用户权限菜单，但必须纳入接入方接口授权和操作审计。
- `external-login` 不纳入用户权限菜单，但必须纳入渠道登录配置、state 防重放和登录审计。
- 免认证路径必须精确到方法和路径，禁止使用过宽通配符，例如禁止直接配置 `/api/**`。
- 所有免认证接口必须有风险说明、负责人和下线时间；长期有效需定期复核。

建议数据模型：

| 表 | 说明 | 关键字段 |
|---|---|---|
| `sys_auth_exempt_api` | 免认证接口配置 | path、method、auth_mode、risk_level、status、expire_time、owner |
| `sys_auth_exempt_audit` | 免认证配置审计 | api_id、operation、before_value、after_value、operator、operate_time |

### 7.5 外部身份登录与自动开户规范

渝快办是外部身份登录的一类代表场景。该类场景不是普通三方 Open API 调用，而是外部渠道内的用户访问我方前端应用后，由我方后端完成外部身份认证、本系统用户绑定/自动开户，并签发本系统 JWT。

外部渠道只作为外部 Identity Provider（IdP）。渠道认证结果只能作为登录凭证输入，不能直接作为本系统登录态；本系统最终认证主体、OAuth2 Token 签发权和 Token 生命周期管理始终归 `auth`。

统一链路：

```text
群众访问外部渠道内的我方应用
  ↓
前端获取渠道 authCode/ticket/state
  ↓
POST /api/h5/v1/auth/{provider}/login
  ↓
auth 编排外部登录
  ↓
integration 调渠道认证接口换取 ExternalIdentity
  ↓
auth 调 system 查询/绑定/创建本系统用户
  ↓
system 校验用户状态、绑定外部身份、分配默认角色
  ↓
auth 签发本系统 JWT
  ↓
前端携带平台 JWT 访问 /api/h5/v1/**
```

路径规范：

```text
POST /api/h5/v1/auth/{provider}/login
```

示例：

```text
POST /api/h5/v1/auth/ykb/login
POST /api/h5/v1/auth/ykz/login
```

模块职责：

| 能力 | 归属模块 | 说明 |
|---|---|---|
| 外部登录入口 | `auth` | 接收 authCode/ticket/state，编排认证、开户、绑定、签发 JWT |
| 渠道认证适配 | `integration` | 调渝快办、渝快政等渠道认证接口，统一转换为 `ExternalIdentity`；不签发本系统 JWT |
| 自动开户/绑定 | `system` | 维护本系统用户、外部身份绑定、默认角色分配和用户状态校验 |
| 后续请求鉴权 | `security` | 平台 JWT 鉴权、权限加载、失败默认拒绝 |
| 敏感字段保护 | `data` + `crypto` | 姓名、手机号、证件号等字段加密，hash 辅助列用于匹配 |
| 临时态缓存 | `cache` | authCode/state 防重放、渠道认证结果短缓存 |

外部身份标准模型：

| 字段 | 说明 |
|---|---|
| `provider` | 外部身份来源，例如 `ykb`、`ykz` |
| `externalUserId` | 外部用户唯一 ID |
| `unionId` | 外部统一 ID，渠道提供时使用 |
| `realName` | 姓名，落库时按敏感字段处理 |
| `phone` | 手机号，落库时加密并维护 hash |
| `idCardNo` | 证件号，落库时加密并维护 hash |
| `certified` | 是否实名 |
| `authTime` | 外部认证时间 |
| `rawPayloadDigest` | 原始报文摘要，用于审计，不保存完整敏感原文 |

自动开户规则：

- 默认角色、默认部门、用户名生成规则必须配置化，不得写死在代码中。
- 默认角色不存在、停用或已删除时，拒绝登录并记录告警。
- 同一 `provider + externalUserId/unionId` 重复登录必须命中已有绑定，不允许重复创建用户。
- 默认不使用手机号自动合并已有用户；确需合并时必须使用 hash 辅助列、唯一匹配、审计记录和显式配置。
- 本系统用户被停用、锁定、删除时，即使外部渠道认证通过，也不得签发平台 JWT。
- 渠道 token、原始认证报文不得返回前端；平台只返回本系统 JWT。

该登录入口的安全语义：

- `/api/h5/v1/auth/{provider}/login` 可跳过用户 JWT，但认证模式不是 `anonymous`，应配置为 `external-login`。
- 必须校验 `state`，防止 CSRF 和登录串号。
- 渠道 authCode/ticket 必须一次性使用，防重放状态由 cache 模块维护。
- 外部认证失败、渠道服务不可用、自动开户失败时，必须失败拒绝，不允许降级为匿名登录。

### 7.6 DTO 命名规范

```text
xxx.dto.req.UserCreateReq
xxx.dto.req.UserUpdateReq
xxx.dto.req.UserQueryReq
xxx.dto.resp.UserResp
xxx.dto.resp.UserDetailResp
```

要求：

- `Req`、`Resp` 严格区分。
- 外部接口不得返回实体对象。
- RPC DTO 与 HTTP DTO 分开定义，RPC DTO 放 `api` 模块。
- DTO 字段必须做长度、格式、枚举取值校验。

### 7.7 响应规范

所有 Controller 返回：

```java
ResponseResult<T>
```

推荐语义：

| 场景 | data |
|---|---|
| 新增/修改/删除成功 | `Void` 或业务需要的 ID |
| 详情查询 | `XxxResp` |
| 列表查询 | `List<XxxResp>` |
| 分页查询 | `IPage<XxxResp>` 或统一分页包装 |
| 下拉选项 | `List<XxxOptionResp>` |

## 8. 可选部署设计

### 8.1 服务可选性

| 服务 | 可选性 | 缺失影响 |
|---|---|---|
| gateway | 必选 | 无统一入口 |
| auth | 必选 | 无法登录和签发 Token |
| system | 必选 | 无用户权限源数据 |
| event | 可选 | 不提供事件流程能力 |
| message | 可选 | 不提供消息通知能力 |
| job | 可选 | 不提供定时任务执行能力 |
| integration | 可选 | 不提供渝快政、渝快办等三方渠道协议适配能力；标准 Open API 仍可由业务 Owner 模块直接提供 |

### 8.2 基础模块可选性

| 基础模块 | 可选性 | 降级策略 |
|---|---|---|
| cache | 按需 | 无 Redis 能力时相关缓存能力不可用，业务需走数据库或失败默认拒绝 |
| crypto | 涉及加解密时必选 | 未引入则 HTTP 传输解密、响应加密、数据库字段加密等能力不可用 |
| security | 资源服务必选 | 未引入则不具备统一鉴权能力 |
| log | 按需 | 未引入则不记录操作日志 |
| data | 访问数据库服务必选 | 未引入则无 MyBatis-Plus、审计填充、数据权限能力 |
| file | 涉及文件上传/下载时按需 | 未引入则不提供统一文件上传、下载、对象存储适配和文件元数据管理能力 |

### 8.3 自动装配原则

- 基础模块通过 Spring Boot AutoConfiguration 提供能力。
- 自动装配必须使用 `@ConditionalOnMissingBean` 支持业务覆盖。
- 依赖缺失时优先安全降级，不能让鉴权绕过。
- 可选能力不得强制业务模块引入无关依赖。

## 9. 配置治理

> 统一 YAML 示例和配置项说明见 [configuration-examples.md](./configuration-examples.md)。

配置按层分离：

| 配置文件 | 内容 |
|---|---|
| `common-datasource.yml` | 数据源、MyBatis-Plus 公共配置 |
| `common-redis.yml` | Redis、缓存公共配置 |
| `common-api.yml` | API 根路径、端类型前缀、默认版本 |
| `common-crypto.yml` | 加解密算法、KeyProvider、密钥版本、传输加密默认策略 |
| `common-security.yml` | JWT、公钥、权限缓存、超级管理员标识、三方签名、免认证接口策略 |
| `common-log.yml` | 操作日志队列、批量、脱敏等配置 |
| `common-file.yml` | 文件上传下载、对象存储类型、MinIO 连接、桶名、对象 Key 规则、访问地址有效期等配置 |
| `{service-name}.yaml` | 服务独有配置 |

服务配置导入顺序：

```yaml
spring:
  config:
    import:
      - optional:nacos:common-datasource.yml
      - optional:nacos:common-redis.yml
      - optional:nacos:common-api.yml
      - optional:nacos:common-crypto.yml
      - optional:nacos:common-security.yml
      - optional:nacos:common-log.yml
      - optional:nacos:common-file.yml
      - nacos:${spring.application.name}.yaml
```

约束：

- 禁止硬编码密钥、地址、账号密码。
- 生产环境必须通过 Nacos 或环境变量覆盖默认敏感配置。
- 新增配置项必须说明默认值、作用范围、适用模块和是否允许动态刷新。

## 10. 数据设计通用规范

| 项 | 规范 |
|---|---|
| 主键 | BIGINT 雪花 ID |
| 逻辑删除 | `del_flag`，0-存在，1-删除 |
| 审计字段 | `create_by`、`create_time`、`update_by`、`update_time` |
| 创建/更新人 | 统一使用当前用户 ID，类型 BIGINT |
| 唯一字段复用 | 需要软删除后复用时使用 `deleted_id` 参与联合唯一索引 |
| SQL 查询 | 禁止 `select *` |
| 写操作 | 必须记录操作日志 |
| 分页 | 使用框架标准分页 |

软删除唯一字段示例：

```sql
deleted_id BIGINT NOT NULL DEFAULT 0 COMMENT '删除唯一标识：未删除为0，删除后写入当前记录ID',
UNIQUE KEY uk_xxx_biz_deleted (biz_code, deleted_id)
```

删除时：

```text
del_flag = 1
deleted_id = id
```

## 11. 扩展治理

新增模块或能力必须先回答：

1. 该能力的数据 Owner 是哪个模块？
2. 是否需要跨模块调用？如果需要，RPC 契约是否应放 `api`？
3. 是否属于基础能力？如果属于，应沉淀到 cache/crypto/data/log/security/core-common 哪个模块；如果属于二开业务公共能力，是否应放入 common？
4. 是否引入循环依赖？
5. 是否支持可选部署？
6. HTTP API 是否符合统一请求、响应、权限、日志、分页规范？
7. 是否需要缓存？如果是跨模块共享 Key，Key 生成和失效规则是否由 cache 模块提供；如果是模块私有 Key，是否已在模块内统一封装？
8. 是否涉及安全敏感数据？是否完成输入校验、传输解密、存储加密、脱敏、审计和权限控制？
9. 是否对三方系统或免认证开放？如果是，是否完成接入方授权、签名、防重放、限流、审计和有效期控制？
10. 是否属于外部身份登录？如果是，是否完成渠道认证、外部身份绑定、自动开户幂等、默认角色配置、平台 JWT 签发和用户状态校验？

## 12. 模块设计文档关系

总体设计是模块设计的上层约束。现有模块详细设计包括：

| 文档 | 说明 |
|---|---|
| [auth-module-design.md](./auth-module-design.md) | 统一认证中心模块详细设计 |
| [gateway-module-design.md](./gateway-module-design.md) | API 网关模块详细设计 |
| [system-module-design.md](./system-module-design.md) | 系统管理模块详细设计 |
| [security-module-design.md](./security-module-design.md) | 安全鉴权模块详细设计 |
| [operation-log-design.md](./operation-log-design.md) | 操作日志模块详细设计 |
| [crypto-module-design.md](./crypto-module-design.md) | 加解密基础模块详细设计 |
| [integration-module-design.md](./integration-module-design.md) | 三方渠道适配与外部身份接入详细设计 |

当模块设计与本文档冲突时，优先按总体设计修订模块设计，确保整体架构保持高内聚、低耦合、可复用、可选部署和 API 统一。
