package com.femto.post.activity;

import java.util.List;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.easemob.redpacketui.RedPacketConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.femto.post.R;
import com.femto.post.application.MyApplication;
import com.femto.post.fragment.Fragment_Live;
import com.femto.post.fragment.Fragment_MustSee;
import com.femto.post.fragment.Fragment_Notice;
import com.femto.post.fragment.Fragment_Smart;
import com.femto.post.fragment.Fragment_forecast;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.InviteMessgeDao;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.chatuidemo.domain.InviteMessage;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsResultAction;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.chatuidemo.ui.ContactListFragment;
import com.hyphenate.chatuidemo.ui.ConversationListFragment;
import com.hyphenate.chatuidemo.ui.GroupsActivity;
import com.hyphenate.chatuidemo.ui.LoginActivity;
import com.hyphenate.chatuidemo.ui.SettingsFragment;
import com.hyphenate.chatuidemo.ui.MainActivity.MyContactListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;

public class MainActivity extends BaseActivity {
	private RelativeLayout rl_left, rl_right_text, rl_right;
	@SuppressWarnings("unused")
	private TextView tv_title, tv_right;
	private RadioButton rb_smart, rb_live, rb_forecast, rb_notice, rb_mustsee;
	private Fragment_forecast ff;
	private Fragment_Live fl;
	//private ContactListFragment fl;
	private Fragment_MustSee fm;
	private Fragment_Notice fn;
	private Fragment_Smart fs;
	private FragmentTransaction transaction;
	private ImageView im_left;
	private View v1, v2, v3, v4, v5;
	private long firstClickTime;
	
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;
	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			if (MyApplication.islogin) {
				Intent intent = new Intent(this, Activity_Person.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.rl_right_text:
			Intent intent_open = new Intent(this, Activity_OpenAccount.class);
			startActivity(intent_open);
			break;
		case R.id.rl_right:
			showShare();
			// Intent intent_ = new Intent();
			// intent_.setAction("android.intent.action.VIEW");
			// Uri uri = Uri.parse("http://www.baidu.com/s?wd=" + "刘德华");
			// intent_.setData(uri);
			// startActivity(intent_);
			// if (!isAvilible(this, "com.baidu.BaiduMap")) {
			// showToast("您的手机尚未安装百度地图!", 0);
			// return;
			// }
			// Intent intent_map;
			// try {
			// intent_map = Intent
			// .getIntent("intent://map/line?coordtype=&zoom=&region=\"\"&name=帝王大厦&src=飞码科技|蓝牙眼镜#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
			// startActivity(intent_map);
			// } catch (URISyntaxException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } // 调起百度地图客户端（Android）展示上海市"28"路公交车的检索结果

			break;
		case R.id.rb_smart:
			fragmentShowOrHide(fs, ff, fl, fn, fm, false);
			showView(v1, v2, v3, v4, v5);
			break;
		case R.id.rb_live:
			fragmentShowOrHide(fl, fs, ff, fn, fm, false);
			showView(v2, v1, v3, v4, v5);
			break;
		case R.id.rb_forecast:
			fragmentShowOrHide(ff, fs, fl, fn, fm, false);
			showView(v3, v1, v2, v4, v5);
			break;
		case R.id.rb_notice:
			fragmentShowOrHide(fn, fs, ff, fl, fm, false);
			showView(v4, v1, v2, v3, v5);
			break;
		case R.id.rb_mustsee:
			fragmentShowOrHide(fm, fs, ff, fl, fn, false);
			showView(v5, v1, v2, v3, v4);
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		LoginHuanXin();
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_right = (TextView) findViewById(R.id.tv_right);
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		rl_right_text = (RelativeLayout) findViewById(R.id.rl_right_text);
		rl_right = (RelativeLayout) findViewById(R.id.rl_right);

		rb_smart = (RadioButton) findViewById(R.id.rb_smart);
		rb_live = (RadioButton) findViewById(R.id.rb_live);
		rb_forecast = (RadioButton) findViewById(R.id.rb_forecast);
		rb_notice = (RadioButton) findViewById(R.id.rb_notice);
		rb_mustsee = (RadioButton) findViewById(R.id.rb_mustsee);
		im_left = (ImageView) findViewById(R.id.im_left);

		v1 = findViewById(R.id.v1);
		v2 = findViewById(R.id.v2);
		v3 = findViewById(R.id.v3);
		v4 = findViewById(R.id.v4);
		v5 = findViewById(R.id.v5);
	}

	private void LoginHuanXin() {
		String name = MyApplication.userId;
		Log.i("test3", "name="+name);
		String password = MyApplication.password;
		Log.i("test3", "password="+password);
		if(TextUtils.isEmpty(name)&&TextUtils.isEmpty(password)) {
			return;
		}
		EMClient.getInstance().login(name,"123456",new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						EMClient.getInstance().groupManager().loadAllGroups();
						EMClient.getInstance().chatManager().loadAllConversations();
						//全局变量  环信登录成功
						MyApplication.IsLoginHuanxin = true;
				//		Toast.makeText(MainActivity.this, "登录聊天服务器成功", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
		 
			}
		 
			@Override
			public void onError(int code, String message) {
				Log.i("test3", "登录聊天服务器失败！");
				Toast.makeText(MainActivity.this, "登录聊天服务器失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		tv_title.setText("邮币精灵");
		rl_right_text.setVisibility(View.VISIBLE);
		rl_right.setVisibility(View.VISIBLE);
		rl_left.setOnClickListener(this);
		rl_right.setOnClickListener(this);
		rl_right_text.setOnClickListener(this);

		rb_smart.setOnClickListener(this);
		rb_live.setOnClickListener(this);
		rb_forecast.setOnClickListener(this);
		rb_notice.setOnClickListener(this);
		rb_mustsee.setOnClickListener(this);
		im_left.setImageResource(R.drawable.user);
		//环信
		requestPermissions();
		
//		MobclickAgent.updateOnlineConfig(this);
//		UmengUpdateAgent.setUpdateOnlyWifi(false);
//		UmengUpdateAgent.update(this);

		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog(); 
		}

		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		//conversationListFragment = new ConversationListFragment();
		//contactListFragment = new ContactListFragment();
		//settingFragment = new SettingsFragment();
		//fragments = new Fragment[] { conversationListFragment, contactListFragment, settingFragment };
		// 添加显示第一个fragment
		/*getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
				.commit();*/
		
		initFragment();
		//注册local广播接收者，用于接收demohelper中发出的群组联系人的变动通知
		registerBroadcastReceiver();
		
		
		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
		/*环信*/
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (savedInstanceState == null) {
		//
		// }
	}

	// 保证当当前activity被回收后不保存当前状态,不保存已经创建的fragment。
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
	}
	
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_main);
		MyApplication.addActivity(this);
	}

	private void showView(View v1, View v2, View v3, View v4, View v5) {
		v1.setVisibility(View.VISIBLE);
		v2.setVisibility(View.INVISIBLE);
		v3.setVisibility(View.INVISIBLE);
		v4.setVisibility(View.INVISIBLE);
		v5.setVisibility(View.INVISIBLE);
	}

	private void initFragment() {
		transaction = getSupportFragmentManager().beginTransaction();
		ff = new Fragment_forecast();//评级检测
		//fl = new ContactListFragment();//邮市直播
		fl =  new Fragment_Live();
		fm = new Fragment_MustSee();//炒邮必看
		fn = new Fragment_Notice();//公告资讯
		fs = new Fragment_Smart();//智能盯盘
		transaction.add(R.id.fl_contain, ff);
		transaction.add(R.id.fl_contain, fl);
		transaction.add(R.id.fl_contain, fm);
		transaction.add(R.id.fl_contain, fn);
		transaction.add(R.id.fl_contain, fs);
		fragmentShowOrHide(fs, ff, fl, fn, fm, true);
	}

	private void fragmentShowOrHide(Fragment showFragment,
			Fragment hideFragment1, Fragment hideFragment2,
			Fragment hideFragment3, Fragment hideFragment4, boolean isInit) {

		if (!isInit) {
			transaction = getSupportFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.hide(hideFragment2);
		transaction.hide(hideFragment3);
		transaction.hide(hideFragment4);
		transaction.commit();
	}


	// 分享
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.app_name));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		// 启动分享GUI
		oks.show(this);
	}
	 
	//实现ConnectionListener接口
	private class MyConnectionListener implements EMConnectionListener {
	    @Override
		public void onConnected() {
		}
		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
	 
				@Override
				public void run() {
					if(error == EMError.USER_REMOVED){
						// 显示帐号已经被移除
					}else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
						// 显示帐号在其他设备登录
					} else {
					if (NetUtils.hasNetwork(MainActivity.this)) {
						//连接不到聊天服务器
						Log.i("test", "连接服务器失败");
						Toast.makeText(MainActivity.this, "连接服务器失败", 0).show();
					}
					else {
						Toast.makeText(MainActivity.this, "当前网络不可用", 0).show();
						Log.i("test", "当前网络不可用");
					}
						//当前网络不可用，请检查网络设置
					}
				}
			});
		}
	}
	@TargetApi(23)
	private void requestPermissions() {
		PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
			@Override
			public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDenied(String permission) {
				//Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
EMMessageListener messageListener = new EMMessageListener() {
		
		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// 提示新消息
		    for (EMMessage message : messages) {
		        DemoHelper.getInstance().getNotifier().onNewMsg(message);
		    }
			//refreshUIWithMessage();
		}
		
		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
			for (EMMessage message : messages) {
				EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
				final String action = cmdMsgBody.action();//获取自定义action
				if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION) && message.getChatType() == EMMessage.ChatType.GroupChat) {
					RedPacketUtil.receiveRedPacketAckMessage(message);
				}
			}
	//		refreshUIWithMessage();
		}
		
		@Override
		public void onMessageReadAckReceived(List<EMMessage> messages) {
		}
		
		@Override
		public void onMessageDeliveryAckReceived(List<EMMessage> message) {
		}
		
		@Override
		public void onMessageChanged(EMMessage message, Object change) {}
	};

	/*private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (currentTabIndex == 0) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (conversationListFragment != null) {
						conversationListFragment.refresh();
					}
				}
			}
		});
	}*/
	
	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
		intentFilter.addAction(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
             //   updateUnreadLabel();
              //  updateUnreadAddressLable();
         //       if (currentTabIndex == 0) {
                    // 当前页面如果为聊天历史页面，刷新此页面
           //         if (conversationListFragment != null) {
             //           conversationListFragment.refresh();
              //      }
              //  } else if (currentTabIndex == 1) {
                   /* if(fl != null&&fl.isResumed()) {
                        fl.refresh();
                    }*/
             //   }
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
				if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)){
					/*if (conversationListFragment != null){
						conversationListFragment.refresh();
					}*/
					/*if(fl!=null) {
						fl.refresh();
					}*/
				}
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

	public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {}
        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
							username.equals(ChatActivity.activityInstance.toChatUsername)) {
					    String st10 = getResources().getString(R.string.have_you_removed);
					    Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
					    .show();
					    ChatActivity.activityInstance.finish();
					}
                }
            });
        }
        @Override
        public void onContactInvited(String username, String reason) {}
        @Override
        public void onContactAgreed(String username) {}
        @Override
        public void onContactRefused(String username) {}
	}
	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		
		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		unregisterBroadcastReceiver();

		try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
		
	}

	/**
	 * 刷新未读消息数
	 */
	/*public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}*/

	/**
	 * 刷新申请与通知消息数
	 *//*
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
//					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}*/

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
		for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
			chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;




	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		DemoHelper.getInstance().getNotifier().viberateAndPlayTone(null);

		// 刷新bottom bar消息未读数
		//updateUnreadAddressLable();
		// 刷新好友页面ui
		//if (currentTabIndex == 1)
		//	contactListFragment.refresh();
		/*if(fl.isResumed()) {
			fl.refresh();
		}*/
		
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		//保存未读数，这里没有精确计算
		inviteMessgeDao.saveUnreadMessageCount(1);
	}


	@Override
	protected void onResume() {
		super.onResume();
		
		/*if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
		}*/

		// unregister this event listener when this activity enters the
		// background
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	@Override
	protected void onStop() {
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	/*@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;
    //private ConversationListFragment conversationListFragment;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				//EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
			//	EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//getMenuInflater().inflate(R.menu.context_tab_contact, menu);
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
	}
}
