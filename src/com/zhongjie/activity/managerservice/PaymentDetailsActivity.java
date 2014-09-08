package com.zhongjie.activity.managerservice;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.PaymentTypeListJson;
import com.zhongjie.model.PaymentTypeListModel;
import com.zhongjie.model.PaymentTypeModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.util.pay.Result;
import com.zhongjie.view.CommonLoadingDialog;

public class PaymentDetailsActivity extends BaseSecondActivity{
	
	private CommonRequest mRequest;
	private String year, quarter;
	private TextView mAddressTxt, mDateTxt, mTotalFeeTxt;
	private LinearLayout mTypeArea;
	private View mSubmitView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_payment_details);
		super.onCreate(savedInstanceState);
		new QueryPaymentTypeTask().execute();
	}
	
	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		year = getIntent().getStringExtra("year");
		quarter = getIntent().getStringExtra("quarter");
	}

	@Override
	protected void findViews() {
		mAddressTxt = (TextView)findViewById(R.id.act_payment_details_address);
		mDateTxt = (TextView)findViewById(R.id.act_payment_details_date);
		mTotalFeeTxt = (TextView)findViewById(R.id.act_payment_details_totalFee);
		mTypeArea = (LinearLayout)findViewById(R.id.act_payment_details_paytype);
		mSubmitView = findViewById(R.id.act_payment_details_submit);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo_payment);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mDateTxt.append(year+"年"+intToStr(quarter)+"季度");
	}
	
	private void initInfos(final PaymentTypeListModel typeModel){
		if(null == typeModel)
			return;
		mAddressTxt.append(typeModel.unit+"栋"+typeModel.room+"室");
//		mDateTxt.append(typeModel.year+"年"+intToStr(typeModel.quarter)+"季度");
		if(null != typeModel.item && typeModel.item.size() > 0){
			for(int i = 0;i < typeModel.item.size();i++){
				PaymentTypeModel ptm = typeModel.item.get(i);
				LinearLayout ll = (LinearLayout)getLayoutInflater()
						.inflate(R.layout.include_paytype, mTypeArea, false);
				TextView name = (TextView)ll.findViewById(R.id.include_paytype_name);
				TextView money = (TextView)ll.findViewById(R.id.include_paytype_money);
				name.setText(ptm.item);
				money.append(ptm.money + "元");
				mTypeArea.addView(ll);
				if(i != typeModel.item.size() - 1){
					mTypeArea.addView(getLayoutInflater()
						.inflate(R.layout.include_dotted_line, mTypeArea, false));
				}
			}
			mSubmitView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new Thread(){
						@Override
						public void run() {
							PayTask alipay = new PayTask(PaymentDetailsActivity.this);
							String resultStr = alipay.pay(typeModel.payInfo);
							Result r = new Result(resultStr);
							if(r.getErrorCode().equals("9000")){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												showToast("交易成功~");
												goHomeActivity(MainActivity.TAB_2);
											}
										});
									}
								});
							}
						}
					}.start();
				}
			});
		}else{
			mSubmitView.setEnabled(false);
		}
		mTotalFeeTxt.setText("￥" + typeModel.totalMoney);
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
	
	class QueryPaymentTypeTask extends AsyncTask<String, Void, PaymentTypeListJson> {
		CommonLoadingDialog cld;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(PaymentDetailsActivity.this);
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
		protected PaymentTypeListJson doInBackground(String... params) {
			PaymentTypeListJson uj = null;
			try {

				String result = mRequest.paymentQuery(UserModelManager
						.getInstance().getmUser().sessId, year, quarter);
				if (!TextUtils.isEmpty(result)) {
					uj = JSON.parseObject(result, PaymentTypeListJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(),
						"QueryPaymentTypeTask error", e);
			}
			return uj;
		}

		@Override
		protected void onPostExecute(PaymentTypeListJson result) {
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
	
}
