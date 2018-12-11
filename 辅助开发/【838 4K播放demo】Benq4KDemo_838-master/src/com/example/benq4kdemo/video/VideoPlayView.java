package com.example.benq4kdemo.video;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataEditor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.Metadata;
import android.media.TimedText;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.benq4kdemo.utils.Constants;
import com.example.benq4kdemo.utils.Tools;
import com.mstar.android.media.MMediaPlayer;
import com.mstar.android.media.VideoCodecInfo;
import android.media.SubtitleTrack;


@SuppressLint("NewApi")
public class VideoPlayView extends SurfaceView {

    private String TAG = "VideoPlayView";

    private Uri mUri;
    private int mDuration;

    private static final int STATE_ERROR = -1;

    private static final int STATE_IDLE = 0;

    private static final int STATE_PREPARING = 1;

    private static final int STATE_PREPARED = 2;

    private static final int STATE_PLAYING = 3;

    private static final int STATE_PAUSED = 4;

    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private static final String MVC = "MVC";

    private static final int KEY_PARAMETER_SET_RESUME_PLAY = 2014;

    private int mCurrentState = STATE_IDLE;

    private int mTargetState = STATE_IDLE;

    private SurfaceHolder mSurfaceHolder = null;
    
    private MMediaPlayer mMMediaPlayer = null;

    private MMediaPlayer mNextMMediaPlayer = null;

    private int mVideoWidth;

    private int mVideoHeight;

    private boolean bVideoDisplayByHardware = false;
    private playerCallback myPlayerCallback = null;

    private int mSeekWhenPrepared; 

    private boolean isVoiceOpen = true;

    private float currentVoice = 1.0f;

    private long startTime;

    private long startSeekTime;

    private long endSeekTime;

    public boolean bResumePlay = false;

    private static final int IO_ERROR = 9000;
    private Context mContext;

    private Handler mHandler;

    public VideoPlayView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setVideoPath(String path) {
        mUri = Uri.parse(path);
        mSeekWhenPrepared = 0;
        int md5 = SystemProperties.getInt("mstar.md5", 0);
        if (md5 == 1) {
            int ind = path.lastIndexOf(".");
            if (ind>0) {
                String sOrgMD5 = path.substring(0,ind) + ".md5";
                int lastInd = path.lastIndexOf("/");
                String sDstMD5 = path.substring(0,lastInd+1) + "golden.md5";
                Tools.copyfile(sOrgMD5, sDstMD5);
            }
        }
        Log.i(TAG, "***********setVideoURI:" + mUri);
        bVideoDisplayByHardware = false;
        if (mContext != null) {
            String classname = mContext.getClass().toString();
            if (!classname.contains("Net")) {
                if (Tools.isVideoSWDisplayModeOn() && !Tools.getHardwareName().equals("monet")) {
                    ((Video4KActivity)mContext).setVideoDisplayFullScreen();
                }
        }
        }
        
        Log.i(TAG, "openPlayer for Nonstreamless mode.");
        openPlayer();
        
        requestLayout();
        invalidate();
    }

    public boolean is4kVideo() {
        Log.i(TAG, "VideoPlayview is4kVideo mVideoWidth:" + mVideoWidth + " mVideoHeight:" + mVideoHeight);
        if (mVideoWidth >= 3840 && mVideoHeight >= 1080) {
            return true;
        }
        return false;
    }

    public boolean isVideoDisplayByHardware() {
        return bVideoDisplayByHardware;
    }

    /**
     * call before play next.
     */
    public void stopPlayback() {
        if (mMMediaPlayer != null && mTargetState != STATE_IDLE) {
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mVideoWidth = 0;
            mVideoHeight = 0;
            mMMediaPlayer.stop();
            Log.i(TAG, "stopPlayback: *****release start*****");
            mMMediaPlayer.release();
            Log.i(TAG, "stopPlayback: *****release end*****");
            mMMediaPlayer = null;
            setBackgroundColor(Color.BLACK);
        }
    }

    /**
     * When abnormal stop play.
     */
    public void stopPlayer() {
        synchronized(this) {
            if (mMMediaPlayer != null && mTargetState != STATE_IDLE) {
                mCurrentState = STATE_IDLE;
                mTargetState = STATE_IDLE;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            releaseSeamlessPlay();
                            if (mMMediaPlayer.isPlaying()) {
                                Log.i(TAG, "*****stop start*****");
                                mMMediaPlayer.stop();
                                Log.i(TAG, "*****stop end*****");
                            }
                            Log.i(TAG, "***stopPlayer()**release start*****");
                            mMMediaPlayer.release();
                            Log.i(TAG, "***stopPlayer()**release end*****");
                            mMMediaPlayer = null;
                        } catch (Exception e) {
                            Log.i(TAG, "Exception:" + e);
                        }

                    }
                }).start();
            }
        }
    }

    public void releaseSeamlessPlay(){
        Log.i(TAG,"Tools.isVideoStreamlessModeOn():"+Tools.isVideoStreamlessModeOn());
        Log.i(TAG,"mMMediaPlayer:"+mMMediaPlayer);
        if (Tools.isVideoStreamlessModeOn() && mMMediaPlayer!=null) {
            Log.i(TAG,"SetSeamlessMode E_PLAYER_SEAMLESS_NONE");
            mMMediaPlayer.SetSeamlessMode(MMediaPlayer.EnumPlayerSeamlessMode.E_PLAYER_SEAMLESS_NONE);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * Start player.
     */
    private void openPlayer() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        // close the built-in music service of android
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        this.getContext().sendBroadcast(i);
        // Close the user's music callback interface
        if (myPlayerCallback != null)
            myPlayerCallback.onCloseMusic();

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            mMMediaPlayer = new MMediaPlayer();
            mDuration = -1;
            mMMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMMediaPlayer.setOnErrorListener(mErrorListener);
            mMMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMMediaPlayer.setOnInfoListener(mInfoListener);
            mMMediaPlayer.setOnTimedTextListener(mTimedTextListener);
            mMMediaPlayer.setOnSeekCompleteListener(mMMediaPlayerSeekCompleteListener);
            mMMediaPlayer.setDataSource(this.getContext(), mUri);
            
            if (Tools.isVideoStreamlessModeOn()) {
                if (!Tools.isElderPlatformForStreamLessMode()) {
                    Log.v(TAG,"einstein/napoli flow set seamless mode E_PLAYER_SEAMLESS_DS");
                    mMMediaPlayer.SetSeamlessMode(MMediaPlayer.EnumPlayerSeamlessMode.E_PLAYER_SEAMLESS_DS);
                }
            }

            boolean isRotateModeOn = Tools.isRotateModeOn();
            Log.i(TAG, "isRotateModeOn:" + isRotateModeOn);
            if (true == isRotateModeOn) {
                int rotateDegrees = Tools.getRotateDegrees();
                Log.i(TAG, "rotateDegrees:" + rotateDegrees);
                imageRotate(rotateDegrees);
            }

            if (mSurfaceHolder != null) {
                mMMediaPlayer.setDisplay(mSurfaceHolder);
            }
            mMMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMMediaPlayer.setScreenOnWhilePlaying(true);
            Log.i(TAG, "***********prepareAsync: " + mSurfaceHolder);
            if (Constants.bSupportDivx) {
                if (Tools.getResumePlayState(mUri)) {
                    bResumePlay = true;
                    mMMediaPlayer.setParameter(KEY_PARAMETER_SET_RESUME_PLAY, 1);
                }
                String fn = Tools.getFileName(mUri.getPath());
                SystemProperties.set("mstar.path", fn);
            }
            startTime = System.currentTimeMillis();
            mMMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.

            mCurrentState = STATE_PREPARING;
            mTargetState = STATE_PREPARED;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            if (myPlayerCallback != null) {
                myPlayerCallback.onError(mMMediaPlayer, IO_ERROR, 0);
            }
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        } catch (IllegalStateException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        } catch (SecurityException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
            return;
        }
    }

    private void errorCallback(int errId) {
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if (myPlayerCallback != null)
            myPlayerCallback.onError(mMMediaPlayer,
                    MMediaPlayer.MEDIA_ERROR_UNKNOWN, errId);
    }

    // The following is a series of the player listener in callback
    MMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            Log.e(TAG, "MediaPlayer: "+mp+"    Video Size Changed: (" + mVideoWidth + "," + mVideoHeight+")");
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                // Note: can't literally change the size of the SurfaceView, can
                // affect the effect of the PIP
                // getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
            if (!bVideoDisplayByHardware ||  Tools.isRotate90OR270Degree()) {
                if (mContext != null) {
                    String classname = mContext.getClass().toString();
                    if (!classname.contains("Net")) {
                        if (Tools.isVideoSWDisplayModeOn()) {
                            ((Video4KActivity)mContext).setVideoDisplayRotate90();
                        }
                    }
                }
            }

            if (myPlayerCallback != null) {
                myPlayerCallback.onVideoSizeChanged(mMMediaPlayer, mVideoWidth, mVideoHeight);
            }
        }
    };

    MMediaPlayer.OnPreparedListener mPreparedListener = new MMediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            Log.i(TAG, "******onPrepared*myPlayerCallback*****" + myPlayerCallback);
            if (bVideoDisplayByHardware && mContext != null && !Tools.isRotate90OR270Degree()) {
                String classname = mContext.getClass().toString();
                if (!classname.contains("Net")) {
                    if (Tools.isVideoSWDisplayModeOn() && !Tools.getHardwareName().equals("monet")) {
                        ((Video4KActivity)mContext).setVideoDisplayFullScreen();
                    }
                }
            }
            requestLayout();
            invalidate();
            //delay 1500ms to avoid splash video screen
        	new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setBackgroundColor(Color.TRANSPARENT);
				}
			}, 1500);
        	
            if(Tools.getHardwareName().equals("monet")) {
                Log.i(TAG, "mPreparedListener : is monet platform!");
                if(is4kVideo()) {
                    VideoCodecInfo vcInfo = getVideoInfo();
                    if (vcInfo != null) {
                        String vcType = vcInfo.getCodecType();
                        Log.i(TAG, "mPreparedListener vcType:" + vcType);
                        if(!vcType.equalsIgnoreCase("H265") && !vcType.equalsIgnoreCase("HEVC")) {
                            errorCallback(MediaError.ERROR_UNSUPPORTED);
                            Log.i(TAG, "mPreparedListener : not support,exit!");
                            return;
                        }
                    }
                }
            }
            if (myPlayerCallback != null) {
                myPlayerCallback.onPrepared(mMMediaPlayer);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            // mSeekWhenPrepared may be changed after seekTo() call
            int seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            } else {
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }

            if (mHandler != null) {
                Message msg = new Message();
                msg.what = Constants.CHECK_IS_SUPPORTED;
                mHandler.sendMessage(msg);
            }
        }
    };

    MMediaPlayer.OnPreparedListener mPreparedListener2 = new MMediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.i(TAG, "******onPrepared2*myPlayerCallback*****" + myPlayerCallback);
            mCurrentState = STATE_PREPARING;
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (bVideoDisplayByHardware && mContext != null && !Tools.isRotate90OR270Degree()) {
                String classname = mContext.getClass().toString();
                if (!classname.contains("Net")) {
                    if (Tools.isVideoSWDisplayModeOn()) {
                        ((Video4KActivity)mContext).setVideoDisplayFullScreen();
                    }
                }
            }
            requestLayout();
            invalidate();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                Log.i(TAG, "MediaPlayer2 start.....");
                mNextMMediaPlayer.start();
                String hardwareName = Tools.getHardwareName();
                if (hardwareName.equals("edison") || hardwareName.equals("kaiser")) {
                    Log.i(TAG, "MediaPlayer1 release.....(edison/kaiser flow)");
                    mMMediaPlayer.release();
                }
                mMMediaPlayer = null;
                mMMediaPlayer = mNextMMediaPlayer;
                mNextMMediaPlayer = null;
                mCurrentState = STATE_PLAYING;
            }
            if (myPlayerCallback != null) {
                myPlayerCallback.onPrepared(mMMediaPlayer);
            }
            if (mHandler != null) {
                Message msg = new Message();
                msg.what = Constants.CHECK_IS_SUPPORTED;
                mHandler.sendMessage(msg);
            }
        }
    };

    private MMediaPlayer.OnCompletionListener mCompletionListener = new MMediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "MediaPlayer  call  onCompletion ..");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (Constants.bSupportDivx) {
                Tools.setResumePlayState(0);
            }
            if (myPlayerCallback != null) {
                myPlayerCallback.onCompletion(mMMediaPlayer);
            }
        }
    };

    private MMediaPlayer.OnErrorListener mErrorListener = new MMediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.e(TAG, "Error: " + framework_err + "," + impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if(mMMediaPlayer == null) {
                mMMediaPlayer = mNextMMediaPlayer;
                mNextMMediaPlayer = null;
            }

            /* If an error handler has been supplied, use it and finish. */
            if (myPlayerCallback != null) {
                if (myPlayerCallback.onError(mMMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }
            return true;
        }
    };


    private MMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (myPlayerCallback != null)
                myPlayerCallback.onBufferingUpdate(mp, percent);
        }
    };

    private MMediaPlayer.OnInfoListener mInfoListener = new MMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "onInfo what:" + what  + " extra:" + extra);
            if (MMediaPlayer.MEDIA_INFO_VIDEO_DISPLAY_BY_HARDWARE == what) {
                bVideoDisplayByHardware = true;
            }

            if (myPlayerCallback != null) {
                myPlayerCallback.onInfo(mp, what, extra);
                return true;
            }
            return false;
        }
    };

    private MMediaPlayer.OnTimedTextListener mTimedTextListener = new OnTimedTextListener() {
        @Override
        public void onTimedText(MediaPlayer arg0, TimedText arg1) {
            if (arg1 != null) {
                Log.i(TAG, "********mTimedTextListener********" + arg1.getText());
                if (myPlayerCallback != null) {
                    myPlayerCallback.onUpdateSubtitle(arg1.getText());
                }
            } else {
                Log.i(TAG, "********mTimedTextListener********  null");
                if (myPlayerCallback != null) {
                    myPlayerCallback.onUpdateSubtitle(" ");
                }
            }
        }
    };

    private MMediaPlayer.OnSeekCompleteListener mMMediaPlayerSeekCompleteListener = new MMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            endSeekTime = System.currentTimeMillis();
            Log.i(TAG, ">>>SeekComplete>>>>>>seek time : "
                    + (endSeekTime - startSeekTime));
            setVoice(true);
            if (myPlayerCallback != null) {
                myPlayerCallback.onSeekComplete(mp);
            }
        }
    };

    /**
     * Surface relevant callback interface.
     */
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                int h) {
            mSurfaceHolder = holder;
            Log.i(TAG, "*************surfaceChanged************" + w + " " + h);
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            // mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
            openPlayer();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            Log.i(TAG, "*************surfaceDestroyed************");
            release(true);
        }
    };

    /*
     * release the media player in any state.
     */
    private void release(boolean cleartargetstate) {
        Log.i(TAG, "***********release*******" + (mTargetState == STATE_IDLE));
        if (mTargetState == STATE_IDLE) {
            return;
        }
        mCurrentState = STATE_IDLE;
        if (cleartargetstate) {
            mTargetState = STATE_IDLE;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mMMediaPlayer != null && mMMediaPlayer.isPlaying()) {
                        try {
                            mMMediaPlayer.stop();
                        } catch (IllegalStateException e) {
                            Log.i(TAG, "stop fail! please try again!");
                            try {
                                this.wait(2000);
                                mMMediaPlayer.stop();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    if (mMMediaPlayer != null) {
                        Log.i(TAG, "*****release start*****");
                        mMMediaPlayer.release();// release will done reset
                        Log.i(TAG, "*****release end*****");
                    }
                    mMMediaPlayer = null;
                }
            }).start();
        } else {
            if (mMMediaPlayer != null) {
                Log.i(TAG, "***********release Player");
                mMMediaPlayer.release();
            }
            mMMediaPlayer = null;
        }

    }

    public void setPlayingState() {
        mCurrentState = STATE_PREPARED;
        mTargetState = STATE_PLAYING;
    }

    public void start() {
        if (isInPlaybackState()) {
            mMMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (mMMediaPlayer.isPlaying()) {
                mMMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public boolean isVideoWidthHeightEqualZero(){
        if (isInPlaybackState()) {
            if (mVideoWidth == 0 && mVideoHeight == 0) {
                Log.i(TAG,"isVideoWidthHeightEqualZero yes");
                return true;
            }
        }
        return false;
    }

    /**
     * cache duration as mDuration for faster access.
     *
     * @return
     */
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mMMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    /**
     * Get the current play time.
     *
     * @return
     */
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Jump to a certain time.
     *
     * @param msec
     */
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
			if (msec>getDuration()) {
                Log.i(TAG,"seekTo is bigger than Duration");
                return;
            }
            startSeekTime = System.currentTimeMillis();
            mMMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
        if (mMMediaPlayer == null ){
            return false;
        }
        try{
            return isInPlaybackState() && mMMediaPlayer.isPlaying();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Determine whether normal play.
     *
     * @return
     */
    public boolean isInPlaybackState() {
        return (mMMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public int getMediaParam(int param) {
        Log.i("andrew", "the mMMediaPlayer:" + mMMediaPlayer);
        if (mMMediaPlayer != null) {
            return mMMediaPlayer.getIntParameter(param);
        } else
            return 0;
    }

    public void setVideoScale(int leftMargin, int topMargin, int width, int height) {

        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;

        setLayoutParams(lp);
    }

    public void setVideoScaleFrameLayout(int leftMargin, int topMargin, int width, int height) {

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            // The following the forced outfit in the decision must be based on
            // the XML type.
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layoutParams;
            params.leftMargin = leftMargin;
            params.rightMargin = leftMargin;
            params.topMargin = topMargin;
            params.bottomMargin = topMargin;
            params.width = width;
            params.height = height;
            setLayoutParams(params);
        }
    }

    public void setVideoScaleLinearLayout(int leftMargin, int topMargin, int width, int height) {

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            // The following the forced outfit in the decision must be based on
            // the XML type.
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
            params.leftMargin = leftMargin;
            params.rightMargin = leftMargin;
            params.topMargin = topMargin;
            params.bottomMargin = topMargin;
            params.width = width;
            params.height = height;

            setLayoutParams(params);
        }
    }

    public double calculateZoom(double ScrennWidth, double ScrennHeight) {
        double dRet = 1.0;
        double VideoWidth = (double) mVideoWidth;
        double VideoHeight = (double) mVideoHeight;
        double dw = ScrennWidth / VideoWidth;
        double dh = ScrennHeight / VideoHeight;
        if (dw > dh)
            dRet = dh;
        else
            dRet = dw;

        return dRet;
    }

    public MMediaPlayer getMMediaPlayer() {
        return mMMediaPlayer;
    }

    public void setVoice(boolean isSetOpen) {
        if (isInPlaybackState()) {
            if (isSetOpen) {
                mMMediaPlayer.setVolume(currentVoice, currentVoice);
                isVoiceOpen = true;
            } else {
                mMMediaPlayer.setVolume(0, 0);
                isVoiceOpen = false;
            }
        }
    }

    public void addVoice(boolean flag) {
        if (mMMediaPlayer != null) {
            int voice = getVoice();
            if (flag) {
                if (voice < 10) {
                    voice = voice + 1;
                }
            } else {
                if (voice > 0) {
                    voice = voice - 1;
                }
            }
            setVoice(voice);
        }
    }

    public void setVoice(int voice) {
        if (isInPlaybackState()) {
            if (voice >= 0 && voice <= 10) {
                currentVoice = voice * 0.1f;
            }
            Log.i(TAG, "******currentVoice*******" + currentVoice);
            mMMediaPlayer.setVolume(currentVoice, currentVoice);
        }
    }

    public int getVoice() {
        return (int) (currentVoice * 10);
    }

    public boolean isVoiceOpen() {
        return isVoiceOpen;
    }

    /**
     * Register a callback to be invoked
     *
     * @param l The callback that will be run
     */
    public void setPlayerCallbackListener(playerCallback l) {
        myPlayerCallback = l;
    }


    /**
     * User callback interface.
     */
    public interface playerCallback {
        // error tip
        boolean onError(MediaPlayer mp, int framework_err, int impl_err);

        // play complete
        void onCompletion(MediaPlayer mp);

        boolean onInfo(MediaPlayer mp, int what, int extra);

        void onBufferingUpdate(MediaPlayer mp, int percent);

        void onPrepared(MediaPlayer mp);

        // Finish back
        void onSeekComplete(MediaPlayer mp);

        // Video began to play before, closed music.
        void onCloseMusic();

        void onUpdateSubtitle(String sub);

        void onVideoSizeChanged(MediaPlayer mp, int width, int height);
    }

    /****************************************/
    // mstar Extension APIs start
    /**
     * Set the speed of the video broadcast.
     *
     * @param speed
     * @return
     */
    public boolean setPlayMode(int speed) {
        if (speed < -32 || speed > 32)
            return false;

        if (isInPlaybackState()) {
            Log.i(TAG, "****setPlayMode***" + speed);
            return mMMediaPlayer.setPlayMode(speed);
        }
        return false;
    }

    /**
     * For video broadcast speed.
     *
     * @return
     */
    public int getPlayMode() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getPlayMode();
        }
        return 64;
    }

    /**
     * get audio codec type.
     *
     * @return
     */
    public String getAudioCodecType() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getAudioCodecType();
        }
        return null;
    }

    /**
     * get video Info.
     *
     * @return
     */
    public VideoCodecInfo getVideoInfo() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getVideoInfo();
        }
        return null;
    }

    /**
     * Adds an external timed text source file.
     *
     * Currently supported format is SubRip with the file extension .srt, case insensitive.
     * Note that a single external timed text source may contain multiple tracks in it.
     * One can find the total number of available tracks using {@link #getTrackInfo()} to see what
     * additional tracks become available after this method call.
     *
     * @param path The file path of external timed text source file.
     * @param mimeType The mime type of the file. Must be one of the mime types listed above.
     * @throws IOException if the file cannot be accessed or is corrupted.
     * @throws IllegalArgumentException if the mimeType is not supported.
     * @throws IllegalStateException if called in an invalid state.
     */
    public void addTimedTextSource(String path, String mimeType) {
        if (isInPlaybackState()) {
            Log.i(TAG,"addTimedTextSource path:" + path + " mimeType:" + mimeType);
            try {
                mMMediaPlayer.addTimedTextSource(path, mimeType);
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
    }

    /**
     * Returns an array of track information.
     *
     * @return Array of track info. The total number of tracks is the array length.
     * Must be called again if an external timed text source has been added after any of the
     * addTimedTextSource methods are called.
     * @throws IllegalStateException if it is called in an invalid state.
     */
    public MediaPlayer.TrackInfo[] getTrackInfo() {
        if (isInPlaybackState()) {
            Log.i(TAG, "getTrackInfo");
            try {
                return mMMediaPlayer.getTrackInfo();
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
        return null;
    }

    /**
     * Returns an array of track information.
     *
     * @return Array of track info. The total number of tracks is the array length.
     * Must be called again if an external timed text source has been added after any of the
     * addTimedTextSource methods are called.
     * @throws IllegalStateException if it is called in an invalid state.
     */
    public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
    public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
    public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
    public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
    public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
    public static final int MEDIA_TRACK_TYPE_TIMEDBITMAP = 5;

    public MMediaPlayer.MsTrackInfo[] getMsTrackInfo() {
        if (isInPlaybackState()) {
            Log.i(TAG, "getMsTrackInfo");
            try {
                return mMMediaPlayer.getMsTrackInfo();
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
        return null;
    }

    public MMediaPlayer.MsTrackInfo getMsTrackInfo(int index) {
        if (isInPlaybackState()) {
            Log.i(TAG, "getMsTrackInfo index:" + index);
            try {
                MMediaPlayer.MsTrackInfo[] trackInfo = mMMediaPlayer.getMsTrackInfo();
                if (trackInfo != null && trackInfo.length > index) {
                    return trackInfo[index];
                }
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
        return null;
    }

    /*
    public static final int MEDIA_TRACK_TYPE_UNKNOWN = 0;
    public static final int MEDIA_TRACK_TYPE_VIDEO = 1;
    public static final int MEDIA_TRACK_TYPE_AUDIO = 2;
    public static final int MEDIA_TRACK_TYPE_TIMEDTEXT = 3;
    public static final int MEDIA_TRACK_TYPE_SUBTITLE = 4;
    public static final int MEDIA_TRACK_TYPE_TIMEDBITMAP = 5;
    */

    public int getMsAudioTrackCount() {
        return getMsTrackInfoCount(MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO);
    }

    public int getMsTimedTextTrackCount() {
        return getMsTrackInfoCount(MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
    }

    public int getMsTimedBitmapTrackCount() {
        return getMsTrackInfoCount(MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP);
    }

    private int[] mMsAudioTrackIndex = null;
    private int[] mMsTimedTextTrackIndex = null;
    private int[] mMsTimedBitmapTrackIndex = null;

    public int getMsTrackSelectedIndex(int type, int index) {
        switch (type) {
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                if (mMsAudioTrackIndex != null) {

                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                if (mMsTimedTextTrackIndex != null) {

                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP:
                if (mMsTimedBitmapTrackIndex != null) {

                }
                break;
            default:
                break;
        }
        return -1;
    }

    public void setMsTrackIndex(int type, int index) {
        switch (type) {
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                if (mMsAudioTrackIndex != null) {
                    selectTrack(mMsAudioTrackIndex[index]);
                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                if (mMsTimedTextTrackIndex != null) {
                    selectTrack(mMsTimedTextTrackIndex[index]);
                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP:
                if (mMsTimedBitmapTrackIndex != null) {
                    selectTrack(mMsTimedBitmapTrackIndex[index]);
                }
                break;
            default:
                break;
        }
    }

    public int getMsTrackInfoCount(int type) {
        Log.i(TAG, "getMsTrackInfoCount type:" + type);
        int getMsTrackInfoCount = 0;
        if (isInPlaybackState()) {
            MMediaPlayer.MsTrackInfo []getMsTrackInfo = getMsTrackInfo();
            if (getMsTrackInfo != null) {
                Log.i(TAG, "getMsTrackInfo.length:" + getMsTrackInfo.length);

                // Get TrackType Count
                int length = getMsTrackInfo.length;
                for (int i = 0; i < length; i++) {
                    Log.i(TAG, "getMsTrackInfo[" + i + "].getTrackType:" + getMsTrackInfo[i].getTrackType());
                    if (type == getMsTrackInfo[i].getTrackType()) {
                        getMsTrackInfoCount++;
                    }
                }

                // Product Model: MStar Android TV,19,4.4.4
                Log.i(TAG, "Product Model: " + android.os.Build.MODEL + ","  + Build.VERSION.SDK_INT + "," + android.os.Build.VERSION.RELEASE);
                // Check if we're running on Android 5.0 or higher
                // public static final int LOLLIPOP = 21;
                if (Build.VERSION.SDK_INT < 21) {
                    int[] trackIndex = null;
                    if (getMsTrackInfoCount > 0) {
                        trackIndex = new int[getMsTrackInfoCount];
                    }

                    // Store TrackType Index
                    int j = 0;
                    for (int i = 0; i < length; i++) {
                        Log.i(TAG, "getMsTrackInfo[" + i + "].getTrackType:" + getMsTrackInfo[i].getTrackType());
                        if (type == getMsTrackInfo[i].getTrackType()) {
                            trackIndex[j++] = i;
                        }
                    }

                    switch (type) {
                        case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                            mMsAudioTrackIndex = new int[getMsTrackInfoCount];
                            mMsAudioTrackIndex = trackIndex;
                            break;
                        case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                            mMsTimedTextTrackIndex= new int[getMsTrackInfoCount];
                            mMsTimedTextTrackIndex = trackIndex;
                            break;
                        case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP:
                            mMsTimedBitmapTrackIndex = new int[getMsTrackInfoCount];
                            mMsTimedBitmapTrackIndex = trackIndex;
                            break;
                        default:
                            break;
                    }
                } else {
                    // ro.build.version.release
                }
                Log.i(TAG, "getMsTrackInfoCount:" + getMsTrackInfoCount);
            }
        }
        return getMsTrackInfoCount;
    }


    /**
     * Selects a track.
     * <p>
     * If a MediaPlayer is in invalid state, it throws an IllegalStateException exception.
     * If a MediaPlayer is in <em>Started</em> state, the selected track is presented immediately.
     * If a MediaPlayer is not in Started state, it just marks the track to be played.
     * </p>
     * <p>
     * In any valid state, if it is called multiple times on the same type of track (ie. Video,
     * Audio, Timed Text), the most recent one will be chosen.
     * </p>
     * <p>
     * The first audio and video tracks are selected by default if available, even though
     * this method is not called. However, no timed text track will be selected until
     * this function is called.
     * </p>
     * <p>
     * Currently, only timed text tracks or audio tracks can be selected via this method.
     * In addition, the support for selecting an audio track at runtime is pretty limited
     * in that an audio track can only be selected in the <em>Prepared</em> state.
     * </p>
     * @param index the index of the track to be selected. The valid range of the index
     * is 0..total number of track - 1. The total number of tracks as well as the type of
     * each individual track can be found by calling {@link #getTrackInfo()} method.
     * @throws IllegalStateException if called in an invalid state.
     *
     * @see android.media.MediaPlayer#getTrackInfo
     */
    public void selectTrack(int index) {
        if (isInPlaybackState()) {
            Log.i(TAG, "selectTrack");
            try {
                mMMediaPlayer.selectTrack(index);
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
    }

    /**
     * Deselect a track.
     * <p>
     * Currently, the track must be a timed text track and no audio or video tracks can be
     * deselected. If the timed text track identified by index has not been
     * selected before, it throws an exception.
     * </p>
     * @param index the index of the track to be deselected. The valid range of the index
     * is 0..total number of tracks - 1. The total number of tracks as well as the type of
     * each individual track can be found by calling {@link #getTrackInfo()} method.
     * @throws IllegalStateException if called in an invalid state.
     *
     * @see android.media.MediaPlayer#getTrackInfo
     */
    public void deselectTrack(int index) {
        if (isInPlaybackState()) {
            Log.i(TAG, "deselectTrack");
            try {
                mMMediaPlayer.deselectTrack(index);
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
    }

    // Android 5.0 or higher API
    public SubtitleTrack getSelectedTrack() {
        // Check if we're running on Android 5.0 or higher
        // public static final int LOLLIPOP = 21;
        if (Build.VERSION.SDK_INT >= 21) {
            if (isInPlaybackState()) {
                Log.i(TAG, "getSelectedTrack");
                try {
                    // mMMediaPlayer.getSelectedTrack();
                    try {
                        Class clz = Class.forName("com.mstar.android.media.MMediaPlayer");
                        Method getSelectedTrack = clz.getDeclaredMethod("getSelectedTrack");
                        return (SubtitleTrack)getSelectedTrack.invoke(clz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Exception:" + e);
                }
            }
        }
        return null;

    }


    /**
     * check mvc.
     */
    public boolean isMVCSource() {
        if (isInPlaybackState()) {
            VideoCodecInfo vcInfo = mMMediaPlayer.getVideoInfo();
            if (vcInfo != null) {
                String vcType = vcInfo.getCodecType();
                Log.i(TAG, "getCodecType:" + vcType);
                if (MVC.equals(vcType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean imageRotate(int degrees) {
        return mMMediaPlayer.ImageRotate(degrees, false);
    }

    public boolean imageRotate(int degrees , MMediaPlayer mp) {
        return mp.ImageRotate(degrees, false);
    }

}
