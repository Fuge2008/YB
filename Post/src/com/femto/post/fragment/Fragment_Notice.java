package com.femto.post.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.femto.post.R;
import com.femto.post.commontvalue.CommonValue;

public class Fragment_Notice extends BaseFragment implements OnClickListener {
	private View view;
	private RadioButton rb_today, rb_buy, rb_trusteeship, rb_policy;
	private FragmentTransaction transaction;
	private Fragment_Grail fg;
	private Fragment_Grail fr;
	private Fragment_Grail fd;
	private Fragment_Grail fc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_notice, container, false);
		initView(view);
		initCont();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_today:
			fragmentShowOrHide(fg, fr, fd, fc, false);
			break;
		case R.id.rb_buy:
			fragmentShowOrHide(fr, fg, fd, fc, false);
			break;
		case R.id.rb_trusteeship:
			fragmentShowOrHide(fd, fg, fr, fc, false);
			break;
		case R.id.rb_policy:
			fragmentShowOrHide(fc, fg, fr, fd, false);
			break;

		default:
			break;
		}
	}

	private void initView(View v) {
		rb_today = (RadioButton) v.findViewById(R.id.rb_today);
		rb_buy = (RadioButton) v.findViewById(R.id.rb_buy);
		rb_trusteeship = (RadioButton) v.findViewById(R.id.rb_trusteeship);
		rb_policy = (RadioButton) v.findViewById(R.id.rb_policy);
	}

	private void initCont() {
		rb_today.setOnClickListener(this);
		rb_buy.setOnClickListener(this);
		rb_trusteeship.setOnClickListener(this);
		rb_policy.setOnClickListener(this);

	}

	private boolean isLoad = false;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isLoad) {
			isLoad = true;
			initFragment();
		}
	}

	private void initFragment() {
		transaction = getFragmentManager().beginTransaction();
		fg = new Fragment_Grail(CommonValue.Statu_jinrigonggao, true);
		fc = new Fragment_Grail(CommonValue.Statu_zhengcezixun, false);
		fd = new Fragment_Grail(CommonValue.Statu_tuoguangonggao, false);
		fr = new Fragment_Grail(CommonValue.Statu_shengougonggao, false);
		// ftt = new Fragment_Headline();

		transaction.add(R.id.fl_contain_notice, fc);
		transaction.add(R.id.fl_contain_notice, fd);
		transaction.add(R.id.fl_contain_notice, fg);
		transaction.add(R.id.fl_contain_notice, fr);
		fragmentShowOrHide(fg, fr, fd, fc, true);
	}

	private void fragmentShowOrHide(Fragment showFragment,
			Fragment hideFragment1, Fragment hideFragment2,
			Fragment hideFragment3, boolean isInit) {
		if (!isInit) {
			transaction = getFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.hide(hideFragment2);
		transaction.hide(hideFragment3);
		transaction.commit();
	}
}
