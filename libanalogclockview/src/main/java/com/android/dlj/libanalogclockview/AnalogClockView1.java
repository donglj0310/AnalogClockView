package com.android.dlj.libanalogclockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Calendar;
import java.util.Date;

public class AnalogClockView1 extends View {

    private Thread mRefreshThread;
    private float mRefreshTime = 1000;

    private int DEFAULT_HOUR_COLOR = Color.GRAY;
    private int DEFAULT_MINUTE_COLOR = Color.GRAY;
    private int DEFAULT_SECOND_COLOR = Color.RED;
    private int DEFAULT_CIRCLE_COLOR = Color.GRAY;
    private int DEFAULT_TEXT_COLOR = Color.BLACK;
    private int DEFAULT_LINE_COLOR = Color.GRAY;
    private int DEFAULT_CENTER_POINT_COLOR = Color.BLACK;

    private int DEFAULT_RADIUS = 100;
    private float mWidth;
    private float mHeight;
    private float mRadius = DEFAULT_RADIUS;

    private Paint mMinutePaint;
    private Paint mSecondPaint;
    private Paint mHourPaint;
    private Paint mCenterPointPaint;
    private Paint mCirclePaint;

    private float mCircleWidth = mRadius * 0.01f;
    private float mHourPointWidth = mRadius / 60;
    private float mMinutePointWidth = mRadius * 0.01f;
    private float mHourPointLength = mRadius * 2 / 15;
    private float mMinutePointLength = mRadius / 15;
    private float mTextSize = mRadius / 6;

    private float mCenterRadius = mRadius / 30;

    private float mHourWidth = mRadius * 4 / 100;
    private float mMinuteWidth = mRadius * 3 / 100;
    private float mSecondWidth = mRadius * 2 / 100;

    private float density_second = 0.90f;
    private float density_minute = 0.70f;
    private float density_hour = 0.50f;

    //current time
    private float mMillSecond;
    private float mSecond;
    private float mMinute;
    private float mHour;

    public AnalogClockView1(Context context) {
        super(context);
        init(null, 0);
    }

    public AnalogClockView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AnalogClockView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private ViewTreeObserver observer = this.getViewTreeObserver();

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AnalogClockView, defStyle, 0);
        mRadius = a.getFloat(R.styleable.AnalogClockView_radius, mRadius);
        mTextSize = a.getDimension(R.styleable.AnalogClockView_textSize, mTextSize);
        mHourPointWidth = a.getDimension(R.styleable.AnalogClockView_hourPointWidth, mHourPointWidth);
        mHourPointLength = a.getDimension(R.styleable.AnalogClockView_hourPointLength, mHourPointLength);
        mMinutePointWidth = a.getDimension(R.styleable.AnalogClockView_minutePointWidth, mMinutePointWidth);
        mMinutePointLength = a.getDimension(R.styleable.AnalogClockView_minutePointLength, mMinutePointLength);
        mRefreshTime = a.getFloat(R.styleable.AnalogClockView_refreshTime, mRefreshTime);
        density_hour = a.getFloat(R.styleable.AnalogClockView_density_hour, density_hour);
        density_minute = a.getFloat(R.styleable.AnalogClockView_density_minute, density_minute);
        density_second = a.getFloat(R.styleable.AnalogClockView_density_second, density_second);
        mHourPaint = getAvailablePaint(DEFAULT_HOUR_COLOR, mHourWidth, Paint.Style.FILL_AND_STROKE);
        mMinutePaint = getAvailablePaint(DEFAULT_MINUTE_COLOR, mMinuteWidth, Paint.Style.FILL_AND_STROKE);
        mSecondPaint = getAvailablePaint(DEFAULT_SECOND_COLOR, mSecondWidth, Paint.Style.FILL_AND_STROKE);
        mCirclePaint = getAvailablePaint(DEFAULT_CIRCLE_COLOR, mCircleWidth, Paint.Style.FILL_AND_STROKE);
        mCenterPointPaint = getAvailablePaint(DEFAULT_CENTER_POINT_COLOR, mCenterRadius, Paint.Style.FILL_AND_STROKE);
    }

    private Paint getAvailablePaint(int color, float strockWidth, Paint.Style style) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setColor(color);
        paint.setStyle(style);
        paint.setStrokeWidth(strockWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);//画笔风格
        paint.setDither(true);//设置图像抖动处理
        paint.setStrokeJoin(Paint.Join.ROUND);//画笔线等连接处的轮廓样式
        paint.setSubpixelText(true);//有助于显示
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("dlj", "AnalogClockView onDraw 106: ");

        //draw circle
        Paint paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setAntiAlias(true);
        paintCircle.setStrokeWidth(mCircleWidth);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, paintCircle);

        //draw degree
        Paint degreePaint = new Paint();
        degreePaint.setAntiAlias(true);
        float lineLength = 0;
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                degreePaint.setStrokeWidth(mHourPointWidth);
                lineLength = mHourPointLength;
            } else {
                degreePaint.setStrokeWidth(mMinutePointWidth);
                lineLength = mMinutePointLength;
            }
            //每旋转60度进行一次刻画
            canvas.drawLine(mWidth / 2, mHeight / 2 - mRadius + mCircleWidth, mWidth / 2, mHeight / 2 - mRadius + lineLength, degreePaint);
            canvas.rotate(360 / 60, mWidth / 2, mHeight / 2);
        }

        //draw point
        canvas.drawCircle(mWidth / 2, mHeight / 2, mCenterRadius, mCenterPointPaint);

        //get time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        mSecond = calendar.get(Calendar.SECOND);
        mMinute = calendar.get(Calendar.MINUTE);
        mHour = calendar.get(Calendar.HOUR);

        //draw second
        drawSecond(canvas, mSecondPaint);

        //draw minute
        drawMinute(canvas, mMinutePaint);

        //draw hour
        drawHour(canvas, mHourPaint);

        //clock number
        degreePaint.setTextSize(mTextSize);
        String targetText[] = getContext().getResources().getStringArray(R.array.clock);
        float startX = mWidth / 2 - degreePaint.measureText(targetText[1]) / 2;
        float startY = mHeight / 2 - mRadius * 0.85f /*+ mCircleWidth*/;
        float textR = (float) Math.sqrt(Math.pow(mWidth / 2 - startX, 2) + Math.pow(mHeight / 2 - startY, 2));

        for (int i = 0; i < 12; i++) {
            float x = (float) (startX + Math.sin(Math.PI / 6 * i) * textR);
            float y = (float) (startY + textR - Math.cos(Math.PI / 6 * i) * textR);
            if (i != 11 && i != 10 && i != 0) {
                y = y + degreePaint.measureText(targetText[i]) / 2;
            } else {
                x = x - degreePaint.measureText(targetText[i]) / 4;
                y = y + degreePaint.measureText(targetText[i]) / 4;
            }
            canvas.drawText(targetText[i], x, y, degreePaint);
        }

    }

    private void drawSecond(Canvas canvas, Paint paint) {
        float degree = mRefreshTime > 1000 ? mSecond * 360 / 60 : mSecond * 360 / 60 ;
        canvas.rotate(degree, mWidth / 2, mHeight / 2);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2, mHeight / 2 - (mRadius - mCircleWidth) * density_second, paint);
        canvas.rotate(-degree, mWidth / 2, mHeight / 2);
    }

    private void drawMinute(Canvas canvas, Paint paint) {
        float degree = mMinute * 360 / 60 + mSecond * 360 / 60 / 60;
        canvas.rotate(degree, mWidth / 2, mHeight / 2);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2, mHeight / 2 - (mRadius - mCircleWidth) * density_minute, paint);
        canvas.rotate(-degree, mWidth / 2, mHeight / 2);
    }

    private void drawHour(Canvas canvas, Paint paint) {
        float degreeHour = mHour * 360 / 12;
        float degreeMinute = mMinute / 60 * 360 / 12;
        float degree = degreeHour + degreeMinute;
        canvas.rotate(degree, mWidth / 2, mHeight / 2);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2, mHeight / 2 - (mRadius - mCircleWidth) * density_hour, paint);
        canvas.rotate(-degree, mWidth / 2, mHeight / 2);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRefreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //fresh time
                    SystemClock.sleep((long) mRefreshTime);
                    postInvalidate();
                }
            }
        });
        mRefreshThread.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRefreshThread.interrupt();
    }
}
