package com.zhongjie.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.zhongjie.model.CommodityModel;
import com.zhongjie.model.ShopCartModel;

public class ShopCartManager {
	public static final String SAVE_KEY = "ShopCartManager";
	
	private static ShopCartManager mInstance;
	
	private List<ShopCartModel> mCartList;
	public List<ShopCartModel> mCheckedList;

	private ShopCartManager(){}
	
	public static ShopCartManager getInstance(){
		if(null == mInstance){
			mInstance = new ShopCartManager();
			mInstance.mCartList = new ArrayList<ShopCartModel>();
		}
			
		return mInstance;
	}
	
	public static ShopCartManager getInstance(List<ShopCartModel> mCartList){
		if(null == mInstance){
			mInstance = new ShopCartManager();
			if(null != mCartList)
				mInstance.mCartList = mCartList;
			else
				mInstance.mCartList = new ArrayList<ShopCartModel>();
		}else{
			mInstance.mCartList = mCartList;
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
				scm.number = 1;
				mCartList.add(scm);
			}
		}
	}
	
	/**
	 * 得到购物车
	 * @return
	 */
	public List<ShopCartModel> getShopCart(){
		return mCartList;
	}
	
	/**
	 * 添加商品到购物车
	 * @param cm
	 */
	public void addInShopCart(CommodityModel cm, int count){
		addInShopCart(cm, count, true);
	}
	
	/**
	 * 添加商品到购物车
	 * @param cm
	 * @param count 数量
	 * @param isRealCount 参数count是否为商品在购物车里的真实数量(true 为真 , false 为当前购物车中此商品再增加的数量)
	 */
	public void addInShopCart(CommodityModel cm, int count, boolean isRealCount){
		if(cm != null){
			int index = mCartList.indexOf(cm);
			if(-1 != index){
				if(isRealCount)
					mCartList.get(index).number = count;
				else
					mCartList.get(index).number += count;
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
	public int getCommodityCount(String commodidtyId){
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
	
	/**
	 * 获得购物车物品的总数量
	 * @return
	 */
	public int getTotalCount(){
		int totalCount = 0;
		if(null != mCartList){
			for(ShopCartModel scm : mCartList){
				totalCount += scm.number;
			}
			return totalCount;
		}
		return totalCount;
	}
	
	public void save(Context context){
		if(null != mCartList && mCartList.size() > 0){
			SharedPreferencesUtil.getInstance(context).saveString(SAVE_KEY, JSON.toJSONString(mCartList));
		}
	}
	
	public void readDataFromSavd(Context context){
		String data = SharedPreferencesUtil.getInstance(context).getString(SAVE_KEY);
		if(!Utils.isEmpty(data)){
			mCartList = JSON.parseArray(data, ShopCartModel.class);
		}
	}
	
	public void removeFromCheckedList(ShopCartModel scm){
		if(null != mCheckedList)
			mCheckedList.remove(scm);
	}
	
	public void addInCheckedList(ShopCartModel scm){
		if(null == mCheckedList)
			mCheckedList = new ArrayList<ShopCartModel>();
		mCheckedList.add(scm);
	}
}
