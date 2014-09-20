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
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zhongjie.MainActivity;
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
	private View goCommentView, mIcJianView, mIcJiaView, mAddInCartView, mBuyView;
	private CommonRequest mRequest;
	private CommodityModel mDetails;
	private EditText mCountEdittext;
	private ShopCartManager mCartManager;
	private String mCommodityId;
	private RadioGroup mTasteGroup;
	private TextView mCommodityIntroduce;
	private LinearLayout mCommodityIntroduceArea;
	private String mSelectedTaste;
	private Object lock = new Object();
	private int mPagerHeight;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_details);
		super.onCreate(savedInstanceState);
		new QueryCommodityDetails().execute();
	}
	
	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		mCommodityId = getIntent().getStringExtra("commodityId");
		mCartManager = ShopCartManager.getInstance();
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
		mIcJiaView = findViewById(R.id.act_commodity_details_icJia);
		mIcJianView = findViewById(R.id.act_commodity_details_icJian);
		mAddInCartView = findViewById(R.id.act_commodity_details_addInCart);
		mBuyView = findViewById(R.id.act_commodity_details_buy);
		mTasteGroup = (RadioGroup)findViewById(R.id.act_commodity_details_taste);
		mCommodityIntroduce = (TextView)findViewById(R.id.act_commodity_details_introduce);
		mCommodityIntroduceArea = (LinearLayout)findViewById(R.id.act_commodity_details_introduce_area);
	}

	@Override
	protected void initViews() {
		mAddInCartView.setOnClickListener(this);
		mBuyView.setOnClickListener(this);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		goCommentView.setOnClickListener(this);
		mIcJianView.setOnClickListener(this);
		mIcJiaView.setOnClickListener(this);
		mCountEdittext.setText(1 + "");
		mCountEdittext.setSelection(mCountEdittext.getText().toString().length());
		mCountEdittext.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(count == 1 && s.toString().equals("0")){
					mCountEdittext.setText("1");
					mCountEdittext.setSelection(mCountEdittext.getText().toString().length());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		mPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				mPagerHeight = mPager.getWidth();
				mPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
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
			
			if(null != cdm.taste && cdm.taste.length > 0){
				mTasteGroup.setVisibility(View.VISIBLE);
				int i = 0;
				for(String str : cdm.taste){
					RadioButton rb = (RadioButton)getLayoutInflater()
							.inflate(R.layout.radiobutton_commodity_details, mTasteGroup, false);
					rb.setText(str);
					rb.setId(i);
					rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if(isChecked)
								mSelectedTaste = buttonView.getText().toString(); 
						}
					});
					mTasteGroup.addView(rb);
					i++;
				}
				mTasteGroup.check(0);
			}
			
			mCommodityIntroduce.setText(cdm.detail);
			if(null != cdm.detailImage && cdm.detailImage.length > 0){
				DisplayImageOptions op = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).displayer(new FadeInBitmapDisplayer(300))
				.showImageForEmptyUri(R.drawable.ic_default_head)
				.showImageOnFail(R.drawable.ic_default_head)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
				for(String url : cdm.detailImage){
					ImageView iv = (ImageView)getLayoutInflater().inflate(R.layout.imageview_commodity_details, mCommodityIntroduceArea, false);
					ImageLoader.getInstance().displayImage(url, iv, op);
					mCommodityIntroduceArea.addView(iv);
				}
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
			if(mDetails.evaluate > 0){
				Intent intent = new Intent(CommodityDetailsActivity.this, CommodityCommentActivity.class);
				intent.putExtra("commodityId", mDetails.commodityId);
				startActivity(intent);
			}else{
				showToast("暂无人评价");
			}
			break;
		case R.id.act_commodity_details_addInCart:
			String countStr = mCountEdittext.getText().toString();
			if(!Utils.isEmpty(countStr)){
				try{
					int count = Integer.valueOf(countStr);
					mDetails.selectedTaste = mSelectedTaste;
					mCartManager.addInShopCart(mDetails, count, false);
					showToast("商品已放入购物车，可进入购物车内查看并结算");
				}catch(Exception e){
					showToast("输入数量不正确");
					Logger.e(TAG, "", e);
				}
			}
			break;
		case R.id.act_commodity_details_buy:
			countStr = mCountEdittext.getText().toString();
			if(!Utils.isEmpty(countStr)){
				try{
					int count = Integer.valueOf(countStr);
					mDetails.selectedTaste = mSelectedTaste;
					mCartManager.addInShopCart(mDetails, count, false);
				}catch(Exception e){
					showToast("输入数量不正确");
					Logger.e(TAG, "", e);
				}
			}
			goHomeActivity(MainActivity.TAB_3);
			break;
		case R.id.act_commodity_details_icJia:
			String countStr2 = mCountEdittext.getText().toString();
			if(!Utils.isEmpty(countStr2)){
				try{
					int count = Integer.valueOf(countStr2);
					mCountEdittext.setText(++count + "");
					mCountEdittext.setSelection(mCountEdittext.getText().toString().length());
				}catch(Exception e){
					showToast("输入数量不正确");
					Logger.e(TAG, "", e);
				}
			}
			break;
		case R.id.act_commodity_details_icJian:
			String countStr3 = mCountEdittext.getText().toString();
			if(!Utils.isEmpty(countStr3)){
				try{
					int count = Integer.valueOf(countStr3);
					--count;
					if(count == 0)
						count = 1;
					mCountEdittext.setText(count + "");
					mCountEdittext.setSelection(mCountEdittext.getText().toString().length());
				}catch(Exception e){
					showToast("输入数量不正确");
					Logger.e(TAG, "", e);
				}
			}
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
				synchronized (lock) {
					if(mPagerHeight == 0){
						lock.wait();
					}
				}
			} catch (Exception e) {
				Logger.e(TAG, "QueryCommodityDetails error", e);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(CommodityJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			mPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mPagerHeight));
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
