package com.example.erasmusexpensetracker.data.repository
import com.example.erasmusexpensetracker.data.local.dao.CategoryDao
import com.example.erasmusexpensetracker.data.local.dao.BudgetDao
import com.example.erasmusexpensetracker.data.local.dao.ExpenseDao
import com.example.erasmusexpensetracker.data.local.entities.Expense
import com.example.erasmusexpensetracker.data.local.entities.Budget
import com.example.erasmusexpensetracker.data.local.entities.Category
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao,
                        private val categoryDao: CategoryDao,
                        private val budgetDao: BudgetDao
) {
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allBudgets: Flow<List<Budget>> = budgetDao.getAllBudgets()



    suspend fun insert(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun delete(expense: Expense) = expenseDao.deleteExpense(expense)
    suspend fun update(expense: Expense) = expenseDao.updateExpense(expense)


    suspend fun addCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun addBudget(budget: Budget) = budgetDao.insertBudget(budget)
    suspend fun updateBudget(budget: Budget) = budgetDao.updateBudget(budget)
    suspend fun deleteBudget(budget: Budget) = budgetDao.deleteBudget(budget)
    fun getBudgetFor(categoryId: Int, month: Int, year: Int): Flow<Budget?> {
        return budgetDao.getBudgetForCategoryMonthAndYear(categoryId, month, year)
    }

    suspend fun getTotalForCategoryInMonth(categoryId: Int, month: Int, year: Int): Double {
        val monthStr = month.toString().padStart(2, '0')
        val yearStr = year.toString()
        return expenseDao.getTotalForCategoryInMonth(categoryId, monthStr, yearStr) ?: 0.0
    }

}