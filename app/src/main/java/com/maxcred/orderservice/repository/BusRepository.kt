package com.maxcred.orderservice.repository

import androidx.lifecycle.LiveData
import com.maxcred.orderservice.data.db.entity.BusEntity

data class BusResult(
    val id: Long,
    val vehicle: String,
    val licensePlate: String,
)

interface BusRepository {
    suspend fun isLicensePlateExists(licensePlate: String): Boolean
    suspend fun getBusIdByPlate(licensePlate: String): Long
    suspend fun getBusById(id: Long): BusEntity?
    suspend fun getBusByPlate(licensePlate: String): BusEntity?
    suspend fun insertBus(vehicle: String, licensePlate: String): BusResult
    suspend fun updateBus(id: Long, vehicle: String, licensePlate: String)
    suspend fun deleteBus(id: Long)
    suspend fun deleteAllBuses()
    fun getAllBuses(): LiveData<List<BusEntity>>
}
