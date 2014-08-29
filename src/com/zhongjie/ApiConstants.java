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
	//修改头像
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
	//获取随手够自提点列表
	public static final String URL_ESHOP_QUERY_ARAY = BASE_HOST + "eshop/queryAray";
	//获取干洗自提点列表
	public static final String URL_CLEAN_QUERY_ARAY = BASE_HOST + "clean/queryAray";
	//干洗提交订单
	public static final String URL_CLEAN_SUBMITORDER = BASE_HOST + "clean/submitOrder";
	//报修
	public static final String URL_REPAIR_SUBMIT = BASE_HOST + "repair/submit";
	//上传报修图片
	public static final String URL_REPAIR_IMAGEUPLOAD = BASE_HOST + "repair/imageUpload";
	//查看首页
	public static final String URL_HOME_SHOW= BASE_HOST + "home/show";
	//随手够提交订单
	public static final String URL_ESHOP_SUBMITORDER = BASE_HOST + "eshop/submitOrder";
	//干洗品类列表
	public static final String URL_CLEAN_QUERYLIST = BASE_HOST + "clean/queryList";
	//订单提交
	public static final String URL_ORDER_SUBMIT = BASE_HOST + "order/submit";
}
