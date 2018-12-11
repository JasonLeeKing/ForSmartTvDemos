package com.ktc.tvremote.client.bean;

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

import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.adapter.BindDeviceAdapter;
import com.ktc.tvremote.client.adapter.DeviceAdapter;
import com.ktc.tvremote.client.utils.CustomUI;
import com.ktc.tvremote.client.utils.MyUtils;
import com.ktc.tvremote.client.utils.WAPI;

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
	public GetDeviceList(Context cxt,TextView name){
		context = cxt;
		devicename = name;
		TAG = "GetDeviceList";
	}
	//lists�ǻ�ȡ���İ��豸�б�
	public GetDeviceList(Context cxt,ArrayList<DeviceInfo> lists){
		context = cxt;
		this.lists = lists;
		TAG = "GetDeviceList";
	}
	//�÷����ڵ����г�ģ���У�������ȡ�󶨵��豸�б�����¼���󣬵����Ŀ�а�װ�����Ӱ�ť���÷����������á�
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
				//����Ҫ�Ĳ������ݸ����������url����ȡ��������url��
				final String url = WAPI.addDownloadParams(GlobalParams.userid,appid, lists.get(arg2).getDeviceid());
				//�ڸ��豸�����������������ʱ�����������ύ��Ϣ����ʾ�򣬵��豸������Ϣ�󣬽���ȡ����
				Log.i("bindurl", url);
				CustomUI.showtips(context,R.string.market_tips_submit);
				new Thread(){
					public void run() {
						int code = 1;
						//��������õ���Ӧ
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
		//��dialog���ù̶�������ֵ
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
				//���豸��������Ϣ�����ܳɹ���ʧ�ܣ�������ȡ����
				if(CustomUI.tipdialog!=null)
				CustomUI.tipdialog.dismiss();
			//	Toast.makeText(context, "�����������ɹ�", 0).show();
				//�����������ɹ���ʾ��
				CustomUI.showtips(context,R.string.market_addtask_success,2);
			}else{
				//���豸��������Ϣ�����ܳɹ���ʧ�ܣ�������ȡ����
				if(CustomUI.tipdialog!=null)
					CustomUI.tipdialog.dismiss();
				//�����������ʧ����ʾ��
					CustomUI.showtips(context,R.string.market_addtask_failed,2);
			}
			
			super.handleMessage(msg);
		}
	};
	
	//�÷�������ControlFragment��ʹ�õģ�������ȡ�������ڰ�װ��server��apk���豸
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
		//�����Ŀ��ѡ�����ӵ�Ŀ���豸����ʱ���豸����Ϣ����ip,id,name���浽���أ��˺�����н��ᱻ�õ�
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

	//��ȡ���豸���͹�������Ϣ�������豸���ƣ�id��ip
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
					//�����ȡ���豸id��list�д��ڣ���ô�����Ϊ���»�ȡ���ġ�
					lists.get(i).setDevicename(deviceName);
					lists.get(i).setDeviceip(ip);
					Log.i(TAG, "list devicenameingetmessage" + lists.get(0).getDevicename());
					break;
				}
			}
			if(noExist) {
					DeviceInfo device = new DeviceInfo();
					//�������һ����list�в����ڵ��µ���Ŀ��������ӵ�list�С�
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
