package com.maxcred.orderservice.repository

import androidx.lifecycle.LiveData
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.data.db.entity.RegisterEntity

data class RegisterResult(
    val id: Long,
    val username: String,
    val email: String,
    val password: String
)

interface RegisterRepository {
    suspend fun isEmailExists(email: String): Boolean
    suspend fun insertRegister(username: String, email: String, password: String): RegisterResult
    suspend fun updateRegister(id: Long, username: String, email: String, password: String)
    suspend fun deleteRegister(id: Long)
    suspend fun deleteAll()
    fun getAllRegister(): LiveData<List<RegisterEntity>>
}
