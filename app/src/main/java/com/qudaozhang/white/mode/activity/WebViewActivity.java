package com.qudaozhang.white.mode.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qudaozhang.white.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
@EActivity(R.layout.activity_webview_layout)
public class WebViewActivity extends BaseActivity {

	@ViewById
	WebView webView;
	
	@Extra
	String title,url,html;
	
	@Extra
	String fromDate,toDate,dayNum;
	
	@Extra
	int lv = 300;
	
	@Extra
	boolean callJS = false;
	
	@Extra
	boolean local = true;
	
	@AfterViews
	public void initView() {
		super.initViews();
		
		String title = getIntent().getStringExtra("title");
		String url = getIntent().getStringExtra("url");
		setTitleText(title);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebClient());
		webView.addJavascriptInterface(new JavaScriptInterface(), "load");


		if(local){
			webView.loadUrl("file:///android_asset/"+url);
		}else{
			if(!TextUtils.isEmpty(url)){
				webView.loadUrl(url);
			}else if(!TextUtils.isEmpty(html)){
				webView.loadData(html, "text/html; charset=UTF-8", null);//这种写法可以正确解码
			}

		}
		
	}
	
	public class JavaScriptInterface{
		
	}
	
	public class WebClient extends WebViewClient{
	   @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            
            if(callJS){
            	
//            	String setNameUrl = String.format("javascript:setName('%s')", "乙方（借款方）:"+app.personalInfo.nickname);
//            	webView.loadUrl(setNameUrl);
//
//            	String setIdentyUrl = String.format("javascript:setIdenty('%s')", "身份证号:"+app.personalInfo.cardid);
//            	webView.loadUrl(setIdentyUrl);
//
//            	String setPhoneUrl = String.format("javascript:setPhone('%s')", "乙方手机号码:"+app.personalInfo.phone);
//            	webView.loadUrl(setPhoneUrl);
//
//            	String loadTime = String.format("自<u>%s</u>年<u>%s</u>月<u>%s</u>日至<u>%s</u>年<u>%s</u>月<u>%s</u>日，共计<u>%s</u>天，每天按借款本金%s收取利息。",
//            			fromDate.split("-")[0],
//            			fromDate.split("-")[1],
//            			fromDate.split("-")[2],
//            			toDate.split("-")[0],
//            			toDate.split("-")[1],
//            			toDate.split("-")[2],
//            			dayNum,"0.03%"
//            			);
//            	String loadTimeUrl = String.format("javascript:setLoanTime('%s')", loadTime);
//            	webView.loadUrl(loadTimeUrl);
            	
//            	String signDateUrl = String.format("javascript:setSignDate('%s')", "日期:" + fromDate);
//            	webView.loadUrl(signDateUrl);
//            	String signDate1Url = String.format("javascript:setSignDate1('%s')", "日期:" + fromDate);
//            	webView.loadUrl(signDate1Url);
//            	String signDat2Url = String.format("javascript:setSignDate2('%s')", "日期:" + fromDate);
//            	webView.loadUrl(signDat2Url);
//            	
//            	String signName = String.format("javascript:setSignName('%s')", "乙方<span style = \"color:red\">(签字）：</span>"+app.personalInfo.nickname);
//            	webView.loadUrl(signName);
            	
            	String selectUrl = "";
            	if(lv == 600 ){
            		selectUrl = String.format("javascript:selectImg('%s','%s')", "type1","check_select.png");
            	}else if(lv == 800){
            		selectUrl = String.format("javascript:selectImg('%s','%s')", "type2","check_select.png");
            	}else if(lv == 1000 ){
            		selectUrl = String.format("javascript:selectImg('%s','%s')", "type3","check_select.png");
            	}else if(lv == 1200){
            		selectUrl = String.format("javascript:selectImg('%s','%s')", "type4","check_select.png");
            	}
            	
            	webView.loadUrl(selectUrl);
            }
        }
	}
}
