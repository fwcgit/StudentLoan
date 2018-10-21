package com.qudaozhang.white.mode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qudaozhang.white.R;
import com.qudaozhang.white.interfaces.ILetterViewCallback;
import com.qudaozhang.white.utils.ConvertUtils;
import com.qudaozhang.white.utils.LogUtils;

public class LetterIndexView extends View {
	private int width = 0,height = 0;
	private String drawStr = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
	private float textWidth,textHeight;
	private Paint paint;
	private float padingTop;
	private float space;
	private float drawHeight;
	private ILetterViewCallback callback;
	
	public LetterIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public LetterIndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LetterIndexView(Context context) {
		super(context);
		init();
	}
	
	
	private void init(){
		
		paint = new Paint();
		paint.setColor(ContextCompat.getColor(getContext(), R.color.bg));
		paint.setTextSize(ConvertUtils.sp2px(getContext(), 12));
		paint.setAntiAlias(true);
		
		Rect rect = new Rect();
		paint.getTextBounds("A", 0, 1, rect);
		textWidth = Math.abs(rect.right - rect.left);
		textHeight = Math.abs(rect.bottom - rect.top);
		
		space = ConvertUtils.dip2px(getContext(), 8);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(width == 0 || height == 0){
			invalidate();
		}
		
		padingTop = (height - (28f * textHeight + space * 27f))/2;
		drawHeight = 28f * textHeight + space * 27f;
		
		for (int i = 0; i < drawStr.length(); i++) {
			String c = drawStr.substring(i,i+1);
			canvas.drawText(c, (width- textWidth)/2, padingTop+textHeight/2+textHeight *i+ i* space, paint);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		float y = event.getY();
		
		int tempIndex = (int) ((y-padingTop + ConvertUtils.dip2px(getContext(), 5))/ drawHeight * drawStr.length());
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			tempIndex = (int) ((y-padingTop + ConvertUtils.dip2px(getContext(), 5))/ drawHeight * drawStr.length());
			break;
		case MotionEvent.ACTION_UP:
			if(tempIndex >=0 && tempIndex < drawStr.length()){
				if(callback != null){
					callback.letterCallback(tempIndex, String.valueOf(drawStr.charAt(tempIndex)));
				}
				LogUtils.logDug("LetterIndexView index = "+tempIndex + "char = "+drawStr.charAt(tempIndex));
			}
			
			
			break;
		default:
			break;
		}
		return true;
	}

	public void setCallback(ILetterViewCallback callback) {
		this.callback = callback;
	}
	
	
}
