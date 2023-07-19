package com.dss.tooldocapplication.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import com.dss.tooldocapplication.R
import java.util.concurrent.TimeUnit


class DialogLoading(val context: Context) {

    private var alertDialog: AlertDialog? = null
//    var loadView: CircularProgressBar? = null

    private var timeShow: Long = 0
    private var timeOut: Long = 20000
    private val looper = Handler(Looper.getMainLooper())

    fun show(timeOutListener: (() -> Unit?)? = null, dismissListener: (() -> Unit?)? = null) {
        if (alertDialog != null && alertDialog?.isShowing == true) {
            return
        }

        timeShow = System.currentTimeMillis()

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        @SuppressLint("InflateParams") val dialogView: View =
            inflater.inflate(R.layout.loading_progress_app, null)
//        loadView = dialogView.findViewById(R.id.circle_progress)
//        loadView?.indeterminateMode = true

        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create().apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            setOnDismissListener {
                dismissListener?.invoke()
            }
        }

        looper.postDelayed({
            dismissDialog()
            timeOutListener?.invoke()
        }, timeOut)
    }

    fun dismissDialog() {
        if (alertDialog != null && alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }
        looper.removeCallbacksAndMessages(null)
    }

    internal fun dismissLoading() {
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toSeconds(
                timeShow
            ) >= 1.5
        ) {
            dismissDialog()
        } else {
            looper.postDelayed({ dismissDialog() }, 1000)
        }
    }
}