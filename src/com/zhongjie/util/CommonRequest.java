package com.zhongjie.util;

import java.util.HashMap;

import android.content.Context;

import com.zhongjie.ApiConstants;
import com.zhongjie.http.HttpUtil;

public class CommonRequest {
	
	private HttpUtil mHttpUtil;
	
	public CommonRequest(Context appContext){
		mHttpUtil = new HttpUtil(appContext.getApplicationContext());
	}
	
	/**
	 * 用戶註冊
	 * @param mobile
	 * @param password
	 * @param code
	 */
	public String register(String mobile, String password, String code){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("mobile", mobile);
		data.put("password", password);
		data.put("code", code);
		return mHttpUtil.executePost(ApiConstants.URL_USER_REGISTER, data);
	}
	/**
	 * 获取短信验证码
	 * @param mobile
	 * @return
	 */
	public String sendSMS(String mobile){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("mobile", mobile);
		return mHttpUtil.executePost(ApiConstants.URL_SEND_SMS, data);
	}
	/**
	 * 获取用户资料
	 * @param sessId
	 * @return
	 */
	public String queryUserInfo(String sessId){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("sessId", sessId);
		return mHttpUtil.executePost(ApiConstants.URL_USER_INFO, data);
	}
	
	public String doLogin(String mobile, String pwd){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("mobile", mobile);
		data.put("password", pwd);
		return mHttpUtil.executePost(ApiConstants.URL_USER_LOGIN, data);
	}
}
