package com.example.erasmusexpensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.erasmusexpensetracker.ui.screens.budgets.AddBudgetForm
import com.example.erasmusexpensetracker.ui.screens.budgets.AddBudgetScreen
import com.example.erasmusexpensetracker.ui.screens.categories.AddCategoryScreen
import com.example.erasmusexpensetracker.ui.screens.budgets.BudgetListScreen
import com.example.erasmusexpensetracker.ui.screens.categories.AddCategoryForm
import com.example.erasmusexpensetracker.ui.screens.categories.CategoryListScreen
import com.example.erasmusexpensetracker.ui.screens.dashboard.DashboardScreen
import com.example.erasmusexpensetracker.ui.screens.expenses.AddExpenseForm
import com.example.erasmusexpensetracker.ui.screens.expenses.AddExpenseScreen
import com.example.erasmusexpensetracker.ui.screens.expenses.ExpenseListScreen
import com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel

// ui/navigation/NavGraph.kt
@Composable
fun AppNavGraph(navController: NavHostController,
                viewModel: ExpenseViewModel,
                modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel)
        }
        composable(Screen.Expenses.route) {
            ExpenseListScreen(
                viewModel,
                navController
            )
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(viewModel, navController)
        }
        composable(Screen.EditExpense.route) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toIntOrNull()
            val expenses by viewModel.expenses.collectAsState(emptyList())
            val expenseToEdit = expenses.find { it.id == expenseId }
            val categories by viewModel.categories.collectAsState(emptyList())

            expenseToEdit?.let { expense ->
                AddExpenseForm(
                    initialExpense = expense,
                    categories = categories,
                    onAdd = {
                        viewModel.updateExpense(it)
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(Screen.Categories.route) {
            CategoryListScreen(viewModel, navController)
        }
        composable(Screen.AddCategory.route) {
            AddCategoryScreen(viewModel, navController)
        }
        composable(Screen.EditCategory.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            val categories by viewModel.categories.collectAsState(emptyList())
            val categoryToEdit = categories.find { it.id == categoryId }

            categoryToEdit?.let { category ->
                AddCategoryForm(
                    initialCategory = category,
                    onAdd = {
                        viewModel.updateCategory(it)
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(Screen.Budgets.route) {
            BudgetListScreen(viewModel, navController)
        }
        composable(Screen.AddBudget.route) {
            AddBudgetScreen(viewModel, navController)
        }
        composable(Screen.EditBudget.route) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId")?.toIntOrNull()
            val budgets by viewModel.budgets.collectAsState(emptyList())
            val budgetToEdit = budgets.find { it.id == budgetId }
            val categories by viewModel.categories.collectAsState(emptyList())

            budgetToEdit?.let { budget ->
                AddBudgetForm(
                    initialBudget = budget,
                    categories = categories,
                    onAdd = {
                        viewModel.updateBudget(it)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
