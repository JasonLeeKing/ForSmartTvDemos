package com.ktc.remote.client.adapter;

import java.util.List;

import com.example.mytabhost.R;
import com.ktc.remote.client.bean.DeviceInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NameListAdapter extends BaseAdapter {

	private Context context;
	private List<String> lists;
	public NameListAdapter(Context cxt,List<String> lists){
		context = cxt;
		this.lists = lists;
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
		// TODO Auto-generated method stub
		TextView tv;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.rename_item, null);
			}
			
		tv = (TextView) convertView.findViewById(R.id.rename);
		tv.setText(lists.get(position));
		return convertView;
	}

}
