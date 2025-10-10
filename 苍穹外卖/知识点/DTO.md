## 为什么有 employeeDTO？
为什么前端传入的参数较少（EmployeeDTO 只有 id, username, name, phone, sex, idNumber），但数据库中却有很多字段（Employee 包含 password, createTime, updateTime, status 等更多字段）？

### 1. 分层设计思想
项目采用了典型的分层架构设计：
- **DTO (Data Transfer Object)** - 用于前后端数据传输，只包含必要字段
- **Entity (实体类)** - 对应数据库表结构，包含所有字段
- **VO (View Object)** - 用于返回给前端的数据展示对象

### 2. 核心方法 - BeanUtils. CopyProperties
这是解决字段不匹配问题的关键方法：

```java
// 创建实体对象
Employee employee = new Employee();

// 一次性拷贝相同属性名的字段
BeanUtils.copyProperties(employeeDTO, employee);
```
### 3. 手动补充其他字段
对于 DTO 中没有的字段，后端会手动设置默认值或系统值：

```java
// 设置账号状态
employee.setStatus(StatusConstant.ENABLE);

// 设置默认密码
employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

// 设置创建和更新时间
employee.setCreateTime(LocalDateTime.now());
employee.setUpdateTime(LocalDateTime.now());

// 设置创建人和修改人
employee.setCreateUser(BaseContext.getCurrentId());
employee.setUpdateUser(BaseContext.getCurrentId());
```
### 4. 完整流程
1. 前端发送 EmployeeDTO（只包含必要字段）
2. 后端接收 DTO 并创建 Employee 实体
3. 使用 `BeanUtils.copyProperties` 拷贝同名字段
4. 手动补充其他系统字段
5. 调用 Mapper 插入完整数据到数据库

### 5. 安全与设计优势
- **安全性**：避免前端直接操作敏感字段（如 password、status）

# 2、DTO/VO 模型注解（描述数据实体）
# @ApiModel、@ApiModelProperty

@ApiModel 用于描述一个 Model 的信息（这种一般用在 post 创建的时候，使用@RequestBody 这样的场景，请求参数无法使用@ApiImplicitParam 注解进行描述的时候）。

@ApiModelProperty 用来描述一个 Model 的**属性**。
Apimodelpro

用于标记数据传输对象（DTO）或视图对象（VO），说明实体类及字段的含义，方便文档展示参数结构。

| 注解（Swagger 2. X）     | 对应 OpenAPI 3. X 注解 | 作用             | 核心属性 & 用法示例                                                                                                                                                                                                                                                                                                                                                              |
| ------------------- | ----------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `@ApiModel`         | `@Schema`         | 描述 DTO/VO 类的含义 | - `value`：类名称  <br>- `description`：类详细描述  <br>`java @ApiModel(value = "EmployeeLoginDTO", description = "员工登录请求参数") public class EmployeeLoginDTO { ... }`                                                                                                                                                                                                               |
| `@ApiModelProperty` | `@Schema`         | 描述 DTO/VO 类的字段 | - `value`：字段说明  <br>- `required`：是否必填  <br>- `example`：示例值  <br>- `hidden`：是否隐藏（true 则文档不显示该字段）  <br>`java @ApiModel(...) public class EmployeeLoginDTO { @ApiModelProperty(value = "用户名", required = true, example = "admin") private String username; @ApiModelProperty(value = "密码", required = true, example = "123456", hidden = false) private String password; }` |
# 3、方法级别注解（描述具体接口）
# @ApiOperation

@ApiOperation 注解在用于对一个操作或**HTTP 方法进行描述**。具有相同路径的不同操作会被归组为同一个操作对象。不同的 HTTP 请求方法及路径组合构成一个唯一操作。
用于标记控制器中的方法，说明单个接口的功能、请求方式、响应等。

| 注解（Swagger 2. X） | 对应 OpenAPI 3. X 注解 | 作用            | 核心属性 & 用法示例                                                                                                                                                                                                                                                           |
| --------------- | ----------------- | ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `@ApiOperation` | `@Operation`      | 描述接口的功能、请求方式等 | - `value`：接口简短说明  <br>- `notes`：接口详细描述  <br>- `httpMethod`：指定 HTTP 方法（GET/POST 等，可选，通常与 `@GetMapping` 等重复）  <br>`java @ApiOperation( value = "员工登录", notes = "输入用户名和密码，返回登录令牌", httpMethod = "POST" ) @PostMapping("/login") public Result<LoginVO> login(...) { ... }` |
