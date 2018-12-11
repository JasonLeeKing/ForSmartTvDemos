package com.ktc.tvremote.client.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;

public class ServerData {
	public static final String TAG = "soniq";

	public static final int UDP_PORT = 8078;// 3690;
	public static final int TCP_PORT = 8079;

	// ��Э��
	// server����client����Ϣ

	// �㲥��������ַ��Ϣ: CMD+length+ip�ַ���
	public static final int CMD_SERVER_INIT = 80001001;

	public static final int CMD_SERVER_PING = 80001008;

	// client��server����Ϣ
	// �㲥��Ϣ����ȡ��������Ϣ
	public static final int CMD_CLIENT_INIT = 10001001;
	public static final int CMD_CLIENT_KEY = 10001002;
	public static final int CMD_CLIENT_SETNAME = 10001003;
	public static final int CMD_CLIENT_PING = 10001008;

	public static final int CMD_CLIENT_MOUSEMOVE = 10002001;
	public static final int CMD_CLIENT_MOUSECLICK = 10002002;

	public static final int CMD_CLIENT_APP_LIST = 10003001;
	public static final int CMD_CLIENT_OPEN_APP = 10003002;
	public static final int CMD_CLIENT_UNINSTALL_APP = 10003003;

	public static String getLocalIpAddress() {
		String ipaddress = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						ipaddress = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			return "û�л�ȡ��IP";
		}

		return ipaddress;
	}

	public static String getWIFILocalIpAdress(WifiManager wifiManager) {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = formatIpAddress(ipAddress);
		return ip;
	}

	private static String formatIpAddress(int ipAdress) {
		return (ipAdress & 0xFF) + "." + ((ipAdress >> 8) & 0xFF) + "."
				+ ((ipAdress >> 16) & 0xFF) + "." + (ipAdress >> 24 & 0xFF);
	}

	/***
	 * byte array ת��Ϊ int
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);

		return result;
	}

	/**
	 * ��bytes ��ʽת����int��ʽ
	 * 
	 * @param b
	 * @param offset
	 * @return
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * ϵͳ�ṩ�����鿽������arraycopy
	 * */
	public static byte[] sysCopy(List<byte[]> srcArrays) {
		int len = 0;
		for (byte[] srcArray : srcArrays) {
			len += srcArray.length;
		}
		byte[] destArray = new byte[len];
		int destLen = 0;
		for (byte[] srcArray : srcArrays) {
			System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
			destLen += srcArray.length;
		}

		return destArray;
	}

	public static void sendKeyCode(String ip, int keyCode) {
		List<byte[]> bytes = new ArrayList<byte[]>();
		bytes.add(intToByteArray(ServerData.CMD_CLIENT_KEY));
		int len = 4;
		bytes.add(intToByteArray(len));
		bytes.add(intToByteArray(keyCode));
		sendMessage(ip, sysCopy(bytes));
	}

	public static byte[] buildMessage(int cmd, String content) {
		List<byte[]> bytes = new ArrayList<byte[]>();
		bytes.add(ServerData.intToByteArray(cmd));

		int length = 0;
		if (content != null)
			length = content.getBytes().length;

		bytes.add(ServerData.intToByteArray(length));

		if (content != null) {
			bytes.add(content.getBytes());
		}

		return ServerData.sysCopy(bytes);
	}

	public static int sendMessage(String sendip, int cmd, String content) {
		byte[] data = buildMessage(cmd, content);

		return sendMessage(sendip, data);
	}

	public static int sendMessage(String sendip, byte[] buf) {
		try {
			DatagramSocket sendSocket = new DatagramSocket();
			InetAddress ip = InetAddress.getByName(sendip);
			Log.v("sendMessage", String.valueOf(ip));

			DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, ip,
					UDP_PORT);
			sendSocket.send(sendPacket);
			sendSocket.close();
			return 0;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	public static int getJsonInt(JSONObject jsonObject, String name,
			int defaultValue) {
		try {
			int n = jsonObject.getInt(name);

			return n;
		} catch (Exception e) {

		}

		return defaultValue;
	}

	public static String getJsonString(JSONObject jsonObject, String name) {
		try {
			return jsonObject.getString(name);
		} catch (Exception e) {

		}

		return "";
	}

	public static JSONObject getJsonObject(JSONObject jsonObject, String name) {
		try {
			return jsonObject.getJSONObject(name);
		} catch (Exception e) {

		}

		return null;
	}

	public static JSONArray getJsonArray(JSONObject jsonObject, String name) {
		try {
			return jsonObject.getJSONArray(name);
		} catch (Exception e) {

		}

		return null;
	}

	public static int getAppListFromServer(String serverIp,
			ArrayList<AppInfo> appList) {
		String content = getContentFromServer(serverIp,
				ServerData.CMD_CLIENT_APP_LIST);
		if (content == null || content.length() == 0)
			return 1;

		Log.v("mycontent", content);
		try {

			JSONArray jsonArray = new JSONArray(content);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);

				AppInfo appInfo = new AppInfo();
				appInfo.setAppName(getJsonString(obj, "appName"));
				appInfo.setPkgname(getJsonString(obj, "packageName"));
				appInfo.setAppVersion(getJsonString(obj, "versionName"));
				appInfo.setUninstall(getJsonString(obj, "uninstall"));
				Log.v("uninstall", appInfo.getUninstall());
				// appInfo.versionCode = getJsonInt(obj, "versionCode", 0);
				String s = getJsonString(obj, "appIcon");

				try {
					byte[] b = Base64.decode(s, Base64.DEFAULT);
					// appInfo.icon = BitmapFactory.decodeByteArray(b, 0,
					// b.length);
					BitmapDrawable icon = new BitmapDrawable(
							BitmapFactory.decodeByteArray(b, 0, b.length));
					appInfo.setIvIcon(icon);
				} catch (Exception e) {
					continue;
				}

				appList.add(appInfo);
			}

			return 0;
		} catch (Exception e) {
		}

		return 1;
	}

	public static int openApp(String serverIp, String packageName) {
		return sendMessage(serverIp, ServerData.CMD_CLIENT_OPEN_APP,
				packageName);
		// String content = "";
		//
		// DataInputStream dis = null;
		// DataOutputStream dos = null;
		//
		// try{
		// InetAddress serverAddr = InetAddress.getByName(serverIp);
		// Socket socket = new Socket(serverAddr, TCP_PORT);
		//
		// dis = new DataInputStream(new BufferedInputStream(
		// socket.getInputStream()));
		// dos = new DataOutputStream(new BufferedOutputStream(
		// socket.getOutputStream()));
		//
		// dos.writeInt(ServerData.CMD_OPEN_APP);
		// dos.writeInt(packageName.getBytes().length);
		// dos.write(packageName.getBytes(), 0, packageName.getBytes().length);
		// dos.flush();
		//
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		// finally
		// {
		// try
		// {
		// if( dis != null )
		// dis.close();
		// if( dos != null )
		// dos.close();
		// }
		// catch(Exception e)
		// {
		// }
		// }
		//
		// return 0;

	}

	public static int sendCommand(String serverIp, int cmd, String content) {
		DataInputStream dis = null;
		DataOutputStream dos = null;

		try {
			InetAddress serverAddr = InetAddress.getByName(serverIp);
			Socket socket = new Socket(serverAddr, TCP_PORT);

			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(new BufferedOutputStream(
					socket.getOutputStream()));

			dos.writeInt(cmd);
			if (content != null && content.length() > 0) {
				dos.writeInt(content.getBytes().length);
				dos.write(content.getBytes(), 0, content.getBytes().length);
			}

			dos.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
			} catch (Exception e) {
			}
		}

		return 0;
	}

	public static int uninstallApp(String serverIp, String packageName) {
		return sendMessage(serverIp, ServerData.CMD_CLIENT_UNINSTALL_APP,
				packageName);
		// String content = "";
		//
		// DataInputStream dis = null;
		// DataOutputStream dos = null;
		//
		// try{
		// InetAddress serverAddr = InetAddress.getByName(serverIp);
		// Socket socket = new Socket(serverAddr, TCP_PORT);
		//
		// dis = new DataInputStream(new BufferedInputStream(
		// socket.getInputStream()));
		// dos = new DataOutputStream(new BufferedOutputStream(
		// socket.getOutputStream()));
		//
		// dos.writeInt(ServerData.CMD_UNINSTALL_APP);
		// dos.writeInt(packageName.getBytes().length);
		// dos.write(packageName.getBytes(), 0, packageName.getBytes().length);
		// dos.flush();
		//
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		// finally
		// {
		// try
		// {
		// if( dis != null )
		// dis.close();
		// if( dos != null )
		// dos.close();
		// }
		// catch(Exception e)
		// {
		// }
		// }
		//
		// return 0;
		//
	}

	public static String getContentFromServer(String serverIP, int cmd) {

		String content = "";

		DataInputStream dis = null;
		DataOutputStream dos = null;

		try {
			InetAddress serverAddr = InetAddress.getByName(serverIP);
			Socket socket = new Socket(serverAddr, TCP_PORT);

			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(new BufferedOutputStream(
					socket.getOutputStream()));

			dos.writeInt(cmd);
			dos.flush();

			int length = dis.readInt();
			if (length > 0) {
				// ����
				byte[] buf = new byte[length];

				int left = length;
				int recved = 0;

				boolean b = true;
				while (left > 0) {
					int n = dis.read(buf, recved, left);
					if (n <= 0) {
						b = false;
						break;
					}

					recved += n;
					left -= n;
				}

				if (b)
					content = new String(buf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
			} catch (Exception e) {
			}
		}

		return content;
	}

}