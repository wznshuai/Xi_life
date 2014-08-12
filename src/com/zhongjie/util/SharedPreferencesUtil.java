package com.zhongjie.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

public class SharedPreferencesUtil {

	private static SharedPreferencesUtil instance;
	private SharedPreferences settings;

	private SharedPreferencesUtil(Context context) {
		settings = context.getSharedPreferences("XiLife_data", Context.MODE_PRIVATE);
	}

	public static SharedPreferencesUtil getInstance(Context context) {
		
		if (instance == null) {
			synchronized (SharedPreferencesUtil.class) {
				instance = new SharedPreferencesUtil(
						context.getApplicationContext());
			}
		}

		return instance;

	}

	public void saveString(String key, String value) {
		synchronized (settings) {
			settings.edit().putString(key, value).commit();
		}
	}

	public String getString(String key) {
		synchronized (settings) {
			return settings.getString(key, "");
		}
	}

	public void saveInt(String key, int value) {
		synchronized (settings) {
			settings.edit().putInt(key, value).commit();
		}
	}

	public int getInt(String key, int defaultValue) {
		synchronized (settings) {
			return settings.getInt(key, defaultValue);
		}
	}

	public Double getOptDouble(String key) {
		synchronized (settings) {
			String retStr = settings.getString(key, null);
			Double ret = null;
			try {
				ret = Double.parseDouble(retStr);
			} catch (Exception e) {
			}
			return ret;
		}
	}

	public Boolean getOptBoolean(String key) {
		synchronized (settings) {
			String retStr = settings.getString(key, null);
			Boolean ret = null;
			try {
				ret = Boolean.parseBoolean(retStr);
			} catch (Exception e) {
			}
			return ret;
		}
	}

	public Double getDouble(String key) {
		synchronized (settings) {
			String retStr = settings.getString(key, null);
			Double ret = null;
			try {
				if (retStr != null) {
					ret = Double.parseDouble(retStr);
				} else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
			return ret;
		}
	}

	public void saveHashMap(final String key, HashMap<String, String> map) {
		final JSONObject ret = new JSONObject(map);
		synchronized (settings) {
			settings.edit().putString(key, ret.toString()).commit();
		}
	}

	public HashMap<String, String> getHashMapByKey(String key) {
		HashMap<String, String> ret = new HashMap<String, String>();
		synchronized (settings) {
			String mapStr = settings.getString(key, "{}");
			JSONObject mapJson = null;
			try {
				mapJson = new JSONObject(mapStr);
			} catch (Exception e) {
				return ret;
			}

			if (mapJson != null) {
				Iterator<String> it = mapJson.keys();
				while (it.hasNext()) {
					String theKey = it.next();
					String theValue = mapJson.optString(theKey);
					ret.put(theKey, theValue);
				}
			}
		}
		return ret;
	}

	public void saveBoolean(String key, boolean bool) {
		synchronized (settings) {
			settings.edit().putBoolean(key, bool).commit();
		}
	}

	public boolean getBoolean(String key) {
		synchronized (settings) {
			return settings.getBoolean(key, false);
		}
	}
	public boolean getBoolean(String key,boolean is) {
		synchronized (settings) {
			return settings.getBoolean(key, is);
		}
	}
	public void saveArrayList(String key, ArrayList<String> list) {
		synchronized (settings) {
			settings.edit().putString(key, JSON.toJSONString(list)).commit();
		}
	}

	public ArrayList<String> getArrayList(String key) {
		ArrayList<String> ret = new ArrayList<String>();
		synchronized (settings) {
			String listStr = settings.getString(key, "{}");
			JSONArray listJson = null;
			try {
				listJson = new JSONArray(listStr);
			} catch (Exception e) {
				return ret;
			}

			if (listJson != null) {
				for (int i = 0; i < listJson.length(); i++) {
					String temp = listJson.optString(i);
					ret.add(temp);
				}
			}
		}
		return ret;
	}

	public void removeByKey(String key) {
		settings.edit().remove(key).commit();
	}

	public boolean contains(String alarmHour) {
		
		return settings.contains(alarmHour);
	}
}
