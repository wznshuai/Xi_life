package com.zhongjie.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RatingBar;

public class MyRatingbar extends RatingBar{
	
	private SlideRightOutView srov;

	public MyRatingbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyRatingbar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyRatingbar(Context context) {
		super(context);
	}
	
	

	public void setSrov(SlideRightOutView srov) {
		this.srov = srov;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(null != srov)
			srov.requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(event);
	}
}
