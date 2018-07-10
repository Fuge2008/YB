package com.femto.post.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.entity.Quotation;
import com.femto.post.entity.Tables;
import com.femto.post.fragment.Fragment_Smart;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_StockExchange extends BaseActivity implements
		OnItemClickListener,OnRefreshListener2<ListView> {
	private PullToRefreshListView lv_se;
	private MyAdapter adapter;
	private RelativeLayout rl_left;
	private TextView tv_title;
	private ArrayList<Tables> tables;
	private TextView tv_onekeyos, tv_name_q, tv_cjl, tv_cje, tv_tx_nub,
			tv_tx_id;
	private LinearLayout ll_index;
	private int p;
	//缓存的tables用于改变列表顺序用的   相当于一个中转
	private ArrayList<Tables> cacheTables;//存放不带指数两字的条目
	private ArrayList<Tables> cacheTables2;//存放所有带指数两字的条目...
	private String wenJiaoId;
	private Quotation qt;
	private String userId;
	
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
		LogUtils.i("initView====");
		lv_se = (PullToRefreshListView) findViewById(R.id.lv_se);
		adapter = new MyAdapter();
		lv_se.setAdapter(adapter);
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_name_q = (TextView) findViewById(R.id.tv_name_q);
		tv_cjl = (TextView) findViewById(R.id.tv_cjl);
		tv_cje = (TextView) findViewById(R.id.tv_cje);
		tv_tx_nub = (TextView) findViewById(R.id.tv_tx_nub);
		tv_tx_id = (TextView) findViewById(R.id.tv_tx_id);
		ll_index = (LinearLayout) findViewById(R.id.ll_index);
		lv_se.setOnRefreshListener(this);
		initParams();
	}

	@Override
	public void initUtils() {

	}
	@Override
	public void Control() {
		rl_left.setOnClickListener(Activity_StockExchange.this);
		tv_title.setText("" + qt.getName());
		LogUtils.i("文交所名字。。"+qt.getName());
		tv_name_q.setText("" + qt.getName());
		tv_cjl.setText("成交量:"
				+ MyApplication.df.format(Double.parseDouble(qt.getSumNum()) / 10000d) + "万");
		tv_cje.setText("成交额:"
				+ MyApplication.df.format(Double.parseDouble(qt.getSumMoney()) / 100000000d) + "亿");
		tv_tx_nub.setText("" + qt.getCurPrice());
		tv_tx_id.setText("" + qt.getCurrentGains());
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_stockexchange);
		MyApplication.addActivity(this);
	}
	
	private void initParams() {
		qt = (Quotation) getIntent().getSerializableExtra("qt");
		wenJiaoId = qt.getWenjiaoId();
		userId = MyApplication.userId;
		LogUtils.i("userId====="+userId);
		if(userId==null) {
			userId = "0";
		}
		LogUtils.i("wenJiaoId==="+wenJiaoId);
		getTablesData(wenJiaoId);
	}
	
	private void getTablesData(String wenJiaoId) {
		showProgressDialog("加载数据中。。。");
		RequestParams params = new RequestParams();
		params.addBodyParameter("wenJiaoId", wenJiaoId);
		params.addBodyParameter("user.id", userId);
		new HttpUtils().send(HttpMethod.POST, AppUrl.USERGETTWOMENU, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				stopRefresh();
				Toast.makeText(Activity_StockExchange.this, "网络连接失败", 0).show();
			}
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dismissProgressDialog();
				stopRefresh();
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					LogUtils.i("jsonObject==="+jsonObject.toString());
					String result = jsonObject.getString("result");
					LogUtils.i("result======="+result);
					if(result.equals("0")) {
						String tableList = jsonObject.getString("jy");
								tables = new Gson().fromJson(tableList,  
						                new TypeToken<List<Tables>>() {  
						                }.getType());
								LogUtils.i("tables的长度为"+tables.size());
								for(int i=0;i<tables.size();i++) {
									Tables table = tables.get(i);
									if((table.getDingpanId()!=0)) {
										table.setLooked(true);
									}
								}
								changeOrderForTable();
							/*for(int i=0;i<tables.size();i++) {
								if(!tables.get(i).getDingpanId().equals("0")) {
									tables.get(i).setLooked(true);
								}
							}*/
							adapter.notifyDataSetChanged();
						}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sendRequestChangeTables() {
		LogUtils.i("sendRequestChangeTables");
		//Michael临时补救办法..后期改
		showProgressDialog("加载数据中...");
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userId);
		params.addBodyParameter("wenJiaoId", qt.getId()+"");
		LogUtils.i("sendRequestChangeTables");
		RequestCallBack<String> callBack = new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogUtils.i("连接网络失败");
				Toast.makeText(Activity_StockExchange.this, "网络连接失败", 0).show();
			}
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.i("连接网络成功...");
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					LogUtils.i("jsonObject"+jsonObject);
					String result = jsonObject.getString("result");
					LogUtils.i("result==="+result);
					if(result.equals("0")) {
						String json = jsonObject.getString("jy");
						//解析集合...
						JSONArray jsonArray = new JSONArray(json);
						Log.i("test3", "tables的长度为"+tables.size());
						Log.i("test3", "jsonArray的长度为..."+jsonArray.length());
						for(int i=0;i<tables.size();i++) {
							for(int j=0;j<jsonArray.length();j++) {
								if(tables.get(i).getCode().equals(jsonArray.getJSONObject(j).getString("code"))) {
								//	Log.i("test3", "i========"+i);
									tables.get(i).setLooked(true);
									tables.get(i).setDingpanId(jsonArray.getJSONObject(j).getInt("dingPanId"));
								}
							}
						}
						//Michael临时处理办法...
							changeOrderForTable();
							dismissProgressDialog();
							adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtils.i("网络连接异常。。");
				}
			}
		};
		new HttpUtils().send(HttpMethod.POST, AppUrl.usergetDingPans,params, callBack);
	}

	private void changeOrderForTable() {
		if (tables == null || tables.size() == 0) {
			finish();
			return;
		}
		cacheTables = new ArrayList<Tables>();
		cacheTables2 = new ArrayList<Tables>();
		for (int i=0;i<tables.size();i++) {
			Tables table = tables.get(i);
			Log.i("test", table.getFullname());
			if(table.getFullname().contains("指数")&&!table.getFullname().equals("综合指数")) {
				cacheTables2.add(table);
			} else {
				cacheTables.add(table);
			}
		}
		//将集合清空,然后重新添加数据进去
		tables.clear();
		tables.addAll(cacheTables);
		tables.addAll(cacheTables2);
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return tables == null ? 0 : tables.size();
		}

		@Override
		public Object getItem(int position) {
			return tables.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			final MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_StockExchange.this,
						R.layout.item_elv_postdetails, null);
				h.tv_newprice = (TextView) v.findViewById(R.id.tv_newprice);
				h.tv_qname = (TextView) v.findViewById(R.id.tv_qname);
				h.tv_itd = (TextView) v.findViewById(R.id.tv_itd);
				h.tv_qcode = (TextView) v.findViewById(R.id.tv_qcode);
				h.tv_relieve = (TextView) v.findViewById(R.id.tv_relieve);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_newprice.setText("" + tables.get(position).getCurPrice());
			h.tv_qname.setText("" + tables.get(position).getFullname());
			h.tv_itd.setText("" + tables.get(position).getCurrentGains());
			h.tv_qcode.setText("" + tables.get(position).getCode());
			//判断是否处于盯盘中
			boolean isLooked = tables.get(position).getLooked();
	//		Log.i("test3", "isLooked"+position+isLooked);
			if(isLooked) {
				h.tv_relieve.setText("盯盘中");
				h.tv_relieve.setBackgroundResource(R.drawable.round_bg);
			} else {
				h.tv_relieve.setText("开启\n盯盘");
				h.tv_relieve.setBackgroundResource(R.drawable.round_bg_gray);
			}
//			if (tables.get(position).getCurrentGains() > 0) {
//				h.tv_itd.setTextColor(getResources().getColor(R.color.red));
//				h.tv_newprice.setTextColor(getResources().getColor(R.color.red));
//			} else {
//				h.tv_itd.setTextColor(getResources().getColor(R.color.green));
//				h.tv_newprice.setTextColor(getResources().getColor(R.color.green));
//			}
			h.tv_relieve.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LogUtils.i("点击盯盘了。。。");
					boolean isLookeds = tables.get(position).getLooked();
					LogUtils.i("isLookeds"+isLookeds);
					String code = tables.get(position).getCode()+"";
					LogUtils.i("code"+code);
					//showDia();
					if(isLookeds) {
						showDia(tables,h.tv_relieve,position);
					} else {
						if(!MyApplication.islogin) {
							Intent intent = new Intent(Activity_StockExchange.this,Activity_Login.class);
							startActivity(intent);
						}
						
						if(MyApplication.islogin) {
							//判断是否为会员 如果是会员直接发送盯盘请求  如果不是先跳到Activity_Member
							JudgeIsVIP(code,tables,h.tv_relieve,position);
						}
					}
				}
			});
			return v;
		}
	}
	private void JudgeIsVIP(final String code,final List<Tables> tables,final TextView tv_relieve,final int position) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("user.id", userId);
		new HttpUtils().send(HttpMethod.POST, AppUrl.USERGETISMEMBER, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogUtils.i("onFailure");
				Toast.makeText(Activity_StockExchange.this, "网络连接失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.i("onsuccess");
				//","result":"0","isMember":"0"}
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					String result = jsonObject.getString("result");
					LogUtils.i("result===="+result);
					if(result.equals("0")) {
						String isMember = jsonObject.getString("isMember");
						LogUtils.i("isMember==="+isMember);
						if(isMember.equals("0")) {//会员
							openDingPan(code,tables,tv_relieve,position);
						} else {
							Intent intent = new Intent(Activity_StockExchange.this,Activity_Member.class);
							startActivity(intent);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					LogUtils.i("JSONException====");
					return;
				}
			}
		});
	}
	private void openDingPan(String num,final List<Tables>tables,final TextView tv,final int position) {
		showProgressDialog("请求盯盘中...");
		RequestParams params = new RequestParams();
	//	params.addBodyParameter("uid", MyApplication.userId);
		params.addBodyParameter("uid",userId);
		if(userId.equals("0")) {
			Toast.makeText(Activity_StockExchange.this, "登陆之后才可以盯盘", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.i("test", "uid=="+userId);
		params.addBodyParameter("wenJiaoId", wenJiaoId+"");
		params.addBodyParameter("code", num+"");
		
		RequestCallBack<String> callBack = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				Toast.makeText(Activity_StockExchange.this, "网络连接失败", 0).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dismissProgressDialog();
				Log.i("test3", "盯盘请求成功..");
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					String result = jsonObject.getString("result");
					Log.i("test3", "盯盘返回码为"+result);
					if(result.equals("0")) {
						Log.i("test3", "盯盘成功");
						String DingPanId = jsonObject.getString("DingPanId");
						tables.get(position).setLooked(true);
						tables.get(position).setDingpanId(Integer.parseInt(DingPanId));
						tv.setText("盯盘中");
						tv.setBackgroundResource(R.drawable.round_bg);
					} else {
						Log.i("test3", "盯盘失败");
						Toast.makeText(Activity_StockExchange.this, "盯盘失败", 0).show();
					}
				} catch (JSONException e) {
					Log.i("test3", "盯盘异常。。。");
					e.printStackTrace();
					dismissProgressDialog();
				}
				
			}
		};
		new HttpUtils().send(HttpMethod.POST, AppUrl.userOneDingPan, params,callBack);
	}
	class MyHolder {
		TextView tv_newprice, tv_qname, tv_itd, tv_qcode, tv_relieve;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
//		Log.i("test3", "点击条目准备跳转...");
//		Intent intent = new Intent(this, Activity_KLine.class);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("tabls", qs.getTables().get(position));
//		intent.putExtras(bundle);
//		intent.putExtra("p", p);
//		intent.putExtra("pp", position);
//		startActivity(intent);
//		Log.i("test3", "跳转K线图页面中...");
	}

	// 解除盯盘
	private void showDia(final List<Tables>tables,final TextView tv,final int position) {
		final Dialog dialog = new Dialog(this, R.style.MyDialogStyle);
		View view = View.inflate(this, R.layout.dialog_reliece, null);
		dialog.setContentView(view);
		dialog.show();
		TextView tv_relieve_dia = (TextView) view
				.findViewById(R.id.tv_relieve_dia);
		TextView tv_cancel_dia = (TextView) view
				.findViewById(R.id.tv_cancel_dia);
		tv_relieve_dia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				cancelDingpan(tables,tv,position);
				//tables.get(position).setLooked(false);
				//tv.setText("开启盯盘");
				//tv.setBackgroundResource(R.drawable.round_bg_gray);
			}
		});
		tv_cancel_dia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	private void cancelDingpan(final List<Tables> tables,final TextView tv,final int position) {
		showProgressDialog("发送取消盯盘请求中..");
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", tables.get(position).getDingpanId()+"");
		RequestCallBack<String> callBack = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				Toast.makeText(Activity_StockExchange.this, "取消盯盘失败", 0).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dismissProgressDialog();
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					String result = jsonObject.getString("result");
					if(result.equals("0")) {
						//取消成功...
						tables.get(position).setLooked(false);
						//设置盯盘的id为0可做可不做  相当于后台删除那条数据....
						tables.get(position).setDingpanId(0);
						tv.setText("开启\n盯盘");
						tv.setBackgroundResource(R.drawable.round_bg_gray);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					dismissProgressDialog();
				}
			}
			
		};
		new HttpUtils().send(HttpMethod.POST, AppUrl.userQuxiaoDingPan, params, callBack);
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		tables.clear();
		initParams();
	}
	
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}
	private void stopRefresh() {
		if(lv_se!=null&&lv_se.isRefreshing()) {
			lv_se.onRefreshComplete();
		}
	}
}
