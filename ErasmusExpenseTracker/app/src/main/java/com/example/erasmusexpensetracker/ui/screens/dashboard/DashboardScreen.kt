package com.example.erasmusexpensetracker.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.erasmusexpensetracker.ui.components.BarChartWithYearSelector
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(emptyList())

    val totalIncome = expenses.filter { it.income }.sumOf { it.amount }
    val totalExpense = expenses.filter { !it.income }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Dashboard Summary",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Net Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (netBalance >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Net Balance", style = MaterialTheme.typography.titleMedium)
                Text(
                    "€%.2f".format(netBalance),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (netBalance >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Income: €%.2f".format(totalIncome), color = Color(0xFF2E7D32))
                Text("Expenses: €%.2f".format(totalExpense), color = Color(0xFFC62828))
            }
        }
        // Recent Expenses Section
        Text(
            text = "Recent Expenses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        val recentExpenses = expenses
            .filter { !it.income }
            .sortedByDescending { it.date }
            .take(3)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recentExpenses.forEach { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(expense.title , style = MaterialTheme.typography.titleSmall)
                        Text("€%.2f".format(expense.amount), color = Color(0xFFC62828))
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(Date(expense.date)),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp), // Ensures charts render properly
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Expenses Chart
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Expenses",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                com.example.erasmusexpensetracker.ui.components.PieChartView(
                    expenses = expenses.filter { !it.income },
                    categories = categories,
                    label = "",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Incomes Chart
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Incomes",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF388E3C),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                com.example.erasmusexpensetracker.ui.components.PieChartView(
                    expenses = expenses.filter { it.income },
                    categories = categories,
                    label = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text("Net Balance - Last 28 Days", style = MaterialTheme.typography.titleMedium)
        com.example.erasmusexpensetracker.ui.components.LineChartView(expenses)

        Text("Expenses vs. income by Month", style = MaterialTheme.typography.titleMedium)
        BarChartWithYearSelector(expenses)
    }
}
