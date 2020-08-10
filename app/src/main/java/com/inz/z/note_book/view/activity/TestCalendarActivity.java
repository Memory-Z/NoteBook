package com.inz.z.note_book.view.activity;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.inz.z.base.util.L;
import com.inz.z.base.view.AbsBaseActivity;
import com.inz.z.base.view.widget.BaseScrollView;
import com.inz.z.base.view.widget.DotView;
import com.inz.z.base.view.widget.WaveView;
import com.inz.z.note_book.R;

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/02/01 14:46.
 */
public class TestCalendarActivity extends AbsBaseActivity {
    private static final String TAG = "TestCalendarActivity";
    @Override
    protected void initWindow() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.calendar_view_date;
    }

    @Override
    protected void initView() {
        LinearLayout linearLayout = findViewById(R.id.calendar_content_ll);
//        DotView dotView = new DotView(mContext);
//        linearLayout.addView(dotView);
        BaseScrollView baseScrollView = findViewById(R.id.calendar_max_bsv);

        View outView = findViewById(R.id.calendar_content_out_ss_rl);
//        outView.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        outView.postDelayed(
//                                new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        outView.setRefreshing(false);
//
//                                    }
//                                },
//                                500
//                        );
//                    }
//                }
//        );
//        outView.setOnTouchListener(
//                new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        L.i(TAG, "onTouch: setOnTouchListener,, " + event.getAction());
//                        return true;
//                    }
//                }
//        );
        if (outView.getParent() != null) {
            linearLayout.removeView(outView);
        }
        baseScrollView.setContentView(outView);
    }

    @Override
    protected void initData() {

    }
}
