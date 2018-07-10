package com.femto.post.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.femto.post.customview.CustomProgressDialog;

public abstract class BaseActivity extends FragmentActivity implements
		OnClickListener {
	private CustomProgressDialog pd = null;
	// private WakeLock mWakeLock;
	private final static String CLASS_LABEL = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
		// getWindow().getDecorView().setSystemUiVisibility(
		// View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// }

		// // 沉浸模式
		// if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		// // 状态栏
		// getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// // 虚拟按键
		// //
		// getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// }
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// // //
		// getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
		// WindowManager.LayoutParams.TYPE_STATUS_BAR);
		setContentView();
		initView();
		Control();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
		// CLASS_LABEL);
		// mWakeLock.acquire();
	}

	/**
	 * 初始化view
	 */
	public abstract void initView();

	/**
	 * 初始化工具
	 */
	public abstract void initUtils();

	/**
	 * 设置view
	 */
	public abstract void Control();

	/**
	 * 加载布局
	 */
	public abstract void setContentView();

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	}

	/**
	 * 显示加载框
	 */
	public void showProgressDialog(String title) {
		if (pd == null) {
			pd = CustomProgressDialog.createDialog(this);
			pd.setMessage(title);
		}
		pd.setCanceledOnTouchOutside(false);
		pd.show();
	}

	/**
	 * 取消加载框
	 */
	public void dismissProgressDialog() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	/**
	 * Activity跳转
	 * 
	 * @param cls
	 * @param bundle
	 */
	public void openActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtra("bundle", bundle);
		}
		startActivity(intent);
	}

	/* 打开动画 */
	public void openAnimationActivity(Class<?> cls, String anim, boolean loop,
			String audio) {
		Intent intent = new Intent();
		intent.putExtra("anim", anim);
		intent.putExtra("loop", loop);
		intent.putExtra("audio", audio);
		intent.setClass(this, cls);
		startActivity(intent);
	}

	/**
	 * 获取String资源
	 * 
	 * @param id
	 */
	public String getStringById(int id) {

		return getResources().getString(id);
	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// if (mWakeLock == null) {
	// // 获取唤醒锁,保持屏幕常亮
	// PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	// mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
	// CLASS_LABEL);
	// mWakeLock.acquire();
	// }
	// }

	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	//
	// if (mWakeLock != null) {
	// mWakeLock.release();
	// mWakeLock = null;
	// }
	//
	// }
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	// if (mWakeLock != null) {
	// mWakeLock.release();
	// mWakeLock = null;
	// }
	// }

	/**
	 * Toast显示
	 * 
	 * @param text
	 * @param timeTag
	 *            0:short
	 */
	public void showToast(String text, int timeTag) {
		if (timeTag == 0) {
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}
	}

	public int getWith() {
		return getWindowManager().getDefaultDisplay().getWidth();
	}

	public int getHight() {
		return getWindowManager().getDefaultDisplay().getHeight();
	}

	public int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public void setAppType(String typekey, int type) {
		SharedPreferences sp = getSharedPreferences("apptype",
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt("" + typekey, type);
		edit.commit();
	}

	public int getAppType(String typekey) {
		SharedPreferences sp = getSharedPreferences("apptype",
				Context.MODE_PRIVATE);
		return sp.getInt(typekey, 0);
	}

	public void call(String phonenub) {
		// 用intent启动拨打电话
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phonenub));
		startActivity(intent);
	}

	public List<ImageView> getImageViews(List<Integer> datas) {
		List<ImageView> is = new ArrayList<ImageView>();
		for (int i = 0; i < datas.size(); i++) {
			ImageView image = new ImageView(this);
			image.setScaleType(ScaleType.CENTER_CROP);
			image.setImageResource(datas.get(i));
			is.add(image);
		}
		return is;
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public boolean isAvilible(Context context, String packageName) {
		// 获取packagemanager
		final PackageManager packageManager = context.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		// 用于存储所有已安装程序的包名
		List<String> packageNames = new ArrayList<String>();
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String packName = packageInfos.get(i).packageName;
				packageNames.add(packName);
			}
		}
		// 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
		return packageNames.contains(packageName);
	}
}
