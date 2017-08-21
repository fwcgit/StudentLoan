package com.studentloan.white.utils;

import android.util.Log;

import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
/***
 * 日志工具
 * @author fu
 *
 */
public class LogUtils {
	private static String Tag = MyApplication.getInstance().getPackageName();
	private static boolean showLog = MyContacts.showLog;
	private static boolean redLog = true;
	
	public static void logDug(String msg){
		if(!showLog) return;
		if(redLog)
			Log.e(Tag, msg);
		else
			Log.d(Tag, msg);
	}
	
	public static void logError(String msg){
		if(!showLog || msg == null) return;
		Log.e(Tag, msg);
	}
	
	public static void logDug(String tag,String msg){
		if(!showLog) return;
		if(redLog)
			Log.e(tag, msg);
		else
			Log.d(tag, msg);

	}
	
	public static void logError(String tag,String msg){
		if(!showLog) return;
		Log.e(tag, msg);
	}
}
