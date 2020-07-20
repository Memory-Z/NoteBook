package com.inz.z.base.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Locale;

/**
 * 标准- 带进度条 按钮
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/06/09 09:28.
 */
public class BaseCircularProgressButton extends AppCompatButton {
    private static final String TAG = "BaseCircularProgressBut";

    private static final int CIRCULAR_SIZE = 16;

    private Context mContext;
    private View mView;


    /**
     * 标准显示文字
     */
    private String mTextStr;
    /**
     * 加载文字
     */
    private String mLoadStr;

    /**
     * 文字画笔
     */
    private TextPaint mTextPaint;
    private Rect textRect;
    /**
     * 加载画笔
     */
    private Paint loadingPaint;
    /**
     * 加载框大小
     */
    private PointF mPointF;
    private int circularSize = CIRCULAR_SIZE;

    private boolean startLoading = true;

    public BaseCircularProgressButton(Context context) {
        super(context);
        this.mContext = context;
        initTools();
    }

    public BaseCircularProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initTools();
    }

    public BaseCircularProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initTools();
    }

    /**
     * 初始化工具
     */
    private void initTools() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setTextLocale(Locale.getDefault());
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.RED);

        loadingPaint = new Paint();
        loadingPaint.setAntiAlias(true);
        loadingPaint.setColor(Color.GREEN);
        loadingPaint.setStrokeWidth(2);
        loadingPaint.setStrokeCap(Paint.Cap.ROUND);

        textRect = new Rect();

        mPointF = new PointF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (height > width) {
            return;
        }
        int circular = (height - getPaddingTop() - getPaddingBottom()) / 3 * 2;
        int minSize = dip2px(mContext, CIRCULAR_SIZE);
        circularSize = Math.min(circular, minSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTextPaint.setTextSize(getTextSize());

        int height = getHeight();
        int width = getWidth();
        Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
        int textTop = fontMetricsInt.top;
        int textBottom = fontMetricsInt.bottom;
        int textDistance = (textBottom - textTop) / 2 - textBottom;
        float textCenterY = height / 2F + textDistance;
        String text = getText().toString();
        float textStart = getPaddingStart();
        if (startLoading) {
            textStart = getDrawTextStart();
            float circleStart = getPaddingStart();
            float cX = circleStart + circularSize / 2F;
            float cY = circularSize / 2F + getPaddingTop();
            canvas.drawCircle(cX, cY, 360, loadingPaint);
        }
        text = getDrawText(width, textStart, text);
        float textCenterX = (width - getPaddingEnd() - textStart) / 2 + getPaddingStart();
        canvas.drawText(text, textCenterX, textCenterY, mTextPaint);
        if (startLoading) {
            invalidate();
        }
    }

    /**
     * 获取文字开始位置
     *
     * @return 文字开始位置
     */
    private float getDrawTextStart() {
        float padding = (getHeight() - circularSize) / 2F;
        float paddingStart = getPaddingStart() + padding;
        return paddingStart + padding + circularSize;
    }

    /**
     * 获取需要绘制的文字
     *
     * @param width 界面宽
     * @return 绘制文字
     */
    private String getDrawText(int width, float textStart, String content) {
        String str = content;
        float oneWordWidth = mTextPaint.measureText(str, 0, 1);
        float aliveWidth = width - getPaddingEnd() - textStart;
        int textNum = (int) Math.floor(aliveWidth / oneWordWidth);
        if (str.length() > textNum) {
            str = str.substring(0, textNum - 1) + "...";
        }
        return str;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        CharSequence charSequence = getText();

    }

    /**
     * 根据手机的分辨率dp的单位转成(像素)
     */
    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public boolean isStartLoading() {
        return startLoading;
    }

    public void setStartLoading(boolean startLoading) {
        this.startLoading = startLoading;
        notify();
    }
}
