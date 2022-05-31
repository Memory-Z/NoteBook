package com.inz.z.base.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.StringRes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2018/10/25 9:16.
 */
public class BaseTools {

    /**
     * 获取随机数
     *
     * @return 大写随机数
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().toUpperCase(Locale.CHINA);
    }

    private static DateFormat baseDateFormat;
    private static DateFormat dateFormatTime;
    private static DateFormat dateFormatYMD;
    private static DateFormat dateFormatYM;
    private static DateFormat dateFormatMD;
    private static DateFormat dateFormatY;
    private static DateFormat dateFormatHm;

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static DateFormat getBaseDateFormat() {
        if (baseDateFormat == null) {
            baseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return baseDateFormat;
    }

    public static DateFormat getDateFormatTime() {
        if (dateFormatTime == null) {
            dateFormatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
        return dateFormatTime;
    }

    public static DateFormat getDateFormatYMD() {
        if (dateFormatYMD == null) {
            dateFormatYMD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }
        return dateFormatYMD;
    }

    public static DateFormat getDateFormatYM() {
        if (dateFormatYM == null) {
            dateFormatYM = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        }
        return dateFormatYM;
    }

    public static DateFormat getDateFormatMD() {
        if (dateFormatMD == null) {
            dateFormatMD = new SimpleDateFormat("MM-dd", Locale.getDefault());
        }
        return dateFormatMD;
    }

    public static DateFormat getDateFormatY() {
        if (dateFormatY == null) {
            dateFormatY = new SimpleDateFormat("yyyy", Locale.getDefault());
        }
        return dateFormatY;
    }

    public static DateFormat getDateFormatHm() {
        if (dateFormatHm == null) {
            dateFormatHm = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }
        return dateFormatHm;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static DateFormat getDateFormat(String dateFormat, Locale locale) {
        return new SimpleDateFormat(dateFormat, locale);
    }

    /**
     * 获取 当前时间
     *
     * @return Date
     */
    public static Date getLocalDate() {
        return Calendar.getInstance(Locale.getDefault()).getTime();
    }

    /**
     * 获取当前时间字符串
     *
     * @return 时间串  yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTimeStr() {
        return getDateFormatTime().format(getLocalDate());
    }

    /**
     * 获取最近时间
     *
     * @param time 时间
     * @return 时间串
     */
    public static String getNearDateStr(long time) {
        long currTime = System.currentTimeMillis();
        int difTime = (int) ((currTime - time) / 1000);
        Locale locale = Locale.getDefault();
        if (difTime <= 0) {
            return "刚刚";
        } else if (difTime < 60) {
            // 小于 60"
            return difTime + "s 前";
        } else if (difTime < 60 * 60) {
            // 小于 60'
            return getDateFormat("MM:ss", locale).format(time);
        } else if (difTime < 24 * 60 * 60) {
            // 小于 24h
            return getDateFormatTime().format(time);
        } else {
            return getBaseDateFormat().format(time);
        }
    }

    /**
     * 获取时间长度.
     *
     * @param duration 时间段
     * @return 时间段字符串
     */
    public static String getTimeDurationStr(long duration) {
        String str = "";
        if (duration < 1000) {
            // 毫秒
            str = duration + "ms";
        } else if (duration < 60 * 1000) {
            // 秒
            int s = (int) (duration / 1000);
            long ms = (int) (duration % 1000) / 1000L;
            str = (s + ms) + "s";
        } else if (duration < 60 * 60 * 1000) {
            // 分
            int s = (int) (duration / 1000);
            int m = s / 60;
            long sm = s % 60 / 60L;
            str = (m + sm) + "min";
        } else {
            // 时
            int m = (int) (duration / 60 / 1000);
            int h = m / 60;
            long mh = m % 60 / 60L;
            str = (h + mh) + "h";
        }

        return str;
    }

    /**
     * 获取 日期 间 时间差
     *
     * @param newCalendar 新日期
     * @param oldCalendar 旧日期
     * @return 相差时间
     */
    public static int getDiffDay(Calendar newCalendar, Calendar oldCalendar) {
        newCalendar.set(Calendar.MINUTE, 0);
        newCalendar.set(Calendar.HOUR_OF_DAY, 0);
        newCalendar.set(Calendar.SECOND, 0);
        newCalendar.set(Calendar.MILLISECOND, 0);

        oldCalendar.set(Calendar.MINUTE, 0);
        oldCalendar.set(Calendar.HOUR_OF_DAY, 0);
        oldCalendar.set(Calendar.SECOND, 0);
        oldCalendar.set(Calendar.MILLISECOND, 0);

        long newTime = newCalendar.getTimeInMillis();
        long oldTime = oldCalendar.getTimeInMillis();
        long diffDay = (newTime - oldTime) / (24 * 60 * 60 * 1000);
        return (int) diffDay;
    }

    /**
     * 校验 邮箱格式是否正确
     *
     * @param string 邮箱地址
     * @return 是否正确
     */
    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        // ^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$
        //正常邮箱 /^\w+((-\w)|(\.\w))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/
        // 含有特殊 字符的 个人邮箱  和 正常邮箱
        // js: 个人邮箱     /^[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+@[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+(\.[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+)+$/
        // java：个人邮箱  [\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+@[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+\\.[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+
        // 范围 更广的 邮箱验证 "/^[^@]+@.+\\..+$/"
        String regEx1 = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        return m.matches();
    }

    /**
     * 校验 手机号是否正确
     *
     * @param mobileNumber 手机号
     * @return 是否正确
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
        Matcher matcher = regex.matcher(mobileNumber);
        return matcher.matches();
    }


    /**
     * 将 dp 转换成 px
     *
     * @return px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    /**
     * px转换dip
     */
    public static int px2dip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * px转换sp
     */
    public static int px2sp(Context context, int pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp转换px
     */
    public static int sp2px(Context context, int spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static Toast toast;

    public static void showShortBottomToast(Context context, String content) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showShortBottomToast(Context context, @StringRes int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void showShortCenterToast(Context context, String content) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showShortCenterToast(Context context, @StringRes int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
