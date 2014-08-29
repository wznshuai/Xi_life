package com.zhongjie.activity.managerservice;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseListActivity;
import com.zhongjie.activity.anytimebuy.CommodityDetailsActivity;
import com.zhongjie.activity.shoppingcar.FillOrderFroDryCleanActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.model.CommodityModel;
import com.zhongjie.model.DryCleanListJson;
import com.zhongjie.model.DryCleanModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.util.ShopCartManagerForDryClean;
import com.zhongjie.util.Utils;
import com.zhongjie.view.PromptView;

public class DryCleanActivity extends BaseListActivity{

	private List<DryCleanModel> mDryCleanList; 
	private ShopCartManagerForDryClean mCartManager;
	private PromptView mPromptView;
	private CommonRequest mRequest;
	private View mBottomView;
	private TextView mTotalFee;
	private NumberFormat format;
	private MyAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_dry_clean);
		super.onCreate(savedInstanceState);
		new QueryCommodityList().execute();
	}

	@Override
	protected void initData() {
		format = NumberFormat.getCurrencyInstance();
		format.setCurrency(Currency.getInstance(Locale.CHINA));
		mCartManager = ShopCartManagerForDryClean.getInstance();
		mRequest = new CommonRequest(getApplicationContext());
	}

	@Override
	protected void findViews() {
		mListView = (ListView) findViewById(R.id.act_dry_clean_listview);
		mPromptView = (PromptView)findViewById(R.id.promptView);
		mBottomView = findViewById(R.id.act_dry_clean_bottom);
		mTotalFee = (TextView) mBottomView
				.findViewById(R.id.act_dry_clean_totalFee);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mListView.setAdapter(new MyAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CommodityModel cm = (CommodityModel)arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent(DryCleanActivity.this, CommodityDetailsActivity.class);
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
		mBottomView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserModelManager.getInstance().isLogin()) {
					Intent intent = new Intent(DryCleanActivity.this,
							FillOrderFroDryCleanActivity.class);
					startActivity(intent);
				} else {
					startActivity(new Intent(DryCleanActivity.this,
							LoginActivity.class));
				}
			}
		});
	}

	private void computeTotalMoney() {
		float totalFee = 0.00f;

		if (null != mCartManager.mCartList
				&& mCartManager.mCartList.size() > 0) {
			for (DryCleanModel scm : mCartManager.mCartList) {
				try {
					float price = Float.valueOf(scm.price);
					totalFee += price * scm.count;
				} catch (NumberFormatException e) {
					Logger.e(TAG, "计算总钱数出错", e);
					continue;
				}
			}
		}
		mTotalFee.setText(format.format(totalFee));
	}

	class MyAdapter extends BaseAdapter {

		int checkedPos = -1;

		@Override
		public int getCount() {
			return null == mDryCleanList ? 0 : mDryCleanList.size();
		}

		@Override
		public DryCleanModel getItem(int position) {
			return null == mDryCleanList ? null : mDryCleanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (null == convertView) {
				vh = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item_dryclean, parent, false);
				vh.checkbox = (CheckBox) convertView
						.findViewById(R.id.list_item_shopcart_checkbox);
				vh.edittext = (EditText) convertView
						.findViewById(R.id.list_item_shopcart_edittext);
				vh.img = (ImageView) convertView
						.findViewById(R.id.list_item_shopcart_img);
				vh.money = (TextView) convertView
						.findViewById(R.id.list_item_shopcart_money);
				vh.name = (TextView) convertView
						.findViewById(R.id.list_item_shopcart_name);
				vh.jia = convertView.findViewById(R.id.list_item_shopcart_ic_jia);
				vh.jian = convertView.findViewById(R.id.list_item_shopcart_ic_jian);

				convertView.setTag(vh);
				
				vh.checkbox.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckBox check = (CheckBox) v;
						if (check.isChecked()) {
							mCartManager
									.addInShopCart(getItem((Integer) check
											.getTag()));
							mBottomView.setVisibility(View.VISIBLE);
						} else {
							mCartManager.mCartList.remove(getItem((Integer) check
											.getTag()));
							if (null == mCartManager.mCartList
									|| mCartManager.mCartList.size() < 1) {
								mBottomView.setVisibility(View.GONE);
							}
						}
						computeTotalMoney();
					}
				});
				vh.edittext.addTextChangedListener(new TextWatcher() {
					
					EditText edit;
	
					public TextWatcher setEdit(EditText edit) {
						this.edit = edit;
						return this;
					}
	
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if(count == 1 && s.toString().equals("0")){
							edit.setText("1");
							edit.setSelection(edit.getText().toString().length());
						}
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						if(!TextUtils.isEmpty(s)){
							try {
								getItem((Integer)edit.getTag()).count = Integer.valueOf(s.toString().trim());
								computeTotalMoney();
							} catch (Exception e) {
								Logger.e(TAG, "", e);
							}
						}
					}
				}.setEdit(vh.edittext));
				
				vh.jia.setOnClickListener(new OnClickListener() {
					EditText edit;
					
					public OnClickListener setEdit(EditText edit) {
						this.edit = edit;
						return this;
					}

					@Override
					public void onClick(View v) {
						String countStr2 = edit.getText().toString();
						if(!Utils.isEmpty(countStr2)){
							try{
								int count = Integer.valueOf(countStr2);
								edit.setText(++count + "");
								edit.setSelection(edit.getText().toString().length());
							}catch(Exception e){
								showToast("输入数量不正确");
								Logger.e(TAG, "", e);
							}
						}
					}
				}.setEdit(vh.edittext));
				
				vh.jian.setOnClickListener(new OnClickListener() {
					EditText edit;
					
					public OnClickListener setEdit(EditText edit) {
						this.edit = edit;
						return this;
					}

					@Override
					public void onClick(View v) {
						String countStr3 = edit.getText().toString();
						if(!Utils.isEmpty(countStr3)){
							try{
								int count = Integer.valueOf(countStr3);
								--count;
								if(count == 0)
									count = 1;
								edit.setText(count + "");
								edit.setSelection(edit.getText().toString().length());
							}catch(Exception e){
								showToast("输入数量不正确");
								Logger.e(TAG, "", e);
							}
						}
					}
				}.setEdit(vh.edittext));
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			convertView.setTag(R.string.item_pos, position);

			vh.checkbox.setTag(position);
			
			vh.edittext.setTag(position);
			
			DryCleanModel scm = getItem(position);
			if (null != scm) {
				ImageLoader.getInstance().displayImage(scm.image, vh.img,
						options);
				if (!Utils.isEmpty(scm.price)){
					
					vh.money.setText("￥" + scm.price);
				}
					
				if (!Utils.isEmpty(scm.name))
					vh.name.setText(scm.name);

				vh.edittext.setText(scm.count + "");
				vh.edittext.setSelection(vh.edittext.getText().toString()
						.length());
				if (null != mCartManager.mCartList
						&& mCartManager.mCartList.size() > 0) {
					if (-1 != mCartManager.mCartList.indexOf(scm)) {
						vh.checkbox.setChecked(true);
					} else {
						vh.checkbox.setChecked(false);
					}
				} else {
					vh.checkbox.setChecked(false);
				}
			}

			return convertView;
		}

		class ViewHolder {
			ImageView img;
			TextView name, money;
			CheckBox checkbox;
			EditText edittext;
			View jia, jian;
		}
	}
	
	class QueryCommodityList extends AsyncTask<String, Void, DryCleanListJson>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(start == 0)
				mPromptView.showLoading();
			else
				showFooterView(FooterView.MORE);
		}
		
		@Override
		protected DryCleanListJson doInBackground(String... params) {
			DryCleanListJson eclj = null;
			try {
				String json = mRequest.queryCleanList(start, step);
				if(!TextUtils.isEmpty(json)){
					eclj = JSON.parseObject(json, DryCleanListJson.class);
				}
			} catch (Exception e) {
				Logger.d(TAG, "", e);
			}
			return eclj;
		}
		
		@Override
		protected void onPostExecute(DryCleanListJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(start == 0)
				mPromptView.showContent();
			else
				showFooterView(FooterView.HIDE_ALL);
			if(null != result){
				if(0 == result.code){
					if(null != result.data && result.data.cleanItemCount > 0){
						maxCount = result.data.cleanItemCount;
						if(start == 0){
							mDryCleanList = result.data.cleanItem;
							if(null == mAdapter)
								mAdapter = new MyAdapter();
							mListView.setAdapter(mAdapter);
						}else{
							mDryCleanList.addAll(result.data.cleanItem);
							mAdapter.notifyDataSetChanged();
						}
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
	protected void onDestroy() {
		mCartManager.mCartList.clear();
		mCartManager = null;
		super.onDestroy();
	}
}
