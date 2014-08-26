package com.zhongjie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class BaseFragment extends Fragment{
	/**
	 * getActivity().getApplicationContext();
	 */
	public Context mCon;
	public View mainView = null;
	public View goback;
	public String title , tag;
	public int resourceId;
	public DisplayImageOptions options;
	public boolean isFirstLoading = true;
	

	protected boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}
	
	/**
	 * 
	 */
	protected void initData() {
		
	}
	
	protected void findViews(){}
	
	protected void initViews(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.showImageForEmptyUri(R.drawable.ic_default_head)
		.showImageOnFail(R.drawable.ic_default_head)
		.displayer(new FadeInBitmapDisplayer(300))
		.imageScaleType(ImageScaleType.EXACTLY)
		.build();
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mCon = getActivity().getApplicationContext();
		initData();
		findViews();
		initViews();
	}
	
	protected void initSomething(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println(getClass().getSimpleName() + "  onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public View findParentActViewById(int id){
		return getActivity().findViewById(id);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		System.out.println(getClass().getSimpleName() + "  onResume");
		initSomething();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println(getClass().getSimpleName() + "  onPause");
	}
	
	public MainActivity getActivityMine(){
		return (MainActivity)getActivity();
	}
	
	
	public void back(){
		getFragmentManager().popBackStack(getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//		getFragmentManager().beginTransaction().setCustomAnimations(R.anim.act_anim_in2, R.anim.act_anim_out2).remove(BaseFragment.this).commit();
	}
	protected View findViewById(int id){
		if(mainView != null)
			return mainView.findViewById(id);
		else
			return getActivity().findViewById(id);
	}
	
	protected void getRequestFailAlertDialog(DialogInterface.OnClickListener positiveClick){
		new AlertDialog.Builder(getActivity()).setMessage("请求失败")
			.setPositiveButton("重新请求", positiveClick).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					dialog = null;
				}
			}).create().show();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("aa", true);
		System.out.println(getClass().getSimpleName() + "  onSaveInstanceState");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		System.out.println(getClass().getSimpleName() + "  onDetach");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		System.out.println(getClass().getSimpleName() + "  onDestroyView");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println(getClass().getSimpleName() + "  onDestroy");
	}
	
	protected void showToast(String msg){
		Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	protected void showToast(int resId){
		Toast.makeText(getActivity().getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 是否可以继续执行
	 * @return
	 */
	public boolean canGoon() {
		if (!isAdded() || isRemoving() || isDetached()) {
			return false;
		}
		return true;
	}
}
