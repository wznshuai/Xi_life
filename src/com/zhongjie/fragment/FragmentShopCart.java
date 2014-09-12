package com.zhongjie.fragment;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.anytimebuy.CommodityDetailsActivity;
import com.zhongjie.activity.shoppingcar.FillOrderActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.model.ShopCartModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.Logger;
import com.zhongjie.util.ShopCartManager;
import com.zhongjie.util.Utils;
import com.zhongjie.view.CommonDialog;
import com.zhongjie.view.CommonDialog.OnButtonClickListener;

public class FragmentShopCart extends BaseFragment {
	private static FragmentShopCart mInstance;

	private ListView mListView;
	private View mEmptyView, mRealContent, mBottomView;
	private ImageView mTopRightImg;
	private List<ShopCartModel> mCartData;
	private MyShopCartAdapter mAdapter;
	private TextView mTotalFee;
	private ShopCartManager mCartManager;
	private NumberFormat format;
	
	public static FragmentShopCart newInstance() {
		if (null == mInstance)
			mInstance = new FragmentShopCart();
		return mInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_shoppingcar, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		format = NumberFormat.getCurrencyInstance();
		format.setCurrency(Currency.getInstance(Locale.CHINA));
		mCartManager = ShopCartManager.getInstance();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	private void initDataOnShow() {

		mCartData = ShopCartManager.getInstance().getShopCart();
		if (null != mCartData && mCartData.size() > 0) {
			mEmptyView.setVisibility(View.GONE);
			if (null == mAdapter) {
				mAdapter = new MyShopCartAdapter();
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			mRealContent.setVisibility(View.VISIBLE);
			mBottomView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (UserModelManager.getInstance().isLogin()) {
						if (null != mCartManager.mCheckedList
								&& mCartManager.mCheckedList.size() > 0) {
							Intent intent = new Intent(getActivity(),
									FillOrderActivity.class);
							startActivity(intent);
						}else{
							showToast("请勾选您所需的商品!");
						}
					} else {
						startActivity(new Intent(getActivity(),
								LoginActivity.class));
					}
				}
			});
			if (null != mCartManager.mCheckedList && mCartManager.mCheckedList.size() > 0) {
				mTopRightImg.setVisibility(View.VISIBLE);
			}else{
				mTopRightImg.setVisibility(View.GONE);
			}
		} else {
			mEmptyView.setVisibility(View.VISIBLE);
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			mRealContent.setVisibility(View.GONE);
			mTopRightImg.setVisibility(View.GONE);
			findViewById(R.id.fra_shoppingcar_goAnytimeBuy).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							((MainActivity) getActivity())
									.setCurrentTabByTag(MainActivity.TAB_1);
							;
						}
					});
		}

	}

	@Override
	protected void findViews() {
		super.findViews();
		mTopRightImg = (ImageView) getActivity().findViewById(
				R.id.topbar_rightImg);
		mEmptyView = findViewById(R.id.fra_shoppingcar_empty);
		mListView = (ListView) findViewById(R.id.fra_shoppingcar_listview);
		mRealContent = findViewById(R.id.fra_shoppingcar_realcontent);
		mBottomView = findViewById(R.id.fra_shoppingcar_bottom);
		mTotalFee = (TextView) mBottomView
				.findViewById(R.id.fra_shoppingcar_totalFee);
	}

	@Override
	protected void initViews() {
		super.initViews();
		getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo_shopcart);
		mTopRightImg.setImageResource(R.drawable.ic_trash_big);
		mTopRightImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mCartData && mCartData.size() > 0
						&& null != mCartManager.mCheckedList && mCartManager.mCheckedList.size() > 0) {
					CommonDialog cd = CommonDialog.creatDialog(getActivity());
					cd.setMessage("确定要删除已选商品?");
					cd.setLeftButtonInfo("取消", new OnButtonClickListener() {
						@Override
						public void onClick(CommonDialog cd, View view) {
							cd.dismiss();
						}
					});
					cd.setRightButtonInfo("确定", new OnButtonClickListener() {

						@Override
						public void onClick(CommonDialog cd, View view) {
							cd.dismiss();
							Iterator<ShopCartModel> scmIterator = mCartManager.mCheckedList.iterator();
							while (scmIterator.hasNext()) {
								ShopCartModel scm = scmIterator.next();
								scmIterator.remove();
								mCartData.remove(scm);
							}
							mAdapter.notifyDataSetChanged();
							mTopRightImg.setVisibility(View.GONE);

							if (null == mCartData || mCartData.size() == 0) {
								mEmptyView.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zhongjie.BaseFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (getActivityMine().getCurrentTabTag().equals(MainActivity.TAB_3)) {
			initViews();
			initDataOnShow();
			if (null != mCartManager.mCheckedList && mCartManager.mCheckedList.size() > 0) {
				mTopRightImg.setVisibility(View.VISIBLE);
				computeTotalMoney();
			}
		}
	}

	@Override
	public void onPause() {
		getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo);
		mTopRightImg.setImageResource(0);
		mTopRightImg.setVisibility(View.GONE);
		super.onPause();
	}

	private void computeTotalMoney() {
		float totalFee = 0.00f;

		if (null != mCartManager.mCheckedList
				&& mCartManager.mCheckedList.size() > 0) {
			for (ShopCartModel scm : mCartManager.mCheckedList) {
				try {
					float price = Float.valueOf(scm.price);
					totalFee += price * scm.number;
				} catch (NumberFormatException e) {
					Logger.e(tag, "计算总钱数出错", e);
					continue;
				}
			}
		}
		mTotalFee.setText(format.format(totalFee));
	}

	class MyShopCartAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return null == mCartData ? 0 : mCartData.size();
		}

		@Override
		public ShopCartModel getItem(int position) {
			return null == mCartData ? null : mCartData.get(position);
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
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.listview_item_shopcart, parent, false);
				vh.checkbox = (CheckBox) convertView
						.findViewById(R.id.list_item_shopcart_checkbox);
				vh.edittext = (EditText) convertView
						.findViewById(R.id.list_item_shopcart_edittext);
				vh.img = (ImageView) convertView
						.findViewById(R.id.list_item_shopcart_img);
//				vh.introduce = (TextView) convertView
//						.findViewById(R.id.list_item_shopcart_introduce);
				vh.money = (TextView) convertView
						.findViewById(R.id.list_item_shopcart_money);
				vh.name = (TextView) convertView
						.findViewById(R.id.list_item_shopcart_name);
				vh.weight = (TextView) convertView
						.findViewById(R.id.list_item_shopcart_weight);
				vh.jia = convertView.findViewById(R.id.list_item_shopcart_ic_jia);
				vh.jian = convertView.findViewById(R.id.list_item_shopcart_ic_jian);
				vh.taste = (TextView)convertView.findViewById(R.id.list_item_shopcart_taste);

				convertView.setTag(vh);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int pos = (Integer) v.getTag(R.string.item_pos);
						ShopCartModel scm = getItem(pos);
						Intent intent = new Intent(getActivity(),
								CommodityDetailsActivity.class);
						intent.putExtra("commodityId", scm.commodityId);
						intent.putExtra("commodityName", scm.name);
						startActivity(intent);
					}
				});
				vh.checkbox.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckBox check = (CheckBox) v;
						if (check.isChecked()) {
							mCartManager
									.addInCheckedList(getItem((Integer) check
											.getTag()));
							mTopRightImg.setVisibility(View.VISIBLE);
						} else {
							mCartManager
									.removeFromCheckedList(getItem((Integer) check
											.getTag()));
							if (null == mCartManager.mCheckedList
									|| mCartManager.mCheckedList.size() < 1) {
								mTopRightImg.setVisibility(View.GONE);
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
								mCartData.get((Integer)edit.getTag()).number = Integer.valueOf(s.toString().trim());
								computeTotalMoney();
							} catch (Exception e) {
								Logger.e(tag, "", e);
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
								Logger.e(tag, "", e);
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
								Logger.e(tag, "", e);
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
			
			ShopCartModel scm = getItem(position);
			if (null != scm) {
				ImageLoader.getInstance().displayImage(scm.image, vh.img,
						options);
//				if (!Utils.isEmpty(scm.detail))
//					vh.introduce.setText(scm.detail);
				if (!Utils.isEmpty(scm.price)){
					
					vh.money.setText("￥" + scm.price);
				}
					
				if (!Utils.isEmpty(scm.name))
					vh.name.setText(scm.name);
				if (!Utils.isEmpty(scm.weight))
					vh.weight.setText(scm.weight);
				
				if(Utils.isEmpty(scm.selectedTaste)){
					vh.taste.setVisibility(View.GONE);
				}else{
					vh.taste.setVisibility(View.VISIBLE);
					vh.taste.setText(scm.selectedTaste);	
				}

				vh.edittext.setText(scm.number + "");
				vh.edittext.setSelection(vh.edittext.getText().toString()
						.length());
				if (null != mCartManager.mCheckedList
						&& mCartManager.mCheckedList.size() > 0) {
					if (-1 != mCartManager.mCheckedList.indexOf(scm)) {
						scm.isChecked = true;
						vh.checkbox.setChecked(true);
					} else {
						scm.isChecked = false;
						vh.checkbox.setChecked(false);
					}
				} else {
					scm.isChecked = false;
					vh.checkbox.setChecked(false);
				}
			}

			return convertView;
		}

		class ViewHolder {
			ImageView img;
			TextView name, introduce, weight, money, taste;
			CheckBox checkbox;
			EditText edittext;
			View jia, jian;
		}

	}
}
