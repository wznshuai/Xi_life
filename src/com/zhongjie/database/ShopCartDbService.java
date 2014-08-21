package com.zhongjie.database;

import java.util.List;

import com.zhongjie.MyApplication;
import com.zhongjie.model.ShopCartModel;
import com.zhongjie.model.UserModelManager;

public class ShopCartDbService {
	public static void save(ShopCartModel sm) {
		UserModelManager um = UserModelManager.getInstance();
		if (null != um && um.isLogin()) {
			sm.sessId = um.getmUser().sessId;
			MyApplication.finalDb.save(sm);
		}
	}

	public static void saveForBatch(List<ShopCartModel> smList) {
		UserModelManager um = UserModelManager.getInstance();
		if (null != um && um.isLogin()) {
			for (ShopCartModel sm : smList)
				sm.sessId = um.getmUser().sessId;
			MyApplication.finalDb.saveForBatch(smList);
		}
	}

	public static void updateForBatch(List<ShopCartModel> smList) {
		UserModelManager um = UserModelManager.getInstance();
		if (null != um && um.isLogin()) {
			for (ShopCartModel sm : smList)
				sm.sessId = um.getmUser().sessId;
			MyApplication.finalDb.updateForBatch(smList);
		}
	}

	public static List<ShopCartModel> findAll() {
		UserModelManager um = UserModelManager.getInstance();
		if (null != um && um.isLogin()) {
			return MyApplication.finalDb.findAllByWhere(ShopCartModel.class, "sessId='" + um.getmUser().sessId + "'");
		}
		return null;
	}
	
	public static void dellAll(){
		UserModelManager um = UserModelManager.getInstance();
		if (null != um && um.isLogin()) {
			MyApplication.finalDb.deleteByWhere(ShopCartModel.class, "sessId='" + um.getmUser().sessId + "'");
		}
	}
}
