package com.inz.z.addressbook.widget;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * 导航栏 实体
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/03/06 13:35.
 */
public class NavItemViewBean implements Serializable {
    /**
     * 导航内容
     */
    private String tag = "";
    /**
     * 是否可点击
     */
    private boolean canClick = true;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isCanClick() {
        return canClick;
    }

    public void setCanClick(boolean canClick) {
        this.canClick = canClick;
    }

    @NonNull
    @Override
    public String toString() {
        return "NavItemViewBean{" +
                "tag='" + tag + '\'' +
                ", canClick=" + canClick +
                '}';
    }
}
