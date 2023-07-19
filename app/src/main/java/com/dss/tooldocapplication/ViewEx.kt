package com.dss.tooldocapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Paint
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

fun View.onAvoidDoubleClick(
    throttleDelay: Long = 600L,
    onClick: (View) -> Unit,
) {
    setOnClickListener {
        onClick(this)
        isClickable = false
        postDelayed({ isClickable = true }, throttleDelay)
    }
}

fun View.setAnimation(context: Activity, anim: Int?, duration: Long, onAnimation: (View) -> Unit) {
    this.startAnimation(anim?.let { AnimationUtils.loadAnimation(context, it) })
    Timer().schedule(duration) {
        context.runOnUiThread {
            onAnimation(this@setAnimation)
        }

    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

inline fun View.snack(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    action(view.resources.getString(actionRes), color, listener)
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        if (startIndexOfLink == -1) continue
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun ImageView.ofHeight(): Int {
    return this.drawable.intrinsicHeight
}

fun ImageView.ofWidth(): Int {
    return this.drawable.intrinsicWidth
}

fun ImageView.setTint(colorRes: Int) {
    try {
        ImageViewCompat.setImageTintList(
            this,
            ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        )
    } catch (e: Exception) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(colorRes))
    }
}

fun Window.setLightStatusBars(b: Boolean) {
    WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = b
}

// màu chữ mặc định dược set ở theme
fun View.autoColorDarkMode(
    light: Int = R.color.white,
    dark: Int = R.color.black,
    requireColor: Boolean = true
) {
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> {
            when (this) {
                is TextView -> {
                    setTextColor(ContextCompat.getColor(this.context, light))
                }
                is ImageView -> {
                    setTint(ContextCompat.getColor(this.context, light))
                }
                is ViewGroup -> {
                    if (requireColor) {
                        setBackgroundColor(ContextCompat.getColor(this.context, dark))
                    } else {
                        setBackgroundColor(
                            ContextCompat.getColor(
                                this.context,
                                R.color.black
                            )
                        )
                    }
                }
            }
        }
        else -> {
            when (this) {
                is TextView -> {
                    setTextColor(ContextCompat.getColor(this.context, dark))
                }
                is ImageView -> {
                    setTint(ContextCompat.getColor(this.context, dark))
                }
                is ViewGroup -> {
                    if (requireColor) {
                        setBackgroundColor(ContextCompat.getColor(this.context, light))
                    } else {
                        setBackgroundColor(ContextCompat.getColor(this.context, R.color.white))
                    }
                }
            }
        }
    }
}


fun ViewGroup.autoBackgroundDarkMode(
    light: Int,
    dark: Int
) {
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> {
            background = ContextCompat.getDrawable(this.context, dark)
        }
        else -> {
            background = ContextCompat.getDrawable(this.context, light)
        }
    }
}


fun View.setMargins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        this.requestLayout()
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnLongClickHandle(handler: Handler, listener: () -> Unit) {
    setOnTouchListener { v, event ->
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                listener.invoke()
            }
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacksAndMessages(null)
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacksAndMessages(null)
            }
        }
        true
    }
}

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun getScreenWidth(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun getScreenHeight(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

fun dpToPx(context: Context, value: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Context.dimenDpToPx(dimen: Int): Int {
    return resources.getDimension(dimen).toInt()
}


fun Context.toastMsg(msg: Int) =
    Toast.makeText(this, this.getString(msg), Toast.LENGTH_SHORT).show()

fun Context.toastMsg(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.showSoftKeyboard(v: View) {
    v.post {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun AppCompatActivity.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            0
        )
    }
}

fun Fragment.hideSoftKeyboard() {
    val inputMethodManager = requireActivity().getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().currentFocus!!.windowToken,
            0
        )
    }
}