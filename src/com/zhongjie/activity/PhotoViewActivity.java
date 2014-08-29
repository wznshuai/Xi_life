package com.zhongjie.activity;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;

public class PhotoViewActivity extends Activity {

	public final static String BIGIMG_URL = "BIGIMG_URL";
	public final static String SMALLIMG_URL = "SMALLIMG_URL";

	private PhotoView mPhotoView;
	private String mBigUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photoview);
		initData();
		findViews();
		initViews();
	}

	protected void initData() {
		mBigUrl = getIntent().getStringExtra(BIGIMG_URL);
	}

	protected void findViews() {
		mPhotoView = (PhotoView) findViewById(R.id.act_photoview);
	}

	protected void initViews() {
		ImageLoader.getInstance().displayImage(mBigUrl, mPhotoView);
	}
}