package com.zhongjie.model;

public class CheckVersionModel {
	public String versionCode;//<String>:服务器最新版本号
	public int updateFlag;//<String>:更新标识(0:当前已是最新版本,不需要更新; 1:有新版本,不强制更新; 2:有新版本,强制更新)
	public String updateUrl;//<String>:更新URL(updateFlag为1或2时显示)
}
