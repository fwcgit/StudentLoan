package com.studentloan.white.mode.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.model.MxParam;
import com.moxie.client.model.TitleParams;
import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.IntegerResponse;
import com.studentloan.white.net.data.Shenghe;
import com.studentloan.white.net.data.ShengheResponse;
import com.studentloan.white.net.data.WhiteCollarInfo;
import com.studentloan.white.utils.DialogUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by fu on 2017/5/8.
 */

@EActivity(R.layout.activity_profession_info_layout)
public class ProfessionInfoActivity extends BaseActivity {
    String mBannerBgColor = "#ff26aa28"; //SDK里Banner的背景色
    String mBannerTxtColor= "#ffffff"; //SDK里Banner的字体颜色
    String mBannerTxtContent= "学信网认证"; //SDK里Banner的文字描述
    String mThemeColor= "#ff9500"; //SDK里页面主色调

    @ViewById
    public TextView workTypeTv,companCityTv,wangyinTv;
    @ViewById
    public ScrollView scrollView;
    @ViewById
    public EditText workTimeEt,wageDateEt,wageRangeEt;
    @ViewById
    public EditText companNameEt,companAddrEt,companPhoneEt;
    @ViewById
    public LinearLayout cityLayout;
    @ViewById
    public  View workTimeView,wageDateView,wageRangeView,yinlinAuthView;

    public int retryCount = 0;

    Handler handler = new Handler();

    private Dialog dialog;

    @AfterViews
    @Override
    public void initViews() {
        super.initViews();

        getUserInfo();

        setTitleText("职业信息");

        showView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        getShengheState();

    }

    private void showView(){
        if(null != userInfo){
            WhiteCollarInfo wc = userInfo.whiteCollar;

            if(userInfo.wangYinRenZhengJieGuo == 1){
                wangyinTv.setText("已完成");
            }

            if(null != wc){
                companNameEt.setText(wc.companyName);
                companCityTv.setText(wc.city);
                companAddrEt.setText(wc.companyAddr);
                companPhoneEt.setText(wc.phone);
                workTimeEt.setText(wc.workingHours);
                wageDateEt.setText(wc.payoffDate+"号");

                if(wc.salaryRange[0] == 6000){
                    wageRangeEt.setText("6000以上");
                }else if(wc.salaryRange[0] == 0){
                    wageRangeEt.setText("2000以下");
                }else{

                    wageRangeEt.setText(wc.salaryRange[0]+"-"+wc.salaryRange[1]);
                }

            }
        }
    }

    @Override
    public void rightTv(TextView tv) {
        tv.setVisibility(View.VISIBLE);
        tv.setText("保存");
        getUserInfo();
        if(userInfo.wangYinRenZhengJieGuo == 1){
            tv.setVisibility(View.GONE);
            workTimeEt.setEnabled(false);
            wageDateEt.setEnabled(false);
            wageRangeEt.setEnabled(false);
            companNameEt.setEnabled(false);
            companAddrEt.setEnabled(false);
            companPhoneEt.setEnabled(false);
            cityLayout.setEnabled(false);
            workTimeView.setEnabled(false);
            wageDateView.setEnabled(false);
            wageRangeView.setEnabled(false);
            yinlinAuthView.setEnabled(false);

        }
    }

    @Click
    public void cityLayout(){
        com.studentloan.white.mode.activity.SelectCityActivity_.intent(this).startForResult(SelectCityActivity.REQUEST_CODE);
    }

    @Click
    public void yinlinAuthView(){
        if(userInfo.wangYinRenZhengJieGuo == 1) return ;
        save(true);
    }

    @Override
    public void rightTvClick(TextView tv) {
        if(userInfo.wangYinRenZhengJieGuo == 1) return ;
        save(false);
    }

    @Click
    public void workTimeView(){
        DialogUtils.getInstance().showSelectList(this, new String[]{"一年以内","一到三年","三到五年","五年以上"}, new DialogCallback() {
            @Override
            public void typeStr(String type) {
                workTimeEt.setText(type);
            }
        });
    }


    @Click
    public void wageDateView(){

        String[] strs = new String[31];
        for(int i = 1;i <= 31;i++){
            strs[i-1] = i+"";
        }

        DialogUtils.getInstance().showSelectList(this, strs, new DialogCallback() {
            @Override
            public void typeStr(String type) {
                wageDateEt.setText(type+"号");
            }
        });
    }

    @Click
    public void wageRangeView(){

        DialogUtils.getInstance().showSelectList(this, new String[]{"2000以下","2000-4000","4000-6000","6000以上"}, new DialogCallback() {
            @Override
            public void typeStr(String type) {
                wageRangeEt.setText(type);
            }
        });
    }

    private void save(final boolean wangyin){
        String companName = companNameEt.getText().toString();
        String companCity = companCityTv.getText().toString();
        String companAddr = companAddrEt.getText().toString();
        String companPhone = companPhoneEt.getText().toString();
        String  workTime = workTimeEt.getText().toString();
        String  wageDate = wageDateEt.getText().toString();
        String wangeRange = wageRangeEt.getText().toString();

        if(TextUtils.isEmpty(companName)){
            showToast("请填写公司名");
            return;
        }

        if(TextUtils.isEmpty(companCity)){
            showToast("请选择城市");
            return;
        }

        if(TextUtils.isEmpty(companAddr)){
            showToast("请填写公司详细地址");
            return;
        }

        if(TextUtils.isEmpty(companPhone)){
            showToast("请填写首位电话");
            return;
        }

        if(TextUtils.isEmpty(workTime)){
            showToast("请选择工作时长");
            return;
        }

        if(TextUtils.isEmpty(wageDate)){
            showToast("请选择发薪日期");
            return;
        }

        if(TextUtils.isEmpty(wangeRange)){
            showToast("请选择工资范围");
            return;
        }


        final WhiteCollarInfo wc = new WhiteCollarInfo();
        wc.companyName = companName;
        wc.companyAddr = companAddr;
        wc.city = companCity;
        wc.workingHours = workTime;
        wc.payoffDate = Integer.valueOf(wageDate.substring(0,wageDate.indexOf("号")));

        wc.salaryRange = new int[2];
        if(wangeRange.equals("2000以下")){
            wc.salaryRange[0] = 0;
            wc.salaryRange[1] = 2000;
        }else if(wangeRange.equals("2000-4000")){
            wc.salaryRange[0] = 2000;
            wc.salaryRange[1] = 4000;
        }else if(wangeRange.equals("4000-6000")){
            wc.salaryRange[0] = 4000;
            wc.salaryRange[1] = 6000;
        }else if(wangeRange.equals("6000以上")){
            wc.salaryRange[0] = 6000;
            wc.salaryRange[1] = 0;
        }

        wc.phone = companPhone;

        String foramtUrl = String.format(ServerInterface.WHITE_COLLAR_INFO,userInfo.account.cellphone,userInfo.token);
        String bodyJson = new Gson().toJson(wc);

        requestPostBody(foramtUrl.hashCode(), null, bodyJson, foramtUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){
                        userInfo.whiteCollar = wc;
                        if(wangyin){
                            yinlinAuth();
                        }else{
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },true);
    }



    private void yinlinAuth(){

        if(userInfo.wangYinRenZhengJieGuo == 1){
            return;
        }else{
            if(userInfo.shengYuWangYinRenZhengCiShu <=0 ){
                showToast("网银认证次数以达上限！");
                return;
            }
        }

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
                .backgroundDrawable(R.drawable.bg_actionbar)
                .rightNormalImgResId(R.drawable.refresh)
                .immersedEnable(true)
                .build();

        mxParam.setTitleParams(titleParams);

        //手机号、身份信息预填
        mxParam.setFunction(MxParam.PARAM_FUNCTION_ONLINEBANK);
        mxParam.setLoginType(MxParam.PARAM_ITEM_TYPE_DEBITCARD);
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
                            Toast.makeText(ProfessionInfoActivity.this, "导入失败(平台方服务问题)", Toast.LENGTH_SHORT).show();
                            return true;
                        case MxParam.ResultCode.MOXIE_SERVER_ERROR:
                            Toast.makeText(ProfessionInfoActivity.this, "导入失败(魔蝎数据服务异常)", Toast.LENGTH_SHORT).show();
                            return true;
                        case MxParam.ResultCode.USER_INPUT_ERROR:
                            Toast.makeText(ProfessionInfoActivity.this, "导入失败(" + moxieCallBackData.getMessage() + ")", Toast.LENGTH_SHORT).show();
                            moxieContext.finish();
                            return true;
                        case MxParam.ResultCode.IMPORT_FAIL:
                            Toast.makeText(ProfessionInfoActivity.this, "导入失败", Toast.LENGTH_SHORT).show();
                            moxieContext.finish();
                            return true;
                        case MxParam.ResultCode.IMPORT_SUCCESS:
                            //根据taskType进行对应的处理
                            switch (moxieCallBackData.getTaskType()) {
                                case MxParam.PARAM_FUNCTION_EMAIL:
                                    Toast.makeText(ProfessionInfoActivity.this, "邮箱导入成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case MxParam.PARAM_FUNCTION_ONLINEBANK:

                                    retryCount = 0;

                                    showLoadingDialog();

                                    getShengheState();

                                    queryWangyin();

                                    break;
                                //.....
                                default:

                                    Toast.makeText(ProfessionInfoActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
                            }
                            moxieContext.finish();
                            return true;
                    }
                }
                return false;
            }
        });
    }

    public void queryWangyin(){
        String formatUrl = String.format(ServerInterface.QUERY_WANG_YIN,MyApplication.getInstance().userInfo.account.cellphone);

        requestGet(formatUrl.hashCode(), formatUrl, IntegerResponse.class, new HttpListener<IntegerResponse>() {
            @Override
            public void onSucceed(int what, Response<IntegerResponse> response) {
                if(response.isSucceed() && response.get() != null){

                    if(response.get().errorCode.equals("0")){

                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }

                        MyApplication.getInstance().userInfo.wangYinRenZhengJieGuo = 1;
                        MyApplication.getInstance().userInfo.shengYuWangYinRenZhengCiShu = response.get().result;

                        showView();

                        MyApplication.mainActivity.getShengheState();

                        finish();

                    }else{
                        int time = 0;

                        if(retryCount == 0){
                            time = 10 * 1000;
                        }else if(retryCount == 1){
                            time = 10 * 1000;
                        }else if(retryCount == 2){
                            time = 10 * 1000;
                        }else if(retryCount ==3){
                            time = 15 * 1000;
                        }

                        if(retryCount >= 3){
                            if(dialog != null && dialog.isShowing()){
                                dialog.dismiss();
                            }
                            return;
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queryWangyin();
                            }
                        },time);

                        retryCount++;
                    }
                }else{
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<IntegerResponse> response) {
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        },false);
    }

    private void showLoadingDialog(){
        dialog = new Dialog(this, R.style.custom_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == SelectCityActivity.REQUEST_CODE){
                companCityTv.setText(data.getStringExtra("name"));
                companCityTv.setTag(data.getStringExtra("code"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

                        showView();
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<ShengheResponse> response) {

            }
        },true);
    }
}
