package com.inz.z.note_book.view.activity

import android.text.SpannableString
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.inz.z.base.util.L
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.base.view.widget.BaseNoDataView
import com.inz.z.note_book.R
import com.inz.z.note_book.view.widget.CountdownRingView
import kotlinx.android.synthetic.main.calendar_view_date.*
import java.util.*

/**
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/02/01 14:46.
 */
class TestCalendarActivity : AbsBaseActivity() {
    override fun initWindow() {}
    override fun getLayoutId(): Int {
        return R.layout.calendar_view_date
    }

    val imgArray = intArrayOf(
        R.drawable.img_phone_4,
        R.drawable.img_phone_1,
        R.drawable.img_phone_2,
        R.drawable.img_phone_3,
        R.drawable.img_phone
    )
    var position = 0

    override fun initView() {
//        LinearLayout linearLayout = findViewById(R.id.calendar_content_ll);
//        DotView dotView = new DotView(mContext);
//        linearLayout.addView(dotView);
//        BaseScrollView baseScrollView = findViewById(R.id.calendar_max_bsv);
//
//        View outView = findViewById(R.id.calendar_content_out_ss_rl);
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
//        if (outView.getParent() != null) {
//            linearLayout.removeView(outView);
//        }
//        baseScrollView.setContentView(outView);
//        val calendar = Calendar.getInstance(Locale.getDefault())
//        //        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
//        calendar[Calendar.HOUR_OF_DAY] = 18
//        calendar[Calendar.MINUTE] = 0
//        calendar[Calendar.SECOND] = 0
//        calendar[Calendar.MILLISECOND] = 0
//        val pv = findViewById<CountdownRingView>(R.id.test_calendar_countdown_pv)
//        pv.start(CountdownRingView.MODE_COUNT_TIME_FIXED, calendar.timeInMillis)
//        val iv = findViewById<ImageView>(R.id.test_calendar_image_iv)
//        Glide.with(mContext).load(imgArray[position]).into(iv)
//        iv.setOnClickListener {
//            if (position < (imgArray.size - 1)) {
//                position += 1
//            } else {
//                position = 0
//            }
//            Glide.with(mContext).load(imgArray[position]).into(iv)
//        }

        test_base_no_data_view.apply {
            setMessage(" !!!! ~~~ !!!! ")
            setTitle("---- ")
            listener = object : BaseNoDataView.BaseNoDataListener {
                override fun onRefreshButtonClick(view: View?) {
                    test_base_no_data_view.startRefresh("loading ...")
                    test_base_no_data_view.postDelayed(
                        {
                            val random = Random()

                            val imgRes = random.nextInt(imgResArray.size)
                            L.i(TAG, "-------------- $imgRes")
                            test_base_no_data_view?.stopRefresh(
                                "success - ",
                                true,
                                false,
                                SpannableString(""),
                                imgResArray[imgRes]
                            )
                        },
                        5000
                    )
                }
            }
            stopRefresh("hint", true)
        }
    }

    val imgResArray = intArrayOf(
        R.drawable.img_photo_0,
        R.drawable.img_photo_1,
        R.drawable.img_photo_2,
        R.drawable.img_photo_3
    )

    override fun initData() {}
    override fun needCheckVersion(): Boolean {
        return false
    }

    companion object {
        private const val TAG = "TestCalendarActivity"
    }
}