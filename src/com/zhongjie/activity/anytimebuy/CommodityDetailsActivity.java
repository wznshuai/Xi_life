package com.zhongjie.activity.anytimebuy;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.fragment.FragmentBigImg;
import com.zhongjie.model.CommodityJson;
import com.zhongjie.model.CommodityModel;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.util.ShopCartManager;
import com.zhongjie.util.Utils;
import com.zhongjie.view.CommonLoadingDialog;
import com.zhongjie.view.MyRatingbar;
import com.zhongjie.view.MyViewPager;
import com.zhongjie.view.SlideRightOutView;
import com.zhongjie.view.viewpagerindicator.CirclePageIndicator;

public class CommodityDetailsActivity extends BaseSecondActivity implements OnClickListener{
	
	private MyViewPager mPager;
	private CirclePageIndicator mIndicator;
	private MyRatingbar mRatingbar;
	private TextView mCommentCount, mGoodPercent, 
				mCommodityNameTxt, mCommodityWeight, mCommodityPrice, mCommodityOldPrice;
	private View goCommentView, mIcJian, mIcJia;
	private int mCommodityId, mCountInShopCart;
	private String mCommodityName;
	private CommonRequest mRequest;
	private CommodityModel mDetails;
	private EditText mCountEdittext;
	private ShopCartManager mCartManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_details);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		mCommodityId = getIntent().getIntExtra("commodityId", -1);
		mCommodityName = getIntent().getStringExtra("commodityName");
		mCartManager = ShopCartManager.getInstance();
		mCountInShopCart = mCartManager.getCommodityCount(mCommodityId);
	}

	@Override
	protected void findViews() {
		mPager = (MyViewPager)findViewById(R.id.act_commodity_details_viewpager);
		mIndicator = (CirclePageIndicator)findViewById(R.id.act_commodity_indicator_dot);
		mRatingbar = (MyRatingbar)findViewById(R.id.act_commodity_details_ratingbar);
		mCommentCount = (TextView)findViewById(R.id.act_commodity_details_countComment);
		mGoodPercent = (TextView)findViewById(R.id.act_commodity_details_hpl);
		goCommentView = findViewById(R.id.act_commodity_details_goComment);
		mCommodityNameTxt = (TextView)findViewById(R.id.act_commodity_details_commodityName);
		mCommodityWeight = (TextView)findViewById(R.id.act_commodity_details_weight);
		mCommodityPrice = (TextView)findViewById(R.id.act_commodity_price);
		mCommodityOldPrice = (TextView)findViewById(R.id.act_commodity_oldPrice);
		mCountEdittext = (EditText)findViewById(R.id.act_commodity_details_commodityCount);
		mIcJia = findViewById(R.id.act_commodity_details_icJia);
		mIcJian = findViewById(R.id.act_commodity_details_icJian);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		goCommentView.setOnClickListener(this);
		mCountEdittext.setText(mCountInShopCart);
	}
	
	
	private void initInfos(CommodityModel cdm){
		if(null != cdm){
			mDetails = cdm;
			List<String> detailImgList = new ArrayList<String>();
			if(!Utils.isEmpty(cdm.imageA)){
				detailImgList.add(cdm.imageA);
			}
			if(!Utils.isEmpty(cdm.imageB)){
				detailImgList.add(cdm.imageB);
			}
			if(!Utils.isEmpty(cdm.imageC)){
				detailImgList.add(cdm.imageC);
			}
			
			if(detailImgList.size() > 0){
				mPager.setSrov((SlideRightOutView)findViewById(R.string.slide_view));
				mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), detailImgList));
				mIndicator.setViewPager(mPager);
			}
			
			mCommodityNameTxt.setText(cdm.name);
			mCommodityOldPrice.setText(cdm.oldPrice);
			mCommodityPrice.setText(cdm.price);
			mCommodityWeight.setText(cdm.weight);
			mCommentCount.setText(Html.fromHtml("<font color='#ff0099'>"+ cdm.evaluate +"</font>人  评价"));
			mGoodPercent.setText(Html.fromHtml("<font color='#ff0099'>" + cdm.good + "%</font> 好评"));
			mRatingbar.setRating(cdm.good);
			
			
			cdm = null;
		}
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
		List<String> detailImgList;
		public MyPagerAdapter(FragmentManager fm, List<String> detailImgList) {
			super(fm);
			this.detailImgList = detailImgList;
		}

		@Override
		public Fragment getItem(int arg0) {
			return new FragmentBigImg(detailImgList.get(arg0));
		}

		@Override
		public int getCount() {
			return null == detailImgList ? 0 : detailImgList.size();
		}
	}
	
	class QueryCommodityDetails extends AsyncTask<String, Void, CommodityJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(CommodityDetailsActivity.this);
			cld.setCanceledOnTouchOutside(false);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}

		@Override
		protected CommodityJson doInBackground(String... params) {
			CommodityJson uj = null;
			try {
				String result = mRequest.queryCommodityDetails(mCommodityId);
				if(!TextUtils.isEmpty(result)){
					uj = JSON.parseObject(result, CommodityJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "QueryCommodityDetails error", e);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(CommodityJson result) {
			super.onPostExecute(result);
			if(!canGOON())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(result.code == 0){
					if(null != result.data){
						initInfos(result.data);
					}
				}else{
					showToast(result.errMsg);
				}
			}
		}
	}
	
}
