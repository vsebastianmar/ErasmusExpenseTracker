package com.example.erasmusexpensetracker.ui.screens.expenses

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.erasmusexpensetracker.data.local.entities.Category
import com.example.erasmusexpensetracker.data.local.entities.Expense
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddExpenseScreen(viewModel: ExpenseViewModel, navController: NavController) {
    val categories by viewModel.categories.collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()

    AddExpenseForm(
        categories = categories,
        onAdd = { expense ->
            coroutineScope.launch {
                viewModel.addExpense(expense)

                val cal = Calendar.getInstance().apply { timeInMillis = expense.date }
                val month = cal.get(Calendar.MONTH) + 1
                val year = cal.get(Calendar.YEAR)

                viewModel.checkBudgetStatus(expense.categoryId, month, year)
                navController.popBackStack()
            }
        }
    )
}
// para crear y editar expenses
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseForm(
    initialExpense: Expense? = null,
    categories: List<Category>,
    onAdd: (Expense) -> Unit
) {
    // ----‑ Estado de campos ----
    var title by remember { mutableStateOf(initialExpense?.title ?: "") }
    var amount by remember { mutableStateOf(initialExpense?.amount?.toString() ?: "") }
    var selectedCategoryId by remember {
        mutableIntStateOf(initialExpense?.categoryId ?: categories.firstOrNull()?.id ?: -1)
    }
    var isIncome by remember { mutableStateOf(initialExpense?.income ?: false) }

    // ----‑ Dropdown categoría ----
    var expanded by remember { mutableStateOf(false) }
    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: ""

    // ----‑ DatePicker ----
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendar = Calendar.getInstance()
    initialExpense?.let { calendar.timeInMillis = it.date }

    var selectedDay   by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear  by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                selectedYear = y; selectedMonth = m; selectedDay = d
                showDatePicker = false
            },
            selectedYear, selectedMonth, selectedDay
        ).show()
    }

    val formattedDate = remember(selectedDay, selectedMonth, selectedYear) {
        Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, selectedDay)
        }.let { dateFormatter.format(it.time) }
    }

    // ----‑ Validación ----
    val parsedAmount  = amount.toDoubleOrNull()
    val titleError    = title.isBlank()
    val amountError   = parsedAmount == null || parsedAmount <= 0.0
    val formValid     = !titleError && !amountError && selectedCategoryId != -1

    // ----‑ UI ----
    Column(Modifier.padding(16.dp).fillMaxWidth()) {
        Text(
            text  = if (initialExpense != null) "Edit Expense" else "Add Expense",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value     = title,
            onValueChange = { title = it },
            label     = { Text("Title") },
            isError   = titleError,
            modifier  = Modifier.fillMaxWidth()
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
        ExposedDropdownMenuBox(expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value       = selectedCategoryName,
                onValueChange = {},
                readOnly    = true,
                trailingIcon= { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier    = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            selectedCategoryId = cat.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Date", style = MaterialTheme.typography.labelLarge)
        Box(Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
            OutlinedTextField(
                value     = formattedDate,
                onValueChange = {},
                readOnly  = true,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Is Income?")
            Spacer(Modifier.width(8.dp))
            Switch(checked = isIncome, onCheckedChange = { isIncome = it })
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick  = {
                val dateMillis = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                onAdd(
                    Expense(
                        id          = initialExpense?.id ?: 0,
                        title       = title,
                        amount      = parsedAmount ?: 0.0,
                        categoryId  = selectedCategoryId,
                        date        = dateMillis,
                        income      = isIncome
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
