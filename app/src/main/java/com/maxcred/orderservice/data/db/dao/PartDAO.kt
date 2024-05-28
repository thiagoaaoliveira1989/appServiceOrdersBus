package com.maxcred.orderservice.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maxcred.orderservice.data.db.entity.PartEntity

@Dao
interface PartDAO {
    @Insert
    suspend fun insert(part: PartEntity): Long

    @Update
    suspend fun update(part: PartEntity)

    @Query("DELETE FROM part WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM part")
    suspend fun deleteAll()

    @Query("SELECT * FROM part")
    fun getAllParts(): LiveData<List<PartEntity>>

    @Query("SELECT * FROM part WHERE id = :id")
    suspend fun getPartById(id: Long): PartEntity?

    @Query("SELECT * FROM part WHERE serviceOrderId = :serviceOrderId")
    fun getPartsForServiceOrder(serviceOrderId: Long): LiveData<List<PartEntity>>
}
