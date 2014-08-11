/** 
 * Copyright (c) 2013 xuewu.wei.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongjie.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;

public class Validator {
	public static boolean validateNull(TextView view, String notnullStr) {
		if (TextUtils.isEmpty(view.getText())) {
			view.setError(notnullStr);
			return false;
		}
		return true;
	}

	public static boolean validateContent(TextView view, String notnullStr) {
		String text=view.getText().toString().trim();
		if (text.length()>=10&&text.length()<=500) {
			return true;
		} else {
			view.setError(notnullStr);
			view.requestFocus();
			return false;
		}
	}

	public static boolean validatePassword(TextView view, String notnullStr) {
		if(!validateNull(view, "不能为空"))
			return false;
		Pattern p = Pattern.compile("[^\\s]{6,14}");
		Matcher m = p.matcher(view.getText().toString().trim());
		if (m.matches()) {
			return true;
		} else {
			view.setError(notnullStr);
			view.requestFocus();
			return false;
		}
	}

	public static boolean validateUserName(TextView view, String notnullStr) {
		Pattern p = Pattern.compile("^[\\w+$]{6,14}+$");
		Matcher m = p.matcher(view.getText().toString().trim());
		if (m.matches()) {
			p = Pattern.compile("^\\d*$");
			m = p.matcher(view.getText().toString().trim());
			if (!m.matches()) {
				return true;
			}
		}

		view.setError(notnullStr);
		view.requestFocus();
		return false;
	}

	public static boolean validateNickName(TextView view) {
		String rx = "[\\w]";
		String rx2 = "[\u4e00-\u9fa5]";
		final int MAXCOUNT = 20;
		int num = 0;
		boolean flag = true;
		String str = view.getText().toString();
		for(int i = 0;i < str.length();i++){
			char c = str.charAt(i);
			if(Pattern.compile(rx).matcher(c + "").matches()){
				num += 1;
			}else if(Pattern.compile(rx2).matcher(c + "").matches()){
				num += 2;
			}else{
				flag = false;
				break;
			}
		}
		
		if(num > MAXCOUNT)
			flag = false;
		
		if(!flag){
//			view.setError(notnullStr);
//			view.requestFocus();
		}
		
		return flag;		
	}

	public static boolean validatePhone(TextView view, String tips) {
		if (!validateNull(view, "不能为空")) {
			return false;
		} else {
//			Pattern p = Patterns.PHONE;
			Pattern p = Pattern.compile("[\\d]{11}");
			Matcher m = p.matcher(view.getText().toString().trim());
			boolean flag = m.matches();
			if(!flag){
				view.setError(tips);
				view.requestFocus();
			}
			return flag;
		}
	}

	public static boolean validateEmail(TextView view, String notnullStr) {
		Pattern p = Patterns.EMAIL_ADDRESS;
		Matcher m = p.matcher(view.getText().toString().trim());
		if (m.matches()) {
			return true;
		} else {
			view.setError(notnullStr);
			view.requestFocus();
			return false;
		}
	}

	public static boolean validateNullURL(TextView view, String notnullStr) {
		if (TextUtils.isEmpty(view.getText())) {
			view.setError(notnullStr);
			return false;
		} else {
			Pattern p = Patterns.WEB_URL;
			Matcher m = p.matcher(view.getText().toString().trim());
			return m.matches();
		}
	}

	public static boolean validateNullIP(TextView view, String notnullStr) {
		if (TextUtils.isEmpty(view.getText())) {
			view.setError(notnullStr);
			return false;
		} else {
			Pattern p = Patterns.IP_ADDRESS;
			Matcher m = p.matcher(view.getText().toString().trim());
			return m.matches();
		}
	}
}
