package com.zhongjie.util;

import java.util.ArrayList;
import java.util.List;

import com.zhongjie.model.DryCleanModel;

public class ShopCartManagerForDryClean {
	
	private static ShopCartManagerForDryClean mInstance;
	
	public List<DryCleanModel> mCartList;

	private ShopCartManagerForDryClean(){}
	
	public static ShopCartManagerForDryClean getInstance(){
		if(null == mInstance){
			mInstance = new ShopCartManagerForDryClean();
			mInstance.mCartList = new ArrayList<DryCleanModel>();
		}
			
		return mInstance;
	}
	
	public static ShopCartManagerForDryClean getInstance(List<DryCleanModel> mCartList){
		if(null == mInstance){
			mInstance = new ShopCartManagerForDryClean();
			if(null != mCartList)
				mInstance.mCartList = mCartList;
			else
				mInstance.mCartList = new ArrayList<DryCleanModel>();
		}else{
			mInstance.mCartList = mCartList;
		}
			
		return mInstance;
	}
	
	
	/**
	 * 添加商品到购物车
	 * @param cm
	 */
	public void addInShopCart(DryCleanModel cm){
		if(cm != null){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				mCartList.get(index).count += cm.count;
			}else{
				mCartList.add(cm);
			}
		}
	}
	
	/**
	 * 得到购物车
	 * @return
	 */
	public List<DryCleanModel> getShopCart(){
		return mCartList;
	}
	
	/**
	 * 添加商品到购物车
	 * @param cm
	 */
	public void addInShopCart(DryCleanModel cm, int count){
		addInShopCart(cm, count, true);
	}
	
	/**
	 * 添加商品到购物车
	 * @param cm
	 * @param count 数量
	 * @param isRealCount 参数count是否为商品在购物车里的真实数量(true 为真 , false 为当前购物车中此商品再增加的数量)
	 */
	public void addInShopCart(DryCleanModel cm, int count, boolean isRealCount){
		if(cm != null){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				if(isRealCount)
					mCartList.get(index).count = count;
				else
					mCartList.get(index).count += count;
			}else{
				cm.count = count;
				mCartList.add(cm);
			}
		}
	}
	
	
	
	/**
	 * 获取商品在购物车中的数量
	 * @param cm
	 * @return
	 */
	public int getCommodityCount(DryCleanModel cm){
		int count = 0;
		if(null != cm){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				count = mCartList.get(index).count;
			}
		}
		return count;
	}
	
	/**
	 * 获取商品在购物车中的数量
	 * @param cm
	 * @return
	 */
	public int getCommodityCount(String cleanId){
		int count = 0;
		DryCleanModel cm = new DryCleanModel();
		cm.cleanId = cleanId;
		if(null != cm){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				count = mCartList.get(index).count;
			}
		}
		return count;
	}
	
	/**
	 * 获得购物车物品的总数量
	 * @return
	 */
	public int getTotalCount(){
		int totalCount = 0;
		if(null != mCartList){
			for(DryCleanModel scm : mCartList){
				totalCount += scm.count;
			}
			return totalCount;
		}
		return totalCount;
	}
}
