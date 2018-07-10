package com.femto.post.activity;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONObject;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.hyphenate.chat.EMClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mob.tools.utils.UIHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Login extends BaseActivity implements Callback,PlatformActionListener {
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR= 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	
	private RelativeLayout rl_left;
	private TextView tv_title, tv_regist_login, tv_login, tv_forgetpd;
	private EditText ed_phone, ed_password;
	private RelativeLayout weChat_login;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
			break;
		case R.id.tv_regist_login:
			openActivity(Activity_Regist.class, null);
			break;
		case R.id.tv_login:
			Login();
			break;
		case R.id.tv_forgetpd:
			openActivity(Activity_FindPassWord.class, null);
			break;
		case R.id.weChat_login:
			weChatLogin();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_regist_login = (TextView) findViewById(R.id.tv_regist_login);
		tv_login = (TextView) findViewById(R.id.tv_login);
		tv_forgetpd = (TextView) findViewById(R.id.tv_forgetpd);
		ed_password = (EditText) findViewById(R.id.ed_password);
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		weChat_login = (RelativeLayout)findViewById(R.id.weChat_login);
		weChat_login.setOnClickListener(this);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_left.setOnClickListener(this);
		tv_login.setOnClickListener(this);
		tv_forgetpd.setOnClickListener(this);
		tv_regist_login.setOnClickListener(this);
		tv_title.setText("登录");
	}

	@Override
	public void setContentView() {
		ShareSDK.initSDK(this);
		setContentView(R.layout.activity_login);
		MyApplication.addActivity(this);
	}

	private void Login() {
		RequestParams params = new RequestParams();
		String name = ed_phone.getText().toString().trim();
		final String password = ed_password.getText().toString().trim();
		if(TextUtils.isEmpty(name)||TextUtils.isEmpty(password)) {
			Toast.makeText(Activity_Login.this, "用户名或密码不能为空", 0).show();
			return;
		}
		params.put("name", name);
		params.put("password", password);
		params.put("token", MyApplication.token);
		params.put("ios", 0);
		showProgressDialog("登录中");
		MyApplication.ahc.post(AppUrl.useruserlogin, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						LogUtils.i("response==="+response.toString());
						String message = response.optString("message");
						LogUtils.i("message==="+message);
						String result = response.optString("result");
						if (result.equals("0")) {
							/****************************/
							//int userId = response.optInt("userId");
							String userId = response.optString("userId");
							String userName = response.optString("userName");
							String phone = response.optString("phone");
							String url = response.optString("url");
							String name = response.optString("name");
							SharedPreferences sp = getSharedPreferences(
									"login", Context.MODE_PRIVATE);
							Editor edit = sp.edit();
							//edit.putInt("userId", userId);
							edit.putString("userId", userId);
							edit.putString("userName", userName);
							edit.putString("phone", phone);
							edit.putString("name", name);
							edit.putString("url", url);
							edit.putBoolean("islogin", true);
							edit.putString("password",password);
							edit.commit();
							MyApplication.getBasicInformation();
							openActivity(MainActivity.class, null);
							finish();
						}
						showToast("" + message, 0);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						super.onFailure(statusCode, headers, responseString, throwable);
						dismissProgressDialog();
						Toast.makeText(Activity_Login.this, "登录失败", Toast.LENGTH_SHORT).show();
					}
				});
	}
	
	//微信登录
	private void weChatLogin() {
		Platform weChat = ShareSDK.getPlatform(this,Wechat.NAME);
		LogUtils.i("weChat==="+weChat.getName());
		authorize(weChat);
	}
	
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	/**
	 * 微信登录相关
	 * @param plat
	 */
	private void authorize(Platform plat) {	
		LogUtils.i("isValid==="+plat.isAuthValid());
		if(plat.isAuthValid()) {
			LogUtils.i("palt==="+plat.getName());
			String userId = plat.getDb().getUserId();
			Log.i("test2", "userId"+userId);
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
				login(plat.getName(), userId, null);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	private void login(String plat, String userId, HashMap<String, Object> userInfo) {
		LogUtils.i("login=======");
		Message msg = new Message();
		msg.what = MSG_LOGIN;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}
	
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
			login(platform.getName(), platform.getDb().getUserId(), res);
		}
		System.out.println(res);
		System.out.println("------User Name ---------" + platform.getDb().getUserName());
		System.out.println("------User ID ---------" + platform.getDb().getUserId());
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}
	
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_USERID_FOUND: {
				Toast.makeText(this, "用户信息已存在,正在执行跳转操作...", Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_LOGIN: {
				
			//	String text = getString("使用%s帐号登录中…", msg.obj);
				String text = "第三方账号登录中";
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				System.out.println("---------------");
				
//				Builder builder = new Builder(this);
//				builder.setTitle(R.string.if_register_needed);
//				builder.setMessage(R.string.after_auth);
//				builder.setPositiveButton(R.string.ok, null);
//				builder.create().show();
			}
			break;
			case MSG_AUTH_CANCEL: {
				Toast.makeText(this, "授权取消", Toast.LENGTH_SHORT).show();
				System.out.println("-------MSG_AUTH_CANCEL--------");
			}
			break;
			case MSG_AUTH_ERROR: {
				Toast.makeText(this, "授权错误", Toast.LENGTH_SHORT).show();
				System.out.println("-------MSG_AUTH_ERROR--------");
			}
			break;
			case MSG_AUTH_COMPLETE: {
				Toast.makeText(this, "授权成功，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
				System.out.println("--------MSG_AUTH_COMPLETE-------");
			}
			break;
		}
		return false;
	}
}
