package com.qudaozhang.white.net;



import android.text.TextUtils;

import com.google.gson.Gson;
import com.qudaozhang.white.utils.LogUtils;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.RestRequest;
import com.yolanda.nohttp.rest.StringRequest;

public class CustomDataRequest<T> extends RestRequest<T> {
	public static final String ACCEPT = "application/json;q=1";
	Class<?> cls;
	public CustomDataRequest(String url,Class<?> cls) {
		super(url);
		this.cls = cls;

	}
	

	public CustomDataRequest(String url, RequestMethod requestMethod,Class<?> cls) {
		super(url, requestMethod);
		this.cls = cls;
	}




//	public T parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
//		String jsonStr = StringRequest.parseResponseString(url, responseHeaders, responseBody);
//
//		LogUtils.logDug("HTTP Response Result = "+jsonStr);
//
//		if(null == cls) return  (T)jsonStr;
//
//		try {
//			if(TextUtils.isEmpty(jsonStr)) return null;
//			return  (T) new Gson().fromJson(jsonStr,cls);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public static String getACCEPT() {
		return ACCEPT;
	}

	@Override
	public T parseResponse(Headers headers, byte[] bytes) throws Throwable {
		String jsonStr = StringRequest.parseResponseString(headers, bytes);

		LogUtils.logDug("HTTP Response Result = "+jsonStr);

		if(null == cls) return  (T)jsonStr;

		try {
			if(TextUtils.isEmpty(jsonStr)) return null;
			return  (T) new Gson().fromJson(jsonStr,cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
