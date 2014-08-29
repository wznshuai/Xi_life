package com.zhongjie.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.BaseFragment;
import com.zhongjie.R;
import com.zhongjie.activity.PhotoViewActivity;
import com.zhongjie.util.Utils;

public class FragmentBigImg extends BaseFragment{
	
	private int resId;
	private String imgUrl, bigImagUrl;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView iv = new ImageView(getActivity().getApplicationContext());
		iv.setScaleType(ScaleType.FIT_XY);
		if(!Utils.isEmpty(imgUrl)){
			ImageLoader.getInstance().displayImage(imgUrl, iv, options);
		}else{
			iv.setImageResource(resId == 0 ? R.drawable.temp_datu : resId);
		}
		
		if(!Utils.isEmpty(bigImagUrl)){
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
					intent.putExtra(PhotoViewActivity.BIGIMG_URL, bigImagUrl);
					startActivity(intent);
				}
			});
		}
		return iv;
	}
	
	public FragmentBigImg(){
	}
	
	public FragmentBigImg(int resId){
		this.resId = resId;
	}
	
	public FragmentBigImg(String imgUrl){
		this.imgUrl = imgUrl;
	}
	
	public FragmentBigImg(String imgUrl, String bigImgUrl){
		this.imgUrl = imgUrl;
		this.bigImagUrl = bigImgUrl;
	}
}
