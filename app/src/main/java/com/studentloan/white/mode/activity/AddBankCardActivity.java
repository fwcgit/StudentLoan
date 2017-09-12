package com.studentloan.white.mode.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BankCard;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.IntegerResponse;
import com.studentloan.white.net.data.StringArrayResponse;
import com.studentloan.white.utils.DialogUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fu on 2017/6/29.
 */

@EActivity(R.layout.activity_add_bankcard_layout)
public class AddBankCardActivity extends BaseActivity {
    @ViewById
    public TextView bankCardNameTv;
    @ViewById
    public EditText bankCartEt,phoneEt,codeEt;

    @ViewById
    Button sendCodeBtn;
    private Timer timer;
    private int sendCodeTime = 60;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("添加银行卡");

        getUserInfo();
    }

    @Click
    public void bankCardLayout(){
        String formatUrl = String.format(ServerInterface.GET_BANKS_NAME,userInfo.account.cellphone);
        requestGet(formatUrl.hashCode(), formatUrl, StringArrayResponse.class, new HttpListener<StringArrayResponse>() {
            @Override
            public void onSucceed(int what, Response<StringArrayResponse> response) {
                if(response.isSucceed() && response.get()!=null){
                    if(response.get().result != null && response.get().result.length > 0){

                        DialogUtils.getInstance().showSelectList(AddBankCardActivity.this,
                                response.get().result, new DialogCallback() {
                                    @Override
                                    public void typeStr(String type) {
                                        bankCardNameTv.setText(type);
                                    }
                                });
                    }
                }
            }

            @Override
                public void onFailed(int what, Response<StringArrayResponse> response) {

            }
        },true);

    }

    @Click
    public void sendCodeBtn(){
        sendSmsCode();
    }

    @Override
    public void rightTv(TextView tv) {
        tv.setVisibility(View.VISIBLE);
        tv.setText("保存");
    }

    @Override
    public void rightTvClick(TextView tv) {
        addBankCard();
    }

    private void sendSmsCode(){

        String phone = phoneEt.getText().toString();

        if(TextUtils.isEmpty(phone)){
            showToast("请输入预留手机号");
            return;
        }

        if(!app.isTelPhone(phone)){
            showToast("请输入合法的手机号");
            return;
        }

        String formatUrl = String.format(ServerInterface.SMS_BIND_BANK_CARD,phone,userInfo.token);
        requestPost(formatUrl.hashCode(), null, formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){
                        startTimer();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },false);
    }

    private void addBankCard(){
        String bankCardName = bankCardNameTv.getText().toString();
        String bankCardNum = bankCartEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String code = codeEt.getText().toString();

        if(TextUtils.isEmpty(bankCardName)){
            showToast("请选择银行名称");
            return;
        }

        if(TextUtils.isEmpty(bankCardNum)){
            showToast("请输入银行卡号");
            return;
        }

        if(TextUtils.isEmpty(phone)){
            showToast("请输入预留手机号");
            return;
        }

        if(!app.isTelPhone(phone)){
            showToast("请输入合法的手机号");
            return;
        }

        if(TextUtils.isEmpty(code)){
            showToast("请输入验证码");
            return;
        }

        BankCard bc = new BankCard();
        bc.bankName = bankCardName;
        bc.bankCardNum = bankCardNum;
        bc.boundCellphone = phone;

        String bodyJson = new Gson().toJson(bc);

        String foramtUrl = String.format(ServerInterface.ADD_BANKCARD,userInfo.account.cellphone,code,userInfo.token);

        requestPostBody(foramtUrl.hashCode(),null,bodyJson,foramtUrl,IntegerResponse.class,new HttpListener<IntegerResponse>(){

            @Override
            public void onSucceed(int what, Response<IntegerResponse> response) {
                    if(response.isSucceed() && response.get() != null){
                        if(response.get().errorCode.equals("0")){
                            showToast("添加成功");
                            finish();
                        }
                    }
            }

            @Override
            public void onFailed(int what, Response<IntegerResponse> response) {

            }
        },true);

    }

    private void startTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        sendCodeBtn.setEnabled(false);
        sendCodeTime = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        sendCodeBtn.setText(sendCodeTime+"后重发");
                        sendCodeTime--;
                        if(sendCodeTime <= 0){
                            stopTimer();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopTimer(){
        sendCodeTime = 0;
        sendCodeBtn.setEnabled(true);
        sendCodeBtn.setText("发送验证码");
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
