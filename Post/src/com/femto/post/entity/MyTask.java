package com.femto.post.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;

import android.R.integer;
import android.os.AsyncTask;
import android.widget.EditText;

/**
 * 用于异步操作来读取网页中HTML代码
 * 
 * @author jianyan
 * 
 */
public class MyTask extends AsyncTask<String, integer, StringBuffer> {

	// private EditText edtHTTP;// 用于显示HTML代码
	private StringBuffer sbHTML;// 用于储存HTML代码

	public MyTask() {

		sbHTML = new StringBuffer();
	}

	/**
	 * doInBackground方法内部执行后台任务,不可在此方法内修改UI
	 */
	@Override
	protected StringBuffer doInBackground(String... params) {
		// 初始化HTTP的客户端
		HttpClient hc = new DefaultHttpClient();
		// 实例化HttpGet对象
		HttpGet hg = new HttpGet(params[0]);

		try {
			// 让HTTP客户端已Get的方式请求数据，并把所得的数据赋值给HttpResponse的对象
			HttpResponse hr = hc.execute(hg);
			// 使用缓存的方式读取所返回的数据
			BufferedReader br = new BufferedReader(new InputStreamReader(hr
					.getEntity().getContent()));

			// 读取网页所返回的HTML代码
			String line = "";
			sbHTML = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sbHTML.append(line);
			}

			return sbHTML;
		} catch (IOException e) {

		}
		return null;
	}

	/**
	 * onPostExecute方法用于在执行完后台任务后更新UI,显示结果
	 */
	@Override
	protected void onPostExecute(StringBuffer result) {
		// 判断是否为null，若不为null，则在页面显示HTML代码
		if (result != null) {
			String mmm;
			try {
				mmm = new String(result.toString().getBytes(), "UTf-8");
				System.out.println("zuo获取---" + mmm);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		super.onPostExecute(result);
	}
}
