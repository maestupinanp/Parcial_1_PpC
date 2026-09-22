package com.example.parcial_1.logic

import com.example.parcial_1.model.Case
import com.example.parcial_1.model.Evidence
import com.example.parcial_1.model.Finding
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class CaseRepository(private val caseDao: CaseDao) {
    val allCases: Flow<List<Case>> = caseDao.getAllCases()
    val activeCasesCount: Flow<Int> = caseDao.getActiveCasesCount()
    val closedCasesCount: Flow<Int> = caseDao.getClosedCasesCount()

    suspend fun getCaseById(id: Int): Case? = caseDao.getCaseById(id)
    
    suspend fun insertCase(case: Case) {
        val cases = caseDao.getAllCasesList()
        val nextNumber = cases.size + 1
        val formattedNumber = "Caso #${String.format(Locale.US, "%03d", nextNumber)}"
        caseDao.insertCase(case.copy(caseNumber = formattedNumber))
    }

    suspend fun updateCase(case: Case) = caseDao.updateCase(case)

    suspend fun deleteCase(case: Case) {
        caseDao.deleteCase(case)
        reindexCases()
    }

    private suspend fun reindexCases() {
        val cases = caseDao.getAllCasesList()
        val updatedCases = cases.mapIndexed { index, c ->
            val newNumber = "Caso #${String.format(Locale.US, "%03d", index + 1)}"
            c.copy(caseNumber = newNumber)
        }
        caseDao.updateCases(updatedCases)
    }

    // Findings
    fun getFindingsForCase(caseId: Int): Flow<List<Finding>> = caseDao.getFindingsForCase(caseId)
    suspend fun insertFinding(finding: Finding) = caseDao.insertFinding(finding)

    // Evidence
    fun getEvidenceForCase(caseId: Int): Flow<List<Evidence>> = caseDao.getEvidenceForCase(caseId)
    suspend fun insertEvidence(evidence: Evidence) = caseDao.insertEvidence(evidence)
}
