package com.femto.post.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.futures.util.LogUtils;
import com.android.futures.util.PreferenceUtils;
import com.femto.post.R;
import com.femto.post.activity.Activity_Login;
import com.femto.post.activity.Activity_Member;
import com.femto.post.activity.Activity_Optionalstare;
import com.femto.post.activity.Activity_StockExchange;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.entity.Quotation;
import com.femto.post.entity.Tables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Fragment_Smart extends BaseFragment implements
		OnRefreshListener2<ListView>, OnClickListener, OnItemClickListener{
	private View view;
	private PullToRefreshListView lv_smart;
	private MyAdapter adapter;
	private TextView tv_optstare;
	@SuppressWarnings("unused")
	private RelativeLayout top_search;
	private TextView tv_tosearch;
	private List<Quotation> qs;
	DecimalFormat df = new DecimalFormat("######0.00");
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_smart, container, false);
		initView(view);
		initCont();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_optstare:
			Intent intent = null;
			if(MyApplication.islogin) {
				intent = new Intent(getActivity(),
						Activity_Optionalstare.class);
		//		startActivity(intent);
			} else {
				intent = new Intent(getActivity(),Activity_Login.class);
			} 
			startActivity(intent);
			break;
		/*case R.id.tv_tosearch:
			Intent intent_sreach = new Intent(getActivity(),
					Activity_Search.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("q", qs);
			intent_sreach.putExtras(bundle);
			startActivity(intent_sreach);
			break;*/
		case R.id.tv_tosearch:
			LogUtils.i("tv_tosearch");
			break;
		default:
			break;
		}
	}

	private void initView(View v) {
		lv_smart = (PullToRefreshListView) v.findViewById(R.id.lv_smart);
		tv_optstare = (TextView) v.findViewById(R.id.tv_optstare);
		top_search = (RelativeLayout) v.findViewById(R.id.top_search);
		tv_tosearch = (TextView) v.findViewById(R.id.tv_tosearch);
	}

	private void initCont() {
		qs = new ArrayList<Quotation>();
		lv_smart.setOnRefreshListener(this);
		tv_optstare.setOnClickListener(this);
		tv_tosearch.setOnClickListener(this);
		lv_smart.setOnItemClickListener(this);
		// lv_smart.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		lv_smart.setAdapter(adapter);	
		getYBP();
	}
	
	// 获取邮币平台
	public void getYBP() {
		showProgressDialog("加载数据中...");
		if (!MyApplication.isConnectNetwork(getActivity())) {
			return;
		}
		MyApplication.ahc.post(AppUrl.USERGETfIRSTMENU,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						if(lv_smart!=null&&lv_smart.isRefreshing()) {
							lv_smart.onRefreshComplete();
						}
						dismissProgressDialog();
						super.onSuccess(statusCode, headers, response);
						LogUtils.i("==获取平台="+response.toString());
						final JSONArray optJSONArray = response.optJSONArray("jy");
						LogUtils.i("jy"+optJSONArray.toString());
						if (optJSONArray != null) {
									for (int i = 0; i < optJSONArray.length(); i++) {
										//":[{"wenJiaoId":"1","id":1,"wenJiaoName":"南京文交所","currentGains":"2.5%","curPrice":"2172.6","sumNum":"6814632","sumMoney":"816475667"},{"
										JSONObject j = optJSONArray.optJSONObject(i);
										int id = j.optInt("id");
										String wenjiaoId = j.optString("wenJiaoId");
										String name = j.optString("wenJiaoName");
										String curPrice = j.optString("curPrice");
										String currentGains = j.optString("currentGains");
										String sumNum = j.optString("sumNum");
										String sumMoney = j.optString("sumMoney");
										Quotation quotation = new Quotation(id,wenjiaoId, name, curPrice, currentGains, sumNum, sumMoney);
										qs.add(quotation);
										changeWenjiaoOrder(qs);
										getActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												adapter.notifyDataSetChanged();
											}
										});
								}
  				    }}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
					if(lv_smart!=null&&lv_smart.isRefreshing()) {
						lv_smart.onRefreshComplete();
					}
					dismissProgressDialog();
					Toast.makeText(getContext(), "加载文交所信息失败", Toast.LENGTH_SHORT).show();
				}
			});
	}
	
	//更改文交所的顺序。。。
	private void changeWenjiaoOrder(List<Quotation> qs) {
		
	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return qs == null ? 0 : qs.size();
		}

		@Override
		public Object getItem(int position) {
			return qs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(getActivity(), R.layout.item_smart, null);
				h.tv_onekeyos = (TextView) v.findViewById(R.id.tv_onekeyos);
				h.tv_name_q = (TextView) v.findViewById(R.id.tv_name_q);
				h.tv_cjl = (TextView) v.findViewById(R.id.tv_cjl);
				h.tv_cje = (TextView) v.findViewById(R.id.tv_cje);
				h.tv_tx_nub = (TextView) v.findViewById(R.id.tv_tx_nub);
				h.tv_tx_id = (TextView) v.findViewById(R.id.tv_tx_id);
				h.ll_index = (LinearLayout) v.findViewById(R.id.ll_index);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_name_q.setText("" + qs.get(position).getName());
			h.tv_cjl.setText("成交量:"
					+ df.format(Double.parseDouble(qs.get(position).getSumNum()) / 10000d) + "万");
			h.tv_cje.setText("成交额:"
					+ df.format(Double.parseDouble(qs.get(position).getSumMoney())/ 100000000d)
					+ "亿");
			h.tv_tx_nub.setText(""
					+ qs.get(position).getCurPrice());
			h.tv_tx_id.setText(""
					+ qs.get(position).getCurrentGains());
				if (Double.parseDouble(qs.get(position).getCurrentGains().split("%")[0]) < 0) {
					h.ll_index.setBackgroundColor(getResources().getColor(
							R.color.green));
				} else {
					h.ll_index.setBackgroundColor(getResources().getColor(
							R.color.red));
				}
			return v;
		}
	}

	class MyHolder {
		TextView tv_onekeyos, tv_name_q, tv_cjl, tv_cje, tv_tx_nub, tv_tx_id;
		LinearLayout ll_index;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		qs.clear();
		getYBP();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position = position - 1;
		Intent intent = new Intent(getActivity(), Activity_StockExchange.class);
		intent.putExtra("qt", qs.get(position));
		LogUtils.i("id为"+qs.get(position).getId());
		LogUtils.i("qt为"+qs.get(position).toString());
		startActivity(intent);
	}
     
}
