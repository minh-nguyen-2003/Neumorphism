package vn.minh_nguyen.vkey.library.neumorphic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes

class NeumorphismDarkTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var isConcave = false
    private var lightDirection = LightDirection.TOP_LEFT
    private var mainColor = Color.parseColor("#000000")
    private var elevationFactor = 1.0f
    private var shadowRadiusBase = 6f
    private val baseOffset = 3f

    enum class LightDirection {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.NeumorphismView) {
            isConcave = getBoolean(R.styleable.NeumorphismView_isConcave, false)
            mainColor = getColor(R.styleable.NeumorphismView_mainColor, mainColor)
            elevationFactor = getFloat(R.styleable.NeumorphismView_elevationFactor, elevationFactor)
            val dir = getInt(R.styleable.NeumorphismView_lightDirection, 0)
            lightDirection = LightDirection.values()[dir]
        }

        // ép tối thiểu để shadow không chết
        if (Color.red(mainColor) == 0 &&
            Color.green(mainColor) == 0 &&
            Color.blue(mainColor) == 0
        ) {
            mainColor = Color.parseColor("#181818")
        }

        setTextColor(mainColor)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        val textStr = text.toString()
        if (textStr.isEmpty()) return

        val x = paddingLeft.toFloat()
        val fm = paint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val y = paddingTop - fm.ascent +
                (height - paddingTop - paddingBottom - textHeight) / 2

        val offset = baseOffset * elevationFactor
        val shadowRadius = shadowRadiusBase * elevationFactor

        val (dxLight, dyLight, dxDark, dyDark) = when (lightDirection) {
            LightDirection.TOP_LEFT -> floatArrayOf(-offset, -offset, offset, offset)
            LightDirection.TOP_RIGHT -> floatArrayOf(offset, -offset, -offset, offset)
            LightDirection.BOTTOM_LEFT -> floatArrayOf(-offset, offset, offset, -offset)
            LightDirection.BOTTOM_RIGHT -> floatArrayOf(offset, offset, -offset, -offset)
        }

        val (lx, ly, dx, dy) =
            if (isConcave)
                floatArrayOf(dxDark, dyDark, dxLight, dyLight)
            else
                floatArrayOf(dxLight, dyLight, dxDark, dyDark)

        // ===== Dark shadow (absolute black) =====
        val darkPaint = Paint(paint)
        darkPaint.color = mainColor
        darkPaint.setShadowLayer(
            shadowRadius,
            dx,
            dy,
            Color.argb(200, 0, 0, 0)
        )
        canvas.drawText(textStr, x, y, darkPaint)

        // ===== Light highlight (soft gray) =====
        val lightPaint = Paint(paint)
        lightPaint.color = mainColor
        lightPaint.setShadowLayer(
            shadowRadius,
            lx,
            ly,
            Color.argb(140, 220, 220, 220)
        )
        canvas.drawText(textStr, x, y, lightPaint)

        // ===== Main text =====
        val mainPaint = Paint(paint)
        mainPaint.color = mainColor
        mainPaint.clearShadowLayer()
        canvas.drawText(textStr, x, y, mainPaint)
    }
}