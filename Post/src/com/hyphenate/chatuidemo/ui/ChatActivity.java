package com.hyphenate.chatuidemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.android.futures.util.LogUtils;
import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.fragment.EaseChatFragments;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.util.EasyUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.RetryHandler;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 聊天页面，需要fragment的使用{@link #EaseChatFragment}
 *
 */
public class ChatActivity extends BaseActivity{
    public static ChatActivity activityInstance;
    private EaseChatFragments chatFragment;
    public String toChatUsername;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString("userId");
        //可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        //退出环信聊天
        LogoutHuanXin();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
    
    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override 
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
    //当页面Destory的时候退出换信聊天
    private void LogoutHuanXin() {
    	RequestParams params = new RequestParams();
    	if(!MyApplication.islogin) {
    		return;
    	}
    	params.addBodyParameter("lvname", toChatUsername);
    	params.addBodyParameter("uid", MyApplication.userId);
    	LogUtils.i("lvname==="+toChatUsername);
    	LogUtils.i("uid===="+MyApplication.userId);
    	LogUtils.i("url===="+AppUrl.quiteGroup);
    	new HttpUtils().send(HttpMethod.POST, AppUrl.quiteGroup, params,new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogUtils.i("退出聊天室失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				//TODO  后台返回result
				if(result.equals("0")) {
					LogUtils.i("退出聊天室成功");
				} else {
					LogUtils.i("退出聊天室失败");
				}
			}
		});
    }
}
