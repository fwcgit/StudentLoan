/** */
package com.studentloan.white.utils;

import android.content.Context;
import android.util.TypedValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 转换辅助类
 * @author fu
 */
public class ConvertUtils {
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
    
	public static int pixl2DIP(float pixl, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				pixl, context.getResources().getDisplayMetrics());
	}



	/**
	 * 日期时间转字符串
	 * 
	 * @param date
	 *            DATE型
	 * @param dateTimeFormatStr
	 *            格式
	 * @return
	 * 
	 *         示例：当前日期：2007年07月01日12时14分25秒。 dateTimeToStr(new
	 *         Date(),"yyyy-MM-dd HH:mm:ss"); 返回：2007-07-01 12:14:25
	 */
	public static String dateTimeToStr(Date date, String dateTimeFormatStr) {
		String rsStr = null;
		if (dateTimeFormatStr != null) {
			SimpleDateFormat df = new SimpleDateFormat(dateTimeFormatStr);
			rsStr = df.format(date);
		} else {
			rsStr = dateTimeToStr(date, "yyyy年MM日");
		}
		return rsStr;
	}

	public static Date dateStrToTime(String dateStr, String dateTimeFormatStr) {
		SimpleDateFormat df = new SimpleDateFormat(dateTimeFormatStr);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	  * 将sp值转换为px值，保证文字大小不变
	  * 
	  * @param spValue
	  * @param （DisplayMetrics类中属性scaledDensity）
	  * @return
	  */
	 public static int sp2px(Context context,float spValue) {
		 float fontScale = context.getResources().getDisplayMetrics().density;
	  return (int) (spValue * fontScale + 0.5f);
	 }
}
