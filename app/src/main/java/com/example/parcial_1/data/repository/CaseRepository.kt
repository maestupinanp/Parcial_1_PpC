package com.example.parcial_1.data.repository

import com.example.parcial_1.data.local.CaseDao
import com.example.parcial_1.data.model.Case
import kotlinx.coroutines.flow.Flow

class CaseRepository(private val caseDao: CaseDao) {
    val allCases: Flow<List<Case>> = caseDao.getAllCases()
    val activeCasesCount: Flow<Int> = caseDao.getActiveCasesCount()
    val closedCasesCount: Flow<Int> = caseDao.getClosedCasesCount()

    suspend fun getCaseById(id: Int): Case? = caseDao.getCaseById(id)
    suspend fun insertCase(case: Case) = caseDao.insertCase(case)
    suspend fun updateCase(case: Case) = caseDao.updateCase(case)
    suspend fun deleteCase(case: Case) = caseDao.deleteCase(case)
}
