package com.ktc.tvremote.client.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.utils.CustomUI;
import com.ktc.tvremote.client.utils.MyUtils;
import com.ktc.tvremote.client.utils.WAPI;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class ClientUpgrade {

	private boolean bTips;
	private Context _context;
	private Thread _download_thread;
	
	private String _downloadApkUrl = null;
	private String _savePath = null;
	private String _saveFilename = null;
	private boolean _interceptFlag = false;
	private int _progress = 0;
	private ProgressBar _progressBar;
	
	private Dialog _downloadDialog;
	
	private ClientUpgradeCallback _callback = null;
	
	private final static int DOWN_UPDATE = 1;
	private final static int DOWN_OVER = 2;
	private final static int DOWN_ERROR = 10;
	
	public final static int STATE_ALREADY_NEW_VERSION = 1;
	public final static int STATE_CHECK_ERROR = 2;
	public final static int STATE_UPGRADE = 3;
	public static boolean bRun = false;
	
	public ClientUpgrade(Context context,boolean bTips)
	{
		_context = context;
		this.bTips = bTips;
	}
	
	public interface ClientUpgradeCallback
	{
		public void onCheckFinished(int state);
	}
	
	private void doCallback(int state)
	{
		if( _callback != null && bTips)
			_callback.onCheckFinished(state);
	}
	
	private class CheckVersionAsyncTask extends AsyncTask<String,Integer,String>
	{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{
//			Thread.sleep(5000);
			}
			catch(Exception e)
			{
				
			}
			return checkVersion();
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			int state = doParseResult(result);
			doCallback(state);
			
			if (bTips) {
				if(CustomUI.tipdialog!=null)
					CustomUI.tipdialog.dismiss();
			}
		}
		
		
		private int doParseResult(String result)
		{
			// 	UPGRADE|%s|%s|%s|%s", version, desc, downloadurl, force;

			if (result == null) {
				bRun = false;
				return STATE_CHECK_ERROR;
			}

			String[] ss = result.split("\\|");
			if (ss != null && ss.length > 0 && ss[0].equalsIgnoreCase("NOT")) {
				bRun = false;
				return STATE_ALREADY_NEW_VERSION;
			}

			Log.i("", "result=" + ss.length);
			if (ss == null || ss.length != 6) {
				bRun = false;
				return STATE_CHECK_ERROR;
			}

			boolean force = false;
			if (ss[4].equalsIgnoreCase("yes"))
				force = true;

			_downloadApkUrl = ss[3];
			String[] tt = _downloadApkUrl.split("/");
			if (tt.length < 2) {
				bRun = false;
				return STATE_CHECK_ERROR;
			}
			
			
			_savePath = "";
			_saveFilename = String.format("%s/%s",_context.getCacheDir(), tt[tt.length - 1]);
//			_saveFilename = String.format("%s/%s",_context.getFilesDir(), tt[tt.length - 1]);
//			_saveFilename = "/sdcard/tvmarket.apk";
			
//			String sDir = String.format("%s/updateapk/", _context.getFilesDir());
//			File destDir = new File(sDir);
//			if (!destDir.exists()) {
//				destDir.mkdirs();
//			}

			
//			String msg = ss[2];
//			if( msg.length() <= 0 )
//			{
//				msg = String.format("�°汾%s�����ˣ��Ͽ������ɣ�", ss[0]);
//			}

			String msg1 = _context.getResources().getString(R.string.upgrade_tips1);
			String msg2 = _context.getResources().getString(R.string.upgrade_tips2);
			String msg = String.format("%s%s%s",msg1,ss[5],msg2);
			
			
			Dialog dlg;
			
			if( force )
			{
				dlg = new AlertDialog.Builder(_context).setTitle(_context.getResources().getString(R.string.tips_title))
						.setMessage(msg)
						.setPositiveButton(_context.getResources().getString(R.string.tips_ok),new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								bRun = false;
								downloadApk();
							}
						})
						.create();
				
			}
			else
			{
				dlg = new AlertDialog.Builder(_context).setTitle(_context.getResources().getString(R.string.tips_title))
						.setMessage(msg)
						.setPositiveButton(_context.getResources().getString(R.string.tips_ok), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which) {
								bRun = false;
								downloadApk();
							}})
						.setNegativeButton(_context.getResources().getString(R.string.tips_cancel), new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								bRun = false;
							}})
						.create();
			}
			dlg.show();
			/*�������д��� ��֤����������������� ֻ�ܶ���������������
			Ŀ����Ϊ�˱�֤bRun��׼ȷ�� ����Ŀ����Ϊ�˷�ֹ��������������ʾ��*/
			dlg.setCanceledOnTouchOutside(false);
			dlg.setCancelable(false);//
			
			return STATE_UPGRADE;
		}		
	}
		
	public void startCheckVersion(ClientUpgradeCallback callback)
	{
		if (!bRun) {
			bRun = true;
		}
		else {
			return;
		}
		
		_callback = callback;

		if (bTips) {
			try {
				CustomUI.showtips(_context, R.string.market_tips_get_info);
			} catch (Exception e) {
			}
		}
		CheckVersionAsyncTask ct = new CheckVersionAsyncTask();
		ct.execute(null, null, null);
	}	
	
	private void downloadApk()
	{
		showDownloadDialog();
		
		_download_thread = new Thread(mDownloadRunnable);
		_download_thread.start();
	}
	
	private Runnable mDownloadRunnable = new Runnable() {
		@Override
		public void run() {
			try
			{
				URL url = new URL(_downloadApkUrl);
				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				

				if( _savePath.length() > 0 )
				{
					File file = new File(_savePath);
					if( !file.exists() )
						file.mkdirs();
				}
				
				String apkFilename = _saveFilename;
				File apkFile = new File(apkFilename);
				if( apkFile.exists() )
					apkFile.delete();
				
				FileOutputStream fos = new FileOutputStream(apkFile);
				
//	    		FileOutputStream fos = _context.openFileOutput(_saveFilename, Context.MODE_PRIVATE);
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{
					int numread = is.read(buf);
					count += numread;
					
					Log.i("", "download: " + count + " total: " + length);
					_progress = (int)(((float)count/length) * 100);
					
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if( numread <= 0 )
					{
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					
					fos.write(buf, 0, numread);
				}while( !_interceptFlag );
				
				fos.close();
				is.close();
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWN_ERROR);
			}
		}		
	};
	
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case DOWN_UPDATE:
				_progressBar.setProgress(_progress);
				_downloadDialog.setTitle(String.format("%s: %d%%",_context.getResources().getString(R.string.downloading),_progress));
				break;
			case DOWN_OVER:
				_progress = 100;
				_progressBar.setProgress(_progress);
				_downloadDialog.setTitle(String.format("%s: %d%%",_context.getResources().getString(R.string.downloading),_progress));
				_downloadDialog.dismiss();
//				MainActivity.getInstance().finish();
				installApk();
				break;
			case DOWN_ERROR:
				_downloadDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
	
	public void execCmd(String cmd)
	{
    	try {
    		Runtime.getRuntime().exec(cmd);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}                	
	}	
	private int installApk()
	{
		File file = new File(_saveFilename);
		if( !file.exists() )
			return 1;
		
    	execCmd("chmod 777 " + file.toString());		
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		
//		String s = String.format("%s/%s", _context.getFilesDir(), _saveFilename);
//		Log.d("", s);
//		intent.setDataAndType(Uri.parse("file://" + s), "application/vnd.android.package-archive");
		_context.startActivity(intent);
		
		return 0;
	}	
	
	private void showDownloadDialog()
	{
		AlertDialog.Builder builder = new Builder(_context);
		
		builder.setTitle(_context.getResources().getString(R.string.downloading));

		View ll = LayoutInflater.from(_context).inflate(R.layout.client_upgrade, null);
		_progressBar = (ProgressBar)ll.findViewById(R.id.progressBar1);
		builder.setView(ll);

		builder.setNegativeButton(_context.getResources().getString(R.string.tips_cancel), new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				_interceptFlag = true;
			}
			
		});		
		
		_downloadDialog = builder.create();
		_downloadDialog.show();
	}
	
	public static boolean hasNewVersion(String local_version_string, String server_version_string)
	{
		String[] local_version = local_version_string.split("\\.");
		String[] server_version = server_version_string.split("\\.");
		
//		Log.v(AppConfig.TAG, "local=" + local_version.length);
//		Log.v(AppConfig.TAG, "server=" + server_version.length);		
		
		for(int i = 0; i < local_version.length || i < server_version.length; i++ )
		{
			int cur_code, new_code;
			if( i >= local_version.length )
				cur_code = 0;
			else
				cur_code = Integer.parseInt(local_version[i]);
			
			if( i >= server_version.length )
				new_code = 0;
			else
				new_code = Integer.parseInt(server_version[i]);

			if( new_code > cur_code )
				return true;
			else if( new_code < cur_code )
				return false;
			
		}

		return false;
	}
		
	public String checkVersion()
	{
		try
		{
			String currentVersion = MyUtils.getVersionCode(_context);
			String urlString = WAPI.GetUpgradeURL();

			String content = WAPI.get_content_from_remote_url(urlString);
			if( content == null )
				return null;

			ArrayList<String> fieldList = new ArrayList<String>();
			int iret = WAPI.parseVersionInfoResponse(_context, content, fieldList);
			if( iret == 0 && fieldList.size() == 5)
			{
				String versioncode = fieldList.get(0);
				String desc = fieldList.get(1);
				String downloadurl = fieldList.get(2);
				String force = fieldList.get(3);
				String version = fieldList.get(4);
				
				if( hasNewVersion(currentVersion, versioncode) )
				{
					String result = String.format("UPGRADE|%s|%s|%s|%s|%s", versioncode, desc, downloadurl, force, version);
					return result;
				}
				else
				{
					return "NOT";
				}
			}
			
//			////-------------
//			String version = "3";
//			String desc = "�汾���£�";
//			String downloadurl = "http://gdown.baidu.com/data/wisegame/acf37139854a6f10/baidusuoping_16779017.apk";
//			String force = "yes";
//			if( hasNewVersion(currentVersion, version) )
//			{
//				String result = String.format("UPGRADE|%s|%s|%s|%s", version, desc, downloadurl, force);
//				return result;
//			}
//			else
//			{
//				return "NOT";
//			}
			
		}
		catch(Exception e)
		{
			
		}
		
		return null;
		
	}
}
