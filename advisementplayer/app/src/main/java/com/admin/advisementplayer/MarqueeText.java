package com.admin.advisementplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

public class MarqueeText extends TextView implements Runnable {
    private int currentScrollX;// 当前滚动的位置
    private boolean isRun = false;
    private int textWidth;
    private boolean isMeasure = false;
    private String mText;


    public MarqueeText(Context context) {
        this(context,null);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected void onDraw(Canvas canvas) {
// TODO Auto-generated method stub
        super.onDraw(canvas);
        if (!isMeasure) {// 文字宽度只需获取一次就可以了
            getTextWidth(canvas);
            isMeasure = true;
        }

    }

    /**
     * 获取文字宽度
     */
    private void getTextWidth(Canvas canvas) {
        Paint paint = this.getPaint();
        mText = this.getText().toString();
//        int size = (int) this.getTextSize();
        textWidth = (int) paint.measureText(mText);
    }
    @Override
    public void run() {
////        从左到右
//        currentScrollX -= 2;// 滚动速度
//        scrollTo(currentScrollX,0);
//        if (isStop) {
//            return;
//        }
//        if (getScrollX() <= -(this.getWidth())) {
//            scrollTo(textWidth, 0);
//            currentScrollX = textWidth;
////        return;
//        }
        //从右到左
        currentScrollX += 2;//滚动速度
        scrollTo(currentScrollX, 0);
        if (textWidth != 0 && textWidth <= this.getWidth()) {
            if (!isRun) {
                return;
            }
        }

        if (getScrollX() >= textWidth) {
            currentScrollX = -this.getWidth();
            scrollTo(currentScrollX, 0);
        }

        postDelayed(this, 8);
    }

    // 开始滚动
    public void startScroll(boolean isRun) {
        this.isRun = isRun;
        this.removeCallbacks(this);
        post(this);
    }

    // 停止滚动
    public void stopScroll() {
        isRun = true;
    }

    // 从头开始滚动
    public void startFor0() {
        currentScrollX = 0;
//        startScroll();
    }
}