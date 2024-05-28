package com.maxcred.orderservice.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "part",
    foreignKeys = [ForeignKey(
        entity = ServiceOrderEntity::class,
        parentColumns = ["id"],
        childColumns = ["serviceOrderId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "partQty")
    val partQty: String,
    @ColumnInfo(name = "partCode")
    val partCode: String?,
    @ColumnInfo(name = "partDescription")
    val partDescription: String?,
    @ColumnInfo(name = "partCost")
    val partCost: String,
    @ColumnInfo(name = "totalPartCostValue")
    val totalPartCostValue: String,
    @ColumnInfo(name = "serviceOrderId")
    val serviceOrderId: Long // Foreign key to ServiceOrderEntity
)
