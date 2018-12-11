package com.ktc.tvremote.client.adapter;

import java.util.ArrayList;
import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.bean.ButtonInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ButtonInfo> buttonlists;
	
	public SetListAdapter(Context cxt,ArrayList<ButtonInfo> lists){
		context = cxt;
		buttonlists = lists;
	}
	
	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return buttonlists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO �Զ����ɵķ������
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO �Զ����ɵķ������
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {

		ButtonInfo btninfo = buttonlists.get(position);
		if (btninfo.getBtnType() == ButtonInfo.BTN_TYPE_TITLE)
			return false;

		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// TODO Auto-generated method stub
		ButtonInfo btninfo = buttonlists.get(position);
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,R.layout.set_list_item, null);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.btn_name = (TextView) convertView
				.findViewById(R.id.group_list_item_text);
		holder.btn_arrow = (ImageView) convertView
				.findViewById(R.id.group_list_item_rightarrow);
		holder.btn_underline = (ImageView) convertView
				.findViewById(R.id.group_list_item_underline);
		holder.btn_name.setText(btninfo.getBtnName());
		if (btninfo.getBtnType() == ButtonInfo.BTN_TYPE_NORMAL) {
			holder.btn_arrow.setVisibility(View.GONE);
			holder.btn_underline.setVisibility(View.VISIBLE);

			holder.btn_name.setTextSize(20);
			holder.btn_name.setTextColor(context.getResources().getColor(
					R.color.set_item_text_color));
			holder.btn_name.setPadding(30, 0, 0, 0);

		} else if (btninfo.getBtnType() == ButtonInfo.BTN_TYPE_AROOW) {
			holder.btn_arrow.setVisibility(View.VISIBLE);
			holder.btn_underline.setVisibility(View.VISIBLE);

			holder.btn_name.setTextSize(20);
			holder.btn_name.setTextColor(context.getResources().getColor(
					R.color.set_item_text_color));
			holder.btn_name.setPadding(30, 0, 0, 0);
		} else if (btninfo.getBtnType() == ButtonInfo.BTN_TYPE_TITLE) {
			holder.btn_arrow.setVisibility(View.GONE);
			holder.btn_underline.setVisibility(View.GONE);

			holder.btn_name.setTextSize(16);
			holder.btn_name.setTextColor(context.getResources().getColor(
					R.color.set_title_text_color));
			holder.btn_name.setPadding(10, 0, 0, 0);
		}

		return convertView;
	}
	
	class ViewHolder {
		private TextView btn_name;
		private ImageView btn_arrow;
		private ImageView btn_underline;
	}
}
