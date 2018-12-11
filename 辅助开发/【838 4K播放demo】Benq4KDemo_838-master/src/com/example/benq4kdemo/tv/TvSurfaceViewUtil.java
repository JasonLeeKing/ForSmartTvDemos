package com.example.benq4kdemo.tv;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

public class TvSurfaceViewUtil {
	private String TAG = TvSurfaceViewUtil.class.getName();
	static private TvSurfaceViewUtil mTvSurfaceViewUtil;
    private android.view.SurfaceHolder.Callback callback;
	public static final String STR_STATUS_SUSPENDING = "1";
	private int panel_width;
	private int panel_height;
	
	protected TvSurfaceViewUtil() {
        try {
            panel_width = TvManager.getInstance().getPictureManager().getPanelWidthHeight().width;
            panel_height = TvManager.getInstance().getPictureManager().getPanelWidthHeight().height;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
	}
	
	public int getPanelW(){
		return panel_width ;
	}
	
	public int getPanelH(){
		return panel_height ;
	}
	
	/**
	*TODO 获取TvSurfaceViewUtil实例对象
	*@param null
	*@return void
	*@exception  
	*/
	static public TvSurfaceViewUtil getInstance(){
		if(mTvSurfaceViewUtil == null){
			mTvSurfaceViewUtil = new TvSurfaceViewUtil();
		}
		return mTvSurfaceViewUtil;
	}
	
	/**
	*TODO 初始化SurfaceView的回调方法
	*@param SurfaceView , Handler ,Runnable
	*@return void
	*@exception  
	*/
	public void initSurfaceView(SurfaceView surfaceView) {
        final SurfaceHolder mSurfaceHolder = surfaceView.getHolder();
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (holder == null || holder.getSurface() == null
                            || holder.getSurface().isValid() == false)
                        return;
                    if (TvManager.getInstance() != null) {
                        TvManager.getInstance().getPlayerManager().setDisplay(mSurfaceHolder);
                    }
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        };
        mSurfaceHolder.addCallback((SurfaceHolder.Callback) callback);
    }
	
	 /**
	*TODO 移除消息队列中的线程
	*@param SurfaceView
	*@return void
	*@exception  
	*/
	public void RemoveCallback(SurfaceView surfaceView){
		 if (surfaceView != null) {
	            surfaceView.getHolder().removeCallback((SurfaceHolder.Callback) callback);
	            surfaceView = null;
	     }
	 }
	 
	 
	 /**
	*TODO 缩放TV小窗口
	*@param Context
	*@return void
	*@exception  
	*/
	public void setSmallScale(int mX , int mY , int mWidth , int mHeight) {
		try {
            VideoWindowType videoWindowType = new VideoWindowType();
        	videoWindowType.x = mX;
            videoWindowType.y = mY;
            videoWindowType.width = mWidth;
            videoWindowType.height = mHeight;
            
            TvManager mTvManager = TvManager.getInstance() ;
            if (mTvManager != null) {
            	PictureManager mPictureManager = mTvManager.getPictureManager();
                if (mPictureManager != null) {
                	mPictureManager.selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                	mPictureManager.setDisplayWindow(videoWindowType);
                	mPictureManager.scaleWindow();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	 
	
    /**
    *TODO 放大TV窗口为全屏显示
    *@param null
    *@return void
    *@exception  
    */
    public void setFullScale() {
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            videoWindowType.height = 0xffff;
            videoWindowType.width = 0xffff;
            videoWindowType.x = 0xffff;
            videoWindowType.y = 0xffff;
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager()
                        .selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }
	    
}
