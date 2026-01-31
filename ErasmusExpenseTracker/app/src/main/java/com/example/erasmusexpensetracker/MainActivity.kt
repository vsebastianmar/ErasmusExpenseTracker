package com.example.erasmusexpensetracker


import java.util.Calendar
import java.util.Locale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.erasmusexpensetracker.ui.theme.ErasmusExpenseTrackerTheme

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.rememberNavController
import com.example.erasmusexpensetracker.data.local.AppDatabase

import com.example.erasmusexpensetracker.data.repository.ExpenseRepository
import com.example.erasmusexpensetracker.ui.screens.MainScaffold


// Java Date + Locale
import java.text.SimpleDateFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = ExpenseRepository(database.expenseDao(), database.categoryDao(), database.budgetDao())

        enableEdgeToEdge()
        setContent {
            ErasmusExpenseTrackerTheme {
                val viewModel: com.example.erasmusexpensetracker.viewmodel.ExpenseViewModel = viewModel(
                    factory = com.example.erasmusexpensetracker.viewmodel.ExpenseViewModelFactory(
                        repository
                    )
                )
                val navController = rememberNavController()
                MainScaffold(viewModel, navController)
            }
        }
    }
}




fun monthName(month: Int): String {
    return SimpleDateFormat("MMMM", Locale.getDefault())
        .format(Calendar.getInstance().apply { set(Calendar.MONTH, month) }.time)
}













