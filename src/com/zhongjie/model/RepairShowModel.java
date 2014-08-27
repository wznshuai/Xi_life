package com.zhongjie.model;


public class RepairShowModel {
	 public String unit;//<String>:单元
     public String room;//<String>:房间
     public String repairDate;//最大预约时间（yyyy-MM-dd）
     public String[] classify;//<List>:报修分类
     public String needClassify;//要报修分类的名字
     public String image;//报修图片的地址
     public String needRepairDate;//客户选择的预约时间
}
