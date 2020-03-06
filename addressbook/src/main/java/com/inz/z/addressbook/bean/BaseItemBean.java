package com.inz.z.addressbook.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * 基础单项
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/2/25 16:45.
 */
public abstract class BaseItemBean implements Serializable {
    /**
     * 需要排序的号
     */
    private String sortStr = "";

    public String getSortStr() {
        return sortStr;
    }

    public void setSortStr(String sortStr) {
        this.sortStr = sortStr;
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseItemBean{" +
                "sortStr='" + sortStr + '\'' +
                '}';
    }
}
