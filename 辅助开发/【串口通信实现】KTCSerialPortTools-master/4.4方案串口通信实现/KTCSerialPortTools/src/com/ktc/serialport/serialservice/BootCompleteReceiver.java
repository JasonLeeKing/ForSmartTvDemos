package com.ktc.serialport.serialservice;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 * TODO 开机广播
 *
 * @author Arvin
 * 2018-3-11
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";

    private boolean isServiceRunning = false;
    
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        	Log.i(TAG, "---Intent.ACTION_BOOT_COMPLETED---");
            context.startService(new Intent(context, SerialConsoleService.class));
        }

    }

}
