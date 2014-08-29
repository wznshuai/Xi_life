package com.zhongjie.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.managerservice.DryCleanActivity;
import com.zhongjie.activity.managerservice.RepairsActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.view.MyViewPager;
import com.zhongjie.view.SlideRightOutView;
import com.zhongjie.view.viewpagerindicator.CirclePageIndicator;

public class FragmentManagerService extends BaseFragment implements OnClickListener{
	private static FragmentManagerService mInstance;
	
	private MyViewPager mPager;
	private CirclePageIndicator mIndicator;
	private View mGoRepairs, mGoDryClean;
	
	public static FragmentManagerService newInstance(){
		if(null == mInstance)
			mInstance = new FragmentManagerService();
		return mInstance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_manager_service, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		mPager = (MyViewPager)findViewById(R.id.fra_manager_service_viewpager);
		mIndicator = (CirclePageIndicator)findViewById(R.id.fra_manager_service_indicator_dot);
		mGoRepairs = findViewById(R.id.fra_manager_service_repairs);
		mGoDryClean = findViewById(R.id.fra_manager_service_dryclean);
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		mPager.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
		mPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		mIndicator.setViewPager(mPager);
		mGoRepairs.setOnClickListener(this);
		mGoDryClean.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getActivityMine().getCurrentTabTag().equals(MainActivity.TAB_2)){
			mPager.startAutoScroll();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mPager.stopAutoScroll();
	}
	
	class MyPagerAdapter extends FragmentStatePagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return new FragmentBigImg(R.drawable.temp_ic_ad);
		}

		@Override
		public int getCount() {
			return 5;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fra_manager_service_repairs:
			if(UserModelManager.getInstance().isLogin()){
				startActivity(new Intent(getActivity(), RepairsActivity.class));
			}else{
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.fra_manager_service_dryclean:
			startActivity(new Intent(getActivity(), DryCleanActivity.class));
			break;
		default:
			break;
		}
	}
	
	
}
