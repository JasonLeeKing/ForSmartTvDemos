package com.ktc.serialport.serialtest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.util.Log;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import com.ktc.serialport.home.MyApplication;
import com.ktc.serialport.utils.KtcHexUtil;

/**
*
* TODO 串口操作工具类
*
* @author Arvin
* 2018-3-11
*/
public abstract class SerialHelper{
	private static final String TAG = "SerialHelper";
	
	private MyApplication mMyApplication;
	private SerialPortFinder mSerialPortFinder;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	
	/**
	 * TODO 初始化SerialHelper串口工具类
	 * @param null
	 * @return void
	 */
	public void initSerialHelper(){
		mMyApplication = MyApplication.getInstance();
		mSerialPortFinder = mMyApplication.getSerialPortFinder();
		try {
			mSerialPort = mMyApplication.getSerialPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO 获取SerialPort单例对象
	 * @param 
	 * @return SerialPort
	 */
	public SerialPort getSerialPort(){
		if(mSerialPort == null){
			try {
				if(mMyApplication == null){
					mMyApplication = MyApplication.getInstance();
				}
				mSerialPort = mMyApplication.getSerialPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mSerialPort;
	}
	
	/**
	 * TODO 获取SerialPortFinder单例对象
	 * @param 
	 * @return SerialPortFinder
	 */
	public SerialPortFinder getSerialPortFinder(){
		if(mSerialPortFinder == null){
			try {
				if(mMyApplication == null){
					mMyApplication = MyApplication.getInstance();
				}
				mSerialPortFinder = mMyApplication.getSerialPortFinder();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mSerialPortFinder;
	}
	
	/**
	 * TODO 初始化SerialPort端口配置
	 * @param File device, int baudrate, int flags
	 * @return void
	 */
	public void initSerialPortConfig(File device, int baudrate, int flags) throws Exception{
		if(mSerialPort != null){
			Log.i(TAG, "---initSerialPortConfig---");
			mSerialPort.initSerialPort(device, baudrate, flags);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			
			mReadThread = new ReadThread();
			mReadThread.start();
		}
	}
	
	/**
	 * TODO 以String形式发送指令到PC端
	 * @param String
	 * @return boolean
	 */
	public boolean sendStrCmd(String cmdHex) {
		Log.i(TAG, "---sendStrCmd---");
		String mCSHex = getCmdCS(cmdHex) ;
		Log.i(TAG, "---mCSHex---:  "+mCSHex);
		
		return sendBufferCmd(KtcHexUtil.hexStr2ByteArray(cmdHex + mCSHex));
	}
	
	/**
	 * TODO 以byte[]形式发送指令到PC端
	 * @param byte[]
	 * @return boolean
	 */
	public boolean sendBufferCmd(byte[] mBuffer) {
		Log.i(TAG, "---sendBufferCmd---");
		boolean result = true;
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
			} else {
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}finally{
			try {
				if (mOutputStream != null) {
					mOutputStream.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * TODO 获取命令的校验位CheckSum(rule:Checksum(CS) :  0xFF – (amount of all Data exclude checksum byte) & 0xFF)
	 * @param String cmdHex
	 * @return String
	 */
	private String getCmdCS(String cmdHex) {
		if (cmdHex == null || cmdHex.equals("")) {
			return ""; 
		}
		int total = 0;
		int len = cmdHex.length();
		int num = 0;
		while (num < len) {
			String s = cmdHex.substring(num, num + 2);
			total += Integer.parseInt(s, 16);
			num = num + 2; 
		}
		//用256求余最大是255，即16进制的FF
		int mod = total % 256;
		String hex = Integer.toHexString(mod);
		// 如果不够两位校验位的长度，补0
		if(hex.length() < 2){
			hex = "0"+hex ;
		}
		
		return Integer.toHexString(Integer.parseInt("FF", 16) - Integer.parseInt(hex, 16));
	}
	
	/**
	 * TODO 串口接收数据处理
	 * @param final byte[] buffer, final int size
	 * @return void
	 */
	protected abstract void onDataReceived(final byte[] buffer, final int size);
	
	/**
	 * TODO 开启子线程实时监听串口数据
	 * @author Arvin
	 * 2018-3-10
	 */
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			Log.i(TAG , "---ReadThread_run---");
			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[512];
					Log.i(TAG, "ReadThread_buffer:  "+buffer.length);
					if (mInputStream == null){
						return ;
					} 
					size = mInputStream.read(buffer);
					if (size > 0) {
						String msg = KtcHexUtil.byte2HexStr(buffer, size);
						Log.i(TAG, "ReadThread_msg:   "+msg);
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.i(TAG, "IOException:   "+e.toString());
					return;
				}
			}
		}
	}
	
	/**
	 * TODO 关闭串口通信
	 * @param null
	 * @return void
	 */
	public void closeSerialPort(){
		Log.i(TAG, "---closeSerialPort---");
		try {
			if (mReadThread != null){
				mReadThread.interrupt();
			}
			mMyApplication.closeSerialPort();
			mSerialPort = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}