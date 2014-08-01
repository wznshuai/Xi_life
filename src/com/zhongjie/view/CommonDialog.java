package com.zhongjie.view;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongjie.R;

public class CommonDialog{

	private AlertDialog.Builder builder;
	private Context context;
	private AlertDialog dialog;
	
	private TextView titleTv;
	private TextView messageTv;
	private Button leftBtn;
	private Button rightBtn;
	private Button centerBtn;
	private ListView listView;
	private LinearLayout contentView;
	/**
	 * CommonDialog
	 */
	private CommonDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		this.dialog=builder.create();
		dialog.show();
		initViews();
	}
	public static CommonDialog creatDialog(Context context){
		return new CommonDialog(context);
	}
	private void initViews(){
		dialog.setContentView(R.layout.common_dialog);
		 titleTv=(TextView) dialog.findViewById(R.id.dialog_common_title);
		 messageTv=(TextView) dialog.findViewById(R.id.dialog_common_message);
		 leftBtn=(Button) dialog.findViewById(R.id.dialog_common_left);
		 rightBtn=(Button) dialog.findViewById(R.id.dialog_common_right);
		 centerBtn=(Button) dialog.findViewById(R.id.dialog_common_center);
		 listView=(ListView) dialog.findViewById(R.id.dialog_common_list);
	 	 contentView=(LinearLayout) dialog.findViewById(R.id.dialog_common_content);
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
			dialog.findViewById(R.id.dialog_common_title_line).setVisibility(View.VISIBLE);
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
		return setLeftButtonInfo(text, onButtonClickListener, false);
	}
	
	public CommonDialog setLeftButtonInfo(String text, final OnButtonClickListener onButtonClickListener, final boolean isAutoClose){
		centerBtn.setVisibility(View.GONE);
		leftBtn.setVisibility(View.VISIBLE);
		dialog.findViewById(R.id.dialog_common_line).setVisibility(View.VISIBLE);
		dialog.findViewById(R.id.dialog_common_btn_area).setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(text))leftBtn.setText(text);
		leftBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(onButtonClickListener!=null)
					onButtonClickListener.onClick(CommonDialog.this, v);
				if(isAutoClose)
					dismiss();
			}
			
		});
		return this;
	}
	
	public CommonDialog setRightButtonInfo(String text, final OnButtonClickListener onButtonClickListener){
		centerBtn.setVisibility(View.GONE);
		rightBtn.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(text))rightBtn.setText(text);
		dialog.findViewById(R.id.dialog_common_line).setVisibility(View.VISIBLE);
		dialog.findViewById(R.id.dialog_common_btn_area).setVisibility(View.VISIBLE);
		rightBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(onButtonClickListener!=null)
					onButtonClickListener.onClick(CommonDialog.this, v);
			}
			
		});
		return this;
	}
	public CommonDialog setCenterButtonInfo(String text,final OnButtonClickListener onButtonClickListener){
		leftBtn.setVisibility(View.GONE);
		rightBtn.setVisibility(View.GONE);
		centerBtn.setVisibility(View.VISIBLE);
		dialog.findViewById(R.id.dialog_common_line).setVisibility(View.VISIBLE);
		dialog.findViewById(R.id.dialog_common_btn_area).setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(text))centerBtn.setText(text);
		centerBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(onButtonClickListener!=null)
					onButtonClickListener.onClick(CommonDialog.this, v);
			}
			
		});
		return this;
	}
	public CommonDialog setListViewInfo(BaseAdapter adapter, final OnDialogItemClickListener onDialogItemClickListener){
		listView.setVisibility(View.VISIBLE);
		dialog.findViewById(R.id.dialog_common_line).setVisibility(View.GONE);
		dialog.findViewById(R.id.dialog_common_btn_area).setVisibility(View.GONE);
		listView.setAdapter(adapter);	
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(onDialogItemClickListener!=null)
				onDialogItemClickListener.onItemClick(parent, view, position, id);
				dismiss();
			}
			
		});
		return this;
	}
	public CommonDialog setContentView(int resId){
		View view=LayoutInflater.from(context).inflate(resId, contentView, false);
		contentView.addView(view);
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
