package vn.minh_nguyen.vkey.library.neumorphic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes

class NeumorphismTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var isConcave = false
    private var lightDirection = LightDirection.TOP_LEFT
    private var mainColor = Color.parseColor("#E0E0E0")
    private var elevationFactor = 1.0f
    private var shadowRadiusBase = 6f

    enum class LightDirection { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    private val baseOffset = 3f

    init {
        context.withStyledAttributes(attrs, R.styleable.NeumorphismView) {
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
        setTextColor(mainColor)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val x = paddingLeft.toFloat()

        val fm = paint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val y = paddingTop - fm.ascent + (height - paddingTop - paddingBottom - textHeight) / 2

        val offset = baseOffset * elevationFactor
        val shadowRadius = shadowRadiusBase * elevationFactor

        // Ánh sáng TOP_LEFT = (-x, -y), các hướng khác đúng chiều
        val (dxLight, dyLight, dxDark, dyDark) = when (lightDirection) {
            LightDirection.TOP_LEFT -> floatArrayOf(-offset, -offset, offset, offset)
            LightDirection.TOP_RIGHT -> floatArrayOf(offset, -offset, -offset, offset)
            LightDirection.BOTTOM_LEFT -> floatArrayOf(-offset, offset, offset, -offset)
            LightDirection.BOTTOM_RIGHT -> floatArrayOf(offset, offset, -offset, -offset)
        }

        // Đảo nếu concave
        val (lx, ly, dx, dy) = if (isConcave) floatArrayOf(dxDark, dyDark, dxLight, dyLight)
        else floatArrayOf(dxLight, dyLight, dxDark, dyDark)

        // Shadow tối
        val darkPaint = Paint(paint)
        darkPaint.color = adjustColorBrightness(mainColor, 0.8f)
        darkPaint.setShadowLayer(shadowRadius, dx, dy, darkPaint.color)
        canvas.drawText(text, x, y, darkPaint)

        // Shadow sáng
        val lightPaint = Paint(paint)
        lightPaint.color = adjustColorBrightness(mainColor, 1.2f)
        lightPaint.setShadowLayer(shadowRadius, lx, ly, lightPaint.color)
        canvas.drawText(text, x, y, lightPaint)

        // Chữ chính
        val mainPaint = Paint(paint)
        mainPaint.color = mainColor
        mainPaint.clearShadowLayer()
        canvas.drawText(text, x, y, mainPaint)
    }

    private fun adjustColorBrightness(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).coerceIn(0f, 255f).toInt()
        val g = (Color.green(color) * factor).coerceIn(0f, 255f).toInt()
        val b = (Color.blue(color) * factor).coerceIn(0f, 255f).toInt()
        return Color.argb(a, r, g, b)
    }
}