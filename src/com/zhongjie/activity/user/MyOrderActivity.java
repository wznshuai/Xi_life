package com.zhongjie.activity.user;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.anytimebuy.SendCommentActivity;
import com.zhongjie.activity.shoppingcar.FillOrderActivity;

public class MyOrderActivity extends BaseSecondActivity {

	private static String STATUS_WAIT_PAY = "待支付";
	private static String STATUS_WAIT_COMMENT = "待评价";
	private static String STATUS_HAD_COMMPLETED = "已完成";
	private static String STATUS_HAD_CANCELED = "已取消";

	private ListView mListView;

	@Override
	protected void initData() {

	}

	@Override
	protected void findViews() {
		mListView = (ListView) findViewById(R.id.act_order_listview);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("我的订单");
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mListView.setAdapter(new OrderAdapter());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order);
		super.onCreate(savedInstanceState);
	}

	class OrderAdapter extends BaseAdapter {

		String[] statusArray = { STATUS_HAD_CANCELED, STATUS_HAD_COMMPLETED,
				STATUS_WAIT_COMMENT, STATUS_WAIT_PAY };

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (null == convertView) {
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item_myorder, parent, false);
				vh = new ViewHolder();
				vh.doNeed = convertView
						.findViewById(R.id.list_item_myorder_doNeed);
				vh.showStatus = (TextView) convertView
						.findViewById(R.id.list_item_myorder_status);
				vh.trashIC = convertView
						.findViewById(R.id.list_item_myorder_trashIC);
				vh.commodityArea = (ViewGroup) convertView
						.findViewById(R.id.list_item_myorder_commodity_area);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			Random random = new Random();
			String status = statusArray[random.nextInt(4)];
			vh.showStatus.setText(Html.fromHtml("订单状态 : <font color='#cc0000'>"
					+ status + "</font>"));
			vh.commodityArea.removeAllViews();

			for (int i = 0; i < 3; i++) {
				View commodityItem = getLayoutInflater()
						.inflate(R.layout.common_order_view,
								vh.commodityArea, false);
				View goComment = commodityItem
						.findViewById(R.id.common_order_view_goComment);
				goComment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(MyOrderActivity.this,
								SendCommentActivity.class);
						startActivity(intent);
					}
				});
				if (status.equals(STATUS_WAIT_COMMENT)) {
					goComment.setVisibility(View.VISIBLE);
					;
				} else {
					goComment.setVisibility(View.GONE);
					;
				}
				vh.commodityArea.addView(commodityItem);
			}

			if (status.equals(STATUS_WAIT_PAY)) {
				vh.trashIC.setVisibility(View.VISIBLE);
				vh.doNeed.setVisibility(View.VISIBLE);
			} else if (status.equals(STATUS_WAIT_COMMENT)) {
				vh.trashIC.setVisibility(View.GONE);
				vh.doNeed.setVisibility(View.GONE);
			} else {
				vh.trashIC.setVisibility(View.GONE);
				vh.doNeed.setVisibility(View.GONE);
			}

			vh.doNeed.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					TextView tv = (TextView) v;
					String str = tv.getText().toString();
					Intent intent = new Intent();
					if (str.equals("去评价")) {
						intent.setClass(MyOrderActivity.this,
								SendCommentActivity.class);
					} else if (str.equals("去付款")) {
						intent.setClass(MyOrderActivity.this,
								FillOrderActivity.class);
					}
					startActivity(intent);
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView showStatus;
			ViewGroup commodityArea;
			View trashIC, doNeed;
		}
	}

}
