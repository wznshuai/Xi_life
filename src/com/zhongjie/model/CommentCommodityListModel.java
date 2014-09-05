package com.zhongjie.model;

import java.util.List;

public class CommentCommodityListModel {
	 public String evaluate;//<String>:商品评价人数
	 public String start = "0";//<String>:商品评价星级(1:一颗星; 2:两颗星; ...)
	 public String good;//<String>:商品好评度(0-100 的整数)
	 public List<CommentCommodiytModel> detail;
}
