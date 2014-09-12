package com.zhongjie;

import java.io.File;
import java.security.KeyStore;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DaoConfig;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zhongjie.global.Session;
import com.zhongjie.util.Constants;
import com.zhongjie.util.SSLSocketFactoryEx;
import com.zhongjie.util.ShopCartManager;

public class MyApplication extends Application{

	private HttpClient httpClient;
	private final String TAG = "MyApplication";
	public static FinalDb finalDb;
	public final static String DB_NAME = "Xi_life.db"; 
	private static Context instance;

	 @Override
	 public void onCreate() {
		 super.onCreate();
		 instance = getApplicationContext();
		 createHttpClient();
		 initImageLoader(this);
		 createDirs();
	 }
	 
	 public static final Context getMyApplicationContext() {
			return instance;
		}
	 
	 @SuppressWarnings("unused")
	private void init(){
		 /**
			 * 创建数据库
			 */
			DaoConfig dc = new DaoConfig();
			File f = new File(Constants.APP_DB_DIR);
			if(!f.exists())
				f.mkdirs();
			f = null;
			dc.setTargetDirectory(Constants.APP_DB_DIR);
			dc.setContext(this);
			dc.setDbName(DB_NAME);
			finalDb = FinalDb.create(dc);
	 }


	@Override
	public void onLowMemory() {
		super.onLowMemory();

		new Thread(new Runnable() {
			@Override
			public void run() {
				shutdownHttpClient();
			}
		}).start();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		new Thread(new Runnable() {
			@Override
			public void run() {
				shutdownHttpClient();
			}
		}).start();
	}
	
	private void createDirs() {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File sdcard = Environment.getExternalStorageDirectory();
			if (sdcard.exists()) {
				File baseDirectory = new File(Constants.APP_DIR);
				if (!baseDirectory.exists())
					baseDirectory.mkdirs();
				System.out.println("baseDirectory : " + baseDirectory.getPath());
			} else {
				Log.e(this.getClass().getName(), "sdcard not use!");
			}
		} else {
			// File flash = new File("/flash");
			// File flash = getFilesDir();
		}
	}

	private final int timeout = 10 * 1000;

	// 创建HttpClient实例
	private HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.useExpectContinue(params);
		ConnManagerParams.setTimeout(params, timeout);
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, true);
		HttpProtocolParams.setUserAgent(params, MyApplication.class.getName());
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schReg.register(new Scheme("https", sf, 443));
		} catch (Exception e) {

			Log.e(TAG, "创建httpclient 发生错误", e);
		}

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}

	// 关闭连接管理器并释放资源
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
	}
	
	private  void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCache(new UnlimitedDiscCache(StorageUtils.getOwnCacheDirectory(context, Constants.APP_IMAGE)))
				.discCacheSize(200*1024*1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2*1024*1024)
				.threadPoolSize(3)
//				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	// 对外提供HttpClient实例
	public HttpClient getHttpClient() {

		if (httpClient == null) {
			httpClient = createHttpClient();
		}

		return httpClient;
	}
	
	public void exitApp() {
		ShopCartManager.getInstance().save(getApplicationContext());
		Session.getSession().cleanUpSession();
		shutdownHttpClient();
	    Process.killProcess(Process.myPid());
	    System.exit(0);
	}
}
