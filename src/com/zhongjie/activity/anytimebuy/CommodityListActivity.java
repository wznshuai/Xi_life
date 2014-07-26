package com.zhongjie.activity.anytimebuy;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.util.Utils;

public class CommodityListActivity extends BaseSecondActivity {

	private ListView mListView;
	private volatile boolean isShowing = false;
	private WindowManager mWm;
	private View mFloatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_commodity_list);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void findViews() {
		mListView = (ListView) findViewById(R.id.act_commodity_list);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterImg.setImageResource(R.drawable.ic_logo_ssg);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mListView.setAdapter(new MyCommodityAdapter());
		createFloatView(this);
	}

	private void createFloatView(final Activity act) {
		if (isShowing)
			return;
		mRemoveFloatView();
		isShowing = true;
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		mFloatView = LayoutInflater.from(act).inflate(
				R.layout.float_view_shoppingcar, null);
		lp.packageName = act.getPackageName();
		lp.width = Utils.dp2px(getApplicationContext(), 60);
		lp.height = Utils.dp2px(getApplicationContext(), 60);
		lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		lp.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		lp.y = Utils.dp2px(act.getApplicationContext(), 100);
		lp.x = Utils.dp2px(act.getApplicationContext(), 20);
		lp.format = PixelFormat.RGBA_8888;
		mWm = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
		lp.type = LayoutParams.TYPE_APPLICATION;
		mFloatView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		mWm.addView(mFloatView, lp);
	}

	private void mRemoveFloatView() {
		if (!isShowing)
			return;
		isShowing = false;
		if (null != mWm && null != mFloatView) {
			mWm.removeView(mFloatView);
		}
		mWm = null;
		mFloatView = null;
	}

	class MyCommodityAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView)
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item_commodity, null);
			return convertView;
		}
	}
	
	@Override
	public void finish() {
		mRemoveFloatView();
		super.finish();
	}
}
