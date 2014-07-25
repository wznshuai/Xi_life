package com.zhongjie.model;

public class UserModelManager {
	private static UserModelManager mUserManager = null;
	private UserModel mUser;
	
	private UserModelManager(){}
	
	public static UserModelManager getInstance(){
		synchronized (UserModelManager.class) {
			if(null == mUserManager){
				mUserManager = new UserModelManager();			
			}
		}
		return mUserManager;
	}
	
	public boolean isLogin(){
		return null == mUser ? false : true;
	}
	
	public UserModel getmUser() {
		return mUser;
	}
	
	public void setmUser(UserModel mUser) {
		this.mUser = mUser;
	}
}
