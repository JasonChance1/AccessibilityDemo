package com.example.accessibilitydemo.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.accessibilitydemo.entity.Point


@Entity
data class Record (
    @PrimaryKey(autoGenerate = true) val id:Int,
    @TypeConverters(JsonDataConverter::class) val records:List<Point>,
    @ColumnInfo(name = "name") var name:String
)