package com.maxcred.orderservice.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceOrderWithParts(
    @Embedded val serviceOrder: ServiceOrderEntity,
    @Relation(
        parentColumn = "orderNumber",
        entityColumn = "serviceOrderId"
    )
    val parts: List<PartEntity>
)
