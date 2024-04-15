package com.example.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.accessibilitydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val clickEnabled = MutableLiveData(false)

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


        binding.requestPermission.setOnClickListener {
            if (this.isAccessibilitySettingsOn(MyService::class.java)) {
                Toast.makeText(this, "已开启", Toast.LENGTH_LONG).show()
            } else {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        binding.clickSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.e("clickSwitch","clickSwitch")
            clickEnabled.postValue(isChecked)
        }

        clickEnabled.observe(this) { isEnabled ->
            handleAutoClick(isEnabled)
        }
    }

    override fun onResume() {
        super.onResume()
        if(isAccessibilitySettingsOn(MyService::class.java)){
            binding.serviceText.setText("权限已开启")
        }else{
            binding.serviceText.text = "无权限"
        }
    }

    private fun handleAutoClick(enable: Boolean) {
        if (enable) {
            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    // 确保服务已启用并且用户开关仍然是打开的
                    if (clickEnabled.value == true) {
                        // 模拟点击，此处需通过无障碍服务实现
//                        service.click(424.57492f, 905.7778f)
                        handler.postDelayed(this, 500)  // 每500ms点击一次
                    }
                }
            }
            handler.post(runnable)
        }
    }

    fun Context.isAccessibilitySettingsOn(clazz: Class<out AccessibilityService?>): Boolean {
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
