package com.zhongjie.activity.user;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class SettingActivity extends BaseSecondActivity{
	
	private View mCleanCache;
	private TextView mTotalCache;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mCleanCache = findViewById(R.id.act_setting_clean_img_cache);
		mTotalCache = (TextView)findViewById(R.id.act_setting_total_cache);
	}
	
	private String getTotalCache(){
		ImageLoader loader = ImageLoader.getInstance();
		long size = loader.getDiscCache().getCacheDir().getTotalSpace();
		BigDecimal bd = new BigDecimal(BigDecimal.ROUND_HALF_DOWN);
		return new DecimalFormat().format(size);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("设置");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTotalCache.setText(text);
		mCleanCache.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ImageLoader.getInstance().clearDiscCache();
			}
		});
	}
	
}
