package com.alpharays.qrscanner.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alpharays.qrscanner.data.entities.QrDao
import com.alpharays.qrscanner.data.entities.QrHistory

@Database(entities = [QrHistory::class], version = 1, exportSchema = false)
abstract class QrDatabase : RoomDatabase() {

    abstract fun qrDao(): QrDao

    companion object {
        @Volatile
        private var INSTANCE: QrDatabase? = null
        fun getDatabase(context: Context): QrDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}