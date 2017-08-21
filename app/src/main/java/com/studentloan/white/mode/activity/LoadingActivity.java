package com.studentloan.white.mode.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.studentloan.white.R;
import com.studentloan.white.utils.JurisdictionApply;
import com.studentloan.white.utils.SettingShareData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/***
 * 启动加载
 * @author fu
 * @create date 2016.05.11
 */
@EActivity(R.layout.activity_loading_layout)
public class LoadingActivity extends BaseActivity implements JurisdictionApply.IJurisdictionListener {
	Handler handler = new Handler();
	
	@RequiresApi(api = Build.VERSION_CODES.M)
	@AfterViews
	public void initViews(){
		JurisdictionApply.getInstance().setJurisdictionListener(this);

		JurisdictionApply.getInstance().init(this);

		JurisdictionApply.getInstance().applyJuricdiction();

	}

	@Override
	public void applyfinish() {
		handler.postDelayed(new Runnable() {
			public void run() {
				finish();

				if(!SettingShareData.getInstance(app).getKeyValueBoolean("guide", false)){
					GuideActivity_.intent(LoadingActivity.this).ishowBtn(true).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
				}else{
					LoginActivity_.intent(LoadingActivity.this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
				}

			}
		}, 3 * 1000);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		JurisdictionApply.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
	}
}
