package com.arvin.flyview;

import com.arvin.flyview.main.TwoRecyclerViewActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;

public class TestActivity extends Activity implements View.OnClickListener {

    private RelativeLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //{{{use start
        FlyFrameView mFlyFrameView = new FlyFrameView(this);
        mFlyFrameView.setBackgroundResource(R.drawable.border_highlight);

        main = (RelativeLayout) findViewById(R.id.main);
        mFlyFrameView.attachTo(main);

        for (int i = 0; i < main.getChildCount(); i++) {
            main.getChildAt(i).setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					if(hasFocus){
						view.bringToFront();
					}
				}
			});
        }
        //use end}}}
        
        for (int i = 0; i < main.getChildCount(); i++) {
            main.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.textView1:
			startActivity(new Intent(TestActivity.this, TwoRecyclerViewActivity.class));
			break;

		default:
			break;
		}
    }


}
