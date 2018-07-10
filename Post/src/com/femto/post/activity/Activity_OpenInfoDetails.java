package com.femto.post.activity;

import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.application.MyApplication;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_OpenInfoDetails extends BaseActivity {

	private RelativeLayout rl_left;
	private TextView tv_title;
	private WebView webView;

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
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		webView = (WebView)findViewById(R.id.webView);
	}

	@Override
	public void initUtils() {
		
	}

	@Override
	public void Control() {
		String url = getIntent().getStringExtra("url");
		LogUtils.i("url===="+url);
		String title = getIntent().getStringExtra("title");
		tv_title.setText(title);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
	}

	@Override
	public void setContentView() {
		LogUtils.i("setContentView()");
		setContentView(R.layout.activity_open_details);
		MyApplication.addActivity(this);
	}

}
