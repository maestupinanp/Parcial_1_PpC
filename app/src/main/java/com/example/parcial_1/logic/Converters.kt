package com.example.parcial_1.logic

import androidx.room.TypeConverter
import com.example.parcial_1.model.CaseStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: CaseStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): CaseStatus {
        return CaseStatus.valueOf(value)
    }
}
