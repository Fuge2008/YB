package com.femto.post.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.activity.Activity_Optionalstare.MyAdapter;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_OpenAccount extends BaseActivity {
	private RelativeLayout rl_left;
	private TextView tv_title;
	private List<OpenWjInfo> openWjInfoLists;
	private PullToRefreshListView lv_opencount;
	private MyAdapter adapter;
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
		lv_opencount = (PullToRefreshListView)findViewById(R.id.lv_opencount);
		adapter = new MyAdapter();
		lv_opencount.setOnRefreshListener(onRefreshListener2);
		lv_opencount.setAdapter(adapter);
		lv_opencount.setOnItemClickListener(oicl);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		tv_title.setText("我要开户");
		rl_left.setOnClickListener(this);
		openWjInfoLists = new ArrayList<Activity_OpenAccount.OpenWjInfo>();
		getData();
	}
	
	private void getData() {
		LogUtils.i(AppUrl.BASEURL+AppUrl.USERGETWENJIAOOPEN);
		MyApplication.ahc.post(AppUrl.USERGETWENJIAOOPEN, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				stopRefresh();
				LogUtils.i("response==="+response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("jy");
					if(jsonArray!=null) {
						for(int i=0;i<jsonArray.length();i++) {
							JSONObject j = jsonArray.getJSONObject(i);
							String name = j.getString("name");
							String openurl = j.getString("openurl");
							openWjInfoLists.add(new OpenWjInfo(name,openurl));
						}
					}
					LogUtils.i("openWjInfoLists的长度"+openWjInfoLists.size());
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtils.i("JSONException");
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				stopRefresh();
				LogUtils.i("onFailure====");
			}
		}); 
	}
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_openaccount);
		MyApplication.addActivity(this);
	}
	
	class OpenWjInfo {
		String name;
		String url;
		
		public OpenWjInfo() {
			super();
		}

		public OpenWjInfo(String name, String url) {
			super();
			this.name = name;
			this.url = url;
		}
		
	}
	
	private class MyAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return openWjInfoLists==null?0:openWjInfoLists.size();
		}

		@Override
		public Object getItem(int position) {
			return openWjInfoLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null) {
				convertView = View.inflate(Activity_OpenAccount.this, R.layout.item_group_name, null);
			}
			TextView tv_group = (TextView)convertView.findViewById(R.id.tv_group);
			tv_group.setText(openWjInfoLists.get(position).name);
			return convertView;
		}
	}
	
	private OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtils.i("onItemClick");
			OpenWjInfo openWjinfo = openWjInfoLists.get(position-1);
			Intent intent = new Intent(Activity_OpenAccount.this,Activity_OpenInfoDetails.class);
			intent.putExtra("url", openWjinfo.url);
			LogUtils.i("url,"+openWjinfo.url);
			intent.putExtra("title", openWjinfo.name);
			startActivity(intent);
			LogUtils.i("startActivity");
		}
	};
	
	private OnRefreshListener2<ListView> onRefreshListener2 = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			openWjInfoLists.clear();
			getData();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			
		}

	};
	
	private void stopRefresh() {
		if(lv_opencount!=null&&lv_opencount.isRefreshing()) {
			lv_opencount.onRefreshComplete();
		} 
	}
}
