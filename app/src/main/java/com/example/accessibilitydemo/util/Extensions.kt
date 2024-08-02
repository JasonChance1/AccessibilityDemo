package com.example.accessibilitydemo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser


fun Context.requestOverlayPermission(){
    if (!Settings.canDrawOverlays(this)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${this.packageName}")
        )
        this.startActivity(intent)
    }
}

fun <T : Any> T.toJson(): JsonObject {
    return try {
        Gson().toJsonTree(this).asJsonObject
    } catch (e: Exception) {
        e.printStackTrace()
        JsonObject()
    }
}


fun <T> List<T>.toJsonArray(): JsonArray? {
    val gson = Gson()
    return try {
        JsonParser.parseString(gson.toJson(this)).asJsonArray
    } catch (e: Exception) {
        null
    }
}

/**
 * 控制视图是否可见
 * @param isVisible true-显示 false-隐藏
 */
fun View.setVisible(isVisible: Boolean, showAnim: Boolean = true) {
    if (showAnim) {
        TransitionManager.beginDelayedTransition(this.rootView as? ViewGroup)
    }
    visibility = if (isVisible) View.VISIBLE else View.GONE
}