package com.inz.z.addressbook.bean;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inz.z.addressbook.BuildConfig;
import com.inz.z.addressbook.tool.PinyinToolHelper;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.io.Serializable;

/**
 * 拼音 转换 实体
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/2/25 13:10.
 */
public abstract class AbsPinyinBean implements Serializable {

    private static final String NUMBER_TEMPLATE = "*";
    private static final String UNKNOWN_TEMPLATE = "#";


    /**
     * 拼音首字母
     */
    private String pinyinFirstChar = "";

    /**
     * 获取中文拼音
     *
     * @return 中文拼音
     */
    private String getChineseTranPinyin(String str) {
        if (!"".equals(str)) {
            PinyinToolHelper toolHelper = PinyinToolHelper.getInstance();
            return toolHelper.getChinesePinyin(str);
        }
        return "";
    }

    /**
     * 获取需要转换的中文字符
     *
     * @return 中文字符
     */
    @NonNull
    protected String getChineseStr() {
        return "";
    }

    /**
     * 获取第一个英文字母
     *
     * @return 英文首字母
     */
    @NonNull
    private String getFirstChar() {
        String str = getChineseStr();
        if (!"".equals(str)) {
            return str.substring(0, 1);
        }
        return "";
    }

    /**
     * 获取拼音首字母
     *
     * @return 首字母
     */
    @NonNull
    public String getPinyinFirstChar() {
        String pinyin = getChineseTranPinyin(getFirstChar());
        return getFirstTemplate(pinyin);
    }

    /**
     * 获取拼音模板
     *
     * @param pinyin 拼音
     * @return 对应的第一个字母
     */
    private String getFirstTemplate(String pinyin) {
        if (!"".equals(pinyin)) {
            pinyinFirstChar = pinyin.substring(0, 1);
            if ("[".equals(pinyinFirstChar)) {
                pinyinFirstChar = pinyin.substring(1, 2);
            }
            try {
                Integer.parseInt(pinyinFirstChar);
                return NUMBER_TEMPLATE;
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG) {
                    Log.e("PinyinBean", "getPinyinFirstChar: ", e);
                }
            }
            return pinyinFirstChar.toUpperCase();
        }
        return UNKNOWN_TEMPLATE;
    }
}
