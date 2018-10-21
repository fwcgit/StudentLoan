package com.qudaozhang.white.mode.activity;

import android.content.Intent;

import com.qudaozhang.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_help_activity)
public class HelpActivity extends BaseActivity {
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		setTitleText("帮助中心");
	}
	
	private void invokeActivity(int index){
		com.qudaozhang.white.mode.activity.HelpDetailActivity_.intent(this).
		flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).index(index).start();
	}
	@Click
	public void layout1(){
		invokeActivity(0);
	}
	@Click
	public void layout2(){
		invokeActivity(1);
	}
	@Click
	public void layout3(){
		invokeActivity(2);
	}
	@Click
	public void layout4(){
		invokeActivity(3);
	}
	@Click
	public void layout5(){
		invokeActivity(4);
	}

}
