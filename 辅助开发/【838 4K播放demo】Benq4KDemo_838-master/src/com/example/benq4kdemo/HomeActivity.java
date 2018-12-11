package com.example.benq4kdemo;

import com.example.benq4kdemo.photo2.ImagePlayerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class HomeActivity extends Activity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_small_win:
			startActivity("com.example.benq4kdemo.tv.SmallWinActivity");
			break;
		case R.id.home_4k_photo:
			Intent mIntent = new Intent(this , ImagePlayerActivity.class);
	    	startActivity(mIntent);	
			break;
		case R.id.home_4k_video:
			startActivity("com.example.benq4kdemo.video.Video4KActivity");
			break;
		default:
			break;
		}
	}
	
	private void startActivity(String action){
    	Intent mIntent = new Intent(action);
    	mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivityForResult(mIntent, 0);
    }

}
