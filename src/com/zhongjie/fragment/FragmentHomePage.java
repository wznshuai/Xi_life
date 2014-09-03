package com.zhongjie.fragment;

import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.PhotoViewActivity;
import com.zhongjie.activity.anytimebuy.CommodityDetailsActivity;
import com.zhongjie.activity.managerservice.DryCleanActivity;
import com.zhongjie.activity.managerservice.PaymentActivity;
import com.zhongjie.activity.managerservice.RepairsActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.model.ADModel;
import com.zhongjie.model.CommodityModel;
import com.zhongjie.model.FullModel;
import com.zhongjie.model.HomeShowJson;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.view.CommonLoadingDialog;
import com.zhongjie.view.MyViewPager;
import com.zhongjie.view.SlideRightOutView;
import com.zhongjie.view.viewpagerindicator.CirclePageIndicator;

public class FragmentHomePage extends BaseFragment implements OnClickListener{
	
	private static FragmentHomePage mInstance;
	
	private MyViewPager mPager;
	private CirclePageIndicator mIndicator;
	private ListView mListView;
	private View mRepairs, mGoUserCenter, mHeaderView, mGoDryClean, mGoPayment;
	private CommonRequest mRequest;
	private List<ADModel> mAdList;
	private List<FullModel> mFullList;
	private List<CommodityModel> mCommodityList;
	private View mFull1, mFull2;
	private ImageView mFullImg1, mFullImg2;
	private MyCommodityAdapter mCommodityAdapter;
	
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
	protected void initData() {
		super.initData();
		mRequest = new CommonRequest(getActivity());
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
		mFull1 = mHeaderView.findViewById(R.id.header_homepage_full1_area);
		mFull2 = mHeaderView.findViewById(R.id.header_homepage_full2_area);
		mFullImg1 = (ImageView)mHeaderView.findViewById(R.id.header_homepage_full1_img);
		mFullImg2 = (ImageView)mHeaderView.findViewById(R.id.header_homepage_full2_img);
		mGoDryClean = mHeaderView.findViewById(R.id.fra_homepage_dryclean);
		mGoPayment = mHeaderView.findViewById(R.id.fra_homepage_goPayment);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getActivityMine().getCurrentTabTag().equals(MainActivity.TAB_CENTER)){
			mPager.startAutoScroll();
			getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo_homepage);
			if(null == mAdList || null == mFullList || null == mCommodityList){
				new HomeDataTask().execute();
			}
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
		mCommodityAdapter = new MyCommodityAdapter();
		mListView.setAdapter(mCommodityAdapter);
		mRepairs.setOnClickListener(this);
		mGoUserCenter.setOnClickListener(this);
		mGoDryClean.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0)
					return;
				CommodityModel cm = mCommodityAdapter.getItem(position - mListView.getHeaderViewsCount());
				Intent intent = new Intent(getActivity(), CommodityDetailsActivity.class);
				intent.putExtra("commodityId", cm.commodityId);
				intent.putExtra("commodityName", cm.name);
				startActivity(intent);
			}
			
		});
		mFull1.setOnClickListener(this);
		mFull2.setOnClickListener(this);
		mFullImg1.setOnClickListener(this);
		mFullImg2.setOnClickListener(this);
		mGoPayment.setOnClickListener(this);
	}
	
	class MyPagerAdapter extends FragmentStatePagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			
			return new FragmentBigImg(mAdList.get(arg0).image, mAdList.get(arg0).url);
		}

		@Override
		public int getCount() {
			return null == mAdList ? 0 : mAdList.size();
		}
	}
	
	private void initHomeData(){
		if(null != mFullList){
			for(int i = 0;i < mFullList.size();i++){
				FullModel fm = mFullList.get(i);
				ImageLoader.getInstance().displayImage(fm.image, mFullImg1, options);
				if(i == 1)
					break;
			}
		}
	}
	
	
	class MyCommodityAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return null == mCommodityList ? 0 : mCommodityList.size();
		}

		@Override
		public CommodityModel getItem(int position) {
			return null == mCommodityList ? null : mCommodityList.get(position);
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
				vh.commodityDescription = (TextView)convertView.findViewById(R.id.list_item_commodity_description);
				vh.commodityName = (TextView)convertView.findViewById(R.id.list_item_commodity_name);
				vh.commodityPrice = (TextView)convertView.findViewById(R.id.list_item_commodity_money);
				vh.commodityWeight = (TextView)convertView.findViewById(R.id.list_item_commodity_weight);
				vh.img = (ImageView)convertView.findViewById(R.id.list_item_commodity_img);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			CommodityModel cm = getItem(position);
			if(null != cm){
				vh.commodityDescription.setText(cm.detail);
				vh.commodityName.setText(cm.name);
				vh.commodityPrice.setTag(cm.price);
				vh.commodityWeight.setText(cm.weight);
				ImageLoader.getInstance().displayImage(cm.image, vh.img, options);
			}
			return convertView;
		}
		
		class ViewHolder{
			TextView commodityName, commodityWeight, commodityPrice, commodityDescription;	
			ImageView img;
		}
	}
	
	class HomeDataTask extends AsyncTask<Void, Void, HomeShowJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(getActivity());
			cld.setCanceledOnTouchOutside(true);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}

		@Override
		protected HomeShowJson doInBackground(Void... params) {
			HomeShowJson uj = null;
			try {
				String result = mRequest.repairImageUpload();
				if(!TextUtils.isEmpty(result)){
					uj = JSON.parseObject(result, HomeShowJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "HomeDataTask error", e);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(HomeShowJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(result.code == 0){
					if(null != result.data){
						mAdList = result.data.ad;
						mFullList = result.data.full;
						mCommodityList = result.data.commodity;
						mCommodityAdapter.notifyDataSetChanged();
						mPager.getAdapter().notifyDataSetChanged();
						initHomeData();
					}
				}else{
					showToast(result.errMsg);
				}
			}
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
		case R.id.fra_homepage_dryclean:
			startActivity(new Intent(getActivity(), DryCleanActivity.class));
			break;
		case R.id.header_homepage_full1_area:
		case R.id.header_homepage_full1_img:
			if(null != mFullList && mFullList.size() > 0){
				Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
				intent.putExtra(PhotoViewActivity.BIGIMG_URL, mFullList.get(0).url);
				startActivity(intent);
			}
			break;
		case R.id.header_homepage_full2_area:
		case R.id.header_homepage_full2_img:
			if(null != mFullList && mFullList.size() > 1){
				Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
				intent.putExtra(PhotoViewActivity.BIGIMG_URL, mFullList.get(1).url);
				startActivity(intent);
			}
			break;
		case R.id.fra_homepage_goPayment:
			if(UserModelManager.getInstance().isLogin()){
				startActivity(new Intent(getActivity(), PaymentActivity.class));
			}else{
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		default:
			break;
		}
	}
	
}
