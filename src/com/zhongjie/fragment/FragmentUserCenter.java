package com.zhongjie.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongjie.BaseFragment;
import com.zhongjie.R;
import com.zhongjie.activity.user.IntegralActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.activity.user.MyOrderActivity;
import com.zhongjie.activity.user.MyRepairsActivity;
import com.zhongjie.activity.user.SettingActivity;
import com.zhongjie.activity.user.UserInfoAcivity;

public class FragmentUserCenter extends BaseFragment{
	
	private static FragmentUserCenter mInstance;
	
	private ImageView mHeadImg;
	private View mEditView, goIntegralView, goSettingView, goMyRepairsView, goMyOrderView;
	private static final int REQUEST_CODE = 0x001; 
	
	private TextView mTopRightView;
	
	public static FragmentUserCenter newInstance(){
		if(null == mInstance)
			mInstance = new FragmentUserCenter();
		return mInstance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usercenter, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViews();
		initViews();
	}
	
	public void findViews(){
		mTopRightView = (TextView)getActivity().findViewById(R.id.topbar_rightTxt);
		mHeadImg = (ImageView)findViewById(R.id.fra_usercenter_head);
		mEditView = findViewById(R.id.fra_usercenter_edit);
		goIntegralView = findViewById(R.id.fra_usercenter_goIntegral);
		goSettingView = findViewById(R.id.fra_usercenter_goSetting);
		goMyRepairsView = findViewById(R.id.fra_usercenter_goMyrepairs);
		goMyOrderView = findViewById(R.id.fra_usercenter_goMyOrder);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			mTopRightView.setText("去登录");
			mTopRightView.setVisibility(View.VISIBLE);
		}else{
			mTopRightView.setVisibility(View.GONE);
		}
	}
	
	public void initViews(){
		mTopRightView.setText("去登录");
		mTopRightView.setVisibility(View.VISIBLE);
		
		mTopRightView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(300))
		.imageScaleType(ImageScaleType.EXACTLY)
		.showImageForEmptyUri(R.drawable.ic_default_head)
		.showImageOnFail(R.drawable.ic_default_head)
		.showImageOnLoading(R.drawable.ic_default_head)
		.build();
		ImageLoader.getInstance().displayImage("", mHeadImg, options);
		mEditView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), UserInfoAcivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		goIntegralView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), IntegralActivity.class);
				startActivity(intent);
			}
		});
		
		goSettingView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});
		
		goMyRepairsView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyRepairsActivity.class);
				startActivity(intent);
			}
		});
		
		goMyOrderView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				startActivity(intent);
			}
		});
		
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
