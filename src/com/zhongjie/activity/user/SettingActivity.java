package com.zhongjie.activity.user;

import java.io.File;
import java.text.DecimalFormat;

import CheckVersionModel.CheckVersionJson;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongjie.ApnConstants;
import com.zhongjie.R;
import com.zhongjie.activity.BaseSecondActivity;
import com.zhongjie.model.MsgCarry;
import com.zhongjie.service.UpdateService;
import com.zhongjie.util.CommonRequest;
import com.zhongjie.util.Logger;
import com.zhongjie.util.UpgradeFileUtil;
import com.zhongjie.view.CommonLoadingDialog;

public class SettingActivity extends BaseSecondActivity {

	private View mCleanCache, mGoAboutUs, mGoServiceAgreement, mCheckUpdate;
	private TextView mTotalCache;
	public ProgressDialog m_pDialog;
	private CommonRequest mRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		mRequest = new CommonRequest(getApplicationContext());
	}

	@Override
	protected void findViews() {
		mCleanCache = findViewById(R.id.act_setting_clean_img_cache);
		mTotalCache = (TextView) findViewById(R.id.act_setting_total_cache);
		mGoAboutUs = findViewById(R.id.act_setting_aboutUs);
		mGoServiceAgreement = findViewById(R.id.act_setting_service_agreement);
		mCheckUpdate = findViewById(R.id.act_setting_checkUpdate);
	}

	private String getTotalCache() {
		ImageLoader loader = ImageLoader.getInstance();
		File f = loader.getDiscCache().getCacheDir();
		long size = 0;
		if (f.exists() && f.isDirectory()) {
			for (File f2 : f.listFiles()) {
				if (f2.isFile())
					size += f2.length();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(size / 1024 / 1024);
	}

	@Override
	protected void initViews() {
		mTopLeftImg.setImageResource(R.drawable.ic_top_back);
		mTopLeftImg.setVisibility(View.VISIBLE);
		mTopCenterTxt.setText("设置");
		mTopCenterTxt.setVisibility(View.VISIBLE);
		mTotalCache.setText(getTotalCache() + "M");
		mCleanCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Void>() {
					CommonLoadingDialog cld = null;
					protected void onPreExecute() {
						cld = CommonLoadingDialog.create(SettingActivity.this);
						cld.show();
					};

					@Override
					protected Void doInBackground(Void... params) {
						ImageLoader.getInstance().clearDiscCache();
						return null;
					}
					
					protected void onPostExecute(Void result) {
						if(null != cld)
							cld.cancel();
						cld = null;
						mTotalCache.setText(getTotalCache() + "M");
					};
					
				}.execute();
				
			}
		});
		mGoAboutUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
				intent.putExtra("url", "http://appserver.vbangke.com/resource/about/aboutus.html");
				intent.putExtra("title", "关于我们");
				startActivity(intent);
			}
		});
		mGoServiceAgreement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
				intent.putExtra("url", "http://appserver.vbangke.com/resource/about/agreement.html");
				intent.putExtra("title", "服务协议");
				startActivity(intent);
			}
		});
		mCheckUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new UpgradeTask().execute();
			}
		});
	}
	
	/********* 版本检查更新 ***********/
	public class UpgradeTask extends AsyncTask<Void, Void, CheckVersionJson> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected CheckVersionJson doInBackground(Void... params) {
			
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

			if (null == this || isFinishing())
				return;

			Message msg = new Message();
			if (null != cvr && null != cvr.data && cvr.data.updateFlag != 0) {

				if (cvr.data.updateFlag == 2) {
					msg.what = ApnConstants.UPGRADE_FORCE;
				} else if(cvr.data.updateFlag == 1){
					msg.what = ApnConstants.UPGRADE_ASK;
				}

				msg.obj = new MsgCarry(cvr.data.versionCode, cvr.data.updateUrl, 0);

			} else {
				msg.what = ApnConstants.UPGRADE_NORMAL;
			}
			uphandler.sendMessage(msg);
		}
	}
	
	private void createProgressDialog() {

		m_pDialog = new ProgressDialog(SettingActivity.this);

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

	/**
	 * 更新UI
	 */
	final Handler uphandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			MsgCarry mc = (MsgCarry) msg.obj;
			switch (msg.what) {
			case ApnConstants.UPGRADE_ASK:
				askVersionUpdate(mc.getServerVersion(), mc.getDownloadUrl());
				break;

			case ApnConstants.UPGRADE_FORCE:
				break;

			case ApnConstants.UPGRADE_PERCENT: {
				updateProgressDialog(mc.getPercent());
			}
				break;

			case ApnConstants.DOWN_OK:
				// 下载完成，点击安装
				if (null != m_pDialog) {
					m_pDialog.dismiss();

					Uri uri = Uri.fromFile(UpgradeFileUtil.updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					startActivity(intent);
				}
				break;

			case ApnConstants.DOWN_ERROR:
				if (null != m_pDialog) {
					m_pDialog.dismiss();
				}
				break;

			case ApnConstants.UPGRADE_NORMAL:
				Toast.makeText(getApplicationContext(), "当前已是最新版本",
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}

		}

	};

	/***
	 * 询问是否更新版本
	 */
	private void askVersionUpdate(final String serverVersion,
			final String downloadUrl) {

		final String apkName = getApkName(serverVersion);
		// 发现新版本，提示用户更新
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
										getApplicationContext(),
										UpdateService.class);
								updateIntent.putExtra("apk_name", apkName);
								updateIntent.putExtra("download_url",
										downloadUrl);
								startService(updateIntent);
							}
						})
				.setNegativeButton("下次再说",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		alert.create().show();
	}

	private String getApkName(String version) {
		return getResources().getString(R.string.apk_name_prefix) + version;
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

}
