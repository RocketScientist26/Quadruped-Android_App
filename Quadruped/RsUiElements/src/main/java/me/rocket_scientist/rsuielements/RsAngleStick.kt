package me.rocket_scientist.rsuielements

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.Math.*
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class RsAngleStick (context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    interface OnMoveListener {
        fun onMove(angle: Int, strength: Int)
    }
    fun setOnMoveListener(l: OnMoveListener?) {
        on_move_callback = l
    }

    //External
    private var button_paint: Paint? = null
    private var button_bitmap: Bitmap? = null
    private var button_size_ratio: Float = 0.25f
    private var on_move_callback: OnMoveListener? = null

    //Internal
    private var button_radius: Int = 0
    private var border_radius: Int = 0
    private var x: Int = 0
    private var y: Int = 0

    //Angle/Strength calculation
    private fun angle():Int {
        return toDegrees( atan2((y - button_radius).toDouble(), (x - button_radius).toDouble()) ).toInt()
    }
    private fun strength():Int {
        val dist = sqrt((x - button_radius).toFloat().pow(2) + (y - button_radius).toFloat().pow(2))
        return ( dist / ((border_radius - button_radius).toFloat() / 80.0f) ).toInt()
    }

    //Events
    override fun onDraw(canvas: Canvas) {
        if (button_bitmap != null) {
            canvas.drawBitmap( button_bitmap!!, (x - button_radius).toFloat(), (y - button_radius).toFloat(), button_paint )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val d: Int = min(w, h)
        button_radius = (d * button_size_ratio / 2f).toInt()
        border_radius = d - button_radius
        x = button_radius
        y = button_radius
        if (button_bitmap != null){
            button_bitmap = Bitmap.createScaledBitmap( button_bitmap!!, button_radius * 2, button_radius * 2, true )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d: Int = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(d, d)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!this.isEnabled) {
            return true
        }

        x = event.x.toInt()
        y = event.y.toInt()

        //!TBD
        val angle = atan2((y - button_radius).toDouble(), (x - button_radius).toDouble())
        val dist = sqrt(x.toFloat().pow(2) + y.toFloat().pow(2))
        if (dist > border_radius) {
            x = (border_radius * cos(angle)).toInt()
            y = (border_radius * sin(angle)).toInt()
        }

        if(x < button_radius){
            x = button_radius
        }
        if(y < button_radius){
            y = button_radius
        }

        when(event.action){
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                x = button_radius
                y = button_radius
                if (on_move_callback != null) on_move_callback!!.onMove(angle(), strength())
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (on_move_callback != null) on_move_callback!!.onMove(angle(), strength())
            }
        }

        invalidate()
        return true
    }

    init {
        val styledAttributes: TypedArray = context.theme.obtainStyledAttributes( attrs, R.styleable.RsAngleStick, 0, 0 )
        val buttonDrawable: Drawable?
        try {
            buttonDrawable = styledAttributes.getDrawable(R.styleable.RsAngleStick_RsAngleStick_ButtonImage)
            button_size_ratio = styledAttributes.getFraction( R.styleable.RsAngleStick_RsAngleStick_ButtonSizeRatio, 1, 1, 0.25f )
        } finally {
            styledAttributes.recycle()
        }

        //!TBD
        if (buttonDrawable is BitmapDrawable) {
            button_bitmap = buttonDrawable.bitmap
            button_paint = Paint()
        }
    }
}