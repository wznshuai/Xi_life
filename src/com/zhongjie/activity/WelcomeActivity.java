package com.zhongjie.activity;

import java.lang.ref.WeakReference;

import CheckVersionModel.CheckVersionJson;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.zhongjie.ApnConstants;
import com.zhongjie.MainActivity;
import com.zhongjie.R;
import com.zhongjie.model.MsgCarry;
import com.zhongjie.model.UserModel;
import com.zhongjie.model.UserModelManager;
import com.zhongjie.service.UpdateService;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Constants;
import com.zhongjie.util.Logger;
import com.zhongjie.util.SharedPreferencesUtil;
import com.zhongjie.util.ShopCartManager;
import com.zhongjie.util.UpgradeFileUtil;


public class WelcomeActivity extends Activity{
	
	private CommonRequest mRequest;
	private boolean shouldFinishApp = false;
	public ProgressDialog m_pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initData();
		new UpgradeTask().execute();
	}
	
	private void initData(){
		mRequest = new CommonRequest(getApplicationContext());
	}

	class AutoLoginTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ShopCartManager.getInstance().readDataFromSavd(getApplicationContext());
			String sessId = SharedPreferencesUtil.getInstance(getApplicationContext()).getString(Constants.USER_SESSID);
			if(null != sessId && !TextUtils.isEmpty(sessId)){
				UserModel um = new UserModel();
				um.sessId = sessId;
				UserModelManager.getInstance().setmUser(um);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
			finish();
		}
	}
	
	/********* 版本检查更新 ***********/
	public class UpgradeTask extends AsyncTask<Void, Void, CheckVersionJson> {

		@Override
		protected CheckVersionJson doInBackground(Void... params) {
			//自动登录
			ShopCartManager.getInstance().readDataFromSavd(getApplicationContext());
			String sessId = SharedPreferencesUtil.getInstance(getApplicationContext()).getString(Constants.USER_SESSID);
			if(null != sessId && !TextUtils.isEmpty(sessId)){
				UserModel um = new UserModel();
				um.sessId = sessId;
				UserModelManager.getInstance().setmUser(um);
			}
		
			
			CheckVersionJson uj = null;
			try {
				String result = mRequest.queryAppUpdate(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
				if(!TextUtils.isEmpty(result)){
					uj = JSON.parseObject(result, CheckVersionJson.class);
				}
			} catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "UpgradeTask error", e);
			}
				
			
			return uj;
		}

		@Override
		protected void onPostExecute(CheckVersionJson cvr) {

			if (null != cvr && null != cvr.data && cvr.data.updateFlag != 0) {
				if (cvr.data.updateFlag == 2) {
					forceVersionUpdate(cvr.data.versionCode, cvr.data.updateUrl);
				} else if(cvr.data.updateFlag == 1){
					askVersionUpdate(cvr.data.versionCode, cvr.data.updateUrl);
				}

			} else {
				new AutoLoginTask().execute();
			}
		}
	}
	
	private String getApkName(String version) {
		return getResources().getString(R.string.apk_name_prefix) + version;
	}

	/***
	 * 询问是否更新版本
	 */
	private void askVersionUpdate(final String serverVersion,
			final String downloadUrl) {

		final String apkName = getApkName(serverVersion);
		// 发现新版本，提示用户更新
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCancelable(false);
		alert.setTitle("软件升级")
				.setMessage("嘻生活" + serverVersion + "版已经发布,快去体验新功能吧.")
				.setPositiveButton("升级新版",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// 开启更新服务UpdateService
								// 这里为了把update更好模块化，可以传一些updateService依赖的值
								// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
								Intent updateIntent = new Intent(
										WelcomeActivity.this,
										UpdateService.class);
								updateIntent.putExtra("apk_name", apkName);
								updateIntent.putExtra("download_url",
										downloadUrl);
								startService(updateIntent);
								finish();
							}
						})
				.setNegativeButton("下次再说",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								finish();
							}
						});
		alert.create().show();
	}

	/***
	 * 强制用户更新版本
	 */
	private void forceVersionUpdate(final String serverVersion,
			final String downloadUrl) {

		final String apkName = getApkName(serverVersion);

		// 发现新版本，自动更新
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCancelable(false);
		alert.setTitle("软件升级").setMessage("当前版本太旧了,无法正常使用嘻生活,请升级.")
				.setPositiveButton("升级", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						createProgressDialog();

						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									// 创建文件
									UpgradeFileUtil.createFile(apkName);
									 UpdateService.downloadFile(uphandler,
									 downloadUrl,
									 UpgradeFileUtil.updateFile
									 .toString());
								} catch (Exception e) {
									e.printStackTrace();
									 uphandler
									 .sendEmptyMessage(ApnConstants.DOWN_ERROR);
								}
							}
						}, "upgradeThread").start();
					}
				})
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if (dialog != null) {
							dialog.dismiss();
						}
						// ((MyApplication) getApplication())
						// .ExitApp(WelcomeActivity.this);
						// TODO 退出
						shouldFinishApp = true;
						finish();
					}
				});
		alert.create().show();
	}
	
	private void createProgressDialog() {

		m_pDialog = new ProgressDialog(WelcomeActivity.this);

		// 设置进度条风格
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		// 设置ProgressDialog 标题
		m_pDialog.setTitle("软件升级");

		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage("正在为您下载最新版本的嘻生活...");

		// 设置ProgressDialog 标题图标
		/* m_pDialog.setIcon(R.drawable.ic_launcher); */

		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);

		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);

		// 让ProgressDialog显示
		m_pDialog.show();
	}

	private void updateProgressDialog(int percent) {
		if (null == m_pDialog) {
			return;
		}

		if (percent >= 0 && percent <= 100) {
			m_pDialog.setProgress(percent);

			if (100 == percent) {
				 uphandler.sendEmptyMessage(ApnConstants.DOWN_OK);
			}
		}
	}
	
	private MyHandler uphandler = new MyHandler(this);

	/**
	 * 使用静态的内部类，不会持有当前对象的引用
	 */
	private static class MyHandler extends Handler {
		private final WeakReference<WelcomeActivity> mActivity;

		public MyHandler(WelcomeActivity activity) {
			mActivity = new WeakReference<WelcomeActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			WelcomeActivity activity = mActivity.get();
			if (activity == null || activity.isFinishing())
				return;
			
			MsgCarry mc = (MsgCarry) msg.obj;
			switch (msg.what) {
			case ApnConstants.UPGRADE_PERCENT: {
				activity.updateProgressDialog(mc.getPercent());
			}
				break;

			case ApnConstants.DOWN_OK:
				// 下载完成，点击安装
				if (null != activity.m_pDialog) {
					activity.m_pDialog.dismiss();

					Uri uri = Uri.fromFile(UpgradeFileUtil.updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					activity.startActivity(intent);
				}
				break;

			case ApnConstants.DOWN_ERROR:
				if (null != activity.m_pDialog) {
					activity.m_pDialog.dismiss();
				}
				break;

			default:
				break;
			}

		}
	}
	
	@Override
	public void finish() {
		uphandler = null;
		if (!shouldFinishApp) {
			Intent intent = new Intent(this,
					MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(0, 0);
		}
		super.finish();
	}
}
