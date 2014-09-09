package com.zhongjie;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.zhongjie.activity.BaseActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.fragment.FragmentAnyTimeBuy;
import com.zhongjie.fragment.FragmentHomePage;
import com.zhongjie.fragment.FragmentManagerService;
import com.zhongjie.fragment.FragmentShopCart;
import com.zhongjie.fragment.FragmentUserCenter;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.Constants;
import com.zhongjie.view.CommonDialog;
import com.zhongjie.view.CommonDialog.OnButtonClickListener;

public class MainActivity extends BaseActivity implements OnTabChangeListener{
	
	public final static String TAB_1 = "TAB_1";
	public final static String TAB_2 = "TAB_2";
	public final static String TAB_CENTER = "TAB_CENTER";
	public final static String TAB_3 = "TAB_3";
	public final static String TAB_4 = "TAB_4";
	
	private TabHost mTabHost;
	private FragmentManager mFm;
	private String last_show_TAB = ""; 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		mTabHost.setup();
		addTab(TAB_1, R.drawable.tab_1_selector, "嘻厨房");
		addTab(TAB_2, R.drawable.tab_2_selector, "嘻管家");
		addTab(TAB_CENTER, 0, "");
		addTab(TAB_3, R.drawable.tab_3_selector, "购物车");
		addTab(TAB_4, R.drawable.tab_4_selector, "我的");
//		mTabHost.getTabWidget().getChildAt(2).setEnabled(false);
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTabByTag(TAB_CENTER);
	}
	
	public void setTopCenterLogo(int id){
		mTopCenterImg.setImageResource(id);
		mTopCenterImg.setVisibility(View.VISIBLE);
	}
	
	public void setTopRightImg(int id){
		mTopRightTxt.setBackgroundResource(id);
		mTopRightTxt.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(null != intent.getStringExtra(Constants.MAINACTIVITY_TAB_KEY)){
			setCurrentTabByTag(intent.getStringExtra(Constants.MAINACTIVITY_TAB_KEY));
		}
	}

	
	private void addTab(String tag, int indicatorDrawableId, String tab_text) {
		TabSpec spec = mTabHost.newTabSpec(tag);
		spec.setIndicator(createTabView(indicatorDrawableId, tab_text, tag));
		spec.setContent(new TabContentFactory() {
			
			@Override
			public View createTabContent(String tag) {
				return findViewById(R.id.container);
			}
		});
		mTabHost.addTab(spec);
	}
	
	private View createTabView(int resId, String tab_text, String tag){
		View view = null;
//		if(tag.equals(TAB_CENTER)){
//			view = new View(this);
//			view.setla
//		}else{
			view = LayoutInflater.from(this).inflate(R.layout.tabwidget_common_view, null);
			TextView text = (TextView)view.findViewById(R.id.tabwidget_txt);
			text.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
			text.setText(tab_text);
//		}
		return view;
	}


	@Override
	protected void initData() {
		mFm = getSupportFragmentManager();
	}

	@Override
	protected void findViews() {
		initTopViews();
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo);
		mTopCenterImg.setVisibility(View.VISIBLE);
	}
	
	public void goToHomePage() {
		if (mFm.getBackStackEntryCount() > 1)
			mFm.popBackStack(mFm.getBackStackEntryAt(0).getId(), 0);
	}
	
	public void setCurrentTabByTag(String tag){
		mTabHost.setCurrentTabByTag(tag);
	}
	
	public String getCurrentTabTag(){
		return mTabHost.getCurrentTabTag();
	}

	private void setCurrentFragment(String tabId){
		FragmentTransaction ft = mFm.beginTransaction();
		Fragment f = null;
		
		if(!TextUtils.isEmpty(last_show_TAB) && !tabId.equals(last_show_TAB)){
			f = mFm.findFragmentByTag(last_show_TAB);
			if(null != f){
				ft.hide(f);
				f.onPause();
			}
		}
		
		if(tabId.equals(TAB_1)){
			f = FragmentAnyTimeBuy.newInstance();
		}else if(tabId.equals(TAB_2)){
			f = FragmentManagerService.newInstance();
		}else if(tabId.equals(TAB_3)){
			f = FragmentShopCart.newInstance();
		}else if(tabId.equals(TAB_4)){
			f = FragmentUserCenter.newInstance();
		}else if(tabId.equals(TAB_CENTER)){
			f = FragmentHomePage.newInstance();
		}
		if(f.isAdded()){
			ft.show(f);
			f.onResume();
		}else{
			ft.add(R.id.container, f, tabId);
		}
		
		
		ft.commit();
		last_show_TAB = tabId;
	}
	
	@Override
	public void onTabChanged(String tabId) {
		if(last_show_TAB.equals(tabId))
			return;
		if(tabId.equals(TAB_4)){
			if(!UserModelManager.getInstance().isLogin()){
				mTabHost.setCurrentTabByTag(last_show_TAB);
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				return;
			}
		}
		setCurrentFragment(tabId);
	}
	
	@Override
	public void onBackPressed() {
		CommonDialog cd = CommonDialog.creatDialog(this);
		cd.setMessage("确定要退出嘻生活?");
		cd.setLeftButtonInfo("取消", new OnButtonClickListener() {
			
			@Override
			public void onClick(CommonDialog cd, View view) {
				cd.dismiss();
			}
		});
		cd.setRightButtonInfo("确定", new OnButtonClickListener() {
			
			@Override
			public void onClick(CommonDialog cd, View view) {
				((MyApplication)getApplication()).exitApp();
			}
		});
	}
}
