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

public class Fragment_MustSee extends BaseFragment implements OnClickListener {
	private View view;
	private RadioButton rb_technology, rb_experience, rb_system, rb_newbie;
	private FragmentTransaction transaction;
	private Fragment_Grail fg;
	private Fragment_Grail fr;
	private Fragment_Grail fd;
	private Fragment_Grail fc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_mustsee, container, false);
		initView(view);
		initCont();
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rb_technology:
			fragmentShowOrHide(fg, fr, fd, fc, false);
			break;
		case R.id.rb_experience:
			fragmentShowOrHide(fr, fg, fd, fc, false);
			break;
		case R.id.rb_system:
			fragmentShowOrHide(fd, fg, fr, fc, false);
			break;
		case R.id.rb_newbie:
			fragmentShowOrHide(fc, fg, fr, fd, false);
			break;

		default:
			break;
		}
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		rb_technology = (RadioButton) v.findViewById(R.id.rb_technology);
		rb_experience = (RadioButton) v.findViewById(R.id.rb_experience);
		rb_system = (RadioButton) v.findViewById(R.id.rb_system);
		rb_newbie = (RadioButton) v.findViewById(R.id.rb_newbie);
	}

	private void initCont() {
		// TODO Auto-generated method stub
		rb_technology.setOnClickListener(this);
		rb_experience.setOnClickListener(this);
		rb_system.setOnClickListener(this);
		rb_newbie.setOnClickListener(this);

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
		fg = new Fragment_Grail(CommonValue.Statu_jishufenxiang, true);
		fc = new Fragment_Grail(CommonValue.Statu_xinshoubidu, false);
		fd = new Fragment_Grail(CommonValue.Statu_zhengcezhidu, false);

		fr = new Fragment_Grail(CommonValue.Statu_jingyanfenxinag, false);
		// ftt = new Fragment_Headline();

		transaction.add(R.id.fl_contain_mustsee, fc);
		transaction.add(R.id.fl_contain_mustsee, fd);
		transaction.add(R.id.fl_contain_mustsee, fg);
		transaction.add(R.id.fl_contain_mustsee, fr);
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
