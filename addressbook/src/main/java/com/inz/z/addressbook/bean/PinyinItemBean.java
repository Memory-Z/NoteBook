package com.inz.z.addressbook.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.UUID;

/**
 * 拼音项实体
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/2/25 12:10.
 */
public abstract class PinyinItemBean<T extends BaseItemBean> extends AbsPinyinBean implements Serializable {

    public PinyinItemBean() {
        id = UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * id
     */
    private String id = "";
    /**
     * 具体数据内容
     */
    private T data;

    @NonNull
    @Override
    protected String getChineseStr() {
        return data != null ? data.getSortStr() : "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "PinyinItemBean{" +
                "id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
