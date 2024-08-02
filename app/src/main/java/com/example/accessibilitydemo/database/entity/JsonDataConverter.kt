package com.example.accessibilitydemo.database.entity

import androidx.room.TypeConverter
import com.example.accessibilitydemo.entity.Point
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonDataConverter {

    @TypeConverter
    fun fromJsonData(attrJsonStr: List<Point>): String {
        return Gson().toJson(attrJsonStr)
    }

    @TypeConverter
    fun toJsonData(json: String): List<Point> {
        val type = object : TypeToken<List<Point>>() {}.type
        return Gson().fromJson(json, type)
    }
}