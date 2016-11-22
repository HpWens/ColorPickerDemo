package com.github.colorpickerdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.github.colorpickerdemo.listener.OnSeekColorListener;

/**
 * Created by Administrator on 11/15 0015.
 * It's very fashion
 */
public class ColorPicker extends View {

    private Context context;
    /**
     * Currently selected color
     */
    private float[] colorHSV = new float[]{0f, 1f, 1f};

    private Paint colorWheelPaint;

    private Paint touchCirclePaint;

    private int radius;

    private int centerX;

    private int centerY;

    private int touchCircleX;

    private int touchCircleY;

    private OnSeekColorListener onSeekColorListener;

    public ColorPicker(Context context) {
        this(context, null);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        this.context = context;
        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);

        touchCirclePaint = new Paint();
        touchCirclePaint.setStyle(Paint.Style.STROKE);
        touchCirclePaint.setColor(Color.WHITE);
        touchCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //处理 wrap_content问题
        int defaultDimension = dip2px(200);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultDimension, defaultDimension);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultDimension, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultDimension);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(centerX, centerY);
        createColorWheel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, colorWheelPaint);
        canvas.drawCircle(touchCircleX, touchCircleY, 10, touchCirclePaint);
    }

    /**
     * create color wheel
     */
    private void createColorWheel() {
        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount];
        float hsv[] = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }

        SweepGradient sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        RadialGradient radialGradient = new RadialGradient(centerX, centerY,
                radius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        colorWheelPaint.setShader(composeShader);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int cx = x - centerX;
                int cy = y - centerY;
                double d = Math.sqrt(cx * cx + cy * cy);

                if (d <= radius) {
                    colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
                    colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / radius)));
                    if (onSeekColorListener != null) {
                        touchCircleY = y;
                        touchCircleX = x;
                        onSeekColorListener.onSeekColorListener(getColor());
                        postInvalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void setOnSeekColorListener(OnSeekColorListener listener) {
        this.onSeekColorListener = listener;
    }

    /**
     * @param color 0~360
     */
    public void setHSVColor(int color) {
        colorHSV[0] = color;
        colorWheelPaint.setColor(Color.HSVToColor(colorHSV));
        postInvalidate();
    }

    /**
     * @param value 0~1.0
     */
    public void setHSVValue(float value) {
        colorHSV[2] = value;
        colorWheelPaint.setColor(Color.HSVToColor(colorHSV));
        postInvalidate();
    }

    /**
     * @param saturation 0~1.0
     */
    public void setHSVSaturation(float saturation) {
        colorHSV[1] = saturation;
        colorWheelPaint.setColor(Color.HSVToColor(colorHSV));
        postInvalidate();
    }

    public void setColor(int color) {
        Color.colorToHSV(color, colorHSV);
    }

    public int getColor() {
        return Color.HSVToColor(colorHSV);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putFloatArray("color", colorHSV);
        state.putParcelable("super", super.onSaveInstanceState());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            colorHSV = bundle.getFloatArray("color");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
