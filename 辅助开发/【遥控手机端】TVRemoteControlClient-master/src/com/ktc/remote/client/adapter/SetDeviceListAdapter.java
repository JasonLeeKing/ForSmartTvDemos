package com.ktc.remote.client.adapter;

import java.util.ArrayList;

import com.example.mytabhost.R;
import com.ktc.remote.client.bean.DeviceInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetDeviceListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DeviceInfo>devicelists;
	
	public SetDeviceListAdapter(Context cxt,ArrayList<DeviceInfo> lists){
		context = cxt;
		devicelists = lists;
	}
	
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return devicelists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {

		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// TODO Auto-generated method stub
		DeviceInfo devinfo = devicelists.get(position);
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,R.layout.set_device_list_item, null);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.item_name = (TextView) convertView.findViewById(R.id.list_item_text);
		holder.item_name.setText(devinfo.getDevicename());

		return convertView;
	}
	
	class ViewHolder {
		private TextView item_name;
	}
}
