package com.zhongjie.activity.shoppingcar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.shoppingcar.FillOrderActivity.MyAdapter.ViewHolder;
import com.zhongjie.model.ArayListJson;
import com.zhongjie.model.ArayModel;
import com.zhongjie.model.BaseJson;
import com.zhongjie.model.ShopCartModel;
import com.zhongjie.model.SubmitCommodityModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.util.ShopCartManager;
import com.zhongjie.util.Utils;
import com.zhongjie.util.Validator;
import com.zhongjie.view.CommonDialog;
import com.zhongjie.view.CommonLoadingDialog;
import com.zhongjie.view.PromptView;

public class FillOrderActivity extends BaseSecondActivity implements
		OnClickListener {
	private final static String PS = "01";
	private final static String ZT = "02";

	private LinearLayout mCommodityArea;
	private View mPullView, mSelectTypeView, mZTArea, mPSArea, mSubmit;
	private ImageView mHandleView;
	private TextView mHandleTxt, mDispatchingType, mTotalFee;
	private PromptView mPromptView;
	private ListView mListView;
	private ShopCartManager mCartManager;
	private CommonRequest mRequest;
	private List<ArayModel> mArayList;
	private String mDispatchMode = ZT;
	private String mArayacakId;
	private String man, phone, address;
	private EditText mAddressEdit, mPhoneEdit, mNameEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_fill_order);
		super.onCreate(savedInstanceState);
		new QueryArayTask().execute();
	}

	@Override
	protected void initData() {
		mCartManager = ShopCartManager.getInstance();
		mRequest = new CommonRequest(getApplicationContext());
	}

	@Override
	protected void findViews() {
		mCommodityArea = (LinearLayout) findViewById(R.id.act_fill_order_commodity_area);
		mPullView = findViewById(R.id.act_fill_order_pull_area);
		mHandleView = (ImageView) findViewById(R.id.act_fill_order_handle);
		mHandleTxt = (TextView) findViewById(R.id.act_fill_order_handle_txt);
		mSelectTypeView = findViewById(R.id.act_fill_order_selectType);
		mDispatchingType = (TextView) findViewById(R.id.act_fill_order_dispatching_type);
		mZTArea = findViewById(R.id.act_fill_order_dispatching_zt);
		mPSArea = findViewById(R.id.act_fill_order_dispatching_ps);
		mPromptView = (PromptView) findViewById(R.id.promptView);
		mListView = (ListView) findViewById(R.id.act_fill_order_listview);
		mSubmit = findViewById(R.id.act_fill_order_submit_order);
		mTotalFee = (TextView) findViewById(R.id.act_fill_order_totalFee);
		mAddressEdit = (EditText) findViewById(R.id.act_fill_order_address);
		mPhoneEdit = (EditText) findViewById(R.id.act_fill_order_phone);
		mNameEdit = (EditText) findViewById(R.id.act_fill_order_name);
	}

	public void createCommodityView(ShopCartModel scm) {
		View v = getLayoutInflater().inflate(
				R.layout.inculde_fill_order_commodity, mCommodityArea, false);
		TextView count = (TextView) v
				.findViewById(R.id.include_fill_order_commodity_count);
		ImageView img = (ImageView) v
				.findViewById(R.id.include_fill_order_commodity_img);
		TextView taste = (TextView) v
				.findViewById(R.id.include_fill_order_commodity_taste);
		TextView name = (TextView) v
				.findViewById(R.id.include_fill_order_commodity_name);
		TextView price = (TextView) v
				.findViewById(R.id.include_fill_order_commodity_price);
		TextView weight = (TextView) v
				.findViewById(R.id.include_fill_order_commodity_weight);
		count.setText("x" + scm.count);
		ImageLoader.getInstance().displayImage(scm.image, img, options);
		if (Utils.isEmpty(scm.selectedTaste)) {
			taste.setVisibility(View.GONE);
		} else {
			taste.setText(scm.selectedTaste);
		}
		name.setText(scm.name);
		price.setText(scm.price);
		weight.setText(scm.weight);

		mCommodityArea.addView(v);
	}

	private void initPullView() {
		if (null != mCartManager.mCheckedList
				&& mCartManager.mCheckedList.size() > 0) {
			createCommodityView(mCartManager.mCheckedList.get(0));
			if (mCartManager.mCheckedList.size() > 1) {
				mPullView.setVisibility(View.VISIBLE);
				mPullView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mCommodityArea.getChildCount() == 1) {
							for (int i = 1; i < mCartManager.mCheckedList
									.size(); i++) {
								createCommodityView(mCartManager.mCheckedList
										.get(i));
							}
							mHandleView.setImageResource(R.drawable.ic_push);
							mHandleTxt.setText("点击收起");
						} else {
							mCommodityArea.removeViews(1,
									mCommodityArea.getChildCount() - 1);
							mHandleView.setImageResource(R.drawable.ic_pull);
							mHandleTxt.setText("共#件商品，点击展开");
						}
					}
				});
			}
		}
	}

	@Override
	protected void initViews() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mPromptView.showContent();
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ViewHolder vh = (ViewHolder) view.getTag();
						vh.radioBtn.setChecked(true);
						((BaseAdapter) mListView.getAdapter())
								.notifyDataSetChanged();
					}

				});
			}
		}, 1000);
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo);
		mTopCenterImg.setVisibility(View.VISIBLE);
		initPullView();

		mSelectTypeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CommonDialog cd = CommonDialog
						.creatDialog(FillOrderActivity.this);
				cd.setTitle("请选择配送方式");
				cd.setContentView(R.layout.dialog_select_dispatching_type);
				cd.findViewById(R.id.dialog_select_dispatching_type_ps)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								setDispatchingForPS();
								cd.dismiss();
							}
						});
				cd.findViewById(R.id.dialog_select_dispatching_type_zt)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								setDispatchingForZT();
								cd.dismiss();
							}
						});
			}
		});
		mSubmit.setOnClickListener(this);
	}

	class QueryArayTask extends AsyncTask<Void, Void, ArayListJson> {
		CommonLoadingDialog cld;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPromptView.showLoading();
		}

		@Override
		protected ArayListJson doInBackground(Void... params) {
			ArayListJson uj = null;
			try {
				String result = mRequest.queryAray();
				if (!TextUtils.isEmpty(result)) {
					uj = JSON.parseObject(result, ArayListJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "", e);
			}
			return uj;
		}

		@Override
		protected void onPostExecute(ArayListJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			mPromptView.showContent();
			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			if (null != result) {
				if (result.code == 0) {
					if (null != result.data && result.data.size() > 0) {
						mArayList = result.data;
						mListView.setAdapter(new MyAdapter());
					} else {
						mPromptView.showEmpty();
					}
				} else {
					showToast(result.errMsg);
				}
			}
		}
	}

	class SubmitOrderTask extends AsyncTask<Void, Void, BaseJson> {
		CommonLoadingDialog cld;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(FillOrderActivity.this);
			cld.setCanceledOnTouchOutside(false);
			cld.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}

		@Override
		protected BaseJson doInBackground(Void... params) {
			BaseJson uj = null;
			try {
				List<SubmitCommodityModel> scmList = null;
				if (null != mCartManager.mCheckedList) {
					for (ShopCartModel scm : mCartManager.mCheckedList) {
						if (null == scmList)
							scmList = new ArrayList<SubmitCommodityModel>();
						scmList.add(new SubmitCommodityModel(scm.count,
								scm.commodityId, scm.selectedTaste));
					}
				}
				String commodityInfo = JSON.toJSONString(null == scmList ? ""
						: scmList);
				String result = mRequest.submitOrder(UserModelManager
						.getInstance().getmUser().sessId, commodityInfo,
						mDispatchMode, mDispatchMode.equals(ZT) ? mArayacakId
								: null, mDispatchMode.equals(ZT) ? null : man,
						phone, address, null);
				if (!TextUtils.isEmpty(result)) {
					uj = JSON.parseObject(result, BaseJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "SubmitOrderTask error", e);
			}
			return uj;
		}

		@Override
		protected void onPostExecute(BaseJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			if (null != result) {
				if (result.code == 0) {
					Intent intent = new Intent(FillOrderActivity.this,
							SubmitOrderSuccess.class);
					startActivity(intent);
				} else {
					showToast(result.errMsg);
				}
			}
		}
	}

	class MyAdapter extends BaseAdapter {

		int checkedPos = -1;

		@Override
		public int getCount() {
			return null == mArayList ? 0 : mArayList.size();
		}

		@Override
		public ArayModel getItem(int position) {
			return null == mArayList ? null : mArayList.get(position);
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
						R.layout.listview_item_select_zt, parent, false);
				vh.radioBtn = (RadioButton) convertView
						.findViewById(R.id.list_item_select_zt_radiobtn);
				vh.adress = (TextView) convertView
						.findViewById(R.id.list_item_select_zt_address);
				vh.phone = (TextView) convertView
						.findViewById(R.id.list_item_select_zt_phone);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.radioBtn.setTag(position);
			if (checkedPos == position)
				vh.radioBtn.setChecked(true);
			else
				vh.radioBtn.setChecked(false);
			vh.radioBtn
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								checkedPos = (Integer) buttonView.getTag();
								mArayacakId = getItem(checkedPos).id;
							}
						}
					});

			ArayModel am = getItem(position);

			if (null != am) {
				vh.adress.setText(am.name + " : " + am.address);
				vh.phone.setText("联系电话 : " + am.phone);
			}

			return convertView;
		}

		class ViewHolder {
			RadioButton radioBtn;
			TextView adress, phone;
		}

	}

	private void setDispatchingForPS() {
		mDispatchingType.setText("配送");
		mDispatchMode = PS;
		mZTArea.setVisibility(View.GONE);
		mPSArea.setVisibility(View.VISIBLE);
	}

	private void setDispatchingForZT() {
		mDispatchingType.setText("自提");
		mDispatchMode = ZT;
		mZTArea.setVisibility(View.VISIBLE);
		mPSArea.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		computeTotalMoney();
	}

	private void computeTotalMoney() {
		float totalFee = 0.00f;

		if (null != mCartManager.mCheckedList
				&& mCartManager.mCheckedList.size() > 0) {
			for (ShopCartModel scm : mCartManager.mCheckedList) {
				try {
					float price = Float.valueOf(scm.price);
					totalFee += price * scm.count;
				} catch (NumberFormatException e) {
					Logger.e(TAG, "计算总钱数出错", e);
					continue;
				}
			}
		}

		NumberFormat format = NumberFormat.getCurrencyInstance();
		format.setCurrency(Currency.getInstance(Locale.CHINA));
		mTotalFee.setText(format.format(totalFee));
	}

	private boolean doValidate() {
		boolean flag = true;
		List<EditText> list = new ArrayList<EditText>();

		if (!Validator.validateNull(mPhoneEdit, getString(R.string.notnull))) {
			list.add(mPhoneEdit);
			flag = false;
		}

		if (!Validator.validateNull(mAddressEdit, getString(R.string.notnull))) {
			list.add(mAddressEdit);
			flag = false;
		}

		if (!Validator.validateNull(mNameEdit, getString(R.string.notnull))) {
			list.add(mNameEdit);
			flag = false;
		}

		man = mNameEdit.getText().toString().trim();
		phone = mPhoneEdit.getText().toString().trim();
		address = mAddressEdit.getText().toString().trim();

		if (!flag) {
			if (null != list && list.size() > 0) {
				list.get(0).requestFocus();
			}
		}

		list.clear();
		list = null;

		return flag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_fill_order_submit_order:
			if (mDispatchMode.equals(PS) && doValidate()) {
				new SubmitOrderTask().execute();
			}else if(mDispatchMode.equals(ZT)){
				new SubmitOrderTask().execute();
			}
			break;

		default:
			break;
		}
	}

}
