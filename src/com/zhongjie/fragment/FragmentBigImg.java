package com.zhongjie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.zhongjie.BaseFragment;
import com.zhongjie.R;

public class FragmentBigImg extends BaseFragment{
	
	private int resId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView iv = new ImageView(getActivity().getApplicationContext());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setImageResource(resId == 0 ? R.drawable.temp_datu : resId);
		return iv;
	}
	
	public FragmentBigImg(){
	}
	
	public FragmentBigImg(int resId){
		this.resId = resId;
	}
}
