package com.qudaozhang.white.mode.fragment;

import android.content.Intent;
import android.widget.TextView;

import com.qudaozhang.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/***
 * 更多
 * @author fu
 * @create date 2016.05.11
 */
@EFragment(R.layout.fragment_more_layout)
public class MoreFragment extends BaseFramgent {
	@ViewById
	public TextView versionTv;

	@AfterViews
	public void initViews(){
		titleBar.setTitle("更多");
		versionTv.setText("V"+app.systemOpt.appSysInfo.getAppVersion());
	}
	
	@Click
	public void aboutInfoLayout(){
		//关于我们
		com.qudaozhang.white.mode.activity.AboutActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	
	@Click
	public void newHardGuidLayout(){
		com.qudaozhang.white.mode.activity.GuideActivity_.intent(this).ishowBtn(false).start();
	}
	
	@Click
	public void helpLayout(){
//		//帮助
		com.qudaozhang.white.mode.activity.HelpActivity_.intent(this).start();

	}
}
