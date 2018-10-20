package com.studentloan.white;

/***
 * 常量
 * @author fu
 * @create date 2016.05.11
 */
public class MyContacts {
	public static boolean showLog = true;//是否显示日志
	public static boolean online = true;
//	public static final String TEST_URL = "http://139.196.104.98:84";
//	public static final String ON_LINE_URL = "http://139.196.104.98:81";

	public static final String ON_LINE_URL 	= "http://app.yuhuizichan.com:12345";
	public static final String TEST_URL		= "http://192.168.121.200:26000";

	//获取任务状态时使用(合作方申请接入后由魔蝎数据提供)
	//public static final String mApiKey = "aaf8abeeecc34d9487f9d0b224ed7bd1";
	public static final String mxApiKey = "56eb834e3b294e61a7e041902cefa34c";//魔蝎KEY
	public static  String BASE_URL = "";

	/**
	 * 人脸识别UUID
	 */

	public static String FACE_UUID = "999888";

	static {
		BASE_URL = online ? ON_LINE_URL : TEST_URL;
	}
	
	
}

