## 一 . 为什么是 springboot?
#### 1.1 SpringBoot的特点
Springboot 对 spring 进行了缺点的优化（配置重量级，组件代码轻量级，配置导致开发时的损耗。即需要同时去处理业务逻辑问题和 spring 的配置问题）。
但是 springboot 开袋即食，无需配置 xml，无需代码生成。
同时提供：大型项目中的非功能性特性（嵌入式微服务器）

一句话 springboot 提供了一种快速使用 spring 的方式 
#### 1.2 SpringBoot的功能
起步依赖：起步依赖本质上是一个 maven 项目的对象模型（pom），定义了其他库的传递依赖，这些东西加在一起即支持某项功能。
自动配置：springboot 的自动配置是一个程序启动时的过程，考虑了的众多的因素，才决定 spring 配置应该用哪个，不该用哪个。该过程 spring 自动完成
## 二、SpringBoot快速入门
#### 2.1.1 创建Maven工程

使用idea工具创建一个maven工程，该工程为普通的java工程即可
#### 2.1.2 添加SpringBoot的起步依赖
SpringBoot的起步依赖spring-boot-starter-parent
相关依赖：
web的启动依赖
Mybatis 依赖
Mysql 依赖
#### 2.1.3 编写SpringBoot引导类
要通过SpringBoot提供的引导类起步SpringBoot才可以进行访问
即关于配置

父类
启动注解@SpringBootApplication
混合注解：
- `@SpringBootConfiguration`：定义容器  本质是 `@Configuration`，标识这个类是配置类，
- `@ComponentScan`：自动扫描 `@Component` 定义 bean对象
（包括 `@Service`、`@Controller` 等派生注解）的类，将它们注册为 Spring 容器中的 Bean。
- `@EnableAutoConfiguration`：**核心中的核心**，开启自动配置功能（这也是和传统 Spring 最大的区别）。 **Spring Boot 根据类路径（编译后的 class 文件）和注解自动识别并加载需要的配置。**



测试依赖
- [ ] #### 2.1.4 编写Controller
- [ ] @Controller 声明整个类

- [ ] @具体的处理方法：
- [ ] @RequestMapping ＋方法
