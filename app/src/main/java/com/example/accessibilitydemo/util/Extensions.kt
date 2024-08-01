package com.example.accessibilitydemo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings


fun Context.requestOverlayPermission(){
    if (!Settings.canDrawOverlays(this)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${this.packageName}")
        )
        this.startActivity(intent)
    }
}