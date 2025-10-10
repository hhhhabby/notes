1. ThreadLocal 基础概念
ThreadLocal 是 Java 提供的一种线程本地存储机制，它为每个线程都创建了一个独立的变量副本，使得每个线程都可以独立地改变自己的副本，而不会影响其他线程的副本。
让我用文字详细描述这个流程：

## 用户请求处理流程（包含 BaseContext 使用）

### 第一步：用户发起请求
- 员工通过浏览器或 APP 发送 HTTP 请求到后端
- 请求头中包含 JWT 令牌（token），里面包含了员工信息

### 第二步：拦截器处理 JWT 令牌
1. 系统首先调用 [JwtTokenAdminInterceptor](file://C:\Users\26487\Desktop\苍穹外卖\资料\资料\day01\后端初始工程\sky-take-out\sky-server\src\main\java\com\sky\interceptor\JwtTokenAdminInterceptor.java#L18-L60) 拦截器
2. 从请求头中获取 JWT 令牌
3. 解析令牌，提取员工 ID（比如员工 ID 是 1001）
4. 调用 [BaseContext.setCurrentId(1001)](file://C:\Users\26487\Desktop\苍穹外卖\资料\资料\day01\后端初始工程\sky-take-out\sky-common\src\main\java\com\sky\context\BaseContext.java#L7-L9) 方法

### 第三步：BaseContext 类存储员工 ID
```java
// BaseContext类内部有一个ThreadLocal变量
public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

// setCurrentId方法把员工ID保存到当前线程的"小盒子"里
public static void setCurrentId(Long id) {
    threadLocal.set(id); // 当前线程的"小盒子"里放进了1001
}
```


### 第四步：业务处理中使用员工 ID
当执行到 [EmployeeServiceImpl.save()](file://C:\Users\26487\Desktop\苍穹外卖\资料\资料\day01\后端初始工程\sky-take-out\sky-server\src\main\java\com\sky\service\impl\EmployeeServiceImpl.java#L59-L85) 方法时：
```java
// 需要记录是谁创建了这个员工
employee.setCreateUser(BaseContext.getCurrentId()); // 获取到1001
employee.setUpdateUser(BaseContext.getCurrentId());  // 获取到1001
```


[BaseContext.getCurrentId()](file://C:\Users\26487\Desktop\苍穹外卖\资料\资料\day01\后端初始工程\sky-take-out\sky-common\src\main\java\com\sky\context\BaseContext.java#L12-L14) 方法会从当前线程的"小盒子"里取出之前放进去的员工 ID（1001）

### 第五步：请求结束
- 清理工作：调用 [BaseContext.removeCurrentId()](file://C:\Users\26487\Desktop\苍穹外卖\资料\资料\day01\后端初始工程\sky-take-out\sky-common\src\main\java\com\sky\context\BaseContext.java#L17-L19) 清除 ThreadLocal 中的数据
- 避免内存泄漏

## 关键点解释

**ThreadLocal 就像每个线程的私有"小盒子"：**
- 线程 A 有自己独立的"小盒子"，放的是员工 1001 的信息
- 线程 B 有自己独立的"小盒子"，放的是员工 1002 的信息
- 两个线程互不干扰，保证了数据安全

这样在整个请求处理过程中，任何地方都能方便地获取到当前操作员工的 ID，而不需要在每个方法参数中传递。