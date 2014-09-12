package com.zhongjie.model;

public class MsgCarry
{
	private String serverVersion;
	private String downloadUrl;
	private int percent;

	public MsgCarry()
	{
		super();
	}

	public MsgCarry(String serverVersion, String downloadUrl, int percent)
	{
		super();
		this.serverVersion = serverVersion;
		this.downloadUrl = downloadUrl;
		this.percent = percent;
	}

	public int getPercent()
	{
		return percent;
	}

	public void setPercent(int percent)
	{
		this.percent = percent;
	}

	public String getServerVersion()
	{
		return serverVersion;
	}

	public void setServerVersion(String serverVersion)
	{
		this.serverVersion = serverVersion;
	}

	public String getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}
}