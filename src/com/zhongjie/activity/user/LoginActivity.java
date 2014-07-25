package com.zhongjie.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class LoginActivity extends BaseSecondActivity implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
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
		mTopCenterTxt.setText(R.string.user_login);
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setOnClickListener(this);
		findViewById(R.id.act_login_register).setOnClickListener(this);
		findViewById(R.id.act_login_forgetpwd).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_leftImg:
			finish();
			break;
		case R.id.act_login_register:
			startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			break;
		case R.id.act_login_forgetpwd:
			startActivity(new Intent(LoginActivity.this, ForgetpwdActivity.class));
			break;
		}
	}
}
