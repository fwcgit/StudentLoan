package com.studentloan.white.net;

import android.content.Context;

import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
import com.studentloan.white.utils.LogUtils;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;

import java.io.File;
import java.util.Map;


/***
 * NoHttp网络框架
 * @author fu
 *
 */
public class NoHttpRequest {
	private static NoHttpRequest httpRequest;
	public static NoHttpRequest getInstance(){
		return httpRequest == null ? httpRequest = new NoHttpRequest() :httpRequest;
	}
	private  NoHttpRequest(){
		
	}
	
	public void init(Context ctx){
		NoHttp.initialize(MyApplication.getInstance());
		Logger.setDebug(false); // 开启NoHttp调试模式。
		Logger.setTag("NoHttpSample"); // 设置NoHttp打印Log的TAG。
	}

	public <T>  void requestPostBody(Context context ,int what,Map<String,String> params,String bodyData,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = POST" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL+ interfaceUrl);

		if(null != params){
			LogUtils.logDug("request params :");

			for (String key : params.keySet()) {
				LogUtils.logDug(key + " :" + params.get(key));
			}
		}


		if(null != bodyData){
			LogUtils.logDug("request bodyData :"+bodyData);
		}



		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.POST,cls);


		if(null != params){
			request.add(params);
		}

		if(null != bodyData){
			request.setDefineRequestBodyForJson(bodyData);
		}

		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);

	}

	public <T>  void requestPost(Context context ,int what,Map<String,String> params,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = POST" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL+ interfaceUrl);

		if(null != params){
			LogUtils.logDug("request params :");

			for (String key : params.keySet()) {
				LogUtils.logDug(key + " :" + params.get(key));
			}
		}



		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.POST,cls);

		if(null != params){
			request.add(params);
		}

		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);
	}

	public <T>  void requestPostUrl(Context context ,int what,Map<String,String> params,String bodyData,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = POST" );
		LogUtils.logDug("request url = "+interfaceUrl);

		if(null != params){
			LogUtils.logDug("request params :");

			for (String key : params.keySet()) {
				LogUtils.logDug(key + " :" + params.get(key));
			}
		}

		if(null != bodyData){
			LogUtils.logDug("request bodyData :"+bodyData);
		}
		
		Request<T> request = new CustomDataRequest<T>(interfaceUrl,RequestMethod.POST,cls);

		if(null != params){
			request.add(params);
		}

		if(null != bodyData){
			request.setDefineRequestBodyForJson(bodyData);
		}

		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);
	}

	public <T>  void requestDel(Context context ,int what,Map<String,String> params,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = POST" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL+ interfaceUrl);

		if(null != params){
			LogUtils.logDug("request params :");

			for (String key : params.keySet()) {
				LogUtils.logDug(key + " :" + params.get(key));
			}
		}

		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.DELETE,cls);

		if(null != params){
			request.add(params);
		}

		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);
	}

	public <T>  void requestPut(Context context ,int what,Map<String,String> params,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = PUT" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL+ interfaceUrl);

		if(null != params){
			LogUtils.logDug("request params :");

			for (String key : params.keySet()) {
				LogUtils.logDug(key + " :" + params.get(key));
			}
		}

		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.PUT,cls);

		if(null != params){
			request.add(params);
		}

		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);

	}
	
	public <T>  void requestGet(Context context ,int what,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = Get" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL+ interfaceUrl);
		
         
		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.GET,cls);
		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);
		
	}
	
	/****
	 * 上传文件
	 * @param what
	 * @param fileAbspath
	 * @param interfaceUrl
	 * @param callback
	 * @param isShowDialog
	 */
	public <T>void upFilePost(Context context,int what,String fileAbspath,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = POST" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL );
		LogUtils.logDug("request params :");
		LogUtils.logDug("filePath = " + fileAbspath);
		
		if(!(new File(fileAbspath).exists())){
			LogUtils.logDug("文件不存在");
			return;
		}
		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.POST,cls);
		request.add("file", new FileBinary(new File(fileAbspath)));
		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);
	}
	
	
	/****
	 * 上传文件
	 * @param what
	 * @param fileAbspath
	 * @param interfaceUrl
	 * @param callback
	 * @param isShowDialog
	 */
	public <T>void upFilePost(Context context,int what,String fileAbspath[],String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		LogUtils.logDug("net request start");
		LogUtils.logDug("request what =  " + what);
		LogUtils.logDug("request method = POST" );
		LogUtils.logDug("request url = "+MyContacts.BASE_URL );
		LogUtils.logDug("request params :");
		LogUtils.logDug("filePath :" );
		for (String string : fileAbspath) {
			if(string != null){
				LogUtils.logDug(string);
			}
		}
		
		Request<T> request = new CustomDataRequest<T>(MyContacts.BASE_URL+interfaceUrl,RequestMethod.POST,cls);
		
		
		for (int i = 0; i < fileAbspath.length; i++) {
			String path = fileAbspath[i];
			if(path != null){
				request.add("file"+i, new FileBinary(new File(path)));
			}
			
		}
		CallServer.getRequestInstance().add(context, what, request, callback,MyApplication.mainActivity,isShowDialog);
	}
}
