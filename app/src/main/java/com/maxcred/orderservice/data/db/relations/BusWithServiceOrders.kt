package com.maxcred.orderservice.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity

data class BusWithServiceOrders(
    @Embedded val bus: BusEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "busId"
    )
    val serviceOrders: List<ServiceOrderEntity>
)
