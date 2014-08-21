package com.zhongjie.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.model.UserModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.Constants;
import com.zhongjie.util.SharedPreferencesUtil;
import com.zhongjie.util.ShopCartManager;


public class WelcomeActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		new AutoLoginTask().execute();
	}

	class AutoLoginTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ShopCartManager.getInstance().readDataFromSavd(getApplicationContext());
			String sessId = SharedPreferencesUtil.getInstance(getApplicationContext()).getString(Constants.USER_SESSID);
			if(null != sessId && !TextUtils.isEmpty(sessId)){
				UserModel um = new UserModel();
				um.sessId = sessId;
				UserModelManager.getInstance().setmUser(um);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
			finish();
		}
	}
	
}
