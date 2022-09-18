package me.rocket_scientist.rsuielements

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar

class RsVertSlider : AppCompatSeekBar {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super( context!!, attrs, defStyle )
    constructor(context: Context?, attrs: AttributeSet?) : super( context!!, attrs )
}