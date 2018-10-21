package com.qudaozhang.white.mode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.qudaozhang.white.R;

/**
 * Created by fu on 2017/6/28.
 */

public class ScaleTextView extends View {
    private int max = 3;
    private int color;
    private float offset = 0;
    private float textSize = sp2px(getContext(),12);
    private Paint paint;

    //private String[] datas  = new String[]{"600","800","1000","1200","1400","1600"};

    private String[] datas  = null;

    public ScaleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

       TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleTextView);
        max = ta.getInt(R.styleable.ScaleTextView_max,3);
        color = ta.getColor(R.styleable.ScaleTextView_color,0xff333333);

        offset = ta.getFloat(R.styleable.ScaleTextView_offset,0);
        offset = dip2px(context,offset);

        textSize = ta.getFloat(R.styleable.ScaleTextView_textSize,12f);
        textSize = sp2px(getContext(),textSize);

        init();
    }

    public void setMax(int max){
        this.max = max;
    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(textSize);
    }

    public void setData(String[] datas){
        this.datas = datas;
        if( null != datas){
            postInvalidate();
        }
    }

    public void setData(int max ,String[] datas){
        this.max = max;
        this.datas = datas;
        if( null != datas){
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0 ){
            postInvalidate();
        }

        if(null == datas) return;

        float x = 0;
        float y = 0;
        int w = 0;
        int h = 0;

        Rect rect = new Rect();

        for (int i = 0 ;i < datas.length;i++){
            String text = datas[i];

            paint.getTextBounds(text,0,text.length(),rect);

            w = Math.abs(rect.right - rect.left);
            h = Math.abs(rect.bottom - rect.top);

            if(i == 0){
                x = 0;
            }else if(i == datas.length-1){
                x = getMeasuredWidth() - w-dip2px(getContext(),0.5f);
            }else{

                float xx = w / 2f;

                x = offset + i * ((getMeasuredWidth()-  offset * 2f) / max) - xx;
            }

            y = getMeasuredHeight();

            canvas.drawText(text,x,y,paint);
        }

        super.onDraw(canvas);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context,float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
