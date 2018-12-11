package com.ktc.remote.client.adapter;

import java.util.ArrayList;

import com.example.mytabhost.R;
import com.ktc.remote.client.bean.DownloadTaskInfo;
import com.ktc.remote.client.utils.ImageCache;
import com.ktc.remote.client.utils.ImageCallBack;
import com.ktc.remote.client.utils.ImageDownload;
import com.ktc.remote.client.utils.MyUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetDownloadListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DownloadTaskInfo> lists;
	private ImageCallBack icb;
	public SetDownloadListAdapter(Context cxt,ArrayList<DownloadTaskInfo> lists,ImageCallBack icb){
		context = cxt;
		this.lists = lists;
		this.icb = icb;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.set_download_task_item, null);
			holder.iv_icon = (ImageView) convertView
					.findViewById(R.id.download_task_ico);
			holder.appname = (TextView) convertView
					.findViewById(R.id.download_task_appname);
			holder.devname = (TextView) convertView
					.findViewById(R.id.download_task_device_name);
			holder.downloadstatus = (TextView) convertView
					.findViewById(R.id.download_task_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DownloadTaskInfo downloadInfo = lists.get(position);
		holder.appname.setText(downloadInfo.getAppname());
		holder.iv_icon.setTag(downloadInfo.getAppname());
		Bitmap bm = ImageCache.getInstance().get(downloadInfo.getAppname());
		String imgUrl = downloadInfo.getIcon();
		if (bm != null) {
			holder.iv_icon.setImageBitmap(bm);
		} else {
			holder.iv_icon.setImageResource(R.drawable.defualt);
			if (icb != null) {
				new ImageDownload(icb).execute(imgUrl, downloadInfo.getAppname());
			}
		}

		holder.devname.setText(context.getResources().getString(R.string.task_status_download_device) + ":" + downloadInfo.getDevname());
		holder.downloadstatus.setText(MyUtils.GetDownloadStatusText(context,downloadInfo.getStatus()));
		return convertView;
	}

	class ViewHolder{
		private ImageView iv_icon;
		private TextView appname;
		private TextView devname;
		private TextView downloadstatus;
	}	
}
