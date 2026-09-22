package com.example.parcial_1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parcial_1.logic.CaseRepository
import com.example.parcial_1.model.Case
import com.example.parcial_1.model.Evidence
import com.example.parcial_1.model.Finding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CaseViewModel(private val repository: CaseRepository) : ViewModel() {

    val allCases: StateFlow<List<Case>> = repository.allCases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeCasesCount: StateFlow<Int> = repository.activeCasesCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val closedCasesCount: StateFlow<Int> = repository.closedCasesCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun insertCase(case: Case, onNameExists: () -> Unit = {}, onSuccess: () -> Unit = {}) = viewModelScope.launch {
        val exists = allCases.value.any { it.title.equals(case.title, ignoreCase = true) }
        if (exists) {
            onNameExists()
        } else {
            repository.insertCase(case)
            onSuccess()
        }
    }

    fun updateCase(case: Case, onNameExists: () -> Unit = {}, onSuccess: () -> Unit = {}) = viewModelScope.launch {
        val exists = allCases.value.any { 
            it.title.equals(case.title, ignoreCase = true) && it.id != case.id 
        }
        if (exists) {
            onNameExists()
        } else {
            repository.updateCase(case)
            onSuccess()
        }
    }

    fun deleteCase(case: Case) = viewModelScope.launch {
        repository.deleteCase(case)
    }

    suspend fun getCaseById(id: Int): Case? {
        return repository.getCaseById(id)
    }

    // Findings
    fun getFindingsForCase(caseId: Int): Flow<List<Finding>> {
        return repository.getFindingsForCase(caseId)
    }

    fun insertFinding(finding: Finding) = viewModelScope.launch {
        repository.insertFinding(finding)
    }

    // Evidence
    fun getEvidenceForCase(caseId: Int): Flow<List<Evidence>> {
        return repository.getEvidenceForCase(caseId)
    }

    fun insertEvidence(evidence: Evidence) = viewModelScope.launch {
        repository.insertEvidence(evidence)
    }
}

class CaseViewModelFactory(private val repository: CaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
