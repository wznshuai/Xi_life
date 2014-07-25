package com.zhongjie.activity.user;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class MyRepairsActivity extends BaseSecondActivity{
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_myrepairs);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mListView = (ListView)findViewById(R.id.act_myrepairs_listview);
	}

	@Override
	protected void initViews() {
		mTopCenterTxt.setText("我的报修");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mListView.setAdapter(new MyRepairsAdapter());
	}
	
	class MyRepairsAdapter extends BaseAdapter{

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
			ViewHolder vh;
			if(null == convertView){
				convertView = getLayoutInflater().inflate(R.layout.listview_item_myrepairs, null);
				vh = new ViewHolder();
				vh.repairsType = (TextView)convertView.findViewById(R.id.list_item_myrepairs);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			
			vh.repairsType.setText(Html.fromHtml("报修分类  : <font color='red'>水管爆裂</font>"));
			
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		TextView repairsType;
	}
}
