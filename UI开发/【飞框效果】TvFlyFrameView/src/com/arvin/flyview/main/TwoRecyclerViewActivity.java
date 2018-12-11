package com.arvin.flyview.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.arvin.flyview.FlyFrameView;
import com.arvin.flyview.R;
import com.arvin.flyview.adapter.TwoRecyclerAdapter;
import com.arvin.flyview.widget.TvGridLayoutManagerScrolling;

public class TwoRecyclerViewActivity extends Activity {

    private FlyFrameView mFlyFrameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_recyclerview);

        mFlyFrameView = new FlyFrameView(this);
        mFlyFrameView.setBackgroundResource(R.drawable.border_red);
        testRecyclerViewLinerLayout();
        testRecyclerViewGridLayout();
    }


    private void testRecyclerViewLinerLayout() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.firstRecyclerView);
        // 创建一个线性布局管理器

        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        mFlyFrameView.attachTo(recyclerView);

        createData(recyclerView, R.layout.item_recycler_linerlayout);

    }

    private void testRecyclerViewGridLayout() {
        //test grid
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.secondRecyclerView);
        GridLayoutManager gridlayoutManager = new TvGridLayoutManagerScrolling(this, 4);
        gridlayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridlayoutManager);
        recyclerView.setFocusable(false);

        mFlyFrameView.attachTo(recyclerView);

        createData(recyclerView,R.layout.item_recycler_gridlayout);

    }


    private void createData(RecyclerView recyclerView,int id) {
        //创建数据集
        String[] dataset = new String[100];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }
        // 创建Adapter，并指定数据集
        TwoRecyclerAdapter adapter = new TwoRecyclerAdapter(this, dataset,id);
        // 设置Adapter
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }

}
