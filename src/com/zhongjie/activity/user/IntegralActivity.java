package com.zhongjie.activity.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class IntegralActivity extends BaseSecondActivity{
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_integral);
		super.onCreate(savedInstanceState);
	}
	

	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mListView = (ListView)findViewById(R.id.act_integral_listview);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mListView.setAdapter(new MyIntegralAdapter());
	}
	
	class MyIntegralAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return 20;
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
			ViewHolder vh;
			if(null == convertView){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(IntegralActivity.this).inflate(R.layout.listview_item_integral, null);
				vh.circularView = (ImageView)convertView.findViewById(R.id.list_item_integral_circular);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			if(position == 0){
				vh.circularView.setImageResource(R.drawable.circular_juhuang);
			}else if(position == 1){
				vh.circularView.setImageResource(R.drawable.circular_qianlv);
			}else{
				vh.circularView.setImageResource(R.drawable.circular_blue);
			}
			return convertView;
		}
		
		
		class ViewHolder{
			ImageView circularView;
		}
	}
	
}
