package com.qudaozhang.white.mode.activity;

import android.widget.TextView;

import com.qudaozhang.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 奖励金额
 * @author fu
 *
 */
@EActivity(R.layout.activity_award_layout)
public class AwardActivity extends BaseActivity {
	@ViewById
	TextView priceTv,inviteTv;
	
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		setTitleText("奖励金额");
		getUserInfo();

		priceTv.setText(userInfo.jiangLiJinE+"元");

		if(userInfo.account != null){
			inviteTv.setText(userInfo.account.inviteCode);
		}


	}
}
