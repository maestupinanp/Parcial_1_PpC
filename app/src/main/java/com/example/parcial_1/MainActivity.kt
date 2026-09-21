package com.example.parcial_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parcial_1.data.local.AppDatabase
import com.example.parcial_1.data.repository.CaseRepository
import com.example.parcial_1.ui.screens.AddEditCaseScreen
import com.example.parcial_1.ui.screens.CaseDetailScreen
import com.example.parcial_1.ui.screens.CaseListScreen
import com.example.parcial_1.ui.screens.HomeScreen
import com.example.parcial_1.ui.theme.CaseTrackTheme
import com.example.parcial_1.ui.viewmodel.CaseViewModel
import com.example.parcial_1.ui.viewmodel.CaseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = CaseRepository(database.caseDao())
        val factory = CaseViewModelFactory(repository)

        setContent {
            CaseTrackTheme {
                CaseTrackApp(factory)
            }
        }
    }
}

sealed class Screen(val route: String, val label: String = "", val icon: @Composable () -> Unit = {}) {
    object Home : Screen("home", "Inicio", { Icon(Icons.Default.Home, contentDescription = null) })
    object Cases : Screen("cases", "Casos", { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) })
    object AddCase : Screen("add_case")
    object EditCase : Screen("edit_case/{caseId}")
    object CaseDetail : Screen("case_detail/{caseId}")
}

@Composable
fun CaseTrackApp(factory: CaseViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: CaseViewModel = viewModel(factory = factory)
    val bottomNavItems = listOf(Screen.Home, Screen.Cases)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Solo mostrar bottom bar en pantallas principales
            val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = screen.icon,
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == Screen.Home.route || currentRoute == Screen.Cases.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddCase.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Caso")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel) }
            composable(Screen.Cases.route) { 
                CaseListScreen(viewModel, onCaseClick = { id -> 
                    navController.navigate("case_detail/$id")
                }) 
            }
            composable(Screen.AddCase.route) { 
                AddEditCaseScreen(viewModel, onSaveSuccess = { 
                    navController.navigateUp()
                }) 
            }
            composable(
                route = Screen.EditCase.route,
                arguments = listOf(navArgument("caseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getInt("caseId")
                AddEditCaseScreen(viewModel, caseId = caseId, onSaveSuccess = {
                    navController.navigateUp()
                })
            }
            composable(
                route = Screen.CaseDetail.route,
                arguments = listOf(navArgument("caseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getInt("caseId") ?: 0
                CaseDetailScreen(
                    viewModel = viewModel,
                    caseId = caseId,
                    onBack = { navController.navigateUp() },
                    onEdit = { id -> navController.navigate("edit_case/$id") }
                )
            }
        }
    }
}
