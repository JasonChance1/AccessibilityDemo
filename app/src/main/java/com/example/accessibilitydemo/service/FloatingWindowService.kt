package com.example.accessibilitydemo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.example.accessibilitydemo.R
import com.example.accessibilitydemo.entity.Point
import com.example.accessibilitydemo.entity.Position
import com.example.accessibilitydemo.util.toJson
import com.example.accessibilitydemo.util.toJsonArray

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var pointsContainer: FrameLayout
    private val pointsList = mutableListOf<Point>() // 存储所有点的列表
    private val types = Point.PointType.entries.toList()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        addFloatingWindow()
    }

    private fun addFloatingWindow() {
        // 使用 LayoutInflater 从布局文件中加载自定义布局
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null)

        // 设置布局参数
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // 初始位置
        layoutParams.x = 0
        layoutParams.y = 100
        layoutParams.gravity = Gravity.TOP or Gravity.START

        floatingView.findViewById<ImageButton>(R.id.close).setOnClickListener {
//            stopService(Intent(this, FloatingWindowService::class.java))
            clearAllPoint()
            stopSelf()
        }

        floatingView.findViewById<ImageButton>(R.id.add).setOnClickListener {
            showPointTypeDialog()
        }

        floatingView.findViewById<ImageButton>(R.id.remove).setOnClickListener {
            removePoint()
        }

        floatingView.findViewById<ImageButton>(R.id.complete).setOnClickListener{
            collectAllPointsPosition()
        }

        // 触摸监听以实现拖动悬浮窗
        floatingView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = layoutParams.x
                        initialY = layoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, layoutParams)
                        return true
                    }
                }
                return false
            }
        })

        // 将自定义视图添加到窗口管理器
        windowManager.addView(floatingView, layoutParams)
    }


    override fun onDestroy() {
        super.onDestroy()
        // 移除悬浮窗
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }


    private fun addPoint(type: Point.PointType) {
        val pointView = LayoutInflater.from(this).inflate(R.layout.item_point, null)
        val pointParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        pointParams.gravity = Gravity.CENTER
        val number = pointsList.size + 1
        // 设置点视图上的编号
        val numberTextView = pointView.findViewById<TextView>(R.id.number)
        numberTextView.text = number.toString()

        windowManager.addView(pointView, pointParams)

        val point = Point(
            number = number,
            type = type,
            view = pointView,
            position = Position(0f, 0f)
        )

        pointsList.add(point)

        pointView.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = pointParams.x
                        initialY = pointParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        pointParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        pointParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(pointView, pointParams)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun collectAllPointsPosition() {
        for (point in pointsList) {
            val location = IntArray(2)
            point.view.getLocationOnScreen(location)
            point.position.x = location[0].toFloat()
            point.position.y = location[1].toFloat()
            Log.d(
                "PointPosition",
                point.toJson().toString()
            )
        }
        Log.e("pointPosition",pointsList.toJsonArray().toString())
        // 显示收集结果（可根据实际需求调整）
    }

    private fun removePoint() {
        if (pointsList.isNotEmpty()) {
            windowManager.removeView(pointsList.last().view)
            pointsList.removeLast()
        }
    }

    private fun clearAllPoint() {
        for (point in pointsList) {
            windowManager.removeView(point.view)
        }
        pointsList.clear()
    }

    private fun showPointTypeDialog() {
        // 创建自定义对话框布局视图
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_point_type, null)

        // 配置对话框的布局参数
        val dialogParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
        )

        dialogParams.gravity = Gravity.CENTER // 在屏幕中央显示对话框
        dialogParams.dimAmount = 0.5f // 设置背景变暗效果

        // 查找对话框中的按钮并设置点击监听器
        val btnClick = dialogView.findViewById<TextView>(R.id.click)
        val btnSwipe = dialogView.findViewById<TextView>(R.id.swipe)
        val btnLongClick = dialogView.findViewById<TextView>(R.id.longClick)
        val btnCancel = dialogView.findViewById<TextView>(R.id.cancel)

        btnClick.setOnClickListener {
            windowManager.removeView(dialogView) // 移除对话框
            addPoint(Point.PointType.CLICK)
        }

        btnSwipe.setOnClickListener {
            windowManager.removeView(dialogView) // 移除对话框
            addPoint(Point.PointType.SWIPE)
        }

        btnLongClick.setOnClickListener {
            windowManager.removeView(dialogView) // 移除对话框
            addPoint(Point.PointType.LONG_CLICK)
        }

        btnCancel.setOnClickListener {
            windowManager.removeView(dialogView) // 取消并移除对话框
        }

        // 添加对话框视图到窗口管理器
        windowManager.addView(dialogView, dialogParams)
    }

}
