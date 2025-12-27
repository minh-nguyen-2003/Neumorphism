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
    private var mainColor = Color.parseColor("#E0E0E0")
    private var elevationFactor = 1.0f

    enum class LightDirection { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        context.withStyledAttributes(attrs, R.styleable.NeumorphismView) {
            radius = getDimension(R.styleable.NeumorphismView_cornerRadius, 32f)
            isConcave = getBoolean(R.styleable.NeumorphismView_isConcave, false)
            mainColor = getColor(R.styleable.NeumorphismView_mainColor, mainColor)
            elevationFactor = getFloat(R.styleable.NeumorphismView_elevationFactor, 1.0f)
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
        val rect = RectF(padding, padding, width - padding, height - padding)

        val shadowOffset = 19f * elevationFactor
        val shadowRadius = 23f * elevationFactor

        // Quy ước: ánh sáng từ trên trái xuống dưới phải = dxLight = -x, dyLight = -y
        val (dxLight, dyLight, dxDark, dyDark) = when (lightDirection) {
            LightDirection.TOP_LEFT -> floatArrayOf(-shadowOffset, -shadowOffset, shadowOffset, shadowOffset)
            LightDirection.TOP_RIGHT -> floatArrayOf(shadowOffset, -shadowOffset, -shadowOffset, shadowOffset)
            LightDirection.BOTTOM_LEFT -> floatArrayOf(-shadowOffset, shadowOffset, shadowOffset, -shadowOffset)
            LightDirection.BOTTOM_RIGHT -> floatArrayOf(shadowOffset, shadowOffset, -shadowOffset, -shadowOffset)
        }

        // Nếu là concave thì đảo highlight và shadow dark
        val (lx, ly, dx, dy) = if (isConcave) {
            floatArrayOf(dxDark, dyDark, dxLight, dyLight)
        } else {
            floatArrayOf(dxLight, dyLight, dxDark, dyDark)
        }

        // Vẽ shadow dark
        darkPaint.color = mainColor
        darkPaint.setShadowLayer(shadowRadius, dx, dy, adjustColorBrightness(mainColor, 0.8f))
        canvas.drawRoundRect(rect, radius, radius, darkPaint)

        // Vẽ shadow light
        lightPaint.color = mainColor
        lightPaint.setShadowLayer(shadowRadius, lx, ly, adjustColorBrightness(mainColor, 1.2f))
        canvas.drawRoundRect(rect, radius, radius, lightPaint)

        // Vẽ background chính
        bgPaint.clearShadowLayer()
        bgPaint.color = mainColor
        canvas.drawRoundRect(rect, radius, radius, bgPaint)
    }

    private fun adjustColorBrightness(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).coerceIn(0f, 255f).toInt()
        val g = (Color.green(color) * factor).coerceIn(0f, 255f).toInt()
        val b = (Color.blue(color) * factor).coerceIn(0f, 255f).toInt()
        return Color.argb(a, r, g, b)
    }

    override fun getPaddingLeft() = (super.getPaddingLeft() + 40).toInt()
    override fun getPaddingTop() = (super.getPaddingTop() + 40).toInt()
    override fun getPaddingRight() = (super.getPaddingRight() + 40).toInt()
    override fun getPaddingBottom() = (super.getPaddingBottom() + 40).toInt()
}