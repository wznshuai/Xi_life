package com.zhongjie.activity.user;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseListActivity;
import com.zhongjie.activity.anytimebuy.SendCommentActivity;
import com.zhongjie.activity.shoppingcar.FillOrderActivity;
import com.zhongjie.model.OrderListJson;
import com.zhongjie.model.OrderModel;
import com.zhongjie.model.OrderStatus;
import com.zhongjie.model.ShopCartModel;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.view.PromptView;

public class MyOrderActivity extends BaseListActivity {

	private static String STATUS_WAIT_PAY = "待支付";
	private static String STATUS_WAIT_COMMENT = "待评价";
	private static String STATUS_HAD_COMMPLETED = "已完成";
	private static String STATUS_HAD_CANCELED = "已取消";

	private PromptView mPromptView;
	private CommonRequest mRequest;
	private String sessId;
	private List<OrderModel> mOrderList;
	private String mCurStatus;
	private RadioGroup mRadioGroup;

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		sessId = getIntent().getStringExtra("sessId");
		mCurStatus = getIntent().getStringExtra("status");
	}

	@Override
	protected void findViews() {
		mListView = (ListView) findViewById(R.id.act_order_listview);
		mRadioGroup = (RadioGroup)findViewById(R.id.act_order_radioGroup);
	}

	@Override
	protected void initViews() {
		
		if(mCurStatus.equals(OrderStatus.STATUS_HAD_CANCELED)){
			mRadioGroup.check(mRadioGroup.getChildAt(3).getId());
		}else if(mCurStatus.equals(OrderStatus.STATUS_HAD_COMMPLETED)){
			mRadioGroup.check(mRadioGroup.getChildAt(2).getId());
		}else if(mCurStatus.equals(OrderStatus.STATUS_WAIT_COMMENT)){
			mRadioGroup.check(mRadioGroup.getChildAt(1).getId());
		}else if(mCurStatus.equals(OrderStatus.STATUS_WAIT_PAY)){
			mRadioGroup.check(mRadioGroup.getChildAt(0).getId());
		}
		
		mTopCenterTxt.setText("我的订单");
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mListView.setAdapter(new OrderAdapter());
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition()
							- mListView.getHeaderViewsCount() == view
							.getAdapter().getCount() - 1) {
						int maxPage = maxCount % step == 0 ? maxCount / step
								: maxCount / step + 1;
						if (start + 1 < maxPage) {
							start++;
							new QueryUserOrderTask().execute();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mCurStatus = getOrderStatusCode(((RadioButton)group.findViewById(checkedId)).getText().toString());
				mListView.setAdapter(null);
				start = 0;
				new QueryUserOrderTask().execute();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order);
		super.onCreate(savedInstanceState);
		new QueryUserOrderTask().execute();
	}

	class QueryUserOrderTask extends AsyncTask<String, Void, OrderListJson> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (start == 0)
				mPromptView.showLoading();
			else
				showFooterView(FooterView.MORE);
		}

		@Override
		protected OrderListJson doInBackground(String... params) {
			OrderListJson eclj = null;
			String json = mRequest.queryUserOrder(sessId, mCurStatus, start, step);
			if (!TextUtils.isEmpty(json)) {
				eclj = JSON.parseObject(json, OrderListJson.class);
			}
			return eclj;
		}

		@Override
		protected void onPostExecute(OrderListJson result) {
			super.onPostExecute(result);
			if (!canGOON())
				return;
			if (start == 0)
				mPromptView.showContent();
			else
				showFooterView(FooterView.HIDE_ALL);
			if (null != result) {
				if (0 == result.code) {
					if (null != result.data
							&& result.data.orderItemCount > 0) {
						maxCount = result.data.orderItemCount;
						mOrderList = result.data.orderItem;
						mListView.setAdapter(new OrderAdapter());
					} else {
						mPromptView.showEmpty();
					}
				} else {
					showToast(result.errMsg);
					mPromptView.showError();
				}
			} else {
				mPromptView.showError();
			}
		}
	}
	
	public String getOrderStatusName(String status){
		if(status.equals(OrderStatus.STATUS_HAD_CANCELED)){
			return STATUS_HAD_CANCELED;
		}else if(status.equals(OrderStatus.STATUS_HAD_COMMPLETED)){
			return STATUS_HAD_COMMPLETED;
		}else if(status.equals(OrderStatus.STATUS_WAIT_COMMENT)){
			return STATUS_WAIT_COMMENT;
		}else if(status.equals(OrderStatus.STATUS_WAIT_PAY)){
			return STATUS_WAIT_PAY;
		}else{
			return "";
		}
	}
	
	public String getOrderStatusCode(String status){
		if(status.equals(STATUS_HAD_CANCELED)){
			return OrderStatus.STATUS_HAD_CANCELED;
		}else if(status.equals(STATUS_HAD_COMMPLETED)){
			return OrderStatus.STATUS_HAD_COMMPLETED;
		}else if(status.equals(STATUS_WAIT_COMMENT)){
			return OrderStatus.STATUS_WAIT_COMMENT;
		}else if(status.equals(STATUS_WAIT_PAY)){
			return OrderStatus.STATUS_WAIT_PAY;
		}else{
			return "";
		}
	}

	class OrderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return null == mOrderList ? 0 : mOrderList.size();
		}

		@Override
		public OrderModel getItem(int position) {
			return null == mOrderList ? null : mOrderList.get(position);
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
			
			OrderModel order = getItem(position);
			if(null != order){
				String status = getOrderStatusName(order.orderStatus);
				vh.showStatus.setText(Html.fromHtml("订单状态 : <font color='#cc0000'>"
						+ status + "</font>"));
				vh.commodityArea.removeAllViews();
				
				if(null != order.orderDetail && order.orderDetail.size() > 0){
					int size = order.orderDetail.size();
					for (int i = 0; i < size; i++) {
						ShopCartModel scm = order.orderDetail.get(i);
						View commodityItem = getLayoutInflater().inflate(
								R.layout.common_order_view, vh.commodityArea, false); 
						View goComment = commodityItem
								.findViewById(R.id.common_order_view_goComment);
						TextView commodityName = (TextView)commodityItem
								.findViewById(R.id.common_order_view_commodityName);
						ImageView commodityImg = (ImageView)commodityItem.findViewById(R.id.common_order_img);
						TextView commodityPrice = (TextView)commodityItem.findViewById(R.id.common_order_price);
						TextView commodityCount = (TextView)commodityItem.findViewById(R.id.common_order_count);
						
						commodityName.setText(scm.name);
						ImageLoader.getInstance().displayImage(scm.image, commodityImg);
						commodityPrice.setText(scm.price);
						commodityCount.setText(scm.number);
						
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
			}
			return convertView;
		}

		class ViewHolder {
			TextView showStatus;
			ViewGroup commodityArea;
			View trashIC, doNeed;
		}
	}

}
