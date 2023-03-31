package cn.quibbler.pickview

import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.View
import cn.quibbler.pickview.util.sp2px
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture

class LoopView : View {

    companion object {
        private val TAG = LoopView::class.simpleName ?: "TAG_LoopView"

        const val MSG_INVALIDATE = 1000
        const val MSG_SCROLL_LOOP = 2000
        const val MSG_SELECTED_ITEM = 3000

    }

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var mScheduledFuture: ScheduledFuture<*>? = null

    private var mTotalScrollY = 0
    private var mLoopListener: LoopScrollListener? = null
    private var mGestureDetector: GestureDetector? = null
    private var mSelectedItem = 0
    private val mOnGestureListener: GestureDetector.SimpleOnGestureListener = LoopViewGestureListener()
    private var mContext: Context? = null

    private val mTopBottomTextPaint: Paint = Paint() //paint that draw top and bottom text
    private val mCenterTextPaint: Paint = Paint() // paint that draw center text
    private val mCenterLinePaint: Paint = Paint() // paint that draw line besides center text

    private var mDataList: Array<*>? = null

    private var mTextSize = 0
    private var mMaxTextWidth = 0
    private var mMaxTextHeight = 0
    private var mTopBottomTextColor = 0
    private var mCenterTextColor = 0
    private var mCenterLineColor = 0

    private var lineSpacingMultiplier = 0f

    private var mCanLoop = false

    private var mTopLineY = 0
    private var mBottomLineY = 0
    private var mCurrentIndex = 0
    private var mInitPosition = 0
    private var mPaddingLeftRight = 0
    private var mPaddingTopBottom = 0

    private var mItemHeight = 0f

    private var mDrawItemsCount = 0
    private var mCircularDiameter = 0
    private var mWidgetHeight = 0
    private var mCircularRadius = 0
    private var mWidgetWidth = 0

    public val mHandler = Handler(Looper.getMainLooper(), object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                MSG_INVALIDATE -> {
                    invalidate()
                }
                MSG_SCROLL_LOOP -> {
                    startSmoothScrollTo()
                }
                MSG_SELECTED_ITEM -> {
                    itemSelected()
                }
                else -> {
                    //empty
                }
            }
            return false
        }
    })

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    private fun initView(context: Context?, attrs: AttributeSet?) {
        val array = context?.obtainStyledAttributes(attrs, R.styleable.LoopView)
        array?.let {
            mTopBottomTextColor = it.getColor(R.styleable.LoopView_topBottomTextColor, 0xffafafaf.toInt());
            mCenterTextColor = it.getColor(R.styleable.LoopView_centerTextColor, 0xff313131.toInt());
            mCenterLineColor = it.getColor(R.styleable.LoopView_lineColor, 0xffc5c5c5.toInt());
            mCanLoop = it.getBoolean(R.styleable.LoopView_canLoop, true);
            mInitPosition = it.getInt(R.styleable.LoopView_initPosition, -1);
            mTextSize = it.getDimensionPixelSize(R.styleable.LoopView_textSize, sp2px(context, 16f));
            mDrawItemsCount = it.getInt(R.styleable.LoopView_drawItemCount, 7);
        }
        array?.recycle()

        lineSpacingMultiplier = 2f

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        mGestureDetector = GestureDetector(context, mOnGestureListener)
        mGestureDetector?.setIsLongpressEnabled(false)
    }

}