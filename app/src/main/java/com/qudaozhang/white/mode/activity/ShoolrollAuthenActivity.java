package com.qudaozhang.white.mode.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qudaozhang.white.R;
import com.qudaozhang.white.interfaces.WellCallBack;
import com.qudaozhang.white.mode.view.DateSelector;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/***
 * 学籍认证
 * @author fu
 *
 */
@EActivity(R.layout.activity_shoolroll_authen_layout)
public class ShoolrollAuthenActivity extends BaseActivity {
	@ViewById
	LinearLayout cityLayout,shoolLayout,dateLayout;
	@ViewById
	TextView cityTv,shoolTv,dateTv;
	
	@ViewById
	EditText majorEt,classEt,studentNumberEt,addressEt,stuAccountEt,stuPwdEt;
	
	@ViewById
	Button submitBtn;
	
	boolean isChange = true;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		setTitleText("学籍认证");
		

		
		getXjInfo();
	}
	
	@Click
	public void cityLayout(){
		if(!isChange) return;
	}
	
	@Click
	public void shoolLayout(){
		if(!isChange) return;
		com.qudaozhang.white.mode.activity.SelectShoolActivity_.intent(this).startForResult(SelectShoolActivity.REQUEST_CODE);
	}
	
	@Click
	public void dateLayout(){
		if(!isChange) return;
		DateSelector dataSelect = new DateSelector(this, new WellCallBack() {
			@Override
			public void contentBack(String content) {
				dateTv.setText(content);
			}
		});
		dataSelect.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, -200);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case SelectCityActivity.REQUEST_CODE:
				cityTv.setText(data.getStringExtra("name"));
				cityTv.setTag(data.getStringExtra("code"));
				break;
			case SelectShoolActivity.REQUEST_CODE:
				shoolTv.setText(data.getStringExtra("name"));
				shoolTv.setTag(data.getStringExtra("code"));
				break;
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	} 
	
	public void getXjInfo(){
//		if(app.userInfo == null){
//			app.reLogin();
//			finish();
//			return;
//		}
//
//		if(TextUtils.isEmpty(app.userInfo.mid)){
//			app.reLogin();
//			finish();
//			return;
//		}
//		requestGet(this,ServerInterface.GET_MEMBER_XJINFO.hashCode(), ServerInterface.GET_MEMBER_XJINFO+"?mid="+app.userInfo.mid+"&token="+app.userInfo.token,
//				ShollRollInfoResponse.class, new HttpListener<ShollRollInfoResponse>() {
//
//			@Override
//			public void onSucceed(int what, Response<ShollRollInfoResponse> response) {
//				if(response.isSucceed() && response.get() != null){
//					if(response.get().Success){
//						ShoolRollInfo shoolRollInfo = response.get().Data;
//						cityTv.setText(shoolRollInfo.city);
//						shoolTv.setText(shoolRollInfo.xuexiao);
//						dateTv.setText(shoolRollInfo.rxtime);
//						majorEt.setText(shoolRollInfo.zhuanye);
//						classEt.setText(shoolRollInfo.banji);
//						studentNumberEt.setText(shoolRollInfo.xuehao);
//						addressEt.setText(shoolRollInfo.address);
//						stuAccountEt.setText(shoolRollInfo.xxname);
//						stuPwdEt.setText(shoolRollInfo.xxpwd);
//					}
//
//				}
//			}
//
//					@Override
//					public void onFailed(int what, Response<ShollRollInfoResponse> response) {
//
//					}
//
//
//		}, true);
	}
	@Click
	public void submitBtn(){
//		String city = cityTv.getText().toString();
		String shool = shoolTv.getText().toString();
		String date = dateTv.getText().toString();
		String major = majorEt.getText().toString();
		String classStr = classEt.getText().toString();
		String studentNumber = studentNumberEt.getText().toString();
		String address = addressEt.getText().toString();
		String stuAcc = stuAccountEt.getText().toString();
		String stuPwd = stuPwdEt.getText().toString();
//		if(TextUtils.isEmpty(city)){
//			showToast("所在城市不能为空!");
//			return;
//		}
		
		if(TextUtils.isEmpty(shool)){
			showToast("学校不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(date)){
			showToast("入学时间不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(major)){
			showToast("专业不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(classStr)){
			showToast("班级不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(studentNumber)){
			showToast("学号不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(address)){
			showToast("地址不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(stuAcc)){
			showToast("学信网账号不能为空!");
			return;
		}
		
		if(TextUtils.isEmpty(stuPwd)){
			showToast("学信网密码不能为空!");
			return;
		}
		
//		city	true	string	学校所在城市
//		xuexiao	true	string	学校名称
//		rxtime	true	datetime	入学时间
//		zhuanye	true	string	专业
//		banji	true	string	班级
//		xuehao	true	string	学号
//		address	true	string	宿舍地址
		

	}
}
