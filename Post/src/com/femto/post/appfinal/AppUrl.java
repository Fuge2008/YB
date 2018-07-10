package com.femto.post.appfinal;

public class AppUrl {
	 public static String BASEURL = "http://120.25.207.66:8080";
	// public static String BASEURL = "http://115.28.77.44";
	//最新的本地的地址...
	//public static String BASEURL = "http://192.168.1.132:8080";
	public static String usersendCode = BASEURL + "/YouBi/usersendCode";
	public static String userregist = BASEURL + "/YouBi/userregist";
	public static String useruserlogin = BASEURL + "/YouBi/useruserlogin";
	// 找回密码
	public static String userfindPassByCode = BASEURL
			+ "/YouBi/userfindPassByCode";
	// 忘记密码找回发送验证码
	public static String userpassWordSendCode = BASEURL
			+ "/YouBi/userpassWordSendCode";
	// 直播室
	public static String usergetLiveRoomList = BASEURL
			+ "/YouBi/usergetLiveRoomList";
	// 直播室数据
	public static String usergetLiveRoomDiscussList = BASEURL
			+ "/YouBi/usergetLiveRoomDiscussList";
	// 邮币平台名称
	public static String usergetCodeList = BASEURL + "/YouBi/usergetCodeList";
	// 邮币平台详细数据
	public static String usergetYouBiData = BASEURL + "/YouBi/usergetYouBiData";
	// 邮币日K图
//	public static String usergetDayData = BASEURL + "/YouBi/usergetDayData";
	public static String usergetDayData = BASEURL + "/YouBi/usergetCodeDataToDay";
	
	// 邮币周K图
//	public static String usergetWeekPrice = BASEURL + "/YouBi/usergetWeekPrice";
	public static String usergetWeekPrice = BASEURL + "/YouBi/usergetCodeDataToWeek";
	
	// 邮币分时图
//	public static String usergetMinPrice = BASEURL + "/YouBi/usergetMinPrice";
	public static String usergetMinPrice = BASEURL + "/YouBi/usergetCodeDataToMin";
	// 邮币咨询
	public static String admingetConsultingAnnouncement = BASEURL
			+ "/YouBi/admingetConsultingAnnouncement";
	//获取咨询数据接口...
	public static String getInFo = BASEURL+"/YouBi/usergetNewsByStatus";
	//饼状图的url
	public static String usergetpiechart = BASEURL + "/YouBi/usergetMainforceRetailInOut";
	//上传个人头像的url
	public static String userPhoto = BASEURL + "/YouBi/useruploadUser";
	//上传用户名的url
	//public static String userName = BASEURL + "YouBi/"
	//根据id查文章...
	public static String usergetArticleById = BASEURL+"/YouBi/usergetArticleById";
	//更新个人信息...
	public static String updateUserInfo =BASEURL+ "/YouBi/userupdateUserInfo";
	//更改手机号的地址
	public static String updatePhoneInfo = BASEURL+"/YouBi/userupdatePhone";
	//一键盯盘的url
	public static String userOneDingPan = BASEURL+"/YouBi/useroneDingPan";
	//取消盯盘的url
	public static String userQuxiaoDingPan = BASEURL+"/YouBi/userquXiaoDingPan";
	//总表一键盯盘
	public static String useronesDingPan = BASEURL+"/YouBi/useronesDingPan";
	//获取已盯盘列表...
	public static String usergetDingPans = BASEURL+"/YouBi/usergetDingPans";
	//获取柱状图数据
	public static String usergetInOutPriceToDay10 = BASEURL+"/YouBi/usergetInOutPriceToDay10";
	//获取日k图数据  临时用  后期注掉...
	public static String usergetdayKInfo = BASEURL+"/YouBi/usershowDailyTradingDataCodeAndshowDailyTradingDataCodeTemp";
	//获取周k图数据  临时用  后期注掉...
	public static String usergetweekKInfo = BASEURL+"/YouBi/usershowWeeklyTradingDataCodeAndshowWeeklyTradingDataCodeTemp";
	//获取分时数据  临时用 后期注掉...
	public static String usergethourKInfo = BASEURL+"/YouBi/usershowMinuteHoursTradingDataCodeTemp";
	//获取10天流入资金  临时用  后期注掉...
	public static String usergetZhuInfo = BASEURL+"/YouBi/usergetTenDailyTempOrTenDaily";
	//获取饼状图数据  临时用  后期注掉...
	public static String usergetBingInfo = BASEURL +"/YouBi/usergetDailyDataCodeTemp";
	//申请加入聊天室
	public static String applyToGroup = BASEURL+"/YouBi/useraddLiveRoomToUser";
	//查询用户权限
	public static String querryuserPower = BASEURL + "/YouBi/usergetUserChatStatu";
	//退出群组权限
	public static String quiteGroup = BASEURL+"/YouBi/userdeleteUserToLiveRoom";
	//获取首页文交所列表
	public static String USERGETfIRSTMENU = BASEURL+"/YouBi/usergetFirstMenu";
	//获取股票列表的url
	public static String USERGETTWOMENU = BASEURL+"/YouBi/usergetTwoMenu";
	//获取开户文交所信息
	public static String USERGETWENJIAOOPEN = BASEURL+"/YouBi/usergetWernJiaoSuoopenUrl";
	//判断用户是否为会员
	public static String USERGETISMEMBER = BASEURL+"/YouBi/usergetIsMember";
	//获取自选盯盘数据
	public static String USERGETMYDINGPAN = BASEURL+"/YouBi/usergetMyDingPan";
}
