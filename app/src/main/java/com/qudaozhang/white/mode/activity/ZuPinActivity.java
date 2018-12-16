package com.qudaozhang.white.mode.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qudaozhang.white.MyApplication;
import com.qudaozhang.white.R;
import com.qudaozhang.white.interfaces.DialogCallback;
import com.qudaozhang.white.mode.data.AppSysInfo;
import com.qudaozhang.white.net.HttpListener;
import com.qudaozhang.white.net.ServerInterface;
import com.qudaozhang.white.net.data.BankCard;
import com.qudaozhang.white.net.data.BooleanResponse;
import com.qudaozhang.white.net.data.BorrowLimitsResponse;
import com.qudaozhang.white.net.data.DeviceInfo;
import com.qudaozhang.white.net.data.GetBankCardResponse;
import com.qudaozhang.white.net.data.JieKuanFeiYong;
import com.qudaozhang.white.net.data.StringResponse;
import com.qudaozhang.white.utils.DialogUtils;
import com.qudaozhang.white.utils.SystemOpt;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fu on 2018/4/24.
 */
@EActivity(R.layout.activity_zuping_layout)
public class ZuPinActivity extends BaseActivity {

    @ViewById
    TextView deviceNameTv,systemVersionTv,phoneTypeTv,imeiTv;

    @ViewById
    EditText priceEt;

    AppSysInfo appSysInfo = SystemOpt.getInstance().appSysInfo;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        getUserInfo();

        titleBar.setTitle("租赁");

        deviceNameTv.setText(appSysInfo.getDeviceName());
        systemVersionTv.setText(appSysInfo.getSystemVersion());
        phoneTypeTv.setText(appSysInfo.getPhoneType());
        imeiTv.setText(appSysInfo.getDeviceId());

        if(userInfo.submit == 1 && userInfo.verificationResult == 1) {
            getBorrowLimits();
        }

    }


    /***
     * 获取所有银行银行卡
     */
    private void getBankCardList(final String type){
        String formatUrl = String.format(ServerInterface.GET_BANK_CARD_LIST,userInfo.account.cellphone);
        requestGet(formatUrl.hashCode(), formatUrl, GetBankCardResponse.class, new HttpListener<GetBankCardResponse>() {
            @Override
            public void onSucceed(int what, Response<GetBankCardResponse> response) {

                if(response.isSucceed() && response.get() != null){
                    List<BankCard> result = response.get().result;
                    for (BankCard bc:result) {
                        if(bc.isPrimary > 0){
                            getPdf(type,bc);
                            break;
                        }
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<GetBankCardResponse> response) {

            }
        },true);
    }

    public void getPdf(final String type, BankCard bc){

        final String price = priceEt.getText().toString();

        String formatUrl = "http://sign.yuhuizichan.com:18981/LoanSign/getSignPdf";

        Map<String,String> params = new HashMap<>();
        params.put("type",type );
        params.put("name",userInfo.account.name );
        params.put("cellphone",userInfo.account.cellphone);
        params.put("idcard",userInfo.account.idCard );
        params.put("address",userInfo.identification.address);
        params.put("email",userInfo.moreInfo.email);
        params.put("bankName",bc.bankName );
        params.put("bankCard",bc.bankCardNum );
        params.put("jieKuanJinE",price );
        params.put("jieKuanRiQi",System.currentTimeMillis()+"");
        params.put("jieKuanTianShu","15");
        params.put("deviceId",appSysInfo.getDeviceId());
        params.put("deviceName",appSysInfo.getDeviceName());
        params.put("deviceType",appSysInfo.getPhoneType());

        requestPostUrl(formatUrl.hashCode(),params, "",formatUrl, StringResponse.class, new HttpListener<StringResponse>() {
            @Override
            public void onSucceed(int what, Response<StringResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(!TextUtils.isEmpty(response.get().data)){
                        com.qudaozhang.white.mode.activity.WebViewActivity_.intent(ZuPinActivity.this)
                                .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .title(type.equals("3") ? "手机买卖协议" : "手机租赁协议")
                                .local(false)
                                .html(response.get().data)
                                .start();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<StringResponse> response) {

            }
        },true);

    }

    @Click
    public void submitBtn(){
        final String price = priceEt.getText().toString();

        if(TextUtils.isEmpty(price)){
            showToast("请输入估值金额！");
            return;
        }

        //float dzPrice = Float.valueOf(price) * 0.95f;

        JieKuanFeiYong feiYong = new JieKuanFeiYong();
        feiYong.jinE = Integer.valueOf(price);
        feiYong.feiYong = feiYong.jinE * 0.01f * 15f;
        feiYong.daoZhangJinE = (int) (feiYong.jinE - feiYong.feiYong);
        feiYong.tianShu = 15;
        feiYong.yingHuanJinE = feiYong.jinE * 0.95f;
        DialogUtils.getInstance().showLoanConfirm(ZuPinActivity.this, feiYong,new DialogCallback() {
            @Override
            public void confirm() {
                checkPersonalInfo(price);
            }

            @Override
            public void xieyi() {
                DialogUtils.getInstance().showSelectHetong(ZuPinActivity.this, new DialogCallback() {
                    @Override
                    public void hetong(int type) {
                        getBankCardList((type+2)+"");
                    }
                });
            }
        });

    }


    public void getBorrowLimits(){
        String  formatUrl = String.format(ServerInterface.BORROW_LIMITS,userInfo.account.cellphone,userInfo.token);
        requestGet(formatUrl.hashCode(),formatUrl, BorrowLimitsResponse.class, new HttpListener<BorrowLimitsResponse>() {
            @Override
            public void onSucceed(int what, Response<BorrowLimitsResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    int ls[] = response.get().result;
                    priceEt.setText((ls[ls.length-1])+"");
                }
            }

            @Override
            public void onFailed(int what, Response<BorrowLimitsResponse> response) {

            }
        },true);
    }

    private  void requestLoan(String price){
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.deviceName = appSysInfo.getDeviceName();
        deviceInfo.deviceID = appSysInfo.getDeviceId();
        deviceInfo.deviceType = appSysInfo.getPhoneType();
        deviceInfo.sysVersion = appSysInfo.getSystemVersion();
        deviceInfo.appName = "趣到账";

        String formatUrl = String.format(ServerInterface.ZUPING,userInfo.account.cellphone,price,15,userInfo.token);
        requestPostBody(formatUrl.hashCode(), null,new Gson().toJson(deviceInfo) ,formatUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){
                        showToast("租赁成功");
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
    
    public void checkPersonalInfo(final String price){

        if(userInfo.submit != 1 || userInfo.verificationResult != 1) {
            Toast.makeText(this,"请完成个人信息认证！",Toast.LENGTH_SHORT).show();
            finish();
            skipPersionalInfo();
            return;
        }

        MyApplication.mainActivity.getHuankuan(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    Toast.makeText(ZuPinActivity.this, "还有未完成的租赁!", Toast.LENGTH_SHORT).show();
                }else{

                    if(userInfo.identification == null){
                        if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
                            Toast.makeText(ZuPinActivity.this,"你的个人信息已超最大认证次数.无法使用",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }

                    int dayTime = 24 * 60 * 60 * 1000;

                    if(null == userInfo.yunYingShangVeriTime || (userInfo.serverTime - userInfo.yunYingShangVeriTime)/dayTime > 60  ){
                        Toast.makeText(ZuPinActivity.this,"请完成手机运营商信息",Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    requestLoan(price);
                }
                return false;
            }
        });
    }

    private void skipPersionalInfo(){

        if(userInfo.submit != 1 || userInfo.verificationResult != 0){

            if(userInfo.identification == null){
                if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
                    Toast.makeText(this,"你的个人信息已超最大认证次数.无法使用",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(userInfo.wangYinRenZhengJieGuo != 1){
                if(userInfo.shengYuWangYinRenZhengCiShu <= 0){
                    Toast.makeText(this,"你的网银认证已超最大次数.无法使用",Toast.LENGTH_SHORT).show();

                    return;
                }
            }
        }

        //个人资料
        com.qudaozhang.white.mode.activity.PersonalDataActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }
}
