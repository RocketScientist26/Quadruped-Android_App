package me.rocket_scientist.quadruped;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.support.v7.widget.AppCompatSeekBar;

public class VerticalSeekbarClass extends AppCompatSeekBar {

    public VerticalSeekbarClass(Context context) {
        super(context);
    }

    public VerticalSeekbarClass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekbarClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    static public int wh;
    static public int ph;
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        float scalefactor = (float)getHeight()/((float)getHeight()+((float)wh));//1f-(float)(getWidth()/wh);
        int oldw = getWidth();
        c.scale(scalefactor, scalefactor);
        c.translate(-getHeight()-(wh/2), ((int)(oldw-(scalefactor*(float)oldw)))/1.5f);
        super.onDraw(c);
    }

    private OnSeekBarChangeListener onChangeListener;
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }

    private int lastProgress = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onChangeListener.onStartTrackingTouch(this);
                setPressed(true);
                setSelected(true);
                break;
            case MotionEvent.ACTION_MOVE:
                super.onTouchEvent(event);
                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());

                // Ensure progress stays within boundaries
                if(progress < 0) {progress = 0;}
                if(progress > getMax()) {progress = getMax();}
                setProgress(progress);  // Draw progress
                if(progress != lastProgress) {
                    // Only enact listener if the progress has actually changed
                    lastProgress = progress;
                    onChangeListener.onProgressChanged(this, progress, true);
                }

                onSizeChanged(getWidth(), getHeight() , 0, 0);
                setPressed(true);
                setSelected(true);
                break;
            case MotionEvent.ACTION_UP:
                onChangeListener.onStopTrackingTouch(this);
                setPressed(false);
                setSelected(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(event);
                setPressed(false);
                setSelected(false);
                break;
        }
        return true;
    }

    public synchronized void setProgressAndThumb(int progress) {
        setProgress(progress);
        onSizeChanged(getWidth(), getHeight() , 0, 0);
        if(progress != lastProgress) {
            // Only enact listener if the progress has actually changed
            lastProgress = progress;
            onChangeListener.onProgressChanged(this, progress, true);
        }
    }

    public synchronized void setMaximum(int maximum) {
        setMax(maximum);
    }

    public synchronized int getMaximum() {
        return getMax();
    }
}