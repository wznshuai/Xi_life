package com.zhongjie.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;

public class Utils {
	private static int screenWidth = 0;
	private static String TAG = "Utils";

	public static int getScreenWidth(Context context) {

		if (screenWidth == 0) {
			if (context == null)
				return 0;
			screenWidth = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getWidth();
		}
		return screenWidth;
	}

	private static int screenHeight = 0;

	public static int getScreenHeight(Context context) {

		if (screenHeight == 0) {
			if (context == null)
				return 0;
			screenHeight = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getHeight();
		}
		return screenHeight;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Resources res, float dpValue) {
		final float scale = res.getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {
		/*
		 * final float scale =
		 * context.getResources().getDisplayMetrics().density; return (int)
		 * (dpValue * scale + 0.5f);
		 */
		return dp2px(context.getResources(), dpValue);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * Returns true if the string is null or 0-length.
	 * 
	 * @param str
	 *            the string to be examined
	 * @return true if str is null or zero length
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
	
	/**
	 * 父类的值赋值到子类
	 * @param father
	 * @param child
	 */
	public static void fatherToChild(Object father, Object child) {
		if (!(child.getClass().getSuperclass() == father.getClass())) {
			System.err.println("child不是father的子类");
		}
		Class<? extends Object> fatherClass = father.getClass();
		Field ff[] = fatherClass.getDeclaredFields();
		for (Field f : ff) {
			try {
				if(f.getModifiers() == Modifier.PRIVATE)
					continue;
				f.set(child, f.get(father));
			} catch (Exception e) {
				Logger.e(TAG, "fatherToChild exception", e);
			} 
		}
	}
}
