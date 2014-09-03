package com.zhongjie.activity.managerservice;

import java.util.Calendar;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import antistatic.spinnerwheel.WheelVerticalView;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import com.alibaba.fastjson.JSON;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.model.PaymentShowJson;
import com.zhongjie.model.PaymentShowModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.view.CommonLoadingDialog;

public class PaymentActivity extends BaseSecondActivity {

	private WheelVerticalView mYearWheel, mMonthWheel;
	private Calendar mCurCalendar;
	private View mSubmit;
	private CommonRequest mRequest;
	private TextView mAddress;
	private PaymentShowModel mPaymentShowModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_payment);
		super.onCreate(savedInstanceState);
		new QueryPaymentInfoTask().execute();
	}

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		mCurCalendar = Calendar.getInstance(Locale.CHINA);
	}

	@Override
	protected void findViews() {
		mYearWheel = (WheelVerticalView) findViewById(R.id.act_payment_wheel_year);
		mMonthWheel = (WheelVerticalView) findViewById(R.id.act_payment_wheel_month);
		mSubmit = findViewById(R.id.act_payment_submit);
		mAddress = (TextView) findViewById(R.id.act_payment_address);
	}

	private void initCalendar() {
		final int curYear = mCurCalendar.get(Calendar.YEAR);
		mYearWheel.setViewAdapter(new NumericWheelAdapter(this, curYear,
				curYear));
		mMonthWheel.setViewAdapter(new ArrayWheelAdapter<String>(this,
				new String[] { "未知" }));
	}


	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo_payment);
		mTopCenterImg.setVisibility(View.VISIBLE);

		initCalendar();

		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserModelManager.getInstance().isLogin()) {
					Intent intent = new Intent(PaymentActivity.this, PaymentDetailsActivity.class);
					intent.putExtra("year", mPaymentShowModel.year);
					intent.putExtra("quarter", mPaymentShowModel.quarter[mMonthWheel.getCurrentItem()]);
					startActivity(intent);
				} else {
					startActivity(new Intent(PaymentActivity.this,
							LoginActivity.class));
				}
			}
		});
	}

	private void initInfos(PaymentShowModel rsm) {
		if (null != rsm) {
			mPaymentShowModel = rsm;
			mAddress.setText("房号 :  " + rsm.unit + "栋" + rsm.room + "室");
			if (null != rsm.quarter) {
				int size = rsm.quarter.length;
				String[] a = new String[size];
				for (int i = 0; i < rsm.quarter.length; i++) {
					a[i] = intToStr(rsm.quarter[i]);
				}

				mMonthWheel.setViewAdapter(new ArrayWheelAdapter<String>(this,
						a));
			}
			rsm = null;
		}
	}
	
	private String intToStr(String quarter){
		if(quarter.equals("1")){
			return "一";
		}else if(quarter.equals("2")){
			return "二";
		}else if(quarter.equals("3")){
			return "三";
		}else if(quarter.equals("4")){
			return "四";
		}
		return "无效的";
	}
	
	@SuppressWarnings("unused")
	private String strToInt(String quarter){
		if(quarter.equals("一")){
			return "1";
		}else if(quarter.equals("二")){
			return "2";
		}else if(quarter.equals("三")){
			return "3";
		}else if(quarter.equals("四")){
			return "4";
		}
		return "无效的";
	}
	

	class QueryPaymentInfoTask extends AsyncTask<String, Void, PaymentShowJson> {
		CommonLoadingDialog cld;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(PaymentActivity.this);
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
		protected PaymentShowJson doInBackground(String... params) {
			PaymentShowJson uj = null;
			try {

				String result = mRequest.paymentShow(UserModelManager
						.getInstance().getmUser().sessId);
				if (!TextUtils.isEmpty(result)) {
					uj = JSON.parseObject(result, PaymentShowJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(),
						"QueryPaymentInfoTask error", e);
			}
			return uj;
		}

		@Override
		protected void onPostExecute(PaymentShowJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			if (null != result) {
				if (result.code == 0) {
					if (null != result.data) {
						initInfos(result.data);
					}
				} else {
					showToast(result.errMsg);
				}
			}
		}
	}

//	class SubmitRepair extends AsyncTask<String, Void, BaseJson> {
//		CommonLoadingDialog cld;
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			cld = CommonLoadingDialog.create(PaymentActivity.this);
//			cld.setCanceledOnTouchOutside(false);
//			cld.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					cancel(true);
//				}
//			});
//			cld.show();
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		protected BaseJson doInBackground(String... params) {
//			BaseJson uj = null;
//			try {
//				String year = ((NumericWheelAdapter) mYearWheel
//						.getViewAdapter()).getItemText(
//						mYearWheel.getCurrentItem()).toString();
//				String month = ((NumericWheelAdapter) mMonthWheel
//						.getViewAdapter()).getItemText(
//						mMonthWheel.getCurrentItem()).toString();
//				String day = ((NumericWheelAdapter) mDayWheel.getViewAdapter())
//						.getItemText(mDayWheel.getCurrentItem()).toString();
//				mRepairModel.needClassify = ((ArrayWheelAdapter<String>) mRepairsTypeWheel
//						.getViewAdapter()).getItemText(
//						mYearWheel.getCurrentItem()).toString();
//				mRepairModel.needRepairDate = year + "-" + month + "-" + day;
//				String result = mRequest.repairSubmit(UserModelManager
//						.getInstance().getmUser().sessId,
//						mRepairModel.needRepairDate, mRepairModel.needClassify,
//						mRepairModel.image);
//				if (!TextUtils.isEmpty(result)) {
//					uj = JSON.parseObject(result, BaseJson.class);
//				}
//			} catch (Exception e) {
//				Logger.e(getClass().getSimpleName(),
//						"QueryRepairsInfoTask error", e);
//			}
//			return uj;
//		}
//
//		@Override
//		protected void onPostExecute(BaseJson result) {
//			super.onPostExecute(result);
//			if (!canGoon())
//				return;
//			if (null != cld) {
//				cld.cancel();
//				cld = null;
//			}
//			if (null != result) {
//				if (result.code == 0) {
//					Session session = Session.getSession();
//					session.put("repairModel", mRepairModel);
//					startActivity(new Intent(PaymentActivity.this,
//							RepairsSuccess.class));
//				} else {
//					showToast(result.errMsg);
//				}
//			}
//		}
//	}
}
