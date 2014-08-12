package com.zhongjie.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.user.IntegralActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.activity.user.MyOrderActivity;
import com.zhongjie.activity.user.MyRepairsActivity;
import com.zhongjie.activity.user.SettingActivity;
import com.zhongjie.activity.user.UserInfoAcivity;
import com.zhongjie.model.UserJson;
import com.zhongjie.model.UserModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.SharedPreferencesUtil;
import com.zhongjie.view.CommonLoadingDialog;

public class FragmentUserCenter extends BaseFragment{
	
	private static FragmentUserCenter mInstance;
	
	private ImageView mHeadImg;
	private View mEditView, goIntegralView, goSettingView, goMyRepairsView, goMyOrderView, mLogoutView;
	private static final int REQUEST_CODE = 0x001; 
	
	private TextView mTopRightView, mAddress, mIntergal, mNickname;
	private CommonRequest mRequest;
	private UserModelManager mUserManager;
	
	public static FragmentUserCenter newInstance(){
		if(null == mInstance)
			mInstance = new FragmentUserCenter();
		return mInstance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usercenter, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		findViews();
		initViews();
	}
	
	@Override
	protected void initData() {
		super.initData();
		mRequest = new CommonRequest(getActivity().getApplicationContext());
		mUserManager = UserModelManager.getInstance();
	}
	
	public void findViews(){
		mAddress = (TextView)findViewById(R.id.fra_usercenter_address);
		mNickname = (TextView)findViewById(R.id.fra_usercenter_nickname);
		mIntergal = (TextView)findViewById(R.id.fra_usercenter_integral);
		mTopRightView = (TextView)getActivity().findViewById(R.id.topbar_rightTxt);
		mHeadImg = (ImageView)findViewById(R.id.fra_usercenter_head);
		mEditView = findViewById(R.id.fra_usercenter_edit);
		goIntegralView = findViewById(R.id.fra_usercenter_goIntegral);
		goSettingView = findViewById(R.id.fra_usercenter_goSetting);
		goMyRepairsView = findViewById(R.id.fra_usercenter_goMyrepairs);
		goMyOrderView = findViewById(R.id.fra_usercenter_goMyOrder);
		mLogoutView = findViewById(R.id.fra_usercenter_logout);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initUserInfo(mUserManager.getmUser());
		loadUserInfo();
	}
	
	
	public void initViews(){
		
		mTopRightView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(300))
		.imageScaleType(ImageScaleType.EXACTLY)
		.showImageForEmptyUri(R.drawable.ic_default_head)
		.showImageOnFail(R.drawable.ic_default_head)
		.showImageOnLoading(R.drawable.ic_default_head)
		.build();
		ImageLoader.getInstance().displayImage("", mHeadImg, options);
		mEditView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), UserInfoAcivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		goIntegralView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), IntegralActivity.class);
				startActivity(intent);
			}
		});
		
		goSettingView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});
		
		goMyRepairsView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyRepairsActivity.class);
				startActivity(intent);
			}
		});
		
		goMyOrderView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				startActivity(intent);
			}
		});
		
		mLogoutView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				logout();
				((MainActivity)getActivity()).setCurrentTabByTag(MainActivity.TAB_1);
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void logout(){
		SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).removeByKey(Constants.USER_SESSID);
		mUserManager.setmUser(null);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.remove(mInstance);
		ft.commit();
		mInstance = null;
	}
	
	private void loadUserInfo(){
		if(mUserManager.isLogin()){
			new QueryUserInfoTask().execute();
		}
	}
	
	private void initUserInfo(UserModel um){
		if(null != um){
			mNickname.setText(TextUtils.isEmpty(um.nickName) ? getString(R.string.nickname_null) : um.nickName);
			if(um.unit == null && um.romm == null){
				mAddress.setText(getString(R.string.adress_null));
			}else{
				mAddress.setText(um.unit + "栋" + um.romm);
			}
			mIntergal.setText("积分 ：" + um.integral);
			ImageLoader.getInstance().displayImage(um.image, mHeadImg, options);
		}
	}
	
	class QueryUserInfoTask extends AsyncTask<String, Void, UserJson>{

		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(getActivity());
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}
		
		@Override
		protected UserJson doInBackground(String... params) {
			UserJson uj = null;
			String json = mRequest.queryUserInfo(mUserManager.getmUser().sessId);
			if(!TextUtils.isEmpty(json)){
				uj = JSON.parseObject(json, UserJson.class);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(UserJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(0 == result.code){
					initUserInfo(result.data);
				}else{
					showToast(result.errMsg);
				}
			}
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
