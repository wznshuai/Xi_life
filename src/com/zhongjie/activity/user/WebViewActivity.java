package com.zhongjie.activity.user;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class WebViewActivity extends BaseSecondActivity{

	private WebView mWebView;
	private String url;
	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_webview);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
	}

	@Override
	protected void findViews() {
		mWebView = (WebView)findViewById(R.id.act_webview);
	}

	@Override
	protected void initViews() {
		mWebView.loadUrl(url);
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText(title);
	}
	
}
