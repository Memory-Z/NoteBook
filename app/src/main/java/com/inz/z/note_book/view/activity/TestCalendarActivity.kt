package com.inz.z.note_book.view.activity

import android.content.Intent
import com.inz.slide_table.BaseSlideRowRvAdapter
import com.inz.slide_table.BaseSlideRvAdapter
import com.inz.slide_table.BaseSlideTableBean
import com.inz.slide_table.Gold
import com.inz.z.base.view.AbsBaseActivity
import com.inz.z.note_book.R
import kotlinx.android.synthetic.main.calendar_view_date.*
import java.math.BigDecimal
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

    var slideRvAdapter: BaseSlideRvAdapter? = null
    var columnSlideRvAdapter: BaseSlideRvAdapter? = null
    var rowTitleRvAdapter: BaseSlideRowRvAdapter? = null


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
//
//        test_base_no_data_view.apply {
//            setMessage(" !!!! ~~~ !!!! ")
//            setTitle("---- ")
//            listener = object : BaseNoDataView.BaseNoDataListener {
//                override fun onRefreshButtonClick(view: View?) {
//                    test_base_no_data_view.startRefresh("loading ...")
//                    test_base_no_data_view.postDelayed(
//                        {
//                            refreshNoDataViewImage()
//                        },
//                        5000
//                    )
//                }
//            }
//            stopRefresh("hint", true)
//        }

        slideRvAdapter = BaseSlideRvAdapter(mContext)
        columnSlideRvAdapter = BaseSlideRvAdapter(mContext)
        rowTitleRvAdapter = BaseSlideRowRvAdapter(mContext, slideRvAdapter)

        calendar_content_stv?.apply {
            this.setRowContentAdapter(slideRvAdapter)
            this.setRowHeaderAdapter(columnSlideRvAdapter)
            this.setRowTitleRvAdapter(rowTitleRvAdapter)
            this.setTableTitle("TABLE - TABLE ")
        }

    }

//    val imgResArray = intArrayOf(
//        R.drawable.img_photo_0,
//        R.drawable.img_photo_1,
//        R.drawable.img_photo_2,
//        R.drawable.img_photo_3
//    )

    override fun initData() {
////        xmlFileReader()
//        val dcimFile = getExternalFilesDir(Environment.DIRECTORY_DCIM)
//        val picFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val dirFilePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//        L.i(TAG, "---------------- $dcimFile --- $picFile -- $dirFilePath  ")

        val titleList = ArrayList<String>()
        for (index in 0..5) {
            titleList.add("INDEX ---- :$index")
        }
        initViewData()
//        calendar_sriv.setItemList(titleList)
//        val itemTextViewSet = SlideRowItemView.ItemTextViewSet.Builder(mContext)
//            .setTextBackground(
//                ContextCompat.getDrawable(mContext, R.color.card_four_color),
//                ContextCompat.getDrawable(mContext, R.color.card_main_color),
//                ContextCompat.getDrawable(mContext, R.color.card_inverse_four_color)
//            )
//            .setTextColorRes(R.color.colorAccent)
//            .setItemWidthDp(64, 48)
////            .setTextMargin(
////                BaseTools.dp2px(mContext, 16F),
////                BaseTools.dp2px(mContext, 0F),
////                BaseTools.dp2px(mContext, 0F),
////                BaseTools.dp2px(mContext, 0F)
////            )
////            .setTextPadding(
////                BaseTools.sp2px(mContext, 4),
////                BaseTools.sp2px(mContext, 0),
////                BaseTools.sp2px(mContext, 4),
////                BaseTools.sp2px(mContext, 0)
////            )
//            .build()
//        calendar_sriv.setItemTextViewSet(itemTextViewSet)
    }

    override fun needCheckVersion(): Boolean {
        return false
    }

    companion object {
        private const val TAG = "TestCalendarActivity"
    }

//    private fun xmlFileReader() {
//
//        val xmlResourceParser = mContext.resources.getXml(R.xml.file_type_header);
//        val list = XmlFileUtils.getXmlValueDataList(
//            xmlResourceParser,
//            FileTypeHeaderBean.HEADER_TAG,
//            FileTypeHeaderBean::class.java
//        )
//        L.i(TAG, " --------------- > $list")
////        try {
////            var eventType = xmlResourceParser.eventType
////            while (eventType != XmlResourceParser.END_DOCUMENT) {
////                when (eventType) {
////                    XmlResourceParser.START_TAG -> {
////                        val count = xmlResourceParser.attributeCount
////                        val name = xmlResourceParser.name
////                        if (FileTypeHeaderBean.HEADER_TAG.equals(name)) {
////                            val fileTypeHeaderBean = FileTypeHeaderBean()
////                            fileTypeHeaderBean.tagName = name
////                            val fields = fileTypeHeaderBean.javaClass.declaredFields
////                            val nameList = mutableListOf<String>()
////                            for (index in 0..count-1) {
////                                nameList.add(xmlResourceParser.getAttributeName(index))
////                            }
////                            for (field in fields) {
////                                val n = field.name
////                                field.isAccessible = true
////                                var xmlValue = ""
////
////                                nameList.forEachIndexed { index, s ->
////                                    if (s.equals(n)) {
////                                        xmlValue = xmlResourceParser.getAttributeValue(index) ?: ""
////                                        return@forEachIndexed
////                                    }
////                                }
////                                if (!TextUtils.isEmpty(xmlValue)) {
////                                    field.set(fileTypeHeaderBean, xmlValue)
////                                }
////                            }
////                            L.i(
////                                TAG,
////                                "Start_ tag ${xmlResourceParser.name}  -- ${count} ---- $fileTypeHeaderBean"
////                            )
////                        }
////                    }
////                    XmlResourceParser.END_TAG -> {
////                        L.i(TAG, "End_ tag")
////                    }
////                    XmlResourceParser.START_DOCUMENT -> {
////                        L.i(TAG, "start_ document. ")
////                    }
////                    XmlResourceParser.TEXT -> {
////                        L.i(TAG, "text: ${xmlResourceParser.text}")
////                    }
////                }
////                eventType = xmlResourceParser.next()
////            }
////        } catch (e: Exception) {
////            L.e(TAG, "", e)
////        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_OK -> {

            }
            1000 -> {
                val bundle = data?.extras
//                bundle?.apply {
//                    val fileBeanList: ArrayList<BaseChooseFileBean>? =
//                        this.getParcelableArrayList(ChooseFileActivity.CHOOSE_FILE_LIST_TAG)
//
//                    val chooseFileSize = this.getInt(ChooseFileActivity.CHOOSE_FILE_SIZE_TAG, 0)
//                    L.i(TAG, "-----------> $fileBeanList ---------$chooseFileSize")
//                }
            }
            else -> {

            }

        }
    }


//    private fun refreshNoDataViewImage() {
//        val random = Random()
//        val imgRes = random.nextInt(imgResArray.size)
//        L.i(TAG, "-------------- $imgRes")
//        test_base_no_data_view?.stopRefresh(
//            "success - ",
//            canRetry = true,
//            useSpannable = false,
//            spannableStr = SpannableString(""),
//            hintImage = imgResArray[imgRes]
//        )
////
////                            val option = BitmapFactory.Options()
////                            option.inScaled = false
////                            option.inPreferredConfig = Bitmap.Config.ARGB_8888
////                            val drawableImg =
////                                BitmapFactory.decodeResource(
////                                    mContext.resources,
////                                    R.drawable.img_icon_0,
////                                    option
////                                ).copy(Bitmap.Config.ARGB_8888, false)
////
////
////                            val drawableImg2 =
////                                BitmapFactory.decodeResource(
////                                    mContext.resources,
////                                    R.drawable.img_icon_1,
////                                    option
////                                ).copy(Bitmap.Config.ARGB_8888, false)
////                            if (drawableImg != null && drawableImg2 != null) {
////                                drawableImg.density = mContext.resources.displayMetrics.densityDpi
////                                drawableImg2.density = mContext.resources.displayMetrics.densityDpi
////                                val d = ImageUtils.mergeBitmap(
////                                    drawableImg,
////                                    drawableImg2,
////                                    Constants.BitmapMergeType.VERTICAL,
////                                    true,
////                                    1600,
////                                    0
////                                )
////                                L.i(TAG, "---------------- < ${d.width} + ${d.height} ")
////                            }
//        if (imgRes == 0) {
//            ChooseFileActivity.gotoChooseFileActivity(
//                this@TestCalendarActivity,
//                1000,
//                ChooseFileActivity.MODE_TABLE,
//                Constants.FileShowType.SHOW_TYPE_IMAGE,
//                2
//            )
//        } else {
//            test_base_no_data_view?.postDelayed(
//                {
//                    refreshNoDataViewImage()
//                },
//                2000
//            )
//        }
//    }


    private fun initViewData() {
        val beanList = ArrayList<BaseSlideTableBean>()
        val random = Random()
        for (i in 0..100) {
            val bean = BaseSlideTableBean()
            val gold = Gold()
            val title = "Title$i"
            gold.run {
                id = "id - $i"
                name = "name - $i $title";
                count = random.nextInt(20) + 10
                sellOut = random.nextInt(20)
                price = BigDecimal.valueOf(random.nextDouble())
            }
            bean.setData(gold)
            bean.setHeaderRow(i == 0)
            beanList.add(bean)
        }
        slideRvAdapter?.setContentData(beanList)
        columnSlideRvAdapter?.setContentData(beanList.subList(0, 1))
        rowTitleRvAdapter?.refreshTitleList(beanList)

    }
}