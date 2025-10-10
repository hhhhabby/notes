### 用于存放**模块级的配置类**，通过代码方式自定义框架或业务的配置规则。

- `MvcConfig`：大概率是对 **Spring MVC 的扩展配置**，比如注册拦截器（如`AdminInterceptor`）

1. **拦截 API 请求** - 通过 [AdminInterceptor](file://C:\Users\26487\Desktop\gas-analysis-management-system-master\gas-parent\gas-admin\src\main\java\com\yc\gas\admin\interceptor\AdminInterceptor.java#L31-L137) 对业务接口进行安全控制
2. **直接返回静态资源** - 通过资源映射直接提供 Swagger UI 等静态文件
3.     开发人员可以无需登录就能访问 API 文档


1.OperateLogAspectConfig
- `OperateLogAspectConfig`：从命名（`Aspect`）推测，是**操作日志的 AOP（面向切面编程）配置**—— 通过 AOP “切面”，无需在每个业务方法中重复写代码，就能统一记录用户的操作日志（如 “谁在什么时间执行了什么操作”）。

