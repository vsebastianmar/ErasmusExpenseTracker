package com.example.erasmusexpensetracker.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.erasmusexpensetracker.data.local.dao.CategoryDao
import com.example.erasmusexpensetracker.data.local.dao.BudgetDao
import com.example.erasmusexpensetracker.data.local.dao.ExpenseDao
import com.example.erasmusexpensetracker.data.local.entities.Expense
import com.example.erasmusexpensetracker.data.local.entities.Budget
import com.example.erasmusexpensetracker.data.local.entities.Category

@Database(entities = [Expense::class, Category::class, Budget::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expenses_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}