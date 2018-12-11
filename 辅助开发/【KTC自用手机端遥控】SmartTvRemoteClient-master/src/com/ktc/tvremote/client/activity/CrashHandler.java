package com.ktc.tvremote.client.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

/**
 * @TODO 捕获全局crash事件
 * @author Arvin
 * @since 2018.6.20
 */
public class CrashHandler implements UncaughtExceptionHandler {  
  
    public static final String TAG = "CrashHandler";  
  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    private static CrashHandler INSTANCE = new CrashHandler();  
  
    private CrashHandler() {  
    }  
  
    public static CrashHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * 初始化 
     * @param context 
     */  
    public void init(Context context) {  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
  
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(0);  
            Looper.myLooper().quit();  
        }  
    }  
  
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Looper.loop();  
            }  
        }.start();  
        return true;  
    }  
}  

