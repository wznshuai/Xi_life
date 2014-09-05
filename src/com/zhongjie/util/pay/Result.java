package com.zhongjie.util.pay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

public class Result {
	
	private static final Map<String, String> sResultStatus;

	private String mResult;
	
	public String resultStatus = null;
	String memo = null;
	String result = null;
	boolean isSignOk = false;
	String mOrderNo;
	String mBody;
	String mSubject;
	String mTotalFee;
	String errorCode;
	

	public Result(String result) {
		this.mResult = result;
	}

	static {
		sResultStatus = new HashMap<String, String>();
		sResultStatus.put("9000", "操作成功");
		sResultStatus.put("4000", "系统异常");
		sResultStatus.put("4001", "数据格式不正确");
		sResultStatus.put("4003", "该用户绑定的支付宝账户被冻结或不允许支付");
		sResultStatus.put("4004", "该用户已解除绑定");
		sResultStatus.put("4005", "绑定失败或没有绑定");
		sResultStatus.put("4006", "订单支付失败");
		sResultStatus.put("4010", "重新绑定账户");
		sResultStatus.put("6000", "支付服务正在进行升级操作");
		sResultStatus.put("6001", "用户中途取消支付操作");
		sResultStatus.put("7001", "网页支付失败");
		sResultStatus.put("8000", "支付结果确认中");
	}

	public  String getResult() {
		String src = mResult.replace("{", "");
		src = src.replace("}", "");
		return getContent(src, "memo=", ";result");
	}
	
	public boolean isOK(){
		parseResult();
		return isSignOk;
	}
	
	/*
	 * service="mobile.securitypay.pay"&partner="2088901724200650"
	 * &_input_charset="utf-8"&notify_url="http%3A%2F%2F42.62.67.243%2Findex.php%2Fpay%2Fnotify"
	 * &out_trade_no="1000650-5"&subject="魅力香水 新年特惠 adidas+阿迪达斯走珠 香体止+汗走珠 多种香型可选"
	 * &payment_type="1"&it_b_pay="30m"&seller_id="2088901724200650"
	 * &total_fee="0.01"&body="新年特惠 adidas 阿迪达斯走珠 香体+止汗走珠 多种香型可选"
	 * &success="true"&sign_type="RSA"
	 * &sign="RfzrS8ctZVIIMgbj0dgj504FpAnk60EaUasQXL0X5pDJ
	 * d2GTShBHEjX7YL2B4VZKU0c1ExFstMIelpS68uvcHkcvtA9WxymhiVqYI5/bxBEAL0MBfggCeR959f4kQ2tzwL++svxX44gH/
	 * Riomm0fiY7YZGb+XN17A2mTRVRnbdo="
	 */

	public String getErrorCode() {
		String src = mResult.replace("{", "");
		src = src.replace("}", "");
		errorCode = getContent(src, "resultStatus=", ";memo");
		return errorCode;
	}
	

	public  void parseResult() {
		
		try {
			String src = mResult.replace("{", "");
			src = src.replace("}", "");
			String rs = getContent(src, "resultStatus=", ";memo");
			if (sResultStatus.containsKey(rs)) {
				resultStatus = sResultStatus.get(rs);
			} else {
				resultStatus = "其他错误";
			}
			resultStatus += "(" + rs + ")";

			memo = getContent(src, "memo=", ";result");
			result = getContent(src, "result=", null);
			isSignOk = checkSign(result);
			if(isSignOk){
				String[] data = result.split("out_trade_no=\"");
				if(null != data && data.length > 1){
					mOrderNo = data[1].substring(0, data[1].indexOf("\"&"));
				}
				
				data = result.split("body=\"");
				if(null != data && data.length > 1){
					mBody = data[1].substring(0, data[1].indexOf("\"&"));
				}
				
				data = result.split("subject=\"");
				if(null != data && data.length > 1){
					mSubject = data[1].substring(0, data[1].indexOf("\"&"));
				}
				
				data = result.split("total_fee=\"");
				if(null != data && data.length > 1){
					mTotalFee = data[1].substring(0, data[1].indexOf("\"&"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private  boolean checkSign(String result) {
		boolean retVal = false;
		try {
			JSONObject json = string2JSON(result, "&");

			int pos = result.indexOf("&sign_type=");
			String signContent = result.substring(0, pos);

			String signType = json.getString("sign_type");
			signType = signType.replace("\"", "");

			String sign = json.getString("sign");
			sign = sign.replace("\"", "");
			
			if (signType.equalsIgnoreCase("RSA")) {
				retVal = Rsa.doCheck(signContent, sign, Keys.PUBLIC);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("Result", "Exception =" + e);
		}
		Log.i("Result", "checkSign =" + retVal);
		return retVal;
	}

	public  JSONObject string2JSON(String src, String split) {
		JSONObject json = new JSONObject();

		try {
			String[] arr = src.split(split);
			for (int i = 0; i < arr.length; i++) {
				String[] arrKey = arr[i].split("=");
				json.put(arrKey[0], arr[i].substring(arrKey[0].length() + 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	private  String getContent(String src, String startTag, String endTag) {
		String content = src;
		int start = src.indexOf(startTag);
		start += startTag.length();

		try {
			if (endTag != null) {
				int end = src.indexOf(endTag);
				content = src.substring(start, end);
			} else {
				content = src.substring(start);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}

	public String getmOrderNo() {
		return mOrderNo;
	}

	public String getmBody() {
		return mBody;
	}

	public String getmSubject() {
		return mSubject;
	}

	public String getmTotalFee() {
		return mTotalFee;
	}
	
	
}
