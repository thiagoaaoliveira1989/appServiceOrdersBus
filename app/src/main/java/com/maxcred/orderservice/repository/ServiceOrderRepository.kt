package com.maxcred.orderservice.repository

import androidx.lifecycle.LiveData
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity

data class ServiceOrderResult(
    val id: Long,
    val orderNumber: String,
    val kmBus: String,
    val startDate: String,
    val endDate: String,
    val description: String?,
    val mechanic: String,
    val supervisor: String,
    val busId: Long
)

interface ServiceOrderRepository {
    suspend fun isServiceOrderNumberExists(orderNumber: String): Boolean

    suspend fun getServiceOrderById(id: Long): ServiceOrderResult?

    suspend fun getServiceOrderIdByPlate(orderNumber: String): Long

    suspend fun insertServiceOrder(
        orderNumber: String, kmBus: String, startDate: String, endDate: String, description: String?,
        mechanic: String, supervisor: String, busId: Long
    ): ServiceOrderResult

    suspend fun updateServiceOrder(
        id: Long, orderNumber: String, kmBus: String, startDate: String, endDate: String, description: String?,
        mechanic: String, supervisor: String, busId: Long
    )

    suspend fun deleteServiceOrder(id: Long)
    fun getAllServiceOrders(): LiveData<List<ServiceOrderEntity>>

}
