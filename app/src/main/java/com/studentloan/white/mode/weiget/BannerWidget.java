package com.studentloan.white.mode.weiget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.studentloan.white.R;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class BannerWidget extends FrameLayout {
	Context mContext;
	Timer timer;
	int bannerPagerCount;
	BannerAdapter bannerAdapter;
	
	Handler handler = new Handler();
	
	View[] bannerViews;
	
	CustomViewPager viewPager;
	LinearLayout pointLayout;
	
	int pointNormal,pointSelect;
	
	public BannerWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		pointNormal = R.drawable.test_point;
		pointSelect = R.drawable.test_point_select;
		
		createViewPager();
	}

	public BannerWidget(Context context) {
		super(context);
		mContext = context;
		
		pointNormal = R.drawable.test_point;
		pointSelect = R.drawable.test_point_select;
		
		createViewPager();
		
	}
	
	
	private void createViewPager(){
		viewPager = new CustomViewPager(getContext());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		viewPager.setLayoutParams(params);
		
		addView(viewPager);
	}
	
	private void createPointView(){
		pointLayout = new LinearLayout(getContext());
		pointLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		pointLayout.setOrientation(LinearLayout.HORIZONTAL);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		params.setMargins(0, 0, 0, dip2px(getContext(), 12));
		
		pointLayout.setLayoutParams(params);
		
		for (int i = 0; i < bannerViews.length; i++) {
			ImageView imageView = new ImageView(getContext());
			LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			pointParams.setMargins(0, 0, dip2px(getContext(), 4), 0);
			imageView.setLayoutParams(pointParams);
			if(i == 0){
				imageView.setImageResource(pointSelect);
			}else{
				imageView.setImageResource(pointNormal);
			}
			
			pointLayout.addView(imageView);
		}
		
		addView(pointLayout);
	}
	
	public void setPointNormal(int pointNormal) {
		this.pointNormal = pointNormal;
	}

	public void setPointSelect(int pointSelect) {
		this.pointSelect = pointSelect;
	}

	public class CustomViewPager extends ViewPager {


		
		public CustomViewPager(Context context) {
			super(context);
			
		}

		public CustomViewPager(Context context, AttributeSet attrs) {
			super(context, attrs);
			
		}
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			return false;
		}
		

	}
	
	@SuppressWarnings("deprecation")
	public void setData(BannerData data){
		if(data == null){
			return ;
		}
		
		if(data.local){
			if(data.resListLocal.isEmpty()){
				return;
			}
		}
		createImgViews(data.resListLocal);
		
		viewPager.setAdapter(bannerAdapter = new BannerAdapter());
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				
				changePointState(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		createPointView();
		
		viewPager.setCurrentItem(0);
		
		startTimerBanner();
	}
	
	private void changePointState(int page ){
		for (int i = 0; i < pointLayout.getChildCount(); i++) {
			ImageView p = (ImageView) pointLayout.getChildAt(i);
			if(page == i){
				p.setImageResource(pointSelect);
			}else{
				p.setImageResource(pointNormal);
			}
		}
	}
	
	private void createImgViews(List<Integer> list){
		bannerViews = new View[list.size()];
		for (int i = 0; i < list.size(); i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageResource(list.get(i));
			bannerViews[i] = imageView;
		}
		
	}
	
	public void stopTimerBanner(){
		if(timer != null){
			timer.cancel();
		}
	}
	public void startTimerBanner(){
		if(timer != null){
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				if(bannerPagerCount+1 >= bannerAdapter.getCount()){
					bannerPagerCount = 0;
				}else{
					bannerPagerCount++;
				}
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						viewPager.setCurrentItem(bannerPagerCount,true);
					}
				});
			}
		}, 1000, 3000);
	}
	
	public class BannerData{
		public BannerData(){
			
		}
		public boolean local = true;
		private List<Integer> resListLocal = new ArrayList<Integer>();
		private List<String> resListNet = new ArrayList<String>();
		
		public List<Integer> getResListLocal() {
			return resListLocal;
		}
		public void setResListLocal(List<Integer> resListLocal) {
			this.resListLocal = resListLocal;
		}
		public List<String> getResListNet() {
			return resListNet;
		}
		public void setResListNet(List<String> resListNet) {
			this.resListNet = resListNet;
		}
		
	}
	
	public class BannerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return bannerViews == null ? 0 : bannerViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
				container.addView(bannerViews[position]);
			return bannerViews[position];
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
}
