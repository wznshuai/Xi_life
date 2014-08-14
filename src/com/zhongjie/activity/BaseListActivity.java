package com.zhongjie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongjie.R;

public abstract class BaseListActivity extends BaseSecondActivity{
	
	public ListView mListView;
	/** 加载中提示 */
	public RelativeLayout mFooterViewLoading;
	/** 列表底部信息类型 */
	public enum FooterView {
		MORE, LOADING, NO_CONNECTION, HIDE_ALL, NO_DATA
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initViews() {
		mListView.addFooterView(mFooterViewLoading);
		showFooterView(FooterView.HIDE_ALL);
	}
	
	/**
	 * 显示列表底部信息
	 * 
	 * @param footerView
	 */
	public void showFooterView(FooterView footerView) {
		mListView.setFooterDividersEnabled(false);
		mFooterViewLoading.setVisibility(View.VISIBLE);
		if (footerView == FooterView.MORE) {
			mFooterViewLoading.setClickable(true);
			mFooterViewLoading.findViewById(R.id.property_loading)
					.setVisibility(View.VISIBLE);
			((TextView) mFooterViewLoading.findViewById(R.id.property_loading))
					.setText("点击载入更多...");
			mFooterViewLoading.findViewById(R.id.property_footer_loading)
					.setVisibility(View.GONE);
		} else if (footerView == FooterView.LOADING) {
			mFooterViewLoading.setClickable(false);
			mFooterViewLoading.findViewById(R.id.property_loading)
					.setVisibility(View.VISIBLE);
			mFooterViewLoading.findViewById(R.id.property_footer_loading)
					.setVisibility(View.VISIBLE);
			((TextView) mFooterViewLoading.findViewById(R.id.property_loading))
					.setText("努力加载中...");
		} else if (footerView == FooterView.NO_CONNECTION) {
			mFooterViewLoading.setClickable(true);
			mFooterViewLoading.findViewById(R.id.property_loading)
					.setVisibility(View.VISIBLE);
			mFooterViewLoading.findViewById(R.id.property_footer_loading)
					.setVisibility(View.GONE);
			((TextView) mFooterViewLoading.findViewById(R.id.property_loading))
					.setText("网络状态不佳,点击重新载入");
		} else if (footerView == FooterView.NO_DATA) {
			mFooterViewLoading.setClickable(true);
			mFooterViewLoading.findViewById(R.id.property_loading)
					.setVisibility(View.VISIBLE);
			mFooterViewLoading.findViewById(R.id.property_footer_loading)
					.setVisibility(View.GONE);
			((TextView) mFooterViewLoading.findViewById(R.id.property_loading))
					.setText("获取数据失败");
		} else if (footerView == FooterView.HIDE_ALL) {
			mFooterViewLoading.setClickable(true);
			mFooterViewLoading.findViewById(R.id.property_footer_loading)
					.setVisibility(View.GONE);
			mFooterViewLoading.findViewById(R.id.property_loading)
					.setVisibility(View.GONE);
			mFooterViewLoading.setVisibility(View.GONE);
		}
	}
}
