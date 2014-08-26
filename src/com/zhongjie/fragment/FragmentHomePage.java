package com.zhongjie.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.managerservice.RepairsActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.model.CommodityModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.view.MyViewPager;
import com.zhongjie.view.SlideRightOutView;
import com.zhongjie.view.viewpagerindicator.CirclePageIndicator;

public class FragmentHomePage extends BaseFragment implements OnClickListener{
	
	private static FragmentHomePage mInstance;
	
	private MyViewPager mPager;
	private CirclePageIndicator mIndicator;
	private ListView mListView;
	private View mRepairs, mGoUserCenter, mHeaderView;
	
	public static FragmentHomePage newInstance(){
		if(null == mInstance)
			mInstance = new FragmentHomePage();
		return mInstance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_homepage, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.header_homepage, mListView, false);
		mPager = (MyViewPager)mHeaderView.findViewById(R.id.fra_homepage_viewpager);
		mIndicator = (CirclePageIndicator)mHeaderView.findViewById(R.id.fra_homepage_indicator_dot);
		mListView = (ListView)findViewById(R.id.fra_homepage_listview);
		mRepairs = mHeaderView.findViewById(R.id.fra_homepage_repairs);
		mGoUserCenter = mHeaderView.findViewById(R.id.fra_homepage_goUserCenter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getActivityMine().getCurrentTabTag().equals(MainActivity.TAB_CENTER)){
			mPager.startAutoScroll();
			getActivityMine().setTopCenterLogo(R.drawable.ic_logo_homepage);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mPager.stopAutoScroll();
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		mPager.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
		mPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		mIndicator.setViewPager(mPager);
		mListView.addHeaderView(mHeaderView);
		mListView.setAdapter(new MyCommodityAdapter());
		mRepairs.setOnClickListener(this);
		mGoUserCenter.setOnClickListener(this);
	}
	
	class MyPagerAdapter extends FragmentStatePagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return new FragmentBigImg(R.drawable.temp_homepage);
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
	
	class MyCommodityAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CommodityModel getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (null == convertView){
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listview_item_homepage, parent, false);
				vh = new ViewHolder();
				vh.addInShoppingCar = convertView.findViewById(R.id.list_item_commodity_add_in_shoppingcar);
				vh.commodityDescription = (TextView)convertView.findViewById(R.id.list_item_commodity_description);
				vh.commodityName = (TextView)convertView.findViewById(R.id.list_item_commodity_name);
				vh.commodityPrice = (TextView)convertView.findViewById(R.id.list_item_commodity_money);
				vh.commodityWeight = (TextView)convertView.findViewById(R.id.list_item_commodity_weight);
				vh.img = (ImageView)convertView.findViewById(R.id.list_item_commodity_img);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			
			return convertView;
		}
		
		class ViewHolder{
			TextView commodityName, commodityWeight, commodityPrice, commodityDescription;	
			View addInShoppingCar;
			ImageView img;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fra_homepage_repairs:
			if(UserModelManager.getInstance().isLogin()){
				startActivity(new Intent(getActivity(), RepairsActivity.class));
			}else{
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.fra_homepage_goUserCenter:
			getActivityMine().setCurrentTabByTag(MainActivity.TAB_4);
			break;
		default:
			break;
		}
	}
	
}
