package com.femto.post.activity;

import com.femto.post.R;
import com.femto.post.application.MyApplication;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_MyMessage extends BaseActivity {
	private RelativeLayout rl_left;
	private TextView tv_title;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		tv_title.setText("我的消息");
		rl_left.setOnClickListener(this);
		MyApplication.addActivity(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_mymessage);
	}

}
