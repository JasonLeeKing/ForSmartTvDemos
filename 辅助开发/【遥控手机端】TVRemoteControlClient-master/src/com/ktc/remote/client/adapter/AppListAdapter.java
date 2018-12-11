package com.ktc.remote.client.adapter;

import java.util.ArrayList;

import com.example.mytabhost.R;
import com.ktc.remote.client.bean.AppInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<AppInfo> applists;
	public AppListAdapter(Context cxt,ArrayList<AppInfo> applists){
		context = cxt;
		this.applists = applists;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return applists.size();
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
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.appmanage_item, null);
			holder.iv_icon =(ImageView) convertView.findViewById(R.id.appmanage_iv);
			holder.appname =(TextView) convertView.findViewById(R.id.appmanage_appname);
			holder.app_version =(TextView) convertView.findViewById(R.id.appmanage_version);
			convertView.setTag(holder);
			
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		AppInfo appinfo = applists.get(position);
		
		holder.iv_icon.setImageDrawable(appinfo.getIvIcon());
		holder.appname.setText(appinfo.getAppName());
		holder.app_version.setText(context.getResources().getString(R.string.version)+" "+appinfo.getAppVersion());
		
		return convertView;
	}
	
	class ViewHolder {
		private ImageView iv_icon;
		private TextView appname;
		private TextView app_version;
	}

}
