package com.studentloan.white.mode.activity;

import android.widget.Button;
import android.widget.EditText;

import com.studentloan.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/***
 * 我的支付宝
 * @author fu
 *
 */
@EActivity(R.layout.activity_bound_alipay_layout)
public class BoundAlipayActivity extends BaseActivity {
	@ViewById
	EditText alipayEt;
	@ViewById
	Button submitBtn;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		setTitleText("我的支付宝");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	public void showView(){

	}
	
	@Click
	public void submitBtn(){

	}
}
