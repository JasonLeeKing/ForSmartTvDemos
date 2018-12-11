package com.ktc.remote.client.bean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.example.mytabhost.R;
import com.ktc.remote.client.adapter.BindDeviceAdapter;
import com.ktc.remote.client.adapter.DeviceAdapter;
import com.ktc.remote.client.utils.CustomUI;
import com.ktc.remote.client.utils.MyUtils;
import com.ktc.remote.client.utils.WAPI;

public class GetDeviceList {
	private Dialog dialog;
	private Context context;
	private ListView bind_device_list;
	private ArrayList<DeviceInfo> lists;
	private String ip;
	private DeviceAdapter adapter;
	private BindDeviceAdapter bindadapter;
	private Animation loadinganimation;
	private ImageView loadingview;
	private String TAG;
	private Button cancel;
	private TextView devicename;
	private View bind_device_view;
	//该构造方法传入的TextView是ControllerFragment中id为devicename的TextView，目的是当选中设备列表中的一个时，将其值赋给该textview
	public GetDeviceList(Context cxt,TextView name){
		context = cxt;
		devicename = name;
		TAG = "GetDeviceList";
	}
	//lists是获取到的绑定设备列表
	public GetDeviceList(Context cxt,ArrayList<DeviceInfo> lists){
		context = cxt;
		this.lists = lists;
		TAG = "GetDeviceList";
	}
	//该方法在电子市场模块中，用来获取绑定的设备列表。当登录过后，点击条目中安装到电视按钮，该方法将被调用。
	public void getBindListDialog(final int appid){
		dialog = new Dialog(context, R.style.dialog);
		bind_device_view = LayoutInflater.from(context).inflate(R.layout.bind_device, null);
		bind_device_list = (ListView) bind_device_view.findViewById(R.id.bind_device_list);
		cancel = (Button) bind_device_view.findViewById(R.id.bind_device_cancel);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			
			}
			
		});
		
		bindadapter = new BindDeviceAdapter(context,lists);
		bind_device_list.setAdapter(bindadapter);
		
		bind_device_list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//将需要的参数传递给下载任务的url，获取到完整的url，
				final String url = WAPI.addDownloadParams(GlobalParams.userid,appid, lists.get(arg2).getDeviceid());
				//在给设备发送下载任务的命令时，弹出正在提交信息的提示框，当设备返回消息后，将其取消。
				Log.i("bindurl", url);
				CustomUI.showtips(context,R.string.market_tips_submit);
				new Thread(){
					public void run() {
						int code = 1;
						//发出命令，得到响应
						String content = WAPI.http_get_content(url);
						try{
						JSONObject jsonObject = new JSONObject(content);
						JSONObject resultObject = jsonObject.getJSONObject("result");
						code = resultObject.getInt("code");
						}catch(Exception e){
							
						}
						Message msg = Message.obtain();
						msg.what = code;
						handler.sendMessage(msg);
					};
				}.start();
				dialog.dismiss();
			}
			
		});
		//给dialog设置固定的像素值
		int w = MyUtils.dip2px(context,270);
		int h = MyUtils.dip2px(context, 270);
		
		dialog.setContentView(bind_device_view,new ViewGroup.LayoutParams(w, h));
	
		dialog.show();
	}
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0){
				//当设备返回来消息，不管成功或失败，都将其取消掉
				if(CustomUI.tipdialog!=null)
				CustomUI.tipdialog.dismiss();
			//	Toast.makeText(context, "添加下载任务成功", 0).show();
				//弹出添加任务成功提示框
				CustomUI.showtips(context,R.string.market_addtask_success,2);
			}else{
				//当设备返回来消息，不管成功或失败，都将其取消掉
				if(CustomUI.tipdialog!=null)
					CustomUI.tipdialog.dismiss();
				//弹出添加任务失败提示框
					CustomUI.showtips(context,R.string.market_addtask_failed,2);
			}
			
			super.handleMessage(msg);
		}
	};
	
	//该方法是在ControlFragment里使用的，用来获取局域网内安装有server端apk的设备
	public void show(){
		dialog = new Dialog(context, R.style.dialog);
		bind_device_view = LayoutInflater.from(context).inflate(R.layout.search, null);
		bind_device_list = (ListView) bind_device_view.findViewById(R.id.search_list);
		loadinganimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		
		loadingview = (ImageView) bind_device_view.findViewById(R.id.imageViewLoading);
		loadingview.startAnimation(loadinganimation);
	
		cancel = (Button) bind_device_view.findViewById(R.id.search_cancel);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			
			}
			
		});
		lists = new ArrayList<DeviceInfo>();
		ip = "255.255.255.255";
		adapter = new DeviceAdapter(context,lists,ip);
		bind_device_list.setAdapter(adapter);
		//点击条目即选择连接的目标设备。此时将设备的信息包括ip,id,name保存到本地，此后操作中将会被用到
		bind_device_list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				DeviceInfo deviceInfo = lists.get(arg2);
				GlobalParams.IP = deviceInfo.getDeviceip();
				GlobalParams.DEVICEID = deviceInfo.getDeviceid();
				GlobalParams.DEVICENAME = deviceInfo.getDevicename();
				Log.v("--- deviceInfo.getDevicename", "--- deviceInfo.getDevicename" + deviceInfo.getDevicename());
				devicename.setText(deviceInfo.getDevicename()+"("+deviceInfo.getDeviceip()+")");
			//	deviceip.setText(deviceInfo.getDeviceip());
				dialog.dismiss();
			}
			
		});
		
		int w = MyUtils.dip2px(context,270);
		int h = MyUtils.dip2px(context, 270);
		
		dialog.setContentView(bind_device_view,new ViewGroup.LayoutParams(w, h));
		loadingview.startAnimation(loadinganimation);
		String localip = "";
	    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
	    if (wifiManager.isWifiEnabled()) {  
	    	localip = ServerData.getWIFILocalIpAdress(wifiManager);  
	    } else {
	    	localip = ServerData.getLocalIpAddress();
	    }	
	    Log.v(TAG, "localIP:"+ localip);
	    
	    Log.v(TAG, localip + "---" + ip);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				ServerData.sendMessage(ip, ServerData.CMD_CLIENT_INIT, "");
				getMessage();
			}
		}).start();
		dialog.show();
	}

	//获取到设备发送过来的信息，包括设备名称，id，ip
	private void getMessage(){
		byte[] buf = new byte[64];
		try{
			
		DatagramSocket mySocket = new DatagramSocket(ServerData.UDP_PORT);
		
		while(GlobalParams.isget) {
			boolean noExist = true;
		DatagramPacket myPacket = new DatagramPacket(buf, buf.length);
		Log.v(TAG, "receive...");
		mySocket.receive(myPacket);
		byte[] data = myPacket.getData();
		int value = ServerData.byteArrayToInt(data, 0);
		Log.i(TAG, "getMessage:" +  String.valueOf(value));
		
		if(value == ServerData.CMD_SERVER_INIT) {
			
			String infoString = getInfoString(data);
			String ip = getTagValue(infoString, "ip");
			String id = getTagValue(infoString, "id");
			String deviceName = getTagValue(infoString, "name");
		    int flag = 0;
			for(int i = 0; i < lists.size(); i++) {
				if(lists.get(i).getDeviceid().equals(id)) {
					noExist = false;
					//如果获取的设备id在list中存在，那么将其改为最新获取到的。
					lists.get(i).setDevicename(deviceName);
					lists.get(i).setDeviceip(ip);
					Log.i(TAG, "list devicenameingetmessage" + lists.get(0).getDevicename());
					break;
				}
			}
			if(noExist) {
					DeviceInfo device = new DeviceInfo();
					//如果这是一个在list中不存在的新的条目，则将其添加到list中。
					device.setDevicename(deviceName);
					device.setDeviceip(ip);
					device.setDeviceid(id);
					lists.add(device);
					Log.v(TAG, "list devicenameingetmessagenoExist" + lists.get(0).getDevicename());
			}
			
			mHandler.sendEmptyMessage(10);
		}
		}
		mySocket.close();
		}catch(IOException e){
			Log.v(TAG, "soniqtvremotecontrol: Prot is use!");
		}
	}
	
	Handler mHandler = new Handler() { 
        @Override
		public void handleMessage(Message msg) {  
            switch (msg.what) {      
            case 10:
            	Log.v(TAG, "listClient.size:" + String.valueOf(lists.size()));
            	Log.v(TAG, "list devicenameinhandler" + lists.get(0).getDevicename());
            	loadinganimation.cancel();
            	loadingview.setAnimation(null);
            	loadingview.setVisibility(View.GONE);
			//	emarket_list.setAdapter(adapter);
            	bind_device_list.setVisibility(View.VISIBLE);
            	adapter.notifyDataSetChanged();            	
                break;      
            } 
            
            super.handleMessage(msg);  
        }  
          
    };
/**/
	private String getTagValue(String content, String tag)
    {
    	String bTag = String.format("%s=", tag);
    	String eTag = ";";
    	
    	int b = content.indexOf(bTag);
    	if( b < 0 )
    		return null;
    	
    	b += bTag.length();
    	
    	int e = content.indexOf(eTag, b);
    	if( e < 0 )
    		return content.substring(b);
    	else
    		return content.substring(b, e);
    }
    
	String getInfoString(byte[] data) {
		
		try{
		int length = ServerData.byteArrayToInt(data, 4);
		if( length > 0 )
		{
			byte[] bc = new byte[length];
			System.arraycopy(data, 8, bc, 0, length);
			
			String infoString = new String(bc, "utf-8");
            Log.v(ServerData.TAG, "CMD_SERVER_INIT:" +infoString);
            
            String serverip = getTagValue(infoString, "ip");
            Log.v(ServerData.TAG, "server ip=" + serverip);
            
            return infoString;
		}
		
		}
		catch(Exception e)
		{
			
		}
		
		return null;
    }
	
String getClient(byte[] data) {
		
		try{
		int length = ServerData.byteArrayToInt(data, 4);
		if( length > 0 )
		{
			byte[] bc = new byte[length];
			System.arraycopy(data, 8, bc, 0, length);
			
			String infoString = new String(bc, "utf-8");
            Log.v(ServerData.TAG, "CMD_SERVER_INIT:" +infoString);
            
            String serverip = getTagValue(infoString, "ip");
            Log.v(ServerData.TAG, "server ip=" + serverip);
            
            return serverip;
            
		}
		
		}
		catch(Exception e)
		{
			
		}
		
		return null;
            
    }

	
}
