package com.zhongjie.model;

public class SubmitCommodityModel {
	public int count;
	public String commodityId;
	public String taste;
	
	@SuppressWarnings("unused")
	private SubmitCommodityModel() {
		super();
	}

	public SubmitCommodityModel(int count, String commodityId, String taste) {
		super();
		this.count = count;
		this.commodityId = commodityId;
		this.taste = taste;
	}
}
