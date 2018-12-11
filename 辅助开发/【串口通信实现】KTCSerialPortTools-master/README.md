# Android串口通信实现

> 之前在做项目时需要和其他代工厂配合实现工厂生产调试自动化，所以需要用到串口通信，了解到google原本是有开放jni接口（android-serialport-api）支持android应用直接读写串口数据。



关于串口通信的连接和操作步骤很简单，业务逻辑并不复杂：

> 打开串口（配置端口）---->读写串口数据---->退出后关闭串口

下面我们来逐步分解讨论~



## 一、什么是串口通信

串行端口 (SerialPort)简称为串口，主要用于数据被逐位按顺序传送的通讯方式称为串口通讯（NOTE：简单来讲就是按顺序一位一位地传输数据）。 

说到串口通信，串口自然是和硬件端口有关，侦听硬件信息必然是要走JNI层并经过驱动层最终实现与硬件设备的信息互通，JNI可以使我们可以在应用层java代码中调用c语言连接驱动，侦听硬件端口数据流。由于Android系统目前主流的芯片架构都是ARM架构，而ARM架构本身是支持串口通信的，比如标准的RS232通信和我们的debug板等等，但是遗憾的是Android源码的framework层并未直接封装关于串口通信的类库，可是google原本在设计Android架构时已经预留有串口通信接口且提供简单的串口通信demo（android-serialport-api），当然这个demo是很简陋的，许多功能不能满足我们的业务需求，但是重要的是它提供了一个很好的思路，为我们创建了一个应用层-->硬件层之间的传输管道，并且很方便扩展。



## 二、什么是android-serialport-api

> android-serialport-api是有google独立开源的串行通信辅助库，代码比较简单，主要是实现向Java层提供JNI访问底层端口，并侦听收发端口数据流。

- **android-serialport-api在JNI层中有两个主要的类：**

| 参数                                           | 说明         |
| ---------------------------------------------- | ------------ |
| Java_android_1serialport_1api_SerialPort_open  | 打开指定端口 |
| Java_android_1serialport_1api_SerialPort_close | 关闭串口     |

- **android-serialport-api在Java层中有两个主要的类：**

| 参数             | 说明                                 |
| ---------------- | ------------------------------------ |
| SerialPort       | 获取串口的类(其实就是获取输入输出流) |
| SerialPortFinder | 获取硬件地址的类                     |



### 2.1 SerialPort的介绍

#### 2.1.1调用的流程简析

> java open –> jni open –>c open

- SerialPort.java: open(device.getAbsolutePath(), baudrate, flags)
- SerialPort.h: Java_android_1serialport_1api_SerialPort_open(JNIEnv *, jclass, jstring, jint, jint)
- SerialPort.c: (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)



#### 2.1.2 SerialPort的initSerialPort函数

SerialPort.getInstance.initSerialPort(File device, int baudrate, int flags)

| 参数     | 说明             |
| -------- | ---------------- |
| device   | 要操作的文件对象 |
| baudrate | 波特率           |
| flags    | 文件操作的标志   |



#### 2.1.3 C语言中的open函数

Java_android_1serialport_1api_SerialPort_open(JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)；

| 参数     | 说明                                                         |
| -------- | ------------------------------------------------------------ |
| path     | 硬件地址                                                     |
| baudrate | 端口波特率                                                   |
| flags    | 文件的打开打开方式: O_RDONLY (00)以只读方式打开文件;O_WRONLY(01) 以只写方式打开文件;O_RDWR(02) 以可读写方式打开文件 |



### 2.2 android-serialport-api代码分析

#### 2.2.1 SerialPort.java接口类

> 主要用来加载SO文件，通过JNI的方式打开关闭串口 



#### 2.2.2 SerialPort.h接口类



#### 2.2.3 SerialPort.c实现类



#### 2.2.4 SerialPortFinder.java

> 如果要操作一个串口设备，需要知道该端口的地址和波特率。
>
> 其中波特率一般是根据硬件的说明来设定的，所以在选择的波特率的时候，我们可以提供一些值来做选择，KTC主板一般使用的波特率是115200。 
>
> 其次硬件地址，需要从/proc/tty/drivers文件中解析获得，SerialPortFinder就是获取硬件地址的类。 



至此，android_serialport_api串口通信JNI层配置逻辑基本完成，该阶段的主要目的是遍历deviers节点并获取端口硬件地址、配置波特率，同时疏通Java open-->JNI Open-->C Open流程。

下一步将进入应用层适配及通信逻辑实现阶段~



## 三、Android层串口通信适配及实现

**写在前面的话**

> - KTC 调试端口为ttyS0；serial通信端口为ttyS1
> - ttyS1端口波特率为115200
> - 通信原则：PC端和客户端需要约定命令规格，例如：
>
> Command Format:  0xC0(Vender) + ITEM (2bytes)+ DL(Data LEN)(2bytes) + Data + CS
>
> Checksum(CS) :  0xFF – (amount of all Data exclude checksum byte) & 0xFF
>
> Return Format:  ITEM + DL( Data LEN ) + Data + CS



### 3.1 串口通信适配流程

> 监听Intent.ACTION_BOOT_COMPLETED-->启动SerialConsoleService-->初始化SerialPort所有配置
>
> -->定义SerialHelper.onDataReceived侦听端口字节流并解析回传数据-->侦听结束，关闭串口



### 3.2 各操作类源码分析

#### 3.2.1 MyApplication应用管理类

- **getSerialPort()**

  > 初始化SerialPort单例对象

- **getSerialPortFinder()**

  > 初始化SerialPortFinder单例对象

- **closeSerialPort()**

  > 关闭串口通信

#### 3.2.2 SerialConsoleService串口侦听服务

- **创建内部串口帮助类SerialControl**

  > onDataReceived	侦听及回传数据

- **parseCmds(byte[] buffer, int size)**

  > 解析校验串口字节流并处理对应业务，回传任务状态等

#### 3.2.3 SerialHelper串口操作帮助类

- **initSerialPortConfig(File device, int baudrate, int flags)**

  > 初始化SerialPort端口配置

- **sendStrCmd(String cmdHex)**

  > 以String形式发送指令到PC端

- **sendBufferCmd(byte[] mBuffer)**

  > 以byte[]形式发送指令到PC端

- **getCmdCS(String cmdHex)**

  > 获取命令的校验位CheckSum

- **onDataReceived(final byte[] buffer, final int size)**

  > 串口数据侦听接收

- **线程ReadThread**

  > 开启子线程实时监听串口数据,并通过onDataReceived更新到SerialConsoleService进行数据流解析

- **closeSerialPort()**

  > 关闭串口通信

### 3.3 进程保活及数据可靠性传输

> - 由于需要实现开机自启动，而且需要命令持续侦听，所以做好进程保活是必须的
> - 数据可靠性：串口通信采用顺序字节流进行传输，不适合传输大量的数据（>1KB）,大数据传输会出现概率性数据丢包问题，因此需要做好数据校验及状态回传，数据异常时支持数据重传等



## 四、注意事项等

- 存放SerialPort对象的包名必须和JNI中SerialPort.c调用它的类所在的包名一致

  > 注意：因为对JNI有一定了解的人都知道，在写c语言链接库时候，函数的命名是和调用它的类所在的包名相关的，一旦包名与链接库中函数的命名不相符，就不能调用链接库的函数 

- 端口操作权限的问题，很多设备直接操作串口，会提示无权限 read/write 的问题，需要提权后才可以操作

  > 注意：需要在init.rc中初始化端口的权限eg: chmod 777 /dev/ttyS1

- 查看串口节点命令

  > 命令：ls  -l  /dev

  

## 五 附录：

### 5.1 源码参考

google :http://code.google.com/p/android-serialport-api/source/browse/trunk/android-serialport-api/

github：https://github.com/cepr/android-serialport-api

