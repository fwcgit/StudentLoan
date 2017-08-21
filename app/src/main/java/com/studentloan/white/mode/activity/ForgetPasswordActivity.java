package com.studentloan.white.mode.activity;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.widget.Button;
import android.widget.EditText;

import com.studentloan.white.R;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.utils.MD5;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.activity_forget_password_layout)
public class ForgetPasswordActivity extends BaseActivity {
	@ViewById
	EditText accountEt,smsCodeEt,aginPwdEt,passEt;
	@ViewById
	Button sendCodeBtn;
	private Timer timer;
	private int sendCodeTime = 60;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		titleBar.setTitle("忘记密码");

		setPassHint();

		getUserInfo();
	}

	private void setPassHint(){
		String hint = passEt.getHint().toString();
		SpannableString span = new SpannableString(hint);
		span.setSpan(new AbsoluteSizeSpan(sp2px(10)), hint.indexOf('('), hint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		passEt.setHint(span);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 *
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public  int sp2px(float spValue) {
		final float fontScale = getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	@Click
	public void submitBtn(){
		String phone  = accountEt.getText().toString();
		String smsCode = smsCodeEt.getText().toString();
		String pwd = passEt.getText().toString();
		String aginPwd = aginPwdEt.getText().toString();
		
		if(TextUtils.isEmpty(phone)){
			showToast("请输入合法的手机号");
			return;
		}


		if(!app.isTelPhone(phone)){
			showToast("请输入合法的手机号");
			return;
		}
		
		if(TextUtils.isEmpty(smsCode)){
			showToast("短信验证码不正确");
			return;
		}
		
		
		if(TextUtils.isEmpty(pwd)){
			showToast("密码不正确");
			return;
		}
		
		if(TextUtils.isEmpty(aginPwd)){
			showToast("确认密码不正确");
			return;
		}
		
		if(pwd.length() >= 6 && !app.isValidPassword(pwd)){
			showToast("密码不正确");
			return;
		}
		
		if(aginPwd.length() >= 6 && !app.isValidPassword(aginPwd)){
			showToast("确认密码不正确");
			return;
		}
		
		if(!pwd.equals(aginPwd)){
			showToast("两交密码不一致!");
			return;
		}

        pwd = MD5.md5(pwd);

		String urlFormat = String.format(ServerInterface.RESET_PWD,userInfo.account.cellphone,smsCode,pwd);
		
		requestPut(urlFormat.hashCode(), null,urlFormat, BooleanResponse.class, new HttpListener<BooleanResponse>(){

			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null ){
					//stopTimer();
					if(response.get().result){
						showToast("密码已重置");
						finish();
					}else{
						showToast("密码重置失败");
					}
				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}


		}, true);
	}
	
	@Click
	public void sendCodeBtn(){
		String phone  = accountEt.getText().toString();
		if(TextUtils.isEmpty(phone)){
			showToast("手机号不能为空!");
			return;
		}
		startTimer();
		String urlFormat = String.format(ServerInterface.SEND_SMS_RESET,phone);

		requestPost(urlFormat.hashCode(), null, urlFormat,BooleanResponse.class,new HttpListener<BooleanResponse>(){

			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null ){
					//stopTimer();
					if(response.get().result){

					}
				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}

		} ,false);
	}
	
    private void startTimer(){
    	if(timer != null){
    		timer.cancel();
    		timer = null;
    	}
    	sendCodeTime = 60;
    	sendCodeBtn.setEnabled(false);
    	timer = new Timer();
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						sendCodeBtn.setText(sendCodeTime+"后重发");
						sendCodeTime--;
						if(sendCodeTime <= 0){
							stopTimer();
						}
					}
				});
			}
		}, 0, 1000);
    }
    
    private void stopTimer(){
    	sendCodeTime = 0;
    	sendCodeBtn.setEnabled(true);
		sendCodeBtn.setText("发送验证码");
    	if(timer != null){
    		timer.cancel();
    		timer = null;
    	}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	stopTimer();
    }
}
