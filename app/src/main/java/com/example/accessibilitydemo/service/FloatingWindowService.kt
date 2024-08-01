package com.example.accessibilitydemo.service
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.accessibilitydemo.R

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var pointsContainer: FrameLayout
    private val pointsList = mutableListOf<View>() // 存储所有点的列表

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

        floatingView.findViewById<Button>(R.id.close).setOnClickListener {
            stopService(Intent(this, FloatingWindowService::class.java))
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

    private fun addPoint() {
        val pointView = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(50, 50).apply {
                leftMargin = 100
                topMargin = 100
            }
            setBackgroundResource(R.drawable.baseline_my_location_24) // 自定义背景，圆形点
        }

        // 设置拖动功能
        pointView.setOnTouchListener(object : View.OnTouchListener {
            var dX = 0f
            var dY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                    }
                }
                return true
            }
        })

        // 添加点到容器和列表
        pointsContainer.addView(pointView)
        pointsList.add(pointView)
    }

    private fun getAllPointsPosition() {
        for (point in pointsList) {
            val location = IntArray(2)
            point.getLocationOnScreen(location)
            Log.d("PointPosition", "Point at: (${location[0]}, ${location[1]})")
        }
    }

}
