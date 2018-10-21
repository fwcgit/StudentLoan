package com.qudaozhang.white.mode.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qudaozhang.white.R;
import com.qudaozhang.white.net.HttpListener;
import com.qudaozhang.white.net.ServerInterface;
import com.qudaozhang.white.net.data.BooleanResponse;
import com.qudaozhang.white.net.data.MoreInfo;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(!emailFormat(email)){
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

    public boolean emailFormat(String email)
    {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }
}
