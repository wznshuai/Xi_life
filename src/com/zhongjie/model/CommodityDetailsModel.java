package com.zhongjie.model;

public class CommodityDetailsModel extends CommodityModel{
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
}
