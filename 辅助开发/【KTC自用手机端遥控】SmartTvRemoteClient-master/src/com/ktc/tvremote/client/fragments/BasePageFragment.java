package com.ktc.tvremote.client.fragments;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author longzj
 */
public abstract class BasePageFragment extends Fragment implements IFragmentPageChange {
	
	private final String TAG = "yzh_BasePageFragment";
	protected Context mAppContext;
	protected boolean isVisible = false;
	protected View mRootView;

	
	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
		mAppContext = activity.getApplicationContext();
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mRootView == null){
			mRootView = inflater.inflate(loadFragmentLayoutRes(), null);
		}
		initView(mRootView);
		return mRootView;
	}
	
	/**
	 * 初始化fragment的layout id
	 * @return 返回id
	 */
	protected abstract int loadFragmentLayoutRes ();
	
	/**
	 * 实例化相关控件
	 * @param view 当前fragment的rootView
	 */
	protected abstract void initView(View view);
	
	
	@Override
	public void onDestroyView () {
		super.onDestroyView();
		ViewGroup group = (ViewGroup) mRootView.getParent();
		if(group != null){
			group.removeView(mRootView);
		}
	}
	
	@Override
	public void onPageSelectedChange (boolean userHandle, int tendency) {
		if(!userHandle){
			onPageSelectedChange(tendency);
		}
	}
	
	@Override
	public void onPageSelectedChange (int tendency) {
		Log.e(TAG, "onPageSelectedChange: " + getClass().getSimpleName());
	}
	
	@Override
	public void onPageStateIdle () {
		Log.e(TAG, "onPageStateIdle: " + getClass().getSimpleName());
	}
	
	@Override
	public void onPageStateNoIdle () {
		Log.e(TAG, "onPageStateNoIdle: " + getClass().getSimpleName());
	}
	
}
