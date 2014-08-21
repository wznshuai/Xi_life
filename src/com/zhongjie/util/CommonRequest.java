package com.zhongjie.util;

import java.io.File;
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
	/**
	 * 登录
	 * @param mobile
	 * @param pwd
	 * @return
	 */
	public String doLogin(String mobile, String pwd){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("mobile", mobile);
		data.put("password", pwd);
		return mHttpUtil.executePost(ApiConstants.URL_USER_LOGIN, data);
	}
	/**
	 * 修改用户资料
	 * @param sessId
	 * @param nickName
	 * @param image
	 * @param unit
	 * @param romm
	 * @return
	 */
	public String modifyUserInfo(String sessId, String nickName, String image, String unit, String romm){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("sessId", sessId);
		data.put("nickName", nickName);
		data.put("image", image);
		data.put("unit", unit);
		data.put("room", romm);
		return mHttpUtil.executePost(ApiConstants.URL_USER_MODIFY_INFO, data);
	}
	
	/**
	 * 上传图片
	 * @param imageFile
	 * @return
	 */
	public String uploadImage(File... imageFile){
		HashMap<String, String> data = new HashMap<String, String>();
		return mHttpUtil.executePost(ApiConstants.URL_USER_UPLOADIMAGE, data, "file", imageFile);
	}
	/**
	 * 随手够商品目录
	 * @param imageFile
	 * @return
	 */
	public String queryEshopCatelog(){
		return mHttpUtil.executeGet(ApiConstants.URL_ESHOP_CATALOG);
	}
	/**
	 * 查询商品列表
	 * @param catagoryId 商品分类ID
	 * @param start 开始页数
	 * @param step 每页长度
	 * @return
	 */
	public String queryCommodityList(String catalogId, int start, int step){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("catalogId", catalogId);
		data.put("start", start + "");
		data.put("step", step + "");
		return mHttpUtil.executeGet(ApiConstants.URL_ESHOP_COMMODITYLIST, data);
	}
	/**
	 * 查询商品详情
	 * @param commodityId
	 * @return
	 */
	public String queryCommodityDetails(String commodityId){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("commodityId", commodityId);
		return mHttpUtil.executeGet(ApiConstants.URL_ESHOP_COMMODITY_DETAILS, data);
	}
	/**
	 * 查询用户订单
	 * @param sessId 用户唯一吗
	 * @param status 订单状态 (00:待支付; 01:已支付; 90:已完成; 99:已取消)
	 * @param start 开始页数
	 * @param step 每页长度
	 * @return
	 */
	public String queryUserOrder(String sessId, String status, int start, int step){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("sessId", sessId);
		data.put("status", status);
		data.put("start", start + "");
		data.put("step", step + "");
		return mHttpUtil.executeGet(ApiConstants.URL_ESHOP_USER_ORDER, data);
	}
	/**
	 * 获取报修页面信息
	 * @param sessId
	 * @return
	 */
	public String repairShow(String sessId){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("sessId", sessId);
		return mHttpUtil.executePost(ApiConstants.URL_REPAIR_SHOW, data);
	}
	/**
	 * 获取报修页面信息
	 * @param sessId
	 * @return
	 */
	public String queryAray(){
		return mHttpUtil.executeGet(ApiConstants.URL_ESHOP_QUERY_ARAY);
	}
}
