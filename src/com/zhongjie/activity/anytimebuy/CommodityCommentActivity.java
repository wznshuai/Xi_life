package com.zhongjie.activity.anytimebuy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseListActivity;
import com.zhongjie.model.CommentCommodityListJson;
import com.zhongjie.model.CommentCommodityListModel;
import com.zhongjie.model.CommentCommodiytModel;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Utils;
import com.zhongjie.view.MyRatingbar;
import com.zhongjie.view.PromptView;

public class CommodityCommentActivity extends BaseListActivity{
	
	private PullToRefreshListView mListView;
	private TextView mCommentCount, mGoodPercent;
	private int photoAreaWidth;
	private PromptView mPromptView;
	private CommonRequest mRequest;
	private CommentCommodityListModel mComment;
	private String mCommodityId;
	private MyCommentAdapter mAdapter;
	private View mHeaderView;
	private MyRatingbar mRatingbar;

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		mCommodityId = getIntent().getStringExtra("commodityId");
	}

	@Override
	protected void findViews() {
		mListView = (PullToRefreshListView)findViewById(R.id.act_commodity_comment_listview);
//		buyBtn = findViewById(R.id.act_commodity_comment_buy);
		mPromptView = (PromptView)findViewById(R.id.promptView);
		mHeaderView = getLayoutInflater().inflate(R.layout.header_commodity_comment, mListView.getRefreshableView(), false);
		mCommentCount = (TextView)mHeaderView.findViewById(R.id.act_commodity_details_countComment);
		mGoodPercent = (TextView)mHeaderView.findViewById(R.id.act_commodity_details_hpl);
		mRatingbar = (MyRatingbar)mHeaderView.findViewById(R.id.act_commodity_details_ratingbar);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("商品评价");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mAdapter = new MyCommentAdapter();
		mListView.setMode(Mode.DISABLED);
		mListView.setAdapter(mAdapter);
		
		mListView.getRefreshableView().addHeaderView(mHeaderView);
		
		mListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				photoAreaWidth = mListView.getWidth() 
						- getResources().getDimensionPixelSize(R.dimen.order_photo) 
						- Utils.dp2px(getApplicationContext(), 40);
				mListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mListView.setAdapter(new MyCommentAdapter());
			}
		});
		
//		buyBtn.setOnClickListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_comment);
		super.onCreate(savedInstanceState);
		new QueryCommentList().execute();
	}
	
	class MyCommentAdapter extends BaseAdapter{

		int margin = 3;
		
		@Override
		public int getCount() {
			return null == mComment || null == mComment.detail ? 0 : mComment.detail.size();
		}

		@Override
		public CommentCommodiytModel getItem(int position) {
			return null == mComment || null == mComment.detail ? null : mComment.detail.get(position);
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
				convertView = getLayoutInflater().inflate(R.layout.listview_item_commodity_comment, null);
				vh.tempLine1 = convertView.findViewById(R.id.tempId3);
				vh.tempLine2 = convertView.findViewById(R.id.tempId4);
				vh.photoArea = (LinearLayout)convertView.findViewById(R.id.list_item_commodity_comment_photoArea);
				vh.ratingBar = (RatingBar)convertView.findViewById(R.id.list_item_commodity_comment_ratingbar);
				vh.nickname = (TextView)convertView.findViewById(R.id.list_item_commodity_comment_nickname);
				vh.content = (TextView)convertView.findViewById(R.id.list_item_commodity_comment_content);
				vh.time = (TextView)convertView.findViewById(R.id.list_item_commodity_comment_time);
				vh.img = (ImageView)convertView.findViewById(R.id.list_item_commodity_comment_img);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			
			CommentCommodiytModel model = getItem(position);
			
			if(null != model){
				ImageLoader.getInstance().displayImage(model.nickImage, vh.img, options);
				vh.nickname.setText(model.nickName);
				vh.content.setText(model.content);
				vh.time.setText(model.createTime);
				vh.ratingBar.setRating(Integer.valueOf(model.point));
				vh.photoArea.removeAllViews();
				int photoCount = null == model.imageList ? 0 : model.imageList.length;
				int width = (photoAreaWidth - 2*Utils.dp2px(getApplicationContext(), margin))/3;
				if(photoCount > 0){
					if(photoCount == 1){
						ImageView iv = new ImageView(getApplicationContext());
						LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						iv.setLayoutParams(lp);
						iv.setScaleType(ScaleType.CENTER_CROP);
						ImageLoader.getInstance().displayImage(model.imageList[0], iv, options);
						vh.photoArea.addView(iv);
					}else{
						LinearLayout horLinear = null;
						for(int i = 0;i < photoCount;i++){
							ImageView iv = new ImageView(getApplicationContext());
							LayoutParams lp = new LayoutParams(width, width);
							iv.setScaleType(ScaleType.CENTER_CROP);
							ImageLoader.getInstance().displayImage(model.imageList[0], iv, options);
							if(i % 3 == 0){
								horLinear = new LinearLayout(getApplicationContext());
								LayoutParams hlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
								if(i != 0)
									hlp.topMargin = Utils.dp2px(getApplicationContext(), margin);
								horLinear.setLayoutParams(hlp);
								horLinear.setOrientation(LinearLayout.HORIZONTAL);
								
							}else{
								lp.leftMargin = Utils.dp2px(getApplicationContext(), margin);
							}
							
							iv.setLayoutParams(lp);
							
							horLinear.addView(iv);
							
							if(i % 3 == 2 || i == photoCount - 1){
								vh.photoArea.addView(horLinear);
							}
						}
					}
				}
			}
			
			if(0 == position){
				setLineStatus(vh, true);
				convertView.setBackgroundResource(R.drawable.bg_top);
			}else if(position == getCount() - 1){
				setLineStatus(vh, false);
				convertView.setBackgroundResource(R.drawable.bg_bottom);
			}else{
				setLineStatus(vh, true);
				convertView.setBackgroundResource(R.drawable.bg_center);
			}
			return convertView;
		}
		
		void setLineStatus(ViewHolder vh, boolean isShow){
			if(isShow){
				vh.tempLine1.setVisibility(View.VISIBLE);
				vh.tempLine2.setVisibility(View.VISIBLE);
			}else{
				vh.tempLine1.setVisibility(View.GONE);
				vh.tempLine2.setVisibility(View.GONE);
			}
		}
		
		class ViewHolder{
			TextView nickname, time, content;
			RatingBar ratingBar;
			View tempLine1, tempLine2;
			LinearLayout photoArea;
			ImageView img;
		}
		
	}
	
	class QueryCommentList extends AsyncTask<String, Void, CommentCommodityListJson> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (start == 0 && !mListView.isRefreshing())
				mPromptView.showLoading();
		}

		@Override
		protected CommentCommodityListJson doInBackground(String... params) {
			CommentCommodityListJson eclj = null;
			String json = mRequest.queryCommodityEvaluate(mCommodityId);
			if (!TextUtils.isEmpty(json)) {
				eclj = JSON.parseObject(json, CommentCommodityListJson.class);
			}
			return eclj;
		}

		@Override
		protected void onPostExecute(CommentCommodityListJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			
			if (start == 0)
				mPromptView.showContent();
			if(mListView.isRefreshing())
				mListView.onRefreshComplete();
			
			if (null != result) {
				if (0 == result.code) {
					if (null != result.data) {
						mComment = result.data;
						if(null != mAdapter)
							mAdapter.notifyDataSetChanged();
						initSomthing();
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
	
	private void initSomthing(){
		if(null != mComment){
			mCommentCount.setText(Html.fromHtml("<font color='#ff0099'>" + mComment.evaluate + "</font>人  评价"));
			mGoodPercent.setText(Html.fromHtml("<font color='#ff0099'>" + mComment.good + "%</font> 好评"));
			mRatingbar.setRating(Integer.valueOf(mComment.start));
		}
	}
	
}
