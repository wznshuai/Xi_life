package com.zhongjie.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;

import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.managerservice.DryCleanActivity;
import com.zhongjie.activity.managerservice.PaymentActivity;
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
	private View mGoRepairs, mGoDryClean, mGoPayment;
	private int mPagerHeight;
	private Object lock = new Object();
	
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
		mGoPayment = findViewById(R.id.fra_manager_service_goPayment);
		mPager = (MyViewPager)findViewById(R.id.fra_manager_service_viewpager);
		mIndicator = (CirclePageIndicator)findViewById(R.id.fra_manager_service_indicator_dot);
		mGoRepairs = findViewById(R.id.fra_manager_service_repairs);
		mGoDryClean = findViewById(R.id.fra_manager_service_dryclean);
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		mPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				mPagerHeight = mPager.getWidth()*35/48;
				mPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		mPager.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				synchronized (lock) {
					if(mPagerHeight == 0){
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				return null;
			}
			
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				mPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mPagerHeight));
				mPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
				mIndicator.setViewPager(mPager);
			}
			
		}.execute();
		
		mGoRepairs.setOnClickListener(this);
		mGoDryClean.setOnClickListener(this);
		mGoPayment.setOnClickListener(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		if(getActivityMine().getCurrentTabTag().equals(MainActivity.TAB_2)){
			getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo_managerservice);
			mPager.startAutoScroll();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo);
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
			return 2;
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
		case R.id.fra_manager_service_goPayment:
			if(UserModelManager.getInstance().isLogin()){
				startActivity(new Intent(getActivity(), PaymentActivity.class));
			}else{
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		default:
			break;
		}
	}
	
	
}
