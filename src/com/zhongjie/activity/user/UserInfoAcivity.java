package com.zhongjie.activity.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;

public class UserInfoAcivity extends BaseSecondActivity implements OnClickListener{
	
	private ImageView mHead;
	private View mHeadEdit, mConfirmView;
	private TextView mNickname, mAddress1, mAddress2;
	private EditText mNicknameEdit, mAddress1Edit, mAddress2Edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_userinfo);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initData() {
		
	}

	@Override
	protected void findViews() {
		mHead = (ImageView)findViewById(R.id.act_userinfo_head);
		mHeadEdit = findViewById(R.id.act_userinfo_edit);
		mNickname = (TextView)findViewById(R.id.act_userinfo_nickname);
		mNicknameEdit = (EditText)findViewById(R.id.act_userinfo_editnickname);
		mAddress1 = (TextView)findViewById(R.id.act_userinfo_address1);
		mAddress1Edit = (EditText)findViewById(R.id.act_userinfo_editaddress1);
		mAddress2 = (TextView)findViewById(R.id.act_userinfo_address2);
		mAddress2Edit = (EditText)findViewById(R.id.act_userinfo_editaddress2);
		mConfirmView = findViewById(R.id.act_userinfo_confirmLL);
	}

	@Override
	protected void initViews() {
		ImageLoader.getInstance().displayImage("", mHead, 
				new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_default_head)
				.showImageOnFail(R.drawable.ic_default_head).build());
		mTopCenterTxt.setText("个人资料");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopRightTxt.setText("修改");
		mTopRightTxt.setVisibility(View.VISIBLE);
		mTopRightTxt.setOnClickListener(this);
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
	}
	
	private void changeEditStatus(boolean isEdit){
		if(isEdit){
			mHeadEdit.setVisibility(View.VISIBLE);
			mNickname.setVisibility(View.GONE);
			mNicknameEdit.setVisibility(View.VISIBLE);
			mAddress1.setVisibility(View.GONE);
			mAddress1Edit.setVisibility(View.VISIBLE);
			mAddress2.setVisibility(View.GONE);
			mAddress2Edit.setVisibility(View.VISIBLE);
			mConfirmView.setVisibility(View.VISIBLE);
		}else{
			mHeadEdit.setVisibility(View.GONE);
			mNickname.setVisibility(View.VISIBLE);
			mNicknameEdit.setVisibility(View.GONE);
			mAddress1.setVisibility(View.VISIBLE);
			mAddress1Edit.setVisibility(View.GONE);
			mAddress2.setVisibility(View.VISIBLE);
			mAddress2Edit.setVisibility(View.GONE);
			mConfirmView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_rightTxt:
			if(mTopRightTxt.getText().equals("修改")){
				mTopRightTxt.setText("取消");
				changeEditStatus(true);
			}else{
				mTopRightTxt.setText("修改");
				changeEditStatus(false);
			}
			break;

		default:
			break;
		}
	}
	
	
}
