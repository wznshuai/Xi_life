package com.zhongjie.util;

import java.util.ArrayList;
import java.util.List;

import com.zhongjie.model.CommodityModel;
import com.zhongjie.model.ShopCartModel;

public class ShopCartManager {
	private static ShopCartManager mInstance;
	
	private List<ShopCartModel> mCartList;
	private ShopCartManager(){}
	
	public static ShopCartManager newInstance(){
		if(null == mInstance){
			mInstance = new ShopCartManager();
			mInstance.mCartList = new ArrayList<ShopCartModel>();
		}
			
		return mInstance;
	}
	/**
	 * 添加商品到购物车
	 * @param cm
	 */
	public void addInShopCart(CommodityModel cm){
		if(cm != null){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				mCartList.get(index).count += 1;
			}else{
				mCartList.add(new ShopCartModel(cm));
			}
		}
	}
	/**
	 * 得到购物车
	 * @return
	 */
	public List<ShopCartModel> getShopCar(){
		return mCartList;
	}
}
