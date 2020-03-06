package com.inz.z.calendar_view;

import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/01/31 15:42.
 */
public class Tools {
    /**
     * 获取是否 为 暗黑 模式
     *
     * @param mContext 上下文
     * @return 是否为暗黑模式
     */
    public static boolean isDarkMode(@NonNull Context mContext) {
        int mode = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * sp转换px
     */
    public static int sp2px(@NonNull Context context, int spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
