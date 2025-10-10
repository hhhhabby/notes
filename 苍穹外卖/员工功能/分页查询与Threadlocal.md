#特色 
DTO+插件+thread

Pagehelper 的底层
底层原理都是共享当前线程的局部变量。
Service 实现类通过 threadlocal 存进去的 page 和 pagesize 两个局部变量
Mapper 层在取出来


# Mapper 层 
#flashcard 
Mapper 层注解（预编译 sql）：
查询
Insert  into emp （a, b, c）values ( #{a} ,#{b} ,#{c} ,)
看文档
# Service 层：
日志
逻辑处理使用 pagehelper (DTO)
调用
# Controller 层
Post/Api/接受请求 (requestbody)
日志
调用 service

# Threadlocal
1. 在令牌  controller service 生成对应的线程 id
## [Thread 类的常用属性和方法]

**Thread** 类是 Java 中用于创建和管理线程的核心类。它提供了多种属性和方法来控制线程的行为和状态。
常用属性
- **线程 ID**：通过 _getId ()_ 方法获取线程的唯一标识符。
- **线程名称**：通过 _getName ()_ 获取线程名称，或通过 _setName (String name)_ 设置线程名称。
- **线程状态**：通过 _getState ()_ 获取线程的当前状态，例如 NEW、RUNNABLE、BLOCKED 等。
- **线程优先级**：通过 _getPriority ()_ 获取线程优先级，或通过 _setPriority (int priority)_ 设置优先级。
- **是否为后台线程**：通过 _isDaemon ()_ 判断线程是否为后台线程，或通过 _setDaemon (boolean on)_ 设置为后台线程。
- **线程存活状态**：通过 _isAlive ()_ 判断线程是否仍在运行。

