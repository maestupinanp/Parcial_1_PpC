package com.example.parcial_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcial_1.model.Case
import com.example.parcial_1.model.Evidence
import com.example.parcial_1.model.Finding
import com.example.parcial_1.ui.components.AppHeader
import com.example.parcial_1.viewmodel.CaseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    viewModel: CaseViewModel,
    caseId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    var case by remember { mutableStateOf<Case?>(null) }
    val findings by viewModel.getFindingsForCase(caseId).collectAsState(initial = emptyList())
    val evidence by viewModel.getEvidenceForCase(caseId).collectAsState(initial = emptyList())
    
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }
    val scrollState = rememberScrollState()

    var showAddFindingDialog by remember { mutableStateOf(false) }
    var showAddEvidenceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(caseId) {
        case = viewModel.getCaseById(caseId)
    }

    Scaffold(
        topBar = {
            Column {
                AppHeader(subtitle = "Resumen detallado de la investigación")
                TopAppBar(
                    title = { Text("Detalle del Caso") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEdit(caseId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                )
            }
        }
    ) { padding ->
        case?.let { c ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = c.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    StatusBadge(status = c.status)
                }
                
                Text(text = "Caso #${c.id}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "Iniciado: " + SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(c.startDate)), 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text(text = "Información general", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                DetailItem(label = "Cliente / Solicitante", value = c.clientName)
                DetailItem(label = "Descripción", value = c.description)
                
                HorizontalDivider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Hallazgos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { showAddFindingDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Hallazgo")
                    }
                }
                
                if (findings.isEmpty()) {
                    Text("No hay hallazgos registrados", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    findings.forEach { finding ->
                        HistoryItem(
                            number = finding.number,
                            date = dateFormat.format(Date(finding.date)),
                            description = finding.description
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Evidencias", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = { showAddEvidenceDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Evidencia")
                    }
                }

                if (evidence.isEmpty()) {
                    Text("No hay evidencias registradas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    evidence.forEach { item ->
                        HistoryItem(
                            number = item.number,
                            date = dateFormat.format(Date(item.date)),
                            description = item.description
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    if (showAddFindingDialog) {
        AddUpdateDialog(
            title = "Nuevo Hallazgo",
            onDismiss = { showAddFindingDialog = false },
            onConfirm = { number, desc ->
                viewModel.insertFinding(Finding(caseId = caseId, number = number, description = desc))
                showAddFindingDialog = false
            }
        )
    }

    if (showAddEvidenceDialog) {
        AddUpdateDialog(
            title = "Nueva Evidencia",
            onDismiss = { showAddEvidenceDialog = false },
            onConfirm = { number, desc ->
                viewModel.insertEvidence(Evidence(caseId = caseId, number = number, description = desc))
                showAddEvidenceDialog = false
            }
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HistoryItem(number: String, date: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Nro: $number", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(text = date, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun AddUpdateDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Número / Código") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(number, description) },
                enabled = number.isNotBlank() && description.isNotBlank()
            ) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
