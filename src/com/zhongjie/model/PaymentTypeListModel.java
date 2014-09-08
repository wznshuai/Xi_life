package com.zhongjie.model;

import java.util.List;

public class PaymentTypeListModel {
	public String unit;//<String>:单元
    public String room;//<String>:房间
    public String year;//<String>:年份
    public String quarter;//<String>:季度
    public List<PaymentTypeModel> item;//<List>:缴费明细
    public String totalMoney;//<String>:总金额
    public String code;//<String>:缴费订单号
    public String payInfo;//支付宝调用信息
}
