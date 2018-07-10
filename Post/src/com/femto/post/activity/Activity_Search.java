/*package com.femto.post.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.femto.post.R;
import com.femto.post.application.MyApplication;
import com.femto.post.entity.Quotation;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class Activity_Search extends BaseActivity implements
		OnRefreshListener2<ListView>, OnItemClickListener, OnScrollListener {
	private RelativeLayout rl_left;
	private TextView tv_title;
	private ListView lv_search;
	private MyAdapter adapter;
	private ArrayList<Quotation> qs;
	private ArrayList<Quotation> cqs;
	private EditText ed_search_sp;

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
		// TODO Auto-generated method stub
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		lv_search = (ListView) findViewById(R.id.lv_search);
		ed_search_sp = (EditText) findViewById(R.id.ed_search_sp);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		cqs = new ArrayList<Quotation>();
		rl_left.setOnClickListener(this);
		lv_search.setOnItemClickListener(this);
		lv_search.setOnScrollListener(this);
		ed_search_sp.addTextChangedListener(watcher);
		tv_title.setText("搜索");
		adapter = new MyAdapter();
		lv_search.setAdapter(adapter);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_search);
		MyApplication.addActivity(this);
		initParams();
	}

	@SuppressWarnings("unchecked")
	private void initParams() {
		// TODO Auto-generated method stub
		// qs = (ArrayList<Quotation>) getIntent().getSerializableExtra("q");
		qs = MyApplication.qs;
		if (qs == null) {
			finish();
			return;
		}
	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			System.out.println("zuo===" + s.toString());
			if (s.toString().length() == 0) {
				cqs.clear();
				adapter.notifyDataSetChanged();
			} else {
				// getData(s.toString());
				filterData(s.toString());
			}

		}

	};

	private void filterData(String string) {
		// TODO Auto-generated method stub
		cqs.clear();
		for (int i = 0; i < qs.size(); i++) {
			if (qs.get(i).getName().contains("" + string)) {
				cqs.add(qs.get(i));
			}
		}
		adapter.notifyDataSetChanged();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cqs == null ? 0 : cqs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cqs.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_Search.this, R.layout.item_smart,
						null);
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
			h.tv_name_q.setText("" + cqs.get(position).getName());
			h.tv_cjl.setText("成交量:"
					+ MyApplication.df
							.format(cqs.get(position).getSumNum() / 10000d)
					+ "万");
			h.tv_cje.setText("成交额:"
					+ MyApplication.df
							.format(cqs.get(position).getSumMoney() / 100000000d)
					+ "亿");
			h.tv_tx_nub.setText(""
					+ cqs.get(position).getTables().get(0).getCurPrice());
			h.tv_tx_id.setText(""
					+ cqs.get(position).getTables().get(0).getCurrentGains());
			if (cqs.get(position).getTables() != null
					&& cqs.get(position).getTables().size() != 0) {
//				if (cqs.get(position).getTables().get(0).getCurrentGains() < 0) {
//					h.ll_index.setBackgroundColor(getResources().getColor(
//							R.color.green));
//				} else {
//					h.ll_index.setBackgroundColor(getResources().getColor(
//							R.color.red));
//				}
			}

			h.tv_onekeyos.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_Search.this,
							Activity_Member.class);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MyHolder {
		TextView tv_onekeyos, tv_name_q, tv_cjl, tv_cje, tv_tx_nub, tv_tx_id;
		LinearLayout ll_index;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Activity_Search.this,
				Activity_StockExchange.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("q", cqs.get(position));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		closekey();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}
*/