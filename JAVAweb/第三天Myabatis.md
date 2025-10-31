# 如何实现物理分页/分页查询
MySQL 分页查询
在 MySQL 中，分页查询通常使用 _LIMIT_ 关键字来实现。

_LIMIT_ 后面可以跟两个参数，第一个参数是起始索引，第二个参数是查询记录数。


起始索引从 0 开始计算，可以根据需要查询的页码和每页显示的记录数来计算。




SELECT * FROM employee LIMIT 0, 10;

如果要查询第二页的数据，可以这样写：

SELECT * FROM employee LIMIT 10, 10;


后端：
Controller 层
**接受**前端发送的参数 page, pagesize 等等并设置默认值
调用 service 进行分页查询，
调用方法时候就**传递参数**给 service 层

Service
**接收数据**：调用 mapper 接口查询总记录数 total，
**调用方法**时候 mapper 接口获取，数据列表 rows
Total 和 rows 以及其他数据，***封装 pageBean 对象***
传递给 Mapper

EmpMapper
Sql 语句：
查询总数据
分页查询

Json 格式传递回去


# Sql 标签
If
Where 去除 and
Foreach


# 三级缓存机制
会话
全局
