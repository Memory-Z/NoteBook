package com.inz.slide_table;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inz.slide_table.bean.SlideTableBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 13:49.
 */
public class BaseSlideTableBean extends SlideTableBean<Gold> {

    public BaseSlideTableBean() {
    }

    @Nullable
    @Override
    public String getRowTitle() {
        Gold gold = getData();
        return gold == null ? "" : gold.getName();
    }

    @NonNull
    @Override
    public List<String> toDataColumnList() {
        List<String> dataList = new ArrayList<>();
        Gold gold = getData();
        if (gold != null) {
            dataList.add(gold.getId());
            dataList.add(gold.getName());
            dataList.add(gold.getPriceStr());
            dataList.add(gold.sellNumber());
            dataList.add(gold.allNumber());
            dataList.add(gold.getUnitName());
        }
        return dataList;
    }

    @NonNull
    @Override
    public List<String> toDataHeaderList() {
        List<String> titleList=  new ArrayList<>();
        titleList.add("ID");
        titleList.add("NAME");
        titleList.add("PRICE");
        titleList.add("SELL");
        titleList.add("COUNT");
        titleList.add("UNIT");
        return titleList;
    }
}
