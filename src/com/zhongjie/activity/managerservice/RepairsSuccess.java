package com.zhongjie.activity.managerservice;

import android.os.Bundle;
import android.view.View;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class RepairsSuccess extends BaseSecondActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_repairs_success);
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
		mTopCenterImg.setImageResource(R.drawable.ic_logo_repair_top);
		mTopCenterImg.setVisibility(View.VISIBLE);
	}
	
}
