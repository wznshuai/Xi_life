package com.zhongjie;

public class ApiConstants {
	public static final String BASE_HOST = "http://appserver.vbangke.com/";
	//用戶註冊
	public static final String URL_USER_REGISTER = BASE_HOST + "user/register";
	//发短信
	public static final String URL_SEND_SMS = BASE_HOST + "user/sendSMS";
	//用户登录
	public static final String URL_USER_LOGIN = BASE_HOST + "user/login";
	//用户资料
	public static final String URL_USER_INFO = BASE_HOST + "user/info";
	//修改资料
	public static final String URL_USER_MODIFY_INFO = BASE_HOST + "user/modifyUserInfo";
	//上传头像
	public static final String URL_USER_UPLOADIMAGE = BASE_HOST + "user/imageUpload";
	//随收购目录
	public static final String URL_ESHOP_CATALOG = BASE_HOST + "eshop/queryCatalog";
	//查询商品列表
	public static final String URL_ESHOP_COMMODITYLIST = BASE_HOST + "eshop/queryList";
	//查询商品详情
	public static final String URL_ESHOP_COMMODITY_DETAILS = BASE_HOST + "eshop/query";
	//用户订单
	public static final String URL_ESHOP_USER_ORDER = BASE_HOST + "eshop/queryOrderByUser";
	//报修页面
	public static final String URL_REPAIR_SHOW = BASE_HOST + "repair/show";
}
