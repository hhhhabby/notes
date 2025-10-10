# ①Grab_callback 类的修改：
减少连接时重复打印的情况
![[Pasted image 20251008205337.png]]



# ②MvsWebsocket类的修改
主要是强化 onerror 和 onclose 方法
在图像回调方法中:
新增：检查 WebSocket 连接状态

先①在 Grab_Callback 类中添加成员变量
Private boolean connectionClosedLogged = false;

再②增加连接状态检查：

新增清理旧的回调和会话。
检查连接情况 ![[Pasted image 20251008210030.png]]
由于摄像头一直再传数据给后端。如果在断开之前有数据过来，也可能会包一点点错。
小概率事件。但是控制台等会就会识别到与 websocket连接已经断开了，如下
![[Pasted image 20251008211125.png]]

