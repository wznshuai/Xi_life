package com.zhongjie.activity.user;

import android.os.Bundle;
import android.view.View;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class SettingActivity extends BaseSecondActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("设置");
		mTopCenterTxt.setVisibility(View.VISIBLE);
	}
	
}
