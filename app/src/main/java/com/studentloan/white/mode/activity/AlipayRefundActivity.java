package com.studentloan.white.mode.activity;

import android.content.Context;
import android.text.ClipboardManager;

import com.studentloan.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/**
 * Created by fu on 2017/7/31.
 */

@EActivity(R.layout.activity_alipay_layout)
public class AlipayRefundActivity extends BaseActivity {

    @AfterViews
    @Override
    public void initViews() {
        super.initViews();

        setTitleText("还款");

        getUserInfo();
    }

    @Click
    public void copyBtn(){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText("yingjika58@163.com");

        showToast("复制成功");
    }
}
