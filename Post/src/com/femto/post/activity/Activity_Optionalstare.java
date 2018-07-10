package com.femto.post.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.entity.Optionalstare;
import com.femto.post.entity.PostMessage;
import com.femto.post.entity.Quotation;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Optionalstare extends BaseActivity {
	private PullToRefreshExpandableListView elv_os;
	private RelativeLayout rl_left;
	private MyAdapter adapter;
	private List<Optionalstare> os;
	private TextView tv_tiele;
	//private List<PostMessage> pms;
	private List<PostMessage> pms0;
	private List<PostMessage> pms1;
	private List<PostMessage> pms2;
	private List<PostMessage> pms3;
	private List<PostMessage> pms4;
	private List<PostMessage> pms5;
	private List<PostMessage> pms6;
	private List<PostMessage> pms7;
	private List<PostMessage> pms8;
	private List<PostMessage> pms9;
	private List<PostMessage> pms10;
	private List<PostMessage> pms11;
	private List<PostMessage> pms12;
	private List<PostMessage> pms13;
	private int userId;
	
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
		elv_os = (PullToRefreshExpandableListView) findViewById(R.id.elv_os);
		tv_tiele = (TextView) findViewById(R.id.tv_title);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		tv_tiele.setText("自选盯盘");
		rl_left.setOnClickListener(this);
		os = new ArrayList<Optionalstare>();//相当于文交所
		
		adapter = new MyAdapter();
		elv_os.getRefreshableView().setAdapter(adapter);
		// 遍历ExpandableListView全部打开
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			elv_os.getRefreshableView().expandGroup(i);
		}
		
		pms0 = new ArrayList<PostMessage>();
		pms1 = new ArrayList<PostMessage>();
		pms2 = new ArrayList<PostMessage>();
		pms3 = new ArrayList<PostMessage>();
		pms4 = new ArrayList<PostMessage>();
		pms5 = new ArrayList<PostMessage>();
		pms6 = new ArrayList<PostMessage>();
		pms7 = new ArrayList<PostMessage>();
		pms8 = new ArrayList<PostMessage>();
		pms9 = new ArrayList<PostMessage>();
		pms10 = new ArrayList<PostMessage>();
		pms11 = new ArrayList<PostMessage>();
		pms12 = new ArrayList<PostMessage>();
		pms13 = new ArrayList<PostMessage>();
	//	pms.add(new PostMessage("324.33", "2234", "-0.12%", "盯盘中"));
	//	pms.add(new PostMessage("324.33", "2234", "-0.12%", "盯盘中"));
	//	pms.add(new PostMessage("324.33", "2234", "-0.12%", "盯盘中"));
	//	pms.add(new PostMessage("324.33", "2234", "-0.12%", "盯盘中"));
	//	pms.add(new PostMessage("324.33", "2234", "-0.12%", "盯盘中"));
		os.add(new Optionalstare("南京文交所", pms0));
		os.add(new Optionalstare("南方文交所", pms1));
		os.add(new Optionalstare("中国艺交所", pms2));
		os.add(new Optionalstare("永瑞文交所", pms3));
		os.add(new Optionalstare("东北邮币卡", pms4));
		os.add(new Optionalstare("上文申江", pms5));
	//	"南京文交所","南方文交所","中国艺交所","永瑞文交所","东北邮币卡","上文申江","安贵邮币卡","上海邮币卡","恒利邮币卡",
	//	 "华强文交所","北京金马甲","南商所","南昌文交所","成都文交所"
		os.add(new Optionalstare("安贵邮币卡", pms6));
		os.add(new Optionalstare("上海邮币卡", pms7));
		os.add(new Optionalstare("恒利邮币卡", pms8));
		os.add(new Optionalstare("华强文交所", pms9));
		os.add(new Optionalstare("北京金马甲", pms10));
		os.add(new Optionalstare("南商所", pms11));
		os.add(new Optionalstare("南昌文交所", pms12));
		os.add(new Optionalstare("成都文交所", pms13));
		userId = Integer.parseInt(MyApplication.userId);
		getData();
	}
	
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_optionalstare);
		showProgressDialog("加载数据中。。。");
		MyApplication.addActivity(this);
	}
	
	private void getData() {
		LogUtils.i("getData===");
	//	showProgressDialog("加载数据中...");
		RequestParams params = new RequestParams();
		params.put("user.id", userId);//TODO
		LogUtils.i("url===="+AppUrl.USERGETMYDINGPAN);
		MyApplication.ahc.post(AppUrl.USERGETMYDINGPAN,
				params,new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
							super.onSuccess(statusCode, headers, response);
//							{"message":"获取成功","jy":[{"wenJiaoId":1,"wenJiaoName":"南京文交所","currentGains":"-1.16%","code":"100001","dingpanId":7,"fullname":"综合指数","curPrice":"2241.38"},{"wenJiaoId":1,"wenJiaoName":"南京文交所","currentGains":"-1.06%","code":"501001","dingpanId":9,"fullname":"三版壹角券","curPrice":"40.0"}],"result":"0"}						
							dismissProgressDialog();
							LogUtils.i("response==="+response.toString());
							try {
								JSONArray jsonArray = response.getJSONArray("jy");
								LogUtils.i("jsonArray的长度"+jsonArray.length());
								if(jsonArray.length()!=0){
								for(int i=0;i<jsonArray.length();i++) {
									JSONObject j = jsonArray.getJSONObject(i);
									String fullname = j.getString("fullname");
									int wenJiaoId = j.getInt("wenJiaoId");
									String code = j.getString("code");
									String currentGains = j.getString("currentGains");
									String curPrice = j.getString("curPrice");
									String wenJiaoName = j.getString("wenJiaoName");
									int dingpanId = j.getInt("dingpanId");
									PostMessage pm = new PostMessage(fullname, currentGains, curPrice, code,dingpanId);
									int index = indexOfOs(wenJiaoName);
									if(index!=-1) {
										os.get(index).getPms().add(pm);//添加数据..
									}
								}
								} else {
									os.clear();
								}
								removeNullOp();
								adapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
								os.clear();
								adapter.notifyDataSetChanged();
							}
						}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
					dismissProgressDialog();
					LogUtils.i("onFailure===");
					os.clear();
					removeNullOp();
					adapter.notifyDataSetChanged();
				}
			});
	}
	class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return os == null ? 0 : os.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return os.get(groupPosition).getPms() == null ? 0 : os
					.get(groupPosition).getPms().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return os.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return os.get(groupPosition).getPms().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View v,
				ViewGroup parent) {
			Holder1 h1;
			if (v == null) {
				h1 = new Holder1();
				v = View.inflate(Activity_Optionalstare.this,
						R.layout.item_elv_title, null);
				h1.im_elv = (ImageView) v.findViewById(R.id.im_elv);
				h1.tv_wenJiaoName = (TextView)v.findViewById(R.id.wenjiaoName);
				v.setTag(h1);
			} else {
				h1 = (Holder1) v.getTag();
			}
			h1.tv_wenJiaoName.setText(os.get(groupPosition).getName());
			h1.im_elv.setImageResource(R.drawable.group2);
			if (isExpanded) {
				h1.im_elv.setImageResource(R.drawable.group1);
			}
			return v;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition,
				boolean isLastChild, View v, ViewGroup parent) {
			Holder2 h2;
			if (v == null) {
				h2 = new Holder2();
				v = View.inflate(Activity_Optionalstare.this,
						R.layout.item_elv_postdetails, null);
				h2.tv_qname = (TextView)v.findViewById(R.id.tv_qname);
				h2.tv_qcode = (TextView)v.findViewById(R.id.tv_qcode);
				h2.tv_newprice = (TextView)v.findViewById(R.id.tv_newprice);
				h2.tv_itd = (TextView)v.findViewById(R.id.tv_itd);
				h2.tv_relieve = (TextView) v.findViewById(R.id.tv_relieve);
				v.setTag(h2);
			} else {
				h2 = (Holder2) v.getTag();
			}
			List<PostMessage> pms = os.get(groupPosition).getPms();
			h2.tv_qname.setText(pms.get(childPosition).getFullname());
			h2.tv_qcode.setText(pms.get(childPosition).getCode());
			h2.tv_newprice.setText(pms.get(childPosition).getCurPrice());
			h2.tv_itd.setText(pms.get(childPosition).getCurrentGains());
			h2.tv_relieve.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDia(groupPosition,childPosition);
				}

			});
			return v;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	class Holder1 {
		ImageView im_elv;
		TextView tv_wenJiaoName;
	}

	class Holder2 {
		TextView tv_relieve;
		TextView tv_qcode;
		TextView tv_qname;
		TextView tv_newprice;
		TextView tv_itd;
	}

	// 解除盯盘
	private void showDia(final int groupPosition,final int childPosition) {
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
				cancelDingPan(groupPosition,childPosition);
				dialog.dismiss();
			}
		});
		tv_cancel_dia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	private int indexOfOs(String name) {
		for(int i=0;i<os.size();i++) {
			if(name.equals(os.get(i).getName())) {
				return i;
			}
		}
		return -1;	
	}
	//将空集合移除掉
	private void removeNullOp() {
		List<Optionalstare> temps = new ArrayList<Optionalstare>();
		temps.addAll(os);
		for(int i=0;i<os.size();i++) {
			if(os.get(i).getPms().size()==0) {
				temps.remove(os.get(i));
			}
		}
		os.clear();
		os.addAll(temps);
		LogUtils.i("os的长度为"+os.size());
	}
	
	private void cancelDingPan(final int groupPotion,final int childPosition) {
		int id = os.get(groupPotion).getPms().get(childPosition).getDingpanId();
		RequestParams params = new RequestParams();
		params.put("id", id);
		MyApplication.ahc.post(AppUrl.userQuxiaoDingPan, params,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				LogUtils.i("response==="+response.toString());
				String result;
				try {
					result = response.getString("result");
					if(result.equals("0")) {
						Toast.makeText(Activity_Optionalstare.this, "解除盯盘成功", 0).show();
					
					os.get(groupPotion).getPms().remove(os.get(groupPotion).getPms().get(childPosition));
					adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(Activity_Optionalstare.this, "解除盯盘失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					LogUtils.i("JSONException====");
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				LogUtils.i("onFailure====");
			}
		});
	}
}
