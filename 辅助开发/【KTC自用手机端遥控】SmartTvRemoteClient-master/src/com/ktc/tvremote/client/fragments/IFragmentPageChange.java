package com.ktc.tvremote.client.fragments;

/**
 * @author longzj
 */
public interface IFragmentPageChange {
	
	/**
	 * 当滑动页面选定改变时调用,会配合用户控制使用
	 * @param userHandle 是否是人为滑动
	 * @param tendency 当前滑动趋势，小于0从右向左滑，大于0从左向右滑
	 */
	void onPageSelectedChange(boolean userHandle, int tendency);
	
	/**
	 * 当滑动页面选定改变时调用
	 * @param tendency 当前滑动趋势，小于0从右向左滑，大于0从左向右滑
	 */
	void onPageSelectedChange (int tendency);
	
	/**
	 * 当滑动暂停时调用
	 */
	void onPageStateIdle ();
	
	/**
	 * 当页面滑动时回调
	 */
	void onPageStateNoIdle ();
}
