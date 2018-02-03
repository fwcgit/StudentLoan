package com.studentloan.white;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.studentloan.white.crash.CrashHandler;
import com.studentloan.white.db.BaseDataDB;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.data.UserInfo;
import com.studentloan.white.utils.SdUtil;
import com.studentloan.white.utils.SystemBarTintManager;
import com.studentloan.white.utils.SystemOpt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 扩展application
 * @author fu
 *
 */
public class MyApplication extends Application {
	public int heightPixels = 800;
	public int widthPixels = 480;
	public SystemOpt systemOpt = SystemOpt.getInstance();
	
	public static MainActivity mainActivity;
	
	public UserInfo userInfo;//登陆信息

	public static boolean isForeground  = false;
	public static Activity activity;
	public static boolean isPushMsg = false;
	public static boolean isLogin = false;
	
	private static MyApplication application;
	public static MyApplication getInstance(){
		return application;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		//JPushInterface.setDebugMode(false);
		//JPushInterface.init(getApplicationContext());
		//com.netease.nis.bugrpt.CrashHandler.init(this.getApplicationContext());
		initSystem(); 
	}

	@SuppressWarnings("deprecation")
	public void setSystemBar(Activity activity){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(activity,true);
			SystemBarTintManager tintManager = new SystemBarTintManager(activity);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(activity.getResources().getColor(R.color.bg));//通知栏所需颜色
//			tintManager.setStatusBarAlpha(0f);
			tintManager.statusTextColor(activity,true);
		}
	}
	
	@TargetApi(19) 
	public void setTranslucentStatus(Activity activity, boolean on) {
		Window win = activity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	
	public void initSystem() {
		systemOpt.init(this);//读取系统信息
		
		NoHttpRequest.getInstance().init(this);
		
		widthPixels = systemOpt.widthPixels;
		heightPixels = systemOpt.heightPixels;
		
		initImageLoader(this);

		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
		
	}
	
	/***
	 * 密码规则
	 * @param password
	 * @return
	 */
	public  boolean isValidPassword(String password){
		if(password.isEmpty()) return false;
	    String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(password);
	    return matcher.matches();
	}
	
	public boolean isTelPhone(String phone){
		if(phone.isEmpty()) return false;
		String regex = "^(1(([3587][0-9])|(47)|[8][012356789]))\\d{8}$";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}
	
	public boolean isEmail(String email){
		if(email.isEmpty()) return false;
		String regex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	
	/** 初始化ImageLoader */
	@SuppressWarnings("deprecation")
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, SdUtil.IMG_CAHCE);//获取到缓存的目录地址
		//创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024/2);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				//.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
				//.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(maxMemory)) // You can pass your own memory cache implementation你可以通过自己的内存缓存实现
				//.memoryCacheSize(2 * 1024 * 1024)  
				///.discCacheSize(50 * 1024 * 1024)  
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
				//.discCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.discCacheFileCount(100) //缓存的File数量
				.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
				//.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				//.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);//全局初始化此配置
	}

	public void initBaseDb(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				copyDbToSD();
				BaseDataDB.getInstance().init();
			}
		}).start();
	}
	/***
	 * 复制数据库到SD卡
	 */
	public void copyDbToSD() {
		if (SdUtil.getFilePath(SdUtil.DATA_DIR, "studentloan_2.db").exists() ) {
			try {
				if(SdUtil.getFilePath(SdUtil.DATA_DIR, "studentloan_2.db").length() == this.getAssets().open("studentloan_2.db").available()) {
                    return;
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = this.getAssets().open("studentloan_2.db");
			fos = new FileOutputStream(SdUtil.getFilePath(SdUtil.DATA_DIR,
					"studentloan_2.db"));

			byte[] buffer = new byte[1024 * 4];
			int temp = -1;
			while ((temp = is.read(buffer)) > 0) {
				fos.write(buffer, 0, temp);
			}
			fos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	 public void reLogin(){

		MyApplication.isLogin = false;

		com.studentloan.white.mode.activity.LoginActivity_.intent(MyApplication.mainActivity).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();

		sendBroadcast(new Intent("close"));

		if(MyApplication.mainActivity != null){

			MyApplication.mainActivity.finish();
		}

	 }

	 public UserInfo getUserInfo(){
		 if(null == userInfo){
			 reLogin();
			 return null;
		 }
		 return userInfo;
	 }
}
