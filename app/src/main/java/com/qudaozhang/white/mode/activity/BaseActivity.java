package com.qudaozhang.white.mode.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qudaozhang.white.MyApplication;
import com.qudaozhang.white.net.HttpListener;
import com.qudaozhang.white.net.NoHttpRequest;
import com.qudaozhang.white.net.data.UserInfo;
import com.qudaozhang.white.utils.TitleBar;

import java.util.Map;

/***
 * 基类Activity
 * @author fu
 * @create date 2016.05.11
 */
public class BaseActivity extends Activity implements TitleBar.TitleCallBack{
	public MyApplication app;
	public TitleBar titleBar = new TitleBar();
	public UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (MyApplication) getApplication();
		if(!(this instanceof GuideActivity || this instanceof LoadingActivity)){
			app.setSystemBar(this);
		}

		if(!(this instanceof LoginActivity)){
			registerReceiver(broadcastReceiver, new IntentFilter("close"));
		}


	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("close")){
				if(!(BaseActivity.this instanceof LoginActivity)){
					finish();
				}
				
			}
		}
	};
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(!(this instanceof LoginActivity)){
			if(broadcastReceiver != null){
				unregisterReceiver(broadcastReceiver);
			}
		}

	}


	public void initViews(){
		titleBar.initView(getWindow().getDecorView().getRootView(),true);
		titleBar.addTitleCallBack(this);
	}


	/***
	 * 返回按钮
	 */
	@Override
	public void backClick(ImageView backImg) {
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		//JPushInterface.onPause(this);
		MyApplication.isForeground = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//JPushInterface.onResume(this);
		MyApplication.isForeground = true;
		MyApplication.activity = this;
		if(this instanceof LoginActivity_){
			MyApplication.activity = null;
		}
	}


	/***
	 * 右边按钮
	 */
	@Override
	public void rightClick(ImageView rightImg) {
	}
	
	public  void setTitleText(String title){
		if(titleBar != null){
			titleBar.setTitle(title);
		}
	}


	public <T> void requestPostBody(int what,Map<String,String> params,String bodyData,String interfaceUrl,Class<?> cls,HttpListener<T> callback, boolean isShowDialog){
		NoHttpRequest.getInstance().requestPostBody(this,what, params,bodyData, interfaceUrl,cls, callback,isShowDialog);
	}

	public <T> void requestPost(int what,Map<String,String> params,String interfaceUrl,Class<?> cls,HttpListener<T> callback, boolean isShowDialog){
		NoHttpRequest.getInstance().requestPost(this,what, params, interfaceUrl,cls, callback,isShowDialog);
	}

	public <T> void requestPostUrl(int what,Map<String,String> params,String body,String interfaceUrl,Class<?> cls,HttpListener<T> callback, boolean isShowDialog){
		NoHttpRequest.getInstance().requestPostUrl(this,what, params,body, interfaceUrl,cls, callback,isShowDialog);
	}

	public <T> void requestPut(int what,Map<String,String> params,String interfaceUrl,Class<?> cls,HttpListener<T> callback, boolean isShowDialog){
		NoHttpRequest.getInstance().requestPut(this,what, params, interfaceUrl,cls, callback,isShowDialog);
	}

	public <T> void requestDel(int what,Map<String,String> params,String interfaceUrl,Class<?> cls,HttpListener<T> callback, boolean isShowDialog){
		NoHttpRequest.getInstance().requestDel(this,what, params, interfaceUrl,cls, callback,isShowDialog);
	}
	
	public <T>  void requestGet(int what,String interfaceUrl,Class<?> cls,HttpListener<T> callback,boolean isShowDialog){
		NoHttpRequest.getInstance().requestGet(this, what, interfaceUrl, cls, callback, isShowDialog);
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
		NoHttpRequest.getInstance().upFilePost(context, what, fileAbspath, interfaceUrl, cls, callback, isShowDialog);
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
		NoHttpRequest.getInstance().upFilePost(context, what, fileAbspath, interfaceUrl, cls, callback, isShowDialog);
	}
	
	public void showToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int regId){
		Toast.makeText(this,regId,Toast.LENGTH_SHORT).show();
	}
	
	public void findEditTextEnable(ViewGroup viewGroup){
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View view = viewGroup.getChildAt(i);
			if(view instanceof ViewGroup){
				findEditTextEnable((ViewGroup) view);
			}else if(view instanceof EditText){
				((EditText)view).setEnabled(false);
			}
		}
	}


	@Override
	public void rightImg(ImageView rightImg) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rightTv(TextView tv) {

	}

	@Override
	public void rightTvClick(TextView tv) {

	}

	public UserInfo getUserInfo(){
		if(null == app.userInfo){
			app.reLogin();
		}else{
			this.userInfo = app.userInfo;
		}

		return app.userInfo;
	}
	
}
