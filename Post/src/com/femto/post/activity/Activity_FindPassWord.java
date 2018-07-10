package com.femto.post.activity;

import org.apache.http.Header;
import org.json.JSONObject;

import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * * @author 作者 Deep @date 创建时间：2016年3月29日 下午3:03:54 * @version 1.0 * @parameter
 * * @since * @return
 */
public class Activity_FindPassWord extends BaseActivity {
	private RelativeLayout rl_left;
	private TextView tv_title, tv_regist_f, tv_getcode_f;
	private EditText ed_phonenub_f, ed_code_f, ed_password_f, ed_password_rf;
	private boolean isSend;
	private int i;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (i == 0) {
					tv_getcode_f.setText("重新发送");
					tv_getcode_f.setTextColor(getResources().getColor(
							R.color.black));
					isSend = false;
				} else {
					tv_getcode_f.setTextColor(getResources().getColor(
							R.color.sblack));
					tv_getcode_f.setText("(" + (i--) + ")发送验证码");
					sendCond();
				}
				break;

			default:
				break;
			}
		};
	};

	private void sendCond() {
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
			break;
		case R.id.tv_regist_f:
			if (ed_phonenub_f.getText().toString().trim().length() != 11) {
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_code_f.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_password_f.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!ed_password_f.getText().toString().trim()
					.equals(ed_password_rf.getText().toString().trim())) {
				Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			findPd();
			break;
		case R.id.tv_getcode_f:
			if (isSend) {

			} else {
				if (ed_phonenub_f.getText().toString().length() != 11) {
					Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				getcode();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_regist_f = (TextView) findViewById(R.id.tv_regist_f);
		tv_getcode_f = (TextView) findViewById(R.id.tv_getcode_f);
		ed_phonenub_f = (EditText) findViewById(R.id.ed_phonenub_f);
		ed_code_f = (EditText) findViewById(R.id.ed_code_f);
		ed_password_f = (EditText) findViewById(R.id.ed_password_f);
		ed_password_rf = (EditText) findViewById(R.id.ed_password_rf);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_left.setOnClickListener(this);
		tv_getcode_f.setOnClickListener(this);
		tv_regist_f.setOnClickListener(this);
		tv_title.setText("忘记密码");
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.acitivity_findpassword);
		MyApplication.addActivity(this);
	}

	// 获取验证码
	private void getcode() {
		RequestParams params = new RequestParams();
		params.put("telephone", ed_phonenub_f.getText().toString());
		handler.sendEmptyMessage(1);
		isSend = true;
		i = 60;
		MyApplication.ahc.post(AppUrl.userpassWordSendCode, params,
				new JsonHttpResponseHandler() {
					@SuppressWarnings("unused")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						System.out.println("zuo=验证码==" + response.toString());
						int result = response.optInt("result");
						String message = response.optString("message");
						Toast.makeText(Activity_FindPassWord.this,
								"" + message, Toast.LENGTH_SHORT).show();
					}
				});
	}

	// 找回
	private void findPd() {
		RequestParams params = new RequestParams();
		params.put("name", ed_phonenub_f.getText().toString().trim());
		params.put("password", ed_password_f.getText().toString().trim());
		params.put("code", ed_code_f.getText().toString().trim());
		showProgressDialog("正在提交...");
		MyApplication.ahc.post(AppUrl.userfindPassByCode, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						String result = response.optString("result");
						if(result.equals("0")) {
							Toast.makeText(Activity_FindPassWord.this, "修改密码成功", 0).show();
							Activity_FindPassWord.this.finish();
						} else {
							Toast.makeText(Activity_FindPassWord.this, "修改失败", 0).show();
						}
						//String message = response.optString("message");
						/*Toast.makeText(Activity_FindPassWord.this, message,
								Toast.LENGTH_SHORT).show();*/
					}

				});
	}

}
