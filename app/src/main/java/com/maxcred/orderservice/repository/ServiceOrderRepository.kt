package com.maxcred.orderservice.repository

import androidx.lifecycle.LiveData
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity

data class ServiceOrderResult(
    val orderNumber: Long,
    val kmBus: String,
    val startDate: String,
    val endDate: String,
    val description: String?,
    val mechanic: String,
    val encarregado: String,
    val busId: Long
)

interface ServiceOrderRepository {
    suspend fun isServiceOrderNumberExists(orderNumber: Long): Boolean

    suspend fun getServiceOrderById(id: Long): ServiceOrderResult?

    suspend fun getServiceOrderIdByPlate(orderNumber: Long): Long

    suspend fun insertServiceOrder(kmBus: String, startDate: String, endDate: String, description: String?,
        mechanic: String, encarregado: String, busId: Long
    ): ServiceOrderResult

    suspend fun updateServiceOrder(
        orderNumber: Long, kmBus: String, startDate: String, endDate: String, description: String?,
        mechanic: String, encarregado: String, busId: Long
    )

    suspend fun deleteByOrderNumber(orderNumber: Long)
    fun getAllServiceOrders(): LiveData<List<ServiceOrderEntity>>

}
