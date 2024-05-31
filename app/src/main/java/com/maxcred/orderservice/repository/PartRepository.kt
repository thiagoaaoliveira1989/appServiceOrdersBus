package com.maxcred.orderservice.repository

import androidx.lifecycle.LiveData
import com.maxcred.orderservice.data.db.entity.PartEntity

data class PartResult(
    val id: Long,
    val partQty: String,
    val partCode: String?,
    val partDescription: String?,
    val partCost: String,
    val totalPartCostValue: String,
)

interface PartRepository {
    suspend fun insertPart(
        partQty: String, partCode: String?, partDescription: String?, partCost: String, totalPartCostValue: String,
        serviceOrderId: Long): PartResult

    suspend fun updatePart(
        id: Long, partQty: String, partCode: String?, partDescription: String?, partCost: String, totalPartCostValue: String)

    suspend fun deletePart(id: Long)
    suspend fun deleteAllParts()
    suspend fun getPartById(id: Long): PartEntity?
    fun getAllParts(): LiveData<List<PartEntity>>
    suspend fun getPartsForServiceOrder(serviceOrderId: Long): LiveData<List<PartEntity>>
}
