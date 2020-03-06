package com.inz.z.addressbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inz.z.addressbook.R;
import com.inz.z.addressbook.bean.AddressBookBean;
import com.inz.z.addressbook.bean.AddressBookPinyinBean;
import com.inz.z.addressbook.bean.PinyinItemBean;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录 布局适配器
 * Create by inz
 *
 * @author Administrator
 * @version 1.0.0
 * Create by 2020/3/4 10:09.
 */
public class AddressBookRvAdapter extends RecyclerView.Adapter<AddressBookRvAdapter.AddressBookItemRvViewHolder> {
    private static final String TAG = "AddressBookRvAdapter";

    public enum ViewType {
        BASE,
        HEADER,
        DETAIL
    }

    private Context mContext;
    /**
     * 数据
     */
    private List<AddressBookPinyinBean> dataList;

    private ViewType viewType;
    private LayoutInflater layoutInflater;

    public AddressBookRvAdapter(Context mContext, List<AddressBookPinyinBean> dataList) {
        this(mContext, dataList, ViewType.BASE);
    }

    public AddressBookRvAdapter(Context mContext, List<AddressBookPinyinBean> dataList, ViewType viewType) {
        this.mContext = mContext;
        this.dataList = dataList;
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        this.viewType = viewType;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {

        return ViewType.BASE.ordinal();
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @NonNull
    @Override
    public AddressBookItemRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ViewType.BASE.ordinal()) {
            View view = layoutInflater.inflate(R.layout.item_address_book, parent, false);
            return new AddressBookDataItemRvViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressBookItemRvViewHolder viewHolder, int position) {
        if (viewHolder instanceof AddressBookDataItemRvViewHolder) {
            AddressBookDataItemRvViewHolder holder = (AddressBookDataItemRvViewHolder) viewHolder;
            AddressBookPinyinBean bean = dataList.get(position);

            String shortName = bean.getData().getSortStr();
            holder.centerNameTv.setText(shortName);
            holder.leftShortTv.setText(shortName);
            holder.centerNameTv.setText(bean.getData().getUserName());
            String pinyin = bean.getPinyinFirstChar();
        }

    }

    /**
     * 通讯录 项 (标准)
     */
    static class AddressBookItemRvViewHolder extends RecyclerView.ViewHolder {
        AddressBookItemRvViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * 通讯录 数据项 # R.layout.item_address_book
     */
    public class AddressBookDataItemRvViewHolder extends AddressBookItemRvViewHolder implements View.OnClickListener {

        private ImageView leftIv;
        private TextView leftShortTv;
        private TextView centerNameTv;
        private TextView centerHintTv;

        AddressBookDataItemRvViewHolder(@NonNull View itemView) {
            super(itemView);
            leftIv = itemView.findViewById(R.id.item_ab_left_iv);
            leftShortTv = itemView.findViewById(R.id.item_ab_left_short_name_tv);
            centerNameTv = itemView.findViewById(R.id.item_ab_center_name_tv);
            centerHintTv = itemView.findViewById(R.id.item_ab_center_name_hint_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                listener.onDataItemClick(v, position);
            }

        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外接口
    ///////////////////////////////////////////////////////////////////////////

    public void replaceData(List<? extends AddressBookPinyinBean> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public interface AddressBookRvAdapterListener {
        /**
         * 数据项带年纪事件
         *
         * @param v        视图
         * @param position 位
         */
        void onDataItemClick(View v, int position);
    }

    private AddressBookRvAdapterListener listener;

    public void setListener(AddressBookRvAdapterListener listener) {
        this.listener = listener;
    }
}
