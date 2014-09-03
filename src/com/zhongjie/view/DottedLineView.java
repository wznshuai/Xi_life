package com.zhongjie.view;

import com.zhongjie.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DottedLineView extends View{
	
	private Paint mPaint;
	private int mPaintColor;
	
	private void init(Context context, AttributeSet attrs, int defStyleAttr){
		mPaint = new Paint();
		if(null != attrs){
			TypedArray a = context.obtainStyledAttributes(
					attrs, R.styleable.DottedLine, defStyleAttr, 0);
			mPaintColor = a.getColor(R.styleable.DottedLine_line_color, Color.BLACK);
		}else{
			mPaintColor = Color.BLACK;
		}
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(mPaintColor);
	}

	public DottedLineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	public DottedLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public DottedLineView(Context context) {
		super(context);
		init(context, null, 0);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
