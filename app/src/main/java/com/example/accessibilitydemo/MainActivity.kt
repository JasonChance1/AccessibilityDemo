package com.example.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.accessibilitydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("onCreate","oncreate")
//        binding.container.setOnTouchListener { v, event ->
//            when(event.action){
//                MotionEvent.ACTION_DOWN->Log.e("down","x:${event.x},y:${event.y}")
//                MotionEvent.ACTION_MOVE->Log.e("down","x:${event.x},y:${event.y}")
//            }
//
//            true
//        }
        if(!this.isAccessibilitySettingsOn(MyService::class.java)){
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.toHome.setOnClickListener {
            sendBroadcast(Intent(MyService.ACTION_GO_HOME))
        }
    }

    override fun onResume() {
        super.onResume()
        if(isAccessibilitySettingsOn(MyService::class.java)){
            binding.serviceText.text = "权限已开启"
        }else{
            binding.serviceText.text = "无权限"
        }
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
        val mStringColonSplitter = SimpleStringSplitter(':')
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
