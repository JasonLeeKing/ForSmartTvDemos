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
