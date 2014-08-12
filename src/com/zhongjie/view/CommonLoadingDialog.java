package com.zhongjie.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongjie.R;

/**
 * @Description:
 * @version $Revision: 1.0 $
 * @author xuewu.wei
 * @date: 2012-6-5
 */
public class CommonLoadingDialog extends Dialog {

	private TextView mMessageView;
	private ImageView mProgressView;

	private CommonLoadingDialog(Context context) {
		super(context);
	}

	private CommonLoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	private void initViews() {
		mProgressView = (ImageView) findViewById(R.id.dialog_loading_progress);
		mMessageView = (TextView) findViewById(R.id.dialog_loading_message);
	}

	public static CommonLoadingDialog show(Context context) {
		return create(context).show(R.string.common_loading);
	}
	public static CommonLoadingDialog show(Context context,int strId) {
		return create(context).show(strId);
	}
	public static CommonLoadingDialog show(Context context,String str) {
		return create(context).show(str);
	}
	public static CommonLoadingDialog create(Context context) {
		CommonLoadingDialog dialog = new CommonLoadingDialog(context,
				R.style.Theme_TransparentDialog);
		dialog.setContentView(R.layout.dialog_loading);
		dialog.initViews();
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
	
	public  CommonLoadingDialog show(String message) {
		this.setMessage(message);
		show();
		return this;
	}
	public  CommonLoadingDialog show(int id) {
		this.setMessage(id);
		show();
		return this;
	}
	public void onWindowFocusChanged(boolean hasFocus) {
		if (mProgressView == null) {
			return;
		}
		mProgressView.startAnimation(AnimationUtils.loadAnimation(
				this.getContext(), R.anim.circle_progress));
	}
	public CommonLoadingDialog setMessage(int id) {
		if (mMessageView != null) {
			mMessageView.setText(id);
		}
		return this;
	}
	public CommonLoadingDialog setMessage(String message) {
		if (mMessageView != null) {
			mMessageView.setText(message);
		}
		return this;
	}

}
