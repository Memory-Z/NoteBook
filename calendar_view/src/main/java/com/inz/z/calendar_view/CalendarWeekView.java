package com.inz.z.calendar_view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 日历 - 周
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/02/01 13:54.
 */
public class CalendarWeekView extends LinearLayout {
    private static final String TAG = "CalendarWeekView";
    private Context mContext;
    private Calendar calendar;
    private Calendar currentCalendar;

    /**
     * 速度跟踪
     */
    private VelocityTracker velocityTracker = null;

    /**
     * 显示模式： 0；周视图；1：月视图；2：年视图
     */
    private ViewMode viewMode = ViewMode.WEEK;

    private CalendarDayView[] calendarDayViews = new CalendarDayView[7];
    private DayViewBean[] dayViewBeans = new DayViewBean[7];

    private boolean isCheckedOne = false;
    private List<CalendarDayView> checkedCalendarDayViewList = new ArrayList<>();
    private CalendarDayView[] checkedFirstAndLastDayViewArray = new CalendarDayView[2];

    public CalendarWeekView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CalendarWeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarWeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initStyle(attrs);
        initView();
    }

    private void initStyle(AttributeSet attr) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attr, R.styleable.CalendarWeekView, 0, 0);
        int viewModeInt = array.getInteger(R.styleable.CalendarWeekView_view_mode, 0);
        switch (viewModeInt) {
            case 1: {
                viewMode = ViewMode.MONTH;
                break;
            }
            case 2: {
                viewMode = ViewMode.YEAR;
                break;
            }
            case 0:
            default: {
                viewMode = ViewMode.WEEK;
                break;
            }
        }
        array.recycle();
    }

    /**
     * 初始化 视图
     */
    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        currentCalendar = Calendar.getInstance(Locale.CHINA);
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        int dayIndex = currentCalendar.get(Calendar.DAY_OF_WEEK);
        int date = currentCalendar.get(Calendar.DATE);
        int week = currentCalendar.get(Calendar.WEEK_OF_MONTH);
        int month = currentCalendar.get(Calendar.MONTH);

        initDate(week, date);

        for (int i = 0; i < calendarDayViews.length; i++) {
            CalendarDayView dayView = new CalendarDayView(mContext);
            dayView.setViewMode(viewMode);
            dayView.setDayViewListener(new CalendarDayViewListenerImpl());
            calendarDayViews[i] = dayView;
        }
    }

    private DayViewBean checkedDayViewBean;
    private CalendarDayView checkedCalendarDayView;

    /**
     * 日期点击监听
     */
    private class CalendarDayViewListenerImpl implements CalendarDayView.CalendarDayViewListener {
        @Override
        public void onPerformClick(CalendarDayView dayView, @NonNull DayViewBean bean) {
            DayViewBean dayViewBean = dayView.getDayViewBean();
            if (dayViewBean.isOtherMonth()) {
                // 处于 其他月份 不做处理
                return;
            }
            if (isCheckedOne) {
                if (checkedCalendarDayView != null) {
                    DayViewBean checkedDayViewBean = checkedCalendarDayView.getDayViewBean();
                    checkedDayViewBean.setChecked(false);
                    checkedCalendarDayView.setDayViewBean(checkedDayViewBean);
                    checkedCalendarDayViewList.clear();
                }
                dayViewBean.setChecked(true);
                dayView.setDayViewBean(dayViewBean);
                checkedCalendarDayViewList.add(dayView);
            } else {
                // 选中的是否连续
                if (!checkChooseIsContinuous(bean.getCalendar())) {
                    for (CalendarDayView calendarDayView : checkedCalendarDayViewList) {
                        DayViewBean viewBean = calendarDayView.getDayViewBean();
                        viewBean.setChecked(false);
                        calendarDayView.setDayViewBean(viewBean);
                    }
                    checkedCalendarDayViewList.clear();
                }
                checkedCalendarDayViewList.add(dayView);
                int size = checkedCalendarDayViewList.size();
                CalendarDayView[] calendarDayViews = new CalendarDayView[size];
                checkedCalendarDayViewList.toArray(calendarDayViews);
                Arrays.sort(calendarDayViews, new CheckedCalendarDayArrayComparator());
                for (int i = 0; i < size; i++) {
                    CalendarDayView calendarDayView = calendarDayViews[i];
                    DayViewBean viewBean = calendarDayView.getDayViewBean();
                    viewBean.setChecked(true);
                    viewBean.setFirshChecked(false);
                    viewBean.setLastChecked(false);
                    if (i == 0) {
                        viewBean.setFirshChecked(true);
                    }
                    if (i == size - 1) {
                        viewBean.setLastChecked(true);
                    }
                    calendarDayView.setDayViewBean(viewBean);
                }
            }
            if (listener != null) {
                listener.onDayClick(CalendarWeekView.this, dayView);
            }
        }
    }

    /**
     * 判断选中的日期是否连续
     *
     * @return 是否连续
     */
    private boolean checkChooseIsContinuous(Calendar calendar) {
        Calendar oldFirstCalendar = null;
        Calendar oldLastCalendar = null;
        CalendarDayView oldFirstCalendarDayView = checkedFirstAndLastDayViewArray[0];
        if (oldFirstCalendarDayView != null) {
            oldFirstCalendar = oldFirstCalendarDayView.getDayViewBean().getCalendar();
        }
        CalendarDayView oldLastCalendarDayView = checkedFirstAndLastDayViewArray[1];
        if (oldLastCalendarDayView != null) {
            oldLastCalendar = oldLastCalendarDayView.getDayViewBean().getCalendar();
        }
        if (oldFirstCalendar != null) {
            Calendar c = (Calendar) calendar.clone();
            c.add(Calendar.DATE, 1);
            int date = c.get(Calendar.DATE);
            int oldDate = oldFirstCalendar.get(Calendar.DATE);
            if (date == oldDate) {
                return true;
            }
        }
        if (oldLastCalendar != null) {
            Calendar c = (Calendar) calendar.clone();
            c.add(Calendar.DATE, -1);
            int date = c.get(Calendar.DATE);
            int oldDate = oldLastCalendar.get(Calendar.DATE);
            return date == oldDate;
        }
        return false;
    }

    /**
     * 选中日期View 排序
     */
    private class CheckedCalendarDayArrayComparator implements Comparator<CalendarDayView> {
        @Override
        public int compare(CalendarDayView o1, CalendarDayView o2) {
            DayViewBean bean1 = o1.getDayViewBean();
            DayViewBean bean2 = o2.getDayViewBean();
            Calendar calendar1 = bean1.getCalendar();
            Calendar calendar2 = bean2.getCalendar();
            if (calendar1 != null && calendar2 != null) {
                return calendar1.compareTo(calendar2);
            }
            return 0;

        }
    }


    /**
     * 设置数
     *
     * @param weekOfMonth 周数
     * @param date        当前日期
     */
    private void initDate(int weekOfMonth, int date) {

        velocityTracker = VelocityTracker.obtain();

        Log.i(TAG, "initDate: calendar = " + calendar);
        if (calendar == null) {
            calendar = currentCalendar;
        }
        int month = calendar.get(Calendar.MONTH);
//        calendar.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        Log.i(TAG, "initDate: dddd = " + calendar.get(Calendar.DATE) + " w " + calendar.get(Calendar.DAY_OF_WEEK));
        for (int i = 0; i < dayViewBeans.length; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i + 1);
            DayViewBean bean = new DayViewBean();
            int d = calendar.get(Calendar.DATE);
            int m = calendar.get(Calendar.MONTH);
            Calendar c = (Calendar) calendar.clone();
            bean.setCalendar(c);
            bean.setOtherMonth(m != month);
            bean.setDateStr(d + "");
            bean.setCurrentDate(d == date);
            bean.setHaveTag(true);
            if (viewMode == ViewMode.WEEK || viewMode == ViewMode.MONTH) {
                LunarTools tools = new LunarTools(calendar);
                String lunarStr = tools.getLunarDayStr();
                bean.setLunarStr(lunarStr);
            }
            bean.setTagStr("休");
            dayViewBeans[i] = bean;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
        int height = (width - getPaddingStart() - getPaddingEnd()) / 7;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        int startX = l + getPaddingStart();
        int startY = getPaddingTop();
        int width = (r - l) - getPaddingStart() - getPaddingEnd();
        int size = width / 7;
        int height = b - t - getPaddingBottom() - getPaddingTop();
        size = Math.min(size, height);
        int diff = (width - size * 7) / 6;
//        size += diff / 2;
//        int diff = 0;
        Log.i(TAG, "onLayout: diff = " + diff);
        removeAllViews();
        for (int i = 0; i < calendarDayViews.length; i++) {
            CalendarDayView dayView = calendarDayViews[i];
            dayView.setDayViewBean(dayViewBeans[i]);
            dayView.layout(startX + size * i + diff * i, startY, startX + size * (1 + i) + diff * i, startY + size);
//            dayView.layout(startX + size * i, startY, startX + size * (1 + i), startY + height);
            addView(dayView);
        }

    }

    /**
     * 按下时间
     */
    private long onTouchDownTime = 0;
    private float onTouchDownX = 0F, onTouchDownY = 0F;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: " + event.getAction());
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外接口
    ///////////////////////////////////////////////////////////////////////////


    public void setCalendar(Calendar calendar, Calendar currentCalendar) {
        this.calendar = calendar;
        int date = currentCalendar.get(Calendar.DATE);
        int week = currentCalendar.get(Calendar.WEEK_OF_MONTH);
        initDate(week, date);
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
        for (CalendarDayView dayView : calendarDayViews) {
            dayView.setViewMode(viewMode);
        }
    }

    public void setCheckedOne(boolean checkedOne) {
        isCheckedOne = checkedOne;
    }

    public void setCheckedFirstAndLastDayViewArray(CalendarDayView[] checkedFirstAndLastDayViewArray) {
        this.checkedFirstAndLastDayViewArray = checkedFirstAndLastDayViewArray;
    }

    /**
     * 清空选中的日期
     */
    public void clearCheckedDayList() {
        this.checkedCalendarDayViewList.clear();
    }

    /* ------------------------------------ 监听 -------------------------------- */

    /**
     * 周 监听
     */
    public interface CalendarWeekViewListener {
        /**
         * 日期点击
         *
         * @param weekView 周视图
         * @param dayView  日视图
         */
        void onDayClick(CalendarWeekView weekView, CalendarDayView dayView);
    }

    private CalendarWeekViewListener listener;

    public void setListener(CalendarWeekViewListener listener) {
        this.listener = listener;
    }
    /* ------------------------------------ 监听 -------------------------------- */
}
