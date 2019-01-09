package com.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class IndicatorView extends View {
    private int color = Color.RED;
//    private int width = 30;
    public IndicatorView(Context context) {
        this(context,null);
    }
    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setDither(true);
//        canvas.drawCircle(width/2,width/2,width/2,paint);
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,getMeasuredWidth()/2,paint);
    }

    public void setColor(int color){
        this.color = color;
        // 重新绘制View
        invalidate();
    }

    public void setColor(String color){
        this.color = Color.parseColor(color);
        // 重新绘制View
        invalidate();
    }
}
