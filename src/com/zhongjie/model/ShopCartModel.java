package com.zhongjie.model;

public class ShopCartModel extends CommodityModel{
	public int count;//在购物车的数量
	
	public ShopCartModel(CommodityModel cm){
		if(null != cm){
			this.commodityId = cm.commodityId;
			this.count = 0;
			this.image = cm.image;
			this.info = cm.info;
			this.name = cm.name;
			this.oldPrice = cm.oldPrice;
			this.price = cm.price;
			this.weight = cm.weight;
		}
	}
}
