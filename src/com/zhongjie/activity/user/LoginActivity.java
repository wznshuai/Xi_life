package com.zhongjie.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.UserJson;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.Logger;
import com.zhongjie.util.SharedPreferencesUtil;
import com.zhongjie.util.Validator;
import com.zhongjie.view.CommonLoadingDialog;

public class LoginActivity extends BaseSecondActivity implements OnClickListener{
	
	private View mLogin;
	private EditText mPhoneEdit, mPwdEdit;
	private CommonRequest mRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
	}

	@Override
	protected void findViews() {
		mLogin = findViewById(R.id.act_login_login);
		mPhoneEdit = (EditText)findViewById(R.id.act_login_phonenum);
		mPwdEdit = (EditText)findViewById(R.id.act_login_pwd);
	}

	@Override
	protected void initViews() {
		mLogin.setOnClickListener(this);
		mTopCenterTxt.setText(R.string.user_login);
		mTopCenterTxt.setVisibility(View.VISIBLE);
		findViewById(R.id.act_login_register).setOnClickListener(this);
		findViewById(R.id.act_login_forgetpwd).setOnClickListener(this);
	}
	
	class DoLoginTask extends AsyncTask<Void, Void, UserJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(LoginActivity.this);
			cld.setCanceledOnTouchOutside(false);
			cld.show();
		}

		@Override
		protected UserJson doInBackground(Void... params) {
			UserJson uj = null;
			try {
				String result = mRequest.doLogin(mPhoneEdit.getText().toString().trim(),
						mPwdEdit.getText().toString().trim());
				if(!TextUtils.isEmpty(result)){
					uj = JSON.parseObject(result, UserJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "DoLoginTask error", e);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(UserJson result) {
			super.onPostExecute(result);
			if(!canGOON())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(result.code == 0){
					UserModelManager.getInstance().setmUser(result.data);
					SharedPreferencesUtil.getInstance(getApplicationContext()).saveString(Constants.USER_SESSID, result.data.sessId);
					finish();
				}else{
					showToast(result.errMsg);
				}
			}
		}
	}
	
	private void doLogin(){
		if(doVlidate()){
			new DoLoginTask().execute();
		}
	}
	
	private boolean doVlidate(){
		
		boolean flag = true;
		List<EditText> list = new ArrayList<EditText>();
		
		if(!Validator.validatePhone(mPhoneEdit, getString(R.string.user_phonenum_verify))){
			list.add(mPhoneEdit);
			flag = false;
		}
		
		
		if(!Validator.validatePassword(mPwdEdit, getString(R.string.user_pwd_verify))){
			list.add(mPwdEdit);
			flag = false;
		}
		
		if(!flag){
			if(null != list && list.size() > 0){
				list.get(0).requestFocus();
			}
		}
		
		list.clear();
		list = null;
		
		return flag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_login_register:
			startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			break;
		case R.id.act_login_forgetpwd:
			startActivity(new Intent(LoginActivity.this, ForgetpwdActivity.class));
			break;
		case R.id.act_login_login:
			doLogin();
			break;
		}
	}
}
