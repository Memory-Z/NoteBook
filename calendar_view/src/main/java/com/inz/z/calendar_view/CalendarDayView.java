package com.inz.z.calendar_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;

/**
 * 日历布局 - 日
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/31 15:08.
 */
public class CalendarDayView extends View {

    private static final String TAG = "CalendarViewDay";
    private static final int DATE_DARK_COLOR = Color.parseColor("#FF474747");
    private static final int DATE_WHITE_COLOR = Color.parseColor("#FFF5F5F5");
    /**
     * 主色
     */
    private static final int COLOR_PRIMARY = Color.parseColor("#FF40A798");
    private static final int COLOR_WHITE = Color.parseColor("#F7F7F7");
    private static final int COLOR_RED = Color.parseColor("#F34949");
    private static final int COLOR_YELLOW = Color.parseColor("#FFB700");

    private static final int LUNAR_DARK_COLOR = Color.parseColor("#80474747");
    private static final int LUNAR_WHITE_COLOR = Color.parseColor("#80F5F5F5");

    private static final int CURRENT_DARK_COLOR = Color.parseColor("#4D474747");
    private static final int CURRENT_WHITE_COLOR = Color.parseColor("#4D474747");

    private static final int CHECKED_DARK_COLOR = Color.parseColor("#FF40A798");
    private static final int CHECKED_WHITE_COLOR = Color.parseColor("#FF40FF99");

    private static final int TEXT_SIZE = 16;

    private Context mContext;

    /**
     * 日期画笔
     */
    private Paint datePaint;
    private int dateTextColor = DATE_DARK_COLOR;
    private int dateTextSize = TEXT_SIZE;

    /**
     * 阴历画笔
     */
    private Paint lunarPaint;
    private int lunarTextColor = LUNAR_DARK_COLOR;
    private int lunarTextSize = TEXT_SIZE - 2;

    /**
     * 选中背景画笔
     */
    private Paint checkedPaint;
    private int checkedColor = CHECKED_DARK_COLOR;
    private RectF checkedRectF;

    /**
     * 计划画笔（存在计划）
     */
    private Paint schedulePaint;

    /**
     * 标记画笔（休/班）
     */
    private Paint tagPaint;
    private int tagColor = COLOR_WHITE;
    private Rect tagRect;

    private Paint tagBackgroundPaint;
    private static int tagBackgroundColor = COLOR_RED;
    private Path tagBackgroundPath;

    /**
     * 当前日期背景画笔
     */
    private Paint currentPaint;
    private int currentColor = CURRENT_DARK_COLOR;


    private DayViewBean dayViewBean = new DayViewBean();
    /**
     * 是否只能选中 一个
     */
    private boolean isCheckedOne = false;
    /**
     * 是否重置大小 ： 默认重置
     */
    private boolean resetSize = false;
    private ViewMode viewMode = ViewMode.WEEK;

    public CalendarDayView(Context context) {
        super(context);
        mContext = context;
        initView();
        initPaint();
    }

    public CalendarDayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarDayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initStyle(attrs);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (resetSize) {
            int size = Math.min(width, height);
            setMeasuredDimension(size, size);
        }
    }

    private void initView() {
        boolean isDark = Tools.isDarkMode(mContext);
        dateTextSize = Tools.sp2px(mContext, TEXT_SIZE);
        lunarTextSize = dateTextSize - Tools.sp2px(mContext, 8);
        dateTextColor = isDark ? DATE_WHITE_COLOR : DATE_DARK_COLOR;
        lunarTextColor = isDark ? LUNAR_WHITE_COLOR : LUNAR_DARK_COLOR;
        checkedColor = isDark ? CHECKED_WHITE_COLOR : CHECKED_DARK_COLOR;
        currentColor = isDark ? CURRENT_WHITE_COLOR : CURRENT_DARK_COLOR;
        tagColor = COLOR_WHITE;
    }

    private void initStyle(AttributeSet attr) {
        boolean isDark = Tools.isDarkMode(mContext);

        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attr, R.styleable.CalendarDayView, 0, 0);
        dateTextSize = array.getDimensionPixelSize(R.styleable.CalendarDayView_date_text_size, TEXT_SIZE);
        lunarTextSize = dateTextSize - Tools.sp2px(mContext, 8);
        dateTextColor = array.getColor(R.styleable.CalendarDayView_date_text_color, isDark ? DATE_WHITE_COLOR : DATE_DARK_COLOR);
        lunarTextColor = array.getColor(R.styleable.CalendarDayView_lunar_text_color, isDark ? LUNAR_WHITE_COLOR : LUNAR_DARK_COLOR);
        checkedColor = array.getColor(R.styleable.CalendarDayView_checked_color, isDark ? CHECKED_WHITE_COLOR : CHECKED_DARK_COLOR);
        currentColor = array.getColor(R.styleable.CalendarDayView_current_color, isDark ? CURRENT_WHITE_COLOR : CURRENT_DARK_COLOR);
        tagColor = array.getColor(R.styleable.CalendarDayView_tag_color, COLOR_WHITE);
        array.recycle();
    }

    /**
     * 初始化 画笔
     */
    private void initPaint() {
        // 日期
        datePaint = new Paint();
        datePaint.setAntiAlias(true);
        datePaint.setTextSize(dateTextSize);
        datePaint.setColor(dateTextColor);
        datePaint.setStyle(Paint.Style.FILL);
        datePaint.setTextAlign(Paint.Align.CENTER);

        // 阴历
        lunarPaint = new Paint();
        lunarPaint.setAntiAlias(true);
        lunarPaint.setTextSize(lunarTextSize);
        lunarPaint.setColor(lunarTextColor);
        lunarPaint.setStyle(Paint.Style.FILL);
        lunarPaint.setTextAlign(Paint.Align.CENTER);

        // 选中背景
        checkedPaint = new Paint();
        checkedPaint.setAntiAlias(true);
        checkedPaint.setStyle(Paint.Style.FILL);
        checkedPaint.setColor(checkedColor);

        checkedRectF = new RectF();

        // 计划 （存在计划）
        schedulePaint = new Paint();
        schedulePaint.setAntiAlias(true);
        schedulePaint.setStyle(Paint.Style.FILL);
        schedulePaint.setColor(lunarTextColor);

        // 标记
        tagPaint = new Paint();
        tagPaint.setAntiAlias(true);
        tagPaint.setStyle(Paint.Style.FILL);
        tagPaint.setColor(tagColor);
        tagPaint.setTextSize(lunarTextSize);
        tagPaint.setTextAlign(Paint.Align.CENTER);

        tagRect = new Rect();

        tagBackgroundPaint = new Paint();
        tagBackgroundPaint.setColor(tagBackgroundColor);
        tagBackgroundPaint.setAntiAlias(true);
        tagBackgroundPaint.setStyle(Paint.Style.FILL);
        tagBackgroundPath = new Path();


        // 当前日期
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setStyle(Paint.Style.FILL);
        currentPaint.setColor(currentColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dayViewBean.isOtherMonth()) {
            // 其他月份，在 月/年 视图 下显示
            return;
        }
        float w = getWidth() - getPaddingStart() - getPaddingEnd();
        float h = getHeight() - getPaddingTop() - getPaddingBottom();
        float startX = getPaddingStart();
        float startY = getPaddingTop();
        float endX = startX + w;
        float endY = startY + h;
        float centerX = startX + w / 2;
        float centerY = startY + h / 2;

        datePaint.setColor(dateTextColor);
        lunarPaint.setColor(lunarTextColor);

        if (dayViewBean.isCurrentDate()) {
            datePaint.setColor(COLOR_RED);
            lunarPaint.setColor(COLOR_RED);
            checkedPaint.setColor(checkedColor);

//            if (!dayViewBean.isChecked()) {
//                canvas.drawCircle(centerX, centerY, centerX, currentPaint);
//            }
        }
        if (dayViewBean.isChecked()) {
            datePaint.setColor(COLOR_WHITE);
            lunarPaint.setColor(COLOR_WHITE);
            checkedPaint.setColor(COLOR_RED);

            checkedRectF.setEmpty();
            checkedRectF.set(startX, startY, endX, endY);
            if (dayViewBean.isFirshChecked() && dayViewBean.isLastChecked()) {
                canvas.drawCircle(centerX, centerY, centerX, checkedPaint);
            } else {
                checkedPaint.setColor(tagBackgroundColor);
                if (!dayViewBean.isFirshChecked() && !dayViewBean.isLastChecked()) {
                    canvas.drawCircle(centerX, centerY, centerX, checkedPaint);
//                    canvas.drawRect(0, startY, getWidth(), endY, checkedPaint);
                } else {
                    if (dayViewBean.isFirshChecked()) {
                        canvas.drawArc(checkedRectF, 90, 180, true, checkedPaint);
                        canvas.drawRect(centerX, startY, getWidth(), endY, checkedPaint);
                    }
                    if (dayViewBean.isLastChecked()) {
                        canvas.drawArc(checkedRectF, -90, 180, true, checkedPaint);
                        canvas.drawRect(0, startY, centerX, endY, checkedPaint);
                    }
                }
            }
        }
        Paint.FontMetricsInt fontMetricsInt = datePaint.getFontMetricsInt();
        Paint.FontMetricsInt lunarTextFontMetricsInt = lunarPaint.getFontMetricsInt();
        float dateTextHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        float lunarTextHeight = lunarTextFontMetricsInt.bottom - lunarTextFontMetricsInt.top;
        float contentHeight = dateTextHeight + lunarTextHeight / 3F * 5;
        float textStartY = (h - contentHeight) / 2F;
        float dateCenterY = startY + textStartY + dateTextHeight / 2 + lunarTextHeight / 5;
        float dateBaseLineY = dateCenterY - (fontMetricsInt.top + fontMetricsInt.bottom) / 2F;
        canvas.drawText(dayViewBean.getDateStr(), centerX, dateBaseLineY, datePaint);

        float lunarCenterY = startY + h - textStartY - lunarTextHeight / 3 * 2;
        float lunarBaseLineY = lunarCenterY - (lunarTextFontMetricsInt.top + lunarTextFontMetricsInt.bottom) / 2F;
        canvas.drawText(dayViewBean.getLunarStr(), centerX, lunarBaseLineY, lunarPaint);

        // 存在标签
        if (dayViewBean.isHaveTag()) {
            drawTag(canvas);
        }
    }

    /**
     * 绘制标记
     *
     * @param canvas 画布
     */
    private void drawTag(Canvas canvas) {
        float w = getWidth() - getPaddingStart() - getPaddingEnd();
        float h = getHeight() - getPaddingBottom() - getPaddingTop();
        float startX = getPaddingStart();
        float startY = getPaddingTop();
        float endX = startX + w;

        Paint.FontMetricsInt tagFontMetricsInt = tagPaint.getFontMetricsInt();
        float tagTextHeight = tagFontMetricsInt.bottom - tagFontMetricsInt.top;
        String tagStr = dayViewBean.getTagStr();
        tagPaint.getTextBounds(tagStr, 0, tagStr.length(), tagRect);
        float tagTextWidth = tagRect.right - tagRect.left;
        float tagSize = Math.max(tagTextHeight, tagTextWidth);

        float tagCenterX = endX - tagSize;
        float tagCenterY = startY + h / 2;

        getTagBackgroundPath(tagSize, endX - tagSize / 2, startY + h / 2 - tagSize / 2);

        canvas.drawPath(tagBackgroundPath, tagBackgroundPaint);

        float tagBaseLine = tagCenterY - (tagFontMetricsInt.top + tagFontMetricsInt.bottom) / 2F;
        canvas.drawText(tagStr, tagCenterX, tagBaseLine, tagPaint);

    }

    /**
     * 获取标签背景路径
     */
    private void getTagBackgroundPath(float tagSize, float endX, float startY) {
        tagBackgroundPath.reset();
        float tagBackgroundRoundSize = tagSize / 8;
        tagBackgroundPath.moveTo(endX - tagSize + tagBackgroundRoundSize, startY);
        tagBackgroundPath.lineTo(endX - tagBackgroundRoundSize, startY);
        tagBackgroundPath.arcTo(
                endX - tagBackgroundRoundSize * 2,
                startY,
                endX,
                startY + tagBackgroundRoundSize * 2,
                -90,
                90,
                false
        );
        tagBackgroundPath.lineTo(endX, startY + tagSize - tagBackgroundRoundSize);
        tagBackgroundPath.arcTo(
                endX - tagBackgroundRoundSize * 2,
                startY + tagSize - tagBackgroundRoundSize * 2,
                endX,
                startY + tagSize,
                0,
                90,
                false
        );
        tagBackgroundPath.lineTo(endX - tagSize + tagBackgroundRoundSize, startY + tagSize);
        tagBackgroundPath.arcTo(
                endX - tagSize,
                startY + tagSize - tagBackgroundRoundSize * 2,
                endX - tagSize + tagBackgroundRoundSize * 2,
                startY + tagSize,
                90,
                90,
                false
        );
        tagBackgroundPath.lineTo(endX - tagSize, startY + tagBackgroundRoundSize);
        tagBackgroundPath.arcTo(
                endX - tagSize,
                startY,
                endX - tagSize + tagBackgroundRoundSize * 2,
                startY + tagBackgroundRoundSize * 2,
                180,
                90,
                false
        );
        tagBackgroundPath.close();
    }

    private float mLastMotionX = 0F, mLastMotionY = 0F;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: " + event.getAction());
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (Float.compare(mLastMotionX, x) == 0 && Float.compare(mLastMotionY, y) == 0) {
                    performClick();
                    break;
                }
            }
            default: {
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        if (dayViewListener != null) {
            dayViewListener.onPerformClick(this, dayViewBean);
        }
        dayViewBean.setChecked(true);
        invalidate();
        return super.performClick();
    }

    @Override
    public String toString() {
        return "CalendarDayView{ " + dayViewBean + " }";
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外开放接口
    ///////////////////////////////////////////////////////////////////////////


    public void setDayViewBean(DayViewBean dayViewBean) {
        this.dayViewBean = dayViewBean;
        invalidate();
    }

    public DayViewBean getDayViewBean() {
        return dayViewBean;
    }

    public void setDateTextColor(int dateTextColor) {
        this.dateTextColor = dateTextColor;
    }

    public void setDateTextSize(int dateTextSize) {
        this.dateTextSize = dateTextSize;
    }

    public void setLunarTextColor(int lunarTextColor) {
        this.lunarTextColor = lunarTextColor;
    }

    public void setLunarTextSize(int lunarTextSize) {
        this.lunarTextSize = lunarTextSize;
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }

    public static void setTagBackgroundColor(int tagBackgroundColor) {
        CalendarDayView.tagBackgroundColor = tagBackgroundColor;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public void setResetSize(boolean resetSize) {
        this.resetSize = resetSize;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    /* -------------------------- 监听 ---------------------------- */

    /**
     * 日历 - 日- 监听
     */
    public interface CalendarDayViewListener {
        /**
         * 日期点击
         *
         * @param view View
         * @param bean 对象
         */
        void onPerformClick(CalendarDayView view, @NonNull DayViewBean bean);
    }

    private CalendarDayViewListener dayViewListener;

    public void setDayViewListener(CalendarDayViewListener dayViewListener) {
        this.dayViewListener = dayViewListener;
    }

    /* -------------------------- 监听 ---------------------------- */
}
