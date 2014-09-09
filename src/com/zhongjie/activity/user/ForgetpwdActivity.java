package com.zhongjie.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.BaseJson;
import com.zhongjie.model.UserJson;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.SharedPreferencesUtil;
import com.zhongjie.util.Validator;

public class ForgetpwdActivity extends BaseSecondActivity implements OnClickListener{
	
	private TextView mSendSms;
	private CommonRequest mRequest;
	private EditText mPhoneEdit, mPasswordEdit, mVerifycodeEdit;
	private static final int SMS_WAIT_TIME = 30;
	private static final int SMS_TIME = 0, SMS_AGAIN_OK = 1;//handler 发送字段
	private static final String SEND_AGAIN = "重新发送";
	private boolean isStopThread = false;
	private View mSubmit;
	
	private Handler mTimerHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SMS_TIME:
				mSendSms.setText(SEND_AGAIN + "(" + msg.arg1 + ")");
				break;
			case SMS_AGAIN_OK:
				mSendSms.setEnabled(true);
				mSendSms.setText("获取短信验证码");
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_forgetpwd);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		mRequest = new CommonRequest(this);
	}

	@Override
	protected void findViews() {
		mPasswordEdit = (EditText)findViewById(R.id.act_forgetpwd_pwd);
		mSubmit = findViewById(R.id.act_forgetpwd_submit);
		mSendSms = (TextView)findViewById(R.id.act_forgetpwd_sendSMS);
		mPhoneEdit = (EditText)findViewById(R.id.act_forgetpwd_phonenum);
		mVerifycodeEdit = (EditText)findViewById(R.id.act_forgetpwd_verifycode);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_home);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("忘记密码");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setOnClickListener(this);
		mSendSms.setOnClickListener(this);
		mSubmit.setOnClickListener(this);
	}
	
	private void sendSMS(){
		String num = mPhoneEdit.getText().toString();
		if(Validator.validatePhone(mPhoneEdit, getString(R.string.user_phonenum_verify))){
			mSendSms.setEnabled(false);
			new Thread(){
				int count = SMS_WAIT_TIME;
				public void run() {
					while(!isStopThread && count > 0){
						Message msg = new Message();
						msg.what = SMS_TIME;
						msg.arg1 = count;
						mTimerHandler.sendMessage(msg);
						count--;
						if(count == 0){
							Message msg2 = new Message();
							msg2.what = SMS_AGAIN_OK;
							mTimerHandler.sendMessage(msg2);
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}.start();
			new SendSMSTask().execute(num);
		}
	}
	
	private boolean doValidate(){
		boolean flag = true;
		List<EditText> list = new ArrayList<EditText>();
		
		if(!Validator.validatePhone(mPhoneEdit, getString(R.string.user_phonenum_verify))){
			list.add(mPhoneEdit);
			flag = false;
		}
		
		
		if(!Validator.validatePassword(mPasswordEdit, getString(R.string.user_pwd_verify))){
			list.add(mPasswordEdit);
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
	
	private void doRegister(){
		if(doValidate()){
			new ChangePwdTask().execute();
		}
	}
	
	class SendSMSTask extends AsyncTask<String, Void, BaseJson>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected BaseJson doInBackground(String... params) {
			BaseJson bm = null;
			String result = mRequest.sendSMS(params[0]);
			if(!TextUtils.isEmpty(result)){
				bm = JSON.parseObject(result, BaseJson.class);
			}
			return bm;
		}
		
		@Override
		protected void onPostExecute(BaseJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != result){
				if(result.code != 0)
					showToast(result.errMsg);
				else
					showToast("短信已经发出，请注意查收");
			}
		}
		
	}
	
	class ChangePwdTask extends AsyncTask<String, Void, UserJson>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected UserJson doInBackground(String... params) {
			UserJson bm = null;
			String mobile = mPhoneEdit.getText().toString();
			String password = mPasswordEdit.getText().toString();
			String verifyCode = mVerifycodeEdit.getText().toString();
			String result = mRequest.changePwd(mobile, password, verifyCode);
			if(!TextUtils.isEmpty(result)){
				bm = JSON.parseObject(result, UserJson.class);
			}
			return bm;
		}
		
		@Override
		protected void onPostExecute(UserJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != result){
				if(result.code != 0)
					showToast(result.errMsg);
				else{
					showToast("修改密码成功");
					finish();
				}
			}
		}
		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_leftImg:
			goHomeActivity();
			break;
		case R.id.act_forgetpwd_sendSMS:
			sendSMS();
			break;
		case R.id.act_forgetpwd_submit:
			doRegister();
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		isStopThread = true;
		super.onDestroy();
	}
	
}
