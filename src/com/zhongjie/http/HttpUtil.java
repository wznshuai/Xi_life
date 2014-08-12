package com.zhongjie.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.zhongjie.MyApplication;
import com.zhongjie.util.Logger;

/**
 * 
 * 
 *         說明：所有http请求都应该通过这个类，包含加密算法，后端如有必要可以进行验证：key的值，
 *         具体算法详见encryptPostParams方法
 * 
 */

public class HttpUtil {

	private final static String TAG = "HttpUtil";
	private MyApplication myApplication;

	// private String privateKey = "cacd33f7714d675c432e659b5289949a";//
	// “ting_phone”一次md5值加密

	public HttpUtil(Context context) {
		if (context != null) {
			myApplication = (MyApplication) context.getApplicationContext();
		}
	}

	public String executeGet(String url) {
		return executeGet(url, new HashMap<String, String>());
	}

	public String executeGet(String url, HashMap<String, String> params) {
		String result = null;
		try {
			HttpClient client = myApplication.getHttpClient(); // 获取HttpClient实例
			if (client == null) {
				Logger.d(TAG, "获取 client为null");
				return result;
			}
			if (params != null && params.size() != 0)
				url = url + encryptGetParams(params);
			Logger.d(TAG, url);
			HttpGet get = putGetHeadParams(url);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
				Logger.d(TAG, result);
			}
		} catch (Exception e) {
			Logger.e(TAG, "in executeGet error", e);
		}
		return result;
	}

	private HttpGet putGetHeadParams(String url) {
		HttpGet httpGet = new HttpGet(url);// user-agent:
											// ting/MI-ONE/Android15/1.5.28
		httpGet.addHeader("Accept", "*/*");
		return httpGet;
	}

	private HttpPost putPostHeadParams(String url) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Accept", "*/*");
		return httpPost;
	}

	private String encryptGetParams(HashMap<String, String> params)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String encryptString = "?";
		// if (AppConstants.isAddCookie) {
		if (params.containsKey("uid")) {
			params.remove("uid");
		}
		if (params.containsKey("token")) {
			params.remove("token");
		}
		// } else {
		// // 公共参数
		params.put("device", "android");
		// params.put("version", myApplication.getVersion());
		// params.put("deviceId", myApplication.getDeviceId());
		// }
		ArrayList<String> alist = new ArrayList<String>();
		// alist.add(privateKey);
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				if (value == null || value.trim().equals("")) {
					it.remove();
					params.remove(key);
				} else {
					// value = value.replaceAll(" ", "%20");
					// encryptString += key + "=" + value + "&";
					encryptString += key + "="
							+ URLEncoder.encode(value, "utf-8") + "&";
					alist.add(value);
				}

			}
		}
		// Collections.sort(alist);
		// String keyString = "";
		// for (String s : alist) {
		// keyString += s;
		// }
		// keyString = MD5.md5(keyString);
		// encryptString += "key=" + keyString;
		if (encryptString.length() > 2
				&& encryptString.charAt(encryptString.length() - 1) == '&') {
			encryptString = encryptString.substring(0,
					encryptString.length() - 1);
		}
		return encryptString;
	}

	private void encryptPostParams(HttpPost httpPost,
			HashMap<String, String> params, String url)
			throws UnsupportedEncodingException {

		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		params.put("device", "android");
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			if (value == null || value.trim().equals("")) {
				it.remove();
				params.remove(key);
			} else {
				nameValuePairList.add(new BasicNameValuePair(key, value));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList,
				HTTP.UTF_8));
		StringBuffer sb = new StringBuffer("?");
		for (NameValuePair nvp : nameValuePairList) {
			sb.append(nvp.getName() + "=" + nvp.getValue());
			sb.append("&");
		}
		Logger.d(TAG, "RemoteRequester:doPost : " + url + sb);
	}

	public String executePost(String url) {
		return executePost(url, new HashMap<String, String>());
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @param key
	 *            传 file时的键名
	 * @param files
	 *            要上传的文件
	 * @return
	 */
	public String executePost(String url, HashMap<String, String> params,
			String key, File... files) {
		String result = null;
		HttpPost httpPost = putPostHeadParams(url);
		HttpContext localContext = new BasicHttpContext();
		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			encryptPostParams(entity, params);
			if (files != null) {
				for (File myfile : files) {
					if (myfile != null) {
						entity.addPart(key, new FileBody(myfile, "image/jpeg"));
					}
				}
			}
			httpPost.setEntity(entity);

			HttpResponse response = myApplication.getHttpClient().execute(
					httpPost, localContext);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			} 

		} catch (Exception e) {
			Logger.e(TAG , "RemoteRequester:doPost : " , e);
		}
		return result;
	}
	
	private void encryptPostParams(MultipartEntity entity,
			HashMap<String, String> params) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		ArrayList<String> alist = new ArrayList<String>();

		// 公共参数
		params.put("device", "android");

		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			if (value == null || value.trim().equals("")) {
				it.remove();
				params.remove(key);
			} else {
				alist.add(value);
				entity.addPart(key,
						new StringBody(value, Charset.forName(HTTP.UTF_8)));
			}
		}
	}


	/**
	 * 
	 * 请使用这个方法进行post请求
	 * 
	 * */
	public String executePost(String url, HashMap<String, String> params) {

		String result = null;
		HttpPost httpPost = putPostHeadParams(url);
		HttpContext localContext = new BasicHttpContext();
		try {
			encryptPostParams(httpPost, params, url);
			HttpResponse response = myApplication.getHttpClient().execute(
					httpPost, localContext);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
				Logger.d(TAG, result);
			}
		} catch (Exception e) {

			Logger.e(TAG, "int executePost" ,e);
		}
		return result;
	}
}
