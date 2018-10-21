package com.studentloan.white;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.mode.fragment.HomeFragment;
import com.studentloan.white.mode.fragment.MoreFragment;
import com.studentloan.white.mode.fragment.PersonalCenterFragment;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.Borrow;
import com.studentloan.white.net.data.BorrowResponse;
import com.studentloan.white.net.data.Shenghe;
import com.studentloan.white.net.data.ShengheResponse;
import com.studentloan.white.net.data.UserInfo;
import com.studentloan.white.utils.DialogUtils;
import com.studentloan.white.utils.SystemOpt;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/***
 * 主Activity
 * @author fu
 *1.0.2
 */
@SuppressLint("NewApi")
@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

	private static final int SD_WRITE_REQUEST_CODE = 0X212;
	private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X211;
	private static final int READ_PHONE_STATE = 0X213;

	private static final int RESULT_CAMARE_IMAGE = 0x001;
	private static final int RESULT_PHOTO_IMAGE = 0x002;

	@ViewById
	public ImageView homeImg,persionImg,moreImg;
	@ViewById
	public LinearLayout layoutButtom;

	private HomeFragment homeFragment;
	private PersonalCenterFragment personalFragment;
	private MoreFragment moreFragment;

	private int currIndex = 0;
	private Fragment[] fragments;
	private   boolean isShip = false;
	private  int skipIndex = 0;
	@ViewById
	View speedLoanLayout,personalLayout,moreLayout;


	int[] selectRes = new int[]{R.drawable.icon_home_select,R.drawable.icon_persoal_center_select,R.drawable.icon_more_select};
	int[] normalRes = new int[]{R.drawable.icon_home_normal,R.drawable.icon_personal_center_normal,R.drawable.icon_more_normal};

	ImageView[] selectImgs;

	private long mExitTime;

	public void skipFragment(int i){
		isShip = true;
		skipIndex = i;
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
	}

	@AfterViews
	public void initViews(){

		MyApplication.mainActivity = this;

		MyApplication.getInstance().setSystemBar(this);

		SystemOpt.getInstance().init(this);//读取系统信息

		homeFragment = com.studentloan.white.mode.fragment.HomeFragment_.builder().build();
		personalFragment = com.studentloan.white.mode.fragment.PersonalCenterFragment_.builder().build();
		moreFragment = com.studentloan.white.mode.fragment.MoreFragment_.builder().build();

		getSupportFragmentManager().beginTransaction().
		add(R.id.homeLayout,homeFragment).
		add(R.id.homeLayout,personalFragment).
		add(R.id.homeLayout,moreFragment).
		hide(moreFragment).hide(personalFragment).show(homeFragment).commit();

		fragments = new Fragment[]{homeFragment,personalFragment,moreFragment};

		selectImgs = new ImageView[]{homeImg,persionImg,moreImg};


		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
			requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
		}

		if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
			requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
		}

		if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
			requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, SD_WRITE_REQUEST_CODE);
		}else{
			MyApplication.getInstance().initBaseDb();
		}


		if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
			requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, SD_WRITE_REQUEST_CODE);
		}

		checkVersionUpdate();

	}

	@Override
	protected void onResume() {
		super.onResume();

		MyApplication.isForeground = true;
		MyApplication.activity = this;

		getShengheState();

		if(isShip){
			isShip = false;
			switchFragment(skipIndex);
			switch (skipIndex)
			{
				case 0:
					findTextViewSetColor(speedLoanLayout);
					break;
				case 1:
					findTextViewSetColor(personalLayout);
					break;
				case 2:
					findTextViewSetColor(moreLayout);
					break;
			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyApplication.isForeground = false;
	}

	@Click
	public void speedLoanLayout(View v){
		switchFragment(0);
		findTextViewSetColor(v);
	}
	@Click
	public void personalLayout(View v){
		switchFragment(1);
		findTextViewSetColor(v);
	}
	@Click
	public void moreLayout(View v){
		switchFragment(2);
		findTextViewSetColor(v);
	}

	public void switchFragment(int index){
		getShengheState();
		if(index != currIndex){
			setSelectImg(index);
			getSupportFragmentManager().beginTransaction().hide(fragments[currIndex]).commit();
			getSupportFragmentManager().beginTransaction().show(fragments[index]).commit();
			currIndex = index;
		}
	}

	private void findTextViewSetColor(View child){
		for (int i = 0; i < layoutButtom.getChildCount(); i++) {
			LinearLayout layout = (LinearLayout) layoutButtom.getChildAt(i);
			for (int j = 0; j < layout.getChildCount(); j++) {
				View v = layout.getChildAt(j);
				if(v instanceof TextView){
					TextView tv = (TextView) v;
					if(layout == child){
						tv.setTextColor(ContextCompat.getColor(this, R.color.home_select));
					}else{
						tv.setTextColor(ContextCompat.getColor(this, R.color.home_normal));
					}
				}
			}
		}
	}

	private void setSelectImg(int index){
		for (int i = 0; i < selectImgs.length; i++) {
			ImageView img = selectImgs[i];
			if(i == index){
				img.setImageResource(selectRes[index]);
			}else{
				img.setImageResource(normalRes[i]);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else{
				MyApplication.isLogin = false;
				MyApplication.mainActivity = null;
				finish();
				//android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == SD_WRITE_REQUEST_CODE) {
			if (grantResults != null && grantResults.length > 0) {
				if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
					Toast.makeText(MainActivity.this, "请允许访问外部存储设备！，再次授权！", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					MyApplication.getInstance().initBaseDb();
				}
			} else {
				Toast.makeText(MainActivity.this, "请允许访问外部存储设备！，再次授权！", Toast.LENGTH_SHORT).show();
				finish();
			}

		}else if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
			if(grantResults[0] == PackageManager.PERMISSION_DENIED){
				Toast.makeText(MainActivity.this,"相机授权拒绝,请到授权管理打开，再次授权！",Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	public void getShengheState(){
		if(MyApplication.getInstance().userInfo == null) return;

		String formartUrl = String.format(ServerInterface.PASS_VERIFICATION,MyApplication.getInstance().userInfo.account.cellphone);

		NoHttpRequest.getInstance().requestGet(this, formartUrl.hashCode(), formartUrl, ShengheResponse.class, new HttpListener<ShengheResponse>() {
			@Override
			public void onSucceed(int what, Response<ShengheResponse> response) {
				if(response.isSucceed() && response.get() != null){

					if(response.get().result != null){
						Shenghe sh = response.get().result;

						MyApplication.getInstance().userInfo.verificationResult = sh.verificationResult;
						MyApplication.getInstance().userInfo.verificationDateTime = sh.verificationDateTime;
						MyApplication.getInstance().userInfo.submit = sh.submit;
						MyApplication.getInstance().userInfo.submitTime = sh.submitTime;
						MyApplication.getInstance().userInfo.submitCiShu = sh.submitCiShu;
						MyApplication.getInstance().userInfo.blackList = sh.blackList;
						MyApplication.getInstance().userInfo.frozen = sh.frozen;
						MyApplication.getInstance().userInfo.yunYingShangVeriTime = sh.yunYingShangVeriTime;
						MyApplication.getInstance().userInfo.shengYuShenFenRenZhengCiShu = sh.shengYuShenFenRenZhengCiShu;
						MyApplication.getInstance().userInfo.shengYuWangYinRenZhengCiShu = sh.shengYuWangYinRenZhengCiShu;
					}

				}
			}

			@Override
			public void onFailed(int what, Response<ShengheResponse> response) {

			}
		},false);
	}


	public void getHuankuan(final Handler.Callback callback){
		String formatUrl = String.format(ServerInterface.BORROW_PROGRESS,MyApplication.getInstance().userInfo.account.cellphone,MyApplication.getInstance().userInfo.token);
		NoHttpRequest.getInstance().requestGet(this,formatUrl.hashCode(), formatUrl, BorrowResponse.class, new HttpListener<BorrowResponse>() {
				@TargetApi(Build.VERSION_CODES.N)
				@Override
				public void onSucceed(int what, Response<BorrowResponse> response) {
					Message msg = new Message();
					if(response.isSucceed() && response.get() != null){
						if(response.get().result != null){
							Borrow br = response.get().result;

							if(br.jieKuanZhuangTai == 0 || br.jieKuanZhuangTai == 1 || br.jieKuanZhuangTai == 2){
								msg.arg1= 1;
							}else{
								msg.arg1= 0;
							}

						}else{
							msg.arg1= 0;
						}
					}else{
						msg.arg1= 0;
					}

					callback.handleMessage(msg);

				}

				@Override
				public void onFailed(int what, Response<BorrowResponse> response) {
					Message msg = new Message();
					msg.arg1= -1;
					callback.handleMessage(msg);
				}
			},true);
		}


		private void checkVersionUpdate(){

			final UserInfo ui = MyApplication.getInstance().userInfo;
			String versionName = SystemOpt.getInstance().appSysInfo.getAppVersion();

			if(!versionName.equals(ui.latestVersion)){
				DialogUtils.getInstance().showUpdate(this, new DialogCallback() {
					@Override
					public void confirm() {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_BROWSABLE);
						intent.setData(Uri.parse(MyContacts.BASE_URL+ui.downloadUrl));
						startActivity(intent);
					}

					@Override
					public void cancel() {
						if(ui.foreceUpdate){
							MyApplication.isLogin = false;
							MyApplication.mainActivity = null;
							finish();
						}
					}
				});
			}
		}


}
