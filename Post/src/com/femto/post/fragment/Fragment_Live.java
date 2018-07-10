package com.femto.post.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.customview.CircleImageView;
import com.femto.post.customview.HorizontalListView;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.chatuidemo.ui.GroupsActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 *	未解决问题:点击条目的时候  当直播室为高级会员直播室的时候  判断用户是否为会员  如果是会员才能进入  
 *
 */
public class Fragment_Live extends BaseFragment implements OnItemClickListener,
		OnRefreshListener2<ListView> {
	private View view;
	private PullToRefreshListView lv_live;
	private MyAdapter adapter;
	private ListView lv_hori;
	private HoriAdaprer hadapter;
	private List<LiveRoom> lrs;//存放群id和群名
	private List<LiveMessage> lms;
	//拿到环信群id的集合..
	private List<String> groupIdLists = new ArrayList<String>();
	private HttpUtils httpUtils = new HttpUtils();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_live, container, false);
		initView(view);
		initCont();
		return view;
	}

	private void initView(View v) {
		lv_hori = (ListView) v.findViewById(R.id.lv_hori);
	}

	// 初始化点击事件和数据
	private void initCont() {
		lrs = new ArrayList<Fragment_Live.LiveRoom>();
		lms = new ArrayList<Fragment_Live.LiveMessage>();
		lv_hori.setOnItemClickListener(this);
	//	lv_live.setOnRefreshListener(this);
		// lv_live.setMode(Mode.BOTH);
		// 直播区域adapter
		adapter = new MyAdapter();
		// 直播室数据adapter
		hadapter = new HoriAdaprer();
	//	lv_live.setAdapter(adapter);
		lv_hori.setAdapter(hadapter);
	}

	// 当fragment显示的时候判断是否加载过数据如果没有加载就加载
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			if((lrs.size()==0)) {
				getLiveRoome();
			}
		}
	}

	// 获取直播室
	private void getLiveRoome() {
		showProgressDialog("加载中...");
		MyApplication.ahc.post(AppUrl.usergetLiveRoomList,
				new JsonHttpResponseHandler() {
					@SuppressWarnings("unused")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						System.out.println("zuo直播室+" + response.toString());
						LogUtils.i("直播室数据===="+response.toString());
						String message = response.optString("message");
						JSONArray optJSONArray = response.optJSONArray("list");
						if (optJSONArray != null) {
							for (int i = 0; i < optJSONArray.length(); i++) {
								JSONObject j = optJSONArray.optJSONObject(i);
								String groupId = j.optString("groupId");
								String name = j.optString("name");
								int roomId = j.optInt("roomId");
								lrs.add(new LiveRoom(groupId, name,roomId));
							}
							hadapter.notifyDataSetChanged();
							if (lrs.size() != 0) {
							//	getLiveMessage(lrs.get(0).roomId);
							} else {
								dismissProgressDialog();
								Toast.makeText(getActivity(), "暂无数据!",
										Toast.LENGTH_SHORT).show();
							}
						}
					}

				});
		hadapter.notifyDataSetChanged();
	}

	/**
	 * TODO
	 * 
	 * @param roomId
	 *            2016年3月29日
	 */
	// 获取指定直播室数据
	private void getLiveMessage(int roomId) {
		RequestParams params = new RequestParams();
		params.put("roomId", roomId);

		MyApplication.ahc.post(AppUrl.usergetLiveRoomDiscussList, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						JSONArray optJSONArray = response.optJSONArray("list");
						String nubs = response.optString("nubs");
						dismissProgressDialog();
					}
				});

	}

	class LiveRoom {
		String groupId;
		String name;
		int roomId;

		public LiveRoom(String groupId, String name,int roomId) {
			super();
			this.groupId = groupId;
			this.name = name;
			this.roomId = roomId;
		}

	}

	class LiveMessage {
		int msgId;
		String userId;
		String userName, createDate, msg, name, url;

		public LiveMessage(int msgId, String userId, String userName,
				String createDate, String msg, String name, String url) {
			super();
			this.msgId = msgId;
			this.userId = userId;
			this.userName = userName;
			this.createDate = createDate;
			this.msg = msg;
			this.name = name;
			this.url = url;
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return lms == null ? 0 : lms.size();
		}

		@Override
		public Object getItem(int position) {
			return lms.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_live, null);
				h.im_head_live = (CircleImageView) v
						.findViewById(R.id.im_head_live);
				h.tv_message_live = (TextView) v
						.findViewById(R.id.tv_message_live);
				h.tv_time_live = (TextView) v.findViewById(R.id.tv_time_live);
				h.tv_name_live = (TextView) v.findViewById(R.id.tv_name_live);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_message_live.setText("" + lms.get(position).msg);
			h.tv_time_live.setText("" + lms.get(position).createDate);
			h.tv_name_live.setText("" + lms.get(position).userName);
			ImageLoader.getInstance()
					.displayImage(lms.get(position).url, h.im_head_live,
							MyApplication.getOptions(R.drawable.person));
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_live;
		TextView tv_name_live, tv_time_live, tv_message_live;
	}

	class HoriAdaprer extends BaseAdapter {

		@Override
		public int getCount() {
			return lrs == null ? 0 : lrs.size();
		}

		@Override
		public Object getItem(int position) {
			return lrs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			HoriHolder h;
			if (v == null) {
				h = new HoriHolder();
			//	v = View.inflate(getActivity(), R.layout.item_textview, null);
				v = View.inflate(getActivity(), R.layout.item_group_name, null);
			//	h.tv_title_item = (TextView) v.findViewById(R.id.tv_title_item);
			//	h.rl_textview = (RelativeLayout) v
			//			.findViewById(R.id.rl_textview);
				h.tv_group = (TextView)v.findViewById(R.id.tv_group);
				h.ll_group = (LinearLayout)v.findViewById(R.id.ll_group);
				v.setTag(h);
			} else {
				h = (HoriHolder) v.getTag();
			}
			h.tv_group.setText(lrs.get(position).name);
		/*//	h.tv_title_item.setText("" + lrs.get(position).name);
			h.tv_group.setText(""+lrs.get(position).name);
			if (selePosition == position) {
		//		h.rl_textview.setBackgroundResource(R.drawable.red_cen_co);
		//		h.tv_title_item.setTextColor(getResources().getColor(
		//				R.color.white));
				h.ll_group.setBackgroundResource(R.drawable.red_cen_co);
				h.tv_group.setTextColor(getResources().getColor(R.color.white));
			} else {
			//	h.rl_textview.setBackgroundResource(R.drawable.tran_cen_co);
			//	h.tv_title_item.setTextColor(getResources().getColor(
			//			R.color.black));
				h.ll_group.setBackgroundResource(R.drawable.tran_cen_co);
				h.tv_group.setTextColor(getResources().getColor(R.color.black));
			}*/
			return v;
		}
	}

	class HoriHolder {
		TextView tv_title_item;
		RelativeLayout rl_textview;
		TextView tv_group;
		LinearLayout ll_group;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,
			long id) {
		if(MyApplication.islogin) {
			//如果聊天室为高级会员聊天室
			if(lrs.get(position).name.equals("高级会员直播室")) {
				//TODO  发送网络请求给后台  查询该用户是否为会员  如果是会员  则跳转到对应的聊天室  
				/**
				 * 这个位置需要发送网络请求给后台
				 */
				Toast.makeText(getContext(), "您还不是会员，不能进入", Toast.LENGTH_SHORT).show();
			}
			LogUtils.i("加入聊天室。。。");
			showProgressDialog("正在进入聊天室");
			com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
			params.addBodyParameter("lvname", lrs.get(position).groupId);
			params.addBodyParameter("uid", MyApplication.userId);
			LogUtils.i("groupId====="+lrs.get(position).groupId);
			LogUtils.i("uid===="+MyApplication.userId);
			RequestCallBack<String> callBack = new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("onFailure====");
					Toast.makeText(getContext(), "网络连接失败", 0).show();
				}
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					try {
						JSONObject jsonObject = new JSONObject(responseInfo.result);
						String result = jsonObject.getString("result");
						LogUtils.i("result==="+result);
						if(result.equals("0")) {
							//加入成功  发送请求获取用户权限...
							getUserPower(position);
						} else if(result.equals("1")) {
							Toast.makeText(getContext(), "进入直播间失败", 0).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			new HttpUtils().send(HttpMethod.POST, AppUrl.applyToGroup,params, callBack);
			//跳转到聊天室的页面...
		} else {
			Toast.makeText(getContext(), "请先登录账号", 0).show();
		}
		
	}

	private void getUserPower(final int position) {
		com.lidroid.xutils.http.RequestParams param = new com.lidroid.xutils.http.RequestParams();
		param.addBodyParameter("lvname",lrs.get(position).groupId);
		param.addBodyParameter("uid", MyApplication.userId);
		RequestCallBack<String> callBacks = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					dismissProgressDialog();
					JSONObject jsoinObject = new JSONObject(responseInfo.result);
					String result = jsoinObject.getString("result");
					LogUtils.i("result===="+result);
					if(result.equals("0")) {
						Log.i("test3", "取得用户权限成功");
						String statu = jsoinObject.getString("statu");
						if(statu.equals("0")) {
							MyApplication.isUserSpeak = true;
						} else {
							MyApplication.isUserSpeak = false;
						}
						Intent intent = new Intent(getActivity(), ChatActivity.class);
						intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
						intent.putExtra("userId", lrs.get(position).groupId);
						LogUtils.i("groupId===="+lrs.get(position).groupId);
						startActivityForResult(intent, 0);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		};
		new HttpUtils().send(HttpMethod.POST, AppUrl.querryuserPower,param, callBacks);
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (lrs.size() != 0) {
			lms.clear();
	//		getLiveMessage(lrs.get(selePosition).roomId);
		}

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!MyApplication.islogin) {
	//		Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
		}
	}
	
}
