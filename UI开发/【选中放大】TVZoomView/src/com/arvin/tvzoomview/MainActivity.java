package com.arvin.tvzoomview;

import com.arvin.tvzoomview.R;
import com.custom.tvzoomview.TvZoomLayout;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
	
	private TvZoomLayout layout1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        layout1 = (TvZoomLayout)findViewById(R.id.layout1);
        layout1.requestFocus();
    }
    
}
