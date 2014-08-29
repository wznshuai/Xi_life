package com.zhongjie.model;


public class DryCleanModel {
	public String cleanId;//<String>:品类商品ID
    public String name;//<String>:品类商品名称
    public String image;//<String>:品类商品简图
    public String price;//<String>:品类商品价格(元)
    public int count = 1;//添加到购物车的数量
    
    @Override
    public boolean equals(Object o) {
		if(o == null)
			return false;
		else if(o == this)
			return true;
		else if(o instanceof DryCleanModel){
			if(((DryCleanModel)o).cleanId.equals(cleanId)){
				return true;
			}
		}
		return false;
	}
}
