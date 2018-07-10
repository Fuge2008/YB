package com.femto.post.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.femto.post.customview.CustomProgressDialog;

public class BaseFragment extends Fragment {
	private CustomProgressDialog pd = null;
	protected Context mContext;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	public void showProgressDialog(String title) {
		if (pd == null) {
			pd = CustomProgressDialog.createDialog(getActivity());
			pd.setMessage(title);
		}
		pd.setCanceledOnTouchOutside(false);
		pd.show();
	}

	/**
	 * 取消加载框
	 */
	public void dismissProgressDialog() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}
	
	public int getScroWith() {
		return getActivity().getWindowManager().getDefaultDisplay().getWidth();
	}

	public int dp2px2(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
