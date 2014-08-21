package com.zhongjie.activity.anytimebuy;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.BaseListActivity;
import com.zhongjie.model.CommodityListJson;
import com.zhongjie.model.CommodityModel;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.Logger;
import com.zhongjie.util.ShopCartManager;
import com.zhongjie.util.Utils;
import com.zhongjie.view.PromptView;

public class CommodityListActivity extends BaseListActivity {

	private volatile boolean isShowing = false;
	private WindowManager mWm;
	private View mFloatView;
	private TextView mBuyCountView;
	private String mCatalogId;
	private List<CommodityModel> mCommodityList; 
	private ShopCartManager mCartManager;
	private PromptView mPromptView;
	private CommonRequest mRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_list);
		super.onCreate(savedInstanceState);
		new QueryCommodityList().execute();
	}

	@Override
	protected void initData() {
		mCatalogId = getIntent().getStringExtra("catalogId");
		mCartManager = ShopCartManager.getInstance();
		mRequest = new CommonRequest(getApplicationContext());
	}

	@Override
	protected void findViews() {
		mListView = (ListView) findViewById(R.id.act_commodity_list);
		mPromptView = (PromptView)findViewById(R.id.promptView);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mBuyCountView.setText(" " + mCartManager.getTotalCount() + " ");
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mListView.setAdapter(new MyCommodityAdapter());
		createFloatView(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CommodityModel cm = (CommodityModel)arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent(CommodityListActivity.this, CommodityDetailsActivity.class);
				intent.putExtra("commodityId", cm.commodityId);
				intent.putExtra("commodityName", cm.name);
				startActivity(intent);
			}
			
		});
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					if(view.getLastVisiblePosition() - mListView.getHeaderViewsCount() == view.getAdapter().getCount() - 1){
						int maxPage = maxCount%step == 0 ? maxCount/step : maxCount/step + 1;
						if(start + 1 < maxPage){
							start++;
							new QueryCommodityList().execute();
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}

	private void createFloatView(final Activity act) {
		if (isShowing)
			return;
		mRemoveFloatView();
		isShowing = true;
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		mFloatView = LayoutInflater.from(act).inflate(
				R.layout.float_view_shoppingcar, null);
		mBuyCountView = (TextView)mFloatView.findViewById(R.id.float_view_shoppingcar_buyCount);
		lp.packageName = act.getPackageName();
		lp.width = Utils.dp2px(getApplicationContext(), 60);
		lp.height = Utils.dp2px(getApplicationContext(), 60);
		lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		lp.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		lp.y = Utils.dp2px(act.getApplicationContext(), 100);
		lp.x = Utils.dp2px(act.getApplicationContext(), 20);
		lp.format = PixelFormat.RGBA_8888;
		mWm = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
		lp.type = LayoutParams.TYPE_APPLICATION;
		mFloatView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommodityListActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(Constants.MAINACTIVITY_TAB_KEY, MainActivity.TAB_3);
				startActivity(intent);
				finish();
			}
		});
		mWm.addView(mFloatView, lp);
	}

	private void mRemoveFloatView() {
		if (!isShowing)
			return;
		isShowing = false;
		if (null != mWm && null != mFloatView) {
			mWm.removeView(mFloatView);
		}
		mWm = null;
		mFloatView = null;
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
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item_commodity, parent, false);
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
			
			CommodityModel mCommodity = getItem(position);
			vh.addInShoppingCar.setTag(position);
			if(null != mCommodity){
				vh.commodityDescription.setText(mCommodity.info);
				vh.commodityName.setText(mCommodity.name);
				vh.commodityPrice.setText(mCommodity.price);
				vh.commodityWeight.setText(mCommodity.weight);
				
				vh.addInShoppingCar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(null != mBuyCountView){
							int pos = (Integer)arg0.getTag();
							CommodityModel cm = getItem(pos);
							if(null != cm){
								mCartManager.addInShopCart(cm);
								mBuyCountView.setText(" " + mCartManager.getTotalCount() + " ");
							}
						}
					}
				});
				ImageLoader.getInstance().displayImage(mCommodity.image, vh.img, options);
			}
			
			return convertView;
		}
		
		class ViewHolder{
			TextView commodityName, commodityWeight, commodityPrice, commodityDescription;	
			View addInShoppingCar;
			ImageView img;
		}
	}
	
	class QueryCommodityList extends AsyncTask<String, Void, CommodityListJson>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(start == 0)
				mPromptView.showLoading();
			else
				showFooterView(FooterView.MORE);
		}
		
		@Override
		protected CommodityListJson doInBackground(String... params) {
			CommodityListJson eclj = null;
			try {
				String json = mRequest.queryCommodityList(mCatalogId, start, step);
				if(!TextUtils.isEmpty(json)){
					eclj = JSON.parseObject(json, CommodityListJson.class);
				}
			} catch (Exception e) {
				Logger.d(TAG, "", e);
			}
			return eclj;
		}
		
		@Override
		protected void onPostExecute(CommodityListJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(start == 0)
				mPromptView.showContent();
			else
				showFooterView(FooterView.HIDE_ALL);
			if(null != result){
				if(0 == result.code){
					if(null != result.data && result.data.commodityItemCount > 0){
						maxCount = result.data.commodityItemCount;
						mCommodityList = result.data.commodityItem;
						mListView.setAdapter(new MyCommodityAdapter());
					}else{
						mPromptView.showEmpty();
					}
				}else{
					showToast(result.errMsg);
					mPromptView.showError();
				}
			}else{
				mPromptView.showError();
			}
		}
	}
	
	
	@Override
	public void finish() {
		mRemoveFloatView();
		super.finish();
	}
}
