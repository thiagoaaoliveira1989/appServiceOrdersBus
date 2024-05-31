package com.maxcred.orderservice.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity
import com.maxcred.orderservice.data.db.entity.ServiceOrderWithParts

@Dao
interface ServiceOrderDAO {
    @Insert
    suspend fun insert(serviceOrder: ServiceOrderEntity): Long

    @Query("SELECT orderNumber FROM service_order WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getServiceOrderIdByOrderNumber(orderNumber: Long): Long

    @Query("SELECT * FROM service_order WHERE orderNumber = :orderNumber")
    suspend fun getServiceOrderByOrderNumber(orderNumber: Long): ServiceOrderEntity?

    @Update
    suspend fun update(serviceOrder: ServiceOrderEntity)

    @Query("DELETE FROM service_order WHERE orderNumber = :orderNumber")
    suspend fun deleteByOrderNumber(orderNumber: Long)

    @Query("DELETE FROM service_order")
    suspend fun deleteAll()

    @Query("SELECT * FROM service_order")
    fun getAllServiceOrders(): LiveData<List<ServiceOrderEntity>>

    @Query("SELECT COUNT(*) > 0 FROM service_order WHERE orderNumber = :orderNumber")
    suspend fun isServiceOrderNumberExists(orderNumber: Long): Boolean

    @Transaction
    @Query("SELECT * FROM service_order WHERE orderNumber = :orderNumber")
    suspend fun getServiceOrderWithParts(orderNumber: Long): ServiceOrderWithParts?
}
