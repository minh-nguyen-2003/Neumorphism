package vn.minh_nguyen.vkey.library.neumorphic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes

class NeumorphismLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lightPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius = 32f
    private var isConcave = false
    private var lightDirection = LightDirection.TOP_LEFT

    enum class LightDirection { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    init {
        setWillNotDraw(false) // quan trọng để onDraw được gọi
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.withStyledAttributes(attrs, R.styleable.NeumorphismView) {
            radius = getDimension(R.styleable.NeumorphismView_cornerRadius, 32f)
            isConcave = getBoolean(R.styleable.NeumorphismView_isConcave, false)
            val dir = getInt(R.styleable.NeumorphismView_lightDirection, 0)
            lightDirection = when (dir) {
                1 -> LightDirection.TOP_RIGHT
                2 -> LightDirection.BOTTOM_LEFT
                3 -> LightDirection.BOTTOM_RIGHT
                else -> LightDirection.TOP_LEFT
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 40f
        val rect = RectF(
            padding,
            padding,
            width - padding,
            height - padding
        )

        val shadowOffset = 19f
        val shadowRadius = 23f

        // Tính offset cho shadow
        val (dxLight, dyLight, dxDark, dyDark) = if (isConcave) {
            when (lightDirection) {
                LightDirection.TOP_LEFT -> floatArrayOf(shadowOffset, shadowOffset, -shadowOffset, -shadowOffset)
                LightDirection.TOP_RIGHT -> floatArrayOf(-shadowOffset, shadowOffset, shadowOffset, -shadowOffset)
                LightDirection.BOTTOM_LEFT -> floatArrayOf(shadowOffset, -shadowOffset, -shadowOffset, shadowOffset)
                LightDirection.BOTTOM_RIGHT -> floatArrayOf(-shadowOffset, -shadowOffset, shadowOffset, shadowOffset)
            }
        } else {
            when (lightDirection) {
                LightDirection.TOP_LEFT -> floatArrayOf(-shadowOffset, -shadowOffset, shadowOffset, shadowOffset)
                LightDirection.TOP_RIGHT -> floatArrayOf(shadowOffset, -shadowOffset, -shadowOffset, shadowOffset)
                LightDirection.BOTTOM_LEFT -> floatArrayOf(-shadowOffset, shadowOffset, shadowOffset, -shadowOffset)
                LightDirection.BOTTOM_RIGHT -> floatArrayOf(shadowOffset, shadowOffset, -shadowOffset, -shadowOffset)
            }
        }

        // Shadow dark
        darkPaint.color = Color.parseColor("#E0E0E0")
        darkPaint.setShadowLayer(shadowRadius, dxDark, dyDark, Color.parseColor("#B5B5B5"))
        canvas.drawRoundRect(rect, radius, radius, darkPaint)

        // Shadow light
        lightPaint.color = Color.parseColor("#E0E0E0")
        lightPaint.setShadowLayer(shadowRadius, dxLight, dyLight, Color.WHITE)
        canvas.drawRoundRect(rect, radius, radius, lightPaint)

        // Background
        bgPaint.clearShadowLayer()
        bgPaint.color = Color.parseColor("#E0E0E0")
        canvas.drawRoundRect(rect, radius, radius, bgPaint)
    }

    // padding nội bộ để các view con không bị shadow cắt
    override fun getPaddingLeft() = (super.getPaddingLeft() + 40).toInt()
    override fun getPaddingTop() = (super.getPaddingTop() + 40).toInt()
    override fun getPaddingRight() = (super.getPaddingRight() + 40).toInt()
    override fun getPaddingBottom() = (super.getPaddingBottom() + 40).toInt()
}