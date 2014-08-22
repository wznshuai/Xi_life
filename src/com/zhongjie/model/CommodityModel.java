package com.zhongjie.model;

import com.zhongjie.util.Utils;


public class CommodityModel {
	public String commodityId;// <String>:商品ID
	public String name;// <String>:商品名称
	public String image;// <String>:商品简图
	public String info;// <String>:商品简介
	public String weight;// <String>:商品重量
	public String price;// <String>:商品价格(元)
	public String oldPrice;// <String>:商品原价(元)

	public String imageA;// <String>:商品图片一
	public String imageB;// <String>:商品图片二
	public String imageC;// <String>:商品图片三
	public int descMode;// <String>:商品详情模板(1:第一套; 2:第二套; ...)
	public String detail;// <String>:商品详情文字描述
	public String[] taste;// <List>:商品口味
	public String[] detailImage;// <List>:商品详情图片描述
	public int evaluate;// <String>:商品评价人数
	public int start;// <String>:商品评价星级(1:一颗星; 2:两颗星; ...)
	public int good;// <String>:商品好评度(0-100 的整数)
	public String selectedTaste;//客户选择的口味

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		else if(o == this)
			return true;
		else if(o instanceof CommodityModel){
			if(((CommodityModel)o).commodityId.equals(commodityId)){
				if(Utils.isEmpty(selectedTaste) && Utils.isEmpty(((CommodityModel)o).selectedTaste)){
					return true;
				}else if(!Utils.isEmpty(selectedTaste) 
						&& !Utils.isEmpty(((CommodityModel)o).selectedTaste)
						&& selectedTaste.equals(((CommodityModel)o).selectedTaste)){
					return true;
				}
			}
		}
		return false;
	}
}
