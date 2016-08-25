package com.android.dlj.libanalogclockview;

/**
 * TODO: document your custom view class.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WatchView extends View {

    private Paint minPaint;
    private Paint secondPaint;
    private Paint hourPaint;
    private Paint circlePaint;
    private Paint textPaint;
    private Paint linePaint;

    private int mWidth;
    private int mHeight;

    private static final double ROUND = 2d * Math.PI;
    private static final double QUARTER = 1d / 4d;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Calendar calendar = new GregorianCalendar();

    public WatchView(Context context) {
        super(context);
    }

    public WatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 创建所有的paint
     */
    private void initPaint() {

        minPaint = getAvailablePaint(Color.GRAY, 0, 15, Paint.Style.FILL_AND_STROKE);
        secondPaint = getAvailablePaint(Color.RED, 0, 3, Paint.Style.FILL_AND_STROKE);
        hourPaint = getAvailablePaint(Color.GRAY, 0, 15, Paint.Style.FILL_AND_STROKE);
        circlePaint = getAvailablePaint(Color.GRAY, 5, 10, Paint.Style.STROKE);
        textPaint = getAvailablePaint(Color.BLACK , 45, 3, Paint.Style.FILL_AND_STROKE);
        textPaint.setAlpha(100);
        linePaint = getAvailablePaint(Color.GRAY, 0, 3, Paint.Style.STROKE);

    }

    private Paint getAvailablePaint(int color, int textSize, int strockWidth, Paint.Style style) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setTextSize(textSize);
        paint.setAlpha(1);
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(strockWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeCap(Paint.Cap.ROUND);//画笔风格
        paint.setDither(true);//设置图像抖动处理
        paint.setStrokeJoin(Paint.Join.ROUND);//画笔线等连接处的轮廓样式
        paint.setSubpixelText(true);//有助于显示

        return paint;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = mWidth / 2;
        int y = mHeight / 2;
        //r
        int radius = Math.min(x, y) - 10;
        Log.d("dudu3", "onDraw: "+(y/2-(x/2-20))+"r:"+radius+x+y);
        float second = (calendar.get(Calendar.SECOND)) / 60f;
        Log.d("dudu", "onDraw: "+calendar.get(Calendar.SECOND)+":"+second);
        float minute = (calendar.get(Calendar.MINUTE) + second) / 60f;
        Log.d("dudu1", "onDraw: " + calendar.get(Calendar.MINUTE) + ":" + minute);
        float hour = (calendar.get(Calendar.HOUR) + minute) / 12f;

        drawHand(canvas, hourPaint, x, y, radius * 0.5f, hour);
        drawHand(canvas, minPaint, x, y, radius * 0.6f, minute);
        drawSecond(canvas, secondPaint, x, y, radius, second);
        drawHourNumbers(canvas, textPaint, mWidth, mHeight,radius);
        drawMinutecircle(canvas, linePaint, mWidth, mHeight, radius);
        circlePaint.setColor(Color.GRAY);
        circlePaint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, 3, circlePaint);//表中心点
    }

    private void drawHourNumbers(Canvas canvas, Paint paint, int width, int height ,int radius ) {

        int i = 0;
        for (i = 1; i < 13; i++) {

            canvas.save(); //save current state of canvas.
            canvas.rotate(360 / 12 * i, width / 2, height / 2);
            //绘制表盘
            //canvas.drawLine(width / 2, (height-radius*2)/2, width / 2, (height-radius*2)/2 + 40, paint);
            //绘制文字
            canvas.drawCircle(width / 2, (height-radius*2)/2,10,paint);
            canvas.drawText("" + i, width / 2, height / 2 - width / 2 + 80, paint);
            //恢复开始位置
            canvas.restore();

        }
    }

    private void drawMinutecircle(Canvas canvas, Paint paint, int width, int height,int radius) {

        for (int i = 1; i < 61; i++) {

            canvas.save(); //save current state of canvas.
            canvas.rotate(360 / 60 * i, width / 2, height / 2);
            //绘制表盘
            canvas.drawCircle(width / 2, (height - radius * 2) / 2, 10, paint);
            Log.d("radians1", "drawSecond: " + (width / 2));
            //恢复开始位置
            canvas.restore();

        }
    }
    private void drawMinuteLine(Canvas canvas, Paint paint, int width, int height,int radius) {

        for (int i = 1; i < 61; i++) {

            canvas.save(); //save current state of canvas.
            canvas.rotate(360 / 12 / 5 * i, width / 2, height / 2);
            //绘制表盘
            canvas.drawLine(width / 2, (height - radius * 2) / 2, width / 2, (height - radius * 2) / 2 + 30, paint);
            //恢复开始位置
            canvas.restore();

        }

    }

    private void drawSecond(Canvas canvas, Paint paint, float x, float y, float length, float round) {
        // 三角函数的坐标轴是以 3 点方向为 0 的，所以记得要减去四分之一个圆周哦
        double radians = (round - QUARTER) * ROUND;
        Log.d("radians2", "drawSecond: " + (x + (float) Math.cos(radians) * length));
       canvas.drawCircle(x + (float) Math.cos(radians) * length,y + (float) Math.sin(radians) * length,10,paint);
//        canvas.drawLine(
//                x,
//                y,
//                x + (float) Math.cos(radians) * length,
//                y + (float) Math.sin(radians) * length,
//                paint);
    }

    private void drawHand(Canvas canvas, Paint paint, float x, float y, float length, float round) {
        // 三角函数的坐标轴是以 3 点方向为 0 的
        double radians = (round - QUARTER) * ROUND;

        canvas.drawLine(
                x,
                y,
                x + (float) Math.cos(radians) * length,
                y + (float) Math.sin(radians) * length,
                paint);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(r);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {

            calendar.setTimeInMillis(System.currentTimeMillis());
            invalidate();
            mHandler.postDelayed(this, 1000);

        }
    };
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

    }
}
