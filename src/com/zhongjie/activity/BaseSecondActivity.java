package com.zhongjie.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.zhongjie.MainActivity;
import com.zhongjie.R;



public abstract class BaseSecondActivity extends BaseActivity{

	@Override
	public void setContentView(int layoutResID) {
		View srov = getLayoutInflater().inflate(R.layout.slide_left_out_view, null);
		srov.setId(R.string.slide_view);
		View mainView = getLayoutInflater().inflate(layoutResID, null);
//		mainView.setBackgroundColor(Color.BLACK);
//		mainView.findViewById(R.string.real_content).setBackgroundResource(R.drawable.main_bg);
		((ViewGroup)mainView.findViewById(R.string.real_content))
		.addView(getLayoutInflater().inflate(R.layout.topview_flower, null),
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		((ViewGroup)srov).addView(mainView, 
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setContentView(srov);
//		super.setContentView(layoutResID);
	}
	
	protected void goHomeActivity(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}
}
