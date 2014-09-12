package com.zhongjie.util;

import android.os.Environment;

public class Constants {
	public static final String APP_DIR = Environment
			.getExternalStorageDirectory() + "/Xi_Life";
	public static final String APP_TEMP = APP_DIR + "/temp";
	public static final String APP_IMAGE = APP_DIR + "/image";
	public static final String APP_DB_DIR = APP_DIR + "/database";
	public static final String APP_HTTP_CACHE = APP_DIR + "/httpcache";
	public static final String UPDATE_DIR = APP_DIR + "/update";
	public static final String SHOP_CART_KEY = "SHOPPING_CAR_KEY";
	
	public static final String MAINACTIVITY_TAB_KEY = "MAINACTIVITY_TAB_KEY";
	
	public static final String GO_USERINFO_EDIT = "GO_USERINFO_EDIT";
	
	public static final String USER_SESSID = "USER_SESSID";
	
}
