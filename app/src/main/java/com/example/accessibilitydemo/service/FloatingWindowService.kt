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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.accessibilitydemo.R
import com.example.accessibilitydemo.databinding.LayoutFloatingWindowBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

        floatingView.findViewById<ImageButton>(R.id.close).setOnClickListener {
//            stopService(Intent(this, FloatingWindowService::class.java))
            clearAllPoint()
            stopSelf()
        }

        floatingView.findViewById<ImageButton>(R.id.add).setOnClickListener {
            addPoint()
        }

        floatingView.findViewById<ImageButton>(R.id.remove).setOnClickListener{
            removePoint()
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
        // 创建新的点视图
        val pointView = LayoutInflater.from(this).inflate(R.layout.item_point, null)
        pointView.findViewById<TextView>(R.id.point).text = " ${pointsList.size + 1}"
        // 设置点的布局参数
        val pointParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        pointParams.gravity = Gravity.CENTER // 默认将点置于屏幕中央

        // 将点添加到窗口管理器
        windowManager.addView(pointView, pointParams)
        pointsList.add(pointView)

        // 为每个点设置独立的拖动事件
        pointView.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            @SuppressLint("ClickableViewAccessibility")
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

    fun removePoint(){
        if(pointsList.isNotEmpty()){
            windowManager.removeView(pointsList.last())
        }
        pointsList.removeLast()
    }

    fun clearAllPoint(){
        for(point in pointsList){
            windowManager.removeView(point)
        }
        pointsList.clear()
    }

    private fun getAllPointsPosition() {
        for (point in pointsList) {
            val location = IntArray(2)
            point.getLocationOnScreen(location)
            Log.d("PointPosition", "Point at: (${location[0]}, ${location[1]})")
        }
    }

}
