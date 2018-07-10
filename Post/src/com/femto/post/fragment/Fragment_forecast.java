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

public class Fragment_forecast extends BaseFragment implements OnClickListener {
	private View view;
	private RadioButton rb_grail, rb_rate, rb_datacheck, rb_commends;
	private FragmentTransaction transaction;
	private Fragment_Grail fg;
	private Fragment_Grail fr;
	private Fragment_Grail fd;
	private Fragment_Grail fc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_forecast, container, false);
		initView(view);
		initCont();
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rb_grail:
			fragmentShowOrHide(fg, fr, fd, fc, false);
			break;
		case R.id.rb_rate:
			fragmentShowOrHide(fr, fg, fd, fc, false);
			break;
		case R.id.rb_datacheck:
			fragmentShowOrHide(fd, fg, fr, fc, false);
			break;
		case R.id.rb_commends:
			fragmentShowOrHide(fc, fg, fr, fd, false);
			break;

		default:
			break;
		}
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		rb_grail = (RadioButton) v.findViewById(R.id.rb_grail);
		rb_rate = (RadioButton) v.findViewById(R.id.rb_rate);
		rb_datacheck = (RadioButton) v.findViewById(R.id.rb_datacheck);
		rb_commends = (RadioButton) v.findViewById(R.id.rb_commends);
	}

	private void initCont() {
		// TODO Auto-generated method stub
		rb_grail.setOnClickListener(this);
		rb_rate.setOnClickListener(this);
		rb_datacheck.setOnClickListener(this);
		rb_commends.setOnClickListener(this);

	}

	private boolean isLoad = false;

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden && !isLoad) {
			isLoad = true;
			initFragment();
		}
	}

	private void initFragment() {
		transaction = getFragmentManager().beginTransaction();
		fg = new Fragment_Grail(CommonValue.Statu_dapanjiexi, true);
		fc = new Fragment_Grail(CommonValue.Statu_dashipinglun, false);
		fd = new Fragment_Grail(CommonValue.Statu_shujujiance, false);
		fr = new Fragment_Grail(CommonValue.Statu_gepiaopingji, false);
		// ftt = new Fragment_Headline();

		transaction.add(R.id.fl_contain_forecast, fc);
		transaction.add(R.id.fl_contain_forecast, fd);
		transaction.add(R.id.fl_contain_forecast, fg);
		transaction.add(R.id.fl_contain_forecast, fr);
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
