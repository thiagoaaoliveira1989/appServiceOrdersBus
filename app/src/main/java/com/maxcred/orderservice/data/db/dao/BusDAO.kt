package com.maxcred.orderservice.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maxcred.orderservice.data.db.entity.BusEntity

@Dao
interface BusDAO {
    @Query("SELECT * FROM bus WHERE licensePlate = :licensePlate LIMIT 1")
    suspend fun getBusByPlate(licensePlate: String): BusEntity?

    @Query("SELECT * FROM bus WHERE id = :id LIMIT 1")
    suspend fun getBusById(id: Long): BusEntity?

    @Query("SELECT id FROM bus WHERE licensePlate = :licensePlate LIMIT 1")
    suspend fun getBusIdByPlate(licensePlate: String): Long

    @Query("SELECT * FROM bus")
    fun getAllBus(): LiveData<List<BusEntity>>

    @Query("SELECT COUNT(*) FROM bus WHERE licensePlate = :licensePlate")
    suspend fun isLicensePlateExists(licensePlate: String): Boolean

    @Insert
    suspend fun insertBus(bus: BusEntity): Long

    @Update
    suspend fun updateBus(bus: BusEntity)

    @Query("DELETE FROM bus WHERE id = :id")
    suspend fun deleteBus(id: Long)

    @Query("DELETE FROM bus")
    suspend fun deleteAllBuses()
}
