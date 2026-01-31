package com.example.erasmusexpensetracker.data.local.dao
import androidx.room.*
import com.example.erasmusexpensetracker.data.local.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Int): Flow<Expense?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>


    @Query("""
    SELECT SUM(amount) FROM expenses 
    WHERE categoryId = :categoryId 
      AND strftime('%m', date / 1000, 'unixepoch') = :monthStr
      AND strftime('%Y', date / 1000, 'unixepoch') = :yearStr
""")
    suspend fun getTotalForCategoryInMonth(categoryId: Int, monthStr: String, yearStr: String): Double?

}