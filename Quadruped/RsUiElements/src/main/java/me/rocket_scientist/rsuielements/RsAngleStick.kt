package me.rocket_scientist.rsuielements

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class RsAngleStick @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs), Runnable {
    interface OnMoveListener {
        fun onMove(angle: Int, strength: Int)
    }
    interface OnMultipleLongPressListener {
        fun onMultipleLongPress()
    }

    // DRAWING
    private val mPaintCircleButton: Paint
    private val mPaintCircleBorder: Paint
    private val mPaintBackground: Paint
    private var mPaintBitmapButton: Paint? = null
    private var mButtonBitmap: Bitmap? = null

    private var mButtonSizeRatio: Float = 0f
    private var mBackgroundSizeRatio: Float = 0f

    // COORDINATE
    private var mPosX: Int = 0
    private var mPosY: Int = 0
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0
    private var mFixedCenterX: Int = 0
    private var mFixedCenterY: Int = 0
    private var mFixedCenter: Boolean = false
    private var mRightAligned: Boolean = false
    var isAutoReCenterButton: Boolean = false
    var isButtonStickToBorder: Boolean = false
    private var mEnabled: Boolean = false

    // SIZE
    private var mButtonRadius: Int = 0
    private var mBorderRadius: Int = 0
    private var mBackgroundRadius: Float = 0f
    private var mCallback: OnMoveListener? = null
    private var mLoopInterval: Long = DEFAULT_LOOP_INTERVAL.toLong()
    private var mThread: Thread? = Thread(this)
    private var mOnMultipleLongPressListener: OnMultipleLongPressListener? = null
    private val mHandlerMultipleLongPress: Handler = Handler()
    private val mRunnableMultipleLongPress: Runnable
    private var mMoveTolerance: Int = 0
    var buttonDirection: Int = 0

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs) {}

    private fun initPosition() {
        // get the center of view to position circle
        if (mRightAligned == false) {
            mPosX = mButtonRadius
            mCenterX = mPosX
            mFixedCenterX = mCenterX
            mPosY = mButtonRadius
            mCenterY = mPosY
            mFixedCenterY = mCenterY
        } else {
            //TBD
            mPosX = width - mButtonRadius
            mCenterX = mPosX
            mFixedCenterX = mCenterX
            mPosY = mButtonRadius
            mCenterY = mPosY
            mFixedCenterY = mCenterY
        }
    }

    /**
     * Draw the background, the border and the button
     * @param canvas the canvas on which the shapes will be drawn
     */
    override fun onDraw(canvas: Canvas) {
        // Draw the background
        canvas.drawCircle(
            mFixedCenterX.toFloat(),
            mFixedCenterY.toFloat(),
            mBackgroundRadius,
            mPaintBackground
        )

        // Draw the circle border
        canvas.drawCircle(
            mFixedCenterX.toFloat(),
            mFixedCenterY.toFloat(),
            mBorderRadius.toFloat(),
            mPaintCircleBorder
        )

        // Draw the button from image
        if (mButtonBitmap != null) {
            canvas.drawBitmap(
                mButtonBitmap!!, (
                        (mPosX + mFixedCenterX) - mCenterX - mButtonRadius).toFloat(), (
                        (mPosY + mFixedCenterY) - mCenterY - mButtonRadius).toFloat(),
                mPaintBitmapButton
            )
        } else {
            canvas.drawCircle(
                (
                        mPosX + mFixedCenterX - mCenterX).toFloat(), (
                        mPosY + mFixedCenterY - mCenterY).toFloat(),
                mButtonRadius.toFloat(),
                mPaintCircleButton
            )
        }
    }

    /**
     * This is called during layout when the size of this view has changed.
     * Here we get the center of the view and the radius to draw all the shapes.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldW Old width of this view.
     * @param oldH Old height of this view.
     */
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        // radius based on smallest size : height OR width
        val d: Int = Math.min(w, h)
        mButtonRadius = (d / 2 * mButtonSizeRatio).toInt()
        mBorderRadius = (d / 2 * mBackgroundSizeRatio).toInt()
        mBackgroundRadius = mBorderRadius - (mPaintCircleBorder.strokeWidth / 2)
        initPosition()
        if (mButtonBitmap != null) mButtonBitmap = Bitmap.createScaledBitmap(
            mButtonBitmap!!, mButtonRadius * 2, mButtonRadius * 2, true
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // setting the measured values to resize the view to a certain width and height
        val d: Int = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec))
        setMeasuredDimension(d, d)
    }

    private fun measure(measureSpec: Int): Int {
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            // if no bounds are specified return a default size (200)
            return DEFAULT_SIZE
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            return MeasureSpec.getSize(measureSpec)
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // if disabled we don't move the
        if (!mEnabled) {
            return true
        }


        // to move the button according to the finger coordinate
        // (or limited to one axe according to direction option
        mPosY =
            if (buttonDirection < 0) mCenterY else event.y.toInt() // direction negative is horizontal axe
        mPosX =
            if (buttonDirection > 0) mCenterX else event.x.toInt() // direction positive is vertical axe
        if (event.action == MotionEvent.ACTION_UP) {

            // stop listener because the finger left the touch screen
            mThread!!.interrupt()

            // re-center the button or not (depending on settings)
            if (isAutoReCenterButton) {
                resetButtonPosition()

                // update now the last strength and angle which should be zero after resetButton
                if (mCallback != null) mCallback!!.onMove(angle, strength)
            }

            // if mAutoReCenterButton is false we will send the last strength and angle a bit
            // later only after processing new position X and Y otherwise it could be above the border limit
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mThread != null && mThread!!.isAlive) {
                mThread!!.interrupt()
            }
            mThread = Thread(this)
            mThread!!.start()
            if (mCallback != null) mCallback!!.onMove(angle, strength)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN ->                 // when the first touch occurs we update the center (if set to auto-defined center)
                if (!mFixedCenter) {
                    mCenterX = mPosX
                    mCenterY = mPosY
                }
            MotionEvent.ACTION_POINTER_DOWN -> {

                // when the second finger touch
                if (event.pointerCount == 2) {
                    mHandlerMultipleLongPress.postDelayed(
                        mRunnableMultipleLongPress,
                        (ViewConfiguration.getLongPressTimeout() * 2).toLong()
                    )
                    mMoveTolerance = MOVE_TOLERANCE
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mMoveTolerance--
                if (mMoveTolerance == 0) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {

                // when the last multiple touch is released
                if (event.pointerCount == 2) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress)
                }
            }
        }
        val abs: Double =
            (Math.sqrt(((mPosX - mCenterX) * (mPosX - mCenterX) + (mPosY - mCenterY) * (mPosY - mCenterY)).toDouble())) / 1.62f

        // (abs > mBorderRadius) means button is too far therefore we limit to border
        // (buttonStickBorder && abs != 0) means wherever is the button we stick it to the border except when abs == 0
        val xtmp1: Int = angle
        if (mRightAligned == false) {
            if ((xtmp1 > 90) && (xtmp1 < 270)) {
                mPosX = mCenterX
            }
            if ((xtmp1 > 0) && (xtmp1 < 180)) {
                mPosY = mCenterY
            }
        } else {
            //TBD
            if (((xtmp1 < 90) && (xtmp1 > 0)) || ((xtmp1 < 359) && (xtmp1 > 270))) {
                mPosX = mCenterX
            }
            if ((xtmp1 > 0) && (xtmp1 < 180)) {
                mPosY = mCenterY
            }
        }
        if (abs > mBorderRadius || (isButtonStickToBorder && abs != 0.0)) {
            mPosX = ((mPosX - mCenterX) * mBorderRadius / abs + mCenterX).toInt()
            mPosY = ((mPosY - mCenterY) * mBorderRadius / abs + mCenterY).toInt()
        }
        if (!isAutoReCenterButton) {
            // Now update the last strength and angle if not reset to center
            if (mCallback != null) mCallback!!.onMove(angle, strength)
        }


        // to force a new draw
        invalidate()
        return true
    }
    private val angle: Int
        private get() {
            val angle: Int = Math.toDegrees(
                Math.atan2(
                    (mCenterY - mPosY).toDouble(),
                    (mPosX - mCenterX).toDouble()
                )
            ).toInt()
            return if (angle < 0) angle + 360 else angle // make it as a regular counter-clock protractor
        }
    private val strength: Int
        private get() = ((100 * Math.sqrt(
            ((mPosX - mCenterX)
                    * (mPosX - mCenterX) + (mPosY - mCenterY)
                    * (mPosY - mCenterY)).toDouble()
        ) / mBorderRadius) / 2).toInt()
    fun resetButtonPosition() {
        mPosX = mCenterX
        mPosY = mCenterY
    }
    override fun isEnabled(): Boolean {
        return mEnabled
    }
    var buttonSizeRatio: Float
        get() {
            return mButtonSizeRatio
        }
        set(newRatio) {
            if ((newRatio > 0.0f) && (newRatio <= 1.0f)) {
                mButtonSizeRatio = newRatio
            }
        }
    fun getmBackgroundSizeRatio(): Float {
        return mBackgroundSizeRatio
    }

    val normalizedX: Int
        get() {
            return Math.round((mPosX - mButtonRadius) * 100.0f / (width - mButtonRadius * 2))
        }
    val normalizedY: Int
        get() {
            return Math.round((mPosY - mButtonRadius) * 100.0f / (height - mButtonRadius * 2))
        }
    fun setButtonDrawable(d: Drawable?) {
        if (d != null) {
            if (d is BitmapDrawable) {
                mButtonBitmap = d.bitmap
                if (mButtonRadius != 0) {
                    mButtonBitmap = Bitmap.createScaledBitmap(
                        mButtonBitmap!!,
                        mButtonRadius * 2,
                        mButtonRadius * 2,
                        true
                    )
                }
                if (mPaintBitmapButton != null) mPaintBitmapButton = Paint()
            }
        }
    }

    fun setButtonColor(color: Int) {
        mPaintCircleButton.color = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        mPaintCircleBorder.color = color
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        mPaintBackground.color = color
        invalidate()
    }

    fun setBorderWidth(width: Int) {
        mPaintCircleBorder.strokeWidth = width.toFloat()
        mBackgroundRadius = mBorderRadius - (width / 2.0f)
        invalidate()
    }

    fun setOnMoveListener(l: OnMoveListener?) {
        setOnMoveListener(l, DEFAULT_LOOP_INTERVAL)
    }

    fun setOnMoveListener(l: OnMoveListener?, loopInterval: Int) {
        mCallback = l
        mLoopInterval = loopInterval.toLong()
    }

    fun setOnMultiLongPressListener(l: OnMultipleLongPressListener?) {
        mOnMultipleLongPressListener = l
    }

    fun setFixedCenter(fixedCenter: Boolean) {
        if (fixedCenter) {
            initPosition()
        }
        mFixedCenter = fixedCenter
        invalidate()
    }

    override fun setEnabled(enabled: Boolean) {
        mEnabled = enabled
    }

    fun setBackgroundSizeRatio(newRatio: Float) {
        if ((newRatio > 0.0f) && (newRatio <= 1.0f)) {
            mBackgroundSizeRatio = newRatio
        }
    }

    override fun run() {
        while (!Thread.interrupted()) {
            post(object : Runnable {
                override fun run() {
                    if (mCallback != null) mCallback!!.onMove(angle, strength)
                }
            })
            try {
                Thread.sleep(mLoopInterval)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    companion object {
        private val DEFAULT_LOOP_INTERVAL: Int = 50 // in milliseconds
        private val MOVE_TOLERANCE: Int = 10
        private val DEFAULT_COLOR_BUTTON: Int = Color.BLACK
        private val DEFAULT_COLOR_BORDER: Int = Color.TRANSPARENT
        private val DEFAULT_BACKGROUND_COLOR: Int = Color.TRANSPARENT
        private val DEFAULT_SIZE: Int = 200
        private val DEFAULT_WIDTH_BORDER: Int = 3
        private val DEFAULT_FIXED_CENTER: Boolean = true
        private val DEFAULT_RIGHT_ALIGNED: Boolean = false
        private val DEFAULT_AUTO_RECENTER_BUTTON: Boolean = true
        private val DEFAULT_BUTTON_STICK_TO_BORDER: Boolean = false
        var BUTTON_DIRECTION_BOTH: Int = 0
    }

    init {
        val styledAttributes: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RsAngleStick,
            0, 0
        )
        val buttonColor: Int
        val borderColor: Int
        val backgroundColor: Int
        val borderWidth: Int
        val buttonDrawable: Drawable?
        try {
            mRightAligned = styledAttributes.getBoolean(
                R.styleable.RsAngleStick_RsAngleStick_Enabled_RightAligned,
                DEFAULT_RIGHT_ALIGNED
            )
            buttonDrawable = styledAttributes.getDrawable(R.styleable.RsAngleStick_RsAngleStick_ButtonImage)
            mEnabled = styledAttributes.getBoolean(R.styleable.RsAngleStick_RsAngleStick_Enabled, true)
            mButtonSizeRatio = styledAttributes.getFraction(
                R.styleable.RsAngleStick_RsAngleStick_ButtonSizeRatio,
                1,
                1,
                0.25f
            )
        } finally {
            styledAttributes.recycle()
        }

        mPaintCircleButton = Paint()
        mPaintCircleButton.isAntiAlias = true
        mPaintCircleButton.style = Paint.Style.FILL
        if (buttonDrawable != null) {
            if (buttonDrawable is BitmapDrawable) {
                mButtonBitmap = buttonDrawable.bitmap
                mPaintBitmapButton = Paint()
            }
        }
        mPaintCircleBorder = Paint()
        mPaintCircleBorder.isAntiAlias = true
        mPaintCircleBorder.style = Paint.Style.STROKE
        mPaintBackground = Paint()
        mPaintBackground.isAntiAlias = true
        mPaintBackground.style = Paint.Style.FILL

        mRunnableMultipleLongPress = object : Runnable {
            override fun run() {
                if (mOnMultipleLongPressListener != null) mOnMultipleLongPressListener!!.onMultipleLongPress()
            }
        }
    }
}