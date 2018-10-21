package com.qudaozhang.white.mode.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.qudaozhang.white.MyApplication;
import com.qudaozhang.white.R;
import com.qudaozhang.white.net.HttpListener;
import com.qudaozhang.white.net.ServerInterface;
import com.qudaozhang.white.net.data.LoginInfoResponse;
import com.qudaozhang.white.utils.LogUtils;
import com.qudaozhang.white.utils.MD5;
import com.qudaozhang.white.utils.SettingShareData;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/***
 * 登陆
 * @author fu
 * @create date 2016.05.11
 */
@EActivity(R.layout.activity_login_layout)
public class LoginActivity extends BaseActivity implements HttpListener<LoginInfoResponse>  {
	@ViewById
	public EditText userEt,pwdEt;
	@ViewById
	public CheckBox rememCb;
	@ViewById
	public Button loginBtn;
	
	@Extra
	public boolean isPushMsg = false;

	public boolean isCheck = true;

	@Override
	@AfterViews
	public void initViews(){
		super.initViews();

		LogUtils.logDug("LoginActivity");
		//checkUpdateVersion();

		MyApplication.isLogin = false;
		
		titleBar.setTitle("用户登录");
		titleBar.showBack(false);
		
		if(SettingShareData.getInstance(this).getKeyValueBoolean("remem", false)){
			rememCb.setChecked(true);
			String user = SettingShareData.getInstance(this).getKeyValueString("user", "");
			String pwd = SettingShareData.getInstance(this).getKeyValueString("pwd", "");
			userEt.setText(user);
			pwdEt.setText(pwd);
		}else{
			rememCb.setChecked(false);
		}
	}
	
	
	@Click
	public void loginBtn(){
		if(!isCheck) return;

		if(TextUtils.isEmpty(userEt.getText().toString())){
			showToast("请输入合法的手机号");
			return;
		}

		if(!app.isTelPhone(userEt.getText().toString())){
			showToast("请输入合法的手机号");
			return;
		}
		
		if(TextUtils.isEmpty(pwdEt.getText().toString())){
			showToast("密码不正确");
			return;
		}
		if(!app.isValidPassword(pwdEt.getText().toString())){
			showToast("密码不正确");
			return;
		}

		loginBtn.setEnabled(false);
		
        String pwd = pwdEt.getText().toString();
        pwd = MD5.md5(pwd);

        String urlFormat = String.format(ServerInterface.LOGIN,userEt.getText().toString(),pwd,app.systemOpt.appSysInfo.getAppVersion());
		requestPost(urlFormat.hashCode(), null, urlFormat, LoginInfoResponse.class,this,true);
	}
	
	@Click
	public void registerBtn(){
		com.qudaozhang.white.mode.activity.RegisterActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	
	@Click
	public void forgetPwdTv(){
		com.qudaozhang.white.mode.activity.ForgetPasswordActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}


	@Override
	public void onSucceed(int what, Response<LoginInfoResponse> response) {

		loginBtn.setEnabled(true);

		if(response.isSucceed()&&response.get() != null){
			if(response.get().errorCode.equals("0")){
				if(rememCb.isChecked()){
					SettingShareData.getInstance(this).setKeyValue("user", userEt.getText().toString());
					SettingShareData.getInstance(this).setKeyValue("pwd", pwdEt.getText().toString());
					SettingShareData.getInstance(this).setKeyValue("remem", true);
				}else{
					SettingShareData.getInstance(this).setKeyValue("user", "");
					SettingShareData.getInstance(this).setKeyValue("pwd", "");
					SettingShareData.getInstance(this).setKeyValue("remem", false);
				}
				app.userInfo = response.get().result;
				MyApplication.isLogin = true;
				com.qudaozhang.white.MainActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP ).start();
				finish();
			}
		}
	}

	@Override
	public void onFailed(int what, Response<LoginInfoResponse> response) {
		loginBtn.setEnabled(true);
	}

}
