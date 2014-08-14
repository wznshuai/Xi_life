package com.zhongjie.model;

public class CommodityModel {
	public int commodityId;// <String>:商品ID
	public String name;// <String>:商品名称
	public String image;// <String>:商品简图
	public String info;// <String>:商品简介
	public String weight;// <String>:商品重量
	public String price;// <String>:商品价格(元)
	public String oldPrice;// <String>:商品原价(元)
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		else if(o == this)
			return true;
		else if(o instanceof CommodityModel){
			if(((CommodityModel)o).commodityId == commodityId)
				return true;
		}
		return false;
	}
}
