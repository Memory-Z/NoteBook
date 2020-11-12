package com.inz.slide_table.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.inz.slide_table.R;
import com.inz.slide_table.adapter.SlideRowTitleRvAdapter;
import com.inz.slide_table.adapter.SlideRowTitleRvViewHolder;
import com.inz.slide_table.adapter.SlideRvAdapter;
import com.inz.slide_table.adapter.SlideRvViewHolder;
import com.inz.slide_table.bean.SlideTableBean;

/**
 * 滑动表格
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/11/03 10:44.
 */
public class SlideTableView extends ConstraintLayout {
    private static final String TAG = "SlideTableView";
    private static final float DEFAULT_ROW_TITLE_WIDTH = 48F;

    private View mView;
    private Context mContext;

    // 界面 元素

    private RecyclerView contentLeftRv;
    private RecyclerView contentRightRv;
    private RecyclerView headerHeaderRv;
    private SwipeRefreshLayout contentSrl;
    private TextView titleTv;

    /**
     * 滑动监听
     */
    private SlideTableViewListener slideTableViewListener;
    /**
     * 表格名称
     */
    private String tableTitle = "";
    private int rowTitleLayoutWidth = 0;

    public SlideTableView(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SlideTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideTableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initViewStyle(attrs);
        initView();
    }

    @SuppressLint("RestrictedApi")
    private void initViewStyle(@Nullable AttributeSet attrs) {
        TintTypedArray array = TintTypedArray.obtainStyledAttributes(mContext, attrs, R.styleable.SlideTableView, 0, 0);
        String tableName = array.getString(R.styleable.SlideTableView_slide_table_name);
        if (!TextUtils.isEmpty(tableName)) {
            setTableTitle(tableName);
        }
        boolean canRefresh = array.getBoolean(R.styleable.SlideTableView_slide_table_can_refresh, true);
        setCanRefresh(canRefresh);

        float width = array.getDimension(R.styleable.SlideTableView_slide_table_row_title_width, DEFAULT_ROW_TITLE_WIDTH);
        rowTitleLayoutWidth = (int) width;
        array.recycle();
    }

    private void initView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.slide_table_view, this, true);
        }
        contentLeftRv = mView.findViewById(R.id.slide_hor_item_title_rv);
        contentRightRv = mView.findViewById(R.id.slide_content_data_rv);
        headerHeaderRv = mView.findViewById(R.id.slide_ver_header_title_rv);
        MyHorizontalScrollView headerMhsv = mView.findViewById(R.id.slide_ver_header_title_mhsv);
        MyHorizontalScrollView contentMhsv = mView.findViewById(R.id.slide_content_mhsv);
        headerMhsv.setLinkedScorllView(contentMhsv);
        contentMhsv.setLinkedScorllView(headerMhsv);
        contentSrl = mView.findViewById(R.id.slide_content_srl);
        contentSrl.setOnRefreshListener(
                () -> {
                    if (slideTableViewListener != null) {
                        slideTableViewListener.onSwipeRefresh();
                    } else {
                        refreshFinish();
                    }
                }
        );
        titleTv = mView.findViewById(R.id.slide_title_tv);
        titleTv.setWidth(rowTitleLayoutWidth);
        initViewParams(mView);
    }

    private void initViewParams(@NonNull View mView) {


        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(mContext);
        verticalLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contentRightRv.setLayoutManager(verticalLayoutManager);
        contentRightRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (contentLeftRv != null && recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    contentLeftRv.scrollBy(dx, dy);
                }
            }
        });
        LinearLayoutManager leftVerticalLayoutManager = new LinearLayoutManager(mContext);
        leftVerticalLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contentLeftRv.setLayoutManager(leftVerticalLayoutManager);
        contentLeftRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (contentRightRv != null && recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    contentRightRv.scrollBy(dx, dy);
                }
            }
        });

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mContext);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        headerHeaderRv.setLayoutManager(horizontalLayoutManager);

    }

    ///////////////////////////////////////////////////////////////////////////
    // OPEN
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 滑动表格监听
     */
    public interface SlideTableViewListener {

        void onSwipeRefresh();
    }

    public void setSlideTableViewListener(SlideTableViewListener slideTableViewListener) {
        this.slideTableViewListener = slideTableViewListener;
    }

    /**
     * 设置是否可以刷新
     *
     * @param canRefresh 是否可以刷新
     */
    public void setCanRefresh(boolean canRefresh) {
        if (contentSrl != null) {
            contentSrl.setRefreshing(false);
            contentSrl.setEnabled(canRefresh);
        }
    }

    /**
     * 刷新完成
     */
    public void refreshFinish() {
        if (contentSrl != null) {
            contentSrl.setRefreshing(false);
        }
    }

    /**
     * 设置表格标题
     *
     * @param tableTitle 标题
     */
    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
        if (titleTv != null) {
            titleTv.setText(tableTitle);
        }
    }

    public <ADAPTER extends SlideRvAdapter<? extends SlideTableBean<?>, ? extends SlideRvViewHolder>> void setRowHeaderAdapter(ADAPTER adapter) {
        if (headerHeaderRv != null) {
            headerHeaderRv.setAdapter(adapter);
        }
    }

    public <ADAPTER extends SlideRvAdapter<? extends SlideTableBean<?>, ? extends SlideRvViewHolder>> void setRowContentAdapter(ADAPTER adapter) {
        if (contentRightRv != null) {
            contentRightRv.setAdapter(adapter);
        }
    }

    public <ADAPTER extends SlideRowTitleRvAdapter<? extends SlideTableBean<?>, ? extends SlideRowTitleRvViewHolder>> void setRowTitleRvAdapter(ADAPTER adapter) {
        if (contentLeftRv != null) {
            contentLeftRv.setAdapter(adapter);
        }
    }
}
