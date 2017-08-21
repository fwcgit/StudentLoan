package com.studentloan.white.mode.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.studentloan.white.MyApplication;
import com.studentloan.white.R;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.Borrow;
import com.studentloan.white.net.data.BorrowResponse;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;


/***
 * 首页
 * @author fu
 * @create date 2016.05.11
 */
@EFragment(R.layout.fragment_home_layout)
public class HomeFragment extends BaseFramgent {

	@AfterViews
	public void initViews() {
		titleBar.setTitle("首页");

	}

	@Click
	public void lijidkImg() {

		if(userInfo.submit != 1 || userInfo.verificationResult != 1){
			Toast.makeText(getActivity(),"请完成个人信息认证！",Toast.LENGTH_SHORT).show();
			return;
		}

		if(userInfo.submit == 1 && userInfo.verificationResult == 1){

			if (userInfo.blackList == 1 || userInfo.frozen == 1) {
				MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
					@Override
					public boolean handleMessage(Message msg) {
						if (msg.arg1 == 1) {
							Toast.makeText(getActivity(), "还有未完成的借款!", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), "账户已被冻结无法使用", Toast.LENGTH_SHORT).show();
						}
						return false;
					}
				});
			} else {

				MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
					@Override
					public boolean handleMessage(Message msg) {
						if (msg.arg1 == 1) {
							Toast.makeText(getActivity(), "还有未完成的借款!", Toast.LENGTH_SHORT).show();
						}else{

							if(userInfo.identification == null){
								if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
									Toast.makeText(getActivity(),"你的个人信息已超最大认证次数.无法使用",Toast.LENGTH_SHORT).show();
									return false;
								}
							}

							if(userInfo.wangYinRenZhengJieGuo != 1){
								if(userInfo.shengYuWangYinRenZhengCiShu <= 0){
									Toast.makeText(getActivity(),"你的网银认证已超最大次数.无法使用",Toast.LENGTH_SHORT).show();

									return false;
								}
							}

							int dayTime = 24 * 60 * 60 * 1000;

							if(null == userInfo.yunYingShangVeriTime || (userInfo.serverTime - userInfo.yunYingShangVeriTime)/dayTime > 60  ){
								Toast.makeText(getActivity(),"请完成手机运营商信息",Toast.LENGTH_SHORT).show();
								return false;
							}

							com.studentloan.white.mode.activity.BorrowMoneyActivity_.intent(getActivity()).title("立即借款").flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
						}
						return false;
					}
				});


			}


		}

	}

	@Click
	public void nowRefundImg() {

		if(userInfo.submit != 1 || userInfo.verificationResult != 1){
			Toast.makeText(getActivity(),"请完成个人信息认证！",Toast.LENGTH_SHORT).show();
			return;
		}

		if(userInfo.submit == 1 && userInfo.verificationResult == 1){
			if (userInfo.blackList == 1 || userInfo.frozen == 1) {
				MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
					@Override
					public boolean handleMessage(Message msg) {
						if (msg.arg1 == 1) {
							getHuankuanData();
						} else {
							Toast.makeText(getActivity(), "账户已被冻结无法使用", Toast.LENGTH_SHORT).show();
						}
						return false;
					}
				});
			} else {
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

				getHuankuanData();
			}
		}





	}

	public void getHuankuanData() {
		String formatUrl = String.format(ServerInterface.BORROW_PROGRESS, userInfo.account.cellphone, userInfo.token);
		NoHttpRequest.getInstance().requestGet(getActivity(), formatUrl.hashCode(), formatUrl, BorrowResponse.class, new HttpListener<BorrowResponse>() {
			@Override
			public void onSucceed(int what, Response<BorrowResponse> response) {
				if (response.isSucceed() && response.get() != null) {
					Borrow borrow = response.get().result;
					if (null != borrow) {
						if (borrow.jieKuanZhuangTai == 2) {
							//立即还款
							com.studentloan.white.mode.activity.RefundActivity_.intent(getActivity()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
						} else if(borrow.jieKuanZhuangTai == 3 || borrow.jieKuanZhuangTai < 0){
							Toast.makeText(getActivity(), "没有借款", Toast.LENGTH_SHORT).show();
						}else if(borrow.jieKuanZhuangTai == 0 || borrow.jieKuanZhuangTai == 1){
							Toast.makeText(getActivity(), "等待放款", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}

			@Override
			public void onFailed(int what, Response<BorrowResponse> response) {

			}
		}, true);
	}
}

