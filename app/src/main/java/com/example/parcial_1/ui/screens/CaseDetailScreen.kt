package com.example.parcial_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcial_1.model.Case
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
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val scrollState = rememberScrollState()

    LaunchedEffect(caseId) {
        case = viewModel.getCaseById(caseId)
    }

    Scaffold(
        topBar = {
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
                Text(text = dateFormat.format(Date(c.startDate)), color = MaterialTheme.colorScheme.onSurfaceVariant)

                HorizontalDivider()

                Text(text = "Información general", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                DetailItem(label = "Cliente / Solicitante", value = c.clientName)
                DetailItem(label = "Descripción", value = c.description)
                
                HorizontalDivider()
                
                Text(text = "Investigación", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                DetailItem(label = "Hallazgos", value = c.findings.ifBlank { "Sin hallazgos registrados" })
                DetailItem(label = "Evidencias", value = c.evidence.ifBlank { "Sin evidencias registradas" })
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
