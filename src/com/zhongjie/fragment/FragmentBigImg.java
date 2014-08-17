package com.zhongjie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.BaseFragment;
import com.zhongjie.R;
import com.zhongjie.util.Utils;

public class FragmentBigImg extends BaseFragment{
	
	private int resId;
	private String imgUrl;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView iv = new ImageView(getActivity().getApplicationContext());
		iv.setScaleType(ScaleType.CENTER_CROP);
		if(!Utils.isEmpty(imgUrl)){
			ImageLoader.getInstance().displayImage(imgUrl, iv);
		}else{
			iv.setImageResource(resId == 0 ? R.drawable.temp_datu : resId);
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
}
