package com.brins.calendar.custom

import android.content.Context
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.brins.calendar.R

class RoundConstraintLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null ) : ConstraintLayout(context, attrs) {

    private var mMaskPaint: Paint = Paint()
    private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private var mMaskBitmap: Bitmap? = null
    private val mLayerPaint = Paint()
    private val ROUND_RADIUS_DEFAULT = 4
    private val mRadius: Int

    init {
        setWillNotDraw(false)
        mMaskPaint.isAntiAlias = true
        mMaskPaint.isFilterBitmap = true
        mMaskPaint.color = -0x1
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundConstraintLayout)
        mRadius = a.getDimensionPixelSize(R.styleable.RoundConstraintLayout_round_corner, ROUND_RADIUS_DEFAULT)
        a.recycle()
    }

    private fun updateMask() {
        if (mMaskBitmap != null && !mMaskBitmap?.isRecycled!!) {
            mMaskBitmap!!.recycle()
            mMaskBitmap = null
        }
        try {
            mMaskBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888)
        } catch (e: Throwable) {

        }

        if (mMaskBitmap != null) {
            val canvas = Canvas(mMaskBitmap)
            canvas.drawRoundRect(RectF(0f, 0f, mMaskBitmap!!.width.toFloat(),
                    mMaskBitmap!!.height.toFloat()), mRadius.toFloat(), mRadius.toFloat(), mMaskPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMask()
    }

    override fun draw(canvas: Canvas) {
        if (mMaskBitmap != null){
            val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(),
                    mLayerPaint)
            super.draw(canvas)
            mMaskPaint.xfermode = mXfermode
            canvas.drawBitmap(mMaskBitmap, 0f, 0f, mMaskPaint)
            mMaskPaint.xfermode = null
            canvas.restoreToCount(sc)
        }else{
            super.draw(canvas)
        }
    }
}