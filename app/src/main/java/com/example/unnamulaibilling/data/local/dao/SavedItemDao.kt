package com.example.unnamulaibilling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unnamulaibilling.data.local.entity.SavedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedItemDao {
    @Query("SELECT * FROM saved_items ORDER BY descriptionAndMaterial ASC")
    fun getAllSavedItems(): Flow<List<SavedItemEntity>>

    @Query("SELECT * FROM saved_items WHERE descriptionAndMaterial LIKE '%' || :searchQuery || '%'")
    fun searchSavedItems(searchQuery: String): Flow<List<SavedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SavedItemEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(items: List<SavedItemEntity>)

    @Query("DELETE FROM saved_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)
}
