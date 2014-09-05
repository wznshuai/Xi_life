package com.zhongjie.activity.user;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
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
import com.zhongjie.model.IntegralListJson;
import com.zhongjie.model.IntegralModel;
import com.zhongjie.model.UserModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.view.PromptView;

public class IntegralActivity extends BaseSecondActivity{
	
	private PullToRefreshListView mListView;
	public int start = 0, step = 20, maxCount;
	private PromptView mPromptView;
	private CommonRequest mRequest;
	private String sessId;
	private List<IntegralModel> mIntegralList;
	private MyIntegralAdapter mAdapter;
	private UserModel um;
	private ImageView head;
	private TextView name, address, integral1, integral2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_integral);
		super.onCreate(savedInstanceState);
		new QueryUserIntegralTask().execute();
	}
	

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		um = UserModelManager.getInstance().getmUser();
		if(null != um)
			sessId = um.sessId;
	}

	@Override
	protected void findViews() {
		mListView = (PullToRefreshListView)findViewById(R.id.act_integral_listview);
		mPromptView = (PromptView)findViewById(R.id.promptView);
		head = (ImageView)findViewById(R.id.act_integral_head);
		address = (TextView)findViewById(R.id.act_integral_address);
		integral1 = (TextView)findViewById(R.id.act_integral_integral_1);
		integral2 = (TextView)findViewById(R.id.act_integral_integral2);
		name = (TextView)findViewById(R.id.act_integral_name);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mListView.setMode(Mode.DISABLED);
		if(null != um){
			ImageLoader.getInstance().displayImage(um.image, head, options);
			address.setText(um.unit + "栋" + um.room + "室");
			integral1.setText("积分 : " + um.integral);
			integral2.setText("积分  " + um.integral);
			name.setText(TextUtils.isEmpty(um.nickName) ? getString(R.string.nickname_null)
					: um.nickName);
		}
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("上次加载时间: " + label);
				start++;
				new QueryUserIntegralTask().execute();
			}
			
		});
	}
	
	class QueryUserIntegralTask extends AsyncTask<String, Void, IntegralListJson> {
		String status;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (start == 0 && !mListView.isRefreshing())
				mPromptView.showLoading();
		}

		@Override
		protected IntegralListJson doInBackground(String... params) {
			IntegralListJson eclj = null;
			String json = mRequest.queryUserIntegral(sessId, start, step);
			if (!TextUtils.isEmpty(json)) {
				eclj = JSON.parseObject(json, IntegralListJson.class);
			}
			return eclj;
		}

		@Override
		protected void onPostExecute(IntegralListJson result) {
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
							&& result.data.integralItemCount > 0) {
						maxCount = result.data.integralItemCount;
						int maxPage = maxCount % step == 0 ? maxCount / step
								: maxCount / step + 1;
						if(start + 1 == maxPage)
							mListView.setMode(Mode.DISABLED);
						else if(start + 1 < maxPage)
							mListView.setMode(Mode.PULL_FROM_END);
						
						if(start == 0){
							mIntegralList = result.data.integralItem;
							if(null == mAdapter)
								mAdapter = new MyIntegralAdapter();
							mListView.setAdapter(mAdapter);
						}else{
							mIntegralList.addAll(result.data.integralItem);
							mAdapter.notifyDataSetChanged();
						}
					} else {
						if(null == mIntegralList || mIntegralList.size() == 0)
							mPromptView.showEmpty();
					}
				} else {
					showToast(result.errMsg);
					if(null == mIntegralList || mIntegralList.size() == 0)
						mPromptView.showError();
				}
			} else {
				if(null == mIntegralList || mIntegralList.size() == 0)
					mPromptView.showError();
			}
		}
	}
	
	class MyIntegralAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return null == mIntegralList ? 0 : mIntegralList.size();
		}

		@Override
		public IntegralModel getItem(int position) {
			return null == mIntegralList ? null : mIntegralList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if(null == convertView){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(IntegralActivity.this).inflate(R.layout.listview_item_integral, null);
				vh.circularView = (ImageView)convertView.findViewById(R.id.list_item_integral_circular);
				vh.cost = (TextView)convertView.findViewById(R.id.list_item_integral_cost);
				vh.item = (TextView)convertView.findViewById(R.id.list_item_integral_item);
				vh.currTime = (TextView)convertView.findViewById(R.id.list_item_integral_currTime);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			IntegralModel integral = getItem(position);
			
			vh.cost.setText(integral.cost);
			vh.item.setText(integral.item);
			vh.currTime.setText(integral.currTime);
			
			if(position == 0){
				vh.circularView.setImageResource(R.drawable.circular_juhuang);
			}else if(position == 1){
				vh.circularView.setImageResource(R.drawable.circular_qianlv);
			}else{
				vh.circularView.setImageResource(R.drawable.circular_blue);
			}
			return convertView;
		}
		
		
		class ViewHolder{
			ImageView circularView;
			TextView item, currTime, cost;
		}
	}
	
}
