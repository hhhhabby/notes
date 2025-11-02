# IOC 与 AOP 是 “基础支撑” 与 “功能实现” 的关系：

- **IOC 是前提**：日志切面类（如`LogAspect`）和被日志增强的目标对象（如`UserService`），都需要被 IOC 容器管理（加`@Component`等注解）——IOC 负责创建这些对象并纳入容器，为 AOP 提供 “可操作的目标”。
- **AOP 是手段**：基于 IOC 管理的对象，AOP 通过动态代理，在目标方法（如`UserService.save()`）执行前后，自动织入日志逻辑（如打印入参、耗时），实现 “不修改业务代码却能增强日志功能”。

简单说：IOC “管对象的出生”，AOP “用这些对象做增强”，两者配合让日志功能既能解耦又能自动生效。

# 什么是 aop？
AOP即面向切面编程，AOP 是 OOP（面向对象编程）的一种延续，二者互补，并不对立。

AOP 的目的是将OOP 不能很好地处理一些分散在多个类或对象中的公共行为（如日志记录、事务管理、权限控制、接口限流、接口幂等等），这些行为通常被称为 **横切关注点（cross-cutting concerns）** 。如果我们在每个类或对象中都重复实现这些行为，那么会导致代码的冗余、复杂和难以维护。

OOP 的目的是将业务逻辑按照对象的属性和行为进行封装，通过类、对象、继承、多态等概念，实现代码的模块化和层次化（也能实现代码的复用），提高代码的可读性和可维护性。

# Aop 可以干什么？
- 日志记录：自定义日志记录注解，利用 AOP，一行代码即可实现日志记录。
- 性能统计：利用 AOP 在目标方法的执行前后统计方法的执行时间，方便优化和分析。
- 事务管理：`@Transactional` 注解可以让 Spring 为我们进行事务管理比如回滚异常操作，免去了重复的事务管理逻辑。`@Transactional`注解就是基于 AOP 实现的。

# Aop 的实现

AOP 的常见实现方式有动态代理、字节码操作等方式。

Spring AOP 就是基于动态代理的。
如果要代理的对象，实现了某个接口，那么 Spring AOP 会使用 **JDK Proxy**，去创建代理对象，而对于没有实现接口的对象，就无法使用 JDK Proxy 去进行代理了，这时候 Spring AOP 会使用 CGLIB 生成一个被代理对象的子类来作为代理，如下图所示：



# 实现 AOP 的具体代码
1. 切面类必须加@Aspect和@Component（缺一不可，@Component确保 Spring 能扫描到切面）；​

2. 切入点表达式必须正确（如包路径、类名不能写错，否则拦截不到方法）；​

3. 目标类（Controller/Service）必须被 Spring 管理（加@RestController/@Service），否则 AOP 无法生成代理对象；​

4. 若目标方法是private/final：CGLIB 代理会失效（无法继承），需改用 JDK 动态代理（目标类实现接口）或移除private/final修饰。




### 一、简答题（共 4 题）

1.  请简述 AOP（面向切面编程）的核心定义，以及它解决了传统 OOP（面向对象编程）中的什么问题？
2.  “连接点（待增强对象方法）
	”“切入点（指向简化的指针）” 
	”“通知（提取的简化方法）

什么时候用方法？
	/切面（通知+切入点）
	切面类（通知+切点表达式+类名）
3.  列举 Spring AOP 中支持的 5 种通知类型，并简要说明每种通知的执行时机。
4.  简述 AOP 动态代理的两种实现方式（JDK 动态代理、CGLIB 动态代理）的核心区别，以及 Spring AOP 默认使用哪种代理方式？
### 二、选择题（共 3 题，每题只有 1 个正确答案）
1.  下列关于 Spring AOP 与 AspectJ 的说法，错误的是（  ）
    A. Spring AOP 基于动态代理实现，AspectJ 基于编译期 / 类加载期织入实现
    B. Spring AOP 支持 AspectJ 的注解（如 @Aspect、@Before），但不依赖 AspectJ 核心包
    C. AspectJ 的功能比 Spring AOP 更完整，支持更多切入点表达式和通知类型

# 切点表达式

Execution (返回值+具体位置+参数 )


@Retention
@Annotation（）


方法加注解 @mylog


# 连接点
ProceedingJoinPoint(对象)------》@Aroud 通知
JoinPoint（对象）----->其他通知


对象干嘛的？
获得 id
获得方法签名
获得原方法名字
获得原方法参数
增强方法调用原方法


# Spring AOP 切面类完整实现

以下是基于 Spring 框架的 AOP 切面类完整实现方案，包含**依赖配置（继承父工程版本）**、**切面类核心逻辑**、**作用对象指定规则**及**时间统计功能**，并附带详细注释说明。

## 一、依赖配置（Maven）

由于依赖父工程的版本管理，`pom.xml` 中无需指定 `version`，直接引入 Spring AOP 核心依赖即可（父工程需提前声明 Spring 相关依赖的版本）。

Xml

```xml
<!-- pom.xml -->
<dependencies>
    <!-- 1. Spring 核心依赖（IOC 容器，切面类需 @Component 注解） -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <!-- 不指定 version：继承父工程中声明的 spring-context 版本 -->
    </dependency>

    <!-- 2. Spring AOP 核心依赖（提供 @Aspect、@Around 等注解） -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-aop</artifactId>
        <!-- 不指定 version：继承父工程中声明的 spring-aop 版本 -->
    </dependency>

    <!-- 3. AOP 切面表达式解析依赖（可选，若用 AspectJ 语法需引入） -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <!-- 不指定 version：继承父工程中声明的 aspectjweaver 版本 -->
    </dependency>
</dependencies>
```

### 依赖说明 #flashcard
- `spring-context`：提供 Spring 容器核心功能，AOP 的配置（如切面扫描）依赖此模块。
- `spring-aop`：Spring AOP 的核心实现，包含代理、通知（Advice）等核心类。
- `aspectjweaver`：AspectJ 的织入器，Spring AOP 支持 AspectJ 注解风格（如 `@Aspect`、`@Before`、`@Around`），必须引入此依赖才能解析这些注解。
<!--ID: 1762057476654-->



## 二、切面类完整实现（时间统计示例） #flashcard




```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<!--ID: 1762057332488-->


/**
 * 方法执行时间统计切面
 * 1. @Aspect：标识当前类为「切面类」，包含通知（Advice）和切点（Pointcut）
 * 2. @Component：将切面类注入 Spring IOC 容器，否则 AOP 无法扫描到
 */
@Aspect
@Component
public class MethodTimeAspect {

    // 日志对象（推荐 SLF4J，而非 System.out）
    private static final Logger log = LoggerFactory.getLogger(MethodTimeAspect.class);

    /**
     * 环绕通知（@Around）：在目标方法执行前后都执行逻辑
     * value：切点表达式，指定「作用对象」（哪些方法会被切面拦截）
     * proceedingJoinPoint：形参，代表「目标方法」，必须作为环绕通知的唯一参数
     */
    @Around(value = "execution(* com.example.demo.service..*(..))") // 切点表达式：拦截 service 包下所有方法
    
    
    
    
    
    public Object calculateMethodTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        long startTime = System.currentTimeMillis(); // 毫秒级时间戳

        // 2. 执行原始目标方法（必须调用，否则目标方法不执行） 
        Object result = proceedingJoinPoint.proceed();
        
         // 调用目标方法，获取返回值（若目标方法无返回值则为 null）

        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime; 
        
        // 打印日志：包含目标方法名、耗时
        String targetMethodName =                                           		    proceedingJoinPoint.getSignature().getDeclaringTypeName() 

        + "." + proceedingJoinPoint.getSignature().getName();
                   
        log.info("目标方法 [{}] 执行完成，耗时：{} 毫秒", targetMethodName, costTime);

        // 返回目标方法的结果（若上层调用需要结果，必须返回，否则会丢失返回值）
        return result;
    }
}
```



## 三、关键细节说明

### 1. 切面类注解作用

|注解|作用|
|---|---|
| `@Aspect` |标识当前类是「切面类」，Spring 会识别此类为 AOP 切面，而非普通 Bean。|
| `@Component` |将切面类注入 Spring 容器，AOP 机制依赖容器扫描才能生效（必加）。|

### 2. @Around 作用对象指定（切点表达式）

`@Around(value = "xxx")` 中的 `xxx` 是**切点表达式**，用于精确控制「哪些方法会被切面拦截」，常见场景如下：

| 表达式示例                                                                  | 作用对象说明（拦截范围）                            | 适用场景                 |
| ---------------------------------------------------------------------- | --------------------------------------- | -------------------- |
| `execution(* com.example.service..*(..))`                              | 拦截 `com.example.service` 包及子包下的**所有方法** | 全局监控某个业务层（如 service） |
| `execution(* com.example.service.UserService.*(..))`                   | 拦截 `UserService` 类的**所有方法**             | 监控单个类                |
| `execution(* com.example.service.UserService.get*(..))`                | 拦截 `UserService` 类中**以 get 开头的方法**      | 监控特定前缀方法（如查询方法）      |
| `@annotation(com.example.annotation.Log)`                              | 拦截**加了 @Log 自定义注解**的所有方法                | 按注解灵活控制（推荐）          |
| `execution(* com.example.service.UserService.addUser(String,Integer))` | 拦截 `addUser` 方法（参数为 String + Integer）   | 精确匹配方法签名             |

#### 表达式语法说明

- `*`：通配符，第一个 `*` 代表「任意返回值类型」，后续 `*` 代表「任意类 / 任意方法」。
- `..`：代表「当前包及所有子包」（用于包路径）或「任意参数个数 / 类型」（用于方法参数）。
- `(..)`：代表方法的「参数列表为任意个数、任意类型」。

### 3. 环绕通知的核心形参：ProceedingJoinPoint

- **作用**：代表「被拦截的目标方法」，是环绕通知的**唯一必需参数**。
- **核心方法**：
    - `proceed()`：执行原始目标方法（必须调用！若不调用，目标方法会被拦截但不执行）。
    - `getSignature()`：获取目标方法的签名（如类名、方法名）。
    - `getArgs()`：获取目标方法的入参列表（可用于日志打印、参数校验）。

### 4. 时间统计逻辑

- 用 `System.currentTimeMillis()` 获取「方法执行前」和「执行后」的时间戳，差值即为方法耗时。
- 若需更高精度（如微秒），可改用 `System.nanoTime()`（1 微秒 = 1000 纳秒），适合耗时极短的方法。















%% ## 四、扩展场景

### 1. 多个切点复用

若多个通知（如 @Around、@Before）需拦截相同范围，可通过 `@Pointcut` 定义通用切点，避免重复写表达式：

Java

运行

```java
@Aspect
@Component
public class MethodTimeAspect {
    // 1. 定义通用切点（复用表达式）
    @Pointcut(value = "execution(* com.example.demo.service..*(..))")
    public void serviceMethodPointcut() {} // 方法体为空，仅作为切点标识

    // 2. 环绕通知引用通用切点
    @Around(value = "serviceMethodPointcut()")
    public Object calculateMethodTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 逻辑同上...
    }

    // 3. 其他通知也可引用（如前置通知）
    @Before(value = "serviceMethodPointcut()")
    public void beforeMethod() {
        log.info("目标方法执行前...");
    }
}
```

### 2. 按自定义注解拦截

若需灵活控制哪些方法被拦截，可自定义注解（如 `@NeedLog`），仅拦截加了该注解的方法：

1. 定义自定义注解：

Java

运行

```java
import java.lang.annotation.*;

@Target(ElementType.METHOD) // 仅作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时生效（AOP 需运行时扫描）
public @interface NeedLog {
    // 可加属性，如描述信息
    String desc() default "";
}
```

2. 切面引用注解切点：

Java

运行

```java
@Around(value = "@annotation(com.example.demo.annotation.NeedLog)")
public Object calculateMethodTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    // 逻辑同上...
    // 可获取注解属性：
    NeedLog needLog = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod().getAnnotation(NeedLog.class);
    log.info("方法描述：{}", needLog.desc());
    // ...
}
```

3. 目标方法加注解即可被拦截：

Java

运行

```java
@Service
public class UserService {
    // 加 @NeedLog 注解，会被切面拦截
    @NeedLog(desc = "查询用户列表")
    public List<User> listUsers() {
        // ...
    }
}
```

通过以上配置，即可实现一个功能完整、可灵活扩展的 Spring AOP 切面，满足方法耗时统计、日志打印、参数校验等常见需求。 %%