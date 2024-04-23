package com.example.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * @description:
 * @author Wandervogel
 * @date :2024/4/15
 * @version 1.0.0
 */
class MyService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {// 窗口/界面发生改变
                Log.e("包名", event.className.toString())
                event.className.let { className ->
                    event.source?.let {
                        when (className) {
                            "com.example.accessibilitydemo.MainActivity" -> goHomeAndLaunchQQ()
                            "com.tencent.mobileqq.activity.ChatActivity" -> {
                                it.findById("com.tencent.mobileqq:id/send_btn")?.click()
                                it.findById("com.tencent.mobileqq:id/qex")?.click()
                            }

                            "com.tencent.mobileqq.activity.SplashActivity" -> {
//                                it.findById("com.tencent.mobileqq:id/aua")?.click()
                                it.findByText("[红包]")?.click()
                            }

                            "com.tencent.biz.TenpayActivity" -> {
                                it.findAccessibilityNodeInfosByViewId(
                                    "com.tencent.mobileqq:id/vt7"
                                ).takeIf { it.isNotEmpty() }?.last()?.click()
                                Log.e(
                                    "TenpayActivity-开",
                                    it.findAccessibilityNodeInfosByText("開").size.toString()
                                )
                            }

                            else -> {}
                        }
                    }
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
        }

        val text = when (event?.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "TYPE_VIEW_CLICKED"
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> "TYPE_VIEW_LONG_CLICKED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "TYPE_WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> "TYPE_NOTIFICATION_STATE_CHANGED"
            AccessibilityEvent.TYPE_ANNOUNCEMENT -> "TYPE_ANNOUNCEMENT"

            AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> "TYPE_ASSIST_READING_CONTEXT"

            AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> "TYPE_GESTURE_DETECTION_END"

            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> "TYPE_GESTURE_DETECTION_START"


            AccessibilityEvent.TYPE_SPEECH_STATE_CHANGE -> "TYPE_SPEECH_STATE_CHANGE"

            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> "TYPE_TOUCH_EXPLORATION_GESTURE_END"

            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> "TYPE_TOUCH_EXPLORATION_GESTURE_START"

            AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> "TYPE_TOUCH_INTERACTION_END"

            AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> "TYPE_TOUCH_INTERACTION_START"

            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> "TYPE_VIEW_ACCESSIBILITY_FOCUSED"
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED"

            AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> "TYPE_VIEW_CONTEXT_CLICKED"

            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "TYPE_VIEW_FOCUSED"

            AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> "TYPE_VIEW_HOVER_ENTER"

            AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> "TYPE_VIEW_HOVER_EXIT"

            AccessibilityEvent.TYPE_VIEW_SCROLLED -> "TYPE_VIEW_SCROLLED"

            AccessibilityEvent.TYPE_VIEW_SELECTED -> "TYPE_VIEW_SELECTED"
            AccessibilityEvent.TYPE_VIEW_TARGETED_BY_SCROLL -> "TYPE_VIEW_TARGETED_BY_SCROLL"
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "TYPE_VIEW_TEXT_CHANGED"
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> "TYPE_VIEW_TEXT_SELECTION_CHANGED"

            AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY"

            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> "TYPE_WINDOWS_CHANGED"

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "TYPE_WINDOW_CONTENT_CHANGED"

            else -> ""
        }
        Log.e("--", text)
    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("连接建立", "连接建立")
    }

    fun click(x: Float, y: Float) {
        Log.e("点击", "点击")
        val path = Path().apply {
            moveTo(x, y)
        }
        val gestureBuilder = GestureDescription.Builder()
        val gestureDescription = gestureBuilder
            .addStroke(GestureDescription.StrokeDescription(path, 0, 1))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

    // 返回桌面
    private fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
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
}