package com.example.erasmusexpensetracker.viewmodel
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.erasmusexpensetracker.data.local.entities.Budget
import com.example.erasmusexpensetracker.data.local.entities.Category
import com.example.erasmusexpensetracker.data.repository.ExpenseRepository
import com.example.erasmusexpensetracker.data.local.entities.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class ExpenseViewModel(private val repo: ExpenseRepository) : ViewModel() {

    val expenses = repo.allExpenses
    val categories = repo.allCategories
    val budgets = repo.allBudgets

    // -- Basic CRUD methods --
    fun addExpense(expense: Expense) {
        viewModelScope.launch { repo.insert(expense) }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch { repo.update(expense) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repo.delete(expense) }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch { repo.addCategory(category) }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { repo.deleteCategory(category) }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch { repo.updateCategory(category) }
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch { repo.addBudget(budget) }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch { repo.updateBudget(budget) }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch { repo.deleteBudget(budget) }
    }

    // -- Snackbar message state --
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private suspend fun showSnackbar(message: String) {
        _snackbarMessage.emit(message)
    }

    suspend fun checkBudgetStatus(
        categoryId: Int,
        month: Int,
        year: Int
    ): Boolean = withContext(Dispatchers.IO) {
        val allBudgets = budgets.first()
        Log.d("BudgetDebug", "All budgets: $allBudgets")

        val expenses = repo.allExpenses.first().filter { it.categoryId == categoryId }
        for (exp in expenses) {
            val cal = Calendar.getInstance().apply { timeInMillis = exp.date }
            Log.d("BudgetCheck", "Expense: ${exp.title}, date=${exp.date}, parsedMonth=${cal.get(Calendar.MONTH) + 1}, year=${cal.get(Calendar.YEAR)}")
        }

        val totalSpent = repo.getTotalForCategoryInMonth(categoryId, month, year)
        val budget = repo.getBudgetFor(categoryId, month, year).firstOrNull()
        Log.d("BudgetCheck", "Checking for cat=$categoryId, month=$month, year=$year, totalSpent=$totalSpent")

        budget?.let {
            val usageRatio = totalSpent / it.amount
            Log.d("BudgetCheck", "Budget fetched: $budget, usage ratio = $usageRatio")

            when {
                usageRatio > 1.0 -> {
                    showSnackbar("⚠️ Budget exceeded!")
                    return@withContext true
                }
                usageRatio > 0.9 -> {
                    showSnackbar("⚠️ Over 90% of budget used")
                    return@withContext true
                }
                else -> {
                    showSnackbar("✅ Budget is within limits")
                }
            }
        }

        false
    }
    @Composable
    fun getProgressForBudget(budget: Budget): State<Double> {
        return produceState(initialValue = 0.0, budget) {
            value = repo.getTotalForCategoryInMonth(
                budget.categoryId,
                budget.month,
                budget.year
            )
        }
    }
    var titleFilter by mutableStateOf("")
        private set

    var typeFilter by mutableStateOf("All")
        private set

    var categoryFilter by mutableIntStateOf(-1) // -1 = All
        private set

    fun updateTitleFilter(value: String) {
        titleFilter = value
    }

    fun updateTypeFilter(value: String) {
        typeFilter = value
    }

    fun updateCategoryFilter(value: Int) {
        categoryFilter = value
    }
}

// Factory
class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}