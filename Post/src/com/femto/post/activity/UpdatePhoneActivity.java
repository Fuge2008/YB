package com.femto.post.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class UpdatePhoneActivity extends BaseActivity {

	private TextView tv_title;
	private TextView tv_right_genggai;
	private ImageView im_left;
	private EditText et_phone;
	private int RESULT_CODE = 1;

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("手机号码");
		tv_right_genggai = (TextView) findViewById(R.id.tv_right_genggai);
		tv_right_genggai.setVisibility(View.VISIBLE);
		im_left = (ImageView)findViewById(R.id.im_left);
		et_phone = (EditText)findViewById(R.id.et_phone);
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
		setContentView(R.layout.activity_update_phone);
	}

	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.im_left:
				UpdatePhoneActivity.this.finish();
				break;
			case R.id.tv_right_genggai:
				sendPhoneToNet();
				break;
			default:
				break;
			}
		}

		private void sendPhoneToNet() {
			final String phone = et_phone.getText().toString().trim();
			if(phone.length()!=11) {
				Toast.makeText(UpdatePhoneActivity.this, "请输入正确的手机号！", 0).show();
				return;
			}
			showProgressDialog("提交信息中");
			RequestParams params = new RequestParams();
			params.addBodyParameter("user.id", MyApplication.userId);
			params.addBodyParameter("user.phone", phone);
			RequestCallBack<String> callBack = new RequestCallBack<String>() {


				@Override
				public void onFailure(HttpException arg0, String arg1) {
					dismissProgressDialog();
					Toast.makeText(UpdatePhoneActivity.this, "网络连接异常", 0).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					dismissProgressDialog();
					try {
						JSONObject jsonObject = new JSONObject(responseInfo.result);
						String result = jsonObject.getString("result");
						if(result.equals("0")) {
							Toast.makeText(UpdatePhoneActivity.this, "提交手机信息成功", 0).show();
							SharedPreferences sp = getSharedPreferences(
									"login", Context.MODE_PRIVATE);
							Editor edit = sp.edit();
							//edit.putInt("userId", userId);
							edit.putString("phone", phone);
							edit.commit();
							MyApplication.getBasicInformation();
							Intent intent=new Intent();  
							//intent.putExtra("back", "Back Data");  
				            setResult(RESULT_CODE, intent);  
				            UpdatePhoneActivity.this.finish(); 
						} else {
							Toast.makeText(UpdatePhoneActivity.this, "提交收集信息失败", 0).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			new HttpUtils().send(HttpMethod.POST, AppUrl.updatePhoneInfo, params,callBack);
		}
	};
}
