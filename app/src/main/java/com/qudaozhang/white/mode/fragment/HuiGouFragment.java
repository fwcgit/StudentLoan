package com.qudaozhang.white.mode.fragment;

import org.androidannotations.annotations.AfterViews;

public class HuiGouFragment extends BaseFramgent {
    @AfterViews
    public void initViews() {
        titleBar.setTitle("首页");
    }
}
