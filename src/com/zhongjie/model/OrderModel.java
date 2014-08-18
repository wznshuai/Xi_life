package com.zhongjie.model;

import java.util.List;

public class OrderModel {
	 public String orderId;//<String>:订单ID
     public String orderMoney;//<String>:订单金额
     public List<ShopCartModel> orderDetail;//<List>:订单购买商品信息
     public String orderStatus;//<String>:订单状态(00:待支付; 01:已支付; 90:已完成; 99:已取消)
}
