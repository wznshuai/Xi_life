package com.zhongjie.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongjie.R;

public abstract class BaseActivity extends FragmentActivity{
	
	protected TextView mTopRightTxt, mTopCenterTxt;
	protected ImageView mTopLeftImg, mTopCenterImg;
	protected View mTopRightView;
	
	abstract protected void initData();
	abstract protected void findViews();
	abstract protected void initViews();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTopViews();
		initData();
		findViews();
		initViews();
	}
	
	protected void initTopViews(){
		mTopRightTxt = (TextView)findViewById(R.id.topbar_rightTxt);
		mTopCenterTxt = (TextView)findViewById(R.id.topbar_centerTxt);
		mTopLeftImg = (ImageView)findViewById(R.id.topbar_leftImg);
		mTopCenterImg = (ImageView)findViewById(R.id.topbar_centerImg);
		mTopRightView = findViewById(R.id.topbar_rightView);
		mTopLeftImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public void setContentView(int layoutResID) {
		View mainView = getLayoutInflater().inflate(layoutResID, null);
		((ViewGroup)mainView.findViewById(R.string.real_content))
		.addView(getLayoutInflater().inflate(R.layout.topview_flower, null),
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		super.setContentView(mainView);
	}
}
