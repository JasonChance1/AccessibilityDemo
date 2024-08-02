package com.example.accessibilitydemo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.accessibilitydemo.database.entity.Record

@Dao
interface RecordDao {
    @Query("SELECT * FROM record")
    fun getAll(): LiveData<List<Record>>


    @Insert
    fun insert(record: Record)

    @Query("DELETE FROM record")
    fun deleteAll()

    @Delete
    fun delete(record: Record)

    @Update
    fun update(record: Record)

    @Query("SELECT * FROM record WHERE id = :id")
    fun getRecordById(id: Int): Record
}
