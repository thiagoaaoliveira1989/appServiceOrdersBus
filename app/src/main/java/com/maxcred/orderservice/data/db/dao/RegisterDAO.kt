package com.maxcred.orderservice.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.data.db.entity.RegisterEntity

@Dao
interface RegisterDAO {


    @Query("SELECT COUNT(*) FROM register WHERE email = :email")
    suspend fun isEmailExists(email: String): Boolean

    @Insert
    suspend fun insert(register: RegisterEntity): Long

    @Update
    suspend fun update(register: RegisterEntity)

    @Query("DELETE FROM register WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM register")
    suspend fun deleteAll()

    @Query("SELECT * FROM register")
    fun getAllRegister(): LiveData<List<RegisterEntity>>
}
