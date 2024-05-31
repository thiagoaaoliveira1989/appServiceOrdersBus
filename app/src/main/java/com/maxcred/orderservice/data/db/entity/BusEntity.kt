package com.maxcred.orderservice.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bus")
data class BusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "vehicle")
    val vehicle: String?,
    @ColumnInfo(name = "licensePlate")
    val licensePlate: String,
    @ColumnInfo(name = "numberCar")
    val numberCar: String,
)
