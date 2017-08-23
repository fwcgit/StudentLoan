package com.studentloan.white.mode.activity;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.studentloan.white.R;
import com.studentloan.white.interfaces.OnRefreshFooterListener;
import com.studentloan.white.interfaces.OnRefreshHeadListener;
import com.studentloan.white.mode.adapter.HistoryNotesAdapter;
import com.studentloan.white.mode.view.PullUpView;
import com.studentloan.white.net.HttpListener;
import com.studentloan.white.net.ServerInterface;
import com.studentloan.white.net.data.BorrowArrayResponse;
import com.studentloan.white.utils.ConvertUtils;
import com.yolanda.nohttp.rest.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/***
 * 历史记录
 * @author fu
 *
 */
@EActivity(R.layout.activity_history_notes_layout)
public class HistoryNotesActivity extends BaseActivity implements OnRefreshFooterListener,OnRefreshHeadListener {
	@ViewById
	public RecyclerView recyclerView;
	@ViewById
	public PullUpView sv;
	
	int page = 0;
	int tempPage = 0 ;
	int row = 10;
	HistoryNotesAdapter adapter;
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		setTitleText("借款记录");

		getUserInfo();

		EventBus.getDefault().register(this);

		sv.setModelName(getClass().getName());
		sv.setFootView(true);
		sv.setOnRefreshFooterListener(this);
		sv.setOnRefreshHeadListener(this);
		sv.startDownRefresh();

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.addItemDecoration(new ItemSpaceDecoration());
		recyclerView.setAdapter(adapter = new HistoryNotesAdapter(this));


//		listView.setAdapter(adapter = new HistoryNotesAdapter(this));
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				Borrow borrow = (Borrow) adapter.getItem(i);
//				if(borrow.jieKuanZhuangTai == 2){
//					com.studentloan.white.mode.activity.RefundActivity_.intent(HistoryNotesActivity.this).
//							flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
//				}
//			}
//		});
	}

	class ItemSpaceDecoration extends RecyclerView.ItemDecoration{
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			super.getItemOffsets(outRect, view, parent, state);

			if(recyclerView.getChildPosition(view) == 0){

				outRect.top = ConvertUtils.dip2px(HistoryNotesActivity.this,12f);
				outRect.bottom = ConvertUtils.dip2px(HistoryNotesActivity.this,12f);

			}else{

				outRect.bottom = ConvertUtils.dip2px(HistoryNotesActivity.this,12f);

			}

		}
	}

	@Override
	public void headRefresh() {
		page = 0;
		tempPage = page;
		getData();
	}


	@Override
	public void refreshFooter() {
		tempPage = page;
		++page;
		getData();
	}
	
	
	private void getData(){
		String formatUrl = String.format(ServerInterface.QUERY_BORROWS,userInfo.account.cellphone,page,10);
		requestGet(formatUrl.hashCode(), formatUrl, BorrowArrayResponse.class, new HttpListener<BorrowArrayResponse>() {
			@Override
			public void onSucceed(int what, Response<BorrowArrayResponse> response) {
				sv.pullShow();
				if(response.isSucceed() && response.get() != null){
					if(adapter != null &&  response.get().result != null && !response.get().result.isEmpty()){
						adapter.setHistoryList(response.get().result,page == 0);
					}
				}else{
					page = tempPage;
				}
			}

			@Override
			public void onFailed(int what, Response<BorrowArrayResponse> response) {
				page = tempPage;
				sv.pullShow();
			}
		},false);
	}

	@Subscribe
	public void onEvent(String tag){
		sv.startDownRefresh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		EventBus.getDefault().unregister(this);
	}
}
