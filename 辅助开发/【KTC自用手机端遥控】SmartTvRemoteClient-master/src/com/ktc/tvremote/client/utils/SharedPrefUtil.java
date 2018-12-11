package com.ktc.tvremote.client.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

/**
 * @author Arvin
 * @version v1.0
 * @since 2018.6.5
 */
public class SharedPrefUtil {

    private static final String SHAREDPREFERENCE_NAME = "SmartTvRemoteShare";
    private static SharedPrefUtil mInstance;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private SharedPrefUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized static SharedPrefUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefUtil(context);
        }
        return mInstance;
    }
    
    public synchronized boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putInt(String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putLong(String key, long value) {
        mEditor.putLong(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putFloat(String key, float value) {
        mEditor.putFloat(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putStringSet(String key, Set<String> value) {
        mEditor.putStringSet(key, value);
        return mEditor.commit();
    }

    public String getString(String key, String value) {
        return mSharedPreferences.getString(key, value);
    }

    public int getInt(String key, int value) {
        return mSharedPreferences.getInt(key, value);
    }

    public long getLong(String key, long value) {
        return mSharedPreferences.getLong(key, value);
    }

    public float getFloat(String key, float value) {
        return mSharedPreferences.getFloat(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return mSharedPreferences.getBoolean(key, value);
    }

    public Set<String> getStringSet(String key, Set<String> value) {
        return mSharedPreferences.getStringSet(key, value);
    }

    public boolean remove(String key) {
        mEditor.remove(key);
        return mEditor.commit();
    }
}
