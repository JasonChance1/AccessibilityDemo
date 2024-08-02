package com.example.accessibilitydemo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
