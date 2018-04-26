package com.studentloan.white.mode.activity;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.mode.data.AppSysInfo;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.DeviceInfo;
import com.studentloan.white.net.data.JieKuanFeiYong;
import com.studentloan.white.utils.DialogUtils;
import com.studentloan.white.utils.SystemOpt;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

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
    }

    @Click
    public void submitBtn(){
        final String price = priceEt.getText().toString();

        if(TextUtils.isEmpty(price)){
            showToast("请输入估值金额！");
            return;
        }

        float dzPrice = Float.valueOf(price) * 0.95f;

        JieKuanFeiYong feiYong = new JieKuanFeiYong();
        feiYong.jinE = Integer.valueOf(price);
        feiYong.feiYong = feiYong.jinE * 0.01f * 15f;
        feiYong.daoZhangJinE = (int) (feiYong.jinE - feiYong.feiYong);
        feiYong.tianShu = 15;
        feiYong.yingHuanJinE = feiYong.jinE * 0.95f;
        DialogUtils.getInstance().showLoanConfirm(ZuPinActivity.this, feiYong,new DialogCallback() {
            @Override
            public void confirm() {
                requestLoan(price);
            }
        });



    }

    private  void requestLoan(String price){
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.deviceName = appSysInfo.getDeviceName();
        deviceInfo.deviceID = appSysInfo.getDeviceId();
        deviceInfo.deviceType = appSysInfo.getPhoneType();
        deviceInfo.sysVersion = appSysInfo.getSystemVersion();
        deviceInfo.appName = "急借白卡";

        String formatUrl = String.format(ServerInterface.ZUPING,userInfo.account.cellphone,price,15);
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
}
