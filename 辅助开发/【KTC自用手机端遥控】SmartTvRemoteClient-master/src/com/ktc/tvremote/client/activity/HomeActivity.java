package com.ktc.tvremote.client.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.ktc.tvremote.client.holder.HomeHolder;
import com.ktc.tvremote.client.R;

/**
 * @TODO 遥控客户端主入口
 * @author Arvin
 * @since 2018.6.5
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener {

    private HomeHolder mHolder ;
    private long exitTime = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        mHolder = new HomeHolder(this);
    }
    
    @Override
    public void onBackPressed() {
		ExitApp();
    }
	
    private void ExitApp(){
        if ((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(this, getResources().getString(R.string.exit_apllication), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
        } else{
                this.finish();
        }
    }
    
	@Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.top_bar_left:
            	if(!mHolder.superSlidingPaneLayout.isOpen()){
            		mHolder.superSlidingPaneLayout.openPane();
                }else{
                	mHolder.superSlidingPaneLayout.closePane();
                }
                break;
            case R.id.slide_menu_splash:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_filemanager:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_appmanager:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_media:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_screencap:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_laboratory:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_feedback:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_about:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
            case R.id.slide_menu_settings:
            	mHolder.superSlidingPaneLayout.closePane();
                break;
                
			default:
				break;
        }
    }
    
}
