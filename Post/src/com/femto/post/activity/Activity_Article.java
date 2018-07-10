package com.femto.post.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.util.SpUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class Activity_Article extends BaseActivity {
	private RelativeLayout rl_left;
	private TextView tv_title;
	private WebView web_view;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
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
		web_view = (WebView) findViewById(R.id.web_view);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		//String id = getIntent().getExtras().getString("id");
		String id = SpUtil.readSp(Activity_Article.this, "id", null);
		rl_left.setOnClickListener(this);
		tv_title.setText("资讯正文");
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id);
	//	params.addBodyParameter("pageModel.pageSize", 3+"");
	//	params.addBodyParameter("pageModel.pageIndex", 1+"");
		showProgressDialog("正在加载数据中...");
		new HttpUtils().send(HttpMethod.POST, AppUrl.usergetArticleById, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				Toast.makeText(Activity_Article.this, "加载数据失败", 0).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dismissProgressDialog();
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					Log.i("test3", jsonObject.toString());
					String result = jsonObject.getString("result");
					if(result.equals("0")) {
						Log.i("test", "获取数据成功....");
						//拿到list...
						/*String json = jsonObject.getString("list");
						JSONArray jsonArr = new JSONArray(json);
						JSONObject jsonObj = jsonArr.getJSONObject(0);
						String data = jsonObj.getString("info");*/
						String data = jsonObject.getString("info");
						initWebView(data);
					} else {
						Toast.makeText(Activity_Article.this, "获取数据异常..", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					dismissProgressDialog();
					Toast.makeText(Activity_Article.this, "获取数据异常..", 0).show();
				}
				
			}
		});
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_article);
		MyApplication.addActivity(this);
	}

	// 设置webview
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView(String html) {
		// TODO Auto-generated method stub
		// showProgressDialog("加载中");
		/*web_view.loadUrl(url);
		// 启用javascript
		web_view.getSettings().setJavaScriptEnabled(true);
		// 随便找了个带图片的网站
		web_view.loadUrl(url);
		// 添加js交互接口类，并起别名 imagelistner

		web_view.setWebViewClient(new MyWebViewClient());*/
		//web_view.loadData(html, "text/html", "UTF-8");
		web_view.loadDataWithBaseURL(AppUrl.BASEURL+"/", html, "text/html", "UTF-8", null);
		web_view.getSettings().setJavaScriptEnabled(true);
	}

	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);
		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageFinished(WebView view, String url) {

			view.getSettings().setJavaScriptEnabled(true);

			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			System.out.println("zuo加载完成设置监听");

		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}
}
