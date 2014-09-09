package com.zhongjie.activity.user;

import java.io.File;
import java.text.DecimalFormat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.view.CommonLoadingDialog;

public class SettingActivity extends BaseSecondActivity {

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
		mTotalCache = (TextView) findViewById(R.id.act_setting_total_cache);
	}

	private String getTotalCache() {
		ImageLoader loader = ImageLoader.getInstance();
		File f = loader.getDiscCache().getCacheDir();
		long size = 0;
		if (f.exists() && f.isDirectory()) {
			for (File f2 : f.listFiles()) {
				if (f2.isFile())
					size += f2.length();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(size / 1024 / 1024);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("设置");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTotalCache.setText(getTotalCache() + "M");
		mCleanCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Void>() {
					CommonLoadingDialog cld = null;
					protected void onPreExecute() {
						cld = CommonLoadingDialog.create(SettingActivity.this);
						cld.show();
					};

					@Override
					protected Void doInBackground(Void... params) {
						ImageLoader.getInstance().clearDiscCache();
						return null;
					}
					
					protected void onPostExecute(Void result) {
						if(null != cld)
							cld.cancel();
						cld = null;
						mTotalCache.setText(getTotalCache() + "M");
					};
					
				}.execute();
				
			}
		});
	}

}
