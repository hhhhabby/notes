# 缓存机制 
Integer a = 100;// 自动装箱，调用valueOf(100)，使用缓存对象 
Integer b = Integer.valueOf(100); // 直接使用缓存对象
Integer c = new Integer(100);// 新建对象，不使用缓存
System.out.println(a == b); // true（同一缓存对象） 
System.out.println(a == c); // false（a是缓存对象，c是新对象）

#### 扩展：其他包装类的缓存机制

Java 中不仅`Integer`有缓存，其他包装类也有类似优化：
- `Byte`：缓存`-128~127`（范围固定，不可修改）。
- `Short`：缓存`-128~127`（范围固定）。
- `Long`：缓存`-128~127`（范围固定）。
- `Character`：缓存`0~127`（ASCII 码范围内的字符）。