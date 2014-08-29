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
	 * 获取自提点列表
	 * @param type 0为随手够， 1为干洗
	 * @return
	 */
	public String queryAray(int type){
		String url = null;
		if(type == 0)
			url = ApiConstants.URL_ESHOP_QUERY_ARAY;
		else if(type == 1)
			url = ApiConstants.URL_CLEAN_QUERY_ARAY;
		return mHttpUtil.executeGet(url);
	}
	
	
	/**
	 * 干洗提交订单
	 * @param sessId
	 * @param cleanInfo
	 * @param takeTime
	 * @param dispatchMode
	 * @param arayacakId
	 * @param man
	 * @param phone
	 * @param address
	 * @param remark
	 * @return
	 */
	public String submitCLeanDryOrder(String sessId, String cleanInfo, String takeTime, 
			String dispatchMode, String arayacakId, 
			String man, String phone, String address, String remark){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("sessId", sessId);
		data.put("cleanInfo", cleanInfo);
		data.put("takeTime", takeTime);
		data.put("dispatchMode", dispatchMode);
		data.put("arayacakId", arayacakId);
		data.put("man", man);
		data.put("phone", phone);
		data.put("address", address);
		data.put("remark", remark);
		return mHttpUtil.executePost(ApiConstants.URL_CLEAN_SUBMITORDER, data);
	}
	/**
	 * 报修
	 * @param sessID
	 * @param year
	 * @param month
	 * @param day
	 * @param classify
	 * @param image
	 * @return
	 */
	public String repairSubmit(String sessId, String repairDate, String classify, String image){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("sessId", sessId);
		data.put("repairDate", repairDate);
		data.put("classify", classify);
		data.put("image", image);
		return mHttpUtil.executePost(ApiConstants.URL_REPAIR_SUBMIT, data);
	}
	
	/**
	 * 上传报修图片
	 * @param imageFile
	 * @return
	 */
	public String repairImageUpload(File... imageFile){
		HashMap<String, String> data = new HashMap<String, String>();
		return mHttpUtil.executePost(ApiConstants.URL_REPAIR_IMAGEUPLOAD, data, "file", imageFile);
	}
	/**
	 * 查看首页
	 * @return
	 */
	public String repairImageUpload(){
		return mHttpUtil.executeGet(ApiConstants.URL_HOME_SHOW);
	}
	/**
	 * 随手够提交订单
	 * @param type 类型(01:随手购; 02:干洗)
	 * @param sessId
	 * @param commodityInfo
	 * @param takeTime 预约取件时间(YYYY-MM-DD) (type=02, 干洗时使用, 必填)
	 * @param dispatchMode
	 * @param arayacakId
	 * @param man
	 * @param phone
	 * @param invoice 发票抬头(type=01, 随手购时使用, 非必填)
	 * @param address
	 * @param remark
	 * @return
	 */
	public String submitOrder(String type, String sessId, String commodityInfo, String takeTime, String dispatchMode, 
			String arayacakId,  
			String man, String phone, String invoice, String address, String remark){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("type", type);
		data.put("sessId", sessId);
		data.put("commodityInfo", commodityInfo);
		data.put("takeTime", takeTime);
		data.put("dispatchMode", dispatchMode);
		data.put("arayacakId", arayacakId);
		data.put("man", man);
		data.put("invoice", invoice);
		data.put("phone", phone);
		data.put("address", address);
		data.put("remark", remark);
		return mHttpUtil.executePost(ApiConstants.URL_ORDER_SUBMIT, data);
	}
	/**
	 * 查询干洗品类列表
	 * @param catagoryId 商品分类ID
	 * @param start 开始页数
	 * @param step 每页长度
	 * @return
	 */
	public String queryCleanList(int start, int step){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("start", start + "");
		data.put("step", step + "");
		return mHttpUtil.executeGet(ApiConstants.URL_CLEAN_QUERYLIST, data);
	}
}
