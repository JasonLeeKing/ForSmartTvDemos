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

>  java open –> jni open –>c open

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

```
package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.util.Log;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	
	private static SerialPort mSerialPort ;
	
	/**
	 * @TODO SerialPort单例对象
	 * @return SerialPort
	 */
	public SerialPort getInstance(){
		if(mSerialPort == null){
			mSerialPort = new SerialPort();
		}
		return mSerialPort ;
	}
	
	/**
	 * TODO 初始化串口配置并打开串口{设备、波特率、标志位}
	 * @param File device, int baudrate, int flags
	 * @return void
	 */
	public void initSerialPort(File device, int baudrate, int flags) throws Exception {
		Log.i(TAG, "----initSerialPort---:  "+"device: "+device.getAbsolutePath()+"\nbaudrate:  "+baudrate+"\nflags:  "+flags);
		//检查device权限
		if (!device.canRead() || !device.canWrite()) {
			//如果device丢失权限，需要再次获取权限
			try {
				Process su;
				su = Runtime.getRuntime().exec("/system/xbin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		//打开设备，调用jni 的open方法
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		Log.i(TAG, "---mFd---  "+mFd);
		if (mFd == null) {
			Log.e(TAG, "---native open returns null---");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	/**
	 * TODO 获取串口输入流
	 * @param null
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	/**
	 * TODO 获取串口输出流
	 * @param null
	 * @return OutputStream
	 */
	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	/**************************Jni 开放接口********************/
	/**
     * @TODO 打开串口设备
     * @param path 设备的绝对路径
     * @param baudrate 波特率
     * @param flags 标志
     * @deprecated  直接关联到so中的jni open
     */
	private native static FileDescriptor open(String path, int baudrate, int flags);
	
	//关闭设备
	public native void close();
	
	//加载动态链接库
	static {
		System.loadLibrary("serial_port");
	}
}


```



#### 2.2.2 SerialPort.h接口类

```
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class android_serialport_api_SerialPort */

#ifndef _Included_android_serialport_api_SerialPort
#define _Included_android_serialport_api_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     android_serialport_api_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open
  (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     android_serialport_api_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif

```



#### 2.2.3 SerialPort.c实现类

```
/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPort.h"

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

/*
 * getBaudrate(jint baudrate)
 * TODO: 获取端口的波特率
 */
static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/*
 * Class:     android_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open
  (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
{
	//变量定义，参数检查
	int fd;
	speed_t speed;
	jobject mFileDescriptor;

	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
	}

	/* Opening device */
	//打开设备
	{
		jboolean iscopy;
		const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
		LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
		//使用c语言的open函数打开文件
		fd = open(path_utf, O_RDWR | flags);
		LOGD("open() fd = %d", fd);
		(*env)->ReleaseStringUTFChars(env, path, path_utf);
		if (fd == -1)
		{
			/* Throw an exception */
			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Configure device */
	//配置device
	{
		struct termios cfg;
		LOGD("Configuring serial port");
		if (tcgetattr(fd, &cfg))
		{
			LOGE("tcgetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}

		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);

		if (tcsetattr(fd, TCSANOW, &cfg))
		{
			LOGE("tcsetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Create a corresponding file descriptor */
	//创建我们需要的 file descriptor
	{
		jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
		jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
		jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
		mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
		(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
	}

	return mFileDescriptor;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
  (JNIEnv *env, jobject thiz)
{
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	close(descriptor);
}
```



#### 2.2.4 SerialPortFinder.java

> 如果要操作一个串口设备，需要知道该端口的地址和波特率。
>
> 其中波特率一般是根据硬件的说明来设定的，所以在选择的波特率的时候，我们可以提供一些值来做选择，KTC主板一般使用的波特率是115200。 
>
> 其次硬件地址，需要从/proc/tty/drivers文件中解析获得，SerialPortFinder就是获取硬件地址的类。 



```
/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

/**
 * @TODO 串口设备寻检器
 * @author Arvin
 */
public class SerialPortFinder {
	private static final String TAG = "SerialPortFinder";
	
	private Vector<Driver> mDrivers = null;

	
	/**
	 * @TODO 定义设备类
	 */
	public class Driver {
		public Driver(String name, String root) {
			mDriverName = name;
			mDeviceRoot = root;
		}
		
		private String mDriverName;//设备名称
        private String mDeviceRoot;//设备根节点
        Vector<File> mDevices = null;//设备集合
        
        /**
         * @TODO 获取设备集合 这个是特定类型的设备 比如USB等
         * @return Vector<File>
         */
		public Vector<File> getDevices() {
			if (mDevices == null) {
				mDevices = new Vector<File>();
				File dev = new File("/dev");
				 //获取/dev挂载节点下的设备列表
				File[] files = dev.listFiles();
				int i;
				for (i=0; i<files.length; i++) {
                    //比如 我们传递进来的文件路径是：  /dev/tty1 那么我们获取道的文件的绝对路径 如：/dev/tty0 /dev/tty1
					if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
						Log.d(TAG, "Found new device: " + files[i]);
						mDevices.add(files[i]);
					}
				}
			}
			return mDevices;
		}
		
		public String getName() {
			return mDriverName;
		}
	}

	/**
     * @TODO 获取所有设备挂载节点  
     * @return
     * @throws IOException
     * @desc 其实就是读取 /proc/tty/drivers 这个文件,drivers中有设备的地址的总地址添加到一个集合中
     */
	Vector<Driver> getDrivers() throws IOException {
		if (mDrivers == null) {
			mDrivers = new Vector<Driver>();
			LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
			String l;
			while((l = r.readLine()) != null) {
				// Issue 3:
				// Since driver name may contain spaces, we do not extract driver name with split()
				//0x15:第一字符串（/dev/tty）到第二个字符（/dev/tty）之间字符串长度是21（0x15）
                //eg:/dev/tty             /dev/tty        5       0 system:/dev/tty
				String drivername = l.substring(0, 0x15).trim();//其实就是获取第一个非空格字符串
				String[] w = l.split(" +");//正则表达式：" +" 表示有一个或者多个空格
				if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
					Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4]);
					mDrivers.add(new Driver(drivername, w[w.length-4]));
				}
			}
			r.close();//关闭流
		}
		return mDrivers;
	}

	/**
     * 得到所有的设备的名称
     * @return String[]
     */
	public String[] getAllDevices() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();//遍历所有设备的根地址
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();//获取包含根地址的设备对象的集合
				while(itdev.hasNext()) {//迭代获得具体的设备
					String device = itdev.next().getName();
					String value = String.format("%s (%s)", device, driver.getName());
					devices.add(value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}

	/**
     * @TODO 获取所有设备节点的绝对路径列表
     * @return String[]
     */
	public String[] getAllDevicesPath() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();
				while(itdev.hasNext()) {
					//获取设备节点的绝对路径并添加至列表
					String device = itdev.next().getAbsolutePath();
					devices.add(device);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}
}

```

> 至此，android_serialport_api串口通信JNI层配置逻辑基本完成，该阶段的主要目的是遍历deviers节点并获取端口硬件地址、配置波特率，同时疏通Java open-->JNI Open-->C Open流程。
>
> 下一步将进入应用层适配及通信逻辑实现阶段~



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

>  监听Intent.ACTION_BOOT_COMPLETED-->启动SerialConsoleService-->初始化SerialPort所有配置
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

### 5.2 操作界面

#### 5.2.1 PC终端界面

![](C:\Users\yzh\Desktop\PC.png)

#### 5.2.2 TV终端可视化操作界面

![](C:\Users\yzh\Desktop\TV.png)



