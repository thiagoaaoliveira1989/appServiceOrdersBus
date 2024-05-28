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

    @Query("SELECT id FROM service_order WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getServiceOrderIdByPlate(orderNumber: String): Long

    @Query("SELECT * FROM service_order WHERE id = :id")
    suspend fun getServiceOrderById(id: Long): ServiceOrderEntity?

    @Update
    suspend fun update(serviceOrder: ServiceOrderEntity)

    @Query("DELETE FROM service_order WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM service_order")
    suspend fun deleteAll()

    @Query("SELECT * FROM service_order")
    fun getAllServiceOrders(): LiveData<List<ServiceOrderEntity>>

    @Query("SELECT COUNT(*) FROM service_order WHERE orderNumber = :orderNumber")
    suspend fun isServiceOrderNumberExists(orderNumber: String): Boolean

    @Transaction
    @Query("SELECT * FROM service_order WHERE id = :orderId")
    suspend fun getServiceOrderWithParts(orderId: Long): ServiceOrderWithParts?
}
