package com.femto.post.application;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easemob.redpacketsdk.RedPacket;
import com.femto.post.R;
import com.femto.post.activity.MainActivity;
import com.femto.post.entity.Quotation;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

public class MyApplication extends Application {
	public static Context applicationContext;
	public static AsyncHttpClient ahc = new AsyncHttpClient();
	private static List<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;
	public static boolean ispress = false;
	public static ArrayList<Quotation> quotation;
	public static DecimalFormat df = new DecimalFormat("######0.00");
	public static ArrayList<Quotation> qs = null;
	public static String username;
	//public static int userId;
	public static String userId;
	public static boolean islogin;
	public static String name;
	public static String url;
	public static String phone;
	public static String password;
	//获取的信鸽推送的token
	public static String token;
	//环信是否登录的变量...
	public static Boolean IsLoginHuanxin = false;
	/*环信变量...。。。*/
	// login user name
	public final String PREF_USERNAME = "username";
		
		/**
		 * 当前用户nickname,为了苹果推送不是userid而是昵称
		 */
	public static String currentUserNick = "";
	//环信用户是否可以发言
	public static boolean isUserSpeak = false;
	@Override
	public void onCreate() {
		MultiDex.install(this);
		super.onCreate();
		// IntentFilter filter = new IntentFilter(
		// "android.hardware.usb.action.USB_DEVICE_DETACHED");
		// registerReceiver(mUsbReceiver, filter);
		// manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		Context context = getApplicationContext();
		Log.i("test", "初始化信鸽推送");
		XGPushManager.registerPush(getApplicationContext(),
				new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {
				Log.i("TPush", "注册成功，设备token为：" + data);
				token = data+"";
				//longing(username,userpassword);
			}
			@Override
			public void onFail(Object data, int errCode, String msg) {
				Log.i("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
				//如果注册失败  token值设为null
				token = null;
				//longing(username,userpassword);
		}
		});
		instance = this;
		applicationContext = this;
		initImageLoader(applicationContext);
		// initUSB();
		getBasicInformation();
		ahc.setTimeout(3000);
		/*初始化环信*/
		EMOptions options = new EMOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAutoLogin(true);
		options.setAcceptInvitationAlways(false);
		Context appContext = this;
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果APP启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
		 
		if (processAppName == null ||!processAppName.equalsIgnoreCase(appContext.getPackageName())) {
		    Log.e("test", "enter the service process!");
		    // 则此application::onCreate 是被service 调用的，直接返回
		    return;
		}
		//初始化
		EMClient.getInstance().init(applicationContext, options);
		//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
		EMClient.getInstance().setDebugMode(true);
		//init demo helper
		DemoHelper.getInstance().init(applicationContext);
		RedPacket.getInstance().initContext(applicationContext);
	}

	public static MyApplication getinstance() {
		return instance;
	}

	public static void initImageLoader(Context context) {
		File cacheDir;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			System.out.println("zuo===SD卡存在");
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/cardImages";
			File path1 = new File(path);
			cacheDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/cardImages");
			if (!cacheDir.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				cacheDir.mkdirs();
			}

		} else {
			cacheDir = StorageUtils.getOwnCacheDirectory(context,
					"imageloader/Cache");
		}
		// diskCacheExtraOptions
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// maxwidth, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)

				// default = device screen dimensions
				// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
				// null)
				// 线程池内加载的数量
				.threadPriority(2)
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 *
				// 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				// .memoryCacheSize(5 * 1024 * 1024)
				.discCacheSize(200 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(1000)
				// 缓存的文件数量
				.discCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout
																				// (5
																				// s),
																				// readTimeout
																				// (30
																				// s)超时时间
				.writeDebugLogs() // Remove for releaseapp
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}

	// 获取ImageLoader Options
	public static DisplayImageOptions getOptions(int dra) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(dra) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(dra) // image连接地址为空时
				.showImageOnFail(dra) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		return options;
	}

	/**
	 * 判断某个界面是否在前台
	 * 
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	public static boolean isForeground(Context context) {
		if (context == null
				|| TextUtils.isEmpty("org.cocos2dx.lua.AppActivity")) {
			return false;
		}
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if ("org.cocos2dx.lua.AppActivity".equals(cpn.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// 判断某个activity是否在前台
	public static boolean ActivityIscurrent(Context context, String pakename) {
		if (context == null || TextUtils.isEmpty(pakename)) {
			return false;
		}
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (pakename.equals(cpn.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// 判断网络是否连接
	public static boolean isConnectNetwork(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.d("error", e.toString());
		}
		Toast.makeText(applicationContext, "网络无法连接!", Toast.LENGTH_SHORT)
				.show();
		return false;
	}

	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public static void exit() {

		for (Activity a : activityList) {
			a.finish();
		}

	}

	public static void getBasicInformation() {
		SharedPreferences sp = applicationContext.getSharedPreferences("login",
				Context.MODE_PRIVATE);
		username = sp.getString("username", "");
		name = sp.getString("name", "");
		userId = sp.getString("userId", null);
		//userId = sp.getInt("userId", 0);
		islogin = sp.getBoolean("islogin", false);
		url = sp.getString("url", null);
		password = sp.getString("password", null);
		sp.getString("phone", null);
	}
	
	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}
	//环信Demo的方法....
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
