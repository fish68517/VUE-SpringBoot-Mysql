package com.example.dialerreplica.service

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dialerreplica.MainActivity
import com.example.dialerreplica.R
import com.example.dialerreplica.model.CallPhase
import com.example.dialerreplica.model.CallSessionSnapshot
import kotlinx.coroutines.flow.MutableStateFlow

// Activity 可见时使用应用内入口；离开应用后由通话前台服务管理系统悬浮窗。
internal object CallOverlayState {
    val activityVisible = MutableStateFlow(false)
    const val ACTION_RETURN = "com.example.dialerreplica.RETURN_TO_CALL"

    fun returnIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
        action = ACTION_RETURN
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
}

internal class CallOverlay(private val context: Context) {
    private val windows = context.getSystemService(WindowManager::class.java)
    private var pill: LinearLayout? = null
    private var pillStatus: TextView? = null
    private var banner: TextView? = null
    private fun dp(value: Int) = (value * context.resources.displayMetrics.density).toInt()

    fun update(session: CallSessionSnapshot, activityVisible: Boolean) {
        val active = session.phase == CallPhase.DIALING || session.phase == CallPhase.CONNECTED
        if (activityVisible || !active || !Settings.canDrawOverlays(context) ||
            context.getSystemService(KeyguardManager::class.java).isKeyguardLocked) {
            hide()
            return
        }
        if (pill == null) {
            try {
                // 与应用内胶囊保持一致；波形使用独立矢量图，不使用会连成台阶的文字。
                pill = createPill()
                windows.addView(pill, params(dp(132), dp(40), dp(4)))
                banner = createLabel(Color.rgb(91, 199, 71), Color.WHITE, 14).also {
                    it.text = "点击返回通话"
                    it.textSize = 14f
                    it.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    it.setPadding(dp(16), 0, dp(16), 0)
                    val width = (context.resources.displayMetrics.widthPixels - dp(32)).coerceAtLeast(dp(120))
                    windows.addView(it, params(width, dp(44), dp(64)))
                }
            } catch (exception: RuntimeException) {
                // 权限可能在通话中被收回；失败时仍可通过通知返回。
                android.util.Log.w("CallOverlay", "Unable to show call overlay", exception)
                hide()
            }
        }
        pillStatus?.text = if (session.phase == CallPhase.CONNECTED) {
            formatDuration(session.callElapsedMs)
        } else "拨号中"
    }

    private fun createPill() = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = GradientDrawable().apply {
            setColor(Color.BLACK)
            cornerRadius = dp(22).toFloat()
        }
        elevation = dp(4).toFloat()
        clipToOutline = true
        contentDescription = "点击返回通话"
        setOnClickListener { returnToCall() }
        fun addSpace() { addView(View(context), LinearLayout.LayoutParams(0, 1, 1f)) }
        addSpace()
        addView(ImageView(context).apply {
            setImageResource(R.drawable.ic_call_overlay_wave)
            scaleType = ImageView.ScaleType.FIT_CENTER
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }, LinearLayout.LayoutParams(dp(25), dp(25)))
        addSpace()
        pillStatus = TextView(context).apply {
            setTextColor(Color.rgb(20, 193, 80))
            textSize = 14f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            includeFontPadding = false
            gravity = Gravity.CENTER
            setSingleLine(true)
        }
        addView(pillStatus, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        addSpace()
    }

    private fun returnToCall() {
        runCatching { context.startActivity(CallOverlayState.returnIntent(context)) }
            .onFailure { android.util.Log.w("CallOverlay", "Unable to restore call", it) }
    }

    private fun createLabel(backgroundColor: Int, foregroundColor: Int, radius: Int) = TextView(context).apply {
        gravity = Gravity.CENTER
        setTextColor(foregroundColor)
        background = GradientDrawable().apply {
            setColor(backgroundColor)
            cornerRadius = dp(radius).toFloat()
        }
        contentDescription = "点击返回通话"
        setOnClickListener { returnToCall() }
    }

    // 两个独立的小窗口，不创建覆盖整个屏幕的透明触摸层。
    // 使用系统安全区域：普通应用不能覆盖真正的系统状态栏。
    private fun params(width: Int, height: Int, top: Int) = WindowManager.LayoutParams(
        width, height, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT,
    ).apply {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        y = top
    }

    fun hide() {
        pill?.let { runCatching { windows.removeViewImmediate(it) } }
        banner?.let { runCatching { windows.removeViewImmediate(it) } }
        pill = null
        pillStatus = null
        banner = null
    }
}
