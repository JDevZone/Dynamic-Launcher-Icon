package com.random.dynamiclaunch.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

fun Context.updateComponentState(name: String, enabledState: Int) {
    try {
        packageManager.setComponentEnabledSetting(
            ComponentName(this, "$packageName.$name"),
            enabledState,
            PackageManager.DONT_KILL_APP
        )
    } catch (e: Exception) {
        Log.d(
            "Dynamic Launch",
            "exception in packageManager.setComponentEnabledSetting call ${e.localizedMessage ?: ""}"
        )
    }
}