package com.example.accessibilitydemo.ui

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.example.accessibilitydemo.databinding.ActivityMainBinding
import com.example.accessibilitydemo.service.FloatingWindowService
import com.example.accessibilitydemo.service.MyAccessibilityService
import com.example.accessibilitydemo.util.requestOverlayPermission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("onCreate","oncreate")

        bindEvent()
    }

    private fun bindEvent(){
        binding.toHome.setOnClickListener {
            sendBroadcast(Intent(MyAccessibilityService.ACTION_GO_HOME))
        }

        binding.openFloat.setOnClickListener {
            val intent = Intent(this, FloatingWindowService::class.java)
            startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 判断是否开启了无障碍服务
        if(!this@MainActivity.isAccessibilitySettingsOn(MyAccessibilityService::class.java)){
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        // 请求悬浮窗权限
        this@MainActivity.requestOverlayPermission()
    }


    private fun Context.isAccessibilitySettingsOn(clazz: Class<out AccessibilityService?>): Boolean {
        var accessibilityEnabled = false    // 判断设备的无障碍功能是否可用
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 1
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled) {
            // 获取启用的无障碍服务
            val settingValue: String? = Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                // 遍历判断是否包含我们的服务
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(
                            "${packageName}/${clazz.canonicalName}",
                            ignoreCase = true
                        )
                    ) return true

                }
            }
        }
        return false
    }

}
