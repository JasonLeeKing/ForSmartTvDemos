package com.ktc.tvremote.client.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author longzj
 */
public class ViewPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {
	
	private List<T> mPagers;
	
	
	public ViewPagerAdapter (FragmentManager fm, List<T> data) {
		super(fm);
		mPagers = data;
	}
	
	@Override
	public T getItem (int position) {
		return mPagers.get(position);
	}
	
	@Override
	public int getCount () {
		return mPagers.size();
	}
	
}
