package com.zhongjie.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhongjie.view.autoscroll.AutoScrollViewPager;

public class MyViewPager extends AutoScrollViewPager{
	
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
