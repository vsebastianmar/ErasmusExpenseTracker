package com.example.erasmusexpensetracker.ui.screens.budgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.erasmusexpensetracker.data.local.entities.Budget
import com.example.erasmusexpensetracker.data.local.entities.Category
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel
import com.example.erasmusexpensetracker.monthName
import java.util.Calendar


@Composable
fun AddBudgetScreen(viewModel: ExpenseViewModel, navController: NavController) {
    val categories by viewModel.categories.collectAsState(emptyList())

    AddBudgetForm(
        categories = categories,
        onAdd = { budget ->
            viewModel.addBudget(budget)
            navController.popBackStack()
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetForm(
    initialBudget: Budget? = null,
    categories: List<Category>,
    onAdd: (Budget) -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    var amount            by remember { mutableStateOf(initialBudget?.amount?.toString() ?: "") }
    var selectedCategoryId by remember {
        mutableIntStateOf(initialBudget?.categoryId ?: -1)
    }
    var selectedMonth     by remember {
        mutableIntStateOf(initialBudget?.month?.minus(1) ?: Calendar.getInstance().get(Calendar.MONTH))
    }
    var selectedYear      by remember {
        mutableIntStateOf(initialBudget?.year ?: currentYear)
    }

    // Dropdown states
    var expandedCat   by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear  by remember { mutableStateOf(false) }

    val monthNames = (0..11).map { monthName(it) }
    val years      = (currentYear..currentYear + 5).toList()

    val selectedCategoryName = when (selectedCategoryId) {
        -1 -> "All Categories"
        else -> categories.find { it.id == selectedCategoryId }?.name ?: ""
    }

    // ----‑ Validación ----
    val parsedAmount = amount.toDoubleOrNull()
    val amountError  = parsedAmount == null || parsedAmount <= 0.0
    val formValid    = !amountError   // (El resto de campos siempre tiene valor)

    Column(Modifier.padding(16.dp).fillMaxWidth()) {
        Text(
            text  = if (initialBudget != null) "Edit Budget" else "Add Budget",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value     = amount,
            onValueChange = { amount = it },
            label     = { Text("Amount") },
            isError   = amountError,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Text("Category", style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(expandedCat, onExpandedChange = { expandedCat = !expandedCat }) {
            OutlinedTextField(
                value       = selectedCategoryName,
                onValueChange = {},
                readOnly    = true,
                trailingIcon= { ExposedDropdownMenuDefaults.TrailingIcon(expandedCat) },
                modifier    = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expandedCat, onDismissRequest = { expandedCat = false }) {
                DropdownMenuItem(
                    text = { Text("All Categories") },
                    onClick = {
                        selectedCategoryId = -1
                        expandedCat = false
                    }
                )
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            selectedCategoryId = cat.id
                            expandedCat = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Month", style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(expandedMonth, onExpandedChange = { expandedMonth = !expandedMonth }) {
            OutlinedTextField(
                value       = monthNames[selectedMonth],
                onValueChange = {},
                readOnly    = true,
                trailingIcon= { ExposedDropdownMenuDefaults.TrailingIcon(expandedMonth) },
                modifier    = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expandedMonth, onDismissRequest = { expandedMonth = false }) {
                monthNames.forEachIndexed { idx, name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedMonth = idx
                            expandedMonth = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Year", style = MaterialTheme.typography.labelLarge)
        ExposedDropdownMenuBox(expandedYear, onExpandedChange = { expandedYear = !expandedYear }) {
            OutlinedTextField(
                value       = selectedYear.toString(),
                onValueChange = {},
                readOnly    = true,
                trailingIcon= { ExposedDropdownMenuDefaults.TrailingIcon(expandedYear) },
                modifier    = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expandedYear, onDismissRequest = { expandedYear = false }) {
                years.forEach { y ->
                    DropdownMenuItem(
                        text = { Text(y.toString()) },
                        onClick = {
                            selectedYear = y
                            expandedYear = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick  = {
                onAdd(
                    Budget(
                        id          = initialBudget?.id ?: 0,
                        categoryId  = selectedCategoryId,
                        amount      = parsedAmount ?: 0.0,
                        month       = selectedMonth + 1, // 1‑based en DB
                        year        = selectedYear
                    )
                )
            },
            enabled = formValid,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}
