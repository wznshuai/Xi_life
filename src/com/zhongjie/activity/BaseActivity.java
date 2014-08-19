package com.zhongjie.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.GetChars;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongjie.R;

public abstract class BaseActivity extends FragmentActivity{
	
	protected final String TAG = getClass().getSimpleName(); 
	protected TextView mTopRightTxt, mTopCenterTxt;
	protected ImageView mTopLeftImg, mTopCenterImg;
	
	
	abstract protected void initData();
	abstract protected void findViews();
	abstract protected void initViews();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(getClass().getSimpleName() + "  onCreate");
		initTopViews();
		initData();
		findViews();
		initViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println(getClass().getSimpleName() + "  onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println(getClass().getSimpleName() + "  onPause");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println(getClass().getSimpleName() + "  onDestroy");
	}
	
	protected void initTopViews(){
		mTopRightTxt = (TextView)findViewById(R.id.topbar_rightTxt);
		mTopCenterTxt = (TextView)findViewById(R.id.topbar_centerTxt);
		mTopLeftImg = (ImageView)findViewById(R.id.topbar_leftImg);
		mTopCenterImg = (ImageView)findViewById(R.id.topbar_centerImg);
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
	
	protected void showToast(String msg){
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	protected void showToast(int resId){
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
	}
	
	protected boolean canGOON(){
		return !isFinishing() && null != this;
	}
}
