package com.studentloan.white.mode.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fuiou.pay.FyPay;
import com.fuiou.pay.FyPayCallBack;
import com.fuiou.pay.util.AppConfig;
import com.fuiou.pay.util.MD5UtilString;
import com.studentloan.white.MyApplication;
import com.studentloan.white.MyContacts;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.DialogCallback;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.NoHttpRequest;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BankCard;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.Borrow;
import com.studentloan.white.net.data.BorrowResponse;
import com.studentloan.white.net.data.GetBankCardResponse;
import com.studentloan.white.net.data.StringResponse;
import com.studentloan.white.utils.ConvertUtils;
import com.studentloan.white.utils.DialogUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fu on 2018/4/25.
 */

@EActivity(R.layout.activity_huigou_layout)
public class HuiGouActivity extends BaseActivity {

    private Borrow br;

    @ViewById
    public TextView jkPriceTv,jkTimeTv,repartDatTv,yuqiDaysTv,xuzuTv;

    @ViewById
    public TextView payChannelFeeTv,totalPayFeeTv;

    @ViewById
    public View mainLayout;

    private SimpleDateFormat dateFormat = null;

    @ViewById
    public Button xuZuBtn;

    boolean isXuzu = false;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        getUserInfo();

        setTitleText("立即回购");

        FyPay.setDev(true);//此代码是配置jar包为环境配置，true是生产   false测试
        FyPay.init(this);

        mainLayout.setVisibility(View.GONE);

        if(userInfo.submit == 1 && userInfo.verificationResult != 1){
			Toast.makeText(this,"审核中！",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(HuiGouActivity.this, "账户已被冻结无法使用", Toast.LENGTH_SHORT).show();
                        }
                        return false;

                    }
                });
            } else {
                if(userInfo.identification == null){
                    if(userInfo.shengYuShenFenRenZhengCiShu <= 0 ){
                        Toast.makeText(HuiGouActivity.this,"你的个人信息已超最大认证次数.无法使用",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                getHuankuanData();
            }
        }


    }

    public void getHuankuanData() {
        String formatUrl = String.format(ServerInterface.BORROW_PROGRESS, userInfo.account.cellphone, userInfo.token);
        NoHttpRequest.getInstance().requestGet(HuiGouActivity.this, formatUrl.hashCode(), formatUrl, BorrowResponse.class, new HttpListener<BorrowResponse>() {
            @Override
            public void onSucceed(int what, Response<BorrowResponse> response) {
                if (response.isSucceed() && response.get() != null) {
                    Borrow borrow = response.get().result;
                    if (null != borrow) {
                        if (borrow.jieKuanZhuangTai == 2) {
                            getLoanInfo();
                        } else if(borrow.jieKuanZhuangTai == 3 || borrow.jieKuanZhuangTai < 0){
                            Toast.makeText(HuiGouActivity.this, "没有租赁", Toast.LENGTH_SHORT).show();
                        }else if(borrow.jieKuanZhuangTai == 0 || borrow.jieKuanZhuangTai == 1){
                            Toast.makeText(HuiGouActivity.this, "等待放款", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BorrowResponse> response) {

            }
        }, true);
    }

    @Click
    public void xuZuBtn(){
        isXuzu = true;
        getBankCardList();
    }
    @Click
    public void payBtn(){
        isXuzu = false;
        getBankCardList();

    }

    @Click
    public void layout2(){
        DialogUtils.getInstance().showSelectHetong(this, new DialogCallback() {
            @Override
            public void hetong(int type) {
                getPdf(type+"",userInfo.account.idCard);
            }
        });
    }

    @Click
    public void alipayTv(){

        com.studentloan.white.mode.activity.AlipayRefundActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }


    public void getPdf(String type,String idcard){

        String formatUrl = "http://sign.yuhuizichan.com:18981/LoanSign/getSignPdf";

        Map<String,String> params = new HashMap<>();
        params.put("type",type );
        params.put("idcard",idcard);

        requestPostUrl(formatUrl.hashCode(),params, null,formatUrl,StringResponse.class, new HttpListener<StringResponse>() {
            @Override
            public void onSucceed(int what, Response<StringResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().errorCode.equals("0")){
                        if(!TextUtils.isEmpty(response.get().data)){
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(response.get().data);
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    }else{
                        showToast(response.get().msg);
                    }

                }
            }

            @Override
            public void onFailed(int what, Response<StringResponse> response) {

            }
        },true);
    }

    public void getLoanInfo(){
        String formatUrl = String.format(ServerInterface.BORROW_PROGRESS,userInfo.account.cellphone,userInfo.token);
        requestGet(formatUrl.hashCode(), formatUrl, BorrowResponse.class, new HttpListener<BorrowResponse>() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onSucceed(int what, Response<BorrowResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result != null){
                        br = response.get().result;

                        mainLayout.setVisibility(View.VISIBLE);

                        jkPriceTv.setText(br.yingHuanKuanJinE+"");
                        jkTimeTv.setText(br.jieKuanTianShu+"天");
                        repartDatTv.setText(dateFormat.format(new Date(br.huanKuanDeadline))+"日");
                        yuqiDaysTv.setText(br.overdueDays+"天");
                        xuzuTv.setText((br.xuZu*8)+"天");
                        payChannelFeeTv.setText(ConvertUtils.float2String(br.shouXuFei)+"元/笔");
                        totalPayFeeTv.setText(String.format("%.2f",br.zongZhiFuFeiYong));
                        if(br.xuZu < 2){
                            xuZuBtn.setVisibility(View.VISIBLE);
                        }else{
                            xuZuBtn.setEnabled(false);
                        }

                    }else{
                        Toast.makeText(HuiGouActivity.this, "没有租赁", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(HuiGouActivity.this, "没有租赁", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailed(int what, Response<BorrowResponse> response) {

            }
        },true);
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
                    List<BankCard> result = response.get().result;
                    DialogUtils.getInstance().selectBankCard(HuiGouActivity.this, result, new DialogCallback() {
                        @Override
                        public void typeStr(String type) {
                            if(TextUtils.isEmpty(type)){
                                showToast("请选择银行卡!");
                                return;
                            }
                            BankCard bc = new BankCard();
                            bc.bankName = type.split(",")[0];
                            bc.bankCardNum = type.split(",")[1];
                            if(isXuzu){
                                getXUZHUNumber(bc);
                            }else{
                                getOrderNumber(bc);
                            }

                        }
                    });

                }
            }

            @Override
            public void onFailed(int what, Response<GetBankCardResponse> response) {

            }
        },true);
    }

    private void getXUZHUNumber(final BankCard bankCard){
        String formatUrl = String.format(ServerInterface.XUZHU_LIUSHUI,br.borrowId,userInfo.token);
        requestPost(formatUrl.hashCode(),null, formatUrl, StringResponse.class, new HttpListener<StringResponse>() {
            @Override
            public void onSucceed(int what, Response<StringResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(!TextUtils.isEmpty(response.get().result)){
                        pay(bankCard,response.get().result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<StringResponse> response) {

            }
        },true);
    }
    private void getOrderNumber(final BankCard bankCard){
        String formatUrl = String.format(ServerInterface.GET_ORDER_NUMBER,userInfo.account.cellphone);
        requestGet(formatUrl.hashCode(), formatUrl, StringResponse.class, new HttpListener<StringResponse>() {
            @Override
            public void onSucceed(int what, Response<StringResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(!TextUtils.isEmpty(response.get().result)){
                        pay(bankCard,response.get().result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<StringResponse> response) {

            }
        },true);
    }

    private void pay(BankCard bankCard, String mchntOrdId){

        int jl = userInfo.jiangLiJinE;

        if(userInfo.jiangLiJinE > 300){
            jl = 300;
        }
        final int jlNoti = jl;

        String mchnt_key = "fh46fyu0lezippityvtrrdwbv4cus33v";
        String mchnt_cd = "0002900F0395202";

        String amount = ((int)((br.zongZhiFuFeiYong-jl) * 100d))+"";

        if(isXuzu){
            amount = ((int)((br.jieKuanJinE*0.01*8f-jl) * 100d)+400)+"";
        }

        //String mchntOrdId = userInfo.account.accountId + "_"+(System.currentTimeMillis() / 1000);
        String Sing = MD5UtilString.MD5Encode("02" + "|" + "2.0" + "|"
                + mchnt_cd + "|"
                + mchntOrdId+ "|"
                + userInfo.account.accountId+ "|"
                + amount + "|"
                + bankCard.bankCardNum + "|" + (MyContacts.BASE_URL+userInfo.backUrl) + "|"
                + userInfo.identification.name + "|"
                + userInfo.identification.idCard + "|"
                + "0|" +mchnt_key);
        Bundle bundle =new Bundle();
        bundle.putString(AppConfig.MCHNT_CD, mchnt_cd);
        bundle.putString(AppConfig.MCHNT_AMT, amount);
        bundle.putString(AppConfig.MCHNT_BACK_URL, (MyContacts.BASE_URL+userInfo.backUrl));
        bundle.putString(AppConfig.MCHNT_BANK_NUMBER, bankCard.bankCardNum);
        bundle.putString(AppConfig.MCHNT_ORDER_ID, mchntOrdId);
        bundle.putString(AppConfig.MCHNT_USER_IDCARD_TYPE,"0");
        bundle.putString(AppConfig.MCHNT_USER_ID,userInfo.account.accountId+"");
        bundle.putString(AppConfig.MCHNT_USER_IDNU,userInfo.identification.idCard);
        bundle.putString(AppConfig.MCHNT_USER_NAME,userInfo.identification.name);
        bundle.putString(AppConfig.MCHNT_SING_KEY,Sing);
        bundle.putString(AppConfig.MCHNT_SDK_SIGNTP,"MD5");
        bundle.putString(AppConfig.MCHNT_SDK_TYPE,"02");
        bundle.putString(AppConfig.MCHNT_SDK_VERSION,"2.0");

        int i= FyPay.pay(this, bundle, new FyPayCallBack() {

            @Override
            public void onPayComplete(String arg0, String arg1, Bundle arg2) {

            }

            @Override
            public void onPayBackMessage(String msg) {
                // TODO Auto-generated method stub
                Log.e("fuiou", "----------extraData:"+msg.toString());

                String ser = "<RESPONSECODE>";
                int start = msg.indexOf(ser);

                if(start < 0) return;

                String code = msg.substring(start+ser.length(), start+ser.length()+4);

                if(code.equals("0000")){

                    notifationJiangliE(jlNoti);
                    showToast("支付成功");

                    EventBus.getDefault().post(new String("success"));

                    finish();
                }else{
                    showToast("支付失败");
                }
            }
        });

    }

    private void notifationJiangliE(final int jl){
        String formatUrl = String.format(ServerInterface.NOTIFATION_JIAANGLIJIN_E,
                userInfo.account.cellphone,br.borrowId,jl,userInfo.token);
        requestPut(formatUrl.hashCode(), null,formatUrl,BooleanResponse.class,new HttpListener<BooleanResponse>(){
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    userInfo.jiangLiJinE = userInfo.jiangLiJinE - jl;
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },false);
    }
}
