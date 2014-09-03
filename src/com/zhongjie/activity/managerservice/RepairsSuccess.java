package com.zhongjie.activity.managerservice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.global.Session;
import com.zhongjie.model.RepairShowModel;
import com.zhongjie.util.Utils;

public class RepairsSuccess extends BaseSecondActivity{
	
	private RepairShowModel mRepairModel;
	private TextView mAddress, mDate;
	private ImageView mImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_repairs_success);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		mRepairModel = (RepairShowModel)Session.getSession().remove("repairModel");
	}

	@Override
	protected void findViews() {
		mImg = (ImageView)findViewById(R.id.act_repairs_success_img);
		mAddress = (TextView)findViewById(R.id.act_repairs_success_address);
		mDate = (TextView)findViewById(R.id.act_repairs_success_date);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo_repair);
		mTopCenterImg.setVisibility(View.VISIBLE);
		if(Utils.isEmpty(mRepairModel.image)){
			mImg.setVisibility(View.GONE);
		}else{
			ImageLoader.getInstance().displayImage(mRepairModel.image, mImg, options);
		}
		mAddress.append(mRepairModel.unit + "栋" + mRepairModel.room + "室");
		mDate.append(mRepairModel.needRepairDate);
	}
	
}
