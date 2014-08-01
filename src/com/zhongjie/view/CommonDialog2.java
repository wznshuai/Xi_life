package com.zhongjie.view;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongjie.R;

public class CommonDialog2 extends Dialog {

	private Activity mContext;
	private ListView mListView;

	private List<String> mTitles;

	public void setmTitles(List<String> mTitles) {
		this.mTitles = mTitles;
		if (null != mAdapter)
			mAdapter.notifyDataSetChanged();

	}

	private MenuAdapter mAdapter;
	private OnItemClickListener mListener;

	public CommonDialog2(Activity context, List<String> titles) {
		super(context, R.style.shareDialog);
		this.mContext = context;
		mTitles = titles;

	}

	public void setTitle(int index, String title) {
		if (mTitles == null || index < 0 || index > mTitles.size()) {
			return;
		}
		mTitles.set(index, title);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_common);
		TextView titleTv = (TextView) findViewById(R.id.dialog_common_title);
		titleTv.setText("请选择需要的操作");
		mListView = (ListView) findViewById(R.id.dialog_common_list);

		mAdapter = new MenuAdapter();
		mListView.setAdapter(mAdapter);
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
		mListener = l;
	}

	@Override
	public void show() {
		super.show();
		mListView.setOnItemClickListener(mListener);
	}

	class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mTitles.size();

		}

		@Override
		public Object getItem(int position) {

			return mTitles.get(position);

		}

		@Override
		public long getItemId(int arg0) {

			return 0;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.group_item_view, null);
				holder = new ViewHolder();

				convertView.setTag(holder);
				holder.title = (TextView) convertView
						.findViewById(R.id.group_item);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setTextColor(Color.BLACK);
			holder.title.setText(mTitles.get(position));

			return convertView;

		}

	}

	class ViewHolder {
		ImageView icon;
		TextView title;
	}
}
