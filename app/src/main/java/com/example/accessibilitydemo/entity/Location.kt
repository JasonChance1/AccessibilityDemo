package com.example.accessibilitydemo.entity

import android.view.View

data class Point (
    var number: Int,// 点序号
    var type: PointType,// 点类型
    @Transient var view:View,// 视图
    var position:Position,// 坐标
    var swipeDistance:Float = 0f// 滑动距离
){
    enum class PointType{
        CLICK,
        SWIPE,
        LONG_CLICK
    }
}

data class Position(var x:Float, var y:Float)