package com.zhongjie.activity.anytimebuy;

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
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.global.Session;
import com.zhongjie.model.BaseJson;
import com.zhongjie.model.ShopCartModel;
import com.zhongjie.model.UploadImageJson;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.view.CommonDialog2;
import com.zhongjie.view.CommonLoadingDialog;
import com.zhongjie.view.MyRatingbar;
import com.zhongjie.view.SlideRightOutView;

public class SendCommentActivity extends BaseSecondActivity implements
		OnClickListener {

	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int IMAGE_FROM_CAMERA = 0x0a1;
	private static final int IMAGE_FROM_PHOTOS = 0xfe2;
	public static final int PHOTORESOULT = 0xaf3;// 结果

	private GridView mGridView;

	private MyRatingbar mRatingbar;

	private List<SendCommentGridModel> mGridList;
	
	private ShopCartModel mCommodityModel;
	private LinearLayout mCommodityArea;
	private ImageView mCommodityImg;
	private TextView mCommodityName, mCommodityPrice;
	private CommonRequest mRequest;
	private StringBuffer mUploadUrl;
	private String sessId;
	private EditText mEdit;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_send_comment);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		mGridList = new ArrayList<SendCommentActivity.SendCommentGridModel>();
		SendCommentGridModel scgm = new SendCommentGridModel();
		scgm.isPhoto = false;
		mGridList.add(scgm);
		mCommodityModel = (ShopCartModel)Session.getSession().remove("commodity");
		mRequest = new CommonRequest(getApplicationContext());
		sessId = UserModelManager.getInstance().getmUser().sessId;
	}

	@Override
	protected void findViews() {
		mRatingbar = (MyRatingbar) findViewById(R.id.act_send_comment_ratingbar);
		mRatingbar.setSrov((SlideRightOutView) mSlide);
		mGridView = (GridView) findViewById(R.id.act_send_comment_geidview);
		mCommodityArea = (LinearLayout)findViewById(R.id.act_send_comment_commodity);
		mCommodityImg = (ImageView)findViewById(R.id.act_send_comment_commodity_img);
		mCommodityName = (TextView)findViewById(R.id.act_send_comment_commodity_name);
		mCommodityPrice = (TextView)findViewById(R.id.act_send_comment_commodity_price);
		mEdit = (EditText)findViewById(R.id.act_send_comment_edit);
	}

	@Override
	protected void initViews() {
		initCommodity();
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("商品评价");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTopRightTxt.setText("提交");
		mTopRightTxt.setVisibility(View.VISIBLE);
		
		mTopRightTxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(mEdit.getText().toString())){
					showToast("请填写评价内容");
				}else{
					new SendCommentTask().execute();
				}
			}
		});

		mGridView.setAdapter(new MyGridAdapter());
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
//						int width = mGridView.getWidth();
//						int mCellWidth = width
//								/ 4
//								- Utils.dp2px(getApplicationContext(),
//										mCellMargin) * 3;
						mGridView.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(!mGridList.get(position).isPhoto)
					showSelectDialog();
			}
			
		});
	}
	
	
	
	private void initCommodity(){
		if(null != mCommodityModel){
			ImageLoader.getInstance().displayImage(mCommodityModel.image, mCommodityImg, options);
			mCommodityName.setText(mCommodityModel.name);
			mCommodityPrice.setText("￥" + mCommodityModel.price);
		}
	}

	class MyGridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mGridList == null ? 0 : mGridList.size();
		}

		@Override
		public SendCommentGridModel getItem(int position) {
			return mGridList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			ViewHolder vh;
			if(null == convertView){
				vh = new ViewHolder();
				if(type == 0){
					convertView = getLayoutInflater().inflate(R.layout.gridview_item_send_comment_1, null);
				}else if(type == 1){
					convertView = getLayoutInflater().inflate(R.layout.gridview_item_send_comment_2, null);
					vh.photo = (ImageView)convertView.findViewById(R.id.grid_item_send_comment_photo);
				}
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder)convertView.getTag();
			}
			
			if(type == 1){
				vh.photo.setImageBitmap(getItem(position).bmp);
			}
			
			return convertView;
		}
		
		class ViewHolder{
			ImageView photo;
		}

		@Override
		public int getItemViewType(int position) {
			int type = Adapter.IGNORE_ITEM_VIEW_TYPE;
			if (null != mGridList && mGridList.size() > 0) {
				type = mGridList.get(position).isPhoto ? 1 : 0;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

	}

	class SendCommentGridModel {
		
		public SendCommentGridModel(){
			
		}
		
		public SendCommentGridModel(Bitmap bm){
			this.bmp = bm;
		}
		
		public Bitmap bmp;
		public boolean isPhoto = true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_send_comment_add_photo:
			showSelectDialog();
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
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
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

	private void addInView(Bitmap bmp) {
		mGridList.add(mGridList.size() - 1, new SendCommentGridModel(bmp));
		if(mGridList.size() == 10){
			mGridList.remove(9);
		}
		((BaseAdapter)mGridView.getAdapter()).notifyDataSetChanged();
	}
	
	class SendCommentTask extends AsyncTask<Void, Void, BaseJson> {
		CommonLoadingDialog cld = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(SendCommentActivity.this);
			cld.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			cld.show();
		}

		
		@Override
		protected BaseJson doInBackground(Void... params) {
			String result = mRequest.commodityEvaluate(sessId, 
					mCommodityModel.commodityId, 
					mRatingbar.getRating() + "", mEdit.getText().toString(), 
					null == mUploadUrl ? null : mUploadUrl.toString());
			BaseJson uij = null;
			if(!TextUtils.isEmpty(result)){
				uij = JSON.parseObject(result, BaseJson.class);
			}
			return uij;
		}

		@Override
		protected void onPostExecute(BaseJson result) {

			if (!canGoon())
				return;

			if (null != cld) {
				cld.cancel();
				cld = null;
			}
			
			if(null != result){
				if(result.code == 0){
					showToast("谢谢您的评价!");
					finish();
				}else{
					showToast(result.errMsg);
				}
			}else{
				showToast("上传图片失败");
			}
		}
	}
	
	class UploadImgTask extends AsyncTask<File, Void, UploadImageJson> {
		CommonLoadingDialog cld = null;
		String largeLogo;
		String middleLogo;
		String smallLogo;
		String backgroundLogo;
		int flag;
		File file;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cld = CommonLoadingDialog.create(SendCommentActivity.this);
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
			file = params[0];
			String result = mRequest.repairImageUpload(file);
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
						if(null == mUploadUrl){
							mUploadUrl = new StringBuffer();
							mUploadUrl.append(result.data.uploadPath);
						}else{
							mUploadUrl.append("!" + result.data.uploadPath);
						}
						addInView(BitmapFactory.decodeFile(file.getPath()));
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
			if (resultCode == Activity.RESULT_OK){
				new UploadImgTask().execute(getTempHeadFile(true));
			}
		}
	}
}
