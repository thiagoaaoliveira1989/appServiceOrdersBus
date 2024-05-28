package com.maxcred.orderservice.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maxcred.orderservice.data.db.dao.BusDAO
import com.maxcred.orderservice.data.db.dao.PartDAO
import com.maxcred.orderservice.data.db.dao.RegisterDAO
import com.maxcred.orderservice.data.db.dao.ServiceOrderDAO
import com.maxcred.orderservice.data.db.entity.BusEntity
import com.maxcred.orderservice.data.db.entity.PartEntity
import com.maxcred.orderservice.data.db.entity.RegisterEntity
import com.maxcred.orderservice.data.db.entity.ServiceOrderEntity

@Database(
    entities = [RegisterEntity::class, BusEntity::class, ServiceOrderEntity::class, PartEntity::class],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract val busDAO: BusDAO
    abstract val registerDAO: RegisterDAO
    abstract val serviceOrderDAO: ServiceOrderDAO
    abstract val partDAO: PartDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
