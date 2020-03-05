package com.inz.z.addressbook.tool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;

/**
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/2/25 13:31.
 */
public class PinyinToolHelper {
    private static PinyinToolHelper toolHelper;

    private static PinyinHelper helper;
    private static HanyuPinyinOutputFormat format;

    private PinyinToolHelper() {
        format = getFormat();
    }

    public static PinyinToolHelper getInstance() {
        if (toolHelper == null) {
            synchronized (PinyinToolHelper.class) {
                toolHelper = new PinyinToolHelper();
            }
        }
        return toolHelper;
    }

    /**
     * 获取拼音格式
     *
     * @return format
     */
    private HanyuPinyinOutputFormat getFormat() {
        if (format == null) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            return format;
        }
        return format;
    }

    /**
     * 获取中文字符拼音
     *
     * @param chineseStr 中文字符
     * @return 中文拼音
     */
    @NonNull
    public String getChinesePinyin(@NonNull String chineseStr) {
        if (format != null) {
            StringBuilder sb = new StringBuilder();
            char[] chineseArray = chineseStr.toCharArray();
            try {
                for (char c : chineseArray) {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyin != null) {
                        sb.append(Arrays.toString(pinyin));
                    }
                }
                String result = sb.toString();
                if (result.length() == 0 && chineseStr.length() > 0) {
                    return chineseStr.substring(0, 1);
                }
                return result;
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
        }
        if (chineseStr.length() > 0) {
            return chineseStr.substring(0, 1);
        }
        return chineseStr;
    }

}
