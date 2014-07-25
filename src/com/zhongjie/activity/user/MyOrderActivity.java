package com.zhongjie.activity.user;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class MyOrderActivity extends BaseSecondActivity{
	
	
	private ListView mListView;
	
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mListView = (ListView)findViewById(R.id.act_order_listview);
	}

	@Override
	protected void initViews() {
		mListView.setAdapter(new OrderAdapter());	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order);
		super.onCreate(savedInstanceState);
	}
	
	class OrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null == convertView)
				convertView = getLayoutInflater().inflate(R.layout.listview_item_myorder, null);
			return convertView;
		}
		
	}
	
}
