package com.arivin.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.Random;

import com.arvin.hlistview.HListView;
import com.arvin.tvhlistview.R;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        HListView listView = (HListView) findViewById(R.id.list_view);
        listView.setAdapter(new TestAdapter(this));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "onItemClick  " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "onItemLongClick" + position, Toast.LENGTH_SHORT).show();
        return true;
    }

    private static class TestAdapter extends BaseAdapter {

        private final Context mContext;

        public TestAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_item, parent, false);
                convertView.setBackgroundColor(0xff << 24 | new Random().nextInt(0xffffff));
            }
            return convertView;
        }
    }
}
