package com.studentloan.white.mode.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.studentloan.white.R;

import android.text.TextUtils;
import android.widget.EditText;

@EActivity(R.layout.activity_xuexin_layout)
public class XueXinAuthActivity extends BaseActivity {
	@ViewById
	public EditText xuexinAccount,xuexinPwdEt;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		
	}
	
	@Click
	public void submitBtn(){
		String account = xuexinAccount.getText().toString();
		String pwd = xuexinPwdEt.getText().toString();
		
		if(TextUtils.isEmpty(account)){
			showToast("学信网账号不能空！");
			return;
		}
		
		if(TextUtils.isEmpty(pwd)){
			showToast("学信密码号不能空！");
			return;
		}
	}
}
