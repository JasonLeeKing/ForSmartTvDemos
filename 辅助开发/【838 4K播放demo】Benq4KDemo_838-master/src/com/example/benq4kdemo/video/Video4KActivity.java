package com.example.benq4kdemo.video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.benq4kdemo.R;
import com.example.benq4kdemo.utils.Tools;
import com.mstar.android.media.MMediaPlayer;
import com.mstar.android.storage.MStorageManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class Video4KActivity extends Activity implements OnClickListener{

	private final static String TAG = "yzh" ;
	
	private RelativeLayout video_ly ;
	private VideoPlayView mVideoPlayView ;
	private SeekBar seekbar_left ,seekbar_top , seekbar_right , seekbar_bottom;
	
	private int mVideoWidth;
    private int mVideoHeight;
    
    private int panel_width;
	private int panel_height;
	
	private String Benq_4K_VIDEOS ;
    private List<String> listDatas = new ArrayList<String>();
    private int index ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
    }

    @Override
	public void onResume() {
		super.onResume();
    }
	
    private void initView() {
    	video_ly = (RelativeLayout) findViewById(R.id.video_ly);
    	mVideoPlayView = (VideoPlayView) findViewById(R.id.videoviewone);
    	seekbar_left = (SeekBar) findViewById(R.id.seekbar_left);
    	seekbar_top = (SeekBar) findViewById(R.id.seekbar_top);
    	seekbar_right = (SeekBar) findViewById(R.id.seekbar_right);
    	seekbar_bottom = (SeekBar) findViewById(R.id.seekbar_bottom);
    	
        try {
			panel_width = TvManager.getInstance().getPictureManager().getPanelWidthHeight().width;
			panel_height = TvManager.getInstance().getPictureManager().getPanelWidthHeight().height;
		} catch (TvCommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.i(TAG, "panel_width:  "+panel_width+"\npanel_height:   "+panel_height);
    	
    	MStorageManager storageManager = MStorageManager.getInstance(this);
 		String[] volumes = storageManager.getVolumePaths();
 		if(volumes != null && volumes.length > 1 ){
 			Benq_4K_VIDEOS = volumes[1]+"/Benq_4K_VIDEOS";
 			File dirFile = new File(Benq_4K_VIDEOS);
 			if(dirFile.listFiles().length > 0){
 				listDatas.clear();
 				for(File mFile : dirFile.listFiles()){
 	 				listDatas.add(mFile.getAbsolutePath());
 	 				Log.i(TAG, "mFile:  "+mFile.getName());
 	 			}
 			}
 		}
 		
 		if(listDatas.size() > 0){
 			InitVideoPlayer(listDatas.get(index));
 		}else{
 			finish();
 		}
    }
    
    public boolean isVideoSize_4K(int viewId) {
        if (mVideoPlayView != null) {
            if (mVideoPlayView.is4kVideo()) {
                Log.i(TAG, "viewId:" + viewId + "is4KVideo");
                return true;
            }
        }
        return false;
    }
    
 // Video processing related handler
    public Handler videoHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
        };
    };
    
    private void InitVideoPlayer(String videoPlayPath) {
       // Setting error of the monitor
       if (mVideoPlayView != null) {
           if (!Tools.isVideoStreamlessModeOn()) {
               mVideoPlayView.stopPlayback();
           }

           mVideoPlayView.setPlayerCallbackListener(myPlayerCallback);
       }

       Log.i(TAG, "*******videoPlayPath*****" + videoPlayPath + "SDK_INT: " + Build.VERSION.SDK_INT);
       videoPlayPath = Tools.fixPath(videoPlayPath);

       
       if (videoPlayPath != null) {
           mVideoPlayView.setVideoPath(videoPlayPath);
       }
       mVideoPlayView.setHandler(videoHandler);

   }
    
    private void doMoveToNextOrPrevios(boolean toNext){

        if (listDatas.size() == 0) {
            return;
        }

        if(toNext){
        	index = index + 1 ;
        }else{
        	index = index - 1 ;
        }
        
        if(index >= listDatas.size()){
        	index = 0 ;
        }else if(index < 0){
        	index = listDatas.size() - 1 ;
        }
        Log.i(TAG, "moveToNextOrPrevious()  video_position: " + index);
        InitVideoPlayer(listDatas.get(index));
    }

    public VideoPlayView.playerCallback myPlayerCallback = new VideoPlayView.playerCallback() {
		@Override
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            String strMessage = "";
            switch (framework_err) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    break;
                default:
                    break;
            }
            mVideoPlayView.stopPlayback();
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
        	doMoveToNextOrPrevios(true);
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "*******onInfo******" + what + " getVideoWidth:" + mp.getVideoWidth() + " getVideoHeight:" + mp.getVideoHeight());

            switch (what) {
                case MMediaPlayer.MEDIA_INFO_SUBTITLE_UPDATA:
                    break;
                case MMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    return true;
                case MMediaPlayer.MEDIA_INFO_AUDIO_UNSUPPORT:
                    break;
                case MMediaPlayer.MEDIA_INFO_VIDEO_UNSUPPORT:
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    break;
                case MMediaPlayer.MEDIA_INFO_TRICK_PLAY_COMPLETE:
                    break;
                default:
                    Log.i(TAG, "Play onInfo::: default onInfo!");
                    break;
            }
            return false;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
        }

        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.i(TAG, "onVideoSizeChanged width:" + width + " height:" + height );
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            Log.i(TAG, "onPrepared mVideoWidth:" + mVideoWidth + " mVideoHeight:" + mVideoHeight);
            mVideoPlayView.start();
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
        }

        @Override
        public void onCloseMusic() {
        }

        @Override
        public void onUpdateSubtitle(String sub) {
        }
    };
    
    private void exitPlayer() {
        if (mVideoPlayView != null) {
        	mVideoPlayView.stopPlayer();
        	mVideoPlayView.setPlayerCallbackListener(null);
        }
        //reset roate
        Tools.setRotateMode("false");
		Tools.setRotateDegrees("0");
    }
    
    public void setVideoDisplayFullScreen() {
        Log.i(TAG, "---setVideoDisplayFullScreen--");
        RelativeLayout.LayoutParams params = null;
        params = new RelativeLayout.LayoutParams(panel_width , panel_height);
        video_ly.setLayoutParams(params);
    }
    
    public void updateVideoDisplay(int left , int top , int right , int bottom) {
        Log.i(TAG, "---setVideoDisplayFullScreen--");
        RelativeLayout.LayoutParams params = null;
        params = new RelativeLayout.LayoutParams(panel_width , panel_height);
        params.setMargins(left, top , right, bottom);
        video_ly.setLayoutParams(params);
    }
    
    /* This function is for initialize video display aspect ratio before mediaplayer initialize.*/
    public void setVideoDisplayRotate90() {
        float ratio = (float)panel_height/panel_width;
        RelativeLayout.LayoutParams params = null;
        int videoHeight = panel_height;
        int videoWidth = panel_height * panel_height / panel_width;
        Log.i(TAG, "--- setVideoDisplayRotate90--- mScreenResolutionWidth:" + panel_width + " screenHeight:"
                + panel_height + " ratio:" + ratio + " videoWidth:" + videoWidth);
        params = new RelativeLayout.LayoutParams(videoWidth , videoHeight);
        video_ly.setLayoutParams(params);
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.pre:
				doMoveToNextOrPrevios(false);
				break;
			case R.id.next:
				doMoveToNextOrPrevios(true);
				break;
			case R.id.roate_0:
				Tools.setRotateMode("true");
				Tools.setRotateDegrees("0");
				InitVideoPlayer(listDatas.get(index));
				break;
			case R.id.roate_90:
				Tools.setRotateMode("true");
				Tools.setRotateDegrees("90");
				InitVideoPlayer(listDatas.get(index));
				break;
			case R.id.roate_180:
				Tools.setRotateMode("true");
				Tools.setRotateDegrees("180");
				InitVideoPlayer(listDatas.get(index));
				break;
			case R.id.roate_270:
				Tools.setRotateMode("true");
				Tools.setRotateDegrees("270");
				InitVideoPlayer(listDatas.get(index));
				break;
			case R.id.ok:
				int left = seekbar_left.getProgress()/2*(panel_width/100);
				int top = seekbar_top.getProgress()/2*(panel_height/100);
				int right = seekbar_right.getProgress()/2*(panel_width/100);
				int bottom = seekbar_bottom.getProgress()/2*(panel_height/100);
				updateVideoDisplay(left , top , right , bottom);
				break;
			default:
				break;
		}
	}
    

	@Override
    public void onPause() {
        super.onPause();
    }
	
	@Override
    protected void onStop() {
		exitPlayer();
        super.onStop();
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
	}

}
