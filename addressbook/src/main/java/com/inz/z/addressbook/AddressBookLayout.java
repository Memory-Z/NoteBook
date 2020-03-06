package com.inz.z.addressbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.TintTypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.z.addressbook.adapter.AddressBookRvAdapter;
import com.inz.z.addressbook.bean.AddressBookBean;
import com.inz.z.addressbook.bean.AddressBookPinyinBean;
import com.inz.z.addressbook.bean.PinyinItemBean;
import com.inz.z.addressbook.widget.AddressNavView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 通讯录
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/2/25 11:25.
 */
public class AddressBookLayout extends RelativeLayout {
    private static final String TAG = "AddressBookLayout";

    private Context mContext;
    private View mView;

    private RecyclerView addressBookRv;
    private RecyclerView.LayoutManager layoutManager;
    private AddressBookRvAdapter addressBookRvAdapter;
    private AddressBookItemDecoration addItemDecoration;
    /**
     * 数据类型
     */
    private List<AddressBookPinyinBean> itemBeanList = new ArrayList<>();
    /**
     * 右侧导航栏
     */
    private AddressNavView addressNavView;

    public AddressBookLayout(Context context) {
        super(context);
        this.mContext = context;
        initView();
        initData();
    }

    public AddressBookLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddressBookLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initStyle(attrs);
        initView();
        initData();
    }

    @SuppressLint("RestrictedApi")
    private void initStyle(AttributeSet attrs) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.AddressBookLayout, 0, 0);
        array.recycle();
    }

    private void initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.address_book_layout, this, true);
            addressBookRv = mView.findViewById(R.id.address_book_layout_rv);

            layoutManager = new LinearLayoutManager(mContext);
            addressBookRv.setLayoutManager(layoutManager);
            addressNavView = mView.findViewById(R.id.address_book_layout_anv);
            addressNavView.setLayoutManager(layoutManager);
            addressBookRvAdapter = new AddressBookRvAdapter(mContext, null);
            addressBookRv.setAdapter(addressBookRvAdapter);
            addItemDecoration = new AddressBookItemDecoration(mContext, itemBeanList);
            addressBookRv.addItemDecoration(addItemDecoration);
        }
    }

    private void initData() {
        initAddressBookData();
    }

    private static String[] nameArray =
            {"未必是", "是多久啊是你", "爱说大话", "啊", "埃德加hi举报", "123撒旦", "请问安全", "请问a", "我去饿撒的", "请问问题", "请问木器", "请问目的", "请问插件", "请问erq", "请问房间", "没看快女", "不能次哦就", "join我", "吗，看v骄傲", "片假名即可v出门", "立刻就哦i阿森纳的可能", "可能就看你喜欢", "咯咯咯i比", "十多年尽可能", "i我借你"};

    private void initAddressBookData() {
        for (int i = 0; i < 20; i++) {
            AddressBookPinyinBean bookPinyinBean = new AddressBookPinyinBean();
            AddressBookBean bookBean = new AddressBookBean();
            int length = nameArray.length;
            Random random = new Random();
            int p = random.nextInt(length);
            String text = nameArray[p];
            Log.i(TAG, "initAddressBookData: Text = " + text);
            bookBean.setUserName(text);
            bookBean.setSpecialPhone("座机电话号码 --- > " + i);
            bookBean.setTellPhone("移动电话 ---> " + i);
            bookPinyinBean.setData(bookBean);
            itemBeanList.add(bookPinyinBean);
            Log.i(TAG, "initAddressBookData: TAG = " + bookPinyinBean.getPinyinFirstChar());
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            itemBeanList.sort(new AddressPinyinSort());
//        } else {
        AddressBookPinyinBean[] beans = new AddressBookPinyinBean[itemBeanList.size()];
        itemBeanList.toArray(beans);
        Arrays.sort(beans, new AddressPinyinSort());
        itemBeanList = Arrays.asList(beans);
//        }
        addressBookRvAdapter.replaceData(itemBeanList);
        addItemDecoration.setDataList(itemBeanList);

    }

    /**
     * 拼音排序
     */
    private static class AddressPinyinSort implements Comparator<AddressBookPinyinBean> {
        @Override
        public int compare(AddressBookPinyinBean o1, AddressBookPinyinBean o2) {
            String o1f = o1.getPinyinFirstChar();
            String o2f = o2.getPinyinFirstChar();
            if (o1f.equals(o2f)) {
                String o1n = o1.getData().getSortStr();
                String o2n = o2.getData().getSortStr();
                Collator collator = Collator.getInstance(Locale.CHINESE);
                collator.compare(o1n, o2n);
                Log.i(TAG, "compare: >>>>>>>> " + o1n + " - " + o2n + " : " + collator.compare(o1n, o2n))
                ;
                return collator.compare(o1n, o2n);
            } else {
                return o1f.compareTo(o2f);
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // 对外接口
    ///////////////////////////////////////////////////////////////////////////


    public List<AddressBookPinyinBean> getItemBeanList() {
        return itemBeanList;
    }

    public void setItemBeanList(List<AddressBookPinyinBean> itemBeanList) {
        this.itemBeanList = itemBeanList;
    }
}
