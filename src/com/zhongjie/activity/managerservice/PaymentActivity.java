package com.zhongjie.activity.managerservice;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.WheelVerticalView;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import com.alibaba.fastjson.JSON;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.global.Session;
import com.zhongjie.model.BaseJson;
import com.zhongjie.model.PaymentShowJson;
import com.zhongjie.model.PaymentShowModel;
import com.zhongjie.model.RepairShowModel;
import com.zhongjie.model.RepairsShowJson;
import com.zhongjie.model.UploadImageJson;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.Logger;
import com.zhongjie.view.CommonDialog2;
import com.zhongjie.view.CommonLoadingDialog;

public class PaymentActivity extends BaseSecondActivity {

	private WheelVerticalView mYearWheel, mMonthWheel;
	private Calendar mCurCalendar;
	private View mSubmit;
	private CommonRequest mRequest;
	private TextView mAddress;
	private PaymentShowModel mRepairModel;

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

	private int paresStrDate(String strDate) {
		if (strDate.charAt(0) == 0 && strDate.length() > 1) {
			return Integer.valueOf(strDate.substring(1, strDate.length()));
		}
		return Integer.valueOf(strDate);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_logo_repair_top);
		mTopCenterImg.setVisibility(View.VISIBLE);

		initCalendar();

		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserModelManager.getInstance().isLogin()) {
//					new SubmitRepair().execute();
				} else {
					startActivity(new Intent(PaymentActivity.this,
							LoginActivity.class));
				}
			}
		});
	}

	private void initInfos(PaymentShowModel rsm) {
		if (null != rsm) {
			mRepairModel = rsm;
			mAddress.setText("房号 :  " + rsm.unit + "栋" + rsm.room + "室");
			if (null != rsm.quarter) {
				int size = rsm.quarter.length;
				String[] a = new String[size];
				for (int i = 0; i < rsm.quarter.length; i++) {
					a[i] = intToStr(rsm.quarter[i]) + "季度";
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
