# 接口和类和Main 的区别

```java
// 包含main函数的类（程序的启动入口）
public class TestCalculator {
    public static void main(String[] args) {
        // main函数中调用Calculator类的逻辑
        Calculator calc = new Calculator();
        int result = calc.add(3, 5);
        System.out.println(result); // 输出8
    }
}
```
![[Pasted image 20250926212523.png]]
# 区分    类和方法和成员方法

```java
// 类：Student（学生的模板）
class Student {
    // 属性（成员变量）：学生的特征
    String name; // 姓名
    int age;     // 年龄

    // 方法1：打印学生信息（学生的行为）
    public void printInfo() {
        System.out.println("姓名：" + name + "，年龄：" + age);
    }

    // 方法2：修改年龄（学生的行为）
    public void setAge(int newAge) {
        age = newAge; // 逻辑：将新年龄赋值给属性age
    }
}
```
#### 成员方法：类中定义的 “普通方法”
- **成员方法**（无`static`）：属于对象，通过 “对象。方法 ()” 调用，例如：
    ```java
    Student s = new Student(); // 创建对象
    s.setName("张三"); // 调用成员方法（必须通过对象s）
    ```
- **类**：`Student` 是一个类，它像一个 “模板”，描述了 “学生” 有哪些特征（`name`、`age`）和行为（`printInfo`、`setAge`）



# **如何通过 “查看定义” 准确区分 Java 中的接口和类？

- **如果定义中是`interface`→ 这是接口**  
    例如，点击`EmployeeMapper`跳转到定义：
    ```java
    // 开头是 interface 关键字 → 确定是接口
    public interface EmployeeMapper {
        // 只有方法声明（无 {} 实现）
        Employee getById(Long id);
        void insert(Employee employee);
    }
    ```

- **如果定义中是`class`→ 这是类**  
    例如，点击`Employee`跳转到定义：
    ```java
    // 开头是 class 关键字 → 确定是类
    public class Employee {
        // 有属性、构造方法、带 {} 的方法实现
        private Long id;
        private String name;
        
        public Long getId() {
            return id; // 方法体实现
        }
    }
    ```

