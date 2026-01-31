package com.example.erasmusexpensetracker.ui.screens.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel
import com.example.erasmusexpensetracker.monthName
import com.example.erasmusexpensetracker.ui.navigation.Screen

@Composable
fun BudgetListScreen(viewModel: ExpenseViewModel, navController: NavController) {
    BudgetList(viewModel = viewModel, navController = navController)
}

@Composable
fun BudgetList(viewModel: ExpenseViewModel, navController: NavController) {
    val budgets by viewModel.budgets.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(emptyList())

    LazyColumn {
        items(budgets, key = { it.id }) { budget ->
            val categoryName = categories.find { it.id == budget.categoryId }?.name ?: "Unknown"
            val spent by viewModel.getProgressForBudget(budget)

            val percentage = (spent / budget.amount).coerceIn(0.0, 1.0)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = categoryName)
                            Text(text = "Budget: ${budget.amount}€ for ${monthName(budget.month -1)}")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = {
                                navController.navigate(Screen.EditBudget.passId(budget.id))

                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Budget")
                            }
                            IconButton(onClick = {
                                viewModel.deleteBudget(budget)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Budget")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Barra de progreso
                    LinearProgressIndicator(
                        progress = { percentage.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Spent: ${"%.2f".format(spent)}€ (${(percentage * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}