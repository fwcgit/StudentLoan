package com.studentloan.white.mode.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
import com.studentloan.white.R;
import com.studentloan.white.mode.view.RoundImageView;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BorrowLimitsResponse;
import com.studentloan.white.utils.TitleBar.TitleCallBack;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/***
 * 个人中心
 * @author fu
 * @create data 2016.05.11
 */
@EFragment(R.layout.fragment_personal_center_layout)
public class PersonalCenterFragment extends BaseFramgent {
	@ViewById
	TextView userNameTv,accountTv,sumTv;
	
	@ViewById
	RoundImageView headImg;
	
	ImageView rightImg;

	@AfterViews
	public void initViews(){
		titleBar.setTitle("个人中心");
		titleBar.addTitleCallBack(new TitleCallBack() {
			
			@Override
			public void rightImg(ImageView rightImg) {
				PersonalCenterFragment.this.rightImg = rightImg;
				//PersonalCenterFragment.this.rightImg.setVisibility(View.VISIBLE);
				PersonalCenterFragment.this.rightImg.setImageResource(R.drawable.icon_shared);
				PersonalCenterFragment.this.rightImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
					}
				});
			}

			@Override
			public void rightTv(TextView tv) {

			}

			@Override
			public void rightTvClick(TextView tv) {

			}

			@Override
			public void rightClick(ImageView rightImg) {
				
				
			}
			
			@Override
			public void backClick(ImageView backImg) {
				
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if(null != userInfo){
			if(!TextUtils.isEmpty(userInfo.account.name)){
				if(userInfo.account.name.length() >= 2){
					userNameTv.setText("*"+userInfo.account.name.substring(1,userInfo.account.name.length()));

				}else{
					userNameTv.setText(userInfo.account.name);
				}
			}

			String phone = userInfo.account.cellphone;
			phone = phone.substring(0,3)+"****"+phone.substring(7,11);
			accountTv.setText(phone);

			if(!TextUtils.isEmpty(userInfo.headImageUrl)){
				ImageLoader.getInstance().displayImage(MyContacts.BASE_URL+userInfo.headImageUrl, headImg);
			}

			getBorrowLimits();
		}

	}
	
	@Click
	public void infoLayout(){
//		//人个信息
//		com.studentloan.android.mode.activity.PersonalInfoActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
		
		//账户信息
		com.studentloan.white.mode.activity.AccountInfoActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	@Click
	public void headImg(){
		
	}
	
	@Click
	public void personalInfoLayout(){

		if(userInfo.submit != 1 || userInfo.verificationResult != 0){

			if(userInfo.identification == null){
				if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
					Toast.makeText(getActivity(),"你的个人信息已超最大认证次数.无法使用",Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if(userInfo.wangYinRenZhengJieGuo != 1){
				if(userInfo.shengYuWangYinRenZhengCiShu <= 0){
					Toast.makeText(getActivity(),"你的网银认证已超最大次数.无法使用",Toast.LENGTH_SHORT).show();

					return;
				}
			}
		}


		if(userInfo.blackList == 1 || userInfo.frozen == 1){
			MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if(msg.arg1 == 1){
						Toast.makeText(getActivity(),"还有未完成的借款!",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getActivity(),"账户已被冻结无法使用",Toast.LENGTH_SHORT).show();
					}
					return false;
				}
			});
		}else{
			//个人资料
			com.studentloan.white.mode.activity.PersonalDataActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
		}

	}
	

	
	@Click
	public void giveMoneyLayout(){
		//奖励金额
		com.studentloan.white.mode.activity.AwardActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	
	@Click
	public void historyNoteLayout(){
		//历史记录
		com.studentloan.white.mode.activity.HistoryNotesActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	
	@Click
	public void processLayout(){
		//进度查询
		com.studentloan.white.mode.activity.ProgressQueryActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	@Click
	public void  msgLayout(){
		//消息
		com.studentloan.white.mode.activity.MsgActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}


	@Click
	public  void bankCarLayout(){

		if(userInfo.submit != 1 || userInfo.verificationResult != 0){

			if(userInfo.identification == null){
				if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
					Toast.makeText(getActivity(),"你的个人信息已超最大认证次数.无法使用",Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if(userInfo.wangYinRenZhengJieGuo != 1){
				if(userInfo.shengYuWangYinRenZhengCiShu <= 0){
					Toast.makeText(getActivity(),"你的网银认证已超最大次数.无法使用",Toast.LENGTH_SHORT).show();

					return;
				}
			}
		}


		if(userInfo.blackList == 1 || userInfo.frozen == 1){
			MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if(msg.arg1 == 1){
						com.studentloan.white.mode.activity.BoundBankCardActivity_.intent(getActivity()).
								flags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK).start();
					}else{
						Toast.makeText(getActivity(),"账户已被冻结无法使用",Toast.LENGTH_SHORT).show();
					}
					return false;
				}
			});
		}else{
			com.studentloan.white.mode.activity.BoundBankCardActivity_.intent(this).
					flags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK).start();
		}


	}

	public void getBorrowLimits(){

		if(userInfo.verificationResult != 1){
			sumTv.setText("可用余额：0元");
			return;
		}

		String  formatUrl = String.format(ServerInterface.BORROW_LIMITS,userInfo.account.cellphone,userInfo.token);
		NoHttpRequest.getInstance().requestGet(getActivity(),formatUrl.hashCode(),formatUrl, BorrowLimitsResponse.class, new HttpListener<BorrowLimitsResponse>() {
			@Override
			public void onSucceed(int what, Response<BorrowLimitsResponse> response) {
				if(response.isSucceed() && response.get() != null){
					int[] result = response.get().result;
					if(result == null || result.length <= 0){
						sumTv.setText("可用余额：0元");
					}else{
						int temp = result[0];
						for(int i = 0 ; i < result.length ; i++){
							if(temp <= result[i]){
								temp = result[i];
							}
						}

						sumTv.setText("可用余额："+temp+"元");
					}
				}
			}

			@Override
			public void onFailed(int what, Response<BorrowLimitsResponse> response) {

			}
		},false);
	}
	
}
