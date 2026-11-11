package com.example.unnamulaibilling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_items")
data class SavedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descriptionAndMaterial: String,
    val hsnCode: String,
    val unit: String,
    val rate: Double
)
