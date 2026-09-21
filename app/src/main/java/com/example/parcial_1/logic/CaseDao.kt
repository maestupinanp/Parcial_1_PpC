package com.example.parcial_1.logic

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_1.model.Case
import com.example.parcial_1.model.Evidence
import com.example.parcial_1.model.Finding
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY startDate DESC")
    fun getAllCases(): Flow<List<Case>>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getCaseById(id: Int): Case?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: Case): Long

    @Update
    suspend fun updateCase(case: Case)

    @Delete
    suspend fun deleteCase(case: Case)

    @Query("SELECT COUNT(*) FROM cases WHERE status = 'IN_INVESTIGATION'")
    fun getActiveCasesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cases WHERE status = 'CLOSED'")
    fun getClosedCasesCount(): Flow<Int>

    // Findings
    @Query("SELECT * FROM findings WHERE caseId = :caseId ORDER BY date DESC")
    fun getFindingsForCase(caseId: Int): Flow<List<Finding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinding(finding: Finding): Long

    // Evidence
    @Query("SELECT * FROM evidence WHERE caseId = :caseId ORDER BY date DESC")
    fun getEvidenceForCase(caseId: Int): Flow<List<Evidence>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: Evidence): Long
}
