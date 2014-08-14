package com.zhongjie.activity.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.UploadImageJson;
import com.zhongjie.model.UserJson;
import com.zhongjie.model.UserModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.view.CommonDialog2;
import com.zhongjie.view.CommonLoadingDialog;

public class UserInfoAcivity extends BaseSecondActivity implements OnClickListener{
	
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int IMAGE_FROM_CAMERA = 0x0a1;
	private static final int IMAGE_FROM_PHOTOS = 0xfe2;
	public static final int PHOTORESOULT = 0xaf3;// 结果

	private static final int STATUS_CAN_MODIFY = 1;
	private static final int STATUS_CANNOT_MODIFY = 2;
	
	
	private ImageView mHead;
	private View mHeadEdit, mConfirmView, mSubmit;
	private TextView mNickname, mAddress1, mAddress2, mPhone;
	private EditText mNicknameEdit, mAddress1Edit, mAddress2Edit;
	private CommonRequest mRequest;
	private UserModelManager mUserManager;
	private UserModel mUm;
	private int CURRENT_STATUS = STATUS_CANNOT_MODIFY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_userinfo);
		super.onCreate(savedInstanceState);
		new QueryUserInfoTask().execute();
	}
	
	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		mUserManager = UserModelManager.getInstance();
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
		mPhone = (TextView)findViewById(R.id.act_userinfo_phone);
		mSubmit = findViewById(R.id.act_userinfo_submit);
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
		mHead.setOnClickListener(this);
		mSubmit.setOnClickListener(this);
	}
	
	private void changeEditStatus(boolean isEdit){
		if(isEdit){
			CURRENT_STATUS = STATUS_CAN_MODIFY;
			mHeadEdit.setVisibility(View.VISIBLE);
			mNickname.setVisibility(View.GONE);
			mNicknameEdit.setVisibility(View.VISIBLE);
			mAddress1.setVisibility(View.GONE);
			mAddress1Edit.setVisibility(View.VISIBLE);
			mAddress2.setVisibility(View.GONE);
			mAddress2Edit.setVisibility(View.VISIBLE);
			mConfirmView.setVisibility(View.VISIBLE);
			if(null != mUm){
				mNicknameEdit.setHint((TextUtils.isEmpty(mUm.nickName) ? getString(R.string.nickname_null) : mUm.nickName));
				mAddress1Edit.setHint(null == mUm.unit ? "0" : mUm.unit);
				mAddress2Edit.setHint(null == mUm.room ? "0" : mUm.room);
			}
		}else{
			CURRENT_STATUS = STATUS_CANNOT_MODIFY;
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
	
	private void initUserInfo(UserModel um){
		if(null != um){
			mUm = um;
			mNickname.setText(TextUtils.isEmpty(um.nickName) ? getString(R.string.nickname_null) : um.nickName);
			mAddress1.setText(null == um.unit ? "0" : um.unit);
			mAddress2.setText(null == um.room ? "0" : um.room);
			mPhone.setText(um.phone);
		}
	}
	
	class QueryUserInfoTask extends AsyncTask<String, Void, UserJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(UserInfoAcivity.this);
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
			if(!canGOON())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(0 == result.code){
					initUserInfo(result.data);
					UserModelManager.getInstance().setmUser(result.data);
				}else{
					showToast(result.errMsg);
				}
			}
		}
	}
	
	class ModifyUserInfoTask extends AsyncTask<String, Void, UserJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(UserInfoAcivity.this);
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
			String nickname = mNicknameEdit.getText().toString();
			String unit = mAddress1Edit.getText().toString();
			if(unit.equals("0"))
				unit = "";
			String romm = mAddress2Edit.getText().toString();
			if(romm.equals("0"))
				romm = "";
			String json = mRequest.modifyUserInfo(mUm.sessId, nickname, mUm.image, unit, romm);
			if(!TextUtils.isEmpty(json)){
				uj = JSON.parseObject(json, UserJson.class);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(UserJson result) {
			super.onPostExecute(result);
			if(!canGOON())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(0 == result.code){
					initUserInfo(result.data);
					UserModelManager.getInstance().setmUser(result.data);
					changeEditStatus(false);
				}else{
					showToast(result.errMsg);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topbar_rightTxt:
			if(CURRENT_STATUS == STATUS_CANNOT_MODIFY){
				mTopRightTxt.setText("取消");
				changeEditStatus(true);
			}else{
				mTopRightTxt.setText("修改");
				changeEditStatus(false);
			}
			break;
		case R.id.act_userinfo_head:
			if(CURRENT_STATUS == STATUS_CAN_MODIFY){
				showSelectDialog();
			}
			break;
		case R.id.act_userinfo_submit:
			new ModifyUserInfoTask().execute();
			break;
		default:
			break;
		}
	}
	
	private void showSelectDialog() {
		List<String> mTitles = new ArrayList<String>();
		mTitles.add("从相册选择");
		mTitles.add("拍照");

		final CommonDialog2 cd2 = new CommonDialog2(this, mTitles);

		cd2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 1:
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {
						getFromCamera();// 从相机获取
						cd2.cancel();
					} else {
						// 没有SD卡;
						Toast.makeText(getApplicationContext(), "手机没有SD卡",
								Toast.LENGTH_SHORT).show();
						cd2.cancel();
					}
					break;
				case 0:
					getFromPhotos();// 从相相册获取
					cd2.cancel();
					break;
				}
			}

		});

		cd2.show();
	}

	private File getTempHeadFile(boolean isCrop) {
		File f = null;
		File head = null;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			f = new File(android.os.Environment.getExternalStorageDirectory(),
					Constants.APP_TEMP);
		else
			f = getApplicationContext().getCacheDir();

		if (!f.exists())
			f.mkdirs();
		else {
			if (f.isFile()) {
				f.deleteOnExit();
				f.mkdirs();
			}
		}
		if (isCrop)
			head = new File(f, "head.jpg");
		else
			head = new File(f, "head2.jpg");
		System.out.println("head path : " + head.getPath());
		if (!head.exists()) {
			try {
				head.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		f = null;
		return head;
	}

	private void getFromCamera() {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(getTempHeadFile(false)));
			startActivityForResult(intent, IMAGE_FROM_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getFromPhotos() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_UNSPECIFIED);
		startActivityForResult(intent, IMAGE_FROM_PHOTOS);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("scale", true);
		Uri imageUri = Uri.fromFile(getTempHeadFile(true));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		try {
			startActivityForResult(intent, PHOTORESOULT);
		} catch (Exception e) {
		}
	}
	
	class UpdateHeadTask extends AsyncTask<File, Void, UploadImageJson> {
		CommonLoadingDialog cld = null;
		String largeLogo;
		String middleLogo;
		String smallLogo;
		String backgroundLogo;
		int flag;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(UserInfoAcivity.this);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.setTitle("上传");
			cld.setMessage("头像上传中");
			cld.show();
		}

		@Override
		protected UploadImageJson doInBackground(File... params) {
			String result = mRequest.uploadImage(params[0]);
			UploadImageJson uij = null;
			if(!TextUtils.isEmpty(result)){
				uij = JSON.parseObject(result, UploadImageJson.class);
			}
			return uij;
		}

		@Override
		protected void onPostExecute(UploadImageJson result) {

			if (!canGOON())
				return;

			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			
			if(null != result){
				if(result.code == 0){
					if(null != result.data){
						mUm.image = result.data.uploadPath;
						mHead.setImageBitmap(BitmapFactory.decodeFile(getTempHeadFile(true).getPath()));
					}
				}else{
					showToast(result.errMsg);
				}
			}else{
				showToast("修改头像失败");
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IMAGE_FROM_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				startPhotoZoom(Uri.fromFile(getTempHeadFile(false)));
			}
		} else if (requestCode == IMAGE_FROM_PHOTOS) {
			if (resultCode == Activity.RESULT_OK) {
				startPhotoZoom(data.getData());
			}
		} else if (requestCode == PHOTORESOULT) {
			if (resultCode == Activity.RESULT_OK)
				new UpdateHeadTask().execute(getTempHeadFile(true));
		}
	}
}
