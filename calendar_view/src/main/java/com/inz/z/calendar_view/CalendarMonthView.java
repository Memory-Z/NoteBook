package com.inz.z.calendar_view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 日历 - 月视图
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/02/02 11:34.
 */
public class CalendarMonthView extends LinearLayout {

    private static final String TAG = "CalendarMonthView";
    private Context mContext;
    private Calendar calendar;
    private Calendar currentCalendar;
    private VelocityTracker velocityTracker;
    private int mTouchSlop = 0;
    private float mMinimumVelocity = 0F;
    private float mMaximumVelocity = 0F;
    private static final int MIN_FLING_VELOCITY = 400; // dips
    private float mWeekViewHeight = 0F;
    /**
     * 是否折叠月视图
     */
    private boolean isFoldMothView = false;

    private ViewMode viewMode = ViewMode.MONTH;
    private boolean isCheckedOne = true;
    private List<CalendarWeekView> calendarWeekViewList = new ArrayList<>();
    private List<CalendarDayView> checkedCalendarDayViewList = new ArrayList<>();
    private CalendarDayView[] checkedFirstAndLastDayViewArray = new CalendarDayView[2];


    public CalendarMonthView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initStyle(attrs);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        currentCalendar = Calendar.getInstance(Locale.CHINA);
        int date = currentCalendar.get(Calendar.DATE);
        int weekOfMonth = currentCalendar.get(Calendar.WEEK_OF_MONTH);
        initData(weekOfMonth, date);
    }

    private void initStyle(AttributeSet attr) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attr, R.styleable.CalendarMonthView, 0, 0);

        array.recycle();
    }

    private void initData(int weekOfMoth, int date) {
        velocityTracker = VelocityTracker.obtain();
        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        float density = mContext.getResources().getDisplayMetrics().density;
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = MIN_FLING_VELOCITY * density;
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();


        if (calendar == null) {
            calendar = currentCalendar;
        }
        int chooseDate = calendar.get(Calendar.DATE);

        // 周数
        int weekNum = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        int minDate = calendar.getActualMinimum(Calendar.DATE);
        Log.i(TAG, "initData: minDate = " + minDate);
        for (int i = 0; i < weekNum; i++) {
            Calendar c = (Calendar) calendar.clone();
            c.set(Calendar.DAY_OF_MONTH, minDate + 7 * i);
            CalendarWeekView weekView = new CalendarWeekView(mContext);
            weekView.setListener(new CalendarWeekViewListenerImpl());
            weekView.setCalendar(c, currentCalendar);
            weekView.setCheckedOne(isCheckedOne);
            weekView.setViewMode(viewMode);
            calendarWeekViewList.add(weekView);
        }
    }


    /**
     * 周视图监听实现
     */
    private class CalendarWeekViewListenerImpl implements CalendarWeekView.CalendarWeekViewListener {
        @Override
        public void onDayClick(CalendarWeekView weekView, CalendarDayView dayView) {
            DayViewBean bean = dayView.getDayViewBean();
            if (isCheckedOne) {
                if (checkedCalendarDayViewList.size() > 0) {
                    for (CalendarDayView view : checkedCalendarDayViewList) {
                        DayViewBean dayViewBean = view.getDayViewBean();
                        dayViewBean.setChecked(false);
                        view.setDayViewBean(dayViewBean);
                    }
                    clearWeekCheckedList();
                    checkedCalendarDayViewList.clear();
                }
                bean.setChecked(true);
                dayView.setDayViewBean(bean);
                checkedCalendarDayViewList.add(dayView);
            } else {
                // 选中的是否连续
                if (!checkChooseIsContinuous(bean.getCalendar())) {
                    for (CalendarDayView calendarDayView : checkedCalendarDayViewList) {
                        DayViewBean dayViewBean = calendarDayView.getDayViewBean();
                        dayViewBean.setChecked(false);
                        calendarDayView.setDayViewBean(dayViewBean);
                    }
                    clearWeekCheckedList();
                    checkedCalendarDayViewList.clear();
                }
                checkedCalendarDayViewList.add(dayView);
                int size = checkedCalendarDayViewList.size();
                CalendarDayView[] calendarDayViews = new CalendarDayView[size];
                checkedCalendarDayViewList.toArray(calendarDayViews);
                Arrays.sort(calendarDayViews, new CheckedCalendarDayArrayComparator());
                Log.i(TAG, "onDayClick: " + Arrays.toString(calendarDayViews));
                for (int i = 0; i < size; i++) {
                    CalendarDayView calendarDayView = calendarDayViews[i];
                    DayViewBean dayViewBean = calendarDayView.getDayViewBean();
                    dayViewBean.setChecked(true);
                    dayViewBean.setFirshChecked(false);
                    dayViewBean.setLastChecked(false);
                    if (i == 0) {
                        dayViewBean.setFirshChecked(true);
                        checkedFirstAndLastDayViewArray[0] = calendarDayView;
                    }
                    if (i == size - 1) {
                        dayViewBean.setLastChecked(true);
                        checkedFirstAndLastDayViewArray[1] = calendarDayView;
                    }
                    calendarDayView.setDayViewBean(dayViewBean);
                }
                // 同步 选中的第一/最后 日期
                for (CalendarWeekView calendarWeekView : calendarWeekViewList) {
                    calendarWeekView.setCheckedFirstAndLastDayViewArray(checkedFirstAndLastDayViewArray);
                }
            }
        }
    }

    /**
     * 清空选中日期列表
     */
    private void clearWeekCheckedList() {
        for (CalendarWeekView weekView : calendarWeekViewList) {
            weekView.clearCheckedDayList();
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
            if (date == oldDate) {
                return true;
            }
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (calendar == null) {
            calendar = currentCalendar;
        }
        int weekNumber = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int weekViewHeight = (width - getPaddingStart() - getPaddingEnd()) / 7;
        mWeekViewHeight = weekViewHeight;
        int height = weekViewHeight * weekNumber + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int startX = getPaddingStart();
        int startY = getPaddingTop();
        int width = r - l - getPaddingStart() - getPaddingEnd();
        int height = b - t - getPaddingTop() - getPaddingBottom();
        int weekWidth = width / 7;
        int size = calendarWeekViewList.size();
        int weekHeight = height / (size == 0 ? 1 : size);
        Log.i(TAG, "onLayout: size - " + size);
        removeAllViews();
        for (int i = 0; i < size; i++) {
            CalendarWeekView weekView = calendarWeekViewList.get(i);
            weekView.setPadding(4, 4, 4, 4);
            weekView.layout(startX, startY + weekHeight * i, startX + width, startY + weekHeight * (i + 1));
            addView(weekView);
        }
    }

    private long onLastTouchDownTime = 0L;
    private float mLastMotionX = 0F, mLastMotionY = 0F;
    private float mInitialMotionX = 0F, mInitialMotionY = 0F;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: " + event.getAction());
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        long time = System.currentTimeMillis();
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = x;
                mLastMotionY = mInitialMotionY = y;
                onLastTouchDownTime = time;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float diffX = Math.abs(x - mInitialMotionX);
                float diffY = Math.abs(y - mInitialMotionX);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onTouchEvent: diffX = " + diffX + " , diffY = " + diffY);
                }
                if (diffY > diffX && diffY > mTouchSlop) {
                    // 折叠 / 展开
                    mLastMotionY = y - mInitialMotionY > 0 ? y + mTouchSlop : y - mTouchSlop;
                    mLastMotionX = x;
                    foldMonthView(y - mInitialMotionY > 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (time - onLastTouchDownTime < 300) {
                    performClick();
                    break;
                }
                VelocityTracker tracker = velocityTracker;
                tracker.computeCurrentVelocity(1000);
                float xv = tracker.getXVelocity();
                float yv = tracker.getYVelocity();
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onTouchEvent: xv = " + xv + " , yv = " + yv);
                }
                float diffX = Math.abs(x - mInitialMotionX);
                float diffY = Math.abs(y - mInitialMotionY);
                if (diffY == 0 && diffX == 0) {
                    monthViewClick(x, y);
                }
                if (diffY > diffX && diffY > mTouchSlop) {
                    // 折叠 / 展开
                    foldMonthView(yv > 0);
                }
                break;
            }
            default: {

            }
        }
        return true;
    }

    /**
     * 月视图点击事件
     *
     * @param x x
     * @param y y
     */
    private void monthViewClick(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        int startX = getPaddingStart();
        int startY = getPaddingTop();
        int endX = width - getPaddingEnd();
        int endY = height = getPaddingBottom();
        int widthSize = endX - startX;
        int heightSize = endY - startY;
        int weekCount = (int) Math.abs(heightSize / mWeekViewHeight);
        if (weekCount > 1) {
            int weekViewOrder = (int) ((y - startY) / mWeekViewHeight + ((y - startY) % mWeekViewHeight > 0 ? 1 : 0));
            if (weekViewOrder > calendarWeekViewList.size()) {
                weekViewOrder = calendarWeekViewList.size();
            }
            if (weekViewOrder < 0) {
                weekViewOrder = 0;
            }
            CalendarWeekView calendarWeekView = calendarWeekViewList.get(weekViewOrder);
            calendarWeekView.performClick();
        } else {
            int size = checkedCalendarDayViewList.size();
            if (size == 1) {
                CalendarDayView dayView = checkedCalendarDayViewList.get(0);
                DayViewBean bean = dayView.getDayViewBean();
                Calendar calendar = bean.getCalendar();
                if (calendar != null) {
                    int monthOrder = calendar.get(Calendar.WEEK_OF_MONTH);
                    CalendarWeekView weekView = null;
                    for (int i = 0; i < calendarWeekViewList.size(); i++) {
                        if (i != monthOrder) {
                            weekView = calendarWeekViewList.get(i);
                            break;
                        }
                    }
                    if (weekView != null) {
                        weekView.performClick();
                    }
                }
            }
        }

    }

    /**
     * 折叠月视图
     *
     * @param isFold 是否折叠： true: 折叠； false： 展开
     */
    private void foldMonthView(boolean isFold) {
        if (viewMode == ViewMode.MONTH) {
            if (isFold == isFoldMothView || !isCheckedOne) {
                // 已经处于状态
                // 多选模式下不支持折叠
                return;
            }
            // 获取当前选中的最后一个日期
            int size = checkedCalendarDayViewList.size();
            if (size == 0) {
                if (calendar != null) {
                    int monthOrder = calendar.get(Calendar.WEEK_OF_MONTH);
                    for (int i = 0; i < calendarWeekViewList.size(); i++) {
                        if (i != monthOrder) {
                            CalendarWeekView weekView = calendarWeekViewList.get(i);
                            weekView.setVisibility(GONE);
                        }
                    }
//                    onSizeChanged(getWidth(), (int) mWeekViewHeight, getWidth(), getHeight());
                    layout(getLeft(), getTop(), getRight(), (int) (getLeft() + getPaddingTop() + getPaddingBottom() + mWeekViewHeight));
                }
            }
            if (size == 1) {
                CalendarDayView dayView = checkedCalendarDayViewList.get(0);
                DayViewBean bean = dayView.getDayViewBean();
                Calendar calendar = bean.getCalendar();
                if (calendar != null) {
                    int monthOrder = calendar.get(Calendar.WEEK_OF_MONTH);
                    for (int i = 0; i < calendarWeekViewList.size(); i++) {
                        if (i != monthOrder) {
                            CalendarWeekView weekView = calendarWeekViewList.get(i);
                            weekView.setVisibility(GONE);
                        }
                    }
//                    onSizeChanged(getWidth(), (int) mWeekViewHeight, getWidth(), getHeight());
                    layout(getLeft(), getTop(), getRight(), (int) (getLeft() + getPaddingTop() + getPaddingBottom() + mWeekViewHeight));
                }
            }
        }
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


    public void setCheckedOne(boolean checkedOne) {
        isCheckedOne = checkedOne;
        for (CalendarWeekView weekView : calendarWeekViewList) {
            weekView.setCheckedOne(checkedOne);
        }
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    /* ----------------------------- 监听 -------------------------------- */

    public interface CalendarMonthViewListener {
        /**
         * 日期点击
         *
         * @param monthView 月
         * @param weekView  周
         * @param dayView   日
         */
        void onDayClick(CalendarMonthView monthView, CalendarWeekView weekView, CalendarDayView dayView);
    }

    private CalendarMonthViewListener listener;

    public void setListener(CalendarMonthViewListener listener) {
        this.listener = listener;
    }

    /* ----------------------------- 监听 -------------------------------- */

}
