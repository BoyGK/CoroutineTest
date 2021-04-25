package com.nullpt.testapplication.widget

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager

/**
 * 居中弹出ialog
 */
fun Activity.showCenterDialog(view: View, shownCall: (() -> Unit)? = null): Dialog {
    val dialog = Dialog(this)
    dialog.setCancelable(true)
    dialog.setContentView(view)
    val window = dialog.window!!
    val attr = window.attributes
    attr.width = WindowManager.LayoutParams.WRAP_CONTENT
    attr.height = WindowManager.LayoutParams.WRAP_CONTENT
    attr?.dimAmount = 0.3f
    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    window.attributes = attr
    dialog.setOnShowListener {
        shownCall?.invoke()
    }
    dialog.show()
    return dialog
}

fun log(message: () -> String) {
    Log.i("TestApplication", message.invoke())
}

inline fun Application.registerActivityLifecycleCallbacks(
        crossinline onActivityCreated: (activity: Activity, savedInstanceState: Bundle?) -> Unit
) {
    registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            onActivityCreated.invoke(activity, savedInstanceState)
        }

        override fun onActivityResumed(activity: Activity) {
        }

    })
}