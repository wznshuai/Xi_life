package com.zhongjie.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{
	
	private SlideRightOutView srov;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	public void setSrov(SlideRightOutView srov) {
		this.srov = srov;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(null != srov)
			srov.requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(arg0);
	}
}
