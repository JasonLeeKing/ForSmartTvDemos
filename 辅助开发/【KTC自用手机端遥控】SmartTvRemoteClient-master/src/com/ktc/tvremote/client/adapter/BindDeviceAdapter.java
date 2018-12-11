package com.ktc.tvremote.client.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.bean.DeviceInfo;
import com.ktc.tvremote.client.bean.GlobalParams;
import com.ktc.tvremote.client.utils.WAPI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BindDeviceAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DeviceInfo> lists;
	public BindDeviceAdapter(Context cxt,ArrayList<DeviceInfo> devicelists){
		context = cxt;
		this.lists = devicelists;
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
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    View view = inflater.inflate(R.layout.bind_device_item, null); 
		tv = (TextView) view.findViewById(R.id.bind_device_name);
		tv.setText(lists.get(position).getDevicename());
		
		return view;
	}
}