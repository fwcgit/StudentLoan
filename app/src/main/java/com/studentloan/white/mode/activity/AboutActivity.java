package com.studentloan.white.mode.activity;

import android.content.Intent;
import android.net.Uri;

import com.studentloan.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/***
 * 关于我们
 * @author fu
 *
 */
@EActivity(R.layout.activity_about_layout)
public class AboutActivity extends BaseActivity {
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		setTitleText("联系方式");


	}

	@Click
	public void callPhoneImg(){
		Intent intent = new Intent(Intent.ACTION_DIAL);
		Uri data = Uri.parse("tel:" + "4006862671");
		intent.setData(data);
		startActivity(intent);
	}
}
