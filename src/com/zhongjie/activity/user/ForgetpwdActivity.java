package com.zhongjie.activity.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class ForgetpwdActivity extends BaseSecondActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_forgetpwd);
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
		mTopLeftImg.setImageResource(R.drawable.ic_home);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("忘记密码");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_leftImg:
			goHomeActivity();
			break;

		default:
			break;
		}
	}
}
