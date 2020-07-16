package com.bytedance.androidcamp.network.dou.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

public class MarqueeTextView extends AppCompatTextView implements Runnable{
    private int currentScrollX = 0;
    private boolean isStop = false;
    private int textWidth;
    private boolean isMeasure = false;
    public MarqueeTextView(Context context) {
        super(context);
    }
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isMeasure) {
            getTextWidth();
            isMeasure = true;
        }
    }
    private void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        textWidth = (int) ((TextPaint) paint).measureText(str);
    }
    @Override
    public void run() {
        currentScrollX += 2;
        scrollTo(currentScrollX, 0);
        if (isStop) {
            return;
        }
        if (getScrollX() >= this.getWidth()) {
            scrollTo(textWidth, 0);
            currentScrollX = -this.getWidth();
        }
        postDelayed(this, 15);
    }
    public void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        post(this);
    }
    public void stopScroll() {
        isStop = true;
    }
    public void startFor0() {
        currentScrollX = 0;
        startScroll();
    }
}