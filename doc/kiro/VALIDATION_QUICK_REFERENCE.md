# 数据验证快速参考

## 快速开始

### 1. 在 DTO 中添加验证注解

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    private String name;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be non-negative")
    @Max(value = 150, message = "Age must be less than 150")
    private Integer age;
    
    @Email(message = "Invalid email format")
    private String email;
}
```

### 2. 在 Controller 中使用 @Valid

```java
@PostMapping
public ResponseEntity<ApiResponse<MyResponse>> create(
        @Valid @RequestBody MyRequest request) {
    // 如果验证失败，Spring 会自动返回 400 Bad Request
    MyResponse response = myService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(response));
}
```

### 3. 在 Service 中添加业务逻辑验证

```java
public MyResponse create(MyRequest request) {
    // 验证业务规则
    if (request.getAge() < 18) {
        throw new IllegalArgumentException("Age must be at least 18");
    }
    
    // 验证数据库约束
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException("Email already exists");
    }
    
    // 创建对象
    MyEntity entity = MyEntity.builder()
            .name(request.getName())
            .age(request.getAge())
            .email(request.getEmail())
            .build();
    
    return MyResponse.from(repository.save(entity));
}
```

## 常用验证注解

### 字符串验证

```java
@NotBlank(message = "Field is required")  // 不能为空或仅空格
@NotEmpty(message = "Field is required")  // 不能为空
@Size(min = 1, max = 100)                 // 长度范围
@Email                                     // 邮箱格式
@Pattern(regexp = "^[a-z]+$")             // 正则表达式
```

### 数字验证

```java
@NotNull                                   // 不能为 null
@Min(value = 0)                           // 最小值
@Max(value = 100)                         // 最大值
@DecimalMin(value = "0.0")                // 十进制最小值
@DecimalMax(value = "100.0")              // 十进制最大值
```

### 集合验证

```java
@NotEmpty                                  // 集合不能为空
@Size(min = 1, max = 10)                  // 集合大小范围
```

## 自定义验证器

### 创建注解

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MyValidator.class)
public @interface ValidMyRule {
    String message() default "Validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### 创建验证器

```java
public class MyValidator implements ConstraintValidator<ValidMyRule, Object> {
    
    @Override
    public void initialize(ValidMyRule annotation) {
        // 初始化
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        // 验证逻辑
        boolean isValid = /* 验证条件 */;
        
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Error message")
                    .addConstraintViolation();
        }
        
        return isValid;
    }
}
```

### 使用自定义验证器

```java
@ValidMyRule
public class MyRequest {
    // 字段
}
```

## 错误响应格式

### 验证失败响应

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
      "field": "age",
      "message": "Age must be between 0 and 150"
    }
  ],
  "timestamp": "2024-01-01T12:00:00"
}
```

### 业务逻辑验证失败响应

```json
{
  "code": 400,
  "message": "Email already exists",
  "timestamp": "2024-01-01T12:00:00"
}
```

## 系统中的验证示例

### 日期范围验证

```java
@ValidDateRange(startDateField = "startDate", endDateField = "endDate")
public class CreateTravelRecordRequest {
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
}
```

### 文件大小验证

```java
public class FileUploadRequest {
    @NotNull
    @ValidFileSize(maxSizeInMB = 500)
    private Long fileSize;
}
```

### 坐标范围验证

```java
public class CreateMapFootprintRequest {
    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;
    
    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;
}
```

## 测试验证

### 单元测试

```java
@Test
public void testValidation() {
    MyRequest request = MyRequest.builder()
            .name("")  // 无效
            .age(-1)   // 无效
            .email("invalid")  // 无效
            .build();
    
    Set<ConstraintViolation<MyRequest>> violations = 
            validator.validate(request);
    
    assertThat(violations).isNotEmpty();
    assertThat(violations).hasSize(3);
}
```

### 集成测试

```java
@Test
public void testApiValidation() {
    MyRequest request = MyRequest.builder()
            .name("")
            .age(-1)
            .email("invalid")
            .build();
    
    mockMvc.perform(post("/api/my")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.errors").isArray());
}
```

## 常见问题

### Q: 如何验证集合中的元素？

A: 在集合字段上使用 `@Valid`，在元素类上添加验证注解：

```java
public class MyRequest {
    @Valid
    @NotEmpty
    private List<MyItem> items;
}

public class MyItem {
    @NotBlank
    private String name;
}
```

### Q: 如何跳过某个字段的验证？

A: 使用验证组（Groups）：

```java
public interface CreateGroup {}
public interface UpdateGroup {}

public class MyRequest {
    @NotBlank(groups = CreateGroup.class)
    private String id;
    
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String name;
}

// 在 Controller 中
@Validated(CreateGroup.class)
@PostMapping
public void create(@Valid MyRequest request) {}
```

### Q: 如何自定义错误消息？

A: 在注解中指定 message 参数：

```java
@NotBlank(message = "用户名不能为空")
private String username;

@Size(min = 6, max = 20, message = "密码长度必须在 6-20 之间")
private String password;
```

### Q: 验证失败时如何自定义响应？

A: 在 GlobalExceptionHandler 中处理 MethodArgumentNotValidException：

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<?>> handleValidationException(
        MethodArgumentNotValidException ex) {
    // 自定义错误处理
}
```

## 最佳实践

1. **分层验证**
   - DTO 层：基本数据类型和格式验证
   - Service 层：业务规则验证
   - Controller 层：使用 @Valid 触发验证

2. **清晰的错误消息**
   - 使用用户友好的语言
   - 包含具体的约束条件
   - 提供修复建议

3. **性能考虑**
   - 避免在验证器中执行数据库查询
   - 在 Service 层进行数据库相关验证
   - 使用缓存减少重复验证

4. **安全性**
   - 验证所有用户输入
   - 防止 SQL 注入和 XSS 攻击
   - 检查用户权限

5. **可维护性**
   - 为复杂验证创建自定义注解
   - 编写清晰的验证逻辑
   - 添加注释说明验证规则

## 相关文件

- `com.travelMemory.validation.*` - 验证器和注解
- `com.travelMemory.dto.*` - 所有 DTO 类
- `com.travelMemory.common.GlobalExceptionHandler` - 异常处理
- `com.travelMemory.service.*` - Service 层验证
- `SpringBoot/src/main/java/com/travelMemory/validation/VALIDATION_GUIDE.md` - 详细指南
