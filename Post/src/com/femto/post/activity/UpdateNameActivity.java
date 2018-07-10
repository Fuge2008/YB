package com.femto.post.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateNameActivity extends BaseActivity {
	
	private TextView tv_title;
	private TextView tv_right_genggai;
	private ImageView im_left;
	private EditText et_name;
	private int requestCode = 5;
	
	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView() {
		Log.i("test", "initView");
		tv_title = (TextView) findViewById(R.id.tv_title);
		Log.i("test", tv_title.toString());
		tv_title.setText("个人昵称");
		tv_right_genggai = (TextView) findViewById(R.id.tv_right_genggai);
		tv_right_genggai.setVisibility(View.VISIBLE);
		im_left = (ImageView)findViewById(R.id.im_left);
		et_name = (EditText)findViewById(R.id.et_name);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		im_left.setOnClickListener(listener);
		tv_right_genggai.setOnClickListener(listener);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_update_name);
	}

	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.im_left:
				UpdateNameActivity.this.finish();
				break;
			case R.id.tv_right_genggai:
				sendUserNameToNet();
			default:
				break;
			}
		}

		private void sendUserNameToNet() {
			String userName = et_name.getText().toString();
			if(TextUtils.isEmpty(userName.trim())) {
				Toast.makeText(UpdateNameActivity.this, "昵称不能为空", 0).show();
				return;
			} 
			showProgressDialog("提交个人信息中...");
			com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
			params.addBodyParameter("id", MyApplication.userId);
			Log.i("test3","userId==="+MyApplication.userId);
			params.addBodyParameter("username", userName);
			Log.i("test3", userName);
			RequestCallBack<String> callBack = new RequestCallBack<String>() {
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					dismissProgressDialog();
					try {
						JSONObject jsonObject = new JSONObject(responseInfo.result);
						String result = jsonObject.getString("result");
						if(result.equals("0")) {
							String username = jsonObject.getString("username");
							SharedPreferences sp = getSharedPreferences(
									"login", Context.MODE_PRIVATE);
							Editor edit = sp.edit();
							//edit.putInt("userId", userId);
							edit.putString("username", username);
							edit.commit();
							MyApplication.getBasicInformation();
							Intent intent = new Intent();
							setResult(requestCode, intent);
							UpdateNameActivity.this.finish();
							Toast.makeText(UpdateNameActivity.this, "修改信息成功", 0).show();
						} else {
							Toast.makeText(UpdateNameActivity.this, "修改信息失败", 0).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(UpdateNameActivity.this, "修改信息失败", 0).show();
					}
					
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					dismissProgressDialog();
					Toast.makeText(UpdateNameActivity.this, "网络异常", 0).show();
				}

			};
			new HttpUtils().send(HttpMethod.POST, AppUrl.updateUserInfo, params,callBack);
		}
	};
}
