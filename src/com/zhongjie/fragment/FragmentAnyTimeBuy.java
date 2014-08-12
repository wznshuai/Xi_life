package com.zhongjie.fragment;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.BaseFragment;
import com.zhongjie.R;
import com.zhongjie.activity.anytimebuy.CommodityListActivity;
import com.zhongjie.model.EshopCatelogListJson;
import com.zhongjie.model.EshopCatelogModel;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.view.PromptView;

public class FragmentAnyTimeBuy extends BaseFragment{
	
	private static FragmentAnyTimeBuy mInstance;
	
	private ListView mListView;
	private CommonRequest mRequest;
	private PromptView mPromptView;
	private List<EshopCatelogModel> mEshopCatelogList;
	
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
		initData();
		findViews();
		initViews();
		new QueryEshopCatelog().execute();
	}
	
	@Override
	protected void initData() {
		super.initData();
		mRequest = new CommonRequest(getActivity().getApplicationContext());
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		mListView = (ListView)findViewById(R.id.fra_anytimebuy_listview);
		mPromptView = (PromptView)findViewById(R.id.promptView);
	}
	
	@Override
	protected void initViews() {
		super.initViews();
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
			return null == mEshopCatelogList ? 0 : mEshopCatelogList.size();
		}

		@Override
		public EshopCatelogModel getItem(int position) {
			return null == mEshopCatelogList ? null : mEshopCatelogList.get(position);
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
			EshopCatelogModel eShop = getItem(position);
			ImageView img = (ImageView)convertView.findViewById(R.id.list_item_anytimebuy_img);
			ImageLoader.getInstance().displayImage(eShop.catalogImage, img);
			return convertView;
		}
	}
	
	class QueryEshopCatelog extends AsyncTask<String, Void, EshopCatelogListJson>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPromptView.showLoading();
		}
		
		@Override
		protected EshopCatelogListJson doInBackground(String... params) {
			EshopCatelogListJson eclj = null;
			String json = mRequest.queryEshopCatelog();
			if(!TextUtils.isEmpty(json)){
				eclj = JSON.parseObject(json, EshopCatelogListJson.class);
			}
			return eclj;
		}
		
		@Override
		protected void onPostExecute(EshopCatelogListJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			mPromptView.showContent();
			if(null != result){
				if(0 == result.code){
					if(null != result.data && result.data.size() > 0){
						mEshopCatelogList = result.data;
						mListView.setAdapter(new MyAnytimeBuyAdapter());
					}else{
						mPromptView.showEmpty();
					}
				}else{
					showToast(result.errMsg);
					mPromptView.showError();
				}
			}else{
				mPromptView.showError();
			}
		}
	}
}	
