package com.example.benq4kdemo.tv;

import com.example.benq4kdemo.R;
import com.example.benq4kdemo.utils.Constants;
import com.mstar.android.tv.TvCommonManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;


public class SmallWinActivity extends Activity{

    private RelativeLayout tvSurfaceViewLayout;
    private SeekBar seekbar_x ,seekbar_y , seekbar_w , seekbar_h;
    private Button ok ;
    
  	private SurfaceView tvSurfaceView = null;
  	private TvSurfaceViewUtil mTvUtil ;
    private TvCommonManager tvCommonManager;
    
    //MSG
    private static final int MSG_WIN_SCALE_FULL = 0x01;
    private static final int MSG_WIN_SCALE_SMALL = 0x02;
    private static final int MSG_INIT_INPUTSOURCE = 0x03;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smallwin);
        
        initView();
    }

    @Override
	public void onResume() {
		super.onResume();
        handlerForTv.sendEmptyMessage(MSG_INIT_INPUTSOURCE);
    }
	
    private void initView() {
    	tvSurfaceViewLayout = (RelativeLayout) findViewById(R.id.tv_win);
    	seekbar_x = (SeekBar) findViewById(R.id.seekbar_x);
    	seekbar_y = (SeekBar) findViewById(R.id.seekbar_y);
    	seekbar_w = (SeekBar) findViewById(R.id.seekbar_w);
    	seekbar_h = (SeekBar) findViewById(R.id.seekbar_h);
    	ok = (Button) findViewById(R.id.ok);
    	
    	ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int x = seekbar_x.getProgress()*(mTvUtil.getPanelW()/100);
				int y = seekbar_y.getProgress()*(mTvUtil.getPanelH()/100);
				int w = seekbar_w.getProgress()*(mTvUtil.getPanelW()/100);
				int h = seekbar_h.getProgress()*(mTvUtil.getPanelH()/100);
				mTvUtil.setSmallScale(x , y , w , h);
			}
		});
    	
        mTvUtil = TvSurfaceViewUtil.getInstance();
		tvCommonManager = TvCommonManager.getInstance();
        handlerForTv.sendEmptyMessageDelayed(MSG_INIT_INPUTSOURCE, 1000);

        if (tvSurfaceView == null) {
            handlerForTv.postDelayed(handlerCreateTv, 500);
        }
    }
    
	Handler handlerForTv = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_WIN_SCALE_SMALL:
				break;
			case MSG_WIN_SCALE_FULL:
				break;
			case MSG_INIT_INPUTSOURCE:
				forceToSetCurSource();
				break;
			default:
				break;
			}
		}
	};
	
    Runnable handlerCreateTv = new Runnable() {

        @Override
        public void run() {
            try {
            	if(tvSurfaceView == null){
            		tvSurfaceView = new SurfaceView(SmallWinActivity.this);
                    tvSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    tvSurfaceViewLayout.addView(tvSurfaceView);
                    tvSurfaceView.setBackgroundColor(Color.TRANSPARENT);
            	}
            	mTvUtil.initSurfaceView(tvSurfaceView);
            	//初始化小窗口位置及尺寸
            	mTvUtil.setSmallScale(0 , 0 , mTvUtil.getPanelW() , mTvUtil.getPanelH());
            } catch (Exception e) {
                e.printStackTrace();
            }
            handlerForTv.removeCallbacks(handlerCreateTv);
        }
    };
    
    /**
	*TODO HomeTv页面可见时强制切回原信源
	*@param int
	*/
    private void forceToSetCurSource(){
 	    int currentTvSource = TvCommonManager.INPUT_SOURCE_NONE;
 		currentTvSource = TvCommonManager.getInstance().getCurrentTvInputSource();
 		if(currentTvSource == TvCommonManager.INPUT_SOURCE_STORAGE || currentTvSource == TvCommonManager.INPUT_SOURCE_NONE){
            currentTvSource = getPreInputSource() != currentTvSource ? getPreInputSource() : TvCommonManager.INPUT_SOURCE_ATV;
// 		   tvCommonManager.setInputSource(currentTvSource);
            tvCommonManager.setInputSource(TvCommonManager.INPUT_SOURCE_HDMI2);
 		}
 		handlerForTv.removeMessages(MSG_INIT_INPUTSOURCE);
 	}

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
	public void onDestroy() {
		if (tvSurfaceView != null) {
			mTvUtil.RemoveCallback(tvSurfaceView);
        }
		super.onDestroy();
	}
	
    private int getPreInputSource() {
        Context context = null;
        try {
            context = createPackageContext(Constants.TVPLAYER_PKG_NAME, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFERENCES_INPUT_SOURCE,
                Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE  + Context.MODE_MULTI_PROCESS);
        return settings.getInt(Constants.PREFERENCES_PREVIOUS_INPUT_SOURCE, TvCommonManager.INPUT_SOURCE_HDMI);
    }

}
