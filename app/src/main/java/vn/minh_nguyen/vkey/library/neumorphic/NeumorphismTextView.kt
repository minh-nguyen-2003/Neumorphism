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

    enum class LightDirection { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    private val tempPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val offset = 3f  // khoảng offset cho shadow nhỏ quanh chữ

    init {
        context.withStyledAttributes(attrs, R.styleable.NeumorphismView) {
            isConcave = getBoolean(R.styleable.NeumorphismView_isConcave, false)
            val dir = getInt(R.styleable.NeumorphismView_lightDirection, 0)
            lightDirection = when (dir) {
                1 -> LightDirection.TOP_RIGHT
                2 -> LightDirection.BOTTOM_LEFT
                3 -> LightDirection.BOTTOM_RIGHT
                else -> LightDirection.TOP_LEFT
            }
        }
        setTextColor(Color.parseColor("#E0E0E0"))
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val x = paddingLeft.toFloat()
        val y = baseline.toFloat()

        tempPaint.textSize = textSize
        tempPaint.typeface = typeface
        tempPaint.isAntiAlias = true
        tempPaint.style = Paint.Style.FILL

        // Xác định offset shadow dựa theo lồi/lõm
        val (dxLight, dyLight, dxDark, dyDark) = if (isConcave) {
            when (lightDirection) {
                LightDirection.TOP_LEFT -> floatArrayOf(offset, offset, -offset, -offset)
                LightDirection.TOP_RIGHT -> floatArrayOf(-offset, offset, offset, -offset)
                LightDirection.BOTTOM_LEFT -> floatArrayOf(offset, -offset, -offset, offset)
                LightDirection.BOTTOM_RIGHT -> floatArrayOf(-offset, -offset, offset, offset)
            }
        } else {
            when (lightDirection) {
                LightDirection.TOP_LEFT -> floatArrayOf(-offset, -offset, offset, offset)
                LightDirection.TOP_RIGHT -> floatArrayOf(offset, -offset, -offset, offset)
                LightDirection.BOTTOM_LEFT -> floatArrayOf(-offset, offset, offset, -offset)
                LightDirection.BOTTOM_RIGHT -> floatArrayOf(offset, offset, -offset, -offset)
            }
        }

        // Vẽ shadow tối (deboss)
        tempPaint.color = Color.parseColor("#B5B5B5")
        canvas.drawText(text, x + dxDark, y + dyDark, tempPaint)

        // Vẽ highlight sáng (emboss)
        tempPaint.color = Color.WHITE
        canvas.drawText(text, x + dxLight, y + dyLight, tempPaint)

        // Vẽ chữ thật lên trên
        tempPaint.color = Color.parseColor("#E0E0E0")
        canvas.drawText(text, x, y, tempPaint)
    }
}