package com.zhongjie.activity.user;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.OrderListJson;
import com.zhongjie.model.OrderModel;
import com.zhongjie.model.OrderStatus;
import com.zhongjie.model.ShopCartModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.pay.Result;
import com.zhongjie.view.PromptView;

public class MyDryCleanOrderActivity extends BaseSecondActivity {

	private static String STATUS_WAIT_PAY = "待支付";
	private static String STATUS_WAIT_COMMENT = "已支付";
	private static String STATUS_HAD_COMMPLETED = "已完成";
	private static String STATUS_HAD_CANCELED = "已取消";
	
	public int start = 0, step = 20, maxCount;

	private PromptView mPromptView;
	private CommonRequest mRequest;
	private String sessId;
	private List<OrderModel> mOrderList;
	private String mCurStatus;
	private RadioGroup mRadioGroup;

	private PullToRefreshListView mListView;
	private OrderAdapter mAdapter;
	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		sessId = UserModelManager.getInstance().getmUser().sessId;
		mCurStatus = getIntent().getStringExtra("status");
	}

	@Override
	protected void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.act_order_listview);
		mRadioGroup = (RadioGroup)findViewById(R.id.act_order_radioGroup);
		mPromptView = (PromptView)findViewById(R.id.promptView);
	}

	@Override
	protected void initViews() {
		
		if(mCurStatus.equals(OrderStatus.STATUS_HAD_CANCELED)){
			System.out.println("STATUS_HAD_CANCELED");
			mRadioGroup.check(mRadioGroup.getChildAt(6).getId());
		}else if(mCurStatus.equals(OrderStatus.STATUS_HAD_COMMPLETED)){
			mRadioGroup.check(mRadioGroup.getChildAt(4).getId());
		}else if(mCurStatus.equals(OrderStatus.STATUS_WAIT_COMMENT)){
			mRadioGroup.check(mRadioGroup.getChildAt(2).getId());
		}else if(mCurStatus.equals(OrderStatus.STATUS_WAIT_PAY)){
			mRadioGroup.check(mRadioGroup.getChildAt(0).getId());
		}
		
		mTopCenterTxt.setText("干洗记录");
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setVisibility(View.VISIBLE);
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mCurStatus = getOrderStatusCode(((RadioButton)group.findViewById(checkedId)).getText().toString());
				start = 0;
				if(null != mOrderList)
					mOrderList.clear();
				mListView.onRefreshComplete();
				if(null != mAdapter)
					mAdapter.notifyDataSetChanged();
				new QueryUserOrderTask().execute(mCurStatus);
			}
		});
		
		
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次刷新时间: " + label);
				start = 0;
				new QueryUserOrderTask().execute(mCurStatus);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次加载时间: " + label);
				start++;
				new QueryUserOrderTask().execute(mCurStatus);
			}
			
		});
		mListView.setMode(Mode.PULL_FROM_START);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_dryclean_order);
		super.onCreate(savedInstanceState);
		new QueryUserOrderTask().execute(mCurStatus);
	}

	class QueryUserOrderTask extends AsyncTask<String, Void, OrderListJson> {
		String status;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (start == 0 && !mListView.isRefreshing())
				mPromptView.showLoading();
		}

		@Override
		protected OrderListJson doInBackground(String... params) {
			status = params[0];
			OrderListJson eclj = null;
			String json = mRequest.queryDryCleanOrder(sessId, status, start, step);
			if (!TextUtils.isEmpty(json)) {
				eclj = JSON.parseObject(json, OrderListJson.class);
			}
			return eclj;
		}

		@Override
		protected void onPostExecute(OrderListJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			
			if(!status.equals(mCurStatus))
				return;
			
			if (start == 0)
				mPromptView.showContent();
			if(mListView.isRefreshing())
				mListView.onRefreshComplete();
			
			if (null != result) {
				if (0 == result.code) {
					if (null != result.data
							&& result.data.orderItemCount > 0) {
						maxCount = result.data.orderItemCount;
						int maxPage = maxCount % step == 0 ? maxCount / step
								: maxCount / step + 1;
						if(start + 1 == maxPage)
							mListView.setMode(Mode.PULL_FROM_START);
						else if(start + 1 < maxPage)
							mListView.setMode(Mode.BOTH);
						
						if(start == 0){
							mOrderList = result.data.orderItem;
							if(null == mAdapter)
								mAdapter = new OrderAdapter();
							mListView.setAdapter(mAdapter);
						}else{
							mOrderList.addAll(result.data.orderItem);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						if(null == mOrderList || mOrderList.size() == 0)
							mPromptView.showEmpty();
					}
				} else {
					showToast(result.errMsg);
					if(null == mOrderList || mOrderList.size() == 0)
						mPromptView.showError();
				}
			} else {
				if(null == mOrderList || mOrderList.size() == 0)
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
	
	@Override
	protected void onResume() {
		super.onResume();
		mListView.setCurrentMode(Mode.PULL_FROM_START);
		mListView.setRefreshing();
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
				
				vh.doNeed.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView tv = (TextView) v;
						final OrderModel order = getItem((Integer)v.getTag());
						String str = tv.getText().toString();
						if (str.equals("去付款")) {
							new Thread(){
								@Override
								public void run() {
									PayTask alipay = new PayTask(MyDryCleanOrderActivity.this);
									String resultStr = alipay.pay(order.payInfo);
									Result r = new Result(resultStr);
									if(r.getErrorCode().equals("9000")){
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												showToast("交易成功~");
											}
										});
									}
								}
							}.start();
						}
					}
				});

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			
			final OrderModel order = getItem(position);
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
						TextView commodityName = (TextView)commodityItem
								.findViewById(R.id.common_order_view_commodityName);
						ImageView commodityImg = (ImageView)commodityItem.findViewById(R.id.common_order_img);
						TextView commodityPrice = (TextView)commodityItem.findViewById(R.id.common_order_price);
						TextView commodityCount = (TextView)commodityItem.findViewById(R.id.common_order_count);
						View goComment = commodityItem.findViewById(R.id.common_order_view_goComment);
						
						commodityName.setText(scm.name);
						ImageLoader.getInstance().displayImage(scm.image, commodityImg);
						commodityPrice.setText(scm.price);
						commodityCount.setText("数量 : " + scm.number);
						goComment.setVisibility(View.GONE);
						vh.commodityArea.addView(commodityItem);
					}
				}
				
				vh.doNeed.setTag(position);

				if (status.equals(STATUS_WAIT_PAY)) {
//					vh.trashIC.setVisibility(View.VISIBLE);
					vh.doNeed.setVisibility(View.VISIBLE);
				} else if (status.equals(STATUS_WAIT_COMMENT)) {
					vh.trashIC.setVisibility(View.GONE);
					vh.doNeed.setVisibility(View.GONE);
				} else {
					vh.trashIC.setVisibility(View.GONE);
					vh.doNeed.setVisibility(View.GONE);
				}

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
