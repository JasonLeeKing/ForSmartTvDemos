package com.ktc.remote.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ktc.remote.client.bean.AppInfo;
import com.ktc.remote.client.bean.DeviceInfo;
import com.ktc.remote.client.bean.DownloadTaskInfo;
import com.ktc.remote.client.bean.GlobalParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class WAPI {

	private static WAPI myInstance = null;

	public final static String WAPI_BASE_URL = "http://www.timesyw.com:8080/tvmarket/WAPI";

	//1. 用户注册接口
	public final static String WAPI_USER_REG_URL = WAPI_BASE_URL + "/smc/userreg.jsp";
	//2. 用户登录接口
	public final static String WAPI_USER_LOGIN_URL = WAPI_BASE_URL + "/smc/userlogin.jsp";
	//3. 修改密码接口
	public final static String WAPI_CHANGE_PWD_URL = WAPI_BASE_URL + "/smc/changepwd.jsp";
	//4. 密码发送
	public final static String WAPI_GET_PWD_URL = WAPI_BASE_URL + "/smc/getpwd.jsp";
	//10. 设备绑定接口
	public final static String WAPI_BIND_DEVICE_URL = WAPI_BASE_URL + "/smc/binddevice.jsp";
	//11. 查询绑定的设备列表
	public final static String WAPI_GET_DEVICE_LIST_URL = WAPI_BASE_URL + "/smc/getdevicelist.jsp";
	//12. 解除设备绑定
	public final static String WAPI_REMOVE_DEVICE_URL = WAPI_BASE_URL + "/smc/removedevice.jsp";
	//20. 添加下载任务
	public final static String WAPI_ADD_TASK_URL = WAPI_BASE_URL + "/smc/addtask.jsp";
	//21. 查询我的下载任务
	public final static String WAPI_GET_MY_TASK_URL = WAPI_BASE_URL + "/smc/getmytask.jsp";
	//22. 查询未下载任务(手机遥控器服务器用), 只返回一条记录
	public final static String WAPI_GET_DOWNLOAD_TASK_URL = WAPI_BASE_URL + "/smc/getdownloadtask.jsp";
	//23. 设置下载任务的状态
	public final static String WAPI_SET_TASK_STATUS_URL = WAPI_BASE_URL + "/smc/settaskstatus.jsp";
	//24. 删除下载任务
	public final static String WAPI_DELETE_TASK_URL = WAPI_BASE_URL + "/smc/deletetask.jsp";
	//30. 检查更新接口
	public final static String WAPI_CHECK_VERSION_URL = WAPI_BASE_URL + "/checkversion.jsp";
	//31
	public final static String WAPI_GET_APP_RANK_LIST_URL = WAPI_BASE_URL + "/getappranklist.jsp?rankid=3";

	public static String addLangParams(String urlString)
	{
		String newURLString = String.format("%s&lang=%s",WAPI_GET_APP_RANK_LIST_URL, urlString);
		
		return newURLString;
	}
	
	public static String addUseridParams(String userid)
	{
		String newURLString = String.format("%s?userid=%s",WAPI_GET_DEVICE_LIST_URL, userid);
		
		return newURLString;
	}
	
	public static String addBindDeviceParams(String userid,String deviceid,String devicename)
	{
		String newURLString = String.format("%s?userid=%s&devid=%s&devname=%s",WAPI_BIND_DEVICE_URL,userid,deviceid,devicename);
		
		return newURLString;
	}
	
	public static String addDownloadParams(String userid,int appid,String deviceid)
	{
		String newURLString = String.format("%s?userid=%s&appid=%s&devid=%s",WAPI_ADD_TASK_URL,userid,appid,deviceid);
		
		return newURLString;
	}

	public static String GetUnBindURL(String userid,String devid)
	{
		String newURLString = String.format("%s?userid=%s&devid=%s",WAPI_REMOVE_DEVICE_URL, userid,devid);
		
		return newURLString;
	}

	public static String GetUnDownloadURL(int taskid) {
		String newURLString = String.format("%s?userid=%s&taskid=%s",WAPI_DELETE_TASK_URL, GlobalParams.userid,taskid);		
		return newURLString;
	}
	
	public static WAPI getInstance()
	{
		if( myInstance == null )
			myInstance = new WAPI();
		
		return myInstance;
	}
	
	public static Bitmap http_get_icon(String icon_url)
	{
		HttpGet request = new HttpGet(icon_url);
		
		HttpClient httpClient = new DefaultHttpClient();
		try
		{
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			HttpResponse response = httpClient.execute(request);
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static int parseAppListJSONResponse(String responseString, 
			List<AppInfo> appList)
	{
		int ret = 1;
		
		try{
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			int code = resultObject.getInt("code");
			if( code == 0 )
			{
				JSONArray dataArray = jsonObject.getJSONArray("applist");
				ret = 0;
				for(int i = 0; i < dataArray.length(); i++ )
				{
					JSONObject obj = dataArray.getJSONObject(i);
					
					AppInfo appinfo = new AppInfo();
					
					appinfo.setId(getJsonInt(obj, "id", 0));
					appinfo.setAppName(getJsonString(obj, "name"));
					appinfo.setPy(getJsonString(obj, "py"));
					appinfo.setClassid(getJsonInt(obj, "classid", 0));
					appinfo.setClassname(getJsonString(obj, "classname"));
					appinfo.setAppVersion(getJsonString(obj, "version"));
					appinfo.setPkgname(getJsonString(obj, "pkgname"));
					appinfo.setSize(getJsonInt(obj, "size", 0));
					appinfo.setIcon(getJsonString(obj, "icon"));
					appinfo.setDownurl(getJsonString(obj, "downurl"));
					appinfo.setDescription(getJsonString(obj, "desc"));
					appinfo.setVersioncode(getJsonInt(obj, "versioncode", 0));
					appinfo.setOptype(getJsonString(obj, "optype"));
					appList.add(appinfo);/**/
					
				}
			}
			
		}
		catch(Exception e)
		{
			
		}
		
		return ret;
	}
	
	
	public static int parseDeviceList(String responseString, 
			ArrayList<DeviceInfo> deviceList)
	{
		int ret = 1;
		
		try{
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			int code = resultObject.getInt("code");
			if( code == 0 )
			{
				
				JSONArray dataArray = jsonObject.getJSONArray("devlist");
				ret = 0;
				Log.v("devicelist", "列表"+dataArray.length());
				for(int i = 0; i < dataArray.length(); i++ )
				{
					Log.v("devicelist", "列表"+i);
					JSONObject obj = dataArray.getJSONObject(i);
					
					DeviceInfo deviceinfo = new DeviceInfo();
					
					deviceinfo.setDevicename(getJsonString(obj, "devname"));
					Log.v("devicelist", deviceinfo.getDevicename());
					deviceinfo.setDeviceid(getJsonString(obj, "devid"));
					Log.v("devicelist", deviceinfo.getDeviceid());
					deviceList.add(deviceinfo);/**/
					Log.v("devicelist", deviceList.size()+"");
				}
			}
			
		}
		catch(Exception e)
		{
			
		}
		
		return ret;
	}
	
	
	public static int parseBindDevice(String responseString)
	{
		int ret = 1;
		
		try{
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			
			int code = resultObject.getInt("code");
			if( code == 0 )
				ret = 0;
		}
		catch(Exception e)
		{
			
		}
		
		return ret;
	}

	public static String http_get_content(String url)
	{
		HttpGet request = new HttpGet(url);
//		request.setHeader("User-Agent", MyProfile.http_user_agent);
		
		HttpClient httpClient = new DefaultHttpClient();
		try
		{
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			HttpResponse response = httpClient.execute(request);
			if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				String str = EntityUtils.toString(response.getEntity());
				return str;
			}
		}
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			String string = e.toString();
			e.printStackTrace();
		}
		return null;
	}		
	
	
	public static String getValueByTagName(Element element, String tagName)
	{
		NodeList nodeList = element.getElementsByTagName(tagName);
		if( nodeList != null && nodeList.getLength() > 0 )
		{
			Element e = (Element)nodeList.item(0);
			if( e != null && e.getFirstChild() != null )
			{
				return e.getFirstChild().getNodeValue().trim();
			}
		}
		return "";
	}
	
	
	public static String getAttributeValueByTagName(Element element, String tagName)
	{
		String s = element.getAttribute(tagName);
		return s;
	}	
	
	public static int getIntValueByTagName(Element element, String tagName)
	{
		String s = getValueByTagName(element, tagName);
		if( s == "" )
			return 0;
		
		return Integer.parseInt(s);
	}	

	
	public static int getIntAttributeValueByTagName(Element element, String tagName)
	{
		String s = getAttributeValueByTagName(element, tagName);
		if( s == null || s.length() < 1 )
			return 0;
		
		return Integer.parseInt(s);
	}		

	public static String get_content_from_remote_url(String url)
	{		
		try
		{
			String scontent = http_get_content(url);
			if( scontent == null || scontent == "" )
				return null;
			
			return scontent;
		}
		catch(Exception e)
		{
			
		}
		
		return null;
		
	}
		
	public static int getJsonInt(JSONObject jsonObject, String name, int defaultValue)
	{
		try
		{
			int n = jsonObject.getInt(name);
			
			return n;
		}
		catch(Exception e)
		{
			
		}
		
		return defaultValue;
	}
	
	public static String getJsonString(JSONObject jsonObject, String name)
	{
		try
		{
			return jsonObject.getString(name);
		}
		catch(Exception e)
		{
			
		}
		
		return "";
	}
	
	public static JSONObject getJsonObject(JSONObject jsonObject, String name)
	{
		try
		{
			return jsonObject.getJSONObject(name);
		}
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	
	public static JSONArray getJsonArray(JSONObject jsonObject, String name)
	{
		try
		{
			return jsonObject.getJSONArray(name);
		}
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	
	
	
	public static int parseVersionInfoResponse(Context context, String responseString, 
			ArrayList<String> fieldList)
	{
		int ret = 1;
		
		try{
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			int code = resultObject.getInt("code");
			if( code == 0 )
			{
				jsonObject = jsonObject.getJSONObject("versioninfo");

				String version = jsonObject.getString("version");
				String versioncode = jsonObject.getString("versioncode");
				String desc = jsonObject.getString("desc");
				String force = getJsonString(jsonObject, "force");
				String downloadurl = jsonObject.getString("downloadurl");

				if( force == null || force.length() < 1)
					force = "no";

				fieldList.add(versioncode);
				fieldList.add(desc);
				fieldList.add(downloadurl);
				fieldList.add(force);
				fieldList.add(version);
				
				ret = 0;
			}
			
		}
		catch(Exception e)
		{
			
		}
		
		return ret;
	}
	
	public static int parseUserLoginInfoResponse(Context context,
			String responseString, ArrayList<String> fieldList) {
		int code = 1;
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			code = resultObject.getInt("code");
			String message = resultObject.getString("message");
			fieldList.add(message);
			if( code == 0 )
			{
				jsonObject = jsonObject.getJSONObject("userinfo");				
				String userid = jsonObject.getString("userid");
				String username = jsonObject.getString("username");
				fieldList.add(userid);
				fieldList.add(username);				
			}
		} catch (Exception e) {
		}
		return code;
	}

	public static String GetUpgradeURL() {
		String newURLString = String.format("%s?client=remotecontrolandroid",WAPI_CHECK_VERSION_URL);
		return newURLString;
	}

	public static String addLoginParams(String user_name, String user_pwd) {
		String newURLString = String.format("%s?user=%s&pwd=%s&lang=%s",WAPI_USER_LOGIN_URL,user_name,user_pwd,MyUtils.GetLangString());
		return newURLString;
	}

	public static String addRegisterParams(String user_name, String user_pwd) {
		String newURLString = String.format("%s?user=%s&pwd=%s&lang=%s",WAPI_USER_REG_URL,user_name,user_pwd,MyUtils.GetLangString());
		return newURLString;
	}
	
	public static String addChangePwdParams(String user_name, String old_pwd, String new_pwd) {
		String newURLString = String.format("%s?userid=%s&oldpwd=%s&newpwd=%s",WAPI_CHANGE_PWD_URL,user_name,old_pwd,new_pwd);
		return newURLString;
	}

	public static String GetBindDeviceURL() {
		String newURLString = String.format("%s?userid=%s&devid=%s&devname=%s",WAPI_BIND_DEVICE_URL,GlobalParams.userid,GlobalParams.DEVICEID,GlobalParams.DEVICENAME);
		return newURLString;
	}
	
	public static String GetDownloadTaskListURL() {
		String newURLString = String.format("%s?userid=%s",WAPI_GET_MY_TASK_URL,GlobalParams.userid);
		return newURLString;
	}
	
	public static int parseChangePwdResponse(Context applicationContext,
			String responseString, ArrayList<String> fieldList) {
		int code = 1;
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			code = resultObject.getInt("code");
			String message = resultObject.getString("message");
			fieldList.add(message);
		} catch (Exception e) {
		}
		return code;
	}

	public static int parseUnDownloadParam(String responseString,
			ArrayList<String> fieldList) {
		int code = 1;
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			code = resultObject.getInt("code");
			String message = resultObject.getString("message");
			fieldList.add(message);
		} catch (Exception e) {
		}
		return code;
	}
	public static int parseUnBindParam(String responseString,
			ArrayList<String> fieldList) {
		int code = 1;
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			code = resultObject.getInt("code");
			String message = resultObject.getString("message");
			fieldList.add(message);
		} catch (Exception e) {
		}
		return code;
	}

	public static int parseBindDeviceInfo(String responseString,
			ArrayList<String> fieldList) {
		int code = 1;
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			code = resultObject.getInt("code");
			String message = resultObject.getString("message");
			fieldList.add(message);
		} catch (Exception e) {
		}
		return code;
	}

	public static int parseDownloadTaskList(String responseString,
			ArrayList<DownloadTaskInfo> downloadTaskLists) {
		int ret = 1;
		
		try{
			JSONObject jsonObject = new JSONObject(responseString);
			JSONObject resultObject = jsonObject.getJSONObject("result");
			ret = resultObject.getInt("code");
			if( ret == 0 )
			{
				JSONArray dataArray = jsonObject.getJSONArray("tasklist");
				ret = 0;
				for(int i = 0; i < dataArray.length(); i++ )
				{
					JSONObject obj = dataArray.getJSONObject(i);					
					DownloadTaskInfo deviceinfo = new DownloadTaskInfo();
					deviceinfo.setTaskid(getJsonInt(obj, "taskid",-1));
					deviceinfo.setAppid(getJsonString(obj, "appid"));
					deviceinfo.setIcon(getJsonString(obj, "icon"));
					deviceinfo.setAppname(getJsonString(obj, "appname"));
					deviceinfo.setPkgname(getJsonString(obj, "pkgname"));
					deviceinfo.setDevid(getJsonString(obj, "devid"));
					deviceinfo.setDevname(getJsonString(obj, "devname"));
					deviceinfo.setStatus(getJsonInt(obj, "status",0));
					downloadTaskLists.add(deviceinfo);
				}
			}			
		}
		catch(Exception e)
		{			
		}
		
		return ret;
	}
}
