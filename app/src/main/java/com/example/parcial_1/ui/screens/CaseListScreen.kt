package com.example.parcial_1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcial_1.data.model.Case
import com.example.parcial_1.data.model.CaseStatus
import com.example.parcial_1.ui.theme.CaseTrackTheme
import com.example.parcial_1.ui.viewmodel.CaseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaseListScreen(viewModel: CaseViewModel, onCaseClick: (Int) -> Unit) {
    val cases by viewModel.allCases.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredCases = cases.filter { 
        it.title.contains(searchQuery, ignoreCase = true) || 
        it.clientName.contains(searchQuery, ignoreCase = true) 
    }

    CaseListContent(
        cases = filteredCases,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onCaseClick = onCaseClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseListContent(
    cases: List<Case>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCaseClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mis casos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar por título o cliente...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cases) { case ->
                CaseItem(case = case, onClick = { onCaseClick(case.id) })
            }
        }
    }
}

@Composable
fun CaseItem(case: Case, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for image
            Surface(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "CASE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = case.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    StatusBadge(status = case.status)
                }
                Text(
                    text = "Caso #${case.id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(Date(case.startDate)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: CaseStatus) {
    val color = if (status == CaseStatus.IN_INVESTIGATION) Color(0xFFFFECB3) else Color(0xFFC8E6C9)
    val textColor = if (status == CaseStatus.IN_INVESTIGATION) Color(0xFF827717) else Color(0xFF2E7D32)
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CaseListPreview() {
    val sampleCases = listOf(
        Case(1, "Robo en la galería", "Robo de arte", "Galería de Arte", status = CaseStatus.IN_INVESTIGATION),
        Case(2, "Fraude corporativo", "Fraude millonario", "Empresa X", status = CaseStatus.IN_INVESTIGATION),
        Case(3, "Desaparición", "Persona perdida", "Familia Pérez", status = CaseStatus.CLOSED)
    )
    CaseTrackTheme {
        CaseListContent(sampleCases, "", {}, {})
    }
}
