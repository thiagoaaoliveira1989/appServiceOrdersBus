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
    var kmBus: String,
    var startDate: String,
    var endDate: String,
    var description: String?,
    var mechanic: String,
    var encarregado: String,
    val busId: Long
)
