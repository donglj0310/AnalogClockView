package com.android.dlj.libanalogclockview;

import android.content.Context;
import android.content.res.TypedArray;
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

public class AnalogClockView2 extends View {

    private int DEFAULT_HOUR_COLOR = Color.GRAY;
    private int DEFAULT_MINUTE_COLOR = Color.GRAY;
    private int DEFAULT_SECOND_COLOR = Color.RED;
    private int DEFAULT_CIRCLE_COLOR = Color.GRAY;
    private int DEFAULT_TEXT_COLOR = Color.BLACK;
    private int DEFAULT_LINE_COLOR = Color.GRAY;

    private float DEFAULT_RADIUS = 300;
    private float mRadius = DEFAULT_RADIUS;

    private float DEFAULT_HOUR_WIDTH = mRadius / 20;
    private float DEFAULT_MINUTE_WIDTH = mRadius / 20;
    private float DEFAULT_SECOND_WIDTH = mRadius / 100;
    private float DEFAULT_CIRCLE_WIDTH = mRadius / 30;
    private float DEFAULT_TEXT_WIDTH = mRadius / 100;
    private float DEFAULT_LINE_WIDTH = mRadius / 100;

    private float DEFAULT_CIRCLE_SIZE = mRadius / 60;
    private float DEFAULT_TEXT_SIZE = mRadius * 0.1f;

    private long DEFAULT_REFRESH_TIME = 1000;

    private float mCircleWidth = DEFAULT_CIRCLE_WIDTH;

    private Paint mMinutePaint;
    private Paint mSecondPaint;
    private Paint mHourPaint;
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mLinePaint;

    private int mWidth;
    private int mHeight;

    private static final double ROUND = 2d * Math.PI;
    private static final double QUARTER = 1d / 4d;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private long mRefreshTime = DEFAULT_REFRESH_TIME;

    private Calendar calendar = new GregorianCalendar();

    public AnalogClockView2(Context context) {
        super(context);
    }

    public AnalogClockView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint(attrs, 0);
    }

    public AnalogClockView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint(attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 创建所有的paint
     */
    private void initPaint(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AnalogClockView, defStyle, 0);
        mRadius = a.getFloat(R.styleable.AnalogClockView_radius, mRadius);
        mMinutePaint = getAvailablePaint(DEFAULT_MINUTE_COLOR, 0, DEFAULT_MINUTE_WIDTH, Paint.Style.FILL_AND_STROKE);
        mSecondPaint = getAvailablePaint(DEFAULT_SECOND_COLOR, 0, DEFAULT_SECOND_WIDTH, Paint.Style.FILL_AND_STROKE);
        mHourPaint = getAvailablePaint(DEFAULT_HOUR_COLOR, 0, DEFAULT_HOUR_WIDTH, Paint.Style.FILL_AND_STROKE);
        mCirclePaint = getAvailablePaint(DEFAULT_CIRCLE_COLOR, DEFAULT_CIRCLE_SIZE, DEFAULT_CIRCLE_WIDTH, Paint.Style.STROKE);
        mTextPaint = getAvailablePaint(DEFAULT_TEXT_COLOR, DEFAULT_TEXT_SIZE, DEFAULT_TEXT_WIDTH, Paint.Style.FILL_AND_STROKE);
        mTextPaint.setAlpha(100);
        mLinePaint = getAvailablePaint(DEFAULT_LINE_COLOR, 0, DEFAULT_LINE_WIDTH, Paint.Style.STROKE);

    }

    private Paint getAvailablePaint(int color, float textSize, float strockWidth, Paint.Style style) {

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
//        int radius = Math.min(x, y) - mCircleWidth;
        float radius = mRadius;
        float second = (calendar.get(Calendar.SECOND)) / 60f;
        float minute = (calendar.get(Calendar.MINUTE) + second) / 60f;
        float hour = (calendar.get(Calendar.HOUR) + minute) / 12f;

        drawHand(canvas, mHourPaint, x, y, radius * 0.5f, hour);
        drawHand(canvas, mMinutePaint, x, y, radius * 0.6f, minute);
        drawSecond(canvas, mSecondPaint, x, y, radius, second);
        drawHourNumbers(canvas, mTextPaint, mWidth, mHeight, radius);
        drawMinutecircle(canvas, mLinePaint, mWidth, mHeight, radius);
        mCirclePaint.setColor(Color.GRAY);
        mCirclePaint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, 3, mCirclePaint);//表中心点
    }

    private void drawHourNumbers(Canvas canvas, Paint paint, float width, float height, float radius) {

        int i = 0;
        for (i = 1; i < 13; i++) {

            canvas.save(); //save current state of canvas.
            canvas.rotate(360 / 12 * i, width / 2, height / 2);
            //绘制文字
            canvas.drawCircle(width / 2, (height - radius * 2) / 2, mCircleWidth, paint);
            canvas.drawText("" + i, width / 2, height / 2 - radius + mRadius * 0.2f, paint);
            Log.d("dlj", width + " " + height + " " + radius + " " + mRadius);
            //恢复开始位置
            canvas.restore();

        }
    }

    private void drawMinutecircle(Canvas canvas, Paint paint, float width, float height, float radius) {

        for (int i = 1; i < 61; i++) {

            canvas.save(); //save current state of canvas.
            canvas.rotate(360 / 60 * i, width / 2, height / 2);
            //绘制表盘
            canvas.drawCircle(width / 2, (height - radius * 2) / 2, mCircleWidth, paint);
            Log.d("radians1", "drawSecond: " + (width / 2));
            //恢复开始位置
            canvas.restore();

        }
    }

    private void drawSecond(Canvas canvas, Paint paint, float x, float y, float length, float round) {
        // 三角函数的坐标轴是以 3 点方向为 0 的，所以记得要减去四分之一个圆周哦
        double radians = (round - QUARTER) * ROUND;
        Log.d("radians2", "drawSecond: " + (x + (float) Math.cos(radians) * length));
        canvas.drawCircle(x + (float) Math.cos(radians) * length, y + (float) Math.sin(radians) * length, mCircleWidth, paint);
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
            mHandler.postDelayed(this, mRefreshTime);

        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

    }
}
