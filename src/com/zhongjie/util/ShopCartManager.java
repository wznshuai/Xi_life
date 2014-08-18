package com.zhongjie.util;

import java.util.ArrayList;
import java.util.List;

import com.zhongjie.model.CommodityModel;
import com.zhongjie.model.ShopCartModel;

public class ShopCartManager {
	private static ShopCartManager mInstance;
	
	private List<ShopCartModel> mCartList;
	private ShopCartManager(){}
	
	public static ShopCartManager getInstance(){
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
				mCartList.get(index).number += 1;
			}else{
				ShopCartModel scm = new ShopCartModel();
				Utils.fatherToChild(cm, scm);
				mCartList.add(scm);
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
	
	/**
	 * 添加商品到购物车
	 * @param cm
	 */
	public void addInShopCart(CommodityModel cm, int count){
		if(cm != null){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				mCartList.get(index).number = count;
			}else{
				ShopCartModel scm = new ShopCartModel();
				Utils.fatherToChild(cm, scm);
				scm.number = count;
				mCartList.add(scm);
			}
		}
	}
	/**
	 * 获取商品在购物车中的数量
	 * @param cm
	 * @return
	 */
	public int getCommodityCount(CommodityModel cm){
		int count = 0;
		if(null != cm){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				count = mCartList.get(index).number;
			}
		}
		return count;
	}
	
	/**
	 * 获取商品在购物车中的数量
	 * @param cm
	 * @return
	 */
	public int getCommodityCount(int commodidtyId){
		int count = 0;
		CommodityModel cm = new CommodityModel();
		cm.commodityId = commodidtyId;
		if(null != cm){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				count = mCartList.get(index).number;
			}
		}
		return count;
	}
}
