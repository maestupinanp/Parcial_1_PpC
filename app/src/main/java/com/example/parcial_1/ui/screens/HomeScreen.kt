package com.example.parcial_1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcial_1.ui.theme.CaseTrackTheme
import com.example.parcial_1.ui.viewmodel.CaseViewModel

@Composable
fun HomeScreen(viewModel: CaseViewModel) {
    val activeCount by viewModel.activeCasesCount.collectAsState()
    val closedCount by viewModel.closedCasesCount.collectAsState()

    HomeScreenContent(activeCount, closedCount)
}

@Composable
fun HomeScreenContent(activeCount: Int, closedCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CaseTrack",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tus casos, siempre bajo control",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Perfil",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Hola, Detective",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "La verdad siempre deja rastro",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        val cards = listOf(
            HomeCardData("Mis casos", "$activeCount activos", Icons.Default.Folder, MaterialTheme.colorScheme.primaryContainer),
            HomeCardData("Nuevo caso", "Registrar ahora", Icons.Default.Add, MaterialTheme.colorScheme.secondaryContainer),
            HomeCardData("Estadísticas", "Tu actividad", Icons.Default.BarChart, MaterialTheme.colorScheme.tertiaryContainer),
            HomeCardData("Casos cerrados", "$closedCount finalizados", Icons.Default.AccessTime, MaterialTheme.colorScheme.surfaceVariant)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(cards) { card ->
                HomeCard(card)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "\"Observar. Analizar. Concluir.\"",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class HomeCardData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(data: HomeCardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = data.color)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = data.icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Column {
                Text(text = data.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = data.subtitle, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CaseTrackTheme {
        HomeScreenContent(5, 3)
    }
}
