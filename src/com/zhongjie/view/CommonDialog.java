package com.zhongjie.view;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.TextView;

import com.zhongjie.R;
import com.zhongjie.util.Utils;

public class CommonDialog{

	private AlertDialog.Builder builder;
	private Context context;
	private AlertDialog dialog;
	
	private TextView titleTv;
	private TextView messageTv;
	private TextView leftBtn;
	private TextView rightBtn;
	private View lineView;
	/**
	 * CommonDialog
	 */
	private CommonDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		this.dialog = builder.create();
		dialog.show();
		LayoutParams lp = dialog.getWindow().getAttributes();
		lp.height = (int)(Utils.getScreenHeight(context)*0.2f);
		dialog.getWindow().setAttributes(lp);
		initViews();
	}
	public static CommonDialog creatDialog(Context context){
		return new CommonDialog(context);
	}
	private void initViews(){
		dialog.setContentView(R.layout.common_dialog);
		leftBtn=(TextView) dialog.findViewById(R.id.dialog_leftBtn);
		rightBtn=(TextView) dialog.findViewById(R.id.dialog_rightBtn);
		messageTv = (TextView)dialog.findViewById(R.id.dialog_content);
		lineView = dialog.findViewById(R.id.tempId1);
	}
	public boolean isShow(){
		return dialog.isShowing();
	}
	public CommonDialog self(){
		return this;
	}
	public CommonDialog show(){
		dialog.show();
		//SOFT_INPUT_
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return this;
	}
	public CommonDialog dismiss(){
		dialog.cancel();
		return this;
	}
	public CommonDialog setTitle(int resId){
		String str=context.getString(resId);
		return setTitle(str);
	}
	public CommonDialog setTitle(String title){
		if(!TextUtils.isEmpty(title)){
			titleTv.setText(title);
			titleTv.setVisibility(View.VISIBLE);
		}
		else titleTv.setVisibility(View.GONE);
		return this;
	}
	public CommonDialog setMessage(int resId){
		String str=context.getString(resId);
		return setMessage(str);
	}
	public CommonDialog setMessage(CharSequence message){
		if(!TextUtils.isEmpty(message)){
			messageTv.setText(message);
			messageTv.setVisibility(View.VISIBLE);
		}
		else messageTv.setVisibility(View.GONE);
		return this;
	}		
	
	public CommonDialog setLeftButtonInfo(String text, final OnButtonClickListener onButtonClickListener){
		leftBtn.setVisibility(View.VISIBLE);
		lineView.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(text))leftBtn.setText(text);
		leftBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(onButtonClickListener!=null)
					onButtonClickListener.onClick(CommonDialog.this, v);
			}
			
		});
		return this;
	}
	
	public CommonDialog setRightButtonInfo(String text, final OnButtonClickListener onButtonClickListener){
		rightBtn.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(text))rightBtn.setText(text);
		rightBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(onButtonClickListener!=null)
					onButtonClickListener.onClick(CommonDialog.this, v);
			}
			
		});
		return this;
	}
	
	public void setCanceledOnTouchOutside(boolean cancel){
		dialog.setCanceledOnTouchOutside(cancel);
	}
	public void setCancelable(boolean cancel){
		dialog.setCancelable(cancel);
	}
	public interface OnButtonClickListener{
		public void onClick(CommonDialog cd, View view);
	}
	public interface OnDialogItemClickListener{
		public void onItemClick(AdapterView<?> parent, View view,int position, long id);
	}
	
	public View findViewById(int resId){
		return dialog.findViewById(resId);
	}
}
