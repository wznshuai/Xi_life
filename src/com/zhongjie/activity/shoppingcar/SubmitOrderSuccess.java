package com.zhongjie.activity.shoppingcar;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class SubmitOrderSuccess extends BaseSecondActivity{
	
	private TextView noticeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_submit_order_success);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		noticeView = (TextView)findViewById(R.id.tempId6);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("提交订单成功");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		noticeView.setText(Html.fromHtml("订单已生成。如您此时暂不支付，可在\"<font color='#ff0099'>我的订单 —— 待支付订单</font>\"中选择支付"));
	}
	
}
