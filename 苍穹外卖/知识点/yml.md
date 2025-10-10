- [x] **问：Spring Boot项目中，数据库连接信息一般配置在哪个文件里？**
答：通常配置在 `src/main/resources` 目录下的 `application.yml`（或 `application.yaml`）文件中，这是Spring Boot默认的核心配置文件之一。
- [x] **问：yml配置文件里，数据库连接的核心配置项有哪些？**
答：主要包括以下几项（以MySQL为例）
驱动名字
Url
Id+密码
- [x] **问：为什么开发阶段会把数据库密码直接写在yml文件里？**
答：开发阶段为了便捷，直接在配置文件中写明文密码可以快速配置和调试，避免额外的配置步骤，提高开发效率。
- [x] **问：生产环境中，直接在yml文件里写数据库密码有什么风险？如何处理？** 
答：风险是密码明文暴露，存在安全隐患。处理方式通常有： - 使用配置中心（如Nacos、Apollo）统一管理敏感配置； - 通过环境变量注入密码（yml中用 `${环境变量名}` 引用）； - 借助Spring Boot的加密工具（如jasypt）对密码加密后再写入配置文件。 
- [x] **问：yml文件中配置的数据库连接信息，Spring Boot是如何读取并生效的？** 
答：Spring Boot启动时会自动扫描 `src/main/resources` 下的配置文件，通过内置的配置绑定机制（如 `@ConfigurationProperties` 注解）将yml中的 `spring.datasource` 相关配置映射到数据源对象中，最终用于建立数据库连接。