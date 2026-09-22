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
import com.example.parcial_1.ui.components.AppHeader
import com.example.parcial_1.ui.components.StatusBadge
import com.example.parcial_1.viewmodel.CaseViewModel
import com.example.parcial_1.model.Case
import com.example.parcial_1.model.CaseStatus
import com.example.parcial_1.ui.theme.CaseTrackTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaseListScreen(
    viewModel: CaseViewModel, 
    initialFilter: String? = null,
    onCaseClick: (Int) -> Unit
) {
    val cases by viewModel.allCases.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    // 0: Todos, 1: Activos, 2: Cerrados
    var selectedTabIndex by remember(initialFilter) { 
        mutableIntStateOf(
            when (initialFilter) {
                "IN_INVESTIGATION" -> 1
                "CLOSED" -> 2
                else -> 0
            }
        )
    }
    
    val filteredCases = cases.filter { case ->
        val matchesSearch = case.title.contains(searchQuery, ignoreCase = true) || 
                           case.clientName.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTabIndex) {
            1 -> case.status == CaseStatus.IN_INVESTIGATION
            2 -> case.status == CaseStatus.CLOSED
            else -> true
        }
        matchesSearch && matchesTab
    }

    CaseListContent(
        cases = filteredCases,
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { selectedTabIndex = it },
        onCaseClick = onCaseClick
    )
}

@Composable
fun CaseListContent(
    cases: List<Case>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onCaseClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(subtitle = "Gestión de casos y expedientes")

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Mis casos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por título o cliente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            val tabs = listOf("Todos", "Activos", "Cerrados")
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(title) }
                    )
                }
            }
        }

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
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                StatusBadge(status = case.status)

            }
        }
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
        CaseListContent(sampleCases, "", {}, 0, {}, {})
    }
}
