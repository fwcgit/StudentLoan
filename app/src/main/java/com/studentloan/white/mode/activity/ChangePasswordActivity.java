package com.studentloan.white.mode.activity;

import android.text.TextUtils;
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

/***
 * 修改密码
 * @author fu
 *
 */
@EActivity(R.layout.activity_change_password_layout)
public class ChangePasswordActivity extends BaseActivity {
	@ViewById
	EditText oldPwdEt,newPwdEt,newRepeatPwdEt;
	
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		setTitleText("修改密码");

		getUserInfo();
	}
	
	@Click
	public void confirmBtn(){

		String oldpwd = oldPwdEt.getText().toString();
		String newPwd = newPwdEt.getText().toString();
		String newRepeatPwd = newRepeatPwdEt.getText().toString();

		if(TextUtils.isEmpty(oldpwd) || !app.isValidPassword(oldpwd)){
			showToast("旧密码错误!");
			return;
		}

		if(TextUtils.isEmpty(newPwd) || !app.isValidPassword(newPwd)){
			showToast("新密码错误!");
			return;
		}

		if(TextUtils.isEmpty(newRepeatPwd) || !app.isValidPassword(newRepeatPwd)){
			showToast("重复新密码错误!");
			return;
		}

		if(!newPwd.equals(newRepeatPwd)){
			showToast("新密码与重复密码不一致!");
			return;
		}


		oldpwd = MD5.md5(oldpwd);
		newPwd = MD5.md5(newPwd);

		String urlFormat = String.format(ServerInterface.MODIFY_PWD,userInfo.account.cellphone,oldpwd,newPwd,userInfo.token);

		requestPut(urlFormat.hashCode(), null, urlFormat, BooleanResponse.class, new HttpListener<BooleanResponse>() {

			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null){
					if(response.get().result){
						showToast("密码修改成功");
						app.reLogin();
					}else{
						showToast("密码修改失败");
					}

				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}

		}, true);

	}
}
