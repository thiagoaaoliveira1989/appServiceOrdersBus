package com.maxcred.orderservice.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_order",
    foreignKeys = [ForeignKey(
        entity = BusEntity::class,
        parentColumns = ["id"],
        childColumns = ["busId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ServiceOrderEntity(
    @PrimaryKey(autoGenerate = true) val orderNumber: Long = 0,
    val kmBus: String,
    val startDate: String,
    val endDate: String,
    val description: String?,
    val mechanic: String,
    val encarregado: String,
    val busId: Long
)
