package com.ktc.tvremote.client.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.tvremote.client.R;
import com.ktc.tvremote.client.activity.HomeActivity;
import com.ktc.tvremote.client.adapter.ViewPagerAdapter;
import com.ktc.tvremote.client.fragments.BasePageFragment;
import com.ktc.tvremote.client.fragments.FragmentController;
import com.ktc.tvremote.client.fragments.FragmentMouse;
import com.ktc.tvremote.client.fragments.FragmentTxtInput;
import com.ktc.tvremote.client.transform.BGAPageTransformer;
import com.ktc.tvremote.client.transform.FixViewpagerScrollerSpeed;
import com.ktc.tvremote.client.transform.TransitionEffect;
import com.ktc.tvremote.client.views.SuperSlidingPaneLayout;

public class HomeHolder {

	private HomeActivity mAcivity ;
	public SuperSlidingPaneLayout superSlidingPaneLayout;
	public View slideMenu;
	
	private ViewPager mViewPager ;
    private ViewPagerAdapter<BasePageFragment> mPagerAdapter ;
    private FragmentManager mFragmentManager ;
    private static final int OFF_SCREEN_LIMIT = 3;
    
	public HomeHolder(HomeActivity mAcivity) {
		this.mAcivity = mAcivity ;
		initView();
	}
	
	private void initView() {
		superSlidingPaneLayout = (SuperSlidingPaneLayout)mAcivity.findViewById(R.id.superSlidingPaneLayout);
        superSlidingPaneLayout.setSliderFadeColor(0);
        slideMenu = mAcivity.findViewById(R.id.slidemenu);
        ViewGroup.LayoutParams params = slideMenu.getLayoutParams();
        params.width = (int)(mAcivity.getResources().getDisplayMetrics().widthPixels * 0.65f);
        slideMenu.setLayoutParams(params);
        
        superSlidingPaneLayout.setMode(SuperSlidingPaneLayout.Mode.values()[new Random().nextInt(SuperSlidingPaneLayout.Mode.values().length)]);
        
        
        mViewPager = (ViewPager) mAcivity.findViewById(R.id.remote_viewpager);
		initPager();
	}
	
	private void initPager () {
		FragmentTransaction transaction;
		mFragmentManager = mAcivity.getSupportFragmentManager();
        transaction = mFragmentManager.beginTransaction();
        
		clearFragmentCache();
		List<BasePageFragment> data = new ArrayList<BasePageFragment>();
		data.add(new FragmentController());
		data.add(new FragmentMouse());
		data.add(new FragmentTxtInput());
		
		transaction.commitAllowingStateLoss();
		mPagerAdapter = new ViewPagerAdapter<BasePageFragment>(mFragmentManager, data);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        //Default;Alpha;Cube;Depth;Tablet
        mViewPager.setPageTransformer(true, BGAPageTransformer.getPageTransformer(TransitionEffect.Cube));
        FixViewpagerScrollerSpeed.setViewPagerScrollSpeed(mViewPager , 600);
        mViewPager.setOffscreenPageLimit(OFF_SCREEN_LIMIT);
        mViewPager.setCurrentItem(0);
	}
	
	private void clearFragmentCache () {
		List<Fragment> list = mFragmentManager.getFragments();
		if (list != null && !list.isEmpty()) {
			for (Fragment fragment : list) {
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				ft.remove(fragment);
				ft.commit();
			}
		}
	}
	
	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
        }
    };

	
}
