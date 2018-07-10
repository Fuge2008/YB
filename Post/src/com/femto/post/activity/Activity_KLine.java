package com.femto.post.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.limc.androidcharts.entity.LineEntity;
import cn.limc.androidcharts.entity.TitleValueColorEntity;
import cn.limc.androidcharts.view.MACandleStickChart;
import cn.limc.androidcharts.view.PieChart;

import com.android.futures.entity.OHLCEntity;
import com.android.futures.entity.TimesEntity;
import com.android.futures.util.LogUtils;
import com.android.futures.util.PreferenceUtils;
import com.android.futures.view.KChartsView;
import com.android.futures.view.TimesView;
import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.customview.DoubleHistogram;
import com.femto.post.customview.ViewVOL;
import com.femto.post.entity.MyTask;
import com.femto.post.entity.Quotation;
import com.femto.post.entity.Tables;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Activity_KLine extends BaseActivity {
	private RelativeLayout rl_left;
	private TextView tv_title, tv_zzzd;
	private RadioButton rb_time, rb_dayk, rb_weekk;
	private RadioButton rb_vol, rb_macd, rb_kdj,rb_rsi;
	// K线图
	//private KChartsView mMyChartsView;
	private ViewVOL my_charts_viewwek;
	// 分时
	private TimesView mTimesView;
	private JSONArray mDatas;
	private DoubleHistogram dh;
	private MyTask myTask;
	private Tables tables;
	//
	private TextView tv_todayopen, tv_yesopen, tv_hight, tv_lowest, tv_zf,
			tv_turnover, tv_turnovere, tv_cprice;
	private int p;
	private int pp;
	private LinearLayout ll_zz, ll_vol;
	private MACandleStickChart macandlestickchart, macandlestickchartweek;
	private List<OHLCEntity> dayohlc;
	private List<OHLCEntity> weekohlc;
	private List<OHLCEntity> timeohlc;

	private PieChart piechart;
	private ArrayList<Tables> daytables;
	private ArrayList<Tables> mintables;
	private ArrayList<Tables> weekdables;
	private List<TimesEntity> timesList = new ArrayList<TimesEntity>();

	private List<OHLCEntity> weekohlcs;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
			break;
		case R.id.rb_time:
			mTimesView.setVisibility(View.VISIBLE);
		//	mMyChartsView.setVisibility(View.INVISIBLE);
			macandlestickchart.setVisibility(View.INVISIBLE);
			ll_vol.setVisibility(View.INVISIBLE);
			break;
		case R.id.rb_dayk:
			// setKLineDay();

			macandlestickchart.setVisibility(View.VISIBLE);
			mTimesView.setVisibility(View.INVISIBLE);
			macandlestickchartweek.setVisibility(View.GONE);
			my_charts_viewwek.setVisibility(View.VISIBLE);
			my_charts_viewweek_2.setVisibility(View.GONE);
			ll_vol.setVisibility(View.VISIBLE);

			break;
		case R.id.rb_weekk:
			// setKLineWeek();
			macandlestickchartweek.setVisibility(View.VISIBLE);
			macandlestickchart.setVisibility(View.GONE);
			mTimesView.setVisibility(View.INVISIBLE);
			ll_vol.setVisibility(View.VISIBLE);
			my_charts_viewwek.setVisibility(View.GONE);
			my_charts_viewweek_2.setVisibility(View.VISIBLE);

			break;
		case R.id.rb_vol:
			my_charts_viewwek.setTitles("VOL");
			break;
		case R.id.rb_macd:
			my_charts_viewwek.setTitles("MACD");
			break;
		case R.id.rb_kdj:
			my_charts_viewwek.setTitles("KDJ");
			break;
		case R.id.rb_rsi:
			my_charts_viewwek.setTitles("RSI");
			break;
		default:
			break;
		}
	}

	// 初始化view
	@Override
	public void initView() {
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_zzzd = (TextView) findViewById(R.id.tv_zzzd);
		rb_time = (RadioButton) findViewById(R.id.rb_time);
		rb_dayk = (RadioButton) findViewById(R.id.rb_dayk);
		rb_weekk = (RadioButton) findViewById(R.id.rb_weekk);
		rb_vol = (RadioButton) findViewById(R.id.rb_vol);
		rb_macd = (RadioButton) findViewById(R.id.rb_macd);
		rb_kdj = (RadioButton) findViewById(R.id.rb_kdj);
		rb_rsi = (RadioButton) findViewById(R.id.rb_rsi);
	//	mMyChartsView = (KChartsView) findViewById(R.id.my_charts_view);
		my_charts_viewwek = (ViewVOL) findViewById(R.id.my_charts_viewweek);
		my_charts_viewweek_2 = (ViewVOL)findViewById(R.id.my_charts_viewweek_2);
		mTimesView = (TimesView) findViewById(R.id.my_fenshi_view);
		dh = (DoubleHistogram) findViewById(R.id.dh);
		tv_todayopen = (TextView) findViewById(R.id.tv_todayopen);
		tv_yesopen = (TextView) findViewById(R.id.tv_yesopen);
		tv_hight = (TextView) findViewById(R.id.tv_hight);
		tv_lowest = (TextView) findViewById(R.id.tv_lowest);
		tv_zf = (TextView) findViewById(R.id.tv_zf);
		tv_turnover = (TextView) findViewById(R.id.tv_turnover);
		tv_turnovere = (TextView) findViewById(R.id.tv_turnovere);
		tv_cprice = (TextView) findViewById(R.id.tv_cprice);
		ll_zz = (LinearLayout) findViewById(R.id.ll_zz);
		ll_vol = (LinearLayout) findViewById(R.id.ll_vol);
		macandlestickchart = (MACandleStickChart) findViewById(R.id.macandlestickchart);
		macandlestickchartweek = (MACandleStickChart) findViewById(R.id.macandlestickchartweek);
		piechart = (PieChart) findViewById(R.id.piechart);
		//Michael临时办法   
		RadioGroup rg = (RadioGroup) findViewById(R.id.rg);
		rg.setVisibility(View.GONE);
		//rb_vol.setChecked(true);
		// setTime();
		// initOHLC();
		initParams();
	}

	@Override
	public void initUtils() {
	}

	@Override
	public void Control() {
		rl_left.setOnClickListener(this);
		rb_time.setOnClickListener(this);
		rb_dayk.setOnClickListener(this);
		rb_weekk.setOnClickListener(this);
		rb_vol.setOnClickListener(this);
		rb_macd.setOnClickListener(this);
		rb_kdj.setOnClickListener(this);
		rb_rsi.setOnClickListener(this);
//		rb_vol.setChecked(true);
		daytables = new ArrayList<Tables>();
		mintables = new ArrayList<Tables>();
		weekdables = new ArrayList<Tables>();
		setData();
		getTimeData(5);
		getWeekData(4);
		getDayK(4);

		initPieChart();
		//初始化柱状图....
		initHistogram();
	}
	
	//初始化柱状图...
	private void initHistogram() {
		Log.i("test3", "获取柱状图数据中....");
		showProgressDialog("加载中...");
		com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
		//params.addBodyParameter("wenJiaoId", qt.getId()+"");
		//params.addBodyParameter("num", tables.getNum()+"");
		params.addBodyParameter("TradingCode", tables.getCode());
		params.addBodyParameter("CollectionName", qt.getId()+"");
		RequestCallBack<String> callBack = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i("test3", "获取柱状图连接网络失败...");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Log.i("test3", "获取柱状图网络连接成功..");
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					Log.i("test3", "获取柱状图的数据为"+jsonObject.toString());
					String result = jsonObject.getString("result");
					Log.i("test3", "柱状图的result为"+result);
					if(result.equals("0")) {
						Log.i("test3", "获取柱状图数据成功...");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.i("test3", "获取柱状图数据遇到异常..");
				}
			}
		};
//		new HttpUtils().send(HttpMethod.POST, AppUrl.usergetInOutPriceToDay10, params,callBack);
		new HttpUtils().send(HttpMethod.POST, AppUrl.usergetZhuInfo, params,callBack);
	}

	// 设置数据方便做刷新处理
	private void setData() {
		dh.setData(noteTextValues, realityValues, thinkValues);
		tv_todayopen.setText("" + tables.getOpenPrice());
		tv_yesopen.setText("" + tables.getYesterBalancePrice());
		tv_hight.setText("" + tables.getHighPrice());
		tv_lowest.setText("" + tables.getLowPrice());
		tv_zf.setText("" + tables.getCurrentGains() + "%");
//		tv_turnover.setText(""
//				+ MyApplication.df.format(tables.getTotalAmount() / 10000d)
//				+ "万");
//		tv_turnovere.setText(""
//				+ MyApplication.df.format(tables.getTotalMoney() / 100000000d)
//				+ "亿");
	//	tv_cprice.setText(""
	//			+ MyApplication.qs.get(p).getTables().get(0).getCurPrice());
	//	tv_zzzd.setText(""
	//			+ MyApplication.qs.get(p).getTables().get(0).getCurrentGains()+"%");
//		if (MyApplication.qs.get(p).getTables().get(0).getCurrentGains() < 0) {
//
//			ll_zz.setBackgroundColor(getResources().getColor(R.color.green));
//		} else {
//			ll_zz.setBackgroundColor(getResources().getColor(R.color.red));
//		}
		//Michael暂注...
		//tv_title.setText("综合指数");
		tv_title.setText(tables.getFullname());
	}

	@Override
	public void setContentView() {
		Log.i("test3", "k线图布局加载中...");
		setContentView(R.layout.activity_kline);
		MyApplication.addActivity(this);
//		initParams();
	}

	// 全局化qs便于数据整体刷新；纪录p为哪一个Quotation,pp为Quotation中的哪一个Tables
	private void initParams() {
		// tables = (Tables) getIntent().getSerializableExtra("tabls");
		p = getIntent().getIntExtra("p", 0);
		pp = getIntent().getIntExtra("pp", 0);
		qt = MyApplication.qs.get(p);
//		tables = qt.getTables().get(pp);
		dayohlc = new ArrayList<OHLCEntity>();
		timeohlc = new ArrayList<OHLCEntity>();
		weekohlc = new ArrayList<OHLCEntity>();
	}

	// 分时
	private void setTime() {
		mTimesView.setVisibility(View.VISIBLE);
		//mMyChartsView.setVisibility(View.INVISIBLE);
		macandlestickchart.setVisibility(View.INVISIBLE);
		ll_vol.setVisibility(View.INVISIBLE);
		try {
			mDatas = new JSONObject(
					"{\"error\":\"成功\",\"status\":0,\"data\":[{\"changeAmount\":5,\"weightedIndex\":6855,\"time\":\"09:31\",\"sell\":970,\"buy\":970,\"volume\":30,\"changeRate\":0.073,\"nonWeightedIndex\":6855},{\"changeAmount\":6,\"weightedIndex\":6855,\"time\":\"09:32\",\"sell\":590,\"buy\":590,\"volume\":40,\"changeRate\":0.088,\"nonWeightedIndex\":6855},{\"changeAmount\":6,\"weightedIndex\":6855,\"time\":\"09:33\",\"sell\":580,\"buy\":580,\"volume\":40,\"changeRate\":0.088,\"nonWeightedIndex\":6856},{\"changeAmount\":7,\"weightedIndex\":6856,\"time\":\"09:34\",\"sell\":626,\"buy\":626,\"volume\":54,\"changeRate\":0.102,\"nonWeightedIndex\":6856},{\"changeAmount\":7,\"weightedIndex\":6856,\"time\":\"09:35\",\"sell\":756,\"buy\":756,\"volume\":54,\"changeRate\":0.102,\"nonWeightedIndex\":6857},{\"changeAmount\":8,\"weightedIndex\":6856,\"time\":\"09:36\",\"sell\":778,\"buy\":778,\"volume\":66,\"changeRate\":0.117,\"nonWeightedIndex\":6857},{\"changeAmount\":8,\"weightedIndex\":6856,\"time\":\"09:37\",\"sell\":708,\"buy\":708,\"volume\":66,\"changeRate\":0.117,\"nonWeightedIndex\":6858},{\"changeAmount\":8,\"weightedIndex\":6856,\"time\":\"09:38\",\"sell\":708,\"buy\":708,\"volume\":66,\"changeRate\":0.117,\"nonWeightedIndex\":6858},{\"changeAmount\":9,\"weightedIndex\":6857,\"time\":\"09:39\",\"sell\":684,\"buy\":684,\"volume\":78,\"changeRate\":0.131,\"nonWeightedIndex\":6858},{\"changeAmount\":9,\"weightedIndex\":6857,\"time\":\"09:40\",\"sell\":696,\"buy\":696,\"volume\":88,\"changeRate\":0.131,\"nonWeightedIndex\":6859},{\"changeAmount\":9,\"weightedIndex\":6857,\"time\":\"09:41\",\"sell\":580,\"buy\":580,\"volume\":88,\"changeRate\":0.131,\"nonWeightedIndex\":6859},{\"changeAmount\":10,\"weightedIndex\":6857,\"time\":\"09:42\",\"sell\":540,\"buy\":540,\"volume\":98,\"changeRate\":0.146,\"nonWeightedIndex\":6859},{\"changeAmount\":10,\"weightedIndex\":6857,\"time\":\"09:43\",\"sell\":570,\"buy\":570,\"volume\":98,\"changeRate\":0.146,\"nonWeightedIndex\":6860},{\"changeAmount\":12,\"weightedIndex\":6858,\"time\":\"09:44\",\"sell\":1040,\"buy\":1040,\"volume\":118,\"changeRate\":0.175,\"nonWeightedIndex\":6861},{\"changeAmount\":12,\"weightedIndex\":6858,\"time\":\"09:45\",\"sell\":1180,\"buy\":1180,\"volume\":118,\"changeRate\":0.175,\"nonWeightedIndex\":6862},{\"changeAmount\":10,\"weightedIndex\":6858,\"time\":\"09:46\",\"sell\":928,\"buy\":928,\"volume\":130,\"changeRate\":0.146,\"nonWeightedIndex\":6861},{\"changeAmount\":10,\"weightedIndex\":6858,\"time\":\"09:47\",\"sell\":708,\"buy\":708,\"volume\":130,\"changeRate\":0.146,\"nonWeightedIndex\":6860},{\"changeAmount\":9,\"weightedIndex\":6858,\"time\":\"09:48\",\"sell\":596,\"buy\":596,\"volume\":140,\"changeRate\":0.131,\"nonWeightedIndex\":6859},{\"changeAmount\":9,\"weightedIndex\":6858,\"time\":\"09:49\",\"sell\":590,\"buy\":590,\"volume\":140,\"changeRate\":0.131,\"nonWeightedIndex\":6859},{\"changeAmount\":10,\"weightedIndex\":6858,\"time\":\"09:50\",\"sell\":560,\"buy\":560,\"volume\":150,\"changeRate\":0.146,\"nonWeightedIndex\":6859},{\"changeAmount\":8,\"weightedIndex\":6858,\"time\":\"09:51\",\"sell\":608,\"buy\":608,\"volume\":162,\"changeRate\":0.117,\"nonWeightedIndex\":6859},{\"changeAmount\":8,\"weightedIndex\":6858,\"time\":\"09:52\",\"sell\":708,\"buy\":708,\"volume\":162,\"changeRate\":0.117,\"nonWeightedIndex\":6858},{\"changeAmount\":8,\"weightedIndex\":6858,\"time\":\"09:53\",\"sell\":708,\"buy\":708,\"volume\":174,\"changeRate\":0.117,\"nonWeightedIndex\":6858},{\"changeAmount\":9,\"weightedIndex\":6858,\"time\":\"09:54\",\"sell\":636,\"buy\":636,\"volume\":182,\"changeRate\":0.131,\"nonWeightedIndex\":6858},{\"changeAmount\":9,\"weightedIndex\":6858,\"time\":\"09:55\",\"sell\":488,\"buy\":488,\"volume\":182,\"changeRate\":0.131,\"nonWeightedIndex\":6859},{\"changeAmount\":7,\"weightedIndex\":6858,\"time\":\"09:56\",\"sell\":496,\"buy\":496,\"volume\":196,\"changeRate\":0.102,\"nonWeightedIndex\":6858},{\"changeAmount\":7,\"weightedIndex\":6858,\"time\":\"09:57\",\"sell\":756,\"buy\":756,\"volume\":196,\"changeRate\":0.102,\"nonWeightedIndex\":6857},{\"changeAmount\":8,\"weightedIndex\":6858,\"time\":\"09:58\",\"sell\":796,\"buy\":796,\"volume\":206,\"changeRate\":0.117,\"nonWeightedIndex\":6857},{\"changeAmount\":8,\"weightedIndex\":6858,\"time\":\"09:59\",\"sell\":610,\"buy\":610,\"volume\":206,\"changeRate\":0.117,\"nonWeightedIndex\":6858},{\"changeAmount\":6,\"weightedIndex\":6858,\"time\":\"10:00\",\"sell\":640,\"buy\":640,\"volume\":226,\"changeRate\":0.088,\"nonWeightedIndex\":6857},{\"changeAmount\":6,\"weightedIndex\":6858,\"time\":\"10:01\",\"sell\":1120,\"buy\":1120,\"volume\":226,\"changeRate\":0.088,\"nonWeightedIndex\":6856},{\"changeAmount\":5,\"weightedIndex\":6858,\"time\":\"10:02\",\"sell\":664,\"buy\":664,\"volume\":234,\"changeRate\":0.073,\"nonWeightedIndex\":6855},{\"changeAmount\":4,\"weightedIndex\":6858,\"time\":\"10:03\",\"sell\":664,\"buy\":664,\"volume\":254,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":4,\"weightedIndex\":6858,\"time\":\"10:04\",\"sell\":1180,\"buy\":1180,\"volume\":254,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":5,\"weightedIndex\":6858,\"time\":\"10:05\",\"sell\":850,\"buy\":850,\"volume\":264,\"changeRate\":0.073,\"nonWeightedIndex\":6854},{\"changeAmount\":5,\"weightedIndex\":6858,\"time\":\"10:06\",\"sell\":590,\"buy\":590,\"volume\":264,\"changeRate\":0.073,\"nonWeightedIndex\":6855},{\"changeAmount\":5,\"weightedIndex\":6858,\"time\":\"10:07\",\"sell\":590,\"buy\":590,\"volume\":264,\"changeRate\":0.073,\"nonWeightedIndex\":6855},{\"changeAmount\":6,\"weightedIndex\":6857,\"time\":\"10:08\",\"sell\":492,\"buy\":492,\"volume\":272,\"changeRate\":0.088,\"nonWeightedIndex\":6855},{\"changeAmount\":6,\"weightedIndex\":6857,\"time\":\"10:09\",\"sell\":448,\"buy\":448,\"volume\":272,\"changeRate\":0.088,\"nonWeightedIndex\":6856},{\"changeAmount\":7,\"weightedIndex\":6857,\"time\":\"10:10\",\"sell\":480,\"buy\":480,\"volume\":284,\"changeRate\":0.102,\"nonWeightedIndex\":6856},{\"changeAmount\":7,\"weightedIndex\":6857,\"time\":\"10:11\",\"sell\":708,\"buy\":708,\"volume\":284,\"changeRate\":0.102,\"nonWeightedIndex\":6857},{\"changeAmount\":7,\"weightedIndex\":6857,\"time\":\"10:12\",\"sell\":660,\"buy\":660,\"volume\":284,\"changeRate\":0.102,\"nonWeightedIndex\":6857},{\"changeAmount\":5,\"weightedIndex\":6857,\"time\":\"10:13\",\"sell\":784,\"buy\":784,\"volume\":298,\"changeRate\":0.073,\"nonWeightedIndex\":6855},{\"changeAmount\":5,\"weightedIndex\":6857,\"time\":\"10:14\",\"sell\":826,\"buy\":826,\"volume\":298,\"changeRate\":0.073,\"nonWeightedIndex\":6855},{\"changeAmount\":4,\"weightedIndex\":6857,\"time\":\"10:15\",\"sell\":800,\"buy\":800,\"volume\":308,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":4,\"weightedIndex\":6857,\"time\":\"10:16\",\"sell\":560,\"buy\":560,\"volume\":308,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":4,\"weightedIndex\":6857,\"time\":\"10:17\",\"sell\":590,\"buy\":590,\"volume\":308,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":3,\"weightedIndex\":6857,\"time\":\"10:18\",\"sell\":788,\"buy\":788,\"volume\":324,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":3,\"weightedIndex\":6857,\"time\":\"10:19\",\"sell\":944,\"buy\":944,\"volume\":324,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":3,\"weightedIndex\":6857,\"time\":\"10:20\",\"sell\":764,\"buy\":764,\"volume\":334,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":3,\"weightedIndex\":6857,\"time\":\"10:21\",\"sell\":590,\"buy\":590,\"volume\":334,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":1,\"weightedIndex\":6857,\"time\":\"10:22\",\"sell\":746,\"buy\":746,\"volume\":356,\"changeRate\":0.015,\"nonWeightedIndex\":6852},{\"changeAmount\":1,\"weightedIndex\":6857,\"time\":\"10:23\",\"sell\":1298,\"buy\":1298,\"volume\":356,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6856,\"time\":\"10:24\",\"sell\":754,\"buy\":754,\"volume\":366,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6856,\"time\":\"10:25\",\"sell\":590,\"buy\":590,\"volume\":366,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":0,\"weightedIndex\":6856,\"time\":\"10:26\",\"sell\":590,\"buy\":590,\"volume\":376,\"changeRate\":0,\"nonWeightedIndex\":6850},{\"changeAmount\":0,\"weightedIndex\":6856,\"time\":\"10:27\",\"sell\":514,\"buy\":514,\"volume\":384,\"changeRate\":0,\"nonWeightedIndex\":6850},{\"changeAmount\":0,\"weightedIndex\":6856,\"time\":\"10:28\",\"sell\":472,\"buy\":472,\"volume\":384,\"changeRate\":0,\"nonWeightedIndex\":6850},{\"changeAmount\":0,\"weightedIndex\":6856,\"time\":\"10:29\",\"sell\":472,\"buy\":472,\"volume\":384,\"changeRate\":0,\"nonWeightedIndex\":6850},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:30\",\"sell\":464,\"buy\":464,\"volume\":392,\"changeRate\":0.029,\"nonWeightedIndex\":6851},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:31\",\"sell\":432,\"buy\":432,\"volume\":392,\"changeRate\":0.029,\"nonWeightedIndex\":6852},{\"changeAmount\":3,\"weightedIndex\":6856,\"time\":\"10:32\",\"sell\":660,\"buy\":660,\"volume\":404,\"changeRate\":0.044,\"nonWeightedIndex\":6852},{\"changeAmount\":3,\"weightedIndex\":6856,\"time\":\"10:33\",\"sell\":708,\"buy\":708,\"volume\":404,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":4,\"weightedIndex\":6856,\"time\":\"10:34\",\"sell\":604,\"buy\":604,\"volume\":412,\"changeRate\":0.058,\"nonWeightedIndex\":6853},{\"changeAmount\":4,\"weightedIndex\":6856,\"time\":\"10:35\",\"sell\":448,\"buy\":448,\"volume\":412,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:36\",\"sell\":540,\"buy\":540,\"volume\":422,\"changeRate\":0.029,\"nonWeightedIndex\":6852},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:37\",\"sell\":590,\"buy\":590,\"volume\":422,\"changeRate\":0.029,\"nonWeightedIndex\":6852},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:38\",\"sell\":560,\"buy\":560,\"volume\":422,\"changeRate\":0.029,\"nonWeightedIndex\":6852},{\"changeAmount\":3,\"weightedIndex\":6856,\"time\":\"10:39\",\"sell\":590,\"buy\":590,\"volume\":432,\"changeRate\":0.044,\"nonWeightedIndex\":6852},{\"changeAmount\":4,\"weightedIndex\":6856,\"time\":\"10:40\",\"sell\":588,\"buy\":588,\"volume\":440,\"changeRate\":0.058,\"nonWeightedIndex\":6853},{\"changeAmount\":4,\"weightedIndex\":6856,\"time\":\"10:41\",\"sell\":472,\"buy\":472,\"volume\":440,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":4,\"weightedIndex\":6856,\"time\":\"10:42\",\"sell\":440,\"buy\":440,\"volume\":440,\"changeRate\":0.058,\"nonWeightedIndex\":6854},{\"changeAmount\":3,\"weightedIndex\":6856,\"time\":\"10:43\",\"sell\":472,\"buy\":472,\"volume\":448,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":3,\"weightedIndex\":6856,\"time\":\"10:44\",\"sell\":472,\"buy\":472,\"volume\":448,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":3,\"weightedIndex\":6856,\"time\":\"10:45\",\"sell\":472,\"buy\":472,\"volume\":448,\"changeRate\":0.044,\"nonWeightedIndex\":6853},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:46\",\"sell\":532,\"buy\":532,\"volume\":458,\"changeRate\":0.029,\"nonWeightedIndex\":6852},{\"changeAmount\":2,\"weightedIndex\":6856,\"time\":\"10:47\",\"sell\":590,\"buy\":590,\"volume\":458,\"changeRate\":0.029,\"nonWeightedIndex\":6852},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:48\",\"sell\":490,\"buy\":490,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:49\",\"sell\":448,\"buy\":448,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:50\",\"sell\":472,\"buy\":472,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:51\",\"sell\":472,\"buy\":472,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:52\",\"sell\":472,\"buy\":472,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:53\",\"sell\":448,\"buy\":448,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:54\",\"sell\":472,\"buy\":472,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":1,\"weightedIndex\":6855,\"time\":\"10:55\",\"sell\":472,\"buy\":472,\"volume\":466,\"changeRate\":0.015,\"nonWeightedIndex\":6851},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"10:56\",\"sell\":536,\"buy\":536,\"volume\":486,\"changeRate\":-0.015,\"nonWeightedIndex\":6850},{\"changeAmount\":-2,\"weightedIndex\":6855,\"time\":\"10:57\",\"sell\":1168,\"buy\":1168,\"volume\":500,\"changeRate\":-0.029,\"nonWeightedIndex\":6848},{\"changeAmount\":-2,\"weightedIndex\":6855,\"time\":\"10:58\",\"sell\":826,\"buy\":826,\"volume\":500,\"changeRate\":-0.029,\"nonWeightedIndex\":6848},{\"changeAmount\":-2,\"weightedIndex\":6855,\"time\":\"10:59\",\"sell\":826,\"buy\":826,\"volume\":500,\"changeRate\":-0.029,\"nonWeightedIndex\":6848},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:00\",\"sell\":666,\"buy\":666,\"volume\":510,\"changeRate\":-0.015,\"nonWeightedIndex\":6848},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:01\",\"sell\":560,\"buy\":560,\"volume\":510,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:02\",\"sell\":590,\"buy\":590,\"volume\":510,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:03\",\"sell\":468,\"buy\":468,\"volume\":518,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":0,\"weightedIndex\":6855,\"time\":\"11:04\",\"sell\":348,\"buy\":348,\"volume\":522,\"changeRate\":0,\"nonWeightedIndex\":6849},{\"changeAmount\":0,\"weightedIndex\":6855,\"time\":\"11:05\",\"sell\":228,\"buy\":228,\"volume\":522,\"changeRate\":0,\"nonWeightedIndex\":6850},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:06\",\"sell\":434,\"buy\":434,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:07\",\"sell\":590,\"buy\":590,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:08\",\"sell\":560,\"buy\":560,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:09\",\"sell\":590,\"buy\":590,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:10\",\"sell\":590,\"buy\":590,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:11\",\"sell\":540,\"buy\":540,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-1,\"weightedIndex\":6855,\"time\":\"11:12\",\"sell\":590,\"buy\":590,\"volume\":532,\"changeRate\":-0.015,\"nonWeightedIndex\":6849},{\"changeAmount\":-2,\"weightedIndex\":6855,\"time\":\"11:13\",\"sell\":590,\"buy\":590,\"volume\":542,\"changeRate\":-0.029,\"nonWeightedIndex\":6848},{\"changeAmount\":-3,\"weightedIndex\":6854,\"time\":\"11:14\",\"sell\":610,\"buy\":610,\"volume\":554,\"changeRate\":-0.044,\"nonWeightedIndex\":6847},{\"changeAmount\":-4,\"weightedIndex\":6854,\"time\":\"11:15\",\"sell\":672,\"buy\":672,\"volume\":564,\"changeRate\":-0.058,\"nonWeightedIndex\":6847},{\"changeAmount\":-4,\"weightedIndex\":6854,\"time\":\"11:16\",\"sell\":590,\"buy\":590,\"volume\":564,\"changeRate\":-0.058,\"nonWeightedIndex\":6846},{\"changeAmount\":-4,\"weightedIndex\":6854,\"time\":\"11:17\",\"sell\":580,\"buy\":580,\"volume\":564,\"changeRate\":-0.058,\"nonWeightedIndex\":6846},{\"changeAmount\":-3,\"weightedIndex\":6854,\"time\":\"11:18\",\"sell\":590,\"buy\":590,\"volume\":574,\"changeRate\":-0.044,\"nonWeightedIndex\":6846},{\"changeAmount\":-3,\"weightedIndex\":6854,\"time\":\"11:19\",\"sell\":560,\"buy\":560,\"volume\":574,\"changeRate\":-0.044,\"nonWeightedIndex\":6847},{\"changeAmount\":-3,\"weightedIndex\":6854,\"time\":\"11:20\",\"sell\":590,\"buy\":590,\"volume\":574,\"changeRate\":-0.044,\"nonWeightedIndex\":6847},{\"changeAmount\":-5,\"weightedIndex\":6854,\"time\":\"11:21\",\"sell\":870,\"buy\":870,\"volume\":594,\"changeRate\":-0.073,\"nonWeightedIndex\":6845},{\"changeAmount\":-5,\"weightedIndex\":6854,\"time\":\"11:22\",\"sell\":1180,\"buy\":1180,\"volume\":594,\"changeRate\":-0.073,\"nonWeightedIndex\":6845},{\"changeAmount\":-5,\"weightedIndex\":6854,\"time\":\"11:23\",\"sell\":568,\"buy\":568,\"volume\":602,\"changeRate\":-0.073,\"nonWeightedIndex\":6845},{\"changeAmount\":-5,\"weightedIndex\":6854,\"time\":\"11:24\",\"sell\":472,\"buy\":472,\"volume\":602,\"changeRate\":-0.073,\"nonWeightedIndex\":6845},{\"changeAmount\":-4,\"weightedIndex\":6854,\"time\":\"11:25\",\"sell\":666,\"buy\":666,\"volume\":616,\"changeRate\":-0.058,\"nonWeightedIndex\":6845},{\"changeAmount\":-4,\"weightedIndex\":6854,\"time\":\"11:26\",\"sell\":756,\"buy\":756,\"volume\":616,\"changeRate\":-0.058,\"nonWeightedIndex\":6846},{\"changeAmount\":-3,\"weightedIndex\":6853,\"time\":\"11:27\",\"sell\":602,\"buy\":602,\"volume\":626,\"changeRate\":-0.044,\"nonWeightedIndex\":6846},{\"changeAmount\":-3,\"weightedIndex\":6853,\"time\":\"11:28\",\"sell\":590,\"buy\":590,\"volume\":626,\"changeRate\":-0.044,\"nonWeightedIndex\":6847},{\"changeAmount\":-5,\"weightedIndex\":6853,\"time\":\"11:29\",\"sell\":1070,\"buy\":1070,\"volume\":646,\"changeRate\":-0.073,\"nonWeightedIndex\":6845},{\"changeAmount\":-6,\"weightedIndex\":6853,\"time\":\"11:30\",\"sell\":1960,\"buy\":1960,\"volume\":686,\"changeRate\":-0.088,\"nonWeightedIndex\":6844},{\"changeAmount\":-6,\"weightedIndex\":6853,\"time\":\"13:31\",\"sell\":2360,\"buy\":2360,\"volume\":686,\"changeRate\":-0.088,\"nonWeightedIndex\":6844},{\"changeAmount\":-6,\"weightedIndex\":6852,\"time\":\"13:32\",\"sell\":1700,\"buy\":1700,\"volume\":696,\"changeRate\":-0.088,\"nonWeightedIndex\":6844},{\"changeAmount\":-6,\"weightedIndex\":6852,\"time\":\"13:33\",\"sell\":560,\"buy\":560,\"volume\":696,\"changeRate\":-0.088,\"nonWeightedIndex\":6844},{\"changeAmount\":-5,\"weightedIndex\":6852,\"time\":\"13:34\",\"sell\":632,\"buy\":632,\"volume\":708,\"changeRate\":-0.073,\"nonWeightedIndex\":6844},{\"changeAmount\":-5,\"weightedIndex\":6852,\"time\":\"13:35\",\"sell\":660,\"buy\":660,\"volume\":708,\"changeRate\":-0.073,\"nonWeightedIndex\":6845},{\"changeAmount\":-6,\"weightedIndex\":6852,\"time\":\"13:36\",\"sell\":756,\"buy\":756,\"volume\":728,\"changeRate\":-0.088,\"nonWeightedIndex\":6844},{\"changeAmount\":-6,\"weightedIndex\":6852,\"time\":\"13:37\",\"sell\":1180,\"buy\":1180,\"volume\":728,\"changeRate\":-0.088,\"nonWeightedIndex\":6844},{\"changeAmount\":-7,\"weightedIndex\":6852,\"time\":\"13:38\",\"sell\":1120,\"buy\":1120,\"volume\":750,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-7,\"weightedIndex\":6852,\"time\":\"13:39\",\"sell\":1298,\"buy\":1298,\"volume\":750,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-8,\"weightedIndex\":6852,\"time\":\"13:40\",\"sell\":1176,\"buy\":1176,\"volume\":764,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-8,\"weightedIndex\":6852,\"time\":\"13:41\",\"sell\":826,\"buy\":826,\"volume\":764,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-8,\"weightedIndex\":6852,\"time\":\"13:42\",\"sell\":826,\"buy\":826,\"volume\":764,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-10,\"weightedIndex\":6851,\"time\":\"13:43\",\"sell\":1264,\"buy\":1264,\"volume\":798,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-10,\"weightedIndex\":6851,\"time\":\"13:44\",\"sell\":2006,\"buy\":2006,\"volume\":798,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-10,\"weightedIndex\":6851,\"time\":\"13:45\",\"sell\":1424,\"buy\":1424,\"volume\":812,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-10,\"weightedIndex\":6851,\"time\":\"13:46\",\"sell\":826,\"buy\":826,\"volume\":812,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-9,\"weightedIndex\":6851,\"time\":\"13:47\",\"sell\":762,\"buy\":762,\"volume\":822,\"changeRate\":-0.131,\"nonWeightedIndex\":6840},{\"changeAmount\":-9,\"weightedIndex\":6851,\"time\":\"13:48\",\"sell\":590,\"buy\":590,\"volume\":822,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-8,\"weightedIndex\":6851,\"time\":\"13:49\",\"sell\":618,\"buy\":618,\"volume\":834,\"changeRate\":-0.117,\"nonWeightedIndex\":6841},{\"changeAmount\":-8,\"weightedIndex\":6851,\"time\":\"13:50\",\"sell\":672,\"buy\":672,\"volume\":834,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-7,\"weightedIndex\":6851,\"time\":\"13:51\",\"sell\":680,\"buy\":680,\"volume\":844,\"changeRate\":-0.102,\"nonWeightedIndex\":6842},{\"changeAmount\":-7,\"weightedIndex\":6851,\"time\":\"13:52\",\"sell\":550,\"buy\":550,\"volume\":844,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-7,\"weightedIndex\":6851,\"time\":\"13:53\",\"sell\":686,\"buy\":686,\"volume\":856,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-7,\"weightedIndex\":6851,\"time\":\"13:54\",\"sell\":708,\"buy\":708,\"volume\":856,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-7,\"weightedIndex\":6851,\"time\":\"13:55\",\"sell\":672,\"buy\":672,\"volume\":856,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-8,\"weightedIndex\":6850,\"time\":\"13:56\",\"sell\":668,\"buy\":668,\"volume\":866,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-8,\"weightedIndex\":6850,\"time\":\"13:57\",\"sell\":550,\"buy\":550,\"volume\":866,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-8,\"weightedIndex\":6850,\"time\":\"13:58\",\"sell\":706,\"buy\":706,\"volume\":878,\"changeRate\":-0.117,\"nonWeightedIndex\":6842},{\"changeAmount\":-9,\"weightedIndex\":6850,\"time\":\"13:59\",\"sell\":620,\"buy\":620,\"volume\":888,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-9,\"weightedIndex\":6850,\"time\":\"14:00\",\"sell\":590,\"buy\":590,\"volume\":888,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-9,\"weightedIndex\":6850,\"time\":\"14:01\",\"sell\":590,\"buy\":590,\"volume\":898,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-7,\"weightedIndex\":6850,\"time\":\"14:02\",\"sell\":616,\"buy\":616,\"volume\":910,\"changeRate\":-0.102,\"nonWeightedIndex\":6842},{\"changeAmount\":-7,\"weightedIndex\":6850,\"time\":\"14:03\",\"sell\":576,\"buy\":576,\"volume\":918,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-7,\"weightedIndex\":6850,\"time\":\"14:04\",\"sell\":448,\"buy\":448,\"volume\":918,\"changeRate\":-0.102,\"nonWeightedIndex\":6843},{\"changeAmount\":-9,\"weightedIndex\":6850,\"time\":\"14:05\",\"sell\":940,\"buy\":940,\"volume\":938,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-9,\"weightedIndex\":6850,\"time\":\"14:06\",\"sell\":1052,\"buy\":1052,\"volume\":946,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-9,\"weightedIndex\":6850,\"time\":\"14:07\",\"sell\":472,\"buy\":472,\"volume\":946,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-10,\"weightedIndex\":6850,\"time\":\"14:08\",\"sell\":472,\"buy\":472,\"volume\":954,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-12,\"weightedIndex\":6849,\"time\":\"14:09\",\"sell\":592,\"buy\":592,\"volume\":978,\"changeRate\":-0.175,\"nonWeightedIndex\":6839},{\"changeAmount\":-12,\"weightedIndex\":6849,\"time\":\"14:10\",\"sell\":1416,\"buy\":1416,\"volume\":978,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-13,\"weightedIndex\":6849,\"time\":\"14:11\",\"sell\":996,\"buy\":996,\"volume\":990,\"changeRate\":-0.19,\"nonWeightedIndex\":6837},{\"changeAmount\":-13,\"weightedIndex\":6849,\"time\":\"14:12\",\"sell\":708,\"buy\":708,\"volume\":990,\"changeRate\":-0.19,\"nonWeightedIndex\":6837},{\"changeAmount\":-12,\"weightedIndex\":6849,\"time\":\"14:13\",\"sell\":662,\"buy\":662,\"volume\":1000,\"changeRate\":-0.175,\"nonWeightedIndex\":6837},{\"changeAmount\":-12,\"weightedIndex\":6849,\"time\":\"14:14\",\"sell\":570,\"buy\":570,\"volume\":1000,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-11,\"weightedIndex\":6849,\"time\":\"14:15\",\"sell\":588,\"buy\":588,\"volume\":1012,\"changeRate\":-0.16,\"nonWeightedIndex\":6838},{\"changeAmount\":-11,\"weightedIndex\":6849,\"time\":\"14:16\",\"sell\":672,\"buy\":672,\"volume\":1012,\"changeRate\":-0.16,\"nonWeightedIndex\":6839},{\"changeAmount\":-11,\"weightedIndex\":6849,\"time\":\"14:17\",\"sell\":684,\"buy\":684,\"volume\":1024,\"changeRate\":-0.16,\"nonWeightedIndex\":6839},{\"changeAmount\":-11,\"weightedIndex\":6849,\"time\":\"14:18\",\"sell\":672,\"buy\":672,\"volume\":1024,\"changeRate\":-0.16,\"nonWeightedIndex\":6839},{\"changeAmount\":-11,\"weightedIndex\":6849,\"time\":\"14:19\",\"sell\":708,\"buy\":708,\"volume\":1024,\"changeRate\":-0.16,\"nonWeightedIndex\":6839},{\"changeAmount\":-10,\"weightedIndex\":6849,\"time\":\"14:20\",\"sell\":568,\"buy\":568,\"volume\":1034,\"changeRate\":-0.146,\"nonWeightedIndex\":6839},{\"changeAmount\":-10,\"weightedIndex\":6849,\"time\":\"14:21\",\"sell\":520,\"buy\":520,\"volume\":1034,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-10,\"weightedIndex\":6849,\"time\":\"14:22\",\"sell\":672,\"buy\":672,\"volume\":1046,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-9,\"weightedIndex\":6849,\"time\":\"14:23\",\"sell\":682,\"buy\":682,\"volume\":1056,\"changeRate\":-0.131,\"nonWeightedIndex\":6840},{\"changeAmount\":-9,\"weightedIndex\":6849,\"time\":\"14:24\",\"sell\":590,\"buy\":590,\"volume\":1056,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-9,\"weightedIndex\":6848,\"time\":\"14:25\",\"sell\":624,\"buy\":624,\"volume\":1068,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-9,\"weightedIndex\":6848,\"time\":\"14:26\",\"sell\":708,\"buy\":708,\"volume\":1068,\"changeRate\":-0.131,\"nonWeightedIndex\":6841},{\"changeAmount\":-10,\"weightedIndex\":6848,\"time\":\"14:27\",\"sell\":622,\"buy\":622,\"volume\":1078,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-10,\"weightedIndex\":6848,\"time\":\"14:28\",\"sell\":590,\"buy\":590,\"volume\":1078,\"changeRate\":-0.146,\"nonWeightedIndex\":6840},{\"changeAmount\":-12,\"weightedIndex\":6848,\"time\":\"14:29\",\"sell\":700,\"buy\":700,\"volume\":1098,\"changeRate\":-0.175,\"nonWeightedIndex\":6839},{\"changeAmount\":-12,\"weightedIndex\":6848,\"time\":\"14:30\",\"sell\":1180,\"buy\":1180,\"volume\":1098,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-12,\"weightedIndex\":6848,\"time\":\"14:31\",\"sell\":1120,\"buy\":1120,\"volume\":1098,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-11,\"weightedIndex\":6848,\"time\":\"14:32\",\"sell\":650,\"buy\":650,\"volume\":1108,\"changeRate\":-0.16,\"nonWeightedIndex\":6838},{\"changeAmount\":-11,\"weightedIndex\":6848,\"time\":\"14:33\",\"sell\":590,\"buy\":590,\"volume\":1108,\"changeRate\":-0.16,\"nonWeightedIndex\":6839},{\"changeAmount\":-11,\"weightedIndex\":6848,\"time\":\"14:34\",\"sell\":434,\"buy\":434,\"volume\":1116,\"changeRate\":-0.16,\"nonWeightedIndex\":6839},{\"changeAmount\":-12,\"weightedIndex\":6848,\"time\":\"14:35\",\"sell\":546,\"buy\":546,\"volume\":1126,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-12,\"weightedIndex\":6848,\"time\":\"14:36\",\"sell\":560,\"buy\":560,\"volume\":1126,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-12,\"weightedIndex\":6848,\"time\":\"14:37\",\"sell\":590,\"buy\":590,\"volume\":1126,\"changeRate\":-0.175,\"nonWeightedIndex\":6838},{\"changeAmount\":-14,\"weightedIndex\":6848,\"time\":\"14:38\",\"sell\":560,\"buy\":560,\"volume\":1136,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6848,\"time\":\"14:39\",\"sell\":590,\"buy\":590,\"volume\":1136,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-15,\"weightedIndex\":6848,\"time\":\"14:40\",\"sell\":884,\"buy\":884,\"volume\":1152,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6848,\"time\":\"14:41\",\"sell\":912,\"buy\":912,\"volume\":1152,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6848,\"time\":\"14:42\",\"sell\":820,\"buy\":820,\"volume\":1162,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6848,\"time\":\"14:43\",\"sell\":560,\"buy\":560,\"volume\":1162,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6848,\"time\":\"14:44\",\"sell\":590,\"buy\":590,\"volume\":1162,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-17,\"weightedIndex\":6847,\"time\":\"14:45\",\"sell\":550,\"buy\":550,\"volume\":1172,\"changeRate\":-0.248,\"nonWeightedIndex\":6833},{\"changeAmount\":-17,\"weightedIndex\":6847,\"time\":\"14:46\",\"sell\":590,\"buy\":590,\"volume\":1172,\"changeRate\":-0.248,\"nonWeightedIndex\":6833},{\"changeAmount\":-17,\"weightedIndex\":6847,\"time\":\"14:47\",\"sell\":608,\"buy\":608,\"volume\":1184,\"changeRate\":-0.248,\"nonWeightedIndex\":6833},{\"changeAmount\":-17,\"weightedIndex\":6847,\"time\":\"14:48\",\"sell\":708,\"buy\":708,\"volume\":1184,\"changeRate\":-0.248,\"nonWeightedIndex\":6833},{\"changeAmount\":-16,\"weightedIndex\":6847,\"time\":\"14:49\",\"sell\":650,\"buy\":650,\"volume\":1194,\"changeRate\":-0.233,\"nonWeightedIndex\":6833},{\"changeAmount\":-16,\"weightedIndex\":6847,\"time\":\"14:50\",\"sell\":590,\"buy\":590,\"volume\":1194,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-15,\"weightedIndex\":6847,\"time\":\"14:51\",\"sell\":498,\"buy\":498,\"volume\":1202,\"changeRate\":-0.219,\"nonWeightedIndex\":6834},{\"changeAmount\":-15,\"weightedIndex\":6847,\"time\":\"14:52\",\"sell\":472,\"buy\":472,\"volume\":1202,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-14,\"weightedIndex\":6847,\"time\":\"14:53\",\"sell\":530,\"buy\":530,\"volume\":1212,\"changeRate\":-0.204,\"nonWeightedIndex\":6835},{\"changeAmount\":-14,\"weightedIndex\":6847,\"time\":\"14:54\",\"sell\":560,\"buy\":560,\"volume\":1212,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6847,\"time\":\"14:55\",\"sell\":590,\"buy\":590,\"volume\":1222,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6847,\"time\":\"14:56\",\"sell\":550,\"buy\":550,\"volume\":1222,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6847,\"time\":\"14:57\",\"sell\":590,\"buy\":590,\"volume\":1222,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-15,\"weightedIndex\":6847,\"time\":\"14:58\",\"sell\":920,\"buy\":920,\"volume\":1242,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6847,\"time\":\"14:59\",\"sell\":1180,\"buy\":1180,\"volume\":1242,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6847,\"time\":\"15:00\",\"sell\":828,\"buy\":828,\"volume\":1254,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6847,\"time\":\"15:01\",\"sell\":708,\"buy\":708,\"volume\":1254,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:02\",\"sell\":904,\"buy\":904,\"volume\":1274,\"changeRate\":-0.204,\"nonWeightedIndex\":6835},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:03\",\"sell\":1160,\"buy\":1160,\"volume\":1274,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-13,\"weightedIndex\":6846,\"time\":\"15:04\",\"sell\":804,\"buy\":804,\"volume\":1286,\"changeRate\":-0.19,\"nonWeightedIndex\":6836},{\"changeAmount\":-13,\"weightedIndex\":6846,\"time\":\"15:05\",\"sell\":660,\"buy\":660,\"volume\":1286,\"changeRate\":-0.19,\"nonWeightedIndex\":6837},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:06\",\"sell\":800,\"buy\":800,\"volume\":1306,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:07\",\"sell\":1100,\"buy\":1100,\"volume\":1306,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:08\",\"sell\":1180,\"buy\":1180,\"volume\":1306,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-15,\"weightedIndex\":6846,\"time\":\"15:09\",\"sell\":1080,\"buy\":1080,\"volume\":1326,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-15,\"weightedIndex\":6846,\"time\":\"15:10\",\"sell\":1100,\"buy\":1100,\"volume\":1326,\"changeRate\":-0.219,\"nonWeightedIndex\":6835},{\"changeAmount\":-16,\"weightedIndex\":6846,\"time\":\"15:11\",\"sell\":1100,\"buy\":1100,\"volume\":1346,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-16,\"weightedIndex\":6846,\"time\":\"15:12\",\"sell\":1180,\"buy\":1180,\"volume\":1346,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-16,\"weightedIndex\":6846,\"time\":\"15:13\",\"sell\":844,\"buy\":844,\"volume\":1360,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-16,\"weightedIndex\":6846,\"time\":\"15:14\",\"sell\":826,\"buy\":826,\"volume\":1360,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-15,\"weightedIndex\":6846,\"time\":\"15:15\",\"sell\":634,\"buy\":634,\"volume\":1370,\"changeRate\":-0.219,\"nonWeightedIndex\":6834},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:16\",\"sell\":650,\"buy\":650,\"volume\":1390,\"changeRate\":-0.204,\"nonWeightedIndex\":6835},{\"changeAmount\":-14,\"weightedIndex\":6846,\"time\":\"15:17\",\"sell\":1180,\"buy\":1180,\"volume\":1390,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-16,\"weightedIndex\":6845,\"time\":\"15:18\",\"sell\":1160,\"buy\":1160,\"volume\":1430,\"changeRate\":-0.233,\"nonWeightedIndex\":6835},{\"changeAmount\":-16,\"weightedIndex\":6845,\"time\":\"15:19\",\"sell\":2360,\"buy\":2360,\"volume\":1430,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-16,\"weightedIndex\":6845,\"time\":\"15:20\",\"sell\":2200,\"buy\":2200,\"volume\":1430,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-17,\"weightedIndex\":6845,\"time\":\"15:21\",\"sell\":1112,\"buy\":1112,\"volume\":1444,\"changeRate\":-0.248,\"nonWeightedIndex\":6833},{\"changeAmount\":-16,\"weightedIndex\":6845,\"time\":\"15:22\",\"sell\":706,\"buy\":706,\"volume\":1454,\"changeRate\":-0.233,\"nonWeightedIndex\":6833},{\"changeAmount\":-16,\"weightedIndex\":6845,\"time\":\"15:23\",\"sell\":590,\"buy\":590,\"volume\":1454,\"changeRate\":-0.233,\"nonWeightedIndex\":6834},{\"changeAmount\":-15,\"weightedIndex\":6845,\"time\":\"15:24\",\"sell\":592,\"buy\":592,\"volume\":1468,\"changeRate\":-0.219,\"nonWeightedIndex\":6834},{\"changeAmount\":-14,\"weightedIndex\":6845,\"time\":\"15:25\",\"sell\":838,\"buy\":838,\"volume\":1484,\"changeRate\":-0.204,\"nonWeightedIndex\":6835},{\"changeAmount\":-14,\"weightedIndex\":6845,\"time\":\"15:26\",\"sell\":880,\"buy\":880,\"volume\":1484,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6845,\"time\":\"15:27\",\"sell\":854,\"buy\":854,\"volume\":1494,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-14,\"weightedIndex\":6845,\"time\":\"15:28\",\"sell\":550,\"buy\":550,\"volume\":1494,\"changeRate\":-0.204,\"nonWeightedIndex\":6836},{\"changeAmount\":-13,\"weightedIndex\":6845,\"time\":\"15:29\",\"sell\":590,\"buy\":590,\"volume\":1504,\"changeRate\":-0.19,\"nonWeightedIndex\":6836},{\"changeAmount\":-12,\"weightedIndex\":6845,\"time\":\"15:30\",\"sell\":900,\"buy\":900,\"volume\":1524,\"changeRate\":-0.175,\"nonWeightedIndex\":6837}]}")
					.getJSONArray("data");
		} catch (JSONException e) {
			Toast.makeText(this, "数据组装异常", Toast.LENGTH_SHORT).show();
		}
		List<TimesEntity> timesList = new ArrayList<TimesEntity>();
		JSONObject data = null;
		try {
			for (int i = 0; i < mDatas.length(); i++) {
				data = mDatas.getJSONObject(i);
				timesList.add(new TimesEntity(data.getString("time"), data
						.getDouble("weightedIndex"), data
						.getDouble("nonWeightedIndex"), data.getInt("buy"),
						data.getInt("sell"), data.getInt("volume")));
			}
		} catch (Exception e) {
			Toast.makeText(Activity_KLine.this, "数据解析异常", Toast.LENGTH_SHORT)
					.show();
		}

		mTimesView.setTimesList(timesList);
	}

	// 设置底部Vol等
	private void setVol() {
		my_charts_viewwek.setOHLCData(dayohlc);
		my_charts_viewwek.postInvalidate();
	}

	/** 文本信息 **/
	private String[] noteTextValues = { "0301", "0302", "0303", "0306", "0307",
			"0308", "0309", "0310" };

	/** 文本对应的值 **/
	private double[] realityValues = { 600, 200, 100, 200, 845, 855, 544, 655 };

	private double[] thinkValues = { 355, 145, 554, 451, 453, 455, 455, 452 };

	private List<Float> initMA(int days) {

		if (days < 2) {
			return null;
		}

		List<Float> MA5Values = new ArrayList<Float>();

		float sum = 0;
		float avg = 0;
		for (int i = 0; i < this.dayohlc.size(); i++) {
			float close = (float) dayohlc.get(i).getClose();
			if (i < days) {
				sum = sum + close;
				avg = sum / (i + 1f);
			} else {
				sum = sum + close - (float) dayohlc.get(i - days).getClose();
				avg = sum / days;
			}
			MA5Values.add(avg);
		}

		return MA5Values;
	}

	// 日K线图
	private void initMACandleStickChart(List<OHLCEntity> dayohlc) {
		List<LineEntity> lines = new ArrayList<LineEntity>();

		// 计算5日均线  颜色为黑色....
		LineEntity MA5 = new LineEntity();
		MA5.setTitle("MA5");
		MA5.setLineColor(Color.BLACK);
		MA5.setLineData(initMA(5));
		lines.add(MA5);

		// 计算10日均线  颜色为红...
		LineEntity MA10 = new LineEntity();
		MA10.setTitle("MA10");
		MA10.setLineColor(Color.RED);
		MA10.setLineData(initMA(10));
		lines.add(MA10);

		// 计算25日均线  颜色为绿色
		LineEntity MA25 = new LineEntity();
		MA25.setTitle("MA25");
		MA25.setLineColor(Color.GREEN);
		MA25.setLineData(initMA(25));
		lines.add(MA25);

		macandlestickchart.setAxisXColor(Color.LTGRAY);
		macandlestickchart.setAxisYColor(Color.LTGRAY);
		macandlestickchart.setLatitudeColor(Color.GRAY);
		macandlestickchart.setLongitudeColor(Color.GRAY);
		macandlestickchart.setBorderColor(Color.LTGRAY);
		macandlestickchart.setLongtitudeFontColor(Color.WHITE);
		macandlestickchart.setLatitudeFontColor(Color.WHITE);
		macandlestickchart.setAxisMarginRight(1);

		// 最大显示足数
		macandlestickchart.setMaxCandleSticksNum(dayohlc.size());
		// 最大纬线数
		macandlestickchart.setLatitudeNum(5);
		// 最大经线数
		macandlestickchart.setLongtitudeNum(4);
		// 最大价格
	//	macandlestickchart.setMaxPrice(1000);
		// 最小价格
	//	macandlestickchart.setMinPrice(200);
		macandlestickchart.setDisplayAxisXTitle(true);
		macandlestickchart.setDisplayAxisYTitle(true);
		macandlestickchart.setDisplayLatitude(true);
		macandlestickchart.setDisplayLongitude(true);
		macandlestickchart.setBackgroudColor(Color.BLACK);

		// 为chart2增加均线
		macandlestickchart.setLineData(lines);
		macandlestickchart.setOHLCData(dayohlc);
		macandlestickchart.setBackgroudColor(getResources().getColor(R.color.white));
		macandlestickchart.setLatitudeFontSize(15);
	}

	// 周K线图
	private void initMACandleStickChartWeek(List<OHLCEntity> ohlc) {
		List<LineEntity> lines = new ArrayList<LineEntity>();

		// 计算5日均线
		LineEntity MA5 = new LineEntity();
		MA5.setTitle("MA5");
		MA5.setLineColor(Color.BLACK);
		MA5.setLineData(initMA(5));
		lines.add(MA5);

		// 计算10日均线
		LineEntity MA10 = new LineEntity();
		MA10.setTitle("MA10");
		MA10.setLineColor(Color.RED);
		MA10.setLineData(initMA(10));
		lines.add(MA10);

		// 计算25日均线
		LineEntity MA25 = new LineEntity();
		MA25.setTitle("MA25");
		MA25.setLineColor(Color.GREEN);
		MA25.setLineData(initMA(25));
		lines.add(MA25);

		macandlestickchartweek.setAxisXColor(Color.LTGRAY);
		macandlestickchartweek.setAxisYColor(Color.LTGRAY);
		macandlestickchartweek.setLatitudeColor(Color.GRAY);
		macandlestickchartweek.setLongitudeColor(Color.GRAY);
		macandlestickchartweek.setBorderColor(Color.LTGRAY);
		macandlestickchartweek.setLongtitudeFontColor(Color.WHITE);
		macandlestickchartweek.setLatitudeFontColor(Color.WHITE);
		macandlestickchartweek.setAxisMarginRight(1);

		// 最大显示足数
		macandlestickchartweek.setMaxCandleSticksNum(ohlc.size());
		// 最大纬线数
		macandlestickchartweek.setLatitudeNum(5);
		// 最大经线数
		macandlestickchartweek.setLongtitudeNum(4);
		// 最大价格
		//macandlestickchartweek.setMaxPrice(1000);
		// 最小价格
		//macandlestickchartweek.setMinPrice(200);

		macandlestickchartweek.setDisplayAxisXTitle(true);
		macandlestickchartweek.setDisplayAxisYTitle(true);
		macandlestickchartweek.setDisplayLatitude(true);
		macandlestickchartweek.setDisplayLongitude(true);
		macandlestickchartweek.setBackgroudColor(Color.BLACK);

		// 为chart2增加均线
	//	macandlestickchartweek.setLineData(lines);

		// 为chart2增加均线
		macandlestickchartweek.setOHLCData(ohlc);
		macandlestickchartweek.setBackgroudColor(getResources().getColor(
				R.color.white));
		macandlestickchartweek.setLatitudeFontSize(15);
		
	}

	private	float retailOutPrice;
	private float retailIntPrice;
	private float mainforceOutPrice;
	private float mainforceIntPrice;
	private Quotation qt;
	private ViewVOL my_charts_viewweek_2;
	// 饼状图
	private void initPieChart() {
		List<TitleValueColorEntity> data3 = new ArrayList<TitleValueColorEntity>();
		RequestParams params = new RequestParams();
//		params.put("num", tables.getNum());
	//	params.put("wenJiaoId", qt.getId());
		params.put("TradingCode", tables.getCode());
		params.put("CollectionName", qt.getId());
		System.out.println("zuoparams=" + params.toString());
		showProgressDialog("加载中...");
	//	MyApplication.ahc.post(AppUrl.usergetpiechart, params,
		MyApplication.ahc.post(AppUrl.usergetBingInfo, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						Log.i("test3", "获取饼状图连接网络成功...");
						dismissProgressDialog();
						//JSONArray optJSONArray = response.optJSONArray("jy");
						//"RetailIntPrice":"109463.68","message":"获取成功","result":"0","RetailOutPrice":"0.0","mainforceOutPrice":"0.0","mainforceIntPrice":"9.079968923339996E9"}
						try {
							retailIntPrice =Float.parseFloat(response.getString("RetailIntPrice"));
							//float retailInprice = Float.parseFloat(retailIntPrice);
							retailOutPrice = Float.parseFloat(response.getString("RetailOutPrice"));
							mainforceOutPrice = Float.parseFloat(response.getString("mainforceOutPrice"));
							mainforceIntPrice = Float.parseFloat(response.getString("mainforceIntPrice"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						LogUtils.i("=圆饼数据===" + response.toString());
						Log.i("test3", "圆饼数据为==="+response.toString());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						LogUtils.i("圆饼数据失败==="+responseString);
						Log.i("test3","获取圆饼数据失败");
					}
				});

		data3.add(new TitleValueColorEntity("散户流入资金", 1, getResources().getColor(
				R.color.red)));
		data3.add(new TitleValueColorEntity("散户流出资金", 2, getResources().getColor(
				R.color.orange)));
		data3.add(new TitleValueColorEntity("主力流入资金", 3, getResources().getColor(
				R.color.yellow)));
		data3.add(new TitleValueColorEntity("主力流出资金", 5, getResources().getColor(
				R.color.greenbg)));
		//data3.add(new TitleValueColorEntity("第五个", 2, getResources().getColor(
		//		R.color.green)));
		piechart.setData(data3);

	}

	// 获取日K数据
	private void getDayK(int time) {
		RequestParams params = new RequestParams();
//		params.put("num", tables.getNum());
	//	params.put("wenJiaoId", qt.getId());
		params.put("CollectionName", qt.getId());
		params.put("TradingCode", tables.getCode());
		System.out.println("zuoparams=" + params.toString());
		showProgressDialog("加载中...");
//		MyApplication.ahc.post(AppUrl.usergetDayData, params,
		MyApplication.ahc.post(AppUrl.usergetdayKInfo, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						JSONArray optJSONArray = response.optJSONArray("jy");
						System.out.println("zuo===日K数据==="
								+ response.toString());
						LogUtils.i("日K数据===" + response.toString());

						if (optJSONArray != null) {
							for (int i = 0; i < optJSONArray.length(); i++) {
								JSONObject j = optJSONArray.optJSONObject(i);

								String data = j.optString("date");
								double openPrice = Double.parseDouble(j
										.optString("openPrice"));
								double curPrice = Double.parseDouble(j
										.optString("curPrice"));
								double highPrice = Double.parseDouble(j
										.optString("highPrice"));
								double lowPrice = Double.parseDouble(j
										.optString("lowPrice"));

								dayohlc.add(new OHLCEntity(openPrice,
										highPrice, lowPrice, curPrice, data));
							}
							setDayKlineData();
						}

					}

				});
	}

	// 设置日K参数
	private void setDayKlineData() {
		// List<OHLCEntity> ohlc = new ArrayList<OHLCEntity>();
		//
		// for (int i = 0; i < daytables.size(); i++) {
		// ohlc.add(new OHLCEntity(daytables.get(i).getOpenPrice(), daytables
		// .get(i).getHighPrice(), daytables.get(i).getLowPrice(),
		// daytables.get(i).getCurPrice(), daytables.get(i).getCd()));
		// }
		//
		// for (int i = ohlc.size(); i > 0; i--) {
		// this.dayohlc.add(ohlc.get(i - 1));
		// }
		initMACandleStickChart(dayohlc);
	//	setVol();
	}

	// 获取周K参数
	private void getWeekData(int time) {
		RequestParams params = new RequestParams();
		//params.put("num", tables.getNum());
		//params.put("wenJiaoId", qt.getId());
		params.put("CollectionName", qt.getId());
		params.put("TradingCode", tables.getCode());
		System.out.println("zuoparams=" + params.toString());
		showProgressDialog("加载中...");
//		MyApplication.ahc.post(AppUrl.usergetWeekPrice, params,
		MyApplication.ahc.post(AppUrl.usergetweekKInfo, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						JSONArray optJSONArray = response.optJSONArray("jy");
						System.out.println("zuo===周K数据==="
								+ response.toString());

						LogUtils.i("=周K数据===" + response.toString());
						if (optJSONArray != null) {
							for (int i = 0; i < optJSONArray.length(); i++) {
								JSONObject j = optJSONArray.optJSONObject(i);

								String data = j.optString("data");
								double openPrice = Double.parseDouble(j
										.optString("openPrice"));
								double curPrice = Double.parseDouble(j
										.optString("curPrice"));
								double highPrice = Double.parseDouble(j
										.optString("highPrice"));
								double lowPrice = Double.parseDouble(j
										.optString("lowPrice"));
								if(highPrice>=lowPrice&&openPrice<=highPrice&&curPrice<=highPrice&&openPrice>=lowPrice&&curPrice>=lowPrice) {
									weekohlc.add(new OHLCEntity(openPrice,
											highPrice, lowPrice, curPrice, data));
								}
							}
							setWeekKlineData();
						}
					}
				});
	}

	// 设置周K参数
	private void setWeekKlineData() {
		// List<OHLCEntity> ohlc = new ArrayList<OHLCEntity>();
		//
		// for (int i = 0; i < weekdables.size(); i++) {
		// ohlc.add(new OHLCEntity(weekdables.get(i).getOpenPrice(),
		// weekdables.get(i).getHighPrice(), weekdables.get(i)
		// .getLowPrice(), weekdables.get(i).getCurPrice(),
		// weekdables.get(i).getCd()));
		// }
		//
		// for (int i = ohlc.size(); i > 0; i--) {
		// this.weekohlc.add(ohlc.get(i - 1));
		// }
		//initMACandleStickChartWeek(dayohlc);
		initMACandleStickChartWeek(weekohlc);
	//	setWeekVol();
	}

	private void setWeekVol() {
		my_charts_viewweek_2.setOHLCData(dayohlc);
		my_charts_viewweek_2.postInvalidate();
	}

	// 获取分时
	private void getTimeData(int time) {
		RequestParams params = new RequestParams();
		// params.put("c",
		// MyApplication.qs.get(p).getTables().get(pp).getCode());
		// params.put("codeId", MyApplication.qs.get(p).getId());
		// params.put("codeId", 17);
		// params.put("time", time);
		int wenjiao_id = PreferenceUtils.getInt(Activity_KLine.this,
				"wenjiao_id");
		LogUtils.i("num=" + wenjiao_id + "-----wenjiaoid=" + pp);

		params.put("num", tables.getNum());
	//	params.put("num", 1);
		//params.put("wenJiaoId", 1);
		params.put("wenJiaoId", qt.getId());
		System.out.println("zuoparams=" + params.toString());
		showProgressDialog("加载中...");
		//MyApplication.ahc.post(AppUrl.usergetMinPrice, params,
		MyApplication.ahc.post(AppUrl.usergethourKInfo, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						dismissProgressDialog();
						JSONArray optJSONArray = response.optJSONArray("jy");
						System.out.println("zuo===分时数据==="
								+ response.toString());

						LogUtils.i("分时数据" + response.toString());
						Log.i("test3", "分时数据"+response.toString());
						if (optJSONArray != null) {
							for (int i = 0; i < optJSONArray.length(); i++) {
								LogUtils.i("数据长度==" + optJSONArray.length());
								
								JSONObject j = optJSONArray.optJSONObject(i);
								String data = j.optString("data");
								double openPrice = Double.parseDouble(j
										.optString("OpenPrice"));
								double curPrice = Double.parseDouble(j
										.optString("CurPrice"));
								double highPrice = Double.parseDouble(j
										.optString("HighPrice"));
								double lowPrice = Double.parseDouble(j
										.optString("LowPrice"));

								timeohlc.add(new OHLCEntity(openPrice,
										highPrice, lowPrice, curPrice, data));

								// String date = j.optString("date");
								// double curPrice = Double.parseDouble(j
								// .optString("curPrice"));
								//
								// timeohlc.add(new
								// OHLCEntity(0,0,0,curPrice,date));
							}
							Log.i("test3", "分时线的集合的长度为"+timeohlc.size());
							setTimeKlineData();
						}
					}

				});
	}

	// 设置分时参数
	private void setTimeKlineData() {
		mTimesView.setVisibility(View.VISIBLE);
	//	mMyChartsView.setVisibility(View.INVISIBLE);
		macandlestickchart.setVisibility(View.INVISIBLE);
		ll_vol.setVisibility(View.INVISIBLE);
		LogUtils.i("获取的数据大小===" + timeohlc.size());
		for (int i = 0; i < timeohlc.size(); i++) {
			timesList.add(new TimesEntity(timeohlc.get(i).getDate(), 0.00,
					0.00, (int) timeohlc.get(i).getClose(), 30, 1000));
		}
	//	mTimesView.setTimesList(timesList);
	}
}
