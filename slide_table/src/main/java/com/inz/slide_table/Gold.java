package com.inz.slide_table;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 13:49.
 */
public class Gold implements Serializable {

    private String id = "";
    private String name = "";
    private int count = 0;
    private BigDecimal price = new BigDecimal(0);
    private int sellOut = 0;
    private String unitName = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSellOut() {
        return sellOut;
    }

    public void setSellOut(int sellOut) {
        this.sellOut = sellOut;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }


    @NonNull
    @Override
    public String toString() {
        return "Gold{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", price=" + price +
                ", sellOut=" + sellOut +
                ", unitName='" + unitName + '\'' +
                '}';
    }

    public String getPriceStr() {
        return getPrice().toString() + "￥";
    }

    public String sellNumber() {
        return getSellOut() + getUnitName();
    }

    public String allNumber() {
        return getCount() + getUnitName();
    }


}
