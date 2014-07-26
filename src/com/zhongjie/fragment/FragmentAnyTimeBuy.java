package com.zhongjie.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.zhongjie.BaseFragment;
import com.zhongjie.R;
import com.zhongjie.activity.anytimebuy.CommodityListActivity;

public class FragmentAnyTimeBuy extends BaseFragment{
	
	private static FragmentAnyTimeBuy mInstance;
	
	private ListView mListView;
	
	public static FragmentAnyTimeBuy newInstance(){
		if(null == mInstance)
			mInstance = new FragmentAnyTimeBuy();
		return mInstance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_anytimebuy, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViews();
		initViews();
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		mListView = (ListView)findViewById(R.id.fra_anytimebuy_listview);
	}
	
	@Override
	protected void initViews() {
		super.initViews();
		mListView.setAdapter(new MyAnytimeBuyAdapter());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(new Intent(getActivity(), CommodityListActivity.class));
			}
			
		});
	}
	
	class MyAnytimeBuyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return 4;
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
			if(null == convertView){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_item_anytimebuy, null);
			}
			ImageView img = (ImageView)convertView.findViewById(R.id.list_item_anytimebuy_img);
			switch (position) {
			case 0:
				img.setImageResource(R.drawable.fantuan);
				break;
			case 1:
				img.setImageResource(R.drawable.zhoubaozi);
				break;
			case 2:
				img.setImageResource(R.drawable.shuiguo);
				break;
			default:
				img.setImageResource(R.drawable.xiaoshi);
				break;
			}
			return convertView;
		}
		
	}
}	
