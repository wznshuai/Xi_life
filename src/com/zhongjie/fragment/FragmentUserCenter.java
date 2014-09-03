package com.zhongjie.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.BaseFragment;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.activity.user.IntegralActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.activity.user.MyDryCleanOrderActivity;
import com.zhongjie.activity.user.MyOrderActivity;
import com.zhongjie.activity.user.MyRepairsActivity;
import com.zhongjie.activity.user.SettingActivity;
import com.zhongjie.activity.user.UserInfoAcivity;
import com.zhongjie.model.OrderStatus;
import com.zhongjie.model.UserJson;
import com.zhongjie.model.UserModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.SharedPreferencesUtil;
import com.zhongjie.view.CommonLoadingDialog;

public class FragmentUserCenter extends BaseFragment {

	private static FragmentUserCenter mInstance;

	private ImageView mHeadImg;
	private View mEditView, goIntegralView, goSettingView, goMyRepairsView,
			goMyOrderView, mLogoutView, mGoDryCleanOrder;
	private static final int REQUEST_CODE = 0x001;

	private TextView mTopRightView, mAddress, mIntergal, mNickname;
	private CommonRequest mRequest;
	private UserModelManager mUserManager;
	private View mWaitpay, mWaitComment, mPaycommplete, mPaycancel;

	public static FragmentUserCenter newInstance() {
		if (null == mInstance)
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
		loadUserInfo();
	}

	@Override
	protected void initData() {
		super.initData();
		mRequest = new CommonRequest(getActivity().getApplicationContext());
		mUserManager = UserModelManager.getInstance();
	}

	public void findViews() {
		mAddress = (TextView) findViewById(R.id.fra_usercenter_address);
		mNickname = (TextView) findViewById(R.id.fra_usercenter_nickname);
		mIntergal = (TextView) findViewById(R.id.fra_usercenter_integral);
		mTopRightView = (TextView) getActivity().findViewById(
				R.id.topbar_rightTxt);
		mHeadImg = (ImageView) findViewById(R.id.fra_usercenter_head);
		mEditView = findViewById(R.id.fra_usercenter_edit);
		goIntegralView = findViewById(R.id.fra_usercenter_goIntegral);
		goSettingView = findViewById(R.id.fra_usercenter_goSetting);
		goMyRepairsView = findViewById(R.id.fra_usercenter_goMyrepairs);
		goMyOrderView = findViewById(R.id.fra_usercenter_goMyOrder);
		mLogoutView = findViewById(R.id.fra_usercenter_logout);
		mWaitpay = findViewById(R.id.fra_usercenter_waitpay);
		mWaitComment = findViewById(R.id.fra_usercenter_waitcomment);
		mPaycancel = findViewById(R.id.fra_usercenter_paycancel);
		mPaycommplete = findViewById(R.id.fra_usercenter_paycomplete);
		mGoDryCleanOrder = findViewById(R.id.fra_usercenter_goDryCleanHistory);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getActivityMine().getCurrentTabTag().equals(MainActivity.TAB_4)) {
			getActivityMine().setTopCenterLogo(R.drawable.ic_top_logo);
			initUserInfo(mUserManager.getmUser());
		}
	}

	public void initViews() {

		ImageLoader.getInstance().displayImage("", mHeadImg, options);
		initOnClickListeners();
	}

	private void initOnClickListeners() {
		mTopRightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
		
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
				Intent intent = new Intent(getActivity(),
						IntegralActivity.class);
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
				Intent intent = new Intent(getActivity(),
						MyRepairsActivity.class);
				startActivity(intent);
			}
		});

		goMyOrderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				intent.putExtra("status", "");
				startActivity(intent);
			}
		});

		mLogoutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logout();
				((MainActivity) getActivity())
						.setCurrentTabByTag(MainActivity.TAB_1);
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
		});
		
		mPaycancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				intent.putExtra("status", OrderStatus.STATUS_HAD_CANCELED);
				startActivity(intent);
			}
		});
		
		mPaycommplete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				intent.putExtra("status", OrderStatus.STATUS_HAD_COMMPLETED);
				startActivity(intent);
			}
		});
		
		mWaitComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				intent.putExtra("status", OrderStatus.STATUS_WAIT_COMMENT);
				startActivity(intent);
			}
		});
		
		mWaitpay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyOrderActivity.class);
				intent.putExtra("status", OrderStatus.STATUS_WAIT_PAY);
				startActivity(intent);
			}
		});
		
		mGoDryCleanOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyDryCleanOrderActivity.class);
				intent.putExtra("status", "");
				startActivity(intent);
			}
		});
	}

	private void logout() {
		SharedPreferencesUtil
				.getInstance(getActivity().getApplicationContext())
				.removeByKey(Constants.USER_SESSID);
		mUserManager.setmUser(null);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.remove(mInstance);
		ft.commit();
		mInstance = null;
	}

	private void loadUserInfo() {
		if (mUserManager.isLogin()) {
			new QueryUserInfoTask().execute();
		}
	}

	private void initUserInfo(UserModel um) {
		if (null != um) {
			mNickname
					.setText(TextUtils.isEmpty(um.nickName) ? getString(R.string.nickname_null)
							: um.nickName);
			if (um.unit == null && um.room == null) {
				mAddress.setText(getString(R.string.adress_null));
			} else {
				mAddress.setText(um.unit + "栋" + um.room);
			}
			mIntergal.setText("积分 ：" + um.integral);
			ImageLoader.getInstance().displayImage(um.image, mHeadImg, options);
		}
	}

	class QueryUserInfoTask extends AsyncTask<String, Void, UserJson> {

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
			String json = mRequest
					.queryUserInfo(mUserManager.getmUser().sessId);
			if (!TextUtils.isEmpty(json)) {
				uj = JSON.parseObject(json, UserJson.class);
			}
			return uj;
		}

		@Override
		protected void onPostExecute(UserJson result) {
			super.onPostExecute(result);
			if (!canGoon())
				return;
			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			if (null != result) {
				if (0 == result.code) {
					if(null != result.data)
						UserModelManager.getInstance().setmUser(result.data);
					initUserInfo(result.data);
				} else {
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
