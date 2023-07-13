package io.upnextgpt.base.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.getActivity(): Activity? {
    var context = this
    while (true) {
        when (context) {
            is Activity -> return context
            is ContextWrapper -> context = context.baseContext
            else -> return null
        }
    }
}