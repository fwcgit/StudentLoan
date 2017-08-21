package com.studentloan.white.mode.activity;

import android.widget.ListView;

import com.studentloan.white.MyApplication;
import com.studentloan.white.R;
import com.studentloan.white.interfaces.OnRefreshFooterListener;
import com.studentloan.white.interfaces.OnRefreshHeadListener;
import com.studentloan.white.mode.adapter.MsgAdapter;
import com.studentloan.white.mode.view.PullUpView;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.MessageResponse;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_msg_layout)
public class MsgActivity extends BaseActivity implements OnRefreshFooterListener,OnRefreshHeadListener {
	@ViewById
	public ListView listView;
	@ViewById
	public PullUpView sv;
	int page = 0;
	int tempPage = 0 ;
	int row = 10;
	MsgAdapter adapter;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();

		MyApplication.isPushMsg = false;
		
		setTitleText("消息");
		getUserInfo();

		sv.setModelName(getClass().getName());
		sv.setFootView(true);
		sv.setOnRefreshFooterListener(this);
		sv.setOnRefreshHeadListener(this);
		listView.setAdapter(adapter = new MsgAdapter(this));
		sv.startDownRefresh();
		
	}
	
	
	private void getMsg(){
		String formatUrl = String.format(ServerInterface.GET_MSG,userInfo.account.cellphone,page,10);
		requestGet(formatUrl.hashCode(), formatUrl, MessageResponse.class, new HttpListener<MessageResponse>() {
			@Override
			public void onSucceed(int what, Response<MessageResponse> response) {
				sv.pullShow();
				if(response.isSucceed() && response.get() != null){
					if(response.get().result != null){
						if(null != adapter){
							adapter.setMsgList(response.get().result,page == 0);
						}
					}
				}else{
					page = tempPage;
				}

			}

			@Override
			public void onFailed(int what, Response<MessageResponse> response) {
					sv.pullShow();
					page = tempPage;
			}
		},false);
	}


	@Override
	public void headRefresh() {
		page = 0;
		tempPage = page;
		getMsg();
	}


	@Override
	public void refreshFooter() {
		tempPage = page;
		++page;
		getMsg();
	}


}
