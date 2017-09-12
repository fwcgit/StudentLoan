package com.studentloan.white.utils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.studentloan.white.R;

public class TitleBar {
	private TextView titleTv;
	private View rootView;
	private TitleCallBack titleCallBack;
	
	public void initView(View view,boolean showBack){
		this.rootView = view;
		titleTv = (TextView) rootView.findViewById(R.id.titleTv);
		rootView.findViewById(R.id.backimg).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(titleCallBack != null){
					titleCallBack.backClick((ImageView) rootView.findViewById(R.id.backimg));
				}
				
			}
		});
		
		rootView.findViewById(R.id.rightimg).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(titleCallBack != null){
					titleCallBack.rightClick((ImageView) rootView.findViewById(R.id.rightimg));
				}
			}
		});

		rootView.findViewById(R.id.rightTv).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(titleCallBack != null){
					titleCallBack.rightTvClick((TextView) rootView.findViewById(R.id.rightTv));
				}
			}
		});
		
		if(showBack){
			rootView.findViewById(R.id.backimg).setVisibility(View.VISIBLE);
		}else{
			rootView.findViewById(R.id.backimg).setVisibility(View.GONE);
		}
		
	}
	
	public void setTitle(String title){
		titleTv.setText(title);
	}
	
	public void addTitleCallBack(TitleCallBack callBack){
		this.titleCallBack = callBack;
		if(this.titleCallBack != null){
			this.titleCallBack.rightImg((ImageView) rootView.findViewById(R.id.rightimg));
			this.titleCallBack.rightTv((TextView) rootView.findViewById(R.id.rightTv));
		}
	}
	public interface  TitleCallBack{
		void backClick(ImageView backImg);
		void rightClick(ImageView rightImg);
		void rightImg(ImageView rightImg);
		void rightTv(TextView tv);
		void rightTvClick(TextView tv);
	}
	
	public void showBack(boolean show){
		if(show){
			rootView.findViewById(R.id.backimg).setVisibility(View.VISIBLE);
		}else{
			rootView.findViewById(R.id.backimg).setVisibility(View.GONE);
		}
	}
}
