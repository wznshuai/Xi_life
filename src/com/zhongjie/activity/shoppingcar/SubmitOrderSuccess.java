package com.zhongjie.activity.shoppingcar;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.global.Session;
import com.zhongjie.model.OrderSubmitSuccessModel;
import com.zhongjie.util.ShopCartManager;
import com.zhongjie.util.pay.Result;

public class SubmitOrderSuccess extends BaseSecondActivity{
	
	private TextView noticeView, orderNumTxt, totalCountTxt, totalFeeTxt;
	private OrderSubmitSuccessModel mOrderModel;
	private View mGoPay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_submit_order_success);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		mOrderModel = (OrderSubmitSuccessModel)Session.getSession().remove("orderInfo");
	}

	@Override
	protected void findViews() {
		noticeView = (TextView)findViewById(R.id.tempId6);
		orderNumTxt = (TextView)findViewById(R.id.act_submit_order_success_orderNum);
		totalCountTxt = (TextView)findViewById(R.id.act_submit_order_success_totalCount);
		totalFeeTxt = (TextView)findViewById(R.id.act_submit_order_success_totalFee);
		mGoPay = findViewById(R.id.act_submit_order_success_pay);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("提交订单成功");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		noticeView.setText(Html.fromHtml("订单已生成。如您此时暂不支付，可在\"<font color='#ff0099'>我的订单 —— 待支付订单</font>\"中选择支付"));
		if(null != mOrderModel){
			orderNumTxt.setText(mOrderModel.code);
			totalCountTxt.setText(mOrderModel.count + "");
			totalFeeTxt.setText("￥" + mOrderModel.money);
		}
		
		mGoPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mOrderModel){
					new Thread(){
						public void run() {
							PayTask alipay = new PayTask(SubmitOrderSuccess.this);
							String result = alipay.pay(mOrderModel.payInfo);
							Result r = new Result(result);
							if(r.getErrorCode().equals("9000")){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										ShopCartManager.getInstance().mCartList
										.removeAll(ShopCartManager.getInstance().mCheckedList);
										ShopCartManager.getInstance().mCheckedList.clear();
										showToast("交易成功~");
										goHomeActivity();
									}
								});
							}
						};
					}.start();
				}
			}
		});
	}
	
}
