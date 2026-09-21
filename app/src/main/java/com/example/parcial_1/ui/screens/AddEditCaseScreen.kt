package com.example.parcial_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parcial_1.data.model.Case
import com.example.parcial_1.data.model.CaseStatus
import com.example.parcial_1.ui.viewmodel.CaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCaseScreen(
    viewModel: CaseViewModel,
    caseId: Int? = null,
    onSaveSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(CaseStatus.IN_INVESTIGATION) }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var caseNumber by remember { mutableStateOf("") }
    
    LaunchedEffect(caseId) {
        if (caseId != null) {
            val existingCase = viewModel.getCaseById(caseId)
            if (existingCase != null) {
                title = existingCase.title
                description = existingCase.description
                clientName = existingCase.clientName
                status = existingCase.status
                startDate = existingCase.startDate
                caseNumber = existingCase.caseNumber
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (caseId == null) "Nuevo Caso" else "Editar Caso") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Cliente / Solicitante") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text("Estado", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CaseStatus.entries.forEach { s ->
                    FilterChip(
                        selected = status == s,
                        onClick = { status = s },
                        label = { Text(s.displayName) }
                    )
                }
            }
            
            Button(
                onClick = {
                    val finalCase = Case(
                        id = caseId ?: 0,
                        title = title,
                        description = description,
                        clientName = clientName,
                        status = status,
                        startDate = startDate,
                        caseNumber = if (caseNumber.isBlank() && caseId == null) {
                            "Caso #${(100..999).random()}"
                        } else {
                            caseNumber
                        }
                    )
                    if (caseId == null) {
                        viewModel.insertCase(finalCase)
                    } else {
                        viewModel.updateCase(finalCase)
                    }
                    onSaveSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && clientName.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}
