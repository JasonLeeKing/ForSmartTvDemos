package com.example.benq4kdemo.photo2;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;
import android.util.Log;
import android.net.Uri;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.DisplayMetrics;
import com.example.benq4kdemo.R;
import com.example.benq4kdemo.utils.Constants;
import com.example.benq4kdemo.utils.ToastFactory;
import com.example.benq4kdemo.utils.Tools;

import java.io.Closeable;
import java.io.InputStream;
import android.os.SystemProperties;
import android.os.Handler;
import android.os.Message;

import com.mstar.android.media.MMediaPlayer;

public class ImagePlayerSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final String TAG = "ImagePlayerSurfaceView";
    private static final int MEDIA_PLAYER_STATE_IDLE = 0;
    private static final int MEDIA_PLAYER_STATE_PREPARING = MEDIA_PLAYER_STATE_IDLE + 1;
    private static final int MEDIA_PLAYER_STATE_PREPARED = MEDIA_PLAYER_STATE_IDLE + 2;
    private static final int MEDIA_PLAYER_STATE_STARTED = MEDIA_PLAYER_STATE_IDLE + 3;
    private static final int MEDIA_PLAYER_STATE_ERROR = MEDIA_PLAYER_STATE_IDLE + 4;
    private int mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_IDLE;
    private Bitmap bitmap;
    private InputStream is;
    private boolean isStop = false;
    private int mDelta = 1;
    private ImagePlayerActivity mImagePlayerActivity;
    private MMediaPlayer mMMediaPlayer=null;
    //private boolean mNextFilePrepared = false;
    //private boolean mPrevFilePrepared = false;
    private int mPrevPrepareState = MEDIA_PLAYER_STATE_IDLE;
    private int mNextPrepareState = MEDIA_PLAYER_STATE_IDLE;
    private String sPath = "";
    private String imgPath = null;
    private SurfaceHolder sfholder=null;
    private int mSurfaceWidth = 1920;
    private int mSurfaceHeight = 1080;
    private int mPanelWidth;
    private int mPanelHeight;
    private float imgDecodedWidth;
    private float imgDecodedHeight;
    private int dstWidthAfterScale;
    private int dstHeightAfterScale;
    private int cropStartX = 0;
    private int cropStartY = 0;
    private Thread updateTimer;
	protected int sampleSize;
	protected double scaleFactor;
	protected int imgOriginalWidth;
	protected int imgOriginalHeight;
	private double initialSize;
	protected double scales;
	private float needfRatio =1.0f;
	private boolean cansacles;
    private android.graphics.Rect dst = new android.graphics.Rect();
    private Thread mStartImagePlayerThread;

    // if < 0, means preparing previous photo,
    // else if > 0, means preparing next photo.
    // else if = 0. means not preparing photo.
    private int mPrepareDelta = 0;
    private Handler mHandler = null;

    public ImagePlayerSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        getContext();
    }

    public ImagePlayerSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        getHolder().addCallback(this);
    }

    public ImagePlayerSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mSurfaceHeight = height;
        mSurfaceWidth = width;
        if (SystemProperties.getInt("mstar.4k2k.photo", 0) != 1) {
            if ((mPanelHeight > mSurfaceHeight) || (mPanelWidth > mSurfaceWidth)) {
                mSurfaceHeight = mPanelHeight;
                mSurfaceWidth = mPanelWidth;
            }
            if (mSurfaceWidth < 1920) {
                setSurfaceSize();
            }
        }

        Log.i(TAG, "surfaceChanged--mSurfaceWidth:"+mSurfaceWidth+"---mSurfaceHeight:"+mSurfaceHeight);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sfholder = holder;
        if (SystemProperties.getInt("mstar.4k2k.photo", 0) != 1) {
            adjustSurfaceSize();
        }

        Log.i(TAG, "surfaceCreated--mSurfaceWidth:"+mSurfaceWidth+"---mSurfaceHeight:"+mSurfaceHeight);
        if(!sPath.equals(""))
            openImagePlayer();
        if (bitmap != null) {
            drawImage();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "********surfaceDestroyed******");
        /* Because SN would set desk-display-mode, so AN APK don't need do this again.
        if (SystemProperties.getInt("mstar.desk-display-mode", 0) != 0) {
            SystemProperties.set("mstar.desk-display-mode", "0");
        }
        */
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        stopPlayback(true);
        sfholder = null;
    }

    public void setImagePath(String imagePath,ImagePlayerActivity ppa) {
        sPath = imagePath;
        mImagePlayerActivity = ppa;
        Log.i(TAG,"the photo path is:"+sPath);
    }


    public void resetMediaPlayer() {
        Log.i(TAG, "resetMediaPlayer mMMediaPlayer:" + mMMediaPlayer + " mCurrentMediaPlayerState:" + mCurrentMediaPlayerState);
            if (mMMediaPlayer != null) {
                try {
                    mMMediaPlayer.reset();
                } catch (Exception ex) {
                    Log.e(TAG, "Exception:" + ex);
                }
                mMMediaPlayer = null;
                mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_IDLE;
            }
    }

    public  void stopPlayback(boolean bActivityExit) {
        Log.i(TAG, "------stopPlayback ------- bActivityExit:" + bActivityExit);
        isStop = true;
        if (mCurrentMediaPlayerState == MEDIA_PLAYER_STATE_IDLE ||
            mCurrentMediaPlayerState == MEDIA_PLAYER_STATE_PREPARING
            ||Constants.bReleasingPlayer
            ) {
            return;
        }
        synchronized(this) {
            if (bActivityExit) {
                // When abnormal stop play.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "mMMediaPlayer:" + mMMediaPlayer + " mCurrentMediaPlayerState:" + mCurrentMediaPlayerState);
                        if (mMMediaPlayer != null ) {

                            Constants.bReleasingPlayer = true;
                            /*
                            while (mCurrentMediaPlayerState < MEDIA_PLAYER_STATE_STARTED) {
                                try {
                                    Log.w(TAG,"player state is not prepared,wait 0.6 second!");
                                    Thread.sleep(600);
                                } catch (InterruptedException e) {
                                 Log.e(TAG,"thread interrupt exception");
                                }
                            }*/
                            try {
                                 if ((mMMediaPlayer !=null) && mMMediaPlayer.isPlaying()) {
                                     if (mMMediaPlayer !=null) {
                                        Log.i(TAG, "*****stop start*****");
                                        mMMediaPlayer.stop();
                                        Log.i(TAG, "*****stop end*****");
                                     }
                                     if (mMMediaPlayer !=null) {
                                        Log.i(TAG, "*****release start*****");
                                        mMMediaPlayer.release();
                                        Log.i(TAG, "*****release end*****");
                                     }
                                     mMMediaPlayer = null;
                                 }
                                 Constants.bReleasingPlayer = false;
                            } catch (IllegalStateException ex) {
                                 Log.e(TAG, "IllegalStateException");
                            }
                        }
                    }
                }).start();
            } else {
                // call before play next.
                if (mMMediaPlayer != null ) {
                    Log.i(TAG, "mMMediaPlayer.stop()");
                    mMMediaPlayer.stop();
                    Log.i(TAG, "mMMediaPlayer.stop() end");
                    Log.i(TAG, "mMMediaPlayer.release()");
                    mMMediaPlayer.release();
                    Log.i(TAG, "mMMediaPlayer.release() end");
                    Log.i(TAG, "mMMediaPlayer= null");
                    mMMediaPlayer = null;
                    mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_IDLE;
                }
            }
        }
    }

    public void setStop() {
        isStop = true;
    }
    protected float getscales(){
	   	
		return needfRatio;
    }
    protected void setscales(){
	
             needfRatio=1.0f;
    }

    public boolean startNextVideo(String sPath, ImagePlayerActivity ppa) {
        Log.i(TAG, "startNextVideo  sPath:"+sPath+"  mImagePlayerActivity:"+mImagePlayerActivity+ " mCurrentMediaPlayerState:" + mCurrentMediaPlayerState);
        if (mCurrentMediaPlayerState == MEDIA_PLAYER_STATE_PREPARED) {
            return false;
        }
        setImagePath(sPath,ppa);
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        drawImage();
        stopPlayback(false);
        mImagePlayerActivity.startShowProgress();
        openImagePlayer();
        return true;
    }

    private void setDstImageSize(float fAngle,float fRatio) {
        Log.i(TAG, "setDstImageSize -------------- begin " + "fAngle:" + fAngle + " fRatio:" + fRatio);
        double radian = fAngle * Math.PI / 180;
        int dstWidth = (int)((int)imgDecodedWidth * fRatio);
        int dstHeight = (int)((int)imgDecodedHeight * fRatio);
        dstWidthAfterScale = (int)(dstWidth * Math.abs(Math.cos(radian)) +
                dstHeight * Math.abs(Math.sin(radian))) - 1;
        dstHeightAfterScale = (int)(dstHeight * Math.abs(Math.cos(radian)) +
                dstWidth * Math.abs(Math.sin(radian))) - 1;
        if (dstHeightAfterScale>mSurfaceHeight) {
            cropStartY = (int)(dstHeightAfterScale-mSurfaceHeight)/2;
        } else {
            cropStartY = 0;
        }
        if (dstWidthAfterScale>mSurfaceWidth) {
            cropStartX = (int)(dstWidthAfterScale-mSurfaceWidth)/2;
        } else {
            cropStartX = 0;
        }
        Log.i(TAG, "after setDstImageSize cropStartY:" + cropStartY + " cropStartX:" + cropStartX);
        Log.i(TAG, "After setDstImageSize dstWidthAfterScale:"+dstWidthAfterScale+" dstHeightAfterScale:"+dstHeightAfterScale);
    }

    protected void rotateImage(float fAngle,float fRatio) {
    	     fRatio = 1;
    	     resetscale(true);
			  
        if (mMMediaPlayer != null) {
            if ((fRatio == 1.0f) && (fAngle == 90 || fAngle == 270 || fAngle == -90 || fAngle == -270)) {
                int tmpWidth =  (int)(imgDecodedWidth + 1.5);
                if (tmpWidth > mSurfaceWidth) {
                    fRatio = Math.min((float)mSurfaceHeight/(float)imgDecodedWidth,(float)mSurfaceWidth/(float)imgDecodedHeight);
                }
                  if(cansacles){
		    
		                  fRatio=(float)initialSize;
		                }
  
            }
			  needfRatio=fRatio;
            if ((dstWidthAfterScale > 3840) && (dstHeightAfterScale > 2160)) {
                // Tools.setVideoMute(true, 50);
                mMMediaPlayer.ImageRotateAndScale(fAngle, fRatio,fRatio,true);
                // Tools.setVideoMute(false, 0);
            } else {
                // Tools.setVideoMute(true, 50);
                mMMediaPlayer.ImageRotateAndScale(fAngle, fRatio,fRatio,false);
                setDstImageSize(fAngle, fRatio);
                // Tools.setVideoMute(false, 0);
            }
        }
    }

    protected void scaleImage(float fAngle,float fRatio){
        if(mMMediaPlayer != null){
            // Tools.setVideoMute(true, 50);
            mMMediaPlayer.ImageRotateAndScale(fAngle, fRatio,fRatio,true);
            // Tools.setVideoMute(false, 0);
            setDstImageSize(fAngle, fRatio);
        }
    }

    // if panRight > 0 means move Right direction
    // if panRight < 0 means move Left direction
    // if panDown > 0 means move Down direction
    // if panDown < 0 means move Up direction
    public void moveDirection(int panRight,int panDown) {
        Log.i(TAG, "------- moveDirection pRight:" + panRight + " pDown:" + panDown);
        if ((mSurfaceWidth > dstWidthAfterScale) && (mSurfaceHeight >
                dstHeightAfterScale)) {
            return;
        }
        if (panRight != 0){
            cropStartX += panRight;
            if (cropStartX < 0){
                cropStartX = 0;
                return;
            } else if ((cropStartX+mSurfaceWidth) > dstWidthAfterScale) {
                cropStartX = dstWidthAfterScale - mSurfaceWidth - 1;
                return;
            }
        } else if (panDown != 0) {
            cropStartY += panDown;
            if (cropStartY < 0){
                cropStartY = 0;
                return;
            } else if ((cropStartY+mSurfaceHeight) > dstHeightAfterScale) {
                cropStartY = dstHeightAfterScale - mSurfaceHeight - 1;
                return;
            }
        }
        if(mMMediaPlayer != null) {
            Log.i(TAG, "dstWidthAfterScale:" + dstWidthAfterScale + " dstHeightAfterScale:" + dstHeightAfterScale);
            Log.i(TAG, "cropStartX:" + cropStartX + " cropStartY:" + cropStartY);
            int cropWidth = Math.min(mSurfaceWidth, dstWidthAfterScale);
            int cropHeight = Math.min(mSurfaceHeight,dstHeightAfterScale);
            Log.i(TAG, "cropWidth:" + cropWidth);
            Log.i(TAG, "cropHeight:" + cropHeight);
            // ImageCropRect API's Parameter should follow:
            // 0 <= cropStartX < dstWidthAfterScale
            // 0 <= cropStartY < dstHeightAfterScale
            // (cropStartX + cropWidth) <= dstWidthAfterScale
            // (cropStartY + cropHeight) <= dstHeightAfterScale

            if (cropStartX < dstWidthAfterScale && cropStartY < dstHeightAfterScale &&
                    cropStartX + cropWidth <= dstWidthAfterScale && cropStartY + cropHeight <= dstHeightAfterScale) {
                Log.i(TAG, "ImageCropRect parameter is valid");
                boolean bImagePanSuccess = mMMediaPlayer.ImageCropRect(cropStartX, cropStartY, cropWidth, cropHeight);
                showToast(bImagePanSuccess ? getResources().getString(R.string.photo_pan_success_toast) : getResources().getString(R.string.photo_pan_failed_toast), Gravity.CENTER, Toast.LENGTH_SHORT);
            } else {
                Log.i(TAG, "ImageCropRect parameter is not valid");
                showToast(getResources().getString(R.string.photo_pan_parameter_invalid), Gravity.CENTER, Toast.LENGTH_SHORT);
            }
        }
    }

    private void startImagePlayer() {
        if (mMMediaPlayer == null) return;
        mStartImagePlayerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "mMMediaPlayer.start() begin");
                    mMMediaPlayer.start();
                    if (Constants.bPhotoSeamlessEnable) {
                        mPrepareDelta = 0;
                    }
                } catch (Exception e){
                    mImagePlayerActivity.stopShowingProgress();
                    /*Toast toast = ToastFactory.getToast(mImagePlayerActivity,
                            getResources().getString(R.string.photo_out_of_memory_toast), Gravity.CENTER);
                    toast.show();*/
                    if (mImagePlayerActivity != null) {
                        mImagePlayerActivity.finish();
                    }
                }
            }
        });
        mStartImagePlayerThread.start();
    }

    public Thread getImagePlayerThread() {
        return mStartImagePlayerThread;
    }

    private void closeFileInputStream(final Closeable c) {
       if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
        }
    }

    private void prepareNextPhoto(final int delta) {
        prepareNextPhotoThread(delta);
    }

    protected void prepareNextPhotoThread(int delta) {
        BitmapFactory.Options  options = new BitmapFactory.Options();
        imgPath = mImagePlayerActivity.getNextPhotoPath(delta);
        Log.i(TAG,"prepareNextPhoto imgPath:"+imgPath);
        setPrepareState(delta,MEDIA_PLAYER_STATE_IDLE);
        if (imgPath == null)
            return;
        int imgWidth = 0;
        int imgHeight = 0;
        boolean bSuccess = false;
        Log.i(TAG,"call ImageDecodeNext with null parameter first time");
        try {
            bSuccess = mMMediaPlayer.ImageDecodeNext(imgPath,0,0,0,null);
        } catch (Exception e) {
            Log.i(TAG,"image decode next exception");
            bSuccess = false;
        }
        if (bSuccess == false) {
            setPrepareState(mPrepareDelta,MEDIA_PLAYER_STATE_ERROR);
        }
    }

    private void setPrepareState(int delta, int state) {
        Log.i(TAG,"setPrepareState delta: "+String.valueOf(delta)+", state: "+state);
        if (delta < 0) {
            mPrevPrepareState = state;
            mPrepareDelta = delta;
        } else {
            mNextPrepareState = state;
            mPrepareDelta = delta;
        }
    }

    public boolean showNextPhoto(final int delta) {
		int state = mPrevPrepareState;
        Log.i(TAG,"showNextPhoto state: "+String.valueOf(state));
        if (mMMediaPlayer != null
            && state == MEDIA_PLAYER_STATE_STARTED
            && getQueueLengthOfSeamlessPlayback() <= minimumQueueLengthCanShowSeamlessPhoto) {
            int index = (delta < 0) ? 0 : 1;
			boolean isSuccess = mMMediaPlayer.ImageShowNext(index);
            if (isSuccess) {
                // update the present photo position by order ascend in the photo set. mantis: 1182403
                mImagePlayerActivity.setCurrentPos(delta);
            } else {
                Log.i(TAG, "showNextPhoto: failed");
                mPrevPrepareState= MEDIA_PLAYER_STATE_IDLE;
                mNextPrepareState= MEDIA_PLAYER_STATE_IDLE;
                prepareNextPhoto(1);
                return false;
            }
        } else if (state == MEDIA_PLAYER_STATE_ERROR) {
            mImagePlayerActivity.showTipDialog(getResources().getString(R.string.file_not_support));
            return false;
        } else if(state < MEDIA_PLAYER_STATE_STARTED){
            String sMessage = "The photo is decoding,please try again later...";
            showToast(sMessage,Gravity.CENTER, Toast.LENGTH_SHORT);
            return false;
        }
        mNextPrepareState= MEDIA_PLAYER_STATE_IDLE;
        mNextPrepareState= MEDIA_PLAYER_STATE_IDLE;
        prepareNextPhoto(1);
        return true;
    }

    protected void openImagePlayer(){
        try {
            mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_IDLE;
            resetMediaPlayer();
            mMMediaPlayer = new MMediaPlayer();
            mMMediaPlayer.reset();
            if (sfholder == null) {
                return;
            }
            Log.i(TAG, "mMMediaPlayer.setDisplay()");
            mMMediaPlayer.setDisplay(sfholder);
            Log.i(TAG,"the photo path is:"+sPath);
            Uri mUri= Uri.parse(sPath);
            Log.i(TAG, "mMMediaPlayer.setDataSource:"+mUri);
            mMMediaPlayer.setOnErrorListener(mErrorListener);
            mMMediaPlayer.setOnInfoListener(mInfoListener);
            mMMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMMediaPlayer.setDataSource(this.getContext(), mUri);
            Log.i(TAG, "mMMediaPlayer.setDataSource end");
            mMMediaPlayer.setOnPreparedListener(new MMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // The process of getting the width or height of photos is just like video
                    // flow as the image flow
                    Log.i(TAG, "onPrepared");
                    mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_PREPARED;
                          imgOriginalWidth = mp.getVideoWidth();
                          imgOriginalHeight = mp.getVideoHeight();
                    Log.i(TAG, "imgOriginalWidth:" + imgOriginalWidth + " imgOriginalHeight:" + imgOriginalHeight);
                    if (SystemProperties.getInt("mstar.4k2k.photo", 0) == 1) {
                        if ((imgOriginalWidth >= 3840) && (imgOriginalHeight >= 2160)) {
                            mSurfaceWidth = 3840;
                            mSurfaceHeight = 2160;
                            Log.i(TAG, "mSurfaceWidth:" + mSurfaceWidth + " mSurfaceHeight:" + mSurfaceHeight);
                        } else {
                            adjustSurfaceSize();
                        }
                    }
                    /*// Currently media player do not support image size >= 1920*8 * 1080*8
                    if (imgOriginalWidth * imgOriginalHeight >= 1920 * 8 * 1080 * 8) {
                        Log.i(TAG, "Currently media player do not support image size >= 1920*8 * 1080*8");
                        Toast toast = ToastFactory.getToast(mImagePlayerActivity,
                                getResources().getString(R.string.can_not_decode),
                                Gravity.CENTER);
                        toast.show();
                        resetMediaPlayer();
                        if (mImagePlayerActivity.getPhotoFileListSize() <= 1) {
                            mImagePlayerActivity.finish();
                        } else {
                            mImagePlayerActivity.moveNextOrPrevious(1);
                        }
                        return;
                    }*/
               
                   sampleSize = 1;
                    resetscale(false);
                    MMediaPlayer.InitParameter  initParameter = mMMediaPlayer.new InitParameter();
                    initParameter.degrees = 0;
                    initParameter.scaleX = (float)scaleFactor;
                    initParameter.scaleY = (float)scaleFactor;
                    initParameter.cropX = 0;
                    initParameter.cropY = 0;
                    initParameter.cropWidth = 0;
                    initParameter.cropHeight = 0;
                    Log.i(TAG, "imgDecodedWidth:" + imgDecodedWidth + " imgDecodedHeight:" + imgDecodedHeight + " sampleSize:" + sampleSize + " scaleFactor:" + initParameter.scaleX);
                    mMMediaPlayer.SetImageSampleSize(sampleSize, mSurfaceWidth, mSurfaceHeight, initParameter);
                    initParameter = null;
                    if (mImagePlayerActivity.mIsSourceChange == false) {
                        startImagePlayer();
                    }
                } });
            Log.i(TAG, "mMMediaPlayer.prepareAsync()");
            mMMediaPlayer.prepareAsync();
            mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_PREPARING;
        } catch (Exception e){
            Log.i(TAG, "Exception:" + e);
            mImagePlayerActivity.showTipDialog(mImagePlayerActivity.getResources().getString(R.string.file_not_support));
        }
    }

    private void resetscale(Boolean bool){
    	if(bool){
    		String path= mImagePlayerActivity.getNextPhotoPath(0);
       	    Bitmap bitmap= BitmapFactory.decodeFile(path);  
       	     imgOriginalHeight= bitmap.getHeight();  
             imgOriginalWidth= bitmap.getWidth(); 
    	}
                    cansacles=false;
		 	 initialSize = Math.max((double)imgOriginalWidth/(double)mSurfaceWidth,(double)imgOriginalHeight/(double)mSurfaceHeight);
            if(((double)imgOriginalWidth==(double)mSurfaceHeight)&&((double)imgOriginalHeight==(double)mSurfaceWidth)){
				 cansacles=true;
			 }
			scaleFactor = 1.0f;
   Log.i(TAG,"initialSize1:"+initialSize);
   if ((initialSize == 1.0f) || (initialSize == 2.0f) || (initialSize == 4.0f) || (initialSize == 8.0f)) {
       scaleFactor = 1.0f;
       sampleSize = (int)initialSize;
   } else if(initialSize < 1.0f) {
       scaleFactor = 1.0f;
       sampleSize = 1;
       Log.i(TAG,"initialSize:2"+initialSize);
   } else {
       if (initialSize < 2.0f) {
           sampleSize = 1;
       } else if (initialSize < 4.0f) {
           sampleSize = 2;
       } else if (initialSize < 8.0f) {
           sampleSize = 4;
       } else {
           sampleSize = 8;
       }
       scaleFactor = sampleSize / initialSize;
   }
   if(sPath.endsWith(".MPO") || sPath.endsWith(".mpo")) {
       if (imgOriginalWidth * imgOriginalHeight > 2000 * 2000) {
           if (sampleSize < 8) {
               sampleSize *= 2;
           }
           scaleFactor = sampleSize / initialSize;
       }
   }
   Log.i(TAG, "scaleFactor=" + scaleFactor);
   Log.i(TAG, "height=" + imgOriginalHeight + " width:=" + imgOriginalHeight);
   imgDecodedWidth = (int)(imgOriginalWidth/sampleSize * scaleFactor);
   imgDecodedHeight = (int)(imgOriginalHeight/sampleSize * scaleFactor);
   Log.i(TAG, "imgDecodedWidth=" + imgDecodedWidth + " imgDecodedHeight:=" + imgDecodedHeight);
   dstWidthAfterScale = (int)imgDecodedWidth;
   dstHeightAfterScale = (int)imgDecodedHeight;
    }
    private void initQueueLengthOfSeamlessPlayback(){
        queueLengthOfSeamlessPlayback = 0;
    }

    private int getQueueLengthOfSeamlessPlayback(){
        Log.i(TAG,"getQueueLengthOfSeamlessPlayback():"+queueLengthOfSeamlessPlayback);
        return queueLengthOfSeamlessPlayback;
    }

    private void enqueueSeamLessPlayback(){
        if (Tools.isPhotoStreamlessModeOn()) {
            queueLengthOfSeamlessPlayback++;
        }
    }

    private void dequeueSeamLessPlayback(){
        if (Tools.isPhotoStreamlessModeOn() && queueLengthOfSeamlessPlayback>0) {
            queueLengthOfSeamlessPlayback--;
        }
    }

    private int queueLengthOfSeamlessPlayback = 0;
    private int minimumQueueLengthCanShowSeamlessPhoto = 1;

    private MMediaPlayer.OnInfoListener mInfoListener = new MMediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "onInfo what:" + what  + " extra:" + extra);
            switch (what) {
                case MediaPlayer.MEDIA_INFO_STARTED_AS_NEXT:
                    dequeueSeamLessPlayback();
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    dequeueSeamLessPlayback();
                    Log.i(TAG, "onInfo MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START");
                    if (Constants.bPhotoSeamlessEnable) {
                        if (mPrepareDelta < 0) {
                            setPrepareState(-1, MEDIA_PLAYER_STATE_STARTED);
                        } else if (mPrepareDelta > 0) {
                            if (getQueueLengthOfSeamlessPlayback() == 0) {
                                setPrepareState(1, MEDIA_PLAYER_STATE_STARTED);
                                prepareNextPhoto(-1);
                            }
                        } else if (mPrepareDelta == 0) {
                            mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_STARTED;
                            mImagePlayerActivity.stopShowingProgress();
                            mImagePlayerActivity.hideControlDelay();
                            mImagePlayerActivity.startPPT_Player();
                            // decode next one and then decode pre one
                            prepareNextPhoto(1);
                        }
                    } else {

                        mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_STARTED;
                        mImagePlayerActivity.stopShowingProgress();
                        mImagePlayerActivity.hideControlDelay();
                        mImagePlayerActivity.startPPT_Player();

                    }
                    break;

            }
            return false;
        }
    };
	protected int imgWidth;
	protected int imgHeight;
	
    // The following is a series of the player listener in callback
    MMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (imgPath == null) {
                return;
            }
            int delta = mPrepareDelta;
             imgWidth = mp.getVideoWidth();
             imgHeight = mp.getVideoHeight();
            Log.i(TAG, "MediaPlayer: "+mp+"    Video Size Changed: (" + imgWidth + "," + imgHeight+")");

            setPrepareState(delta, MEDIA_PLAYER_STATE_PREPARING);
            int sampleSize = 1;
            boolean bSuccess = false;
            Log.i(TAG,"the decoded next photo w:"+imgWidth+" ;h:"+imgHeight);
            // Currently media player do not support image size >= 1920*8 * 1080*8
            if (imgWidth * imgHeight >= 1920 * 8 * 1080 * 8) {
                Log.i(TAG, "Currently media player do not support image size >= 1920*8 * 1080*8");
                //mPhotoControl.showTipDialog(R.string.can_not_decode, 1);
                if (delta>0 && mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    int showToast = 0x12;
                    msg.what = showToast;
                    msg.arg1 = R.string.can_not_decode_next;
                    mHandler.sendMessage(msg);

                } else if (delta<0 && mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    int showToast = 0x12;
                    msg.what = showToast;
                    msg.arg1 = R.string.can_not_decode_previous;
                    mHandler.sendMessage(msg);
                }
                return;
            }
            double initialSize = Math.max((double)imgWidth/(double)mSurfaceWidth,(double)imgHeight/(double)mSurfaceHeight);
            double scaleFactor = 1.0f;
            if ((initialSize == 1.0f) || (initialSize == 2.0f) || (initialSize == 4.0f) || (initialSize == 8.0f)) {
                scaleFactor = 1.0f;
                sampleSize = (int)initialSize;
            } else if(initialSize < 1.0f) {
                scaleFactor = 1.0f;
                sampleSize = 1;
            } else {
                if (initialSize < 2.0f) {
                     sampleSize = 1;
                } else if (initialSize < 4.0f) {
                     sampleSize = 2;
                } else if (initialSize < 8.0f) {
                     sampleSize = 4;
                } else {
                     sampleSize = 8;
                }
                scaleFactor = sampleSize / initialSize;
            }

            if(imgPath.endsWith(".MPO") || imgPath.endsWith(".mpo")) {
                if (imgWidth * imgHeight > 2000 * 2000) {
                    if (sampleSize < 8) {
                        sampleSize *= 2;
                    }
                    scaleFactor = sampleSize / initialSize;
                }
            }

            MMediaPlayer.InitParameter  initParameter = mMMediaPlayer.new InitParameter();
            initParameter.degrees = 0;
            initParameter.scaleX = (float)scaleFactor;
            initParameter.scaleY = (float)scaleFactor;
            initParameter.cropX = 0;
            initParameter.cropY = 0;
            initParameter.cropWidth = 0;
            initParameter.cropHeight = 0;
            int index = (delta < 0) ? 0 : 1;
            try {
              bSuccess = mMMediaPlayer.ImageDecodeNext(imgPath,sampleSize,mSurfaceWidth,mSurfaceHeight,initParameter,index);
            } catch (Exception e) {
              Log.i(TAG,"image decode next exception");
              bSuccess = false;
            }
            if (bSuccess == false) {
                setPrepareState(mPrepareDelta,MEDIA_PLAYER_STATE_ERROR);
            }
            enqueueSeamLessPlayback();

        }
    };

    private MMediaPlayer.OnErrorListener mErrorListener = new MMediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.e(TAG, "Error: " + framework_err + "," + impl_err);
            dequeueSeamLessPlayback();
            mCurrentMediaPlayerState = MEDIA_PLAYER_STATE_ERROR;
            /* If an error handler has been supplied, use it and finish. */
            mImagePlayerActivity.onError(mp,framework_err,impl_err);
            stopPlayback(true);
            resetMediaPlayer();
            mPrevPrepareState = MEDIA_PLAYER_STATE_IDLE;
            mNextPrepareState = MEDIA_PLAYER_STATE_IDLE;
            return true;
        }
    };

    protected void drawImage() {
        Log.i(TAG,"andrew drawImage");
        ImagePlayerSurfaceView.this.postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
         if (bitmap != null && canvas != null) {
             int srcWidth = bitmap.getWidth();
             int srcHeight = bitmap.getHeight();
             // Some GIF photo's size is Larger than Screen Size, So need to be scaled to adapt to ScreenSize..
             if (srcWidth > this.getWidth() && srcHeight > this.getHeight() && this.getWidth() > 0 && this.getHeight() > 0) {
                 float widthScale = (float) this.getHeight() / srcWidth;
                 float heightScale = (float) this.getHeight() / srcHeight;
                 int width, height;
                 if (widthScale > heightScale) {
                     width = Math.round(bitmap.getWidth() * heightScale);
                     height = Math.round(bitmap.getHeight() * heightScale);
                 } else {
                     width = Math.round(bitmap.getWidth() * widthScale);
                     height = Math.round(bitmap.getHeight() * widthScale);
                 }
                 bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
             }
            Paint paint = new Paint();
            android.graphics.Rect src = new android.graphics.Rect();
            src.left = 0;
            src.top = 0;
            src.bottom = bitmap.getHeight();
            src.right = bitmap.getWidth();
            dst.left = 0;
            dst.top = 0;
            dst.bottom = this.getHeight();
            dst.right = this.getWidth();
            if(!bitmap.isRecycled()){
                paint.setColor(Color.BLACK);
                canvas.drawRect(dst, paint);
                center(src,dst);
                canvas.drawBitmap(bitmap, src, dst, paint);
            }
        }else {
            if (canvas != null) {
                dst.left = 0;
                dst.top = 0;
                dst.bottom = this.getHeight();
                dst.right = this.getWidth();
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setAlpha(0);
                canvas.drawRect(dst, paint);
            }
        }

    }


    protected void center(android.graphics.Rect src,android.graphics.Rect dst) {
        //bmp = resizeDownIfTooBig(bmp, true);
        float height = bitmap.getHeight();
        float width = bitmap.getWidth();
        float deltaX = 0, deltaY = 0;
        int viewHeight = getHeight();
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - src.top;
        }  else if (src.top > 0) {
            deltaY = -src.top;
        } else if (src.bottom < viewHeight) {
            deltaY = getHeight() - src.bottom;
        }
        int viewWidth = getWidth();
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - src.left;
        } else if (src.left > 0) {
            deltaX = -src.left;
        } else if (src.right < viewWidth) {
            deltaX = viewWidth - src.right;
        }
        dst.top = src.top + (int)deltaY;
        dst.left = src.left + (int)deltaX;
        dst.bottom = bitmap.getHeight() + (int)deltaY;
        dst.right = bitmap.getWidth() + (int)deltaX;
    }

    protected void cleanView(int width, int height) {
        Canvas canvas = getHolder().lockCanvas();
        if (bitmap != null && canvas != null) {
            Paint paint = new Paint();
            android.graphics.Rect src = new android.graphics.Rect();
            paint.setColor(Color.BLACK);
            src.left = 0;
            src.top = 0;
            src.bottom = height;
            src.right = width;
            canvas.drawRect(src, paint);
            //canvas.save(1);
        }
        if (canvas != null) {
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    protected void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    protected boolean updateView() {
        if (this.bitmap != null) {
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    private void setSurfaceSize() {
        int[] config = Tools.getOsdSize();
        mSurfaceWidth = config[0];
        mSurfaceHeight = config[1];
    }

    private void showToast(final String text, int gravity, int duration) {
        Toast toast = ToastFactory.getToast(mImagePlayerActivity, text, gravity);
        toast.show();
    }

    private void adjustSurfaceSize() {
        int[] config = Tools.getPanelSize();
        mPanelWidth = config[0];
        mPanelHeight = config[1];
        Log.i(TAG,"adjustSurfaceSize:"+String.valueOf(mPanelWidth)+" "+String.valueOf(mPanelHeight));
        if (mPanelWidth != 0 && mPanelHeight != 0) {
            Log.i(TAG, "getPanelConfig true");
        } else {
            Log.i(TAG, "getPanelConfig false");
            DisplayMetrics  dm = new DisplayMetrics();
            mImagePlayerActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            int tmpPanelWidth = (int)(dm.widthPixels* density);
            int tmpPanelHeight = (int)(dm.heightPixels* density);
            String strPM = "SurfaceSize:" +tmpPanelWidth +" * "+tmpPanelHeight;
            Log.i(TAG,strPM);
            mPanelWidth = tmpPanelWidth;
            mPanelHeight = tmpPanelHeight;
        }
        if ((mPanelHeight > mSurfaceHeight) || (mPanelWidth > mSurfaceWidth)) {
            mSurfaceHeight = mPanelHeight;
            mSurfaceWidth = mPanelWidth;
        }
    }
}
