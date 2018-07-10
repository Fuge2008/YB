package com.femto.post.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.femto.post.R;
import com.femto.post.application.MyApplication;

public class Activity_Member extends BaseActivity {
	private RelativeLayout rl_left;
	private TextView tv_title, tv_memberfreefee;
	private RelativeLayout pay_channel;
	private RelativeLayout free_channel;
	private RelativeLayout benefit_channel;
	private RelativeLayout apply_free;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
			break;
		case R.id.apply_free:
			
			break;
		case R.id.free_channel:
			
			break;
		case R.id.benefit_channel:
			
			break;
			
		case R.id.pay_channel:
			showPayDialog();
			break;
		default:
			break;
		}
	}
	
	private void showPayDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Member.this);
		View v = View.inflate(Activity_Member.this, R.layout.pay_dialog, null);
		builder.setView(benefit_channel);
	}
	
	@Override
	public void initView() {
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_memberfreefee = (TextView) findViewById(R.id.tv_memberfreefee);
		
		pay_channel = (RelativeLayout)findViewById(R.id.pay_channel);
		free_channel = (RelativeLayout)findViewById(R.id.free_channel);
		benefit_channel = (RelativeLayout)findViewById(R.id.benefit_channel);
		
		apply_free = (RelativeLayout)findViewById(R.id.apply_free);
		
		pay_channel.setOnClickListener(this);
		free_channel.setOnClickListener(this);
		benefit_channel.setOnClickListener(this);
		apply_free.setOnClickListener(this);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_left.setOnClickListener(this);
		tv_title.setText("开通会员");
		SpannableStringBuilder builder = new SpannableStringBuilder(
				tv_memberfreefee.getText().toString());
		// ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
		ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
		ForegroundColorSpan redSpan1 = new ForegroundColorSpan(Color.RED);
		builder.setSpan(redSpan, 2, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(redSpan1, 12, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		SpannableString ssss = new SpannableString("");

		tv_memberfreefee.setText(builder);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_member);
		MyApplication.addActivity(this);
	}

}
