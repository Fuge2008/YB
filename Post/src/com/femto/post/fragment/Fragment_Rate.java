package com.femto.post.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.femto.post.R;

public class Fragment_Rate extends BaseFragment {
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_mustsee, container, false);

		initView(view);
		initCont();
		return view;
	}

	private void initView(View v) {
		// TODO Auto-generated method stub

	}

	private void initCont() {
		// TODO Auto-generated method stub

	}
}
