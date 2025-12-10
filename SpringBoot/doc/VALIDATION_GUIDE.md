# 数据验证实现指南

## 概述

本系统实现了三层数据验证机制，确保数据的完整性和有效性：

1. **请求参数验证（Bean Validation）** - 在 DTO 层使用注解验证
2. **业务逻辑验证** - 在 Service 层实现复杂的业务规则验证
3. **自定义验证器** - 为特定的验证需求创建自定义注解和验证器

## 1. 请求参数验证（Bean Validation）

### 1.1 标准验证注解

系统使用 Jakarta Validation（原 javax.validation）提供的标准注解：

#### 常用注解

| 注解 | 说明 | 示例 |
|------|------|------|
| `@NotNull` | 字段不能为 null | `@NotNull(message = "ID is required")` |
| `@NotBlank` | 字符串不能为空或仅包含空格 | `@NotBlank(message = "Name is required")` |
| `@NotEmpty` | 集合不能为空 | `@NotEmpty(message = "List cannot be empty")` |
| `@Size` | 字符串、集合或数组的大小 | `@Size(min = 1, max = 200)` |
| `@Min` | 数字最小值 | `@Min(value = 0)` |
| `@Max` | 数字最大值 | `@Max(value = 100)` |
| `@DecimalMin` | 十进制数最小值 | `@DecimalMin(value = "0.0")` |
| `@DecimalMax` | 十进制数最大值 | `@DecimalMax(value = "100.0")` |
| `@Email` | 邮箱格式验证 | `@Email(message = "Invalid email")` |
| `@Pattern` | 正则表达式验证 | `@Pattern(regexp = "^[a-z]+$")` |

### 1.2 DTO 验证示例

#### RegisterRequest - 用户注册请求

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;
}
```

#### CreateTravelRecordRequest - 创建旅游记录请求

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange(startDateField = "startDate", endDateField = "endDate")
public class CreateTravelRecordRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotBlank(message = "Destination is required")
    @Size(min = 1, max = 200, message = "Destination must be between 1 and 200 characters")
    private String destination;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String diaryContent;

    private Boolean isPublic;
}
```

### 1.3 Controller 中的验证

在 Controller 中使用 `@Valid` 注解触发 DTO 验证：

```java
@PostMapping("/register")
public ResponseEntity<ApiResponse<AuthResponse>> register(
        @Valid @RequestBody RegisterRequest registerRequest) {
    // 如果验证失败，Spring 会自动返回 400 Bad Request
    // 验证错误会被 GlobalExceptionHandler 捕获并格式化
    AuthResponse authResponse = authService.register(registerRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(authResponse));
}
```

## 2. 自定义验证器

### 2.1 日期范围验证器

用于验证开始日期不晚于结束日期。

#### 注解定义 - ValidDateRange

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "Start date must be before or equal to end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startDateField();
    String endDateField();
}
```

#### 验证器实现 - DateRangeValidator

```java
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.startDateField = annotation.startDateField();
        this.endDateField = annotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        LocalDate startDate = getFieldValue(value, startDateField, LocalDate.class);
        LocalDate endDate = getFieldValue(value, endDateField, LocalDate.class);

        if (startDate == null || endDate == null) return true;

        if (startDate.isAfter(endDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Start date must be before or equal to end date")
                    .addPropertyNode(startDateField)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
```

#### 使用示例

```java
@ValidDateRange(startDateField = "startDate", endDateField = "endDate")
public class CreateTravelRecordRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
```

### 2.2 文件大小验证器

用于验证文件大小不超过最大限制。

#### 注解定义 - ValidFileSize

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface ValidFileSize {
    String message() default "File size exceeds maximum allowed size";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int maxSizeInMB() default 500;
}
```

#### 验证器实现 - FileSizeValidator

```java
public class FileSizeValidator implements ConstraintValidator<ValidFileSize, Long> {
    private long maxSizeInBytes;

    @Override
    public void initialize(ValidFileSize annotation) {
        this.maxSizeInBytes = (long) annotation.maxSizeInMB() * 1024 * 1024;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value > maxSizeInBytes) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("File size must not exceed %d MB", 
                    maxSizeInBytes / (1024 * 1024)))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
```

#### 使用示例

```java
public class FileUploadRequest {
    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be greater than 0")
    @ValidFileSize(maxSizeInMB = 500, message = "File size must not exceed 500 MB")
    private Long fileSize;
}
```

## 3. 业务逻辑验证

在 Service 层实现复杂的业务规则验证。

### 3.1 TravelService 中的验证

```java
public TravelRecordResponse createTravelRecord(Long userId, CreateTravelRecordRequest request) {
    // 验证日期范围（虽然 DTO 已验证，但在 Service 层再次验证以确保安全）
    if (request.getStartDate().isAfter(request.getEndDate())) {
        throw new IllegalArgumentException("Start date must be before or equal to end date");
    }

    // 创建记录
    TravelRecord travelRecord = TravelRecord.builder()
            .userId(userId)
            .title(request.getTitle())
            .destination(request.getDestination())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .description(request.getDescription())
            .diaryContent(request.getDiaryContent())
            .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
            .viewCount(0)
            .build();

    return TravelRecordResponse.from(travelRecordRepository.save(travelRecord));
}
```

### 3.2 CommentService 中的验证

```java
public CommentResponse createComment(Long userId, CreateCommentRequest request) {
    // 验证旅游记录存在
    TravelRecord travelRecord = travelRecordRepository.findById(request.getTravelRecordId())
            .orElseThrow(() -> new IllegalArgumentException("Travel record not found"));

    // 验证权限：只能评论公开记录或自己的记录
    if (!travelRecord.getIsPublic() && !travelRecord.getUserId().equals(userId)) {
        throw new IllegalArgumentException("Cannot comment on private records");
    }

    // 验证评论内容不为空
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
        throw new IllegalArgumentException("Comment content cannot be empty");
    }

    // 创建评论
    Comment comment = Comment.builder()
            .travelRecordId(request.getTravelRecordId())
            .userId(userId)
            .content(request.getContent().trim())
            .build();

    return CommentResponse.from(commentRepository.save(comment));
}
```

### 3.3 FootprintService 中的验证

```java
public MapFootprintResponse addFootprint(Long userId, CreateMapFootprintRequest request) {
    // 验证旅游记录存在且属于用户
    TravelRecord travelRecord = travelRecordRepository.findByIdAndUserId(
            request.getTravelRecordId(), userId)
            .orElseThrow(() -> new IllegalArgumentException("Travel record not found or access denied"));

    // 验证坐标范围（虽然 DTO 已验证，但在 Service 层再次验证）
    if (request.getLatitude().doubleValue() < -90 || request.getLatitude().doubleValue() > 90) {
        throw new IllegalArgumentException("Latitude must be between -90 and 90");
    }
    if (request.getLongitude().doubleValue() < -180 || request.getLongitude().doubleValue() > 180) {
        throw new IllegalArgumentException("Longitude must be between -180 and 180");
    }

    // 创建足迹
    MapFootprint footprint = MapFootprint.builder()
            .travelRecordId(request.getTravelRecordId())
            .locationName(request.getLocationName())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .visitDate(request.getVisitDate())
            .build();

    return MapFootprintResponse.from(mapFootprintRepository.save(footprint));
}
```

### 3.4 ItineraryService 中的验证

```java
public ItineraryItemResponse createItineraryItem(Long planId, Long userId, 
        CreateItineraryItemRequest request) {
    // 验证计划存在且属于用户
    TravelPlan plan = travelPlanRepository.findByIdAndUserId(planId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Travel plan not found or access denied"));

    // 验证行程项日期在计划日期范围内
    if (request.getItemDate().isBefore(plan.getStartDate()) || 
        request.getItemDate().isAfter(plan.getEndDate())) {
        throw new IllegalArgumentException("Item date must be within the plan's date range");
    }

    // 创建行程项
    ItineraryItem item = ItineraryItem.builder()
            .planId(planId)
            .itemDate(request.getItemDate())
            .itemType(request.getItemType())
            .title(request.getTitle())
            .description(request.getDescription())
            .location(request.getLocation())
            .build();

    return ItineraryItemResponse.from(itineraryItemRepository.save(item));
}
```

## 4. 错误处理

### 4.1 全局异常处理

系统使用 `GlobalExceptionHandler` 统一处理验证错误：

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex) {
    List<ApiResponse.FieldError> errors = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.add(ApiResponse.FieldError.builder()
                .field(fieldName)
                .message(errorMessage)
                .build());
    });
    return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, "Validation failed", errors));
}
```

### 4.2 错误响应格式

验证失败时返回的响应格式：

```json
{
  "code": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    },
    {
      "field": "password",
      "message": "Password must be between 6 and 100 characters"
    }
  ],
  "timestamp": "2024-01-01T12:00:00"
}
```

## 5. 验证最佳实践

### 5.1 分层验证

- **DTO 层**：验证基本的数据类型和格式
- **Service 层**：验证业务规则和数据关系
- **Controller 层**：使用 `@Valid` 触发 DTO 验证

### 5.2 验证消息

- 使用清晰、用户友好的错误消息
- 包含具体的约束条件（如最小/最大值）
- 使用中文或英文保持一致

### 5.3 自定义验证器

- 为复杂的验证规则创建自定义注解
- 在验证器中提供清晰的错误消息
- 处理 null 值（通常返回 true）

### 5.4 性能考虑

- 避免在验证器中执行数据库查询
- 在 Service 层进行数据库相关的验证
- 使用缓存减少重复验证

## 6. 测试验证

### 6.1 单元测试示例

```java
@Test
public void testRegisterRequestValidation() {
    RegisterRequest request = RegisterRequest.builder()
            .username("ab")  // 太短
            .email("invalid-email")  // 无效格式
            .password("123")  // 太短且无大写字母
            .build();

    Set<ConstraintViolation<RegisterRequest>> violations = 
            validator.validate(request);
    
    assertThat(violations).isNotEmpty();
    assertThat(violations).hasSize(3);
}
```

### 6.2 集成测试示例

```java
@Test
public void testRegisterWithInvalidEmail() {
    RegisterRequest request = RegisterRequest.builder()
            .username("testuser")
            .email("invalid-email")
            .password("Password123")
            .build();

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.errors[0].field").value("email"));
}
```

## 7. 常见问题

### Q: 为什么在 Service 层还要验证，DTO 已经验证过了？
A: 为了防御性编程和确保数据安全。DTO 验证可能被绕过，Service 层验证是最后一道防线。

### Q: 如何创建跨字段验证？
A: 使用类级别的自定义注解（如 `@ValidDateRange`），在验证器中访问多个字段。

### Q: 验证失败时如何自定义错误响应？
A: 在 `GlobalExceptionHandler` 中处理 `MethodArgumentNotValidException`，格式化错误信息。

### Q: 如何验证集合中的元素？
A: 使用 `@Valid` 注解在集合字段上，并在集合元素类上添加验证注解。

## 8. 相关文件

- `com.travelMemory.validation.ValidDateRange` - 日期范围验证注解
- `com.travelMemory.validation.DateRangeValidator` - 日期范围验证器
- `com.travelMemory.validation.ValidFileSize` - 文件大小验证注解
- `com.travelMemory.validation.FileSizeValidator` - 文件大小验证器
- `com.travelMemory.common.GlobalExceptionHandler` - 全局异常处理
- `com.travelMemory.dto.*` - 所有 DTO 类
