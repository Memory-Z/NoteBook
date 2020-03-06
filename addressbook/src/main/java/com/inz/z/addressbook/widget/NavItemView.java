package com.inz.z.addressbook.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.inz.z.addressbook.tool.Tools;

/**
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/3/4 14:10.
 */
public class NavItemView extends View {
    private static final String TAG = "NavItemView";
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#2D2D2D");
    private static final int DEFAULT_TEXT_GRAY_COLOR = Color.parseColor("#9A9A9A");

    private Context mContext;
    private Paint textPaint;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textSize = 16;

    private NavItemViewBean navItemViewBean;

    public NavItemView(Context context) {
        super(context);
        this.mContext = context;
        initPaint();
    }

    public NavItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initPaint();
    }

    private void initPaint() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(DEFAULT_TEXT_COLOR);
        textPaint.setTextSize(Tools.sp2px(mContext, textSize));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        String tag = "";
        if (navItemViewBean != null) {
            if (navItemViewBean.isCanClick()) {
                textPaint.setColor(textColor);
            } else {
                textPaint.setColor(DEFAULT_TEXT_GRAY_COLOR);
            }
            tag = navItemViewBean.getTag();
        } else {
            textPaint.setColor(textColor);
        }
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int textCenterY = height / 2 - (fontMetricsInt.bottom + fontMetricsInt.top) / 2;
        int centerX = width / 2;
        canvas.drawText(tag, centerX, textCenterY, textPaint);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外接口
    ///////////////////////////////////////////////////////////////////////////

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public NavItemViewBean getNavItemViewBean() {
        return navItemViewBean == null ? (navItemViewBean = new NavItemViewBean()) : navItemViewBean;
    }

    public void setNavItemViewBean(NavItemViewBean navItemViewBean) {
        this.navItemViewBean = navItemViewBean;
    }
}
