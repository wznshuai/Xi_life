package com.zhongjie.activity.shoppingcar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.shoppingcar.FillOrderActivity.MyAdapter.ViewHolder;
import com.zhongjie.util.Utils;
import com.zhongjie.view.CommonDialog;
import com.zhongjie.view.PromptView;

public class FillOrderActivity extends BaseSecondActivity implements OnClickListener{
	
	private LinearLayout mCommodityArea;
	private View mPullView, mSelectTypeView, mZTArea, mPSArea, mSubmit;
	private ImageView mHandleView;
	private TextView mHandleTxt, mDispatchingType;
	private PromptView mPromptView;
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_fill_order);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mCommodityArea = (LinearLayout)findViewById(R.id.act_fill_order_commodity_area);
		mPullView = findViewById(R.id.act_fill_order_pull_area);
		mHandleView = (ImageView)findViewById(R.id.act_fill_order_handle);
		mHandleTxt = (TextView)findViewById(R.id.act_fill_order_handle_txt);
		mSelectTypeView = findViewById(R.id.act_fill_order_selectType);
		mDispatchingType = (TextView)findViewById(R.id.act_fill_order_dispatching_type);
		mZTArea = findViewById(R.id.act_fill_order_dispatching_zt);
		mPSArea = findViewById(R.id.act_fill_order_dispatching_ps);
		mPromptView = (PromptView)findViewById(R.id.promptView);
		mListView = (ListView)findViewById(R.id.act_fill_order_listview);
		mSubmit = findViewById(R.id.act_fill_order_submit_order);
	}

	@Override
	protected void initViews() {
		mPromptView.showLoading();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPromptView.showContent();
				mListView.setAdapter(new MyAdapter());
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ViewHolder vh = (ViewHolder)view.getTag();
						vh.radioBtn.setChecked(true);
						((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
					}
					
				});
			}
		}, 1000);
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo);
		mTopCenterImg.setVisibility(View.VISIBLE);
		mCommodityArea.addView(getLayoutInflater().inflate(R.layout.inculde_fill_order_commodity, null));
		mPullView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCommodityArea.getChildCount() == 1){
					for(int i = 0;i < 5;i++){
						View  mCommodityView = getLayoutInflater().inflate(R.layout.inculde_fill_order_commodity, null);
						mCommodityArea = (LinearLayout)findViewById(R.id.act_fill_order_commodity_area);
						LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						lp.topMargin = Utils.dp2px(getApplicationContext(), 5);
						mCommodityView.setLayoutParams(lp);
						mCommodityArea.addView(mCommodityView);
					}	
					mHandleView.setImageResource(R.drawable.ic_push);
					mHandleTxt.setText("点击收起");
				}else{
					mCommodityArea.removeViews(1, mCommodityArea.getChildCount() - 1);
					mHandleView.setImageResource(R.drawable.ic_pull);
					mHandleTxt.setText("共#件商品，点击展开");
				}
			}
		});
		
		mSelectTypeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final CommonDialog cd = CommonDialog.creatDialog(FillOrderActivity.this);
				cd.setTitle("请选择配送方式");
				cd.setContentView(R.layout.dialog_select_dispatching_type);
				cd.findViewById(R.id.dialog_select_dispatching_type_ps).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setDispatchingForPS();
						cd.dismiss();
					}
				});
				cd.findViewById(R.id.dialog_select_dispatching_type_zt).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setDispatchingForZT();
						cd.dismiss();
					}
				});
			}
		});
		mSubmit.setOnClickListener(this);
	}
	
	
	class MyAdapter extends BaseAdapter{
		
		int checkedPos = -1;
		
		@Override
		public int getCount() {
			return 3;
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
				convertView = getLayoutInflater().inflate(R.layout.listview_item_select_zt, parent, false);
				vh.radioBtn = (RadioButton)convertView.findViewById(R.id.list_item_select_zt_radiobtn);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			vh.radioBtn.setTag(position);
			if(checkedPos == position)
				vh.radioBtn.setChecked(true);
			else
				vh.radioBtn.setChecked(false);
			vh.radioBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
						checkedPos = (Integer)buttonView.getTag();
				}
			});
			
			return convertView;
		}
		
		class ViewHolder{
			RadioButton radioBtn;
		}
		
	}
	
	private void setDispatchingForPS(){
		mDispatchingType.setText("配送");
		mZTArea.setVisibility(View.GONE);
		mPSArea.setVisibility(View.VISIBLE);
	}
	
	private void setDispatchingForZT(){
		mDispatchingType.setText("自提");
		mZTArea.setVisibility(View.VISIBLE);
		mPSArea.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_fill_order_submit_order:
			Intent intent = new Intent(FillOrderActivity.this, SubmitOrderSuccess.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	
}
