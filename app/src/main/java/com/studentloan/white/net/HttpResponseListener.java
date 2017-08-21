/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.studentloan.white.net;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.studentloan.white.MyApplication;
import com.studentloan.white.R;
import com.studentloan.white.net.data.BaseResponse;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.net.ProtocolException;

/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author YOLANDA;
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

	private Dialog dialog;
	
    @SuppressWarnings("unused")
	private Request<?> mRequest;

    private Context ctx;
    
	private boolean isShowDialog;
	
    /**
     * 结果回调.
     */
    private HttpListener<T> callback;

    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener<T> httpCallback,Context ctx,boolean isShowDialog) {
        this.mRequest = request;
        this.callback = httpCallback;
        this.ctx = context;
        this.isShowDialog = isShowDialog;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
    	if(!isShowDialog) return;
    	showLoadingDialog();
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
    	if(!isShowDialog) return;
    	cancelDialog();
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null){
        	callback.onSucceed(what, response);
            if(response.isSucceed()){
            	if(response.get() != null && response.get() instanceof BaseResponse){
            		BaseResponse baseResponse = (BaseResponse) response.get();
                    if(baseResponse.prompt){
                        if(!baseResponse.errorCode.equals("0")){
                            showToast(baseResponse.errorMsg);
                        }

                        if(baseResponse.errorCode.equals("200") || baseResponse.errorCode.equals("300")){
                            MyApplication.getInstance().reLogin();
                        }
                    }
            	}
            }
        }
            
        

    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        if(!isShowDialog) return;
        cancelDialog();

        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好
            showToast( R.string.error_please_check_network);
        } else if (exception instanceof TimeoutError) {// 请求超时
            showToast(R.string.error_timeout);
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            showToast( R.string.error_not_found_server);
        } else if (exception instanceof URLError) {// URL是错的
            showToast( R.string.error_url_error);
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            showToast( R.string.error_not_found_cache);
        } else if (exception instanceof ProtocolException) {
            showToast( R.string.error_system_unsupport_method);
        } else if (exception instanceof ParseError) {
            showToast( R.string.error_parse_data_error);
        } else {
            showToast( R.string.error_unknow);
        }
        Logger.e("错误：" + exception.getMessage());
        if (callback != null)
            callback.onFailed(what, response);
    }

	private void showLoadingDialog(){

        if(ctx instanceof Activity){
            if(((Activity)ctx).isFinishing()) return;
        }

        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
		dialog = new Dialog(ctx, R.style.custom_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new OnKeyListener() {
			
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
	
	private void cancelDialog(){

        if(ctx instanceof Activity){
            if(((Activity)ctx).isFinishing()) return;
        }

        if ( dialog != null && dialog.isShowing()) {
                dialog.dismiss();
        }
	}
	
	private void showToast(int strReg){
		Toast.makeText(ctx, strReg, Toast.LENGTH_SHORT).show();
	}

    private void showToast(String str){
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }

}
