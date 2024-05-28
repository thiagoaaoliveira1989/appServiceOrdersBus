package com.maxcred.orderservice.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "register")
data class RegisterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "username")
    val username: String?,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password")
    val password: String
)
