package com.femto.post.fragment;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.post.R;
import com.femto.post.activity.Activity_Article;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.util.SpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("ValidFragment")
public class Fragment_Grail extends BaseFragment implements
		OnItemClickListener, OnRefreshListener2<ListView> {
	private View view;
	private PullToRefreshListView lv_common;
	private MyAdapter adapter;
	private int statu;
	private List<News> news;
	//所有的新闻题目
	private List<News> allNews = new ArrayList<News>();
	private boolean isfirst;
	private HttpUtils httpUtils;
	private int pageIndex = 1;
	@SuppressLint("ValidFragment")
	public Fragment_Grail(int statu, Boolean isfirst) {
		super();
		this.statu = statu;
		Log.i("test", "statu="+statu);
		this.isfirst = isfirst;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_listview, container, false);
		initView(view);
		initCont();
		return view;
	}

	private void initView(View v) {
		lv_common = (PullToRefreshListView) v.findViewById(R.id.lv_common);

	}

	private void initCont() {
		allNews = new ArrayList<Fragment_Grail.News>();
		lv_common.setOnItemClickListener(this);
		lv_common.setOnRefreshListener(this);
	//	lv_common.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		lv_common.setAdapter(adapter);
		if (isfirst) {
			getData(statu,0);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && allNews.size() == 0) {
			getData(statu,0);
		}
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return allNews == null ? 0 : allNews.size();
		}

		@Override
		public Object getItem(int position) {
			return allNews.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_common, null);
				h.title = (TextView) v.findViewById(R.id.title);
				h.tv_time_news = (TextView) v.findViewById(R.id.tv_time_news);
				//h.tv_secondtitle_news = (TextView) v
				//		.findViewById(R.id.tv_secondtitle_news);
				h.im_news = (ImageView) v.findViewById(R.id.im_news);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.title.setText(allNews.get(position).title);
			//h.tv_secondtitle_news.setText(news.get(position).info);
			h.tv_time_news.setText(news.get(position).createdate);
			String picUrl = AppUrl.BASEURL+"/YouBi/"+allNews.get(position).url;
			ImageLoader.getInstance().displayImage(picUrl,
					h.im_news, MyApplication.getOptions(R.drawable.defalutimg));
			return v;
		}
	}

	class MyHolder {
		TextView title,/* tv_secondtitle_news,*/ tv_time_news;
		ImageView im_news;
	}

	private void getData(int statu,int index) {

		RequestParams params = new RequestParams();
//		params.addBodyParameter("status", status+"");
		params.addBodyParameter("status", statu+"");
		params.addBodyParameter("pageModel.pageIndex", index+"");
		Log.i("test","status = "+statu);
		showProgressDialog("加载中...");
		httpUtils = new HttpUtils();
		RequestCallBack<String> callBack = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException response, String arg1) {
				dismissProgressDialog();
				if(lv_common!=null&&lv_common.isRefreshing()) {
					lv_common.onRefreshComplete();
				}
				Toast.makeText(getActivity(), "网络连接超时", 0).show();
				Log.i("test", "网络连接异常");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					
					Log.i("test","连接成功...");
					dismissProgressDialog();
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					String result = jsonObject.getString("result");
					Log.i("test3", "获取数据为=================+"+jsonObject.toString());
					Log.i("test", "result="+result);
					if(result.equals("0")) {
						pageIndex ++;
						//TODO待后期完善  pullToRefresh并不是这么用滴
						//当加载数据成功的时候  停止下拉刷新...
						if(lv_common!=null&&lv_common.isRefreshing()) {
							lv_common.onRefreshComplete();
						}
						Log.i("test","获取到数据了");
						String json = jsonObject.getString("list");
						Log.i("test","获取到的json数据为"+json);
						Gson gson = new Gson();
						news = new ArrayList<Fragment_Grail.News>();
						//news = gosn.fromJson(json, news);
					//	news = gosn.fromJson(json, new TypeToken<List<Fragment_Grail.News>>(){}.getTy);
						 news = gson.fromJson(json,  
					                new TypeToken<List<Fragment_Grail.News>>() {  
					                }.getType());
						 //每次加载到的数据放入一个总的集合当中...
						 allNews.addAll(news);
						 //刷新数据...
						 Log.i("test3","图片的地址为"+allNews.get(0).url);
						 adapter.notifyDataSetChanged();
					} else {
						if(result.equals("3")) {
							Toast.makeText(getActivity(), "已加载全部信息..", 0).show();
							if(lv_common!=null&&lv_common.isRefreshing()) {
								lv_common.onRefreshComplete();
							}
						} else {
							Toast.makeText(getActivity(), "获取数据异常", 0).show();
							if(lv_common!=null&&lv_common.isRefreshing()) {
								lv_common.onRefreshComplete();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "获取数据失败", 0).show();
					if(lv_common!=null&&lv_common.isRefreshing()) {
						lv_common.onRefreshComplete();
					}
				}
			}
		};
		httpUtils.send(HttpMethod.POST, AppUrl.getInFo, params,callBack);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i("test3", "posititon========"+position);
		String id1 = allNews.get(position-1).id;
		Log.i("test3", "id1="+id1);
		String picUrl = allNews.get(position-1).url;
		SpUtil.writeSp(getActivity(), "id", id1);
		//Log.i("test", "id1="+id1);
		Intent intent = new Intent(getActivity(),Activity_Article.class);
		//根据id查文章...
		/*Bundle bundle = new Bundle();
		bundle.putString("id", id1);*/
		startActivity(intent);
		/*position = position - 1;
		Intent intent = new Intent(getActivity(), Activity_Article.class);
		//intent.putExtra("url", news.get(position).url);
		startActivity(intent);*/
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		allNews.clear();
		pageIndex = 1;
		getData(statu,pageIndex);
	}
	//上拉加载更多
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getData(statu,pageIndex);
	}
	class News {
		//String url, headTitle, secondayTitle, text, createTime, picUrl;
		String id,title,url,info,createdate;
		//long date;

		/*//public News(String url, String headTitle, String secondayTitle,
				String text, String createTime, String picUrl) {
			super();
			this.url = url;
			this.headTitle = headTitle;
			this.secondayTitle = secondayTitle;
			this.text = text;
			this.createTime = createTime;
			this.picUrl = picUrl;
		}*/
		public News(String id,String title,String url,String info,String createdate) {
			super();
			this.id = id;
			this.url = url;
			this.title = title;
			this.info = info;
			this.createdate = createdate;
			//this.date = date;
		}
	}
}
