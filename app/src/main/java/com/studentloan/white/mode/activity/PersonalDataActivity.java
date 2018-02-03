package com.studentloan.white.mode.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.model.MxParam;
import com.moxie.client.model.TitleParams;
import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
import com.studentloan.white.R;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.GetBankCardResponse;
import com.studentloan.white.net.data.Shenghe;
import com.studentloan.white.net.data.ShengheResponse;
import com.studentloan.white.utils.ContactsUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

/***
 * 个人资料
 * 
 * @author fu
 *
 */
@EActivity(R.layout.activity_personal_data_layout)
public class PersonalDataActivity extends BaseActivity implements ContactsUtils.IContactsCallBack {

	public static final int REQUEST_XUEXIN_CODE = 0x10001;
	public static final int REQUEST_SERVER_CODE = 0x10002;

	@ViewById
	TextView rightTv,xuexinComleteTv,contactsComleteTv,operatorComleteTv,moreComleteTv;

	private  ContactsUtils contactsUtils;
	private Dialog dialog;

	Handler handler = new Handler();

	int retryCount = 0;


	@Override
	@AfterViews
	public void initViews() {
		super.initViews();

		setTitleText("个人资料");

		getUserInfo();

		rightTv.setVisibility(View.VISIBLE);
		rightTv.setText("提交审核");

		contactsUtils = new ContactsUtils(this, this);

	}

	private void refreshView(){
		if(null !=userInfo.xueXinVeriTime){
			xuexinComleteTv.setText("已完善");
		}

		if(null != userInfo.emergencyContact){
			contactsComleteTv.setText("已完善");
		}

		int dayTime = 24 * 60 * 60 * 1000;

		if(null != userInfo.yunYingShangVeriTime && (userInfo.serverTime - userInfo.yunYingShangVeriTime)/dayTime <= 60 ){
			operatorComleteTv.setText("已完善");
		}

		if(null != userInfo.moreInfo){
			moreComleteTv.setText("已完善");
		}

		if(userInfo.verificationResult == 1){
			rightTv.setText("已通过");
			rightTv.setEnabled(false);
		}else{
			if(userInfo.submit == 1){
				rightTv.setText("审核中");
				rightTv.setEnabled(false);
			}else{
				rightTv.setText("提交审核");
				rightTv.setEnabled(true);
			}
		}

		MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if(msg.arg1 == 1){

					operatorComleteTv.setText("已完善");

				}
				return false;
			}
		});


	}
	
	@Override
	protected void onResume() {
		super.onResume();

		getShengheState();
		refreshView();
	}


	@Click
	public void persionLayout(){
		if(userInfo.submit == 1 || userInfo.verificationResult == 1) return;
		if(userInfo.identification != null) return;

		if(null == userInfo.xueXinVeriTime || userInfo.xueXinVeriTime <=0 ){
			showToast("请完善学信网信息");
			return;
		}

		if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
			showToast("你的个人信息已超最大认证次数.无法使用");
			return;
		}

		com.studentloan.white.mode.activity.PersonalInfoActivity_.intent(this).
					flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}

	@Click
	public void xuexinLayout(){
		xuexinAuth();
	}

	@Click
	public void urgencyContactsLayout(){

		if(null == userInfo.xueXinVeriTime || userInfo.xueXinVeriTime <=0 ){
			showToast("请完善学信网信息");
			return;
		}

		if(userInfo.identification == null){
			showToast("请先完善个人信息");
			return;
		}

		if(userInfo.submit == 1 || userInfo.verificationResult == 1) return;


		com.studentloan.white.mode.activity.AddContactsActivity_.intent(this).
					flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}

	@Click
	public void moreInfoLayout(){

		if(userInfo.identification == null){
			if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
				showToast("你的个人信息已超最大认证次数.无法使用");
				return;
			}
		}

		if(null == userInfo.xueXinVeriTime || userInfo.xueXinVeriTime <=0 ){
			showToast("请完善学信网信息");
			return;
		}

		if(null == userInfo.identification){
			showToast("请完成个人信息");
			return;
		}

		if(null == userInfo.whiteCollar){
			showToast("请完成紧急联系人信息");
			return;
		}

		int dayTime = 24 * 60 * 60 * 1000;

		if(null == userInfo.yunYingShangVeriTime || (userInfo.serverTime - userInfo.yunYingShangVeriTime)/dayTime > 60  ){
			showToast("请完善手机运营商信息");
			return;
		}

		com.studentloan.white.mode.activity.MoreInfoActivity_.intent(this).
					flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();

	}

	@Click
	public void operatorLayout(){

		if(userInfo.identification == null){
			if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
				showToast("你的个人信息已超最大认证次数.无法使用");
				return;
			}
		}

		int dayTime = 24 * 60 * 60 * 1000;

		if(null != userInfo.yunYingShangVeriTime && (userInfo.serverTime - userInfo.yunYingShangVeriTime)/dayTime < 60 ){
				return;
		}

		if(null == userInfo.xueXinVeriTime || userInfo.xueXinVeriTime <=0 ){
			showToast("请完善学信网信息");
			return;
		}

		if(null == userInfo.identification){
			showToast("请完成个人信息");
			return;
		}

		if(null == userInfo.whiteCollar){
			showToast("请完成紧急联系人信息");
			return;
		}


		MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if(msg.arg1 == 1){
					showToast("还有未完成的借款!");
				}else{
					if(msg.arg1 == 0){
						operatorAuth();
					}
				}
				return false;
			}
		});

	}


	@Override
	public void rightTvClick(TextView tv) {

		if(null == userInfo.xueXinVeriTime || userInfo.xueXinVeriTime <=0 ){
			showToast("请完善学信网信息");
			return;
		}


		if(null == userInfo.identification){
			showToast("请完善个人信息");
			return;
		}

		if(null == userInfo.emergencyContact){
			showToast("请完成紧急联系人信息");
			return;
		}

		int dayTime = 24 * 60 * 60 * 1000;

		if(null == userInfo.yunYingShangVeriTime || (userInfo.serverTime - userInfo.yunYingShangVeriTime)/dayTime > 60  ){
			showToast("请完善手机运营商信息");
			return;
		}

		if(null == userInfo.moreInfo){
			showToast("请完成更多信息");
			return;
		}

		getBankCardList();
	}
	/***
	 * 获取所有银行银行卡
	 */
	private void getBankCardList(){
		String formatUrl = String.format(ServerInterface.GET_BANK_CARD_LIST,userInfo.account.cellphone);
		requestGet(formatUrl.hashCode(), formatUrl, GetBankCardResponse.class, new HttpListener<GetBankCardResponse>() {
			@Override
			public void onSucceed(int what, Response<GetBankCardResponse> response) {

				if(response.isSucceed() && response.get() != null){
					if(response.get().result == null || response.get().result.isEmpty()) {
						showToast("请绑定银行卡！");
					}else{
						showLoadingDialog();
						contactsUtils.readContacts();
					}
				}
			}

			@Override
			public void onFailed(int what, Response<GetBankCardResponse> response) {

			}
		},false);
	}


	private void submitCheck(){
		String formatUrl = String.format(ServerInterface.SUBMIT,userInfo.account.cellphone,userInfo.token);
		requestPost(formatUrl.hashCode(), null, formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null){
					if(response.get().result){
						userInfo.submit = 1;
						rightTv.setText("审核中");
						rightTv.setEnabled(false);

						showToast("智能审核通过等待人工审核");
					}
				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}
		},true);
	}


	
	private void operatorAuth(){
		MxParam mxParam = new MxParam();
		mxParam.setUserId(userInfo.account.accountId+"");
		mxParam.setApiKey(MyContacts.mxApiKey);
		mxParam.setThemeColor("#26aa28");//主题色（非必传）
		//mxParam.setAgreementUrl(mainActivity.getSharedPreferValue("agreementUrl"));//自定义协议地址（非必传）
		mxParam.setAgreementEntryText("同意数据获取协议");    //自定义协议相关说明（非必传）
		mxParam.setCacheDisable(MxParam.PARAM_COMMON_YES);//不使用缓存（非必传）
		mxParam.setLoadingViewText("验证过程中不会浪费您任何流量\n请稍等片刻");  //设置导入过程中的自定义提示文案，为居中显示
		mxParam.setQuitDisable(true); //设置导入过程中，触发返回键或者点击actionbar的返回按钮的时候，不执行魔蝎的默认行为

		//设置title
		TitleParams titleParams = new TitleParams.Builder()
				//不设置此方法会默认使用魔蝎的icon
				//.leftNormalImgResId(R.drawable.ic_launcher)
				//用于设置selector，表示按下的效果，不设置默认使用leftNormalImgResId()设置的图片
				.leftPressedImgResId(R.drawable.moxie_client_banner_back_black)
				//.titleColor(0xff26aa28)
				.backgroundDrawable(R.drawable.bg_actionbar)
				.rightNormalImgResId(R.drawable.refresh)
				.immersedEnable(true)
				.build();

		mxParam.setTitleParams(titleParams);

		//手机号、身份信息预填
		HashMap<String, String> extendParam = new HashMap<String, String>();
		extendParam.put(MxParam.PARAM_CARRIER_IDCARD, userInfo.account.idCard); // 身份证
		extendParam.put(MxParam.PARAM_CARRIER_PHONE, userInfo.account.cellphone); // 手机号
		extendParam.put(MxParam.PARAM_CARRIER_NAME, userInfo.account.name); // 姓名
		//extendParam.put(MxParam.PARAM_CARRIER_PASSWORD, "123456"); // 密码
		extendParam.put(MxParam.PARAM_CARRIER_EDITABLE, MxParam.PARAM_COMMON_NO); // 是否允许用户修改以上信息
		mxParam.setExtendParams(extendParam);


		mxParam.setFunction(MxParam.PARAM_FUNCTION_CARRIER);
		MoxieSDK.getInstance().start(this, mxParam, new MoxieCallBack() {
			@Override
			public boolean callback(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
				if (moxieCallBackData != null) {
					switch (moxieCallBackData.getCode()) {
						/**
						 * 如果用户正在导入魔蝎SDK会出现这个情况，如需获取最终状态请轮询贵方后台接口
						 * 魔蝎后台会向贵方后台推送Task通知和Bill通知
						 * Task通知：登录成功/登录失败
						 * Bill通知：账单通知
						 */
						case MxParam.ResultCode.IMPORTING:
						case MxParam.ResultCode.IMPORT_UNSTART:
							//showDialog(moxieContext);
							moxieContext.finish();
							return true;
						case MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR:
							Toast.makeText(PersonalDataActivity.this, "导入失败(平台方服务问题)", Toast.LENGTH_SHORT).show();
							return true;
						case MxParam.ResultCode.MOXIE_SERVER_ERROR:
							Toast.makeText(PersonalDataActivity.this, "导入失败(魔蝎数据服务异常)", Toast.LENGTH_SHORT).show();
							return true;
						case MxParam.ResultCode.USER_INPUT_ERROR:
							Toast.makeText(PersonalDataActivity.this, "导入失败(" + moxieCallBackData.getMessage() + ")", Toast.LENGTH_SHORT).show();
							moxieContext.finish();
							return true;
						case MxParam.ResultCode.IMPORT_FAIL:
							Toast.makeText(PersonalDataActivity.this, "导入失败", Toast.LENGTH_SHORT).show();
							moxieContext.finish();
							return true;
						case MxParam.ResultCode.IMPORT_SUCCESS:
							//根据taskType进行对应的处理
							switch (moxieCallBackData.getTaskType()) {
								case MxParam.PARAM_FUNCTION_EMAIL:
									Toast.makeText(PersonalDataActivity.this, "邮箱导入成功", Toast.LENGTH_SHORT).show();
									break;
								case MxParam.PARAM_FUNCTION_ONLINEBANK:
									Toast.makeText(PersonalDataActivity.this, "网银导入成功", Toast.LENGTH_SHORT).show();
									break;
								case MxParam.PARAM_FUNCTION_CARRIER:
									showAuth();
									quearyOperatorAuth();
									Toast.makeText(PersonalDataActivity.this, "运营商认证成功", Toast.LENGTH_SHORT).show();
								//.....
								default:

							}
							moxieContext.finish();
							return true;
					}
				}
				return false;
			}
		});
	}

	private void xuexinAuth(){
		MxParam mxParam = new MxParam();
		mxParam.setUserId(userInfo.account.accountId+"");
		mxParam.setApiKey(MyContacts.mxApiKey);
		mxParam.setThemeColor("#26aa28");//主题色（非必传）
		//mxParam.setAgreementUrl(mainActivity.getSharedPreferValue("agreementUrl"));//自定义协议地址（非必传）
		mxParam.setAgreementEntryText("同意数据获取协议");    //自定义协议相关说明（非必传）
		mxParam.setCacheDisable(MxParam.PARAM_COMMON_YES);//不使用缓存（非必传）
		mxParam.setLoadingViewText("验证过程中不会浪费您任何流量\n请稍等片刻");  //设置导入过程中的自定义提示文案，为居中显示
		mxParam.setQuitDisable(true); //设置导入过程中，触发返回键或者点击actionbar的返回按钮的时候，不执行魔蝎的默认行为

		//设置title
		TitleParams titleParams = new TitleParams.Builder()
				//不设置此方法会默认使用魔蝎的icon
				//.leftNormalImgResId(R.drawable.ic_launcher)
				//用于设置selector，表示按下的效果，不设置默认使用leftNormalImgResId()设置的图片
				.leftPressedImgResId(R.drawable.moxie_client_banner_back_black)
				//.titleColor(0xff26aa28)
				.backgroundDrawable(R.drawable.bg_actionbar)
				.rightNormalImgResId(R.drawable.refresh)
				.immersedEnable(true)
				.build();

		mxParam.setTitleParams(titleParams);
		mxParam.setFunction(MxParam.PARAM_FUNCTION_CHSI);
		MoxieSDK.getInstance().start(this, mxParam, new MoxieCallBack() {
			@Override
			public boolean callback(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
				if (moxieCallBackData != null) {
					switch (moxieCallBackData.getCode()) {
						/**
						 * 如果用户正在导入魔蝎SDK会出现这个情况，如需获取最终状态请轮询贵方后台接口
						 * 魔蝎后台会向贵方后台推送Task通知和Bill通知
						 * Task通知：登录成功/登录失败
						 * Bill通知：账单通知
						 */
						case MxParam.ResultCode.IMPORTING:
						case MxParam.ResultCode.IMPORT_UNSTART:
							//showDialog(moxieContext);
							moxieContext.finish();
							return true;
						case MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR:
							Toast.makeText(PersonalDataActivity.this, "导入失败(平台方服务问题)", Toast.LENGTH_SHORT).show();
							return true;
						case MxParam.ResultCode.MOXIE_SERVER_ERROR:
							Toast.makeText(PersonalDataActivity.this, "导入失败(魔蝎数据服务异常)", Toast.LENGTH_SHORT).show();
							return true;
						case MxParam.ResultCode.USER_INPUT_ERROR:
							Toast.makeText(PersonalDataActivity.this, "导入失败(" + moxieCallBackData.getMessage() + ")", Toast.LENGTH_SHORT).show();
							moxieContext.finish();
							return true;
						case MxParam.ResultCode.IMPORT_FAIL:
							Toast.makeText(PersonalDataActivity.this, "导入失败", Toast.LENGTH_SHORT).show();
							moxieContext.finish();
							return true;
						case MxParam.ResultCode.IMPORT_SUCCESS:
							//根据taskType进行对应的处理
							switch (moxieCallBackData.getTaskType()) {
								case MxParam.PARAM_FUNCTION_EMAIL:
									Toast.makeText(PersonalDataActivity.this, "邮箱导入成功", Toast.LENGTH_SHORT).show();
									break;
								case MxParam.PARAM_FUNCTION_ONLINEBANK:
									Toast.makeText(PersonalDataActivity.this, "网银导入成功", Toast.LENGTH_SHORT).show();
									break;
								case MxParam.PARAM_FUNCTION_CARRIER:
									showAuth();
									quearyOperatorAuth();
									Toast.makeText(PersonalDataActivity.this, "运营商认证成功", Toast.LENGTH_SHORT).show();
									break;
								case MxParam.PARAM_FUNCTION_CHSI:
									showAuth();
									quearyXuexinAuth();
									Toast.makeText(PersonalDataActivity.this, "学信网认证成功", Toast.LENGTH_SHORT).show();
									break;
								default:

							}
							moxieContext.finish();
							return true;
					}
				}
				return false;
			}
		});
	}

	private void showLoadingDialog(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
		dialog = new Dialog(this, R.style.custom_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
		
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = MyApplication.getInstance().widthPixels;
		lp.height = MyApplication.getInstance().heightPixels;
		
		dialog.setContentView(R.layout.dialog_loading_layout);
		
		dialog.show();
	}


	private void quearyOperatorAuth(){
			String formatUrl = String.format(ServerInterface.PASS_YUNYINGSHANG,userInfo.account.cellphone);
			requestGet(formatUrl.hashCode(), formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
				@Override
				public void onSucceed(int what, Response<BooleanResponse> response) {
					if(response.isSucceed() && response.get() != null){
						if(response.get().result){
							if(null != dialog && dialog.isShowing()){
								dialog.dismiss();
								userInfo.yunYingShangVeriTime = System.currentTimeMillis();
								refreshView();
							}
						}else{

								int time = 0;

								if(retryCount == 0){
									time = 10 * 1000;
								}else if(retryCount == 1){
									time = 20 * 1000;
								}else if(retryCount == 2){
									time = 30 * 1000;
								}else if(retryCount ==3){
									time = 45 * 1000;
								}

								if(retryCount >= 4){
									return;
								}

								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										quearyOperatorAuth();
									}
								},time);

								retryCount++;

						}
					}
				}

				@Override
				public void onFailed(int what, Response<BooleanResponse> response) {

				}
			},false);
	}

	private void quearyXuexinAuth(){
		String formatUrl = String.format(ServerInterface.XUEXIN_AUTH,userInfo.account.cellphone);
		requestGet(formatUrl.hashCode(), formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null){
					if(response.get().result){
						if(null != dialog && dialog.isShowing()){
							dialog.dismiss();
							userInfo.xueXinVeriTime = System.currentTimeMillis();
							refreshView();
						}
					}else{

						int time = 0;

						if(retryCount == 0){
							time = 10 * 1000;
						}else if(retryCount == 1){
							time = 20 * 1000;
						}else if(retryCount == 2){
							time = 30 * 1000;
						}else if(retryCount ==3){
							time = 45 * 1000;
						}

						if(retryCount >= 4){
							return;
						}

						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								quearyXuexinAuth();
							}
						},time);

						retryCount++;

					}
				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}
		},false);
	}

	public void showAuth(){
		showLoadingDialog();
	}

	@Override
	public void handComplate() {


		if(null != dialog && dialog.isShowing()){
			dialog.dismiss();
		}

		contactsUtils.upContacts();

		submitCheck();

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

						refreshView();
					}

				}
			}

			@Override
			public void onFailed(int what, Response<ShengheResponse> response) {

			}
		},true);
	}
}
