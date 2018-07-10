package com.femto.post.activity;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Activity_Regist extends BaseActivity {
	private RelativeLayout rl_back;
	private TextView tv_acttitle, tv_getcode, tv_regist;
	private EditText ed_phonenub, ed_code, ed_password_regist;
	private int i = 60;
	private boolean isSend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (i == 0) {
					tv_getcode.setText("重新发送");
					tv_getcode.setTextColor(getResources().getColor(
							R.color.black));
					isSend = false;
				} else {
					tv_getcode.setTextColor(getResources().getColor(
							R.color.sblack));
					tv_getcode.setText("(" + (i--) + ")发送验证码");
					sendCond();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
			break;
		case R.id.tv_getcode:
			if (isSend) {

			} else {
				if (ed_phonenub.getText().toString().length() != 11) {
					Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				getcode();
			}
			break;
		case R.id.tv_regist:
			if (ed_phonenub.getText().toString().trim().length() != 11) {
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_code.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_password_regist.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				return;
			}
			regist();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back = (RelativeLayout) findViewById(R.id.rl_left);
		tv_acttitle = (TextView) findViewById(R.id.tv_title);
		tv_getcode = (TextView) findViewById(R.id.tv_getcode);
		tv_regist = (TextView) findViewById(R.id.tv_regist);
		ed_phonenub = (EditText) findViewById(R.id.ed_phonenub);
		ed_code = (EditText) findViewById(R.id.ed_code);
		ed_password_regist = (EditText) findViewById(R.id.ed_password_regist);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		rl_back.setOnClickListener(this);
		tv_regist.setOnClickListener(this);
		tv_getcode.setOnClickListener(this);
		tv_acttitle.setText("注册");
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_regist);
		MyApplication.addActivity(this);
	}

	private void sendCond() {
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	// 注册
	private void regist() {
		RequestParams params = new RequestParams();
		params.put("user.name", ed_phonenub.getText().toString().trim());
		params.put("user.passWord", ed_password_regist.getText().toString()
				.trim());
		params.put("code", ed_code.getText().toString().trim());
		showProgressDialog("正在注册...");
		MyApplication.ahc.post(AppUrl.userregist, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						String result = response.optString("result");
						String message = response.optString("message");
						getII(result);
						if (result.equals("0")) {
							Login();
						}
						Toast.makeText(Activity_Regist.this, message,
								Toast.LENGTH_SHORT).show();
					}

				});
	}

	private void getII(String result) {
		
	}

	// 获取验证码
	private void getcode() {
		RequestParams params = new RequestParams();
		params.put("user.name", ed_phonenub.getText().toString());
		handler.sendEmptyMessage(1);
		isSend = true;
		i = 60;
		MyApplication.ahc.post(AppUrl.usersendCode, params,
				new JsonHttpResponseHandler() {
					@SuppressWarnings("unused")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						System.out.println("zuo=验证码==" + response.toString());
						int result = response.optInt("result");
						String message = response.optString("message");
						Toast.makeText(Activity_Regist.this, "" + message,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	// 登录
	private void Login() {
		RequestParams params = new RequestParams();
		params.put("name", ed_phonenub.getText().toString().trim());
		params.put("password", ed_password_regist.getText().toString().trim());
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
						String message = response.optString("message");
						String result = response.optString("result");
						if (result.equals("0")) {
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
							edit.commit();
							MyApplication.getBasicInformation();
							openActivity(MainActivity.class, null);
							finish();
						}
						showToast("" + message, 0);
					}
				});
	}
}
