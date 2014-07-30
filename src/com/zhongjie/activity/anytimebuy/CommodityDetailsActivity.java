package com.zhongjie.activity.anytimebuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.fragment.FragmentBigImg;
import com.zhongjie.view.MyRatingbar;
import com.zhongjie.view.MyViewPager;
import com.zhongjie.view.SlideRightOutView;
import com.zhongjie.view.viewpagerindicator.CirclePageIndicator;

public class CommodityDetailsActivity extends BaseSecondActivity implements OnClickListener{
	
	private MyViewPager mPager;
	private CirclePageIndicator mIndicator;
	private MyRatingbar mRatingbar;
	private TextView mCommentCount, mGoodPercent;
	private View goCommentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_details);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mPager = (MyViewPager)findViewById(R.id.act_commodity_details_viewpager);
		mIndicator = (CirclePageIndicator)findViewById(R.id.act_commodity_indicator_dot);
		mRatingbar = (MyRatingbar)findViewById(R.id.act_commodity_details_ratingbar);
		mCommentCount = (TextView)findViewById(R.id.act_commodity_details_countComment);
		mGoodPercent = (TextView)findViewById(R.id.act_commodity_details_hpl);
		goCommentView = findViewById(R.id.act_commodity_details_goComment);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mPager.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
		mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mIndicator.setViewPager(mPager);
		mRatingbar.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
		mCommentCount.setText(Html.fromHtml("<font color='#ff0099'>110</font>人  评价"));
		mGoodPercent.setText(Html.fromHtml("<font color='#ff0099'>99%</font> 好评"));
		goCommentView.setOnClickListener(this);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_commodity_details_goComment:
			startActivity(new Intent(CommodityDetailsActivity.this, CommodityCommentActivity.class));
			break;

		default:
			break;
		}
	}



	class MyPagerAdapter extends FragmentStatePagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return new FragmentBigImg();
		}

		@Override
		public int getCount() {
			return 5;
		}
	}
	
}
