package com.studentloan.white.mode.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studentloan.white.R;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BooleanResponse;
import com.studentloan.white.net.data.MoreInfo;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by fu on 2017/5/8.
 */
@EActivity(R.layout.activity_more_info_layout)
public class MoreInfoActivity extends BaseActivity {


    @ViewById
    public EditText weixinEt,emailEt,qqEt,alipayEt;

    @AfterViews
    @Override
    public void initViews() {
        super.initViews();
        getUserInfo();
        setTitleText("更多信息");

        if(null != userInfo){
            if(null != userInfo.moreInfo){
                weixinEt.setText(userInfo.moreInfo.weixin);
                emailEt.setText(userInfo.moreInfo.email);
                qqEt.setText(userInfo.moreInfo.qq);
                alipayEt.setText(userInfo.moreInfo.zhiFuBao);
            }
        }
    }

    @Override
    public void rightTv(TextView tv) {
        tv.setVisibility(View.VISIBLE);
        tv.setText("保存");
        getUserInfo();
        if(userInfo.moreInfo != null){
            tv.setVisibility(View.GONE);
            weixinEt.setEnabled(false);
            emailEt.setEnabled(false);
            qqEt.setEnabled(false);
            alipayEt.setEnabled(false);
        }

    }

    @Override
    public void rightTvClick(TextView tv) {
        String alipay =  alipayEt.getText().toString();
        String email = emailEt.getText().toString();
        String qq = qqEt.getText().toString();
        String weixin = weixinEt.getText().toString();

        if(TextUtils.isEmpty(alipay)){
            showToast("请输入支付宝账号");
            return;
        }

        if(TextUtils.isEmpty(email)){
            showToast("请输入常用邮箱");
            return;
        }

        if(!checkEmail(email)){
            showToast("邮箱格式不正确");
            return;
        }

        if(TextUtils.isEmpty(qq)){
            showToast("请输入qq账号");
            return;
        }


        if(TextUtils.isEmpty(weixin)){
            showToast("请输入微信账号");
            return;
        }

        final MoreInfo mi = new MoreInfo();
        mi.zhiFuBao = alipay;
        mi.email = email;
        mi.qq = qq;
        mi.weixin = weixin;

        String formarUrl = String.format(ServerInterface.MORE_INFO,userInfo.account.cellphone,userInfo.token);
        String bodyJson = new Gson().toJson(mi);

        requestPostBody(formarUrl.hashCode(), null, bodyJson, formarUrl, BooleanResponse.class, new HttpListener<BooleanResponse>() {
            @Override
            public void onSucceed(int what, Response<BooleanResponse> response) {
                if(response.isSucceed() && response.get() != null){
                    if(response.get().result){
                        userInfo.moreInfo = mi;
                        showToast("上传成功");
                        finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<BooleanResponse> response) {

            }
        },true);
    }

    public  boolean checkEmail(String email)
    {// 验证邮箱的正则表达式
        String format = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
        //p{Alpha}:内容是必选的，和字母字符[\p{Lower}\p{Upper}]等价。如：200896@163.com不是合法的。
        //w{2,15}: 2~15个[a-zA-Z_0-9]字符；w{}内容是必选的。 如：dyh@152.com是合法的。
        //[a-z0-9]{3,}：至少三个[a-z0-9]字符,[]内的是必选的；如：dyh200896@16.com是不合法的。
        //[.]:'.'号时必选的； 如：dyh200896@163com是不合法的。
        //p{Lower}{2,}小写字母，两个以上。如：dyh200896@163.c是不合法的。
        if (email.matches(format))
        {
            return true;// 邮箱名合法，返回true
        }
        else
        {
            return false;// 邮箱名不合法，返回false
        }
    }
}
