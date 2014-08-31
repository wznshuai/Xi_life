package com.zhongjie.activity.shoppingcar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.WheelVerticalView;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.shoppingcar.FillOrderFroDryCleanActivity.MyAdapter.ViewHolder;
import com.zhongjie.model.ArayListJson;
import com.zhongjie.model.ArayModel;
import com.zhongjie.model.BaseJson;
import com.zhongjie.model.DryCleanModel;
import com.zhongjie.model.SubmitCommodityModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.util.ShopCartManagerForDryClean;
import com.zhongjie.util.Utils;
import com.zhongjie.util.Validator;
import com.zhongjie.view.CommonDialog;
import com.zhongjie.view.CommonLoadingDialog;
import com.zhongjie.view.PromptView;

public class FillOrderFroDryCleanActivity extends BaseSecondActivity implements
		OnClickListener {
	private final static String PS = "01";
	private final static String ZT = "02";

	private LinearLayout mCommodityArea;
	private View mPullView, mSelectTypeView, mZTArea, mPSArea, mSubmit;
	private ImageView mHandleView;
	private TextView mHandleTxt, mDispatchingType, mTotalFee;
	private PromptView mPromptView;
	private ListView mListView;
	private ShopCartManagerForDryClean mCartManager;
	private CommonRequest mRequest;
	private List<ArayModel> mArayList;
	private String mDispatchMode = ZT;
	private String mArayacakId;
	private String man, phone, address, invoice = null;
	private EditText mAddressEdit, mPhoneEdit, mNameEdit, mInvoiceEdit;
	
	private WheelVerticalView mYearWheel, mMonthWheel, mDayWheel;
	private Calendar mCurCalendar, mMaxCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_fill_order_for_dry_clean);
		super.onCreate(savedInstanceState);
		new QueryArayTask().execute();
	}

	@Override
	protected void initData() {
		mCartManager = ShopCartManagerForDryClean.getInstance();
		mRequest = new CommonRequest(getApplicationContext());
		mCurCalendar = Calendar.getInstance(Locale.CHINA);
		mMaxCalendar = (Calendar)mCurCalendar.clone();
		mMaxCalendar.add(Calendar.DAY_OF_YEAR, 15);
	}

	@Override
	protected void findViews() {
		mCommodityArea = (LinearLayout) findViewById(R.id.act_fill_order_for_dry_clean_commodity_area);
		mPullView = findViewById(R.id.act_fill_order_for_dry_clean_pull_area);
		mHandleView = (ImageView) findViewById(R.id.act_fill_order_for_dry_clean_handle);
		mHandleTxt = (TextView) findViewById(R.id.act_fill_order_for_dry_clean_handle_txt);
		mSelectTypeView = findViewById(R.id.act_fill_order_for_dry_clean_selectType);
		mDispatchingType = (TextView) findViewById(R.id.act_fill_order_for_dry_clean_dispatching_type);
		mZTArea = findViewById(R.id.act_fill_order_for_dry_clean_dispatching_zt);
		mPSArea = findViewById(R.id.act_fill_order_for_dry_clean_dispatching_ps);
		mPromptView = (PromptView) findViewById(R.id.promptView);
		mListView = (ListView) findViewById(R.id.act_fill_order_for_dry_clean_listview);
		mSubmit = findViewById(R.id.act_fill_order_for_dry_clean_submit_order);
		mTotalFee = (TextView) findViewById(R.id.act_fill_order_for_dry_clean_totalFee);
		mAddressEdit = (EditText) findViewById(R.id.act_fill_order_for_dry_clean_address);
		mPhoneEdit = (EditText) findViewById(R.id.act_fill_order_for_dry_clean_phone);
		mNameEdit = (EditText) findViewById(R.id.act_fill_order_for_dry_clean_name);
		mInvoiceEdit = (EditText)findViewById(R.id.act_fill_order_for_dry_clean_invoice_title);
		
		mYearWheel = (WheelVerticalView)findViewById(R.id.act_fill_order_for_dry_clean_wheel_year);
		mMonthWheel = (WheelVerticalView)findViewById(R.id.act_fill_order_for_dry_clean_wheel_month);
		mDayWheel = (WheelVerticalView)findViewById(R.id.act_fill_order_for_dry_clean_wheel_day);
	}

	public void createCommodityView(DryCleanModel scm) {
		View v = getLayoutInflater().inflate(
				R.layout.include_fill_order_dry_clean, mCommodityArea, false);
		TextView count = (TextView) v
				.findViewById(R.id.include_fill_order_dry_clean_commodity_count);
		ImageView img = (ImageView) v
				.findViewById(R.id.include_fill_order_dry_clean_commodity_img);
		TextView name = (TextView) v
				.findViewById(R.id.include_fill_order_dry_clean_commodity_name);
		TextView price = (TextView) v
				.findViewById(R.id.include_fill_order_dry_clean_commodity_price);
		count.setText("x" + scm.count);
		ImageLoader.getInstance().displayImage(scm.image, img, options);
		name.setText(scm.name);
		price.setText(scm.price);

		mCommodityArea.addView(v);
	}

	private void initPullView() {
		if (null != mCartManager.mCartList
				&& mCartManager.mCartList.size() > 0) {
			createCommodityView(mCartManager.mCartList.get(0));
			if (mCartManager.mCartList.size() > 1) {
				mPullView.setVisibility(View.VISIBLE);
				mHandleTxt.setText("共" + mCartManager.mCartList.size() + "件商品，点击展开");
				mPullView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mCommodityArea.getChildCount() == 1) {
							for (int i = 1; i < mCartManager.mCartList
									.size(); i++) {
								createCommodityView(mCartManager.mCartList
										.get(i));
							}
							mHandleView.setImageResource(R.drawable.ic_push);
							mHandleTxt.setText("点击收起");
						} else {
							mCommodityArea.removeViews(1,
									mCommodityArea.getChildCount() - 1);
							mHandleView.setImageResource(R.drawable.ic_pull);
							mHandleTxt.setText("共" + mCartManager.mCartList.size() + "件商品，点击展开");
						}
					}
				});
			}
		}
	}

	@Override
	protected void initViews() {
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
		initCalendar();
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo);
		mTopCenterImg.setVisibility(View.VISIBLE);
		initPullView();

		mSelectTypeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CommonDialog cd = CommonDialog
						.creatDialog(FillOrderFroDryCleanActivity.this);
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
	
	private void initCalendar(){
		final int curYear = mCurCalendar.get(Calendar.YEAR);
		final int maxYear = mMaxCalendar.get(Calendar.YEAR);
		final int curMounth = mCurCalendar.get(Calendar.MONTH) + 1;
		final int maxMounth = mMaxCalendar.get(Calendar.MONTH) + 1;
		mYearWheel.setViewAdapter(new NumericWheelAdapter(this, curYear
				, maxYear));
		
		//设置可选月数
		if(curYear == maxYear){
			mMonthWheel.setViewAdapter(new NumericWheelAdapter(this, curMounth
					, maxMounth, "%02d"));
		}else{
			mMonthWheel.setViewAdapter(new NumericWheelAdapter(this, curMounth
					, 12, "%02d"));
			
			mYearWheel.addChangingListener(new OnWheelChangedListener() {
				
				@Override
				public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
					newValue = paresStrDate(((NumericWheelAdapter)mMonthWheel.getViewAdapter()).getItemText(newValue).toString());
					//设置可选月数
					if(newValue == curYear){
						mMonthWheel.setViewAdapter(new NumericWheelAdapter(FillOrderFroDryCleanActivity.this, curMounth
								, 12, "%02d"));
					}else if(newValue == maxYear){
						mMonthWheel.setViewAdapter(new NumericWheelAdapter(FillOrderFroDryCleanActivity.this, 1
								, maxMounth, "%02d"));
					}else{
						mMonthWheel.setViewAdapter(new NumericWheelAdapter(FillOrderFroDryCleanActivity.this, 1
								, 12, "%02d"));
					}
					mMonthWheel.setCurrentItem(0, true);
				}
			});
		}
		
		
		
		if(curMounth == maxMounth){
			mDayWheel.setViewAdapter(new NumericWheelAdapter(this, mCurCalendar.get(Calendar.DAY_OF_MONTH)
					, mMaxCalendar.get(Calendar.DAY_OF_MONTH), "%02d"));
		}else{
			mDayWheel.setViewAdapter(new NumericWheelAdapter(this, mCurCalendar.get(Calendar.DAY_OF_MONTH)
					, mCurCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
			
			mMonthWheel.addChangingListener(new OnWheelChangedListener() {
				
				@Override
				public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
					newValue = paresStrDate(((NumericWheelAdapter)mMonthWheel.getViewAdapter()).getItemText(newValue).toString());
					if(newValue == curMounth){
						mDayWheel.setViewAdapter(new NumericWheelAdapter(FillOrderFroDryCleanActivity.this, mCurCalendar.get(Calendar.DAY_OF_MONTH)
								, mCurCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
					}else if(newValue == maxMounth){
						mDayWheel.setViewAdapter(new NumericWheelAdapter(FillOrderFroDryCleanActivity.this, 1
								, mMaxCalendar.get(Calendar.DAY_OF_MONTH), "%02d"));
					}else{
						Calendar temp = ((Calendar)mCurCalendar.clone());
						temp.set(Calendar.MONTH, newValue - 1);
						mDayWheel.setViewAdapter(new NumericWheelAdapter(FillOrderFroDryCleanActivity.this, 1
								, temp.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
					}
					mDayWheel.setCurrentItem(0, true);
				}
			});
		}
	}
	
	private int paresStrDate(String strDate){
		if(strDate.charAt(0) == 0 && strDate.length() > 1){
			return Integer.valueOf(strDate.substring(1, strDate.length()));
		}
		return Integer.valueOf(strDate);
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
				String result = mRequest.queryAray(1);
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
			cld = CommonLoadingDialog.create(FillOrderFroDryCleanActivity.this);
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
				if (null != mCartManager.mCartList) {
					for (DryCleanModel scm : mCartManager.mCartList) {
						if (null == scmList)
							scmList = new ArrayList<SubmitCommodityModel>();
						scmList.add(new SubmitCommodityModel(scm.count,
								scm.cleanId, null));
					}
				}
				String year = ((NumericWheelAdapter)mYearWheel.getViewAdapter())
						.getItemText(mYearWheel.getCurrentItem()).toString();
				String month = ((NumericWheelAdapter)mMonthWheel.getViewAdapter())
						.getItemText(mMonthWheel.getCurrentItem()).toString();
				String day = ((NumericWheelAdapter)mDayWheel.getViewAdapter())
						.getItemText(mDayWheel.getCurrentItem()).toString();
				
				String takeTime = year + "-" + month + "-" + day;
				
				String commodityInfo = JSON.toJSONString(null == scmList ? ""
						: scmList);
				String result = mRequest.submitOrder("02", UserModelManager
						.getInstance().getmUser().sessId, commodityInfo, takeTime,
						mDispatchMode, mDispatchMode.equals(ZT) ? mArayacakId
								: null, mDispatchMode.equals(ZT) ? null : man,
						phone, invoice, address, null);
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
					Intent intent = new Intent(FillOrderFroDryCleanActivity.this,
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
		case R.id.act_fill_order_for_dry_clean_submit_order:
			invoice = mInvoiceEdit.getText().toString();
			if (mDispatchMode.equals(PS) && doValidate()) {
				new SubmitOrderTask().execute();
			}else if(mDispatchMode.equals(ZT)){
				if(Utils.isEmpty(mArayacakId)){
					showToast("请选择自提点");
				}else
					new SubmitOrderTask().execute();
			}
			break;

		default:
			break;
		}
	}

}
