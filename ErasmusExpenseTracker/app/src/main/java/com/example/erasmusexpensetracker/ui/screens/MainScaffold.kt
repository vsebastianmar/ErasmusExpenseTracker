package com.example.erasmusexpensetracker.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.erasmusexpensetracker.ui.navigation.AppNavGraph
import com.example.erasmusexpensetracker.ui.navigation.Screen
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: ExpenseViewModel,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle snackbar messages from ViewModel
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Determine current screen
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    val fabVisible = currentRoute in listOf(
        Screen.Categories.route,
        Screen.Expenses.route,
        Screen.Budgets.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Navigation",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = currentRoute == Screen.Dashboard.route,
                    onClick = {
                        navController.navigate(Screen.Dashboard.route)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Expenses") },
                    selected = currentRoute == Screen.Expenses.route,
                    onClick = {
                        navController.navigate(Screen.Expenses.route) {
                            popUpTo(Screen.Expenses.route) { inclusive = true }
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Categories") },
                    selected = currentRoute == Screen.Categories.route,
                    onClick = {
                        navController.navigate(Screen.Categories.route)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Budgets") },
                    selected = currentRoute == Screen.Budgets.route,
                    onClick = {
                        navController.navigate(Screen.Budgets.route)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)) {
                                    append("Expense")
                                }
                                append(" ")
                                withStyle(SpanStyle(color = Color(0xFFF44336), fontWeight = FontWeight.SemiBold)) {
                                    append("Tracker")
                                }
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 22.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (fabVisible) {
                    FloatingActionButton(
                        onClick = {
                            when (currentRoute) {
                                Screen.Categories.route -> navController.navigate(Screen.AddCategory.route)
                                Screen.Expenses.route -> navController.navigate(Screen.AddExpense.route)
                                Screen.Budgets.route -> navController.navigate(Screen.AddBudget.route)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            AppNavGraph(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
