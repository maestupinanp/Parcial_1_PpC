package com.example.parcial_1.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_1.data.model.Case
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY startDate DESC")
    fun getAllCases(): Flow<List<Case>>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getCaseById(id: Int): Case?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: Case)

    @Update
    suspend fun updateCase(case: Case)

    @Delete
    suspend fun deleteCase(case: Case)

    @Query("SELECT COUNT(*) FROM cases WHERE status = 'IN_INVESTIGATION'")
    fun getActiveCasesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cases WHERE status = 'CLOSED'")
    fun getClosedCasesCount(): Flow<Int>
}
