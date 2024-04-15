package com.example.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
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
        when(event?.eventType){
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED->{

                if(event.className == "com.tencent.mobileqq.activity.ChatActivity"){
                    event.source?.let {
//                        val nodeList = it.findAccessibilityNodeInfosByText("登录")
//                        if(!nodeList.isNullOrEmpty()){
//                            nodeList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                        }
//                        for (i in 0..100){
//                            it.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/input")[0].input("刷屏")
//                            it.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/send_btn")[0].click()
//                        }

                        it.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/k_2")[0].longClick()
                    }
                }

            }
        }

        val text = when (event?.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "TYPE_VIEW_CLICKED"
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> "TYPE_VIEW_LONG_CLICKED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "TYPE_WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> "TYPE_NOTIFICATION_STATE_CHANGED"
            else -> "${event?.eventType}"
        }
        Log.e("--", text)
    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("连接建立","连接建立")
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
        val launchIntent = packageManager.getLaunchIntentForPackage("com.tencent.mobileqq")
        launchIntent?.let {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        } ?: Log.e("启动错误", "无法找到QQ的启动Intent")
    }

    // 综合操作：先回桌面，再打开QQ
    fun goHomeAndLaunchQQ() {
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
}