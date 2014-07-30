package com.zhongjie.activity.anytimebuy;

import java.util.Random;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.util.Utils;
import com.zhongjie.view.FixedGridLayout;

public class CommodityCommentActivity extends BaseSecondActivity{
	
	private ListView mListView;
	private TextView mCommentCount, mGoodPercent;
	private View mPhotoArea;
	private int photoAreaWidth;

	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mListView = (ListView)findViewById(R.id.act_commodity_comment_listview);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("商品评价");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		
		mListView.addHeaderView(getLayoutInflater().inflate(R.layout.header_commodity_comment, null));
		
		mListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				photoAreaWidth = mListView.getWidth() 
						- getResources().getDimensionPixelSize(R.dimen.order_photo) 
						- Utils.dp2px(getApplicationContext(), 40);
				System.out.println("photoAreaWidth : " + photoAreaWidth);
				mListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mListView.setAdapter(new MyCommentAdapter());
			}
		});
		
		mCommentCount = (TextView)mListView.findViewById(R.id.act_commodity_details_countComment);
		mGoodPercent = (TextView)mListView.findViewById(R.id.act_commodity_details_hpl);
		mCommentCount.setText(Html.fromHtml("<font color='#ff0099'>110</font>人  评价"));
		mGoodPercent.setText(Html.fromHtml("<font color='#ff0099'>99%</font> 好评"));
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_comment);
		super.onCreate(savedInstanceState);
	}
	
	class MyCommentAdapter extends BaseAdapter{

		int margin = 3;
		
		@Override
		public int getCount() {
			return 16;
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
			
			if(null == convertView){
				vh = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.listview_item_commodity_comment, null);
				vh.tempLine1 = convertView.findViewById(R.id.tempId3);
				vh.tempLine2 = convertView.findViewById(R.id.tempId4);
				vh.photoArea = (LinearLayout)convertView.findViewById(R.id.list_item_commodity_comment_photoArea);
				vh.ratingBar = (RatingBar)convertView.findViewById(R.id.list_item_commodity_comment_ratingbar);
				vh.nickname = (TextView)convertView.findViewById(R.id.list_item_commodity_comment_nickname);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
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
			Random random = new Random();
			vh.ratingBar.setRating(random.nextInt(5));

			vh.photoArea.removeAllViews();
			int photoCount = random.nextInt(9);
			int width = (photoAreaWidth - 2*Utils.dp2px(getApplicationContext(), margin))/3;
			vh.nickname.setText(photoCount + "");
			if(photoCount > 0){
				if(photoCount == 1){
					ImageView iv = new ImageView(getApplicationContext());
					LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					iv.setLayoutParams(lp);
					iv.setScaleType(ScaleType.CENTER_CROP);
					iv.setImageResource(R.drawable.temp_ic_fantuan);
					vh.photoArea.addView(iv);
				}else{
					LinearLayout horLinear = null;
					for(int i = 0;i < photoCount;i++){
						ImageView iv = new ImageView(getApplicationContext());
						LayoutParams lp = new LayoutParams(width, width);
						iv.setScaleType(ScaleType.CENTER_CROP);
						iv.setImageResource(R.drawable.temp_ic_fantuan);
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
			TextView nickname;
			RatingBar ratingBar;
			View tempLine1, tempLine2;
			LinearLayout photoArea;
		}
		
	}
}
