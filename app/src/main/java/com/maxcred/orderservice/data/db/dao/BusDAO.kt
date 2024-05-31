package com.maxcred.orderservice.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maxcred.orderservice.data.db.entity.BusEntity

@Dao
interface BusDAO {

    // Verificar se a placa existe, retorna um booleano
    @Query("SELECT COUNT(*) > 0 FROM bus WHERE licensePlate = :licensePlate")
    suspend fun isLicensePlateExists(licensePlate: String): Boolean

    // Pegar id do ônibus pela placa
    @Query("SELECT id FROM bus WHERE licensePlate = :licensePlate LIMIT 1")
    suspend fun getBusIdByPlate(licensePlate: String): Long

    // Retorna toda entidade do ônibus pela placa
    @Query("SELECT * FROM bus WHERE licensePlate = :licensePlate LIMIT 1")
    suspend fun getBusByPlate(licensePlate: String): BusEntity?

    // Pegar o ônibus pelo id
    @Query("SELECT * FROM bus WHERE id = :id LIMIT 1")
    suspend fun getBusById(id: Long): BusEntity?

    // Pegar todos os ônibus
    @Query("SELECT * FROM bus")
    fun getAllBus(): LiveData<List<BusEntity>>

    // Cadastrar ônibus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBus(bus: BusEntity): Long

    // Atualizar ônibus
    @Update
    suspend fun updateBus(bus: BusEntity): Int

    // Deletar ônibus pelo id
    @Query("DELETE FROM bus WHERE id = :id")
    suspend fun deleteBus(id: Long)
}
