package com.example.parcial_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parcial_1.model.Case
import com.example.parcial_1.model.CaseStatus
import com.example.parcial_1.ui.components.AppHeader
import com.example.parcial_1.viewmodel.CaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCaseScreen(
    viewModel: CaseViewModel,
    caseId: Int? = null,
    onSaveSuccess: () -> Unit,
    onDeleteSuccess: () -> Unit
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(CaseStatus.IN_INVESTIGATION) }
    var closingPrecedent by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var caseNumber by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentCase by remember { mutableStateOf<Case?>(null) }

    LaunchedEffect(caseId) {
        if (caseId != null) {
            val existingCase = viewModel.getCaseById(caseId)
            if (existingCase != null) {
                currentCase = existingCase
                title = existingCase.title
                description = existingCase.description
                clientName = existingCase.clientName
                status = existingCase.status
                closingPrecedent = existingCase.closingPrecedent
                startDate = existingCase.startDate
                caseNumber = existingCase.caseNumber
            }
        }
    }

    if(showDeleteDialog){
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Caso")},
            text = { Text("¿Estás seguro de que deseas eliminar este caso?")},
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteCase(currentCase!!)
                    onDeleteSuccess()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar definitivamente")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                AppHeader(subtitle = if (caseId == null) "Registrar nuevo expediente" else "Actualizar información del caso")
                TopAppBar(
                    title = { Text(if (caseId == null) "Nuevo Caso" else "Editar Caso") },
                    actions = {
                        if (caseId != null) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                )
            }
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
                CaseStatus.entries
                    .filter { if (caseId == null) it == CaseStatus.IN_INVESTIGATION else true }
                    .forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.displayName) }
                        )
                    }
            }

            if (status == CaseStatus.CLOSED) {
                OutlinedTextField(
                    value = closingPrecedent,
                    onValueChange = { closingPrecedent = it },
                    label = { Text("Argumento de cierre / Precedente") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = { Text("Describe el motivo del cierre del caso...") },
                    supportingText = {
                        if (closingPrecedent.isBlank()) {
                            Text("Este campo es obligatorio para cerrar el caso", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    isError = closingPrecedent.isBlank()
                )
            }
            
            Button(
                onClick = {
                    val finalCase = Case(
                        id = caseId ?: 0,
                        title = title,
                        description = description,
                        clientName = clientName,
                        status = status,
                        closingPrecedent = if (status == CaseStatus.CLOSED) closingPrecedent else "",
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
                enabled = title.isNotBlank() && 
                        clientName.isNotBlank() && 
                        (status != CaseStatus.CLOSED || closingPrecedent.isNotBlank())
            ) {
                Text("Guardar")
            }
        }
    }
}
