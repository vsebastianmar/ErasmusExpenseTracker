package com.example.erasmusexpensetracker.ui.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.erasmusexpensetracker.data.local.entities.Category
import com.example.erasmusexpensetracker.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseListScreen(viewModel: com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel, navController: NavController) {
    ExpenseList(
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
fun ExpenseList(viewModel: com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel, navController: NavController) {
    val expenses by viewModel.expenses.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()

    val currentCalendar = remember { Calendar.getInstance() }
    var selectedMonth by rememberSaveable { mutableIntStateOf(currentCalendar.get(Calendar.MONTH)) }
    var selectedYear by rememberSaveable { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }
    val monthName = DateFormatSymbols().months[selectedMonth]

    // 🟢 Aplicar TODOS los filtros: mes/año + tipo + título + categoría
    val filteredExpenses = expenses
        .filter { expense ->
            val cal = Calendar.getInstance().apply { timeInMillis = expense.date }
            cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == selectedYear
        }
        .filter { expense ->
            when (viewModel.typeFilter) {
                "Income" -> expense.income
                "Expenses" -> !expense.income
                else -> true
            }
        }
        .filter { expense ->
            viewModel.titleFilter.isBlank() || expense.title.contains(viewModel.titleFilter, ignoreCase = true)
        }
        .filter { expense ->
            viewModel.categoryFilter == -1 || expense.categoryId == viewModel.categoryFilter
        }

    Column(modifier = Modifier.fillMaxSize()) {

        // Month & Year Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.YEAR, selectedYear)
                    add(Calendar.MONTH, -1)
                }
                selectedMonth = cal.get(Calendar.MONTH)
                selectedYear = cal.get(Calendar.YEAR)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }

            Text("$monthName $selectedYear", style = MaterialTheme.typography.titleMedium)

            IconButton(onClick = {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.YEAR, selectedYear)
                    add(Calendar.MONTH, 1)
                }
                selectedMonth = cal.get(Calendar.MONTH)
                selectedYear = cal.get(Calendar.YEAR)
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }

        // 🧭 Filtros
        Column(modifier = Modifier.padding(8.dp)) {

            // 🔍 Título
            OutlinedTextField(
                value = viewModel.titleFilter,
                onValueChange = { viewModel.updateTitleFilter(it) },
                label = { Text("Search name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🔁 Tipo: Todos / Ingresos / Gastos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("All", "Income", "Expenses").forEach { option ->
                    FilterChip(
                        selected = viewModel.typeFilter == option,
                        onClick = { viewModel.updateTypeFilter(option) },
                        label = { Text(option) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🧾 Categoría
            val categoryList = listOf(Category(-1, "Todas")) + categories
            var expanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedCategory = categoryList.find { it.id == viewModel.categoryFilter }?.name ?: "Todas"
                    Text("Categoría: $selectedCategory")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categoryList.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                viewModel.updateCategoryFilter(cat.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // 📋 Lista de gastos filtrados
        LazyColumn {
            items(filteredExpenses, key = { it.id }) { expense ->
                val categoryName = categories.find { it.id == expense.categoryId }?.name ?: "Unknown"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (expense.income) Color(0xFFD0F0C0) else Color(0xFFFFCDD2)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(text = "${expense.title}: ${expense.amount}€ - $categoryName")
                            Text(
                                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                                    Date(expense.date)
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = {
                                navController.navigate(
                                    Screen.EditExpense.passId(expense.id))

                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Expense")
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteExpense(expense)

                                    val cal = Calendar.getInstance().apply { timeInMillis = expense.date }
                                    viewModel.checkBudgetStatus(
                                        categoryId = expense.categoryId,
                                        month = cal.get(Calendar.MONTH),
                                        year = cal.get(Calendar.YEAR)
                                    )
                                }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Expense")
                            }
                        }
                    }
                }
            }
        }
    }
}
