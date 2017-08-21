package com.studentloan.white.mode.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.studentloan.white.MyApplication;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.mode.view.ScaleTextView;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.BorrowLimitsResponse;
import com.studentloan.white.net.data.JieKuanFeiYongResponse;
import com.studentloan.white.utils.ContactsUtils;
import com.studentloan.white.utils.DialogUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
@EActivity(R.layout.activity_feerate_query_layout)
public class BorrowMoneyActivity extends BaseActivity implements ContactsUtils.IContactsCallBack{
	public static final int PERMISSION_CONTACTS_REQUEST_CODE = 0x221;

	@ViewById
	public SeekBar daySeekbar;

	@ViewById
	public ScaleTextView scaleTextView;

	@ViewById
	public TextView amountTv,feiyongTv,arrivalAmountTv;

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	long day = 6 * 24 * 60 * 60 * 1000;

	private String[] limits = new String[]{"600","700","800","900","1000"};

	@Extra
	String title;
	
	int  costPrice;

	int enablePrice = 0;


	ContactsUtils contactsUtils;
	private Dialog dialog;

	@TargetApi(23)
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();

		getUserInfo();

		setTitleText(title);
		
		contactsUtils = new ContactsUtils(this, this);
		
		Date date = new Date();
		date.setTime(date.getTime()+day);

		daySeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				if(progress > enablePrice){
					seekBar.setProgress(enablePrice);
					return;
				}

				if(null != limits && progress < limits.length){

					costPrice = Integer.valueOf(limits[progress]);
					amountTv.setText(limits[progress]+"元");

					calcFeiyong(costPrice,15);
				}


			}
		});


		getBorrowLimits();
		
	}

	@Click
	public void submitBtn(){
		showLoadingDialog();
		contactsUtils.readContacts();
	}

	@Click
	public void serviceAmountImg(){
		DialogUtils.getInstance().showServiceAmount(this,costPrice,15);
	}

	private void showBorrowLimits(int[] lts){

		if(null == lts || lts.length <=0 ){finish(); return;}

		enablePrice = lts.length-1;

		if(lts.length <= limits.length ){
			for (int i  = 0 ;i < lts.length;i++){
				limits[i] = lts[i]+"";
			}
		}else if(lts.length > limits.length){
			limits = new String[lts.length];
			for (int i  = 0 ;i < lts.length;i++){
				limits[i] = lts[i]+"";
			}
		}


		daySeekbar.setMax(limits.length-1);
		daySeekbar.setProgress(lts.length-1);
		scaleTextView.setData(limits.length-1,limits);
	}

	private void calcFeiyong(int price,int day){

		int xinxi = 20;
		int minjian = 20;
		int pintai = 20;

		if(price == 600){
			minjian=20;
			pintai= 20;
		}else if(price == 700){
			minjian=25;
			pintai= 25;
		}else if(price == 800){
			minjian=30;
			pintai= 30;
		}else if(price == 900){
			minjian=35;
			pintai= 35;
		}else if(price == 1000){
			minjian=40;
			pintai= 40;
		}

		float feiyong =0f;
		feiyong = xinxi+minjian+pintai+feiyong;

		feiyongTv.setText(String.format("%.2f",feiyong)+"元");

		arrivalAmountTv.setText(String.format("%.2f",price - feiyong)+"元");


	}

	public void getBorrowLimits(){
		String  formatUrl = String.format(ServerInterface.BORROW_LIMITS,userInfo.account.cellphone,userInfo.token);
		requestGet(formatUrl.hashCode(),formatUrl, BorrowLimitsResponse.class, new HttpListener<BorrowLimitsResponse>() {
			@Override
			public void onSucceed(int what, Response<BorrowLimitsResponse> response) {
					if(response.isSucceed() && response.get() != null){
						showBorrowLimits(response.get().result);
					}else{
						finish();
					}
			}

			@Override
			public void onFailed(int what, Response<BorrowLimitsResponse> response) {
				finish();
			}
		},true);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void handComplate() {


		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}

		contactsUtils.upContacts();

		getBorrowMoneyInfo();
	}

	/***
	 * 申请借款
	 */
	private void requestLoan(){
		String formatUrl = String.format(ServerInterface.REQUEST_LOAN,userInfo.account.cellphone,costPrice,15,userInfo.token);
		requestPost(formatUrl.hashCode(), null, formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
			@Override
			public void onSucceed(int what, Response<BooleanResponse> response) {
				if(response.isSucceed() && response.get() != null){
					if(response.get().result){
						showToast("借款成功");
						finish();
					}else{

					}
				}
			}

			@Override
			public void onFailed(int what, Response<BooleanResponse> response) {

			}
		},true);
	}

	private void showLoadingDialog(){
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
	
	public SpannableString getTextSpan(String str,boolean select){
		SpannableString span = new SpannableString(str);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xff000000);
		if(select){
			colorSpan = new ForegroundColorSpan(0xffffffff);
		}
		span.setSpan(colorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return span;
	}

	private void getBorrowMoneyInfo(){
		String formatUrl = String.format(ServerInterface.BORROW_MONEY_INFO,userInfo.account.cellphone,costPrice,15);
		requestGet(formatUrl.hashCode(), formatUrl, JieKuanFeiYongResponse.class, new HttpListener<JieKuanFeiYongResponse>() {
			@Override
			public void onSucceed(int what, Response<JieKuanFeiYongResponse> response) {

				if(response.isSucceed() && response.get() != null){
					if(response.get().result != null){
						DialogUtils.getInstance().showLoanConfirm(BorrowMoneyActivity.this, response.get().result,new DialogCallback() {
							@Override
							public void confirm() {
								requestLoan();
							}
						});
					}
				}


			}

			@Override
			public void onFailed(int what, Response<JieKuanFeiYongResponse> response) {

			}
		},true);
	}
}

