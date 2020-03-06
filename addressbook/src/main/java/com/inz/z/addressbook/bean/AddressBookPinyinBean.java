package com.inz.z.addressbook.bean;

/**
 * 通讯录 拼音对象
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/3/4 12:38.
 */
public class AddressBookPinyinBean extends PinyinItemBean<AddressBookBean> {

    /**
     * 排序号
     */
    private int position = -1;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
