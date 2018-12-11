package com.ktc.tvremote.client.fragments;

import android.app.Activity;
import android.view.View;
import com.ktc.tvremote.client.R;

/**
 * @TODO 模拟文本输入控制
 * @author Arvin
 * @date 2018.6.20
 */
public class FragmentTxtInput extends BasePageFragment {
	
	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onResume () {
		super.onResume();
		if (getUserVisibleHint()) {
			//加载数据
		}
	}
	
	@Override
	public void onPause () {
		super.onPause();
	}
	
	@Override
	public void onDestroyView () {
		super.onDestroyView();
	}
	
	@Override
	protected int loadFragmentLayoutRes () {
		return R.layout.layout_remote_home;
	}
	
	@Override
	protected void initView (View view) {
		//初始化view等
		initListener();
	}
	
	
	/**
	 * 初始化控件监听器
	 */
	private void initListener () {
		//
	}
	
	@Override
	public void onPageSelectedChange (int tendency) {
		super.onPageSelectedChange(tendency);
	}
	
	@Override
	public void onPageStateIdle () {
		super.onPageStateIdle();
	}
	
	@Override
	public void onDetach () {
		super.onDetach();
	}
	
	@Override
	public void setUserVisibleHint (boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}
	
}
