package com.zhongjie.activity.managerservice;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.WheelVerticalView;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import com.alibaba.fastjson.JSON;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.activity.user.LoginActivity;
import com.zhongjie.global.Session;
import com.zhongjie.model.BaseJson;
import com.zhongjie.model.RepairShowModel;
import com.zhongjie.model.RepairsShowJson;
import com.zhongjie.model.UploadImageJson;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.Logger;
import com.zhongjie.view.CommonDialog2;
import com.zhongjie.view.CommonLoadingDialog;

public class RepairsActivity extends BaseSecondActivity{
	
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int IMAGE_FROM_CAMERA = 0x0a1;
	private static final int IMAGE_FROM_PHOTOS = 0xfe2;
	public static final int PHOTORESOULT = 0xaf3;// 结果
	
	private WheelVerticalView mYearWheel, mMonthWheel, mDayWheel, mRepairsTypeWheel;
	private Calendar mCurCalendar, mMaxCalendar;
	private View mSubmit;
	private CommonRequest mRequest;
	private TextView mAddress;
	private View mTakeAPicture;
	private RepairShowModel mRepairModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_repairs);
		super.onCreate(savedInstanceState);
		new QueryRepairsInfoTask().execute();
	}

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
		mCurCalendar = Calendar.getInstance(Locale.CHINA);
		mMaxCalendar = (Calendar)mCurCalendar.clone();
		mMaxCalendar.add(Calendar.DAY_OF_YEAR, 15);
	}

	@Override
	protected void findViews() {
		mYearWheel = (WheelVerticalView)findViewById(R.id.act_repairs_wheel_year);
		mMonthWheel = (WheelVerticalView)findViewById(R.id.act_repairs_wheel_month);
		mDayWheel = (WheelVerticalView)findViewById(R.id.act_repairs_wheel_day);
		mRepairsTypeWheel = (WheelVerticalView)findViewById(R.id.act_repairs_wheel_repairType);
		mSubmit = findViewById(R.id.act_repairs_submit);
		mAddress = (TextView)findViewById(R.id.act_repairs_address);
		mTakeAPicture = findViewById(R.id.act_repairs_takeAPicture);
	}
	
	private void initCalendar(){
		final int curYear = mCurCalendar.get(Calendar.YEAR);
		final int maxYear = mMaxCalendar.get(Calendar.YEAR);
		final int curMounth = mCurCalendar.get(Calendar.MONTH) + 1;
		final int maxMounth = mMaxCalendar.get(Calendar.MONTH) + 1;
		mRepairsTypeWheel.setViewAdapter(new ArrayWheelAdapter<String>(this, new String[]{"未知"}));
		mYearWheel.setViewAdapter(new NumericWheelAdapter(this, curYear
				, maxYear));
		
		//设置可选月数
		if(curYear == maxYear){
			mMonthWheel.setViewAdapter(new NumericWheelAdapter(this, curMounth
					, maxMounth, "%02d"));
		}else{
			mMonthWheel.setViewAdapter(new NumericWheelAdapter(this, curMounth
					, 12, "%02d"));
			
			mYearWheel.addChangingListener(new OnWheelChangedListener() {
				
				@Override
				public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
					newValue = paresStrDate(((NumericWheelAdapter)mMonthWheel.getViewAdapter()).getItemText(newValue).toString());
					//设置可选月数
					if(newValue == curYear){
						mMonthWheel.setViewAdapter(new NumericWheelAdapter(RepairsActivity.this, curMounth
								, 12, "%02d"));
					}else if(newValue == maxYear){
						mMonthWheel.setViewAdapter(new NumericWheelAdapter(RepairsActivity.this, 1
								, maxMounth, "%02d"));
					}else{
						mMonthWheel.setViewAdapter(new NumericWheelAdapter(RepairsActivity.this, 1
								, 12, "%02d"));
					}
					mMonthWheel.setCurrentItem(0, true);
				}
			});
		}
		
		
		
		if(curMounth == maxMounth){
			mDayWheel.setViewAdapter(new NumericWheelAdapter(this, mCurCalendar.get(Calendar.DAY_OF_MONTH)
					, mMaxCalendar.get(Calendar.DAY_OF_MONTH), "%02d"));
		}else{
			mDayWheel.setViewAdapter(new NumericWheelAdapter(this, mCurCalendar.get(Calendar.DAY_OF_MONTH)
					, mCurCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
			
			mMonthWheel.addChangingListener(new OnWheelChangedListener() {
				
				@Override
				public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
					newValue = paresStrDate(((NumericWheelAdapter)mMonthWheel.getViewAdapter()).getItemText(newValue).toString());
					if(newValue == curMounth){
						mDayWheel.setViewAdapter(new NumericWheelAdapter(RepairsActivity.this, mCurCalendar.get(Calendar.DAY_OF_MONTH)
								, mCurCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
					}else if(newValue == maxMounth){
						mDayWheel.setViewAdapter(new NumericWheelAdapter(RepairsActivity.this, 1
								, mMaxCalendar.get(Calendar.DAY_OF_MONTH), "%02d"));
					}else{
						Calendar temp = ((Calendar)mCurCalendar.clone());
						temp.set(Calendar.MONTH, newValue - 1);
						mDayWheel.setViewAdapter(new NumericWheelAdapter(RepairsActivity.this, 1
								, temp.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
					}
					mDayWheel.setCurrentItem(0, true);
				}
			});
		}
	}
	
	private int paresStrDate(String strDate){
		if(strDate.charAt(0) == 0 && strDate.length() > 1){
			return Integer.valueOf(strDate.substring(1, strDate.length()));
		}
		return Integer.valueOf(strDate);
	}

	@Override
	protected void initViews() {
		mTopCenterImg.setImageResource(R.drawable.ic_top_logo_repair);
		mTopCenterImg.setVisibility(View.VISIBLE);
		
		initCalendar();
		
		
		mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(UserModelManager.getInstance().isLogin()){
					new SubmitRepair().execute();
				}else{
					startActivity(new Intent(RepairsActivity.this, LoginActivity.class));
				}
			}
		});
		
		mTakeAPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mRepairModel)
					showSelectDialog();
			}
		});
	}
	
	private void initInfos(RepairShowModel rsm){
		if(null != rsm){
			mRepairModel = rsm;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				mMaxCalendar.setTime(sdf.parse(rsm.repairDate));
			} catch (ParseException e) {
			}
			initCalendar();
			mAddress.setText("房号 :  " + rsm.unit +"栋" + rsm.room + "室");
			if(null != rsm.classify)
				mRepairsTypeWheel.setViewAdapter(new ArrayWheelAdapter<String>(this, rsm.classify));
			rsm = null;
		}
	}
	
	class QueryRepairsInfoTask extends AsyncTask<String, Void, RepairsShowJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(RepairsActivity.this);
			cld.setCanceledOnTouchOutside(false);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}

		@Override
		protected RepairsShowJson doInBackground(String... params) {
			RepairsShowJson uj = null;
			try {
				
				String result = mRequest.repairShow(UserModelManager.getInstance().getmUser().sessId);
				if(!TextUtils.isEmpty(result)){
					uj = JSON.parseObject(result, RepairsShowJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "QueryRepairsInfoTask error", e);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(RepairsShowJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(result.code == 0){
					if(null != result.data){
						initInfos(result.data);
					}
				}else{
					showToast(result.errMsg);
				}
			}
		}
	}
	
	class SubmitRepair extends AsyncTask<String, Void, BaseJson>{
		CommonLoadingDialog cld;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(RepairsActivity.this);
			cld.setCanceledOnTouchOutside(false);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected BaseJson doInBackground(String... params) {
			BaseJson uj = null;
			try {
				String year = ((NumericWheelAdapter)mYearWheel.getViewAdapter())
						.getItemText(mYearWheel.getCurrentItem()).toString();
				String month = ((NumericWheelAdapter)mMonthWheel.getViewAdapter())
						.getItemText(mMonthWheel.getCurrentItem()).toString();
				String day = ((NumericWheelAdapter)mDayWheel.getViewAdapter())
						.getItemText(mDayWheel.getCurrentItem()).toString();
				mRepairModel.needClassify = ((ArrayWheelAdapter<String>)mRepairsTypeWheel.getViewAdapter())
						.getItemText(mYearWheel.getCurrentItem()).toString();
				mRepairModel.needRepairDate = year + "-" + month + "-" + day;
				String result = mRequest.repairSubmit(
						UserModelManager.getInstance().getmUser().sessId,
						mRepairModel.needRepairDate, mRepairModel.needClassify, mRepairModel.image);
				if(!TextUtils.isEmpty(result)){
					uj = JSON.parseObject(result, BaseJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "QueryRepairsInfoTask error", e);
			}
			return uj;
		}
		
		@Override
		protected void onPostExecute(BaseJson result) {
			super.onPostExecute(result);
			if(!canGoon())
				return;
			if(null != cld){
				cld.cancel();
				cld = null;
			}
			if(null != result){
				if(result.code == 0){
					Session session = Session.getSession();
					session.put("repairModel", mRepairModel);
					startActivity(new Intent(RepairsActivity.this, RepairsSuccess.class));
				}else{
					showToast(result.errMsg);
				}
			}
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
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 300);
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
			cld = CommonLoadingDialog.create(RepairsActivity.this);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.setTitle("上传");
			cld.setMessage("图片上传中");
			cld.show();
		}

		@Override
		protected UploadImageJson doInBackground(File... params) {
			String result = mRequest.repairImageUpload(params[0]);
			UploadImageJson uij = null;
			if(!TextUtils.isEmpty(result)){
				uij = JSON.parseObject(result, UploadImageJson.class);
			}
			return uij;
		}

		@Override
		protected void onPostExecute(UploadImageJson result) {

			if (!canGoon())
				return;

			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			
			if(null != result){
				if(result.code == 0){
					if(null != result.data){
						ViewGroup vg = (ViewGroup)mTakeAPicture.getParent();
						mRepairModel.image = result.data.uploadPath;
						if(vg.getChildCount() > 2){
							((ImageView)vg.getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(getTempHeadFile(true).getPath()));
						}else{
							int width = (int)getResources().getDimension(R.dimen.repairs_photo);
							ImageView img = new ImageView(getApplicationContext());
							img.setScaleType(ScaleType.FIT_XY);
							img.setLayoutParams(new LayoutParams(width, width));
							img.setImageBitmap(BitmapFactory.decodeFile(getTempHeadFile(true).getPath()));
							vg.addView(img, 1);
						}
						vg = null;
					}
				}else{
					showToast(result.errMsg);
				}
			}else{
				showToast("上传图片失败");
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
