package com.example.erasmusexpensetracker.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Expenses : Screen("expenses")
    object AddExpense : Screen("addExpense")
    object EditExpense : Screen("editExpense/{expenseId}") {
        fun passId(id: Int) = "editExpense/$id"
    }
    object Categories : Screen("categories")
    object AddCategory : Screen("addCategory")
    object EditCategory : Screen("editCategory/{categoryId}") {
        fun passId(id: Int) = "editCategory/$id"
    }
    object Budgets : Screen("budgets")
    object AddBudget : Screen("addBudget")
    object EditBudget : Screen("editBudget/{budgetId}") {
        fun passId(id: Int) = "editBudget/$id"
    }
}

