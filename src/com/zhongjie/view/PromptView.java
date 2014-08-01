package com.zhongjie.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongjie.R;

public class PromptView extends LinearLayout {
	public final  int ACTION_NOTHING=0;
	public final  int ACTION_EMPTY=1;
	public final int ACTION_RETRY=2;
	public final  int ACTION_LOGIN=3;
	private View tipsLayout;
	private View loadingLayout;
	private TextView tipsText;
	private TextView loadingText;
	
	private int action=ACTION_NOTHING;
	private OnPromptClickListener onPromptClickListener;
	public PromptView(Context context) {
		super(context);
		init(context);
	}
	public PromptView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.setGravity(Gravity.CENTER);
		this.setVisibility(View.GONE);
		this.setOrientation(LinearLayout.VERTICAL);
		loadingLayout=inflater.inflate(R.layout.prompt_view_loading, null);
		tipsLayout=inflater.inflate(R.layout.prompt_view_tips, null);
		tipsText=(TextView) tipsLayout.findViewById(R.id.mokun_tips_text);
		loadingText=(TextView) loadingLayout.findViewById(R.id.mokun_loading_text);
		this.addView(tipsLayout);
		this.addView(loadingLayout);
		this.setGravity(Gravity.CENTER);
		initListeners();
	}
	
	public void initListeners(){
		tipsText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(onPromptClickListener!=null){
					onPromptClickListener.onClick(v, action);
				}
			}
		});
	}
	
	public void setOnPromptClickListener(OnPromptClickListener onPromptClickListener) {
		this.onPromptClickListener = onPromptClickListener;
	}
	
	public void showContent(){
		tipsLayout.setVisibility(View.GONE);
		loadingLayout.setVisibility(View.GONE);
		this.setVisibility(View.GONE);
	}
	
	public void showLoading(){
		tipsLayout.setVisibility(View.GONE);
		loadingLayout.setVisibility(View.VISIBLE);
		this.setVisibility(View.VISIBLE);
	}
	
	private void showTips(){
		tipsLayout.setVisibility(View.VISIBLE);
		loadingLayout.setVisibility(View.GONE);
		this.setVisibility(View.VISIBLE);
	}
	
	public void showLoading(String str){
		showLoading();
		loadingText.setText(str);
	}
	
	public void showLoading(int resId){
		loadingText.setText(resId);
	}
	
	public void showPrompt(String str){
		showPrompt(ACTION_NOTHING,str);
	}
	
	public void showPrompt(int resId){
		showPrompt(ACTION_NOTHING,resId);
	}
	
	public void showPrompt(String str,Drawable topIcon){
		showPrompt(ACTION_NOTHING,str,topIcon);
	}
	
	public void showPrompt(int resId,Drawable topIcon){
		showPrompt(ACTION_NOTHING,resId,topIcon);
	}
	public void showPrompt(int action,String str){
		Drawable topIcon=this.getResources().getDrawable(R.drawable.prompt_ic_info);
		showPrompt(action,str,topIcon);
	}
	public void showPrompt(int action,int resId){
		Drawable topIcon=this.getResources().getDrawable(R.drawable.prompt_ic_info);
		showPrompt(action,resId,topIcon);
	}
	public void showPrompt(int action,String str,Drawable topIcon){
		showTips();
		this.action=action;
		tipsText.setText(str);
		topIcon.setBounds(0, 0, topIcon.getIntrinsicWidth(), topIcon.getIntrinsicHeight());
		tipsText.setCompoundDrawables(null,topIcon,null,null);
	}
	
	public void showPrompt(int action,int resId,Drawable topIcon){
		showTips();
		this.action=action;
		tipsText.setText(resId);
		topIcon.setBounds(0, 0, topIcon.getIntrinsicWidth(), topIcon.getIntrinsicHeight());
		tipsText.setCompoundDrawables(null,topIcon,null,null);
	}
	/************扩展方法************/
	public void showEmpty(){
		showPrompt(ACTION_NOTHING,R.string.prompt_empty);
	}
	
	public void showError(){
		showPrompt(ACTION_NOTHING,R.string.prompt_error);
	}
	
	public void showNoLogin(){
		showPrompt(ACTION_LOGIN,R.string.prompt_nologin);
	}
	
	public interface OnPromptClickListener{
		void onClick(View v,int action);
	}
}
