package com.zhongjie.activity.user;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.RepairHistoryListJson;
import com.zhongjie.model.RepairHitoryModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.view.PromptView;

public class MyRepairsActivity extends BaseSecondActivity{
	
	private PullToRefreshListView mListView;
	public int start = 0, step = 5, maxCount;

	private PromptView mPromptView;
	private CommonRequest mRequest;
	private String sessId;
	private List<RepairHitoryModel> mOrderList;

	private MyRepairsAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_myrepairs);
		super.onCreate(savedInstanceState);
		new QueryRepairHistory().execute();
	}

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		sessId = UserModelManager.getInstance().getmUser().sessId;
	}

	@Override
	protected void findViews() {
		mListView = (PullToRefreshListView)findViewById(R.id.act_myrepairs_listview);
		mPromptView = (PromptView)findViewById(R.id.promptView);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("我的报修");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次刷新时间: " + label);
				start = 0;
				new QueryRepairHistory().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次加载时间: " + label);
				start++;
				new QueryRepairHistory().execute();
			}
			
		});
		mListView.setMode(Mode.PULL_FROM_START);
	}
	
	class QueryRepairHistory extends AsyncTask<String, Void, RepairHistoryListJson> {
		String status;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (start == 0 && !mListView.isRefreshing())
				mPromptView.showLoading();
		}

		@Override
		protected RepairHistoryListJson doInBackground(String... params) {
			RepairHistoryListJson eclj = null;
			String json = mRequest.queryRepairHistory(sessId, start, step);
			if (!TextUtils.isEmpty(json)) {
				eclj = JSON.parseObject(json, RepairHistoryListJson.class);
			}
			return eclj;
		}

		@Override
		protected void onPostExecute(RepairHistoryListJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			if (start == 0)
				mPromptView.showContent();
			if(mListView.isRefreshing())
				mListView.onRefreshComplete();
			
			if (null != result) {
				if (0 == result.code) {
					if (null != result.data
							&& result.data.repairItemCount > 0) {
						maxCount = result.data.repairItemCount;
						int maxPage = maxCount % step == 0 ? maxCount / step
								: maxCount / step + 1;
						if(start + 1 == maxPage)
							mListView.setMode(Mode.PULL_FROM_START);
						else if(start + 1 < maxPage)
							mListView.setMode(Mode.BOTH);
						
						if(start == 0){
							mOrderList = result.data.repairItem;
							if(null == mAdapter)
								mAdapter = new MyRepairsAdapter();
							mListView.setAdapter(mAdapter);
						}else{
							mOrderList.addAll(result.data.repairItem);
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
	
	class MyRepairsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return null == mOrderList ? 0 : mOrderList.size();
		}

		@Override
		public RepairHitoryModel getItem(int position) {
			return null == mOrderList ? null : mOrderList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if(null == convertView){
				convertView = getLayoutInflater().inflate(R.layout.listview_item_myrepairs, null);
				vh = new ViewHolder();
				vh.repairsType = (TextView)convertView.findViewById(R.id.list_item_myrepairs);
				vh.address = (TextView)convertView.findViewById(R.id.list_item_myrepairs_address);
				vh.time = (TextView)convertView.findViewById(R.id.list_item_myrepairs_time);
				vh.orderTime = (TextView)convertView.findViewById(R.id.list_item_myrepairs_time_order);
				vh.photo = (ImageView)convertView.findViewById(R.id.list_item_myrepairs_photo);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			
			RepairHitoryModel repair = getItem(position);
			
			vh.repairsType.setText(Html.fromHtml("报修分类  : <font color='red'>" + repair.classify + "</font>"));
			vh.address.setText(repair.unit + "栋" + repair.room + "室");
			vh.orderTime.setText("预约时间 : " + repair.repairDate);
			vh.time.setText("报修时间 : " + repair.createTime);
			
			ImageLoader.getInstance().displayImage(repair.image, vh.photo, options);
			
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		TextView repairsType, address, time, orderTime;
		ImageView photo;
	}
}
