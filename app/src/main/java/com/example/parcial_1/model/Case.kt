package com.example.parcial_1.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class CaseStatus(val displayName: String) {
    IN_INVESTIGATION("En investigación"),
    CLOSED("Cerrado")
}

@Entity(tableName = "cases")
data class Case(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val clientName: String,
    val startDate: Long = System.currentTimeMillis(),
    val status: CaseStatus = CaseStatus.IN_INVESTIGATION,
    val caseNumber: String = "",
    val findings: String = "",
    val evidence: String = ""
)
