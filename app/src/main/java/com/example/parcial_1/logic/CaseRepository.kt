package com.example.parcial_1.logic

import com.example.parcial_1.model.Case
import com.example.parcial_1.model.Evidence
import com.example.parcial_1.model.Finding
import kotlinx.coroutines.flow.Flow

class CaseRepository(private val caseDao: CaseDao) {
    val allCases: Flow<List<Case>> = caseDao.getAllCases()
    val activeCasesCount: Flow<Int> = caseDao.getActiveCasesCount()
    val closedCasesCount: Flow<Int> = caseDao.getClosedCasesCount()

    suspend fun getCaseById(id: Int): Case? = caseDao.getCaseById(id)
    suspend fun insertCase(case: Case) = caseDao.insertCase(case)
    suspend fun updateCase(case: Case) = caseDao.updateCase(case)
    suspend fun deleteCase(case: Case) = caseDao.deleteCase(case)

    // Findings
    fun getFindingsForCase(caseId: Int): Flow<List<Finding>> = caseDao.getFindingsForCase(caseId)
    suspend fun insertFinding(finding: Finding) = caseDao.insertFinding(finding)

    // Evidence
    fun getEvidenceForCase(caseId: Int): Flow<List<Evidence>> = caseDao.getEvidenceForCase(caseId)
    suspend fun insertEvidence(evidence: Evidence) = caseDao.insertEvidence(evidence)
}
