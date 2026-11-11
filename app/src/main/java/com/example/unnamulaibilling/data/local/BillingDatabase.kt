package com.example.unnamulaibilling.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.unnamulaibilling.data.local.dao.SavedItemDao
import com.example.unnamulaibilling.data.local.entity.SavedItemEntity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

@Database(entities = [SavedItemEntity::class], version = 1, exportSchema = false)
abstract class BillingDatabase : RoomDatabase() {
    abstract fun savedItemDao(): SavedItemDao

    companion object {
        @Volatile
        private var INSTANCE: BillingDatabase? = null

        fun getDatabase(context: Context): BillingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillingDatabase::class.java,
                    "billing_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute {
                            runBlocking {
                                INSTANCE?.savedItemDao()?.insertItems(
                                    listOf(
                                        SavedItemEntity(descriptionAndMaterial = "JCB Rent / Hour", hsnCode = "997319", unit = "Hrs", rate = 1200.0),
                                        SavedItemEntity(descriptionAndMaterial = "Hitachi Ex 70 / Hour", hsnCode = "997319", unit = "Hrs", rate = 1800.0),
                                        SavedItemEntity(descriptionAndMaterial = "Lorry Earth Transport Load", hsnCode = "996511", unit = "Trip", rate = 2500.0),
                                        SavedItemEntity(descriptionAndMaterial = "Gravel Soil Supply", hsnCode = "2505", unit = "Unit", rate = 3500.0),
                                        SavedItemEntity(descriptionAndMaterial = "Blue Metal 20mm", hsnCode = "2517", unit = "Unit", rate = 4200.0)
                                    )
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
