package com.example.parcial_1.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "findings",
    foreignKeys = [
        ForeignKey(
            entity = Case::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Finding(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val caseId: Int,
    val date: Long = System.currentTimeMillis(),
    val number: String,
    val description: String
)
