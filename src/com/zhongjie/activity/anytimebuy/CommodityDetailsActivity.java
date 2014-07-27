package com.zhongjie.activity.anytimebuy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.fragment.FragmentBigImg;
import com.zhongjie.view.MyViewPager;
import com.zhongjie.view.SlideRightOutView;
import com.zhongjie.view.viewpagerindicator.CirclePageIndicator;

public class CommodityDetailsActivity extends BaseSecondActivity{
	
	private MyViewPager mPager;
	private CirclePageIndicator mIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_details);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mPager = (MyViewPager)findViewById(R.id.act_commodity_details_viewpager);
		mIndicator = (CirclePageIndicator)findViewById(R.id.act_commodity_indicator_dot);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mPager.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
		mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mIndicator.setViewPager(mPager);
	}
	
	class MyPagerAdapter extends FragmentStatePagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return new FragmentBigImg();
		}

		@Override
		public int getCount() {
			return 5;
		}
	}
}
