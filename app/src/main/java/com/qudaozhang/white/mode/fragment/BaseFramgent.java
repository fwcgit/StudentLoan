package com.qudaozhang.white.mode.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.qudaozhang.white.MyApplication;
import com.qudaozhang.white.net.data.UserInfo;
import com.qudaozhang.white.utils.TitleBar;

/**
 * 基类fragment
 * @author fu
 * @create date 2016.05.11
 */
public class BaseFramgent extends Fragment  {
	public TitleBar titleBar = new TitleBar();
	public MyApplication app;
	public UserInfo userInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = MyApplication.getInstance();
		userInfo = app.getUserInfo();
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		titleBar.initView(getView(),false);
		super.onViewCreated(view, savedInstanceState);
	}

	public UserInfo getUserInfo(){
		if(null == app.userInfo){
			app.reLogin();
		}else{
			this.userInfo = app.userInfo;
		}

		return app.userInfo;
	}

	public void showToast(String str){
		Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
	}
	
}
