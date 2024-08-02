package com.example.accessibilitydemo.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.accessibilitydemo.R
import kotlin.random.Random

/**
 * @description:
 * @author Wandervogel
 * @date :2024/4/15
 * @version 1.0.0
 */
class MyAccessibilityService : AccessibilityService() {
    companion object {
        const val ACTION_GO_HOME = "com.example.action.GO_HOME"
        const val ACTION_LAUNCH_QQ = "com.example.action.LAUNCH_QQ"
        const val ACTION_GO_HOME_AND_LAUNCH_QQ = "com.example.action.GO_HOME_AND_LAUNCH_QQ"
        const val AUTO_CHECK = "check"
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_GO_HOME -> goHome()
                ACTION_LAUNCH_QQ -> launchQQ()
                ACTION_GO_HOME_AND_LAUNCH_QQ -> goHomeAndLaunchQQ()
                AUTO_CHECK -> autoCheck()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {// 窗口/界面发生改变
                Log.e("包名", event.className.toString())
                event.className.let { className ->

                }
            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {// 界面内容发生改变
                event.source?.let {
                    Log.e("TYPE_WINDOW_CONTENT_CHANGED-包名", event.className.toString())
                    when (event.className) {
                        "androidx.recyclerview.widget.RecyclerView" -> {
                            it.findByText("红包")?.click()
                        }

                        else -> {}
                    }
                }
            }

            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {

            }

        }
    }

    override fun onInterrupt() {
//        unregisterReceiver(broadcastReceiver)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("连接建立", "连接建立")
        startForegroundService()

        // 注册广播接收器
        val filter = IntentFilter().apply {
            addAction(ACTION_GO_HOME)
            addAction(ACTION_LAUNCH_QQ)
            addAction(ACTION_GO_HOME_AND_LAUNCH_QQ)
            addAction(AUTO_CHECK)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, filter)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    fun click(x: Float, y: Float) {
        Log.e("点击", "点击：$x,$y")
        val path = Path().apply {
            moveTo(x, y)
        }
        Log.e("点击", "path：${path}")
        val gestureBuilder = GestureDescription.Builder()
        val gestureDescription = gestureBuilder
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

    // 手势滑动
    fun swipe(startX: Float, startY: Float, endX: Float, endY: Float, duration: Long) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val gestureBuilder = GestureDescription.Builder()
        val gestureDescription = gestureBuilder
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        Log.e("Swipe", "开始滑动")
        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("Swipe", "手势滑动完成")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d("Swipe", "手势滑动取消")
            }
        }, null)
    }

    private fun delay(time: Long) {
        Thread.sleep(time)
    }

    private val randomDelay: Long
        get() = Random.nextLong(200, 800)

    // 返回桌面
    private fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        delay(randomDelay)

        val startX = 160f
        val startY = 1400f
        val endX = 900f
        val endY = 1400f
        swipe(startX, startY, endX, endY, 100)
        delay(randomDelay)
        click(900f, 1858f)
    }

    // 打开 QQ
    private fun launchQQ() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.tencent.mobileqq",
                "com.tencent.mobileqq.activity.SplashActivity"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("启动错误", "无法找到QQ的启动Intent", e)
        }
    }


    // 综合操作：先回桌面，再打开QQ
    private fun goHomeAndLaunchQQ() {
        goHome()
        Handler(Looper.getMainLooper()).postDelayed({
            launchQQ()
        }, 1000)  // 延迟1秒后启动QQ，确保已返回桌面
    }

    private fun autoCheck() {

    }

    // 点击
    fun AccessibilityNodeInfo.click() = performAction(AccessibilityNodeInfo.ACTION_CLICK)

    // 长按
    fun AccessibilityNodeInfo.longClick() =
        performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)

    // 向下滑动一下
    fun AccessibilityNodeInfo.scrollForward() =
        performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)

    // 向上滑动一下
    fun AccessibilityNodeInfo.scrollBackward() =
        performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

//    fun AccessibilityNodeInfo.scrollLeft() =
//        performAction(AccessibilityNodeInfo.)

    // 填充文本
    fun AccessibilityNodeInfo.input(content: String) = performAction(
        AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
        }
    )


    private fun AccessibilityNodeInfo.findByText(text: String): AccessibilityNodeInfo? {
        findAccessibilityNodeInfosByText(text)?.let {
            if (it.isNotEmpty()) {
                return it[0]
            }
        }
        return null
    }

    private fun AccessibilityNodeInfo.findById(id: String): AccessibilityNodeInfo? {
        findAccessibilityNodeInfosByViewId(id)?.let {
            if (it.isNotEmpty()) {
                return it[0]
            }
        }
        return null
    }

    private fun startForegroundService() {
        val channelId = "my_accessibility_service_channel"
        val channelName = "Accessibility Service Channel"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Accessibility Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.vector_forground)
            .build()

        startForeground(1, notification)
    }
}