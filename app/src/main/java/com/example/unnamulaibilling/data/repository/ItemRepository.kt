package com.example.unnamulaibilling.data.repository

import com.example.unnamulaibilling.data.local.dao.SavedItemDao
import com.example.unnamulaibilling.data.local.entity.SavedItemEntity
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val savedItemDao: SavedItemDao) {

    val allSavedItems: Flow<List<SavedItemEntity>> = savedItemDao.getAllSavedItems()

    fun searchItems(query: String): Flow<List<SavedItemEntity>> {
        return savedItemDao.searchSavedItems(query)
    }

    suspend fun insertItem(item: SavedItemEntity) {
        savedItemDao.insertItem(item)
    }

    suspend fun deleteItem(itemId: Long) {
        savedItemDao.deleteItem(itemId)
    }
}
