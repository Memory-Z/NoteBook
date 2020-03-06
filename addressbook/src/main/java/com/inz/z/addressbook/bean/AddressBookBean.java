package com.inz.z.addressbook.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * 通讯录
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/2/25 16:56.
 */
public class AddressBookBean extends BaseItemBean implements Serializable {

    /**
     * 用户名
     */
    private String userName = "";
    /**
     * 电话号码: 主
     */
    private String tellPhone = "";
    /**
     * 座机号码： 主
     */
    private String specialPhone = "";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        setSortStr(this.userName);
    }

    public String getTellPhone() {
        return tellPhone;
    }

    public void setTellPhone(String tellPhone) {
        this.tellPhone = tellPhone;
    }

    public String getSpecialPhone() {
        return specialPhone;
    }

    public void setSpecialPhone(String specialPhone) {
        this.specialPhone = specialPhone;
    }

    @NonNull
    @Override
    public String toString() {
        return "AddressBookBean{" +
                "userName='" + userName + '\'' +
                ", tellPhone='" + tellPhone + '\'' +
                ", specialPhone='" + specialPhone + '\'' +
                '}';
    }
}
