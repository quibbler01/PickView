package cn.quibbler.pickview

import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.View
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
    private var mOnGestureListener: GestureDetector.SimpleOnGestureListener? = null
    private var mContext: Context? = null

    private var mTopBottomTextPaint: Paint? = null //paint that draw top and bottom text
    private var mCenterTextPaint: Paint? = null // paint that draw center text
    private var mCenterLinePaint: Paint? = null // paint that draw line besides center text

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

    public val mHandler = Handler(object : Handler.Callback {
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

}